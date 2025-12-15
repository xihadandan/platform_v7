/*
 * @(#)2013-5-24 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.exception.WorkFlowException;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.bpm.engine.FlowEngine;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.context.listener.TimerListener;
import com.wellsoft.pt.bpm.engine.core.Direction;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.AlarmElement;
import com.wellsoft.pt.bpm.engine.element.NewFlowTimerElement;
import com.wellsoft.pt.bpm.engine.element.SubTaskTimerElement;
import com.wellsoft.pt.bpm.engine.element.TimerElement;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.timer.listener.TaskTimerListener;
import com.wellsoft.pt.bpm.engine.timer.service.AbstractTaskTimerService;
import com.wellsoft.pt.bpm.engine.timer.service.TimerManagerService;
import com.wellsoft.pt.bpm.engine.timer.support.*;
import com.wellsoft.pt.bpm.engine.util.FlowDelegateUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.timer.dto.TsTimerDto;
import com.wellsoft.pt.timer.entity.TsTimerEntity;
import com.wellsoft.pt.timer.enums.EnumTimingModeType;
import com.wellsoft.pt.timer.enums.EnumTimingState;
import com.wellsoft.pt.timer.facade.service.TsTimerFacadeService;
import com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService;
import com.wellsoft.pt.timer.support.TsTimerParam;
import com.wellsoft.pt.timer.support.TsTimerParamBuilder;
import com.wellsoft.pt.workflow.enums.WorkFlowFieldMapping;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-24.1	zhulh		2013-5-24		Create
 * </pre>
 * @date 2013-5-24
 */
@Service
@Transactional
public class DefaultTimerExecutor extends AbstractTimerExecutor {

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private TaskTimerService taskTimerService;

    @Autowired
    private TaskInstanceService taskInstanceService;

    @Autowired
    private FlowInstanceService flowInstanceService;

    @Autowired
    private FlowInstanceParameterService flowInstanceParameterService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskTimerLogService taskTimerLogService;

    @Autowired
    private TimerManagerService timerManagerService;

    @Autowired
    private IdentityService identityService;

    @Autowired(required = false)
    private Map<String, TimerListener> listenerMap;

    @Autowired
    private TsTimerFacadeService timerFacadeService;

    @Autowired
    private TsWorkTimePlanFacadeService workTimePlanFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.TimerExecutor#timer(com.wellsoft.pt.bpm.engine.node.Node, com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    @Override
    public void timer(Node node, ExecutionContext executionContext) {
        Token token = executionContext.getToken();
        FlowInstance flowInstance = token.getFlowInstance();
        TaskInstance taskInstance = token.getTask();
        TaskData taskData = token.getTaskData();
        String taskId = node.getId();
        String preTaskId = taskData.getPreTaskId(taskId);
        String preTaskInstUuid = taskData.getPreTaskInstUuid(taskInstance.getId());
        // 上一环节计时器
        List<TaskTimer> preTaskTimers = Lists.newArrayList();
        // 流程激活的计时器
        List<TaskTimer> activeTimers = taskTimerService.getActiveTimersByFlowInstUuid(flowInstance.getUuid());
        if (StringUtils.isNotBlank(preTaskId) && StringUtils.isNotBlank(preTaskInstUuid)) {
            preTaskTimers = filterPreTaskTimers(activeTimers, preTaskInstUuid);
        }
        List<TimerElement> timerElements = token.getFlowDelegate().getTimers();
        List<TimerElement> toStartTimerElements = filterToStartTimers(activeTimers, timerElements);

        // 遍历已经生成的计时器，启动相关的计时器
        for (TaskTimer taskTimer : preTaskTimers) {
            // 计时状态
            Integer timerStatus = taskTimer.getStatus();
            // 1、计时器进入计时环节
            if (TimerHelper.isInTimingTask(taskId, taskTimer)) {
                doStartTimer(taskInstance, flowInstance, taskData, taskTimer, timerElements);
            } else {
                // 2、计时器不在计时环节，如果前环节处于计时状态则停止计时
                if (TaskTimerStatus.STARTED.equals(timerStatus)) {
                    if (TimerHelper.isInTimingTask(preTaskId, taskTimer)) {
                        // 暂停计时
                        pauseTimer(taskInstance, flowInstance, taskData, taskTimer);
                    } else {
                        // 更新计时器的当前环节信息
                        updateTimerTaskData(taskInstance, taskTimer);
                    }
                } else {
                    // 更新计时器的当前环节信息
                    updateTimerTaskData(taskInstance, taskTimer);
                }
            }
        }

        // 要发起的计时器
        if (CollectionUtils.isNotEmpty(toStartTimerElements)) {
            for (TimerElement timerElement : toStartTimerElements) {
                // 启动相关的计时器
                // 如果在计时环节且计时系统有效则开始计时
                if (TimerHelper.isInTimingTask(taskId, timerElement)) {
                    // 创建定时器配置信息
                    List<TaskTimer> taskTimerList = createTimers(taskInstance, flowInstance, timerElement, taskData);
                    for (TaskTimer taskTimer : taskTimerList) {
                        // 启动计时器
                        startTimer(taskInstance, flowInstance, taskData, taskTimer, timerElement);
                    }
                }
            }
        }

        // 子流程环节标记按子流程计时器计时
        if (node instanceof SubTaskNode) {
            for (TimerElement timerElement : timerElements) {
                markSubTaskTimerFromNewFlowTimer(node, executionContext, taskInstance, flowInstance, timerElement);
            }
        }
    }

    /**
     * @param activeTimers
     * @param timers
     * @return
     */
    private List<TimerElement> filterToStartTimers(List<TaskTimer> activeTimers, List<TimerElement> timerElements) {
        if (CollectionUtils.isEmpty(timerElements)) {
            return Collections.emptyList();
        }
        List<TimerElement> toStartTimers = Lists.newArrayList();
        if (CollectionUtils.isEmpty(activeTimers)) {
            toStartTimers.addAll(timerElements);
        } else {
            Map<String, TaskTimer> timerMap = ConvertUtils.convertElementToMap(activeTimers, "id");
            for (TimerElement timerElement : timerElements) {
                String timerId = timerElement.getTimerId();
                if (StringUtils.isBlank(timerId) || timerMap.containsKey(timerId)) {
                    continue;
                }
                toStartTimers.add(timerElement);
            }
        }
        return toStartTimers;
    }

    /**
     * @param taskTimers
     * @param preTaskInstUuid
     * @return
     */
    private List<TaskTimer> filterPreTaskTimers(List<TaskTimer> taskTimers, String preTaskInstUuid) {
        List<TaskTimer> preTaskTimers = Lists.newArrayList();
        for (TaskTimer taskTimer : taskTimers) {
            if (StringUtils.equals(taskTimer.getTaskInstUuid(), preTaskInstUuid)) {
                preTaskTimers.add(taskTimer);
            }
        }
        return preTaskTimers;
    }

    /**
     * @param node
     * @param executionContext
     * @param timerElement
     */
    private void markSubTaskTimerFromNewFlowTimer(Node node, ExecutionContext executionContext,
                                                  TaskInstance taskInstance, FlowInstance flowInstance, TimerElement timerElement) {
        List<SubTaskTimerElement> subTaskTimerElements = timerElement.getSubTasks();
        for (SubTaskTimerElement subTaskTimerElement : subTaskTimerElements) {
            if (subTaskTimerElement.isUseNewFlowTimer()) {
                List<NewFlowTimerElement> newFlowTimerElements = subTaskTimerElement.getTimers();
                boolean refSubTaskTimer = saveSubTaskTimerFlowInstanceParameter(node, executionContext,
                        newFlowTimerElements);
                if (refSubTaskTimer) {
                    createRefSubTaskTimer(taskInstance, flowInstance, timerElement, subTaskTimerElement,
                            executionContext.getToken().getTaskData());
                }
            }
        }
    }

    /**
     * @param node
     * @param executionContext
     * @param newFlowTimerElements
     * @return
     */
    private boolean saveSubTaskTimerFlowInstanceParameter(Node node, ExecutionContext executionContext,
                                                          List<NewFlowTimerElement> newFlowTimerElements) {
        boolean refSubTaskTimer = false;
        Token token = executionContext.getToken();
        FlowInstance flowInstance = token.getFlowInstance();
        TaskInstance taskInstance = token.getTask();
        for (NewFlowTimerElement newFlowTimerElement : newFlowTimerElements) {
            String newFlowTimerId = newFlowTimerElement.getNewFlowTimerId();
            if (StringUtils.isBlank(newFlowTimerId)) {
                continue;
            }
            FlowInstanceParameter parameter = new FlowInstanceParameter();
            parameter.setFlowInstUuid(flowInstance.getUuid());
            // 上级子流程环节按子流程计时器计时标记
            String name = taskInstance.getUuid() + "_" + newFlowTimerId + "_parent_task_ref_timer";
            parameter.setName(name);
            List<FlowInstanceParameter> parameters = flowInstanceParameterService.findByExample(parameter);
            if (CollectionUtils.isEmpty(parameters)) {
                parameter.setValue(Config.TRUE);
                flowInstanceParameterService.save(parameter);
                refSubTaskTimer = true;
            }
        }
        return refSubTaskTimer;
    }

