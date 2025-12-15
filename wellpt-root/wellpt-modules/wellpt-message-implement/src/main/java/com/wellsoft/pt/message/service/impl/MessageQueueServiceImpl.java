/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.message.dao.MessageQueueDao;
import com.wellsoft.pt.message.entity.MessageQueue;
import com.wellsoft.pt.message.service.MessageQueueService;
import com.wellsoft.pt.message.support.JmsMessage;
import com.wellsoft.pt.message.support.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.sql.Blob;
import java.util.*;

/**
 * Description: 发件箱实体类
 *
 * @author tony
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2014-10-17.1	tony		2014-10-17		Create
 * </pre>
 * @date 2014-10-17
 */
@Service
public class MessageQueueServiceImpl extends AbstractJpaServiceImpl<MessageQueue, MessageQueueDao, String> implements
        MessageQueueService {

    // 获取待发消息
    private static final String GET_ACTIVE_MSG = "from MessageQueue messageQueue where messageQueue.sentTime<=:now";

    @Override
    @Transactional
    public void send(JmsMessage jmsMessage) {
        Collection<Message> messages = jmsMessage.getMessages();
        if (messages != null && messages.size() > 0) {
            MessageQueue messageQueue = null;
            for (Message message : messages) {
                messageQueue = new MessageQueue();
                Date sentTime = message.getSentTime();
                if (sentTime == null) {
                    sentTime = Calendar.getInstance().getTime();
                }
                messageQueue.setSentTime(sentTime);
                messageQueue.setTemplateId(message.getTemplateId());
                messageQueue.setName(message.getName());
                messageQueue.setCode(String.valueOf((new Date().getTime())));
                messageQueue.setSystem(message.getSystem());
                messageQueue.setTenant(message.getTenant());
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                ObjectOutputStream oo;
                try {
                    oo = new ObjectOutputStream(bo);
                    oo.writeObject(message);
                    byte[] bytes = bo.toByteArray();
                    Blob blob = new SerialBlob(bytes);
                    messageQueue.setMessage(blob);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
                this.dao.save(messageQueue);
            }
        }

    }

    @Override
    public List<MessageQueue> getActiveMsgToSend() {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("now", Calendar.getInstance().getTime());
        PagingInfo pagingInfo = new PagingInfo(1, 1000, false);
        return this.dao.listByHQLAndPage(GET_ACTIVE_MSG, queryMap, pagingInfo);
    }

    @Override
    @Transactional
    public void deleteAll(List<MessageQueue> messageQueues) {
        for (MessageQueue messageQueue : messageQueues) {
            this.dao.delete(messageQueue);
        }
    }

    @Override
    @Transactional
    public void delete(MessageQueue messageQueue) {
        this.dao.delete(messageQueue);

    }

    @Override
    public List<MessageQueue> query(QueryInfo queryInfo) {
        List<MessageQueue> queues = this.dao.listByEntity(new MessageQueue(), queryInfo.getPropertyFilters(),
                queryInfo.getOrderBy(), queryInfo.getPagingInfo());
        return queues;
    }

    @Override
    public long countAll() {
        return this.dao.countByEntity(new MessageQueue());
    }

    @Override
    @Transactional
    public void deleteByCorrelationUuid(String correlationUuid) {
        String hql = "delete from MessageQueue t where t.correlationUuid = :correlationUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("correlationUuid", correlationUuid);
        this.dao.deleteByHQL(hql, params);
    }

}
