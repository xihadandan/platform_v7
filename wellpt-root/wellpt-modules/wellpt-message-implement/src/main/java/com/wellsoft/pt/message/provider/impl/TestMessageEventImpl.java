package com.wellsoft.pt.message.provider.impl;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.message.provider.AbstractMessageEventSourceProvider;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.message.support.MessageEventConstant;
import com.wellsoft.pt.message.websocket.service.MsgHeaders;
import com.wellsoft.pt.message.websocket.service.MsgPayLoad;
import com.wellsoft.pt.message.websocket.service.MsgTypeEnum;
import com.wellsoft.pt.message.websocket.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * @author yt
 * @title: TestMessageEventImpl
 * @date 2020/6/1 10:58 上午
 */
@Service
public class TestMessageEventImpl extends AbstractMessageEventSourceProvider {

    private static Logger logger = LoggerFactory.getLogger(TestMessageEventImpl.class);
    @Autowired
    private WebSocketService webSocketService;

    @Override
    public String getMessageType() {
        return MessageEventConstant.EVENT_CLIENT;
    }

    @Override
    public String getModuleId() {
        return "testModuleId";
    }

    @Override
    public String getModuleName() {
        return "testModuleName";
    }

    @Override
    public void executeServerMessageEvent(Message mesage, String viewpoint, String note) {
        logger.error("==========接收消息后触发事件【测试消息事件1】:{}，{}", new String[]{mesage.getSender(), mesage.getSenderName()});
        MsgPayLoad payLoad = new MsgPayLoad(MsgTypeEnum.inboxOffLine, "【测试消息事件1】");
        MsgHeaders msgHeaders = new MsgHeaders(new MsgPayLoad(MsgTypeEnum.inboxOffLine, mesage.getSubject()));
        webSocketService.sendToUser(mesage.getRecipients().get(0), payLoad, msgHeaders);
    }

    @Override
    public <ENTITY extends IdEntity> void executeClientMessageEvent(String templateId, Collection<ENTITY> entities, Map<Object, Object> dataMap, Map<String, Object> extraData, Collection<String> recipients, ENTITY entity) {
        logger.error("==========发送消息触发事件【测试消息事件2】:{}", recipients.iterator().next());
        MsgPayLoad payLoad = new MsgPayLoad(MsgTypeEnum.inboxOffLine, "【测试消息事件2】");
        MsgHeaders msgHeaders = new MsgHeaders(new MsgPayLoad(MsgTypeEnum.inboxOffLine, templateId));
        webSocketService.sendToUser(recipients.iterator().next(), payLoad, msgHeaders);
    }
}
