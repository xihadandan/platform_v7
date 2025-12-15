package com.wellsoft.pt.repository.websocket;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;

import javax.websocket.Session;
import java.io.IOException;
import java.security.Principal;
import java.util.Map;

/**
 * Description: 监听WPS新建上传的文件
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年12月13日.1	zhongzh		2019年12月13日		Create
 * </pre>
 * @date 2019年12月13日
 */
public class WebSocketSessionManager {

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private final static Map<String, Session> sessionMap = Maps.newConcurrentMap();
    private static Logger logger = LoggerFactory.getLogger(WebSocketSessionManager.class);

    /**
     * 连接建立时的操作
     */
    public static Session put(Session session) {
        logger.info("webSocket[" + session.getId() + "]连接建立");
        return sessionMap.put(session.getId(), session);
    }

    /**
     * 连接关闭时的操作
     */
    public static Session remove(Session session) {
        logger.info("webSocket[" + session.getId() + "]连接断开");
        return sessionMap.remove(session.getId());
    }

    /**
     * @param @param error 发生的错误
     * @Title: onError
     * @Description: 连接发生错误时候的操作
     */
    public static void onError(Session session, Throwable error) {
        logger.error("webSocket[" + session.getId() + "]连接错误", error);
    }

    /**
     * 告知前端
     *
     * @param message
     * @throws IOException
     */
    public static void sendMessage(String wSessionId, SessionCallback callback) throws IOException {
        Session session = sessionMap.get(wSessionId);
        if (session == null) {
            logger.info("session[" + wSessionId + "] is removed");
        } else if (false == session.isOpen()) {
            logger.info("session[" + wSessionId + "] is closed");
        } else {
            callback.doSend(session);
        }
    }

    public static void syncSend(String wSessionId, final String message) throws IOException {
        sendMessage(wSessionId, new SessionCallback() {
            @Override
            public void doSend(Session session) throws IOException {
                session.getBasicRemote().sendText(message);
            }
        });
    }

    public static void syncSend(String wSessionId, final ResultMessage message) throws IOException {
        syncSend(wSessionId, JsonUtils.object2Json(message));
    }

    public static void asyncSend(String wSessionId, final String message) throws IOException {
        sendMessage(wSessionId, new SessionCallback() {
            @Override
            public void doSend(Session session) throws IOException {
                session.getAsyncRemote().sendText(message);
            }
        });
    }

    public static void asyncSend(String wSessionId, ResultMessage message) throws IOException {
        asyncSend(wSessionId, JsonUtils.object2Json(message));
    }

    public static <T extends UserDetails> T getUserDetail(String wSessionId) {
        Session session = sessionMap.get(wSessionId);
        if (session == null || false == session.isOpen()) {
            return null;
        }
        Principal authentication = session.getUserPrincipal();
        if (authentication == null || false == authentication instanceof Authentication) {
            return null;
        }
        Object principal = ((Authentication) authentication).getPrincipal();
        if (!(principal instanceof UserDetails)) {
            return null;
        }
        return (T) principal;
    }

    public static boolean validateSession(String wSessionId) {
        Session session = sessionMap.get(wSessionId);
        if (session == null) {
            throw new RuntimeException("无效的SessionId[" + wSessionId + "]");
        } else if (false == session.isOpen()) {
            throw new RuntimeException("Session[" + wSessionId + "]已关闭");
        }
        return true;
    }
}
