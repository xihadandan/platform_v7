package com.wellsoft.pt.message.websocket.service;

import com.wellsoft.pt.message.entity.MessageInbox;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;

public interface WebSocketService {

    void sendToUser(String user, MsgPayLoad payload, MsgHeaders headers);

    /**
     * 往浏览器发消息
     *
     * @param inboxes
     * @return void
     **/
    void send(List<MessageInbox> inboxes);

    /**
     * 用户浏览器链接到服务端的订阅事件
     *
     * @param event
     * @return void
     **/
    void subscribe(SessionSubscribeEvent event);

    /**
     * 用户收到消息后的回调处理
     *
     * @param json
     * @param headerAccessor
     * @return void
     **/
    void callBack(String json, StompHeaderAccessor headerAccessor);

    /**
     * 发送需要统计离线消息的在线userId
     *
     * @param
     * @return
     * @author baozh
     * @date 2022/1/14 16:54
     */
    void sendOffLine(List<String> onlineUserIds);
}
