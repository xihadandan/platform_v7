package com.wellsoft.pt.org.enums;

public enum EnumLoginNameUniqueScope {
    UNIQUE_IN_GLOBAL("-1", "登录名全局性唯一"), UNIQUE_IN_TENANT("0", "登录名租户内唯一");
    private String value = "";
    private String remark;

    private EnumLoginNameUniqueScope(String value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    public static EnumLoginNameUniqueScope value2EnumObj(String value) {
        EnumLoginNameUniqueScope enumObj = null;
        for (EnumLoginNameUniqueScope status : EnumLoginNameUniqueScope.values()) {
            if (status.getValue().equals(value)) {
                enumObj = status;
            }
        }
        return enumObj;
    }

    public String getValue() {
        return value;
    }

    public String getRemark() {
        return remark;
    }
}