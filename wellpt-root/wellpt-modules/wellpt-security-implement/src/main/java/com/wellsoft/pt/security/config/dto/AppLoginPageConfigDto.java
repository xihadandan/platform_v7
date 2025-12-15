/*
 * @(#)2018-12-07 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.config.dto;

import java.io.Serializable;

/**
 * Description: 数据库表APP_LOGIN_PAGE_CONFIG的对应的DTO类
 *
 * @author linst
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018-12-07.1	leo		2018-12-07		Create
 * </pre>
 * @date 2018-12-07
 */
public class AppLoginPageConfigDto implements Serializable {

    private static final long serialVersionUID = 1544147382340L;

    // 硬件登陆描述
    private String loginBoxHardwareDescription;
    // 是否启用单点登陆
    private String loginBoxSingle;
    // 是否启用忘记密码
    private String loginBoxAccountForgetPassw;
    // 登录框Y位置
    private Integer loginBoxY;
    // 登录框X位置
    private Integer loginBoxX;
    // 页脚标题
    private String footerTitle;
    // 登录框头部高度
    private Integer loginBoxHeaderHeight;
    // 登陆框宽度
    private Integer loginBoxWidth;
    // 是否启用二维码登陆
    private String loginBoxCode;
    // 登录框头部背景图路径
    private String loginBoxHeaderImagePath;
    // 是否启用用户名
    private String loginBoxAccountRememberUse;
    // 页脚高度
    private Integer footerHeight;
    // 背景图路径
    private String bodyBackgroundImagePath;
    // 是否启用验证码
    private String loginBoxAccountCode;
    // 页脚Y位置
    private Integer footerY;
    // 是否启用登录框头
    private String loginBoxHeader;
    // 页面X位置
    private Integer footerX;
    // 是否启用验证码
    private String loginBoxSingleCode;
    // 登录框背景色
    private String loginBoxBackgroundColor;
    // 登陆高度
    private Integer loginBoxHeight;
    // 页脚字体颜色
    private String footerFontColor;
    // 模型名称
    private String modelName;
    // 是否启用短信
    private String loginBoxAccountSms;
    // 是否启用
    private String isEnable;
    // 登陆框_字体颜色
    private String loginBoxFontColor;
    // 登录框层级
    private Integer loginBoxZindex;
    // 是否启用硬件登陆
    private String loginBoxHardware;
    // 是否启用密码
    private String loginBoxAccountRememberPas;
    // 页脚层级
    private Integer footerZindex;
    // 是否启用短信
    private String loginBoxSingleSms;
    // 页脚字体大小
    private String footerFontSize;
    // 是否隐藏页脚
    private String isHihdenFooter;
    // 是否启用用户名
    private String loginBoxSingleRememberUser;
    // 页脚内容
    private String footerFontContent;
    // 背景图
    private String bodyBackgroundImage;
    // 是否启用账号密码登陆
    private String loginBoxAccount;
    // 是否启用忘记密码
    private String loginBoxSingleForgetPasswo;
    // 是否启用密码
    private String loginBoxSingleRememberPass;
    // 登录框头部背景图
    private String loginBoxHeaderImage;
    // 页脚宽度
    private Integer footerWidth;

    // 是否启用KEY登陆
    private String loginBoxKey;
    // KEY登陆描述
    private String loginBoxKeyDescription;

    private String enableOauth;//启用统一认证登录
    private String enableOauthLogin;//使用统一认证登录页
    private String enableCustomizeOauthLogin;
    private String customizeOauthLoginUri;//自定义平台的统一认证登录页地址

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
     * @return the loginBoxSingle
     */
    public String getLoginBoxSingle() {
        return this.loginBoxSingle;
    }

