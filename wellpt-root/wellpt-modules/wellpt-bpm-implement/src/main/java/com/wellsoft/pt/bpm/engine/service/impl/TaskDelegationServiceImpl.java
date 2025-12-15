/*
 * @(#)2015-6-23 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.bpm.engine.dao.TaskDelegationDao;
import com.wellsoft.pt.bpm.engine.entity.TaskDelegation;
import com.wellsoft.pt.bpm.engine.service.TaskDelegationService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Calendar;
import java.util.HashMap;
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
 * 2015-6-23.1	zhulh		2015-6-23		Create
 * </pre>
 * @date 2015-6-23
 */
@Service
public class TaskDelegationServiceImpl extends AbstractJpaServiceImpl<TaskDelegation, TaskDelegationDao, String>
        implements TaskDelegationService {

    private static final String GET_BY_FLOW_INST_UUID = "from TaskDelegation t where t.flowInstUuid = :flowInstUuid";

    private static final String REMOVE_BY_FLOW_INST_UUID = "delete from TaskDelegation t where t.flowInstUuid = :flowInstUuid";

    private static final String GET_OVERDUE_TASK_DELEGATION = "from TaskDelegation t where t.dueToTakeBackWork = true and t.completionState = 0 and t.toTime is not null and t.toTime < :toTime";

    private static final String COMPLETION_DELEGATION = "update TaskDelegation t set t.completionState = :completionState where t.taskInstUuid = :taskInstUuid and t.completionState in(0, 1)";

    private static final String COMPLETION_USER_DELEGATION = "update TaskDelegation t set t.completionState = :completionState where t.taskInstUuid = :taskInstUuid and t.trustee = :trustee and t.completionState in(0, 1)";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskDelegationService#getByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<TaskDelegation> getByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.listByHQL(GET_BY_FLOW_INST_UUID, values);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskDelegationService#removeByFlowInstUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void removeByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        this.dao.deleteByHQL(REMOVE_BY_FLOW_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskDelegationService#get(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public TaskDelegation get(String userId, String taskInstUuid, String taskIdentityUuid) {
        TaskDelegation example = new TaskDelegation();
        example.setTrustee(userId);
        example.setTaskInstUuid(taskInstUuid);
        example.setTaskIdentityUuid(taskIdentityUuid);
        List<TaskDelegation> taskDelegations = this.dao.listByEntity(example);
        if (!taskDelegations.isEmpty()) {
            return taskDelegations.get(0);
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskDelegationService#getOverdueTaskDelegation()
     */
    @Override
    public List<TaskDelegation> getOverdueTaskDelegation() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("toTime", Calendar.getInstance().getTime());
        return this.dao.listByHQL(GET_OVERDUE_TASK_DELEGATION, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskDelegationService#listRunningTaskDelegationByTaskInstUuid(java.lang.String)
     */
    @Override
    public List<TaskDelegation> listRunningTaskDelegationByTaskInstUuid(String taskInstUuid) {
        TaskDelegation entity = new TaskDelegation();
        entity.setTaskInstUuid(taskInstUuid);
        entity.setCompletionState(TaskDelegation.STATUS_NORMAL);
        return this.dao.listByEntity(entity);
    }

    @Override
    public List<TaskDelegation> listRunningByConsignorAndTaskInstUuid(String consignor, String taskInstUuid) {
        Assert.hasLength(consignor, "委托人ID不能为空！");
        Assert.hasLength(taskInstUuid, "环节实例UUID不能为空！");

        TaskDelegation entity = new TaskDelegation();
        entity.setConsignor(consignor);
        entity.setTaskInstUuid(taskInstUuid);
        entity.setCompletionState(TaskDelegation.STATUS_NORMAL);
        return this.dao.listByEntity(entity);
    }

    /**
     * @param trustee
     * @param taskInstUuid
     * @return
     */
    @Override
    public List<TaskDelegation> listByTrusteeAndTaskInstUuid(String trustee, String taskInstUuid) {
        Assert.hasLength(trustee, "受拖人ID不能为空！");
        Assert.hasLength(taskInstUuid, "环节实例UUID不能为空！");

        TaskDelegation entity = new TaskDelegation();
        entity.setTrustee(trustee);
        entity.setTaskInstUuid(taskInstUuid);
        return this.dao.listByEntity(entity);
    }

    /**
     * @param taskIdentityUuid
     */
    @Override
    @Transactional
    public void completeByTaskIdentityUuid(String taskIdentityUuid) {
        String hql = "update TaskDelegation t set t.completionState = :completionState where t.taskIdentityUuid = :taskIdentityUuid and t.completionState = 0";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskIdentityUuid", taskIdentityUuid);
        values.put("completionState", TaskDelegation.STATUS_COMPLETED);
        this.dao.updateByHQL(hql, values);
    }

    @Override
    @Transactional
    public void completeByUserIdAndActionType(String userId, String actionType, String taskInstUuid, boolean taskComplete) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        if (WorkFlowOperation.isActionTypeOfSubmit(actionType)) {
            values.put("completionState", TaskDelegation.STATUS_COMPLETED);
        } else {
            values.put("completionState", TaskDelegation.STATUS_CANCEL);
        }
        if (taskComplete) {
            this.dao.updateByHQL(COMPLETION_DELEGATION, values);
        } else {
            values.put("trustee", userId);
            this.dao.updateByHQL(COMPLETION_USER_DELEGATION, values);
        }
    }

    /**
     * @param taskIdentityUuid
     */
    @Override
    @Transactional
    public void cancelByTaskIdentityUuid(String taskIdentityUuid) {
        String hql = "update TaskDelegation t set t.completionState = :completionState where t.taskIdentityUuid = :taskIdentityUuid and t.completionState in (0, 1)";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskIdentityUuid", taskIdentityUuid);
        values.put("completionState", TaskDelegation.STATUS_CANCEL);
        this.dao.updateByHQL(hql, values);
    }

    @Override
    @Transactional
    public void cancelByTaskInstUuid(String taskInstUuid) {
        Map<String, Object> values = Maps.newHashMap();
        values.put("taskInstUuid", taskInstUuid);
        values.put("completionState", TaskDelegation.STATUS_CANCEL);
        this.dao.updateByHQL(COMPLETION_DELEGATION, values);
    }

    /**
     * @param delegationSettingsUuid
     * @return
     */
    @Override
    public Long countByDelegationSettingsUuid(String delegationSettingsUuid) {
        TaskDelegation taskDelegation = new TaskDelegation();
        taskDelegation.setDelegationSettingsUuid(delegationSettingsUuid);
        return this.dao.countByEntity(taskDelegation);
    }

    /**
     * @param taskIdentityUuid
     * @return
     */
    @Override
    public TaskDelegation getByTaskIdentityUuid(String taskIdentityUuid) {
        List<TaskDelegation> taskDelegations = this.dao.listByFieldEqValue("taskIdentityUuid", taskIdentityUuid);
        return CollectionUtils.isEmpty(taskDelegations) ? null : taskDelegations.get(0);
    }

}
