package com.wellsoft.pt.di.response;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/8/14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/8/14    chenq		2019/8/14		Create
 * </pre>
 */
public class Response {

    private int code = 0;

    private String msg;

    private Object responseBody;

    public Response error(int code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }

    public Response success(String msg) {
        this.msg = msg;
        return this;
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

    public Object getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(Object responseBody) {
        this.responseBody = responseBody;
    }
}
