/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.MessageSendResponse;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author tony
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-15.1	tony		2014-10-15		Create
 * </pre>
 * @date 2014-10-15
 */
public class MessageSendRequest extends WellptRequest<MessageSendResponse> {

    // 发送方系统id
    private String systemid;
    //消息格式模板id
    private String msgTemplateId;
    //消息发送人
    private String senderId;
    //消息接收人
    private List<String> recipients;
    //数据源json
    private String data;
    //相关源url
    private String relatedUrl;

    public String getSystemid() {
        return systemid;
    }

    public void setSystemid(String systemid) {
        this.systemid = systemid;
    }

    public String getMsgTemplateId() {
        return msgTemplateId;
    }

    public void setMsgTemplateId(String msgTemplateId) {
        this.msgTemplateId = msgTemplateId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }


    public List<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<String> recipients) {
        this.recipients = recipients;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getRelatedUrl() {
        return relatedUrl;
    }

    public void setRelatedUrl(String relatedUrl) {
        this.relatedUrl = relatedUrl;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        return ApiServiceName.MESSAGE_SEND;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    public Class<MessageSendResponse> getResponseClass() {
        return MessageSendResponse.class;
    }

}
