/*
 * @(#)2013-5-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.service;

import com.google.common.collect.Sets;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.access.IdentityResolverStrategy;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.element.UserUnitElement;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.entity.TaskTimerUser;
import com.wellsoft.pt.bpm.engine.enums.ParticipantType;
import com.wellsoft.pt.bpm.engine.node.TaskNode;
import com.wellsoft.pt.bpm.engine.service.FlowService;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.timer.support.TimerHelper;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-27.1	zhulh		2013-5-27		Create
 * </pre>
 * @date 2013-5-27
 */
@Service
@Transactional
public class AbstractTaskTimerService extends BaseServiceImpl implements TaskTimerService {

    /**
     * @param taskTimerUuid
     * @param taskTimer
     * @return
     */
    protected static boolean isValidTaskTimer(String taskTimerUuid, TaskTimer taskTimer) {
        if (taskTimer == null) {
            return false;
        }

        TaskService taskService = ApplicationContextHolder.getBean(TaskService.class);
        FlowService flowService = ApplicationContextHolder.getBean(FlowService.class);

        // 环节实例
        TaskInstance taskInstance = taskService.get(taskTimer.getTaskInstUuid());
        // 如果任务已经完成则不需要再进行预警提醒及逾期处理
        if (taskInstance == null || taskInstance.getEndTime() != null) {
            return false;
        }

        // 环节实例不在计时环节中不需要处理
        String taskId = taskInstance.getId();
        if (!TimerHelper.isInTimingTask(taskId, taskTimer)) {
            return false;
        }

        // 流程实例
        FlowInstance flowInstance = flowService.getFlowInstanceByTaskInstUuid(taskTimer.getTaskInstUuid());
        if (flowInstance == null) {
            return false;
        }

        return true;
    }

    /**
     * @param taskTimerUsers
     * @param dueObject
     * @return
     */
    public static List<TaskTimerUser> filterTaskTimerUser(Collection<TaskTimerUser> taskTimerUsers, int dueObject) {
        List<TaskTimerUser> dueUsers = new ArrayList<TaskTimerUser>();
        for (TaskTimerUser taskTimerUser : taskTimerUsers) {
            if (dueObject == taskTimerUser.getUserType()) {
                dueUsers.add(taskTimerUser);
            }
        }
        return dueUsers;
    }

    /**
     * 根据环节实例获取在办人员
     *
     * @param flowInstance
     * @param aclService
     * @return
     */
    public static Set<String> getDoingUserIds(TaskInstance taskInstance) {
        TaskService taskService = ApplicationContextHolder.getBean(TaskService.class);
        return Sets.newLinkedHashSet(taskService.getTodoUserIds(taskInstance.getUuid()));
    }

    /**
     * 根据环节实例获取流程督办人员
     *
     * @param flowInstance
     * @param aclService
     * @return
     */
    public static Set<String> getMonitorUserIds(TaskInstance taskInstance) {
        TaskService taskService = ApplicationContextHolder.getBean(TaskService.class);
        return Sets.newLinkedHashSet(taskService.getSuperviseUserIds(taskInstance.getUuid()));
    }

    /**
     * 流程发起人及前环节办理人作为跟踪人员
     *
     * @param flowInstance
     * @param aclService
     * @return
     */
    protected static Set<String> getTracerUserIds(TaskInstance taskInstance) {
        TaskService taskService = ApplicationContextHolder.getBean(TaskService.class);
        return Sets.newLinkedHashSet(taskService.getTraceUserIds(taskInstance.getUuid()));
    }

    /**
     * 根据环节实例获取流程管理人员
     *
     * @param flowInstance
     * @param aclService
     * @return
     */
    public static Set<String> getAdminUserIds(TaskInstance taskInstance) {
        TaskService taskService = ApplicationContextHolder.getBean(TaskService.class);
        return Sets.newLinkedHashSet(taskService.getMonitorUserIds(taskInstance.getUuid()));
    }

    /**
     * 解析其他人员
     *
     * @param otherUsers
     * @param flowInstance
     * @param aclService
     * @return
     */
    protected static Set<String> getOtherUserIds(List<TaskTimerUser> otherUsers, TaskInstance taskInstance,
                                                 TaskData taskData) {
        List<UserUnitElement> unitElements = new ArrayList<>();
        for (TaskTimerUser taskTimerUser : otherUsers) {
            UserUnitElement unitElement = new UserUnitElement();
            unitElement.setType(taskTimerUser.getType());
            unitElement.setValue(taskTimerUser.getValue());
            unitElement.setArgValue(taskTimerUser.getArgValue());

            unitElements.add(unitElement);
        }
        IdentityResolverStrategy identityResolverStrategy = ApplicationContextHolder
                .getBean(IdentityResolverStrategy.class);
        TaskNode taskNode = new TaskNode();
        taskNode.setId(taskInstance.getId());
        Token token = new Token(taskInstance, taskData);
        Set<String> userIds = new LinkedHashSet<String>();
        List<FlowUserSid> sids = identityResolverStrategy.resolve(taskNode, token, unitElements,
                ParticipantType.TodoUser);
        userIds.addAll(IdentityResolverStrategy.resolveAsOrgIds(sids));
        return userIds;
    }

    /**
     * @param taskTimer
     * @return
     */
    public static boolean enabledTaskAlarm(TaskTimer taskTimer) {
        return Boolean.TRUE.equals(taskTimer.getEnableAlarm()) && StringUtils.isNotBlank(taskTimer.getAlarmTime())
                && !Boolean.TRUE.equals(taskTimer.getAlarmDone());
    }

    /**
     * @param taskTimer
     * @return
     */
    public static boolean enabledTaskDueDoing(TaskTimer taskTimer) {
        return Boolean.TRUE.equals(taskTimer.getEnableDueDoing()) && taskTimer.getTaskDueTime() != null
                && !Boolean.TRUE.equals(taskTimer.getDueDoingDone());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TaskTimerService#handler(com.wellsoft.pt.task.job.JobDetail, com.wellsoft.pt.task.job.JobData, java.lang.String)
     */
    @Override
    public void handler(String taskTimerUuid) {

    }

}
