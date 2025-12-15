package com.wellsoft.pt.message.websocket.config;

import com.wellsoft.pt.message.websocket.service.WebSocketService;
import com.wellsoft.pt.multi.org.facade.service.impl.MultiOrgUserAccountFacadeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.messaging.*;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListSet;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    public static ConcurrentMap<String, ConcurrentSkipListSet<String>> userSessionMap = new ConcurrentHashMap<>();
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private WebSocketService webSocketService;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/wellSocket")
                .setHandshakeHandler(new PrincipalHandshakeHandler())
                .setAllowedOrigins("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic");
        registry.setApplicationDestinationPrefixes("/app");
    }

    /**
     * 请求链接
     *
     * @param sessionConnectEvent
     */
    @EventListener
    public void connect(SessionConnectEvent sessionConnectEvent) {
        logger.info("请求链接：" + sessionConnectEvent.toString());
    }


    private void addUserSession(String userId, String sessionId) {
        ConcurrentSkipListSet<String> sessionSet = userSessionMap.get(userId);
        if (sessionSet == null) {
            sessionSet = new ConcurrentSkipListSet<>();
            userSessionMap.put(userId, sessionSet);
        }
        sessionSet.add(sessionId);
    }

    private void delUserSession(String userId, String sessionId) {
        ConcurrentSkipListSet<String> sessionSet = userSessionMap.get(userId);
        if (sessionSet != null) {
            sessionSet.remove(sessionId);
        }
        //删除当前用户关联用户信息
        MultiOrgUserAccountFacadeServiceImpl.userRelationAccountMap.remove(userId);
    }

    /**
     * 响应链接
     *
     * @param sessionConnectedEvent
     */
    @EventListener
    public void connected(SessionConnectedEvent sessionConnectedEvent) {
        this.addUserSession(sessionConnectedEvent.getUser().getName(), sessionConnectedEvent.getMessage().getHeaders().get("simpSessionId").toString());
        logger.info("响应链接：" + sessionConnectedEvent.toString());
    }

    /**
     * 关闭链接
     *
     * @param sessionDisconnectEvent
     */
    @EventListener
    public void disconnect(SessionDisconnectEvent sessionDisconnectEvent) {
        this.delUserSession(sessionDisconnectEvent.getUser().getName(), sessionDisconnectEvent.getSessionId());
        logger.info("关闭链接：" + sessionDisconnectEvent.toString());
    }

    /**
     * 订阅请求
     *
     * @param sessionSubscribeEvent
     */
    @EventListener
    public void subscribe(SessionSubscribeEvent sessionSubscribeEvent) {
        logger.info("订阅请求：" + sessionSubscribeEvent.toString());
        webSocketService.subscribe(sessionSubscribeEvent);
    }

    /**
     * 删除订阅
     *
     * @param sessionUnsubscribeEvent
     */
    @EventListener
    public void unsubscribe(SessionUnsubscribeEvent sessionUnsubscribeEvent) {
        logger.info("删除订阅：" + sessionUnsubscribeEvent.toString());
    }


}
