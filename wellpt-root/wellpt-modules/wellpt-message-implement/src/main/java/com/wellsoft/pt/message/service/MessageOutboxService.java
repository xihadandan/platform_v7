/*
 * @(#)2013-5-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.message.dao.MessageOutboxDao;
import com.wellsoft.pt.message.entity.MessageOutbox;

import java.util.HashMap;
import java.util.List;

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
public interface MessageOutboxService extends JpaService<MessageOutbox, MessageOutboxDao, String> {

    public void saveMessageOutbox(MessageOutbox messageOutbox);

    public void saveMessageOutboxWithInbox(MessageOutbox messageOutbox);

    public void cancelMessage(String messageId);

    public void deleteMessage(String uuid);

    public MessageOutbox openMessageOutbox(String uuid);

    public void updateMarkFlag(String uuid, String flag);

    public MessageOutbox getMessageOutboxByCid(String correlationId);

    public List<MessageOutbox> getBackupMessage(int backup_during);

    public MessageOutbox getByUuId(String uuid);

    public void deleteAllMessage(List<MessageOutbox> outbox);

    public void retractMessage(String uuid);

    public HashMap<String, String> getInOutMessage(String outbox_uuid);

    void deleteByCorrelationId(String correlationId);

    MessageOutbox getByMessageId(String messageId);

    /**
     * 获取消息发送结果代码，0：成功，1：失败，2：发送中，3：取消，4：超时，5：失败重试，6：部分成功
     *
     * @param messageId
     * @param sendWays
     * @return
     */
    Integer getSendResultCodeByMessageId(String messageId, String... sendWays);
}
