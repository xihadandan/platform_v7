/*
 * @(#)2013-3-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.access;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.entity.TaskDelegation;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.bpm.engine.enums.ActionCode;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.service.FlowUserJobIdentityService;
import com.wellsoft.pt.bpm.engine.service.TaskDelegationService;
import com.wellsoft.pt.bpm.engine.service.TaskOperationService;
import com.wellsoft.pt.bpm.engine.support.SidGranularity;
import com.wellsoft.pt.jpa.comparator.IdEntityComparators;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 解析历史环节中的表单数据信息
 * A转办给B，环节办理人设置为之前所有环节办理人，要求A和B同时收到，但是目前只有A收到。
 * （物控端发起-策略备库管理流程）
 * 计算环节办理人的期望结果如下：
 * 1、A转办给B，B提交，则计B。
 * 2、A会签给B，B提交后，A再提交，则计A。
 * 3、退回/直接退回/提交等人员。
 * 综上现象，则以动作影响为离开环节的人员为计。
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-15.1	zhulh		2013-3-15		Create
 * </pre>
 * @date 2013-3-15
 */
@Component
public class TaskHistoryIdentityResolver extends AbstractIdentityResolver {

    @Autowired
    private TaskOperationService taskOperationService;
//    @Autowired
//    private WfTaskInstanceTodoUserService wfTaskInstanceTodoUserService;

    @Autowired
    private TaskDelegationService taskDelegationService;

    @Autowired
    private FlowUserJobIdentityService flowUserJobIdentityService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.access.IdentityResolver#resolve(com.wellsoft.pt.bpm.engine.node.Node, com.wellsoft.pt.bpm.engine.core.Token, java.util.List)
     */
    @Override
    public List<FlowUserSid> resolve(Node node, Token token, List<String> raws, ParticipantType participantType,
                                     String sidGranularity) {
        if (CollectionUtils.isEmpty(raws) || token.getFlowInstance() == null
                || StringUtils.isBlank(token.getFlowInstance().getUuid())) {
            return Collections.emptyList();
        }

        Set<FlowUserSid> userIdSet = new LinkedHashSet<FlowUserSid>(0);
        String flowInstUuid = token.getFlowInstance().getUuid();
        // 环节操作记录
        List<TaskOperation> taskOperations = taskOperationService.getByFlowInstUuidAndTaskIds(flowInstUuid, raws);
        // 获取办理人操作的环节实例的操作
        List<TaskOperation> actionTaskOperations = filterActionTaskOperations(taskOperations);
        // 获取最新环节实例的操作
        taskOperations = filterLatestTaskOperations(actionTaskOperations, raws);
        // 取历史环节操作人
        Map<FlowUserSid, String> identityUuidMap = Maps.newHashMap();
        for (TaskOperation taskOperation : taskOperations) {
            String userId = taskOperation.getAssignee();
            String userName = taskOperation.getAssigneeName();
            Integer actionCode = taskOperation.getActionCode();
            // 委托提交取受拖人
            if (ActionCode.DELEGATION_SUBMIT.getCode().equals(actionCode) && StringUtils.isNotBlank(userId)) {
                List<TaskDelegation> taskDelegations = taskDelegationService.listByTrusteeAndTaskInstUuid(userId, taskOperation.getTaskInstUuid());
                taskDelegations.forEach(taskDelegation -> {
                    FlowUserSid flowUserSid = new FlowUserSid(taskDelegation.getConsignor(), taskDelegation.getConsignorName(), SidGranularity.USER);
                    userIdSet.add(flowUserSid);
                    if (StringUtils.isNotBlank(taskDelegation.getTaskIdentityUuid()) && ParticipantType.TodoUser.equals(participantType)) {
                        identityUuidMap.put(flowUserSid, taskDelegation.getTaskIdentityUuid());
                    }
                });
            } else {
                if (StringUtils.isNotBlank(userId)) {
                    FlowUserSid flowUserSid = new FlowUserSid(userId, userName, SidGranularity.USER);
                    userIdSet.add(flowUserSid);
                    if (StringUtils.isNotBlank(taskOperation.getTaskIdentityUuid()) && ParticipantType.TodoUser.equals(participantType)) {
                        identityUuidMap.put(flowUserSid, taskOperation.getTaskIdentityUuid());
                    }
                }
            }
        }

        if (MapUtils.isNotEmpty(identityUuidMap)) {
            flowUserJobIdentityService.addUserJobIdentity(identityUuidMap);
        }

//        List<WfTaskInstanceTodoUserEntity> todoUserEntities = wfTaskInstanceTodoUserService.getListByFlowInstUuidAndTaskIds(flowInstUuid, raws);
//        IdentityResolverStrategy.addJobPath(userIdSet, todoUserEntities);
        return Arrays.asList(userIdSet.toArray(new FlowUserSid[0]));
    }


