/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service.impl;

import com.wellsoft.context.util.NetUtils;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.message.dao.MessageQueueHisDao;
import com.wellsoft.pt.message.entity.MessageQueue;
import com.wellsoft.pt.message.entity.MessageQueueHis;
import com.wellsoft.pt.message.service.MessageQueueHisService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
@Service
public class MessageQueueHisServiceImpl extends AbstractJpaServiceImpl<MessageQueueHis, MessageQueueHisDao, String>
        implements MessageQueueHisService {

    @Override
    @Transactional
    public void saveByQueue(MessageQueue messageQueue) {
        MessageQueueHis messageQueueHis = new MessageQueueHis();
        BeanUtils.copyProperties(messageQueue, messageQueueHis);
        messageQueueHis.setSentTime(new Date());
        messageQueueHis.setUuid(null);
        messageQueueHis.setExecIp(NetUtils.getLocalAddress());
        messageQueueHis.setTenant(messageQueue.getTenant());
        messageQueueHis.setSystem(messageQueue.getSystem());
        this.dao.save(messageQueueHis);
    }

    @Override
    @Transactional
    public void saveByQueues(List<MessageQueue> messageQueues) {
        // TODO Auto-generated method stub
        for (MessageQueue messageQueue : messageQueues) {
            this.saveByQueue(messageQueue);
        }
    }

    @Override
    public Long countAllHis() {
        return this.dao.countByEntity(new MessageQueueHis());
    }
}
