/*
 * @(#)2013-6-5 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import com.wellsoft.pt.repository.entity.LogicFileInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Description: 办理过程PV类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-6-5.1	zhulh		2013-6-5		Create
 * </pre>
 * @date 2013-6-5
 */
@ApiModel("办理过程")
public class WorkProcessBean implements Serializable {

    private static final long serialVersionUID = 4683538972711539696L;

    // 办理人
    @ApiModelProperty("办理人名称")
    private String assignee;
    // 办理人Id
    @ApiModelProperty("办理人Id")
    private String assigneeId;
    @ApiModelProperty("决策人名称")
    private String decisionMakerName;
    // 部门
    @ApiModelProperty("部门名称")
    private String deptName;
    // 职位
    @ApiModelProperty("职位名称")
    private String mainJobName;
    @ApiModelProperty("身份名称路径")
    private String identityNamePath;
    // 办理意见
    @ApiModelProperty("办理意见")
    private String opinion;
    @ApiModelProperty("意见立场值")
    private String opinionValue;
    @ApiModelProperty("意见立场名称")
    private String opinionLabel;
    @ApiModelProperty("意见附件")
    private List<LogicFileInfo> opinionFiles;
    // 送办对象
    @ApiModelProperty("送办对象")
    private String toUser;
    // 送阅对象
    @ApiModelProperty("送阅对象")
    private String copyUser;
    // 阅读情况
    @ApiModelProperty("阅读情况")
    private String readStatus;
    // 挂起状态(0正常、1挂起、2结束)
    @ApiModelProperty("挂起状态(0正常、1挂起、2结束)")
    private Integer suspensionState = 0;
    // 办理环节名称
    @ApiModelProperty("办理环节名称")
    private String taskName;
    // 办理环节ID
    @ApiModelProperty("办理环节ID")
    private String taskId;
    // 办理环节ID
    @ApiModelProperty("办理环节实例UUID")
    private String taskInstUuid;
    // 状态
    @ApiModelProperty("状态")
    private String status;
    // 动作类型
    @ApiModelProperty("动作类型")
    private String actionType;
    // 动作名称
    @ApiModelProperty("动作名称")
    private String actionName;
    @ApiModelProperty("动作代码")
    private Integer actionCode;
    // 提交时间
    @ApiModelProperty("提交时间")
    private Date submitTime;
    // 完成时间
    @ApiModelProperty("完成时间")
    private Date endTime;
    // 是否移动端应用的操作
    @ApiModelProperty("是否移动端应用的操作")
    private Boolean isMobileApp;
    @ApiModelProperty("是否撤回")
    private Boolean canceled;
    @ApiModelProperty("是否补审补办")
    private Boolean supplemented;
    // 跳转环节ID
    @ApiModelProperty("跳转环节ID")
    private String gotoTaskId;

    /**
     * @return the assignee
     */
    public String getAssignee() {
        return assignee;
    }

