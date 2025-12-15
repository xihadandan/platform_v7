/*
 * @(#)2012-12-3 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.dao.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.workflow.dao.FlowCategoryDao;
import com.wellsoft.pt.workflow.entity.FlowCategory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 工作流分类持久层操作类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-3.1	zhulh		2012-12-3		Create
 * </pre>
 * @date 2012-12-3
 */
@Repository
public class FlowCategoryDaoImpl extends AbstractJpaDaoImpl<FlowCategory, String> implements FlowCategoryDao {
    private static final String QUERY_TOPLEVEL_CATEGORY = "from FlowCategory flow_category where flow_category.parent.uuid is null";

    /**
     * 获取最顶层的目录分类
     *
     * @return
     */
    public List<FlowCategory> getTopLevel() {
        String hql = QUERY_TOPLEVEL_CATEGORY;
        String system = RequestSystemContextPathResolver.system();
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.isNotBlank(system)) {
            hql += " and flow_category.system = :system";
            params.put("system", system);
        }
        return this.listByHQL(hql, params);
    }

    /**
     * @param categoryId
     * @return
     */
    public FlowCategory getByCode(String code) {
        List<FlowCategory> flowCategories = this.listByFieldEqValue("code", code);
        return CollectionUtils.isNotEmpty(flowCategories) ? flowCategories.get(0) : null;
    }

    /**
     * 如何描述该方法
     *
     * @param unitId
     * @return
     */
    public List<FlowCategory> getAllByUnitId(String unitId) {
        List<Object> value = new ArrayList<Object>();
        value.add(unitId);
        value.add(MultiOrgSystemUnit.PT_ID);
        String system = RequestSystemContextPathResolver.system();
        Map<String, Object> params = Maps.newHashMap();
        params.put("systemUnitIds", value);
        params.put("systemId", system);
        String hql = "from FlowCategory t where t.systemUnitId in(:systemUnitIds)";
        if (StringUtils.isNotBlank(system)) {
            hql += " and t.system = :systemId";
        }
//        return this.listByFieldInValues("systemUnitId", value);
        return this.listByHQL(hql, params);
    }

    /**
     * 如何描述该方法
     *
     * @param systemUnitId
     * @return
     */
    public List<FlowCategory> getAsTreeAsyncByUnitId(String systemUnitId) {
        FlowCategory q = new FlowCategory();
        q.setSystemUnitId(systemUnitId);
        return this.listByEntity(q, null, "code asc", null);
    }
}
