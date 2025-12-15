/*
 * @(#)2012-11-12 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.workhour.dao.impl;

import com.wellsoft.pt.basicdata.workhour.dao.WorkHourDao;
import com.wellsoft.pt.basicdata.workhour.entity.WorkHour;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Repository
public class WorkHourDaoImpl extends AbstractJpaDaoImpl<WorkHour, String> implements WorkHourDao {

    /**
     * 判断是否为工作日的HQL
     */
    private static final String IS_WORK_DAY_BY_CODE = "select count(work_hour.uuid) from WorkHour work_hour where work_hour.code = :code and work_hour.isWorkday = :isWorkday";

    /**
     * 根据类型查询的HQL
     */
    private static final String GET_BY_TYPE = "from WorkHour work_hour where work_hour.type = :type";

    /**
     * 根据类型和代码查询工作日的HQL
     */
    private static final String GET_BY_TYPE_AND_CODE = "from WorkHour work_hour where work_hour.type = :type and work_hour.code = :code";

    /**
     * 根据指定编码，判断是否为工作日
     *
     * @param code
     * @return
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public Boolean isWorkDayByCode(String code) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("code", code);
        values.put("isWorkday", true);
        return (Long) this.getNumberByHQL(IS_WORK_DAY_BY_CODE, values) > 0;
    }

    /**
     * 根据指定类型，获取工作日列表
     *
     * @param type
     * @return
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public List<WorkHour> getWorkHourByType(String type) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("type", type);
        return this.listByHQL(GET_BY_TYPE, values);
    }

    /**
     * 根据指定类型和代码，获取工作日
     *
     * @param type
     * @return
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public WorkHour getWorkHourByTypeAndCode(String type, String code) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("type", type);
        values.put("code", code);
        return this.getOneByHQL(GET_BY_TYPE_AND_CODE, values);
    }

}
