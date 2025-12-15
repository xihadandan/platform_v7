package com.wellsoft.pt.api.response;

import com.wellsoft.pt.api.WellptResponse;

/**
 * Description: 获得组织架构相应类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-19.1  zhengky	2014-8-19	  Create
 * </pre>
 * @date 2014-8-19
 */
public class OrgGetResponse extends WellptResponse {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -507458900251831372L;

    private String code;
    private String msg;
    private boolean success;
    private Object data;

    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code the code to set
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
     * @param msg the msg to set
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return the success
     */
    public boolean getSuccess() {
        return success;
    }

    /**
     * @param success the success to set
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
