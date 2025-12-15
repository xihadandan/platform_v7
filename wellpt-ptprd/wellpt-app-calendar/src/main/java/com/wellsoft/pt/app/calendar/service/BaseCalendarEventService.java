/*
 * @(#)2018年2月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.service;

import com.wellsoft.pt.app.calendar.entity.AppBaseCalendarEventEntity;
import com.wellsoft.pt.basicdata.viewcomponent.facade.service.CalendarEventService;

/**
 * Description: 我的日程事项
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年2月27日.1	chenqiong		2018年2月27日		Create
 * </pre>
 * @date 2018年2月27日
 */
public interface BaseCalendarEventService extends CalendarEventService<AppBaseCalendarEventEntity> {

//	/**
//	 * 通过repeatMarkId,批量删除事件
//	 *
//	 * @param repeatMarkId
//	 */
//	void deleteEventByRepeatMarkId(String repeatMarkId);
//
//	void updateStatus(String uuid, Integer status);
//
//	// 删除日历本对应的所有事项
//	void deleteEventByCalendarUuid(String calendarUuid);
//
//	List<MyCalendarEventEntity> queryAppMyCalendarEventListBySection(String startDate, String endDate);

}
