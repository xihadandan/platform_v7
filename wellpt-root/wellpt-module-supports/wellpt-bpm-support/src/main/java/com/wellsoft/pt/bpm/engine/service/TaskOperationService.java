/*
 * @(#)2013-4-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.pt.bpm.engine.dao.TaskOperationDao;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.bpm.engine.query.api.TaskOperationQueryItem;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.ArrayList;
import java.util.Collection;
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
 * 2013-4-28.1	zhulh		2013-4-28		Create
 * </pre>
 * @date 2013-4-28
 */
public interface TaskOperationService extends JpaService<TaskOperation, TaskOperationDao, String> {

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @return
     */
    TaskOperation get(String uuid);

    /**
     * 统计流程环节中意见立场为指定值的数量
     *
     * @param opinionValue
     * @param taskId
     * @param flowInstUuid
     * @return
     */
    long countOpinion(String opinionValue, String taskId, String flowInstUuid);

    /**
     * 统计流程环节中意见立场不办空的总数量
     *
     * @param taskId
     * @param flowInstUuid
     * @return
     */
    long countAllOpinion(String taskId, String flowInstUuid);

    /**
     * 设置操作历史的主送对象及抄送对象
     *
     * @param uuid
     * @param userIds
     * @param copyUserIds
     */
    void setUsers(String uuid, Collection<String> userIds, Collection<String> copyUserIds);

    /**
     * 如何描述该方法
     *
     * @param preTaskInstUuid
     * @return
     */
    List<TaskOperation> getByTaskInstUuid(String taskInstUuid);

    /**
     * 如何描述该方法
     *
     * @param userId
     * @param flowInstUuid
     * @return
     */
    TaskOperation getLastestByUserId(String userId, String flowInstUuid);

    /**
     * @param userId
     * @param actionCodes
     * @param flowInstUuid
     * @return
     */
    TaskOperation getLastestByUserIdAndActionCodes(String userId, List<Integer> actionCodes, String flowInstUuid);

    /**
     * @param userId
     * @param flowInstUuid
     * @return
     */
    TaskOperation getLastestDoneByUserId(String userId, String flowInstUuid);

    /**
     * 获取指定流程实例撤回后之前的环节操作
     *
     * @param flowInstUuid
     * @return
     */
    TaskOperation getLastestCancelAfterByFlowInstUuid(String flowInstUuid);

    /**
     * 获取最新抄送给指定用户的操作
     *
     * @param userId
     * @param flowInstUuid
     * @return
     */
    TaskOperation getLastestCopyToByUserId(String userId, String flowInstUuid);

    /**
     * 获取最新提交并抄送给指定用户的环节实例UUID
     *
     * @param userId
     * @param flowInstUuid
     * @return
     */
    String getLastestTaskInstUuidBySubmitAndCopyToUser(String userId, String flowInstUuid);

    /**
     * 如何描述该方法
     *
     * @param action
     * @param actionCode
     * @param actionType
     * @param opinionValue
     * @param opinionName
     * @param opinionText
     * @param ownerId
     * @param userId
     * @param userIds
     * @param copyUserIds
     * @param extraInfo
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     * @return
     */
    String saveTaskOperation(String action, Integer actionCode, String actionType, String opinionValue,
                             String opinionName, String opinionText, String operator, Collection<String> userIds,
                             Collection<String> copyUserIds, String taskIdentityUuid, String extraInfo, TaskInstance taskInstance,
                             FlowInstance flowInstance, TaskData taskData);

    /**
     * 根据环节实例UUID获取该流程所有相关的环节实例操作
     *
     * @param flowInstUuid
     * @return
     */
    List<TaskOperation> getAllTaskOperationByTaskInstUuid(String taskInstUuid);

    /**
     * 根据流程实例UUID获取该流程所有相关的环节实例操作
     *
     * @param flowInstUuid
     * @return
     */
    List<TaskOperation> getAllTaskOperationByFlowInstUuid(String flowInstUuid);

    /**
     * 根据流程实现UUID及环节ID列表获取该环节实例的所有任务操作
     *
     * @param flowInstUuid
     * @param raws
     * @return
     */
    List<TaskOperation> getByFlowInstUuidAndTaskIds(String flowInstUuid, List<String> raws);

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
    List<String> getAssigneeByHQL(String hql, Map<String, Object> values);

    /**
     * 如何描述该方法
     *
     * @param flowInstUuid
     * @return
     */
    List<TaskOperation> getByFlowInstUuid(String flowInstUuid);

    /**
     * 获取分支流相关操作
     *
     * @param flowInstUuid
     * @return
     */
    List<TaskOperationQueryItem> getBranchTaskRelateOperation(String flowInstUuid);

    /**
     * 获取子流程相关操作
     *
     * @param flowInstUuid
     * @return
     */
    Page<TaskOperationQueryItem> getSubflowRelateOperation(String flowInstUuid, String keyword,
                                                           Page<TaskOperationQueryItem> page);

    /**
     * 根据父环节实例UUID获取所有子流程的操作记录
     *
     * @param parentTaskInstUuid
     * @return
     */
    List<TaskOperation> listByParentTaskInstUuid(String parentTaskInstUuid);

    /**
     * 根据环节实例UUID获取所有子流程的操作记录
     *
     * @param taskInstUuid
     * @return
     */
    List<TaskOperation> listByTaskInstUuid(String taskInstUuid);

    /**
     * 根据环节实例UUID列表获取所有子流程的操作记录
     *
     * @param taskInstUuids
     * @return
     */
    List<TaskOperation> listByTaskInstUuids(ArrayList<String> taskInstUuids);

    /**
     * 通过时间区间 获取环节操作里的流程实例uuid列表
     * flow_inst_uuid
     *
     * @param startDateTime
     * @param endDateTime
     * @return java.util.List<com.wellsoft.pt.bpm.engine.entity.TaskOperation>
     **/
    List<TaskOperation> getTaskOperationListByTimeInterval(String startDateTime, String endDateTime);

    /**
     * 根据流程实例UUID、操作代码，获取操作的用户ID列表
     *
     * @param flowInstUuid
     * @param actionCodes
     * @return
     */
    List<String> listAssigneeByFlowInstUuidAndActionCodes(String flowInstUuid, List<Integer> actionCodes);

    /**
     * 根据流程实例UUID、操作代码，获取操作列表
     *
     * @param flowInstUuid
     * @param actionCodes
     * @return
     */
    List<TaskOperation> listByFlowInstUuidAndActionCodes(String flowInstUuid, List<Integer> actionCodes);

    /**
     * @param taskInstUuid
     * @return
     */
    TaskOperation getLastestByTaskInstUuid(String taskInstUuid);

    /**
     * @param flowInstUuid
     * @return
     */
    TaskOperation getLastestByFlowInstUuid(String flowInstUuid);

    /**
     * @param taskInstUuid
     * @param actionCodes
     * @return
     */
    List<TaskOperation> listByTaskInstUuidAndActionCodes(String taskInstUuid, List<Integer> actionCodes);
}
