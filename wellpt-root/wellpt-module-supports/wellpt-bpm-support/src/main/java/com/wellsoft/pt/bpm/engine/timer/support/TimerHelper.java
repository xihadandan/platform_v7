/*
 * @(#)2018年11月8日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.support;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.basicdata.workhour.support.WorkHourUtils;
import com.wellsoft.pt.basicdata.workhour.support.WorkPeriod;
import com.wellsoft.pt.basicdata.workhour.support.WorkingHour;
import com.wellsoft.pt.bpm.engine.element.TimerElement;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.entity.TaskTimerLog;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.service.TaskTimerLogService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.timer.enums.EnumTimingMode;
import com.wellsoft.pt.timer.enums.EnumTimingModeUnit;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
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
 * 2018年11月8日.1	zhulh		2018年11月8日		Create
 * </pre>
 * @date 2018年11月8日
 */
public class TimerHelper {

    private static Logger LOG = LoggerFactory.getLogger(TimerHelper.class);

    /**
     * 计算出定时任务的办理时限
     *
     * @return
     */
    public static Date calculateDueTime(int amount, int unit, Date defaultDueTime) {
        Calendar calendar = Calendar.getInstance();
        Date fromTime = calendar.getTime();
        return calculateDueTime(fromTime, amount, unit, defaultDueTime);
    }

    /**
     * 计算出定时任务的办理时限
     *
     * @return
     */
    public static Date calculateDueTime(Date fromTime, int amount, int unit, Date defaultDueTime) {
        Date dueTime = null;
        // amount小于等于零，返回原来的过期时间
        if (amount < 0) {
            dueTime = defaultDueTime;
        } else if (amount == 0) {
            // amount等于零，返回当前时间
            Date workDate = convertTime(fromTime, Integer.valueOf(unit));
            dueTime = workDate;
        } else {
            dueTime = calculateTime(fromTime, amount, Integer.valueOf(unit));
        }
        return dueTime;
    }

    /**
     * @param fromTime
     * @param amount
     * @param unit
     * @return
     */
    public static Date calculateTime(Date fromTime, int amount, int unit) {
        BasicDataApiFacade basicDataApiFacade = ApplicationContextHolder.getBean(BasicDataApiFacade.class);
        Calendar calendar = Calendar.getInstance();
        Date workDate = convertTime(fromTime, unit);
        Date dueTime = null;
        switch (unit) {
            case TimerUnit.DATE_WORKING_DAY:
            case TimerUnit.WORKING_DAY:
                // 工作日(工作时间)
                dueTime = basicDataApiFacade.getWorkDate(workDate, Double.valueOf(amount), WorkUnit.WorkingDay);
                break;
            case TimerUnit.DATE_WORKING_HOUR:
            case TimerUnit.WORKING_HOUR:
                // 工作小时(工作时间)
                dueTime = basicDataApiFacade.getWorkDate(workDate, Double.valueOf(amount), WorkUnit.WorkingHour);
                break;
            case TimerUnit.DATE_WORKING_MINUTE:
            case TimerUnit.WORKING_MINUTE:
                // 工作分钟(工作时间)
                dueTime = basicDataApiFacade.getWorkDate(workDate, Double.valueOf(amount), WorkUnit.WorkingMinute);
                break;
            case TimerUnit.WORKING_DAY_24:
                // 工作日(24小时制)
                dueTime = basicDataApiFacade.getWorkDate(workDate, Double.valueOf(amount), WorkUnit.WorkingDay);
                break;
            case TimerUnit.WORKING_HOUR_24:
                // 工作小时(24小时制)
                dueTime = basicDataApiFacade.getWorkDate(workDate, Double.valueOf(amount), WorkUnit.WorkingHour);
                break;
            case TimerUnit.WORKING_MINUTE_24:
                // 工作分钟(24小时制)
                dueTime = convertTime(WorkHourUtils.getWorkDateAfterMinuteBy24Rule(workDate, amount), unit);
                break;
            case TimerUnit.DAY:
                // 天
                calendar.setTime(workDate);
                calendar.add(Calendar.DAY_OF_MONTH, amount);
                dueTime = calendar.getTime();
                break;
            case TimerUnit.HOUR:
                // 小时
                calendar.setTime(workDate);
                calendar.add(Calendar.HOUR_OF_DAY, amount);
                dueTime = calendar.getTime();
                break;
            case TimerUnit.MINUTE:
                // 分钟
                calendar.setTime(workDate);
                calendar.add(Calendar.MINUTE, amount);
                dueTime = calendar.getTime();
                break;
            case TimerUnit.DATE:
                // 日期(2000-01-01)
                calendar.setTime(workDate);
                calendar.add(Calendar.DAY_OF_MONTH, amount);
                dueTime = calendar.getTime();
                break;
            case TimerUnit.DATE_HOUR:
                // 日期到时(2000-01-01 12)
                calendar.setTime(workDate);
                calendar.add(Calendar.HOUR_OF_DAY, amount);
                dueTime = calendar.getTime();
                break;
            case TimerUnit.DATE_MINUTE:
                // 日期到分(2000-01-01 12:00)
                calendar.setTime(workDate);
                calendar.add(Calendar.MINUTE, amount);
                dueTime = calendar.getTime();
                break;
            case TimerUnit.DATETIME_START:
                // 日期时间（开始）
                calendar.setTimeInMillis(System.currentTimeMillis() + amount + 3000);
                dueTime = calendar.getTime();
                break;
            case TimerUnit.DATETIME_END:
                // 日期时间（结束）
                calendar.setTimeInMillis(System.currentTimeMillis() + amount - 3000);
                dueTime = calendar.getTime();
                break;
            default:
                dueTime = calendar.getTime();
                break;
        }
        return dueTime;
    }

