/*
 * @(#)8/13/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.store;

import com.wellsoft.context.jdbc.support.QueryItem;
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
public class CdDataDictionaryRefDataStoreQuery extends CdDataDictionaryUsedDataStoreQuery {

    /**
     * 返回查询名称
     *
     * @return
     */
    @Override
    public String getQueryName() {
        return "数据字典_模块引用_数据仓库查询";
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
                "cdDataDictionaryRefQuery",
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
                "cdDataDictionaryRefQuery", getQueryParams(queryContext));
    }

}
