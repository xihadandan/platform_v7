/*
 * @(#)2012-11-26 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.bpm.engine.element.NewFlowElement;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.form.Record;
import com.wellsoft.pt.bpm.engine.form.TaskForm;
import com.wellsoft.pt.bpm.engine.support.*;
import com.wellsoft.pt.bpm.engine.support.groupchat.FlowGroupChatProvider;
import com.wellsoft.pt.bpm.engine.support.groupchat.StartGroupChat;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.workflow.work.bean.*;
import com.wellsoft.pt.workflow.work.vo.SubTaskDataVo;
import org.springframework.security.acls.model.Permission;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 工作流程服务类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-26.1	zhulh		2012-11-26		Create
 * </pre>
 * @date 2012-11-26
 */
public interface WorkService extends BaseService {

//    /**
//     * 根据用户ID获取用户信息
//     *
//     * @param queryInfo
//     * @return
//     */
//    List<MultiOrgUserAccount> getUsers(List<String> userIds);

    /**
     //     * 根据用户ID获取用户信息
     //     *
     //     * @param queryInfo
     //     * @return
     //     */
//    List<User> queryForUsers(String queryValue, List<String> limitUserIds);

//    /**
//     * 如何描述该方法
//     *
//     * @param userIds
//     * @param queryKey
//     * @return
//     */
//    List<QueryItem> queryUsers(List<String> userIds, String queryValue);

    /**
     * 分页查询
     *
     * @param queryInfo
     * @return
     */
    List<FlowDefinition> query(QueryInfo queryInfo);

    List<FlowDefinition> queryRecentUse(QueryInfo queryInfo, boolean isMobile);

    /**
     * 获取待办的工作信息
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @return
     */
    WorkBean getTodo(String taskInstUuid, String flowInstUuid);

    /**
     * 获取工作流程的待办信息
     *
     * @param flowInstUuid
     * @return
     */
    TaskInstance getTodoTaskByFlowInstUuid(String flowInstUuid);

    /**
     * 获取工作流程的已办信息
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @return
     */
    WorkBean getDone(String taskInstUuid, String flowInstUuid);

    /**
     * 获取工作流程的已办信息
     *
     * @param taskInstUuid
     * @param taskIdentityUuid
     * @param flowInstUuid
     * @return
     */
    WorkBean getDone(String taskInstUuid, String taskIdentityUuid, String flowInstUuid);

    /**
     * 获取工作流程的办结信息
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @return
     */
    WorkBean getOver(String taskInstUuid, String flowInstUuid);

    /**
     * 获取未阅的工作信息
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param openToRead
     * @return
     */
    WorkBean getUnread(String taskInstUuid, String flowInstUuid, boolean openToRead);

    /**
     * 获取已阅的工作信息
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @return
     */
    WorkBean getRead(String taskInstUuid, String flowInstUuid);

    /**
     * 获取我关注的工作信息
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @return
     */
    WorkBean getAttention(String taskInstUuid, String flowInstUuid);

    /**
     * 获取督办的工作信息
     *
     * @param flowInstUuid
     * @return
     */
    WorkBean getSupervise(String taskInstUuid, String flowInstUuid);

    /**
     * 获取监控的工作信息
     *
     * @param flowInstUuid
     * @return
     */
    WorkBean getMonitor(String taskInstUuid, String flowInstUuid);

    /**
     * 获取查阅的工作信息
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @return
     */
    WorkBean getViewer(String taskInstUuid, String flowInstUuid);

    /* add by huanglinchuan 2014.10.31 begin */
    // 获取工作信息
    WorkBean getByPermissions(String taskInstUuid, String flowInstUuid, List<Permission> permissions);

    /* add by huanglinchuan 2014.10.31 end */

    /**
     * 获取草稿的工作信息
     *
     * @param flowInstUuid
     * @return
     */
    WorkBean getDraft(String flowInstUuid);

    /**
     * 获取环节表单
     *
     * @param workBean
     * @return
     */
    TaskForm getTaskForm(WorkBean workBean);

    /**
     * 删除工作草稿
     *
     * @param flowInstUuids
     */
    void deleteDraft(Collection<String> flowInstUuids);

