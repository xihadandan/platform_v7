/*
 * @(#)2012-11-2 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.form.TaskForm;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.InteractionTaskData;
import com.wellsoft.pt.bpm.engine.support.PrintResult;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.bpm.engine.support.TaskLockInfo;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.timer.support.TimerWorkTime;
import org.springframework.security.acls.model.Permission;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 流程任务的操作服务接口类
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
public interface TaskService {

    /**
     * 保存流程实例
     *
     * @param task
     */
    public void save(TaskInstance task);

    /**
     * 删除流程实例
     *
     * @param taskInstUuid
     */
    public void delete(String userId, String taskInstUuid);

    /**
     * 管理员删除流程实例
     *
     * @param userId
     * @param taskInstUuid
     */
    public void deleteByAdmin(String userId, String taskInstUuid);

    /**
     * 管理员删除流程实例-假删除
     *
     * @param userId
     * @param taskInstUuid
     */
    public void logicalDeleteByAdmin(String userId, String taskInstUuid);

    /**
     * 强制删除流程实例
     *
     * @param userId
     * @param taskInstUuid
     */
    void forceDelete(String userId, String taskInstUuid);

    /**
     * 恢复逻辑删除的流程
     *
     * @param userId
     * @param taskInstUuid
     */
    void recover(String userId, String taskInstUuid);

    /**
     * 任务签署意见
     *
     * @param uuid
     */
    public void remark(String uuid);

    /**
     * 任务签署意见
     *
     * @param task
     */
    public void remark(TaskInstance task);

    /**
     * 提交任务
     *
     * @param taskInstUuid 任务实例UUID
     * @param taskData     任务数据
     */
    public void submit(String taskInstUuid, TaskData taskData);

    /**
     * 提交任务
     *
     * @param taskInstance 任务实例
     * @param taskData     任务数据
     */
    public void submit(TaskInstance taskInstance, TaskData taskData);

    /**
     * 直接退回任务，现为人操作，退回前一步
     *
     * @param taskInstUuid
     */
    public void rollback(String taskInstUuid, TaskData taskData);

    /**
     * 退回主流程
     *
     * @param taskInstUuid
     * @param taskData
     */
    public void rollbackToMainFlow(String taskInstUuid, TaskData taskData);

    // /**
    // * 直接退回任务，现为人操作，退回前一步
    // *
    // * @param taskInstUuid
    // */
    // public void rollback(String taskInstUuid);
    //
    // /**
    // * 直接退回任务，现为人操作，退回前一步
    // *
    // * @param taskInstance
    // */
    // public void rollback(TaskInstance taskInstance);
    //
    // /**
    // * 退回任务到指定环节，现为人操作，退回前一步
    // *
    // * @param taskInstUuid
    // * @param taskId
    // */
    // public void rollback(String taskInstUuid, String taskId);
    //
    // /**
    // * 退回任务到指定环节，现为人操作，退回前一步
    // *
    // * @param taskInstance
    // * @param taskId
    // */
    // public void rollback(TaskInstance taskInstance, String taskId);

    /**
     * 撤回任务，已为人操作，退回前一步
     *
     * @param uuid
     */
    public void cancel(String uuid);

    /**
     * 撤回任务，已为人操作，退回前一步
     *
     * @param uuid
     * @param opinionText
     */
    public void cancel(String uuid, String opinionText);

    /**
     * 撤回任务，已为人操作，退回前一步
     *
     * @param uuid
     * @param taskData
     */
    public void cancel(String uuid, TaskData taskData);

    /**
     * 撤回任务，已为人操作，退回前一步
     *
     * @param taskInstance
     */
    public void cancel(TaskInstance taskInstance);

    /**
     * 撤回任务，已为人操作，退回前一步
     *
     * @param taskInstance
     * @param opinionText
     */
    public void cancel(TaskInstance taskInstance, String opinionText);

    /**
     * 撤回任务，已为人操作，退回前一步
     *
     * @param taskInstUuid
     * @param taskData
     */
    public void cancel(TaskInstance taskInstance, TaskData taskData);

    /**
     * 撤回已结束任务，已为人操作，退回前一步
     *
     * @param flowInstUuid
     */
    public void cancelOver(String flowInstUuid);

    /**
     * 撤回已结束任务，办理人为最后环节的所有者
     *
     * @param flowInstUuid
     * @param opinionText
     */
    public void cancelOverWithLastTaskOwner(String flowInstUuid, String opinionText);

    /**
     * 任务转办
     *
     * @param uuid
     */
    public void transfer(String userId, String uuid, List<String> userIds, String opinionName, String opinionValue,
                         String opinionText, List<LogicFileInfo> opinionFiles, TaskData taskData);

    /**
     * 任务转办
     *
     * @param taskInstance
     */
    public void transfer(String userId, TaskInstance taskInstance, List<String> userIds, String opinionName,
                         String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles, TaskData taskData);

    /**
     * 任务会签
     *
     * @param uuid
     */
    public void counterSign(String userId, String uuid, List<String> userIds, String opinionName, String opinionValue,
                            String opinionText, List<LogicFileInfo> opinionFiles, TaskData taskData);

    /**
     * 任务会签
     *
     * @param task
     */
    public void counterSign(String userId, TaskInstance task, List<String> userIds, String opinionName,
                            String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles, TaskData taskData);


    /**
     * 任务加签
     *
     * @param userId
     * @param taskInstUuid
     * @param userIds
     * @param opinionName
     * @param opinionValue
     * @param opinionText
     * @param opinionFiles
     * @param taskData
     */
    void addSign(String userId, String taskInstUuid, List<String> userIds, String opinionName, String opinionValue,
                 String opinionText, List<LogicFileInfo> opinionFiles, TaskData taskData);

    /**
     * 关注任务
     *
     * @param uuid
     */
    public void attention(String userId, String uuid);

    /**
     * 关注任务
     *
     * @param task
     */
    public void attention(String userId, TaskInstance task);

    /**
     * 任务公文套打
     *
     * @param uuid
     * @param printTemplateId
     */
    public PrintResult print(String uuid, String printTemplateId);

    /**
     * 任务公文套打(支持多版本)
     *
     * @param uuid
     * @param printTemplateId
     * @param printTemplateUuid
     * @param lang
     * @return
     */
    public PrintResult print(String uuid, String printTemplateId, String printTemplateUuid, String lang);

    /**
     * 任务公文套打
     *
     * @param task
     */
    public PrintResult print(TaskInstance task, String printTemplateId);

    /**
     * 返回文件流-归档流程套打
     *
     * @param task              最新的环节
     * @param printTemplateId   打印模板ID
     * @param printTemplateUuid 打印模板UUID
     * @param lang              字符集
     * @return java.io.InputStream
     **/
    public InputStream getPrintResultAsInputStream(TaskInstance task, String printTemplateId, String printTemplateUuid,
                                                   String lang);

    /**
     * 工作抄送
     *
     * @param userId
     * @param taskInstUuid
     * @param rawCopyUsers
     * @param aclRole
     */
    public void copyTo(String userId, String taskInstUuid, List<String> rawCopyUsers, String aclRole);

    /**
     * 工作抄送
     *
     * @param userId
     * @param taskInstance
     * @param rawCopyUsers
     * @param aclRole
     */
    public void copyTo(String userId, TaskInstance taskInstance, List<String> rawCopyUsers, String aclRole);

    /**
     * 工作催办
     *
     * @param taskInstUuid
     */
    public void remind(String taskInstUuid, String opinionName, String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles);

    /**
     * 工作催办
     *
     * @param task
     */
    public void remind(TaskInstance task, String opinionName, String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles);

    /**
     * 工作标记已阅
     *
     * @param taskInstUuid
     */
    public void markRead(String taskInstUuid);

    /**
     * 工作标记已阅
     *
     * @param task
     */
    public void markRead(TaskInstance task);

    /**
     * 工作标记未阅
     *
     * @param taskInstUuid
     */
    public void markUnread(String taskInstUuid);

    /**
     * 工作标记未阅
     *
     * @param task
     */
    public void markUnread(TaskInstance task);

    /**
     * 工作取消关注
     *
     * @param taskInstUuid
     */
    public void unfollow(String taskInstUuid);

    /**
     * 工作取消关注
     *
     * @param task
     */
    public void unfollow(TaskInstance task);

    /**
     * 工作移交，将用户userId的工作移交给其他人办理
     *
     * @param userId
     * @param taskInstUuid
     * @param rawHandOverUser
     */
    void handOver(String userId, String taskInstUuid, List<String> rawHandOverUser, String opinionName, String opinionValue,
                  String opinionText, List<LogicFileInfo> opinionFiles, boolean requiredHandOverPermission);

    /**
     * 工作移交，将用户userId的工作移交给其他人办理
     *
     * @param userId
     * @param taskInstUuid
     * @param rawHandOverUser
     */
    void handOver(String userId, String taskInstUuid, List<String> rawHandOverUser, String opinionName, String opinionValue,
                  String opinionText, List<LogicFileInfo> opinionFiles, boolean requiredHandOverPermission, TaskData taskData);

    /**
     * 签署意见
     *
     * @param taskInstUuid
     * @param text
     * @param value
     */
    public void signOpinion(String taskInstUuid, String text, String value);

    /**
     * 获取可跳转的环节，不包含开始环节、当前环节、子环节
     *
     * @param taskInstUuid
     * @return
     */
    public Map<String, Object> getToTasks(String taskInstUuid);

    /**
     * @param flowDefinition
     * @param flowInstance
     * @param task
     * @param taskNode
     */
    public TaskInstance save(FlowDefinition flowDefinition, FlowInstance flowInstance, TaskInstance brotherTask,
                             Node taskNode);

    /**
     * 增加工作流操作中相关的权限
     *
     * @param flowInstance
     * @param wfDraft
     * @param sid
     */
    // public void addPermission(FlowInstance flowInstance, Permission
    // permission, String sid);

    /**
     * 根据任务UUID获取任务
     *
     * @param taskUid
     * @return
     */
    TaskInstance get(String taskInstUuid);

    /**
     * 获取流程环节实例UUID获取表单数据
     *
     * @param taskInstUuid
     * @return
     */
    DyFormData getFormData(String taskInstUuid);

    /**
     * 获取流程环节实例获取表单数据
     *
     * @param taskInstance
     * @return
     */
    DyFormData getFormData(TaskInstance taskInstance);

    /**
     * 任务列表查询
     *
     * @param taskPage
     */
    public void query(Page<TaskInstance> taskPage, String hql, Object... values);

    /**
     * 根据任务UUID获取任务表单信息
     *
     * @param taskInstUuid
     * @return
     */
    public TaskForm getTaskForm(String taskInstUuid);

    /**
     * 获取第一个任务表单信息
     *
     * @param flowDefUuid
     * @return
     */
    public TaskForm getStartTaskForm(String flowDefUuid);

    /**
     * 根据任务UUID获取任务配置信息
     *
     * @param uuid
     * @return
     */
    public TaskData getConfigInfo(String taskInstUuid);

    /**
     * 获取环节数据
     *
     * @param taskInstance
     * @return
     */
    public TaskData getTaskData(TaskInstance taskInstance);

    /**
     * 根据任务UUID获取下一任务配置信息
     *
     * @param taskInstUuid
     * @return
     */
    public TaskData getNextConfigInfo(String taskInstUuid);

    /**
     * 根据任务UUID获取下一任务配置信息
     *
     * @param taskInstUuid
     * @return
     */
    TaskData getNextConfigInfo(String taskInstUuid, TaskData taskData);

    /**
     * 根据流程实例UUID，获取流程实例未完成的任务列表
     *
     * @param flowInstUuid
     * @return
     */
    public List<TaskInstance> getUnfinishedTasks(String flowInstUuid);

    /**
     * 根据流程实例UUID，获取流程实例未完成的任务UUID列表
     *
     * @param flowInstUuid
     * @return
     */
    public List<String> getUnfinishedTaskInstanceUuids(String flowInstUuid);

    /**
     * 根据并行环节实例UUID，获取流程实例未完成的任务列表
     *
     * @param parallelTaskInstUuids
     * @return
     */
    List<TaskInstance> listUnfinishedTaskByParallelTaskInstUuids(List<String> parallelTaskInstUuids);

    /**
     * 根据流程实例UUID，获取流程实例已完成的任务列表
     *
     * @param flowInstUuid
     * @return
     */
    public List<TaskInstance> getFinishedTasks(String flowInstUuid);

    /**
     * 根据流程实例UUID，获取流程实例已完成的任务UUID列表
     *
     * @param flowInstUuid
     * @return
     */
    public List<String> getFinishedTaskInstanceUuids(String flowInstUuid);

    /**
     * 根据流程实例UUID，获取流程实例未完成的任务数量
     *
     * @param flowInstUuid
     * @return
     */
    public long countUnfinishedTasks(String flowInstUuid);

    /**
     * 根据流程实例UUID，获取指定用户的任务列表
     *
     * @param userId
     * @param flowInstUuid
     * @return
     */
    public List<TaskInstance> getTodoTasks(String userId, String flowInstUuid);

    /**
     * 根据流程实例UUID，获取所有任务列表
     *
     * @param flowInstUuid
     * @return
     */
    public List<TaskInstance> getAllByFlowInstUuid(String flowInstUuid);

    /**
     * 工作挂起
     *
     * @param taskInstUuid
     */
    public void suspend(String taskInstUuid);

    /**
     * 工作恢复
     *
     * @param taskInstUuid
     */
    public void resume(String taskInstUuid);

    /**
     * 工作计时器挂起
     *
     * @param taskInstUuid
     */
    public void suspendTimer(String taskInstUuid);

    /**
     * 工作计时器恢复
     *
     * @param taskInstUuid
     */
    public void resumeTimer(String taskInstUuid);

    /**
     * 工作拒绝，直接结束
     *
     * @param taskInstUuid
     */
    public void reject(String taskInstUuid);

    /**
     * 发起分支流程
     *
     * @param belongToTaskInstUuid
     * @param belongToFlowInstUuid
     * @param isMajor
     * @param taskUsers
     * @param businessType
     * @param businessRole
     * @param actionName
     */
    public List<String> startBranchTask(String belongToTaskInstUuid, String belongToFlowInstUuid, boolean isMajor,
                                        List<String> taskUsers, String businessType, String businessRole,
                                        String actionName);

    /**
     * 发起分支流程
     *
     * @param belongToTaskInstUuid
     * @param belongToFlowInstUuid
     * @param businessType
     * @param businessRole
     * @param actionName
     */
    public List<String> startBranchTask(String belongToTaskInstUuid, String belongToFlowInstUuid, String businessType,
                                        String businessRole, String actionName);

    /**
     * 发起子流程
     *
     * @param parentTaskInstUuid
     * @param parentFlowInstUuid
     * @param newFlowId
     * @param isMajor
     * @param taskUsers
     * @param businessType
     * @param businessRole
     * @param actionName
     * @return
     */
    public List<String> startSubFlow(String parentTaskInstUuid, String parentFlowInstUuid, String newFlowId,
                                     boolean isMajor, List<String> taskUsers, String businessType,
                                     String businessRole, String actionName);

    /**
     * 发起子流程
     *
     * @param parentTaskInstUuid
     * @param parentFlowInstUuid
     * @param businessType
     * @param businessRole
     * @param actionName
     */
    public List<String> startSubFlow(String parentTaskInstUuid, String parentFlowInstUuid, String businessType,
                                     String businessRole, String actionName);

    /**
     * 重新分发状态为分发失败的子流程
     *
     * @param parentTaskInstUuid
     */
    public void resendSubFlow(String parentTaskInstUuid);

    /**
     * 通过父流程实例UUID，终止子流程
     *
     * @param parentTaskInstUuid
     * @param action
     * @param actionType
     * @param opinionLabel
     * @param opinionValue
     * @param opinionText
     */
    public void stopByParentTaskInstUuid(String parentTaskInstUuid, String action, String actionType,
                                         String opinionLabel, String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles);

    /**
     * 通过父流程实例UUID，终止子流程
     *
     * @param parentFlowInstUuid
     * @param action
     * @param actionType
     * @param opinionLabel
     * @param opinionValue
     * @param opinionText
     */
    public void stopByParentFlowInstUuid(String parentFlowInstUuid, String action, String actionType,
                                         String opinionLabel, String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles);

    /**
     * 通过父流程实例UUID，终止子流程
     *
     * @param parentFlowInstUuid
     * @param action
     * @param actionType
     * @param opinionLabel
     * @param opinionValue
     * @param opinionText
     * @param sendMsg
     */
    public void stopByParentFlowInstUuid(String parentFlowInstUuid, String action, String actionType,
                                         String opinionLabel, String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles, boolean sendMsg);

    /**
     * 通过子流程环节信息，终止子流程
     *
     * @param taskSubFlow
     * @param action
     * @param actionType
     * @param opinionLabel
     * @param opinionValue
     * @param opinionText
     */
    public void stopByTaskSubFlow(TaskSubFlow taskSubFlow, String action, String actionType, String opinionLabel,
                                  String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles);

    /**
     * 通过子流程环节信息，终止子流程
     *
     * @param taskSubFlow
     * @param action
     * @param actionType
     * @param opinionLabel
     * @param opinionValue
     * @param opinionText
     * @param sendMsg
     */
    public void stopByTaskSubFlow(TaskSubFlow taskSubFlow, String action, String actionType, String opinionLabel,
                                  String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles, boolean sendMsg);

    /**
     * 通过环节实例UUID，终止流程分支
     *
     * @param taskInstUuid
     * @param action
     * @param actionType
     * @param actionObjects
     * @param opinionLabel
     * @param opinionValue
     * @param opinionText
     * @param interactionTaskData
     */
    public void stopBranchTask(String taskInstUuid, String action, String actionType, List<String> actionObjects,
                               String opinionLabel, String opinionValue, String opinionText,
                               InteractionTaskData interactionTaskData);

    /**
     * 通过流程实例UUID，终止流程
     *
     * @param flowInstUuid
     * @param action
     * @param actionType
     * @param actionObjects
     * @param opinionLabel
     * @param opinionValue
     * @param opinionText
     * @param interactionTaskData
     */
    public void stopFlow(String flowInstUuid, String action, String actionType, List<String> actionObjects,
                         String opinionLabel, String opinionValue, String opinionText,
                         InteractionTaskData interactionTaskData);

    /**
     * 信息分发
     *
     * @param taskInstUuid
     * @param content
     * @param fileIds
     */
    public void distributeInfo(String taskInstUuid, String content, List<String> fileIds);

    /**
     * 根据流程UUID获取相关的信息分发信息
     *
     * @param flowInstUuid
     * @return
     */
    public List<TaskInfoDistribution> getDistributeInfos(String flowInstUuid);

    /**
     * 根据流程实例UUID，变更流程到期时间
     *
     * @param flowInstUuid
     * @param dueTimeString
     * @param opinionText
     */
    public void changeFlowDueTime(String flowInstUuid, String dueTimeString, String opinionText);

    /**
     * 根据环节实例UUID、流程实例UUID，变更流程到期时间
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param dueTime
     * @param opinionText
     */
    public void changeTaskDueTime(String taskInstUuid, String flowInstUuid, String dueTime, String opinionText);

    /**
     * 获取环节计时器计时信息
     *
     * @param taskInstUuid
     * @return
     */
    List<TimerWorkTime> listTimerWorkTimeByTaskInstUuid(String taskInstUuid);

    /**
     * 获取流程操作日志
     *
     * @param taskInstUuid
     * @return
     */
    public Map<String, List<TaskOperation>> getOperationAsMap(String taskInstUuid);

    /**
     * 获取流程流转日志
     *
     * @param flowInstUuid
     * @return
     */
    public List<TaskActivity> getTaskActivities(String flowInstUuid);

    /**
     * 获取前一环节的办理人ID
     *
     * @param taskInstUuid
     * @return
     */
    public List<String> getPreTaskOperatorIds(String taskInstUuid);

    /**
     * 获取前一环节的办理人名字
     *
     * @param taskInstUuid
     * @return
     */
    public List<String> getPreTaskOperatorNames(String taskInstUuid);

    /**
     * 根据任务UUID获取该任务的待办人ID列表
     *
     * @param taskInstUuid
     * @return
     */
    public List<String> getTodoUserIds(String taskInstUuid);

    /**
     * 根据任务UUID获取该任务的待办人名称列表
     *
     * @param taskInstUuid
     * @return
     */
    public List<String> getTodoUserNames(String taskInstUuid);

    /**
     * 根据任务UUID获取该任务的待办人上级领导ID列表
     *
     * @param taskInstUuid
     * @return
     */
    public List<String> getTodoSuperiorUserIds(String taskInstUuid);

    /**
     * 根据任务UUID获取该任务的待办人ID列表
     *
     * @param taskInstUuid
     * @return
     */
    public List<String> getDoneUserIds(String taskInstUuid);

    /**
     * 根据任务UUID获取该任务的跟踪人ID列表
     *
     * @param taskInstUuid
     * @return
     */
    public List<String> getTraceUserIds(String taskInstUuid);

    /**
     * 根据任务UUID获取该任务的督办人ID列表
     *
     * @param taskInstUuid
     * @return
     */
    public List<String> getSuperviseUserIds(String taskInstUuid);

    /**
     * 根据任务UUID获取该任务的督办人ID列表
     *
     * @param taskInstUuid
     * @param includeAdmin
     * @return
     */
    public List<String> getSuperviseUserIds(String taskInstUuid, boolean includeAdmin);

    /**
     * 根据任务UUID获取该任务的监控人ID列表
     *
     * @param taskInstUuid
     * @return
     */
    public List<String> getMonitorUserIds(String taskInstUuid);

    /**
     * 根据任务UUID获取该任务的监控人ID列表
     *
     * @param taskInstUuid
     * @param includeAdmin
     * @return
     */
    public List<String> getMonitorUserIds(String taskInstUuid, boolean includeAdmin);

    /**
     * 根据任务UUID获取该任务的查阅人ID列表
     *
     * @param taskInstUuid
     * @param includeAdmin
     * @return
     */
    List<String> getViewerUserIds(String taskInstUuid, boolean includeAdmin);

    /**
     * 获取环节实例
     *
     * @param taskInstUuid
     * @return
     */
    public TaskInstance getTask(String taskInstUuid);

    /**
     * 根据环节实例UUID获取环节类型
     *
     * @param taskInstUuid
     * @return
     */
    public Integer getTaskType(String taskInstUuid);

    /**
     * 获取其他并发环节实例
     *
     * @param uuid
     * @param parallelTaskInstUuid
     * @return
     */
    public List<TaskInstance> getOtherParallelTasks(String taskInstUuid, String parallelTaskInstUuid);

    /**
     * 如何描述该方法
     *
     * @param taskInstUuid
     * @param permission
     * @param userId
     */
    // public boolean hasPermission(String taskInstUuid, Permission permission,
    // String userId);

    /**
     * 判断ACL权限
     *
     * @param userDetails
     * @param taskInstUuid
     * @param permission
     */
    public boolean hasPermission(UserDetails userDetails, String taskInstUuid, Permission permission);

    public boolean hasPermissionCurrentUser(String taskInstUuid, Integer[] masks);

    /**
     * 环节实例查询
     *
     * @param hql
     * @param values
     * @return
     */
    public <X> List<X> query(String hql, Map<String, Object> values);

    /**
     * 指定的用户ID是否对环节进行关注
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAttention(String userId, String taskInstUuid);

    /**
     * 指定的用户ID是否对环节进行抄送
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAllowedCopyTo(String userId, String taskInstUuid);

    /**
     * 指定的用户ID是否对环节进行关注
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAllowedAttention(String userId, String taskInstUuid);

    /**
     * 指定的用户ID是否对环节进行取消关注
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAllowedUnfollow(String userId, String taskInstUuid);

    /**
     * 指定的用户ID是否对环节进行催办
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAllowedRemind(String userId, String taskInstUuid);

    /**
     * 指定的用户ID是否对环节进行挂起
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAllowedSuspend(String userId, String taskInstUuid);

    /**
     * 指定的用户ID是否对环节进行恢复
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAllowedResume(String userId, String taskInstUuid);

    /**
     * 指定的用户ID是否允许对环节进行删除
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAllowedDelete(String userId, String taskInstUuid);

    /**
     * 指定的用户ID是否允许对环节进行移交
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAllowedHandOver(String userId, String taskInstUuid);

    /**
     * 转办必须签署意见
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isRequiredHandOverOpinion(String userId, String taskInstUuid);

    /**
     * 指定的用户ID是否允许对环节进行跳转
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAllowedGotoTask(String userId, String taskInstUuid);

    /**
     * 跳转必须签署意见
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isRequiredGotoTaskOpinion(String userId, String taskInstUuid);

    /**
     * 是否允许提交
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAllowedSubmit(String userId, String taskInstUuid);

    /**
     * 会签必须签署意见
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isRequiredSignOpinion(String userId, String taskInstUuid);

    /**
     * 指定的用户ID是否允许对环节进行撤回
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAllowedCancel(String userId, String taskInstUuid);

    /**
     * 判断撤回签署意见
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isRequiredCancelOpinion(String userId, String taskInstUuid);

    /**
     * 判断该环节是否可退回(退回到指定的环节)
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAllowedRollbackToTask(String userId, String taskInstUuid);

    /**
     * 判断该环节是否可直接退回(退回到指定的环节)
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAllowedDirectRollbackToTask(String userId, String taskInstUuid);

    /**
     * 判断该环节是否可直接退回主流程
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAllowedRollbackToMainFlow(String userId, String taskInstUuid);

    /**
     * 判断退回签署意见
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isRequiredRollbackOpinion(String userId, String taskInstUuid);

    /**
     * 判断该环节是否可转办
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAllowedTransfer(String userId, String taskInstUuid);

    /**
     * 判断转办签署意见
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isRequiredTransferOpinion(String userId, String taskInstUuid);

    /**
     * 判断是否允许会签
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isAllowedCounterSign(String userId, String taskInstUuid);

    /**
     * 会签必须签署意见
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean isRequiredCounterSignOpinion(String userId, String taskInstUuid);

    /**
     * 移交到指定环节
     *
     * @param taskInstUuid
     * @param gotoTaskId
     * @param taskData
     */
    public void gotoTask(String taskInstUuid, String gotoTaskId, TaskData taskData);

    /**
     * 获取任务的真正所有者
     *
     * @param taskInstUuid
     * @return
     */
    public Set<String> getTaskOwners(String taskInstUuid);

    /**
     * 获取其他未结束环节
     *
     * @param taskInstUuid
     * @param parallelTaskInstUuid
     * @return
     */
    public List<TaskInstance> getOtherUnfinishedParallelTasks(String taskInstUuid, String parallelTaskInstUuid);

    /**
     * 获取最新的环节
     *
     * @param flowInstUuid
     * @return
     */
    public TaskInstance getLastTaskInstanceByFlowInstUuid(String flowInstUuid);

    /**
     * 添加待办权限
     *
     * @param sid
     * @param taskInstUuid
     */
    void addTodoPermission(String sid, String taskInstUuid);

    /**
     * 添加待办权限
     *
     * @param sids
     * @param taskInstUuid
     */
    void addTodoPermission(Set<String> sids, String taskInstUuid);

    /**
     * 移除待办权限
     *
     * @param sid
     * @param taskInstUuid
     */
    public void removeTodoPermission(String sid, String taskInstUuid);

    /**
     * 添加已办权限
     *
     * @param sid
     * @param taskInstUuid
     */
    public void addDonePermission(String sid, String taskInstUuid);

    /**
     * 移除已办权限
     *
     * @param sid
     * @param entityUuid
     */
    public void removeDonePermission(String sid, String taskInstUuid);

    /**
     * 添加未阅权限
     *
     * @param userId
     * @param taskInstUuid
     */
    void addUnreadPermission(String userId, String taskInstUuid);

    /**
     * 添加未阅权限
     *
     * @param userIds
     * @param taskInstUuid
     */
    void addUnreadPermission(Set<String> userIds, String taskInstUuid);

    /**
     * 添加督办权限
     *
     * @param sid
     * @param taskInstUuid
     */
    public void addSupervisePermission(String sid, String taskInstUuid);

    /**
     * 添加督办权限
     *
     * @param sids
     * @param taskInstUuid
     */
    void addSupervisePermission(Set<String> sids, String taskInstUuid);

    /**
     * 添加监控权限
     *
     * @param sid
     * @param taskInstUuid
     */
    void addMonitorPermission(String sid, String taskInstUuid);

    /**
     * 添加监控权限
     *
     * @param sids
     * @param taskInstUuid
     */
    void addMonitorPermission(Set<String> sids, String taskInstUuid);


    /**
     * 添加查阅权限
     *
     * @param sids
     * @param taskInstUuid
     */
    void addViewerPermission(Set<String> sids, String taskInstUuid);

    /**
     * 移除督办权限
     *
     * @param sid
     * @param taskInstUuid
     */
    public void removeSupervisePermission(String sid, String taskInstUuid);

    /**
     * 添加查阅权限
     *
     * @param sid
     * @param taskInstUuid
     */
    public void addConsultPermission(String sid, String taskInstUuid);

    /**
     * 移除查阅权限
     *
     * @param sid
     * @param taskInstUuid
     */
    public void removeConsultPermission(String sid, String taskInstUuid);

    /**
     * 是否有待办权限
     *
     * @param sid
     * @param taskInstUuid
     * @return
     */
    public boolean hasTodoPermission(String sid, String taskInstUuid);

    /**
     * 是否有待办权限
     *
     * @param user
     * @param taskInstUuid
     * @return
     */
    public boolean hasTodoPermission(UserDetails user, String taskInstUuid);

    /**
     * 是否有已办权限
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean hasDonePermission(String userId, String taskInstUuid);

    /**
     * 是否有监控权限
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean hasMonitorPermission(String userId, String taskInstUuid);

    /**
     * 是否有监控权限
     *
     * @param user
     * @param taskInstUuid
     * @return
     */
    public boolean hasMonitorPermission(UserDetails user, String taskInstUuid);

    public boolean hasMonitorPermissionCurrentUser(String taskInstUuid);

    /**
     * 是否有督办权限
     *
     * @param userId
     * @param taskInstUuid
     * @return
     */
    public boolean hasSupervisePermission(String userId, String taskInstUuid);

    /**
     * 是否有督办权限
     *
     * @param user
     * @param taskInstUuid
     * @return
     */
    public boolean hasSupervisePermission(UserDetails user, String taskInstUuid);

    public boolean hasSupervisePermissionCurrentUser(String taskInstUuid);

    /**
     * 是否有查看权限
     *
     * @param user
     * @param taskInstUuid
     * @return
     */
    public boolean hasViewPermission(UserDetails user, String taskInstUuid);

    public boolean hasViewPermissionCurrentUser(String taskInstUuid);

    /**
     * 复制环节权限
     *
     * @param sourceTaskInstUuid
     * @param targetTaskInstUuid
     * @param requiredPermission
     * @param ignorePermission
     */
    void copyPermissions(String sourceTaskInstUuid, String targetTaskInstUuid, Permission requiredPermission,
                         Permission ignorePermission);

    /**
     * 置顶
     *
     * @param col
     */
    public void topping(Collection<String> col);

    /**
     * 取消置顶
     *
     * @param col
     */
    public void untopping(Collection<String> col);

    /**
     * 根据环节实例UUID获取流程实例UUID
     *
     * @param taskInstUuid
     * @return
     */
    public String getFlowInstUUidByTaskInstUuid(String taskInstUuid);

    List<String> getUnfinishedTaskUuids(String flowInstUuid);

    String getLastTaskInstanceUuidByFlowInstUuid(String flowInstUuid);

    /**
     * 根据环节实例UUID、流程实例UUID，获取流程实例未完成的任务UUID，指定的环节实例UUID未完成时返回该环节实例UUID，否则返回最新的环节实例UUID
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @return
     */
    String getLastTaskInstanceUuidByTaskInstUuidAndFlowInstUuid(String taskInstUuid, String flowInstUuid);

    /**
     * 添加当前用户的环节实例锁
     *
     * @param taskInstUuid
     */
    void addCurrentUserLock(String taskInstUuid);

    /**
     * 删除当前用户的环节实例锁
     *
     * @param taskInstUuid
     */
    void removeCurrentUserLock(String taskInstUuid);

    /**
     * @param userDetails
     */
    void removeAllUserLock(UserDetails userDetails);

    /**
     * 列出环节锁信息
     *
     * @param taskInstUuid
     * @return
     */
    List<TaskLockInfo> listTaskLock(String taskInstUuid);

    /**
     * 解锁
     *
     * @param taskInstUuid
     */
    void releaseLock(String taskInstUuid);

    Set<Permission> getCurrentUserPermissions(String taskInstUuid, String flowDefUuid);

    /**
     * 根据流程实例UUID获取所有阅读者ID列表
     *
     * @param flowInstUuid
     * @return
     */
    List<String> getReaderUserIdsByFlowInstUuid(String flowInstUuid);

    /**
     * 判断是否存在环节实例
     *
     * @param taskInstUuid
     * @return
     */
    boolean existsByUuid(String taskInstUuid);
}
