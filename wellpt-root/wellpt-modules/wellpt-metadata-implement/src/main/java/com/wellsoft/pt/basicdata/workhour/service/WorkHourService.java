/*
 * @(#)2012-11-12 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.workhour.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.pt.basicdata.workhour.dao.WorkHourDao;
import com.wellsoft.pt.basicdata.workhour.entity.WorkHour;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.basicdata.workhour.support.WorkPeriod;
import com.wellsoft.pt.basicdata.workhour.support.WorkingHour;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Date;
import java.util.List;

/**
 * Description: 工作时间服务类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-12.1	zhulh		2012-11-12		Create
 * </pre>
 * @date 2012-11-12
 */
public interface WorkHourService extends JpaService<WorkHour, WorkHourDao, String> {

    /**
     * 根据主键删除工作时间配置
     *
     * @param uid
     */
    void removeByPk(String uid);

    /**
     * 获取指定工作日的信息
     *
     * @param date
     * @return
     */
    WorkingHour getWorkingHour(Date date);

    /**
     * 获取当前日期或将来日期有效的工作日的信息
     *
     * @param date
     * @return
     */
    WorkingHour getEffectiveWorkingHour(Date date);

    /**
     * 判断是否为工作时间
     *
     * @param date
     * @return
     */
    boolean isWorkHour(Date date);

    /**
     * 判断是否为工作日
     *
     * @param date
     * @return
     */
    boolean isWorkDay(Date date);

    /**
     * 返回有效的工作时间点
     *
     * @param date   开始时间点
     * @param amount
     * @param type   工作日、小时时间单位
     * @return
     */
    Date getWorkDate(Date date, Double amount, WorkUnit workingUnit);

    Date getWorkDate(Date date, Double amount, Integer workingUnit);

    /**
     * 返回下一工作日的工作时间点
     *
     * @param date
     * @return
     */
    Date getNextWorkDate(Date date);

    /**
     * 返回有效工作时间区段(单位秒)以及经历几个工作日
     *
     * @param fromTime
     * @param toTime
     * @return
     */
    WorkPeriod getWorkPeriod(Date fromTime, Date toTime);

    /**
     * 固定节假日列表查询
     *
     * @param queryInfo
     * @return
     */
    JqGridQueryData queryFixedHolidaysList(JqGridQueryInfo queryInfo);

    /**
     * 特殊节假日列表查询
     *
     * @param queryInfo
     * @return
     */
    JqGridQueryData querySpecialHolidaysList(JqGridQueryInfo queryInfo);

    /**
     * 补班日期列表查询
     *
     * @param queryInfo
     * @return
     */
    JqGridQueryData queryMakeUpList(JqGridQueryInfo queryInfo);

    /**
     * 返回上一工作日的工作时间点
     *
     * @param date 指定的日期
     * @return
     */
    Date getPrevWorkDate(Date date);

    /**
     * 获取指定月份的工作日
     *
     * @param month 指定月份(包含年月) 格式：(yyyy-MM)
     * @return 返回指定月份的所有工作日
     */
    List<Date> getWorkDatesByMonth(String month);

    /**
     * @param notChedkedVal
     * @return
     */
    WorkHour getByCode(String code);

    /**
     * 通过code和单位找到记录
     *
     * @param code
     * @param unit
     * @return
     */
    WorkHour getByCodeAndUnitId(String code, String unitId);

    /**
     * 通过unit_id 查找工作日集合
     *
     * @param unitId
     * @return
     */
    List<WorkHour> getWorkDayList(String unitId);
}
