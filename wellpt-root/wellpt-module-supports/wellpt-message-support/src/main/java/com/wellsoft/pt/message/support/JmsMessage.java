/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.support;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-9.1	zhulh		2012-11-9		Create
 * </pre>
 * @date 2012-11-9
 */
public class JmsMessage implements Serializable {
    private static final long serialVersionUID = -4904847581839790594L;

    private long delay;

    private String correlationId;

    private Collection<Message> messages;

    private String sendTimeType;

    private Date sendTime;

    private String templateId;

    private String name;


    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    /**
     * @return the delay
     */
    public long getDelay() {
        return delay;
    }

    /**
     * @param delay 要设置的delay
     */
    public void setDelay(long delay) {
        this.delay = delay;
    }

    /**
     * @return the messages
     */
    public Collection<Message> getMessages() {
        return messages;
    }

    /**
     * @param messages 要设置的messages
     */
    public void setMessages(Collection<Message> messages) {
        this.messages = messages;
    }

    public void handleMessage(Collection<Message> messages) {
        this.messages = messages;
    }

    public String getSendTimeType() {
        return sendTimeType;
    }

    public void setSendTimeType(String sendTimeType) {
        this.sendTimeType = sendTimeType;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
