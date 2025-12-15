/*
 * @(#)2013-5-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.service;

import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.message.dao.MessageInboxDao;
import com.wellsoft.pt.message.dto.MessageInboxDto;
import com.wellsoft.pt.message.entity.MessageInbox;

import java.sql.Clob;
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
 * 2014-10-23.1	tony		2014-10-23	Create
 * </pre>
 * @date 2014-10-23
 */
public interface MessageInboxService extends JpaService<MessageInbox, MessageInboxDao, String> {

    public void saveMessageInbox(MessageInbox messageInbox);

    public List<MessageInbox> getOnlineMessage(String type, String userId, String isread);

    public MessageInbox getMessageInbox(String uuid);

    public String getUserNameById(String userId);

    public MessageInbox openMessageInbox(String uuid);

    public void deleteMessage(String uuid);

    public void saveUnread(String uuid);

    public void saveRead(String uuid);

    public Long getOnlineMessageCount(String type, String userId, String isread);

    public Long getOnlineMessageCount(String userId, boolean isread, String classifyUuid);

    public Clob getClobParm(String clobParm);

    public void updateMarkFlag(String uuid, String flag);

    public HashMap<String, String> getInOutMessage(String inbox_uuid);

    public List<MessageInbox> getOnlineMessageByExample(MessageInbox messageInbox);

    /**
     * 根据发件箱UUID取消消息
     *
     * @param outboxUuid
     */
    public void removeMessageByOutUuid(String outboxUuid);

    public List<QueryItem> query(String sqltotoal, Map queryMap, Class<QueryItem> class1);

    /**
     * 获取最新的十条未读数据
     *
     * @return
     */
    public List<MessageInboxDto> queryRecentTenLists();

    /**
     * 更新全部为已读
     */
    public boolean updateToReadState();

    /**
     * 更新全部为未读
     */
    public boolean updateToUnReadState();

    /**
     * 更新全部为已读
     */
    public boolean updateToReadStateByclass(String classifyUuid);

    /**
     * 更新全部为未读
     */
    public boolean updateToUnReadStateByclass(String classifyUuid);

    /**
     * 查询离线消息数量
     *
     * @return
     */
    public Long offLine(String userId);

    /**
     * 消息更新为在线发送
     */
    public int updateOnLine(String userId);

    /**
     * @param messageOutboxUuids
     */
    void deleteByMessageOutboxUuids(List<String> messageOutboxUuids);

    long countByMessageOutboxUuid(String messageOutboxUuid);

    List<MessageInbox> listByMessageOutboxUuid(String messageOutboxUuid);
}
