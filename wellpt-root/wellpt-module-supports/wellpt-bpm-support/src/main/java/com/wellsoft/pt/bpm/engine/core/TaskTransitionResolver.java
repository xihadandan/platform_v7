/*
 * @(#)2012-11-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.GatewayElement;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.entity.TaskBranch;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.DirectionType;
import com.wellsoft.pt.bpm.engine.enums.ForkMode;
import com.wellsoft.pt.bpm.engine.enums.JoinMode;
import com.wellsoft.pt.bpm.engine.exception.JudgmentBranchFlowNotFoundException;
import com.wellsoft.pt.bpm.engine.exception.MultipleJudgmentBranchFlowException;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.expression.Condition;
import com.wellsoft.pt.bpm.engine.expression.ConditionFactory;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.TaskNode;
import com.wellsoft.pt.bpm.engine.service.SameUserSubmitService;
import com.wellsoft.pt.bpm.engine.service.TaskBranchService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.workflow.enums.WorkFlowPrivilege;
import ognl.Ognl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 任务流向解析类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-21.1	zhulh		2012-11-21		Create
 * </pre>
 * @date 2012-11-21
 */
@Component(TaskTransitionResolver.BEAN_NAME)
public class TaskTransitionResolver implements TransitionResolver {

    public static final String BEAN_NAME = "taskTransitionResolver";

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private TaskBranchService taskBranchService;

    @Autowired
    private SameUserSubmitService sameUserSubmitService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.engine.core.TransitionResolver#resolveForkTask(com.wellsoft.pt.workflow.engine.core.Transition, com.wellsoft.pt.workflow.engine.core.Token)
     */
    @Override
    public void resolveForkTask(Transition transition, Token token) {
        FlowDelegate flowDelegate = token.getFlowDelegate();
        // 判断是否为自由流程
        if (flowDelegate.isFree()) {
            resolveFreeTask(transition, token);
        } else {
            // 是否补审补办提交结束
            if (sameUserSubmitService.isSupplementToCompleted(token)) {
                transition.setTos(Lists.newArrayList(flowDelegate.getEndNode()));
                return;
            }
            // 分支模型
            Node from = transition.getFrom();
            TaskNode taskNode = (TaskNode) from;
            // 全部分支
            if (taskNode.getForkMode() == ForkMode.ALL.getValue()) {
                List<String> toIDs = taskNode.getToIDs();
                List<Node> tos = Lists.newArrayList();
                for (String toID : toIDs) {
                    tos.add(flowDelegate.getTaskNode(toID));
                }
                transition.setTos(tos);
            } else if (taskNode.getForkMode() == ForkMode.MULTI.getValue()) {
                // 多路分支
                // 多路分支的下一节点必须是条件判断
                if (flowDelegate.isConditionTask(from)) {
                    resolveConditionTask(transition, token);
                } else {
                    resolveMulitAllTask(transition, token);
                }
            } else {
                String toTaskId = token.getTaskData().getToTaskId(transition.getFromId());
                if (StringUtils.isNotBlank(toTaskId)) {
                    List<Node> tos = Lists.newArrayList();
                    tos.add(flowDelegate.getTaskNode(toTaskId));
                    transition.setTos(tos);
                } else {
                    // 单路分支
                    // 判断是否为分支的判断结点
                    if (flowDelegate.isConditionTask(from)) {
                        resolveConditionTask(transition, token);
                    } else {
                        resolveDirectTask(transition, token);
                    }
                }
            }
        }

        // 确保找到分支结点
        if (transition.getTos() == null || transition.getTos().isEmpty()) {
            throw new WorkFlowException("流程错误，无法解析下一任务结点！");
        }
    }

    /**
     * 如何描述该方法
     *
     * @param transition
     * @param token
     */
    private void resolveFreeTask(Transition transition, Token token) {
        Node from = transition.getFrom();
        FlowDelegate flowDelegate = token.getFlowDelegate();
        // 如果用户已经选择流向，则返回该流向
        String toTaskId = token.getTaskData().getToTaskId(transition.getFromId());
        if (StringUtils.isNotBlank(toTaskId)) {
            List<Node> tos = new ArrayList<Node>();
            tos.add(flowDelegate.getTaskNode(toTaskId));
            transition.setTos(tos);
            return;
        } else {
            if (from instanceof TaskNode) {
                // 最后一个结点直接返回结束结点
                TaskNode taskNode = (TaskNode) from;
                if (!taskNode.getToIDs().isEmpty() && flowDelegate.isLastTaskNode(taskNode.getId())) {
                    List<Node> tos = new ArrayList<Node>();
                    tos.add(flowDelegate.getTaskNode(taskNode.getToID()));
                    transition.setTos(tos);
                    return;
                }
            }
            List<Node> tasks = flowDelegate.getAllTaskNodes();
            List<String> toIDs = new ArrayList<String>();
            for (Node taskNode : tasks) {
                if (from.getId().equals(taskNode.getId())) {
                    continue;
                }
                toIDs.add(taskNode.getId());
            }
            // 抛出异常
            Map<String, Object> variables = getToTaskMap(flowDelegate, from.getId(), toIDs);
            variables.put("fromTaskId", transition.getFromId());
            throw new MultipleJudgmentBranchFlowException(variables);
        }
    }

