/*
 * @(#)2014-11-1 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.basicdata.business.dto.BusinessApplicationConfigDto;
import com.wellsoft.pt.bpm.engine.support.FlowShareItem;
import com.wellsoft.pt.bpm.engine.support.InteractionTaskData;
import com.wellsoft.pt.bpm.engine.support.NewFlow;
import com.wellsoft.pt.bpm.engine.support.NewFlowLabel;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.workflow.work.bean.WorkBean;

import java.util.Collection;
import java.util.List;

/**
 * Description: 视图列表处理
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-11-1.1	zhulh		2014-11-1		Create
 * </pre>
 * @date 2014-11-1
 */
public interface ListWorkService extends BaseService {

    /**
     * 挂职流程检测，如果不是返回false，是抛异常
     *
     * @param taskInstUuids
     * @return
     */
    boolean isGzWorkData(Collection<String> taskInstUuids);

    /**
     * 获取待办数据
     *
     * @param taskInstUuid
     * @return
     */
    WorkBean getTodoWorkData(String taskInstUuid);

    /**
     * 判断工作是否允许提交
     *
     * @param taskInstUuids
     * @return
     */
    boolean isAllowedSubmit(Collection<String> taskInstUuids);

    /**
     * 判断指定的流程环节实例UUID是否需要提交签署意见
     *
     * @param taskInstUuids
     * @return
     */
    boolean isRequiredSignOpinion(Collection<String> taskInstUuids);

    /**
     * 流程定义必填域检验
     *
     * @param taskInstUuid
     */
    void checkSubmitTask(String taskInstUuid);

    /**
     * 流程定义必填域检验
     *
     * @param taskInstUuid
     */
    void submit(String taskInstUuid, String opinionName, String opinionValue, String opinionText);

    /**
     * 判断工作是否允许移交
     *
     * @param taskInstUuids
     * @return
     */
    boolean isAllowedHandOver(Collection<String> taskInstUuids);

    /**
     * 判断指定的流程环节实例UUID是否需要特送个人签署意见
     *
     * @param taskInstUuids
     * @return
     */
    boolean isRequiredHandOverOpinion(Collection<String> taskInstUuids);

    /**
     * @param taskInstUuids
     * @param rawHandOverUsers
     * @param opinionName
     * @param opinionValue
     * @param opinionText
     */
    void handOver(Collection<String> taskInstUuids, List<String> rawHandOverUsers, String opinionName,
                  String opinionValue, String opinionText);

    /**
     * @param taskInstUuids
     * @param rawHandOverUsers
     * @param opinionName
     * @param opinionValue
     * @param opinionText
     */
    void handOver(Collection<String> taskInstUuids, List<String> rawHandOverUsers, String opinionName,
                  String opinionValue, String opinionText, List<LogicFileInfo> opinionFiles);

    /**
     * 判断工作是否允许特送
     *
     * @param taskInstUuids
     * @return
     */
    boolean isAllowedGotoTask(Collection<String> taskInstUuids);

    /**
     * 判断指定的流程环节实例UUID是否需要特送个人签署意见
     *
     * @param taskInstUuids
     * @return
     */
    boolean isRequiredGotoTaskOpinion(Collection<String> taskInstUuids);

    /**
     * 特送环节
     *
     * @param taskInstUuid
     * @param toTaskId
     */
    void gotoTask(String taskInstUuid, String gotoTaskId, List<String> taskUsers);

    /**
     * 特送多个环节
     *
     * @param taskInstUuids
     * @param gotoTaskId
     * @param taskUsers
     */
    void gotoTasks(Collection<String> taskInstUuids, String gotoTaskId, List<String> taskUsers);

    /**
     * 特送多个环节
     *
     * @param taskInstUuids
     * @param gotoTaskId
     * @param taskUsers
     */
    void checkGotoTasks(Collection<String> taskInstUuids);

    /**
     * 根据流程实例UUID、环节ID获取环节的子流程定义信息
     *
     * @param flowInstUuid
     * @param taskId
     * @return
     */
    List<NewFlow> getNewFlows(String flowInstUuid, String taskId);

    /**
     * 根据环节实例UUID、主协办角色标记获取子流程定义标签信息
     *
     * @param parentTaskInstUuid
     * @param roleFlag，0全部、1主办、2协办
     * @return
     */
    List<NewFlowLabel> getNewFlowLabelInfos(String parentTaskInstUuid, String roleFlag);

    /**
     * 发起分支流
     *
     * @param belongToTaskInstUuid
     * @param belongToFlowInstUuid
     * @param isMajor
     * @param taskUsers
     * @param businessType
     * @param businessRole
     * @param actionName
     * @return
     */
    List<String> startBranchTask(String belongToTaskInstUuid, String belongToFlowInstUuid, boolean isMajor,
                                 List<String> taskUsers, String businessType, String businessRole, String actionName);

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
    List<String> startSubFlow(String parentTaskInstUuid, String parentFlowInstUuid, String newFlowId, boolean isMajor,
                              List<String> taskUsers, String businessType, String businessRole, String actionName);

