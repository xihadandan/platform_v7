/*
 * @(#)2019年3月7日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.TaskBranchDao;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskActivity;
import com.wellsoft.pt.bpm.engine.entity.TaskBranch;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.workflow.work.bean.BranchTaskData;

import java.util.Collection;
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
 * 2019年3月7日.1	zhulh		2019年3月7日		Create
 * </pre>
 * @date 2019年3月7日
 */
public interface TaskBranchService extends JpaService<TaskBranch, TaskBranchDao, String> {

    /**
     * 创建分支
     *
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     */
    void createBranchTask(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData);

    /**
     * 添加跟进人员
     *
     * @param parallelTaskId
     * @param taskData
     */
    void addBranchTaskMonitors(String parallelTaskId, TaskData taskData);

    /**
     * 更新分支当前任务
     *
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     */
    void changeCurrentTask(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData);

    /**
     * 同步关联分支的当前任务
     *
     * @param task
     * @param relatedTaskBranchUuid
     */
    void syncBranchTask(TaskInstance taskInstance, String relatedTaskBranchUuid);

    /**
     * 合并分支
     *
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     */
    void joinBranchTask(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData);

    /**
     * 终止分支
     *
     * @param taskInstUuid
     */
    void stopBranchTaskByCurrentTaskInstUuid(String currentTaskInstUuid);

    /**
     * 根据当前环节UUID，还原终止的分支
     *
     * @param currentTaskInstUuid
     */
    void restoreBranchTaskByCurrentTaskInstUuid(String currentTaskInstUuid);

    /**
     * @param currentTaskInstUuid
     * @return
     */
    TaskBranch getByCurrentTaskInstUuid(String currentTaskInstUuid);

    /**
     * @param taskInstUuids
     * @return
     */
    List<TaskBranch> getByCurrentTaskInstUuids(Collection<String> currentTaskInstUuids);

    /**
     * @param flowInstUuid
     * @return
     */
    List<TaskBranch> getByFlowInstUuid(String flowInstUuid);

    /**
     * 根据分发的环节实例UUID撤回分支流数据
     *
     * @param parallelTaskInstUuid
     */
    void cancelBranchTaskByParallelTaskInstUuid(String parallelTaskInstUuid);

    /**
     * 根据分发的环节实例UUID退回分支流数据
     *
     * @param parallelTaskInstUuid
     */
    void rollbackBranchTaskByParallelTaskInstUuid(String parallelTaskInstUuid);

    /**
     * 完成分支任务
     *
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     */
    void completeBranchTask(TaskInstance currentTaskInstance);

    /**
     * 判断是否为最后一个需要聚合的分支
     *
     * @param currentTaskInstance
     * @return
     */
    boolean isTheLastMergeBranch(TaskInstance currentTaskInstance);

    /**
     * 获取未完成的需要合并的分支数量
     *
     * @param currentTaskInstance
     * @return
     */
    long countUnfinishedMergeBranch(TaskInstance currentTaskInstance);

    /**
     * 获取指定流程定义UUID及分发环节ID列表的未完成分支数量
     *
     * @param flowDefUuid
     * @param parallelTaskIds
     * @return
     */
    long countUnfinishedBranchByFlowDefUuidAndTaskIds(String flowDefUuid, List<String> parallelTaskIds);

    /**
     * 判断分支环节是否全部办结
     *
     * @param currentTaskInstance
     * @return
     */
    boolean isBranchTaskCompleted(TaskInstance currentTaskInstance);

    /**
     * 判断分支环节是否全部办结
     *
     * @param parallelTaskInstUuid
     * @return
     */
    boolean isBranchTaskCompletedByParallelTaskInstUuid(String parallelTaskInstUuid);

    /**
     * 获取分支流数据
     *
     * @param taskInstance
     * @param flowInstance
     * @return
     */
    BranchTaskData getBranchTaskData(TaskInstance taskInstance, FlowInstance flowInstance);

    /**
     * 加载分支流数据
     *
     * @param branchTaskData
     * @return
     */
    BranchTaskData loadBranchTaskData(BranchTaskData branchTaskData);

    /**
     * @param branchTask
     * @param taskActivities
     * @return
     */
    List<TaskActivity> filterBranchTaskActivity(TaskBranch branchTask, List<TaskActivity> taskActivities);

    /**
     * 判断是否为动态多分支数据
     *
     * @param parallelTaskInstUuid
     * @return
     */
    boolean isDynamicBranchTask(String parallelTaskInstUuid);

    /**
     * 根据并行环节实例UUID获取环节分支
     *
     * @param parallelTaskInstUuid
     * @return
     */
    List<TaskBranch> listByParallelTaskInstUuid(String parallelTaskInstUuid);

    /**
     * @param flowInstUuid
     */
    void removeByFlowInstUuid(String flowInstUuid);
}