    /**
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     * @param taskTimer
     * @param timerElements
     */
    public void doStartTimer(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData,
                             TaskTimer taskTimer, List<TimerElement> timerElements) {
        // 自动更新时限
        TimerElement timerElement = getTimerElement(timerElements, taskTimer);
        if (!TimerHelper.isReady(taskTimer) && timerElement != null) {
            updateLimitTimeIfRequire(flowInstance, taskData, taskTimer, timerElement);
        }

        // 计时状态
        Integer timerStatus = taskTimer.getStatus();
        switch (timerStatus) {
            case 0:
                // 准备就绪中，启动
                startTimer(taskInstance, flowInstance, taskData, taskTimer, timerElement);
                break;
            case 1:
                // 已启动，更新计时器、计时环节信息
                updateTimerData(taskInstance, flowInstance, taskTimer, taskTimer.getStatus());
                break;
            case 2:
                // 暂停中，恢复计时器
                resumeTimer(taskInstance, flowInstance, taskData, taskTimer);
                break;
            case 3:
                // 已停止，恢复计时器
                resumeTimer(taskInstance, flowInstance, taskData, taskTimer);
                break;
            default:
                break;
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.TimerExecutor#syncTaskFlowData(com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.entity.TaskTimer)
     */
    @Override
    public void syncTaskFlowData(TaskInstance taskInstance, FlowInstance flowInstance, TaskTimer taskTimer) {
        syncTaskFlowData(taskInstance, flowInstance, null, taskTimer);
    }

    /**
     * 同步流程表单计时数据
     *
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     * @param taskTimer
     */
    public void syncTaskFlowData(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData,
                                 TaskTimer taskTimer) {
        Integer timerStatus = taskTimer.getStatus();
        Date taskDueTime = taskTimer.getTaskDueTime();
        Integer timingState = taskTimer.getTimingState();
        TsTimerEntity timerEntity = null;
        if (StringUtils.isNotBlank(taskTimer.getTimerUuid())) {
            timerEntity = timerFacadeService.getTimer(taskTimer.getTimerUuid());
        }
        // 1、更新环节实例计时信息
        if (TimerHelper.isInTimingTask(taskInstance.getId(), taskTimer)) {
            taskInstance.setTimingState(timingState);
            taskInstance.setAlarmState(taskTimer.getAlarmState());
            taskInstance.setOverDueState(taskTimer.getOverDueState());
            taskInstance.setAlarmTime(taskTimer.getTaskAlarmTime());
            // if (TaskTimerStatus.STARTED.equals(timerStatus) || TaskTimerStatus.STOP.equals(timerStatus)) {
            taskInstance.setDueTime(taskDueTime);
//            } else {
//                taskInstance.setDueTime(null);
//            }
            if (timerEntity != null) {
                taskInstance.setOverdueTime(timerEntity.getOverDueTime());
            }
            taskInstance.setTaskTimerUuid(taskTimer.getUuid());
            taskInstanceService.save(taskInstance);
//            List<TaskIdentity> taskIdentities = identityService.getByTaskInstUuidAndSuspensionState(taskInstance.getUuid(), SuspensionState.NORMAL.getState());
//            taskIdentities.forEach(taskIdentity -> taskIdentity.setOverdueState(taskTimer.getOverDueState()));
//            identityService.saveAll(taskIdentities);
            identityService.updateOverdueStateByTaskInstUuid(taskTimer.getOverDueState(), taskInstance.getUuid());
        }

        // 2、更新流程实例计时信息
        flowInstance.setIsTiming(TaskTimerStatus.STARTED.equals(timerStatus));
        flowInstance.setTimingState(timingState);
        flowInstance.setIsOverDue(Integer.valueOf(1).equals(taskTimer.getOverDueState()));
        flowInstance.setAlarmTime(taskTimer.getTaskAlarmTime());
        flowInstance.setDueTime(taskDueTime);
        if (timerEntity != null) {
            flowInstance.setOverdueTime(timerEntity.getOverDueTime());
        }
        flowInstanceService.save(flowInstance);

        // 3、更新表单字段映射
        String formUuid = taskInstance.getFormUuid();
        String dataUuid = taskInstance.getDataUuid();
        DyFormData dyformData = null;
        if (taskData != null) {
            dyformData = taskData.getDyFormData(dataUuid);
        }
        if (dyformData == null) {
            dyformData = dyFormFacade.getDyFormData(formUuid, dataUuid);
        }
        String limitTimeField = taskTimer.getLimitTime2();
        String dueTimeFieldMapping = WorkFlowFieldMapping.DUE_TIME.getValue();
        String timerStatusFieldMapping = WorkFlowFieldMapping.TIMER_STATUS.getValue();
        boolean isDyformDataChanged = false;
        Object fieldValue = null;
        // 更新表单办理时限、到期时间、计时器状态字段
        if (TimerHelper.isDyformFieldOfLimitTime(taskTimer) && StringUtils.isNotBlank(limitTimeField)
                && dyformData.isFieldExist(limitTimeField)) {
            //			以分钟计算，不再支持输入日期、数字时限回写
            //			// 办理时限表单字段值
            //			fieldValue = dyformData.getFieldValue(limitTimeField);
            //			if (TimerHelper.isDateOfLimitTime(taskTimer)) {
            //				if (isDueTimeChanged(fieldValue, taskDueTime, taskTimer)) {
            //					dyformData.setFieldValue(limitTimeField, taskDueTime);
            //					isDyformDataChanged = true;
            //				}
            //			} else {
            //				if (isLimitTimeChanged(fieldValue, taskTimer.getTaskInitLimitTime())) {
            //					dyformData.setFieldValue(limitTimeField, taskTimer.getTaskInitLimitTime().intValue());
            //					isDyformDataChanged = true;
            //				}
            //			}
            // 到期时间映射
            if (dyformData.hasFieldMappingName(dueTimeFieldMapping)) {
                // 到期时间表单字段值
                fieldValue = dyformData.getFieldValueByMappingName(dueTimeFieldMapping);
                if (isDueTimeChanged(fieldValue, taskDueTime, taskTimer)) {
                    dyformData.setFieldValueByMappingName(dueTimeFieldMapping, taskDueTime);
                    isDyformDataChanged = true;
                }
            }
            // 计时器状态映射
            if (dyformData.hasFieldMappingName(timerStatusFieldMapping)) {
                // 计时器状态表单字段值
                fieldValue = dyformData.getFieldValueByMappingName(timerStatusFieldMapping);
                if (isTimingStateChanged(fieldValue, timerStatus)) {
                    dyformData.setFieldValueByMappingName(timerStatusFieldMapping, timerStatus);
                    isDyformDataChanged = true;
                }
            }
        } else {
            // 到期时间映射
            if (dyformData.hasFieldMappingName(dueTimeFieldMapping)) {
                // 到期时间表单字段值
                fieldValue = dyformData.getFieldValueByMappingName(dueTimeFieldMapping);
                if (isDueTimeChanged(fieldValue, taskDueTime, taskTimer)) {
                    // 到期时间映射
                    dyformData.setFieldValueByMappingName(dueTimeFieldMapping, taskDueTime);
                    isDyformDataChanged = true;
                }
            }
            // 计时器状态映射
            if (dyformData.hasFieldMappingName(timerStatusFieldMapping)) {
                // 计时器状态表单字段值
                fieldValue = dyformData.getFieldValueByMappingName(timerStatusFieldMapping);
                if (isTimingStateChanged(fieldValue, timerStatus)) {
                    dyformData.setFieldValueByMappingName(timerStatusFieldMapping, timerStatus);
                    isDyformDataChanged = true;
                }
            }
        }
        if (isDyformDataChanged) {
            dyformData.doForceCover();
            dyFormFacade.saveFormData(dyformData);
        }
    }

    /**
     * @param dyformData
     * @param taskDueTime
     * @return
     */
    private boolean isDueTimeChanged(Object fieldValue, Date dueTime, TaskTimer taskTimer) {
        if (StringUtils.equals(ObjectUtils.toString(fieldValue, StringUtils.EMPTY),
                ObjectUtils.toString(dueTime, StringUtils.EMPTY))) {
            return false;
        }

        if (fieldValue == null || dueTime == null || StringUtils.isBlank(ObjectUtils.toString(fieldValue))) {
            return true;
        }

        Date fieldDateValue = null;
        try {
            fieldDateValue = DateUtils.parse(fieldValue.toString());
        } catch (ParseException e) {
            return true;
        }

        int limitUnit = Integer.valueOf(taskTimer.getLimitUnit());
        return !TimerHelper.convertTime(fieldDateValue, limitUnit).equals(TimerHelper.convertTime(dueTime, limitUnit));
    }

    /**
     * @param fieldMappingValue
     * @param dueTime
     * @return
     */
    private boolean isLimitTimeChanged(Object fieldValue, Double dueTime) {
        if (StringUtils.equals(ObjectUtils.toString(fieldValue, StringUtils.EMPTY),
                ObjectUtils.toString(dueTime, StringUtils.EMPTY))) {
            return false;
        }

        if (fieldValue == null || dueTime == null || StringUtils.isBlank(ObjectUtils.toString(fieldValue))) {
            return true;
        }

        return !dueTime.equals(Double.valueOf(fieldValue.toString()));
    }

    /**
     * @param fieldValue
     * @param timingState
     * @return
     */
    private boolean isTimingStateChanged(Object fieldValue, Integer timingState) {
        if (StringUtils.equals(ObjectUtils.toString(fieldValue, StringUtils.EMPTY),
                ObjectUtils.toString(timingState, StringUtils.EMPTY))) {
            return false;
        }

        if (fieldValue == null || timingState == null || StringUtils.isBlank(ObjectUtils.toString(fieldValue))) {
            return true;
        }

        return !timingState.equals(Integer.valueOf(fieldValue.toString()));
    }

    /**
     * @param taskInstance
     * @param taskTimer
     */
    private void updateTimerTaskData(TaskInstance taskInstance, TaskTimer taskTimer) {
        // 1、更新计时器信息
        String taskId = taskInstance.getId();
        String taskInstUuid = taskInstance.getUuid();
        // 更新计时系统所在的环节
        taskTimer.setTaskId(taskId);
        taskTimer.setTaskInstUuid(taskInstUuid);
        this.dao.save(taskTimer);
    }

    /**
     * @param taskInstance
     * @param flowInstance
     * @param taskTimer
     * @param timerStatus
     */
    private void updateTimerData(TaskInstance taskInstance, FlowInstance flowInstance, TaskTimer taskTimer,
                                 Integer timerStatus) {
        // 1、更新计时器信息
        String taskId = taskInstance.getId();
        String taskInstUuid = taskInstance.getUuid();
        // 更新计时系统所在的环节
        taskTimer.setTaskId(taskId);
        taskTimer.setTaskInstUuid(taskInstUuid);
        // 设置定时器已经启动
        taskTimer.setStatus(timerStatus);
        // 保存定时器
        taskTimerService.save(taskTimer);

        // 2、同步环节、流程数据
        syncTaskFlowData(taskInstance, flowInstance, taskTimer);
    }

    /**
     * @param node
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     * @param taskTimer
     */
    private void startTimer(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData,
                            TaskTimer taskTimer, TimerElement timerElement) {
        // 1、预处理
        if (!preHandle(taskTimer, "start", taskInstance, flowInstance)) {
            return;
        }
        // 2、初始化办理时限
        TaskLimitTimeInitResult initResult = initLimitTime(taskInstance.getFormUuid(), taskInstance.getDataUuid(),
                taskData, taskTimer);
        // 初始化失败，不启动
        if (Boolean.FALSE.equals(initResult.isSuccess())) {
            updateTimerTaskData(taskInstance, taskTimer);
            taskTimerLogService.log(taskInstance.getUuid(), flowInstance.getUuid(), taskTimer,
                    Calendar.getInstance().getTime(), TaskTimerLog.TYPE_ERROR, "初始化失败，不启动！");
            return;
        }

        // 3、更新计时器信息
        Date startTime = Calendar.getInstance().getTime();
        // 设置定时器开始时间
        if (TimerHelper.isReady(taskTimer)) {
            taskTimer.setStartTime(startTime);
        }
        // 设置定时器最新开始时间
        taskTimer.setLastStartTime(startTime);
        // 更新计时系统所在的环节
        String taskId = taskInstance.getId();
        String taskInstUuid = taskInstance.getUuid();
        taskTimer.setTaskId(taskId);
        taskTimer.setTaskInstUuid(taskInstUuid);
        // 到期时间
        // 剩余办理时限
        Double limitTime = taskTimer.getTaskLimitTime();
        String timingMode = taskTimer.getLimitUnit();
        if (StringUtils.isNotBlank(timerElement.getTimingModeType()) && StringUtils.isNotBlank(timerElement.getTimingModeUnit())) {
            if (EnumTimingModeType.CUSTOM.getValue().equals(timerElement.getTimingModeType())) {
                String timingModeType = getFieldValueOfTimingModeType(taskInstance, taskData, taskTimer, timerElement);
                timingMode = TimerHelper.getTimingMode(timingModeType, timerElement.getTimingModeUnit());
            } else {
                if (StringUtils.isBlank(timingMode)) {
                    timingMode = TimerHelper.getTimingMode(timerElement.getTimingModeType(), timerElement.getTimingModeUnit());
                }
            }
        }
        int limitUnit = Integer.valueOf(timingMode);
        Date defaultDueTime = taskTimer.getTaskDueTime();
        String timerConfigUuid = taskTimer.getTimerConfigUuid();
        String workTimePlanUuid = taskTimer.getWorkTimePlanUuid();
        String workTimePlanId = taskTimer.getWorkTimePlanId();
        workTimePlanUuid = workTimePlanFacadeService.getActiveWorkTimePlanUuidById(workTimePlanId, workTimePlanUuid);
        TaskLimitTime taskLimitTime = initResult.getTaskLimitTime();
        if (StringUtils.isNotBlank(timerConfigUuid) && StringUtils.isNotBlank(workTimePlanUuid)
                && taskLimitTime != null) {
            TsTimerParam timerParam = TsTimerParamBuilder.create().setTimerConfigUuid(timerConfigUuid)
                    .setWorkTimePlanUuid(workTimePlanUuid).setTimingMode(timingMode).setStartTime(startTime)
                    .setDateOfLimitTime(taskLimitTime.isDateOfLimitTime()).setTimeLimit(limitTime)
                    .setDueTime(taskLimitTime.getTaskDueTime()).setListener(TaskTimerListener.LISTENER_NAME).build();
            TsTimerDto timerDto = timerFacadeService.startTimer(timerParam);
            taskTimer.setTaskInitLimitTime(timerDto.getInitTimeLimit());
            taskTimer.setTaskLimitTime(timerDto.getTimeLimit());
            taskTimer.setTaskDueTime(timerDto.getDueTime());
            taskTimer.setTimerUuid(timerDto.getUuid());
        } else {
            if (TimerUnit.DATE_WORKING_DAY == limitUnit && defaultDueTime != null) {// 工作日的情况下
                taskTimer.setTaskDueTime(taskTimer.getTaskDueTime());
            } else {
                throw new BusinessException("计时器配置出错！");
                // taskTimer.setTaskDueTime(TimerHelper.calculateDueTime(limitTime.intValue(), limitUnit, defaultDueTime));
            }
        }
        // 提醒时间
        setTimerAlarmInfos(taskInstance, taskData, taskTimer.getTaskDueTime(), taskTimer);
        // 计时状态
        taskTimer.setStatus(TaskTimerStatus.STARTED);
        taskTimer.setLimitUnit(limitUnit + StringUtils.EMPTY);
        // 保存定时器
        taskTimerService.save(taskTimer);

        // 3、同步环节、流程数据
        syncTaskFlowData(taskInstance, flowInstance, taskData, taskTimer);

        // 4、记录日志
        Date logTime = Calendar.getInstance().getTime();
        taskTimerLogService.log(taskInstance.getUuid(), flowInstance.getUuid(), taskTimer, logTime,
                TaskTimerLog.TYPE_START);

        // 5、发布计时器启动事件
        String listener = taskTimer.getListener();
        if (StringUtils.isNotBlank(listener)) {
            String[] listeners = StringUtils.split(listener, Separator.SEMICOLON.getValue());
            for (String l : listeners) {
                TimerListener timerListener = listenerMap.get(l);
                if (timerListener == null) {
                    continue;
                }
                timerListener.onTimerStarted(taskTimer, taskInstance, flowInstance, taskData);
            }
        }
    }

    private String getFieldValueOfTimingModeType(TaskInstance taskInstance, TaskData taskData, TaskTimer taskTimer, TimerElement timerElement) {
        DyFormData dyFormData = taskData.getDyFormData(taskInstance.getDataUuid());
        String timingModeType = StringUtils.trim(Objects.toString(dyFormData.getFieldValue(timerElement.getLimitUnitField()), timerElement.getTimingModeType()));

        List<EnumTimingModeType> validTimingModeTypes = Arrays.stream(EnumTimingModeType.values()).filter(item -> !StringUtils.equals("-1", item.getValue())).collect(Collectors.toList());
        boolean valid = validTimingModeTypes.stream().filter(item -> StringUtils.equals(item.getValue(), timingModeType)).findFirst().isPresent();
        if (BooleanUtils.isNotTrue(valid)) {
            String limitUnitOptionString = validTimingModeTypes.stream().map(item -> item.getValue() + "—" + item.getName())
                    .collect(Collectors.joining(Separator.COMMA.getValue() + " "));
            throw new WorkFlowException("无效的计时方式[" + timingModeType + "]！请确保计时方式值为：" + limitUnitOptionString);
        }
        return timingModeType;
    }

    /**
     * 如何描述该方法
     *
     * @param flowInstance
     * @param taskInstance
     */
    private void pauseTimer(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData,
                            TaskTimer taskTimer) {
        // 如果计时器未启动，则忽略掉
        if (!TimerHelper.isStarted(taskTimer)) {
            Date logTime = Calendar.getInstance().getTime();
            taskTimerLogService.log(taskInstance.getUuid(), flowInstance.getUuid(), taskTimer, logTime,
                    TaskTimerLog.TYPE_ERROR, "计时器未启动，不能暂停！");
            updateTimerTaskData(taskInstance, taskTimer);
            return;
        }

        String timerUuid = taskTimer.getTimerUuid();
        // 1、更新计时器信息
        // 更新计时系统所在的环节
        String taskId = taskInstance.getId();
        String taskInstUuid = taskInstance.getUuid();
        taskTimer.setTaskId(taskId);
        taskTimer.setTaskInstUuid(taskInstUuid);
        // 剩余办理时限
        Date taskDueTime = taskTimer.getTaskDueTime();
        int limitUnit = Integer.valueOf(taskTimer.getLimitUnit());
        Double taskLimitTime = null;
        if (StringUtils.isNotBlank(timerUuid)) {
            taskLimitTime = Double.valueOf(timerFacadeService.pauseTimer(timerUuid));
        } else {
            taskLimitTime = Double.valueOf(TimerHelper.calculateRemainingTaskLimitTime(taskDueTime, limitUnit));
        }
        taskTimer.setTaskLimitTime(taskLimitTime);
        // 计时状态
        taskTimer.setStatus(TaskTimerStatus.PASUE);
        taskTimerService.save(taskTimer);

        // 清空已加入调度的定时器
        timerManagerService.removeAllSchedule(taskTimer.getUuid());

        // 2、同步环节、流程数据
        syncTaskFlowData(taskInstance, flowInstance, taskData, taskTimer);

        // 3、记录日志
        Date logTime = Calendar.getInstance().getTime();
        taskTimerLogService.log(taskInstance.getUuid(), flowInstance.getUuid(), taskTimer, logTime,
                TaskTimerLog.TYPE_PAUSE);

        // 4、发布计时器停止事件
        String listener = taskTimer.getListener();
        if (StringUtils.isNotBlank(listener)) {
            String[] listeners = StringUtils.split(listener, Separator.SEMICOLON.getValue());
            for (String l : listeners) {
                TimerListener timerListener = listenerMap.get(l);
                if (timerListener == null) {
                    continue;
                }
                timerListener.onTimerPause(taskTimer, taskInstance, flowInstance, taskData);
            }
        }
    }

    /**
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     * @param taskTimer
     */
    private void resumeTimer(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData,
                             TaskTimer taskTimer) {
        if (taskInstance.getEndTime() != null) {
            Date logTime = Calendar.getInstance().getTime();
            taskTimerLogService.log(taskInstance.getUuid(), flowInstance.getUuid(), taskTimer, logTime,
                    TaskTimerLog.TYPE_ERROR, String.format("计时环节[%s]已结束，不能重启计时器!", taskInstance.getName()));
        } else {
            String timerUuid = taskTimer.getTimerUuid();
            // 1、更新计时器信息
            Date lastStartTime = Calendar.getInstance().getTime();
            // 设置定时器最新开始时间
            taskTimer.setLastStartTime(lastStartTime);
            // 更新计时系统所在的环节
            String taskId = taskInstance.getId();
            String taskInstUuid = taskInstance.getUuid();
            taskTimer.setTaskId(taskId);
            taskTimer.setTaskInstUuid(taskInstUuid);
            // 办理时限
            int limitTime = taskTimer.getTaskLimitTime().intValue();
            int limitUnit = Integer.valueOf(taskTimer.getLimitUnit());
            Date defaultDueTime = taskTimer.getTaskDueTime();
            Date taskDueTime = null;
            // 恢复计时
            if (StringUtils.isNotBlank(timerUuid)) {
                taskDueTime = timerFacadeService.resumeTimer(timerUuid);
            } else {
                taskDueTime = TimerHelper.calculateDueTime(limitTime, limitUnit, defaultDueTime);
            }
            taskTimer.setTaskDueTime(taskDueTime);
            // 提醒时间
            setTimerAlarmInfos(taskInstance, taskData, taskDueTime, taskTimer);
            // 计时状态
            taskTimer.setStatus(TaskTimerStatus.STARTED);
            // 更新预警、逾期状态
            syncTimingState(taskTimer);
            // 保存定时器
            taskTimerService.save(taskTimer);

            // 2、同步环节、流程数据
            syncTaskFlowData(taskInstance, flowInstance, taskData, taskTimer);

            // 3、记录日志
            Date logTime = Calendar.getInstance().getTime();
            taskTimerLogService.log(taskInstance.getUuid(), flowInstance.getUuid(), taskTimer, logTime,
                    TaskTimerLog.TYPE_RESUME);

            // 4、发布计时器重启事件
            String listener = taskTimer.getListener();
            if (StringUtils.isNotBlank(listener)) {
                String[] listeners = StringUtils.split(listener, Separator.SEMICOLON.getValue());
                for (String l : listeners) {
                    TimerListener timerListener = listenerMap.get(l);
                    if (timerListener == null) {
                        continue;
                    }
                    timerListener.onTimerRestarted(taskTimer, taskInstance, flowInstance, taskData);
                }
            }
        }
    }

    /**
     * @param taskTimer
     * @param taskDueTime
     */
    private void setTimerAlarmInfos(TaskInstance taskInstance, TaskData taskData, Date taskDueTime, TaskTimer taskTimer) {
        if (AbstractTaskTimerService.enabledTaskAlarm(taskTimer)) {
            Map<String, Object> keepData = Maps.newHashMap();
            String alarmTime = getAlarmTime(taskInstance, taskData, taskTimer, keepData);
            if (StringUtils.isBlank(alarmTime)) {
                return;
            }
            Integer alarmAmount = -Double.valueOf(alarmTime).intValue();
            Integer alarmUnit = getAlarmUnit(taskInstance, taskData, taskTimer, keepData);// taskTimer.getAlarmUnit();
            Integer alarmFrequency = getAlarmFrequency(taskInstance, taskData, taskTimer, keepData);
            if (alarmAmount == null || alarmUnit == null || alarmFrequency == null || alarmFrequency <= 0) {
                return;
            }

            Date taskAlarmTime = null;
//            String workTimePlanUuid = taskTimer.getWorkTimePlanUuid();
//            String workTimePlanId = taskTimer.getWorkTimePlanId();
//            workTimePlanUuid = workTimePlanFacadeService.getActiveWorkTimePlanUuidById(workTimePlanId, workTimePlanUuid);
//            if (StringUtils.isNotBlank(workTimePlanUuid)) {
            taskAlarmTime = timerFacadeService.calculateTime(taskTimer.getTimerUuid(), taskDueTime, alarmAmount, alarmUnit + StringUtils.EMPTY);
//            } else {
//                taskAlarmTime = TimerHelper.calculateAlarmTime(taskDueTime, alarmAmount, alarmUnit);
//            }
            // 预警提醒时间间隔
            long alarmRepeatInterval = taskDueTime.getTime() - taskAlarmTime.getTime();
            int alarmTotalCount = alarmFrequency;// taskTimer.getAlarmFrequency();
            if (alarmTotalCount > 1) {
                alarmRepeatInterval = alarmRepeatInterval / alarmTotalCount;
            }
            taskTimer.setTaskAlarmTime(taskAlarmTime);
            taskTimer.setAlarmRepeatInterval(alarmRepeatInterval);
            taskTimer.setAlarmTime(alarmTime);
            taskTimer.setAlarmUnit(alarmUnit);
            taskTimer.setAlarmFrequency(alarmFrequency);
        }
    }

    private Integer getAlarmFrequency(TaskInstance taskInstance, TaskData taskData, TaskTimer taskTimer, Map<String, Object> keepData) {
        if (taskTimer.getAlarmFrequency() != null) {
            return taskTimer.getAlarmFrequency();
        }

        AlarmElement alarmElement = getTimerAlarmElement(taskInstance, taskData, taskTimer, keepData);
        if (alarmElement == null) {
            return taskTimer.getAlarmFrequency();
        }
        if (!StringUtils.equals("2", alarmElement.getAlarmFrequencyType())) {
            return taskTimer.getAlarmFrequency();
        }
        DyFormData dyFormData = getDyformData(taskInstance, taskData, taskTimer, keepData);
        return Integer.valueOf(StringUtils.trim(Objects.toString(dyFormData.getFieldValue(alarmElement.getAlarmFrequency()), StringUtils.EMPTY)));
    }

    private Integer getAlarmUnit(TaskInstance taskInstance, TaskData taskData, TaskTimer taskTimer, Map<String, Object> keepData) {
        if (taskTimer.getAlarmUnit() != null) {
            return taskTimer.getAlarmUnit();
        }

        AlarmElement alarmElement = getTimerAlarmElement(taskInstance, taskData, taskTimer, keepData);
        if (alarmElement == null) {
            return taskTimer.getAlarmUnit();
        }
        if (!StringUtils.equals("2", alarmElement.getAlarmUnitType())) {
            return taskTimer.getAlarmUnit();
        }
        DyFormData dyFormData = getDyformData(taskInstance, taskData, taskTimer, keepData);
        return Integer.valueOf(StringUtils.trim(Objects.toString(dyFormData.getFieldValue(alarmElement.getAlarmUnit()), StringUtils.EMPTY)));
    }

    private String getAlarmTime(TaskInstance taskInstance, TaskData taskData, TaskTimer taskTimer, Map<String, Object> keepData) {
        if (NumberUtils.isNumber(taskTimer.getAlarmTime())) {
            return taskTimer.getAlarmTime();
        }

        AlarmElement alarmElement = getTimerAlarmElement(taskInstance, taskData, taskTimer, keepData);
        if (alarmElement == null) {
            return taskTimer.getAlarmTime();
        }
        if (!StringUtils.equals("2", alarmElement.getAlarmTimeType())) {
            return taskTimer.getAlarmTime();
        }
        DyFormData dyFormData = getDyformData(taskInstance, taskData, taskTimer, keepData);
        return StringUtils.trim(Objects.toString(dyFormData.getFieldValue(alarmElement.getAlarmTime()), StringUtils.EMPTY));
    }

    private DyFormData getDyformData(TaskInstance taskInstance, TaskData taskData, TaskTimer taskTimer, Map<String, Object> keepData) {
        DyFormData dyFormData = (DyFormData) keepData.get("dyFormData");
        if (dyFormData == null) {
            TaskInstance task = taskInstance;
            if (task == null) {
                task = taskService.get(taskTimer.getTaskInstUuid());
            }
            if (taskData != null && taskData.getDyFormData(task.getDataUuid()) != null) {
                dyFormData = taskData.getDyFormData(task.getDataUuid());
            } else {
                dyFormData = dyFormFacade.getDyFormData(task.getFormUuid(), task.getDataUuid());
            }
            keepData.put("dyFormData", dyFormData);
        }
        return dyFormData;
    }

    /**
     * 第一次启动时初始化办理时限
     *
     * @param taskData
     * @param taskTimer
     */
    private TaskLimitTimeInitResult initLimitTime(String formUuid, String dataUuid, TaskData taskData,
                                                  TaskTimer taskTimer) {
        if (!TimerHelper.isReady(taskTimer)) {
            return new TaskLimitTimeInitResult(true);
        }
        DyFormData dyformData = taskData.getDyFormData(dataUuid);
        String limitTimeType = taskTimer.getLimitTimeType();
        String limitTime1 = taskTimer.getLimitTime1();
        String limitTime2 = taskTimer.getLimitTime2();
        String limitUnit = taskTimer.getLimitUnit();
        Boolean ignoreEmptyLimitTime = taskTimer.getIgnoreEmptyLimitTime();
        TaskLimitTime limitTime = TimerHelper.calculateTaskLimitTime(formUuid, dataUuid, dyformData, limitTimeType,
                limitTime1, limitTime2, limitUnit, ignoreEmptyLimitTime);
        Double taskLimitTime = limitTime.getTaskLimitTime();
        // 时限为空时不计时
        if (taskLimitTime == null && limitTime.getTaskDueTime() == null && taskTimer.getIgnoreEmptyLimitTime()) {
            logger.error("流程实例UUID为{}的计时器{}办理时限初始化失败!", taskTimer.getFlowInstUuid(), taskTimer.getName());
            return new TaskLimitTimeInitResult(false);
        }
        taskTimer.setTaskInitLimitTime(taskLimitTime);
        taskTimer.setTaskLimitTime(taskLimitTime);
        if (limitTime.isDateOfLimitTime()) {
            taskTimer.setTaskDueTime(limitTime.getTaskDueTime());
            taskTimer.setTaskInitDueTime(limitTime.getTaskDueTime());
        }
        return new TaskLimitTimeInitResult(true, limitTime);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.TimerExecutor#stop(com.wellsoft.pt.bpm.engine.node.Node, com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    @Override
    public void pause(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData) {
        this.pause(taskInstance, flowInstance, taskData, null);
    }

    @Override
    public void pause(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData, String timerName) {
        String taskInstUuid = taskInstance.getUuid();
        String flowInstUuid = flowInstance.getUuid();
        List<TaskTimer> taskTimers = taskTimerService.getActiveTimersByTaskInstUuidAndFlowInstUuid(taskInstUuid,
                flowInstUuid, timerName);
        for (TaskTimer taskTimer : taskTimers) {
            pauseTimer(taskInstance, flowInstance, taskData, taskTimer);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.TimerExecutor#stop(com.wellsoft.pt.bpm.engine.node.Node, com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    @Override
    public void stop(FlowInstance flowInstance) {
        this.stop(flowInstance, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.TimerExecutor#stopByDirection(com.wellsoft.pt.bpm.engine.core.Direction, com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    @Override
    public void stopByDirection(Direction direction, ExecutionContext executionContext) {
        FlowInstance flowInstance = executionContext.getFlowInstance();
        TaskData taskData = executionContext.getToken().getTaskData();

        List<TaskTimer> taskTimers = taskTimerService.getActiveTimersByDirectionIdAndFlowInstUuid(direction.getId(),
                flowInstance.getUuid());

        if (CollectionUtils.isEmpty(taskTimers)) {
            return;
        }

        for (TaskTimer taskTimer : taskTimers) {
            TaskInstance taskInstance = taskService.getTask(taskTimer.getTaskInstUuid());
            stopTimer(taskInstance, flowInstance, taskData, taskTimer);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.TimerExecutor#stopByTaskInstance(com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.context.ExecutionContext)
     */
    @Override
    public void stopByTaskInstance(TaskInstance taskInstance, ExecutionContext executionContext) {
        FlowInstance flowInstance = executionContext.getFlowInstance();
        TaskData taskData = executionContext.getToken().getTaskData();

        List<TaskTimer> taskTimers = taskTimerService
                .getActiveTimersByTaskInstUuidAndFlowInstUuid(taskInstance.getUuid(), flowInstance.getUuid());

        if (CollectionUtils.isEmpty(taskTimers)) {
            return;
        }

        for (TaskTimer taskTimer : taskTimers) {
            stopTimer(taskInstance, flowInstance, taskData, taskTimer);
        }
    }

    @Override
    public void stopByTaskInstanceAndTimerName(TaskInstance taskInstance, ExecutionContext executionContext,
                                               String timerName) {
        FlowInstance flowInstance = executionContext.getFlowInstance();
        TaskData taskData = executionContext.getToken().getTaskData();

        List<TaskTimer> taskTimers = taskTimerService.getActiveTimersByTaskInstUuidAndFlowInstUuid(
                taskInstance.getUuid(), flowInstance.getUuid(), timerName);

        if (CollectionUtils.isEmpty(taskTimers)) {
            return;
        }

        for (TaskTimer taskTimer : taskTimers) {
            stopTimer(taskInstance, flowInstance, taskData, taskTimer);
        }
    }

    /**
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     * @param taskTimer
     */
    private void stopTimer(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData,
                           TaskTimer taskTimer) {
        Integer currentStatus = taskTimer.getStatus();
        String timerUuid = taskTimer.getTimerUuid();
        // 1、更新计时器信息
        // 更新计时系统所在的环节
        String taskId = taskInstance.getId();
        String taskInstUuid = taskInstance.getUuid();
        taskTimer.setTaskId(taskId);
        taskTimer.setTaskInstUuid(taskInstUuid);
        // 剩余办理时限
        Date taskDueTime = taskTimer.getTaskDueTime();
        Double taskLimitTime = null;
        if (StringUtils.isNotBlank(timerUuid)) {
            taskLimitTime = Double.valueOf(timerFacadeService.stopTimer(timerUuid));
            taskTimer.setTaskLimitTime(taskLimitTime);
        } else {
            if (taskDueTime != null) {
                int limitUnit = Integer.valueOf(taskTimer.getLimitUnit());
                taskLimitTime = Double.valueOf(TimerHelper.calculateRemainingTaskLimitTime(taskDueTime, limitUnit));
                taskTimer.setTaskLimitTime(taskLimitTime);
            }
        }
        // 计时状态
        taskTimer.setStatus(TaskTimerStatus.STOP);
        // 保存定时器
        taskTimerService.save(taskTimer);

        // 清空已加入调度的定时器
        timerManagerService.removeAllSchedule(taskTimer.getUuid());

        // 2、同步环节、流程数据
        if (TaskTimerStatus.STARTED.equals(currentStatus)) {
            syncTaskFlowData(taskInstance, flowInstance, taskData, taskTimer);
        }

        // 3、停止任务定时器跟踪记录
        Date logTime = Calendar.getInstance().getTime();
        taskTimerLogService.log(taskInstance.getUuid(), flowInstance.getUuid(), taskTimer, logTime,
                TaskTimerLog.TYPE_END);

        // 4、发布计时器停止事件
        String listener = taskTimer.getListener();
        if (StringUtils.isNotBlank(listener)) {
            String[] listeners = StringUtils.split(listener, Separator.SEMICOLON.getValue());
            for (String l : listeners) {
                TimerListener timerListener = listenerMap.get(l);
                if (timerListener == null) {
                    continue;
                }
                timerListener.onTimerStopped(taskTimer, taskInstance, flowInstance, taskData);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.TimerExecutor#restart(java.lang.String)
     */
    @Override
    public void restart(String flowInstUuid) {
        List<TaskTimer> taskTimers = taskTimerService.getByFlowInstUuid(flowInstUuid);
        TaskInstance taskInstance = taskService.getLastTaskInstanceByFlowInstUuid(flowInstUuid);
        String taskId = taskInstance.getId();
        for (TaskTimer taskTimer : taskTimers) {
            // 1、计时器进入计时环节
            if (TimerHelper.isInTimingTask(taskId, taskTimer)) {
                FlowInstance flowInstance = taskInstance.getFlowInstance();
                TaskData taskData = new TaskData();
                List<TimerElement> timerElements = Lists.newArrayList();
                doStartTimer(taskInstance, flowInstance, taskData, taskTimer, timerElements);
            } else {
                // 2、计时器不在计时环节，记录日志
                Date logTime = Calendar.getInstance().getTime();
                taskTimerLogService.log(taskInstance.getUuid(), flowInstUuid, taskTimer, logTime,
                        TaskTimerLog.TYPE_INFO, "计时器不在计时环节，无法重启计时器！");
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.TimerExecutor#restart(java.lang.String, java.lang.String)
     */
    @Override
    public void restart(String taskInstUuid, String flowInstUuid) {
        List<TaskTimer> taskTimers = taskTimerService.getActiveTimersByTaskInstUuidAndFlowInstUuid(taskInstUuid,
                flowInstUuid);
        TaskInstance taskInstance = taskService.getTask(taskInstUuid);
        for (TaskTimer taskTimer : taskTimers) {
            // 1、计时器进入计时环节
            if (TimerHelper.isInTimingTask(taskInstance.getId(), taskTimer)) {
                FlowInstance flowInstance = taskInstance.getFlowInstance();
                TaskData taskData = new TaskData();
                List<TimerElement> timerElements = Lists.newArrayList();
                doStartTimer(taskInstance, flowInstance, taskData, taskTimer, timerElements);
            } else {
                // 2、计时器不在计时环节，记录日志
                Date logTime = Calendar.getInstance().getTime();
                taskTimerLogService.log(taskInstance.getUuid(), flowInstUuid, taskTimer, logTime,
                        TaskTimerLog.TYPE_INFO, "计时器不在计时环节，无法重启计时器！");
            }
        }
    }

    /**
     * 如何描述该方法
     *
     * @param taskTimer
     * @param timerEvent
     * @param taskInstance
     * @param flowInstance
     * @return
     */
    private boolean preHandle(TaskTimer taskTimer, String timerEvent, TaskInstance taskInstance,
                              FlowInstance flowInstance) {
        String listener = taskTimer.getListener();
        if (StringUtils.isNotBlank(listener)) {
            String[] listeners = StringUtils.split(listener, Separator.SEMICOLON.getValue());
            for (String l : listeners) {
                TimerListener timerListener = listenerMap.get(l);
                if (timerListener == null) {
                    continue;
                }
                boolean checkResult = timerListener.preHandle(taskTimer, timerEvent, taskInstance, flowInstance);
                if (!checkResult) {
                    return checkResult;
                }
            }
        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.TimerExecutor#changeFlowLimitTime(double, java.lang.String)
     */
    @Override
    public void changeLimitTime(int limitTime, String flowInstUuid) {
        List<TaskTimer> taskTimers = taskTimerService.getByFlowInstUuid(flowInstUuid);
        for (TaskTimer taskTimer : taskTimers) {
            doChangeLimitTime(limitTime, flowInstUuid, taskTimer);
        }
    }

    /**
     * @param limitTime
     * @param flowInstUuid
     * @param taskTimer
     */
    private void doChangeLimitTime(int limitTime, String flowInstUuid, TaskTimer taskTimer) {
        // 计时状态
        Integer timerStatus = taskTimer.getStatus();
        switch (timerStatus) {
            case 0:
                // 准备就绪中，启动
                // 计时器未启动，变更为指定数字且不自动更新
                changeLimitTimeOfReadyTimer(limitTime, flowInstUuid, taskTimer);
                break;
            case 1:
                // 已启动，更新计时器信息，修改时限后按照新的时限计时及更新预警、逾期状态
                changeLimitTimeOfStartedTimer(limitTime, flowInstUuid, taskTimer);
                break;
            case 2:
                // 暂停中，更新计时器信息，计算剩余的办理时限
                changeLimitTimeOfPauseTimer(limitTime, flowInstUuid, taskTimer);
                break;
            case 3:
                // 已停止，更新计时器信息，计算剩余的办理时限
                changeLimitTimeOfStopTimer(limitTime, flowInstUuid, taskTimer);
                break;
            default:
                break;
        }
    }

    /**
     * @param limitTime
     * @param flowInstUuid
     * @param taskTimer
     */
    private void changeLimitTimeOfReadyTimer(int limitTime, String flowInstUuid, TaskTimer taskTimer) {
        String oldLimitTimeType = taskTimer.getLimitTimeType();
        String oldLimitTime = taskTimer.getLimitTime1();
        if (StringUtils.equals(TaskTimer.LIMIT_TIME_TYPE_FORM_FIELD, oldLimitTimeType)) {
            oldLimitTime = taskTimer.getLimitTime2();
        }
        taskTimer.setLimitTimeType(TaskTimer.LIMIT_TIME_TYPE_NUMBER);
        taskTimer.setLimitTime1(limitTime + StringUtils.EMPTY);
        taskTimerService.save(taskTimer);

        // 记录日志
        Date logTime = Calendar.getInstance().getTime();
        String remark = "计时器未启动，变更时限为[" + limitTime + "]，旧的时限类型[" + oldLimitTimeType + "]，时限[" + oldLimitTime
                + "]，新的时限类型[" + taskTimer.getLimitTimeType() + "]，时限[" + taskTimer.getLimitTime1() + "]";
        taskTimerLogService.log(taskTimer, logTime, TaskTimerLog.TYPE_INFO, remark);
    }

    /**
     * @param limitTime
     * @param flowInstUuid
     * @param taskTimer
     */
    private void changeLimitTimeOfStartedTimer(int limitTime, String flowInstUuid, TaskTimer taskTimer) {
        String timerUuid = taskTimer.getTimerUuid();
        // 1、更新计时器信息，修改时限后按照新的时限计时及更新预警、逾期状态
        // 计算剩余的办理时限
        Date oldTaskDueTime = taskTimer.getTaskDueTime();
        int taskInitLimitTime = taskTimer.getTaskInitLimitTime().intValue();
        int limitUnit = Integer.valueOf(taskTimer.getLimitUnit());
        // 新的办理时限
        Date taskDueTime = null;
        TsTimerEntity timerEntity = null;
        if (StringUtils.isNotBlank(timerUuid)) {
            timerFacadeService.changeTimeLimit(timerUuid, limitTime);
            timerEntity = timerFacadeService.getTimer(timerUuid);
            taskDueTime = timerEntity.getDueTime();
        } else {
            int remainingTaskLimitTime = TimerHelper.calculateRemainingTaskLimitTime(oldTaskDueTime, limitUnit);
            // 变更的时限
            int changeLimitTimePart = limitTime - taskInitLimitTime;
            if (remainingTaskLimitTime <= 0) {
                remainingTaskLimitTime = changeLimitTimePart;
            } else {
                remainingTaskLimitTime += changeLimitTimePart;
            }
            // 缩小时限后逾期
            if (remainingTaskLimitTime < 0) {
                Date fromTime = Calendar.getInstance().getTime();
                taskDueTime = TimerHelper.calculateTime(fromTime, remainingTaskLimitTime, limitUnit);
            } else {
                taskDueTime = TimerHelper.calculateDueTime(remainingTaskLimitTime, limitUnit, oldTaskDueTime);
            }
        }
        taskTimer.setTaskDueTime(taskDueTime);
        if (timerEntity != null) {
            taskTimer.setDueDoingDone(timerEntity.getOverDueDoingDone());
        }
        // 提醒时间
        setTimerAlarmInfos(null, null, taskDueTime, taskTimer);
        // 更新预警、逾期状态
        syncTimingState(taskTimer);
        // 变更最初的时限
        taskTimer.setTaskInitLimitTime(Double.valueOf(limitTime));
        // 保存定时器
        taskTimerService.save(taskTimer);

        // 清空已加入调度的定时器
        timerManagerService.removeAllSchedule(taskTimer.getUuid());

        // 2、同步环节、流程数据
        TaskInstance taskInstance = taskService.getTask(taskTimer.getTaskInstUuid());
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        syncTaskFlowData(taskInstance, flowInstance, taskTimer);

        // 3、记录日志
        Date logTime = Calendar.getInstance().getTime();
        String remark = "计时器已启动，变更时限为[" + limitTime + "]，旧的办理时限[" + DateUtils.formatDateTime(oldTaskDueTime)
                + "]，新的办理时限[" + DateUtils.formatDateTime(taskDueTime) + "]";
        taskTimerLogService.log(taskTimer, logTime, TaskTimerLog.TYPE_INFO, remark);

    }

    /**
     * @param limitTime
     * @param flowInstUuid
     * @param taskTimer
     */
    private void changeLimitTimeOfPauseTimer(int limitTime, String flowInstUuid, TaskTimer taskTimer) {
        String timerUuid = taskTimer.getTimerUuid();
        // 1、更新计时器信息
        // 计算剩余的办理时限
        int taskInitLimitTime = taskTimer.getTaskInitLimitTime().intValue();
        int oldTaskLimitTime = taskTimer.getTaskLimitTime().intValue();
        int taskLimitTime = oldTaskLimitTime;
        if (StringUtils.isNotBlank(timerUuid)) {
            taskLimitTime = timerFacadeService.changeTimeLimit(timerUuid, taskLimitTime);
        } else {
            // 变更的时限
            int changeLimitTimePart = limitTime - taskInitLimitTime;
            if (taskLimitTime <= 0) {
                taskLimitTime = changeLimitTimePart;
            } else {
                taskLimitTime += changeLimitTimePart;
            }
        }

        // 剩余办理时限
        taskTimer.setTaskLimitTime(Double.valueOf(taskLimitTime));
        // 变更最初的时限
        taskTimer.setTaskInitLimitTime(Double.valueOf(limitTime));
        if (taskLimitTime > 0) {
            taskTimer.setDueDoingDone(false);
        }
        // 更新预警、逾期状态
        syncTimingState(taskTimer);
        taskTimerService.save(taskTimer);

        // 清空已加入调度的定时器
        timerManagerService.removeAllSchedule(taskTimer.getUuid());

        // 2、同步环节、流程数据
        TaskInstance taskInstance = taskService.getTask(taskTimer.getTaskInstUuid());
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        syncTaskFlowData(taskInstance, flowInstance, taskTimer);

        // 3、记录日志
        Date logTime = Calendar.getInstance().getTime();
        String remark = "计时器已暂停，变更时限为[" + limitTime + "]，旧的剩余办理时限[" + oldTaskLimitTime + "]，新的剩余办理时限[" + taskLimitTime
                + "]";
        taskTimerLogService.log(taskInstance.getUuid(), flowInstance.getUuid(), taskTimer, logTime,
                TaskTimerLog.TYPE_INFO, remark);
    }

    /**
     * @param limitTime
     * @param flowInstUuid
     * @param taskTimer
     */
    private void changeLimitTimeOfStopTimer(int limitTime, String flowInstUuid, TaskTimer taskTimer) {
        String timerUuid = taskTimer.getTimerUuid();
        // 1、更新计时器信息
        // 计算剩余的办理时限
        int taskInitLimitTime = taskTimer.getTaskInitLimitTime().intValue();
        int oldTaskLimitTime = taskTimer.getTaskLimitTime().intValue();
        int taskLimitTime = oldTaskLimitTime;
        if (StringUtils.isNotBlank(timerUuid)) {
            taskLimitTime = timerFacadeService.changeTimeLimit(timerUuid, taskLimitTime);
        } else {
            // 变更的时限
            int changeLimitTimePart = limitTime - taskInitLimitTime;
            if (taskLimitTime <= 0) {
                taskLimitTime = changeLimitTimePart;
            } else {
                taskLimitTime += changeLimitTimePart;
            }
        }

        // 剩余办理时限
        taskTimer.setTaskLimitTime(Double.valueOf(taskLimitTime));
        // 变更最初的时限
        taskTimer.setTaskInitLimitTime(Double.valueOf(limitTime));
        taskTimerService.save(taskTimer);

        // 清空已加入调度的定时器
        timerManagerService.removeAllSchedule(taskTimer.getUuid());

        // 2、同步环节、流程数据
        TaskInstance taskInstance = taskService.getTask(taskTimer.getTaskInstUuid());
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        syncTaskFlowData(taskInstance, flowInstance, taskTimer);

        // 3、记录日志
        Date logTime = Calendar.getInstance().getTime();
        String remark = "计时器已停止，变更时限为[" + limitTime + "]，旧的剩余办理时限[" + oldTaskLimitTime + "]，新的剩余办理时限[" + taskLimitTime
                + "]";
        taskTimerLogService.log(taskInstance.getUuid(), flowInstance.getUuid(), taskTimer, logTime,
                TaskTimerLog.TYPE_INFO, remark);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.TimerExecutor#changeDueTime(java.util.Date, java.lang.String)
     */
    @Override
    public void changeDueTime(Date dueTime, String flowInstUuid) {
        List<TaskTimer> taskTimers = taskTimerService.getByFlowInstUuid(flowInstUuid);
        for (TaskTimer taskTimer : taskTimers) {
            doChangeDueTime(dueTime, flowInstUuid, taskTimer);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.TimerExecutor#changeDueTime(java.util.Date, java.lang.String, java.lang.String)
     */
    @Override
    public void changeDueTime(Date dueTime, String taskInstUuid, String flowInstUuid) {
        List<TaskTimer> taskTimers = taskTimerService.getActiveTimersByTaskInstUuidAndFlowInstUuid(taskInstUuid,
                flowInstUuid);
        for (TaskTimer taskTimer : taskTimers) {
            doChangeDueTime(dueTime, flowInstUuid, taskTimer);
        }
    }

    /**
     * @param dueTime
     * @param flowInstUuid
     * @param taskTimer
     */
    private void doChangeDueTime(Date dueTime, String flowInstUuid, TaskTimer taskTimer) {
        // 计时状态
        Integer timerStatus = taskTimer.getStatus();
        switch (timerStatus) {
            case 0:
                // 准备就绪中，启动
                // 计时器未启动，变更为指定日期且不自动更新
                changeDueTimeOfReadyTimer(dueTime, flowInstUuid, taskTimer);
                break;
            case 1:
                // 已启动，更新计时器信息，修改时限后按照新的时限计时及更新预警、逾期状态
                changeDueTimeOfStartedTimer(dueTime, flowInstUuid, taskTimer);
                break;
            case 2:
                // 暂停中，更新计时器信息，计算剩余的办理时限
                changeDueTimeOfPauseTimer(dueTime, flowInstUuid, taskTimer);
                break;
            case 3:
                // 已停止，更新计时器信息，计算剩余的办理时限
                changeDueTimeOfStopTimer(dueTime, flowInstUuid, taskTimer);
                break;
            default:
                break;
        }
    }

    /**
     * 计时器未启动，变更为指定日期且不自动更新
     *
     * @param dueTime
     * @param flowInstUuid
     * @param taskTimer
     */
    private void changeDueTimeOfReadyTimer(Date dueTime, String flowInstUuid, TaskTimer taskTimer) {
        String oldLimitTimeType = taskTimer.getLimitTimeType();
        String oldLimitTime = taskTimer.getLimitTime1();
        if (StringUtils.equals(TaskTimer.LIMIT_TIME_TYPE_FORM_DATE, oldLimitTimeType)) {
            oldLimitTime = taskTimer.getLimitTime2();
        }
        taskTimer.setLimitTimeType(TaskTimer.LIMIT_TIME_TYPE_FIXED_DATE);
        taskTimer.setLimitTime1(DateUtils.formatDateTime(dueTime));
        taskTimerService.save(taskTimer);

        // 记录日志
        Date logTime = Calendar.getInstance().getTime();
        String remark = "计时器未启动，变更到期时间为[" + taskTimer.getLimitTime1() + "]，旧的时限类型[" + oldLimitTimeType + "]，时限["
                + oldLimitTime + "]，新的时限类型[" + taskTimer.getLimitTimeType() + "]，时限[" + taskTimer.getLimitTime1() + "]";
        taskTimerLogService.log(taskTimer, logTime, TaskTimerLog.TYPE_INFO, remark);
    }

    /**
     * 计时器已启动，变更为指定数字且不自动更新
     *
     * @param dueTime
     * @param flowInstUuid
     * @param taskTimer
     */
    private void changeDueTimeOfStartedTimer(Date dueTime, String flowInstUuid, TaskTimer taskTimer) {
        // 1、更新计时器信息，修改时限后按照新的时限计时及更新预警、逾期状态
        // 计时服务处理
        String timerUuid = taskTimer.getTimerUuid();
        if (StringUtils.isNotBlank(timerUuid)) {
            timerFacadeService.changeDueTime(timerUuid, dueTime);
        }
        // 剩余办理时限
        Date oldTaskDueTime = taskTimer.getTaskDueTime();
        int limitUnit = Integer.valueOf(taskTimer.getLimitUnit());
        Date taskDueTime = TimerHelper.convertTime(dueTime, limitUnit);
        taskTimer.setTaskDueTime(taskDueTime);
        if (taskDueTime != null && taskDueTime.after(Calendar.getInstance().getTime())) {
            taskTimer.setDueDoingDone(false);
        }
        // 提醒时间
        setTimerAlarmInfos(null, null, taskDueTime, taskTimer);
        // 更新预警、逾期状态
        syncTimingState(taskTimer);
        // 变更最初的时限
        taskTimer.setTaskInitDueTime(dueTime);
        // 保存定时器
        taskTimerService.save(taskTimer);

        // 清空已加入调度的定时器
        timerManagerService.removeAllSchedule(taskTimer.getUuid());

        // 2、同步环节、流程数据
        TaskInstance taskInstance = taskService.getTask(taskTimer.getTaskInstUuid());
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        syncTaskFlowData(taskInstance, flowInstance, taskTimer);

        // 3、记录日志
        Date logTime = Calendar.getInstance().getTime();
        String remark = "计时器已启动，变更到期时间为[" + DateUtils.formatDateTime(dueTime) + "]，旧的办理时限["
                + DateUtils.formatDateTime(oldTaskDueTime) + "]，新的办理时限[" + DateUtils.formatDateTime(taskDueTime) + "]";
        taskTimerLogService.log(taskTimer, logTime, TaskTimerLog.TYPE_INFO, remark);
    }

    /**
     * @param taskTimer
     */
    private void syncTimingState(TaskTimer taskTimer) {
        Date currentTime = Calendar.getInstance().getTime();
        // currentTime = TimerHelper.convertTime(currentTime, timerUnit);
        // 预警状态
        Date taskAlarmTime = taskTimer.getTaskAlarmTime();
        if (taskAlarmTime != null && currentTime.after(taskAlarmTime)) {
            taskTimer.setTimingState(TimingState.ALARM);
            taskTimer.setAlarmState(1);
        } else {
            taskTimer.setTimingState(TimingState.NORMAL);
            taskTimer.setAlarmState(0);
        }

        String timerUuid = taskTimer.getTimerUuid();
        if (StringUtils.isNotBlank(timerUuid)) {
            TsTimerEntity timerEntity = timerFacadeService.getTimer(timerUuid);
            taskTimer.setTimingState(timerEntity.getTimingState());
            boolean isOverDue = Integer.valueOf(EnumTimingState.OVER_DUE.getValue()).equals(timerEntity.getTimingState());
            taskTimer.setOverDueState(isOverDue ? 1 : 0);
        } else {
            // 到期状态
            Date taskDueTime = taskTimer.getTaskDueTime();
            if (taskDueTime != null && currentTime.after(taskDueTime)) {
                taskTimer.setTimingState(TimingState.DUE);
                // 逾期状态
                Date taskOverDueTime = null;
                if (StringUtils.isNotBlank(timerUuid)) {
                    taskOverDueTime = timerFacadeService.getOverDueTime(timerUuid);
                } else {
                    int timerUnit = Integer.valueOf(taskTimer.getLimitUnit());
                    taskOverDueTime = TimerHelper.getOverDueTime(taskDueTime, timerUnit);
                }
                if (currentTime.after(taskOverDueTime)) {
                    taskTimer.setTimingState(TimingState.OVER_DUE);
                    taskTimer.setOverDueState(1);
                } else {
                    taskTimer.setOverDueState(0);
                }
            } else {
                taskTimer.setOverDueState(0);
            }
        }
    }

    /**
     * @param dueTime
     * @param flowInstUuid
     * @param taskTimer
     */
    private void changeDueTimeOfPauseTimer(Date dueTime, String flowInstUuid, TaskTimer taskTimer) {
        String timerUuid = taskTimer.getTimerUuid();
        // 1、更新计时器信息
        // 剩余办理时限
        Double oldTaskLimitTime = taskTimer.getTaskLimitTime();
        int limitUnit = Integer.valueOf(taskTimer.getLimitUnit());
        Double taskLimitTime = null;
        // 计时服务处理
        if (StringUtils.isNotBlank(timerUuid)) {
            taskLimitTime = Double.valueOf(timerFacadeService.changeDueTime(timerUuid, dueTime));
        } else {
            taskLimitTime = Double.valueOf(TimerHelper.calculateRemainingTaskLimitTime(dueTime, limitUnit));
        }
        taskTimer.setTaskLimitTime(taskLimitTime);
        // 变更最初的时限
        taskTimer.setTaskInitDueTime(dueTime);
        // 已存在到期时间，更新为变更的到期时间
        if (taskTimer.getTaskDueTime() != null) {
            taskTimer.setTaskDueTime(dueTime);
        }
        if (dueTime != null && dueTime.after(Calendar.getInstance().getTime())) {
            taskTimer.setDueDoingDone(false);
        }
        // 更新预警、逾期状态
        syncTimingState(taskTimer);
        taskTimerService.save(taskTimer);

        // 清空已加入调度的定时器
        timerManagerService.removeAllSchedule(taskTimer.getUuid());

        // 2、同步环节、流程数据
        TaskInstance taskInstance = taskService.getTask(taskTimer.getTaskInstUuid());
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        syncTaskFlowData(taskInstance, flowInstance, taskTimer);

        // 3、记录日志
        Date logTime = Calendar.getInstance().getTime();
        String remark = "计时器已暂停，变更到期时间为[" + DateUtils.formatDateTime(dueTime) + "]，旧的剩余办理时限[" + oldTaskLimitTime
                + "]，新的剩余办理时限[" + taskLimitTime + "]";
        taskTimerLogService.log(taskInstance.getUuid(), flowInstance.getUuid(), taskTimer, logTime,
                TaskTimerLog.TYPE_INFO, remark);
    }

    /**
     * @param dueTime
     * @param flowInstUuid
     * @param taskTimer
     */
    private void changeDueTimeOfStopTimer(Date dueTime, String flowInstUuid, TaskTimer taskTimer) {
        String timerUuid = taskTimer.getTimerUuid();
        // 1、更新计时器信息
        // 剩余办理时限
        Double oldTaskLimitTime = taskTimer.getTaskLimitTime();
        int limitUnit = Integer.valueOf(taskTimer.getLimitUnit());
        Double taskLimitTime = null;
        // 计时服务处理
        if (StringUtils.isNotBlank(timerUuid)) {
            taskLimitTime = Double.valueOf(timerFacadeService.changeDueTime(timerUuid, dueTime));
        } else {
            taskLimitTime = Double.valueOf(TimerHelper.calculateRemainingTaskLimitTime(dueTime, limitUnit));
        }
        taskTimer.setTaskLimitTime(taskLimitTime);
        // 变更最初的时限
        taskTimer.setTaskInitDueTime(dueTime);
        taskTimerService.save(taskTimer);

        // 清空已加入调度的定时器
        timerManagerService.removeAllSchedule(taskTimer.getUuid());

        // 2、同步环节、流程数据
        TaskInstance taskInstance = taskService.getTask(taskTimer.getTaskInstUuid());
        FlowInstance flowInstance = taskInstance.getFlowInstance();
        syncTaskFlowData(taskInstance, flowInstance, taskTimer);

        // 3、记录日志
        Date logTime = Calendar.getInstance().getTime();
        String remark = "计时器已停止，变更到期时间为[" + DateUtils.formatDateTime(dueTime) + "]，旧的剩余办理时限[" + oldTaskLimitTime
                + "]，新的剩余办理时限[" + taskLimitTime + "]";
        taskTimerLogService.log(taskInstance.getUuid(), flowInstance.getUuid(), taskTimer, logTime,
                TaskTimerLog.TYPE_INFO, remark);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.TimerExecutor#update(com.wellsoft.pt.bpm.engine.entity.FlowInstance)
     */
    public void update(FlowInstance flowInstance) {
        String flowInstUuid = flowInstance.getUuid();
        List<TaskTimer> taskTimers = taskTimerService.getByFlowInstUuid(flowInstUuid);
        List<TimerElement> timerElements = FlowDelegateUtils.getFlowDelegate(flowInstance.getFlowDefinition())
                .getTimers();
        for (TaskTimer taskTimer : taskTimers) {
            for (TimerElement timerElement : timerElements) {
                String configTimerName = timerElement.getName();
                if (StringUtils.equals(taskTimer.getName(), configTimerName)) {
                    updateLimitTimeIfRequire(flowInstance, new TaskData(), taskTimer, timerElement);
                }
            }
        }
        this.dao.getSession().evict(flowInstance);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.TimerExecutor#update(java.lang.String)
     */
    @Override
    public void update(String flowInstUuid) {
        FlowInstance flowInstance = FlowEngine.getInstance().getFlowService().getFlowInstance(flowInstUuid);
        update(flowInstance);
    }

    /**
     * @param flowInstance
     * @param taskTimer
     * @param timerElement
     */
    private void updateLimitTimeIfRequire(FlowInstance flowInstance, TaskData taskData, TaskTimer taskTimer,
                                          TimerElement timerElement) {
        // 自动更新时限
        Boolean autoUpdateLimitTime = taskTimer.getAutoUpdateLimitTime();
        if (!Boolean.TRUE.equals(autoUpdateLimitTime)) {
            return;
        }

        // 最新计时器配置
        String formUuid = flowInstance.getFormUuid();
        String dataUuid = flowInstance.getDataUuid();
        DyFormData dyformData = taskData.getDyFormData(dataUuid);
        String limitTimeType = timerElement.getLimitTimeType();// taskTimer.getLimitTimeType();
        String limitTime1 = timerElement.getLimitTime1();// taskTimer.getLimitTime1();
        String limitTime2 = timerElement.getLimitTime();// taskTimer.getLimitTime2();
        String limitUnit = taskTimer.getLimitUnit();// timerElement.getLimitUnit();// taskTimer.getLimitUnit();
        Boolean ignoreEmptyLimitTime = timerElement.getIsIgnoreEmptyLimitTime();// taskTimer.getIgnoreEmptyLimitTime();
        TaskLimitTime limitTime = TimerHelper.calculateTaskLimitTime(formUuid, dataUuid, dyformData, limitTimeType,
                limitTime1, limitTime2, limitUnit, ignoreEmptyLimitTime);
        Double lastestInitLimitTime = limitTime.getTaskLimitTime();

        // 1、办理时限为指定日期
        if (limitTime.isDateOfLimitTime()) {
            Date taskInitDueTime = taskTimer.getTaskInitDueTime();
            Date taskDueTime = limitTime.getTaskDueTime();
            if (taskInitDueTime != null && taskDueTime != null
                    && !TimerHelper.convertTime(taskInitDueTime, Integer.valueOf(limitUnit))
                    .equals(TimerHelper.convertTime(taskDueTime, Integer.valueOf(limitUnit)))) {
                // 变更时限
                doChangeDueTime(taskDueTime, flowInstance.getUuid(), taskTimer);
            }
        } else {
            // 2、办理时限为指定数据
            // 时限为空时不计时
            if (lastestInitLimitTime == null) {
                return;
            }

            // 初始化时限为空，直接返回
            Double taskInitLimitTime = taskTimer.getTaskInitLimitTime();
            if (taskInitLimitTime == null) {
                return;
            }
            // 时限没有变更，直接返回
            if (StringUtils.equals(ObjectUtils.toString(taskInitLimitTime, StringUtils.EMPTY),
                    ObjectUtils.toString(lastestInitLimitTime, StringUtils.EMPTY))) {
                return;
            }

            // 变更时限
            doChangeLimitTime(lastestInitLimitTime.intValue(), flowInstance.getUuid(), taskTimer);
        }
    }

    /**
     * @param flowDefinition
     * @param taskTimer
     * @return
     */
    private TimerElement getTimerElement(List<TimerElement> timerElements, TaskTimer taskTimer) {
        for (TimerElement timerElement : timerElements) {
            String configTimerName = timerElement.getName();
            if (StringUtils.equals(taskTimer.getId(), timerElement.getTimerId())
                    || StringUtils.equals(taskTimer.getName(), configTimerName)) {
                return timerElement;
            }
        }
        return null;
    }

    private AlarmElement getTimerAlarmElement(TaskInstance taskInstance, TaskData taskData, TaskTimer taskTimer, Map<String, Object> keepData) {
        FlowDelegate flowDelegate = (FlowDelegate) keepData.get("flowDelegate");
        if (flowDelegate == null) {
            if (taskData != null && taskData.getToken() != null) {
                flowDelegate = taskData.getToken().getFlowDelegate();
            } else {
                TaskInstance task = taskInstance;
                if (task == null) {
                    task = taskService.get(taskTimer.getTaskInstUuid());
                }
                flowDelegate = FlowDelegateUtils.getFlowDelegate(task.getFlowDefinition());
            }
            keepData.put("flowDelegate", flowDelegate);
        }
        TimerElement timerElement = flowDelegate.getTimers().stream()
                .filter(timer -> StringUtils.equals(timer.getTimerId(), taskTimer.getId()))
                .findFirst().orElse(null);
        if (timerElement == null) {
            timerElement = flowDelegate.getTimers().stream()
                    .filter(timer -> StringUtils.equals(timer.getName(), taskTimer.getName()))
                    .findFirst().orElse(null);
        }
        if (timerElement == null || CollectionUtils.isEmpty(timerElement.getAlarmElements())) {
            return null;
        }

        List<AlarmElement> alarmElements = timerElement.getAlarmElements();
        if (StringUtils.equals(timerElement.getName(), taskTimer.getName())) {
            return alarmElements.get(0);
        } else {
            for (int index = 1; index < alarmElements.size(); index++) {
                if (StringUtils.equals(timerElement.getName() + "[预警提醒-" + (index + 1) + "]", taskTimer.getName())) {
                    return alarmElements.get(index);
                }
            }
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.TimerExecutor#isTaskInstanceInTimer(java.lang.String)
     */
    @Override
    public boolean isTaskInstanceInTimer(String taskInstUuid) {
        return taskTimerService.countByTaskInstUuid(taskInstUuid) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.TimerExecutor#hasTimerConfiguration(java.lang.String)
     */
    @Override
    public boolean hasTimerConfiguration(String flowInstUuid) {
        FlowInstance flowInstance = flowInstanceService.get(flowInstUuid);
        FlowDelegate flowDelegate = FlowDelegateUtils.getFlowDelegate(flowInstance.getFlowDefinition());
        return CollectionUtils.isNotEmpty(flowDelegate.getTimers());
    }

    @Override
    public void stop(FlowInstance flowInstance, String timerName) {
        String flowInstUuid = flowInstance.getUuid();
        List<TaskTimer> taskTimers = taskTimerService.getByFlowInstUuid(flowInstUuid);
        for (TaskTimer taskTimer : taskTimers) {
            String taskInstUuid = taskTimer.getTaskInstUuid();
            if (StringUtils.isBlank(taskInstUuid) || TaskTimerStatus.STOP.equals(taskTimer.getStatus())
                    || (timerName != null && !taskTimer.getName().equals(timerName))) {
                continue;
            }
            TaskInstance taskInstance = taskService.getTask(taskInstUuid);
            // 设置任务数据
            TaskData taskData = new TaskData();
            stopTimer(taskInstance, flowInstance, taskData, taskTimer);
        }
    }

}
