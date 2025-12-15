/*
 * @(#)4/27/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.support.listener;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkTodoTaskEntity;
import com.wellsoft.pt.app.dingtalk.entity.DingtalkWorkRecordEntity;
import com.wellsoft.pt.app.dingtalk.facade.service.DingtalkConfigFacadeService;
import com.wellsoft.pt.app.dingtalk.service.DingtalkTodoTaskService;
import com.wellsoft.pt.app.dingtalk.service.DingtalkUserService;
import com.wellsoft.pt.app.dingtalk.service.DingtalkWorkRecordService;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkApiV2Utils;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkGroupChatUtils;
import com.wellsoft.pt.app.dingtalk.vo.DingtalkConfigVo;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import com.wellsoft.pt.jpa.event.EventListenerPair;
import com.wellsoft.pt.jpa.event.WellptTransactionalEventListener;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.event.WorkDoneEvent;
import com.wellsoft.pt.workflow.event.WorkTodoEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/27/25.1	    zhulh		4/27/25		    Create
 * </pre>
 * @date 4/27/25
 */
@Component
public class DingtalkWorkDoneSendListener extends WellptTransactionalEventListener<WorkDoneEvent> {

    @Autowired
    private DingtalkConfigFacadeService dingtalkConfigFacadeService;

    @Autowired
    private DingtalkWorkRecordService dingtalkWorkRecordService;

    @Autowired
    private DingtalkTodoTaskService dingtalkTodoTaskService;

    @Autowired
    private DingtalkUserService dingtalkUserService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public boolean onAddEvent(List<EventListenerPair> eventListenerPairs, ApplicationEvent event) {
        boolean needExecute = true;
        WorkDoneEvent doneEvent = (WorkDoneEvent) event;
        for (EventListenerPair eventListenerPair : eventListenerPairs) {
            if (eventListenerPair.getEvent() instanceof WorkTodoEvent) {
                WorkTodoEvent todoEvent = (WorkTodoEvent) eventListenerPair.getEvent();
                Set<String> userIds = todoEvent.getUserIds();
                TaskInstance taskInstance = todoEvent.getTaskInstance();
                if (userIds == null || taskInstance == null) {
                    continue;
                }
                if (false == StringUtils.equals(doneEvent.getTaskInstUuid(), taskInstance.getUuid())) {
                    continue;
                }
                if (userIds.contains(doneEvent.getUserId())) {
                    needExecute = false;// 当前待办更新事件不执行
                    userIds.remove(doneEvent.getUserId());// 移除待办用户
                    if (userIds.isEmpty()) {
                        // 办理人为空，忽略待办推送事件
                        eventListenerPair.markIgnoreExecute();
                    }
                }
            } else if (eventListenerPair.getEvent() instanceof WorkDoneEvent) {
                WorkDoneEvent doneEvent2 = (WorkDoneEvent) eventListenerPair.getEvent();
                if (doneEvent.equals(doneEvent2) && this.equals(eventListenerPair.getListener())) {
                    needExecute = true;
                    eventListenerPair.markIgnoreExecute();// 忽略重复的事件
                }
            }
        }
        return needExecute;
    }

    @Override
    public void onApplicationEvent(WorkDoneEvent event) {
        String flowInstUuid = event.getFlowInstUuid();
        String taskInstUuid = event.getTaskInstUuid();
        // 流程数据未持久化（比如办理人异常）
        if (StringUtils.isBlank(flowInstUuid) || StringUtils.isBlank(taskInstUuid)) {
            return;
        }
        String system = event.getSystem();
        String tenant = event.getTenant();
        DingtalkConfigVo dingtalkConfigVo = dingtalkConfigFacadeService.getVoBySystemAndTenant(system, tenant);
        if (BooleanUtils.isNotTrue(dingtalkConfigVo.getEnabled())) {
            return;
        }
        DingtalkConfigVo.DingtalkConfiguration dingtalkConfiguration = dingtalkConfigVo.getConfiguration();
        if (dingtalkConfiguration == null || (!dingtalkConfiguration.isEnabledTodoTask()
                && !dingtalkConfiguration.isEnabledPushMsg() && !dingtalkConfiguration.getGroupChat().isEnabled())) {
            return;
        }

        TaskData taskData = event.getTaskData();
        List<DingtalkWorkRecordEntity> workRecords = dingtalkWorkRecordService.listByTaskInstUuidAndTypeAndState(taskInstUuid, DingtalkWorkRecordEntity.Type.SYSTEM, DingtalkWorkRecordEntity.State.Sent);
        if (DingtalkGroupChatUtils.isGroupChat(workRecords)) {
            DingtalkWorkRecordEntity groupChatEntity = DingtalkGroupChatUtils.getGroupChat(workRecords);
//            // 提交环节，发送群聊消息
//            if (WorkFlowOperation.isActionCodeOfSubmit(taskData.getActionCode(taskInstUuid))
//                    || WorkFlowOperation.isActionTypeOfSubmit(taskData.getActionType(taskInstUuid + taskData.getUserId()))) {
            sendChatMessage(groupChatEntity, event, dingtalkConfigVo);
//            }
        }

        // 更新钉钉待办任务
        updateTodoTask(event.getTaskInstUuid(), event.getFlowInstUuid(), event, dingtalkConfigVo);
    }

