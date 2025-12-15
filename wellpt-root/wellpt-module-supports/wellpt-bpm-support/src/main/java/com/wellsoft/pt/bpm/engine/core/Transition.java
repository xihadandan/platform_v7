/*
 * @(#)2012-11-19 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.handler.TransitionHanlder;
import com.wellsoft.pt.bpm.engine.dispatcher.DispatcherBranchTaskResolver;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.TransferCode;
import com.wellsoft.pt.bpm.engine.node.EndNode;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import com.wellsoft.pt.bpm.engine.support.CustomRuntimeData;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.security.acl.service.AclTaskService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-19.1	zhulh		2012-11-19		Create
 * </pre>
 * @date 2012-11-19
 */
public abstract class Transition {
    private Node from;
    private List<Node> tos = new ArrayList<Node>(0);
    private Node runtimeToNode;

    private TaskNodeComparator taskNodeComparator = new TaskNodeComparator();

    /**
     * @return the from
     */
    public Node getFrom() {
        return from;
    }

    /**
     * @param from 要设置的from
     */
    public void setFrom(Node from) {
        this.from = from;
    }

    /**
     * @return the tos
     */
    public List<Node> getTos() {
        return tos;
    }

    /**
     * @param tos 要设置的tos
     */
    public void setTos(List<Node> tos) {
        // 确保子流程结点、结束结点最后执行，使在分支流程中EndHanlder能正确删除权限
        Collections.sort(tos, taskNodeComparator);
        this.tos = tos;
    }

    public String getFromId() {
        return this.from.getId();
    }

    /**
     * @return the runtimeToNode
     */
    public Node getRuntimeToNode() {
        return runtimeToNode;
    }

    /**
     * @param executionContext
     */
    public void take(ExecutionContext executionContext) {
        Token token = executionContext.getToken();
        String preTaskId = null;
        String preTaskInstUuid = null;
        TaskInstance preTask = token.getTask();
        TaskData taskData = token.getTaskData();
        boolean taskForking = false;
        if (preTask != null) {
            preTaskId = preTask.getId();
            preTaskInstUuid = preTask.getUuid();
            taskForking = taskData.isTaskForking(preTaskId);
        }
        Iterator<Node> it = tos.iterator();
        int order = 0;
        while (it.hasNext()) {
            Node to = it.next();
            this.runtimeToNode = to;
            String toTaskId = to.getId();
            boolean isTheLastForkingTask = false;
            if (preTask != null) {
                taskData.setPreTaskId(toTaskId, preTaskId);
                taskData.setPreTaskInstUuid(toTaskId, preTaskInstUuid);
                if (taskForking && !it.hasNext()) {
                    isTheLastForkingTask = true;
                }
                Integer transferCode = taskData.getTransferCode(preTaskInstUuid);
                if (TransferCode.Submit.getCode().equals(transferCode)
                        || TransferCode.TransferSubmit.equals(transferCode)
                        || TransferCode.DelegationSubmit.equals(transferCode)) {
                    taskData.setPreTaskProperties(toTaskId, FlowConstant.BRANCH.IS_PARALLEL, preTask.getIsParallel());
                    taskData.setPreTaskProperties(toTaskId, FlowConstant.BRANCH.PARALLEL_TASK_INST_UUID,
                            preTask.getParallelTaskInstUuid());
                }
            }
            token.setNode(null);
            token.setTask(null);

            // 流向变换处理
            TransitionHanlder hanlder = ApplicationContextHolder.getBean(TransitionHanlder.class);
            hanlder.execute(this, executionContext);

            // 当前处理的流向
            Direction direction = getDirection(this.getFrom(), to, executionContext);
            // 设置环节流向
            if (direction != null) {
                taskData.setTaskDirection(this.getFrom().getId(), direction.getToID(), direction);
                // taskData.setTaskDirection(direction.getFromID(), direction.getToID(), direction);
                taskData.setTaskForkingOrder(direction.getToID(), order);
            }
            // pass the token to the destinationNode node
            // 动态多分支处理
            if (isDynamicDirection(direction, taskData, executionContext)) {
                // 开始分支任务处理
                startBranchTask(direction, taskData, executionContext);
            } else {
                to.enter(executionContext);
            }
            order++;

            // 并行分发结束，删除旧的权限
            if (isTheLastForkingTask) {
                afterBranch(from, to, preTaskInstUuid, taskData, executionContext);
            }
        }

        // 发起空分支判断
        if (taskForking && CollectionUtils.isEmpty(taskData.getSubmitResult().getTaskInstUUids())) {
            throw new RuntimeException("环节[" + from.getName() + "]的运转模式为分支模式，但没有发起任何分支的环节实例，请检查流程配置是否正确！");
        }

    }

