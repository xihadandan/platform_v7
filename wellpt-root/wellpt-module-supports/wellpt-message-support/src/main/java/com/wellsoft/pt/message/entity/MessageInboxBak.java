package com.wellsoft.pt.message.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Clob;
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
 * 2014-10-23.1	tony		2014-10-23		Create
 * </pre>
 * @date 2014-10-23
 */
@Entity
@Table(name = "msg_message_inbox_bak")
@DynamicUpdate
@DynamicInsert
public class MessageInboxBak extends IdEntity {

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
    @UnCloneable
    private Clob body;
    /**
     * 发送时间
     */
    private Date sentTime;
    /**
     * 接收时间
     */
    private Date receivedTime;
    /**
     * 是否已阅
     */
    private Boolean isread;
    /**
     * 消息重要程度标记
     */
    private String markFlag;
    /**
     * 是否取消
     */
    private Boolean iscancel;
    /**
     * 所属消息
     */
    @UnCloneable
    private MessageOutboxBak messageOutbox;

    private String classifyUuid; //消息分类uuId

    private String classifyName; //消息分类名称

    private String viewpoint;

    private String note;

    private String relatedUrl;

    @UnCloneable
    private Clob messageParm;

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Clob getBody() {
        return body;
    }

    public void setBody(Clob body) {
        this.body = body;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    public Date getReceivedTime() {
        return receivedTime;
    }

    public void setReceivedTime(Date receivedTime) {
        this.receivedTime = receivedTime;
    }

    public Boolean getIsread() {
        return isread;
    }

    public void setIsread(Boolean isread) {
        this.isread = isread;
    }

    @ManyToOne
    @JoinColumn(name = "message_outbox_uuid")
    public MessageOutboxBak getMessageOutbox() {
        return messageOutbox;
    }

    public void setMessageOutbox(MessageOutboxBak messageOutbox) {
        this.messageOutbox = messageOutbox;
    }

    public String getViewpoint() {
        return viewpoint;
    }

    public void setViewpoint(String viewpoint) {
        this.viewpoint = viewpoint;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Clob getMessageParm() {
        return messageParm;
    }

    public void setMessageParm(Clob messageParm) {
        this.messageParm = messageParm;
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

    public String getRelatedUrl() {
        return relatedUrl;
    }

    public void setRelatedUrl(String relatedUrl) {
        this.relatedUrl = relatedUrl;
    }

    public String getClassifyUuid() {
        return classifyUuid;
    }

    public void setClassifyUuid(String classifyUuid) {
        this.classifyUuid = classifyUuid;
    }

    public String getClassifyName() {
        return classifyName;
    }

    public void setClassifyName(String classifyName) {
        this.classifyName = classifyName;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((receivedTime == null) ? 0 : receivedTime.hashCode());
        result = prime * result + ((recipient == null) ? 0 : recipient.hashCode());
        result = prime * result + ((recipientName == null) ? 0 : recipientName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        MessageInboxBak other = (MessageInboxBak) obj;
        if (receivedTime == null) {
            if (other.receivedTime != null)
                return false;
        } else if (!receivedTime.equals(other.receivedTime))
            return false;
        if (recipient == null) {
            if (other.recipient != null)
                return false;
        } else if (!recipient.equals(other.recipient))
            return false;
        if (recipientName == null) {
            if (other.recipientName != null)
                return false;
        } else if (!recipientName.equals(other.recipientName))
            return false;
        return true;
    }

}
