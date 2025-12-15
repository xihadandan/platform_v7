package com.wellsoft.pt.api.exception;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/10    chenq		2018/8/10		Create
 * </pre>
 */
public class ApiBusinessException extends RuntimeException {

    @Min(-10000)
    @Max(-7000)
    private int errorCode;//业务系统的错误编码范围：-7000~-10000

    private String errorMsg;//业务反馈异常信息


    public ApiBusinessException(int errorCode, String errorMsg) {
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
