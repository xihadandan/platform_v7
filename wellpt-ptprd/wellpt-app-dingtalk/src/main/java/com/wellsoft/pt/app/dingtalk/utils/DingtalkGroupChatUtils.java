/*
 * @(#)4/24/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.utils;

import com.wellsoft.pt.app.dingtalk.entity.DingtalkWorkRecordEntity;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkConfigVo;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.TaskElement;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.enums.TaskNodeType;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.workflow.event.WorkTodoEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/24/25.1	    zhulh		4/24/25		    Create
 * </pre>
 * @date 4/24/25
 */
public class DingtalkGroupChatUtils {

    /**
     * @param event
     * @param taskInstance
     * @return
     */
    public static FlowDelegate getFlowDelegate(WorkTodoEvent event, TaskInstance taskInstance) {
        Token token = event.getTaskData().getToken();
        if (token != null) {
            return token.getFlowDelegate();
        }
        return FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
    }

    public static boolean isGroupChat(List<DingtalkWorkRecordEntity> workRecordEntities) {
        List<DingtalkWorkRecordEntity> entities = workRecordEntities.stream().filter(entity -> entity.getType() == null || DingtalkWorkRecordEntity.Type.SYSTEM.equals(entity.getType())).collect(Collectors.toList());
        return CollectionUtils.size(entities) == 1 && BooleanUtils.isTrue(entities.get(0).getGroupChat());
    }

    public static DingtalkWorkRecordEntity getGroupChat(List<DingtalkWorkRecordEntity> workRecords) {
        List<DingtalkWorkRecordEntity> entities = workRecords.stream().filter(entity -> entity.getType() == null || DingtalkWorkRecordEntity.Type.SYSTEM.equals(entity.getType())).collect(Collectors.toList());
        return CollectionUtils.size(entities) == 1 ? entities.get(0) : null;
    }

    public static boolean isMatchGroupChat(List<String> todoUserIds, WorkTodoEvent event, DingtalkConfigVo.DingtalkConfiguration dingtalkConfiguration) {
        DingtalkConfigVo.DingtalkGroupChat groupChat = dingtalkConfiguration.getGroupChat();
        if (groupChat == null || !groupChat.isEnabled()) {
            return false;
        }

        TaskInstance taskInstance = event.getTaskInstance();
        FlowDelegate flowDelegate = getFlowDelegate(event, taskInstance);

        TaskElement taskElement = flowDelegate.getFlow().getTask(taskInstance.getId());

        // 发起方式判断
        if (!(isMatchMultiUserTask(groupChat, taskElement, todoUserIds) || isMatchCollaborationTask(groupChat, taskElement, todoUserIds))) {
            return false;
        }

        // 流程匹配
        return isMatchFlow(groupChat, flowDelegate, event);
    }

    /**
     * @param groupChat
     * @param flowDelegate
     * @param event
     * @return
     */
    private static boolean isMatchFlow(DingtalkConfigVo.DingtalkGroupChat groupChat, FlowDelegate flowDelegate, WorkTodoEvent event) {
        String scope = groupChat.getScope();
        List<String> values = groupChat.getValues();
        if (StringUtils.equals("all", scope)) {
            return true;
        } else if (StringUtils.equals("flow", scope)) {
            String flowDefId = event.getFlowInstance().getId();
            String flowCategoryId = "FLOW_CATEGORY_" + flowDelegate.getFlow().getProperty().getCategorySN();
            return CollectionUtils.isNotEmpty(values) && (values.contains(flowDefId) || values.contains(flowCategoryId));
        } else if (StringUtils.equals("task", scope)) {
            String flowDefId = event.getFlowInstance().getId();
            String taskId = event.getTaskInstance().getId();
            return CollectionUtils.isNotEmpty(values) && values.contains(flowDefId + ":" + taskId);
        }
        return false;
    }

    /**
     * @param groupChat
     * @param taskElement
     * @param todoUserIds
     * @return
     */
    private static boolean isMatchMultiUserTask(DingtalkConfigVo.DingtalkGroupChat groupChat, TaskElement taskElement, List<String> todoUserIds) {
        List<String> startModes = groupChat.getStartModes();
        if (CollectionUtils.isEmpty(startModes)) {
            return false;
        }
        return TaskNodeType.UserTask.getValue().equals(taskElement.getType()) && startModes.contains("multiUser")
                && BooleanUtils.isNotTrue(taskElement.isAnyone()) && CollectionUtils.size(todoUserIds) > 1;
    }

    /**
     * @param groupChat
     * @param taskElement
     * @param todoUserIds
     * @return
     */
    private static boolean isMatchCollaborationTask(DingtalkConfigVo.DingtalkGroupChat groupChat, TaskElement taskElement, List<String> todoUserIds) {
        List<String> startModes = groupChat.getStartModes();
        if (CollectionUtils.isEmpty(startModes)) {
            return false;
        }
        return TaskNodeType.CollaborationTask.getValue().equals(taskElement.getType()) && startModes.contains("collaboration")
                && CollectionUtils.size(todoUserIds) > 1;
    }

}