    /**
     * @param dueTime
     * @param unit
     * @return
     */
    public static Date getOverDueTime(Date deadlineTime, int unit) {
        BasicDataApiFacade basicDataApiFacade = ApplicationContextHolder.getBean(BasicDataApiFacade.class);
        Date dueTime = convertTime(deadlineTime, unit);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dueTime);
        switch (unit) {
            case TimerUnit.WORKING_DAY:
                // 工作日(工作时间)
                if (basicDataApiFacade.isWorkDay(dueTime)) {
                    WorkingHour workingHourOfDate = basicDataApiFacade.getWorkingHour(dueTime);
                    calendar.setTime(workingHourOfDate.getToTime2());
                } else {
                    calendar.setTime(deadlineTime);
                }
                break;
            case TimerUnit.WORKING_HOUR:
                // 工作小时(工作时间)
                if (basicDataApiFacade.isWorkDay(dueTime)) {
                    WorkingHour workingHourOfHour = basicDataApiFacade.getWorkingHour(dueTime);
                    calendar.add(Calendar.HOUR_OF_DAY, 1);
                    Date tmpTime = calendar.getTime();
                    // 上午下班时间，下午上班时间之间，设置为上午下班时间
                    if (tmpTime.after(workingHourOfHour.getToTime1()) && tmpTime.before(workingHourOfHour.getFromTime1())) {
                        calendar.setTime(workingHourOfHour.getToTime1());
                    } else if (tmpTime.after(workingHourOfHour.getToTime2())) {
                        // 下午下班时间之后，设置为下午下班时间
                        calendar.setTime(workingHourOfHour.getToTime2());
                    }
                } else {
                    calendar.setTime(deadlineTime);
                }
                break;
            case TimerUnit.WORKING_MINUTE:
                // 工作分钟(工作时间)
                calendar.add(Calendar.MINUTE, 1);
                break;
            case TimerUnit.WORKING_DAY_24:
                // 工作日(24小时制)
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case TimerUnit.WORKING_HOUR_24:
                // 工作小时(24小时制)
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                break;
            case TimerUnit.WORKING_MINUTE_24:
                // 工作分钟(24小时制)
                calendar.add(Calendar.MINUTE, 1);
                break;
            case TimerUnit.DAY:
                // 天
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case TimerUnit.HOUR:
                // 小时
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                break;
            case TimerUnit.MINUTE:
                // 分钟
                calendar.add(Calendar.MINUTE, 1);
                break;
            case TimerUnit.DATE:
                // 日期(2000-01-01)
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case TimerUnit.DATE_HOUR:
                // 日期到时(2000-01-01 12)
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                break;
            case TimerUnit.DATE_MINUTE:
                // 日期到分(2000-01-01 12:00)
                calendar.add(Calendar.MINUTE, 1);
                break;
            case TimerUnit.DATETIME_START:
                // 日期时间（开始）
                calendar.setTime(dueTime);
                break;
            case TimerUnit.DATETIME_END:
                // 日期时间（结束）
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                break;
            default:
                break;
        }
        return calendar.getTime();
    }

    /**
     * 计算出定时任务的提醒时间
     *
     * @param fromTime
     * @param amount
     * @param unit
     * @return
     */
    public static Date calculateAlarmTime(Date fromTime, int amount, int unit) {
        return calculateTime(fromTime, amount, unit);
    }

    /**
     * 计算剩余的办理时限
     *
     * @param taskTimer
     */
    public static int calculateRemainingTaskLimitTime(Date toTime, int unit) {
        int remainingTime = 0;
        if (toTime == null) {
            return 0;
        }

        Calendar calendar = Calendar.getInstance();
        Date workDate = convertTime(calendar.getTime(), unit);
        Date deadlineTime = convertTime(toTime, unit);
        if (isAfterDueTime(workDate, deadlineTime, unit)) {
            return -1;
        }

        BasicDataApiFacade basicDataApiFacade = ApplicationContextHolder.getBean(BasicDataApiFacade.class);
        switch (unit) {
            case TimerUnit.WORKING_DAY:
                // 工作日(工作时间)
                WorkPeriod workDayPeriod = basicDataApiFacade.getWorkPeriod(workDate, deadlineTime);
                remainingTime = Double.valueOf(workDayPeriod.getWorkDay()).intValue();
                break;
            case TimerUnit.WORKING_HOUR:
                // 工作小时(工作时间)
                WorkPeriod workHourPeriod = basicDataApiFacade.getWorkPeriod(workDate, deadlineTime);
                remainingTime = Double.valueOf(workHourPeriod.getWorkHour()).intValue();
                break;
            case TimerUnit.WORKING_MINUTE:
                // 工作分钟(工作时间)
                WorkPeriod workMinutePeriod = basicDataApiFacade.getWorkPeriod(workDate, deadlineTime);
                remainingTime = Double.valueOf(workMinutePeriod.getWorkMinute()).intValue();
                break;
            case TimerUnit.WORKING_DAY_24:
                // 工作日(24小时制)
                WorkPeriod workDay24Period = basicDataApiFacade.getWorkPeriod(workDate, deadlineTime);
                remainingTime = Double.valueOf(workDay24Period.getWorkDay()).intValue();
                break;
            case TimerUnit.WORKING_HOUR_24:
                // 工作小时(24小时制)
                WorkPeriod workHour24Period = basicDataApiFacade.getWorkPeriod(workDate, deadlineTime);
                remainingTime = Double.valueOf(workHour24Period.getWorkHour()).intValue();
                break;
            case TimerUnit.WORKING_MINUTE_24:
                // 工作分钟(24小时制)
                remainingTime = WorkHourUtils.getWorkMinuteBy24Rule(workDate, deadlineTime);
                break;
            case TimerUnit.DAY:
                // 天
                remainingTime = DateUtils.millisecondToDay(deadlineTime.getTime() - workDate.getTime()).intValue();
                break;
            case TimerUnit.HOUR:
                // 小时
                remainingTime = DateUtils.millisecondToHour(deadlineTime.getTime() - workDate.getTime()).intValue();
                break;
            case TimerUnit.MINUTE:
                // 分钟
                remainingTime = DateUtils.millisecondToMinute(deadlineTime.getTime() - workDate.getTime()).intValue();
                break;
            case TimerUnit.DATE:
                // 日期(2000-01-01)
                remainingTime = DateUtils.millisecondToDay(deadlineTime.getTime() - workDate.getTime()).intValue();
                break;
            case TimerUnit.DATE_HOUR:
                // 日期到时(2000-01-01 12)
                remainingTime = DateUtils.millisecondToHour(deadlineTime.getTime() - workDate.getTime()).intValue();
                break;
            case TimerUnit.DATE_MINUTE:
                // 日期到分(2000-01-01 12:00)
                remainingTime = DateUtils.millisecondToMinute(deadlineTime.getTime() - workDate.getTime()).intValue();
                break;
            case TimerUnit.DATETIME_START:
                // 日期时间(开始)
                remainingTime = DateUtils.millisecondToDay(deadlineTime.getTime() - workDate.getTime()).intValue();
                break;
            case TimerUnit.DATETIME_END:
                // 日期时间(结束)
                remainingTime = DateUtils.millisecondToDay(deadlineTime.getTime() - workDate.getTime()).intValue();
                break;
            default:
                break;
        }
        return remainingTime;
    }

    /**
     * 判断环节ID是否在计时器的计时环节
     *
     * @param taskId
     * @param taskTimer
     */
    public static boolean isInTimingTask(String taskId, TaskTimer taskTimer) {
        // 启动相关的计时器
        List<String> taskIds = Arrays.asList(StringUtils.split(taskTimer.getTaskIds(), Separator.SEMICOLON.getValue()));
        return taskIds.contains(taskId);
    }

    /**
     * 判断环节ID是否在计时器的计时环节
     *
     * @param taskId
     * @param taskTimer
     */
    public static boolean isInTimingTask(String taskId, TimerElement timerElement) {
        // 启动相关的计时器
        List<String> taskIds = Arrays.asList(StringUtils.split(timerElement.getTaskIdsAsString(),
                Separator.SEMICOLON.getValue()));
        return taskIds.contains(taskId);
    }

    /**
     * 判断计时器是否有效，如果办理时限无效则不启用
     * 计时系统的办理时限要支持可输入数字和选择字段名两种方式；
     * 当办理环节设置为空，则这个计时系统无效不启用
     * 当输入数字值空或者为零，则这个计时系统无效不启用
     * 当指定字段名的值空或者为零，则这个计时系统无效不启用
     *
     * @return
     */
    public static boolean isTimerValid(TaskTimer taskTimer) {
        TaskTimerLogService taskTimerLogService = ApplicationContextHolder.getBean(TaskTimerLogService.class);
        Date logTime = Calendar.getInstance().getTime();
        // 1、当办理环节设置为空，则这个计时系统无效不启用
        if (StringUtils.isBlank(taskTimer.getTaskIds())) {
            taskTimerLogService.log(taskTimer, logTime, TaskTimerLog.TYPE_ERROR, "计时环节设置为空！");
            return false;
        }

        // 办理时限
        String limitTimeType = taskTimer.getLimitTimeType();
        // 2、当输入数字值空或者为零，则这个计时系统无效不启用
        if (TaskTimer.LIMIT_TIME_TYPE_NUMBER.equals(limitTimeType)) {
            String limitTime1 = taskTimer.getLimitTime1();
            if (StringUtils.isBlank(limitTime1)) {
                taskTimerLogService.log(taskTimer, logTime, TaskTimerLog.TYPE_ERROR, "指定数字的办理时限为空！");
                return false;
            }
            try {
                Double dueTimeValue = Double.valueOf(limitTime1);
                if (dueTimeValue < 0) {
                    taskTimerLogService.log(taskTimer, logTime, TaskTimerLog.TYPE_ERROR, "指定数字的小于0！");
                    return false;
                }
            } catch (Exception e) {
                String errorMsg = "指定数字的办理时限[" + limitTime1 + "]解析错误！";
                taskTimerLogService.log(taskTimer, logTime, TaskTimerLog.TYPE_ERROR, errorMsg);
                LOG.error(ExceptionUtils.getStackTrace(e));
                return false;
            }
        } else if (TaskTimer.LIMIT_TIME_TYPE_FORM_FIELD.equals(limitTimeType)) {
            String limitTime2 = taskTimer.getLimitTime2();
            // 3、当指定字段名的值空或者为零，则这个计时系统无效不启用
            if (StringUtils.isBlank(limitTime2)) {
                return false;
            }
        } else if (TaskTimer.LIMIT_TIME_TYPE_FORM_DATE.equals(limitTimeType)) {
            String limitTime2 = taskTimer.getLimitTime2();
            // 4、当指定字段名的值空或者为零，则这个计时系统无效不启用
            if (StringUtils.isBlank(limitTime2)) {
                return false;
            }
        } else if (TaskTimer.LIMIT_TIME_TYPE_FIXED_DATE.equals(limitTimeType)) {
            // 5、指定日期
            String limitTime1 = taskTimer.getLimitTime1();
            if (StringUtils.isBlank(limitTime1)) {
                return false;
            }
            try {
                DateUtils.parse(limitTime1);
            } catch (ParseException e) {
                LOG.error(ExceptionUtils.getStackTrace(e));
                return false;
            }
        } else {
            taskTimerLogService.log(taskTimer, logTime, TaskTimerLog.TYPE_ERROR, "无效的办理时限类型[" + limitTimeType + "]！");
            return false;
        }
        return true;
    }

    /**
     * 计算办理时限
     *
     * @param formUuid
     * @param dataUuid
     * @param limitTimeType
     * @param limitTime1
     * @param limitTime2
     * @param limitUnit
     * @param ignoreEmptyLimitTime
     * @return
     */
    public static TaskLimitTime calculateTaskLimitTime(String formUuid, String dataUuid, DyFormData dyFormData,
                                                       String limitTimeType, String limitTime1, String limitTime2, String limitUnit, Boolean ignoreEmptyLimitTime) {
        DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        Double taskLimitTime = null;
        TaskLimitTime limitTime = new TaskLimitTime();
        // 动态时限
        if (TaskTimer.LIMIT_TIME_TYPE_FORM_FIELD.equals(limitTimeType)) {
            DyFormData tmpDyFormData = dyFormData;
            if (tmpDyFormData == null) {
                tmpDyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
            }
            Object fieldValue = tmpDyFormData.getFieldValue(limitTime2);
            if (fieldValue != null && StringUtils.isNotBlank(fieldValue.toString())) {
                try {
                    taskLimitTime = Double.valueOf(StringUtils.trim(fieldValue.toString()));
                } catch (Exception e) {
                    LOG.error(ExceptionUtils.getStackTrace(e));
                    throw new WorkFlowException("请输入有效的办理时限!");
                }
            } else if (Boolean.TRUE.equals(ignoreEmptyLimitTime)) {
                taskLimitTime = null;
            } else {
                throw new WorkFlowException("请输入有效的办理时限!");
            }
        } else if (TaskTimer.LIMIT_TIME_TYPE_FORM_DATE.equals(limitTimeType)) {
            // 动态截止日期
            DyFormData tmpDyFormData = dyFormData;
            if (tmpDyFormData == null) {
                tmpDyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
            }
            String value = null;
            Object fieldValue = tmpDyFormData.getFieldValue(limitTime2);
            // 表单值为日期类转为字符串
            if (fieldValue instanceof Date) {
                value = DateUtils.formatFullDateTime((Date) fieldValue);
            } else {
                value = ObjectUtils.toString(fieldValue, StringUtils.EMPTY).trim();
            }
            if (StringUtils.isNotBlank(value)) {
                try {
                    // taskLimitTime = Double.valueOf(calculateLimitTime(value, limitUnit));
//					if (isDateOfLimitTime(Integer.valueOf(limitUnit))) {
                    limitTime.setDateOfLimitTime(true);
                    Date limitDate = DateUtils.parse(value);
//						if(TimerUnit.DATE_WORKING_DAY==Integer.parseInt(limitUnit)){//工作日
//							WorkingHour workingHour = ApplicationContextHolder.getBean(BasicDataApiFacade.class).getWorkingHour(limitDate);
//							if(workingHour.getIsWorkDay()){
//								limitDate = workingHour.getToTime2();
//							}
//						}
                    limitTime.setTaskDueTime(limitDate);
//					}
                } catch (Exception e) {
                    LOG.error(ExceptionUtils.getStackTrace(e));
                    throw new WorkFlowException("请输入有效的办理时限!");
                }
            } else if (Boolean.TRUE.equals(ignoreEmptyLimitTime)) {
                taskLimitTime = null;
            } else {
                throw new WorkFlowException("请输入有效的办理时限!");
            }
        } else {
            try {
                if (StringUtils.isNotBlank(limitTime1)) {
                    // 指定日期
                    if (TaskTimer.LIMIT_TIME_TYPE_FIXED_DATE.equals(limitTimeType)) {
                        // taskLimitTime = Double.valueOf(calculateLimitTime(limitTime1, limitUnit));
                        limitTime.setDateOfLimitTime(true);
                        limitTime.setTaskDueTime(DateUtils.parse(limitTime1));
                    } else {
                        // 指定数字
                        taskLimitTime = Double.valueOf(limitTime1);
                    }
                } else if (Boolean.TRUE.equals(ignoreEmptyLimitTime)) {
                    taskLimitTime = null;
                }
            } catch (Exception e) {
                LOG.error(ExceptionUtils.getStackTrace(e));
                throw new WorkFlowException("流程计时器办理时限设置出错!");
            }
        }
        limitTime.setTaskLimitTime(taskLimitTime);
        return limitTime;
    }

    private static int calculateLimitTime(String value, String limitUnit) throws ParseException {
        int taskLimitTime = 0;
        Integer localLimitUnit = Integer.valueOf(limitUnit);
        switch (localLimitUnit) {
            case TimerUnit.DATE:
                taskLimitTime = calculateRemainingTaskLimitTime(DateUtils.parse(value), TimerUnit.DATE);
                break;
            case TimerUnit.DATE_HOUR:
                taskLimitTime = calculateRemainingTaskLimitTime(DateUtils.parse(value), TimerUnit.DATE_HOUR);
                break;
            case TimerUnit.DATE_MINUTE:
                taskLimitTime = calculateRemainingTaskLimitTime(DateUtils.parse(value), TimerUnit.DATE_MINUTE);
                break;
            case TimerUnit.DATETIME_START:
                Date limitTimeStart = DateUtils.parse(value, DateUtils.TYPE_START);
                taskLimitTime = Long.valueOf(limitTimeStart.getTime() - System.currentTimeMillis()).intValue();
                break;
            case TimerUnit.DATETIME_END:
                Date limitTimeEnd = DateUtils.parse(value, DateUtils.TYPE_END);
                taskLimitTime = Long.valueOf(limitTimeEnd.getTime() - System.currentTimeMillis()).intValue();
                break;

            case TimerUnit.WORKING_DAY:
            case TimerUnit.WORKING_DAY_24:
            case TimerUnit.DATE_WORKING_DAY:
                taskLimitTime = calculateRemainingTaskLimitTime(DateUtils.parse(value), TimerUnit.WORKING_DAY);
                break;
            case TimerUnit.WORKING_HOUR:
            case TimerUnit.WORKING_HOUR_24:
            case TimerUnit.DATE_WORKING_HOUR:
                taskLimitTime = calculateRemainingTaskLimitTime(DateUtils.parse(value), TimerUnit.WORKING_HOUR);
                break;
            case TimerUnit.WORKING_MINUTE:
            case TimerUnit.WORKING_MINUTE_24:
            case TimerUnit.DATE_WORKING_MINUTE:
                taskLimitTime = calculateRemainingTaskLimitTime(DateUtils.parse(value), TimerUnit.WORKING_MINUTE);
                break;

            default:
                taskLimitTime = Integer.valueOf(value);
                break;
        }
        return taskLimitTime;
    }

    /**
     * @param taskTimer
     * @return
     */
    public static boolean isReady(TaskTimer taskTimer) {
        return TaskTimerStatus.READY.equals(taskTimer.getStatus());
    }

    /**
     * @param taskTimer
     * @return
     */
    public static boolean isStarted(TaskTimer taskTimer) {
        return TaskTimerStatus.STARTED.equals(taskTimer.getStatus());
    }

    /**
     * @param taskTimer
     * @return
     */
    public static boolean isPause(TaskTimer taskTimer) {
        return TaskTimerStatus.PASUE.equals(taskTimer.getStatus());
    }

    /**
     * @param taskTimer
     * @return
     */
    public static boolean isStop(TaskTimer taskTimer) {
        return TaskTimerStatus.STOP.equals(taskTimer.getStatus());
    }

    /**
     * @param workDate
     * @param dueTime
     * @param unit
     * @return
     */
    public static boolean isAfterDueTime(Date workDate, Date dueTime, int unit) {
        boolean isAfter = false;
        // amount小于等于零，返回原来的到期时间
        switch (unit) {
            case TimerUnit.WORKING_DAY:
                // 工作日(工作时间)
            case TimerUnit.WORKING_DAY_24:
                // 工作日(24小时制)
            case TimerUnit.DATE:
                // 日期(2000-01-01)
            case TimerUnit.DATETIME_START:
                // 日期时间(开始)
            case TimerUnit.DATETIME_END:
                // 日期时间(结束)
                Date workingDate1 = convertTime(workDate, unit);
                Date workingDate2 = convertTime(dueTime, unit);
                isAfter = workingDate1.after(workingDate2);
                break;
            case TimerUnit.WORKING_HOUR:
                // 工作小时(工作时间)
            case TimerUnit.WORKING_HOUR_24:
                // 工作小时(24小时制)
            case TimerUnit.DATE_HOUR:
                // 日期到时(2000-01-01 12)
                Date workingHour1 = convertTime(workDate, unit);
                Date workingHour2 = convertTime(dueTime, unit);
                isAfter = workingHour1.after(workingHour2);
                break;
            case TimerUnit.WORKING_MINUTE:
                // 工作分钟(工作时间)
            case TimerUnit.WORKING_MINUTE_24:
                // 工作分钟(24小时制)
            case TimerUnit.DATE_MINUTE:
                // 日期到分(2000-01-01 12:00)
                Date workingMinute1 = convertTime(workDate, unit);
                Date workingMinute2 = convertTime(dueTime, unit);
                isAfter = workingMinute1.after(workingMinute2);
                break;
            case TimerUnit.DAY:
                // 天
                Date date1 = convertTime(workDate, unit);
                Date date2 = convertTime(dueTime, unit);
                isAfter = date1.after(date2);
                break;
            case TimerUnit.HOUR:
                // 小时
                Date hour1 = convertTime(workDate, unit);
                Date hour2 = convertTime(dueTime, unit);
                isAfter = hour1.after(hour2);
                break;
            case TimerUnit.MINUTE:
                // 分钟
                Date minute1 = convertTime(workDate, unit);
                Date minute2 = convertTime(dueTime, unit);
                isAfter = minute1.after(minute2);
                break;
            default:
                break;
        }
        return isAfter;
    }

    /**
     * @param time
     * @param unit
     * @return
     */
    public static Date convertTime(Date time, int unit) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        switch (unit) {
            case TimerUnit.WORKING_DAY:
                // 工作日(工作时间)
            case TimerUnit.WORKING_DAY_24:
                // 工作日(24小时制)
            case TimerUnit.DATE:
                // 日期(2000-01-01)
            case TimerUnit.DATETIME_START:
                // 日期时间(开始)
            case TimerUnit.DATETIME_END:
                // 日期时间(结束)
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            case TimerUnit.WORKING_HOUR:
                // 工作小时(工作时间)
            case TimerUnit.WORKING_HOUR_24:
                // 工作小时(24小时制)
            case TimerUnit.DATE_HOUR:
                // 日期到时(2000-01-01 12)
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            case TimerUnit.WORKING_MINUTE:
                // 工作分钟(工作时间)
            case TimerUnit.WORKING_MINUTE_24:
                // 工作小时(24小时制)
            case TimerUnit.DATE_MINUTE:
                // 日期到分(2000-01-01 12:00)
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            case TimerUnit.DAY:
                // 天
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            case TimerUnit.HOUR:
                // 小时
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            case TimerUnit.MINUTE:
                // 分钟
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            default:
                break;
        }
        return calendar.getTime();
    }

    /**
     * @param taskTimer
     * @return
     */
    public static boolean isDyformFieldOfLimitTime(TaskTimer taskTimer) {
        String limitTimeType = taskTimer.getLimitTimeType();
        return TaskTimer.LIMIT_TIME_TYPE_FORM_FIELD.equals(limitTimeType) || TaskTimer.LIMIT_TIME_TYPE_FORM_DATE.equals(limitTimeType);
    }

    /**
     * @param taskTimer
     * @return
     */
    public static boolean isDateOfLimitTime(TaskTimer taskTimer) {
        Integer limitUnit = Integer.valueOf(taskTimer.getLimitUnit());
        return isDateOfLimitTime(limitUnit) || TaskTimer.LIMIT_TIME_TYPE_FORM_DATE.equals(taskTimer.getLimitTimeType()) || TaskTimer.LIMIT_TIME_TYPE_FIXED_DATE.equals(taskTimer.getLimitTimeType());
    }

    /**
     * @param taskTimer
     * @return
     */
    public static boolean isDateOfLimitTime(int limitUnit) {
        boolean isDateOfLimitTime = false;
        switch (limitUnit) {
            case TimerUnit.DATE:
            case TimerUnit.DATE_HOUR:
            case TimerUnit.DATE_MINUTE:
            case TimerUnit.DATETIME_START:
            case TimerUnit.DATETIME_END:
            case TimerUnit.DATE_WORKING_DAY:
            case TimerUnit.DATE_WORKING_HOUR:
            case TimerUnit.DATE_WORKING_MINUTE:
                isDateOfLimitTime = true;
                break;
            default:
                break;
        }
        return isDateOfLimitTime;
    }

    /**
     * @param unit
     * @return
     */
    public static String getTimerUnitName(int unit) {
        String timerUnitName = "工作日";
        switch (unit) {
            case TimerUnit.WORKING_DAY:
                // 工作日(工作时间)
                timerUnitName = "工作日";
                break;
            case TimerUnit.WORKING_HOUR:
                // 工作小时(工作时间)
                timerUnitName = "工作小时";
                break;
            case TimerUnit.WORKING_MINUTE:
                // 工作分钟(工作时间)
                timerUnitName = "工作分钟";
                break;
            case TimerUnit.WORKING_DAY_24:
                // 工作日(24小时制)
                timerUnitName = "工作日";
                break;
            case TimerUnit.WORKING_HOUR_24:
                // 工作小时(24小时制)
                timerUnitName = "工作小时";
                break;
            case TimerUnit.WORKING_MINUTE_24:
                // 工作分钟(24小时制)
                timerUnitName = "工作分钟";
                break;
            case TimerUnit.DAY:
                // 天
                timerUnitName = "天";
                break;
            case TimerUnit.HOUR:
                // 小时
                timerUnitName = "小时";
                break;
            case TimerUnit.MINUTE:
                // 分钟
                timerUnitName = "分钟";
                break;
            case TimerUnit.DATE:
                // 日期(2000-01-01)
                timerUnitName = "天";
                break;
            case TimerUnit.DATE_HOUR:
                // 日期到时(2000-01-01 12)
                timerUnitName = "小时";
                break;
            case TimerUnit.DATE_MINUTE:
                // 日期到分(2000-01-01 12:00)
                timerUnitName = "分钟";
                break;
            case TimerUnit.DATETIME_START:
                // 日期时间（开始）
                timerUnitName = "天";
                break;
            case TimerUnit.DATETIME_END:
                // 日期时间（结束）
                timerUnitName = "天";
                break;
            default:
                timerUnitName = "工作日";
                break;
        }
        return timerUnitName;
    }


    /**
     * @param timingModeType
     * @param timingModeUnit
     * @return
     */
    public static String getTimingMode(String timingModeType, String timingModeUnit) {
        String timingMode = EnumTimingMode.CUSTOM.getValue();
        switch (timingModeType) {
            case "1":
                if (EnumTimingModeUnit.DAY.getValue().equals(timingModeUnit)) {
                    timingMode = EnumTimingMode.WORKING_DAY.getValue();
                } else if (EnumTimingModeUnit.HOUR.getValue().equals(timingModeUnit)) {
                    timingMode = EnumTimingMode.WORKING_HOUR.getValue();
                } else {
                    timingMode = EnumTimingMode.WORKING_MINUTE.getValue();
                }
                break;
            case "2":
                if (EnumTimingModeUnit.DAY.getValue().equals(timingModeUnit)) {
                    timingMode = EnumTimingMode.WORKING_DAY_24.getValue();
                } else if (EnumTimingModeUnit.HOUR.getValue().equals(timingModeUnit)) {
                    timingMode = EnumTimingMode.WORKING_HOUR_24.getValue();
                } else {
                    timingMode = EnumTimingMode.WORKING_MINUTE_24.getValue();
                }
                break;
            case "3":
                if (EnumTimingModeUnit.DAY.getValue().equals(timingModeUnit)) {
                    timingMode = EnumTimingMode.DAY.getValue();
                } else if (EnumTimingModeUnit.HOUR.getValue().equals(timingModeUnit)) {
                    timingMode = EnumTimingMode.HOUR.getValue();
                } else {
                    timingMode = EnumTimingMode.MINUTE.getValue();
                }
                break;
        }
        return timingMode;
    }

    /**
     * @param limitUnitText
     * @return
     */
    public static String getTimingMode(String limitUnitText) {
        // 计时方式
        int limitUnit = Integer.valueOf(limitUnitText);
        EnumTimingMode timingMode = EnumTimingMode.DAY;
        switch (limitUnit) {
            case TimerUnit.DATE_WORKING_DAY:
            case TimerUnit.WORKING_DAY:
                // 工作日(工作时间)
                timingMode = EnumTimingMode.WORKING_DAY;
                break;
            case TimerUnit.DATE_WORKING_HOUR:
            case TimerUnit.WORKING_HOUR:
                // 工作小时(工作时间)
                timingMode = EnumTimingMode.WORKING_HOUR;
                break;
            case TimerUnit.DATE_WORKING_MINUTE:
            case TimerUnit.WORKING_MINUTE:
                // 工作分钟(工作时间)
                timingMode = EnumTimingMode.WORKING_MINUTE;
                break;
            case TimerUnit.WORKING_DAY_24:
                // 工作日(24小时制)
                timingMode = EnumTimingMode.WORKING_DAY_24;
                break;
            case TimerUnit.WORKING_HOUR_24:
                // 工作小时(24小时制)
                timingMode = EnumTimingMode.WORKING_HOUR_24;
                break;
            case TimerUnit.WORKING_MINUTE_24:
                // 工作分钟(24小时制)
                timingMode = EnumTimingMode.WORKING_MINUTE_24;
                break;
            case TimerUnit.DAY:
                // 天
                timingMode = EnumTimingMode.DAY;
                break;
            case TimerUnit.HOUR:
                // 小时
                timingMode = EnumTimingMode.HOUR;
                break;
            case TimerUnit.MINUTE:
                // 分钟
                timingMode = EnumTimingMode.MINUTE;
                break;
            case TimerUnit.DATE:
                // 日期(2000-01-01)
                timingMode = EnumTimingMode.WORKING_DAY_24;
                break;
            case TimerUnit.DATE_HOUR:
                // 日期到时(2000-01-01 12)
                timingMode = EnumTimingMode.WORKING_HOUR_24;
                break;
            case TimerUnit.DATE_MINUTE:
                // 日期到分(2000-01-01 12:00)
                timingMode = EnumTimingMode.WORKING_MINUTE_24;
                break;
            case TimerUnit.DATETIME_START:
                // 日期时间（开始）
                timingMode = EnumTimingMode.DAY;
                break;
            case TimerUnit.DATETIME_END:
                // 日期时间（结束）
                timingMode = EnumTimingMode.DAY;
                break;
            default:
                break;
        }
        return timingMode.getValue();
    }
}
