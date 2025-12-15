/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service;

import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.message.dao.MessageQueueDao;
import com.wellsoft.pt.message.entity.MessageQueue;
import com.wellsoft.pt.message.support.JmsMessage;

import java.util.List;

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
public interface MessageQueueService extends JpaService<MessageQueue, MessageQueueDao, String> {
    public void send(JmsMessage jmsMessage);

    /**
     * 获取所有需要发送的消息
     *
     * @return
     */
    public List<MessageQueue> getActiveMsgToSend();

    public void deleteAll(List<MessageQueue> messageQueues);

    /**
     * 如何描述该方法
     *
     * @param messageQueue
     */
    void delete(MessageQueue messageQueue);

    List<MessageQueue> query(QueryInfo queryInfo);

    long countAll();

    void deleteByCorrelationUuid(String correlationUuid);
}
