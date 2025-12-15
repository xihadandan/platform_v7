/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.processor.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.config.SystemParamsUtils;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.message.entity.MessageInbox;
import com.wellsoft.pt.message.entity.MessageOutbox;
import com.wellsoft.pt.message.processor.AbstractMessageProcessor;
import com.wellsoft.pt.message.service.MessageInboxService;
import com.wellsoft.pt.message.service.RtxMessageService;
import com.wellsoft.pt.message.service.UserPersonaliseService;
import com.wellsoft.pt.message.support.Message;
import com.wellsoft.pt.message.support.MessageExtraParm;
import com.wellsoft.pt.message.websocket.config.WebSocketConfig;
import com.wellsoft.pt.message.websocket.service.WebSocketService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.multi.org.facade.service.impl.MultiOrgUserAccountFacadeServiceImpl;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.sql.Clob;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-29.1	zhulh		2012-10-29		Create
 * </pre>
 * @date 2012-10-29
 */
@Component
public class OnlineMessageProcessor extends AbstractMessageProcessor {

    private static final String KEY_MSG_RTX_ENABLE = "msg.rtx.enable";

    private static final String MSG_RTX_ENABLE = "true";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.message.processor.MessageProcessor#doProcessor(com.wellsoft.pt.message.support.Message)
     */
    @Autowired
    private MessageInboxService messageInboxService;

    @Autowired
    private RtxMessageService rtxMessageService;
    @Autowired
    private MongoFileService mongoFileService;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Autowired
    private WebSocketService webSocketService;
    @Autowired
    private UserPersonaliseService userPersonaliseService;

    @Override
    public void doProcessor(Message msg) {

        List<String> recipients = msg.getRecipients();
        IdEntity entity = msg.getExtraParm();
        String systemid = "";
        String messageOutbox_msgid = "";
        String relatedUrl = "";
        String recipientName = "";
        if (entity instanceof MessageExtraParm) {
            MessageExtraParm parm = (MessageExtraParm) entity;
            systemid = parm.getSystemid();
            messageOutbox_msgid = parm.getMessageid();
            relatedUrl = parm.getRelatedUrl();
            msg.setRelatedTitle(relatedUrl);
            msg.setRelatedUrl(relatedUrl);
        }
        Map<String, String> recipients_all_map;
        if (StringUtils.equals(msg.getRecipientType(), Message.RECIPIENT_TYPE_INTERNET_USER)) {
            // 互联网用户 消息 -> 直接获取互联网用户信息
            recipients_all_map = orgApiFacade.getInternetUsersByLoginNames(recipients);
        } else {
            recipients_all_map = orgFacadeService.getUserIdNamesByOrgElementIds(recipients);
        }

        Set<String> recipients_all = recipients_all_map.keySet(); // getAllRecipients(recipients,
        // recipients_all_map);//
        // 取得所有的接受者
        // recipientNames 下面的代码里面，没有使用到，这里可以取消赋值操作
        // getReceiversNameByOrgIds(recipients, recipients_all_map,
        // recipientNames);// 取得接收者的名称
        for (String recipientNameTemp : msg.getRecipientNames()) {
            recipientName = recipientName + recipientNameTemp + ";";
        }
        if (!"".equals(recipientName)) {
            recipientName = recipientName.substring(0, recipientName.lastIndexOf(";"));
        }
        List<MessageInbox> inboxs = new ArrayList<>();
        List<String> offLineUserIds = new ArrayList<>();
        for (String recipient : recipients_all) {
            // 保存信息
            MessageInbox inbox = new MessageInbox();
            inbox.setSender(msg.getSender());
            inbox.setRecipient(recipient);
            inbox.setSubject(msg.getSubject());
            inbox.setBody(msg.getBody());
            inbox.setSentTime(msg.getSentTime());
            inbox.setReceivedTime(new Date());
            inbox.setIsread(Boolean.FALSE);
            inbox.setSystem(msg.getSystem());
            inbox.setTenant(msg.getTenant());
            if (WebSocketConfig.userSessionMap.containsKey(recipient)
                    && WebSocketConfig.userSessionMap.get(recipient).size() > 0) {
                inbox.setOnLine(Boolean.TRUE);
                inboxs.add(inbox);
            } else {
                inbox.setOnLine(Boolean.FALSE);
                offLineUserIds.add(recipient);
            }
            inbox.setIscancel(Boolean.FALSE);
            inbox.setRelatedUrl(msg.getRelatedUrl());
            inbox.setCreateTime(new Date());
            inbox.setCreator(msg.getSender());
            inbox.setMessageOutboxUuid(msg.getDataUuid());
            boolean flg = userPersonaliseService.isPopupWin(recipient, msg);
            inbox.setIsOnlinePopup(flg ? "Y" : "N");
            inbox.setSystemUnitId(msg.getSystemUnitId());
            if (msg.getMessageLevel() == null) {
                inbox.setMarkFlag("0");
            } else {
                inbox.setMarkFlag(msg.getMessageLevel());
            }
            if ("system".equals(msg.getSender())) {
                inbox.setSenderName("系统");
            } else {
                if (!"unknown".equals(msg.getSender())) {// 如果没有设定消息类型
                    String name = messageInboxService.getUserNameById(msg.getSender());
                    if (name == null || "".equals(name)) {
                        name = msg.getSenderName();
                    }
                    inbox.setSenderName(name);
                }

            }
            inbox.setRecipientName(recipientName);
            msg.setRecipients(null);// 将收件人置为null
            final String js = JsonUtils.object2Json(msg);

            inbox.setMessageParm(messageInboxService.getClobParm(js));// 保存message参数信息
            inbox.setClassifyUuid(msg.getClassifyUuid());
            inbox.setClassifyName(msg.getClassifyName());
            messageInboxService.saveMessageInbox(inbox);
        }

        messageInboxService.flushSession();
        //发送websocket消息
        webSocketService.send(inboxs);
        // @see JmsMessageProducer.saveMessageOutbox
//		String messageAttach = msg.getAttach();
//		if (messageAttach != null && !"".equals(messageAttach) && messageAttach != null && !"".equals(messageAttach)) {
//			String[] fileids = messageAttach.split(Separator.COMMA.getValue());
//			List<String> field_list = new ArrayList<String>();
//			field_list = Arrays.asList(fileids);
//			saveAttach(msg.getDataUuid(), field_list);
//		}
        // 发送rtx消息
        if (MSG_RTX_ENABLE.equals(Config.getValue(KEY_MSG_RTX_ENABLE))) {
            rtxMessageService.send(msg);
        }

        //一人多岗开启才需要
        String relationEnable = SystemParamsUtils.getValue("system.account.relation.enable");
        if (MultiOrgUserAccountFacadeServiceImpl.ENABLE.equals(relationEnable)) {
            sendOffLine(offLineUserIds);
        }
    }

