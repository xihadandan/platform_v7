/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.server.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.message.entity.MessageQueue;
import com.wellsoft.pt.message.processor.MessageProcessor;
import com.wellsoft.pt.message.processor.impl.*;
import com.wellsoft.pt.message.service.MessageQueueHisService;
import com.wellsoft.pt.message.service.MessageQueueService;
import com.wellsoft.pt.message.service.MessageService;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Description: 如何描述该类
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
@Service
public class JmsMessageConsumer {

    private static final String KEY_MSG_RTX_ENABLE = "msg.rtx.enable";
    private static final String MSG_RTX_ENABLE = "true";
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MessageQueueService messageQueueService;
    @Autowired
    private MessageQueueHisService messageQueueHisService;
    @Autowired
    private MessageService messageService;

    public String receiveMessage() {
        List<MessageQueue> messageQueues = this.messageQueueService.getActiveMsgToSend();
        for (MessageQueue messageQueue : messageQueues) {
            try {
                if (StringUtils.isNotBlank(messageQueue.getCreator())) {
                    IgnoreLoginUtils.login(Config.DEFAULT_TENANT, messageQueue.getCreator());
                } else {
                    IgnoreLoginUtils.loginSuperadmin();
                }
                messageService.messageProcess(messageQueue);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                IgnoreLoginUtils.logout();
            }
        }
        return "success";
    }

    public synchronized void receive(List<Message> messages) {
        try {
            for (Message message : messages) {
                receive(message);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    public synchronized void receive(Message message) throws Exception {
        try {
            String type = message.getType();
            if (Message.TYPE_ON_LINE.equals(type)) {
                doProcess(OnlineMessageProcessor.class, message);
            } else if (Message.TYPE_EMAIL.equals(type)) {
                doProcess(
                        (Class<? extends MessageProcessor>) Class
                                .forName(
                                        "com.wellsoft.pt.message.processor.impl.MailMessageProcessor"),
                        message);
            } else if (Message.TYPE_SMS.equals(type)) {
                doProcess(SmsMessageProcessor.class, message);
            } else if (Message.TYPE_CANCEL.equals(type)) {
                doProcess(CancelMessageProcessor.class, message);
            } else if (Message.TYPE_WEB_SERVICE.equals(type)) {
                doProcess(WebServiceMessageProcessor.class, message);
            } else if (Message.TYPE_ONLINE_CANCEL.equals(type)) {
                doProcess(CancelOnlineMessageProcessor.class, message);
                // logger.error("Unknow message type [" + type + "]");
            } else if (Message.TYPE_INTEFACE.equals(type)) {
                doProcess(IntefaceMessageProcessor.class, message);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void doProcess(Class<? extends MessageProcessor> processor, Message message) {
        try {
            ApplicationContextHolder.getBean(processor).doProcessor(message);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 根据租户进行用户分组
     *
     * @param recipients
     * @return
     * @author linz:
     * @date 创建时间：2015-7-21 上午11:22:53
     * @version 1.0
     * @parameter
     */
    private Map<String, List<String>> getMapTenantId(List<String> recipients) {
        Map<String, List<String>> hasMap = new HashMap<String, List<String>>();
        Set<String> setTenantId = getSetTenantId(recipients);
        List<String> userList = null;
        for (String tenTempId : setTenantId) {
            userList = new ArrayList<String>();
            for (String userId : recipients) {
                String tenantId = getTenantId(userId);
                if (tenantId.equals(tenTempId))
                    userList.add(userId);
            }
            hasMap.put(tenTempId, userList);
        }
        return hasMap;
    }

    private Set<String> getSetTenantId(List<String> recipients) {
        Set<String> setTenantId = new HashSet<String>();
        for (String userId : recipients) {
            String tenantId = getTenantId(userId);
            setTenantId.add(tenantId);
        }
        return setTenantId;
    }

    private String getTenantId(String userId) {
        return userId.substring(0, 4).replace(userId.substring(0, 1), "T");
    }
}
