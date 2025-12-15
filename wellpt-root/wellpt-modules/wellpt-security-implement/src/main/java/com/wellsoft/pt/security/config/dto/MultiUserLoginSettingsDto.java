/*
 * @(#)2021-11-18 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.config.dto;

import com.wellsoft.pt.security.enums.UserLoginSettings;

import java.io.Serializable;


/**
 * Description: 数据库表MULTI_USER_LOGIN_SETTINGS的对应的DTO类
 *
 * @author baozh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-11-18.1	baozh		2021-11-18		Create
 * </pre>
 * @date 2021-11-18
 */
public class MultiUserLoginSettingsDto implements Serializable {

    private static final long serialVersionUID = 1637225044568L;

    //登录类型
    private String loginType;

    //登录类型
    private String loginTypeName;
    //登录类型别名
    private String loginTypeAlias;

    //用户字段
    private String userField;

    //是否启用
    private Integer enable;

    public MultiUserLoginSettingsDto(UserLoginSettings setting) {
        this.loginType = setting.getLoginType();
        this.loginTypeName = setting.getLoginTypeName();
        this.userField = setting.getUserField();
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public String getLoginTypeName() {
        return loginTypeName;
    }

    public void setLoginTypeName(String loginTypeName) {
        this.loginTypeName = loginTypeName;
    }

    public String getLoginTypeAlias() {
        return loginTypeAlias;
    }

    public void setLoginTypeAlias(String loginTypeAlias) {
        this.loginTypeAlias = loginTypeAlias;
    }

    public String getUserField() {
        return userField;
    }

    public void setUserField(String userField) {
        this.userField = userField;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(Integer enable) {
        this.enable = enable;
    }


    @Override
    public String toString() {
        return "{" +
                "loginType='" + loginType + '\'' +
                ", loginTypeName='" + loginTypeName + '\'' +
                ", loginTypeAlias='" + loginTypeAlias + '\'' +
                ", userField='" + userField + '\'' +
                '}';
    }
}
