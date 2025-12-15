/*
 * @(#)2014-8-12 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.api.WellptQueryRequest;
import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.domain.Task;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.TaskQueryRequest;
import com.wellsoft.pt.api.response.TaskQueryResponse;
import com.wellsoft.pt.bpm.engine.FlowEngine;
import com.wellsoft.pt.bpm.engine.query.api.TaskQuery;
import com.wellsoft.pt.bpm.engine.query.api.TaskQueryItem;
import com.wellsoft.pt.security.acl.support.AclPermission;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.security.acls.model.Permission;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
 * 2014-8-12.1	zhulh		2014-8-12		Create
 * </pre>
 * @date 2014-8-12
 */
@Service(ApiServiceName.TASK_QUERY)
public class TaskQueryServiceImpl implements WellptService<TaskQueryRequest> {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(TaskQueryRequest queryRequest) {
        TaskQuery taskQuery = preTaskQuery(queryRequest);
        List<TaskQueryItem> queryItems = taskQuery.list();
        long total = taskQuery.count();

        List<Task> tasks = Lists.newArrayList();
        for (TaskQueryItem taskQueryItem : queryItems) {
            Task task = new Task();
            task.setUuid(taskQueryItem.getUuid());
            task.setFlowInstUuid(taskQueryItem.getFlowInstUuid());
            task.setTitle(taskQueryItem.getTitle());
            task.setId(taskQueryItem.getTaskId());
            task.setName(taskQueryItem.getTaskName());
            task.setPreOperatorName(taskQueryItem.getPreOperatorName());

            task.setStartTime(taskQueryItem.getStartTime());
            task.setFormUuid(taskQueryItem.getFormUuid());
            task.setDataUuid(taskQueryItem.getDataUuid());
            tasks.add(task);
        }
        TaskQueryResponse response = new TaskQueryResponse();
        response.setStart(taskQuery.getFirstResult());
        response.setSize(taskQuery.getMaxResults());
        response.setTotal(total);
        response.setDataList(tasks);
        return response;
    }

    /**
     * @param queryRequest
     * @return
     */
    protected TaskQuery preTaskQuery(WellptQueryRequest<?> queryRequest) {
        PagingInfo pagingInfo = new PagingInfo();
        pagingInfo.setCurrentPage(queryRequest.getPageNo());
        pagingInfo.setPageSize(queryRequest.getPageSize());

        String userId = SpringSecurityUtils.getCurrentUserId();
        TaskQuery taskQuery = FlowEngine.getInstance().createQuery(TaskQuery.class);
        // 查询条件
        taskQuery.setLikeQueryConnector("and");
        BeanWrapper beanWrapper = new BeanWrapperImpl(queryRequest);
        String taskId = ObjectUtils.toString(beanWrapper.getPropertyValue("id"));
        if (StringUtils.isNotBlank(taskId)) {
            taskQuery.taskId(taskId);
        }
        String taskName = ObjectUtils.toString(beanWrapper.getPropertyValue("name"));
        if (StringUtils.isNotBlank(taskName)) {
            taskQuery.taskNameLike(taskName);
        }
        String flowDefId = ObjectUtils.toString(beanWrapper.getPropertyValue("flowDefinitionId"));
        if (StringUtils.isNotBlank(flowDefId)) {
            taskQuery.flowDefId(flowDefId);
        }
        String flowInstUuid = ObjectUtils.toString(beanWrapper.getPropertyValue("flowInstUuid"));
        if (StringUtils.isNotBlank(flowInstUuid)) {
            taskQuery.flowInstUuid(flowInstUuid);
        }
        String preOperatorName = ObjectUtils.toString(beanWrapper.getPropertyValue("preOperatorName"));
        if (StringUtils.isNotBlank(preOperatorName)) {
            taskQuery.preOperatorNameLike(preOperatorName);
        }
        String title = ObjectUtils.toString(beanWrapper.getPropertyValue("title"));
        if (StringUtils.isNotBlank(title)) {
            taskQuery.titleLike(title);
        }

        // 待办权限
        List<Permission> permissions = Lists.newArrayList();
        permissions.addAll(getPermissions());
        taskQuery.permission(userId, permissions);
        // 分页
        taskQuery.setFirstResult(pagingInfo.getFirst());
        taskQuery.setMaxResults(pagingInfo.getPageSize());
        // 排序
        order(taskQuery);
        return taskQuery;
    }

    /**
     * @return
     */
    protected List<Permission> getPermissions() {
        //        return Arrays.asList(AclPermission.TODO, AclPermission.DONE, AclPermission.UNREAD, AclPermission.FLAG_READ,
        //                AclPermission.ATTENTION, AclPermission.SUPERVISE, AclPermission.MONITOR);
        return Arrays.asList(AclPermission.TODO);
    }

    /**
     * @param taskQuery
     */
    protected void order(TaskQuery taskQuery) {
        taskQuery.orderByStartTimeDesc();
    }

}
