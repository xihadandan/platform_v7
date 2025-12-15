/*
 * @(#)3/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.support;

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
 * 3/25/25.1	    zhulh		3/25/25		    Create
 * </pre>
 * @date 3/25/25
 */
public abstract class AbstractMessage implements FeishuMessage {

    private String senderName;
    private Date sendTime;
    private String messageId;
    private String chatId;

    /**
     * @return the senderName
     */
    @Override
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
     * @return the sendTime
     */
    @Override
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
     * @return the messageId
     */
    @Override
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
     * @return the chatId
     */
    @Override
    public String getChatId() {
        return chatId;
    }

    /**
     * @param chatId 要设置的chatId
     */
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
