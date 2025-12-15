/*
 * @(#)2014-8-11 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api.facade.impl;

import com.wellsoft.pt.api.WellptResponse;
import com.wellsoft.pt.api.facade.WellptService;
import com.wellsoft.pt.api.internal.parser.Json2mapUtil;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.request.MessageSendRequest;
import com.wellsoft.pt.api.response.MessageSendResponse;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.message.facade.service.impl.MessageClientApiFacadeImpl;
import com.wellsoft.pt.message.support.MessageExtraParm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.UUID;

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
@Service(ApiServiceName.MESSAGE_SEND)
@Transactional
public class MessageSendServiceImpl extends BaseServiceImpl implements WellptService<MessageSendRequest> {

    @Autowired(required = false)
    public MessageClientApiFacadeImpl messageClientApiFacade;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.api.facade.WellptService#doService(com.wellsoft.pt.api.WellptRequest)
     */
    @Override
    public WellptResponse doService(MessageSendRequest sendRequest) {
        String templateId = sendRequest.getMsgTemplateId();// 模板id
        String systemid = sendRequest.getSystemid();// 系统id
        String relatedUrl = sendRequest.getRelatedUrl();// 相关源url
        Collection<String> receiver = sendRequest.getRecipients();// 消息接收者
        String sender = sendRequest.getSenderId();// 发送者id
        String data = sendRequest.getData();// 数据源json
        String uuid = UUID.randomUUID().toString();
        MessageExtraParm parm = new MessageExtraParm();
        parm.setSystemid(systemid);
        parm.setRelatedUrl(relatedUrl);
        parm.setSender(sender);
        parm.setMessageid(uuid);
        messageClientApiFacade.send(templateId, null, Json2mapUtil.parseJSON2MapRo(data), receiver, parm);
        MessageSendResponse response = new MessageSendResponse();
        response.setMessageId(uuid);
        return response;
    }

}
