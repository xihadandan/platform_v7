package com.wellsoft.pt.api.domain;

public class LoginConfig {

    private static final LoginConfig instance = new LoginConfig();
    private String WEIXIN = "";

    private LoginConfig() {

    }

    public static String getWeiXin() {
        return instance.WEIXIN;
    }

    public static void setWeiXin(String weixin) {
        instance.WEIXIN = weixin;
    }
}
