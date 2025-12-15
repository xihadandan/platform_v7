/*
 * @(#)2014-3-4 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.context.listener.impl.SubTaskTimerRefNewFlowTimerListener;
import com.wellsoft.pt.bpm.engine.element.*;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.service.FlowInstanceParameterService;
import com.wellsoft.pt.bpm.engine.service.TaskSubFlowService;
import com.wellsoft.pt.bpm.engine.service.TaskTimerService;
import com.wellsoft.pt.bpm.engine.service.TaskTimerUserService;
import com.wellsoft.pt.bpm.engine.support.CustomRuntimeData;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.timer.support.TaskTimerStatus;
import com.wellsoft.pt.bpm.engine.timer.support.TimerHelper;
import com.wellsoft.pt.bpm.engine.timer.support.TimerUnit;
import com.wellsoft.pt.bpm.engine.timer.support.TimerUser;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.service.TsWorkTimePlanService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-4.1	zhulh		2014-3-4		Create
 * </pre>
 * @date 2014-3-4
 */
public abstract class AbstractTimerExecutor extends BaseServiceImpl implements TimerExecutor {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private TaskTimerService taskTimerService;

    @Autowired
    private TaskTimerUserService taskTimerUserService;

    @Autowired
    private TaskSubFlowService taskSubFlowService;

    @Autowired
    private FlowInstanceParameterService flowInstanceParameterService;

    @Autowired
    private TsWorkTimePlanService workTimePlanService;

