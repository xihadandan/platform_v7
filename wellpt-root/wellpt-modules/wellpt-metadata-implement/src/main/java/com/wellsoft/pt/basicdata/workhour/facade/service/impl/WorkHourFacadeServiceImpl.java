/*
 * @(#)2018年4月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.workhour.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.basicdata.workhour.entity.WorkHour;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.basicdata.workhour.facade.service.WorkHourFacadeService;
import com.wellsoft.pt.basicdata.workhour.service.WorkHourService;
import com.wellsoft.pt.basicdata.workhour.support.WorkPeriod;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月19日.1	chenqiong		2018年4月19日		Create
 * </pre>
 * @date 2018年4月19日
 */
@Service
public class WorkHourFacadeServiceImpl extends AbstractApiFacade implements WorkHourFacadeService {

    @Autowired
    WorkHourService workhourService;

    @Override
    public List<WorkHour> getAll() {
        return listCurrentUnitWorkHours();
    }

    @Override
    public List<WorkHour> listCurrentUnitWorkHours() {
        List<WorkHour> workHourList = Lists.newArrayList();
        Map<String, Object> params = Maps.newHashMap();
        params.put("unitId", SpringSecurityUtils.getCurrentUserUnitId());
        String hql = "from WorkHour where unitId =:unitId";
        workHourList.addAll(
                workhourService.listByHQL(hql, params));
        return workHourList;
    }

    @Override
    public boolean isWorkHour(Date currentTime) {
        return workhourService.isWorkDay(currentTime);
    }

    @Override
    public Date getWorkDate(Date date, double amount, WorkUnit workinghour) {
        return workhourService.getWorkDate(date, amount, workinghour);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.workhour.facade.service.WorkHourFacadeService#getWorkPeriod(java.util.Date, java.util.Date)
     */
    @Override
    public WorkPeriod getWorkPeriod(Date fromTime, Date toTime) {
        return workhourService.getWorkPeriod(fromTime, toTime);
    }

}
