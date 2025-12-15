package com.wellsoft.pt.message.websocket.service.impl;

import com.wellsoft.context.config.SystemParamsUtils;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.message.dto.MessageInboxDto;
import com.wellsoft.pt.message.entity.MessageInbox;
import com.wellsoft.pt.message.service.MessageInboxService;
import com.wellsoft.pt.message.service.UserPersonaliseService;
import com.wellsoft.pt.message.websocket.service.MsgHeaders;
import com.wellsoft.pt.message.websocket.service.MsgPayLoad;
import com.wellsoft.pt.message.websocket.service.MsgTypeEnum;
import com.wellsoft.pt.message.websocket.service.WebSocketService;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgUserAccountFacadeService;
import com.wellsoft.pt.multi.org.facade.service.impl.MultiOrgUserAccountFacadeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.*;

@Service
public class WebSocketServiceImpl implements WebSocketService {

    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    MultiOrgUserAccountFacadeService multiOrgUserAccountFacadeService;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private MessageInboxService messageInboxService;
    @Autowired
    private UserPersonaliseService userPersonaliseService;
    private String topicAddr = "/topic/callBack";

    @Override
    public void sendToUser(String user, MsgPayLoad payload, MsgHeaders headers) {
        messagingTemplate.convertAndSendToUser(user, topicAddr, payload, headers.toMap());
    }

    @Override
    public void send(List<MessageInbox> messageInboxes) {
        for (MessageInbox messageInbox : messageInboxes) {
            MsgPayLoad payLoad = new MsgPayLoad(MsgTypeEnum.inboxOnLine, MessageInboxDto.revert(messageInbox));
            messagingTemplate.convertAndSendToUser(messageInbox.getRecipient(), topicAddr, payLoad);
        }
    }

    @Override
    public void sendOffLine(List<String> onlineUserIds) {
        for (String onlineUserId : onlineUserIds) {
            Map<String, Long> result = countOffLine(onlineUserId);
            MsgPayLoad payLoad = new MsgPayLoad(MsgTypeEnum.countOffLine, result);
            messagingTemplate.convertAndSendToUser(onlineUserId, topicAddr, payLoad);
        }
    }

    private Map<String, Long> countOffLine(String onlineUserId) {
        Map<String, Long> result = new HashMap<>();
        Set<String> offLineUserIds = MultiOrgUserAccountFacadeServiceImpl.userRelationAccountMap.get(onlineUserId);
        for (String offLineUserId : offLineUserIds) {
            if (!onlineUserId.equals(offLineUserId)) {
                Long count = messageInboxService.offLine(offLineUserId);
                result.put(offLineUserId, count);
            }
        }
        return result;
    }


    @Override
    public void callBack(String json, StompHeaderAccessor headerAccessor) {
        MsgPayLoad msgPayLoad = JsonUtils.gson2Object(json, MsgPayLoad.class);
        if (msgPayLoad.getType().equals(MsgTypeEnum.inboxOffLine)) {
            messageInboxService.updateOnLine(headerAccessor.getUser().getName());
        }
    }

    @Override
    public void subscribe(SessionSubscribeEvent event) {
        String destinationPrefix = messagingTemplate.getUserDestinationPrefix();
        String topicAddrStr = destinationPrefix + topicAddr.substring(1);
        if (topicAddrStr.equals(event.getMessage().getHeaders().get("simpDestination"))) {
            //主开关有开 提示离线弹窗
            if (userPersonaliseService.isMain(event.getUser().getName())) {
                Long count = messageInboxService.offLine(event.getUser().getName());
                if (count > 0) {
                    MsgHeaders msgHeaders = new MsgHeaders(new MsgPayLoad(MsgTypeEnum.inboxOffLine, count));
                    MsgPayLoad payLoad = new MsgPayLoad(MsgTypeEnum.inboxOffLine, count);
                    messagingTemplate.convertAndSendToUser(event.getUser().getName(), topicAddr, payLoad, msgHeaders.toMap());
                }
            }
            //一人多岗开启才需要处理
            String relationEnable = SystemParamsUtils.getValue("system.account.relation.enable");
            if (MultiOrgUserAccountFacadeServiceImpl.ENABLE.equals(relationEnable)) {
                //增加缓存用户关联账户信息
                multiOrgUserAccountFacadeService.addRelationAccountCache(event.getUser().getName());
                //查询离线账号消息数量
                List<String> onlineUserId = new ArrayList<>();
                onlineUserId.add(event.getUser().getName());
                sendOffLine(onlineUserId);
            }

        }
    }
}
