/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.webmail.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Description: 用户邮箱信息
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
@Table(name = "WM_MAIL_USER")
@DynamicUpdate
@DynamicInsert
public class WmMailUserEntity extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1464933745287L;

    // 用户ID
    private String userId;
    // 用户名称
    private String userName;
    // 邮箱用户名，用于验证邮箱密码的账号，默认为邮箱地址
    private String mailUserName;
    // 邮箱地址
    private String mailAddress;
    // 邮箱密码
    private String mailPassword;
    // 邮箱密码加密算法
    private String mailPasswordHashAlgorithm;
    // 回复邮箱地址，默认为空
    private String replyMailAddress;
    // POP3服务器
    private String pop3Server;
    // POP3服务器端口110
    private Integer pop3Port;
    // 发送服务器
    private String smtpServer;
    // 发送服务器端口25
    private Integer smtpPort;
    // IMAP服务器
    private String imapServer;
    // IMAP服务器端口143
    private Integer imapPort;
    // 默认的邮件发送地址，用于有多个邮箱账号的默认邮箱
    private Boolean isDefault;

    private Boolean isPopSsl;//pop是否启用SSL协议

    private Boolean isSmtpSsl;//smtp是否启用SSL协议

    private Boolean isInnerUser;//是否是内部邮件账号

    private Integer syncMessageNumber;//上次同步的邮件序号(用于下一次同步的起始)

    private Integer limitCapacity;//容量限制

    private Long usedCapacity;//已使用容量

    private String usedCapacityReadable;

    private String limitCapacityReadable;

    private String remainCapacityReadable;

    /**
     * @return the userId
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * @param userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the mailUserName
     */
    public String getMailUserName() {
        return this.mailUserName;
    }

    /**
     * @param mailUserName
     */
    public void setMailUserName(String mailUserName) {
        this.mailUserName = mailUserName;
    }

    /**
     * @return the mailAddress
     */
    public String getMailAddress() {
        return this.mailAddress;
    }

    /**
     * @param mailAddress
     */
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    /**
     * @return the mailPassword
     */
    public String getMailPassword() {
        return this.mailPassword;
    }

    /**
     * @param mailPassword
     */
    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }

    /**
     * @return the mailPasswordHashAlgorithm
     */
    public String getMailPasswordHashAlgorithm() {
        return this.mailPasswordHashAlgorithm;
    }

    /**
     * @param mailPasswordHashAlgorithm
     */
    public void setMailPasswordHashAlgorithm(String mailPasswordHashAlgorithm) {
        this.mailPasswordHashAlgorithm = mailPasswordHashAlgorithm;
    }

    /**
     * @return the replyMailAddress
     */
    public String getReplyMailAddress() {
        return this.replyMailAddress;
    }

    /**
     * @param replyMailAddress
     */
    public void setReplyMailAddress(String replyMailAddress) {
        this.replyMailAddress = replyMailAddress;
    }

    /**
     * @return the pop3Server
     */
    @Column(name = "pop3_server")
    public String getPop3Server() {
        return this.pop3Server;
    }

    /**
     * @param pop3Server
     */
    public void setPop3Server(String pop3Server) {
        this.pop3Server = pop3Server;
    }

    /**
     * @return the pop3Port
     */
    @Column(name = "pop3_port")
    public Integer getPop3Port() {
        return this.pop3Port;
    }

    /**
     * @param pop3Port
     */
    public void setPop3Port(Integer pop3Port) {
        this.pop3Port = pop3Port;
    }


    /**
     * @return the imapServer
     */
    public String getImapServer() {
        return this.imapServer;
    }

    /**
     * @param imapServer
     */
    public void setImapServer(String imapServer) {
        this.imapServer = imapServer;
    }

    /**
     * @return the imapPort
     */
    public Integer getImapPort() {
        return this.imapPort;
    }

    /**
     * @param imapPort
     */
    public void setImapPort(Integer imapPort) {
        this.imapPort = imapPort;
    }

    /**
     * @return the isDefault
     */
    public Boolean getIsDefault() {
        return this.isDefault;
    }

    /**
     * @param isDefault
     */
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }


    public Boolean getIsInnerUser() {
        return isInnerUser;
    }

    public void setIsInnerUser(Boolean innerUser) {
        isInnerUser = innerUser;
    }

    public Boolean getIsPopSsl() {
        return isPopSsl;
    }

    public void setIsPopSsl(Boolean popSsl) {
        isPopSsl = popSsl;
    }


    public String getSmtpServer() {
        return smtpServer;
    }

    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    public Integer getSmtpPort() {
        return smtpPort;
    }

    public void setSmtpPort(Integer smtpPort) {
        this.smtpPort = smtpPort;
    }

    public Boolean getIsSmtpSsl() {
        return isSmtpSsl;
    }

    public void setIsSmtpSsl(Boolean smtpSsl) {
        isSmtpSsl = smtpSsl;
    }

    public Integer getSyncMessageNumber() {
        return syncMessageNumber;
    }

    public void setSyncMessageNumber(Integer syncMessageNumber) {
        this.syncMessageNumber = syncMessageNumber;
    }

    public Integer getLimitCapacity() {
        return limitCapacity;
    }

    public void setLimitCapacity(Integer limitCapacity) {
        this.limitCapacity = limitCapacity;
    }

    public Long getUsedCapacity() {
        return usedCapacity;
    }

    public void setUsedCapacity(Long usedCapacity) {
        this.usedCapacity = usedCapacity;
    }

    @Transient
    public String getUsedCapacityReadable() {
        return usedCapacityReadable;
    }

    public void setUsedCapacityReadable(String usedCapacityReadable) {
        this.usedCapacityReadable = usedCapacityReadable;
    }

    @Transient
    public String getLimitCapacityReadable() {
        return limitCapacityReadable;
    }

    public void setLimitCapacityReadable(String limitCapacityReadable) {
        this.limitCapacityReadable = limitCapacityReadable;
    }

    @Transient
    public String getRemainCapacityReadable() {
        return remainCapacityReadable;
    }

    public void setRemainCapacityReadable(String remainCapacityReadable) {
        this.remainCapacityReadable = remainCapacityReadable;
    }
}
