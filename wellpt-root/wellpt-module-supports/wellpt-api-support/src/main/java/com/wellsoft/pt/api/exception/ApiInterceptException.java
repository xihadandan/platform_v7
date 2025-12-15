package com.wellsoft.pt.api.exception;

import com.wellsoft.context.enums.ApiCodeEnum;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/11/2
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/11/2    chenq		2018/11/2		Create
 * </pre>
 */
public class ApiInterceptException extends RuntimeException {

    private int errorCode;//

    private String errorMsg;//

    public ApiInterceptException() {

    }

    public ApiInterceptException(ApiCodeEnum codeEnum) {
        this(codeEnum.getCode(), codeEnum.getDescription());

    }

    public ApiInterceptException(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
