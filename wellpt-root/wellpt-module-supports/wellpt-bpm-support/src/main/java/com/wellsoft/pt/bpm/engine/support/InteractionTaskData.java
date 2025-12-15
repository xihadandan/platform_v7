/*
 * @(#)2018年6月8日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.bpm.engine.constant.FlowConstant;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 交互式的环节数据
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年6月8日.1	zhulh		2018年6月8日		Create
 * </pre>
 * @date 2018年6月8日
 */
@ApiModel("交互式的环节数据")
public class InteractionTaskData extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5972468525582325714L;

    // 开始环节ID
    @ApiModelProperty("开始环节ID")
    protected String fromTaskId;
    // 下一流向ID
    @ApiModelProperty("下一流向ID")
    protected String toDirectionId;
    // 下一流向ID MAP
    @ApiModelProperty("下一流向ID哈希表")
    protected Map<String, String> toDirectionIds = new HashMap<String, String>(0);
    // 下一环节ID
    @ApiModelProperty("下一环节ID")
    protected String toTaskId;
    // 下一环节ID MAP
    @ApiModelProperty("下一环节ID哈希表")
    protected Map<String, String> toTaskIds = new HashMap<String, String>(0);
    // 办理人
    @ApiModelProperty("办理人")
    protected Map<String, List<String>> taskUsers = new HashMap<String, List<String>>(0);
    // 办理人对应的职位路径
    @ApiModelProperty("办理人对应的职位路径")
    protected Map<String, List<String>> taskUserJobPaths = new HashMap<String, List<String>>(0);
    // 抄送人
    @ApiModelProperty("抄送人")
    protected Map<String, List<String>> taskCopyUsers = new HashMap<String, List<String>>(0);
    // 转办人
    @ApiModelProperty("转办人")
    protected Map<String, List<String>> taskTransferUsers = new HashMap<String, List<String>>(0);
    // 会签人
    @ApiModelProperty("会签人")
    protected Map<String, List<String>> taskCounterSignUsers = new HashMap<String, List<String>>(0);
    // 加签人
    @ApiModelProperty("加签人")
    protected Map<String, List<String>> taskAddSignUsers = new HashMap<String, List<String>>(0);
    // 督办人
    @ApiModelProperty("督办人")
    protected Map<String, List<String>> taskMonitors = new HashMap<String, List<String>>(0);
    // 决策人
    @ApiModelProperty("决策人")
    protected Map<String, List<String>> taskDecisionMakers = new HashMap<String, List<String>>(0);

    // 所选职位
    @ApiModelProperty("所选职位")
    protected String jobSelected;

    // 选择的下一子流程ID
    @ApiModelProperty("选择的下一子流程ID")
    protected String toSubFlowId;
    // 子流程等待合并<subFlowInstUuid, isWait>
    @ApiModelProperty("子流程等待合并")
    protected Map<String, Boolean> waitForMerge = new HashMap<String, Boolean>(0);

    // 退回环节ID
    @ApiModelProperty("退回环节ID")
    protected String rollbackToTaskId;
    // 退回环节实例UUID
    @ApiModelProperty("退回环节实例UUID")
    protected String rollbackToTaskInstUuid;

    // 跳转的环节ID
    @ApiModelProperty("跳转的环节ID")
    protected String gotoTaskId;

    // 归档夹UUID
    @ApiModelProperty("归档夹UUID")
    protected String archiveFolderUuid;

    /**
     * @return the fromTaskId
     */
    public String getFromTaskId() {
        return StringUtils.isBlank(fromTaskId) ? FlowConstant.START_FLOW_ID : fromTaskId;
    }

    /**
     * @param fromTaskId 要设置的fromTaskId
     */
    public void setFromTaskId(String fromTaskId) {
        this.fromTaskId = fromTaskId;
    }

    /**
     * @return the toDirectionId
     */
    public String getToDirectionId() {
        return toDirectionId;
    }

    /**
     * @param toDirectionId 要设置的toDirectionId
     */
    public void setToDirectionId(String toDirectionId) {
        this.toDirectionId = toDirectionId;
    }

    /**
     * @return the toDirectionIds
     */
    public Map<String, String> getToDirectionIds() {
        return toDirectionIds;
    }

    /**
     * @param toDirectionIds 要设置的toDirectionIds
     */
    public void setToDirectionIds(Map<String, String> toDirectionIds) {
        this.toDirectionIds = toDirectionIds;
    }

    /**
     * @return the toTaskId
     */
    public String getToTaskId() {
        return toTaskId;
    }

    /**
     * @param toTaskId 要设置的toTaskId
     */
    public void setToTaskId(String toTaskId) {
        this.toTaskId = toTaskId;
    }

    /**
     * @return the toTaskIds
     */
    public Map<String, String> getToTaskIds() {
        return toTaskIds;
    }

    /**
     * @param toTaskIds 要设置的toTaskIds
     */
    public void setToTaskIds(Map<String, String> toTaskIds) {
        this.toTaskIds = toTaskIds;
    }

    /**
     * @return the taskUsers
     */
    public Map<String, List<String>> getTaskUsers() {
        return taskUsers;
    }

    /**
     * @param taskUsers 要设置的taskUsers
     */
    public void setTaskUsers(Map<String, List<String>> taskUsers) {
        this.taskUsers = taskUsers;
    }

    /**
     * @return
     */
    public Map<String, List<String>> getTaskUserJobPaths() {
        return taskUserJobPaths;
    }

    /**
     * @param taskUserJobPaths
     */
    public void setTaskUserJobPaths(Map<String, List<String>> taskUserJobPaths) {
        this.taskUserJobPaths = taskUserJobPaths;
    }

    /**
     * @return the taskCopyUsers
     */
    public Map<String, List<String>> getTaskCopyUsers() {
        return taskCopyUsers;
    }

    /**
     * @param taskCopyUsers 要设置的taskCopyUsers
     */
    public void setTaskCopyUsers(Map<String, List<String>> taskCopyUsers) {
        this.taskCopyUsers = taskCopyUsers;
    }

    public Map<String, List<String>> getTaskTransferUsers() {
        return taskTransferUsers;
    }

    public void setTaskTransferUsers(Map<String, List<String>> taskTransferUsers) {
        this.taskTransferUsers = taskTransferUsers;
    }

    /**
     * @return the taskCounterSignUsers
     */
    public Map<String, List<String>> getTaskCounterSignUsers() {
        return taskCounterSignUsers;
    }

    /**
     * @param taskCounterSignUsers 要设置的taskCounterSignUsers
     */
    public void setTaskCounterSignUsers(Map<String, List<String>> taskCounterSignUsers) {
        this.taskCounterSignUsers = taskCounterSignUsers;
    }

    /**
     * @return the taskAddSignUsers
     */
    public Map<String, List<String>> getTaskAddSignUsers() {
        return taskAddSignUsers;
    }

    /**
     * @param taskAddSignUsers 要设置的taskAddSignUsers
     */
    public void setTaskAddSignUsers(Map<String, List<String>> taskAddSignUsers) {
        this.taskAddSignUsers = taskAddSignUsers;
    }

    /**
     * @return the taskMonitors
     */
    public Map<String, List<String>> getTaskMonitors() {
        return taskMonitors;
    }

    /**
     * @param taskMonitors 要设置的taskMonitors
     */
    public void setTaskMonitors(Map<String, List<String>> taskMonitors) {
        this.taskMonitors = taskMonitors;
    }

    /**
     * @return the taskDecisionMakers
     */
    public Map<String, List<String>> getTaskDecisionMakers() {
        return taskDecisionMakers;
    }

    /**
     * @param taskDecisionMakers 要设置的taskDecisionMakers
     */
    public void setTaskDecisionMakers(Map<String, List<String>> taskDecisionMakers) {
        this.taskDecisionMakers = taskDecisionMakers;
    }

    /**
     * @return the toSubFlowId
     */
    public String getToSubFlowId() {
        return toSubFlowId;
    }

    /**
     * @param toSubFlowId 要设置的toSubFlowId
     */
    public void setToSubFlowId(String toSubFlowId) {
        this.toSubFlowId = toSubFlowId;
    }

    /**
     * @return the waitForMerge
     */
    public Map<String, Boolean> getWaitForMerge() {
        return waitForMerge;
    }

    /**
     * @param waitForMerge 要设置的waitForMerge
     */
    public void setWaitForMerge(Map<String, Boolean> waitForMerge) {
        this.waitForMerge = waitForMerge;
    }

    /**
     * @return the rollbackToTaskId
     */
    public String getRollbackToTaskId() {
        return rollbackToTaskId;
    }

    /**
     * @param rollbackToTaskId 要设置的rollbackToTaskId
     */
    public void setRollbackToTaskId(String rollbackToTaskId) {
        this.rollbackToTaskId = rollbackToTaskId;
    }

    /**
     * @return the rollbackToTaskInstUuid
     */
    public String getRollbackToTaskInstUuid() {
        return rollbackToTaskInstUuid;
    }

    /**
     * @param rollbackToTaskInstUuid 要设置的rollbackToTaskInstUuid
     */
    public void setRollbackToTaskInstUuid(String rollbackToTaskInstUuid) {
        this.rollbackToTaskInstUuid = rollbackToTaskInstUuid;
    }

    /**
     * @return the gotoTaskId
     */
    public String getGotoTaskId() {
        return gotoTaskId;
    }

    /**
     * @param gotoTaskId 要设置的gotoTaskId
     */
    public void setGotoTaskId(String gotoTaskId) {
        this.gotoTaskId = gotoTaskId;
    }

    /**
     * @return the archiveFolderUuid
     */
    public String getArchiveFolderUuid() {
        return archiveFolderUuid;
    }

    /**
     * @param archiveFolderUuid 要设置的archiveFolderUuid
     */
    public void setArchiveFolderUuid(String archiveFolderUuid) {
        this.archiveFolderUuid = archiveFolderUuid;
    }

    public String getJobSelected() {
        return jobSelected;
    }

    public void setJobSelected(String jobSelected) {
        this.jobSelected = jobSelected;
    }
}
