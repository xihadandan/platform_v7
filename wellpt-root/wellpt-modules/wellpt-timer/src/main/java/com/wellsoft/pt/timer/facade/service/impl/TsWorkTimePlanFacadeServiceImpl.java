/*
 * @(#)2021年5月25日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.cache.Cache;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.dto.TsHolidayScheduleDto;
import com.wellsoft.pt.timer.dto.TsWorkTimePlanDto;
import com.wellsoft.pt.timer.dto.TsWorkTimePlanHistoryDto;
import com.wellsoft.pt.timer.entity.TsWorkTimePlanEntity;
import com.wellsoft.pt.timer.entity.TsWorkTimePlanHistoryEntity;
import com.wellsoft.pt.timer.enums.EnumWorkTimePlanStatus;
import com.wellsoft.pt.timer.enums.EnumWorkTimeType;
import com.wellsoft.pt.timer.facade.service.TsHolidayScheduleFacadeService;
import com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService;
import com.wellsoft.pt.timer.service.TsWorkTimePlanHistoryService;
import com.wellsoft.pt.timer.service.TsWorkTimePlanService;
import com.wellsoft.pt.timer.support.*;
import com.wellsoft.pt.timer.support.WorkTimePlan.HolidaySchedule;
import com.wellsoft.pt.timer.support.WorkTimeScheduleConfig.TimePeriodConfig;
import com.wellsoft.pt.timer.support.WorkTimeScheduleConfig.WorkTimeConfig;
import net.sf.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
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
 * 2021年5月25日.1	zhulh		2021年5月25日		Create
 * </pre>
 * @date 2021年5月25日
 */
@Service
public class TsWorkTimePlanFacadeServiceImpl implements TsWorkTimePlanFacadeService {