    /**
     * 并行分发结束，删除旧的权限
     *
     * @param fromNode
     * @param toNode
     * @param preTaskInstUuid
     * @param taskData
     * @param executionContext
     */
    private void afterBranch(Node fromNode, Node toNode, String preTaskInstUuid, TaskData taskData,
                             ExecutionContext executionContext) {
        boolean taskForking = taskData.isTaskForking(fromNode.getId());
        boolean taskJoining = taskData.isTaskJoining(toNode.getId());
        if (taskForking && !taskJoining) {
            AclTaskService aclTaskService = ApplicationContextHolder.getBean(AclTaskService.class);
            // 并行分发结束，删除旧的权限
            aclTaskService.removePermission(preTaskInstUuid);
        }

    }

    /**
     * @param direction
     * @param taskData
     * @param executionContext
     */
    private void startBranchTask(Direction direction, TaskData taskData, ExecutionContext executionContext) {
        // 开始分支任务处理
        Object dispatcherBranchTaskResolverName = taskData
                .getCustomData(CustomRuntimeData.KEY_DISPATCHER_BRANCH_TASK_RESOLVER);
        DispatcherBranchTaskResolver dispatcherBranchTaskResolver = null;
        if (dispatcherBranchTaskResolverName == null) {
            dispatcherBranchTaskResolver = ApplicationContextHolder.getBean("defaultDispatcherBranchTaskResolver",
                    DispatcherBranchTaskResolver.class);
        } else {
            dispatcherBranchTaskResolver = ApplicationContextHolder.getBean(
                    dispatcherBranchTaskResolverName.toString(), DispatcherBranchTaskResolver.class);
        }
        dispatcherBranchTaskResolver.resolve(this, direction, executionContext);
    }

    /**
     * @param fromNode
     * @param toNode
     * @param executionContext
     * @return
     */
    private Direction getDirection(Node fromNode, Node toNode, ExecutionContext executionContext) {
        String directionId = toNode.getDirectionId();
        Direction direction = null;
        if (StringUtils.isNotBlank(directionId)) {
            direction = executionContext.getFlowDelegate().getDirection(directionId);
        } else if (direction == null) {
            direction = executionContext.getFlowDelegate().getDirection(fromNode.getId(), toNode.getId());
        }
        return direction;
    }

    /**
     * @param direction
     * @param taskData
     * @param executionContext
     * @return
     */
    private boolean isDynamicDirection(Direction direction, TaskData taskData, ExecutionContext executionContext) {
        if (direction == null) {
            return false;
        }
        if (taskData.isTaskForking(direction.getFromID())) {
            String branchMode = direction.getBranchMode();
            if (FlowConstant.BRANCH_MODE.DYNAMIC.equals(branchMode)) {
                return true;
            }
        }
        return false;
    }

    private static class TaskNodeComparator implements Comparator<Node> {

        /**
         * (non-Javadoc)
         *
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(Node o1, Node o2) {
            return o1 instanceof SubTaskNode ? 1 : (o1 instanceof EndNode ? 1 : -1);
        }

    }

}
