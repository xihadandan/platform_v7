/*
 * @(#)2014-8-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.api;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-10.1	zhulh		2014-8-10		Create
 * </pre>
 * @date 2014-8-10
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WellptResponse implements Serializable {

    public static final String KEY_CODE = "code";
    public static final String KEY_MSG = "msg";
    public static final String KEY_SUCCESS = "success";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3108271442654052940L;
    private static final String CODE_SUCCESS = "0";
    private static final String MSG_SUCCESS = "Success!";
    private String code;

    private String msg;

    private boolean success;

    public WellptResponse() {
        this.code = CODE_SUCCESS;
        this.msg = MSG_SUCCESS;
        this.success = true;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg 要设置的msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success 要设置的success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

}