    /**
     * 如何描述该方法
     *
     * @param taskOperations
     * @return
     */
    private List<TaskOperation> filterActionTaskOperations(List<TaskOperation> taskOperations) {
        List<TaskOperation> actionTaskOperations = Lists.newArrayList();
        // 取历史环节操作人
        for (TaskOperation taskOperation : taskOperations) {
            Integer actionCode = taskOperation.getActionCode();
            switch (actionCode) {
                case 1:
                case 3:
                case 4:
                case 5:
                case 27:
                    // 提交 SUBMIT(1)
                    // 转办提交 TRANSFER_SUBMIT(3)
                    // 退回 ROLLBACK(4)
                    // 直接退回 DIRECT_ROLLBACK(5)
                    // 委托提交 DELEGATION_SUBMIT(27)
                    actionTaskOperations.add(taskOperation);
                    break;
                default:
                    break;
            }
        }
        return actionTaskOperations;
    }

    /**
     * @param taskOperations
     * @return
     */
    private List<TaskOperation> filterLatestTaskOperations(List<TaskOperation> taskOperations, List<String> taskIds) {
        if (CollectionUtils.isEmpty(taskOperations)) {
            return taskOperations;
        }

        // 获取环节的最新操作
        Collections.sort(taskOperations, IdEntityComparators.CREATE_TIME_DESC);
        // 按环节ID分组
        Map<String, List<TaskOperation>> hisTaskOperationMap = Maps.newHashMap();
        for (String taskId : taskIds) {
            for (TaskOperation taskOperation : taskOperations) {
                if (StringUtils.equals(taskId, taskOperation.getTaskId())) {
                    if (!hisTaskOperationMap.containsKey(taskId)) {
                        hisTaskOperationMap.put(taskId, new ArrayList<TaskOperation>());
                    }
                    hisTaskOperationMap.get(taskId).add(taskOperation);
                }
            }
        }

        // 取环节ID最新的环节实例的所有操作
        List<TaskOperation> latestTaskOperations = Lists.newArrayList();
        for (Entry<String, List<TaskOperation>> entry : hisTaskOperationMap.entrySet()) {
            List<TaskOperation> hisTaskOperations = entry.getValue();
            TaskOperation hisTaskOperation = hisTaskOperations.get(0);
            String taskInstUuid = hisTaskOperation.getTaskInstUuid();
            for (TaskOperation operation : hisTaskOperations) {
                if (StringUtils.equals(taskInstUuid, operation.getTaskInstUuid())) {
                    latestTaskOperations.add(operation);
                }
            }
        }
        return latestTaskOperations;
    }

    /**
     * @param flowInstUuid
     * @param fromTaskId
     * @param toTaskId
     * @return
     */
    public List<String> getDoneUserIds(String flowInstUuid, String fromTaskId, String toTaskId) {
        // 环节操作记录
        List<TaskOperation> taskOperations = taskOperationService.getByFlowInstUuid(flowInstUuid);
        Collections.sort(taskOperations, IdEntityComparators.CREATE_TIME_DESC);
        boolean startOpt = false;
        boolean endOpt = false;
        List<TaskOperation> doneTaskOperations = Lists.newArrayList();
        for (TaskOperation taskOperation : taskOperations) {
            if (!startOpt && StringUtils.equals(fromTaskId, taskOperation.getTaskId())) {
                startOpt = true;
            }
            if (startOpt && !endOpt) {
                doneTaskOperations.add(taskOperation);
            }
            if (StringUtils.equals(toTaskId, taskOperation.getTaskId())) {
                endOpt = true;
            } else if (endOpt) {
                break;
            }
        }
        doneTaskOperations = filterDoneActionTaskOperations(doneTaskOperations);

        // 取历史环节操作人
        List<String> doneUserIds = Lists.newArrayList();
        for (TaskOperation taskOperation : doneTaskOperations) {
            doneUserIds.add(taskOperation.getAssignee());
        }
        return doneUserIds;
    }

    /**
     * @param taskOperations
     * @return
     */
    private List<TaskOperation> filterDoneActionTaskOperations(List<TaskOperation> taskOperations) {
        List<TaskOperation> actionTaskOperations = Lists.newArrayList();
        // 取历史环节操作人
        for (TaskOperation taskOperation : taskOperations) {
            Integer actionCode = taskOperation.getActionCode();
            switch (actionCode) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 7:
                case 8:
                case 27:
                    // 提交 SUBMIT(1)
                    // 会签提交 COUNTER_SIGN_SUBMIT(2)
                    // 转办提交 TRANSFER_SUBMIT(3)
                    // 退回 ROLLBACK(4)
                    // 直接退回 DIRECT_ROLLBACK(5)
                    // 转办 TRANSFER(7)
                    // 会签 COUNTER_SIGN(8)
                    // 委托提交 DELEGATION_SUBMIT(27)
                    actionTaskOperations.add(taskOperation);
                    break;
                default:
                    break;
            }
        }
        return actionTaskOperations;
    }

}