    /**
     * 删除工作
     *
     * @param taskInstUuids
     */
    void delete(Collection<String> taskInstUuids);

    /**
     * 置顶
     *
     * @param taskInstUuids
     */
    void topping(Collection<String> taskInstUuids);

    /**
     * 取消置顶
     *
     * @param taskInstUuids
     */
    void untopping(Collection<String> taskInstUuids);

    /**
     * 管理员删除工作
     *
     * @param taskInstUuids
     */
    void deleteByAdmin(Collection<String> taskInstUuids);

    /**
     * 管理员删除工作-假删除
     *
     * @param taskInstUuids
     */
    void logicalDeleteByAdmin(Collection<String> taskInstUuids);

    /**
     * 恢复逻辑删除的流程
     *
     * @param taskInstUuids
     */
    void recover(Collection<String> taskInstUuids);

    /**
     * 新建工作
     *
     * @param flowDefUuid
     * @return
     */
    WorkBean newWork(String flowDefUuid);

    /**
     * 新建工作
     *
     * @param flowDefId
     * @return
     */
    WorkBean newWorkById(String flowDefId);

    /**
     * 保存工作信息，返回流程实例UUID
     *
     * @param workBean
     */
    Map<String, String> save(WorkBean workBean);

    /**
     * 保存工作信息为临时数据，返回流程实例UUID
     *
     * @param workBean
     * @return
     */
    Map<String, String> saveTemp(WorkBean workBean);

    /**
     * @param workBean
     */
    void fillTaskTodoTempIfRequired(WorkBean workBean);

    /**
     * 提交工作信息
     *
     * @param workBean
     */
    ResultMessage submit(WorkBean workBean);

    /**
     * 完成工作信息
     *
     * @param workBean
     * @return
     */
    ResultMessage complete(WorkBean workBean);

    /**
     * 返回动态表单定义与数据等工作信息
     *
     * @param workBean
     * @return
     */
    WorkBean getWorkData(WorkBean workBean);

    /**
     * 加载分支流程数据
     *
     * @param branchTaskData
     * @return
     */
    BranchTaskData loadBranchTaskData(BranchTaskData branchTaskData);

    /**
     * 加载子流程数据
     *
     * @param subTaskData
     * @return
     */
    SubTaskData loadSubTaskData(SubTaskData subTaskData);

    /**
     * 加载子流程的标题格式
     *
     * @param flowInstUuid 主流程实例UUID
     * @param taskInstUuid 子流程任务实例UUID
     * @param subFlowId    子流程ID
     * @return
     */
    String loadSubFlowTitleExpression(String flowInstUuid, String taskInstUuid, String subFlowId);

    /**
     * 加载子流程的子流程弹框数据
     *
     * @param flowInstUuid 主流程实例UUID
     * @param taskInstUuid 子流程任务实例UUID
     * @param taskId       环节ID
     * @param flowDefId    子流程本身的流程ID
     * @return
     */
    NewFlowElement loadSubFlowElement(String flowInstUuid, String taskInstUuid, String taskId, String flowDefId);

    /**
     * 加载子流程默认展开的配置
     *
     * @param subTaskData flowInstUuid、taskInstUuid
     * @return
     */
    String loadExpandSetting(SubTaskData subTaskData);

    /**
     * 加载子流程默认展开的配置
     *
     * @param flowInstUuid、taskInstUuid
     * @return
     */
    String loadExpandSetting(String flowInstUuid, String taskInstUuid);

    /**
     * 获取办理过程
     *
     * @param workBean
     * @return
     */
    String getProcess(String flowInstUuid, boolean showRollbackRecord, boolean showNoOpinionRecord);

    /**
     * 根据流程环节实例UUID、流程定义ID获取环节办理信息
     *
     * @param taskInstUuid
     * @param flowDefId
     * @return
     */
    Map<String, Map<String, Object>> getTaskProcessByTaskInstUuidAndFlowDefId(String taskInstUuid, String flowDefId);

    /**
     * 获取办理过程
     *
     * @param workBean
     * @return
     */
    List<WorkProcessBean> getWorkProcess(String flowInstUuid);