    /**
     * 计算分支节点的转向，只能有一个，如果有多个弹出流程选项进行选择一个，如果没有，流程无法进行下去
     *
     * @param transition
     * @param token
     */
    private void resolveConditionTask(Transition transition, Token token) {
        TaskNode taskNode = (TaskNode) transition.getFrom();
        FlowDelegate flowDelegate = token.getFlowDelegate();
        List<String> toIDs = taskNode.getToIDs();
        boolean gatewayCondition = false;
        List<Direction> gatewayToDirections = Lists.newArrayListWithCapacity(0);
        if (CollectionUtils.isEmpty(toIDs)) {
            gatewayToDirections.addAll(flowDelegate.getAvailableGatewayToDirections(taskNode.getId()));
            toIDs = gatewayToDirections.stream().map(toDirection -> toDirection.getToID()).collect(Collectors.toList());
            gatewayCondition = true;
        } else {
            // 前端提交时过滤掉作为提交按钮的流向
            List<String> ignoreToIDs = Lists.newArrayListWithCapacity(0);
            if (CollectionUtils.size(toIDs) > 1 && WorkFlowPrivilege.Submit.getCode().equals(token.getTaskData().getSubmitButtonId())
                    && !token.getTaskData().isDaemon()) {
                List<Direction> allDirections = flowDelegate.getDirections(taskNode.getId(), toIDs);
                ignoreToIDs = allDirections.stream().filter(direction -> direction.isUseAsButton()).map(direction -> direction.getToID()).collect(Collectors.toList());
                toIDs.removeAll(ignoreToIDs);
            }
        }
        String fromTaskId = transition.getFromId();
        // 如果用户已经选择流向，则返回该流向
        String toTaskId = token.getTaskData().getToTaskId(fromTaskId);
        String toDirectionId = token.getTaskData().getToDirectionId(fromTaskId);
        List<Node> nodes = new ArrayList<Node>();
        if (StringUtils.isNotBlank(toTaskId)) {
            List<String> toTaskIds = Arrays.asList(StringUtils.split(toTaskId, Separator.SEMICOLON.getValue()));
            for (String toID : toIDs) {
                if (toTaskIds.contains(toID)) {
                    nodes.add(flowDelegate.getTaskNode(toID));
                }
            }
        } else if (StringUtils.isNotBlank(toDirectionId)) {
            List<String> toDirectionIds = Arrays
                    .asList(StringUtils.split(toDirectionId, Separator.SEMICOLON.getValue()));
            for (String directionId : toDirectionIds) {
                nodes.add(flowDelegate.getToTaskNode(flowDelegate.getDirection(directionId)));
            }
        }
        if (CollectionUtils.isNotEmpty(nodes)) {
            transition.setTos(nodes);
            return;
        }

        String fromID = taskNode.getId();
        List<Direction> directions = new ArrayList<Direction>();
        List<Direction> defaultDirections = new ArrayList<Direction>();

        // 1、判断分支条件
        if (gatewayCondition) {
            // 新版网关条件判断
            directions = evaluateGatewayCondition(taskNode, flowDelegate, token);
        } else {
            List<Direction> rawDirections = flowDelegate.getDirections(fromID, toIDs);
            for (Direction direction : rawDirections) {
                if (direction.isDefault()) {
                    defaultDirections.add(direction);
                } else {
                    // 判断分支条件
                    if (evaluate(flowDelegate, token, direction)) {
                        directions.add(direction);
                    }
                }
            }
        }

        // 2、缺省流向(其他流向都不满足时使用此流向)
        if (directions.isEmpty()) {
            directions.addAll(defaultDirections);
        }

        // 3、找不到判断分支流向异常
        if (directions.isEmpty()) {
            // 抛出异常
            Map<String, Object> variables = null;
            if (gatewayCondition) {
                variables = getToTaskMapByDirection(flowDelegate, fromID, gatewayToDirections);
            } else {
                variables = getToTaskMap(flowDelegate, fromID, toIDs);// getToTaskMap(flowDelegate, toIDs);
            }
            variables.put("title", token.getFlowInstance().getTitle());
            variables.put("fromTaskId", transition.getFromId());
            variables.put("submitButtonId", token.getTaskData().getSubmitButtonId());
            variables.put("multiselect", !(taskNode.getForkMode() == ForkMode.SINGLE.getValue()));
            throw new JudgmentBranchFlowNotFoundException(variables);
        }

        // 4、判断条件的多路分支
        if (taskNode.getForkMode() == ForkMode.MULTI.getValue()) {
            List<Node> tos = new ArrayList<Node>();
            for (Direction direction : directions) {
                tos.add(flowDelegate.getToTaskNode(direction));
            }
            // 删除单一、多路、全部聚合混合的多路、全部聚合结点
            if (tos.size() > 1) {
                List<Node> deleteMergeNodes = new ArrayList<Node>();
                for (Node to : tos) {
                    if (to.getJoinMode() == JoinMode.MULTI.getValue() || to.getJoinMode() == JoinMode.ALL.getValue()) {
                        deleteMergeNodes.add(to);
                    }
                }
                if (deleteMergeNodes.size() < tos.size()) {
                    tos.removeAll(deleteMergeNodes);
                }
            }
            transition.setTos(tos);
        } else if (directions.size() > 1) {
            // 5、多个判断分支流向异常
            // 抛出异常
//            List<String> toMulitTaskIds = Lists.newArrayList();
//            for (Direction direction : directions) {
//                toMulitTaskIds.add(direction.getToID());
//            }
            Map<String, Object> variables = getToTaskMapByDirection(flowDelegate, fromID, directions);
            variables.put("fromTaskId", transition.getFromId());
            throw new MultipleJudgmentBranchFlowException(variables);
        } else {
            // 6、获取下一节点
            Node to = flowDelegate.getToTaskNode(directions.get(0));
            List<Node> tos = new ArrayList<Node>();
            tos.add(to);
            transition.setTos(tos);
            // 退回后允许直接提交至本环节
            addTaskOfAllowReturnAfterRollbackIfRequired(taskNode, tos, token);
        }
    }