    protected List<TaskTimer> createTimers(TaskInstance taskInstance, FlowInstance flowInstance,
                                           TimerElement timerElement, TaskData taskData) {
        List<TaskTimer> timerList = Lists.newArrayList();
        // 流程实例UUID
        String flowInstUuid = flowInstance.getUuid();
        // 环节实例UUID
        String taskInstUuid = taskInstance.getUuid();
        // 环节ID
        String taskId = taskInstance.getId();
        // 定时器ID
        String timerId = timerElement.getTimerId();
        // 定时器名称
        String name = timerElement.getName();
        // 计时器配置UUID
        String timerConfigUuid = timerElement.getTimerConfigUuid();
        // 工作时间方案UUID
        String workTimePlanUuid = timerElement.getWorkTimePlanUuid();
        // 工作时间方案ID
        String workTimePlanId = timerElement.getWorkTimePlanId();
        // 办理时限类型
        String limitTimeType = timerElement.getLimitTimeType();
        // 办理时限数字
        String limitTime1 = timerElement.getLimitTime1();
        // 办理时限动态表单字段
        String limitTime2 = timerElement.getLimitTime();
        // 办理时限单位
        String limitUnit = timerElement.getLimitUnit();
        // 计时环节
        String taskIds = timerElement.getTaskIdsAsString();
        // 计时结束流向
        String overDirections = timerElement.getOverDirections();
        // 自动更新时限
        boolean autoUpdateLimitTime = timerElement.getIsAutoUpdateLimitTime();
        // 时限为空时不计时
        boolean ignoreEmptyLimitTime = timerElement.getIsIgnoreEmptyLimitTime();
        // 计时暂停/恢复影响主流程
        boolean affectMainFlow = timerElement.getIsAffectMainFlow();
        // 预警提醒
        boolean enableAlarm = timerElement.getIsEnableAlarm();
        // 逾期处理
        boolean enableDueDoing = timerElement.getIsEnableDueDoing();

        // 预警提醒
        // 提醒时间
        String alarmTime = timerElement.getAlarmTime();
        // 提醒单位
        String alarmUnit = timerElement.getAlarmUnit();
        // 预警提醒次数
        String alarmFrequency = timerElement.getAlarmFrequency();
        // 发起流程
        String alarmFlowId = timerElement.getAlarmFlow() != null ? timerElement.getAlarmFlow().getId() : null;

        // 逾期处理
        // 处理时间
        String dueTime = timerElement.getDueTime();
        // 处理单位
        String dueUnit = timerElement.getDueUnit();
        // 逾期处理次数
        String dueFrequency = timerElement.getDueFrequency();
        // 处理动作
        String dueAction = timerElement.getDueAction();
        // 自动进入下一个办理环节
        String dueToTaskId = timerElement.getDueToTask();
        // 发起流程
        String dueFlowId = timerElement.getDueFlow() != null ? timerElement.getDueFlow().getId() : null;
        // 租户ID
        String tenantId = SpringSecurityUtils.getCurrentTenantId();

        // 事件监听
        String listener = timerElement.getTimerListener();

        // 任务数据传入的办理时限
        String flowDefId = flowInstance.getId();
        String limitType = taskData.getLimitType(flowDefId);
        String limitTime = taskData.getLimitTime(flowDefId);
        if (StringUtils.isNotBlank(limitType) && StringUtils.isNotBlank(limitTime)) {
            // 时限类型
            limitTimeType = limitType;
            limitTime1 = limitTime;
            limitUnit = taskData.getLimitUnit(flowDefId) + StringUtils.EMPTY;
            if (StringUtils.isBlank(limitUnit)) {
                // 工作时间
                limitUnit = TimerUnit.WORKING_DAY + StringUtils.EMPTY;
            }
            // 外部指定时限时不自动更新
            autoUpdateLimitTime = false;
        } else {
            if (!StringUtils.equals("-1", timerElement.getTimingModeType()) && StringUtils.isNotBlank(timerElement.getTimingModeType())
                    && StringUtils.isNotBlank(timerElement.getTimingModeUnit())) {
                limitUnit = TimerHelper.getTimingMode(timerElement.getTimingModeType(), timerElement.getTimingModeUnit());
            }
        }
        TaskTimer taskTimer = new TaskTimer();
        taskTimer.setFlowInstUuid(flowInstUuid);
        taskTimer.setTaskInstUuid(taskInstUuid);
        taskTimer.setTaskId(taskId);
        taskTimer.setId(timerId);
        taskTimer.setName(name);
        taskTimer.setTimerConfigUuid(timerConfigUuid);
        taskTimer.setWorkTimePlanUuid(workTimePlanUuid);
        taskTimer.setWorkTimePlanId(workTimePlanId);
        taskTimer.setLimitTimeType(limitTimeType);
        taskTimer.setLimitTime1(limitTime1);
        taskTimer.setLimitTime2(limitTime2);
        taskTimer.setLimitUnit(limitUnit);
        taskTimer.setTaskIds(taskIds);
        taskTimer.setOverDirections(overDirections);
        taskTimer.setAutoUpdateLimitTime(autoUpdateLimitTime);
        taskTimer.setIgnoreEmptyLimitTime(ignoreEmptyLimitTime);
        taskTimer.setAffectMainFlow(affectMainFlow);
        taskTimer.setEnableAlarm(enableAlarm);
        taskTimer.setEnableDueDoing(enableDueDoing);
        taskTimer.setTenantId(tenantId);
        taskTimer.setStatus(TaskTimerStatus.READY);
        taskTimer.setTimingState(0);
        taskTimer.setAlarmState(0);
        taskTimer.setOverDueState(0);
        if (StringUtils.isNotBlank(dueAction)) {
            taskTimer.setDueAction(Integer.valueOf(dueAction));
            taskTimer.setDueToTaskId(dueToTaskId);
        }
        if (enableDueDoing) {
            taskTimer.setDueTime(dueTime);
            if (!StringUtils.equals("2", timerElement.getDueUnitType())) {
                taskTimer.setDueUnit(Integer.valueOf(dueUnit));
            }
            if (!StringUtils.equals("2", timerElement.getDueFrequencyType())) {
                taskTimer.setDueFrequency(Integer.valueOf(dueFrequency));
            }
            taskTimer.setDueFlowId(dueFlowId);
        }
        taskTimer.setListener(listener);

        // 添加的运行时计时监听器
        String rtTimerListener = (String) taskData.getCustomData(CustomRuntimeData.KEY_TIMER_LISTENER);
        if (StringUtils.isNotBlank(rtTimerListener)) {
            List<String> listeners = Lists.newArrayList(StringUtils.split(rtTimerListener, Separator.SEMICOLON.getValue()));
            if (StringUtils.isNotBlank(taskTimer.getListener())) {
                listeners.addAll(0, Arrays.asList(StringUtils.split(taskTimer.getListener(), Separator.SEMICOLON.getValue())));
            }
            taskTimer.setListener(StringUtils.join(listeners, Separator.SEMICOLON.getValue()));
        }

        // 上级流程子流程环节使用当前流程的计时器处理，添加跟踪计时器处理
        if (isTimerUseByParentTaskInstance(flowInstance, timerElement, taskData)) {
            List<String> listeners = Lists.newArrayList();
            if (StringUtils.isNotBlank(taskTimer.getListener())) {
                listeners.addAll(Arrays.asList(StringUtils.split(taskTimer.getListener(), Separator.SEMICOLON.getValue())));
            }
            listeners.add(StringUtils.uncapitalize(SubTaskTimerRefNewFlowTimerListener.class.getSimpleName()));
            taskTimer.setListener(StringUtils.join(listeners, Separator.SEMICOLON.getValue()));
        }

        List<TaskTimerUser> taskTimerUsers = Lists.newArrayList();
        if (enableAlarm) {
            /**
             * 预警提醒存在多个计时
             */
            List<AlarmElement> alarmElements = timerElement.getAlarmElements();
            if (CollectionUtils.isNotEmpty(alarmElements)) {// 多个预期提醒设置
                addAlarmTaskTimerUser(alarmElements.get(0), taskTimerUsers);
                taskTimer.setAlarmTime(alarmElements.get(0).getAlarmTime());
                if (!StringUtils.equals("2", alarmElements.get(0).getAlarmUnitType())) {
                    taskTimer.setAlarmUnit(Integer.valueOf(alarmElements.get(0).getAlarmUnit()));
                }
                if (!StringUtils.equals("2", alarmElements.get(0).getAlarmFrequencyType())) {
                    taskTimer.setAlarmFrequency(Integer.valueOf(alarmElements.get(0).getAlarmFrequency()));
                }
                taskTimer.setAlarmFlowId(alarmFlowId);
                int size = alarmElements.size();
                if (size > 1) { // 多预警提醒，除了第一个预警提醒，剩下的另外起计时器
                    for (int i = 1; i < size; i++) {
                        TaskTimer alarmTaskTimer = new TaskTimer();
                        List<TaskTimerUser> alarmTaskTimerUsers = new ArrayList<TaskTimerUser>();
                        BeanUtils.copyProperties(taskTimer, alarmTaskTimer);
                        alarmTaskTimer.setName(alarmTaskTimer.getName() + "[预警提醒-" + (i + 1) + "]");
                        alarmTaskTimer.setAlarmTime(alarmElements.get(i).getAlarmTime());
                        if (!StringUtils.equals("2", alarmElements.get(i).getAlarmUnitType())) {
                            alarmTaskTimer.setAlarmUnit(Integer.valueOf(alarmElements.get(i).getAlarmUnit()));
                        }
                        if (!StringUtils.equals("2", alarmElements.get(i).getAlarmFrequencyType())) {
                            alarmTaskTimer.setAlarmFrequency(Integer.valueOf(alarmElements.get(i).getAlarmFrequency()));
                        }
                        alarmTaskTimer.setAlarmFlowId(alarmFlowId);
                        alarmTaskTimer.setEnableDueDoing(false);// 不进行逾期计算
                        addAlarmTaskTimerUser(alarmElements.get(i), alarmTaskTimerUsers);
                        taskTimerService.save(alarmTaskTimer);
                        for (TaskTimerUser taskTimerUser : alarmTaskTimerUsers) {
                            taskTimerUser.setTaskTimerUuid(alarmTaskTimer.getUuid());
                        }
                        taskTimerUserService.saveAll(alarmTaskTimerUsers);
                        timerList.add(alarmTaskTimer);
                    }
                }
            } else {
                // 旧版预警提醒配置
                addAlarmTaskTimerUser(timerElement, taskTimerUsers);
                taskTimer.setAlarmTime(alarmTime);
                taskTimer.setAlarmUnit(StringUtils.isBlank(alarmUnit) ? 0 : Integer.valueOf(alarmUnit));
                taskTimer.setAlarmFrequency(StringUtils.isBlank(alarmFrequency) ? 0 : Integer.valueOf(alarmFrequency));
                taskTimer.setAlarmFlowId(alarmFlowId);
            }
        }

        // 逾期处理
        // 消息通知人员
        List<UserUnitElement> dueObjects = timerElement.getDueObjects();
        addTaskTimerUser(taskTimerUsers, dueObjects, TimerUser.DUE_OBJECT);
        // 消息通知其他人员
        List<UserUnitElement> dueUsers = timerElement.getDueUsers();
        addTaskTimerUser(taskTimerUsers, dueUsers, TimerUser.DUE_USER);
        // 移交给其他人员办理
        List<UserUnitElement> dueToUsers = timerElement.getDueToUsers();
        addTaskTimerUser(taskTimerUsers, dueToUsers, TimerUser.DUE_TO_USER);
        // 发起流程办理人
        List<UserUnitElement> dueFlowDoings = timerElement.getDueFlowDoings();
        addTaskTimerUser(taskTimerUsers, dueFlowDoings, TimerUser.DUE_FLOW_DOING);
        // 发起流程其他人员
        List<UserUnitElement> dueFlowDoingUsers = timerElement.getDueFlowDoingUsers();
        addTaskTimerUser(taskTimerUsers, dueFlowDoingUsers, TimerUser.DUE_FLOW_DOING_USER);
        taskTimerService.save(taskTimer);
        timerList.add(taskTimer);
        for (TaskTimerUser taskTimerUser : taskTimerUsers) {
            taskTimerUser.setTaskTimerUuid(taskTimer.getUuid());
            taskTimerUserService.save(taskTimerUser);
        }

        return timerList;
    }

