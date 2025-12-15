package com.wellsoft.pt.message.provider.impl;

import com.wellsoft.pt.message.provider.MessageIntefaceSourceProvider;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.message.websocket.service.MsgHeaders;
import com.wellsoft.pt.message.websocket.service.MsgPayLoad;
import com.wellsoft.pt.message.websocket.service.MsgTypeEnum;
import com.wellsoft.pt.message.websocket.service.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yt
 * @title: TestMessageIntefaceImpl
 * @date 2020/6/1 10:36 上午
 */
@Service
public class TestMessageIntefaceImpl implements MessageIntefaceSourceProvider {

    private static Logger logger = LoggerFactory.getLogger(TestMessageEventImpl.class);

    @Autowired
    private WebSocketService webSocketService;

    @Override
    public void doService(Message msg) {
        logger.error("==========发送消息触发后台接口类【测试消息接口】:{}，{}", new String[]{msg.getSender(), msg.getSenderName()});
        MsgPayLoad payLoad = new MsgPayLoad(MsgTypeEnum.inboxOffLine, "【测试消息接口】");
        MsgHeaders msgHeaders = new MsgHeaders(new MsgPayLoad(MsgTypeEnum.inboxOffLine, msg.getSubject()));
        webSocketService.sendToUser(msg.getRecipients().get(0), payLoad, msgHeaders);
    }

    @Override
    public String getIntefaceName() {
        return "测试消息接口";
    }
}
