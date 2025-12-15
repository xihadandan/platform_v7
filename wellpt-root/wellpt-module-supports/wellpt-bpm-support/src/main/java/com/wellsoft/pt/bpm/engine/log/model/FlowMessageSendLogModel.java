/*
 * @(#)8/21/25 V1.0
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
 * 8/21/25.1	    zhulh		8/21/25		    Create
 * </pre>
 * @date 8/21/25
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel("流程消息推送日志模型")
public class FlowMessageSendLogModel extends BaseObject {
    private static final long serialVersionUID = 856443850811565389L;

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

    @ApiModelProperty("消息ID")
    private String msgId;

    @ApiModelProperty("消息类型名称")
    private String msgTypeName;

    @ApiModelProperty("消息类型")
    private String msgType;

    @ApiModelProperty("消息发送人")
    private String senderName;

    @ApiModelProperty("消息发送人ID")
    private String senderId;

    @ApiModelProperty("消息接收人")
    private String recipientName;

    @ApiModelProperty("消息接收人ID")
    private String recipientId;

    @ApiModelProperty("推送渠道")
    private String sendWay;

    @ApiModelProperty("推送时间")
    private Date sendTime;

    @ApiModelProperty("推送结果代码，0：成功，1：失败，2：发送中，3：取消，4：超时，5：失败重试，6：部分成功")
    private Integer sendResultCode;

    @ApiModelProperty("详情")
    private String details;

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
     * @return the msgId
     */
    public String getMsgId() {
        return msgId;
    }

    /**
     * @param msgId 要设置的msgId
     */
    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    /**
     * @return the msgTypeName
     */
    public String getMsgTypeName() {
        return msgTypeName;
    }

    /**
     * @param msgTypeName 要设置的msgTypeName
     */
    public void setMsgTypeName(String msgTypeName) {
        this.msgTypeName = msgTypeName;
    }

    /**
     * @return the msgType
     */
    public String getMsgType() {
        return msgType;
    }

    /**
     * @param msgType 要设置的msgType
     */
    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    /**
     * @return the senderName
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * @param senderName 要设置的senderName
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    /**
     * @return the senderId
     */
    public String getSenderId() {
        return senderId;
    }

    /**
     * @param senderId 要设置的senderId
     */
    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    /**
     * @return the recipientName
     */
    public String getRecipientName() {
        return recipientName;
    }

    /**
     * @param recipientName 要设置的recipientName
     */
    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    /**
     * @return the recipientId
     */
    public String getRecipientId() {
        return recipientId;
    }

    /**
     * @param recipientId 要设置的recipientId
     */
    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    /**
     * @return the sendWay
     */
    public String getSendWay() {
        return sendWay;
    }

    /**
     * @param sendWay 要设置的sendWay
     */
    public void setSendWay(String sendWay) {
        this.sendWay = sendWay;
    }

    /**
     * @return the sendTime
     */
    public Date getSendTime() {
        return sendTime;
    }

    /**
     * @param sendTime 要设置的sendTime
     */
    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    /**
     * @return the sendResultCode
     */
    public Integer getSendResultCode() {
        return sendResultCode;
    }

    /**
     * @param sendResultCode 要设置的sendResultCode
     */
    public void setSendResultCode(Integer sendResultCode) {
        this.sendResultCode = sendResultCode;
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