    /**
     * @param taskInstance
     * @param flowInstance
     * @param timerElement
     * @param subTaskTimerElement
     * @param taskData
     */
    protected void createRefSubTaskTimer(TaskInstance taskInstance, FlowInstance flowInstance,
                                         TimerElement timerElement, SubTaskTimerElement subTaskTimerElement, TaskData taskData) {
        List<String> refTimerIds = Lists.newArrayList();
        List<NewFlowTimerElement> subTaskTimers = subTaskTimerElement.getTimers();
        for (NewFlowTimerElement newFlowTimerElement : subTaskTimers) {
            if (StringUtils.isNotBlank(newFlowTimerElement.getNewFlowTimerId())) {
                refTimerIds.add(newFlowTimerElement.getNewFlowTimerId());
            }
        }
        // 引用的计时器ID
        String refIds = StringUtils.join(refTimerIds, Separator.SEMICOLON.getValue());
        // 生成的计时器
        List<TaskTimer> taskTimers = createTimers(taskInstance, flowInstance, timerElement, taskData);
        for (TaskTimer taskTimer : taskTimers) {
            taskTimer.setRefIds(refIds);
            taskTimer.setTaskIds(taskInstance.getId());
        }
        taskTimerService.saveAll(taskTimers);
    }

    /**
     * @param flowInstance
     * @param timerElement
     * @return
     */
    private boolean isTimerUseByParentTaskInstance(FlowInstance flowInstance, TimerElement timerElement,
                                                   TaskData taskData) {
        // 流程实例UUID
        String flowInstUuid = flowInstance.getUuid();
        // 定时器ID
        String timerId = timerElement.getTimerId();
        String parentTaskInstUuid = taskData.getParentTaskInstUuid(flowInstUuid);
        if (StringUtils.isNotBlank(parentTaskInstUuid)) {
            // 上级子流程环节按子流程计时器计时标记
            String name = parentTaskInstUuid + "_" + timerId + "_parent_task_ref_timer";
            long count = flowInstanceParameterService.countByFlowInstUuidAndName(null, name);
            if (count > 0) {
                return true;
            }
        } else {
            List<TaskSubFlow> taskSubFlows = taskSubFlowService.getByFlowInstUuid(flowInstUuid);
            for (TaskSubFlow taskSubFlow : taskSubFlows) {
                String parentFlowInstUuid = taskSubFlow.getParentFlowInstUuid();
                parentTaskInstUuid = taskSubFlow.getParentTaskInstUuid();
                // 上级子流程环节按子流程计时器计时标记
                String name = parentTaskInstUuid + "_" + timerId + "_parent_task_ref_timer";
                long count = flowInstanceParameterService.countByFlowInstUuidAndName(parentFlowInstUuid, name);
                if (count > 0) {
                    return true;
                }
            }
        }
        return false;
    }

