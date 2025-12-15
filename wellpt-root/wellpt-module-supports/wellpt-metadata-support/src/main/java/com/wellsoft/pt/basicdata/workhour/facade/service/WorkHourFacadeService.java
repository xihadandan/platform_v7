/*
 * @(#)2018年4月19日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.workhour.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.context.util.groovy.GroovyUseable;
import com.wellsoft.pt.basicdata.workhour.entity.WorkHour;
import com.wellsoft.pt.basicdata.workhour.enums.WorkUnit;
import com.wellsoft.pt.basicdata.workhour.support.WorkPeriod;

import java.util.Date;
import java.util.List;

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
@GroovyUseable
public interface WorkHourFacadeService extends Facade {

    /**
     * @return
     */
    @Deprecated
    List<WorkHour> getAll();

    /**
     * 查询当前系统单位的所有工作时间
     *
     * @return
     */
    List<WorkHour> listCurrentUnitWorkHours();

    /**
     * @param currentTime
     * @return
     */
    boolean isWorkHour(Date currentTime);

    /**
     * @param currentTime
     * @param d
     * @param workinghour
     * @return
     */
    Date getWorkDate(Date currentTime, double d, WorkUnit workinghour);

    /**
     * @param fromTime
     * @param toTime
     * @return
     */
    WorkPeriod getWorkPeriod(Date fromTime, Date toTime);

}
