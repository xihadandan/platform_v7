/*
 * @(#)2021年6月1日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.timer.enums.EnumPeriodType;
import com.wellsoft.pt.timer.enums.EnumWorkTimeType;
import com.wellsoft.pt.timer.support.WorkTimeScheduleConfig.WorkTimeConfig;

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
 * 2021年6月1日.1	zhulh		2021年6月1日		Create
 * </pre>
 * @date 2021年6月1日
 */
public class WorkTimeScheduleInstanceParser {

    /**
     * @param year
     * @param configs
     * @return
     */
    public static List<WorkTimeScheduleInstance> parse(int year, Collection<WorkTimeScheduleConfig> configs) {
        List<WorkTimeScheduleInstance> instances = Lists.newArrayList();
        for (WorkTimeScheduleConfig workTimeScheduleConfig : configs) {
            try {
                instances.add(parse(year, workTimeScheduleConfig));
            } catch (ParseException e) {
                throw new BusinessException(e);
            }
        }
        Collections.sort(instances);
        return instances;
    }

    /**
     * @param year
     * @param workTimeScheduleConfig
     * @return
     * @throws ParseException
     */
    private static WorkTimeScheduleInstance parse(int year, WorkTimeScheduleConfig workTimeScheduleConfig)
            throws ParseException {
        // 开始时间
        Date fromDate = null;
        // 结束时间
        Date toDate = null;
        // 应用时间周期
        // 全年
        if (EnumPeriodType.AllYear.getValue().equals(workTimeScheduleConfig.getPeriodType())) {
            fromDate = DateUtils.parseDate(year + "-01-01");
            toDate = DateUtils.parseDate(year + "-12-31");
        } else {
            fromDate = DateUtils.parseDate(year + "-" + workTimeScheduleConfig.getFromDate());
            toDate = DateUtils.parseDate(year + "-" + workTimeScheduleConfig.getToDate());
        }
        // 到下一年
        if (workTimeScheduleConfig.isToNextYeay()) {
            toDate = org.apache.commons.lang.time.DateUtils.addYears(toDate, year);
        }
        // 类型1固定工时、2单双周、3弹性工时
        EnumWorkTimeType workTimeType = EnumWorkTimeType.getByValue(workTimeScheduleConfig.getWorkTimeType());
        // 每周工作时长
        String workHoursPerWeek = workTimeScheduleConfig.getWorkHoursPerWeek();
        // true/false, 设置核心工作日
        boolean coreWorkDay = workTimeScheduleConfig.isCoreWorkDay();
        // 工作时间
        List<WorkTimeConfig> workTimes = workTimeScheduleConfig.getWorkTimes();

        WorkTimeScheduleInstance instance = new WorkTimeScheduleInstance();
        instance.setFromDate(fromDate);
        instance.setToDate(toDate);
        instance.setWorkTimeType(workTimeType);
        instance.setWorkHoursPerWeek(workHoursPerWeek);
        instance.setCoreWorkDay(coreWorkDay);
        instance.setWorkTimes(workTimes);
        return instance;
    }

}
