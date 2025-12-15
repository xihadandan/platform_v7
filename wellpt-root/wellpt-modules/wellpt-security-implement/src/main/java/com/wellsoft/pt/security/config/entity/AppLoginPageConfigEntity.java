/*
 * @(#)2018-12-12 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.config.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.sql.Clob;

/**
 * Description: 数据库表APP_LOGIN_PAGE_CONFIG的实体类
 *
 * @author linst
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018-12-12.1	leo		2018-12-12		Create
 * </pre>
 * @date 2018-12-12
 */
@Entity
@Table(name = "APP_LOGIN_PAGE_CONFIG")
@DynamicUpdate
@DynamicInsert
@ApiModel("app登录页面配置")
public class AppLoginPageConfigEntity extends IdEntity {

    private static final long serialVersionUID = 1544576770537L;

    // 硬件登陆描述
    @ApiModelProperty("硬件登陆描述")
    private String loginBoxHardwareDescription;
    // 是否启用忘记密码
    @ApiModelProperty("是否启用忘记密码")
    private String loginBoxAccountForgetPassw;
    // 是否启用短信
    @ApiModelProperty("是否启用短信")
    private String loginBoxAccountSms;

    // 短信验证码超时时间
    @ApiModelProperty("短信验证码超时时间")
    private Integer loginBoxAccountSmsTimeout;
    // 是否启用硬件登陆
    @ApiModelProperty("是否启用硬件登陆")
    private String loginBoxHardware;
    // 是否启用记住密码
    @ApiModelProperty("是否启用记住密码")
    private String loginBoxAccountRememberPas;
    // 背景图
    @JsonIgnore
    private Clob pageBackgroundImage;
    @ApiModelProperty("pageBackgroundImageBase64")
    private String pageBackgroundImageBase64;

    // 风格
    @ApiModelProperty("风格")
    private String pageStyle;
    // 页脚内容
    @ApiModelProperty("页脚内容")
    private String footerContent;
    // 是否启用二维码登陆
    @ApiModelProperty("页脚内容")
    private String loginBoxCode;
    // 是否启用记住用户名
    @ApiModelProperty("是否启用记住用户名")
    private String loginBoxAccountRememberUse;
    // 是否启用验证码
    @ApiModelProperty("是否启用验证码")
    private String loginBoxAccountCode;
    @ApiModelProperty("验证码类型 数字+字母：letterAndNumber 数字：number")
    private String loginBoxAccountCodeType;

    @ApiModelProperty("验证码忽略大小写")
    private String accountCodeIgnoreCase;

    // 验证码超时时间
    @ApiModelProperty("验证码超时时间")
    private Integer loginBoxAccountCodeTimeout;
    // 标题
    @ApiModelProperty("标题")
    private String pageTitle;
    // logo图片
    @JsonIgnore
    private Clob pageLogo;
    @ApiModelProperty("pageLogoBase64")
    private String pageLogoBase64;
    // 是否启用单点登陆
    @ApiModelProperty("是否启用单点登陆")
    private String loginBoxCas;
    @ApiModelProperty("loginBoxCasUrl")
    private String loginBoxCasUrl;
    @ApiModelProperty("loginBoxCasAppUrl")
    private String loginBoxCasAppUrl;
    @ApiModelProperty("systemUnitId")
    private String systemUnitId;

    // 是否启用KEY登陆
    @ApiModelProperty("是否启用KEY登陆")
    private String loginBoxKey;
    // KEY登陆描述
    @ApiModelProperty("KEY登陆描述")
    private String loginBoxKeyDescription;
    @ApiModelProperty("// 钉钉扫码登录：0启用、1不启用")
    private String loginBoxDing;
    @ApiModelProperty("// 钉钉二维码样式")
    private String dingStyle;
    @ApiModelProperty("// 钉钉二维码宽度")
    private String dingWidth;
    @ApiModelProperty("// 钉钉二维码高度")
    private String dingHeight;
    @ApiModelProperty("//启用统一认证登录")
    private String enableOauth;
    @ApiModelProperty("//使用统一认证登录页")
    private String enableOauthLogin;
    @ApiModelProperty("enableCustomizeOauthLogin")
    private String enableCustomizeOauthLogin;
    @ApiModelProperty("//自定义平台的统一认证登录页地址")
    private String customizeOauthLoginUri;

    @ApiModelProperty("pwdEncryptKey")
    private String pwdEncryptKey;
    @ApiModelProperty("passwordEncryptType")
    private String passwordEncryptType;

    @ApiModelProperty("单位登录页地址")
    private String unitLoginPageUri;
    @ApiModelProperty("单位登录页配置，0 默认登录页，1 自定义登录页")
    private String unitLoginPageSwitch;

    @ApiModelProperty("登录用户名提示")
    private String userNamePlaceholder;

    public String getLoginBoxAccountCodeType() {
        return this.loginBoxAccountCodeType;
    }

