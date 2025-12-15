/*
 * @(#)2021年6月2日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.support;

import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService;
import org.apache.commons.lang.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年6月2日.1	zhulh		2021年6月2日		Create
 * </pre>
 * @date 2021年6月2日
 */
public class WorkTimeContext extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5163036939890220530L;

    private String workTimePlanUuid;

    private Map<Integer, WorkTimePlan> workTimePlanMap = Maps.newHashMap();

    private Map<String, String> holidayMap = Maps.newHashMap();

    private Map<String, String> makeupDateMap = Maps.newHashMap();

    private Map<String, String> workTimePlanDateMap = Maps.newHashMap();

    private Map<String, String> workDateMap = Maps.newHashMap();

    /**
     * @param workTimePlan
     */
    public WorkTimeContext(WorkTimePlan workTimePlan) {
        this.workTimePlanUuid = workTimePlan.getWorkTimePlanUuid();
        workTimePlanMap.put(workTimePlan.getYear(), workTimePlan);
    }

    /**
     * @return the workTimePlan
     */
    public WorkTimePlan getWorkTimePlan(Date date) {
        return getWorkTimePlan(DateUtils.getCalendar(date).get(Calendar.YEAR));
    }

    /**
     * @return the workTimePlan
     */
    public WorkTimePlan getWorkTimePlan(int year) {
        WorkTimePlan workTimePlan = workTimePlanMap.get(year);
        if (workTimePlan == null) {
            TsWorkTimePlanFacadeService workTimePlanFacadeService = ApplicationContextHolder.getBean(TsWorkTimePlanFacadeService.class);
            workTimePlan = workTimePlanFacadeService.getWorkTimePlan(workTimePlanUuid, year);
            workTimePlanMap.put(year, workTimePlan);
        }
        return workTimePlan;
    }

    /**
     * @param date
     * @return
     */
    public boolean isMakupDate(Date date) {
        String dateString = DateUtils.formatDate(date);
        return this.makeupDateMap.containsKey(dateString);
    }

    /**
     * @param makeupDate
     * @param workDate
     */
    public void addMakeupDate(String makeupDate, String workDate) {
        this.makeupDateMap.put(makeupDate, workDate);
    }

    /**
     * @param date
     * @return
     */
    public Date getDateOfMakeuped(Date date) {
        String dateString = DateUtils.formatDate(date);
        String dateOfMakeuped = this.makeupDateMap.get(dateString);
        if (StringUtils.isBlank(dateOfMakeuped)) {
            return date;
        }
        String[] dates = StringUtils.split(dateOfMakeuped, "-");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, Integer.valueOf(dates[0]));
        calendar.set(Calendar.MONTH, Integer.valueOf(dates[1]) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dates[2]));
        return calendar.getTime();
    }

    /**
     * @param date
     * @return
     */
    public boolean isScheduleHoliday(Date date) {
        String dateString = DateUtils.formatDate(date);
        return this.holidayMap.containsKey(dateString);
    }

    /**
     * @param date
     */
    public void addScheduleHoliday(Date date) {
        String dateString = DateUtils.formatDate(date);
        this.holidayMap.put(dateString, dateString);
    }

    /**
     * 如何描述该方法
     *
     * @param date
     * @return
     */
    public boolean isWorkTimePlanDate(Date date) {
        String dateString = DateUtils.formatDate(date);
        return this.workTimePlanDateMap.containsKey(dateString);
    }

    /**
     * @param date
     */
    public void addWorkTimePlanDate(Date date) {
        String dateString = DateUtils.formatDate(date);
        this.workTimePlanDateMap.put(dateString, dateString);
    }

    /**
     * @param date
     */
    public boolean isWorkDate(Date date) {
        String dateString = DateUtils.formatDate(date);
        return this.workDateMap.containsKey(dateString);
    }

    /**
     * @param date
     */
    public void addWorkDate(Date date) {
        String dateString = DateUtils.formatDate(date);
        this.workDateMap.put(dateString, dateString);
    }

}
