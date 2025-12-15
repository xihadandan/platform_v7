/*
 * @(#)9/23/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 流程仿真记录
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 9/23/24.1	    zhulh		9/23/24		    Create
 * </pre>
 * @date 9/23/24
 */
@ApiModel("流程仿真记录明细")
@Entity
@Table(name = "WF_FLOW_SIMULATION_RECORD_ITEM")
@DynamicUpdate
@DynamicInsert
public class WfFlowSimulationRecordItemEntity extends com.wellsoft.context.jdbc.entity.Entity {

    private static final long serialVersionUID = -7944136793130683492L;

    @ApiModelProperty("仿真记录UUID")
    private Long recordUuid;

    @ApiModelProperty("流程实例UUID")
    private String flowInstUuid;

    @ApiModelProperty("环节实例UUID")
    private String taskInstUuid;

    @ApiModelProperty("环节名称")
    private String taskName;

    @ApiModelProperty("环节ID")
    private String taskId;

    @ApiModelProperty("前一环节UUID")
    private String preTaskInstUuid;

    @ApiModelProperty("详细信息")
    private String details;

    /**
     * @return the recordUuid
     */
    public Long getRecordUuid() {
        return recordUuid;
    }

    /**
     * @param recordUuid 要设置的recordUuid
     */
    public void setRecordUuid(Long recordUuid) {
        this.recordUuid = recordUuid;
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
     * @return the preTaskInstUuid
     */
    public String getPreTaskInstUuid() {
        return preTaskInstUuid;
    }

    /**
     * @param preTaskInstUuid 要设置的preTaskInstUuid
     */
    public void setPreTaskInstUuid(String preTaskInstUuid) {
        this.preTaskInstUuid = preTaskInstUuid;
    }

    /**
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * @param details 要设置的details
     */
    public void setDetails(String details) {
        this.details = details;
    }
}
