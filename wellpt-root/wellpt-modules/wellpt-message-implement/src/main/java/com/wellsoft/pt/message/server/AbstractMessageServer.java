/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.server;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.message.processor.MessageProcessor;
import com.wellsoft.pt.message.processor.impl.CancelMessageProcessor;
import com.wellsoft.pt.message.processor.impl.OnlineMessageProcessor;
import com.wellsoft.pt.message.processor.impl.SmsMessageProcessor;
import com.wellsoft.pt.message.processor.impl.WebServiceMessageProcessor;
import com.wellsoft.pt.message.support.JmsMessage;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

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
 * 2013-07-5.1	zhulh		2013-07-5		同步接收消息
 * </pre>
 * @date 2012-11-9
 */
public abstract class AbstractMessageServer implements MessageServer {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.server.MessageServer#receive(com.wellsoft.pt.message.support.JmsMessage)
     */
    @Override
    public synchronized void receive(JmsMessage jmsMessage) {
        Collection<Message> messages = jmsMessage.getMessages();
        for (Message message : messages) {
            try {
                IgnoreLoginUtils.login(message.getTenantId(), message.getCreator());

                String type = message.getType();
                if (Message.TYPE_ON_LINE.equals(type)) {
                    doProcess(OnlineMessageProcessor.class, message);
                } else if (Message.TYPE_EMAIL.equals(type)) {
                    doProcess((Class<? extends MessageProcessor>) Class.forName("com.wellsoft.pt.message.processor.impl.MailMessageProcessor"), message);
                    RuntimeException e = new RuntimeException("邮件服务需要重写， 依赖循环时注释掉");
                    logger.error(e.getMessage(), e);
                } else if (Message.TYPE_SMS.equals(type)) {
                    doProcess(SmsMessageProcessor.class, message);
                } else if (Message.TYPE_CANCEL.equals(type)) {
                    doProcess(CancelMessageProcessor.class, message);
                } else if (Message.TYPE_WEB_SERVICE.equals(type)) {
                    doProcess(WebServiceMessageProcessor.class, message);
                } else {
                    logger.error("Unknow message type [" + type + "]");
                }
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            } finally {
                IgnoreLoginUtils.logout();
            }
        }
    }

    /**
     * @param processor
     * @param message
     */
    private void doProcess(Class<? extends MessageProcessor> processor, Message message) {
        try {
            ApplicationContextHolder.getBean(processor).doProcessor(message);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }
    }

}