    private void sendChatMessage(DingtalkWorkRecordEntity groupChatEntity, WorkDoneEvent event, DingtalkConfigVo dingtalkConfigVo) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        TaskData taskData = event.getTaskData();
        String key = taskData.getTaskInstUuid() + taskData.getUserId();
        String opinionText = taskData.getOpinionText(key);
        String actionType = taskData.getActionType(key);
        String actionName = WorkFlowOperation.getName(actionType);
        if (StringUtils.isBlank(actionName)) {
            actionName = actionType;
        }
        if (WorkFlowOperation.TRANSFER.equals(actionType) || WorkFlowOperation.COUNTER_SIGN.equals(actionType)
                || WorkFlowOperation.ADD_SIGN.equals(actionType)
                || WorkFlowOperation.DELEGATION.equals(actionType)
                || WorkFlowOperation.TAKE_BACK_TODO_DELEGATION.equals(actionType)) {
            String actionUserNameKey = StringUtils.substring(actionType, 0, 1).toLowerCase() + StringUtils.substring(actionType, 1) + "UserNames";
            String actionUserNames = Objects.toString(taskData.get(actionUserNameKey), StringUtils.EMPTY);
            String actionWidthUser = StringUtils.isNotBlank(actionUserNames) ? (actionName + " " + actionUserNames) : actionName;
            scheduledExecutorService.schedule(() -> {
                DingtalkApiV2Utils.sendUserChatMessage(userDetails, actionWidthUser, opinionText, groupChatEntity, dingtalkConfigVo);
            }, 5, TimeUnit.SECONDS);
        } else {
            DingtalkApiV2Utils.sendUserChatMessage(userDetails, actionName, opinionText, groupChatEntity, dingtalkConfigVo);
        }
    }

    private void updateTodoTask(String taskInstUuid, String flowInstUuid, WorkDoneEvent event, DingtalkConfigVo dingtalkConfigVo) {
        if (!dingtalkConfigVo.getConfiguration().isEnabledTodoTask()) {
            return;
        }

        List<DingtalkTodoTaskEntity> todoTaskEntities = dingtalkTodoTaskService.listByTaskInstUuidAndState(taskInstUuid, DingtalkTodoTaskEntity.State.Sent);
        if (CollectionUtils.isEmpty(todoTaskEntities)) {
            return;
        }

        DingtalkTodoTaskEntity todoTaskEntity = todoTaskEntities.get(0);
        List<String> oaUserIds = getTodoUserIds(taskInstUuid, event.getTaskData(), event);
        Map<String, String> userIdMap = dingtalkUserService.listUnionIdMapByOaUserIds(oaUserIds, dingtalkConfigVo.getAppId());
        if (MapUtils.isEmpty(userIdMap) && WorkFlowOperation.isActionCodeOfSubmit(event.getTaskData().getActionCode(taskInstUuid))) {
            DingtalkApiV2Utils.completeTodoTask(todoTaskEntity, dingtalkConfigVo);
            dingtalkTodoTaskService.save(todoTaskEntity);
        } else {
            if (MapUtils.isNotEmpty(userIdMap)) {
                todoTaskEntity.setOaUserId(org.apache.commons.lang.StringUtils.join(userIdMap.values(), Separator.SEMICOLON.getValue()));
                todoTaskEntity.setUserUnionId(org.apache.commons.lang.StringUtils.join(userIdMap.keySet(), Separator.SEMICOLON.getValue()));

                DingtalkApiV2Utils.updateTodoTask(todoTaskEntity, dingtalkConfigVo);
                dingtalkTodoTaskService.save(todoTaskEntity);
            }
        }
    }

    private List<String> getTodoUserIds(String taskInstUuid, TaskData taskData, WorkDoneEvent event) {
        List<String> todoUserIds = taskService.getTodoUserIds(taskInstUuid);
        Map<String, String> userIdMap = workflowOrgService.getUsersByIds(todoUserIds, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(taskData.getToken()));
        todoUserIds = Arrays.asList(userIdMap.keySet().toArray(new String[0]));
        return todoUserIds;
    }

}
