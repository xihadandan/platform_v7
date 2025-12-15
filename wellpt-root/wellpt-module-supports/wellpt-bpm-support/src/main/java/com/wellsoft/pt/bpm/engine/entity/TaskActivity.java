/*
 * @(#)2013-4-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 任务流程流转记录
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-6.1	zhulh		2013-4-6		Create
 * </pre>
 * @date 2013-4-6
 */
@Entity
@Table(name = "WF_TASK_ACTIVITY")
@DynamicUpdate
@DynamicInsert
public class TaskActivity extends IdEntity {

    private static final long serialVersionUID = 8122688394589674086L;

    /**
     * 所在任务实例ID
     */
    private String taskId;
    /**
     * 所在任务实例
     */
    private String taskInstUuid;
    /**
     * 前一任务实例ID
     */
    private String preTaskId;
    /**
     * 前一任务实例
     */
    private String preTaskInstUuid;
    /**
     * 前一网关(判断节点)ID
     */
    private String preGatewayIds;
    /**
     * 所在流程实例
     */
    private String flowInstUuid;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 任务流转类型
     */
    private Integer transferCode;

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
     * @return the preTaskId
     */
    public String getPreTaskId() {
        return preTaskId;
    }

    /**
     * @param preTaskId 要设置的preTaskId
     */
    public void setPreTaskId(String preTaskId) {
        this.preTaskId = preTaskId;
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
     * @return the preGatewayIds
     */
    public String getPreGatewayIds() {
        return preGatewayIds;
    }

    /**
     * @param preGatewayIds 要设置的preGatewayIds
     */
    public void setPreGatewayIds(String preGatewayIds) {
        this.preGatewayIds = preGatewayIds;
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
     * @return the startTime
     */
    public Date getStartTime() {
        return startTime;
    }

    /**
     * @param startTime 要设置的startTime
     */
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
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
     * @return the transferCode
     */
    public Integer getTransferCode() {
        return transferCode;
    }

    /**
     * @param transferCode 要设置的transferCode
     */
    public void setTransferCode(Integer transferCode) {
        this.transferCode = transferCode;
    }

}
