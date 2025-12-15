/*
 * @(#)2019年5月29日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.manager.store;

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
 * 2019年5月29日.1	zhulh		2019年5月29日		Create
 * </pre>
 * @date 2019年5月29日
 */
@Component
public class AppFunctionManagerDataStoreQuery extends AbstractDataStoreQueryInterface {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#getQueryName()
     */
    @Override
    public String getQueryName() {
        return "平台管理_产品集成_功能";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#initCriteriaMetadata(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext queryContext) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t1.uuid", "UUID", String.class);
        criteriaMetadata.add("createTime", "t2.create_time", "创建时间", String.class);
        criteriaMetadata.add("creator", "t2.creator", "创建人", String.class);
        criteriaMetadata.add("modifier", "t2.modifier", "修改人", String.class);
        criteriaMetadata.add("modifyTime", "t2.modify_time", "修改时间", Date.class);
        criteriaMetadata.add("recVer", "t2.rec_ver", "recVer", Integer.class);
        criteriaMetadata.add("name", "t1.name", "名称", String.class);
        criteriaMetadata.add("id", "t1.id", "ID", String.class);
        criteriaMetadata.add("type", "t1.type", "类型", String.class);
        criteriaMetadata.add("code", "t1.code", "编号", String.class);
        criteriaMetadata.add("remark", "t1.remark", "备注", String.class);
        criteriaMetadata.add("appPiUuid", "t2.uuid", "产品集成信息UUID", String.class);
        criteriaMetadata.add("parentAppPiUuid", "t2.parent_uuid", "上级产品集成信息UUID", String.class);
        criteriaMetadata.add("isProtected", "t2.is_protected", "资源是否受保护", Boolean.class);
        criteriaMetadata.add("sortOrder", "t2.sort_order", "排序号", Integer.class);
        return criteriaMetadata;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface#query(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public List<QueryItem> query(QueryContext queryContext) {
        List<QueryItem> queryItems = queryContext.getNativeDao().namedQuery("appFunctionManagerQuery",
                getQueryParams(queryContext), QueryItem.class, queryContext.getPagingInfo());
        return queryItems;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.criteria.QueryInterface#count(com.wellsoft.pt.jpa.criteria.QueryContext)
     */
    @Override
    public long count(QueryContext queryContext) {
        return queryContext.getNativeDao().countByNamedQuery("appFunctionManagerQuery", getQueryParams(queryContext));
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
