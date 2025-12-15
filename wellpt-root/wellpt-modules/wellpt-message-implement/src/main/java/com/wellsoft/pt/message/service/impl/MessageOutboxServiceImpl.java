/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.message.dao.MessageOutboxDao;
import com.wellsoft.pt.message.entity.MessageOutbox;
import com.wellsoft.pt.message.entity.ShortMessage;
import com.wellsoft.pt.message.service.*;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.message.support.MessageSendResult;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
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
public class MessageOutboxServiceImpl extends AbstractJpaServiceImpl<MessageOutbox, MessageOutboxDao, String> implements
        MessageOutboxService {

    @Autowired
    private OnlineMessageService onlineMessageService;

    @Autowired
    private MessageInboxService messageInboxService;

    @Autowired
    private ShortMessageService shortMessageService;

    @Autowired
    private MessageQueueService messageQueueService;

    public static String ClobToString(Clob clob) throws SQLException, IOException {
        String reString = "";
        Reader is = clob.getCharacterStream();// 得到流
        BufferedReader br = new BufferedReader(is);
        String s = br.readLine();
        StringBuilder sb = new StringBuilder();
        while (s != null) {// 执行循环将字符串全部取出付值给 StringBuffer由StringBuffer转成STRING
            sb.append(s);
            s = br.readLine();
        }
        reString = sb.toString();
        return reString;
    }

    /**
     * 保存MessageContent
     */
    @Override
    @Transactional
    public void saveMessageOutbox(MessageOutbox messageOutbox) {
        dao.save(messageOutbox);
    }

    /**
     * 取消消息 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.MessageOutboxService#cancelMessage(java.lang.String)
     */
    @Transactional
    public void cancelMessage(String messageId) {
        if (StringUtils.isBlank(messageId)) {
            return;
        }
        MessageOutbox outbox = getByMessage(messageId);
        if (Boolean.FALSE.equals(outbox.getIscancel())) {
            outbox.setIscancel(Boolean.TRUE);
            this.dao.save(outbox);

            messageInboxService.removeMessageByOutUuid(outbox.getUuid());
        }
    }

    /**
     * 根据messageid返回唯一记录
     * 如何描述该方法
     *
     * @param messageId
     * @return
     */
    public MessageOutbox getByMessage(String messageId) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("messageId", messageId);
        return this.dao.getOneByHQL("from MessageOutbox where messageId=:messageId", param);
    }

    @Override
    @Transactional
    public void saveMessageOutboxWithInbox(MessageOutbox messageOutbox) {
        this.dao.save(messageOutbox);

    }

    @Override
    public MessageOutbox openMessageOutbox(String uuid) {
        return this.dao.getOne(uuid);
    }

    /**
     * 删除发件箱信息，标记状态为取消
     */
    @Override
    @Transactional
    public void deleteMessage(String uuid) {
        MessageOutbox outbox = this.dao.getOne(uuid);
        if (Boolean.FALSE.equals(outbox.getIscancel())) {
            outbox.setIscancel(Boolean.TRUE);
            this.dao.save(outbox);
        }

    }

    /**
     *
     */
    @Override
    @Transactional
    public void updateMarkFlag(String uuid, String flag) {
        MessageOutbox outbox = this.dao.getOne(uuid);
        outbox.setMarkFlag(flag);
        this.dao.save(outbox);
    }

    @Override
    public MessageOutbox getMessageOutboxByCid(String correlationId) {
        return dao.listByFieldEqValue("correlationId", correlationId).get(0);
    }

    /**
     * 根据条件取得Messageoutbox的数据
     *
     * @param messageId
     * @return
     */
    @Override
    public List<MessageOutbox> getBackupMessage(int backup_during) {
        List<MessageOutbox> messageOutbox = new ArrayList<MessageOutbox>();
        String hql = "";
        Map<String, Object> values = new HashMap<String, Object>();
        Calendar today = Calendar.getInstance();
        today.add(Calendar.MONTH, backup_during);
        System.out.println(today.getTime());
        hql = "from MessageOutbox m where 1=1 and m.receivedTime<=:receivedTime order by m.receivedTime desc";
        values.put("receivedTime", new Timestamp(today.getTimeInMillis()));
        return this.listByHQL(hql, values);
    }

    @Override
    public MessageOutbox getByUuId(String uuid) {
        return this.dao.getOne(uuid);
    }

    @Transactional
    public void deleteAllMessage(List<MessageOutbox> outbox) {
        this.dao.deleteByEntities(outbox);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.MessageOutboxService#retractMessage(java.lang.String)
     */
    @Override
    @Transactional
    public void retractMessage(String uuid) {
        MessageOutbox messageOutbox = this.dao.getOne(uuid);
        if (messageOutbox.getIscancel() == false) {
            messageOutbox.setSubject("(已撤回)" + messageOutbox.getSubject());
            messageOutbox.setIscancel(true);
            Message msg = new Message();
            String clob = messageOutbox.getRecipient();
            String clobName = messageOutbox.getRecipientName();
            String receviceString = clob;
            String receviceNameString = clobName;
//            try {
//                receviceString = ClobToString(clob);
//                receviceNameString = ClobToString(clobName);
//            } catch (Exception e) {
//                logger.error(e.getMessage(), e);
//            }
            List<String> list = new ArrayList();
            List<String> listName = new ArrayList();
            if (StringUtils.isNotBlank(receviceString)) {
                String listString[] = receviceString.split(",");
                for (String userId : listString) {
                    list.add(userId);
                }
                msg.setRecipients(list);
                listString = receviceNameString.split(",");
                for (String userId : listString) {
                    listName.add(userId);
                }
                msg.setRecipientNames(listName);
            }
            msg.setType("cancelMessageOnline");
            msg.setDataUuid(messageOutbox.getUuid());
            onlineMessageService.send("systemFormat", msg, list, msg);
        }
        this.dao.save(messageOutbox);

    }

    public HashMap<String, String> getInOutMessage(String outbox_uuid) {
        HashMap<String, String> message_map = new HashMap<String, String>();
        String sql = "select outbox.body as messagecontent,outbox.recipient_name as recipientname"
                + " from msg_message_outbox  outbox  where outbox.uuid='" + outbox_uuid + "'";
        Map<String, Object> obj = new HashMap<String, Object>();
        List<QueryItem> list = new ArrayList<QueryItem>();
        try {
            list = this.dao.listQueryItemBySQL(sql, obj, null);
            if (list != null && list.size() > 0) {
                Clob recipientName = null;
                Clob body = null;
                for (QueryItem detail : list) {
                    recipientName = (Clob) detail.get("recipientname");
                    body = (Clob) detail.get("messagecontent");
                    if (recipientName != null) {
                        message_map.put("recipientName", IOUtils.toString(recipientName.getCharacterStream()));
                    } else {
                        message_map.put("recipientName", "");
                    }
                    if (body != null) {
                        message_map.put("messageContent", IOUtils.toString(body.getCharacterStream()));
                    } else {
                        message_map.put("messageContent", "");
                    }
                }

            } else {
                String sqlInbox = "select inbox.body as messagecontent,inbox.recipient_name as recipientname"
                        + " from msg_message_inbox  inbox  where inbox.message_outbox_uuid='" + outbox_uuid + "'";
                list = this.dao.listQueryItemBySQL(sqlInbox, obj, null);
                for (QueryItem detail : list) {
                    String recipientName = (String) detail.get("recipientname");
                    Clob body = (Clob) detail.get("messagecontent");
                    if (recipientName != null) {
                        message_map.put("recipientName", recipientName);
                    } else {
                        message_map.put("recipientName", "");
                    }
                    if (body != null) {
                        message_map.put("messageContent", IOUtils.toString(body.getCharacterStream()));
                    } else {
                        message_map.put("messageContent", "");
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return message_map;
    }

    @Override
    @Transactional
    public void deleteByCorrelationId(String correlationId) {
        messageQueueService.deleteByCorrelationUuid(correlationId);

        String hql = "select t.uuid as uuid from MessageOutbox t where t.correlationId = :correlationId";
        Map<String, Object> params = Maps.newHashMap();
        params.put("correlationId", correlationId);
        List<String> uuids = this.dao.listCharSequenceByHQL(hql, params);

        if (CollectionUtils.isNotEmpty(uuids)) {
            messageInboxService.deleteByMessageOutboxUuids(uuids);
            this.dao.deleteByUuids(uuids);
        }
    }

    @Override
    public MessageOutbox getByMessageId(String messageId) {
        List<MessageOutbox> list = dao.listByFieldEqValue("messageId", messageId);
        return CollectionUtils.isEmpty(list) ? null : list.get(0);
    }

    @Override
    public Integer getSendResultCodeByMessageId(String messageId, String... sendWays) {
        List<MessageOutbox> list = dao.listByFieldEqValue("messageId", messageId);
        if (CollectionUtils.isEmpty(list)) {
            return MessageSendResult.SENDING;
        }
        MessageOutbox messageOutbox = list.get(0);
        String type = messageOutbox.getType();
        if (StringUtils.isBlank(type)) {
            return MessageSendResult.UNKNOWN;
        }

        boolean hasSuccess = false;
        boolean hasFail = false;
        for (String sendWay : sendWays) {
            if (Message.TYPE_ON_LINE.equals(sendWay)) {
                long inboxCount = messageInboxService.countByMessageOutboxUuid(messageOutbox.getUuid());
                if (inboxCount <= 0) {
                    return MessageSendResult.SENDING;
                } else {
                    hasSuccess = true;
                }
            }

            if (Message.TYPE_SMS.equals(sendWay)) {
                List<ShortMessage> shortMessages = shortMessageService.listByMessageOutboxUuid(messageOutbox.getUuid());
                if (CollectionUtils.isEmpty(shortMessages)) {
                    return hasSuccess ? MessageSendResult.SUCCESS_PART : MessageSendResult.SENDING;
                }
                long successCount = shortMessages.stream().filter(shortMessage -> shortMessage.getSendStatus() != null
                                && !shortMessage.getSendStatus().equals(0) && !shortMessage.getSendStatus().equals(2))
                        .count();
                if (successCount <= 0) {
                    hasFail = true;
                }
            }
        }

        if (hasSuccess && !hasFail) {
            return MessageSendResult.SUCCESS;
        }

        if (hasSuccess || hasFail) {
            return MessageSendResult.SUCCESS_PART;
        }

        if (!hasSuccess && !hasFail) {
            return MessageSendResult.FAILED;
        }

        return MessageSendResult.UNKNOWN;
    }

}