    private void addAlarmTaskTimerUser(AlarmElement timerElement, List<TaskTimerUser> taskTimerUsers) {

        // 预警提醒
        // 消息通知人员
        List<UserUnitElement> alarmObjects = timerElement.getAlarmObjects();
        addTaskTimerUser(taskTimerUsers, alarmObjects, TimerUser.ALARM_OBJECT);
        // 消息通知其他人员
        List<UserUnitElement> alarmUsers = timerElement.getAlarmUsers();
        addTaskTimerUser(taskTimerUsers, alarmUsers, TimerUser.ALARM_USER);
        // 发起流程办理人
        List<UserUnitElement> alarmFlowDoings = timerElement.getAlarmFlowDoings();
        addTaskTimerUser(taskTimerUsers, alarmFlowDoings, TimerUser.ALARM_FLOW_DOING);
        // 发起流程其他人员
        List<UserUnitElement> alarmFlowDoingUsers = timerElement.getAlarmFlowDoingUsers();
        addTaskTimerUser(taskTimerUsers, alarmFlowDoingUsers, TimerUser.ALARM_FLOW_DOING_USER);

    }

    /**
     * 添加任务定时用户配置信息
     *
     * @param taskTimerUsers
     * @param unitElements
     * @param userType
     */
    private void addTaskTimerUser(List<TaskTimerUser> taskTimerUsers, List<UserUnitElement> unitElements, Integer userType) {
        for (UnitElement unitElement : unitElements) {
            TaskTimerUser taskTimerUser = new TaskTimerUser();
            taskTimerUser.setUserType(userType);
            taskTimerUser.setType(unitElement.getType());
            taskTimerUser.setValue(unitElement.getValue());
            taskTimerUser.setArgValue(unitElement.getArgValue());
            taskTimerUsers.add(taskTimerUser);
        }
    }

}
