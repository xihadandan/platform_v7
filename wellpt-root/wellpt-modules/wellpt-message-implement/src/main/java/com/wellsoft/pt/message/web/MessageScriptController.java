package com.wellsoft.pt.message.web;

import com.wellsoft.context.util.date.DateUtil;
import com.wellsoft.context.web.controller.BaseController;
import com.wellsoft.pt.message.entity.MessageInbox;
import com.wellsoft.pt.message.service.MessageInboxService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.io.IOUtils;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.annotations.RemoteMethod;
import org.directwebremoting.annotations.RemoteProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RemoteProxy(name = "directController")
public class MessageScriptController extends BaseController {
    @Autowired
    private MessageInboxService messageInboxService;


    /**
     * 初始化scriptSession
     *
     * @return
     */
    @RemoteMethod
    public Map<String, Object> onPageLoad() {
        Map<String, Object> map = new HashMap<String, Object>();
        ScriptSession scriptSession = WebContextFactory.get().getScriptSession();
        scriptSession.setAttribute("userId", SpringSecurityUtils.getCurrentUserId());
        long noReadMessageCount = messageInboxService.getOnlineMessageCount("receive",
                SpringSecurityUtils.getCurrentUserId(), "false");
        MessageInbox messageInbox = new MessageInbox();
        messageInbox.setIsread(false);
        messageInbox.setRecipient(SpringSecurityUtils.getCurrentUserId());
        messageInbox.setIscancel(false);
        messageInbox.setIsOnlinePopup("Y");
        List<MessageInbox> messageInboxs = messageInboxService.getOnlineMessageByExample(messageInbox);
        List<Map<String, String>> msgList = new ArrayList<Map<String, String>>();
        for (MessageInbox inbox : messageInboxs) {
            Map<String, String> msgMap = new HashMap<String, String>();
            Clob messageParm = inbox.getMessageParm();
            String js = "";
            if (messageParm != null) {
                try {
                    js = IOUtils.toString(messageParm.getCharacterStream());
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                }
            }
            msgMap.put("uuid", inbox.getUuid());
            msgMap.put("js", js);
            msgMap.put("sender", inbox.getSender());
            msgMap.put("senderName", inbox.getSenderName());
            msgMap.put("recipient", inbox.getRecipient());
            msgMap.put("recipientName", inbox.getRecipientName());
            msgMap.put("subject", inbox.getSubject());
            // msgMap.put("body", inbox.getBody() == null ? "" :
            // inbox.getBody().toString());
            msgMap.put("body", "");
            msgMap.put("note", inbox.getNote() == null ? "" : inbox.getNote().toString());
            msgMap.put("viewpoint", inbox.getViewpoint() == null ? "" : inbox.getViewpoint().toString());
            msgMap.put("relatedurl", inbox.getRelatedUrl() == null ? "" : inbox.getRelatedUrl().toString());
            msgMap.put("receivedtime", DateUtil.getFormatDate(inbox.getReceivedTime(), "yyyy-MM-dd HH:mm:ss"));
            msgMap.put("outboxuuid", inbox.getMessageOutboxUuid());
            msgMap.put("markflag", inbox.getMarkFlag());
            msgList.add(msgMap);
        }
        //Map<String, SysProperties> sysPropertiess = exchangedataApiFacade.getAllSysProperties("SYSPROPERTIES");
        //String size = sysPropertiess.get("sys_msg_showlist_num").getProValue();// 内网/外网
        // in/out
        map.put("count", noReadMessageCount);
        map.put("messageInboxs", msgList);
        //map.put("size", size);
        return map;
    }

    @RemoteMethod
    public String showMenu(Integer id) {
        StringBuilder builder = new StringBuilder();
        builder.append(id);
        builder.append("--");
        builder.append("--");
        builder.append("--");
        builder.append("--");
        return builder.toString();
    }
}
