/*
 * @(#)2012-10-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.message.support;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 消息Bean
 */
@ApiModel("消息Bean")
public class MessageBean extends IdEntity {

    /**
     * 接收用户id
     */
    @ApiModelProperty(value = "接收用户id")
    private String userId;
    /**
     * 消息内容
     */
    @ApiModelProperty(value = "消息内容")
    private String body;
    /**
     * 消息类型
     */
    @ApiModelProperty(value = "消息类型")
    private String[] type;
    /**
     * 消息主题
     */
    @ApiModelProperty(value = "消息主题")
    private String subject;
    /**
     * 消息紧急程度
     */
    @ApiModelProperty(value = "消息紧急程度")
    private String markflag;
    /**
     * 消息附件
     */
    @ApiModelProperty(value = "消息附件")
    private String[] messageAttach;
    /**
     * 在线源地址
     */
    @ApiModelProperty(value = "在线源地址")
    private String relatedUrl;
    /**
     * 在线消息源标题
     */
    @ApiModelProperty(value = "在线消息源标题")
    private String relatedTitle;
    /**
     * 接收用户名称
     */
    @ApiModelProperty(value = "接收用户名称")
    private String showUser;

    /**
     * 获取 用户Id
     *
     * @return
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 设置 用户Id
     *
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 获取 消息内容
     *
     * @return
     */
    public String getBody() {
        return body;
    }

    /**
     * 设置 消息内容
     *
     * @param body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * 获取 消息类型
     *
     * @return
     */
    public String[] getType() {
        return type;
    }

    /**
     * 设置 消息类型
     *
     * @param type
     */
    public void setType(String[] type) {
        this.type = type;
    }

    /**
     * 获取 消息主题
     *
     * @return
     */
    public String getSubject() {
        return subject;
    }

    /**
     * 设置 消息主题
     *
     * @param subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * 获取 消息紧急程度
     *
     * @return
     */
    public String getMarkflag() {
        return markflag;
    }

    /**
     * 设置 消息紧急程度
     *
     * @param markflag
     */
    public void setMarkflag(String markflag) {
        this.markflag = markflag;
    }

    /**
     * 获取 消息附件
     *
     * @return
     */
    public String[] getMessageAttach() {
        return messageAttach;
    }

    /**
     * 设置 消息附件
     *
     * @param messageAttach
     */
    public void setMessageAttach(String[] messageAttach) {
        this.messageAttach = messageAttach;
    }

    /**
     * 获取 在线源地址
     *
     * @return
     */
    public String getRelatedUrl() {
        return relatedUrl;
    }

    /**
     * 设置 在线源地址
     *
     * @param relatedUrl
     */
    public void setRelatedUrl(String relatedUrl) {
        this.relatedUrl = relatedUrl;
    }

    /**
     * 获取 在线消息源标题
     *
     * @return
     */
    public String getRelatedTitle() {
        return relatedTitle;
    }

    /**
     * 设置 在线消息源标题
     *
     * @param relatedTitle
     */
    public void setRelatedTitle(String relatedTitle) {
        this.relatedTitle = relatedTitle;
    }

    /**
     * 获取 接收用户名称
     *
     * @return
     */
    public String getShowUser() {
        return showUser;
    }

    /**
     * 设置 接收用户名称
     *
     * @param showUser
     */
    public void setShowUser(String showUser) {
        this.showUser = showUser;
    }
}