    /**
     * @param transition
     * @param token
     */
    private void resolveMulitAllTask(Transition transition, Token token) {
        FlowDelegate flowDelegate = token.getFlowDelegate();
        TaskNode fromTaskNode = (TaskNode) transition.getFrom();
        List<String> toIDs = fromTaskNode.getToIDs();
        // 多路分支为全部分支的情况
        // 提交时可选择流向
        TaskElement taskElement = flowDelegate.getFlow().getTask(fromTaskNode.getId());
        if (taskElement.getParallelGateway().getIsChooseForkingDirection()) {
            String toTaskId = token.getTaskData().getToTaskId(transition.getFromId());
            if (StringUtils.isNotBlank(toTaskId)) {
                List<String> toTaskIds = Arrays.asList(StringUtils.split(toTaskId, Separator.SEMICOLON.getValue()));
                List<Node> nodes = new ArrayList<Node>();
                for (String toID : toIDs) {
                    if (toTaskIds.contains(toID)) {
                        nodes.add(flowDelegate.getTaskNode(toID));
                    }
                }
                transition.setTos(nodes);
            } else {
                // 抛出异常
                Map<String, Object> variables = getToTaskMap(flowDelegate, fromTaskNode.getFormID(), toIDs);
                variables.put("title", token.getFlowInstance().getTitle());
                variables.put("fromTaskId", transition.getFromId());
                variables.put("submitButtonId", token.getTaskData().getSubmitButtonId());
                variables.put("multiselect", !(fromTaskNode.getForkMode() == ForkMode.SINGLE.getValue()));
                throw new JudgmentBranchFlowNotFoundException(variables);
            }
        } else {
            List<Node> tos = Lists.newArrayList();
            for (String toID : toIDs) {
                tos.add(flowDelegate.getTaskNode(toID));
            }
            transition.setTos(tos);
        }
    }

