/*
 * @(#)3/21/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.entity;

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
 * 3/21/25.1	    zhulh		3/21/25		    Create
 * </pre>
 * @date 3/21/25
 */
@javax.persistence.Entity
@Table(name = "feishu_work_record")
@DynamicUpdate
@DynamicInsert
public class FeishuWorkRecordEntity extends SysEntity {
    private static final long serialVersionUID = 5685093030607730192L;

    /**
     * 飞书配置信息Uuid
     */
    private Long configUuid;

    private String appId;

    private String title;

    private String flowInstUuid;

    private String taskInstUuid;

    private String url;

    private String content;

    private String oaUserId;

    private String openId;

    private String openOwnerId;

    private String messageId;

    private String chatId;

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
     * @return the openId
     */
    public String getOpenId() {
        return openId;
    }

    /**
     * @param openId 要设置的openId
     */
    public void setOpenId(String openId) {
        this.openId = openId;
    }

    /**
     * @return the openOwnerId
     */
    public String getOpenOwnerId() {
        return openOwnerId;
    }

    /**
     * @param openOwnerId 要设置的openOwnerId
     */
    public void setOpenOwnerId(String openOwnerId) {
        this.openOwnerId = openOwnerId;
    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId 要设置的messageId
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

