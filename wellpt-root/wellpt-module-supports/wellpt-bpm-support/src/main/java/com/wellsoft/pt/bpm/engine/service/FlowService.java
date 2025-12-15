/*
 * @(#)2012-11-2 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.FlowInstanceParameter;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.node.SubTaskNode;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.timer.support.TimerWorkTime;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 流程实例的操作服务接口类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-2.1	zhulh		2012-11-2		Create
 * </pre>
 * @date 2012-11-2
 */
public interface FlowService {

    public static final String AS_DRAFT = SubTaskNode.DRAFT;

    public static final String AUTO_SUBMIT = SubTaskNode.AUTO_SUBMIT;

    public static final String START_TODO = "START_TODO";

    /**
     * 获取流程定义
     *
     * @param flowDefUuid
     * @return
     */
    FlowDefinition getFlowDefinition(String flowDefUuid);

    /**
     * 获取流程定义
     *
     * @param id
     * @return
     */
    FlowDefinition getFlowDefinitionById(String id);

    // /**
    // * 启动流程实例
    // *
    // * @param id
    // * @param dytableId
    // * @return
    // */
    // FlowInstance startFlowInstanceById(String id, String dataUuid);

    /**
     * 获取工作流程的第一个任务的配置信息
     *
     * @param flowDefUuid
     * @return
     */
    TaskData getFirstTaskData(String flowDefUuid);

    /**
     * 获取工作流程的最后一个任务的配置信息
     *
     * @param uuid
     * @return
     */
    TaskData getLastTaskData(String flowDefUuid);

    /**
     * 保存任务为草稿，在新建工作时使用
     *
     * @param flowDefId 流程定义ID
     * @param taskData  任务数据
     * @return
     */
    FlowInstance saveAsDraftByFlowDefId(String flowDefId, TaskData taskData);

    /**
     * 保存任务为草稿，在新建工作时使用
     *
     * @param flowDefUuid 流程定义UUID
     * @param taskData    任务数据
     * @return
     */
    FlowInstance saveAsDraft(String flowDefUuid, TaskData taskData);

    /**
     * 创建空的子流程实例
     *
     * @param parent
     * @param subFlowDefId
     * @return
     */
    FlowInstance createEmptySubFlowInstance(FlowInstance parent, String subFlowDefId);

    /**
     * 更新子流程为草稿，在异步新建子流程工作时使用
     *
     * @param flowInstUuid
     * @param parent
     * @param subFlowDefId
     * @param taskData
     * @param todoName     环节办理人名称
     * @return
     */
    FlowInstance updateSubFlowAsDraftByFlowDefId(String flowInstUuid, FlowInstance parent,
                                                 TaskInstance parentTaskInstance, String subFlowDefId, TaskData taskData, String todoName);

    /**
     * 保存子流程为草稿，在新建子流程工作时使用
     *
     * @param parent
     * @param subFlowDefId
     * @param taskData
     * @param todoName     办理人名称
     * @return
     */
    FlowInstance saveSubFlowAsDraftByFlowDefId(FlowInstance parent, TaskInstance parentTaskInstance,
                                               String subFlowDefId, TaskData taskData, String todoName);

    /**
     * 保存子流程为草稿，在新建子流程工作时使用
     *
     * @param parent
     * @param subFlowDefUuid
     * @param taskData
     * @param todoName       环节办理人名称
     * @return
     */
    FlowInstance saveSubFlowAsDraft(FlowInstance parent, TaskInstance parentTaskInstance, String subFlowDefUuid,
                                    TaskData taskData, String todoName);

    /**
     * 从工作草稿中启动流程实例
     *
     * @param flowInstUuid 草稿流程实例uuid
     * @param taskData     任务数据
     */
    FlowInstance startFlowInstance(String flowInstUuid, TaskData taskData);

    /**
     * 发起流程
     *
     * @param flowDefId   流程定义ID
     * @param startUserId 流程发起人
     * @param toTaskId    提交到指定环节或FlowService.AS_DRAFT(提交为草稿)、FlowService.AUTO_SUBMIT(自动提交)
     * @param toTaskUsers 环节办理人
     * @param formUuid    表单定义UUID
     * @param dataUuid    表单数据UUID
     * @return
     */
    FlowInstance startByFlowDefId(String flowDefId, String startUserId, String toTaskId, List<String> toTaskUsers,
                                  String formUuid, String dataUuid);

    /**
     * 获取第一个办理过程信息
     *
     * @param flowDefUuid
     * @return
     */
    TaskData getFirstTaskProcessInfo(String flowDefUuid);

    /**
     * 获取下一个办理过程信息
     *
     * @param flowDefUuid
     * @param taskId
     * @return
     */
    TaskData getNextTaskProcessInfo(String flowDefUuid, String taskId);

    /**
     * 删除草稿
     *
     * @param flowInstUuid
     */
    void deleteDraft(String userId, String flowInstUuid);

    /**
     * 结束流程
     *
     * @param flowInstUuid
     */
    void stop(String userId, String flowInstUuid);

    /**
     * 查询流程实例参数
     *
     * @param example
     * @return
     */
    List<FlowInstanceParameter> findFlowInstanceParameter(FlowInstanceParameter example);

