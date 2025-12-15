/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

/**
 * Description: 邮件配置
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-06-03.1	t		2016-06-03		Create
 * </pre>
 * @date 2016-06-03
 */
@Entity
@Table(name = "WM_MAIL_CONFIG")
@DynamicUpdate
@DynamicInsert
public class WmMailConfigEntity extends TenantEntity {

    public final static String MAIL_USER_DEFAULT_PASSWORD = "a123456";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1464933720994L;
    /**
     * 域名
     */
    @NotBlank
    private String domain;

    /**
     * POP3服务器
     */
    @NotBlank
    private String pop3Server;

    /**
     * POP3服务器端口110
     */
    @NotNull
    @Digits(fraction = 0, integer = 10)
    private Integer pop3Port;

    /**
     * 发送服务器
     */
    @NotBlank
    private String smtpServer;

    /**
     * 发送服务器端口25
     */
    @NotNull
    @Digits(fraction = 0, integer = 10)
    private Integer smtpPort;

    /**
     * IMAP服务器
     */
    @NotBlank
    private String imapServer;

    /**
     * IMAP服务器端口143
     */
    @NotNull
    @Digits(fraction = 0, integer = 10)
    private Integer imapPort;

    /**
     * 是否在服务器保留备份
     */
    private Boolean keepOnServer;

    /**
     * 默认容量
     */
    private Integer defaultCapacity;

    /**
     * 剩余容量提醒
     */
    private Integer deadlineCapacity;

    /**
     * 邮件服务
     */
    private String mailServerType;

    /**
     * api端口
     */
    private Integer apiPort;

    /**
     * 允许组织选项
     */
    private String allowOrgOptions;

    /**
     * 附件大小限制单位（MB）
     */
    private String attachmentSizeLimit;

    /**
     * 点击接收邮件操作 0：显示收件箱，1：维持不变
     */
    private Integer receiveMailAction;

    /**
     * 是否公网邮箱 0：否，1：是
     */
    private Boolean isPublicEmail;

    /**
     * 是否自动发送回执 0：否，1：是
     */
    private Boolean sendReceipt;


    /**
     * 获取 域名
     *
     * @return the domain
     */
    @Column(name = "\"DOMAIN\"")
    public String getDomain() {
        return this.domain;
    }

    /**
     * 设置 域名
     *
     * @param domain
     */
    public void setDomain(String domain) {
        this.domain = domain;
    }

    /**
     * 获取 POP3服务器
     *
     * @return the pop3Server
     */
    @Column(name = "POP3_SERVER")
    public String getPop3Server() {
        return this.pop3Server;
    }

    /**
     * 设置 POP3服务器
     *
     * @param pop3Server
     */
    public void setPop3Server(String pop3Server) {
        this.pop3Server = pop3Server;
    }

    /**
     * 获取 POP3服务器端口110
     *
     * @return the pop3Port
     */
    @Column(name = "POP3_PORT")
    public Integer getPop3Port() {
        return this.pop3Port;
    }

    /**
     * 设置 POP3服务器端口110
     *
     * @param pop3Port
     */
    public void setPop3Port(Integer pop3Port) {
        this.pop3Port = pop3Port;
    }

    /**
     * 获取 发送服务器
     *
     * @return
     */
    public String getSmtpServer() {
        return smtpServer;
    }

    /**
     * 设置 发送服务器
     *
     * @param smtpServer
     */
    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    /**
     * 获取 发送服务器端口25
     *
     * @return
     */
    public Integer getSmtpPort() {
        return smtpPort;
    }

    /**
     * 设置 发送服务器端口25
     *
     * @param smtpPort
     */
    public void setSmtpPort(Integer smtpPort) {
        this.smtpPort = smtpPort;
    }

    /**
     * 获取 IMAP服务器
     *
     * @return the imapServer
     */
    @Column(name = "IMAP_SERVER")
    public String getImapServer() {
        return this.imapServer;
    }

