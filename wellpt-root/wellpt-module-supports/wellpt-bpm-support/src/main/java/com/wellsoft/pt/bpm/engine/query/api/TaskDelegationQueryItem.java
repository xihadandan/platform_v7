/*
 * @(#)2016年7月13日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query.api;

import com.wellsoft.context.jdbc.support.BaseItem;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年7月13日.1	zhulh		2016年7月13日		Create
 * </pre>
 * @date 2016年7月13日
 */
public class TaskDelegationQueryItem extends BaseItem {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1868473656749562642L;

    // 任务实例UUID
    private String taskInstUuid;
    // 任务实例名称
    private String taskInstName;
    // 任务实例ID
    private String taskInstId;
    // 流程实例UUID
    private String flowInstUuid;
    // 流程实例标题
    private String flowInstTitle;
    // 委托配置引用
    private String dutyAgentUuid;
    // 委托人名称
    private String consignorName;
    // 委托人ID
    private String consignor;
    // 受托人名称
    private String trusteeName;
    // 受托人ID
    private String trustee;
    // 受托人待办信息，多个待办信息以分号隔开
    private String taskIdentityUuid;
    // 到期自动收回受委托人在委托期间还未处理的待办工作
    private Boolean dueToTakeBackWork;
    // 完成状态 0运行中、1等待回收中、2已结束
    private Integer completionState;
    // 开始时间
    private Date fromTime;
    // 结束时间
    private Date toTime;

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
     * @return the taskInstName
     */
    public String getTaskInstName() {
        return taskInstName;
    }

    /**
     * @param taskInstName 要设置的taskInstName
     */
    public void setTaskInstName(String taskInstName) {
        this.taskInstName = taskInstName;
    }

    /**
     * @return the taskInstId
     */
    public String getTaskInstId() {
        return taskInstId;
    }

    /**
     * @param taskInstId 要设置的taskInstId
     */
    public void setTaskInstId(String taskInstId) {
        this.taskInstId = taskInstId;
    }

    /**
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * @param flowInstUuid 要设置的flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * @return the flowInstTitle
     */
    public String getFlowInstTitle() {
        return flowInstTitle;
    }

    /**
     * @param flowInstTitle 要设置的flowInstTitle
     */
    public void setFlowInstTitle(String flowInstTitle) {
        this.flowInstTitle = flowInstTitle;
    }

    /**
     * @return the dutyAgentUuid
     */
    public String getDutyAgentUuid() {
        return dutyAgentUuid;
    }

    /**
     * @param dutyAgentUuid 要设置的dutyAgentUuid
     */
    public void setDutyAgentUuid(String dutyAgentUuid) {
        this.dutyAgentUuid = dutyAgentUuid;
    }

    /**
     * @return the consignorName
     */
    public String getConsignorName() {
        return consignorName;
    }

    /**
     * @param consignorName 要设置的consignorName
     */
    public void setConsignorName(String consignorName) {
        this.consignorName = consignorName;
    }

    /**
     * @return the consignor
     */
    public String getConsignor() {
        return consignor;
    }

    /**
     * @param consignor 要设置的consignor
     */
    public void setConsignor(String consignor) {
        this.consignor = consignor;
    }

    /**
     * @return the trusteeName
     */
    public String getTrusteeName() {
        return trusteeName;
    }

    /**
     * @param trusteeName 要设置的trusteeName
     */
    public void setTrusteeName(String trusteeName) {
        this.trusteeName = trusteeName;
    }

    /**
     * @return the trustee
     */
    public String getTrustee() {
        return trustee;
    }

    /**
     * @param trustee 要设置的trustee
     */
    public void setTrustee(String trustee) {
        this.trustee = trustee;
    }

    /**
     * @return the taskIdentityUuid
     */
    public String getTaskIdentityUuid() {
        return taskIdentityUuid;
    }

    /**
     * @param taskIdentityUuid 要设置的taskIdentityUuid
     */
    public void setTaskIdentityUuid(String taskIdentityUuid) {
        this.taskIdentityUuid = taskIdentityUuid;
    }

    /**
     * @return the dueToTakeBackWork
     */
    public Boolean getDueToTakeBackWork() {
        return dueToTakeBackWork;
    }

    /**
     * @param dueToTakeBackWork 要设置的dueToTakeBackWork
     */
    public void setDueToTakeBackWork(Boolean dueToTakeBackWork) {
        this.dueToTakeBackWork = dueToTakeBackWork;
    }

    /**
     * @return the completionState
     */
    public Integer getCompletionState() {
        return completionState;
    }

    /**
     * @param completionState 要设置的completionState
     */
    public void setCompletionState(Integer completionState) {
        this.completionState = completionState;
    }

    /**
     * @return the fromTime
     */
    public Date getFromTime() {
        return fromTime;
    }

    /**
     * @param fromTime 要设置的fromTime
     */
    public void setFromTime(Date fromTime) {
        this.fromTime = fromTime;
    }

    /**
     * @return the toTime
     */
    public Date getToTime() {
        return toTime;
    }

    /**
     * @param toTime 要设置的toTime
     */
    public void setToTime(Date toTime) {
        this.toTime = toTime;
    }

}
