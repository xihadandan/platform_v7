package com.wellsoft.pt.message.server.impl;

import com.wellsoft.pt.message.entity.MessageOutbox;
import com.wellsoft.pt.message.service.MessageOutboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

public class DefaultResponseQueueListener implements MessageListener {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private MessageOutboxService messageOutboxService;

    @Override
    public void onMessage(Message message) {
        try {
            MessageOutbox messageOutbox = messageOutboxService.getMessageOutboxByCid(message.getJMSCorrelationID());
            messageOutbox.setIssend(true);
        } catch (JMSException e) {
            logger.error(e.getMessage(), e);
        }
    }

}