/*
 * @(#)2015-10-19 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query.api.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.bpm.engine.query.api.TaskOperationQuery;
import com.wellsoft.pt.bpm.engine.query.api.TaskOperationQueryItem;
import com.wellsoft.pt.jpa.query.AbstractQuery;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 流程操作查询
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-10-19.1	zhulh		2015-10-19		Create
 * </pre>
 * @date 2015-10-19
 */
@Service
@Transactional(readOnly = true)
@Scope(value = "prototype")
public class TaskOperationQueryImpl extends AbstractQuery<TaskOperationQuery, TaskOperationQueryItem> implements
        TaskOperationQuery {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#count()
     */
    @Override
    public long count() {
        return this.nativeDao.countByNamedQuery("listTaskOperationQuery", values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#uniqueResult()
     */
    @Override
    public TaskOperationQueryItem uniqueResult() {
        return this.nativeDao.findUniqueByNamedQuery("listTaskOperationQuery", values, TaskOperationQueryItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.Query#list()
     */
    @Override
    public List<TaskOperationQueryItem> list() {
        return list(TaskOperationQueryItem.class);
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
        return this.nativeDao.namedQuery("listTaskOperationQuery", values, itemClass, pagingInfo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskOperationQuery#action(java.lang.String)
     */
    @Override
    public TaskOperationQuery action(String action) {
        return addParameter("action", action);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskOperationQuery#actionType(java.lang.String)
     */
    @Override
    public TaskOperationQuery actionType(String actionType) {
        return addParameter("actionType", actionType);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskOperationQuery#actionCode(java.lang.String)
     */
    @Override
    public TaskOperationQuery actionCode(Integer actionCode) {
        return addParameter("actionCode", actionCode);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskOperationQuery#opinionValue(java.lang.String)
     */
    @Override
    public TaskOperationQuery opinionValue(String opinionValue) {
        return addParameter("opinionValue", opinionValue);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskOperationQuery#opinionLabel(java.lang.String)
     */
    @Override
    public TaskOperationQuery opinionLabel(String opinionLabel) {
        return addParameter("opinionLabel", opinionLabel);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskOperationQuery#opinionText(java.lang.String)
     */
    @Override
    public TaskOperationQuery opinionText(String opinionText) {
        return addParameter("opinionText", opinionText);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskOperationQuery#opinionTextLike(java.lang.String)
     */
    @Override
    public TaskOperationQuery opinionTextLike(String opinionText) {
        return addParameter("opinionTextLike", opinionText);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskOperationQuery#operatorId(java.lang.String)
     */
    @Override
    public TaskOperationQuery operatorId(String operatorId) {
        return addParameter("operatorId", operatorId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskOperationQuery#taskId(java.lang.String)
     */
    @Override
    public TaskOperationQuery taskId(String taskId) {
        return addParameter("taskId", taskId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskOperationQuery#taskName(java.lang.String)
     */
    @Override
    public TaskOperationQuery taskName(String taskName) {
        return addParameter("taskName", taskName);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskOperationQuery#taskInstUuid(java.lang.String)
     */
    @Override
    public TaskOperationQuery taskInstUuid(String taskInstUuid) {
        return addParameter("taskInstUuid", taskInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskOperationQuery#flowInstUuid(java.lang.String)
     */
    @Override
    public TaskOperationQuery flowInstUuid(String flowInstUuid) {
        return addParameter("flowInstUuid", flowInstUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskOperationQuery#flowInstUuids(java.util.List)
     */
    @Override
    public TaskOperationQuery flowInstUuids(List<String> flowInstUuids) {
        return addParameter("flowInstUuids", flowInstUuids);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskOperationQuery#orderByCreateTimeAsc()
     */
    @Override
    public TaskOperationQuery orderByCreateTimeAsc() {
        return addOrderBy("create_time", "asc");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.query.api.TaskOperationQuery#orderByCreateTimeDesc()
     */
    @Override
    public TaskOperationQuery orderByCreateTimeDesc() {
        return addOrderBy("create_time", "desc");
    }

}
