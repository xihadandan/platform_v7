/*
 * @(#)2013-2-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.dao;

import com.wellsoft.pt.org.entity.Unit;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-18.1	zhulh		2013-2-18		Create
 * </pre>
 * @date 2013-2-18
 */
@Repository
public class UnitDao extends OrgHibernateDao<Unit, String> {

    private static final String QUERY_TOPLEVEL_POSITION = "from Unit unit where unit.parent.uuid is null or unit.parent.uuid ='' order by code asc";
    private static final String QUERY_TOPLEVEL_CATEGORY = "from Unit unit where unit.type = 1 and unit.parent.uuid is null or unit.parent.uuid ='' order by code asc";
    private static final String QUERY_UNIT = "from Unit unit where unit.type = 2 order by code asc";

    /**
     * @return
     */
    public List<Unit> getTopLevel() {
        Map<String, Object> values = new HashMap<String, Object>();
        return this.find(QUERY_TOPLEVEL_POSITION, values);
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    public List<Unit> getTopLevelCategory() {
        Map<String, Object> values = new HashMap<String, Object>();
        return this.find(QUERY_TOPLEVEL_CATEGORY, values);
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    public List<Unit> getUnits() {
        Map<String, Object> values = new HashMap<String, Object>();
        return this.find(QUERY_UNIT, values);
    }

    /**
     * 如何描述该方法
     *
     * @param id
     * @return
     */
    public Unit getById(String id) {
        return this.findUniqueBy("id", id);
    }

}
