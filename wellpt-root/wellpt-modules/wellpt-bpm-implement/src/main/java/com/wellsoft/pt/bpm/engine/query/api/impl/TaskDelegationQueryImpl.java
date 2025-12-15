/*
 * @(#)2016年7月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query.api.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.bpm.engine.query.api.TaskDelegationQuery;
import com.wellsoft.pt.bpm.engine.query.api.TaskDelegationQueryItem;
import com.wellsoft.pt.jpa.query.AbstractQuery;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
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
 * 2016年7月13日.1	zhulh		2016年7月13日		Create
 * </pre>
 * @date 2016年7月13日
 */
@Service
@Transactional(readOnly = true)
@Scope(value = "prototype")
public class TaskDelegationQueryImpl extends AbstractQuery<TaskDelegationQuery, TaskDelegationQueryItem> implements
        TaskDelegationQuery {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#count()
     */
    @Override
    public long count() {
        return this.nativeDao.countByNamedQuery("listTaskDelegationQuery", values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#uniqueResult()
     */
    @Override
    public TaskDelegationQueryItem uniqueResult() {
        return this.nativeDao.findUniqueByNamedQuery("listTaskDelegationQuery", values, TaskDelegationQueryItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#list()
     */
    @Override
    public List<TaskDelegationQueryItem> list() {
        return list(TaskDelegationQueryItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#list(java.lang.Class)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> list(Class<ITEM> itemClass) {
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setPageSize(maxResults);
        pagingInfo.setFirst(firstResult);
        pagingInfo.setAutoCount(false);
        return this.nativeDao.namedQuery("listTaskDelegationQuery", values, itemClass, pagingInfo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskDelegationQuery#consignor(java.lang.String)
     */
    @Override
    public TaskDelegationQuery consignor(String consignor) {
        return addParameter("consignor", consignor);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskDelegationQuery#trustee(java.lang.String)
     */
    @Override
    public TaskDelegationQuery trustee(String trustee) {
        return addParameter("trustee", trustee);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskDelegationQuery#completionState(java.lang.Integer[])
     */
    @Override
    public TaskDelegationQuery completionState(Integer... completionStates) {
        for (Integer completionState : completionStates) {
            addParameterList("completionStates", completionState);
        }
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskDelegationQuery#projection(java.lang.String)
     */
    @Override
    public TaskDelegationQuery projection(String projection) {
        addParameter("projectionClause", projection);
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskDelegationQuery#where(java.lang.String, java.util.Map)
     */
    @Override
    public TaskDelegationQuery where(String whereSql, Map<String, Object> params) {
        if (StringUtils.contains(whereSql, "<")) {
            try {
                addParameter("whereSql", TemplateEngineFactory.getDefaultTemplateEngine().process(whereSql, params));
            } catch (Exception e) {
                addParameter("whereSql", whereSql);
            }
        } else {
            addParameter("whereSql", whereSql);
        }
        for (Entry<String, Object> entry : params.entrySet()) {
            addParameter(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskDelegationQuery#order(java.lang.String)
     */
    @Override
    public TaskDelegationQuery order(String orderBy) {
        addParameter("orderString", orderBy);
        return this;
    }

}
