/*
 * @(#)2013-11-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.wellsoft.pt.bpm.engine.dao.TaskTimerLogDao;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.entity.TaskTimerLog;
import com.wellsoft.pt.bpm.engine.service.TaskTimerLogService;
import com.wellsoft.pt.bpm.engine.service.TaskTimerService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 任务计时器跟踪日志服务类实现类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-6.1	zhulh		2013-11-6		Create
 * </pre>
 * @date 2013-11-6
 */
@Service
public class TaskTimerLogServiceImpl extends AbstractJpaServiceImpl<TaskTimerLog, TaskTimerLogDao, String> implements
        TaskTimerLogService {

    private static final String GET_BY_FLOW_INST_UUID = "from TaskTimerLog t where t.flowInstUuid = :flowInstUuid";

    private static final String REMOVE_BY_FLOW_INST_UUID = "delete from TaskTimerLog t where t.flowInstUuid = :flowInstUuid";

    private static final String REMOVE_BY_TASK_TIMER_UUID = "delete from TaskTimerLog t where t.taskTimerUuid = :taskTimerUuid";

    @Autowired
    private TaskTimerLogDao dao;

    @Autowired
    private TaskTimerService taskTimerService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerLogService#get(java.lang.String)
     */
    @Override
    public TaskTimerLog get(String uuid) {
        return dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerLogService#getByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<TaskTimerLog> getByFlowInstUuid(String flowInstUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.listByHQL(GET_BY_FLOW_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerLogService#findByExample(com.wellsoft.pt.bpm.engine.entity.TaskTimerLog)
     */
    @Override
    public List<TaskTimerLog> findByExample(TaskTimerLog example) {
        return dao.listByEntity(example);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerLogService#findByExample(com.wellsoft.pt.bpm.engine.entity.TaskTimerLog)
     */
    @Override
    public List<TaskTimerLog> findByExample(TaskTimerLog example, String orderByProperty) {
        return dao.listByEntity(example, null, orderByProperty, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerLogService#removeByFlowInstUuid(java.lang.String)
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
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerLogService#removeByTaskTimerUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void removeByTaskTimerUuid(String taskTimerUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskTimerUuid", taskTimerUuid);
        this.dao.deleteByHQL(REMOVE_BY_TASK_TIMER_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerLogService#log(java.lang.String, java.lang.String, com.wellsoft.pt.bpm.engine.entity.TaskTimer, java.util.Date, java.lang.String)
     */
    @Override
    @Transactional
    public void log(String taskInstUuid, String flowInstUuid, TaskTimer taskTimer, Date logTime, String type) {
        log(taskInstUuid, flowInstUuid, taskTimer, logTime, type, StringUtils.EMPTY);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerLogService#log(java.lang.String, java.lang.String, com.wellsoft.pt.bpm.engine.entity.TaskTimer, java.util.Date, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void log(String taskInstUuid, String flowInstUuid, TaskTimer taskTimer, Date logTime, String type,
                    String remark) {
        TaskTimerLog taskTimerLog = new TaskTimerLog();
        taskTimerLog.setTaskTimerUuid(taskTimer.getUuid());
        taskTimerLog.setFlowInstUuid(flowInstUuid);
        taskTimerLog.setTaskInstUuid(taskInstUuid);
        taskTimerLog.setLogTime(logTime);
        taskTimerLog.setType(type);
        taskTimerLog.setRemark(remark);
        this.dao.save(taskTimerLog);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerLogService#log(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void log(String taskTimerUuid, String type, String remark) {
        TaskTimer taskTimer = taskTimerService.get(taskTimerUuid);
        Date logTime = Calendar.getInstance().getTime();
        log(taskTimer.getTaskInstUuid(), taskTimer.getFlowInstUuid(), taskTimer, logTime, type, remark);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerLogService#log(com.wellsoft.pt.bpm.engine.entity.TaskTimer, java.util.Date, java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void log(TaskTimer taskTimer, Date logTime, String type, String remark) {
        log(taskTimer.getTaskInstUuid(), taskTimer.getFlowInstUuid(), taskTimer, logTime, type, remark);
    }

}