    //收集要统计发送的uuid
    private void sendOffLine(List<String> offLineUserIds) {
        if (CollectionUtils.isEmpty(offLineUserIds)) {
            return;
        }
        ConcurrentMap<String, Set<String>> userRelationAccountMap = MultiOrgUserAccountFacadeServiceImpl.userRelationAccountMap;
        List<String> onlineUserIds = new ArrayList<>();
        for (Map.Entry<String, Set<String>> setEntry : userRelationAccountMap.entrySet()) {
            for (String value : setEntry.getValue()) {
                if (offLineUserIds.contains(value)) {
                    onlineUserIds.add(setEntry.getKey());
                    break;
                }
            }
        }
        //发送需要统计的在线userId
        webSocketService.sendOffLine(onlineUserIds);

    }

    /**
     * 推送消息
     *
     * @param js
     * @param outbox
     */
    private void senddwr(MessageOutbox outbox) {
        Clob message_parm = null;
        String message = "";
        // 推送未读消息数给用户
        // if(outbox!=null){
        // for(MessageInbox inbox:outbox.getMessageInbox()){
        // message_parm=inbox.getMessageParm();
        // final String userId = inbox.getRecipient();
        // final String inbox_uuid = inbox.getUuid();
        // if(message_parm!=null){
        // try {
        // message= IOUtils.toString(message_parm.getCharacterStream());
        // } catch (IOException e) {
        // e.printStackTrace();
        // } catch (SQLException e) {
        // e.printStackTrace();
        // }
        // }
        // final String js=message;
        // final String sender=inbox.getSender();
        // final String senderName=inbox.getSenderName();
        // final String recipient=inbox.getRecipient();
        // final String recipientName=inbox.getRecipientName();
        // final String subject=inbox.getSubject();
        // final String body=getStringNullblank(inbox.getBody());
        // final String note=getStringNullblank(inbox.getNote());
        // final String viewpoint=getStringNullblank(inbox.getViewpoint());
        // final String relatedurl=getStringNullblank(inbox.getRelatedUrl());
        // final String
        // receivedtime=DateUtil.getFormatDate(inbox.getReceivedTime(),
        // "yyyy-MM-dd HH:mm:ss");
        // final String outboxuuid=outbox.getUuid();
        // final String markflag=inbox.getMarkFlag();
        // final Long noReadMessageCount =
        // messageInboxService.getOnlineMessageCount("receive", userId,
        // "false");
        // Browser.withAllSessionsFiltered(new ScriptSessionFilter() {
        // public boolean match(ScriptSession session) {
        // if (session.getAttribute("userId") == null)
        // return false;
        // else
        // return (session.getAttribute("userId")).equals(userId);
        // }
        // }, new Runnable() {
        // private ScriptBuffer script = new ScriptBuffer();
        // public void run() {
        // script.appendCall("showMessage",
        // noReadMessageCount,js,inbox_uuid,sender,senderName,recipient,recipientName,receivedtime,subject,body,note,viewpoint,relatedurl,outboxuuid,markflag);//先推送数量
        // Collection<ScriptSession> sessions = Browser.getTargetSessions();
        // for (ScriptSession scriptSession : sessions) {
        // scriptSession.addScript(script);
        // }
        // }
        // });

        // }
        // }

    }

    /**
     * 保存消息附件
     *
     * @param mesgUuid
     * @param fileids
     */
    private void saveAttach(String mesgUuid, List<String> fileids) {
        mongoFileService.pushFilesToFolder(mesgUuid, fileids, "messageAttach");

    }

}
