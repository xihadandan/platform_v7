/*
 * @(#)2013-11-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.TaskTimerLogDao;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.entity.TaskTimerLog;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Date;
import java.util.List;

/**
 * Description: 任务计时器跟踪日志服务类
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
public interface TaskTimerLogService extends JpaService<TaskTimerLog, TaskTimerLogDao, String> {
    /**
     * 根据UUID获取任务计时器跟踪日志
     *
     * @param uuid
     * @return
     */
    TaskTimerLog get(String uuid);

    /**
     * 根据流程实例UUID获取任务计时器跟踪日志
     *
     * @param flowInstUuid
     * @return
     */
    List<TaskTimerLog> getByFlowInstUuid(String flowInstUuid);

    /**
     * 根据任务计时器跟踪日志实例查询任务计时器跟踪日志列表
     *
     * @param example
     * @return
     */
    List<TaskTimerLog> findByExample(TaskTimerLog example);

    /**
     * 根据任务计时器跟踪日志实例查询任务计时器跟踪日志列表
     *
     * @param example
     * @param orderByProperty
     * @return
     */
    List<TaskTimerLog> findByExample(TaskTimerLog example, String orderByProperty);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     */
    void removeByFlowInstUuid(String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param taskTimerUuid
     */
    void removeByTaskTimerUuid(String taskTimerUuid);

    /**
     * 保存计时器日志
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param taskTimer
     * @param logTime
     * @param type
     */
    void log(String taskInstUuid, String flowInstUuid, TaskTimer taskTimer, Date logTime, String type);

    /**
     * 保存计时器日志
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param taskTimer
     * @param logTime
     * @param type
     */
    void log(String taskInstUuid, String flowInstUuid, TaskTimer taskTimer, Date logTime, String type, String remark);

    /**
     * 保存计时器日志
     *
     * @param taskTimerUuid
     * @param type
     * @param remark
     */
    void log(String taskTimerUuid, String type, String remark);

    /**
     * 保存计时器日志
     *
     * @param taskTimer
     * @param logTime
     * @param remark
     */
    void log(TaskTimer taskTimer, Date logTime, String type, String remark);

}
