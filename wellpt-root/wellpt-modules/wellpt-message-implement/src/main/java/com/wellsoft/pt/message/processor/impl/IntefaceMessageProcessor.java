/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.processor.impl;

import com.wellsoft.pt.message.entity.MessageTemplate;
import com.wellsoft.pt.message.processor.AbstractMessageProcessor;
import com.wellsoft.pt.message.provider.MessageIntefaceSourceProvider;
import com.wellsoft.pt.message.service.MessageTemplateService;
import com.wellsoft.pt.message.support.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description: 发送消息触发接口实现处理器
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-29.1	zhulh		2012-10-29		Create
 * </pre>
 * @date 2012-10-29
 */
@Component
public class IntefaceMessageProcessor extends AbstractMessageProcessor {

    @Autowired
    private MessageTemplateService messageTemplateService;

    @Autowired(required = false)
    private Map<String, MessageIntefaceSourceProvider> messageIntefaceSourceProviderMap;

    @Override
    public void doProcessor(Message msg) {

        String templateId = msg.getTemplateId();
        MessageTemplate mt = messageTemplateService.getById(templateId);
        String sendWay = mt.getSendWay();
        if (sendWay.indexOf("INTEFACE") > -1) {
            String messageInteface = mt.getMessageInteface();
            messageIntefaceSourceProviderMap.get(messageInteface).doService(msg);
        }
    }

}
