/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.integration.security;

import com.wellsoft.pt.integration.support.WebServiceMessage;
import org.apache.activemq.ScheduledMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Session;

/**
 * Description: 消息接收处理类
 *
 * @author ruanhg
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-11-25.1	Administrator		2013-11-25		Create
 * </pre>
 * @date 2013-11-25
 */
public class WebServiceMessageSender {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    private JmsTemplate jmsTemplate;
    private Destination destination;

    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public void send(WebServiceMessage msg) {
        try {
            // 解析生成消息实体
            jmsTemplate.send(destination, new WebServiceMessageCreator(msg));
        } catch (Exception e) {
            logger.error("jmsTemplate send error : destination[" + destination + "]way[ " + msg.getWay() + "]fromId[ "
                    + msg.fromId + "]toId[ " + msg.getToId() + "]", e);
            throw new RuntimeException(e);
            // e.printStackTrace();
        }
    }

    /**
     * Description: 创建JmsMessageCreator
     *
     * @author ruanhg
     * @version 1.0
     *
     * <pre>
     * 修改记录:
     * 修改后版本	修改人		修改日期			修改内容
     * 2013-11-25.1	Administrator		2013-11-25		Create
     * </pre>
     * @date 2013-11-25
     */
    private static final class WebServiceMessageCreator implements MessageCreator {
        /**
         * JMS要发送的消息
         */
        private WebServiceMessage webServiceMessage;

        public WebServiceMessageCreator(WebServiceMessage webServiceMessage) {
            super();
            this.webServiceMessage = webServiceMessage;
        }

        /**
         * (non-Javadoc)
         *
         * @see org.springframework.jms.core.MessageCreator#createMessage(javax.jms.Session)
         */
        @Override
        public javax.jms.Message createMessage(Session session) throws JMSException {
            ObjectMessage message = session.createObjectMessage();
            message.setObject(webServiceMessage);
            if (webServiceMessage.getDelay() > 0) {
                message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, webServiceMessage.getDelay());
            }
            return message;
        }

    }
}
