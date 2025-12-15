/*
 * @(#)2021年6月4日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.timer.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.timer.entity.TsHolidayEntity;
import com.wellsoft.pt.timer.entity.TsHolidayInstanceEntity;
import com.wellsoft.pt.timer.facade.service.TsHolidayFacadeService;
import com.wellsoft.pt.timer.facade.service.TsHolidayInstanceFacadeService;
import com.wellsoft.pt.timer.service.TsHolidayInstanceService;
import com.wellsoft.pt.timer.service.TsHolidayService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
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
 * 2021年6月4日.1	zhulh		2021年6月4日		Create
 * </pre>
 * @date 2021年6月4日
 */
@Service
public class TsHolidayInstanceFacadeServiceImpl implements TsHolidayInstanceFacadeService {

    @Autowired
    private TsHolidayService holidayService;

    @Autowired
    private TsHolidayFacadeService holidayFacadeService;

    @Autowired
    private TsHolidayInstanceService holidayInstanceService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.timer.facade.service.TsHolidayInstanceFacadeService#syncHolidayInstances()
     */
    @Override
    public void syncHolidayInstances() {
        String year = Calendar.getInstance().get(Calendar.YEAR) + StringUtils.EMPTY;
        List<TsHolidayInstanceEntity> instanceEntities = Lists.newArrayList();
        List<TsHolidayEntity> holidayEntities = holidayService.listAll();
        for (TsHolidayEntity holidayEntity : holidayEntities) {
            TsHolidayInstanceEntity instanceEntity = new TsHolidayInstanceEntity();
            BeanUtils.copyProperties(holidayEntity, instanceEntity, IdEntity.BASE_FIELDS);
            instanceEntity.setHolidayUuid(holidayEntity.getUuid());
            instanceEntity.setYear(year);
            instanceEntity.setInstanceDate(holidayFacadeService.getHolidayInstanceDate(holidayEntity.getUuid(), year));
            instanceEntities.add(instanceEntity);
        }

        // 更新节假日实例
        List<TsHolidayInstanceEntity> toUpdateInstanceEntities = Lists.newArrayList();
        List<String> existsUuids = Lists.newArrayList();
        for (TsHolidayInstanceEntity instanceEntity : instanceEntities) {
            TsHolidayInstanceEntity entity = holidayInstanceService
                    .getByHolidayUuidAndYear(instanceEntity.getHolidayUuid(), year);
            if (entity != null) {
                existsUuids.add(entity.getUuid());
                BeanUtils.copyProperties(instanceEntity, entity, IdEntity.BASE_FIELDS);
            } else {
                toUpdateInstanceEntities.add(instanceEntity);
            }
        }
        // 删除当前年份已经不存在的节假日对应的节假日实例
        holidayInstanceService.deleteAllExcludeUuidsInYear(existsUuids, year);

        // 保存节假日实例
        holidayInstanceService.saveAll(toUpdateInstanceEntities);
    }

}
