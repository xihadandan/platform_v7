/*
 * @(#)3/25/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.support;

import com.google.common.collect.Maps;
import com.lark.oapi.service.im.v1.model.Mention;
import com.lark.oapi.service.im.v1.model.Message;
import com.lark.oapi.service.im.v1.model.MessageBody;
import com.lark.oapi.service.im.v1.model.Sender;
import com.wellsoft.pt.app.feishu.model.FileMessage;
import com.wellsoft.pt.app.feishu.model.TextMessage;
import com.wellsoft.pt.app.feishu.utils.FeishuApiUtils;
import com.wellsoft.pt.app.feishu.vo.FeishuConfigVo;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 3/25/25.1	    zhulh		3/25/25		    Create
 * </pre>
 * @date 3/25/25
 */
public class FeishuMessageParser {

    public FeishuMessageParser() {
    }

    public FeishuMessage parse(Message message, Map<String, String> userNameMap, FeishuConfigVo feishuConfigVo) {
        FeishuMessage feishuMessage = null;
        String msgType = message.getMsgType();
        switch (msgType) {
            case "text":
                feishuMessage = parseText(message, userNameMap, feishuConfigVo);
                break;
            case "file":
                feishuMessage = parseFile(message, userNameMap, feishuConfigVo);
                break;
            case "image":
                feishuMessage = parseImage(message, userNameMap, feishuConfigVo);
                break;
            default:
                TextMessage textMessage = new TextMessage();
                textMessage.setSenderName(getSenderName(message, userNameMap, feishuConfigVo));
                textMessage.setSendTime(getSendTime(message));
                textMessage.setMessageId(message.getMessageId());
                textMessage.setChatId(message.getChatId());
                textMessage.setText(message.getBody().getContent());
                feishuMessage = textMessage;
                break;
        }
        return feishuMessage;
    }

    private FeishuMessage parseText(Message message, Map<String, String> userNameMap, FeishuConfigVo feishuConfigVo) {
        MessageBody body = message.getBody();
        String content = body.getContent();
        TextMessage textMessage = new TextMessage();
        textMessage.setSenderName(getSenderName(message, userNameMap, feishuConfigVo));
        textMessage.setSendTime(getSendTime(message));
        textMessage.setMessageId(message.getMessageId());
        textMessage.setChatId(message.getChatId());
        textMessage.setText(content);
        if (!StringUtils.startsWith(content, "{")) {
            return textMessage;
        }

        Map<String, String> mentionMap = Maps.newHashMap();
        mentionMap.put("@_all", "@所有人");
        Mention[] mentions = message.getMentions();
        if (mentions != null) {
            Arrays.stream(mentions).forEach(mention -> {
                mentionMap.put(mention.getKey(), "@" + mention.getName());
            });
        }

        JSONObject jsonObject = new JSONObject(content);
        String text = jsonObject.optString("text");
        if (StringUtils.isNotBlank(text)) {
            for (Map.Entry<String, String> entry : mentionMap.entrySet()) {
                text = StringUtils.replace(text, entry.getKey(), entry.getValue());
            }
        }
        textMessage.setText(text);
        return textMessage;
    }

    private FeishuMessage parseFile(Message message, Map<String, String> userNameMap, FeishuConfigVo feishuConfigVo) {
        MessageBody body = message.getBody();
        String content = body.getContent();
        FileMessage fileMessage = new FileMessage();
        fileMessage.setSenderName(getSenderName(message, userNameMap, feishuConfigVo));
        fileMessage.setSendTime(getSendTime(message));
        fileMessage.setMessageId(message.getMessageId());
        fileMessage.setChatId(message.getChatId());
        fileMessage.setFileName(content);
        if (!StringUtils.startsWith(content, "{")) {
            return fileMessage;
        }
        JSONObject jsonObject = new JSONObject(content);
        fileMessage.setFileName(jsonObject.optString("file_name"));
        fileMessage.setFileKey(jsonObject.optString("file_key"));
        fileMessage.setType(message.getMsgType());
        return fileMessage;
    }

    private FeishuMessage parseImage(Message message, Map<String, String> userNameMap, FeishuConfigVo feishuConfigVo) {
        MessageBody body = message.getBody();
        String content = body.getContent();
        FileMessage fileMessage = new FileMessage();
        fileMessage.setSenderName(getSenderName(message, userNameMap, feishuConfigVo));
        fileMessage.setSendTime(getSendTime(message));
        fileMessage.setMessageId(message.getMessageId());
        fileMessage.setChatId(message.getChatId());
        fileMessage.setFileName(content);
        if (!StringUtils.startsWith(content, "{")) {
            return fileMessage;
        }
        JSONObject jsonObject = new JSONObject(content);
        String fileKey = jsonObject.optString("image_key");
        if (StringUtils.contains(fileKey, ".")) {
            fileMessage.setFileName(fileKey);
        } else {
            fileMessage.setFileName(fileKey + ".png");
        }
        fileMessage.setFileKey(fileKey);
        fileMessage.setType(message.getMsgType());
        return fileMessage;
    }

    private Date getSendTime(Message message) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(message.getCreateTime()));
        return calendar.getTime();
    }

    private String getSenderName(Message message, Map<String, String> userNameMap, FeishuConfigVo feishuConfigVo) {
        Sender sender = message.getSender();
        String senderName = null;
        // 获取机器人名称
        if ("app".equals(sender.getSenderType())) {
            senderName = FeishuApiUtils.getBotName(sender.getId(), feishuConfigVo);
        } else {
            senderName = userNameMap.get(sender.getId());
        }
        return senderName;
    }

}
