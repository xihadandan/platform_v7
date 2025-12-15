/*
 * @(#)2021年5月24日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.dto.TsHolidayDto;
import com.wellsoft.pt.timer.entity.TsHolidayEntity;
import com.wellsoft.pt.timer.entity.TsHolidayInstanceEntity;
import com.wellsoft.pt.timer.enums.EnumCalendarType;
import com.wellsoft.pt.timer.facade.service.TsHolidayFacadeService;
import com.wellsoft.pt.timer.service.TsHolidayInstanceService;
import com.wellsoft.pt.timer.service.TsHolidayService;
import com.wellsoft.pt.timer.support.LunarCalendarUtils;
import com.wellsoft.pt.timer.support.TsHolidayUsedChecker;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
 * 2021年5月24日.1	zhulh		2021年5月24日		Create
 * </pre>
 * @date 2021年5月24日
 */
@Service
public class TsHolidayFacadeServiceImpl implements TsHolidayFacadeService {

    @Autowired
    private TsHolidayService holidayService;

    @Autowired
    private TsHolidayInstanceService holidayInstanceService;

    @Autowired
    private List<TsHolidayUsedChecker> holidayUsedCheckers;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsHolidayFacadeService#getAllBySystemUnitIdsLikeName(java.lang.String)
     */
    @Override
    public List<TsHolidayDto> getAllBySystemUnitIdsLikeName(String name) {
        List<String> systemUnitIds = new ArrayList<String>();
        systemUnitIds.add(MultiOrgSystemUnit.PT_ID);
        systemUnitIds.add(SpringSecurityUtils.getCurrentUserUnitId());
        List<TsHolidayEntity> entities = holidayService.getAllBySystemUnitIdsLikeName(systemUnitIds, name);
        return BeanUtils.copyCollection(entities, TsHolidayDto.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsHolidayFacadeService#getAllBySystemUnitIdsLikeFields(java.lang.String, java.lang.String)
     */
    @Override
    public List<TsHolidayDto> getAllBySystemUnitIdsLikeFields(String keyword, String tags) {
        List<String> systemUnitIds = new ArrayList<String>();
        systemUnitIds.add(MultiOrgSystemUnit.PT_ID);
        systemUnitIds.add(SpringSecurityUtils.getCurrentUserUnitId());
        List<TsHolidayEntity> entities = holidayService.getAllBySystemUnitIdsLikeFields(systemUnitIds, keyword, tags);
        return BeanUtils.copyCollection(entities, TsHolidayDto.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsHolidayFacadeService#getDto(java.lang.String)
     */
    @Override
    public TsHolidayDto getDto(String uuid) {
        TsHolidayDto holidayDto = new TsHolidayDto();
        TsHolidayEntity holidayEntity = holidayService.getOne(uuid);
        if (holidayEntity != null) {
            BeanUtils.copyProperties(holidayEntity, holidayDto);
        }
        return holidayDto;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsHolidayFacadeService#getHolidayInstanceDate(java.lang.String, java.lang.String)
     */
    @Override
    public String getHolidayInstanceDate(String uuid, String year) {
        TsHolidayInstanceEntity instanceEntity = holidayInstanceService.getByHolidayUuidAndYear(uuid, year);
        if (instanceEntity != null) {
            return instanceEntity.getInstanceDate();
        } else {
            TsHolidayEntity holidayEntity = holidayService.getOne(uuid);
            EnumCalendarType calendarType = EnumCalendarType.getByValue(holidayEntity.getCalendarType());
            // 新历
            if (calendarType != null && EnumCalendarType.Solar.equals(calendarType)) {
                return year + "-" + holidayEntity.getHolidayDate();
            } else {
                // 农历
                String[] days = StringUtils.split(holidayEntity.getHolidayDate(), "-");
                return LunarCalendarUtils.getSolar(Integer.valueOf(year), Integer.valueOf(days[0]),
                        Integer.valueOf(days[1]));
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsHolidayFacadeService#saveDto(com.wellsoft.pt.timer.dto.TsHolidayDto)
     */
    @Override
    public void saveDto(TsHolidayDto holidayDto) {
        TsHolidayEntity holidayEntity = new TsHolidayEntity();
        if (StringUtils.isNotBlank(holidayDto.getUuid())) {
            holidayEntity = holidayService.getOne(holidayDto.getUuid());
        } else {
            // ID唯一性判断
            if (this.holidayService.countById(holidayDto.getId()) > 0) {
                throw new RuntimeException("已经存在ID为[" + holidayDto.getId() + "]的节假日!");
            }
            holidayDto.setSystem(RequestSystemContextPathResolver.system());
            holidayDto.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        BeanUtils.copyProperties(holidayDto, holidayEntity, IdEntity.BASE_FIELDS);
        holidayService.save(holidayEntity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsHolidayFacadeService#deleteAll(java.util.List)
     */
    @Override
    public void deleteAll(List<String> uuids) {
        // 判断是否被引用
        if (isUsedByUuidsForDeleteAll(uuids)) {
            throw new BusinessException("节假日正在使用，无法删除！");
        }
        holidayService.deleteByUuids(uuids);
    }

    /**
     * @param uuids
     * @return
     */
    public boolean isUsedByUuidsForDeleteAll(List<String> uuids) {
        // 判断是否被引用
        for (String uuid : uuids) {
            for (TsHolidayUsedChecker checker : holidayUsedCheckers) {
                if (checker.isHolidayUsed(uuid)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsHolidayFacadeService#isUsedByUuids(java.util.List)
     */
    @Override
    public ResultMessage isUsedByUuids(List<String> uuids) {
        ResultMessage message = new ResultMessage();
        List<String> usedUuids = Lists.newArrayList();
        // 判断是否被引用
        for (String uuid : uuids) {
            for (TsHolidayUsedChecker checker : holidayUsedCheckers) {
                if (checker.isHolidayUsed(uuid)) {
                    usedUuids.add(uuid);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(usedUuids)) {
            List<TsHolidayEntity> holidayEntities = holidayService.listByUuids(usedUuids);
            StringBuilder sb = new StringBuilder("以下节假日正在使用，无法删除");
            for (TsHolidayEntity holidayEntity : holidayEntities) {
                sb.append(Separator.LINE.getValue());
                sb.append(holidayEntity.getName());
            }
            message.clear();
            message.setMsg(sb);
            message.setData(holidayEntities);
        }
        return message;
    }

    /**
     * 初始系统内置节假日
     *
     * @param system
     * @param tenant
     */
    @Override
    public synchronized void initBySystemAndTenant(String system, String tenant) {
        long count = holidayService.countBySystemAndTenant(system, tenant);
        if (count > 0) {
            return;
        }

        String[][] initHolidays = {{"元旦", "NewYear", "1", "01-01", "1月1日", "法定节假日"},
                {"春节", "SpringFestival", "2", "01-01", "正月初一", "法定节假日"},
                {"清明节", "TombSweepingDay", "1", "04-05", "4月5日", "法定节假日"},
                {"劳动节", "InternationalLabourDay", "1", "05-01", "5月1日", "法定节假日"},
                {"端午节", "DragonBoatFestival", "2", "05-05", "五月初五", "法定节假日"},
                {"中秋节", "Mid-AutumnFestival", "2", "08-15", "八月十五", "法定节假日"},
                {"国庆节", "NationalDay", "1", "10-01", "10月1日", "法定节假日"}};

        List<TsHolidayEntity> holidayEntities = Lists.newArrayList();
        for (int index = 0; index < initHolidays.length; index++) {
            String[] holiday = initHolidays[index];
            TsHolidayEntity holidayEntity = new TsHolidayEntity();
            holidayEntity.setName(holiday[0]);
            holidayEntity.setId(holiday[1]);
            holidayEntity.setCalendarType(holiday[2]);
            holidayEntity.setHolidayDate(holiday[3]);
            holidayEntity.setHolidayDateName(holiday[4]);
            holidayEntity.setTag(holiday[5]);
            holidayEntity.setRemark(holiday[4] + holiday[0]);
            holidayEntity.setSystem(system);
            holidayEntity.setTenant(tenant);
            holidayEntities.add(holidayEntity);
        }
        holidayService.saveAll(holidayEntities);
    }

}
