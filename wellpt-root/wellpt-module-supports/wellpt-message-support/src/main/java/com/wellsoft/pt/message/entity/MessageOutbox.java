package com.wellsoft.pt.message.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 发件箱实体类
 *
 * @author tony
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2014-10-16.1	tony		2014-10-16		Create
 * </pre>
 * @date 2014-10-16
 */
@Entity
@Table(name = "msg_message_outbox")
@DynamicUpdate
@DynamicInsert
public class MessageOutbox extends TenantEntity {
    /**
     * 消息模板ID
     */
    private String templateId;
    /**
     * 名称
     */
    private String name;
    /**
     * 消息类型(在线、手机短信、邮件、WebService)
     */
    private String type;
    /**
     * 发送人
     */
    private String sender;
    /**
     * 接收人
     */
    private String recipient;
    /**
     * 发送人名称
     */
    private String senderName;
    /**
     * 接收人名称
     */
    private String recipientName;
    /**
     * 主题
     */
    private String subject;
    /**
     * 内容
     */
    private String body;
    /**
     * 发送时间
     */
    private Date sentTime;
    /**
     * 接收时间
     */
    private Date receivedTime;

    /**
     * 消息标识
     */
    private String messageId;
    /**
     * 发送方系统id
     */
    private String systemid;
    /**
     * 相关源url
     */
    private String relatedUrl;
    /**
     * 相关源urlTitle
     */
    private String relatedTitle;
    /**
     * 是否取消
     */
    private Boolean iscancel;
    /**
     * 消息重要程度标记
     */
    private String markFlag;

    private String correlationId;

    private Boolean issend;

    private String system;

    private String tenant;


    public Boolean getIssend() {
        return issend;
    }

    public void setIssend(Boolean issend) {
        this.issend = issend;
    }

    /**
     * @return the templateId
     */
    public String getTemplateId() {
        return templateId;
    }

    /**
     * @param templateId 要设置的templateId
     */
    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
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
     * @return the sender
     */
    public String getSender() {
        return sender;
    }

    /**
     * @param sender 要设置的sender
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * @return the subject
     */
    public String getSubject() {
        return subject;
    }

    /**
     * @param subject 要设置的subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body 要设置的body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * @return the sentTime
     */
    public Date getSentTime() {
        return sentTime;
    }

    /**
     * @param sentTime 要设置的sentTime
     */
    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    /**
     * @return the receivedTime
     */
    public Date getReceivedTime() {
        return receivedTime;
    }

    /**
     * @param receivedTime 要设置的receivedTime
     */
    public void setReceivedTime(Date receivedTime) {
        this.receivedTime = receivedTime;
    }

    /**
     * @return the recipient
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * @param recipient 要设置的recipient
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId 要设置的messageId
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

    public String getSystemid() {
        return systemid;
    }

    public void setSystemid(String systemid) {
        this.systemid = systemid;
    }

    public String getRelatedUrl() {
        return relatedUrl;
    }

    public void setRelatedUrl(String relatedUrl) {
        this.relatedUrl = relatedUrl;
    }

    public Boolean getIscancel() {
        return iscancel;
    }

    public void setIscancel(Boolean iscancel) {
        this.iscancel = iscancel;
    }

    public String getMarkFlag() {
        return markFlag;
    }

    public void setMarkFlag(String markFlag) {
        this.markFlag = markFlag;
    }

    public String getRelatedTitle() {
        return relatedTitle;
    }

    public void setRelatedTitle(String relatedTitle) {
        this.relatedTitle = relatedTitle;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}
