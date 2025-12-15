package com.wellsoft.pt.common.translate.client;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年03月24日   chenq	 Create
 * </pre>
 */
public class TranslateResponse implements Serializable {

    private String code;

    private String msg;

    private String result;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public TranslateResponse(String code, String msg, String result) {
        this.code = code;
        this.msg = msg;
        this.result = result;
    }

    public TranslateResponse(String result) {
        this.result = result;
    }
}
