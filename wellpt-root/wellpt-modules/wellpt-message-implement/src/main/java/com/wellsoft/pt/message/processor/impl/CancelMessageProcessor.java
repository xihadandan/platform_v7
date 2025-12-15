/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.processor.impl;

import com.wellsoft.pt.message.processor.AbstractMessageProcessor;
import com.wellsoft.pt.message.service.MessageOutboxService;
import com.wellsoft.pt.message.support.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 发件箱实体类
 *
 * @author tony
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2014-10-16.1	tony		2014-10-16		Create
 * </pre>
 * @date 2014-10-16
 */
@Component
public class CancelMessageProcessor extends AbstractMessageProcessor {
    @Autowired
    private MessageOutboxService messageOutboxService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.processor.MessageProcessor#doProcessor(com.wellsoft.pt.message.support.Message)
     */
    @Override
    public void doProcessor(Message msg) {
        String messageid = msg.getDataUuid();
        messageOutboxService.cancelMessage(messageid);
    }
}
