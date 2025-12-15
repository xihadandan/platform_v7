/*
 * @(#)8/13/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.stereotype.Component;

import java.util.Date;
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
 * 8/13/23.1	zhulh		8/13/23		Create
 * </pre>
 * @date 8/13/23
 */
@Component
public class CdDataDictionaryDataStoreQuery extends AbstractDataStoreQueryInterface {

    /**
     * 返回查询名称
     *
     * @return
     */
    @Override
    public String getQueryName() {
        return "数据字典_数据仓库查询";
    }

    /**
     * 返回查询信息元数据
     *
     * @param context
     * @return
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t.uuid", "UUID", Long.class);
        criteriaMetadata.add("createTime", "t.create_time", "创建时间", String.class);
        criteriaMetadata.add("creator", "t.creator", "创建人", String.class);
        criteriaMetadata.add("modifier", "t.modifier", "修改人", String.class);
        criteriaMetadata.add("modifyTime", "t.modify_time", "修改时间", Date.class);
        criteriaMetadata.add("recVer", "t.rec_ver", "recVer", Integer.class);
        criteriaMetadata.add("system", "t.system", "归属系统", String.class);
        criteriaMetadata.add("tenant", "t.tenant", "归属租户", String.class);
        criteriaMetadata.add("name", "t.name", "字典名称", String.class);
        criteriaMetadata.add("code", "t.code", "字典编码", String.class);
        criteriaMetadata.add("extDefinitionJson", "t.ext_definition_json", "扩展定义JSON信息", String.class);
        criteriaMetadata.add("moduleId", "t.module_id", "模块ID", String.class);
        criteriaMetadata.add("categoryUuid", "t.category_uuid", "字典分类UUID", Long.class);
        criteriaMetadata.add("remark", "t.remark", "备注", String.class);
        criteriaMetadata.add("modifierName", "t.modifier_name", "修改人名称", String.class);
        criteriaMetadata.add("categoryName", "t.category_name", "分类名称", String.class);
        criteriaMetadata.add("categoryType", "t.category_type", "分类类型", String.class);
        criteriaMetadata.add("moduleName", "t.module_name", "模块名称", String.class);
        return criteriaMetadata;
    }

    /**
     * 如何描述该方法
     *
     * @param queryContext
     * @return
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> queryItems = queryContext.getNativeDao().namedQuery(
                "cdDataDictionaryQuery",
                getQueryParams(queryContext), QueryItem.class, queryContext.getPagingInfo());
        return queryItems;
    }

    /**
     * 返回查询的条数
     *
     * @param queryContext
     * @return
     */
    @Override
    public long count(QueryContext queryContext) {
        return queryContext.getNativeDao().countByNamedQuery(
                "cdDataDictionaryQuery", getQueryParams(queryContext));
    }

    /**
     * @param queryContext
     * @return
     */
    protected Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> queryParams = queryContext.getQueryParams();
        queryParams.put("keyword", queryContext.getKeyword());
        queryParams.put("whereSql", queryContext.getWhereSqlString());
        queryParams.put("orderBy", queryContext.getOrderString());
        return queryParams;
    }

}