    /**
     * @param loginBoxSingle
     */
    public void setLoginBoxSingle(String loginBoxSingle) {
        this.loginBoxSingle = loginBoxSingle;
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
     * @return the loginBoxY
     */
    public Integer getLoginBoxY() {
        return this.loginBoxY;
    }

    /**
     * @param loginBoxY
     */
    public void setLoginBoxY(Integer loginBoxY) {
        this.loginBoxY = loginBoxY;
    }

    /**
     * @return the loginBoxX
     */
    public Integer getLoginBoxX() {
        return this.loginBoxX;
    }

    /**
     * @param loginBoxX
     */
    public void setLoginBoxX(Integer loginBoxX) {
        this.loginBoxX = loginBoxX;
    }

    /**
     * @return the footerTitle
     */
    public String getFooterTitle() {
        return this.footerTitle;
    }

    /**
     * @param footerTitle
     */
    public void setFooterTitle(String footerTitle) {
        this.footerTitle = footerTitle;
    }

    /**
     * @return the loginBoxHeaderHeight
     */
    public Integer getLoginBoxHeaderHeight() {
        return this.loginBoxHeaderHeight;
    }

    /**
     * @param loginBoxHeaderHeight
     */
    public void setLoginBoxHeaderHeight(Integer loginBoxHeaderHeight) {
        this.loginBoxHeaderHeight = loginBoxHeaderHeight;
    }

    /**
     * @return the loginBoxWidth
     */
    public Integer getLoginBoxWidth() {
        return this.loginBoxWidth;
    }

    /**
     * @param loginBoxWidth
     */
    public void setLoginBoxWidth(Integer loginBoxWidth) {
        this.loginBoxWidth = loginBoxWidth;
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
     * @return the loginBoxHeaderImagePath
     */
    public String getLoginBoxHeaderImagePath() {
        return this.loginBoxHeaderImagePath;
    }

    /**
     * @param loginBoxHeaderImagePath
     */
    public void setLoginBoxHeaderImagePath(String loginBoxHeaderImagePath) {
        this.loginBoxHeaderImagePath = loginBoxHeaderImagePath;
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
     * @return the footerHeight
     */
    public Integer getFooterHeight() {
        return this.footerHeight;
    }

    /**
     * @param footerHeight
     */
    public void setFooterHeight(Integer footerHeight) {
        this.footerHeight = footerHeight;
    }

    /**
     * @return the bodyBackgroundImagePath
     */
    public String getBodyBackgroundImagePath() {
        return this.bodyBackgroundImagePath;
    }

    /**
     * @param bodyBackgroundImagePath
     */
    public void setBodyBackgroundImagePath(String bodyBackgroundImagePath) {
        this.bodyBackgroundImagePath = bodyBackgroundImagePath;
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
     * @return the footerY
     */
    public Integer getFooterY() {
        return this.footerY;
    }

    /**
     * @param footerY
     */
    public void setFooterY(Integer footerY) {
        this.footerY = footerY;
    }

    /**
     * @return the loginBoxHeader
     */
    public String getLoginBoxHeader() {
        return this.loginBoxHeader;
    }

    /**
     * @param loginBoxHeader
     */
    public void setLoginBoxHeader(String loginBoxHeader) {
        this.loginBoxHeader = loginBoxHeader;
    }

    /**
     * @return the footerX
     */
    public Integer getFooterX() {
        return this.footerX;
    }

    /**
     * @param footerX
     */
    public void setFooterX(Integer footerX) {
        this.footerX = footerX;
    }

    /**
     * @return the loginBoxSingleCode
     */
    public String getLoginBoxSingleCode() {
        return this.loginBoxSingleCode;
    }

    /**
     * @param loginBoxSingleCode
     */
    public void setLoginBoxSingleCode(String loginBoxSingleCode) {
        this.loginBoxSingleCode = loginBoxSingleCode;
    }

    /**
     * @return the loginBoxBackgroundColor
     */
    public String getLoginBoxBackgroundColor() {
        return this.loginBoxBackgroundColor;
    }

    /**
     * @param loginBoxBackgroundColor
     */
    public void setLoginBoxBackgroundColor(String loginBoxBackgroundColor) {
        this.loginBoxBackgroundColor = loginBoxBackgroundColor;
    }

    /**
     * @return the loginBoxHeight
     */
    public Integer getLoginBoxHeight() {
        return this.loginBoxHeight;
    }

    /**
     * @param loginBoxHeight
     */
    public void setLoginBoxHeight(Integer loginBoxHeight) {
        this.loginBoxHeight = loginBoxHeight;
    }

    /**
     * @return the footerFontColor
     */
    public String getFooterFontColor() {
        return this.footerFontColor;
    }

    /**
     * @param footerFontColor
     */
    public void setFooterFontColor(String footerFontColor) {
        this.footerFontColor = footerFontColor;
    }

    /**
     * @return the modelName
     */
    public String getModelName() {
        return this.modelName;
    }

    /**
     * @param modelName
     */
    public void setModelName(String modelName) {
        this.modelName = modelName;
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
     * @return the isEnable
     */
    public String getIsEnable() {
        return this.isEnable;
    }

    /**
     * @param isEnable
     */
    public void setIsEnable(String isEnable) {
        this.isEnable = isEnable;
    }

    /**
     * @return the loginBoxFontColor
     */
    public String getLoginBoxFontColor() {
        return this.loginBoxFontColor;
    }

    /**
     * @param loginBoxFontColor
     */
    public void setLoginBoxFontColor(String loginBoxFontColor) {
        this.loginBoxFontColor = loginBoxFontColor;
    }

    /**
     * @return the loginBoxZindex
     */
    public Integer getLoginBoxZindex() {
        return this.loginBoxZindex;
    }

    /**
     * @param loginBoxZindex
     */
    public void setLoginBoxZindex(Integer loginBoxZindex) {
        this.loginBoxZindex = loginBoxZindex;
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
     * @return the footerZindex
     */
    public Integer getFooterZindex() {
        return this.footerZindex;
    }

    /**
     * @param footerZindex
     */
    public void setFooterZindex(Integer footerZindex) {
        this.footerZindex = footerZindex;
    }

    /**
     * @return the loginBoxSingleSms
     */
    public String getLoginBoxSingleSms() {
        return this.loginBoxSingleSms;
    }

    /**
     * @param loginBoxSingleSms
     */
    public void setLoginBoxSingleSms(String loginBoxSingleSms) {
        this.loginBoxSingleSms = loginBoxSingleSms;
    }

    /**
     * @return the footerFontSize
     */
    public String getFooterFontSize() {
        return this.footerFontSize;
    }

    /**
     * @param footerFontSize
     */
    public void setFooterFontSize(String footerFontSize) {
        this.footerFontSize = footerFontSize;
    }

    /**
     * @return the isHihdenFooter
     */
    public String getIsHihdenFooter() {
        return this.isHihdenFooter;
    }

    /**
     * @param isHihdenFooter
     */
    public void setIsHihdenFooter(String isHihdenFooter) {
        this.isHihdenFooter = isHihdenFooter;
    }

    /**
     * @return the loginBoxSingleRememberUser
     */
    public String getLoginBoxSingleRememberUser() {
        return this.loginBoxSingleRememberUser;
    }

    /**
     * @param loginBoxSingleRememberUser
     */
    public void setLoginBoxSingleRememberUser(String loginBoxSingleRememberUser) {
        this.loginBoxSingleRememberUser = loginBoxSingleRememberUser;
    }

    /**
     * @return the footerFontContent
     */
    public String getFooterFontContent() {
        return this.footerFontContent;
    }

    /**
     * @param footerFontContent
     */
    public void setFooterFontContent(String footerFontContent) {
        this.footerFontContent = footerFontContent;
    }

    /**
     * @return the bodyBackgroundImage
     */
    public String getBodyBackgroundImage() {
        return this.bodyBackgroundImage;
    }

    /**
     * @param bodyBackgroundImage
     */
    public void setBodyBackgroundImage(String bodyBackgroundImage) {
        this.bodyBackgroundImage = bodyBackgroundImage;
    }

    /**
     * @return the loginBoxAccount
     */
    public String getLoginBoxAccount() {
        return this.loginBoxAccount;
    }

    /**
     * @param loginBoxAccount
     */
    public void setLoginBoxAccount(String loginBoxAccount) {
        this.loginBoxAccount = loginBoxAccount;
    }

    /**
     * @return the loginBoxSingleForgetPasswo
     */
    public String getLoginBoxSingleForgetPasswo() {
        return this.loginBoxSingleForgetPasswo;
    }

    /**
     * @param loginBoxSingleForgetPasswo
     */
    public void setLoginBoxSingleForgetPasswo(String loginBoxSingleForgetPasswo) {
        this.loginBoxSingleForgetPasswo = loginBoxSingleForgetPasswo;
    }

    /**
     * @return the loginBoxSingleRememberPass
     */
    public String getLoginBoxSingleRememberPass() {
        return this.loginBoxSingleRememberPass;
    }

    /**
     * @param loginBoxSingleRememberPass
     */
    public void setLoginBoxSingleRememberPass(String loginBoxSingleRememberPass) {
        this.loginBoxSingleRememberPass = loginBoxSingleRememberPass;
    }

    /**
     * @return the loginBoxHeaderImage
     */
    public String getLoginBoxHeaderImage() {
        return this.loginBoxHeaderImage;
    }

    /**
     * @param loginBoxHeaderImage
     */
    public void setLoginBoxHeaderImage(String loginBoxHeaderImage) {
        this.loginBoxHeaderImage = loginBoxHeaderImage;
    }

    /**
     * @return the footerWidth
     */
    public Integer getFooterWidth() {
        return this.footerWidth;
    }

    /**
     * @param footerWidth
     */
    public void setFooterWidth(Integer footerWidth) {
        this.footerWidth = footerWidth;
    }


    public String getCustomizeOauthLoginUri() {
        return customizeOauthLoginUri;
    }

    public void setCustomizeOauthLoginUri(String customizeOauthLoginUri) {
        this.customizeOauthLoginUri = customizeOauthLoginUri;
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
}
