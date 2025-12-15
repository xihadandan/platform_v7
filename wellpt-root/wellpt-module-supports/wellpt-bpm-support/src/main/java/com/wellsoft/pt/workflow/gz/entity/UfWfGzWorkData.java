/*
 * @(#)2015-08-12 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.gz.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
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
 * 2015-08-12.1	zhulh		2015-08-12		Create
 * </pre>
 * @date 2015-08-12
 */
@Entity
@Table(name = "UF_WF_GZ_WORK_DATA")
@DynamicUpdate
@DynamicInsert
public class UfWfGzWorkData extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1439371608764L;

    // formUuid
    private String formUuid;
    // status
    private String status;
    // signature
    private String signature;
    // sourceFlowName
    private String sourceFlowName;
    // sourceFlowDefId
    private String sourceFlowDefId;
    // sourceTile
    private String sourceTile;
    // sourceTenantId
    private String sourceTenantId;
    // targetTenantId
    private String targetTenantId;
    // sourceFlowInstUuid
    private String sourceFlowInstUuid;
    // sourceTaskInstUuid
    private String sourceTaskInstUuid;
    // currentTaskName
    private String currentTaskName;
    // currentTaskId
    private String currentTaskId;
    // previousOperatorName
    private String previousOperatorName;
    // previousOperatorId
    private String previousOperatorId;
    // arriveTime
    private Date arriveTime;
    // dueTime
    private Date dueTime;
    // doneTaskName
    private String doneTaskName;
    // doneTaskId
    private String doneTaskId;
    // doneTime
    private Date doneTime;
    // sourceSerialNo
    private String sourceSerialNo;
    // todoUserName
    private String todoUserName;
    // todoUserId
    private String todoUserId;

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return this.formUuid;
    }

    /**
     * @param formUuid
     */
    public String setFormUuid(String formUuid) {
        return this.formUuid = formUuid;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * @param status
     */
    public String setStatus(String status) {
        return this.status = status;
    }

    /**
     * @return the signature
     */
    @Column(name = "SIGNATURE_")
    public String getSignature() {
        return this.signature;
    }

    /**
     * @param signature
     */
    public String setSignature(String signature) {
        return this.signature = signature;
    }

    /**
     * @return the sourceFlowName
     */
    public String getSourceFlowName() {
        return this.sourceFlowName;
    }

    /**
     * @param sourceFlowName
     */
    public String setSourceFlowName(String sourceFlowName) {
        return this.sourceFlowName = sourceFlowName;
    }

    /**
     * @return the sourceFlowDefId
     */
    public String getSourceFlowDefId() {
        return this.sourceFlowDefId;
    }

    /**
     * @param sourceFlowDefId
     */
    public String setSourceFlowDefId(String sourceFlowDefId) {
        return this.sourceFlowDefId = sourceFlowDefId;
    }

    /**
     * @return the sourceTile
     */
    public String getSourceTile() {
        return this.sourceTile;
    }

    /**
     * @param sourceTile
     */
    public String setSourceTile(String sourceTile) {
        return this.sourceTile = sourceTile;
    }

    /**
     * @return the sourceTenantId
     */
    public String getSourceTenantId() {
        return this.sourceTenantId;
    }

    /**
     * @param sourceTenantId
     */
    public String setSourceTenantId(String sourceTenantId) {
        return this.sourceTenantId = sourceTenantId;
    }

    /**
     * @return the targetTenantId
     */
    public String getTargetTenantId() {
        return this.targetTenantId;
    }

    /**
     * @param targetTenantId
     */
    public String setTargetTenantId(String targetTenantId) {
        return this.targetTenantId = targetTenantId;
    }

    /**
     * @return the sourceFlowInstUuid
     */
    public String getSourceFlowInstUuid() {
        return this.sourceFlowInstUuid;
    }

    /**
     * @param sourceFlowInstUuid
     */
    public String setSourceFlowInstUuid(String sourceFlowInstUuid) {
        return this.sourceFlowInstUuid = sourceFlowInstUuid;
    }

    /**
     * @return the sourceTaskInstUuid
     */
    public String getSourceTaskInstUuid() {
        return this.sourceTaskInstUuid;
    }

    /**
     * @param sourceTaskInstUuid
     */
    public String setSourceTaskInstUuid(String sourceTaskInstUuid) {
        return this.sourceTaskInstUuid = sourceTaskInstUuid;
    }

    /**
     * @return the currentTaskName
     */
    public String getCurrentTaskName() {
        return this.currentTaskName;
    }

    /**
     * @param currentTaskName
     */
    public String setCurrentTaskName(String currentTaskName) {
        return this.currentTaskName = currentTaskName;
    }

    /**
     * @return the currentTaskId
     */
    public String getCurrentTaskId() {
        return this.currentTaskId;
    }

    /**
     * @param currentTaskId
     */
    public String setCurrentTaskId(String currentTaskId) {
        return this.currentTaskId = currentTaskId;
    }

    /**
     * @return the previousOperatorName
     */
    public String getPreviousOperatorName() {
        return this.previousOperatorName;
    }

    /**
     * @param previousOperatorName
     */
    public String setPreviousOperatorName(String previousOperatorName) {
        return this.previousOperatorName = previousOperatorName;
    }

    /**
     * @return the previousOperatorId
     */
    public String getPreviousOperatorId() {
        return this.previousOperatorId;
    }

    /**
     * @param previousOperatorId
     */
    public String setPreviousOperatorId(String previousOperatorId) {
        return this.previousOperatorId = previousOperatorId;
    }

    /**
     * @return the arriveTime
     */
    public Date getArriveTime() {
        return this.arriveTime;
    }

    /**
     * @param arriveTime
     */
    public Date setArriveTime(Date arriveTime) {
        return this.arriveTime = arriveTime;
    }

    /**
     * @return the dueTime
     */
    public Date getDueTime() {
        return this.dueTime;
    }

    /**
     * @param dueTime
     */
    public Date setDueTime(Date dueTime) {
        return this.dueTime = dueTime;
    }

    /**
     * @return the doneTaskName
     */
    public String getDoneTaskName() {
        return this.doneTaskName;
    }

    /**
     * @param doneTaskName
     */
    public String setDoneTaskName(String doneTaskName) {
        return this.doneTaskName = doneTaskName;
    }

    /**
     * @return the doneTaskId
     */
    public String getDoneTaskId() {
        return this.doneTaskId;
    }

    /**
     * @param doneTaskId
     */
    public String setDoneTaskId(String doneTaskId) {
        return this.doneTaskId = doneTaskId;
    }

    /**
     * @return the doneTime
     */
    public Date getDoneTime() {
        return this.doneTime;
    }

    /**
     * @param doneTime
     */
    public Date setDoneTime(Date doneTime) {
        return this.doneTime = doneTime;
    }

    /**
     * @return the sourceSerialNo
     */
    public String getSourceSerialNo() {
        return this.sourceSerialNo;
    }

    /**
     * @param sourceSerialNo
     */
    public String setSourceSerialNo(String sourceSerialNo) {
        return this.sourceSerialNo = sourceSerialNo;
    }

    /**
     * @return the todoUserName
     */
    public String getTodoUserName() {
        return this.todoUserName;
    }

    /**
     * @param todoUserName
     */
    public String setTodoUserName(String todoUserName) {
        return this.todoUserName = todoUserName;
    }

    /**
     * @return the todoUserId
     */
    public String getTodoUserId() {
        return this.todoUserId;
    }

    /**
     * @param todoUserId
     */
    public String setTodoUserId(String todoUserId) {
        return this.todoUserId = todoUserId;
    }

}
