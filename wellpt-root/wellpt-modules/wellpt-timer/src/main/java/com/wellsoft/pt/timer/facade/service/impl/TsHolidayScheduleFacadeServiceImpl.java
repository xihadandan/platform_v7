/*
 * @(#)2021年5月24日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.timer.dto.TsHolidayPerYearScheduleCountDto;
import com.wellsoft.pt.timer.dto.TsHolidayScheduleDto;
import com.wellsoft.pt.timer.entity.TsHolidayScheduleEntity;
import com.wellsoft.pt.timer.facade.service.TsHolidayFacadeService;
import com.wellsoft.pt.timer.facade.service.TsHolidayScheduleFacadeService;
import com.wellsoft.pt.timer.facade.service.TsWorkTimePlanFacadeService;
import com.wellsoft.pt.timer.query.TsHolidayPerYearScheduleCountQueryItem;
import com.wellsoft.pt.timer.query.TsHolidayScheduleQueryItem;
import com.wellsoft.pt.timer.service.TsHolidayScheduleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
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
 * 2021年5月24日.1	zhulh		2021年5月24日		Create
 * </pre>
 * @date 2021年5月24日
 */
@Service
public class TsHolidayScheduleFacadeServiceImpl implements TsHolidayScheduleFacadeService {

    @Autowired
    private TsHolidayScheduleService holidayScheduleService;

    @Autowired
    private TsHolidayFacadeService holidayFacadeService;

    @Autowired
    private TsWorkTimePlanFacadeService workTimePlanFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsHolidayScheduleFacadeService#listAllYear()
     */
    @Override
    public List<TsHolidayPerYearScheduleCountDto> listAllYear() {
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        List<TsHolidayPerYearScheduleCountQueryItem> queryItems = holidayScheduleService
                .listHolidayPerYearScheduleCountBySystemUnitId(systemUnitId);
        List<TsHolidayPerYearScheduleCountDto> dtos = BeanUtils.copyCollection(queryItems,
                TsHolidayPerYearScheduleCountDto.class);
        // 节假日安排为空，添加5年的空数据
        List<TsHolidayPerYearScheduleCountDto> futureScheduleCounts = getFuture5YearScheduleCount();
        for (TsHolidayPerYearScheduleCountDto futureScheduleCount : futureScheduleCounts) {
            if (!dtos.contains(futureScheduleCount)) {
                dtos.add(futureScheduleCount);
            }
        }
        // 按看份升序
        Collections.sort(dtos);
        return dtos;
    }

    /**
     * 返回默认的未来5年数量
     *
     * @return
     */
    private List<TsHolidayPerYearScheduleCountDto> getFuture5YearScheduleCount() {
        List<TsHolidayPerYearScheduleCountDto> dtos = Lists.newArrayList();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int index = 0; index < 5; index++) {
            TsHolidayPerYearScheduleCountDto dto = new TsHolidayPerYearScheduleCountDto();
            dto.setYear(Integer.valueOf(year + index).toString());
            dto.setCount(0);
            dtos.add(dto);
        }
        return dtos;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsHolidayScheduleFacadeService#listByYear(java.lang.String)
     */
    @Override
    public List<TsHolidayScheduleDto> listByYear(String year) {
        List<TsHolidayScheduleQueryItem> queryItems = holidayScheduleService.listByYear(year);
        for (TsHolidayScheduleQueryItem item : queryItems) {
            // 计算节假日实例日期
            if (item.getHolidayInstanceDate() == null) {
                String holidayInstanceDate = holidayFacadeService.getHolidayInstanceDate(item.getHolidayUuid(),
                        item.getYear());
                item.setHolidayInstanceDate(holidayInstanceDate);
            }
        }
        return BeanUtils.copyCollection(queryItems, TsHolidayScheduleDto.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsHolidayScheduleFacadeService#saveAllDtos(java.lang.String, java.util.List)
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void saveAllDtos(String year, List<TsHolidayScheduleDto> holidayDtos) {
        List<TsHolidayScheduleQueryItem> queryItems = holidayScheduleService.listByYear(year);
        List<String> deleteUuids = queryItems.stream().map(s -> s.getUuid()).collect(Collectors.toList());
        List<String> savedUuids = holidayDtos.stream().filter(s -> StringUtils.isNotBlank(s.getUuid()))
                .map(s -> s.getUuid()).collect(Collectors.toList());
        deleteUuids.removeAll(savedUuids);
        for (TsHolidayScheduleDto holidayScheduleDto : holidayDtos) {
            saveDto(holidayScheduleDto);
            // 根据节假日安排自动更新配置信息
            workTimePlanFacadeService.autoUpdateByHolidaySchedule(holidayScheduleDto);
        }
        deleteAll(deleteUuids);
    }

    /**
     * @param holidayScheduleDto
     */
    private void saveDto(TsHolidayScheduleDto holidayScheduleDto) {
        TsHolidayScheduleEntity holidayScheduleEntity = new TsHolidayScheduleEntity();
        if (StringUtils.isNotBlank(holidayScheduleDto.getUuid())) {
            holidayScheduleEntity = holidayScheduleService.getOne(holidayScheduleDto.getUuid());
            holidayScheduleDto.setSystem(holidayScheduleEntity.getSystem());
            holidayScheduleDto.setTenant(holidayScheduleEntity.getTenant());
        } else {
            holidayScheduleDto.setSystem(RequestSystemContextPathResolver.system());
            holidayScheduleDto.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        BeanUtils.copyProperties(holidayScheduleDto, holidayScheduleEntity, IdEntity.BASE_FIELDS);
        holidayScheduleService.save(holidayScheduleEntity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsHolidayScheduleFacadeService#deleteAll(java.util.List)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void deleteAll(List<String> uuids) {
        holidayScheduleService.deleteByUuids(uuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsHolidayScheduleFacadeService#getByHolidayAndYear(java.lang.String, java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public TsHolidayScheduleDto getByHolidayAndYear(String holidayUuid, String year) {
        TsHolidayScheduleDto dto = new TsHolidayScheduleDto();
        TsHolidayScheduleEntity entity = holidayScheduleService.getByHolidayAndYear(holidayUuid, year);
        if (entity != null) {
            BeanUtils.copyProperties(entity, dto);
        }
        return dto;
    }

}
