package com.wellsoft.pt.ei.constants;

public enum ExportCheckPathEnum {
    OK(0, "校验通过"),
    ISNULL(101, "路径为空"),
    INVALID(102, "路径不合法"),
    PERMISSION_DENIED(103, "没有权限"),
    NOT_EXIST(104, "盘符不存在");

    private Integer code;
    private String message;

    ExportCheckPathEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "{" +
                "'code':" + code +
                ", 'message':'" + message + '\'' +
                '}';
    }
}
