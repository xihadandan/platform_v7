/*
 * @(#)2022-04-13 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.service.impl;

import com.wellsoft.pt.app.calendar.dao.AppBaseCalendarEventDao;
import com.wellsoft.pt.app.calendar.entity.AppBaseCalendarEventEntity;
import com.wellsoft.pt.app.calendar.service.BaseCalendarEventService;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarEventParams;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

/**
 * Description: 数据库表APP_BASE_CALENDAR_EVENT的service服务接口实现类
 *
 * @author shenhb
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-04-13.1	shenhb		2022-04-13		Create
 * </pre>
 * @date 2022-04-13
 */
@Service
public class AppBaseCalendarEventServiceImpl extends AbstractJpaServiceImpl<AppBaseCalendarEventEntity, AppBaseCalendarEventDao, String> implements BaseCalendarEventService {


    @Override
    public List<AppBaseCalendarEventEntity> loadEvents(CalendarEventParams ep, HashMap<String, Object> params) {
//        String userId = SpringSecurityUtils.getCurrentUserId();
        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        params.put("systemUnitId", systemUnitId);
        List<AppBaseCalendarEventEntity> objs = this.listByNameHQLQuery("queryAppBaseCalendarEventList", params);
        return objs;
    }

    @Override
    @Transactional
    public String saveEvent(AppBaseCalendarEventEntity appBaseCalendarEventEntity) {
        this.save(appBaseCalendarEventEntity);
        return System.currentTimeMillis() + "";
    }

    @Override
    @Transactional
    public void deleteEvent(String uuid) {
        this.delete(uuid);
    }

    @Override
    public AppBaseCalendarEventEntity getEvent(String uuid) {
        return this.getOne(uuid);
    }

    @Override
    public String getTableName() {
        return "app_base_calendar_event";
    }

}