    /**
     * 重新分发状态为分发失败的子流程
     *
     * @param parentTaskInstUuid
     */
    void resendSubFlow(String parentTaskInstUuid);

    /**
     * 重做分支流
     *
     * @param taskInstUuids
     * @param opinionText
     */
    void redoBranchTask(Collection<String> taskInstUuids, String opinionText);

    /**
     * 重做流程
     *
     * @param taskInstUuids
     * @param opinionText
     */
    void redoFlow(Collection<String> taskInstUuids, String opinionText);

    /**
     * 终止分支流
     *
     * @param taskInstUuids
     * @param opinionText
     * @param interactionTaskData
     */
    void stopBranchTask(Collection<String> taskInstUuids, String opinionText, InteractionTaskData interactionTaskData);

    /**
     * 终止流程
     *
     * @param taskInstUuids
     * @param opinionText
     * @param interactionTaskData
     */
    void stopFlow(Collection<String> taskInstUuids, String opinionText, InteractionTaskData interactionTaskData);

    /**
     * 信息分发
     *
     * @param taskInstUuids
     * @param content
     * @param fileIds
     */
    void distributeInfo(Collection<String> taskInstUuids, String content, List<String> fileIds);

    /**
     * 根据流程实例UUID，变更流程到期时间
     *
     * @param flowInstUuids
     * @param dueTime
     * @param opinionText
     */
    void changeFlowDueTime(Collection<String> flowInstUuids, String dueTime, String opinionText);

    /**
     * 根据流程环节数据，变更流程到期时间
     *
     * @param flowShareItems
     * @param dueTime
     * @param opinionText
     */
    void changeTaskDueTime(Collection<FlowShareItem> flowShareItems, String dueTime, String opinionText);

    /*add by huanglinchuan 2014.11.1 begin*/
    //批量催办
    void remind(Collection<String> taskInstUuids, String opinionName, String opinionValue, String opinionText);

    /*add by huanglinchuan 2014.11.1 end*/

    /**
     * 判断指定的流程环节实例UUID是否允许转办
     *
     * @param taskInstUuids
     * @return
     */
    boolean isAllowedTransfer(Collection<String> taskInstUuids);

    /**
     * 判断指定的流程环节实例UUID是否需要转办签署意见
     *
     * @param taskInstUuids
     * @return
     */
    boolean isRequiredTransferOpinion(Collection<String> taskInstUuids);

    /**
     * 判断指定的流程环节实例UUID是否允许会签
     *
     * @param taskInstUuids
     * @return
     */
    boolean isAllowedCounterSign(Collection<String> taskInstUuids);

    /**
     * 判断指定的流程环节实例UUID是否需要会签签署意见
     *
     * @param taskInstUuids
     * @return
     */
    boolean isRequiredCounterSignOpinion(Collection<String> taskInstUuids);

    /**
     * 判断指定的流程环节实例UUID是否允许退回
     *
     * @param taskInstUuids
     * @return
     */
    boolean isAllowedRollback(Collection<String> taskInstUuids);

    /**
     * 判断指定的流程环节实例UUID是否允许直接退回
     *
     * @param taskInstUuids
     * @return
     */
    boolean isAllowedDirectRollback(Collection<String> taskInstUuids);

    /**
     * @param taskInstUuids
     * @return
     */
    ResultMessage checkAndGetToRollbackTasks(Collection<String> taskInstUuids);

    /**
     * @param taskInstUuid
     * @param rollbackToTaskId
     * @param rollbackToTaskInstUuid
     * @param opinionName
     * @param opinionValue
     * @param opinionText
     */
    void rollback(String taskInstUuid, String rollbackToTaskId, String rollbackToTaskInstUuid, String opinionName,
                  String opinionValue, String opinionText);

    /**
     * 判断指定的流程环节实例UUID是否需要退回签署意见
     *
     * @param taskInstUuids
     * @return
     */
    boolean isRequiredRollbackOpinion(Collection<String> taskInstUuids);

    /**
     * 判断指定的流程环节实例UUID是否允许撤回
     *
     * @param taskInstUuids
     * @return
     */
    boolean isAllowedCancel(Collection<String> taskInstUuids);

    /**
     * 判断指定的流程环节实例UUID是否需要撤回签署意见
     *
     * @param taskInstUuids
     * @return
     */
    boolean isRequiredCancelOpinion(Collection<String> taskInstUuids);

    /**
     * 送审批
     *
     * @param taskInstUuid
     */
    void gotoApprove(String taskInstUuid);

    /**
     * 删除工作
     *
     * @param flowInstUuid
     */
    void deleteWork(Collection<String> flowInstUuids);

    /**
     * 根据数据来源、操作类型、业务类别获取表单应用配置
     *
     * @param dataSource
     * @param actionType
     * @param businessType
     */
    BusinessApplicationConfigDto getBusinessApplicationConfig(String dataSource, String actionType, String businessType);

}
