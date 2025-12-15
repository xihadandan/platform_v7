package com.wellsoft.context.enums;

/**
 * Description:api反馈编码
 *
 * @author chenq
 * @date 2018/8/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/10    chenq		2018/8/10		Create
 * </pre>
 */
public enum ApiCodeEnum {

    SUCCESS(0, "成功"),

    GET_REQUEST_DOING(1000, "已收到请求，正在处理"),

    ARGUMENTS_ERROR(-1000, "请求格式错误，通常是参数无效导致"),

    DECODE_SECRET_ERROR(-2000, "加解密失败，通常是秘钥错误或者加解密模式错误导致"),

    SECRET_NOT_FOUND(-2001, "秘钥未设置"),

    VERIFY_TOKEN_ERROR(-3000, "AccessToken校验失败，token未设置或过期等错误导致"),

    REFRESH_TOKEN_ERROR_NOT_EXIST_SYS(-3001, "token刷新异常，无法识别的系统编码"),

    REQUEST_OVER_LIMIT(-4000, "接口访问次数超出限制"),

    REQUEST_OVER_TIME(-4001, "接口超时"),

    REQUEST_OVER_FREQUENCE(-4002, "接口频繁调用异常"),

    API_NOT_FOUND(-5000, "服务或者接口不存在"),

    API_NOT_AUTHROIZE(-5001, "服务或者接口未授权"),

    API_SYSTEM_ERROR(-5002, "系统服务异常"),

    API_RESOURCE_NOT_EXIST(-6000, "资源不存在"),

    RESOURCE_INSUFFICIENT_AUTHENTICATED(-7000, "资源无权限");


    private int code;
    private String description;


    ApiCodeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