    private static final String[] DAY_OF_WEEKS = new String[]{"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
    private Logger logger = LoggerFactory.getLogger(getClass());

    // 版本格式
    // private DecimalFormat versionFormat = new DecimalFormat("0.0");
    @Autowired
    private TsHolidayScheduleFacadeService holidayScheduleFacadeService;

    @Autowired
    private TsWorkTimePlanService workTimePlanService;

    @Autowired
    private TsWorkTimePlanHistoryService workTimePlanHistoryService;

    @Autowired
    private List<TsWorkTimePlanUsedChecker> workTimePlanUsedCheckers;

    @Autowired
    private CacheManager cacheManager;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#getSysDate(java.lang.String)
     */
    @Override
    public Date getSysDate() {
        return workTimePlanService.getSysDate();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#getAllBySystemUnitIdsLikeName(java.lang.String)
     */
    @Override
    public List<TsWorkTimePlanDto> getAllBySystemUnitIdsLikeName(String name) {
        List<String> systemUnitIds = new ArrayList<String>();
        systemUnitIds.add(MultiOrgSystemUnit.PT_ID);
        systemUnitIds.add(SpringSecurityUtils.getCurrentUserUnitId());
        List<TsWorkTimePlanEntity> entities = workTimePlanService.getAllBySystemUnitIdsLikeName(systemUnitIds, name);
        return BeanUtils.copyCollection(entities, TsWorkTimePlanDto.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#setAsDefaultByUuid(java.lang.String)
     */
    @Override
    public void setAsDefaultByUuid(String uuid) {
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        workTimePlanService.setAsDefaultByUuidAndSystemUnitId(uuid, systemUnitId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#getDto(java.lang.String)
     */
    @Override
    public TsWorkTimePlanDto getDto(String uuid) {
        TsWorkTimePlanDto workTimePlanDto = new TsWorkTimePlanDto();
        TsWorkTimePlanEntity workTimePlanEntity = workTimePlanService.getOne(uuid);
        if (workTimePlanEntity != null) {
            BeanUtils.copyProperties(workTimePlanEntity, workTimePlanDto);
            // 确保前端只使用后端规则的值
            workTimePlanDto.setWorkTimeSchedule(JsonUtils
                    .object2Json(json2List(workTimePlanDto.getWorkTimeSchedule(), WorkTimeScheduleConfig.class)));
            workTimePlanDto.setHolidaySchedule(
                    JsonUtils.object2Json(json2List(workTimePlanDto.getHolidaySchedule(), HolidaySchedule.class)));
        }
        // 获取最近要生效的版本
        TsWorkTimePlanEntity toBeActiveVersion = workTimePlanService
                .getLatestToBeActiveVersionById(workTimePlanEntity.getId());
        if (toBeActiveVersion != null) {
            workTimePlanDto.setNewVersionTip(
                    "当前有新版本将于" + DateUtils.formatDateTimeMin(toBeActiveVersion.getActiveTime()) + "生效，详情请查阅版本历史！");
        }
        return workTimePlanDto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#getHistoryDto(java.lang.String)
     */
    @Override
    public TsWorkTimePlanHistoryDto getHistoryDto(String historyUuid) {
        TsWorkTimePlanHistoryDto workTimePlanHistoryDto = new TsWorkTimePlanHistoryDto();
        TsWorkTimePlanHistoryEntity workTimePlanHistoryEntity = workTimePlanHistoryService.getOne(historyUuid);
        if (workTimePlanHistoryEntity != null) {
            BeanUtils.copyProperties(workTimePlanHistoryEntity, workTimePlanHistoryDto);
        }
        return workTimePlanHistoryDto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#listNewVersionTipByUuids(java.util.List)
     */
    @Override
    public Map<String, Object> listNewVersionTipByUuids(List<String> uuids) {
        Map<String, Object> tips = Maps.newHashMap();
        for (String uuid : uuids) {
            // 获取最近要生效的版本
            TsWorkTimePlanEntity toBeActiveVersion = workTimePlanService.getLatestToBeActiveVersionByUuid(uuid);
            if (toBeActiveVersion != null) {
                tips.put(uuid, "当前有新版本将于" + DateUtils.formatDateTimeMin(toBeActiveVersion.getActiveTime()) + "生效！");
            }
        }
        return tips;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#getMaxVersionByUuid(java.lang.String)
     */
    @Override
    public String getMaxVersionByUuid(String uuid) {
        return workTimePlanService.getMaxVersionByUuid(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#saveDto(com.wellsoft.pt.timer.dto.TsWorkTimePlanDto)
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public String saveDto(TsWorkTimePlanDto workTimePlanDto) {
        if (Integer.valueOf(EnumWorkTimePlanStatus.Deactive.getValue()).equals(workTimePlanDto.getStatus())) {
            throw new BusinessException("工作时间方案已经失效，不能保存！");
        }
        TsWorkTimePlanEntity workTimePlanEntity = new TsWorkTimePlanEntity();
        Date currentTime = Calendar.getInstance().getTime();
        if (StringUtils.isNotBlank(workTimePlanDto.getUuid())) {
            workTimePlanEntity = workTimePlanService.getOne(workTimePlanDto.getUuid());
        } else {
            // 生成唯一ID
            workTimePlanDto.setId(UUID.randomUUID().toString());
            // 初始化版本
            workTimePlanDto.setVersion("1.0");
            workTimePlanDto.setActiveTime(workTimePlanService.getSysDate());
            workTimePlanDto.setStatus(EnumWorkTimePlanStatus.Active.getValue());
            workTimePlanDto.setSystem(RequestSystemContextPathResolver.system());
            workTimePlanDto.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        BeanUtils.copyProperties(workTimePlanDto, workTimePlanEntity, IdEntity.BASE_FIELDS);
        workTimePlanService.save(workTimePlanEntity);

        // 历史记录失效
        workTimePlanHistoryService.deactiveByWorkTimePlanUuid(workTimePlanEntity.getUuid(), currentTime);
        // 保存历史记录
        TsWorkTimePlanHistoryEntity historyEntity = new TsWorkTimePlanHistoryEntity();
        BeanUtils.copyProperties(workTimePlanEntity, historyEntity, IdEntity.BASE_FIELDS);
        historyEntity.setActiveTime(currentTime);
        historyEntity.setWorkTimePlanUuid(workTimePlanEntity.getUuid());
        workTimePlanHistoryService.save(historyEntity);
        return workTimePlanEntity.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#autoUpdateByHolidaySchedule(com.wellsoft.pt.timer.dto.TsHolidayScheduleDto)
     */
    @Override
    @Transactional
    public void autoUpdateByHolidaySchedule(TsHolidayScheduleDto holidayScheduleDto) {
        String holidayUuid = holidayScheduleDto.getHolidayUuid();
        Map<String, Object> values = Maps.newHashMap();
        values.put("status", Lists.<Integer>newArrayList(EnumWorkTimePlanStatus.Active.getValue(),
                EnumWorkTimePlanStatus.READY.getValue()));
        values.put("holidayUuid", holidayUuid);
        values.put("year", holidayScheduleDto.getYear());
        List<TsWorkTimePlanEntity> entities = workTimePlanService.listByNameHQLQuery("listToAutoUpdate", values);
        for (TsWorkTimePlanEntity workTimePlanEntity : entities) {
            TsWorkTimePlanDto workTimePlanDto = new TsWorkTimePlanDto();
            BeanUtils.copyProperties(workTimePlanEntity, workTimePlanDto);
            List<HolidaySchedule> holidaySchedules = json2List(workTimePlanDto.getHolidaySchedule(),
                    HolidaySchedule.class);
            // 更新自动更新的节假日配置
            for (HolidaySchedule holidaySchedule : holidaySchedules) {
                if (holidaySchedule.isAutoUpdate() && StringUtils.equals(holidayUuid, holidaySchedule.getHolidayUuid())
                        && Integer.valueOf(holidayScheduleDto.getYear()).equals(holidaySchedule.getYear())) {
                    holidaySchedule.setHolidayName(holidayScheduleDto.getHolidayName());
                    holidaySchedule.setFromDate(holidayScheduleDto.getFromDate());
                    holidaySchedule.setToDate(holidayScheduleDto.getToDate());
                    holidaySchedule.setMakeupDate(holidayScheduleDto.getMakeupDate());
                    holidaySchedule.setRemark(holidayScheduleDto.getRemark());
                }
            }
            workTimePlanDto.setHolidaySchedule(JsonUtils.object2Json(holidaySchedules));
            saveDto(workTimePlanDto);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#saveAsNewVersion(com.wellsoft.pt.timer.dto.TsWorkTimePlanDto)
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public String saveAsNewVersion(TsWorkTimePlanDto workTimePlanDto) {
        if (Integer.valueOf(EnumWorkTimePlanStatus.Deactive.getValue()).equals(workTimePlanDto.getStatus())) {
            throw new BusinessException("工作时间方案已经失效，不能保存为新版本！");
        } else if (Integer.valueOf(EnumWorkTimePlanStatus.READY.getValue()).equals(workTimePlanDto.getStatus())) {
            throw new BusinessException("工作时间方案未生效，不能保存为新版本！");
        }
        TsWorkTimePlanEntity workTimePlanEntity = new TsWorkTimePlanEntity();
        BeanUtils.copyProperties(workTimePlanDto, workTimePlanEntity, IdEntity.BASE_FIELDS);
        // String version = versionFormat.format(Double.valueOf(workTimePlanDto.getVersion()) + 0.1);
        // workTimePlanEntity.setVersion(version);
        workTimePlanEntity.setStatus(EnumWorkTimePlanStatus.READY.getValue());
        // 立即生效
        if (workTimePlanDto.isActiveRightNow()) {
            workTimePlanEntity.setActiveTime(Calendar.getInstance().getTime());
        }
        // 失效时间为空
        workTimePlanEntity.setDeactiveTime(null);
        workTimePlanService.save(workTimePlanEntity);

        // 旧版本默认工作方案设置为false
        Boolean isDefault = workTimePlanEntity.getIsDefault();
        if (BooleanUtils.isTrue(isDefault)) {
            workTimePlanService.setIsDefaultByUuid(workTimePlanDto.getUuid(), false);
        }

        // 保存历史记录
        TsWorkTimePlanHistoryEntity historyEntity = new TsWorkTimePlanHistoryEntity();
        BeanUtils.copyProperties(workTimePlanEntity, historyEntity, IdEntity.BASE_FIELDS);
        historyEntity.setWorkTimePlanUuid(workTimePlanEntity.getUuid());
        workTimePlanHistoryService.save(historyEntity);

        // 同步立即生效信息
        if (workTimePlanDto.isActiveRightNow()) {
            workTimePlanService.syncWorkTimePlanStatus();
        }
        return workTimePlanEntity.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#deleteAll(java.util.List)
     */
    @Override
    @Transactional
    public void deleteAll(List<String> uuids) {
        if (isUsedByUuidsForDeleteAll(uuids)) {
            throw new BusinessException("工作时间方案正被使用，无法删除！");
        }

        for (String uuid : uuids) {
            // 删除历史记录
            workTimePlanHistoryService.deleteByWorkTimePlanUuid(uuid);
            // 删除工作时间方案
            workTimePlanService.delete(uuid);
        }
    }

    /**
     * @param uuids
     * @return
     */
    private boolean isUsedByUuidsForDeleteAll(List<String> uuids) {
        for (String uuid : uuids) {
            for (TsWorkTimePlanUsedChecker workTimePlanUsedChecker : workTimePlanUsedCheckers) {
                if (workTimePlanUsedChecker.isWorkTimePlanUsed(uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#getWorkDate(java.lang.String, java.util.Date, int, com.wellsoft.pt.basicdata.workhour.enums.WorkUnit)
     */
    @Override
    public Date getWorkDate(String uuid, Date fromDate, double amount, WorkUnit workUnit) {
        WorkTimePlan workTimePlan = getWorkTimePlan(uuid, fromDate);
        WorkTimeContext workTimeContext = new WorkTimeContext(workTimePlan);
        return getWorkDate(fromDate, amount, workUnit, workTimeContext);
    }

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
    @Override
    public Date getWorkDate(String uuid, Date fromDate, double amount, WorkUnit workUnit, boolean autoDelay) {
        WorkTimePlan workTimePlan = getWorkTimePlan(uuid, fromDate);
        WorkTimeContext workTimeContext = new WorkTimeContext(workTimePlan);
        Date workDate = getWorkDate(fromDate, amount, workUnit, workTimeContext);
        // 自动推迟到下一工作日开始时间
        if (autoDelay) {
            workDate = autoDelayWorkDateIfRequired(workDate, workUnit, uuid);
        }
        return workDate;
    }

    /**
     * @param workDate
     * @param workUnit
     * @param workTimePlanUuid
     * @return
     */
    @Override
    public Date autoDelayWorkDateIfRequired(Date workDate, WorkUnit workUnit, String workTimePlanUuid) {
        Date retWorkDate = workDate;
        switch (workUnit) {
            case WorkingDay:
            case WorkingHour:
            case WorkingMinute:
                retWorkDate = autoDelayWorkingMinuteIfRequired(retWorkDate, workTimePlanUuid);
                break;
            case WorkingDayOf24Hour:
            case WorkingHourOf24Hour:
            case WorkingMinuteOf24Hour:
                retWorkDate = autoDelayWorkingMinute24IfRequired(retWorkDate, workTimePlanUuid);
                break;

        }
        return retWorkDate;
    }

    /**
     * @param dueTime
     * @param workTimePlanUuid
     * @return
     */
    private Date autoDelayWorkingMinuteIfRequired(Date dueTime, String workTimePlanUuid) {
        Date retDueTime = dueTime;
        WorkTime workTime = getWorkTime(workTimePlanUuid, retDueTime);
        Date toTimeInTimePeriod = workTime.getToTimeInTimePeriod(retDueTime);
        // 则好到下班时间，推迟到下一工作日上班时间点
        if (retDueTime.equals(workTime.getEndTime())) {
            Date nextWorkDate = getNextWorkDate(workTimePlanUuid, retDueTime);
            WorkTime nextWorkTime = getWorkTime(workTimePlanUuid, nextWorkDate);
            retDueTime = nextWorkTime.getFromTime();
        } else if (workTime.isInTimePeriod(retDueTime) && retDueTime.equals(toTimeInTimePeriod)) {
            retDueTime = workTime.getNextFromTimeInTimePeriod(retDueTime);
        }
        return retDueTime;
    }

    /**
     * @param dueTime
     * @param workTimePlanUuid
     * @return
     */
    private Date autoDelayWorkingMinute24IfRequired(Date dueTime, String workTimePlanUuid) {
        Date retDueTime = dueTime;
        // 到期时间不是工作日，取下一工作日
        Date workDate = retDueTime;
        if (!isWorkDay(workTimePlanUuid, workDate)) {
            workDate = getNextWorkDate(workTimePlanUuid, workDate);
        }
        WorkTime workTime = getWorkTime(workTimePlanUuid, workDate);
        // 工作分钟(24小时制)
        if (workTime.isInTimePeriod(retDueTime)) {
            Date toTimeInTimePeriod = workTime.getToTimeInTimePeriod(retDueTime);
            // 则好到下班时间，推迟到第二天上班时间前
            if (retDueTime.equals(workTime.getEndTime())) {
                Date nextWorkDate = getNextWorkDate(workTimePlanUuid, retDueTime);
                WorkTime nextWorkTime = getWorkTime(workTimePlanUuid, nextWorkDate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(nextWorkTime.getFromTime());
                // calendar.add(Calendar.MINUTE, -1);
                retDueTime = calendar.getTime();
            } else if (retDueTime.equals(toTimeInTimePeriod)) {
                // 不在最后时间段的结束时间点，取下一时间段的开始时间
                retDueTime = workTime.getNextFromTimeInTimePeriod(retDueTime);
            }
        } else if (workTime.isBeforeEndTime(retDueTime)) {
            // 在时间段之间的时间点，取下一时间段的开始时间
            retDueTime = workTime.getNextFromTimeBetweenTimePeriod(retDueTime);
        } else {
            // 推迟到第二天上班时间前
            Date nextWorkDate = getNextWorkDate(workTimePlanUuid, retDueTime);
            WorkTime nextWorkTime = getWorkTime(workTimePlanUuid, nextWorkDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nextWorkTime.getFromTime());
            retDueTime = calendar.getTime();
        }
        return retDueTime;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#getNextWorkDate(java.lang.String, java.util.Date)
     */
    @Override
    public Date getNextWorkDate(String uuid, Date fromDate) {
        WorkTimePlan workTimePlan = getWorkTimePlan(uuid, fromDate);
        WorkTimeContext workTimeContext = new WorkTimeContext(workTimePlan);
        Calendar calendar = DateUtils.getCalendar(fromDate);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        while (!isWorkDay(calendar.getTime(), workTimeContext)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return calendar.getTime();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#getPrevWorkDate(java.lang.String, java.util.Date)
     */
    @Override
    public Date getPrevWorkDate(String uuid, Date fromDate) {
        WorkTimePlan workTimePlan = getWorkTimePlan(uuid, fromDate);
        WorkTimeContext workTimeContext = new WorkTimeContext(workTimePlan);
        Calendar calendar = DateUtils.getCalendar(fromDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        while (!isWorkDay(calendar.getTime(), workTimeContext)) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        }
        return calendar.getTime();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#isWorkDay(java.lang.String, java.util.Date)
     */
    @Override
    public boolean isWorkDay(String uuid, Date date) {
        WorkTimePlan workTimePlan = getWorkTimePlan(uuid, date);
        WorkTimeContext workTimeContext = new WorkTimeContext(workTimePlan);
        return isWorkDay(date, workTimeContext);
    }

    @Override
    public boolean isWorkHour(String uuid, Date date) {
        WorkTimePlan workTimePlan = getWorkTimePlan(uuid, date);
        WorkTimeContext workTimeContext = new WorkTimeContext(workTimePlan);
        boolean isWorkDay = isWorkDay(date, workTimeContext);
        if (!isWorkDay) {
            return false;
        }

        WorkTime workTime = getWorkTime(uuid, date);
        return workTime.isInTimePeriod(date);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#getWorkTime(java.lang.String, java.util.Date)
     */
    @Override
    public WorkTime getWorkTime(String uuid, Date date) {
        WorkTimePlan workTimePlan = getWorkTimePlan(uuid, date);
        WorkTimeContext workTimeContext = new WorkTimeContext(workTimePlan);
        return getWorkTime(date, workTimeContext);
    }

    /**
     * @param date
     * @param workTimeContext
     * @return
     */
    private WorkTime getWorkTime(Date date, WorkTimeContext workTimeContext) {
        WorkTime workTime = null;
        // 获取补的日期，不存在返回原日期
        Date workDate = date;
        boolean isMakeupWorkDay = false;
        if (isMakeupWorkDay(date, workTimeContext)) {
            workDate = workTimeContext.getDateOfMakeuped(date);
            isMakeupWorkDay = true;
        }
        List<WorkTimeScheduleInstance> workTimeSchedules = workTimeContext.getWorkTimePlan(workDate).getWorkTimeSchedules();
        for (WorkTimeScheduleInstance workTimeSchedule : workTimeSchedules) {
            // 判断是否在正常工作日内
            if (!workTimeSchedule.isDateInSchedule(workDate)) {
                continue;
            }
            EnumWorkTimeType workTimeType = workTimeSchedule.getWorkTimeType();
            switch (workTimeType) {
                case FixedWork:
                case SingleAndDoubleWeek:
                    // 固定工时、单双周
                    workTime = getWorkTimeInWorkTimePeriod(workDate, workTimeSchedule, isMakeupWorkDay);
                    if (workTime != null) {
                        // 补班日期设置补班信息
                        if (isMakeupWorkDay) {
                            workTime.setIsMakeupWorkDay(isMakeupWorkDay);
                            workTime.setMakeupDate(date);
                        }
                        return workTime;
                    }
                    break;
                case FlexibleWork:
                    // 弹性工时
                    throw new BusinessException("计时服务不支持弹性工时配置解析！");
                default:
                    break;
            }
        }
        return new WorkTime(workDate);
    }

    /**
     * @param workDate
     * @param workTimeSchedule
     * @return
     */
    private WorkTime getWorkTimeInWorkTimePeriod(Date workDate, WorkTimeScheduleInstance workTimeSchedule, boolean isMakeupWorkDay) {
        WorkTime workTime = null;
        String dayOfWeek = DAY_OF_WEEKS[DateUtils.getCalendar(workDate).get(Calendar.DAY_OF_WEEK) - 1];
        List<WorkTimeConfig> workTimes = workTimeSchedule.getWorkTimes();
        // 单双周
        if (EnumWorkTimeType.SingleAndDoubleWeek.equals(workTimeSchedule.getWorkTimeType())) {
            workTimes = workTimeSchedule.getWorkTimes(isDateInOddWeeks(workDate));
        }
        for (WorkTimeConfig workTimeConfig : workTimes) {
            // 非补班，判断是否是工作日；补班的情况，不需要判断是否工作日
            if (!isMakeupWorkDay) {
                if (!StringUtils.contains(workTimeConfig.getWorkDay(), dayOfWeek)) {
                    continue;
                }
            }
            workTime = new WorkTime(workDate);
            if (StringUtils.isNotBlank(workTimeConfig.getWorkHoursPerDay())) {
                workTime.setWorkHoursPerDay(Double.parseDouble(workTimeConfig.getWorkHoursPerDay()));
            }
            List<TimePeriodConfig> timePeriodConfigs = workTimeConfig.getTimePeriods();
            for (TimePeriodConfig timePeriodConfig : timePeriodConfigs) {
                workTime.addTimePeriod(timePeriodConfig.getName(), timePeriodConfig.getFromTimeInstance(workDate),
                        timePeriodConfig.getToTimeInstance(workDate));
            }
        }
        return workTime;
    }

    /**
     * @param fromDate
     * @param amount
     * @param workUnit
     * @param workTimeContext
     * @return
     */
    private Date getWorkDate(Date fromDate, double amount, WorkUnit workUnit, WorkTimeContext workTimeContext) {
        if (amount == 0) {
            return fromDate;
        }
        Date retWorkDate = null;
        switch (workUnit) {
            case WorkingDay:
                retWorkDate = getWorkDateOfWorkingDay(fromDate, amount, workTimeContext);
                break;
            case WorkingHour:
                retWorkDate = getWorkDateOfWorkingHour(fromDate, amount, workTimeContext);
                break;
            case WorkingMinute:
                retWorkDate = getWorkDateOfWorkingMinute(fromDate, amount, workTimeContext);
                break;
            case WorkingDayOf24Hour:
                retWorkDate = getWorkDateOfWorkingDayOf24Hour(fromDate, amount, workTimeContext);
                break;
            case WorkingHourOf24Hour:
                retWorkDate = getWorkDateOfWorkingHourOf24Hour(fromDate, amount, workTimeContext);
                break;
            case WorkingMinuteOf24Hour:
                retWorkDate = getWorkDateOfWorkingMinuteOf24Hour(fromDate, amount, workTimeContext);
                break;
            default:
                break;
        }
        return retWorkDate;
    }

    /**
     * @param fromDate
     * @param amount
     * @param workTimeContext
     * @return
     */
    private Date getWorkDateOfWorkingDayOf24Hour(Date fromDate, double amount, WorkTimeContext workTimeContext) {
        return getWorkDateOfWorkingMinuteOf24Hour(fromDate, amount * 24 * 60, workTimeContext);
    }

    /**
     * @param fromDate
     * @param amount
     * @param workTimeContext
     * @return
     */
    private Date getWorkDateOfWorkingHourOf24Hour(Date fromDate, double amount, WorkTimeContext workTimeContext) {
        return getWorkDateOfWorkingMinuteOf24Hour(fromDate, amount * 60, workTimeContext);
    }

    /**
     * @param fromDate
     * @param amount
     * @param workTimeContext
     * @return
     */
    private Date getWorkDateOfWorkingMinuteOf24Hour(Date fromDate, double amount, WorkTimeContext workTimeContext) {
        Date workDate = fromDate;
        Calendar calendar = DateUtils.getCalendar(workDate);
        int increment = Double.valueOf(amount / Math.abs(amount)).intValue();
        for (int i = 0; i < Math.abs(amount); i++) {
            // 如果不是工作日，则继续推进，时分设置为0
            while (!isWorkDay(calendar.getTime(), workTimeContext)) {
                calendar.add(Calendar.DAY_OF_MONTH, increment);
                if (increment > 0) {
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                }
            }
            // 一天24*60分钟的工作时间
            calendar.add(Calendar.MINUTE, increment);
        }
        workDate = calendar.getTime();
        return workDate;
    }

    /**
     * 按倒计时计算的工作时间
     *
     * @param fromDate
     * @param amount
     * @param workTimeContext
     * @return
     */
    private Date getWorkDateOfWorkingDay(Date fromDate, double amount, WorkTimeContext workTimeContext) {
        Double totalWorkTimeMinutes = getTotalWorkTimeMinutes(fromDate, amount, workTimeContext);
        return getWorkDateOfWorkingMinute(fromDate, totalWorkTimeMinutes, workTimeContext);
    }

    /**
     * @param date
     * @param workTimeContext
     * @return
     */
    private boolean isWorkDay(Date date, WorkTimeContext workTimeContext) {
        if (workTimeContext.isWorkDate(date)) {
            return true;
        }
        // 节假日安排
        // 是否补班日期
        boolean makeupWorkDay = isMakeupWorkDay(date, workTimeContext);
        if (makeupWorkDay) {
            workTimeContext.addWorkDate(date);
            return true;
        }
        // 是否安排的假期
        boolean scheduleHoliday = isScheduleHoliday(date, workTimeContext);
        if (scheduleHoliday) {
            return false;
        }
        // 是否工作时间计划的工作日
        boolean dateInWorkTimePlan = isDateInWorkTimePlan(date, workTimeContext);
        if (dateInWorkTimePlan) {
            workTimeContext.addWorkDate(date);
        }
        return dateInWorkTimePlan;
    }

    /**
     * @param date
     * @param workTimeContext
     * @return
     */
    private boolean isDateInWorkTimePeriod(Date date, WorkTimeContext workTimeContext) {
        boolean inWorkTimePeriod = false;
        // 获取补的日期，不存在返回原日期
        Date workDate = date;
        if (isMakeupWorkDay(date, workTimeContext)) {
            workDate = workTimeContext.getDateOfMakeuped(date);
        }
        List<WorkTimeScheduleInstance> workTimeSchedules = workTimeContext.getWorkTimePlan(workDate).getWorkTimeSchedules();
        for (WorkTimeScheduleInstance workTimeSchedule : workTimeSchedules) {
            // 判断是否在正常工作日内
            if (!workTimeSchedule.isDateInSchedule(workDate)) {
                continue;
            }
            EnumWorkTimeType workTimeType = workTimeSchedule.getWorkTimeType();
            switch (workTimeType) {
                case FixedWork:
                case SingleAndDoubleWeek:
                    // 固定工时、单双周
                    inWorkTimePeriod = isDateInWorkTimePeriod(workDate, workTimeSchedule);
                    if (inWorkTimePeriod) {
                        return inWorkTimePeriod;
                    }
                    break;
                case FlexibleWork:
                    // 弹性工时
                    throw new BusinessException("计时服务不支持弹性工时配置解析！");
                default:
                    break;
            }
        }
        return inWorkTimePeriod;
    }

    /**
     * @param date
     * @param workTimeType
     * @return
     */
    private boolean isDateInWorkTimePeriod(Date date, WorkTimeScheduleInstance workTimeSchedule) {
        String dayOfWeek = DAY_OF_WEEKS[DateUtils.getCalendar(date).get(Calendar.DAY_OF_WEEK) - 1];
        List<WorkTimeConfig> workTimes = workTimeSchedule.getWorkTimes();
        // 单双周
        if (EnumWorkTimeType.SingleAndDoubleWeek.equals(workTimeSchedule.getWorkTimeType())) {
            workTimes = workTimeSchedule.getWorkTimes(isDateInOddWeeks(date));
        }
        for (WorkTimeConfig workTime : workTimes) {
            if (!StringUtils.contains(workTime.getWorkDay(), dayOfWeek)) {
                continue;
            }
            List<TimePeriodConfig> timePeriodConfigs = workTime.getTimePeriods();
            for (TimePeriodConfig timePeriod : timePeriodConfigs) {
                Date fromTime = timePeriod.getFromTimeInstance(date);
                Date toTime = timePeriod.getToTimeInstance(date);
                if (!date.before(fromTime) && !date.after(toTime)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param date
     * @param workTimeContext
     * @return
     */
    private Date getToDateInWorkTimePeriod(Date date, WorkTimeContext workTimeContext) {
        Date toDate = null;
        // 获取补的日期，不存在返回原日期
        Date workDate = date;
        if (isMakeupWorkDay(date, workTimeContext)) {
            workDate = workTimeContext.getDateOfMakeuped(date);
        }
        List<WorkTimeScheduleInstance> workTimeSchedules = workTimeContext.getWorkTimePlan(workDate).getWorkTimeSchedules();
        for (WorkTimeScheduleInstance workTimeSchedule : workTimeSchedules) {
            // 判断是否在正常工作日内
            if (!workTimeSchedule.isDateInSchedule(workDate)) {
                continue;
            }
            EnumWorkTimeType workTimeType = workTimeSchedule.getWorkTimeType();
            switch (workTimeType) {
                case FixedWork:
                case SingleAndDoubleWeek:
                    // 固定工时、单双周
                    toDate = getToDateInWorkTimePeriod(workDate, workTimeSchedule);
                    if (toDate != null) {
                        return toDate;
                    }
                    break;
                case FlexibleWork:
                    // 弹性工时
                    throw new BusinessException("计时服务不支持弹性工时配置解析！");
                default:
                    break;
            }
        }
        return toDate;
    }

    /**
     * @param date
     * @param workTimeSchedule
     * @return
     */
    private Date getToDateInWorkTimePeriod(Date date, WorkTimeScheduleInstance workTimeSchedule) {
        Date toDate = null;
        String dayOfWeek = DAY_OF_WEEKS[DateUtils.getCalendar(date).get(Calendar.DAY_OF_WEEK) - 1];
        List<WorkTimeConfig> workTimes = workTimeSchedule.getWorkTimes();
        // 单双周
        if (EnumWorkTimeType.SingleAndDoubleWeek.equals(workTimeSchedule.getWorkTimeType())) {
            workTimes = workTimeSchedule.getWorkTimes(isDateInOddWeeks(date));
        }
        for (WorkTimeConfig workTime : workTimes) {
            if (!StringUtils.contains(workTime.getWorkDay(), dayOfWeek)) {
                continue;
            }
            List<TimePeriodConfig> timePeriodConfigs = workTime.getTimePeriods();
            // 倒序处理，获取结束时间
            Collections.reverse(timePeriodConfigs);
            for (TimePeriodConfig timePeriod : timePeriodConfigs) {
                return timePeriod.getToTimeInstance(date);
            }
        }
        return toDate;
    }

    /**
     * 判断日期是否单周日期
     *
     * @param date
     * @return
     */
    private boolean isDateInOddWeeks(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        return calendar.get(Calendar.WEEK_OF_YEAR) % 2 == 1;
    }

    /**
     * @param date
     * @param workTimeContext
     * @return
     */
    private boolean isMakeupWorkDay(Date date, WorkTimeContext workTimeContext) {
        if (workTimeContext.isMakupDate(date)) {
            return true;
        }
        List<HolidaySchedule> holidaySchedules = workTimeContext.getWorkTimePlan(date).getHolidaySchedules();
        for (HolidaySchedule holidaySchedule : holidaySchedules) {
            String makeUpDate = holidaySchedule.getMakeupDate();
            if (StringUtils.isBlank(makeUpDate)) {
                continue;
            }
            List<String> makeUpDates = Lists
                    .newArrayList(StringUtils.split(makeUpDate, Separator.SEMICOLON.getValue()));
            for (String dateString : makeUpDates) {
                try {
                    String[] dates = StringUtils.split(dateString, Separator.VERTICAL.getValue());
                    Date configDate = DateUtils.parse(dates[0]);
                    if (DateUtils.isSameDate(configDate, date)) {
                        workTimeContext.addMakeupDate(DateUtils.formatDate(date), dates[1]);
                        return true;
                    }
                } catch (ParseException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return false;
    }

    /**
     * @param date
     * @param workTimeContext
     * @return
     */
    private boolean isScheduleHoliday(Date date, WorkTimeContext workTimeContext) {
        if (workTimeContext.isScheduleHoliday(date)) {
            return true;
        }
        Date compareDate = DateUtils.getMinTimeCalendar(DateUtils.getCalendar(date)).getTime();
        List<HolidaySchedule> holidaySchedules = workTimeContext.getWorkTimePlan(date).getHolidaySchedules();
        for (HolidaySchedule holidaySchedule : holidaySchedules) {
            try {
                Date fromDate = DateUtils.parse(holidaySchedule.getFromDate());
                Date toDate = DateUtils.parse(holidaySchedule.getToDate());
                if (!compareDate.before(fromDate) && !compareDate.after(toDate)) {
                    workTimeContext.addScheduleHoliday(compareDate);
                    return true;
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return false;
    }

    /**
     * @param date
     * @param workTimeContext
     * @return
     */
    private boolean isDateInWorkTimePlan(Date date, WorkTimeContext workTimeContext) {
        boolean workDayInWorkTimePlan = false;
        // 工作计划的时间
        if (workTimeContext.isWorkTimePlanDate(date)) {
            return true;
        }
        List<WorkTimeScheduleInstance> workTimeSchedules = workTimeContext.getWorkTimePlan(date).getWorkTimeSchedules();
        for (WorkTimeScheduleInstance workTimeSchedule : workTimeSchedules) {
            // 判断是否在正常工作日内
            if (!workTimeSchedule.isDateInSchedule(date)) {
                continue;
            }
            EnumWorkTimeType workTimeType = workTimeSchedule.getWorkTimeType();
            switch (workTimeType) {
                case FixedWork:
                case SingleAndDoubleWeek:
                    // 固定工时、单双周
                    workDayInWorkTimePlan = isDateInWorkTimePlan(date, workTimeSchedule);
                    if (workDayInWorkTimePlan) {
                        workTimeContext.addWorkTimePlanDate(date);
                        return workDayInWorkTimePlan;
                    }
                    break;
                case FlexibleWork:
                    // 弹性工时
                    throw new BusinessException("计时服务不支持弹性工时配置解析！");
                default:
                    break;
            }
        }
        return workDayInWorkTimePlan;
    }

    /**
     * @param date
     * @param workTimeSchedule
     * @return
     */
    private boolean isDateInWorkTimePlan(Date date, WorkTimeScheduleInstance workTimeSchedule) {
        String dayOfWeek = DAY_OF_WEEKS[DateUtils.getCalendar(date).get(Calendar.DAY_OF_WEEK) - 1];
        List<WorkTimeConfig> workTimes = workTimeSchedule.getWorkTimes();
        // 单双周
        if (EnumWorkTimeType.SingleAndDoubleWeek.equals(workTimeSchedule.getWorkTimeType())) {
            workTimes = workTimeSchedule.getWorkTimes(isDateInOddWeeks(date));
        }
        for (WorkTimeConfig workTime : workTimes) {
            if (StringUtils.contains(workTime.getWorkDay(), dayOfWeek)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param fromDate
     * @param amount
     * @param workTimePlan
     * @return
     */
    private Date getWorkDateOfWorkingHour(Date fromDate, double amount, WorkTimeContext workTimeContext) {
        return getWorkDateOfWorkingMinute(fromDate, amount * 60, workTimeContext);
    }

    /**
     * @param workDate
     * @param amount
     * @param workTimePlan
     * @return
     */
    private Date getWorkDateOfWorkingMinute(Date fromDate, double amount, WorkTimeContext workTimeContext) {
        Date workDate = fromDate;
        Calendar calendar = DateUtils.getCalendar(workDate);
        int increment = Double.valueOf(amount / Math.abs(amount)).intValue();
        int maxWorkDayOfDate = 356;
        int maxMinuteOfDate = 24 * 60;
        for (int i = 0; i < Math.abs(amount); i++) {
            int workDayCount = 0;
            int minuteCount = 0;
            // 工作分钟加一或减一
            calendar.add(Calendar.MINUTE, increment);
            // 如果不是工作日，则继续推进，时分设置为0
            while (!isWorkDay(calendar.getTime(), workTimeContext)) {
                calendar.add(Calendar.DAY_OF_MONTH, increment);
                if (increment > 0) {
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                }
                if (++workDayCount > maxWorkDayOfDate) {
                    throw new BusinessException("工作时间方案的工作日[" + DateUtils.formatDate(calendar.getTime()) + "]未设置！");
                }
            }
            // 如果不在工作时间段内继续推进
            boolean forward = false;
            while (!isDateInWorkTimePeriod(calendar.getTime(), workTimeContext)) {
                calendar.add(Calendar.MINUTE, increment);
                while (!isWorkDay(calendar.getTime(), workTimeContext)) {
                    calendar.add(Calendar.DAY_OF_MONTH, increment);
                    if (increment > 0) {
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                    } else {
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                    }
                    if (++workDayCount > maxWorkDayOfDate) {
                        throw new BusinessException("工作时间方案的工作日[" + DateUtils.formatDate(calendar.getTime()) + "]未设置！");
                    }
                }
                // 超过一天的分钟数，异常结束
                if (++minuteCount > maxMinuteOfDate) {
                    throw new BusinessException(
                            "工作日[" + DateUtils.formatDate(calendar.getTime()) + "]未设置有效的工作时间段或解析不到有效的工作时间段！");
                }
                forward = true;
            }
            // 恢复推进提前增加的值
            if (forward) {
                calendar.add(Calendar.MINUTE, increment);
            }
        }
        workDate = calendar.getTime();
        return workDate;
    }

    /**
     * 根据工作时间方案UUID、年份，获取对应年份工作时间方案
     *
     * @param uuid
     * @param year
     * @return
     */
    @Override
    public WorkTimePlan getWorkTimePlan(String uuid, int year) {
        Cache cache = cacheManager.getCache(CacheName.DEFAULT);
        String cacheKey = "workTimePlan_" + uuid + "_" + RequestSystemContextPathResolver.system() + "_" + year;
        WorkTimePlan workTimePlan = (WorkTimePlan) cache.getValue(cacheKey);
        if (workTimePlan != null) {
            return workTimePlan;
        }

        String workTimePlanUuid = uuid;
        if (StringUtils.isBlank(workTimePlanUuid)) {
            workTimePlanUuid = workTimePlanService.getDefaultWorkTimePlan().getUuid();
        }
        TsWorkTimePlanHistoryEntity workTimePlanHistoryEntity = workTimePlanHistoryService
                .getActiveByWorkTimePlanUuid(workTimePlanUuid);
        TsWorkTimePlanEntity workTimePlanEntity = null;
        // 工作时间安排
        String workTimeScheduleJson;
        // 节假日安排
        String holidayScheduleJson;
        if (workTimePlanHistoryEntity != null) {
            workTimeScheduleJson = workTimePlanHistoryEntity.getWorkTimeSchedule();
            holidayScheduleJson = workTimePlanHistoryEntity.getHolidaySchedule();
        } else {
            workTimePlanEntity = workTimePlanService.getOne(workTimePlanUuid);
            workTimeScheduleJson = workTimePlanEntity.getWorkTimeSchedule();
            holidayScheduleJson = workTimePlanEntity.getHolidaySchedule();
        }
        List<WorkTimeScheduleConfig> workTimeSchedules = json2List(workTimeScheduleJson, WorkTimeScheduleConfig.class);
        if (CollectionUtils.isEmpty(workTimeSchedules)) {
            String workTimePlanName = workTimePlanHistoryEntity != null ? workTimePlanHistoryEntity.getName() : workTimePlanEntity.getName();
            throw new BusinessException(String.format("工作时间方案[%s]，没有配置工作时间！", workTimePlanName));
        }
        List<WorkTimeScheduleInstance> scheduleInstances = WorkTimeScheduleInstanceParser.parse(year,
                workTimeSchedules);

        Collection<HolidaySchedule> holidaySchedules = json2List(holidayScheduleJson, HolidaySchedule.class);
        for (HolidaySchedule holidaySchedule : holidaySchedules) {
            // 自动更新
            if (holidaySchedule.isAutoUpdate()) {
                TsHolidayScheduleDto holidayScheduleDto = holidayScheduleFacadeService.getByHolidayAndYear(
                        holidaySchedule.getHolidayUuid(), year + StringUtils.EMPTY);
                if (holidayScheduleDto != null && StringUtils.isNotBlank(holidayScheduleDto.getUuid())) {
                    holidaySchedule.setFromDate(holidayScheduleDto.getFromDate());
                    holidaySchedule.setToDate(holidayScheduleDto.getToDate());
                    holidaySchedule.setMakeupDate(holidayScheduleDto.getMakeupDate());
                }
            }
        }

        workTimePlan = new WorkTimePlan();
        workTimePlan.setWorkTimePlanUuid(workTimePlanUuid);
        workTimePlan.setYear(year);
        workTimePlan.setWorkTimeSchedules(scheduleInstances);
        workTimePlan.setHolidaySchedules(Lists.<HolidaySchedule>newArrayList(holidaySchedules));
        cache.put(cacheKey, workTimePlan);
        return workTimePlan;
    }

    /**
     * @param uuid
     * @param workDate
     * @return
     */
    private WorkTimePlan getWorkTimePlan(String uuid, Date workDate) {
        int year = DateUtils.getCalendar(workDate).get(Calendar.YEAR);
        return getWorkTimePlan(uuid, year);
    }

    /**
     * @param json
     * @param cls
     * @return
     */
    private <T> List<T> json2List(String json, Class<T> cls) {
        List<T> list = Lists.newArrayList();
        JSONArray jsonArray = JSONArray.fromObject(json);
        for (int index = 0; index < jsonArray.size(); index++) {
            list.add(JsonUtils.json2Object(jsonArray.getJSONObject(index).toString(), cls));
        }
        return list;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#isUsedByUuids(java.util.List)
     */
    @Override
    public ResultMessage isUsedByUuids(List<String> uuids) {
        ResultMessage message = new ResultMessage();
        List<String> usedUuids = Lists.newArrayList();
        long startTime = System.currentTimeMillis();
        for (String uuid : uuids) {
            for (TsWorkTimePlanUsedChecker workTimePlanUsedChecker : workTimePlanUsedCheckers) {
                if (workTimePlanUsedChecker.isWorkTimePlanUsed(uuid)) {
                    usedUuids.add(uuid);
                }
            }
        }
        long endTime = System.currentTimeMillis();
        logger.error("TsWorkTimePlanUsedChecker times: " + (endTime - startTime));
        if (CollectionUtils.isNotEmpty(usedUuids)) {
            List<TsWorkTimePlanEntity> workTimePlanEntities = workTimePlanService.listByUuids(usedUuids);
            StringBuilder sb = new StringBuilder("以下工作时间方案正在使用，无法删除");
            for (TsWorkTimePlanEntity workTimePlanEntity : workTimePlanEntities) {
                sb.append(Separator.LINE.getValue());
                sb.append(workTimePlanEntity.getName());
            }
            message.clear();
            message.setMsg(sb);
            message.setData(workTimePlanEntities);
        }
        return message;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#getWorkTimes(java.lang.String, java.util.Date, int)
     */
    @Override
    public WorkTimes getWorkTimes(String uuid, Date fromDate, int amount) {
        WorkTimePlan workTimePlan = getWorkTimePlan(uuid, fromDate);
        WorkTimeContext workTimeContext = new WorkTimeContext(workTimePlan);
        return getWorkTimes(fromDate, amount, workTimeContext);
    }

    /**
     * @param fromDate
     * @param amount
     * @param workTimeContext
     * @return
     */
    public WorkTimes getWorkTimes(Date fromDate, int amount, WorkTimeContext workTimeContext) {
        int increment = amount / Math.abs(amount);
        Date workDate = fromDate;
        Calendar calendar = DateUtils.getCalendar(workDate);
        List<WorkTime> workTimes = Lists.newArrayList();
        // 增量
        for (int index = 0; index < Math.abs(amount); index++) {
            // 如果不是工作日，则继续推进
            while (!isWorkDay(calendar.getTime(), workTimeContext)) {
                calendar.add(Calendar.DAY_OF_MONTH, increment);
            }
            workDate = calendar.getTime();
            workTimes.add(getWorkTime(workDate, workTimeContext));
            calendar.add(Calendar.DAY_OF_MONTH, increment);
        }
        return new WorkTimes(workTimes);
    }

    /**
     * 按工作日颗粒度计算的工作时间
     *
     * @param fromDate
     * @param increment
     * @param toEffectiveTimePoint
     * @param workTimeContext
     * @return
     */
    private Date getWorkDateOfWorkingDayByWorkingDayUnit(Date fromDate, int increment, boolean toEffectiveTimePoint,
                                                         WorkTimeContext workTimeContext) {
        Date workDate = fromDate;
        Calendar calendar = DateUtils.getCalendar(workDate);
        // 如果不是工作日，则继续推进
        while (!isWorkDay(calendar.getTime(), workTimeContext)) {
            calendar.add(Calendar.DAY_OF_MONTH, increment);
        }
        workDate = calendar.getTime();
        // 工作时间进入有效的时间点
        if (toEffectiveTimePoint && !isDateInWorkTimePeriod(workDate, workTimeContext)) {
            workDate = getToDateInWorkTimePeriod(workDate, workTimeContext);
        }
        return workDate;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#getWorkTimePeriod(java.lang.String, java.util.Date, java.util.Date)
     */
    @Override
    public WorkTimePeriod getWorkTimePeriod(String uuid, Date fromTime, Date toTime) {
        WorkTimePeriod workTimePeriod = new WorkTimePeriod(fromTime, toTime);
        // 开始时间大于结束时间
        if (toTime.before(fromTime)) {
            return workTimePeriod;
        }
        if (DateUtils.isSameDate(fromTime, toTime)) {
            // 同一天
            if (isWorkDay(uuid, fromTime)) {
                workTimePeriod.addWorkTime(getWorkTime(uuid, fromTime));
            }
        } else {
            // 不同天
            if (isWorkDay(uuid, fromTime)) {
                workTimePeriod.addWorkTime(getWorkTime(uuid, fromTime));
            }
            Calendar fromCalendar = DateUtils.getCalendar(fromTime);
            fromCalendar.add(Calendar.DAY_OF_MONTH, 1);
            while (!DateUtils.isSameDate(fromCalendar.getTime(), toTime)) {
                Date date = DateUtils.getMinTimeCalendar(fromCalendar).getTime();
                if (isWorkDay(uuid, date)) {
                    workTimePeriod.addWorkTime(getWorkTime(uuid, date));
                }
                fromCalendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            if (isWorkDay(uuid, toTime)) {
                workTimePeriod.addWorkTime(getWorkTime(uuid, toTime));
            }
        }
        return workTimePeriod;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#getDefaultWorkTimePlan()
     */
    @Override
    public TsWorkTimePlanDto getDefaultWorkTimePlan() {
        TsWorkTimePlanDto workTimePlanDto = new TsWorkTimePlanDto();
        TsWorkTimePlanEntity workTimePlanEntity = workTimePlanService.getDefaultWorkTimePlan();
        if (workTimePlanEntity != null) {
            BeanUtils.copyProperties(workTimePlanEntity, workTimePlanDto);
        } else {
            throw new BusinessException("默认的工作时间方案不存在！");
        }
        return workTimePlanDto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#getTotalWorkTimeMinutes(java.lang.String, java.util.Date, double)
     */
    @Override
    public double getTotalWorkTimeMinutes(String workTimePlanUuid, Date fromDate, double amount) {
        WorkTimePlan workTimePlan = getWorkTimePlan(workTimePlanUuid, fromDate);
        WorkTimeContext workTimeContext = new WorkTimeContext(workTimePlan);
        return getTotalWorkTimeMinutes(fromDate, amount, workTimeContext);
    }

    /**
     * @param fromDate
     * @param amount
     * @param workTimeContext
     * @return
     */
    private double getTotalWorkTimeMinutes(Date fromDate, double amount, WorkTimeContext workTimeContext) {
        Double totalWorkTimeMinutes = Double.valueOf(0);
        Double absAmount = Math.abs(amount);
        Double ceilAmount = Math.ceil(absAmount);
        double increment = amount / absAmount;
        double difference = ceilAmount - absAmount;
        if (difference > 0) {
            WorkTimes workTimes = getWorkTimes(fromDate, ceilAmount.intValue(), workTimeContext);
            int size = workTimes.getWorkTimes().size();
            double differenceMinute = DateUtils.millisecondToMinute(Double
                    .valueOf(difference * workTimes.getWorkTimes().get(size - 1).getTimeInMillisecond()).longValue());
            totalWorkTimeMinutes = increment * (workTimes.getTotalWorkMinutes() - differenceMinute);
        } else {
            WorkTimes workTimes = getWorkTimes(fromDate, absAmount.intValue(), workTimeContext);
            totalWorkTimeMinutes = increment * workTimes.getTotalWorkMinutes();
        }
        return totalWorkTimeMinutes;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService#getActiveWorkTimePlanUuidById(java.lang.String, java.lang.String)
     */
    @Override
    public String getActiveWorkTimePlanUuidById(String workTimePlanId, String defaultWorkTimePlanUuid) {
        if (StringUtils.isBlank(workTimePlanId)) {
            return defaultWorkTimePlanUuid;
        }
        TsWorkTimePlanEntity workTimePlanEntity = workTimePlanService.getActiveWorkTimePlanById(workTimePlanId);
        if (workTimePlanEntity != null) {
            return workTimePlanEntity.getUuid();
        }
        return defaultWorkTimePlanUuid;
    }

    @Override
    public List<TsWorkTimePlanDto> getAllBySystem(List<String> system) {
        List<TsWorkTimePlanEntity> entities = workTimePlanService.getAllBySystem(system);
        return BeanUtils.copyCollection(entities, TsWorkTimePlanDto.class);
    }

}
