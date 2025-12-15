/*
 * @(#)2022-04-13 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.service;


import com.wellsoft.pt.app.calendar.dao.AppBaseCalendarEventDao;
import com.wellsoft.pt.app.calendar.entity.AppBaseCalendarEventEntity;
import com.wellsoft.pt.jpa.service.JpaService;

/**
 * Description: 数据库表APP_BASE_CALENDAR_EVENT的service服务接口
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
public interface AppBaseCalendarEventService extends JpaService<AppBaseCalendarEventEntity, AppBaseCalendarEventDao, String> {

}
