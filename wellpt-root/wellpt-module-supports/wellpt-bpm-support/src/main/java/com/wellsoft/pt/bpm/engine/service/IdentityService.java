/*
 * @(#)2018年4月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.TaskIdentityDao;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月9日.1	chenqiong		2018年4月9日		Create
 * </pre>
 * @date 2018年4月9日
 */
public interface IdentityService extends JpaService<TaskIdentity, TaskIdentityDao, String> {
    /**
     * 如何描述该方法
     *
     * @param uuid
     * @return
     */
    TaskIdentity get(String uuid);

    /**
     * 如何描述该方法
     *
     * @param asList
     * @return
     */
    List<TaskIdentity> get(Collection<String> uuids);

    /**
     * 如何描述该方法
     *
     * @param identity
     */
    void save(TaskIdentity identity);

    /**
     * 挂起待办，不移除待办
     *
     * @param taskIdentity taskIdentity
     */
    void suspenseTodoWithNotRemoveTodoPermission(TaskIdentity taskIdentity);

    /**
     * 挂起待办
     *
     * @param taskIdentity
     */
    void suspenseTodo(TaskIdentity taskIdentity);

    /**
     * 如何描述该方法
     *
     * @param identity
     */
    void addTodo(TaskIdentity identity);

    /**
     * @param identity
     * @param doneUserId
     */
    void addTodo(TaskIdentity identity, String doneUserId);

    /**
     * 删除待办，包括权限
     *
     * @param identity
     */
    void removeTodo(TaskIdentity identity);

    /**
     * 如何描述该方法
     *
     * @param taskInstUuid
     */
    void removeTodoByTaskInstUuid(String taskInstUuid);

    /**
     * 如何描述该方法
     *
     * @param taskInstUuid
     * @param currentUserId
     */
    void removeTodoByTaskInstUuidAndUserId(String taskInstUuid, String userId);

    /**
     * 删除顺序为空的待办标识
     *
     * @param taskInstUuid
     */
    void removeTodoWithoutSortOrderByTaskInstUuid(String taskInstUuid);

    /**
     * 如何描述该方法
     *
     * @param taskInstUuid
     * @return
     */
    List<TaskIdentity> getTodoByTaskInstUuid(String taskInstUuid);

    /**
     * 如何描述该方法
     *
     * @param taskInstUuid
     * @return
     */
    List<String> getTodoUserIds(String taskInstUuid);

    /**
     * 获取当前待办提交的环节实例UUID
     *
     * @param userId
     * @return
     */
    List<String> listTodoSubmitTaskInstUuidByUserId(String userId);

    // /**
    // * 如何描述该方法
    // *
    // * @param sourceTaskIdentityUuid
    // * @return
    // */
    // List<TaskIdentity> getBySourceTaskIdentityUuid(String
    // sourceTaskIdentityUuid);

    /**
     * 恢复待办人员状态
     *
     * @param taskIdentities
     */
    void restore(List<TaskIdentity> taskIdentities);

    /**
     * 判断指定的Identity是否允许恢复正常的提交
     *
     * @param historyTaskIdentity
     * @return
     */
    boolean isAllowedRestoreTodoSumit(TaskIdentity historyTaskIdentity);

    /**
     * 如何描述该方法
     *
     * @param historyTaskIdentity
     */
    void restoreTodoSumit(TaskIdentity historyTaskIdentity);

    /**
     * 如何描述该方法
     *
     * @param historyTaskIdentity
     * @return
     */
    boolean isAllowedRestoreTodoCounterSignSubmit(TaskIdentity historyTaskIdentity);

    /**
     * 如何描述该方法
     *
     * @param historyTaskIdentity
     * @return
     */
    void restoreTodoCounterSignSubmit(TaskIdentity historyTaskIdentity);

    /**
     * 如何描述该方法
     *
     * @param historyTaskIdentity
     * @return
     */
    boolean isAllowedRestoreTodoTransferSubmit(TaskIdentity historyTaskIdentity);

    /**
     * 如何描述该方法
     *
     * @param historyTaskIdentity
     * @return
     */
    void restoreTodoTransferSubmit(TaskIdentity historyTaskIdentity);

    /**
     * 如何描述该方法
     *
     * @param historyTaskIdentity
     * @return
     */
    boolean isAllowedRestoreTodoDelegationSubmit(TaskIdentity historyTaskIdentity);

    /**
     * 如何描述该方法
     *
     * @param userId
     * @param historyTaskIdentity
     */
    void restoreTodoDelegationSubmit(String userId, TaskIdentity historyTaskIdentity);

    /**
     * 如何描述该方法
     *
     * @param historyTaskIdentity
     * @return
     */
    boolean isAllowedRestoreTransfer(TaskIdentity historyTaskIdentity);

    /**
     * 如何描述该方法
     *
     * @param historyTaskIdentity
     */
    void restoreTransfer(TaskIdentity historyTaskIdentity);

    /**
     * 如何描述该方法
     *
     * @param historyTaskIdentity
     */
    void restoreDelegation(TaskIdentity sourceTaskIdentity);

    /**
     * 如何描述该方法
     *
     * @param historyTaskIdentity
     * @return
     */
    boolean isAllowedRestoreCounterSign(TaskIdentity historyTaskIdentity);

