/*
 * @(#)2020年1月3日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.store;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.multi.org.service.MultiOrgOptionService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MultiOrgOptionDataStoreQuery extends AbstractDataStoreQueryInterface {

    @Autowired
    private MultiOrgOptionService multiOrgOptionService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "组织管理_组织选择项";
    }

    /**
     *
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata metadata = CriteriaMetadata.createMetadata();
        metadata.add("uuid", "uuid", "UUID", String.class);
        metadata.add("name", "name", "名称", String.class);
        metadata.add("id", "id", "ID", String.class);
        metadata.add("code", "code", "编号", String.class);

        metadata.add("isEnable", "isEnable", "状态", String.class);
        metadata.add("isShow", "isShow", "默认显示", String.class);
        metadata.add("style", "style", "展现样式", String.class);
        return metadata;
    }

    /**
     *
     */
    @Override
    public long count(QueryContext context) {
        query(context, false);
        return context.getPagingInfo().getTotalCount();
    }

    /**
     *
     */
    @Override
    public List<QueryItem> query(QueryContext context) {
        return query(context, true);
    }

    public List<QueryItem> query(QueryContext context, boolean isQuery) {
        NativeDao nativeDao = context.getNativeDao();
        Map<String, Object> queryParams = context.getQueryParams();
        queryParams.put("keyword", context.getKeyword());
        // queryParams.put("whereSql", context.getWhereSqlString());
        queryParams.put("orderBy", context.getOrderString());
        List<QueryItem> list = nativeDao.namedQuery("queryMultiOrgOptions", queryParams, QueryItem.class,
                context.getPagingInfo());
        if (isQuery && CollectionUtils.isNotEmpty(list)) {
            for (QueryItem queryItem : list) {
                String id = queryItem.getString(QueryItem.getKey("id"));
                queryItem.put("style", multiOrgOptionService.getOptionStyle(id));
            }
        }

        return list;
    }
}
