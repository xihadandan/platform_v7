/*
 * @(#)2021年6月8日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.element.TimerElement;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.entity.TaskTimerLog;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.bpm.engine.service.TaskTimerLogService;
import com.wellsoft.pt.bpm.engine.service.TaskTimerService;
import com.wellsoft.pt.bpm.engine.timer.listener.TaskTimerListener;
import com.wellsoft.pt.jpa.comparator.IdEntityComparators;
import com.wellsoft.pt.timer.entity.TsTimerConfigEntity;
import com.wellsoft.pt.timer.entity.TsTimerEntity;
import com.wellsoft.pt.timer.entity.TsTimerLogEntity;
import com.wellsoft.pt.timer.enums.EnumTimingMode;
import com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService;
import com.wellsoft.pt.timer.service.TsTimerConfigService;
import com.wellsoft.pt.timer.service.TsTimerLogService;
import com.wellsoft.pt.timer.service.TsTimerService;
import com.wellsoft.pt.timer.support.WorkTimePeriod;
import com.wellsoft.pt.workflow.service.FlowTaskTimerUpgradeService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
 * 2021年6月8日.1	zhulh		2021年6月8日		Create
 * </pre>
 * @date 2021年6月8日
 */
@Service
public class FlowTaskTimerUpgradeServiceImpl implements FlowTaskTimerUpgradeService {

    @Autowired
    private FlowDefinitionService flowDefinitionService;

    @Autowired
    private TaskTimerService taskTimerService;

    @Autowired
    private TaskTimerLogService taskTimerLogServic;

    @Autowired
    private TsTimerConfigService timerConfigService;

    @Autowired
    private TsTimerService timerService;

    @Autowired
    private TsTimerLogService timerLogService;

    @Autowired
    private TsWorkTimePlanFacadeService workTimePlanFacadeService;

