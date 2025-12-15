/*
 * @(#)2013-5-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.bpm.engine.dao.TaskTimerDao;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.service.TaskTimerService;
import com.wellsoft.pt.bpm.engine.timer.support.TaskTimerStatus;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
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
 * 2013-5-25.1	zhulh		2013-5-25		Create
 * </pre>
 * @date 2013-5-25
 */
@Service
public class TaskTimerServiceImpl extends AbstractJpaServiceImpl<TaskTimer, TaskTimerDao, String> implements
        TaskTimerService {

    private static final String GET_BY_FLOW_INST_UUID = "from TaskTimer task_timer where task_timer.flowInstUuid = :flowInstUuid";

    private static final String REMOVE_BY_FLOW_INST_UUID = "delete from TaskTimer task_timer where task_timer.flowInstUuid = :flowInstUuid";

    private static final String GET_BY_TASK_ID_AND_FLOW_INST_UUID = "from TaskTimer task_timer where task_timer.flowInstUuid = :flowInstUuid and task_timer.taskIds like '%' || :taskId || '%'";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerService#get(java.lang.String)
     */
    @Override
    public TaskTimer get(String uuid) {
        return dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        TaskTimer taskTimer = dao.getOne(uuid);
        if (taskTimer != null) {
            dao.delete(taskTimer);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerService#getByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<TaskTimer> getByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return dao.listByHQL(GET_BY_FLOW_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerService#removeByFlowInstUuid(java.lang.String)
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
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerService#getByTaskInstUuidAndFlowInstUuid(java.lang.String, java.lang.String)
     */
    @Override
    public TaskTimer getByTaskInstUuidAndFlowInstUuid(String taskInstUuid, String flowInstUuid) {
        TaskTimer example = new TaskTimer();
        example.setTaskInstUuid(taskInstUuid);
        example.setFlowInstUuid(flowInstUuid);
        List<TaskTimer> taskTimers = this.dao.listByEntity(example);
        return taskTimers.isEmpty() ? null : taskTimers.get(0);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerService#getActiveTimersByTaskInstUuidAndFlowInstUuid(java.lang.String, java.lang.String)
     */
    @Override
    public List<TaskTimer> getActiveTimersByTaskInstUuidAndFlowInstUuid(String taskInstUuid, String flowInstUuid) {
        return this.getActiveTimersByTaskInstUuidAndFlowInstUuid(taskInstUuid, flowInstUuid, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerService#getActiveTimersByDirectionIdAndFlowInstUuid(java.lang.String, java.lang.String)
     */
    @Override
    public List<TaskTimer> getActiveTimersByDirectionIdAndFlowInstUuid(String directionId, String flowInstUuid) {
        String hql = "from TaskTimer t where t.overDirections like '%' || :directionId || '%' and t.flowInstUuid = :flowInstUuid and t.status <> :status";
        Map<String, Object> values = Maps.newHashMap();
        values.put("directionId", directionId);
        values.put("flowInstUuid", flowInstUuid);
        values.put("status", TaskTimerStatus.STOP);
        return this.dao.listByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerService#getActiveTimersByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<TaskTimer> getActiveTimersByFlowInstUuid(String flowInstUuid) {
        return this.getActiveTimersByTaskInstUuidAndFlowInstUuid(null, flowInstUuid, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerService#countByHQL(java.lang.String, java.util.Map)
     */
    @Override
    public long countByHQL(String hql, Map<String, Object> values) {
        return this.dao.countByHQL(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerService#countByTaskInstUuid(java.lang.String)
     */
    @Override
    public long countByTaskInstUuid(String taskInstUuid) {
        TaskTimer taskTimer = new TaskTimer();
        taskTimer.setTaskInstUuid(taskInstUuid);
        return this.dao.countByEntity(taskTimer);
    }

    @Override
    public List<TaskTimer> getActiveTimersByTaskInstUuidAndFlowInstUuid(String taskInstUuid, String flowInstUuid,
                                                                        String timerName) {
        String hql = "from TaskTimer t where t.flowInstUuid = :flowInstUuid and t.status <> :status";
        StringBuilder sb = new StringBuilder();
        sb.append(hql);
        Map<String, Object> values = Maps.newHashMap();
        if (StringUtils.isNotBlank(taskInstUuid)) {
            sb.append(" and t.taskInstUuid = :taskInstUuid ");
            values.put("taskInstUuid", taskInstUuid);
        }
        values.put("flowInstUuid", flowInstUuid);
        values.put("status", TaskTimerStatus.STOP);
        if (StringUtils.isNotBlank(timerName)) {
            sb.append(" and t.name = :name ");
            values.put("name", timerName);
        }
        return this.dao.listByHQL(sb.toString(), values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerService#getByTimerUuid(java.lang.String)
     */
    @Override
    public TaskTimer getByTimerUuid(String timerUuid) {
        return this.dao.getOneByFieldEq("timerUuid", timerUuid);
    }

    @Override
    public List<TaskTimer> listByTaskInstUuid(String taskInstUuid) {
        TaskTimer example = new TaskTimer();
        example.setTaskInstUuid(taskInstUuid);
        return this.dao.listByEntity(example);
    }

    @Override
    public List<TaskTimer> listByFlowInstUuids(List<String> flowInstUuids) {
        if (CollectionUtils.isEmpty(flowInstUuids)) {
            return Collections.emptyList();
        }

        return this.dao.listByFieldInValues("flowInstUuid", flowInstUuids);
    }

}
