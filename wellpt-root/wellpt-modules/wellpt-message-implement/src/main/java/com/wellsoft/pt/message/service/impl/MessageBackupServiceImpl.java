/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service.impl;

import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.message.entity.MessageInboxBak;
import com.wellsoft.pt.message.entity.MessageOutbox;
import com.wellsoft.pt.message.entity.MessageOutboxBak;
import com.wellsoft.pt.message.service.MessageBackupService;
import com.wellsoft.pt.message.service.MessageInboxBakService;
import com.wellsoft.pt.message.service.MessageOutboxBakService;
import com.wellsoft.pt.message.service.MessageOutboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
public class MessageBackupServiceImpl implements MessageBackupService {
    @Autowired
    private MessageOutboxService messageOutboxService;
    @Autowired
    private MessageOutboxBakService messageOutboxBakService;
    @Autowired
    private MessageInboxBakService messageInboxBakService;

    @Override
    @Transactional
    public void BackupMessage(int backup_during) {
        List<MessageOutbox> messageOutbox_list = this.messageOutboxService.getBackupMessage(backup_during);
        for (MessageOutbox outbox : messageOutbox_list) {
            MessageOutboxBak outboxBak = new MessageOutboxBak();
            MessageOutbox outbox_detail = this.messageOutboxService.getByUuId(outbox.getUuid());
            BeanUtils.copyProperties(outbox_detail, outboxBak);
            outboxBak.setUuid(null);
            this.messageOutboxBakService.save(outboxBak);
            List<MessageInboxBak> inboxbak_list = new ArrayList<MessageInboxBak>();
            // for(MessageInbox inbox:outbox_detail.getMessageInbox()){
            // MessageInboxBak inboxBak = new MessageInboxBak();
            // BeanUtils.copyProperties(inbox, inboxBak);
            // inboxBak.setMessageOutbox(outboxBak);
            // inboxbak_list.add(inboxBak);
            // }
            this.messageInboxBakService.saveAll(inboxbak_list);
        }
        this.messageOutboxService.deleteAllMessage(messageOutbox_list);
    }

}
