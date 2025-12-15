/*
 * @(#)2015-5-1 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.wellsoft.pt.bpm.engine.dao.TaskFormOpinionLogDao;
import com.wellsoft.pt.bpm.engine.entity.TaskFormOpinionLog;
import com.wellsoft.pt.bpm.engine.service.TaskFormOpinionLogService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowSuspensionState;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2015-5-1.1	zhulh		2015-5-1		Create
 * </pre>
 * @date 2015-5-1
 */
@Service
public class TaskFormOpinionLogServiceImpl extends
        AbstractJpaServiceImpl<TaskFormOpinionLog, TaskFormOpinionLogDao, String> implements TaskFormOpinionLogService {

    private static final String GET_BY_FLOW_INST_UUID = "from TaskFormOpinionLog t where t.flowInstUuid = :flowInstUuid";

    private static final String REMOVE_BY_FLOW_INST_UUID = "delete from TaskFormOpinionLog t where t.flowInstUuid = :flowInstUuid";

    private static final String REMOVE_BY_TASK_OPERATION_UUID = "update TaskFormOpinionLog t set tstatus = -1 where t.taskOperationUuid = :taskOperationUuid";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionLogService#get(java.lang.String)
     */
    @Override
    public TaskFormOpinionLog get(String uuid) {
        return this.dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionLogService#getByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<TaskFormOpinionLog> getByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.listByHQL(GET_BY_FLOW_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionLogService#log(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Integer)
     */
    @Override
    @Transactional
    public String log(String taskInstUuid, String flowInstUuid, String taskFormOpinionUuid, String fieldName,
                      String content, Integer recordWay) {
        TaskFormOpinionLog taskFormOpinionLog = new TaskFormOpinionLog();
        taskFormOpinionLog.setTaskInstUuid(taskInstUuid);
        taskFormOpinionLog.setFlowInstUuid(flowInstUuid);
        taskFormOpinionLog.setTaskFormOpinionUuid(taskFormOpinionUuid);
        taskFormOpinionLog.setFieldName(fieldName);
        taskFormOpinionLog.setContent(StringUtils.trimToEmpty(content));
        taskFormOpinionLog.setRecordWay(recordWay);
        taskFormOpinionLog.setStatus(WorkFlowSuspensionState.Normal);
        this.dao.save(taskFormOpinionLog);
        return taskFormOpinionLog.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionLogService#getByTaskOperationUuid(java.lang.String)
     */
    @Override
    public List<TaskFormOpinionLog> getByTaskOperationUuid(String taskOperationUuid) {
        TaskFormOpinionLog example = new TaskFormOpinionLog();
        example.setTaskOperationUuid(taskOperationUuid);
        return this.dao.listByEntity(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionLogService#removeByTaskOperationUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void removeByTaskOperationUuid(String taskOperationUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskOperationUuid", taskOperationUuid);
        this.dao.updateByHQL(REMOVE_BY_TASK_OPERATION_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionLogService#removeByFlowInstUuid(java.lang.String)
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
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormOpinionLogService#findByExample(com.wellsoft.pt.bpm.engine.entity.TaskFormOpinionLog, java.lang.String)
     */
    @Override
    public List<TaskFormOpinionLog> findByExample(TaskFormOpinionLog example, String orderBy) {
        return this.dao.listByEntityAndPage(example, null, orderBy);
    }

    @Override
    public long countByEntity(TaskFormOpinionLog entity) {
        return this.dao.countByEntity(entity);
    }

}