    /**
     * @param transition
     * @param token
     */
    private void resolveParallelForkTask(Transition transition, Token token) {
        TaskNode taskNode = (TaskNode) transition.getFrom();
        // 全部分支
        if (taskNode.getForkMode() == ForkMode.ALL.getValue()) {
            FlowDelegate flowDelegate = token.getFlowDelegate();
            List<String> toIDs = taskNode.getToIDs();
            List<Node> tos = new ArrayList<Node>();
            for (String toID : toIDs) {
                tos.add(flowDelegate.getTaskNode(toID));
            }
            transition.setTos(tos);
        } else if (taskNode.getForkMode() == ForkMode.MULTI.getValue()) {
            // 多路分支
            throw new WorkFlowException("");
        } else {
            // 单路分支
            resolveDirectTask(transition, token);
        }
    }

    /**
     * 判断网关分支条件
     *
     * @param flowDelegate
     * @param token
     * @return
     */
    private List<Direction> evaluateGatewayCondition(TaskNode fromNode, FlowDelegate flowDelegate, Token token) {
        List<Direction> validDirections = Lists.newArrayList();
        List<Direction> directions = flowDelegate.getDirections(fromNode.getId());
        if (CollectionUtils.isEmpty(directions)) {
            return validDirections;
        }

        List<String> checkedGatwayIds = Lists.newArrayList();
        List<String> currentGatwayIdPaths = Lists.newArrayList();
        directions.forEach(direction -> {
            GatewayElement gatewayElement = flowDelegate.getFlow().getGateway(direction.getToID());
            if (gatewayElement != null) {
                evaluateGatewayCondition(gatewayElement, validDirections, checkedGatwayIds, currentGatwayIdPaths, flowDelegate, token);
            }
        });
        return validDirections;
    }

    /**
     * @param gatewayElement
     * @param validDirections
     * @param checkedGatwayIds
     * @param currentGatwayIdPaths
     * @param flowDelegate
     * @param token
     */
    public void evaluateGatewayCondition(GatewayElement gatewayElement, List<Direction> validDirections, List<String> checkedGatwayIds,
                                         List<String> currentGatwayIdPaths, FlowDelegate flowDelegate, Token token) {
        checkedGatwayIds.add(gatewayElement.getId());
        currentGatwayIdPaths.add(gatewayElement.getId());
        List<Direction> directions = flowDelegate.getDirections(gatewayElement.getId());

        List<Direction> defaultDirections = Lists.newArrayList();
        List<Direction> conditionDirections = Lists.newArrayList();
        directions.forEach(direction -> {
            if (direction.isDefault()) {
                defaultDirections.add(direction);
            } else {
                conditionDirections.add(direction);
            }
        });

        List<Direction> taskValidDirections = Lists.newArrayList();
        List<Direction> gatewayValidDirections = Lists.newArrayList();
        conditionDirections.forEach(direction -> {
            // 指向网关的流向类型为3
            if (DirectionType.ToGateway.getValue().equals(direction.getType())) {
                GatewayElement nextGatewayElement = flowDelegate.getFlow().getGateway(direction.getToID());
                if (!checkedGatwayIds.contains(nextGatewayElement.getId()) && evaluate(flowDelegate, token, direction)) {
                    gatewayValidDirections.add(direction);
                }
            } else {
                // 环节流向
                if (evaluate(flowDelegate, token, direction)) {
                    taskValidDirections.add(direction);
                    token.getTaskData().setPreGatewayIds(direction.getToID(), Lists.newArrayList(currentGatwayIdPaths));
                }
            }
        });

        // 缺少流向
        if (CollectionUtils.isEmpty(taskValidDirections) && CollectionUtils.isEmpty(gatewayValidDirections)) {
            defaultDirections.forEach(direction -> {
                // 指向网关的流向类型为3
                if (DirectionType.ToGateway.getValue().equals(direction.getType())) {
                    GatewayElement nextGatewayElement = flowDelegate.getFlow().getGateway(direction.getToID());
                    evaluateGatewayCondition(nextGatewayElement, validDirections, checkedGatwayIds, currentGatwayIdPaths, flowDelegate, token);
                } else {
                    // 环节流向
                    validDirections.add(direction);
                    token.getTaskData().setPreGatewayIds(direction.getToID(), Lists.newArrayList(currentGatwayIdPaths));
                }
            });
        } else {
            // 网关流向
            gatewayValidDirections.forEach(direction -> {
                GatewayElement nextGatewayElement = flowDelegate.getFlow().getGateway(direction.getToID());
                evaluateGatewayCondition(nextGatewayElement, validDirections, checkedGatwayIds, currentGatwayIdPaths, flowDelegate, token);
            });
            // 环节流向
            validDirections.addAll(taskValidDirections);
        }
        currentGatwayIdPaths.remove(gatewayElement.getId());
    }

