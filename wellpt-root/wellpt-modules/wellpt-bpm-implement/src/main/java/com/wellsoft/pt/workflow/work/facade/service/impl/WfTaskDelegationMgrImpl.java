/*
 * @(#)2016-07-04 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.facade.service.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryData;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.service.CommonQueryService;
import com.wellsoft.pt.bpm.engine.entity.TaskDelegation;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.workflow.work.bean.TaskDelegationBean;
import com.wellsoft.pt.workflow.work.facade.service.WfTaskDelegationMgr;
import com.wellsoft.pt.workflow.work.service.WfTaskDelegationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
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
 * 2016-07-04.1	zhulh		2016-07-04		Create
 * </pre>
 * @date 2016-07-04
 */
@Service
public class WfTaskDelegationMgrImpl extends BaseServiceImpl implements WfTaskDelegationMgr {

    @Autowired
    private WfTaskDelegationService wfTaskDelegationService;
    @Autowired
    private CommonQueryService commonQueryService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.facade.service.WfTaskDelegationMgr#query(com.wellsoft.pt.core.support.QueryInfo)
     */
    @Override
    @Transactional()
    public QueryData query(QueryInfo queryInfo) {
        // 设置查询字段条件
        Map<String, Object> values = PropertyFilter.convertToMap(queryInfo.getPropertyFilters());
        String orderBy = queryInfo.getOrderBy();
        if (StringUtils.isNotBlank(orderBy)) {
            orderBy = StringUtils.replace(orderBy, "consignorName", "consignor_name");
            orderBy = StringUtils.replace(orderBy, "trusteeName", "trustee_name");
            orderBy = StringUtils.replace(orderBy, "flowTitle", "flow_title");
            orderBy = StringUtils.replace(orderBy, "taskName", "task_name");
            orderBy = StringUtils.replace(orderBy, "dueToTakeBackWork", "due_to_take_back_work");
            orderBy = StringUtils.replace(orderBy, "completionState", "completion_state");
            orderBy = StringUtils.replace(orderBy, "fromTime", "from_time");
            orderBy = StringUtils.replace(orderBy, "toTime", "to_time");
        }
        values.put("orderBy", orderBy);
        List<TaskDelegationBean> results = this.nativeDao.namedQuery("taskDelegataionQuery", values,
                TaskDelegationBean.class, queryInfo.getPagingInfo());
        QueryData queryData = new QueryData();
        queryData.setDataList(BeanUtils.convertCollection(results, TaskDelegationBean.class));
        queryData.setPagingInfo(queryInfo.getPagingInfo());
        return queryData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.facade.service.WfTaskDelegationMgr#getBean(java.lang.String)
     */
    @Override
    public TaskDelegationBean getBean(String uuid) {
        TaskDelegation entity = wfTaskDelegationService.get(uuid);
        TaskDelegationBean bean = new TaskDelegationBean();
        BeanUtils.copyProperties(entity, bean);
        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.facade.service.WfTaskDelegationMgr#saveBean(com.wellsoft.pt.workflow.work.bean.TaskDelegationBean)
     */
    @Override
    @Transactional
    public void saveBean(TaskDelegationBean bean) {
        String uuid = bean.getUuid();
        TaskDelegation entity = new TaskDelegation();
        if (StringUtils.isNotBlank(uuid)) {
            entity = wfTaskDelegationService.get(uuid);
        }
        BeanUtils.copyProperties(bean, entity);
        wfTaskDelegationService.save(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.facade.service.WfTaskDelegationMgr#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        wfTaskDelegationService.delete(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.facade.service.WfTaskDelegationMgr#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<String> uuids) {
        wfTaskDelegationService.deleteByUuids(Lists.newArrayList(uuids));
    }

}