    public static int calculateRemainingTimeLimit(Date fromTime, Date toTime, EnumTimingMode enumTimingMode,
                                                  String workTimePlanUuid) {
        int remainingTime = 0;
        if (toTime == null) {
            return 0;
        }

        Date workDate = convertTime(fromTime, enumTimingMode);
        Date deadlineTime = convertTime(toTime, enumTimingMode);
        if (isAfterDueTime(workDate, deadlineTime, enumTimingMode)) {
            return -1;
        }

        TsWorkTimePlanFacadeService workTimePlanFacadeService = ApplicationContextHolder
                .getBean(TsWorkTimePlanFacadeService.class);
        // BasicDataApiFacade basicDataApiFacade = ApplicationContextHolder.getBean(BasicDataApiFacade.class);
        switch (enumTimingMode) {
            case WORKING_DAY:
                // 工作日(工作时间)
                //			WorkPeriod workDayPeriod = basicDataApiFacade.getWorkPeriod(workDate, deadlineTime);
                //			remainingTime = Double.valueOf(workDayPeriod.getWorkDay()).intValue();
                WorkTimePeriod workDayPeriod = workTimePlanFacadeService.getWorkTimePeriod(workTimePlanUuid, workDate,
                        deadlineTime);
                remainingTime = workDayPeriod.getEffectiveWorkMinute();
                break;
            case WORKING_HOUR:
                // 工作小时(工作时间)
                //			WorkPeriod workHourPeriod = basicDataApiFacade.getWorkPeriod(workDate, deadlineTime);
                //			remainingTime = Double.valueOf(workHourPeriod.getWorkHour()).intValue();
                WorkTimePeriod workHourPeriod = workTimePlanFacadeService.getWorkTimePeriod(workTimePlanUuid, workDate,
                        deadlineTime);
                remainingTime = workHourPeriod.getEffectiveWorkMinute();
                break;
            case WORKING_MINUTE:
                // 工作分钟(工作时间)
                //			WorkPeriod workMinutePeriod = basicDataApiFacade.getWorkPeriod(workDate, deadlineTime);
                //			remainingTime = Double.valueOf(workMinutePeriod.getWorkMinute()).intValue();
                WorkTimePeriod workMinutePeriod = workTimePlanFacadeService.getWorkTimePeriod(workTimePlanUuid, workDate,
                        deadlineTime);
                remainingTime = workMinutePeriod.getEffectiveWorkMinute();
                break;
            case WORKING_DAY_24:
                // 工作日(24小时制)
                // remainingTime = WorkHourUtils.getWorkMinuteBy24Rule(workDate, deadlineTime);
                WorkTimePeriod workDay24Period = workTimePlanFacadeService.getWorkTimePeriod(workTimePlanUuid, workDate,
                        deadlineTime);
                remainingTime = workDay24Period.getEffectiveWorkMinuteBy24Rule();
                break;
            case WORKING_HOUR_24:
                // 工作小时(24小时制)
                // remainingTime = WorkHourUtils.getWorkMinuteBy24Rule(workDate, deadlineTime);
                WorkTimePeriod workHour24Period = workTimePlanFacadeService.getWorkTimePeriod(workTimePlanUuid, workDate,
                        deadlineTime);
                remainingTime = workHour24Period.getEffectiveWorkMinuteBy24Rule();
                break;
            case WORKING_MINUTE_24:
                // 工作分钟(24小时制)
                // remainingTime = WorkHourUtils.getWorkMinuteBy24Rule(workDate, deadlineTime);
                WorkTimePeriod workMinute24Period = workTimePlanFacadeService.getWorkTimePeriod(workTimePlanUuid, workDate,
                        deadlineTime);
                remainingTime = workMinute24Period.getEffectiveWorkMinuteBy24Rule();
                break;
            case DAY:
                // 天
                // remainingTime = DateUtils.millisecondToDay(deadlineTime.getTime() - workDate.getTime()).intValue();
                remainingTime = DateUtils.millisecondToMinute(deadlineTime.getTime() - workDate.getTime()).intValue();
                break;
            case HOUR:
                // 小时
                // remainingTime = DateUtils.millisecondToHour(deadlineTime.getTime() - workDate.getTime()).intValue();
                remainingTime = DateUtils.millisecondToMinute(deadlineTime.getTime() - workDate.getTime()).intValue();
                break;
            case MINUTE:
                // 分钟
                remainingTime = DateUtils.millisecondToMinute(deadlineTime.getTime() - workDate.getTime()).intValue();
                break;
            default:
                break;
        }
        return remainingTime;
    }