    /**
     * 设置 IMAP服务器
     *
     * @param imapServer
     */
    public void setImapServer(String imapServer) {
        this.imapServer = imapServer;
    }

    /**
     * 获取 IMAP服务器端口143
     *
     * @return the imapPort
     */
    @Column(name = "IMAP_PORT")
    public Integer getImapPort() {
        return this.imapPort;
    }

    /**
     * 设置 IMAP服务器端口143
     *
     * @param imapPort
     */
    public void setImapPort(Integer imapPort) {
        this.imapPort = imapPort;
    }

    /**
     * 获取 是否在服务器保留备份
     *
     * @return the keepOnServer
     */
    @Column(name = "KEEP_ON_SERVER")
    public Boolean getKeepOnServer() {
        return this.keepOnServer;
    }

    /**
     * 设置 是否在服务器保留备份
     *
     * @param keepOnServer
     */
    public void setKeepOnServer(Boolean keepOnServer) {
        this.keepOnServer = keepOnServer;
    }

    /**
     * 获取 默认容量
     *
     * @return
     */
    public Integer getDefaultCapacity() {
        return defaultCapacity;
    }

    /**
     * 设置 默认容量
     *
     * @param defaultCapacity
     */
    public void setDefaultCapacity(Integer defaultCapacity) {
        this.defaultCapacity = defaultCapacity;
    }

    /**
     * 获取 剩余容量提醒
     *
     * @return
     */
    public Integer getDeadlineCapacity() {
        return deadlineCapacity;
    }

    /**
     * 设置 剩余容量提醒
     *
     * @param deadlineCapacity
     */
    public void setDeadlineCapacity(Integer deadlineCapacity) {
        this.deadlineCapacity = deadlineCapacity;
    }

    /**
     * 获取 邮件服务
     *
     * @return
     */
    @Column(name = "MAIL_SERVER_TYPE")
    public String getMailServerType() {
        return mailServerType;
    }

    /**
     * 设置 邮件服务
     *
     * @param mailServerType
     */
    public void setMailServerType(String mailServerType) {
        this.mailServerType = mailServerType;
    }

    /**
     * 获取 api端口
     *
     * @return
     */
    @Column(name = "API_PORT")
    public Integer getApiPort() {
        return apiPort;
    }

    /**
     * 设置 api端口
     *
     * @param apiPort
     */
    public void setApiPort(Integer apiPort) {
        this.apiPort = apiPort;
    }

    /**
     * 获取 允许组织选项
     *
     * @return
     */
    @Column(name = "ALLOW_ORG_OPTIONS")
    public String getAllowOrgOptions() {
        return allowOrgOptions;
    }

    /**
     * 设置 允许组织选项
     *
     * @param allowOrgOptions
     */
    public void setAllowOrgOptions(String allowOrgOptions) {
        this.allowOrgOptions = allowOrgOptions;
    }

    /**
     * 获取 附件大小限制单位（MB）
     *
     * @return
     */
    public String getAttachmentSizeLimit() {
        return attachmentSizeLimit;
    }

    /**
     * 设置 附件大小限制单位（MB）
     *
     * @param attachmentSizeLimit
     */
    public void setAttachmentSizeLimit(String attachmentSizeLimit) {
        this.attachmentSizeLimit = attachmentSizeLimit;
    }

    public Integer getReceiveMailAction() {
        return receiveMailAction;
    }

    public void setReceiveMailAction(Integer receiveMailAction) {
        this.receiveMailAction = receiveMailAction;
    }

    public Boolean getIsPublicEmail() {
        return isPublicEmail;
    }

    public void setIsPublicEmail(Boolean publicEmail) {
        isPublicEmail = publicEmail;
    }

    public Boolean getSendReceipt() {
        return sendReceipt;
    }

    public void setSendReceipt(Boolean sendReceipt) {
        this.sendReceipt = sendReceipt;
    }
}
