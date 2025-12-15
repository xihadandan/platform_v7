/*
 * @(#)2019年5月29日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.store;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimaps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月29日.1	zhulh		2019年5月29日		Create
 * </pre>
 * @date 2019年5月29日
 */
@Component
public class AppPageDefinitionManagerDataStoreQuery extends AbstractDataStoreQueryInterface {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_产品集成_页面";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t.uuid", "UUID", String.class);
        criteriaMetadata.add("createTime", "t.create_time", "创建时间", String.class);
        criteriaMetadata.add("creator", "t.creator", "创建人", String.class);
        criteriaMetadata.add("modifier", "t.modifier", "修改人", String.class);
        criteriaMetadata.add("modifyTime", "t.modify_time", "修改时间", Date.class);
        criteriaMetadata.add("recVer", "t.rec_ver", "recVer", Integer.class);
        criteriaMetadata.add("name", "t.name", "名称", String.class);
        criteriaMetadata.add("id", "t.id", "ID", String.class);
        criteriaMetadata.add("version", "t.version", "版本号", String.class);
        criteriaMetadata.add("code", "t.code", "编号", String.class);
        criteriaMetadata.add("wtype", "t.wtype", "页面容器类型", String.class);
        criteriaMetadata.add("title", "t.title", "标题", String.class);
        criteriaMetadata.add("shared", "t.shared", "是否共享的页面", Boolean.class);
        criteriaMetadata.add("isDefault", "t.is_default", "是否默认的页面", Boolean.class);
        criteriaMetadata.add("remark", "t.remark", "备注", String.class);
        criteriaMetadata.add("systemUnitId", "t.system_unit_id", "归属系统单位ID", String.class);
        criteriaMetadata.add("isRef", "t.is_ref", "是否引用", Boolean.class);
        criteriaMetadata.add("maxVersionPageUuid", "max_version_page_uuid", "最新版本页面UUID", String.class);
        criteriaMetadata.add("appPiUuid", "t.app_pi_uuid", "产品集成信息UUID", String.class);
        criteriaMetadata.add("isPc", "t.is_Pc", "pc端状态", String.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface#query(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> queryItems = queryContext.getNativeDao().namedQuery("appPageDefinitionManagerQuery",
                getQueryParams(queryContext), QueryItem.class, queryContext.getPagingInfo());
        // 标记最新版本的页面UUID
        markMaxVersionPageUuid(queryItems);
        return queryItems;
    }

    /**
     * @param queryItems
     */
    private void markMaxVersionPageUuid(List<QueryItem> queryItems) {
        // 按上级环节实例UUID分组
        ImmutableListMultimap<String, QueryItem> immutableListMultimap = Multimaps.index(queryItems.iterator(),
                new Function<QueryItem, String>() {

                    @Override
                    public String apply(QueryItem input) {
                        return input.getString("id");
                    }

                });
        ImmutableMap<String, Collection<QueryItem>> immutableMap = immutableListMultimap.asMap();
        for (Entry<String, Collection<QueryItem>> entry : immutableMap.entrySet()) {
            Collection<QueryItem> collection = entry.getValue();
            if (CollectionUtils.size(collection) > 1) {
                markMaxVersionPageUuid(collection);
            }
        }
    }

    /**
     * @param collection
     */
    private void markMaxVersionPageUuid(Collection<QueryItem> queryItems) {
        QueryItem maxVersionQueryItem = getMaxVersionQueryItem(queryItems);
        String maxVersionPageUuid = maxVersionQueryItem.getString("uuid");
        for (QueryItem queryItem : queryItems) {
            if (!maxVersionQueryItem.equals(queryItem)) {
                queryItem.put("max_version_page_uuid", maxVersionPageUuid);
            }
        }
    }

    /**
     * @param queryItems
     * @return
     */
    private QueryItem getMaxVersionQueryItem(Collection<QueryItem> queryItems) {
        double maxVersion = 0;
        QueryItem maxVersionQueryItem = null;
        for (QueryItem queryItem : queryItems) {
            String version = queryItem.getString("version");
            if (version == null) {
                version = "1";
            }
            if (Double.valueOf(version) > maxVersion) {
                maxVersion = Double.valueOf(version);
                maxVersionQueryItem = queryItem;
            }
        }
        return maxVersionQueryItem;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        return queryContext.getNativeDao().countByNamedQuery("appPageDefinitionManagerQuery",
                getQueryParams(queryContext));
    }

    /**
     * @param queryContext
     * @return
     */
    private Map<String, Object> getQueryParams(QueryContext queryContext) {
        Map<String, Object> queryParams = queryContext.getQueryParams();
        queryParams.put("keyword", queryContext.getKeyword());
        queryParams.put("whereSql", queryContext.getWhereSqlString());
        queryParams.put("orderBy", queryContext.getOrderString());
        return queryParams;
    }

}
