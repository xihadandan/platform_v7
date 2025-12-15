/*
 * @(#)2012-11-9 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service.impl;

import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.message.dao.MessageContentDao;
import com.wellsoft.pt.message.entity.MessageContent;
import com.wellsoft.pt.message.service.MessageContentService;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserAccount;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2013-5-25.1	Administrator		2013-5-25		Create
 * </pre>
 * @date 2013-5-25
 */
@Service
public class MessageContentServiceImpl extends AbstractJpaServiceImpl<MessageContent, MessageContentDao, String>
        implements MessageContentService {
    @Autowired
    private OrgApiFacade orgApiFacade;

    /**
     * 保存MessageContent
     */
    @Override
    @Transactional
    public void saveMessageContent(MessageContent messageContent) {
        dao.save(messageContent);
    }

    /**
     * 查询消息列表
     */
    @Override
    public List<MessageContent> getOnlineMessage(String type, String userId, String isread) {
        List<MessageContent> messageContents = new ArrayList<MessageContent>();
        if ("receive".equals(type)) {
            String hql = "";
            Map<String, Object> values = new HashMap<String, Object>();
            if (isread.equals("")) {
                hql = "from MessageContent m where m.recipient=:recipient order by m.receivedTime desc";
            } else {
                hql = "from MessageContent m where m.isread=:isread and m.recipient=:recipient order by m.receivedTime desc";
                if (isread.equals("false")) {
                    values.put("isread", false);
                } else {
                    values.put("isread", true);
                }
            }
            values.put("recipient", userId);
            messageContents = this.listByHQL(hql, values);
        } else if ("send".equals(type)) {
            String hql = "from MessageContent m where m.sender=:sender order by m.sentTime desc";
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("sender", userId);
            messageContents = this.listByHQL(hql, values);
        }
        return messageContents;
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
                hql += "from MessageContent m where m.recipient=:recipient  ";
            } else {
                hql += "from MessageContent m where m.isread=:isread and m.recipient=:recipient ";
                if (isread.equals("false")) {
                    values.put("isread", false);
                } else {
                    values.put("isread", true);
                }
            }
            values.put("recipient", userId);
            return (Long) this.dao.getNumberByHQL(hql, values);
        } else if ("send".equals(type)) {
            String hql = "select count(uuid) from MessageContent m where m.sender=:sender";
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("sender", userId);
            return (Long) this.dao.getNumberByHQL(hql, values);
        }
        return (long) 0;
    }

    @Override
    public MessageContent getMessageContent(String uuid) {
        return dao.getOne(uuid);
    }

    @Override
    public String getUserNameById(String userId) {
        MultiOrgUserAccount user = orgApiFacade.getAccountByUserId(userId);
        if (user != null) {
            return user.getUserName();
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
    public MessageContent openMessageContent(String uuid) {
        MessageContent mc = this.dao.getOne(uuid);
        if (Boolean.FALSE.equals(mc.getIsread())) {
            mc.setIsread(true);
            this.dao.save(mc);
        }
        return mc;
    }

    @Override
    @Transactional
    public void saveUnread(String uuid) {
        MessageContent mc = this.dao.getOne(uuid);
        if (Boolean.TRUE.equals(mc.getIsread())) {
            mc.setIsread(false);
            this.dao.save(mc);
        }
    }

    @Override
    @Transactional
    public void saveRead(String uuid) {
        MessageContent mc = this.dao.getOne(uuid);
        if (Boolean.FALSE.equals(mc.getIsread())) {
            mc.setIsread(true);
            this.dao.save(mc);
        }
    }

}
