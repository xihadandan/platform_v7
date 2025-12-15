package com.wellsoft.pt.message.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
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
@Table(name = "msg_message_inbox")
@DynamicUpdate
@DynamicInsert
public class MessageInbox extends TenantEntity {

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
     * 是否已阅
     */
    private Boolean isread;
    /**
     * 是否在线发送
     */
    private Boolean onLine;
    /**
     * 是否弹窗提醒
     **/
    private String isOnlinePopup;
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
    private String messageOutboxUuid;

    private String classifyUuid; //消息分类uuId

    private String classifyName; //消息分类名称

    private String viewpoint;

    private String note;

    private String relatedUrl;

    private String system;

    private String tenant;


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

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
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

    public String getMessageOutboxUuid() {
        return messageOutboxUuid;
    }

    public void setMessageOutboxUuid(String messageOutboxUuid) {
        this.messageOutboxUuid = messageOutboxUuid;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((recipient == null) ? 0 : recipient.hashCode());
        result = prime * result + ((recipientName == null) ? 0 : recipientName.hashCode());
        result = prime * result + ((sentTime == null) ? 0 : sentTime.hashCode());
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
        MessageInbox other = (MessageInbox) obj;
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
        if (sentTime == null) {
            if (other.sentTime != null)
                return false;
        } else if (!sentTime.equals(other.sentTime))
            return false;
        return true;
    }

    public String getIsOnlinePopup() {
        return isOnlinePopup;
    }

    public void setIsOnlinePopup(String isOnlinePopup) {
        this.isOnlinePopup = isOnlinePopup;
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

    public Boolean getOnLine() {
        return onLine;
    }

    public void setOnLine(Boolean onLine) {
        this.onLine = onLine;
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