    /**
     * @param assignee 要设置的assignee
     */
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }


    /**
     * @return the assigneeId
     */
    public String getAssigneeId() {
        return assigneeId;
    }

    /**
     * @param assigneeId 要设置的assigneeId
     */
    public void setAssigneeId(String assigneeId) {
        this.assigneeId = assigneeId;
    }

    /**
     * @return the decisionMakerName
     */
    public String getDecisionMakerName() {
        return decisionMakerName;
    }

    /**
     * @param decisionMakerName 要设置的decisionMakerName
     */
    public void setDecisionMakerName(String decisionMakerName) {
        this.decisionMakerName = decisionMakerName;
    }

    /**
     * @return the deptName
     */
    public String getDeptName() {
        return deptName;
    }

    /**
     * @param deptName 要设置的deptName
     */
    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    /**
     * @return the mainJobName
     */
    public String getMainJobName() {
        return mainJobName;
    }

    /**
     * @param mainJobName 要设置的mainJobName
     */
    public void setMainJobName(String mainJobName) {
        this.mainJobName = mainJobName;
    }

    /**
     * @return the identityNamePath
     */
    public String getIdentityNamePath() {
        return identityNamePath;
    }

    /**
     * @param identityNamePath 要设置的identityNamePath
     */
    public void setIdentityNamePath(String identityNamePath) {
        this.identityNamePath = identityNamePath;
    }

    /**
     * @return the opinion
     */
    public String getOpinion() {
        return opinion;
    }

    /**
     * @param opinion 要设置的opinion
     */
    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }

    /**
     * @return the opinionValue
     */
    public String getOpinionValue() {
        return opinionValue;
    }

    /**
     * @param opinionValue 要设置的opinionValue
     */
    public void setOpinionValue(String opinionValue) {
        this.opinionValue = opinionValue;
    }

    /**
     * @return the opinionLabel
     */
    public String getOpinionLabel() {
        return opinionLabel;
    }

    /**
     * @param opinionLabel 要设置的opinionLabel
     */
    public void setOpinionLabel(String opinionLabel) {
        this.opinionLabel = opinionLabel;
    }

    /**
     * @return the opinionFiles
     */
    public List<LogicFileInfo> getOpinionFiles() {
        return opinionFiles;
    }

    /**
     * @param opinionFiles 要设置的opinionFiles
     */
    public void setOpinionFiles(List<LogicFileInfo> opinionFiles) {
        this.opinionFiles = opinionFiles;
    }

    /**
     * @return the toUser
     */
    public String getToUser() {
        return toUser;
    }

    /**
     * @param toUser 要设置的toUser
     */
    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    /**
     * @return the copyUser
     */
    public String getCopyUser() {
        return copyUser;
    }

    /**
     * @param copyUser 要设置的copyUser
     */
    public void setCopyUser(String copyUser) {
        this.copyUser = copyUser;
    }

    /**
     * @return the readStatus
     */
    public String getReadStatus() {
        return readStatus;
    }

    /**
     * @param readStatus 要设置的readStatus
     */
    public void setReadStatus(String readStatus) {
        this.readStatus = readStatus;
    }

    /**
     * @return the suspensionState
     */
    public Integer getSuspensionState() {
        return suspensionState;
    }

    /**
     * @param suspensionState 要设置的suspensionState
     */
    public void setSuspensionState(Integer suspensionState) {
        this.suspensionState = suspensionState;
    }

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @param taskName 要设置的taskName
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId 要设置的taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the taskInstUuid
     */
    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    /**
     * @param taskInstUuid 要设置的taskInstUuid
     */
    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status 要设置的status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the actionType
     */
    public String getActionType() {
        return actionType;
    }

    /**
     * @param actionType 要设置的actionType
     */
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    /**
     * @return the actionName
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * @param actionName 要设置的actionName
     */
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    /**
     * @return the actionCode
     */
    public Integer getActionCode() {
        return actionCode;
    }

    /**
     * @param actionCode 要设置的actionCode
     */
    public void setActionCode(Integer actionCode) {
        this.actionCode = actionCode;
    }

    /**
     * @return the submitTime
     */
    public Date getSubmitTime() {
        return submitTime;
    }

    /**
     * @param submitTime 要设置的submitTime
     */
    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    /**
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime 要设置的endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the isMobileApp
     */
    public Boolean getIsMobileApp() {
        return isMobileApp;
    }

    /**
     * @param isMobileApp 要设置的isMobileApp
     */
    public void setIsMobileApp(Boolean isMobileApp) {
        this.isMobileApp = isMobileApp;
    }

    /**
     * @return the canceled
     */
    public Boolean getCanceled() {
        return canceled;
    }

    /**
     * @param canceled 要设置的canceled
     */
    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    /**
     * @return
     */
    public String getGotoTaskId() {
        return gotoTaskId;
    }

    /**
     * @param gotoTaskId
     */
    public void setGotoTaskId(String gotoTaskId) {
        this.gotoTaskId = gotoTaskId;
    }

    /**
     * @return the supplemented
     */
    public Boolean getSupplemented() {
        return supplemented;
    }

    /**
     * @param supplemented 要设置的supplemented
     */
    public void setSupplemented(Boolean supplemented) {
        this.supplemented = supplemented;
    }
}
