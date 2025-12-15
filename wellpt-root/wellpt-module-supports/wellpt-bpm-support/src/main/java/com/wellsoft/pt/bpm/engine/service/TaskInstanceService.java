/*
 * @(#)2013-3-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.pt.bpm.engine.dao.TaskInstanceDao;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.query.FlowInstanceSystemQueryItem;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
public interface TaskInstanceService extends JpaService<TaskInstance, TaskInstanceDao, String> {

    /**
     * @param uuid
     * @return
     */
    TaskInstance get(String uuid);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     * @return
     */
    List<TaskInstance> getByFlowInstUuid(String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     * @return
     */
    List<TaskInstance> getOrderDescByFlowInstUuid(String flowInstUuid);

    /**
     * 根据任务id获取子任务列表
     *
     * @param parentTaskInstUuid parentTaskInstUuid
     * @return List<TaskInstance>
     */
    List<TaskInstance> getNormalByParentTaskInstUuid(String parentTaskInstUuid);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     */
    void removeByFlowInstUuid(String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param hql
     * @param values
     * @return
     */
    List<TaskInstance> find(String hql, Map<String, Object> values);

    /**
     * @param hql
     * @param values
     * @return
     */
    List<String> listCharSequenceByHQL(String hql, Map<String, Object> values);

    /**
     * 如何描述该方法
     *
     * @param taskInstance
     */
    void merge(TaskInstance taskInstance);

    /**
     * 如何描述该方法
     *
     * @param taskPage
     * @param hql
     * @param values
     */
    void query(Page<TaskInstance> taskPage, String hql, Object... values);

    /**
     * 如何描述该方法
     *
     * @param formUuid
     * @param dataUuid
     * @return
     */
    List<TaskInstance> getByDateUuid(String formUuid, String dataUuid);

    /**
     * @param flowInstUuid
     * @return
     */
    long countUnfinishedTasks(String flowInstUuid);

    /**
     * 根据流程定义UUID、环节ID集合获取未完成的环节实例数量
     *
     * @param flowDefUuid
     * @param taskIds
     * @return
     */
    long countUnfinishedTasksByFlowDefUuidAndTaskIds(String flowDefUuid, Set<String> taskIds);

    /**
     * @param taskInstUuid
     * @return
     */
    String getFlowInstUUidByTaskInstUuid(String taskInstUuid);

    /**
     * @param taskInstUuid
     * @return
     */
    FlowInstanceSystemQueryItem getFlowInstanceSystemQueryItemByTaskInstUuid(String taskInstUuid);

    /**
     * 根据主流程实例UUID、排除的子流程实例UUID，获取未办结的子流程实例待办人员ID
     *
     * @param parentFlowInstUuid
     * @param excludeFlowInstUuid
     * @return
     */
    List<String> listTodoUserIdsByParentFlowInstUuidWithExcludeFlowInstUuid(String parentFlowInstUuid,
                                                                            String excludeFlowInstUuid);

    /**
     * @param taskInstUuid
     * @return
     */
    String getFlowDefUUidByTaskInstUuid(String taskInstUuid);

    /**
     * @param flowInstUuid
     * @return
     */
    List<String> listIdByFlowInstUuid(String flowInstUuid);

    /**
     * @param taskId
     * @param flowInstUuid
     * @return
     */
    TaskInstance getLastTaskInstanceByTaskIdAndFlowInstUuid(String taskId, String flowInstUuid);

    /**
     * @param uuid
     * @return
     */
    Long countByUuid(String uuid);
}
