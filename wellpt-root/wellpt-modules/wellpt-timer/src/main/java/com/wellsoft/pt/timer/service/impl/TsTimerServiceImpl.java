/*
 * @(#)2021年4月7日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.timer.dao.TsTimerDao;
import com.wellsoft.pt.timer.dto.TsTimerConfigDto;
import com.wellsoft.pt.timer.entity.TsTimerAlarmEntity;
import com.wellsoft.pt.timer.entity.TsTimerConfigEntity;
import com.wellsoft.pt.timer.entity.TsTimerEntity;
import com.wellsoft.pt.timer.entity.TsTimerLogEntity;
import com.wellsoft.pt.timer.enums.*;
import com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService;
import com.wellsoft.pt.timer.listener.TimerListener;
import com.wellsoft.pt.timer.service.TsTimerAlarmService;
import com.wellsoft.pt.timer.service.TsTimerConfigService;
import com.wellsoft.pt.timer.service.TsTimerLogService;
import com.wellsoft.pt.timer.service.TsTimerService;
import com.wellsoft.pt.timer.support.*;
import com.wellsoft.pt.timer.support.event.TimerEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
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
 * 2021年4月7日.1	zhulh		2021年4月7日		Create
 * </pre>
 * @date 2021年4月7日
 */
@Service
public class TsTimerServiceImpl extends AbstractJpaServiceImpl<TsTimerEntity, TsTimerDao, String>
        implements TsTimerService {

    private static final String GET_DUE_TASK_TIMERS = "from TsTimerEntity t where t.status = :status and t.timingState in(:timingStates) and t.dueTime < :deadlineTime and (t.dueDoingDone is null or t.dueDoingDone = false)";

    private static final String GET_OVERDUE_TASK_TIMERS = "from TsTimerEntity t where t.status = :status and t.timingState =:timingState and t.dueTime < :deadlineTime and (t.overDueDoingDone is null or t.overDueDoingDone = false)";

    @Autowired(required = false)
    private Map<String, TimerListener> listenerMap;

    @Autowired
    private TsTimerConfigService timerConfigService;

    @Autowired
    private TsTimerAlarmService timerAlarmService;

    @Autowired
    private TsTimerLogService timerLogService;

    /**
     * @param timeLimit
     * @param timerConfigEntity
     * @return
     */
    private static double converTimeLimitIfRequired(Date fromDate, double timeLimit,
                                                    TsTimerConfigEntity timerConfigEntity, String workTimePlanUuid) {
        Double retTimeLimit = timeLimit;
        // 动态截止时间按分钟计算
        if (EnumTimeLimitType.DATE.getValue().equals(timerConfigEntity.getTimeLimitType())
                || EnumTimeLimitType.CUSTOM_DATE.getValue().equals(timerConfigEntity.getTimeLimitType())) {
            return retTimeLimit;
        }

        EnumTimingMode enumTimingMode = EnumTimingMode.getByValue(timerConfigEntity.getTimingMode());
        EnumTimeLimitUnit timeLimitUnit = EnumTimeLimitUnit.getByValue(timerConfigEntity.getTimeLimitUnit());
        TsWorkTimePlanFacadeService workTimePlanFacadeService = ApplicationContextHolder
                .getBean(TsWorkTimePlanFacadeService.class);
        switch (enumTimingMode) {
            case WORKING_DAY:
                // 工作日(工作时间)
                break;
            case WORKING_HOUR:
                // 工作小时(工作时间)
                if (EnumTimeLimitUnit.Day.equals(timeLimitUnit)) {
                    double ceilTimeLimit = Math.ceil(timeLimit);
                    WorkTimes workTimes = workTimePlanFacadeService.getWorkTimes(workTimePlanUuid, fromDate,
                            Double.valueOf(ceilTimeLimit).intValue());
                    double increment = Double.valueOf(timeLimit / Math.abs(ceilTimeLimit));
                    retTimeLimit = increment * workTimes.getTotalWorkHours();
                }
                break;
            case WORKING_MINUTE:
                // 工作分钟(工作时间)
                if (EnumTimeLimitUnit.Day.equals(timeLimitUnit)) {
                    double ceilTimeLimit = Math.ceil(timeLimit);
                    WorkTimes workTimes = workTimePlanFacadeService.getWorkTimes(workTimePlanUuid, fromDate,
                            Double.valueOf(ceilTimeLimit).intValue());
                    double increment = Double.valueOf(timeLimit / Math.abs(timeLimit));
                    retTimeLimit = increment * workTimes.getTotalWorkMinutes();
                } else if (EnumTimeLimitUnit.Hour.equals(timeLimitUnit)) {
                    retTimeLimit = retTimeLimit * 60;
                }
                break;
            case WORKING_DAY_24:
                // 工作日(24小时制不含工作日)
                break;
            case WORKING_HOUR_24:
                // 工作小时(24小时制)
                if (EnumTimeLimitUnit.Day.equals(timeLimitUnit)) {
                    retTimeLimit = retTimeLimit * 24;
                }
                break;
            case WORKING_MINUTE_24:
                // 工作分钟(24小时制)
                if (EnumTimeLimitUnit.Day.equals(timeLimitUnit)) {
                    retTimeLimit = retTimeLimit * 24 * 60;
                } else if (EnumTimeLimitUnit.Hour.equals(timeLimitUnit)) {
                    retTimeLimit = retTimeLimit * 60;
                }
                break;
            case DAY:
                // 天
                break;
            case HOUR:
                // 小时
                if (EnumTimeLimitUnit.Day.equals(timeLimitUnit)) {
                    retTimeLimit = retTimeLimit * 24;
                }
                break;
            case MINUTE:
                // 分钟
                if (EnumTimeLimitUnit.Day.equals(timeLimitUnit)) {
                    retTimeLimit = retTimeLimit * 24 * 60;
                } else if (EnumTimeLimitUnit.Hour.equals(timeLimitUnit)) {
                    retTimeLimit = retTimeLimit * 60;
                }
                break;
            default:
                break;
        }
        return retTimeLimit;
    }

    /**
     * 计算出定时任务的办理时限
     *
     * @return
     */
    public static Date calculateDueTime(String workTimePlanUuid, Date fromTime, double limitTime,
                                        TimerConfig timerConfig, Date defaultDueTime) {
        // 当前启动包含当前时间点
        Double amount = limitTime;
        Date dueTime = null;
        // amount小于零，返回原来的过期时间
        if (amount < 0) {
            dueTime = defaultDueTime;
        } else if (amount == 0) {
            // amount等于零，返回当前时间
            EnumTimingMode enumTimingMode = timerConfig.getEnumTimingMode();
            Date workDate = convertTime(fromTime, enumTimingMode);
            dueTime = workDate;
        } else {
            dueTime = calculateTime(workTimePlanUuid, fromTime, amount, timerConfig);
        }
        // 自动推迟到下一工作时间起始点前
        //		String timeLimitType = timerConfig.getTimeLimitType();
        //		if (amount >= 0 && (EnumTimeLimitType.NUMBER.getValue().equals(timeLimitType)
        //				|| EnumTimeLimitType.CUSTOM_NUMBER.getValue().equals(timeLimitType))) {
        dueTime = autoDelayIfRequired(dueTime, timerConfig, workTimePlanUuid);
        //		}
        return dueTime;
    }

    /**
     * @param amount
     * @param timerConfig
     * @return
     */
    private static Date calculateTime(String workTimePlanUuid, Date fromTime, Double amount, TimerConfig timerConfig) {
        // 计时方式
        EnumTimingMode enumTimingMode = timerConfig.getRuntimeEnumTimingMode();
        if (enumTimingMode == null) {
            enumTimingMode = timerConfig.getEnumTimingMode();
        }
        Calendar calendar = Calendar.getInstance();
        Date workDate = convertTime(fromTime, enumTimingMode);
        Date dueTime = null;

        TsWorkTimePlanFacadeService workTimePlanFacadeService = ApplicationContextHolder
                .getBean(TsWorkTimePlanFacadeService.class);
        switch (enumTimingMode) {
            case WORKING_DAY:
                // 工作日(工作时间)
                //dueTime = workTimePlanFacadeService.getWorkDate(workTimePlanUuid, workDate, amount, WorkUnit.WorkingDay);
                //break;
            case WORKING_HOUR:
                // 工作小时(工作时间)
                //dueTime = workTimePlanFacadeService.getWorkDate(workTimePlanUuid, workDate, amount, WorkUnit.WorkingHour);
                //break;
            case WORKING_MINUTE:
                // 工作分钟(工作时间)
                dueTime = workTimePlanFacadeService.getWorkDate(workTimePlanUuid, workDate, amount, WorkUnit.WorkingMinute);
                break;
            case WORKING_DAY_24:
                // 工作日(24小时制不含工作日)
                //dueTime = workTimePlanFacadeService.getWorkDate(workTimePlanUuid, workDate, amount, WorkUnit.WorkingDayOf24Hour);
                //break;
            case WORKING_HOUR_24:
                // 工作小时(24小时制)
                //			dueTime = workTimePlanFacadeService.getWorkDate(workTimePlanUuid, workDate, amount,
                //					WorkUnit.WorkingHourOf24Hour);
                //			break;
            case WORKING_MINUTE_24:
                // 工作分钟(24小时制)
                dueTime = workTimePlanFacadeService.getWorkDate(workTimePlanUuid, workDate, amount,
                        WorkUnit.WorkingMinuteOf24Hour);
                break;
            case DAY:
                // 天
                //			calendar.setTime(workDate);
                //			calendar.add(Calendar.DAY_OF_MONTH, Double.valueOf(amount * 60 * 24).intValue());
                //			dueTime = calendar.getTime();
                //			break;
            case HOUR:
                // 小时
                //			calendar.setTime(workDate);
                //			calendar.add(Calendar.MINUTE, Double.valueOf(amount * 60).intValue());
                //			dueTime = calendar.getTime();
                //			break;
            case MINUTE:
                // 分钟
                calendar.setTime(workDate);
                calendar.add(Calendar.SECOND, Double.valueOf((amount * 60)).intValue());
                dueTime = calendar.getTime();
                break;
            default:
                dueTime = workDate;
                break;
        }
        return dueTime;
    }

    /**
     * @param deadlineTime
     * @param timerConfig
     * @return
     */
    private static Date getOverDueTime(Date deadlineTime, TimerConfig timerConfig) {
        TsWorkTimePlanFacadeService workTimePlanFacadeService = ApplicationContextHolder
                .getBean(TsWorkTimePlanFacadeService.class);
        EnumTimingMode enumTimingMode = timerConfig.getEnumTimingMode();
        String workTimePlanUuid = timerConfig.getWorkTimePlanUuid();
        Date dueTime = convertTime(deadlineTime, enumTimingMode);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dueTime);
        switch (enumTimingMode) {
            case WORKING_DAY:
                // 工作日(工作时间)
                if (workTimePlanFacadeService.isWorkDay(workTimePlanUuid, dueTime)) {
                    WorkTime workTime = workTimePlanFacadeService.getWorkTime(workTimePlanUuid, dueTime);
                    calendar.setTime(workTime.getEndTime());
                } else {
                    calendar.setTime(deadlineTime);
                }
                break;
            case WORKING_HOUR:
                // 工作小时(工作时间)
                if (workTimePlanFacadeService.isWorkDay(workTimePlanUuid, dueTime)) {
                    WorkTime workTime = workTimePlanFacadeService.getWorkTime(workTimePlanUuid, dueTime);
                    calendar.add(Calendar.HOUR_OF_DAY, 1);
                    Date tmpTime = calendar.getTime();
                    // 工作时间内
                    if (workTime.isInTimePeriod(tmpTime)) {
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                    } else if (calendar.getTime().after(workTime.getEndTime())) {
                        // 下午下班时间之后，设置为下午下班时间
                        calendar.setTime(workTime.getEndTime());
                    } else if (calendar.getTime().before(workTime.getFromTime())) {
                        // 上午上班时间之前，设置为上午上班时间
                        calendar.setTime(workTime.getFromTime());
                    } else {
                        // 上午下班时间，下午上班时间之间，设置为上午下班时间
                        calendar.setTime(workTime.getPreviousEndTimeBetweenTimePeriod(tmpTime));
                    }
                } else {
                    calendar.setTime(deadlineTime);
                }
                break;
            case WORKING_MINUTE:
                // 工作分钟(工作时间)
                dueTime = workTimePlanFacadeService.getWorkDate(workTimePlanUuid, dueTime, 1, WorkUnit.WorkingMinute);
                calendar.setTime(dueTime);
                break;
            case WORKING_DAY_24:
                // 工作日(24小时制)
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR, 0);
                calendar.set(Calendar.MINUTE, 0);
                break;
            case WORKING_HOUR_24:
                // 工作小时(24小时制)
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                calendar.set(Calendar.MINUTE, 0);
                break;
            case WORKING_MINUTE_24:
                // 工作分钟(24小时制)
                calendar.add(Calendar.MINUTE, 1);
                break;
            case DAY:
                // 天
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                break;
            case HOUR:
                // 小时
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                break;
            case MINUTE:
                // 分钟
                calendar.add(Calendar.MINUTE, 1);
                break;
            default:
                break;
        }
        return calendar.getTime();
    }

    /**
     * @param dueTime
     * @param timerConfig
     * @param workTimePlanUuid
     * @return
     */
    private static Date autoDelayIfRequired(Date dueTime, TimerConfig timerConfig, String workTimePlanUuid) {
        Date retDueTime = dueTime;
        EnumTimingMode enumTimingMode = timerConfig.getEnumTimingMode();
        switch (enumTimingMode) {
            case WORKING_DAY:
                // 工作日(工作时间)
                retDueTime = autoDelayWorkingMinuteIfRequired(dueTime, timerConfig, workTimePlanUuid);
                break;
            case WORKING_HOUR:
                // 工作小时(工作时间)
                retDueTime = autoDelayWorkingMinuteIfRequired(dueTime, timerConfig, workTimePlanUuid);
                break;
            case WORKING_MINUTE:
                // 工作分钟(工作时间)
                retDueTime = autoDelayWorkingMinuteIfRequired(dueTime, timerConfig, workTimePlanUuid);
                break;
            case WORKING_DAY_24:
                retDueTime = autoDelayWorkingMinute24IfRequired(dueTime, timerConfig, workTimePlanUuid);
                break;
            case WORKING_HOUR_24:
                retDueTime = autoDelayWorkingMinute24IfRequired(dueTime, timerConfig, workTimePlanUuid);
                break;
            case WORKING_MINUTE_24:
                retDueTime = autoDelayWorkingMinute24IfRequired(dueTime, timerConfig, workTimePlanUuid);
                break;
            case DAY:
                // 天
            case HOUR:
                // 小时
            case MINUTE:
                // 分钟
                break;
            default:
                break;
        }
        return retDueTime;
    }

    /**
     * 默认到期时间自动推迟
     *
     * @param dueTime
     * @param timerConfig
     * @return
     */
    private static Date autoDelayWorkingMinuteIfRequired(Date dueTime, TimerConfig timerConfig,
                                                         String workTimePlanUuid) {
        if (!timerConfig.isAutoDelay()) {
            return dueTime;
        }
        Date retDueTime = dueTime;
        // BasicDataApiFacade basicDataApiFacade = ApplicationContextHolder.getBean(BasicDataApiFacade.class);
        TsWorkTimePlanFacadeService workTimePlanFacadeService = ApplicationContextHolder
                .getBean(TsWorkTimePlanFacadeService.class);
        return workTimePlanFacadeService.autoDelayWorkDateIfRequired(retDueTime, WorkUnit.WorkingMinute, workTimePlanUuid);
//        WorkTime workTime = workTimePlanFacadeService.getWorkTime(workTimePlanUuid, retDueTime);
//        Date toTimeInTimePeriod = workTime.getToTimeInTimePeriod(retDueTime);
//        // WorkingHour workingHour = basicDataApiFacade.getEffectiveWorkingHour(retDueTime);
//        // 则好到上班时间，推迟到前一工作日下时间前
//        if (retDueTime.equals(workTime.getEndTime())) {
//            // Date workDate = basicDataApiFacade.getPrevWorkDate(retDueTime);
//            // workingHour = basicDataApiFacade.getEffectiveWorkingHour(workDate);
//            Date nextWorkDate = workTimePlanFacadeService.getNextWorkDate(workTimePlanUuid, retDueTime);
//            WorkTime nextWorkTime = workTimePlanFacadeService.getWorkTime(workTimePlanUuid, nextWorkDate);
//            retDueTime = nextWorkTime.getFromTime();
//        } else if (workTime.isInTimePeriod(dueTime) && dueTime.equals(toTimeInTimePeriod)) {
//            retDueTime = workTime.getNextFromTimeInTimePeriod(retDueTime);
//        }
//        return retDueTime;
    }

    /**
     * 默认到期时间不自动推迟
     *
     * @param dueTime
     * @param timerConfig
     * @return
     */
    private static Date autoDelayWorkingMinute24IfRequired(Date dueTime, TimerConfig timerConfig,
                                                           String workTimePlanUuid) {
        if (!timerConfig.isAutoDelay()) {
            return dueTime;
        }
        Date retDueTime = dueTime;
        // BasicDataApiFacade basicDataApiFacade = ApplicationContextHolder.getBean(BasicDataApiFacade.class);
        TsWorkTimePlanFacadeService workTimePlanFacadeService = ApplicationContextHolder
                .getBean(TsWorkTimePlanFacadeService.class);
        return workTimePlanFacadeService.autoDelayWorkDateIfRequired(retDueTime, WorkUnit.WorkingMinuteOf24Hour, workTimePlanUuid);
//        // 到期时间不是工作日，取下一工作日
//        Date workDate = retDueTime;
//        if (!workTimePlanFacadeService.isWorkDay(workTimePlanUuid, workDate)) {
//            workDate = workTimePlanFacadeService.getNextWorkDate(workTimePlanUuid, workDate);
//        }
//        WorkTime workTime = workTimePlanFacadeService.getWorkTime(workTimePlanUuid, workDate);
//        // 工作分钟(24小时制)
//        if (workTime.isInTimePeriod(retDueTime)) {
//            Date toTimeInTimePeriod = workTime.getToTimeInTimePeriod(retDueTime);
//            // 则好到下班时间，推迟到第二天上班时间前
//            if (retDueTime.equals(workTime.getEndTime())) {
//                Date nextWorkDate = workTimePlanFacadeService.getNextWorkDate(workTimePlanUuid, retDueTime);
//                WorkTime nextWorkTime = workTimePlanFacadeService.getWorkTime(workTimePlanUuid, nextWorkDate);
//                Calendar calendar = Calendar.getInstance();
//                calendar.setTime(nextWorkTime.getFromTime());
//                // calendar.add(Calendar.MINUTE, -1);
//                retDueTime = calendar.getTime();
//            } else if (retDueTime.equals(toTimeInTimePeriod)) {
//                // 不在最后时间段的结束时间点，取下一时间段的开始时间
//                retDueTime = workTime.getNextFromTimeInTimePeriod(retDueTime);
//            }
//        } else if (workTime.isBeforeEndTime(retDueTime)) {
//            // 在时间段之间的时间点，取下一时间段的开始时间
//            retDueTime = workTime.getNextFromTimeBetweenTimePeriod(retDueTime);
//        } else {
//            // 推迟到第二天上班时间前
//            Date nextWorkDate = workTimePlanFacadeService.getNextWorkDate(workTimePlanUuid, retDueTime);
//            WorkTime nextWorkTime = workTimePlanFacadeService.getWorkTime(workTimePlanUuid, nextWorkDate);
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTime(nextWorkTime.getFromTime());
//            // calendar.add(Calendar.MINUTE, -1);
//            retDueTime = calendar.getTime();
//        }
//        return retDueTime;
    }

    /**
     * 新方式，统一到分钟
     *
     * @param time
     * @param enumTimingMode
     * @return
     */
    private static Date convertTime(Date time, EnumTimingMode enumTimingMode) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        // 统一到分钟
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        //		switch (enumTimingMode) {
        //		case WORKING_DAY:
        //			// 工作日(工作时间)
        //		case WORKING_DAY_24:
        //			// 工作日(24小时制)
        //		case DAY:
        //			// 天
        //			calendar.set(Calendar.HOUR_OF_DAY, 0);
        //			calendar.set(Calendar.MINUTE, 0);
        //			calendar.set(Calendar.SECOND, 0);
        //			calendar.set(Calendar.MILLISECOND, 0);
        //			break;
        //		case WORKING_HOUR:
        //			// 工作小时
        //		case WORKING_HOUR_24:
        //			// 工作小时(24小时制)
        //		case HOUR:
        //			// 小时
        //			calendar.set(Calendar.MINUTE, 0);
        //			calendar.set(Calendar.SECOND, 0);
        //			calendar.set(Calendar.MILLISECOND, 0);
        //			break;
        //		case WORKING_MINUTE:
        //			// 工作分钟(工作时间)
        //		case WORKING_MINUTE_24:
        //			// 工作小时(24小时制)
        //		case MINUTE:
        //			// 分钟
        //			calendar.set(Calendar.SECOND, 0);
        //			calendar.set(Calendar.MILLISECOND, 0);
        //			break;
        //		default:
        //			break;
        //		}
        return calendar.getTime();
    }

    /**
     * 计算剩余的办理时限，新的计时方式剩余时间都转为分钟
     *
     * @param fromTime
     * @param toTime
     * @param enumTimingMode
     * @param workTimePlanUuid
     * @return
     */
    public static double calculateRemainingTimeLimit(Date fromTime, Date toTime, EnumTimingMode enumTimingMode,
                                                     String workTimePlanUuid) {
        double remainingTime = 0;
        if (toTime == null) {
            return 0;
        }

        Date workDate = convertTime(fromTime, enumTimingMode);
        Date deadlineTime = convertTime(toTime, enumTimingMode);
        // 逾期时，继续计算逾期的时长
        int numberUnit = 1;
        if (isAfterDueTime(workDate, deadlineTime, enumTimingMode)) {
            Date tmp = deadlineTime;
            deadlineTime = workDate;
            workDate = tmp;
            numberUnit = numberUnit * -1;
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
        return remainingTime * numberUnit;
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
     * @see com.wellsoft.pt.timer.service.TsTimerService#startTimer(com.wellsoft.pt.timer.support.TsTimerParam)
     */
    @Override
    @Transactional
    public TsTimerEntity startTimer(TsTimerParam timerParam) {
        String timerConfigUuid = timerParam.getTimerConfigUuid();
        String workTimePlanUuid = timerParam.getWorkTimePlanUuid();
        String listener = timerParam.getListener();
        TsTimerEntity timerEntity = null;
        Double timeLimit = null;
        Date startTime = timerParam.getStartTime();
        if (startTime == null) {
            startTime = Calendar.getInstance().getTime();
        }
        TsTimerConfigEntity timerConfigEntity = timerConfigService.getOne(timerConfigUuid);
        EnumTimeLimitType enumTimeLimitType = EnumTimeLimitType.getByValue(timerConfigEntity.getTimeLimitType());
        switch (enumTimeLimitType) {
            case NUMBER:
            case CUSTOM_NUMBER:
                // 设置固定时限
                // 调用服务时指定
                timeLimit = timerParam.getTimeLimit();
                if (timeLimit == null) {
                    timeLimit = Double.valueOf(timerConfigEntity.getTimeLimit());
                }
                timerEntity = doStartTimer(startTime, timerConfigEntity, workTimePlanUuid, timeLimit, listener, timerParam);
                break;
            case DATE:
            case CUSTOM_DATE:
                // 设置截止时间
                // 调用服务时指定
                Date dueTime = timerParam.getDueTime();
                try {
                    if (dueTime == null) {
                        dueTime = DateUtils.parse(timerConfigEntity.getTimeLimit());
                    }
                    // 截止时间按天计算工作日
                    if (StringUtils.equals(EnumTimeLimitUnit.Day.getValue(), timerConfigEntity.getTimeLimitUnit())
                            && isTimingModeOfWorkingDay(timerConfigEntity, timerParam)) {
                        Calendar calendar = DateUtils.getMaxTimeCalendar(DateUtils.getCalendar(dueTime));
                        calendar.add(Calendar.SECOND, 1);
                        dueTime = calendar.getTime();
                    }
                    EnumTimingMode timingMode = null;
                    if (StringUtils.isNotBlank(timerParam.getTimingMode())) {
                        timingMode = EnumTimingMode.getByValue(timerParam.getTimingMode());
                    }
                    if (timingMode == null) {
                        timingMode = EnumTimingMode.getByValue(timerConfigEntity.getTimingMode());
                    }
                    timeLimit = calculateRemainingTimeLimit(startTime, dueTime, timingMode, workTimePlanUuid);
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                    throw new BusinessException("时限类型为设置截止时间的值[" + timerConfigEntity.getTimeLimit() + "]无法转化为日期");
                }
                timerEntity = doStartTimer(startTime, timerConfigEntity, workTimePlanUuid, timeLimit, listener, timerParam);
                break;
            default:
                break;
        }
        return timerEntity;
    }

    /**
     * @param timerConfigEntity
     * @return
     */
    private boolean isTimingModeOfWorkingDay(TsTimerConfigEntity timerConfigEntity, TsTimerParam timerParam) {
        boolean isTimingModeOfWorkingDay = true;
        EnumTimingMode enumTimingMode = null;
        if (StringUtils.isNotBlank(timerParam.getTimingMode())) {
            enumTimingMode = EnumTimingMode.getByValue(timerParam.getTimingMode());
        }
        if (enumTimingMode == null) {
            enumTimingMode = EnumTimingMode.getByValue(timerConfigEntity.getTimingMode());
        }
        switch (enumTimingMode) {
            case DAY:
            case HOUR:
            case MINUTE:
                isTimingModeOfWorkingDay = false;
                break;
            default:
                break;
        }
        return isTimingModeOfWorkingDay;
    }

    /**
     * @param timerConfigEntity
     * @return
     */
    private boolean isIncludeStartTimePointOfLimitTime(TimerConfig timerConfig) {
        String timeLimitType = timerConfig.getTimeLimitType();
        if (EnumTimeLimitType.NUMBER.getValue().equals(timeLimitType)
                || EnumTimeLimitType.CUSTOM_NUMBER.getValue().equals(timeLimitType)) {
            return true;
        }
        return false;
    }

    /**
     * @param startTime
     * @param timerConfigEntity
     * @param workTimePlanUuid
     * @param timeLimit
     * @param listener
     * @return
     */
    private TsTimerEntity doStartTimer(Date startTime, TsTimerConfigEntity timerConfigEntity, String workTimePlanUuid,
                                       double timeLimit, String listener, TsTimerParam timerParam) {
        Date fromTime = startTime != null ? startTime : Calendar.getInstance().getTime();
        TimerConfig timerConfig = getTimerConfig(null, timerConfigEntity);
        if (StringUtils.isNotBlank(timerParam.getTimingMode())) {
            updateTimerConfig(timerParam.getTimingMode(), timerConfig, timerConfigEntity);
        }
        // 转换输入时间的单位转化为计时方式的值
        // 包含当前开始计时时间点
        fromTime = includeStartTimePointOfLimitTimeIfRequired(fromTime, timerConfig);
        // double timeLimitAmount = converTimeLimitIfRequired(fromTime, timeLimit, timerConfigEntity, workTimePlanUuid);
        double timeLimitAmount = converTimeLimitByTimeLimitUnitIfRequired(fromTime, timeLimit, timerConfig, timerConfigEntity,
                workTimePlanUuid);
        Date dueTime = calculateDueTime(workTimePlanUuid, fromTime, timeLimitAmount, timerConfig, fromTime);
        // 剩余办理时限
        double remainingTimeLimit = timeLimitAmount;
        //		if (EnumTimeLimitType.NUMBER.getValue().equals(timerConfigEntity.getTimeLimitType())
        //				|| EnumTimeLimitType.CUSTOM_NUMBER.getValue().equals(timerConfigEntity.getTimeLimitType())) {
        //			remainingTimeLimit = calculateRemainingTimeLimit(fromTime, dueTime, timerConfig.getEnumTimingMode(),
        //					workTimePlanUuid);
        //		}
        Map<String, Object> timerData = Maps.newHashMap();
        TsTimerEntity timerEntity = new TsTimerEntity();
        // 计时配置UUID
        timerEntity.setConfigUuid(timerConfigEntity.getUuid());
        // 工作时间方案UUID
        timerEntity.setWorkTimePlanUuid(workTimePlanUuid);
        // 计算后初始化的办理时限数字
        timerEntity.setInitTimeLimit(Double.valueOf(timeLimitAmount));
        // 计算后初始化的办理时限日期，当时限类型为指定日期或表单日期字段时有效
        timerEntity.setInitDueTime(dueTime);
        // 开始计时时间点
        timerEntity.setStartTime(fromTime);
        // 最新开始时间
        timerEntity.setLastStartTime(fromTime);
        // 计时方式
        if (StringUtils.isNotBlank(timerParam.getTimingMode())) {
            timerEntity.setTimingMode(timerParam.getTimingMode());
        } else {
            timerEntity.setTimingMode(timerConfigEntity.getTimingMode());
        }
        // 时限类型
        timerEntity.setTimeLimitType(timerConfigEntity.getTimeLimitType());
        // 最新的办理时限
        timerEntity.setTimeLimit(remainingTimeLimit);
        // 到期时间
        timerEntity.setDueTime(dueTime);
        // 计时器是运行状态(0未启动、1已启动、2暂停、3结束)
        timerEntity.setStatus(EnumTimerStatus.STARTED.getValue());
        // 计时状态(0正常、1预警、2到期、3逾期)
        timerEntity.setTimingState(EnumTimingState.NORMAL.getValue());
        // 计时监听器
        if (StringUtils.isNotBlank(listener)) {
            timerEntity.setListener(listener);
        } else {
            timerEntity.setListener(timerConfigEntity.getListener());
        }
        // 标记启用预警信息
        if (CollectionUtils.isNotEmpty(timerParam.getTimerAlarms())) {
            timerEntity.setEnableAlarm(true);
            timerData.put("timerAlarms", timerParam.getTimerAlarms());
        } else {
            timerEntity.setEnableAlarm(false);
        }
        // 附加数据
        Map<String, Object> extraData = timerParam.getExtraData();
        if (extraData != null) {
            timerData.putAll(extraData);
        }
        // 计时器数据
        timerEntity.setTimerData(JsonUtils.object2Json(timerData));
        this.save(timerEntity);

        // 记录日志
        timerLogService.log(timerEntity, EnumTimerLogType.START);
        return timerEntity;
    }

    /**
     * @param fromDate
     * @param timeLimit
     * @param timerConfigEntity
     * @param workTimePlanUuid
     * @return
     */
    private double converTimeLimitByTimeLimitUnitIfRequired(Date fromDate, double timeLimit, TimerConfig timerConfig,
                                                            TsTimerConfigEntity timerConfigEntity, String workTimePlanUuid) {
        Double retTimeLimit = timeLimit;
        // 动态截止时间按分钟计算
        if (EnumTimeLimitType.DATE.getValue().equals(timerConfigEntity.getTimeLimitType())
                || EnumTimeLimitType.CUSTOM_DATE.getValue().equals(timerConfigEntity.getTimeLimitType())) {
            return retTimeLimit;
        }

        EnumTimingMode enumTimingMode = timerConfig.getRuntimeEnumTimingMode();// EnumTimingMode.getByValue(timerConfigEntity.getTimingMode());
        EnumTimeLimitUnit timeLimitUnit = EnumTimeLimitUnit.getByValue(timerConfigEntity.getTimeLimitUnit());
        TsWorkTimePlanFacadeService workTimePlanFacadeService = ApplicationContextHolder
                .getBean(TsWorkTimePlanFacadeService.class);
        switch (enumTimingMode) {
            case WORKING_DAY:
                // 工作日(工作时间)
            case WORKING_HOUR:
                // 工作小时(工作时间)
            case WORKING_MINUTE:
                // 工作分钟(工作时间)
                if (EnumTimeLimitUnit.Day.equals(timeLimitUnit)) {
                    //				double ceilTimeLimit = Math.ceil(Math.abs(timeLimit));
                    //				WorkTimes workTimes = workTimePlanFacadeService.getWorkTimes(workTimePlanUuid, fromDate,
                    //						Double.valueOf(ceilTimeLimit).intValue());
                    //				double increment = timeLimit / ceilTimeLimit;
                    //				// 按每天具体的工作小时处理
                    //				retTimeLimit = increment * workTimes.getTotalWorkMinutes();
                    WorkTime workTime = workTimePlanFacadeService.getWorkTime(workTimePlanUuid, fromDate);
                    if (workTime == null || workTime.isAfterEndTime(fromDate)) {
                        Date nextWorkDate = workTimePlanFacadeService.getNextWorkDate(workTimePlanUuid, fromDate);
                        retTimeLimit = workTimePlanFacadeService.getTotalWorkTimeMinutes(workTimePlanUuid, nextWorkDate,
                                timeLimit);
                    } else {
                        retTimeLimit = workTimePlanFacadeService.getTotalWorkTimeMinutes(workTimePlanUuid, fromDate,
                                timeLimit);
                    }
                } else if (EnumTimeLimitUnit.Hour.equals(timeLimitUnit)) {
                    retTimeLimit = retTimeLimit * 60;
                }
                break;
            case WORKING_DAY_24:
                // 工作日(24小时制不含工作日)
            case WORKING_HOUR_24:
                // 工作小时(24小时制)
            case WORKING_MINUTE_24:
                // 工作分钟(24小时制)
                if (EnumTimeLimitUnit.Day.equals(timeLimitUnit)) {
                    retTimeLimit = retTimeLimit * 24 * 60;
                } else if (EnumTimeLimitUnit.Hour.equals(timeLimitUnit)) {
                    retTimeLimit = retTimeLimit * 60;
                }
                break;
            case DAY:
                // 天
            case HOUR:
                // 小时
            case MINUTE:
                // 分钟
                if (EnumTimeLimitUnit.Day.equals(timeLimitUnit)) {
                    retTimeLimit = retTimeLimit * 24 * 60;
                } else if (EnumTimeLimitUnit.Hour.equals(timeLimitUnit)) {
                    retTimeLimit = retTimeLimit * 60;
                }
                break;
            default:
                break;
        }
        return retTimeLimit;
    }

    private TimerConfig getTimerConfig(TsTimerEntity timerEntity, TsTimerConfigEntity timerConfigEntity) {
        TimerConfig timerConfig = new TimerConfig();
        timerConfig.setTimingMode(timerConfigEntity.getTimingMode());
        timerConfig.setIncludeStartTimePoint(BooleanUtils.isTrue(timerConfigEntity.getIncludeStartTimePoint()));
        timerConfig.setAutoDelay(BooleanUtils.isTrue(timerConfigEntity.getAutoDelay()));
        if (timerEntity != null) {
            if (EnumTimingMode.CUSTOM.getValue().equals(timerConfig.getTimingMode())) {
                timerConfig.setTimingMode(timerEntity.getTimingMode());
            }
            timerConfig.setWorkTimePlanUuid(timerEntity.getWorkTimePlanUuid());
            timerConfig.setTimeLimitType(timerEntity.getTimeLimitType());
            timerConfig.setCurrentStatus(timerEntity.getStatus());
        } else {
            timerConfig.setTimeLimitType(timerConfigEntity.getTimeLimitType());
            timerConfig.setCurrentStatus(EnumTimerStatus.READY.getValue());
        }
        // 动态截止时间按分钟计算
        if (EnumTimeLimitType.DATE.getValue().equals(timerConfigEntity.getTimeLimitType())
                || EnumTimeLimitType.CUSTOM_DATE.getValue().equals(timerConfigEntity.getTimeLimitType())) {
            timerConfig.setRuntimeTimingMode(getRuntimeTimingMode(timerConfig.getEnumTimingMode()));
        }
        return timerConfig;
    }

    private void updateTimerConfig(String timingMode, TimerConfig timerConfig, TsTimerConfigEntity timerConfigEntity) {
        timerConfig.setTimingMode(timingMode);
        // 动态截止时间按分钟计算
        if (EnumTimeLimitType.DATE.getValue().equals(timerConfigEntity.getTimeLimitType())
                || EnumTimeLimitType.CUSTOM_DATE.getValue().equals(timerConfigEntity.getTimeLimitType())) {
            timerConfig.setRuntimeTimingMode(getRuntimeTimingMode(timerConfig.getEnumTimingMode()));
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerService#pauseTimer(java.lang.String)
     */
    @Override
    @Transactional
    public double pauseTimer(String timerUuid) {
        TsTimerEntity timerEntity = this.getOne(timerUuid);
        Date dueTime = timerEntity.getDueTime();
        String timingMode = timerEntity.getTimingMode();
        EnumTimingMode enumTimingMode = EnumTimingMode.getByValue(timingMode);
        double timeLimit = timerEntity.getTimeLimit();
        if (Integer.valueOf(EnumTimerStatus.STARTED.getValue()).equals(timerEntity.getStatus())) {
            Date fromTime = Calendar.getInstance().getTime();
            Date lastStartTime = timerEntity.getLastStartTime();
            if (fromTime.before(lastStartTime)) {
                fromTime = lastStartTime;
            }
            timeLimit = calculateRemainingTimeLimit(fromTime, dueTime, enumTimingMode,
                    timerEntity.getWorkTimePlanUuid());
            // 最新的办理时限
            timerEntity.setTimeLimit(Double.valueOf(timeLimit));
        }
        timerEntity.setStatus(EnumTimerStatus.PASUE.getValue());
        this.save(timerEntity);

        // 记录日志
        timerLogService.log(timerEntity, EnumTimerLogType.PAUSE);
        return timeLimit;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerService#resumeTimer(java.lang.String)
     */
    @Override
    @Transactional
    public Date resumeTimer(String timerUuid) {
        TsTimerEntity timerEntity = this.getOne(timerUuid);
        Double timeLimit = timerEntity.getTimeLimit();
        Date dueTime = timerEntity.getDueTime();
        Date currentTime = Calendar.getInstance().getTime();
        Date lastStartTime = timerEntity.getLastStartTime();
        if (currentTime.after(lastStartTime)) {
            lastStartTime = currentTime;
        }
        if (Integer.valueOf(EnumTimerStatus.PASUE.getValue()).equals(timerEntity.getStatus())) {
            TimerConfig timerConfig = getTimerConfig(timerEntity,
                    timerConfigService.getOne(timerEntity.getConfigUuid()));
            timerConfig.setRuntimeTimingMode(getRuntimeTimingMode(timerConfig.getEnumTimingMode()));
            dueTime = calculateDueTime(timerEntity.getWorkTimePlanUuid(), lastStartTime, timeLimit, timerConfig,
                    timerEntity.getDueTime());
        }
        timerEntity.setLastStartTime(lastStartTime);
        timerEntity.setDueTime(dueTime);
        timerEntity.setStatus(EnumTimerStatus.STARTED.getValue());
        this.save(timerEntity);

        // 记录日志
        timerLogService.log(timerEntity, EnumTimerLogType.RESUME);
        return dueTime;
    }

    /**
     * @param timingMode
     * @return
     */
    private String getRuntimeTimingMode(EnumTimingMode enumTimingMode) {
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
        return runtimeTimingMode.getValue();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerService#stopTimer(java.lang.String)
     */
    @Override
    @Transactional
    public double stopTimer(String timerUuid) {
        TsTimerEntity timerEntity = this.getOne(timerUuid);
        Date dueTime = timerEntity.getDueTime();
        String timingMode = timerEntity.getTimingMode();
        EnumTimingMode enumTimingMode = EnumTimingMode.getByValue(timingMode);
        double timeLimit = timerEntity.getTimeLimit();
        if (Integer.valueOf(EnumTimerStatus.STARTED.getValue()).equals(timerEntity.getStatus())) {
            timeLimit = calculateRemainingTimeLimit(Calendar.getInstance().getTime(), dueTime, enumTimingMode,
                    timerEntity.getWorkTimePlanUuid());
            // 最新的办理时限
            timerEntity.setTimeLimit(Double.valueOf(timeLimit));
        } else if (Integer.valueOf(EnumTimerStatus.PASUE.getValue()).equals(timerEntity.getStatus()) && timeLimit < 0) {
//            // 暂停时已经逾期，计算逾期的总时长
//            timeLimit = calculateRemainingTimeLimit(Calendar.getInstance().getTime(), dueTime, enumTimingMode,
//                    timerEntity.getWorkTimePlanUuid());
//            // 最新的办理时限
//            timerEntity.setTimeLimit(Double.valueOf(timeLimit));
        }
        timerEntity.setStatus(EnumTimerStatus.STOP.getValue());
        this.save(timerEntity);

        // 记录日志
        timerLogService.log(timerEntity, EnumTimerLogType.END);
        return timeLimit;
    }

    /**
     * 重新开始计时器
     *
     * @param timerUuid
     * @return
     */
    @Override
    @Transactional
    public Date restartTimer(String timerUuid) {
        TsTimerEntity timerEntity = this.getOne(timerUuid);
        Double timeLimit = timerEntity.getTimeLimit();
        Date dueTime = timerEntity.getDueTime();
        Date currentTime = Calendar.getInstance().getTime();
        Date lastStartTime = timerEntity.getLastStartTime();
        if (currentTime.after(lastStartTime)) {
            lastStartTime = currentTime;
        }
        if (Integer.valueOf(EnumTimerStatus.PASUE.getValue()).equals(timerEntity.getStatus())
                || Integer.valueOf(EnumTimerStatus.STOP.getValue()).equals(timerEntity.getStatus())) {
            TimerConfig timerConfig = getTimerConfig(timerEntity,
                    timerConfigService.getOne(timerEntity.getConfigUuid()));
            timerConfig.setRuntimeTimingMode(getRuntimeTimingMode(timerConfig.getEnumTimingMode()));
            dueTime = calculateDueTime(timerEntity.getWorkTimePlanUuid(), lastStartTime, timeLimit, timerConfig,
                    timerEntity.getDueTime());
        }
        timerEntity.setLastStartTime(lastStartTime);
        timerEntity.setDueTime(dueTime);
        timerEntity.setStatus(EnumTimerStatus.STARTED.getValue());
        this.save(timerEntity);

        // 记录日志
        timerLogService.log(timerEntity, EnumTimerLogType.RESTART);
        return dueTime;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerService#getRemainingTimeLimit(java.lang.String)
     */
    @Override
    public double getRemainingTimeLimit(String timerUuid) {
        TsTimerEntity timerEntity = this.getOne(timerUuid);
        Date dueTime = timerEntity.getDueTime();
        String timingMode = timerEntity.getTimingMode();
        EnumTimingMode enumTimingMode = EnumTimingMode.getByValue(timingMode);
        double timeLimit = calculateRemainingTimeLimit(Calendar.getInstance().getTime(), dueTime, enumTimingMode,
                timerEntity.getWorkTimePlanUuid());
        return timeLimit;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerService#getTimeLimitNameInMinute(java.lang.String)
     */
    @Override
    public String getTimeLimitNameInMinute(String timerUuid) {
        String timeLimitName = "分钟";
        TsTimerEntity timerEntity = this.getOne(timerUuid);
        String timingMode = timerEntity.getTimingMode();
        EnumTimingMode enumTimingMode = EnumTimingMode.getByValue(timingMode);
        switch (enumTimingMode) {
            case WORKING_DAY:
            case WORKING_HOUR:
            case WORKING_MINUTE:
            case WORKING_DAY_24:
            case WORKING_HOUR_24:
            case WORKING_MINUTE_24:
                timeLimitName = "工作分钟";
                break;
            case DAY:
            case HOUR:
            case MINUTE:
                break;
            default:
                break;
        }
        return timeLimitName;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerService#changeTimeLimit(java.lang.String, int)
     */
    @Override
    public int changeTimeLimit(String timerUuid, int timeLimit) {
        TsTimerEntity timerEntity = this.getOne(timerUuid);
        TsTimerConfigEntity timerConfigEntity = timerConfigService.getOne(timerEntity.getConfigUuid());
        Date fromTime = Calendar.getInstance().getTime();
        TimerConfig timerConfig = getTimerConfig(timerEntity, timerConfigEntity);
        Double timeLimitAmount = converTimeLimitByTimeLimitUnitIfRequired(fromTime, timeLimit, timerConfig, timerConfigEntity,
                timerEntity.getWorkTimePlanUuid());
        EnumTimerStatus enumTimerStatus = EnumTimerStatus.getByValue(timerEntity.getStatus());
        switch (enumTimerStatus) {
            case READY:
                break;
            case STARTED:
                changeTimeLimitOfStartedTimer(timerEntity, timeLimitAmount.intValue());
                break;
            case PASUE:
                changeTimeLimitOfPauseTimer(timerEntity, timeLimitAmount.intValue());
                break;
            case STOP:
                changeTimeLimitOfStopTimer(timerEntity, timeLimitAmount.intValue());
                break;
            default:
                break;
        }
        return timerEntity.getTimeLimit().intValue();
    }

    /**
     * @param timerEntity
     * @param timeLimit
     */
    private void changeTimeLimitOfStartedTimer(TsTimerEntity timerEntity, int timeLimit) {
        String workTimePlanUuid = timerEntity.getWorkTimePlanUuid();
        Date fromTime = Calendar.getInstance().getTime();
        Date oldDueTime = timerEntity.getDueTime();
        Date dueTime = oldDueTime;
        TimerConfig timerConfig = getTimerConfig(timerEntity, timerConfigService.getOne(timerEntity.getConfigUuid()));
        int initLimitTime = timerEntity.getInitTimeLimit().intValue();
        EnumTimingMode enumTimingMode = EnumTimingMode.getByValue(timerEntity.getTimingMode());
        double remainingTimeLimit = calculateRemainingTimeLimit(fromTime, dueTime, enumTimingMode, workTimePlanUuid);
        int eraseTimeLimit = Double.valueOf(initLimitTime - remainingTimeLimit).intValue();
        // 变更的时限
        int newTimeLimit = timeLimit - eraseTimeLimit;
        // 新的到期时间
        dueTime = calculateDueTime(workTimePlanUuid, fromTime, newTimeLimit, timerConfig, oldDueTime);
        // 更新计时器
        timerEntity.setInitTimeLimit(Double.valueOf(timeLimit));
        timerEntity.setTimeLimit(Double.valueOf(remainingTimeLimit));
        timerEntity.setDueTime(dueTime);
        if (dueTime.after(Calendar.getInstance().getTime())) {
            timerEntity.setTimingState(EnumTimingState.NORMAL.getValue());
            timerEntity.setDueDoingDone(false);
            timerEntity.setOverDueDoingDone(false);
            timerEntity.setOverDueTime(null);
        }
        this.save(timerEntity);

        // 记录日志
        String remark = "计时器已启动，变更时限为[" + timeLimit + "]，旧的办理时限[" + DateUtils.formatDateTime(oldDueTime) + "]，新的办理时限["
                + DateUtils.formatDateTime(dueTime) + "]";
        timerLogService.log(timerEntity, EnumTimerLogType.INFO, remark);
    }

    /**
     * @param timerEntity
     * @param timeLimit
     */
    private void changeTimeLimitOfPauseTimer(TsTimerEntity timerEntity, int timeLimit) {
        // 旧的剩余办理时限
        int oldTaskLimitTime = timerEntity.getTimeLimit().intValue();
        // 变更时限
        changeTimeLimitOfPauseOrStopTimer(timerEntity, timeLimit);
        // 记录日志
        String remark = "计时器已暂停，变更时限为[" + timeLimit + "]，旧的剩余办理时限[" + oldTaskLimitTime + "]，新的剩余办理时限["
                + timerEntity.getTimeLimit() + "]";
        timerLogService.log(timerEntity, EnumTimerLogType.INFO, remark);
    }

    /**
     * @param timerEntity
     * @param timeLimit
     */
    private void changeTimeLimitOfStopTimer(TsTimerEntity timerEntity, int timeLimit) {
        // 旧的剩余办理时限
        int oldTaskLimitTime = timerEntity.getTimeLimit().intValue();
        // 变更时限
        changeTimeLimitOfPauseOrStopTimer(timerEntity, timeLimit);

        // 记录日志
        String remark = "计时器已暂停，变更时限为[" + timeLimit + "]，旧的剩余办理时限[" + oldTaskLimitTime + "]，新的剩余办理时限["
                + timerEntity.getTimeLimit() + "]";
        timerLogService.log(timerEntity, EnumTimerLogType.INFO, remark);
    }

    /**
     * @param timerEntity
     * @param timeLimit
     */
    private void changeTimeLimitOfPauseOrStopTimer(TsTimerEntity timerEntity, int timeLimit) {
        int initLimitTime = timerEntity.getInitTimeLimit().intValue();
        int remainingTimeLimit = timerEntity.getTimeLimit().intValue();
        // 变更的时限
        int changeLimitTimePart = timeLimit - initLimitTime;
        if (initLimitTime <= 0) {
            remainingTimeLimit = changeLimitTimePart;
        } else {
            remainingTimeLimit += changeLimitTimePart;
        }

        // 更新计时器
        timerEntity.setInitTimeLimit(Double.valueOf(timeLimit));
        timerEntity.setTimeLimit(Double.valueOf(remainingTimeLimit));
        this.save(timerEntity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerService#changeDueTime(java.lang.String, java.util.Date)
     */
    @Override
    public int changeDueTime(String timerUuid, Date dueTime) {
        TsTimerEntity timerEntity = this.getOne(timerUuid);
        EnumTimerStatus enumTimerStatus = EnumTimerStatus.getByValue(timerEntity.getStatus());
        switch (enumTimerStatus) {
            case READY:
                break;
            case STARTED:
                changeDueTimeOfStartedTimer(timerEntity, dueTime);
                break;
            case PASUE:
                changeDueTimeOfPauseTimer(timerEntity, dueTime);
                break;
            case STOP:
                changeDueTimeOfStopTimer(timerEntity, dueTime);
                break;
            default:
                break;
        }
        return this.getOne(timerUuid).getTimeLimit().intValue();
    }

    /**
     * @param timerEntity
     * @param dueTime
     */
    private void changeDueTimeOfStartedTimer(TsTimerEntity timerEntity, Date dueTime) {
        TimerConfig timerConfig = getTimerConfig(timerEntity, timerConfigService.getOne(timerEntity.getConfigUuid()));
        Date oldDueTime = timerEntity.getDueTime();
        // 变更的到期时间
        Date newDueTime = convertTime(dueTime, timerConfig.getEnumTimingMode());

        // 更新计时器
        timerEntity.setDueTime(newDueTime);
        timerEntity.setInitDueTime(dueTime);
        if (newDueTime.after(Calendar.getInstance().getTime())) {
            timerEntity.setTimingState(EnumTimingState.NORMAL.getValue());
            timerEntity.setDueDoingDone(false);
            timerEntity.setOverDueDoingDone(false);
            timerEntity.setOverDueTime(null);
        }
        this.save(timerEntity);

        // 记录日志
        String remark = "计时器已启动，变更到期时间为[" + DateUtils.formatDateTime(dueTime) + "]，旧的办理时限["
                + DateUtils.formatDateTime(oldDueTime) + "]，新的办理时限[" + DateUtils.formatDateTime(newDueTime) + "]";
        timerLogService.log(timerEntity, EnumTimerLogType.INFO, remark);
    }

    /**
     * @param timerEntity
     * @param dueTime
     */
    private void changeDueTimeOfPauseTimer(TsTimerEntity timerEntity, Date dueTime) {
        Double oldTimeLimit = timerEntity.getTimeLimit();

        // 变更时限
        changeDueTimeOfPauseOrStopTimer(timerEntity, dueTime);

        // 记录日志
        String remark = "计时器已暂停，变更到期时间为[" + DateUtils.formatDateTime(dueTime) + "]，旧的剩余办理时限[" + oldTimeLimit
                + "]，新的剩余办理时限[" + timerEntity.getTimeLimit() + "]";
        timerLogService.log(timerEntity, EnumTimerLogType.INFO, remark);
    }

    /**
     * @param timerEntity
     * @param dueTime
     */
    private void changeDueTimeOfStopTimer(TsTimerEntity timerEntity, Date dueTime) {
        Double oldTimeLimit = timerEntity.getTimeLimit();

        // 变更时限
        changeDueTimeOfPauseOrStopTimer(timerEntity, dueTime);

        // 记录日志
        String remark = "计时器已停止，变更到期时间为[" + DateUtils.formatDateTime(dueTime) + "]，旧的剩余办理时限[" + oldTimeLimit
                + "]，新的剩余办理时限[" + timerEntity.getTimeLimit() + "]";
        timerLogService.log(timerEntity, EnumTimerLogType.INFO, remark);
    }

    private void changeDueTimeOfPauseOrStopTimer(TsTimerEntity timerEntity, Date dueTime) {
        TimerConfig timerConfig = getTimerConfig(timerEntity, timerConfigService.getOne(timerEntity.getConfigUuid()));
        // 剩余办理时限
        double remainingTimeLimit = calculateRemainingTimeLimit(Calendar.getInstance().getTime(), dueTime,
                timerConfig.getEnumTimingMode(), timerEntity.getWorkTimePlanUuid());
        // 变更的到期时间
        Date newDueTime = convertTime(dueTime, timerConfig.getEnumTimingMode());

        // 更新计时器
        timerEntity.setTimeLimit(remainingTimeLimit);
        timerEntity.setDueTime(newDueTime);
        timerEntity.setInitDueTime(dueTime);
        if (newDueTime.after(Calendar.getInstance().getTime())) {
            timerEntity.setTimingState(EnumTimingState.NORMAL.getValue());
            timerEntity.setDueDoingDone(false);
            timerEntity.setOverDueDoingDone(false);
            timerEntity.setOverDueTime(null);
        }
        this.save(timerEntity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerService#getOverDueTime(com.wellsoft.pt.timer.entity.TsTimerEntity)
     */
    @Override
    public Date getOverDueTime(TsTimerEntity timerEntity) {
        Date deadlineTime = timerEntity.getDueTime();
        TsTimerConfigEntity timerConfigEntity = timerConfigService.getOne(timerEntity.getConfigUuid());
        TimerConfig timerConfig = null;
        if (timerConfigEntity != null) {
            timerConfig = getTimerConfig(timerEntity, timerConfigEntity);
        } else {
            timerConfig = new TimerConfig();
            timerConfig.setWorkTimePlanUuid(timerEntity.getWorkTimePlanUuid());
            timerConfig.setTimingMode(EnumTimingMode.MINUTE.getValue());
        }
        return getOverDueTime(deadlineTime, timerConfig);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerService#calculateTime(java.lang.String, java.util.Date, int, java.lang.String)
     */
    @Override
    public Date calculateTime(String timerUuid, Date fromTime, double amount, String timingMode) {
        TsTimerEntity timerEntity = this.getOne(timerUuid);
        TsTimerConfigEntity timerConfigEntity = timerConfigService.getOne(timerEntity.getConfigUuid());
        TimerConfig timerConfig = getTimerConfig(timerEntity, timerConfigEntity);
        if (StringUtils.isNotBlank(timingMode)) {
            timerConfig.setTimingMode(timingMode);
            timerConfig.setRuntimeTimingMode(timingMode);
        }

        // 转换输入时间的单位转化为计时方式的值
        TsTimerConfigDto timerConfigDto = new TsTimerConfigDto();
        BeanUtils.copyProperties(timerConfigEntity, timerConfigDto);
        timerConfigDto.setTimeLimitType(EnumTimeLimitType.NUMBER.getValue());
        timerConfigDto.setTimingMode(timerConfig.getTimingMode());
        timerConfigDto.setTimeLimitUnit(getLimitUnitByTimingMode(EnumTimingMode.getByValue(timerConfig.getTimingMode())));
        double timeLimitAmount = converTimeLimitByTimeLimitUnitIfRequired(fromTime, amount, timerConfig, timerConfigDto, timerEntity.getWorkTimePlanUuid());
        return calculateTime(timerEntity.getWorkTimePlanUuid(), fromTime, timeLimitAmount, timerConfig);
    }

    /**
     * @param enumTimingMode
     * @return
     */
    private String getLimitUnitByTimingMode(EnumTimingMode enumTimingMode) {
        String timeLimitUnit = EnumTimeLimitUnit.Minute.getValue();
        switch (enumTimingMode) {
            case WORKING_DAY:
            case WORKING_DAY_24:
            case DAY:
                timeLimitUnit = EnumTimeLimitUnit.Day.getValue();
                break;
            case WORKING_HOUR:
            case WORKING_HOUR_24:
            case HOUR:
                timeLimitUnit = EnumTimeLimitUnit.Hour.getValue();
            default:
                break;
        }
        return timeLimitUnit;
    }

    /**
     * @param fromTime
     * @param timerConfig
     * @return
     */
    private Date includeStartTimePointOfLimitTimeIfRequired(Date fromTime, TimerConfig timerConfig) {
        boolean includeStartTimePointOfLimitTime = isIncludeStartTimePointOfLimitTime(timerConfig);
        if (!includeStartTimePointOfLimitTime) {
            return fromTime;
        }
        // 从当前时间点开始计时
        if (timerConfig.isIncludeStartTimePoint()) {
            return fromTime;
        }
        // 从下一时间点开始计时
        return getNextTimePoint(fromTime, timerConfig);
    }

    /**
     * @param fromTime
     * @param timerConfig
     * @return
     */
    private Date getNextTimePoint(Date fromTime, TimerConfig timerConfig) {
        TsWorkTimePlanFacadeService workTimePlanFacadeService = ApplicationContextHolder
                .getBean(TsWorkTimePlanFacadeService.class);
        EnumTimingMode enumTimingMode = timerConfig.getEnumTimingMode();
        String workTimePlanUuid = timerConfig.getWorkTimePlanUuid();
        Calendar calendar = Calendar.getInstance();
        switch (enumTimingMode) {
            case WORKING_DAY:
                // 工作日(工作时间)
            case WORKING_DAY_24:
                // 工作日(24小时制)
                calendar.setTime(workTimePlanFacadeService.getNextWorkDate(workTimePlanUuid, fromTime));
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            case DAY:
                // 天
                calendar.setTime(fromTime);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            case WORKING_HOUR:
                // 工作小时
                WorkTime workTimeHour = workTimePlanFacadeService.getWorkTime(workTimePlanUuid, fromTime);
                // 当前时间在工作时间内取下一工作时间，否则取下一工作时间开始时间
                if (workTimeHour.isInTimePeriod(fromTime)) {
                    Date workDate = workTimePlanFacadeService.getWorkDate(workTimePlanUuid, fromTime, 1,
                            WorkUnit.WorkingHour);
                    calendar.setTime(workDate);
                } else if (workTimeHour.isBeforeEndTime(fromTime)) {
                    // 在时间段之间的时间点，取下一时间段的开始时间
                    calendar.setTime(workTimeHour.getNextFromTimeBetweenTimePeriod(fromTime));
                } else {
                    Date nextWorkDate = workTimePlanFacadeService.getNextWorkDate(workTimePlanUuid, fromTime);
                    WorkTime nextWorkTime = workTimePlanFacadeService.getWorkTime(workTimePlanUuid, nextWorkDate);
                    calendar.setTime(nextWorkTime.getFromTime());
                }
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            case WORKING_HOUR_24:
                // 工作小时(24小时制)
                calendar.setTime(
                        workTimePlanFacadeService.getWorkDate(workTimePlanUuid, fromTime, 1, WorkUnit.WorkingHourOf24Hour));
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            case HOUR:
                // 小时
                calendar.setTime(fromTime);
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            case WORKING_MINUTE:
                // 工作分钟(工作时间)
                WorkTime workTimeMinute = workTimePlanFacadeService.getWorkTime(workTimePlanUuid, fromTime);
                // 当前时间在工作时间内取下一工作时间，否则取下一工作时间开始时间
                if (workTimeMinute.isInTimePeriod(fromTime)) {
                    calendar.setTime(
                            workTimePlanFacadeService.getWorkDate(workTimePlanUuid, fromTime, 1, WorkUnit.WorkingHour));
                } else {
                    Date nextWorkDate = workTimePlanFacadeService.getNextWorkDate(workTimePlanUuid, fromTime);
                    WorkTime nextWorkTime = workTimePlanFacadeService.getWorkTime(workTimePlanUuid, nextWorkDate);
                    calendar.setTime(nextWorkTime.getFromTime());
                }
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            case WORKING_MINUTE_24:
                // 工作小时(24小时制)
                calendar.setTime(workTimePlanFacadeService.getWorkDate(workTimePlanUuid, fromTime, 1,
                        WorkUnit.WorkingMinuteOf24Hour));
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            case MINUTE:
                // 分钟
                calendar.setTime(fromTime);
                calendar.add(Calendar.MINUTE, 1);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            default:
                break;
        }
        return calendar.getTime();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerService#getToScheduleOfDueTimers()
     */
    @Override
    public List<TsTimerEntity> getToScheduleOfDueTimers() {
        Calendar calendar = Calendar.getInstance();
        // 截止时间
        calendar.add(Calendar.SECOND, 120);
        Date deadlineTime = calendar.getTime();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("status", EnumTimerStatus.STARTED.getValue());
        values.put("timingStates", Lists.newArrayList(EnumTimingState.NORMAL.getValue(), EnumTimingState.ALARM.getValue()));
        values.put("deadlineTime", deadlineTime);
        return this.listByHQL(GET_DUE_TASK_TIMERS, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerService#getToScheduleOfOverDueTimers()
     */
    @Override
    public List<TsTimerEntity> getToScheduleOfOverDueTimers() {
        Calendar calendar = Calendar.getInstance();
        // 截止时间
        calendar.add(Calendar.SECOND, 120);
        Date deadlineTime = calendar.getTime();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("status", EnumTimerStatus.STARTED.getValue());
        values.put("timingState", EnumTimingState.DUE.getValue());
        values.put("deadlineTime", deadlineTime);
        return this.listByHQL(GET_OVERDUE_TASK_TIMERS, values);
    }

    @Override
    public List<TsTimerEntity> getToScheduleOfAlarmTimers() {
        String hql = "from TsTimerEntity t1 where t1.status = :status and t1.enableAlarm = true and not exists(select t2.uuid from TsTimerAlarmEntity t2 where t2.deleteStatus = :deleteStatus and t2.timerUuid = t1.uuid)";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("status", EnumTimerStatus.STARTED.getValue());
        values.put("deleteStatus", 0);
        return this.listByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerService#dueDoing(java.lang.String)
     */
    @Override
    @Transactional
    public void dueDoing(String timerUuid, Date dueTime) {
        TsTimerEntity timerEntity = this.getOne(timerUuid);
        // 标记到期信息
        markDueInfo(timerEntity);
        this.save(timerEntity);

        // 计时监听器
        String listener = timerEntity.getListener();
        if (StringUtils.isNotBlank(listener)) {
            TimerEvent timerEvent = getTimerEvent(timerEntity, null);
            String[] listeners = StringUtils.split(listener, Separator.SEMICOLON.getValue());
            for (String l : listeners) {
                TimerListener timerListener = listenerMap.get(l);
                if (timerListener == null) {
                    continue;
                }
                timerListener.onTimerDue(timerEvent);
            }
        }
        // 记录日志
        timerLogService.log(timerEntity, EnumTimerLogType.DUE_DOING);
    }

    /**
     * @param timerEntity
     */
    private void markDueInfo(TsTimerEntity timerEntity) {
        timerEntity.setDueDoingDone(true);
        timerEntity.setTimingState(EnumTimingState.DUE.getValue());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerService#forceMarkDueInfo(java.lang.String)
     */
    @Override
    @Transactional
    public void forceMarkDueInfo(String timerUuid, String remark) {
        TsTimerEntity timerEntity = this.getOne(timerUuid);
        // 标记到期信息
        markDueInfo(timerEntity);
        this.save(timerEntity);
        // 记录日志
        timerLogService.log(timerEntity, EnumTimerLogType.FORCE_STOP_DUE_DOING, remark);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerService#overDueDoing(java.lang.String, java.util.Date)
     */
    @Override
    @Transactional
    public void overDueDoing(String timerUuid, Date overdueTime) {
        TsTimerEntity timerEntity = this.getOne(timerUuid);
        timerEntity.setOverDueTime(overdueTime);
        markOverDueInfo(timerEntity);
        this.save(timerEntity);

        // 计时监听器
        String listener = timerEntity.getListener();
        if (StringUtils.isNotBlank(listener)) {
            TimerEvent timerEvent = getTimerEvent(timerEntity, null);
            String[] listeners = StringUtils.split(listener, Separator.SEMICOLON.getValue());
            for (String l : listeners) {
                TimerListener timerListener = listenerMap.get(l);
                if (timerListener == null) {
                    continue;
                }
                timerListener.onTimerOverDue(timerEvent);
            }
        }
        // 记录日志
        timerLogService.log(timerEntity, EnumTimerLogType.OVER_DUE_DOING);
    }

    /**
     * @param timerEntity
     */
    private void markOverDueInfo(TsTimerEntity timerEntity) {
        timerEntity.setOverDueDoingDone(true);
        timerEntity.setTimingState(EnumTimingState.OVER_DUE.getValue());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.service.TsTimerService#forceMarkOverDueInfo(java.lang.String)
     */
    @Override
    @Transactional
    public void forceMarkOverDueInfo(String timerUuid, Date overdueTime, String remark) {
        TsTimerEntity timerEntity = this.getOne(timerUuid);
        // 标记逾期信息
        timerEntity.setOverDueTime(overdueTime);
        markOverDueInfo(timerEntity);
        this.save(timerEntity);
        // 记录日志
        timerLogService.log(timerEntity, EnumTimerLogType.FORCE_STOP_OVER_DUE_DOING, remark);
    }

    @Override
    @Transactional
    public void alarmDoing(String timerUuid, String alarmUuid, Date alarmTime) {
        TsTimerAlarmEntity alarmEntity = timerAlarmService.getOne(Long.valueOf(alarmUuid));
        TsTimerEntity timerEntity = this.getOne(timerUuid);
        timerEntity.setTimingState(EnumTimingState.ALARM.getValue());
        this.save(timerEntity);

        // 计时状态中
        if (Integer.valueOf(EnumTimerStatus.STARTED.getValue()).equals(timerEntity.getStatus())) {
            // 计时监听器
            String listener = timerEntity.getListener();
            if (StringUtils.isNotBlank(listener)) {
                TimerEvent timerEvent = getTimerEvent(timerEntity, alarmEntity);
                String[] listeners = StringUtils.split(listener, Separator.SEMICOLON.getValue());
                for (String l : listeners) {
                    TimerListener timerListener = listenerMap.get(l);
                    if (timerListener == null) {
                        continue;
                    }
                    timerListener.onTimerAlarm(timerEvent);
                }
            }
        } else {
            // 不在计时状态的计时器，删除预警
            alarmEntity.setDeleteStatus(1);
        }

        markAlarmInfo(alarmEntity);
        timerAlarmService.save(alarmEntity);
        // 记录日志
        timerLogService.log(timerEntity, EnumTimerLogType.ALARM_DOING, alarmUuid);
    }

    /**
     * @param timerEntity
     * @param alarmEntity
     * @return
     */
    private TimerEvent getTimerEvent(TsTimerEntity timerEntity, TsTimerAlarmEntity alarmEntity) {
        String timerUuid = timerEntity.getUuid();

        Integer timerStatus = timerEntity.getStatus();
        Integer timingState = timerEntity.getTimingState();
        Date dueTime = timerEntity.getDueTime();
        Date overdueTime = timerEntity.getOverDueTime();
        String timerData = timerEntity.getTimerData();

        String alarmUuid = alarmEntity != null ? alarmEntity.getUuid() + StringUtils.EMPTY : null;
        String alarmId = alarmEntity != null ? alarmEntity.getId() : null;
        Date alarmTime = alarmEntity != null ? alarmEntity.getAlarmTime() : null;
        return new TimerEvent(timerUuid, timerStatus, timingState, dueTime, overdueTime, timerData, alarmUuid, alarmId, alarmTime);
    }

    /**
     * @param alarmEntity
     */
    private void markAlarmInfo(TsTimerAlarmEntity alarmEntity) {
        alarmEntity.setCurrentAlarmCount(alarmEntity.getCurrentAlarmCount() + 1);
        if (alarmEntity.getCurrentAlarmCount() >= alarmEntity.getTotalAlarmCount()) {
            alarmEntity.setAlarmDoingDone(true);
        }
    }

    @Override
    @Transactional
    public void forceMarkAlarmInfo(String timerUuid, String alarmUuid) {
        TsTimerAlarmEntity alarmEntity = timerAlarmService.getOne(Long.valueOf(alarmUuid));
        markAlarmInfo(alarmEntity);
        timerAlarmService.save(alarmEntity);

        TsTimerEntity timerEntity = this.getOne(timerUuid);
        // 记录日志
        timerLogService.log(timerEntity, EnumTimerLogType.FORCE_STOP_ALARM_DOING, alarmUuid);
    }

    /**
     * @param timerUuid
     * @return
     */
    @Override
    public TimerWorkTime getTimerWorkTime(String timerUuid) {
        TsTimerEntity timerEntity = this.dao.getOne(timerUuid);
        Date currentTime = Calendar.getInstance().getTime();
        if (timerEntity == null) {
            return new TimerWorkTime(timerUuid, 0d, currentTime, currentTime, 0);
        }

        TimerWorkTime timerWorkTime = null;
        EnumTimerStatus timerStatus = EnumTimerStatus.getByValue(timerEntity.getStatus());
        switch (timerStatus) {
            case READY:
                timerWorkTime = new TimerWorkTime(timerUuid, timerEntity.getInitTimeLimit(), currentTime, currentTime, 0);
                break;
            case STARTED:
                Date fromTime = currentTime;
                Date lastStartTime = timerEntity.getLastStartTime();
                if (fromTime.before(lastStartTime)) {
                    fromTime = lastStartTime;
                }
                Date toTime = timerEntity.getDueTime();
                Double currentUsedTimeLimit = timerEntity.getInitTimeLimit() - calculateRemainingTimeLimit(fromTime,
                        toTime, EnumTimingMode.getByValue(timerEntity.getTimingMode()),
                        timerEntity.getWorkTimePlanUuid());
                timerWorkTime = new TimerWorkTime(timerUuid, timerEntity.getInitTimeLimit(), timerEntity.getStartTime(), currentTime, currentUsedTimeLimit.intValue());
                break;
            case PASUE:
            case STOP:
                EnumTimerLogType logType = EnumTimerStatus.PASUE.equals(timerStatus) ? EnumTimerLogType.PAUSE : EnumTimerLogType.END;
                TsTimerLogEntity logEntity = timerLogService.getByTimerUuidAndType(timerEntity.getUuid(), logType);
                Date endTime = timerEntity.getModifyTime();
                if (logEntity != null) {
                    endTime = logEntity.getLogTime();
                }
                Double usedTimeLimit = timerEntity.getInitTimeLimit() - timerEntity.getTimeLimit();
                timerWorkTime = new TimerWorkTime(timerUuid, timerEntity.getInitTimeLimit(), timerEntity.getStartTime(), endTime, usedTimeLimit.intValue());
                break;
        }
        return timerWorkTime;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.support.TsTimerConfigUsedChecker#isTimerConfigUsed(java.lang.String)
     */
    @Override
    public boolean isTimerConfigUsed(String configUuid) {
        TsTimerEntity entity = new TsTimerEntity();
        entity.setConfigUuid(configUuid);
        return this.dao.countByEntity(entity) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.support.WorkTimePlanUsedChecker#isWorkTimePlanUsed(java.lang.String)
     */
    @Override
    public boolean isWorkTimePlanUsed(String workTimePlanUuid) {
        TsTimerEntity entity = new TsTimerEntity();
        entity.setWorkTimePlanUuid(workTimePlanUuid);
        return this.dao.countByEntity(entity) > 0;
    }

}
