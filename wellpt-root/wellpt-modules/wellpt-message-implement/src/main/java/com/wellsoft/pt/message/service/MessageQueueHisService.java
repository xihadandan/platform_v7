/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.message.dao.MessageQueueHisDao;
import com.wellsoft.pt.message.entity.MessageQueue;
import com.wellsoft.pt.message.entity.MessageQueueHis;

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
public interface MessageQueueHisService extends JpaService<MessageQueueHis, MessageQueueHisDao, String> {

    /**
     * 数据迁移到历史表
     *
     * @param messageQueue
     */
    public void saveByQueue(MessageQueue messageQueue);

    public void saveByQueues(List<MessageQueue> messageQueues);

    Long countAllHis();
}
