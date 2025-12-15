/*
 * @(#)2013-5-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.TaskTimerDao;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface TaskTimerService extends JpaService<TaskTimer, TaskTimerDao, String> {
    /**
     * 如何描述该方法
     *
     * @param taskTimerUuid
     * @return
     */
    TaskTimer get(String uuid);

    /**
     * 如何描述该方法
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * @param flowInstUuid
     * @return
     */
    List<TaskTimer> getByFlowInstUuid(String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     */
    void removeByFlowInstUuid(String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param preTaskId
     * @param uuid
     * @return
     */
    TaskTimer getByTaskInstUuidAndFlowInstUuid(String taskInstUuid, String flowInstUuid);

    /**
     * 根据环节实例UUID、流程实例UUID获取有效的计时器
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @return
     */
    List<TaskTimer> getActiveTimersByTaskInstUuidAndFlowInstUuid(String taskInstUuid, String flowInstUuid);

    /**
     * 根据流向ID、流程实例UUID获取有效的计时器
     *
     * @param directionId
     * @param flowInstUuid
     * @return
     */
    List<TaskTimer> getActiveTimersByDirectionIdAndFlowInstUuid(String directionId, String flowInstUuid);

    /**
     * 根据流程实例UUID获取有效的计时器
     *
     * @param flowInstUuid
     * @return
     */
    List<TaskTimer> getActiveTimersByFlowInstUuid(String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param hql
     * @param values
     * @return
     */
    long countByHQL(String hql, Map<String, Object> values);

    /**
     * @param taskInstUuid
     * @return
     */
    long countByTaskInstUuid(String taskInstUuid);

    List<TaskTimer> getActiveTimersByTaskInstUuidAndFlowInstUuid(String taskInstUuid, String flowInstUuid,
                                                                 String timerName);

    /**
     * @param timerUuid
     * @return
     */
    TaskTimer getByTimerUuid(String timerUuid);

    /**
     * @param taskInstUuid
     * @return
     */
    List<TaskTimer> listByTaskInstUuid(String taskInstUuid);

    List<TaskTimer> listByFlowInstUuids(List<String> flowInstUuids);
}