    /**
     * 获取办理过程
     *
     * @param workBean
     * @return Map<流程实例UUID, List < WorkProcessBean>>
     */
    Map<String, List<WorkProcessBean>> getWorkProcesses(Collection<String> flowInstUuids);

    /**
     * 获取分支流办理过程
     *
     * @param taskInstUuids
     * @return
     */
    Map<String, List<WorkProcessBean>> getBranchTaskProcesses(Collection<String> taskInstUuids);

    /**
     * 获取时间轴信息
     *
     * @param flowInstUuid
     * @return
     */
    WorkTimelineBean getTimeline(String flowInstUuid);

    /**
     * 退回工作，现为人操作，退回前一步
     *
     * @param workBean
     */
    public ResultMessage rollback(WorkBean workBean);

    /**
     * @param workBean
     */
    public ResultMessage rollbackWithWorkData(WorkBean workBean);

    /**
     * 退回主流程
     *
     * @param workBean
     */
    public ResultMessage rollbackToMainFlowWithWorkData(WorkBean workBean);

    /**
     * 撤回工作，已为人操作，退回前一步
     *
     * @param workBean
     */
    public void cancel(Collection<String> taskInstUuids);

    /**
     * 撤回工作，已为人操作，退回前一步
     *
     * @param workBean
     */
    public void cancelWithOpinion(Collection<String> taskInstUuids, String opinionText);

    /**
     * 撤回工作，已为人操作，退回前一步
     *
     * @param workBean
     */
    public void cancelWithWorkData(WorkBean workBean);

    /**
     * 撤回已结束工作，已为人操作，退回前一步
     *
     * @param workBean
     */
    public void cancelOver(Collection<String> flowInstUuids);

    /**
     * 工作委托转办
     *
     * @param taskInstUuids
     * @param userIds
     */
    void transfer(Collection<String> taskInstUuids, List<String> userIds, String opinionName, String opinionValue,
                  String opinionText, List<LogicFileInfo> opinionFiles);

    /**
     * 工作委托转办
     *
     * @param workBean
     */
    void transferWithWorkData(WorkBean workBean, List<String> userIds);

    /**
     * 工作会签
     *
     * @param taskInstUuids
     * @param userIds
     */
    void counterSign(Collection<String> taskInstUuids, List<String> userIds, String opinionName, String opinionValue,
                     String opinionText, List<LogicFileInfo> opinionFiles);

    /**
     * 工作会签
     *
     * @param workBean
     */
    void counterSignWithWorkData(WorkBean workBean, List<String> userIds);

    /**
     * 工作加签
     *
     * @param workBean
     * @param userIds
     */
    void addSignWithWorkData(WorkBean workBean, List<String> userIds);

    /**
     * 关注工作
     *
     * @param taskInstUuids
     */
    void attention(Collection<String> taskInstUuids);

    /**
     * 根据套打模板ID获取打印类型
     *
     * @param printTemplateId
     */
    String getPrintType(String printTemplateId);

    /**
     * 工作套打（html/word）
     *
     * @param printTemplateId
     * @param workBean
     * @return
     */
    LogicFileInfo print(String taskInstUuid, String printTemplateId);

    /**
     * 工作套打（html/word）
     *
     * @param taskInstUuid
     * @param printTemplateId
     * @param printTemplateUuid
     * @param lang
     * @return
     */
    LogicFileInfo print(String taskInstUuid, String printTemplateId, String printTemplateUuid, String lang);

    /**
     * 获取表单附件字段的定义信息
     *
     * @param taskInstUuid
     * @return
     */
    List<DyformFieldDefinition> getDyformFileFieldDefinitions(String taskInstUuid);

    /**
     * 工作抄送
     *
     * @param taskInstUuids
     * @param rawCopyUser
     * @param aclRole
     */
    void copyTo(Collection<String> taskInstUuids, List<String> rawCopyUser, String aclRole);

    @Transactional(readOnly = true)
    List<TreeNode> getPrintTemplatesTree(String taskInstUuid, String flowInstUuid);

