/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.message.dao.MessageInboxDao;
import com.wellsoft.pt.message.dto.MessageClassifyDto;
import com.wellsoft.pt.message.dto.MessageInboxDto;
import com.wellsoft.pt.message.entity.MessageInbox;
import com.wellsoft.pt.message.service.MessageInboxService;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Clob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-23.1	tony		2014-10-23		Create
 * </pre>
 * @date 2014-10-23
 */
@Service
public class MessageInboxServiceImpl extends AbstractJpaServiceImpl<MessageInbox, MessageInboxDao, String> implements
        MessageInboxService {
    @Autowired
    private OrgFacadeService orgApiFacade;

    /**
     * 保存MessageInbox
     */
    @Override
    @Transactional
    public void saveMessageInbox(MessageInbox messageInbox) {
        dao.save(messageInbox);
    }

    /**
     * 查询消息列表
     */
    @Override
    public List<MessageInbox> getOnlineMessage(String type, String userId, String isread) {
        List<MessageInbox> messageInboxs = new ArrayList<MessageInbox>();
        if ("receive".equals(type)) {
            String hql = "";
            Map<String, Object> values = new HashMap<String, Object>();
            if (isread.equals("")) {
                hql = "from MessageInbox m where m.recipient=:recipient order by m.receivedTime desc";
            } else {
                hql = "from MessageInbox m where m.isread=:isread and m.recipient=:recipient order by m.receivedTime desc";
                if (isread.equals("false")) {
                    values.put("isread", false);
                } else {
                    values.put("isread", true);
                }
            }
            values.put("recipient", userId);
            messageInboxs = this.listByHQL(hql, values);
        } else if ("send".equals(type)) {
            String hql = "from MessageInbox m where m.sender=:sender order by m.sentTime desc";
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("sender", userId);
            messageInboxs = this.listByHQL(hql, values);
        }
        return messageInboxs;
    }

    /**
     * 查询消息列表
     */
    @Override
    public Long getOnlineMessageCount(String type, String userId, String isread) {
        if ("receive".equals(type)) {
            String hql = "select count(uuid) ";
            Map<String, Object> values = new HashMap<String, Object>();
            if (isread.equals("")) {
                hql += "from MessageInbox m where m.recipient=:recipient and iscancel=0 ";
            } else {
                hql += "from MessageInbox m where m.isread=:isread and m.recipient=:recipient and iscancel=0 ";
                if (isread.equals("false")) {
                    values.put("isread", false);
                } else {
                    values.put("isread", true);
                }
            }
            values.put("recipient", userId);
            return this.dao.getNumberByHQL(hql, values);
        } else if ("send".equals(type)) {
            String hql = "select count(uuid) from MessageOutbox m where m.sender=:sender ";
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("sender", userId);
            return this.dao.getNumberByHQL(hql, values);
        }
        return (long) 0;
    }

    @Override
    public Long getOnlineMessageCount(String userId, boolean isread, String classifyUuid) {
        String hql = "select count(uuid) from MessageInbox m where m.isread=:isread and m.recipient=:recipient and m.iscancel=0";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("recipient", userId);
        values.put("isread", isread);
        if (StringUtils.isNotBlank(classifyUuid)) {
            if (classifyUuid.equals(MessageClassifyDto.USER_CLASSIFY)) {
                values.put("sender", Message.USER_SYSTEM);
                hql += " and m.sender <> :sender";
            } else if (!classifyUuid.equals(MessageClassifyDto.ALL_CLASSIFY)) {
                values.put("classifyUuid", classifyUuid);
                hql += " and m.classifyUuid = :classifyUuid";
            }
        }
        return this.dao.countByHQL(hql, values);
    }

    @Override
    public MessageInbox getMessageInbox(String uuid) {
        return dao.getOne(uuid);
    }

    @Override
    public String getUserNameById(String userId) {
        Map<String, String> userNames = orgApiFacade.getUserNamesByUserIds(Lists.newArrayList(userId));
        if (MapUtils.isNotEmpty(userNames)) {
            return userNames.get(userId);
        }
        return "";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.MessageContentService#openMessageContent(java.lang.String)
     */
    @Override
    @Transactional
    public MessageInbox openMessageInbox(String uuid) {
        MessageInbox mc = this.dao.getOne(uuid);
        if (Boolean.FALSE.equals(mc.getIsread())) {
            mc.setIsread(true);
            this.dao.save(mc);
        }
        return mc;
    }

    @Override
    @Transactional
    public void saveUnread(String uuid) {
        MessageInbox mc = this.dao.getOne(uuid);
        if (Boolean.TRUE.equals(mc.getIsread())) {
            mc.setIsread(false);
            this.dao.save(mc);
        }
    }

    @Override
    @Transactional
    public void saveRead(String uuid) {
        MessageInbox mc = this.dao.getOne(uuid);
        if (Boolean.FALSE.equals(mc.getIsread())) {
            mc.setIsread(true);
            this.dao.save(mc);
        }
    }

    /**
     * 转化为clob类型
     */
    @Override
    public Clob getClobParm(String clobParm) {
        if (StringUtils.isNotEmpty(clobParm)) {
            return Hibernate.getLobCreator(dao.getSession()).createClob(clobParm);
        } else {
            return null;
        }
    }

    /**
     * 删除收件箱信息
     */
    @Override
    @Transactional
    public void deleteMessage(String uuid) {
        MessageInbox inbox = this.dao.getOne(uuid);
        if (Boolean.FALSE.equals(inbox.getIscancel())) {
            inbox.setIscancel(Boolean.TRUE);
            this.dao.save(inbox);
        }
    }

    /**
     *
     */
    @Override
    @Transactional
    public void updateMarkFlag(String uuid, String flag) {
        MessageInbox inbox = this.dao.getOne(uuid);
        inbox.setMarkFlag(flag);
        this.dao.save(inbox);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.service.MessageInboxService#getInOutMessage(java.lang.String)
     */
    @Override
    public HashMap<String, String> getInOutMessage(String inbox_uuid) {
        HashMap<String, String> message_map = new HashMap<String, String>();
        String sql = "select inbox.body as messagecontent,outbox.recipient_name as recipientname from msg_message_inbox inbox"
                + " left join msg_message_outbox outbox on inbox.message_outbox_uuid = outbox.uuid"
                + " where inbox.uuid='" + inbox_uuid + "'";
        List<QueryItem> list = new ArrayList<QueryItem>();
        try {
            Map queryMap = new HashMap<String, Object>();
            list = this.dao.listQueryItemBySQL(sql, queryMap, null);
            Clob recipientName = null;
            Clob body = null;
            for (Map<String, Object> detail : list) {
                recipientName = (Clob) detail.get("recipientname");
                body = (Clob) detail.get("messagecontent");
                message_map.put("recipientName", IOUtils.toString(recipientName.getCharacterStream()));
                message_map.put("messageContent", IOUtils.toString(body.getCharacterStream()));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return message_map;
    }

    @Override
    public List<MessageInbox> getOnlineMessageByExample(MessageInbox messageInbox) {
        return this.dao.listByEntity(messageInbox);
    }

    @Override
    public void removeMessageByOutUuid(String outboxUuid) {
        List<MessageInbox> inboxs = dao.listByFieldEqValue("messageOutboxUuid", outboxUuid);
        for (MessageInbox messageInbox : inboxs) {
            messageInbox.setIscancel(true);
            dao.save(messageInbox);
        }
    }

    @Override
    public List<QueryItem> query(String sqltotoal, Map queryMap, Class<QueryItem> class1) {
        return this.dao.listQueryItemBySQL(sqltotoal, queryMap, null);
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    @Override
    public List<MessageInboxDto> queryRecentTenLists() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        String hql = "from MessageInbox a where isread=0 and iscancel=0 and recipient=:userId order by createTime desc";
        Query query = this.dao.getSession().createQuery(hql);
        query.setFirstResult(0);
        query.setMaxResults(10);
        query.setParameter("userId", userId);
        List<MessageInbox> result = query.list();
        List<MessageInboxDto> re = new ArrayList<MessageInboxDto>();
        for (MessageInbox mi : result) {
            re.add(MessageInboxDto.revert(mi));
        }
        return re;
    }

    /**
     * 更新全部为已读
     */
    @Override
    @Transactional
    public boolean updateToReadState() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        String hql = "update MessageInbox set isread=1 where recipient=:userId";
        Query query = this.dao.getSession().createQuery(hql);
        query.setParameter("userId", userId);
        int result = query.executeUpdate();
        return result > 0 ? true : false;
    }

    /**
     * 更新全部为未读
     */
    @Override
    @Transactional
    public boolean updateToUnReadState() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        String hql = "update MessageInbox set isread=0 where recipient=:userId";
        Query query = this.dao.getSession().createQuery(hql);
        query.setParameter("userId", userId);
        int result = query.executeUpdate();
        return result > 0 ? true : false;
    }

    @Override
    @Transactional
    public boolean updateToReadStateByclass(String classifyUuid) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        Map<String, Object> values = Maps.newHashMap();
        values.put("userId", userId);
        String hql = "update MessageInbox set isread=1 where recipient=:userId";
        if (MessageClassifyDto.USER_CLASSIFY.equals(classifyUuid)) {
            hql += " and sender <> :sender";
            values.put("sender", Message.USER_SYSTEM);
        } else if (StringUtils.isNotBlank(classifyUuid)) {
            hql += " and classifyUuid = :classifyUuid";
            values.put("classifyUuid", classifyUuid);
        }
        int result = this.updateByHQL(hql, values);
        return result > 0 ? true : false;
    }

    @Override
    @Transactional
    public boolean updateToUnReadStateByclass(String classifyUuid) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        Map<String, Object> values = Maps.newHashMap();
        values.put("userId", userId);
        String hql = "update MessageInbox set isread=0 where recipient=:userId";
        if (MessageClassifyDto.USER_CLASSIFY.equals(classifyUuid)) {
            hql += " and sender <> :sender";
            values.put("sender", Message.USER_SYSTEM);
        } else if (StringUtils.isNotBlank(classifyUuid)) {
            hql += " and classifyUuid = :classifyUuid";
            values.put("classifyUuid", classifyUuid);
        }
        int result = this.updateByHQL(hql, values);
        return result > 0 ? true : false;
    }

    @Override
    public Long offLine(String userId) {
        String hql = "select count(uuid) from MessageInbox a where isread=0 and iscancel=0 and onLine=0 and recipient=:userId";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        Long count = this.dao.countByHQL(hql, values);
        return count;
    }

    @Override
    @Transactional
    public int updateOnLine(String userId) {
        String hql = "update MessageInbox set onLine=1 where recipient=:userId and onLine=0";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        int count = this.updateByHQL(hql, values);
        this.dao.flushSession();
        return count;
    }

    @Override
    @Transactional
    public void deleteByMessageOutboxUuids(List<String> messageOutboxUuids) {
        ListUtils.handleSubList(messageOutboxUuids, 1000, uuids -> {
            String hql = "delete MessageInbox t where t.messageOutboxUuid in(:messageOutboxUuids)";
            Map<String, Object> params = Maps.newHashMap();
            params.put("messageOutboxUuids", uuids);
            this.dao.deleteByHQL(hql, params);
        });
    }

    @Override
    public long countByMessageOutboxUuid(String messageOutboxUuid) {
        String hql = "select count(uuid) from MessageInbox t where t.messageOutboxUuid=:messageOutboxUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("messageOutboxUuid", messageOutboxUuid);
        return this.dao.countByHQL(hql, params);
    }

    @Override
    public List<MessageInbox> listByMessageOutboxUuid(String messageOutboxUuid) {
        String hql = "from MessageInbox t where t.messageOutboxUuid=:messageOutboxUuid";
        Map<String, Object> params = Maps.newHashMap();
        params.put("messageOutboxUuid", messageOutboxUuid);
        return this.dao.listByHQL(hql, params);
    }

}
