/*
 * @(#)2013-5-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.message.dao.MessageContentDao;
import com.wellsoft.pt.message.entity.MessageContent;

import java.util.List;

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
public interface MessageContentService extends JpaService<MessageContent, MessageContentDao, String> {

    public void saveMessageContent(MessageContent messageContent);

    public List<MessageContent> getOnlineMessage(String type, String userId, String isread);

    public MessageContent getMessageContent(String uuid);

    public String getUserNameById(String userId);

    public MessageContent openMessageContent(String uuid);

    public void saveUnread(String uuid);

    public void saveRead(String uuid);

    public Long getOnlineMessageCount(String type, String userId, String isread);

}
