package com.wellsoft.pt.webmail.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 第三方邮箱连接配置
 *
 * @author chenq
 * @date 2018/6/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/6/13    chenq		2018/6/13		Create
 * </pre>
 */
@Entity
@Table(name = "WM_MAIL_OPEN_SERVER")
@DynamicUpdate
@DynamicInsert
public class WmMailOpenServerEntity extends IdEntity {

    /**
     * 域名
     */
    private String domain;

    /**
     * smtp服务器
     */
    private String smtpServer;

    /**
     * smtp端口
     */
    private String smtpPort;

    /**
     * pop服务器
     */
    private String popServer;

    /**
     * pop端口
     */
    private String popPort;

    /**
     * imap服务器
     */
    private String imapServer;

    /**
     * imap端口
     */
    private String imapPort;

    /**
     * s
     * 是否popSsl
     */
    private Boolean isPopSsl;

    /**
     * 是否imapSsl
     */
    private Boolean isImapSsl;

    /**
     * 是否smtpSsl
     */
    private Boolean isSmtpSsl;

    /**
     * 获取 域名
     *
     * @return
     */
    public String getDomain() {
        return domain;
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
     * 获取 smtp服务器
     *
     * @return
     */
    public String getSmtpServer() {
        return smtpServer;
    }

    /**
     * 设置 smtp服务器
     *
     * @param smtpServer
     */
    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    /**
     * 获取 smtp端口
     *
     * @return
     */
    public String getSmtpPort() {
        return smtpPort;
    }

    /**
     * 设置 smtp端口
     *
     * @param smtpPort
     */
    public void setSmtpPort(String smtpPort) {
        this.smtpPort = smtpPort;
    }

    /**
     * 获取 pop服务器
     *
     * @return
     */
    public String getPopServer() {
        return popServer;
    }

    /**
     * 设置 pop服务器
     *
     * @param popServer
     */
    public void setPopServer(String popServer) {
        this.popServer = popServer;
    }

    /**
     * 获取 pop端口
     *
     * @return
     */
    public String getPopPort() {
        return popPort;
    }

    /**
     * 设置 pop端口
     *
     * @param popPort
     */
    public void setPopPort(String popPort) {
        this.popPort = popPort;
    }

    /**
     * 获取 imap服务器
     *
     * @return
     */
    public String getImapServer() {
        return imapServer;
    }

    /**
     * 设置 imap服务器
     *
     * @param imapServer
     */
    public void setImapServer(String imapServer) {
        this.imapServer = imapServer;
    }

    /**
     * 获取 imap端口
     *
     * @return
     */
    public String getImapPort() {
        return imapPort;
    }

    /**
     * 设置 imap端口
     *
     * @param imapPort
     */
    public void setImapPort(String imapPort) {
        this.imapPort = imapPort;
    }

    /**
     * 获取 是否popSsl
     *
     * @return
     */
    public Boolean getIsPopSsl() {
        return isPopSsl;
    }

    /**
     * 设置 是否popSsl
     *
     * @param popSsl
     */
    public void setIsPopSsl(Boolean popSsl) {
        isPopSsl = popSsl;
    }

    /**
     * 获取 是否imapSsl
     *
     * @return
     */
    public Boolean getIsImapSsl() {
        return isImapSsl;
    }

    /**
     * 设置 是否imapSsl
     *
     * @param imapSsl
     */
    public void setIsImapSsl(Boolean imapSsl) {
        isImapSsl = imapSsl;
    }

    /**
     * 获取 是否smtpSsl
     *
     * @return
     */
    public Boolean getIsSmtpSsl() {
        return isSmtpSsl;
    }

    /**
     * 设置 是否smtpSsl
     *
     * @param smtpSsl
     */
    public void setIsSmtpSsl(Boolean smtpSsl) {
        isSmtpSsl = smtpSsl;
    }
}
