package com.wellsoft.pt.message.websocket.config;

import java.security.Principal;

public class WebSocketUserAuthInfo implements Principal {

    /**
     * 用户身份标识符
     */
    private String userId;

    public WebSocketUserAuthInfo(String userId) {
        this.userId = userId;
    }


    @Override
    public String getName() {
        return userId;
    }
}
