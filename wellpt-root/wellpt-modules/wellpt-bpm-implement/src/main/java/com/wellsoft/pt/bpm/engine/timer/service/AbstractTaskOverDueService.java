/*
 * @(#)2013-10-22 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.service;

import com.google.common.collect.Sets;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskTimerUser;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowMessageTemplate;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowTimerUser;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.support.MessageTemplate;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.timer.support.TimerUser;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.bpm.engine.util.MessageClientUtils;
import com.wellsoft.pt.bpm.engine.util.OrgVersionUtils;
import org.apache.commons.collections.CollectionUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-22.1	zhulh		2013-10-22		Create
 * </pre>
 * @date 2013-10-22
 */
public abstract class AbstractTaskOverDueService extends AbstractTaskTimerService {
    /**
     * @param dueUsers
     * @param taskTimerUsers
     * @param taskInstance
     * @param flowInstance
     */
    protected void hanlderAndSendMessage(Collection<TaskTimerUser> taskTimerUsers, TaskInstance taskInstance) {
        // 逾期处理——人员
        List<TaskTimerUser> dueUsers = filterTaskTimerUser(taskTimerUsers, TimerUser.DUE_OBJECT);

        if (dueUsers.isEmpty()) {
            return;
        }
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(taskInstance.getFlowDefinition());
        List<MessageTemplate> messageTemplates = null;
        TaskData taskData = new TaskData();
        taskData.setFormUuid(taskInstance.getFormUuid());
        taskData.setDataUuid(taskInstance.getDataUuid());
        Token token = new Token(taskInstance, taskData);
        taskData.setToken(token);
//        OrgApiFacade orgApiFacade = ApplicationContextHolder.getBean(OrgApiFacade.class);
        WorkflowOrgService workflowOrgService = ApplicationContextHolder.getBean(WorkflowOrgService.class);
        for (TaskTimerUser taskTimerUser : dueUsers) {
            String value = taskTimerUser.getValue();
            WorkFlowTimerUser dueUser = Enum.valueOf(WorkFlowTimerUser.class, value.trim());
            Set<String> userIds = new LinkedHashSet<String>(0);
            switch (dueUser) {
                case Doing: // 在办人员
                    userIds = getDoingUserIds(taskInstance);
                    messageTemplates = flowDelegate.getMessageTemplateMap().get(
                            WorkFlowMessageTemplate.WF_WORK_DUE_DOING.getType());
                    MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_DUE_DOING, messageTemplates,
                            taskInstance, taskInstance.getFlowInstance(), userIds, ParticipantType.TodoUser);
                    break;
                case Monitor: // 督办人员
                    userIds = getMonitorUserIds(taskInstance);
                    messageTemplates = flowDelegate.getMessageTemplateMap().get(
                            WorkFlowMessageTemplate.WF_WORK_DUE_SUPERVISE.getType());
                    MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_DUE_SUPERVISE, messageTemplates,
                            taskInstance, taskInstance.getFlowInstance(), userIds, ParticipantType.SuperviseUser);
                    break;
                case Tracer: // 跟踪人员
                    userIds = getTracerUserIds(taskInstance);
                    messageTemplates = flowDelegate.getMessageTemplateMap().get(
                            WorkFlowMessageTemplate.WF_WORK_DUE_TRACER.getType());
                    MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_DUE_TRACER, messageTemplates,
                            taskInstance, taskInstance.getFlowInstance(), userIds, ParticipantType.MonitorUser);
                    break;
                case Admin: // 流程管理人员
                    userIds = getAdminUserIds(taskInstance);
                    messageTemplates = flowDelegate.getMessageTemplateMap().get(
                            WorkFlowMessageTemplate.WF_WORK_DUE_ADMIN.getType());
                    MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_DUE_ADMIN, messageTemplates,
                            taskInstance, taskInstance.getFlowInstance(), userIds, ParticipantType.MonitorUser);
                    break;
                case Other: // 其他人员
                    // 逾期处理——其他人员
                    List<TaskTimerUser> otherUsers = filterTaskTimerUser(taskTimerUsers, TimerUser.DUE_USER);
                    userIds = getOtherUserIds(otherUsers, taskInstance, taskData);
                    messageTemplates = flowDelegate.getMessageTemplateMap().get(
                            WorkFlowMessageTemplate.WF_WORK_DUE_OTHER.getType());
                    MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_DUE_OTHER, messageTemplates,
                            taskInstance, taskInstance.getFlowInstance(), userIds, ParticipantType.CopyUser);
                    break;
                case DoingSuperior:// 在办人员的上级领导
                    userIds = getDoingUserIds(taskInstance);// 在办人员
                    Set<String> superiros = Sets.newHashSet();
                    if (CollectionUtils.isNotEmpty(userIds)) {
                        for (String u : userIds) {
//                            Set<String> ids = orgApiFacade.queryUserMainJobSuperiorLeaderList(u);
                            Set<String> ids = workflowOrgService.listUserMainJobSuperiorLeader(u, OrgVersionUtils.getAvailableFlowOrgVersionIdsAsArray(token));
                            if (ids != null) {
                                superiros.addAll(ids);
                            }
                        }
                    }
                    if (!superiros.isEmpty()) {
                        messageTemplates = flowDelegate.getMessageTemplateMap().get(
                                WorkFlowMessageTemplate.WF_WORK_ALARM_DOING_SUPERIOR.getType());
                        MessageClientUtils.send(taskData, WorkFlowMessageTemplate.WF_WORK_ALARM_DOING_SUPERIOR, messageTemplates,
                                taskInstance, taskInstance.getFlowInstance(), superiros, ParticipantType.MonitorUser);
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