    /**
     * 工作催办
     *
     * @param taskInstUuids
     * @param opinionName
     * @param opinionValue
     * @param opinionText
     * @param opinionFiles
     */
    void remind(Collection<String> taskInstUuids, String opinionName, String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles);

    /**
     * 工作标记已阅
     *
     * @param workBean
     */
    void markRead(Collection<String> taskInstUuids);

    /**
     * 工作标记未阅
     *
     * @param workBean
     */
    void markUnread(Collection<String> taskInstUuids);

    /**
     * 工作取消关注
     *
     * @param workBean
     */
    void unfollow(Collection<String> taskInstUuids);

    /**
     * 查看主流程设置
     *
     * @param flowInstUuid
     * @param taskId
     * @param childLookParent
     */
    void setViewMainFlow(String flowInstUuid, String taskId, String childLookParent);

    /**
     * 获取打印模板
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @return
     */
    List<PrintTemplate> getPrintTemplates(String taskInstUuid, String flowInstUuid);

    /**
     * 工作移交
     *
     * @param taskInstUuids
     * @param rawHandOverUser
     */
    void handOver(WorkBean workBean, List<String> rawHandOverUser);

    /**
     * 获取可跳转的环节，不包含开始环节、当前环节、子环节
     *
     * @param taskInstUuid
     * @return
     */
    Map<String, Object> getGotoTasks(String taskInstUuid);

    /**
     * 获取当前用户的意见用于签署
     *
     * @return
     */
    WorkOpinionBean getCurrentUserOpinion2Sign(String flowDefUuid, String taskId);

    /**
     * @param flowInstUuid
     * @return
     */
    List<TaskOpinionPositionConfig> getOpinionPositionConfigsByFlowInstUuid(String flowInstUuid);

    /**
     * 签署意见
     *
     * @param workBean
     * @return
     */
    void signOpinion(Collection<String> taskInstUuids, String text, String value);

    /**
     * 获取办理意见
     *
     * @param workBean
     * @return
     */
    String getOpinions(WorkBean workBean);

    /**
     * 获取前一环节的任务操作
     *
     * @param workBean
     * @return
     */
    List<TaskOperation> getPreTaskOperations(String taskInstUuid);

    /**
     * 工作挂起
     *
     * @param taskInstUuids
     */
    void suspend(Collection<String> taskInstUuids);

    /**
     * 工作恢复
     *
     * @param taskInstUuids
     */
    void resume(Collection<String> taskInstUuids);

    /**
     * 工作计时器挂起
     *
     * @param taskInstUuids
     */
    void suspendTimer(Collection<String> taskInstUuids);

    /**
     * 工作计时器恢复
     *
     * @param taskInstUuids
     */
    void resumeTimer(Collection<String> taskInstUuids);

    /**
     * 工作拒绝，直接结束
     *
     * @param taskInstUuids
     */
    void reject(Collection<String> taskInstUuids);

    /**
     * 工作跳转
     *
     * @param taskInstUuids
     */
    ResultMessage gotoTask(WorkBean workBean);

    /**
     * 设置流程标题
     *
     * @param flowDefinition
     * @param flowInstance
     * @param taskData
     * @param dyFormData
     */
    void setFlowInstanceTitle(FlowDefinition flowDefinition, FlowInstance flowInstance, TaskData taskData,
                              DyFormData dyFormData);

    /**
     * 根据环节操作UUID，获取操作信息
     *
     * @param taskOperationUuid
     * @return
     */
    TaskOperation getTaskOperation(String taskOperationUuid);

    /**
     * 流程时效表
     *
     * @param taskInstUUids
     * @return
     */
    void genTime(Collection<String> taskInstUUids, HttpServletResponse response);

    /**
     * 判断taskInstUUids是否是同一个类型流程
     *
     * @param taskInstUUids
     * @return
     */
    String isSameFlow(Collection<String> taskInstUUids);

    public Map<String, Object> viewReadLog(String taskInstUuid);

    /**
     * 根据流程实例UUID获取数据快照列表
     *
     * @param flowInstUuid
     * @return
     */
    List<FlowDataSnapshot> listFlowDataSnapshotWithoutDyformDataByFlowInstUuid(String flowInstUuid);

