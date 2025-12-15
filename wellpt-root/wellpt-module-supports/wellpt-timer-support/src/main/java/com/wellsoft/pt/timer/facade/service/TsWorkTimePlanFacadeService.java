/*
 * @(#)2021年5月25日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.timer.dto.TsHolidayScheduleDto;
import com.wellsoft.pt.timer.dto.TsWorkTimePlanDto;
import com.wellsoft.pt.timer.dto.TsWorkTimePlanHistoryDto;
import com.wellsoft.pt.timer.support.WorkTime;
import com.wellsoft.pt.timer.support.WorkTimePeriod;
import com.wellsoft.pt.timer.support.WorkTimePlan;
import com.wellsoft.pt.timer.support.WorkTimes;

import java.util.Date;
import java.util.List;
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
 * 2021年5月25日.1	zhulh		2021年5月25日		Create
 * </pre>
 * @date 2021年5月25日
 */
public interface TsWorkTimePlanFacadeService extends Facade {

    /**
     * 获取系统当前时间
     *
     * @return
     */
    Date getSysDate();

    /**
     * @param name
     * @return
     */
    List<TsWorkTimePlanDto> getAllBySystemUnitIdsLikeName(String name);

    /**
     * 根据UUID将工作时间方案设置为默认方案
     *
     * @param uuid
     */
    void setAsDefaultByUuid(String uuid);

    /**
     * 获取工作时间方案
     *
     * @param uuid
     * @return
     */
    TsWorkTimePlanDto getDto(String uuid);

    /**
     * @param historyUuid
     * @return
     */
    TsWorkTimePlanHistoryDto getHistoryDto(String historyUuid);

    /**
     * @param uuids
     * @return
     */
    Map<String, Object> listNewVersionTipByUuids(List<String> uuids);

    /**
     * @param uuid
     * @return
     */
    String getMaxVersionByUuid(String uuid);

    /**
     * 保存工作时间方案
     *
     * @param workTimePlanDto
     * @return
     */
    String saveDto(TsWorkTimePlanDto workTimePlanDto);

    /**
     * @param holidayScheduleDto
     */
    void autoUpdateByHolidaySchedule(TsHolidayScheduleDto holidayScheduleDto);

    /**
     * 保存工作时间方案为新版本
     *
     * @param workTimePlanDto
     * @return
     */
    String saveAsNewVersion(TsWorkTimePlanDto workTimePlanDto);

    /**
     * 删除工作时间方案
     *
     * @param uuids
     */
    void deleteAll(List<String> uuids);

    /**
     * 返回有效的工作时间点
     *
     * @param uuid
     * @param fromDate
     * @param amount
     * @param workUnit
     * @return
     */
    Date getWorkDate(String uuid, Date fromDate, double amount, WorkUnit workUnit);

    /**
     * 返回有效的工作时间点
     *
     * @param uuid
     * @param fromDate
     * @param amount
     * @param workUnit
     * @param autoDelay
     * @return
     */
    Date getWorkDate(String uuid, Date fromDate, double amount, WorkUnit workUnit, boolean autoDelay);

    /**
     * 自动推迟工作日
     *
     * @param workDate
     * @param workUnit
     * @param workTimePlanUuid
     * @return
     */
    Date autoDelayWorkDateIfRequired(Date workDate, WorkUnit workUnit, String workTimePlanUuid);

    /**
     * 返回下一工作日的工作时间点
     *
     * @param uuid
     * @param fromDate
     * @return
     */
    Date getNextWorkDate(String uuid, Date fromDate);

    /**
     * 返回前一工作日的工作时间点
     *
     * @param uuid
     * @param fromDate
     * @return
     */
    Date getPrevWorkDate(String uuid, Date fromDate);

    /**
     * @param uuid
     * @param date
     * @return
     */
    boolean isWorkDay(String uuid, Date date);

    /**
     * @param uuid
     * @param date
     * @return
     */
    boolean isWorkHour(String uuid, Date date);

    /**
     * @param date
     * @return
     */
    WorkTime getWorkTime(String uuid, Date date);

    /**
     * 根据工作时间方案UUID、年份，获取对应年份工作时间方案
     *
     * @param uuid
     * @param year
     * @return
     */
    WorkTimePlan getWorkTimePlan(String uuid, int year);

    /**
     * @param uuids
     * @return
     */
    ResultMessage isUsedByUuids(List<String> uuids);

    /**
     * @param uuid
     * @param fromDate
     * @param amount
     * @return
     */
    WorkTimes getWorkTimes(String uuid, Date fromDate, int amount);

    /**
     * @param uuid
     * @param fromTime
     * @param toTime
     * @return
     */
    WorkTimePeriod getWorkTimePeriod(String uuid, Date fromTime, Date toTime);

    /**
     * @return
     */
    TsWorkTimePlanDto getDefaultWorkTimePlan();

    /**
     * @param workTimePlanUuid
     * @param fromDate
     * @param amount
     * @return
     */
    double getTotalWorkTimeMinutes(String workTimePlanUuid, Date fromDate, double amount);

    /**
     * 根据计时方案ID获取有效的UUID
     *
     * @param workTimePlanId
     * @param defaultWorkTimePlanUuid
     * @return
     */
    String getActiveWorkTimePlanUuidById(String workTimePlanId, String defaultWorkTimePlanUuid);

    List<TsWorkTimePlanDto> getAllBySystem(List<String> system);

}
