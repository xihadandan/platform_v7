/*
 * @(#)4/24/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/24/25.1	    zhulh		4/24/25		    Create
 * </pre>
 * @date 4/24/25
 */
@javax.persistence.Entity
@Table(name = "dingtalk_work_record")
@DynamicUpdate
@DynamicInsert
public class DingtalkWorkRecordEntity extends SysEntity {
    private static final long serialVersionUID = 8232420581413754003L;

    private Long configUuid;

    private String appId;

    private String title;

    private String flowInstUuid;

    private String taskInstUuid;

    private String url;

    private String content;

    private String oaUserId;

    private String userId;

    private String ownerId;

    // 消息通知推送返回的ID
    private String msgTaskId;

    private String chatId;

    private String conversationId;

    private Boolean groupChat;

    private Type type;

    private State state;

    private String errMsg;

    /**
     * @return the configUuid
     */
    public Long getConfigUuid() {
        return configUuid;
    }

    /**
     * @param configUuid 要设置的configUuid
     */
    public void setConfigUuid(Long configUuid) {
        this.configUuid = configUuid;
    }

    /**
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId 要设置的appId
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * @param flowInstUuid 要设置的flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * @return the taskInstUuid
     */
    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    /**
     * @param taskInstUuid 要设置的taskInstUuid
     */
    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url 要设置的url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content 要设置的content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the oaUserId
     */
    public String getOaUserId() {
        return oaUserId;
    }

    /**
     * @param oaUserId 要设置的oaUserId
     */
    public void setOaUserId(String oaUserId) {
        this.oaUserId = oaUserId;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the ownerId
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId 要设置的ownerId
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    /**
     * @return the msgTaskId
     */
    public String getMsgTaskId() {
        return msgTaskId;
    }

    /**
     * @param msgTaskId 要设置的msgTaskId
     */
    public void setMsgTaskId(String msgTaskId) {
        this.msgTaskId = msgTaskId;
    }

    /**
     * @return the chatId
     */
    public String getChatId() {
        return chatId;
    }

    /**
     * @param chatId 要设置的chatId
     */
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    /**
     * @return the conversationId
     */
    public String getConversationId() {
        return conversationId;
    }

    /**
     * @param conversationId 要设置的conversationId
     */
    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * @return the groupChat
     */
    @Column(name = "IS_GROUP_CHAT")
    public Boolean getGroupChat() {
        return groupChat;
    }

    /**
     * @param groupChat 要设置的groupChat
     */
    public void setGroupChat(Boolean groupChat) {
        this.groupChat = groupChat;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * @return the state
     */
    public State getState() {
        return state;
    }

    /**
     * @param state 要设置的state
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * @return the errMsg
     */
    public String getErrMsg() {
        return errMsg;
    }

    /**
     * @param errMsg 要设置的errMsg
     */
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public enum Type {
        SYSTEM, USER
    }

    public enum State {
        ToSend, Sent, Cancelled, Deleted
    }

}
