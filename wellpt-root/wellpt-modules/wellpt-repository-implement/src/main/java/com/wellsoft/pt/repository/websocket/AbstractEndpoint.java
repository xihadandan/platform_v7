/*
 * @(#)2019年12月15日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.repository.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

/**
 * Description: AbstractEndpoint
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年12月15日.1	zhongzh		2019年12月15日		Create
 * </pre>
 * @date 2019年12月15日
 */
public abstract class AbstractEndpoint {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param session
     * @param config  EndpointConfig
     * @Title: onOpen
     * @Description: 连接建立时的操作
     */
    public void onOpen(Session session, EndpointConfig config) {
        WebSocketSessionManager.put(session);
    }

    /**
     * @param session
     * @param closeReason 原因
     * @Title: onClose
     * @Description: 连接关闭时的操作
     */
    public void onClose(Session session, CloseReason closeReason) {
        WebSocketSessionManager.remove(session);
    }

    /**
     * @param session
     * @param message 收到的消息
     * @Title: onMessage
     * @Description: 收到消息后的操作
     */
    public void onMessage(Session session, String message) {
        logger.info("webSocket[" + session.getId() + "]收到消息:" + message);
    }

    /**
     * @param session
     * @param error   发生的错误
     * @Title: onError
     * @Description: 连接发生错误时候的操作
     */
    public void onError(Session session, Throwable error) {
        WebSocketSessionManager.onError(session, error);
    }

}
