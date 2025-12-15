package com.wellsoft.oauth2.web.support;

import java.io.Serializable;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/9/19
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/9/19    chenq		2019/9/19		Create
 * </pre>
 */
public class BasicResponse implements Serializable {

    private int code = 0;

    private String msg;

    private Object data;


    public BasicResponse() {
    }

    public static BasicResponse success() {
        return new BasicResponse().setCode(0).setMsg("成功");
    }

    public static BasicResponse fail() {
        return new BasicResponse().setCode(-1).setMsg("失败");
    }

    public int getCode() {
        return code;
    }

    public BasicResponse setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public BasicResponse setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getData() {
        return data;
    }

    public BasicResponse setData(Object data) {
        this.data = data;
        return this;
    }
}
