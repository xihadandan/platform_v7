/*
 * @(#)8/14/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.log.model;

import com.wellsoft.context.base.BaseObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Date;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/14/25.1	    zhulh		8/14/25		    Create
 * </pre>
 * @date 8/14/25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("流程自动处理日志模型")
public class FlowAutoHandleLogModel extends BaseObject {

    @ApiModelProperty("日志ID")
    private String _id;

    @ApiModelProperty("流程标题")
    private String title;

    @ApiModelProperty("流程名称")
    private String flowName;

    @ApiModelProperty("流程ID")
    private String flowDefId;

    @ApiModelProperty("流程实例UUID")
    private String flowInstUuid;

    @ApiModelProperty("环节实例UUID")
    private String taskInstUuid;

    @ApiModelProperty("期望自动处理节点")
    private String expectTaskName;

    @ApiModelProperty("期望自动处理节点ID")
    private String expectTaskId;

    @ApiModelProperty("自动处理校验结果代码,0-成功,1-失败")
    private Integer handleResultCode;

    @ApiModelProperty("类型")
    private String type;

    @ApiModelProperty("详情")
    private String details;

    @ApiModelProperty("操作节点")
    private String operateTaskName;

    @ApiModelProperty("操作节点Id")
    private String operateTaskId;

    @ApiModelProperty("操作者")
    private String operatorName;

    @ApiModelProperty("操作者ID")
    private String operatorId;

    @ApiModelProperty("操作时间")
    private Date operateTime;

    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("归属系统")
    private String system;
    @ApiModelProperty("归属租户")
    private String tenant;

    /**
     * @return the _id
     */
    public String get_id() {
        return _id;
    }

    /**
     * @param _id 要设置的_id
     */
    public void set_id(String _id) {
        this._id = _id;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the flowName
     */
    public String getFlowName() {
        return flowName;
    }

    /**
     * @param flowName 要设置的flowName
     */
    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    /**
     * @return the flowDefId
     */
    public String getFlowDefId() {
        return flowDefId;
    }

    /**
     * @param flowDefId 要设置的flowDefId
     */
    public void setFlowDefId(String flowDefId) {
        this.flowDefId = flowDefId;
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
     * @return the expectTaskName
     */
    public String getExpectTaskName() {
        return expectTaskName;
    }

    /**
     * @param expectTaskName 要设置的expectTaskName
     */
    public void setExpectTaskName(String expectTaskName) {
        this.expectTaskName = expectTaskName;
    }

    /**
     * @return the expectTaskId
     */
    public String getExpectTaskId() {
        return expectTaskId;
    }

    /**
     * @param expectTaskId 要设置的expectTaskId
     */
    public void setExpectTaskId(String expectTaskId) {
        this.expectTaskId = expectTaskId;
    }

    /**
     * @return the handleResultCode
     */
    public Integer getHandleResultCode() {
        return handleResultCode;
    }

    /**
     * @param handleResultCode 要设置的handleResultCode
     */
    public void setHandleResultCode(Integer handleResultCode) {
        this.handleResultCode = handleResultCode;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
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

    /**
     * @return the operateTaskName
     */
    public String getOperateTaskName() {
        return operateTaskName;
    }

    /**
     * @param operateTaskName 要设置的operateTaskName
     */
    public void setOperateTaskName(String operateTaskName) {
        this.operateTaskName = operateTaskName;
    }

    /**
     * @return the operateTaskId
     */
    public String getOperateTaskId() {
        return operateTaskId;
    }

    /**
     * @param operateTaskId 要设置的operateTaskId
     */
    public void setOperateTaskId(String operateTaskId) {
        this.operateTaskId = operateTaskId;
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
     * @return the operateTime
     */
    public Date getOperateTime() {
        return operateTime;
    }

    /**
     * @param operateTime 要设置的operateTime
     */
    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime 要设置的createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

}