    /**
     * 如何描述该方法
     *
     * @param historyTaskIdentity
     */
    void restoreCounterSign(TaskIdentity historyTaskIdentity);

    /**
     * 如何描述该方法
     *
     * @param taskInstance
     */
    void updateTaskIdentity(TaskInstance taskInstance);

    /**
     * @param taskInstance
     * @param taskData
     */
    void updateTaskIdentity(TaskInstance taskInstance, TaskData taskData);

    /**
     * 从权限更新办理人
     *
     * @param taskInstance
     */
    void updateTaskIdentityFromPermission(TaskInstance taskInstance, TaskData taskData);

    /**
     * 判断是否为当前会签任务中的最后一个会签操作, 能够进行会签操作的应该都是会签记录树的子结点
     *
     * @param sourceTaskIdentityUuid
     * @return
     */
    boolean isLastTodoTaskIdentityBySourceTaskIdentityUuid(String sourceTaskIdentityUuid);

    /**
     * 判断是否为当前会签任务中的最后一个会签操作, 能够进行会签操作的应该都是会签记录树的子结点
     *
     * @param sourceTaskIdentityUuid
     * @param creator
     * @param todoType
     * @return
     */
    boolean isLastTodoTaskIdentityBySourceTaskIdentityUuid(String sourceTaskIdentityUuid, String creator, Integer todoType);

    /**
     * 如何描述该方法
     *
     * @param taskInstUuid
     * @param userId
     * @return
     */
    List<TaskIdentity> getTodoByTaskInstUuidAndUserId(String taskInstUuid, String userId);

    /**
     * @param taskInstUuid
     * @param userDetails
     * @return
     */
    List<TaskIdentity> getTodoByTaskInstUuidAndUserDetails(String taskInstUuid, UserDetails userDetails);

    /**
     * @param taskInstUuid
     * @param userIds
     * @return
     */
    List<TaskIdentity> getTodoByTaskInstUuidAndUserIds(String taskInstUuid, List<String> userIds);

    /**
     * 获取操作待办信息挂起的待办类型(转办、会签)
     *
     * @param sourceTaskIdentityUuid
     * @return
     */
    Integer getTodoTypeForSuspension(String sourceTaskIdentityUuid);

    /**
     * 获取委托人当前待办提交的工作
     *
     * @param sids
     * @return
     */
    List<TaskIdentity> getTodoSubmitByUserSids(List<String> sids);

    /**
     * 根据环节实例UUID、用户ID获取用户处理正常或挂起的待办信息数
     *
     * @param taskInstUuid
     * @param trustee
     * @return
     */
    Long countAvailableByTaskInstUuidAndUserId(String taskInstUuid, String userId);

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<TaskIdentity> getByFlowInstUuid(String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     */
    void removeByFlowInstUuid(String flowInstUuid);

    /**
     * @param taskInstUuid
     * @param userIds
     * @return
     */
    List<TaskIdentity> getByTaskInstUuidAndUserIds(String taskInstUuid, List<String> userIds);

    /**
     * 如何描述该方法
     *
     * @param taskInstUuid
     * @return
     */
    List<TaskIdentity> getByTaskInstUuid(String taskInstUuid);

    /**
     * 如何描述该方法
     *
     * @param taskInstUuid
     * @return
     */
    List<TaskIdentity> getByOrdersByTaskInstUuid(String taskInstUuid);

    /**
     * 如何描述该方法
     *
     * @param taskInstUuid
     * @param ownerId
     * @return
     */
    List<TaskIdentity> getByTaskInstUuidAndOwnerId(String taskInstUuid, String ownerId);

    /**
     * @param taskInstUuid
     * @param suspensionState
     * @return
     */
    List<TaskIdentity> getByTaskInstUuidAndSuspensionState(String taskInstUuid, int suspensionState);

    /**
     * @param taskInstUuid
     * @param userId
     * @param todoType
     * @return
     */
    List<TaskIdentity> getTodoByTaskInstUuidAndUserIdAndTodoType(String taskInstUuid, String userId, Integer todoType);


    /**
     * @param taskInstUuid
     * @param todoType
     * @return
     */
    List<TaskIdentity> getTodoByTaskInstUuidAndTodoType(String taskInstUuid, Integer todoType);

    /**
     * 获取指定用户和指定待办办理类型的待办数据
     *
     * @param userId
     * @param todoType
     * @return
     */
    List<TaskIdentity> getTodoByUserIdAndTodoType(String userId, Integer todoType);

    /**
     * @param sourceTaskIdentityUuid
     * @return
     */
    List<TaskIdentity> listBySourceTaskIdentityUuid(String sourceTaskIdentityUuid);

    /**
     * @param sourceTaskIdentityUuids
     * @param todoType
     * @return
     */
    List<TaskIdentity> listBySourceTaskIdentityUuidsAndTodoType(List<String> sourceTaskIdentityUuids, Integer todoType);

    /**
     * @param taskInstUuid
     * @return
     */
    Long countOrderByTaskInstUuid(String taskInstUuid);

    /**
     * @param overdueState
     * @param taskInstUuid
     */
    void updateOverdueStateByTaskInstUuid(Integer overdueState, String taskInstUuid);

}
