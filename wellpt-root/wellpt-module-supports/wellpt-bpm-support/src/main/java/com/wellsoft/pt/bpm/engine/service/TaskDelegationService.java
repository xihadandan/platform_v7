/*
 * @(#)2015-6-23 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.TaskDelegationDao;
import com.wellsoft.pt.bpm.engine.entity.TaskDelegation;
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
 * 2015-6-23.1	zhulh		2015-6-23		Create
 * </pre>
 * @date 2015-6-23
 */
public interface TaskDelegationService extends JpaService<TaskDelegation, TaskDelegationDao, String> {

    List<TaskDelegation> getByFlowInstUuid(String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     */
    void removeByFlowInstUuid(String flowInstUuid);

    /**
     * 根据用户ID、环节实例UUID、办理人标识获取委托信息
     *
     * @param userId
     * @param taskInstUuid
     * @param taskIdentityUuid
     * @return
     */
    TaskDelegation get(String userId, String taskInstUuid, String taskIdentityUuid);

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<TaskDelegation> getOverdueTaskDelegation();

    /**
     * 根据环节实例UUID获取委托中的委托实例
     *
     * @param taskInstUuid
     * @return
     */
    List<TaskDelegation> listRunningTaskDelegationByTaskInstUuid(String taskInstUuid);

    /**
     * 根据委托人ID、环节实例UUID获取委托中的委托实例
     *
     * @param consignor
     * @param taskInstUuid
     */
    List<TaskDelegation> listRunningByConsignorAndTaskInstUuid(String consignor, String taskInstUuid);

    /**
     * 根据受拖人ID、环节实例UUID获取委托实例
     *
     * @param trustee
     * @param taskInstUuid
     * @return
     */
    List<TaskDelegation> listByTrusteeAndTaskInstUuid(String trustee, String taskInstUuid);

    /**
     * 根据待办标识完成委托
     *
     * @param taskIdentityUuid
     */
    void completeByTaskIdentityUuid(String taskIdentityUuid);

    /**
     * 根据用户ID、操作类型、环节实例UUID完成委托
     *
     * @param userId
     * @param actionType
     * @param taskInstUuid
     */
    void completeByUserIdAndActionType(String userId, String actionType, String taskInstUuid, boolean taskComplete);

    /**
     * 根据待办标识取消委托
     *
     * @param taskIdentityUuid
     */
    void cancelByTaskIdentityUuid(String taskIdentityUuid);


    /**
     * 根据环节实例UUID取消委托
     *
     * @param taskInstUuid
     */
    void cancelByTaskInstUuid(String taskInstUuid);

    /**
     * 根据委托设置UUID获取委托实例数量
     *
     * @param delegationSettingsUuid
     * @return
     */
    Long countByDelegationSettingsUuid(String delegationSettingsUuid);

    /**
     * @param taskIdentityUuid
     * @return
     */
    TaskDelegation getByTaskIdentityUuid(String taskIdentityUuid);

}
