/*
 * @(#)Dec 27, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.file.executor;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.dms.file.action.FileAction;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Dec 27, 2017.1	zhulh		Dec 27, 2017		Create
 * </pre>
 * @date Dec 27, 2017
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class FileActionResult<T extends FileAction> extends BaseObject {

    public static final String CODE_SUCCESS = "200";
    public static final String MSG_SUCCESS = "Success!";
    public static final String KEY_CODE = "code";
    public static final String KEY_MSG = "msg";
    public static final String KEY_SUCCESS = "success";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1027193166431493450L;
    private boolean success;

    private String code;

    private String msg;

    private String reson;

    private String details;

    /**
     *
     */
    public FileActionResult() {
        super();
        this.success = true;
        this.code = CODE_SUCCESS;
        this.msg = MSG_SUCCESS;
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
     * @return the reson
     */
    public String getReson() {
        return reson;
    }

    /**
     * @param reson 要设置的reson
     */
    public void setReson(String reson) {
        this.reson = reson;
    }

    /**
     * @return the details
     */
    public String getDetails() {
        return details;
    }

    /**
     * @param details 要设置的details
     */
    public void setDetails(String details) {
        this.details = details;
    }

}