    /**
     * 根据快照ID列表获取快照数据
     *
     * @param ids
     * @return
     */
    List<String> getFlowDataSnapshotByIds(String... ids);

    /**
     * @param objectId
     * @return
     */
    FlowDataSnapshotAuditLog getFlowDataSnapshotAuditLogByObjecId(String objectId);

    /**
     * @param flowInstUuid
     * @param fieldNames
     * @return
     */
    FlowDataDyformFieldModifyInfo getDyformFieldModifyInfo(String flowInstUuid, List<String> fieldNames);

    /**
     * 获取流程实例UUID获取流程办理状态信息
     *
     * @param flowInstUuid
     * @return
     */
    FlowHandingStateInfoDto getFlowHandingStateInfo(String flowInstUuid);

    WorkBean getShare(String taskUuid, String flowInstUuid);

    /**
     * 工作加锁
     *
     * @param taskInstUuid
     */
    void lockWork(String taskInstUuid);

    /**
     * 工作解锁
     *
     * @param taskInstUuid
     */
    void unlockWork(String taskInstUuid);

    /**
     * 获取环节锁信息
     *
     * @param taskInstUuid
     * @return
     */
    List<TaskLockInfo> listLockInfo(String taskInstUuid);

    /**
     * 获取环节锁信息
     *
     * @param taskInstUuids
     * @return
     */
    Map<String, List<TaskLockInfo>> listAllLockInfo(Collection<String> taskInstUuids);

    /**
     * 释放锁
     *
     * @param taskInstUuids
     */
    void releaseAllLock(Collection<String> taskInstUuids);

    /**
     * 提交流程-手写签批
     *
     * @param workBean
     * @return java.lang.Boolean
     **/
    public void submitFlowVisa(WorkBean workBean);

    /**
     * 检查信息记录表达式是否满足条件
     *
     * @param taskInstUuid
     * @param flowInstUuid
     * @param flowDefUuid
     * @param dyFormData
     * @param record
     * @param opinionLabel
     * @param opinionValue
     * @param opinionText
     * @return
     */
    boolean checkRecordPreCondition(String taskInstUuid, String flowInstUuid, String flowDefUuid, DyFormData dyFormData,
                                    Record record, String opinionLabel, String opinionValue, String opinionText);

    /**
     * 分页加载承办信息
     *
     * @param vo
     * @return
     */
    List<FlowShareData> loadShareDatasByPage(SubTaskDataVo vo);

    Page<TaskInfoDistributionData> loadDistributeInfosByPage(SubTaskDataVo vo);

    Page<TaskOperationData> loadRelateOperationByPage(SubTaskDataVo vo);

    /**
     * 获取用户排序字段
     *
     * @return
     * @author baozh
     * @date 2022/1/5 16:34
     * @params vo
     */
    List<Map<String, String>> getSortFields();

    TaskOperationTemp getTaskOperationTemp(String flowInstUuid, String taskInstUuid);

    /**
     * 从流程数据创建TaskData
     *
     * @param workBean
     * @return
     */
    TaskData createTaskDataFromWorkData(WorkBean workBean);

    /**
     * 必填字段是否为空
     *
     * @param workBean
     * @return
     */
    boolean requiredFieldIsBlank(WorkBean workBean);

    /**
     * 获取操作提示信息
     *
     * @param actionId
     * @param actionCode
     * @param taskInstUuid
     * @param taskId
     * @param toTaskId
     * @param taskIdentityUuid
     * @param flowInstUuid
     * @param flowDefUuid
     * @return
     */
    Map<String, Object> getActionTipWithActionId(String actionId, String actionCode, String taskInstUuid, String taskId, String toTaskId,
                                                 String taskIdentityUuid, String flowInstUuid, String flowDefUuid);

    /**
     * 发起群聊
     *
     * @param provider
     * @param startGroupChat
     */
    String startGroupChat(String provider, StartGroupChat startGroupChat);

    /**
     * @return
     */
    List<FlowGroupChatProvider.ProviderInfo> listGroupChatProvider();


//    void translateFlowInstanceTitle(String flowInstanceUuid);
}
