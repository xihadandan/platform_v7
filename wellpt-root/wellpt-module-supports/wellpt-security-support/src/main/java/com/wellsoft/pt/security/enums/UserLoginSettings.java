package com.wellsoft.pt.security.enums;

public enum UserLoginSettings {
    ACCOUNT("account", "账号名", "loginName"),
    ACCOUNTZH("accountZh", "中文账号", "loginNameZh"),
    NAMEEN("nameEn", "英文名", "englishName"),
    TELL("tell", "手机号", "mobilePhone"),
    IDENTIFIERCODE("identifierCode", "身份证号", "idNumber"),
    EMAIL("email", "邮箱", "mainEmail"),
    EMPCODE("empCode", "员工编号", "employeeNumber");


    //登录类型
    private String loginType;

    //登录类型别名
    private String loginTypeName;

    //用户字段
    private String userField;

    UserLoginSettings(String loginType, String loginTypeName, String userField) {
        this.loginType = loginType;
        this.loginTypeName = loginTypeName;
        this.userField = userField;
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

    public String getUserField() {
        return userField;
    }

    public void setUserField(String userField) {
        this.userField = userField;
    }
}
