package com.wellsoft.context.web.controller;

import com.wellsoft.context.enums.ApiCodeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author yt
 * @title: ApiResponse
 * @date 2020/11/5 10:52
 */
@ApiModel(description = "api接口返回统一类")
public class ApiResult<T> implements Serializable {

    @ApiModelProperty(value = "返回码:0-成功")
    private int code;

    @ApiModelProperty(value = "提示消息")
    private String msg;

    @ApiModelProperty(value = "返回具体对象")
    private T data;

    @ApiModelProperty("是否成功")
    private boolean success;

    public static <T> ApiResult<T> success() {
        return build(ApiCodeEnum.SUCCESS.getCode(), ApiCodeEnum.SUCCESS.getDescription(), null);
    }

    public static <T> ApiResult<T> success(T data) {
        return build(ApiCodeEnum.SUCCESS.getCode(), ApiCodeEnum.SUCCESS.getDescription(), data);
    }

    public static <T> ApiResult<T> fail() {
        return build(ApiCodeEnum.API_SYSTEM_ERROR.getCode(), ApiCodeEnum.API_SYSTEM_ERROR.getDescription(), null);
    }

    public static <T> ApiResult<T> fail(String msg) {
        return build(ApiCodeEnum.API_SYSTEM_ERROR.getCode(), msg, null);
    }

    public static <T> ApiResult<T> fail(T data, String msg) {
        return build(ApiCodeEnum.API_SYSTEM_ERROR.getCode(), msg, data);
    }

    public static <T> ApiResult<T> fail(T data) {
        return build(ApiCodeEnum.API_SYSTEM_ERROR.getCode(), ApiCodeEnum.API_SYSTEM_ERROR.getDescription(), data);
    }

    public static <T> ApiResult<T> build(int code, String msg, T data) {
        ApiResult<T> apiResult = new ApiResult<>();
        apiResult.setCode(code);
        apiResult.setMsg(msg);
        apiResult.setData(data);
        return apiResult;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return this.code == 0;
    }
}