    public void setLoginBoxAccountCodeType(final String loginBoxAccountCodeType) {
        this.loginBoxAccountCodeType = loginBoxAccountCodeType;
    }

    public String getLoginBoxKey() {
        return loginBoxKey;
    }

    public void setLoginBoxKey(String loginBoxKey) {
        this.loginBoxKey = loginBoxKey;
    }

    public String getLoginBoxKeyDescription() {
        return loginBoxKeyDescription;
    }

    public void setLoginBoxKeyDescription(String loginBoxKeyDescription) {
        this.loginBoxKeyDescription = loginBoxKeyDescription;
    }

    /**
     * @return the loginBoxHardwareDescription
     */
    public String getLoginBoxHardwareDescription() {
        return this.loginBoxHardwareDescription;
    }

    /**
     * @param loginBoxHardwareDescription
     */
    public void setLoginBoxHardwareDescription(String loginBoxHardwareDescription) {
        this.loginBoxHardwareDescription = loginBoxHardwareDescription;
    }

    /**
     * @return the loginBoxAccountForgetPassw
     */
    public String getLoginBoxAccountForgetPassw() {
        return this.loginBoxAccountForgetPassw;
    }

    /**
     * @param loginBoxAccountForgetPassw
     */
    public void setLoginBoxAccountForgetPassw(String loginBoxAccountForgetPassw) {
        this.loginBoxAccountForgetPassw = loginBoxAccountForgetPassw;
    }

    /**
     * @return the loginBoxAccountSms
     */
    public String getLoginBoxAccountSms() {
        return this.loginBoxAccountSms;
    }

    /**
     * @param loginBoxAccountSms
     */
    public void setLoginBoxAccountSms(String loginBoxAccountSms) {
        this.loginBoxAccountSms = loginBoxAccountSms;
    }

    /**
     * @return the loginBoxHardware
     */
    public String getLoginBoxHardware() {
        return this.loginBoxHardware;
    }

    /**
     * @param loginBoxHardware
     */
    public void setLoginBoxHardware(String loginBoxHardware) {
        this.loginBoxHardware = loginBoxHardware;
    }

    /**
     * @return the loginBoxAccountRememberPas
     */
    public String getLoginBoxAccountRememberPas() {
        return this.loginBoxAccountRememberPas;
    }

    /**
     * @param loginBoxAccountRememberPas
     */
    public void setLoginBoxAccountRememberPas(String loginBoxAccountRememberPas) {
        this.loginBoxAccountRememberPas = loginBoxAccountRememberPas;
    }

    /**
     * @return the pageBackgroundImage
     */
    public Clob getPageBackgroundImage() {
        return this.pageBackgroundImage;
    }

    /**
     * @param pageBackgroundImage
     */
    public void setPageBackgroundImage(Clob pageBackgroundImage) {
        this.pageBackgroundImage = pageBackgroundImage;
    }

    /**
     * @return the pageStyle
     */
    public String getPageStyle() {
        return this.pageStyle;
    }

    /**
     * @param pageStyle
     */
    public void setPageStyle(String pageStyle) {
        this.pageStyle = pageStyle;
    }

    /**
     * @return the footerContent
     */
    public String getFooterContent() {
        return this.footerContent;
    }

    /**
     * @param footerContent
     */
    public void setFooterContent(String footerContent) {
        this.footerContent = footerContent;
    }

    /**
     * @return the loginBoxCode
     */
    public String getLoginBoxCode() {
        return this.loginBoxCode;
    }

    /**
     * @param loginBoxCode
     */
    public void setLoginBoxCode(String loginBoxCode) {
        this.loginBoxCode = loginBoxCode;
    }

    /**
     * @return the loginBoxAccountRememberUse
     */
    public String getLoginBoxAccountRememberUse() {
        return this.loginBoxAccountRememberUse;
    }

    /**
     * @param loginBoxAccountRememberUse
     */
    public void setLoginBoxAccountRememberUse(String loginBoxAccountRememberUse) {
        this.loginBoxAccountRememberUse = loginBoxAccountRememberUse;
    }

    /**
     * @return the loginBoxAccountCode
     */
    public String getLoginBoxAccountCode() {
        return this.loginBoxAccountCode;
    }

    /**
     * @param loginBoxAccountCode
     */
    public void setLoginBoxAccountCode(String loginBoxAccountCode) {
        this.loginBoxAccountCode = loginBoxAccountCode;
    }

    /**
     * @return the pageTitle
     */
    public String getPageTitle() {
        return this.pageTitle;
    }

    /**
     * @param pageTitle
     */
    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    /**
     * @return the pageLogo
     */
    public Clob getPageLogo() {
        return this.pageLogo;
    }

    /**
     * @param pageLogo
     */
    public void setPageLogo(Clob pageLogo) {
        this.pageLogo = pageLogo;
    }

