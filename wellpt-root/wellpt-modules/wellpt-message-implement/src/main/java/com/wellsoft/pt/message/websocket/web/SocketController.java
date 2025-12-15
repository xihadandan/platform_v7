package com.wellsoft.pt.message.websocket.web;

import com.wellsoft.pt.message.websocket.service.WebSocketService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/wellSocket")
public class SocketController {
    Logger log = LoggerFactory.getLogger(SocketController.class);

    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private WebSocketService webSocketService;

    /**
     * 实例化Controller的时候，注入SimpMessagingTemplate
     *
     * @param messagingTemplate
     */
    public SocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // 跳转stomp websocket 页面
    @RequestMapping(value = "/test")
    public ModelAndView toStompWebSocket(HttpSession session, HttpServletRequest request, Model model) {
        session.setAttribute("userId", SpringSecurityUtils.getCurrentUserId());
        return new ModelAndView("/websocket/test", model.asMap());

    }

    /**
     * 前端统一回调
     *
     * @param json           消息JSON字符串
     * @param headerAccessor 消息头（用户）
     * @return
     */
    @MessageMapping("/callBack")
    public void callBack(String json,
                         StompHeaderAccessor headerAccessor) {
        webSocketService.callBack(json, headerAccessor);
    }
}
