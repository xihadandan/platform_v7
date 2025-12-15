package com.wellsoft.pt.basicdata.sso.bean;

import java.io.Serializable;

public class MsgBean implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6704109149529881468L;
    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