    public Integer getLoginBoxAccountSmsTimeout() {
        return loginBoxAccountSmsTimeout;
    }

    public void setLoginBoxAccountSmsTimeout(Integer loginBoxAccountSmsTimeout) {
        this.loginBoxAccountSmsTimeout = loginBoxAccountSmsTimeout;
    }

    public Integer getLoginBoxAccountCodeTimeout() {
        return loginBoxAccountCodeTimeout;
    }

    public void setLoginBoxAccountCodeTimeout(Integer loginBoxAccountCodeTimeout) {
        this.loginBoxAccountCodeTimeout = loginBoxAccountCodeTimeout;
    }

    public String getLoginBoxCas() {
        return loginBoxCas;
    }

    public void setLoginBoxCas(String loginBoxCas) {
        this.loginBoxCas = loginBoxCas;
    }

    public String getLoginBoxCasUrl() {
        return loginBoxCasUrl;
    }

    public void setLoginBoxCasUrl(String loginBoxCasUrl) {
        this.loginBoxCasUrl = loginBoxCasUrl;
    }

    public String getLoginBoxCasAppUrl() {
        return loginBoxCasAppUrl;
    }

    public void setLoginBoxCasAppUrl(String loginBoxCasAppUrl) {
        this.loginBoxCasAppUrl = loginBoxCasAppUrl;
    }

    public String getSystemUnitId() {
        return systemUnitId;
    }

    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    public String getLoginBoxDing() {
        return loginBoxDing;
    }

    public void setLoginBoxDing(String loginBoxDing) {
        this.loginBoxDing = loginBoxDing;
    }

    public String getDingStyle() {
        return dingStyle;
    }

    public void setDingStyle(String dingStyle) {
        this.dingStyle = dingStyle;
    }

    public String getDingWidth() {
        return dingWidth;
    }

    public void setDingWidth(String dingWidth) {
        this.dingWidth = dingWidth;
    }

    public String getDingHeight() {
        return dingHeight;
    }

    public void setDingHeight(String dingHeight) {
        this.dingHeight = dingHeight;
    }

    public String getEnableOauth() {
        return enableOauth;
    }

    public void setEnableOauth(String enableOauth) {
        this.enableOauth = enableOauth;
    }

    public String getEnableOauthLogin() {
        return enableOauthLogin;
    }

    public void setEnableOauthLogin(String enableOauthLogin) {
        this.enableOauthLogin = enableOauthLogin;
    }

    public String getEnableCustomizeOauthLogin() {
        return enableCustomizeOauthLogin;
    }

    public void setEnableCustomizeOauthLogin(String enableCustomizeOauthLogin) {
        this.enableCustomizeOauthLogin = enableCustomizeOauthLogin;
    }

    public String getCustomizeOauthLoginUri() {
        return customizeOauthLoginUri;
    }

    public void setCustomizeOauthLoginUri(String customizeOauthLoginUri) {
        this.customizeOauthLoginUri = customizeOauthLoginUri;
    }

    @Transient
    public String getPageBackgroundImageBase64() {
        return pageBackgroundImageBase64;
    }

    public void setPageBackgroundImageBase64(String pageBackgroundImageBase64) {
        this.pageBackgroundImageBase64 = pageBackgroundImageBase64;
    }

    @Transient
    public String getPageLogoBase64() {
        return pageLogoBase64;
    }

    public void setPageLogoBase64(String pageLogoBase64) {
        this.pageLogoBase64 = pageLogoBase64;
    }

    @Transient
    public String getPwdEncryptKey() {
        return pwdEncryptKey;
    }

    public void setPwdEncryptKey(String pwdEncryptKey) {
        this.pwdEncryptKey = pwdEncryptKey;
    }

    @Transient
    public String getPasswordEncryptType() {
        return passwordEncryptType;
    }

    public void setPasswordEncryptType(String passwordEncryptType) {
        this.passwordEncryptType = passwordEncryptType;
    }

    public String getAccountCodeIgnoreCase() {
        return accountCodeIgnoreCase;
    }

    public void setAccountCodeIgnoreCase(String accountCodeIgnoreCase) {
        this.accountCodeIgnoreCase = accountCodeIgnoreCase;
    }

    @Transient
    public String getUserNamePlaceholder() {
        return userNamePlaceholder;
    }

    public void setUserNamePlaceholder(String userNamePlaceholder) {
        this.userNamePlaceholder = userNamePlaceholder;
    }

    public String getUnitLoginPageUri() {
        return unitLoginPageUri;
    }

    public void setUnitLoginPageUri(String unitLoginPageUri) {
        this.unitLoginPageUri = unitLoginPageUri;
    }

    public String getUnitLoginPageSwitch() {
        return unitLoginPageSwitch;
    }

    public void setUnitLoginPageSwitch(String unitLoginPageSwitch) {
        this.unitLoginPageSwitch = unitLoginPageSwitch;
    }
}
