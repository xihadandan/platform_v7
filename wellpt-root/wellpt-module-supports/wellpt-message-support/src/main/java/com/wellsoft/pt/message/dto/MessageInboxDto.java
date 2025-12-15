/*
 * @(#)2020年1月16日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.dto;

import com.wellsoft.context.util.date.DateUtils;
import com.wellsoft.context.util.io.ClobUtils;
import com.wellsoft.pt.message.entity.MessageInbox;

import java.io.Serializable;
import java.sql.Clob;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author wangrf
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年1月16日.1	wangrf		2020年1月16日		Create
 * </pre>
 * @date 2020年1月16日
 */
public class MessageInboxDto implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3046760688702266342L;

    private String uuid;

    // 创建人
    private String creator;

    // 创建时间
    private Date createTime;

    // 修改人
    private String modifier;

    // 修改时间
    private Date modifyTime;

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
    private String receivedTime;
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

    private String messageParm;

    public static MessageInboxDto revert(MessageInbox messageInbox) {
        MessageInboxDto dto = new MessageInboxDto();
        String body = messageInbox.getBody();
        if (body != null) {
            String bodyStr = body;// ClobUtils.ClobToString(body);
            dto.setBody(bodyStr);
        }
        dto.setUuid(messageInbox.getUuid());
        dto.setCreator(messageInbox.getCreator());
        dto.setCreateTime(messageInbox.getCreateTime());
        dto.setModifier(messageInbox.getModifier());
        dto.setModifyTime(messageInbox.getModifyTime());
        dto.setIscancel(messageInbox.getIscancel());
        dto.setIsOnlinePopup(messageInbox.getIsOnlinePopup());
        dto.setIsread(messageInbox.getIsread());
        dto.setOnLine(messageInbox.getOnLine());
        dto.setMarkFlag(messageInbox.getMarkFlag());
        dto.setMessageOutboxUuid(messageInbox.getMessageOutboxUuid());
        Clob messageParm = messageInbox.getMessageParm();
        if (messageParm != null) {
            String messageParamStr = ClobUtils.ClobToString(messageParm);
            dto.setMessageParm(messageParamStr);
        }
        dto.setNote(messageInbox.getNote());
        dto.setReceivedTime(DateUtils.convertDate(messageInbox.getReceivedTime()));
        dto.setRecipient(messageInbox.getRecipient());
        dto.setRecipientName(messageInbox.getRecipientName());
        dto.setRelatedUrl(messageInbox.getRelatedUrl());
        dto.setSender(messageInbox.getSender());
        dto.setSenderName(messageInbox.getSenderName());
        dto.setSentTime(messageInbox.getSentTime());
        dto.setSubject(messageInbox.getSubject());
        dto.setViewpoint(messageInbox.getViewpoint());
        dto.setClassifyUuid(messageInbox.getClassifyUuid());
        dto.setClassifyName(messageInbox.getClassifyName());
        return dto;
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
    public String getReceivedTime() {
        return receivedTime;
    }

    /**
     * @param receivedTime 要设置的receivedTime
     */
    public void setReceivedTime(String receivedTime) {
        this.receivedTime = receivedTime;
    }

    /**
     * @return the isread
     */
    public Boolean getIsread() {
        return isread;
    }

    /**
     * @param isread 要设置的isread
     */
    public void setIsread(Boolean isread) {
        this.isread = isread;
    }

    /**
     * @return the isOnlinePopup
     */
    public String getIsOnlinePopup() {
        return isOnlinePopup;
    }

    /**
     * @param isOnlinePopup 要设置的isOnlinePopup
     */
    public void setIsOnlinePopup(String isOnlinePopup) {
        this.isOnlinePopup = isOnlinePopup;
    }

    /**
     * @return the markFlag
     */
    public String getMarkFlag() {
        return markFlag;
    }

    /**
     * @param markFlag 要设置的markFlag
     */
    public void setMarkFlag(String markFlag) {
        this.markFlag = markFlag;
    }

    /**
     * @return the iscancel
     */
    public Boolean getIscancel() {
        return iscancel;
    }

    /**
     * @param iscancel 要设置的iscancel
     */
    public void setIscancel(Boolean iscancel) {
        this.iscancel = iscancel;
    }

    /**
     * @return the messageOutboxUuid
     */
    public String getMessageOutboxUuid() {
        return messageOutboxUuid;
    }

    /**
     * @param messageOutboxUuid 要设置的messageOutboxUuid
     */
    public void setMessageOutboxUuid(String messageOutboxUuid) {
        this.messageOutboxUuid = messageOutboxUuid;
    }

    /**
     * @return the viewpoint
     */
    public String getViewpoint() {
        return viewpoint;
    }

    /**
     * @param viewpoint 要设置的viewpoint
     */
    public void setViewpoint(String viewpoint) {
        this.viewpoint = viewpoint;
    }

    /**
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * @param note 要设置的note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * @return the relatedUrl
     */
    public String getRelatedUrl() {
        return relatedUrl;
    }

    /**
     * @param relatedUrl 要设置的relatedUrl
     */
    public void setRelatedUrl(String relatedUrl) {
        this.relatedUrl = relatedUrl;
    }

    /**
     * @return the messageParm
     */
    public String getMessageParm() {
        return messageParm;
    }

    /**
     * @param messageParm 要设置的messageParm
     */
    public void setMessageParm(String messageParm) {
        this.messageParm = messageParm;
    }

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator 要设置的creator
     */
    public void setCreator(String creator) {
        this.creator = creator;
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
     * @return the modifier
     */
    public String getModifier() {
        return modifier;
    }

    /**
     * @param modifier 要设置的modifier
     */
    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    /**
     * @return the modifyTime
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * @param modifyTime 要设置的modifyTime
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
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


}