    private static Date convertTime(Date time, EnumTimingMode enumTimingMode) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        // 统一到分钟
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * @param workDate
     * @param dueTime
     * @param unit
     * @return
     */
    public static boolean isAfterDueTime(Date workDate, Date dueTime, EnumTimingMode enumTimingMode) {
        boolean isAfter = false;
        // amount小于等于零，返回原来的到期时间
        switch (enumTimingMode) {
            case WORKING_DAY:
                // 工作日(工作时间)
            case WORKING_DAY_24:
                // 工作日(24小时制)
            case DAY:
                // 天
                Date workingDate1 = convertTime(workDate, enumTimingMode);
                Date workingDate2 = convertTime(dueTime, enumTimingMode);
                isAfter = workingDate1.after(workingDate2);
                break;
            case WORKING_HOUR:
                // 工作小时(工作时间)
            case WORKING_HOUR_24:
                // 工作小时(24小时制)
            case HOUR:
                // 小时
                Date workingHour1 = convertTime(workDate, enumTimingMode);
                Date workingHour2 = convertTime(dueTime, enumTimingMode);
                isAfter = workingHour1.after(workingHour2);
                break;
            case WORKING_MINUTE:
                // 工作分钟(工作时间)
            case WORKING_MINUTE_24:
                // 工作分钟(24小时制)
            case MINUTE:
                // 分
                Date workingMinute1 = convertTime(workDate, enumTimingMode);
                Date workingMinute2 = convertTime(dueTime, enumTimingMode);
                isAfter = workingMinute1.after(workingMinute2);
                break;
            default:
                break;
        }
        return isAfter;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.service.FlowTaskTimerUpgradeService#upgrade2v6_2_7()
     */
    @Override
    public List<String> upgrade2v6_2_7(String flowDefUuid) {
        List<String> flowDefUuids = listUpgradeFlowDefUuids(flowDefUuid);
        List<String> updateTaskTimerUuids = Lists.newArrayList();
        for (String uuid : flowDefUuids) {
            List<TaskTimer> taskTimers = listUpgradeTaskTimerByFlowDefUuid(uuid);
            // 获取更新的计时器UUID
            for (TaskTimer taskTimer : taskTimers) {
                if (StringUtils.isBlank(taskTimer.getTimerUuid())) {
                    updateTaskTimerUuids.add(taskTimer.getUuid());
                }
            }
            FlowDelegate flowDelegate = flowDefinitionService.getFlowDelegate(uuid);
            upgradeTaskTimers(taskTimers, flowDelegate);
        }
        return updateTaskTimerUuids;
    }

    /**
     * 获取需升级的流程定义UUID列表
     *
     * @param flowDefUuid
     * @return
     */
    private List<String> listUpgradeFlowDefUuids(String flowDefUuid) {
        if (StringUtils.isNotBlank(flowDefUuid)) {
            return Lists.newArrayList(flowDefUuid);
        }
        List<String> uuids = Lists.newArrayList();
        // 获取需升级的流程定义UUID
        String hql = "select t1.uuid from FlowDefinition t1 where t1.flowSchemaUuid in( select t2.uuid from FlowSchema t2 where t2.content like '%timerConfigUuid%')";
        List<?> flowDefinitionUuids = flowDefinitionService.listByHQL(hql, null);
        for (int index = 0; index < flowDefinitionUuids.size(); index++) {
            if (flowDefinitionUuids.get(index) instanceof FlowDefinition) {
                uuids.add(((FlowDefinition) flowDefinitionUuids.get(index)).getUuid());
            } else {
                uuids.add((String) flowDefinitionUuids.get(index));
            }
        }
        return uuids;
    }

    /**
     * @param flowDefUuid
     * @return
     */
    private List<TaskTimer> listUpgradeTaskTimerByFlowDefUuid(String flowDefUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowDefUuid", flowDefUuid);
        String hql = "from TaskTimer t1 where t1.flowInstUuid in(select t2.uuid from FlowInstance t2 where t2.flowDefinition.uuid = :flowDefUuid)";
        return taskTimerService.listByHQL(hql, values);
    }

    /**
     * @param taskTimers
     * @param flowDelegate
     */
    private void upgradeTaskTimers(List<TaskTimer> taskTimers, FlowDelegate flowDelegate) {
        for (TaskTimer taskTimer : taskTimers) {
            upgradeTaskTimer(taskTimer, flowDelegate);
        }
    }

    /**
     * @param taskTimer
     * @param flowDelegate
     */
    private void upgradeTaskTimer(TaskTimer taskTimer, FlowDelegate flowDelegate) {
        TimerElement timerElement = getTimerElement(taskTimer, flowDelegate);
        if (StringUtils.isNotBlank(taskTimer.getTimerUuid())) {
            return;
        }
        if (timerElement == null) {
            return;
        }
        Integer status = taskTimer.getStatus();
        switch (status) {
            case 0:
                // 准备就绪
                upgradeReadyTaskTimer(taskTimer, timerElement);
                break;
            case 1:
                // 已启动
                upgradeStartTaskTimer(taskTimer, timerElement);
                break;
            case 2:
                // 暂停中
                upgradePauseTaskTimer(taskTimer, timerElement);
                break;
            case 3:
                // 已结束
                upgradeStopTaskTimer(taskTimer, timerElement);
                break;
            default:
                break;
        }
    }

    /**
     * @param taskTimer
     * @param flowDelegate
     * @return
     */
    private TimerElement getTimerElement(TaskTimer taskTimer, FlowDelegate flowDelegate) {
        String timerId = taskTimer.getId();
        List<TimerElement> timerElements = flowDelegate.getFlow().getTimers();
        for (TimerElement timerElement : timerElements) {
            if (StringUtils.equals(timerId, timerElement.getTimerId())) {
                return timerElement;
            }
        }
        return null;
    }

    /**
     * @param taskTimer
     * @param flowDelegate
     */
    private void upgradeReadyTaskTimer(TaskTimer taskTimer, TimerElement timerElement) {
    }

    /**
     * @param taskTimer
     * @param flowDelegate
     */
    private void upgradeStartTaskTimer(TaskTimer taskTimer, TimerElement timerElement) {
        // 设置计时器配置及工作方案
        String timerConfigUuid = timerElement.getTimerConfigUuid();
        String workTimePlanUuid = timerElement.getWorkTimePlanUuid();
        String workTimePlanId = timerElement.getWorkTimePlanId();
        workTimePlanUuid = workTimePlanFacadeService.getActiveWorkTimePlanUuidById(workTimePlanId, workTimePlanUuid);
        taskTimer.setTimerConfigUuid(timerConfigUuid);
        taskTimer.setWorkTimePlanUuid(workTimePlanUuid);

        TsTimerConfigEntity timerConfigEntity = timerConfigService.getOne(timerConfigUuid);
        // 创建计时器
        TsTimerEntity timerEntity = createTimer(taskTimer, timerConfigEntity);
        // 创建计时器日志
        createTimerLogs(timerEntity, taskTimer);
        // 更新流程计时器
        taskTimer.setTimerUuid(timerEntity.getUuid());
        taskTimerService.save(taskTimer);
        // 创建计时器日志
        // 记录流程计时器日志
        taskTimerLogServic.log(taskTimer, Calendar.getInstance().getTime(), TaskTimerLog.TYPE_INFO, "升级计时器");
    }

    /**
     * @param taskTimer
     * @param timerConfigEntity
     * @return
     */
    private TsTimerEntity createTimer(TaskTimer taskTimer, TsTimerConfigEntity timerConfigEntity) {
        TsTimerEntity timerEntity = new TsTimerEntity();
        // 计时配置UUID
        timerEntity.setConfigUuid(timerConfigEntity.getUuid());
        // 工作时间方案UUID
        timerEntity.setWorkTimePlanUuid(taskTimer.getWorkTimePlanUuid());
        // 计算后初始化的办理时限数字
        timerEntity.setInitTimeLimit(Double.valueOf(taskTimer.getTaskInitLimitTime()));
        // 计算后初始化的办理时限日期，当时限类型为指定日期或表单日期字段时有效
        timerEntity.setInitDueTime(taskTimer.getTaskInitDueTime());
        // 开始计时时间点
        timerEntity.setStartTime(taskTimer.getStartTime());
        // 最新开始时间
        timerEntity.setLastStartTime(taskTimer.getLastStartTime());
        // 计时方式
        timerEntity.setTimingMode(timerConfigEntity.getTimingMode());
        // 时限类型
        timerEntity.setTimeLimitType(timerConfigEntity.getTimeLimitType());
        // 最新的办理时限，按倒计时方式重新计算
        int remainingTimeLimit = calculateRemainingTimeLimit(taskTimer.getModifyTime(), taskTimer.getTaskDueTime(),
                getRuntimeTimingMode(EnumTimingMode.getByValue(taskTimer.getLimitUnit())),
                taskTimer.getWorkTimePlanUuid());
        timerEntity.setTimeLimit(Double.valueOf(remainingTimeLimit));
        // 到期时间
        timerEntity.setDueTime(taskTimer.getTaskDueTime());
        // 计时器是运行状态(0未启动、1已启动、2暂停、3结束)
        timerEntity.setStatus(taskTimer.getStatus());
        // 计时状态(0正常、1预警、2到期、3逾期)
        timerEntity.setTimingState(taskTimer.getTimingState());
        // 计时监听器
        if (StringUtils.isNotBlank(timerConfigEntity.getListener())) {
            timerEntity.setListener(timerConfigEntity.getListener());
        } else {
            timerEntity.setListener(TaskTimerListener.LISTENER_NAME);
        }
        timerService.save(timerEntity);
        return timerEntity;
    }

    /**
     * @param timerEntity
     * @param taskTimer
     */
    private void createTimerLogs(TsTimerEntity timerEntity, TaskTimer taskTimer) {
        TaskTimerLog entity = new TaskTimerLog();
        entity.setTaskTimerUuid(taskTimer.getUuid());
        List<TaskTimerLog> logs = taskTimerLogServic.listByEntity(entity);
        Collections.sort(logs, IdEntityComparators.CREATE_TIME_ASC);
        for (TaskTimerLog taskTimerLog : logs) {
            TsTimerLogEntity timerLogEntity = new TsTimerLogEntity();
            timerLogEntity.setTimerUuid(timerEntity.getUuid());
            timerLogEntity.setType(taskTimerLog.getType());
            timerLogEntity.setLogTime(taskTimerLog.getLogTime());
            timerLogEntity.setTimeLimit(taskTimer.getTaskLimitTime());
            timerLogEntity.setDueTime(taskTimer.getTaskDueTime());
            timerLogEntity.setRemark(taskTimerLog.getRemark());
            timerLogService.save(timerLogEntity);
        }
    }

    /**
     * @param taskTimer
     * @param flowDelegate
     */
    private void upgradePauseTaskTimer(TaskTimer taskTimer, TimerElement timerElement) {
        // 设置计时器配置及工作方案
        String timerConfigUuid = timerElement.getTimerConfigUuid();
        String workTimePlanUuid = timerElement.getWorkTimePlanUuid();
        String workTimePlanId = timerElement.getWorkTimePlanId();
        workTimePlanUuid = workTimePlanFacadeService.getActiveWorkTimePlanUuidById(workTimePlanId, workTimePlanUuid);
        taskTimer.setTimerConfigUuid(timerConfigUuid);
        taskTimer.setWorkTimePlanUuid(workTimePlanUuid);

        TsTimerConfigEntity timerConfigEntity = timerConfigService.getOne(timerConfigUuid);
        // 创建计时器
        TsTimerEntity timerEntity = createTimer(taskTimer, timerConfigEntity);
        // 创建计时器日志
        createTimerLogs(timerEntity, taskTimer);
        // 更新流程计时器
        taskTimer.setTimerUuid(timerEntity.getUuid());
        // 更新剩余时限
        taskTimer.setTaskLimitTime(timerEntity.getTimeLimit());
        taskTimerService.save(taskTimer);
        // 创建计时器日志
        // 记录流程计时器日志
        taskTimerLogServic.log(taskTimer, Calendar.getInstance().getTime(), TaskTimerLog.TYPE_INFO, "升级计时器");
    }

    /**
     * @param taskTimer
     * @param flowDelegate
     */
    private void upgradeStopTaskTimer(TaskTimer taskTimer, TimerElement timerElement) {
        upgradePauseTaskTimer(taskTimer, timerElement);
    }

    /**
     * @param timingMode
     * @return
     */
    private EnumTimingMode getRuntimeTimingMode(EnumTimingMode enumTimingMode) {
        EnumTimingMode runtimeTimingMode = enumTimingMode;
        switch (enumTimingMode) {
            case WORKING_DAY:
                // 工作日(工作时间)
            case WORKING_HOUR:
                // 工作小时(工作时间)
            case WORKING_MINUTE:
                // 工作分钟(工作时间)
                runtimeTimingMode = EnumTimingMode.WORKING_MINUTE;
                break;
            case WORKING_DAY_24:
                // 工作日(24小时制不含工作日)
            case WORKING_HOUR_24:
                // 工作小时(24小时制)
            case WORKING_MINUTE_24:
                // 工作分钟(24小时制)
                runtimeTimingMode = EnumTimingMode.WORKING_MINUTE_24;
                break;
            case DAY:
                // 天
            case HOUR:
                // 小时
            case MINUTE:
                // 分钟
                runtimeTimingMode = EnumTimingMode.MINUTE;
                break;
            default:
                break;
        }
        return runtimeTimingMode;
    }

}
