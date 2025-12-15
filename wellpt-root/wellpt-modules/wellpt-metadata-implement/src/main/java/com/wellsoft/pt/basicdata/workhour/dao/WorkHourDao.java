/*
 * @(#)2012-11-12 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.workhour.dao;

import com.wellsoft.pt.basicdata.workhour.entity.WorkHour;
import com.wellsoft.pt.jpa.dao.JpaDao;

import java.util.List;

/**
 * Description: 工作时间持久层访问类
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
public interface WorkHourDao extends JpaDao<WorkHour, String> {

    WorkHour getWorkHourByTypeAndCode(String type, String code);

    List<WorkHour> getWorkHourByType(String type);

    Boolean isWorkDayByCode(String code);

}
