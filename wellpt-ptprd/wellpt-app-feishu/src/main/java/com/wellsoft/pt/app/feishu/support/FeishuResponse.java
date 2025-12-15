/*
 * @(#)3/19/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.feishu.support;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeishuResponse extends BaseObject {

    private static final long serialVersionUID = -639213058466185366L;
    
    private int code;

    private String msg;

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
}