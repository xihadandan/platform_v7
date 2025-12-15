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
@ApiModel("流程阅读日志模型")
public class FlowReadLogModel extends BaseObject {

    private static final long serialVersionUID = -7190228927800444517L;

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

    @ApiModelProperty("阅读人名称")
    private String readerName;

    @ApiModelProperty("阅读人ID")
    private String readerId;

    @ApiModelProperty("阅读时间")
    private Date readTime;

    @ApiModelProperty("客户端IP地址")
    private String clientIp;

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
     * @return the readerName
     */
    public String getReaderName() {
        return readerName;
    }

    /**
     * @param readerName 要设置的readerName
     */
    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    /**
     * @return the readerId
     */
    public String getReaderId() {
        return readerId;
    }

    /**
     * @param readerId 要设置的readerId
     */
    public void setReaderId(String readerId) {
        this.readerId = readerId;
    }

    /**
     * @return the readTime
     */
    public Date getReadTime() {
        return readTime;
    }

    /**
     * @param readTime 要设置的readTime
     */
    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    /**
     * @return the clientIp
     */
    public String getClientIp() {
        return clientIp;
    }

    /**
     * @param clientIp 要设置的clientIp
     */
    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
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
