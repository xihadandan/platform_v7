/*
 * @(#)2013-4-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.TaskActivityDao;
import com.wellsoft.pt.bpm.engine.entity.TaskActivity;
import com.wellsoft.pt.bpm.engine.query.TaskActivityQueryItem;
import com.wellsoft.pt.jpa.service.JpaService;

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
 * 2013-4-6.1	zhulh		2013-4-6		Create
 * </pre>
 * @date 2013-4-6
 */
public interface TaskActivityService extends JpaService<TaskActivity, TaskActivityDao, String> {
    /**
     * @param uuid
     * @return
     */
    public TaskActivity get(String uuid);

    /**
     * @param uuid
     * @return
     */
    public TaskActivity getByTaskInstUuid(String taskInstUuid);

    /**
     * @param taskActivity
     * @return
     */
    public List<TaskActivity> findByExample(TaskActivity taskActivity);

    /**
     * @param flowInstUuid
     * @return
     */
    public List<TaskActivity> getHistoryActivities(String flowInstUuid);

    /**
     * 根据并行任务UUID获取任务结点
     *
     * @param parallelTaskInstUuid
     * @param flowInstUuid
     * @return
     */
    public List<TaskActivity> getByParallelTaskInstUuid(String parallelTaskInstUuid, String flowInstUuid);

    /**
     * 根据流程实例UUID，判断指定的环节ID是否为流程实例的第一个环节实例
     *
     * @param flowInstUuid
     * @return
     */
    public boolean isStartedTaskInstance(String flowInstUuid, String taskId);

    /**
     * 根据流程实例UUID查找任务的活动总数
     *
     * @param flowInstUuid
     * @return
     */
    public Long countByFlowInstUuid(String flowInstUuid);

    // /**
    // * 根据任务实例UUID获取该任务实例的所有任务活动
    // *
    // * @param taskInstUuid
    // * @return
    // */
    // public List<TaskActivity> getAllActivityByTaskInstUuid(String
    // taskInstUuid);

    // /**
    // * 根据流程实例UUID获取该流程实例的所有任务活动
    // *
    // * @param flowInstUuid
    // * @return
    // */
    // public List<TaskActivity> getAllActivityByFlowInstUuid(String
    // flowInstUuid);

    /**
     * 根据任务实例UUID获取该任务实例的所有任务活动
     *
     * @param taskInstUuid
     * @return
     */
    public List<TaskActivityQueryItem> getAllActivityByTaskInstUuid(String taskInstUuid);

    /**
     * 根据流程实例UUID获取该流程实例的所有任务活动
     *
     * @param flowInstUuid
     * @return
     */
    public List<TaskActivityQueryItem> getAllActivityByFlowInstUuid(String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     * @return
     */
    public List<TaskActivity> getByFlowInstUuid(String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     */
    public void removeByFlowInstUuid(String flowInstUuid);

    /**
     * 通过当前环节ID和流程实例uuid，获取前办理环节ID
     *
     * @param flowInstUuid
     * @param taskId
     * @return java.lang.String
     **/
    String getPreTaskId(String flowInstUuid, String taskId);

    /**
     * @param creator
     * @param preTaskInstUuid
     * @return
     */
    TaskActivity getByCreatorAndPreTaskInstUuid(String creator, String preTaskInstUuid);
}
