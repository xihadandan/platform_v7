package com.wellsoft.pt.dyform.implement.data.enums;


public enum EnumDyformExceptionType {
    SQLGRAM("SQLGRAM", "sql语法问题"), DATA_OUT_OF_DATE("DATA_OUT_OF_DATE", "数据过时");
    private String value = "";
    private String remark;

    private EnumDyformExceptionType(String value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    public static EnumDyformExceptionType value2EnumObj(String value) {
        EnumDyformExceptionType enumObj = null;
        for (EnumDyformExceptionType status : EnumDyformExceptionType.values()) {
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
