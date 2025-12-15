package com.wellsoft.pt.ei.constants;

public enum ExportFieldTypeEnum {
    STRING("string", "字符"),
    DATE("date", "日期"),
    CLOB("clob", "clob"),
    FILE("file", "文件"),
    BOOLEAN("boolean", "布尔型"),
    INTEGER("integer", "整数"),
    LONG("long", "长整数"),
    DOUBLE("double", "双精度浮点数"),
    FLOAT("float", "浮点型"),
    ;

    private String value;
    private String remark;

    ExportFieldTypeEnum(String value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    public static ExportFieldTypeEnum value2EnumObj(String value) {
        if (value == null) {
            return null;
        }
        ExportFieldTypeEnum enumObj = null;
        for (ExportFieldTypeEnum status : ExportFieldTypeEnum.values()) {
            if (status.value.equalsIgnoreCase(value)) {
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
