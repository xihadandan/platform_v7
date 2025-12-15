/*
 * @(#)9/23/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

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
@ApiModel("流程仿真记录")
@Entity
@Table(name = "WF_FLOW_SIMULATION_RECORD")
@DynamicUpdate
@DynamicInsert
public class WfFlowSimulationRecordEntity extends SysEntity {
    private static final long serialVersionUID = 1861381728947802667L;

    @ApiModelProperty("流程实例UUID")
    private String flowInstUuid;

    @ApiModelProperty("流程定义UUID")
    private String flowDefUuid;

    @ApiModelProperty("表单定义UUID")
    private String formUuid;

    @ApiModelProperty("表单数据UUID")
    private String dataUuid;

    @ApiModelProperty("操作人名称")
    private String operatorName;

    @ApiModelProperty("操作人ID")
    private String operatorId;

    @ApiModelProperty("操作时间")
    private Date operatorTime;

    @ApiModelProperty("发起人名称")
    private String startUserName;

    @ApiModelProperty("发起人ID")
    private String startUserId;

    @ApiModelProperty("操作内容JSON信息")
    private String contentJson;

    @ApiModelProperty("仿真状态，running仿真中、pause暂停、success成功")
    private String state;

//    // 是否生成流水号
//    @ApiModelProperty("是否生成流水号")
//    private Boolean generateSerialNumber;
//
//    // 是否发送消息
//    @ApiModelProperty("是否发送消息")
//    private Boolean sendMsg;
//
//    // 是否归档
//    @ApiModelProperty("是否归档")
//    private Boolean archive;

    @ApiModelProperty("删除状态,0正常，1逻辑删除")
    private Integer deleteStatus;

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
     * @return the flowDefUuid
     */
    public String getFlowDefUuid() {
        return flowDefUuid;
    }

    /**
     * @param flowDefUuid 要设置的flowDefUuid
     */
    public void setFlowDefUuid(String flowDefUuid) {
        this.flowDefUuid = flowDefUuid;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the operatorName
     */
    public String getOperatorName() {
        return operatorName;
    }

    /**
     * @param operatorName 要设置的operatorName
     */
    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    /**
     * @return the operatorId
     */
    public String getOperatorId() {
        return operatorId;
    }

    /**
     * @param operatorId 要设置的operatorId
     */
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * @return the operatorTime
     */
    public Date getOperatorTime() {
        return operatorTime;
    }

    /**
     * @param operatorTime 要设置的operatorTime
     */
    public void setOperatorTime(Date operatorTime) {
        this.operatorTime = operatorTime;
    }

    /**
     * @return the startUserName
     */
    public String getStartUserName() {
        return startUserName;
    }

    /**
     * @param startUserName 要设置的startUserName
     */
    public void setStartUserName(String startUserName) {
        this.startUserName = startUserName;
    }

    /**
     * @return the startUserId
     */
    public String getStartUserId() {
        return startUserId;
    }

    /**
     * @param startUserId 要设置的startUserId
     */
    public void setStartUserId(String startUserId) {
        this.startUserId = startUserId;
    }

    /**
     * @return the contentJson
     */
    public String getContentJson() {
        return contentJson;
    }

    /**
     * @param contentJson 要设置的contentJson
     */
    public void setContentJson(String contentJson) {
        this.contentJson = contentJson;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * @param state 要设置的state
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * @return the deleteStatus
     */
    public Integer getDeleteStatus() {
        return deleteStatus;
    }

    /**
     * @param deleteStatus 要设置的deleteStatus
     */
    public void setDeleteStatus(Integer deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

}
