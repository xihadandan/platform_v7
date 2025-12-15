package com.wellsoft.pt.repository.websocket;

import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.security.Principal;

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
@ServerEndpoint("/newFileFromWPS")
public class NewFileFromWPS extends AbstractEndpoint {

    /**
     * @param session
     * @param config  EndpointConfig
     * @Title: onOpen
     * @Description: 连接建立时的操作
     */
    @OnOpen
    @Override
    public void onOpen(Session session, EndpointConfig config) {
        super.onOpen(session, config);
        UserDetails userDetails = null;
        Principal principal = session.getUserPrincipal();
        if (principal instanceof Authentication) {
            userDetails = (UserDetails) ((Authentication) principal).getPrincipal();
        }
        String msg = null;
        if (userDetails != null) {
            logger.info(msg = "userDetails[" + userDetails.getUserId() + "]开启会话");
        }
        int code = WebSocketUtils.RESULT_MESSAGE_CODE_OPEN;
        ResultMessage message = new ResultMessage(code, msg, true, session.getId());
        try {
            WebSocketSessionManager.syncSend(session.getId(), message);
        } catch (IOException ex) {
            logger.warn(ex.getMessage(), ex);
        }
    }

    /**
     * @param session
     * @param closeReason 原因
     * @Title: onClose
     * @Description: 连接关闭时的操作
     */
    @OnClose
    @Override
    public void onClose(Session session, CloseReason closeReason) {
        super.onClose(session, closeReason);
    }

    /**
     * @param session
     * @param message 收到的消息
     * @Title: onMessage
     * @Description: 收到消息后的操作
     */
    @OnMessage
    @Override
    public void onMessage(Session session, String message) {
        super.onMessage(session, message);
    }

    /**
     * @param session
     * @param error   发生的错误
     * @Title: onError
     * @Description: 连接发生错误时候的操作
     */
    @OnError
    @Override
    public void onError(Session session, Throwable error) {
        super.onError(session, error);
    }

}
