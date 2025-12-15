package com.wellsoft.pt.message.websocket.config;

import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class PrincipalHandshakeHandler extends DefaultHandshakeHandler {
    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        //设置用户Id
        return new WebSocketUserAuthInfo(userId);
    }

}
