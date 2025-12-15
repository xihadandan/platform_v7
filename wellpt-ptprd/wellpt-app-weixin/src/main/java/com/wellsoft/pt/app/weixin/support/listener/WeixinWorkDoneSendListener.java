/*
 * @(#)5/26/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.support.listener;

import com.wellsoft.pt.app.weixin.entity.WeixinWorkRecordEntity;
import com.wellsoft.pt.app.weixin.facade.service.WeixinConfigFacadeService;
import com.wellsoft.pt.app.weixin.service.WeixinWorkRecordService;
import com.wellsoft.pt.app.weixin.utils.WeixinApiUtils;
import com.wellsoft.pt.app.weixin.utils.WeixinGroupChatUtils;
import com.wellsoft.pt.app.weixin.vo.WeixinConfigVo;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.jpa.event.EventListenerPair;
import com.wellsoft.pt.jpa.event.WellptTransactionalEventListener;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.workflow.event.WorkDoneEvent;
import com.wellsoft.pt.workflow.event.WorkTodoEvent;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 5/26/25.1	    zhulh		5/26/25		    Create
 * </pre>
 * @date 5/26/25
 */
@Component
public class WeixinWorkDoneSendListener extends WellptTransactionalEventListener<WorkDoneEvent> {

    @Autowired
    private WeixinConfigFacadeService weixinConfigFacadeService;

    @Autowired
    private WeixinWorkRecordService weixinWorkRecordService;

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
        // String tenant = event.getTenant();
        WeixinConfigVo weixinConfigVo = weixinConfigFacadeService.getVoBySystem(system);
        if (BooleanUtils.isNotTrue(weixinConfigVo.getEnabled())) {
            return;
        }
        WeixinConfigVo.WeixinConfiguration weixinConfiguration = weixinConfigVo.getConfiguration();
        if (weixinConfiguration == null ||
                (!weixinConfiguration.isEnabledPushMsg() && !weixinConfiguration.getGroupChat().isEnabled())) {
            return;
        }

        // TaskData taskData = event.getTaskData();
        List<WeixinWorkRecordEntity> workRecords = weixinWorkRecordService.listByTaskInstUuidAndTypeAndState(taskInstUuid, WeixinWorkRecordEntity.Type.SYSTEM, WeixinWorkRecordEntity.State.Sent);
        if (WeixinGroupChatUtils.isGroupChat(workRecords)) {
            WeixinWorkRecordEntity groupChatEntity = WeixinGroupChatUtils.getGroupChat(workRecords);
            // 环节操作，发送群聊消息
            sendChatMessage(groupChatEntity, event, weixinConfigVo);
        }
    }

    private void sendChatMessage(WeixinWorkRecordEntity recordEntity, WorkDoneEvent event, WeixinConfigVo weixinConfigVo) {
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        TaskData taskData = event.getTaskData();
        String key = taskData.getTaskInstUuid() + taskData.getUserId();
        String opinionText = taskData.getOpinionText(key);
        String actionType = taskData.getActionType(key);
        String actionName = WorkFlowOperation.getName(actionType);
        if (StringUtils.isBlank(actionName)) {
            actionName = actionType;
        }
        String actionUserNameKey = StringUtils.substring(actionType, 0, 1).toLowerCase() + StringUtils.substring(actionType, 1) + "UserNames";
        String actionUserNames = Objects.toString(taskData.get(actionUserNameKey), StringUtils.EMPTY);
        String actionWidthUser = StringUtils.isNotBlank(actionUserNames) ? (actionName + " " + actionUserNames) : actionName;
        WeixinApiUtils.sendUserChatMessage(userDetails, actionWidthUser, opinionText, recordEntity, weixinConfigVo);
    }

}
