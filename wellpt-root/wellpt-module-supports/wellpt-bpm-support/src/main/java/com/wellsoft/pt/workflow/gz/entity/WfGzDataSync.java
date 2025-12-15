/*
 * @(#)2015-07-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.gz.entity;

import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-07-16.1	zhulh		2015-07-16		Create
 * </pre>
 * @date 2015-07-16
 */
@Entity
@CommonEntity
@Table(name = "WF_GZ_DATA_SYNC")
@DynamicUpdate
@DynamicInsert
public class WfGzDataSync extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1437040507291L;

    // sourceFlowInstUuid
    private String sourceFlowInstUuid;
    // targetFlowInstUuid
    private String targetFlowInstUuid;
    // taskInstUuid
    private String taskInstUuid;
    // userId
    private String userId;
    // sourceTenantId
    private String sourceTenantId;
    // targetTenantId
    private String targetTenantId;
    // suspensionState
    private Integer suspensionState;

    /**
     * @return the sourceFlowInstUuid
     */
    public String getSourceFlowInstUuid() {
        return sourceFlowInstUuid;
    }

    /**
     * @param sourceFlowInstUuid 要设置的sourceFlowInstUuid
     */
    public void setSourceFlowInstUuid(String sourceFlowInstUuid) {
        this.sourceFlowInstUuid = sourceFlowInstUuid;
    }

    /**
     * @return the targetFlowInstUuid
     */
    public String getTargetFlowInstUuid() {
        return targetFlowInstUuid;
    }

    /**
     * @param targetFlowInstUuid 要设置的targetFlowInstUuid
     */
    public void setTargetFlowInstUuid(String targetFlowInstUuid) {
        this.targetFlowInstUuid = targetFlowInstUuid;
    }

    /**
     * @return the taskInstUuid
     */
    public String getTaskInstUuid() {
        return this.taskInstUuid;
    }

    /**
     * @param taskInstUuid
     */
    public String setTaskInstUuid(String taskInstUuid) {
        return this.taskInstUuid = taskInstUuid;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * @param userId
     */
    public String setUserId(String userId) {
        return this.userId = userId;
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
     * @return the suspensionState
     */
    public Integer getSuspensionState() {
        return this.suspensionState;
    }

    /**
     * @param suspensionState
     */
    public Integer setSuspensionState(Integer suspensionState) {
        return this.suspensionState = suspensionState;
    }

}
