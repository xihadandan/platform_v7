/*
 * @(#)8/13/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.springframework.stereotype.Component;

import java.util.List;

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
public class CdDataDictionaryUsedDataStoreQuery extends CdDataDictionaryDataStoreQuery {

    /**
     * 返回查询名称
     *
     * @return
     */
    @Override
    public String getQueryName() {
        return "数据字典_使用情况_数据仓库查询";
    }

    /**
     * 返回查询信息元数据
     *
     * @param context
     * @return
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = super.initCriteriaMetadata(context);
        criteriaMetadata.add("functionUuid", "t.function_uuid", "使用功能UUID", String.class);
        criteriaMetadata.add("functionName", "t.function_name", "使用功能名称", String.class);
        criteriaMetadata.add("functionItemName", "t.function_item_name", "使用功能子项名称", String.class);
        criteriaMetadata.add("functionType", "t.function_type", "使用功能类型", String.class);
        criteriaMetadata.add("functionModuleName", "t.function_module_name", "使用功能所在模块名称", String.class);
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
                "cdDataDictionaryUsedQuery",
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
                "cdDataDictionaryUsedQuery", getQueryParams(queryContext));
    }

}
