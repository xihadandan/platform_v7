/*
 * @(#)2018年2月27日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.facade.service;

import com.wellsoft.context.component.select2.Select2GroupData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.calendar.bean.CalendarRights;
import com.wellsoft.pt.app.calendar.entity.MyCalendarEntity;

import java.util.List;
import java.util.Map;

/**
 * Description: 公共通讯录标签门面服务
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
public interface CalendarFacade extends BaseService {

    // 获取我的日历本列表
    public List<MyCalendarEntity> queryAppMyCalendarList();

    // 获取我负责的他人日历本列表
    public List<MyCalendarEntity> queryMyManageCalendarList();

    // 同时获取我的日历本列表和负责的他人日历本列表，以分组selec2方式展示
    public Select2GroupData queryCalendarListForSelect2Group(Select2QueryInfo queryInfo);

    // 更新事项状态
    public void updateEventStatus(String uuid, Integer status);

    Boolean deleteEventsByRepeatMarkId(String repeatMarkId);

    // 获取日历本分组的信息
    Map<String, String> getGroupDataMap();

    // 获取用户对日历本的权限配置
    CalendarRights queryCalendarPrivilege(String calendarUuid);

    // 获取授权给我的可添加的日历本列表
    Select2GroupData queryMyManagerCalendarListByActionForSelect2Group(Select2QueryInfo queryInfo);

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<MyCalendarEntity> queryAppMyCalendarListAndCheckDefaultCalendar();

    /**
     * 获取附件信息
     *
     * @param fileIds
     * @return
     */
    List<Double> getFileSizesByIds(String fileIds);

    //时间段冲突 未结束且开始时间和结束时间重叠 除自身除外 返回提示语
    ResultMessage judgeConflictZone(String startDate, String endDate, String dataUuid);

}
