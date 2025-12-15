/*
 * @(#)2020年5月27日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.support.listener;

import com.wellsoft.pt.app.dingtalk.entity.MultiOrgDingUser;
import com.wellsoft.pt.app.dingtalk.entity.WorkRecord;
import com.wellsoft.pt.app.dingtalk.service.MultiOrgDingUserService;
import com.wellsoft.pt.app.dingtalk.service.WorkRecordService;
import com.wellsoft.pt.app.dingtalk.support.DingtalkConfig;
import com.wellsoft.pt.app.dingtalk.utils.DingtalkApiUtils;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.jpa.event.EventListenerPair;
import com.wellsoft.pt.jpa.event.WellptTransactionalEventListener;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserService;
import com.wellsoft.pt.workflow.event.WorkDoneEvent;
import com.wellsoft.pt.workflow.event.WorkTodoEvent;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月27日.1	zhongzh		2020年5月27日		Create
 * </pre>
 * @date 2020年5月27日
 */
//@Component
@Deprecated
public class WorkRecordUpdateListener extends WellptTransactionalEventListener<WorkDoneEvent> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DingtalkConfig dingtalkConfig;

    @Autowired
    private WorkRecordService workRecordService;

    @Autowired
    private MultiOrgUserService multiOrgUserService;

    @Autowired
    private MultiOrgDingUserService multiOrgDingUserService;

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
                if (doneEvent.equals(doneEvent2)) {
                    needExecute = true;
                    eventListenerPair.markIgnoreExecute();// 忽略重复的事件
                }
            }
        }
        return needExecute;
    }

    /**
     *
     */
    @Override
    public void onApplicationEvent(WorkDoneEvent event) {
        String flowInstUuid = event.getFlowInstUuid();
        String taskInstUuid = event.getTaskInstUuid();
        MultiOrgDingUser multiOrgDingUser = multiOrgDingUserService.getByPtUserId(event.getUserId());
        if (multiOrgDingUser == null) {
            logger.warn("flowInstUuid[" + flowInstUuid + "]taskInstUuid[" + taskInstUuid + "]userId["
                    + event.getUserId() + "]未找到钉钉用户");
            return;
        }
        String dingUserId = multiOrgDingUser.getDing_userId();
        String pushMode = dingtalkConfig.getPushMode();
        if (WorkRecord.PUSH_MODE_SYNC.equals(pushMode)) {
            DingtalkApiUtils.updateWorkRecord(taskInstUuid, dingUserId, DingtalkApiUtils.getAccessToken());
        } else if (WorkRecord.PUSH_MODE_ASYNC.equals(pushMode)) {
            DingtalkApiUtils.updateWorkRecord2(taskInstUuid, dingUserId, DingtalkApiUtils.getAccessToken());
        }

    }

}
