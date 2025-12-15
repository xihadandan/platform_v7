/*
 * @(#)2021-10-15 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.dao.impl;

import com.wellsoft.pt.basicdata.printtemplate.dao.PrintTemplateCategoryDao;
import com.wellsoft.pt.basicdata.printtemplate.entity.PrintTemplateCategory;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PrintTemplateCategoryDaoImpl extends AbstractJpaDaoImpl<PrintTemplateCategory, String> implements PrintTemplateCategoryDao {
    private static final String QUERY_TOPLEVEL_CATEGORY = "from PrintTemplateCategory flow_category where flow_category.parent.uuid is null";

    /**
     * 获取最顶层的目录分类
     *
     * @return
     */
    public List<PrintTemplateCategory> getTopLevel() {
        return this.listByHQL(QUERY_TOPLEVEL_CATEGORY, null);
    }

    public PrintTemplateCategory getByCode(String code) {
        List<PrintTemplateCategory> flowCategories = this.listByFieldEqValue("code", code);
        return CollectionUtils.isNotEmpty(flowCategories) ? flowCategories.get(0) : null;
    }

    /**
     * 如何描述该方法
     *
     * @param unitId
     * @return
     */
    public List<PrintTemplateCategory> getAllByUnitId(String unitId) {
        List<Object> value = new ArrayList<Object>();
        value.add(unitId);
        value.add(MultiOrgSystemUnit.PT_ID);
        return this.listByFieldInValues("systemUnitId", value);
    }

    /**
     * 如何描述该方法
     *
     * @param systemUnitId
     * @return
     */
    public List<PrintTemplateCategory> getAsTreeAsyncByUnitId(String systemUnitId) {
        PrintTemplateCategory q = new PrintTemplateCategory();
        q.setSystemUnitId(systemUnitId);
        return this.listByEntity(q, null, "code asc", null);
    }
}
