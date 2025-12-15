/*
 * @(#)2013-3-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.pt.bpm.engine.dao.TaskInstanceDao;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.query.FlowInstanceSystemQueryItem;
import com.wellsoft.pt.bpm.engine.service.TaskInstanceService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-3-17.1	zhulh		2013-3-17		Create
 * </pre>
 * @date 2013-3-17
 */
@Service
public class TaskInstanceServiceImpl extends AbstractJpaServiceImpl<TaskInstance, TaskInstanceDao, String>
        implements TaskInstanceService {

    private static final String COUNT_TODO_TASKS = "select count(task_instance.uuid) from TaskInstance task_instance where task_instance.flowInstance.uuid = :flowInstUuid and task_instance.endTime is null";

    private static final String GET_BY_FLOW_INST_UUID = "from TaskInstance t where t.flowInstance.uuid = :flowInstUuid";

    private static final String GET_BY_ORDER_DESC_FLOW_INST_UUID = "from TaskInstance t where t.flowInstance.uuid = :flowInstUuid order by t.createTime desc";

    private static final String GET_BY_PARENT_TASK_INST_UUID = "from TaskInstance t where t.suspensionState = 0 and t.parent.uuid = :parentTaskInstUuid";

    private static final String REMOVE_BY_FLOW_INST_UUID = "delete from TaskInstance t where t.flowInstance.uuid = :flowInstUuid";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskInstanceService#get(java.lang.String)
     */
    @Override
    public TaskInstance get(String uuid) {
        return this.dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskInstanceService#getByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<TaskInstance> getByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.listByHQL(GET_BY_FLOW_INST_UUID, values);
    }

    @Override
    public List<TaskInstance> getOrderDescByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.listByHQL(GET_BY_ORDER_DESC_FLOW_INST_UUID, values);
    }

    /**
     * 根据任务id获取子任务列表
     *
     * @param parentTaskInstUuid parentTaskInstUuid
     * @return List<TaskInstance>
     */
    @Override
    public List<TaskInstance> getNormalByParentTaskInstUuid(String parentTaskInstUuid) {
        Map<String, Object> values = new HashMap<>();
        values.put("parentTaskInstUuid", parentTaskInstUuid);
        return this.dao.listByHQL(GET_BY_PARENT_TASK_INST_UUID, values);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskInstanceService#removeByFlowInstUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void removeByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        this.dao.deleteByHQL(REMOVE_BY_FLOW_INST_UUID, values);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskInstanceService#find(java.lang.String, java.util.Map)
     */
    @Override
    public List<TaskInstance> find(String hql, Map<String, Object> values) {
        Assert.hasText(hql, "hql不能为空");
        Query query = dao.getSession().createQuery(hql);
        if (values != null) {
            query.setProperties(values);
        }
        return query.list();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskInstanceService#listCharSequenceByHQL(java.lang.String, java.util.Map)
     */
    @Override
    public List<String> listCharSequenceByHQL(String hql, Map<String, Object> values) {
        return this.dao.listCharSequenceByHQL(hql, values);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskInstanceService#merge(com.wellsoft.pt.bpm.engine.entity.TaskInstance)
     */
    @Override
    @Transactional
    public void merge(TaskInstance taskInstance) {
        this.dao.getSession().merge(taskInstance);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskInstanceService#query(com.wellsoft.context.jdbc.support.Page, java.lang.String, java.lang.Object[])
     */
    @Override
    public void query(Page<TaskInstance> taskPage, String hql, Object... values) {
        dao.query(taskPage, hql, values);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskInstanceService#getByDateUuid(java.lang.String, java.lang.String)
     */
    @Override
    public List<TaskInstance> getByDateUuid(String formUuid, String dataUuid) {
        return dao.getByDateUuid(formUuid, dataUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskInstanceService#countUnfinishedTasks(java.lang.String)
     */
    @Override
    public long countUnfinishedTasks(String flowInstUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.countByHQL(COUNT_TODO_TASKS, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskInstanceService#countUnfinishedTasksByFlowDefUuidAndTaskIds(java.lang.String, java.util.Set)
     */
    @Override
    public long countUnfinishedTasksByFlowDefUuidAndTaskIds(String flowDefUuid, Set<String> taskIds) {
        String hql = "select count(t.uuid) from TaskInstance t where t.flowDefinition.uuid = :flowDefUuid and t.id in(:taskIds) and t.endTime is null";
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowDefUuid", flowDefUuid);
        values.put("taskIds", taskIds);
        return this.dao.countByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskInstanceService#getFlowInstUUidByTaskInstUuid(java.lang.String)
     */
    @Override
    public String getFlowInstUUidByTaskInstUuid(String taskInstUuid) {
        String hql = "select t.flowInstance.uuid from TaskInstance t where t.uuid = :taskInstUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("taskInstUuid", taskInstUuid);
        List<String> flowInstUuids = this.dao.listCharSequenceByHQL(hql, values);
        if (CollectionUtils.isNotEmpty(flowInstUuids)) {
            return flowInstUuids.get(0);
        }
        return null;
    }

    @Override
    public FlowInstanceSystemQueryItem getFlowInstanceSystemQueryItemByTaskInstUuid(String taskInstUuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("taskInstUuid", taskInstUuid);
        List<FlowInstanceSystemQueryItem> queryItems = this.dao.listItemByNameHQLQuery("flowInstanceSystemQuery", FlowInstanceSystemQueryItem.class, params, null);
        return queryItems.get(0);
    }


    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskInstanceService#listTodoUserIdsByParentFlowInstUuidWithExcludeFlowInstUuid(java.lang.String, java.lang.String)
     */
    @Override
    public List<String> listTodoUserIdsByParentFlowInstUuidWithExcludeFlowInstUuid(String parentFlowInstUuid,
                                                                                   String excludeFlowInstUuid) {
        Set<String> todoUserIds = Sets.newHashSet();
        Map<String, Object> values = Maps.newHashMap();
        values.put("parentFlowInstUuid", parentFlowInstUuid);
        values.put("excludeFlowInstUuid", excludeFlowInstUuid);
        List<TaskInstance> taskInstances = this.dao
                .listByNameSQLQuery("listTodoUserIdsByParentFlowInstUuidWithExcludeFlowInstUuid", values);
        for (TaskInstance taskInstance : taskInstances) {
            String todoUserId = taskInstance.getTodoUserId();
            if (StringUtils.isBlank(taskInstance.getTodoUserId())) {
                continue;
            }
            todoUserIds.addAll(Arrays.asList(StringUtils.split(todoUserId, Separator.SEMICOLON.getValue())));
        }
        return Arrays.asList(todoUserIds.toArray(new String[0]));
    }

    @Override
    public String getFlowDefUUidByTaskInstUuid(String taskInstUuid) {
        Assert.hasLength(taskInstUuid, "环节实例UUID不能为空！");

        String hql = "select t.flowDefinition.uuid from TaskInstance t where t.uuid = :taskInstUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("taskInstUuid", taskInstUuid);
        List<String> flowDefUuids = this.dao.listCharSequenceByHQL(hql, values);
        if (CollectionUtils.isNotEmpty(flowDefUuids)) {
            return flowDefUuids.get(0);
        }
        return null;
    }

    @Override
    public List<String> listIdByFlowInstUuid(String flowInstUuid) {
        Assert.hasLength(flowInstUuid, "流程实例UUID不能为空！");

        String hql = "select distinct t.id as id from TaskInstance t where t.flowInstance.uuid = :flowInstUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.listCharSequenceByHQL(hql, values);
    }

    @Override
    public TaskInstance getLastTaskInstanceByTaskIdAndFlowInstUuid(String taskId, String flowInstUuid) {
        Assert.hasLength(taskId, "环节ID不能为空！");
        Assert.hasLength(flowInstUuid, "流程实例UUID不能为空！");

        String hql = "from TaskInstance t where t.id = :taskId and t.flowInstance.uuid = :flowInstUuid order by t.createTime desc";
        Map<String, Object> values = Maps.newHashMap();
        values.put("taskId", taskId);
        values.put("flowInstUuid", flowInstUuid);
        List<TaskInstance> taskInstances = this.dao.listByHQL(hql, values);
        if (CollectionUtils.isNotEmpty(taskInstances)) {
            return taskInstances.get(0);
        }
        return null;
    }

    @Override
    public Long countByUuid(String uuid) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        return this.dao.countByHQL("from TaskInstance t where t.uuid = :uuid", params);
    }

}
