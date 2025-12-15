/*
 * @(#)Mar 29, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query.api.impl;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.bpm.engine.enums.WorkFlowAclSid;
import com.wellsoft.pt.bpm.engine.org.service.WorkflowOrgService;
import com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery;
import com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQueryItem;
import com.wellsoft.pt.jpa.query.AbstractQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Mar 29, 2018.1	zhulh		Mar 29, 2018		Create
 * </pre>
 * @date Mar 29, 2018
 */
@Service
@Scope(value = "prototype")
public class FlowDefinitionQueryImpl extends AbstractQuery<FlowDefinitionQuery, FlowDefinitionQueryItem> implements
        FlowDefinitionQuery {

//    @Autowired
//    private OrgApiFacade orgApiFacade;

    @Autowired
    private WorkflowOrgService workflowOrgService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#count()
     */
    @Override
    @Transactional(readOnly = true)
    public long count() {
        return this.nativeDao.countByNamedQuery("listFlowDefinitionQuery", values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#uniqueResult()
     */
    @Override
    @Transactional(readOnly = true)
    public FlowDefinitionQueryItem uniqueResult() {
        return this.nativeDao.findUniqueByNamedQuery("listFlowDefinitionQuery", values, FlowDefinitionQueryItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#list()
     */
    @Override
    @Transactional(readOnly = true)
    public List<FlowDefinitionQueryItem> list() {
        return list(FlowDefinitionQueryItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#list(java.lang.Class)
     */
    @Override
    @Transactional(readOnly = true)
    public <ITEM extends Serializable> List<ITEM> list(Class<ITEM> itemClass) {
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setPageSize(maxResults);
        pagingInfo.setFirst(firstResult);
        pagingInfo.setAutoCount(false);
        return this.nativeDao.namedQuery("listFlowDefinitionQuery", values, itemClass, pagingInfo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#uuid(java.lang.String)
     */
    @Override
    public FlowDefinitionQuery uuid(String uuid) {
        return addParameter("uuid", uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#uuids(java.util.Collection)
     */
    @Override
    public FlowDefinitionQuery uuids(Collection<String> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return this;
        }
        return addParameter("uuids", uuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#id(java.lang.String)
     */
    @Override
    public FlowDefinitionQuery id(String id) {
        return addParameter("id", id);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#ids(java.util.Collection)
     */
    @Override
    public FlowDefinitionQuery ids(Collection<String> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return this;
        }
        return addParameter("ids", ids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#name(java.lang.String)
     */
    @Override
    public FlowDefinitionQuery name(String name) {
        return addParameter("name", name);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#category(java.lang.String)
     */
    @Override
    public FlowDefinitionQuery category(String category) {
        return addParameter("category", category);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#systemUnitId(java.lang.String)
     */
    @Override
    public FlowDefinitionQuery systemUnitId(String systemUnitId) {
        return addParameter("systemUnitId", systemUnitId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#systemUnitIds(java.util.Collection)
     */
    @Override
    public FlowDefinitionQuery systemUnitIds(Collection<String> systemUnitIds) {
        if (CollectionUtils.isEmpty(systemUnitIds)) {
            return this;
        }
        return addParameter("systemUnitIds", systemUnitIds);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#idLike(java.lang.String)
     */
    @Override
    public FlowDefinitionQuery idLike(String id) {
        return addParameter("idLike", id);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#nameLike(java.lang.String)
     */
    @Override
    public FlowDefinitionQuery nameLike(String name) {
        return addParameter("nameLike", name);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#categoryLike(java.lang.String)
     */
    @Override
    public FlowDefinitionQuery categoryLike(String category) {
        return addParameter("categoryLike", category);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#enabled(boolean)
     */
    @Override
    public FlowDefinitionQuery enabled(boolean enabled) {
        return addParameter("enabled", Boolean.TRUE.equals(enabled) ? 1 : 0);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#mobileShow(boolean)
     */
    @Override
    public FlowDefinitionQuery mobileShow(boolean mobileShow) {
        return addParameter("mobileShow", Boolean.TRUE.equals(mobileShow) ? 1 : 0);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#permission(java.lang.String, java.util.Collection)
     */
    @Override
    public FlowDefinitionQuery permission(String userId, Collection<Permission> permissions) {
        boolean widthPermission = permissions != null && !permissions.isEmpty();
        // 是否带权限查询
        if (widthPermission) {
            List<Integer> masks = new ArrayList<Integer>();
            for (Permission permission : permissions) {
                masks.add(permission.getMask());
            }

//            Set<String> orgIds = orgApiFacade.getUserOrgIds(userId);
            Set<String> orgIds = workflowOrgService.getUserRelatedIds(userId);
            List<String> sids = new ArrayList<String>();
            for (String orgId : orgIds) {
                String sid = orgId;
                if (sid.startsWith(IdPrefix.USER.getValue())) {
                    continue;
                }
                sid = WorkFlowAclSid.ROLE_FLOW_CREATOR + "_" + sid;
                sids.add(sid);
            }
            if (!sids.contains(userId)) {
                sids.add(userId);
            }
            sids.add(WorkFlowAclSid.ROLE_FLOW_ALL_CREATOR.name());

            addParameter("widthPermission", permissions != null && !permissions.isEmpty());
            addParameter("userId", userId);
            addParameter("sids", sids);
            addParameter("masks", masks);
        }
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#distinctVersion()
     */
    @Override
    public FlowDefinitionQuery distinctVersion() {
        return addParameter("distinctVersion", "true");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#orderByCodeAsc()
     */
    @Override
    public FlowDefinitionQuery orderByCodeAsc() {
        return addOrderBy("t1.code", "asc");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#orderByCodeDesc()
     */
    @Override
    public FlowDefinitionQuery orderByCodeDesc() {
        return addOrderBy("t1.code", "desc");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#orderByCategoryAsc()
     */
    @Override
    public FlowDefinitionQuery orderByCategoryAsc() {
        return addOrderBy("t1.category", "asc");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#orderByCategoryDesc()
     */
    @Override
    public FlowDefinitionQuery orderByCategoryDesc() {
        return addOrderBy("t1.category", "desc");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#order(java.lang.String)
     */
    @Override
    public FlowDefinitionQuery order(String orderBy) {
        if (StringUtils.isNotBlank(orderBy)) {
            if (StringUtils.startsWith(StringUtils.lowerCase(orderBy).trim(), "order")) {
                return addParameter("orderString", orderBy);
            } else {
                return addParameter("orderString", "order by " + orderBy);
            }
        }
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.FlowDefinitionQuery#recentUse()
     */
    @Override
    public FlowDefinitionQuery recentUse() {
        return addParameter("recentUse", true);
    }

    @Override
    public FlowDefinitionQuery moduleId(String moduleId) {
        return addParameter("moduleId", moduleId);
    }

    /**
     * 归属系统
     *
     * @param system
     */
    @Override
    public FlowDefinitionQuery system(String system) {
        return addParameter("systemId", system);
    }

}