    /**
     * 保存流程实例参数
     *
     * @param example
     * @return
     */
    void saveFlowInstanceParameter(FlowInstanceParameter parameter);

    /**
     * 删除流程实例参数
     *
     * @param parameter
     */
    void deleteFlowInstanceParameter(FlowInstanceParameter parameter);

    /**
     * 根据流程实例UUID获取流程实例
     *
     * @param flowInstUuid
     * @return
     */
    FlowInstance getFlowInstance(String flowInstUuid);

    /**
     * 根据环节实例UUID获取流程实例
     *
     * @param taskInstUuid
     * @return
     */
    FlowInstance getFlowInstanceByTaskInstUuid(String taskInstUuid);

    Map<String, Object> getFormUuidAndDataUuidByTaskInstUuid(String taskInstUuid);

    /**
     * 根据表单数据UUID获取流程实例UUID
     *
     * @param dataUuid
     * @return
     */
    String getFlowInstUuidByFormDataUuid(String dataUuid);

    /**
     * 判断流程实例是否为主流程
     *
     * @param flowInstUuid
     * @return
     */
    boolean isMainFlowInstance(String flowInstUuid);

    /**
     * 获取未完成的子流程实例
     *
     * @param flowInstUuid
     * @return
     */
    List<FlowInstance> getUnfinishedSubFlowInstances(String flowInstUuid);

    /**
     * 存流程实例
     *
     * @param flowInstance
     */
    void saveFlowInstance(FlowInstance flowInstance);

    /**
     * 使用子流程配置的实时同步的单据转换规则，同步子流程数据
     *
     * @param flowInstance
     * @param taskData
     */
    void syncSubFlowInstances(FlowInstance flowInstance, TaskData taskData);

    /**
     * 使用子流程配置的实时同步的单据转换规则，同步子流程数据
     *
     * @param parentFlowInstUuid
     */
    void syncSubFlowInstancesByParentFlowInstUuid(String parentFlowInstUuid);

    /**
     * 使用指定的单据转换规则，同步子流程数据
     *
     * @param parentFlowInstUuid
     * @param botRuleId
     */
    void syncSubFlowInstancesByParentFlowInstUuidAndBotRuleId(String parentFlowInstUuid, String botRuleId);

    /**
     * 根据前置流程实例UUID获取后置流程实例列表
     *
     * @param flowInstUuid
     * @return
     */
    List<FlowInstance> getBehindFlowInstanceByFrontFlowInstanceUuid(String flowInstUuid);

    /**
     * 流程实例查询
     *
     * @param hql
     * @param values
     * @return
     */
    <X> List<X> query(String hql, Map<String, Object> values);

    /**
     * 根据流程实例UUID判断流程是否结束
     *
     * @param flowInstUuid
     * @return
     */
    Boolean isCompleted(String flowInstUuid);

    /**
     * 根据环节实例UUID判断流程是否结束
     *
     * @param taskInstUuid
     * @return
     */
    Boolean isCompletedByTaskInstUuid(String taskInstUuid);

    /**
     * 删除用户草稿权限
     *
     * @param userId
     * @param flowInstUuid
     */
    void removeDraftPermission(String userId, String flowInstUuid);

    /**
     * 判断用户是否有流程的草稿权限
     *
     * @param userId
     * @param flowInstUuid
     * @return
     */
    public boolean hasDraftPermission(String userId, String flowInstUuid);

    /**
     * 根据流程定义UUID，添加流程阅读者
     *
     * @param flowDefUuid
     * @param orgIds
     */
    void addReader(String flowDefUuid, List<String> orgIds);

    /**
     * 根据流程定义ID，添加流程阅读者到所有版本的流程定义
     *
     * @param flowDefId
     * @param orgIds
     */
    void addReaderById(String flowDefId, List<String> orgIds);

    /**
     * 根据流程定义UUID，删除流程阅读者
     *
     * @param flowDefUuid
     * @param orgIds
     */
    void removeReader(String flowDefUuid, List<String> orgIds);

    /**
     * 根据流程定义UUID，删除所有版本的流程定义的阅读者
     *
     * @param flowDefId
     * @param orgIds
     */
    void removeReaderById(String flowDefId, List<String> orgIds);

    /**
     * 判断用户ID是否有新建指定流程定义的工作权限
     *
     * @param userId
     * @param flowDefUuid
     * @return
     */
    boolean hasCreatePermission(String userId, String flowDefUuid);

    /**
     * 获取环节计时器计时信息
     *
     * @param flowInstUuid
     * @return
     */
    List<TimerWorkTime> listTimerWorkTimeByFlowInstUuid(String flowInstUuid);

    /**
     * 根据流程实例UUID，获取流程实例使用的组织版本ID列表
     *
     * @param flowInstUuid
     * @param flowDefUuid
     * @return
     */
    Set<String> getOrgVersionIdsByFlowInstUuid(String flowInstUuid, String flowDefUuid);

    void addRuntimeFlowAccessPermissionProvider(String flowInstUuid, String flowAccessPermissionProvider);
}
