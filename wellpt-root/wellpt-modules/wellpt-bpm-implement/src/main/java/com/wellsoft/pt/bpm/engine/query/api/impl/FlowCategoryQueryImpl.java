/*
 * @(#)2020年12月10日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query.api.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.bpm.engine.query.api.FlowCategoryQuery;
import com.wellsoft.pt.bpm.engine.query.api.FlowCategoryQueryItem;
import com.wellsoft.pt.jpa.query.AbstractQuery;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
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
 * 2020年12月10日.1	zhulh		2020年12月10日		Create
 * </pre>
 * @date 2020年12月10日
 */
@Service
@Transactional(readOnly = true)
@Scope(value = "prototype")
public class FlowCategoryQueryImpl extends AbstractQuery<FlowCategoryQuery, FlowCategoryQueryItem> implements
        FlowCategoryQuery {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.query.Query#count()
     */
    @Override
    public long count() {
        return this.nativeDao.countByNamedQuery("listFlowCategoryQuery", values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.query.Query#uniqueResult()
     */
    @Override
    public FlowCategoryQueryItem uniqueResult() {
        return this.nativeDao.findUniqueByNamedQuery("listFlowCategoryQuery", values, FlowCategoryQueryItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.query.Query#list()
     */
    @Override
    public List<FlowCategoryQueryItem> list() {
        return list(FlowCategoryQueryItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.query.Query#list(java.lang.Class)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> list(Class<ITEM> itemClass) {
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setPageSize(maxResults);
        pagingInfo.setFirst(firstResult);
        pagingInfo.setAutoCount(false);
        return this.nativeDao.namedQuery("listFlowCategoryQuery", values, itemClass, pagingInfo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowCategoryQuery#uuid(java.lang.String)
     */
    @Override
    public FlowCategoryQuery uuid(String uuid) {
        return addParameter("uuid", uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowCategoryQuery#uuids(java.util.Collection)
     */
    @Override
    public FlowCategoryQuery uuids(Collection<String> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return this;
        }
        return addParameter("uuids", uuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowCategoryQuery#name(java.lang.String)
     */
    @Override
    public FlowCategoryQuery name(String name) {
        return addParameter("name", name);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowCategoryQuery#systemUnitId(java.lang.String)
     */
    @Override
    public FlowCategoryQuery systemUnitId(String systemUnitId) {
        return addParameter("systemUnitId", systemUnitId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowCategoryQuery#systemUnitIds(java.util.Collection)
     */
    @Override
    public FlowCategoryQuery systemUnitIds(Collection<String> systemUnitIds) {
        if (CollectionUtils.isEmpty(systemUnitIds)) {
            return this;
        }
        return addParameter("systemUnitIds", systemUnitIds);
    }

    @Override
    public FlowCategoryQuery systemId(String systemId) {
        return addParameter("systemId", systemId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowCategoryQuery#nameLike(java.lang.String)
     */
    @Override
    public FlowCategoryQuery nameLike(String name) {
        return addParameter("nameLike", name);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowCategoryQuery#orderByCodeAsc()
     */
    @Override
    public FlowCategoryQuery orderByCodeAsc() {
        return addOrderBy("t1.code", "asc");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowCategoryQuery#orderByCodeDesc()
     */
    @Override
    public FlowCategoryQuery orderByCodeDesc() {
        return addOrderBy("t1.code", "desc");
    }

}
