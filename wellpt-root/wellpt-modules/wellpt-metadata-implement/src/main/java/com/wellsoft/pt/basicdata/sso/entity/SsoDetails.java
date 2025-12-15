package com.wellsoft.pt.basicdata.sso.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "xzsp_sso_config")
@DynamicUpdate
@DynamicInsert
public class SsoDetails extends IdEntity {

    private static final long serialVersionUID = -4035616969303300943L;

    /**
     * 编号 code
     */
    private String code;
    /**
     * 系统标识 sysId
     */
    private String sysId;
    /**
     * 系统名称 sysName
     */
    private String sysName;
    /**
     * LOGO地址 logoUrl
     */
    private String logoUrl;
    /**
     * 内网登入地址 loginUrl
     */
    private String loginUrl;
    /**
     * 外网登入地址 outLoginUrl
     */
    private String outLoginUrl;
    /**
     * 内网IP段 ipText
     */
    private String ipText;
    /**
     * 外网IP段 outIpText
     */
    private String outIpText;
    /**
     * 新建窗口 newWindow
     */
    private String newWindow;
    /**
     * 直接登录 dLogin
     */
    private String dLogin;
    /**
     * 登陆方法 loginMethod(1:get、2:post)
     */
    private String loginMethod;
    /**
     * 提交前运行函数 beforeScript
     */
    @Length(max = 1024)
    private String beforeScript;
    /**
     * 登入方法 loginScript
     */
    @Length(max = 1024)
    private String loginScript;
    /**
     * 帐号 whichUserName(1：OA用户、2：用户输入)
     */
    private String whichUserName;
    private String whichPassWord;
    /**
     * 用户字段 userName
     */
    private String userName;
    /**
     * 密码字段 passWord
     */
    private String passWord;
    /**
     * 嵌套HTML innerHtml
     */
    @Length(max = 1024)
    private String innerHtml;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public String getSysName() {
        return sysName;
    }

    public void setSysName(String sysName) {
        this.sysName = sysName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getOutLoginUrl() {
        return outLoginUrl;
    }

    public void setOutLoginUrl(String outLoginUrl) {
        this.outLoginUrl = outLoginUrl;
    }

    public String getIpText() {
        return ipText;
    }

    public void setIpText(String ipText) {
        this.ipText = ipText;
    }

    public String getOutIpText() {
        return outIpText;
    }

    public void setOutIpText(String outIpText) {
        this.outIpText = outIpText;
    }

    public String getNewWindow() {
        return newWindow;
    }

    public void setNewWindow(String newWindow) {
        this.newWindow = newWindow;
    }

    public String getdLogin() {
        return dLogin;
    }

    public void setdLogin(String dLogin) {
        this.dLogin = dLogin;
    }

    public String getLoginMethod() {
        return loginMethod;
    }

    public void setLoginMethod(String loginMethod) {
        this.loginMethod = loginMethod;
    }

    @Length(max = 1024)
    public String getBeforeScript() {
        return beforeScript;
    }

    public void setBeforeScript(String beforeScript) {
        this.beforeScript = beforeScript;
    }

    @Length(max = 1024)
    public String getLoginScript() {
        return loginScript;
    }

    public void setLoginScript(String loginScript) {
        this.loginScript = loginScript;
    }

    public String getWhichUserName() {
        return whichUserName;
    }

    public void setWhichUserName(String whichUserName) {
        this.whichUserName = whichUserName;
    }

    public String getWhichPassWord() {
        return whichPassWord;
    }

    public void setWhichPassWord(String whichPassWord) {
        this.whichPassWord = whichPassWord;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    @Length(max = 1024)
    public String getInnerHtml() {
        return innerHtml;
    }

    public void setInnerHtml(String innerHtml) {
        this.innerHtml = innerHtml;
    }
}