/*
 * @(#)2012-11-20 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 流程任务委托实例信息
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-20.1	zhulh		2012-11-20		Create
 * </pre>
 * @date 2012-11-20
 */
@Entity
@Table(name = "wf_task_delegation")
@DynamicUpdate
@DynamicInsert
public class TaskDelegation extends IdEntity {

    // 运行中
    public static final Integer STATUS_NORMAL = 0;
    // 等待回收中
    public static final Integer STATUS_WAITING_TAKE_BACK = 1;
    // 已结束
    public static final Integer STATUS_COMPLETED = 2;
    // 已撤销
    public static final Integer STATUS_CANCEL = 3;
    private static final long serialVersionUID = -5477380165238093169L;
    // 任务实例UUID
    private String taskInstUuid;
    // 流程实例UUID
    private String flowInstUuid;
    // 委托设置UUID
    private String delegationSettingsUuid;
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
    // 手动终止时自动收回受委托人在委托期间还未处理的待办工作
    private Boolean deactiveToTakeBackWork;
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
     * @return the delegationSettingsUuid
     */
    public String getDelegationSettingsUuid() {
        return delegationSettingsUuid;
    }

    /**
     * @param delegationSettingsUuid 要设置的delegationSettingsUuid
     */
    public void setDelegationSettingsUuid(String delegationSettingsUuid) {
        this.delegationSettingsUuid = delegationSettingsUuid;
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
     * @return the deactiveToTakeBackWork
     */
    public Boolean getDeactiveToTakeBackWork() {
        return deactiveToTakeBackWork;
    }

    /**
     * @param deactiveToTakeBackWork 要设置的deactiveToTakeBackWork
     */
    public void setDeactiveToTakeBackWork(Boolean deactiveToTakeBackWork) {
        this.deactiveToTakeBackWork = deactiveToTakeBackWork;
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
