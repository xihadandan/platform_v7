package com.wellsoft.pt.dyform.implement.data.exceptions;

import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.exception.JsonDataException;
import com.wellsoft.pt.dyform.implement.data.utils.ValidateMsg;

public class FormDataValidateException extends JsonDataException {
    private String msg = "";

    private ValidateMsg validateMsg;

    public FormDataValidateException() {
    }

    public FormDataValidateException(String msg) {
        this.msg = msg;
    }

    public FormDataValidateException(ValidateMsg validateMsg) {
        this.validateMsg = validateMsg;
    }

    @Override
    public Object getData() {
        return validateMsg == null ? this.msg : validateMsg;
    }

    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.FormDataValidateException;
    }

}
