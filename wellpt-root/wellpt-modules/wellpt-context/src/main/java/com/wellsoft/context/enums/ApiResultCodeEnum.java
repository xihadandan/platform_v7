package com.wellsoft.context.enums;

/**
 * @author yt
 * @title: ApiResultCodeEnum
 * @date 2020/11/5 14:02
 */
public enum ApiResultCodeEnum {

    SUCCESS(0, "成功"),
    FAIL(-1, "失败");

    private int code;
    private String msg;

    ApiResultCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
