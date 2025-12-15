/*
 * @(#)ResultMessage.java 2012-10-15 1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.web.controller;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 请求结果信息
 *
 * @author zhulh
 * @date 2012-10-15
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-15.1	zhulh		2012-10-15		Create
 * </pre>
 */
@ApiModel("请求结果信息")
public class ResultMessage implements Message {

    @ApiModelProperty("返回码:0-成功")
    private int code;

    @ApiModelProperty("提示消息")
    private StringBuilder msg;

    @ApiModelProperty("返回具体对象")
    private Object data;

    @ApiModelProperty("是否成功")
    private boolean success;

    /**
     *
     */
    public ResultMessage() {
        this("success");
    }

    /**
     * @param message
     */
    public ResultMessage(String message) {
        this(message, true);
    }

    /**
     * @param message
     * @param success
     */
    public ResultMessage(String message, boolean success) {
        this(message, success, null);
    }

    /**
     * @param message
     * @param success
     * @param data
     */
    public ResultMessage(String message, boolean success, Object data) {
        // this(0, message, success, data);
        addMessage(message);
        setSuccess(success);
        setData(data);
    }

    /**
     * @param message
     * @param success
     * @param data
     */
    public ResultMessage(int code, String message, boolean success, Object data) {
        setCode(code);
        addMessage(message);
        setSuccess(success);
        setData(data);
    }

    /**
     * @return the code
     */
    public int getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.context.web.controller.Message#addMessage(java.lang.String)
     */
    @Override
    public void addMessage(String message) {
        if (msg == null || msg.length() == 0) {
            msg = new StringBuilder();
            msg.append(message);
        } else {
            msg.append(System.getProperty("line.separator"));
            msg.append(message);
        }

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.context.web.controller.Message#getData()
     */
    @Override
    public Object getData() {
        return data;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.context.web.controller.Message#setData(java.lang.Object)
     */
    @Override
    public void setData(Object data) {
        this.data = data;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.context.web.controller.Message#isSuccess()
     */
    @Override
    public boolean isSuccess() {
        return success;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.context.web.controller.Message#setSuccess(boolean)
     */
    @Override
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the msg
     */
    public StringBuilder getMsg() {
        return msg;
    }

    /**
     * @param msg the msg to set
     */
    public void setMsg(StringBuilder msg) {
        this.msg = msg;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.Message#clear()
     */
    @Override
    public void clear() {
        if (msg == null) {
            msg = new StringBuilder();
        } else {
            msg.setLength(0);
        }
    }

}
