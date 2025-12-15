/*
 * @(#)2018年2月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.service;

import com.wellsoft.pt.app.calendar.bean.CalendarRights;
import com.wellsoft.pt.app.calendar.dao.MyCalendarDao;
import com.wellsoft.pt.app.calendar.entity.MyCalendarEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Collection;
import java.util.List;

/**
 * Description: 公共通讯录标签服务类
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
public interface MyCalendarService extends JpaService<MyCalendarEntity, MyCalendarDao, String> {

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<MyCalendarEntity> queryAppMyCalendarList();

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<MyCalendarEntity> queryMyManageCalendarList();

    /**
     * 获取用户对该日历本的权限
     *
     * @param calendarUuid
     * @return
     */
    CalendarRights queryCalendarPrivilege(String calendarUuid);

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<MyCalendarEntity> queryMyAllowAddCalendarList();

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<MyCalendarEntity> queryMyAllowEditCalendarList();

    // 是否有查看日程事项的权限
    boolean isHasViewEventPrivilege(String userId, String calendarUuid);

    MyCalendarEntity getDefaultCalendar(String userId);

    /**
     * 获取附件信息
     *
     * @param fileIds
     * @return
     */
    List<Double> getFileSizesByIds(String fileIds);

    public abstract void removeAllByPk(Collection<String> uids);

    public abstract String generateCalendarCategoryCode();

    public abstract MyCalendarEntity getBean(String uuid);

    public abstract void saveBean(MyCalendarEntity bean);

}