    /**
     * 判断分支条件
     *
     * @param token
     * @param direction
     * @return
     */
    private boolean evaluate(FlowDelegate flowDelegate, Token token, Direction direction) {
        List<String> conditions = direction.getConditions();
        List<String> conditionDatas = direction.getConditionDatas();
        if (CollectionUtils.isEmpty(conditions)) {
            return false;
        }

        List<Condition> conds = new ArrayList<Condition>();
        if (CollectionUtils.isNotEmpty(conditionDatas) && CollectionUtils.size(conditions) == CollectionUtils.size(conditionDatas)) {
            for (int index = 0; index < conditions.size(); index++) {
                conds.add(ConditionFactory.getCondition(conditions.get(index), conditionDatas.get(index)));
            }
        } else {
            for (int index = 0; index < conditions.size(); index++) {
                conds.add(ConditionFactory.getCondition(conditions.get(index)));
            }
        }
        // 获取表单数据
        TaskData taskData = token.getTaskData();
        // DytableApiFacade dytableApiFacade =
        // ApplicationContextHolder.getBean(DytableApiFacade.class);
        // FormAndDataBean rootFormDataBean =
        // dytableApiFacade.getFormData(taskData.getFormUuid(),
        // taskData.getDataUuid());
        // taskData.setRootFormData(taskData.getDataUuid(), rootFormDataBean);
        DyFormData dyFormData = taskData.getDyFormData(taskData.getDataUuid());
        if (dyFormData == null) {
            dyFormData = dyFormFacade.getDyFormData(taskData.getFormUuid(), taskData.getDataUuid(), false);
            taskData.setDyFormData(taskData.getDataUuid(), dyFormData);
        }

        // System.out.println(direction.getRawConditionExpression());
        Node to = flowDelegate.getToTaskNode(direction);
        StringBuilder expression = new StringBuilder();
        for (Condition condition : conds) {
            expression.append(condition.evaluate(token, to));
        }
        Object result = 0;
        try {
            result = Ognl.getValue(expression.toString(), new HashMap<String, String>());
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
        return (result instanceof Boolean) ? (Boolean) result : Integer.valueOf(1).equals(result);
    }

    /**
     * @param flowDelegate
     * @param directions
     * @return
     */
    private Map<String, Object> getToTaskMapByDirection(FlowDelegate flowDelegate, String fromID, List<Direction> directions) {
        List<Map<String, String>> nodes = new ArrayList<Map<String, String>>();
        for (Direction direction : directions) {
            Map<String, String> item = Maps.newHashMap();
            item.put("id", direction.getToID());
            item.put("name", direction.getName());
            item.put("directionId", direction.getId());
            item.put("type", direction.getType());
            item.put("fromId", flowDelegate.getFlow().getId() + "_" + fromID);
            item.put("sortOrder", direction.getSortOrder());
            item.put("remark", direction.getShowRemark() ? direction.getRemark() : StringUtils.EMPTY);
            nodes.add(item);
        }
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("toTasks", nodes);
        variables.put("useDirection", true);
        return variables;
    }

    /**
     * @param flowDelegate
     * @param fromID
     * @param toIDs
     * @return
     */
    private Map<String, Object> getToTaskMap(FlowDelegate flowDelegate, String fromID, List<String> toIDs) {
        Map<String, Object> variables = new HashMap<String, Object>();
        String flowDefId = flowDelegate.getFlow().getId();
        List<Map<String, String>> nodes = new ArrayList<Map<String, String>>();
        List<Direction> directions = flowDelegate.getDirections(fromID, toIDs);
        if (CollectionUtils.isNotEmpty(directions) && CollectionUtils.size(directions) == CollectionUtils.size(toIDs)) {
            for (Direction direction : directions) {
                Map<String, String> item = Maps.newHashMap();
                item.put("id", direction.getToID());
                item.put("name", direction.getName());
                item.put("directionId", direction.getId());
                item.put("type", direction.getType());
                item.put("fromId", flowDefId + "_" + fromID);
                item.put("sortOrder", direction.getSortOrder());
                item.put("remark", direction.getShowRemark() ? direction.getRemark() : StringUtils.EMPTY);
                nodes.add(item);
            }
            variables.put("toTasks", nodes);
            variables.put("useDirection", true);
        } else {
            for (String toID : toIDs) {
                Map<String, String> item = Maps.newHashMap();
                Node task = flowDelegate.getTaskNode(toID);
                if (StringUtils.isNotBlank(task.getId())) {
                    item.put("id", task.getId());
                    item.put("name", task.getName());
                    item.put("type", task.getType());
                    item.put("fromId", fromID);
                    nodes.add(item);
                }
            }
            variables.put("toTasks", nodes);
        }
        return variables;
    }

    /**
     * @param toNodes
     * @param flowDelegate
     * @return
     */
    private Map<String, Object> getToTaskMap(String fromId, List<Node> toNodes, FlowDelegate flowDelegate) {
        List<Map<String, String>> nodes = new ArrayList<Map<String, String>>();
        for (Node task : toNodes) {
            Map<String, String> item = Maps.newHashMap();
            if (StringUtils.isNotBlank(task.getId())) {
                item.put("id", task.getId());
                item.put("name", task.getName());
                item.put("type", task.getType());
                item.put("fromId", fromId);
                nodes.add(item);
            }
        }
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("toTasks", nodes);
        return variables;
    }

    /**
     * 计算直流流向
     *
     * @param transition
     * @param token
     */
    private void resolveDirectTask(Transition transition, Token token) {
        Node node = transition.getFrom();
        FlowDelegate flowDelegate = token.getFlowDelegate();

        // 判断是否为自由流程
        if (node instanceof TaskNode) {
            TaskNode taskNode = (TaskNode) node;
            String fromTaskId = node.getId();
            List<String> toIDs = taskNode.getToIDs();
            // 如果用户已经选择流向，则返回该流向
            String toTaskId = token.getTaskData().getToTaskId(fromTaskId);

            // 前端提交时过滤掉作为提交按钮的流向
            if (StringUtils.isBlank(toTaskId) && CollectionUtils.size(toIDs) > 1
                    && WorkFlowPrivilege.Submit.getCode().equals(token.getTaskData().getSubmitButtonId())
                    && !token.getTaskData().isDaemon()) {
                List<Direction> directions = flowDelegate.getDirections(fromTaskId, toIDs);
                directions = directions.stream().filter(direction -> !direction.isUseAsButton()).collect(Collectors.toList());
                if (CollectionUtils.size(directions) > 0) {
                    toIDs = directions.stream().map(direction -> direction.getToID()).collect(Collectors.toList());
                    taskNode.setToIDs(toIDs);
                }
            }

            if (StringUtils.isBlank(toTaskId) && toIDs.size() > 1) {
                // 用户选择的下一流向ID
                Direction toDirection = null;
                String toDirectionId = token.getTaskData().getToDirectionId(fromTaskId);
                if (StringUtils.isNotBlank(toDirectionId)) {
                    toDirection = flowDelegate.getDirection(toDirectionId);
                }
                List<String> toDirectionIds = Lists.newArrayList();
                if (toDirection == null && StringUtils.contains(toDirectionId, Separator.SEMICOLON.getValue())) {
                    toDirectionIds = Arrays.asList(StringUtils.split(toDirectionId, Separator.SEMICOLON.getValue()));
                }
                if (CollectionUtils.isNotEmpty(toDirectionIds)) {
                    List<Node> tos = new ArrayList<Node>();
                    toDirectionIds.forEach(directionId -> {
                        Direction direction = flowDelegate.getDirection(directionId);
                        Node to = flowDelegate.getToTaskNode(direction);
                        tos.add(to);
                    });
                    transition.setTos(tos);
                } else if (toDirection != null && StringUtils.equals(fromTaskId, toDirection.getFromID())) {
                    List<Node> tos = new ArrayList<Node>();
                    Node to = flowDelegate.getToTaskNode(toDirection);
                    tos.add(to);
                    transition.setTos(tos);
                } else {
                    // 抛出异常
                    // 子流程自动提交且环节ID相关时需区分不同的流程
                    String flowDefId = flowDelegate.getFlow().getId();
                    String fromId = fromTaskId;
                    if (token.getFlowInstance().getParent() != null) {
                        fromId = flowDefId + "_" + fromTaskId;
                    }
                    Map<String, Object> variables = getToTaskMap(flowDelegate, fromTaskId, toIDs);
                    variables.put("title", token.getFlowInstance().getTitle());
                    if (token.getFlowInstance().getParent() != null) {
                        variables.put("flowName", token.getFlowInstance().getName());
                    }
                    variables.put("fromTaskId", fromId);
                    variables.put("submitButtonId", token.getTaskData().getSubmitButtonId());
                    // 退回后允许直接提交至本环节
                    addTaskOfAllowReturnAfterRollbackIfRequired(node, variables, token);
                    throw new JudgmentBranchFlowNotFoundException(variables);
                }
            } else {
                toTaskId = StringUtils.isBlank(toTaskId) ? node.getToID() : toTaskId;
                Direction direction = flowDelegate.getDirection(fromTaskId, toTaskId);
                List<Node> tos = new ArrayList<Node>();
                if (direction != null) {
                    Node to = flowDelegate.getToTaskNode(direction);
                    tos.add(to);
                } else {
                    Node to = flowDelegate.getTaskNode(toTaskId);
                    tos.add(to);
                }
                transition.setTos(tos);
                // 退回后允许直接提交至本环节
                addTaskOfAllowReturnAfterRollbackIfRequired(node, tos, token);
            }
        } else {
            Node to = flowDelegate.getTaskNode(node.getToID());
            List<Node> tos = new ArrayList<Node>();
            tos.add(to);
            transition.setTos(tos);
        }
    }

    /**
     * @param fromTaskNode
     * @param tos
     * @param token
     */
    private void addTaskOfAllowReturnAfterRollbackIfRequired(Node fromTaskNode, List<Node> tos, Token token) {
        if (ForkMode.SINGLE.getValue() != fromTaskNode.getForkMode()) {
            return;
        }
        String fromTaskId = fromTaskNode.getId();
        TaskData taskData = token.getTaskData();
        if (StringUtils.isNotBlank(taskData.getToTaskId(fromTaskId))
                || StringUtils.isNotBlank(taskData.getToDirectionId(fromTaskId))) {
            return;
        }
        String returnTaskId = taskData.getTaskOfAllowReturnAfterRollback(fromTaskId);
        if (StringUtils.isBlank(returnTaskId)) {
            return;
        }
        // 可返回退回的环节已经在目标环节中直接返回
        for (Node node : tos) {
            if (StringUtils.equals(node.getId(), returnTaskId)) {
                return;
            }
        }

        // 抛出异常
        Map<String, Object> variables = getToTaskMap(fromTaskId, tos, token.getFlowDelegate());
        variables.put("fromTaskId", fromTaskId);

        // 附加退回环节
        appendReturnAfterRollback(fromTaskId, returnTaskId, variables, token);

        throw new MultipleJudgmentBranchFlowException(variables);
    }

    /**
     * @param node
     * @param variables
     * @param token
     */
    private void addTaskOfAllowReturnAfterRollbackIfRequired(Node fromTaskNode, Map<String, Object> variables,
                                                             Token token) {
        if (ForkMode.SINGLE.getValue() != fromTaskNode.getForkMode()) {
            return;
        }
        String fromTaskId = fromTaskNode.getId();
        TaskData taskData = token.getTaskData();
        if (StringUtils.isNotBlank(taskData.getToTaskId(fromTaskId))
                || StringUtils.isNotBlank(taskData.getToDirectionId(fromTaskId))) {
            return;
        }
        String returnTaskId = taskData.getTaskOfAllowReturnAfterRollback(fromTaskId);
        if (StringUtils.isBlank(returnTaskId)) {
            return;
        }
        // 附加退回环节
        appendReturnAfterRollback(fromTaskId, returnTaskId, variables, token);
    }

    /**
     * @param fromTaskId
     * @param returnTaskId
     * @param nodes
     * @param token
     */
    @SuppressWarnings("unchecked")
    private void appendReturnAfterRollback(String fromTaskId, String returnTaskId, Map<String, Object> variables,
                                           Token token) {
        List<Map<String, String>> nodes = (List<Map<String, String>>) variables.get("toTasks");
        if (CollectionUtils.isEmpty(nodes)) {
            return;
        }
        for (Map<String, String> node : nodes) {
            if (StringUtils.equals(returnTaskId, node.get("id"))) {
                return;
            }
        }
        // 添加退回的环节
        Map<String, String> item = Maps.newHashMap();
        Node task = token.getFlowDelegate().getTaskNode(returnTaskId);
        item.put("id", task.getId());
        item.put("name", task.getName());
        item.put("type", task.getType());
        item.put("fromId", fromTaskId);
        item.put("returnAfterRollback", "true");
        nodes.add(item);
    }

    /**
     * 查检开始结点的分支模式、结束结点的聚合模式的正确性，并设置任务数据所需要的运转模式
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.core.TransitionResolver#checkAndPrepare(com.wellsoft.pt.bpm.engine.core.Transition, com.wellsoft.pt.bpm.engine.core.Token)
     */
    @Override
    public void checkAndPrepare(Transition transition, Token token) {
        List<Node> tos = transition.getTos();
        if (tos.isEmpty()) {
            return;
        }

        TaskData taskData = token.getTaskData();

        Node form = transition.getFrom();
        String fromTaskId = form.getId();

        // 撤销操作，直接返回
        if (taskData.isCancel(fromTaskId) || taskData.isRollback(fromTaskId) || taskData.isGotoTask(fromTaskId)) {
            return;
        }

        // 开始结点的分支模式
        int forkMode = form.getForkMode();

        // 单路分支->单路聚合、单路分支->多路聚合、单路分支->全部聚合
        if (forkMode == ForkMode.SINGLE.getValue()) {
            for (Node to : tos) {
                String toTaskId = to.getId();
                int joinMode = to.getJoinMode();
                if (joinMode == JoinMode.SINGLE.getValue()) {
                    taskData.setTaskJoining(toTaskId, false);
                } else if (joinMode == JoinMode.MULTI.getValue()) {
                    taskData.setTaskJoining(toTaskId, true);
                } else if (joinMode == JoinMode.ALL.getValue()) {
                    taskData.setTaskJoining(toTaskId, true);
                }
            }
            taskData.setTaskForking(fromTaskId, false);
            return;
        }

        // 多路分支->单路聚合
        if (forkMode == ForkMode.MULTI.getValue()) {
            // 多路分支-> 多路聚合、全部聚合
            for (Node to : tos) {
                if (to.getJoinMode() == JoinMode.MULTI.getValue() || to.getJoinMode() == JoinMode.ALL.getValue()) {
                    throw new WorkFlowException("多路分支节点[" + form.getName() + "]不能流转到多路或全部聚合结点[" + to.getName() + "]!");
                    // taskData.setTaskJoining(to.getId(), true);
                }
            }
            taskData.setTaskForking(fromTaskId, true);
            return;
        }
        // 全部分支->单路聚合
        if (forkMode == ForkMode.ALL.getValue()) {
            // 多路分支-> 多路聚合、全部聚合
            for (Node to : tos) {
                if (to.getJoinMode() == JoinMode.MULTI.getValue() || to.getJoinMode() == JoinMode.ALL.getValue()) {
                    throw new WorkFlowException("全部分支节点[" + form.getName() + "]不能流转到多路或全部聚合结点[" + to.getName() + "]!");
                    // taskData.setTaskJoining(to.getId(), true);
                }
            }
            taskData.setTaskForking(fromTaskId, true);
            return;
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.core.TransitionResolver#resolveJoinTask(com.wellsoft.pt.bpm.engine.core.Transition, com.wellsoft.pt.bpm.engine.core.Token)
     */
    @Override
    public boolean resolveJoinTask(Transition transition, Token token) {
        List<Node> tos = transition.getTos();
        if (tos.size() == 1) {
            Node to = tos.get(0);
            if (to instanceof TaskNode) {
                TaskNode taskNode = (TaskNode) to;
                // 多路聚合、全部聚合
                if (taskNode.getJoinMode() == JoinMode.MULTI.getValue()
                        || taskNode.getJoinMode() == JoinMode.ALL.getValue()) {
                    // 获取当前分支
                    TaskInstance currentTaskInstance = token.getTask();
                    TaskBranch taskBranch = taskBranchService.getByCurrentTaskInstUuid(currentTaskInstance.getUuid());
                    if (taskBranch == null) {
                        String parallelTaskInstUuid = currentTaskInstance.getParallelTaskInstUuid();
                        // 分支已完成，不需要再聚合
                        if (StringUtils.isNotBlank(parallelTaskInstUuid) && taskBranchService
                                .isBranchTaskCompletedByParallelTaskInstUuid(parallelTaskInstUuid)) {
                            return false;
                        }
                        return true;
                    }

                    // 分支不需要聚合或已聚合
                    if (!Boolean.TRUE.equals(taskBranch.getIsMerge())
                            || StringUtils.isNotBlank(taskBranch.getJoinTaskInstUuid())) {
                        return false;
                    }

                    // 获取未完成的需要合并的分支数量
                    long unfinishedMergeBranchCount = taskBranchService.countUnfinishedMergeBranch(currentTaskInstance);

                    // 判断是否最后一个需要聚合的分支
                    return unfinishedMergeBranchCount <= 1;
                } else {
                    // 单一聚合
                    return true;
                }
            }
        }
        return true;
    }

}
