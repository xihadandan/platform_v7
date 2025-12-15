package com.wellsoft.pt.dyform.implement.definition.exceptions;

import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.exception.JsonDataException;

public class FormDefinitionUpdateException extends JsonDataException implements IDyformDefinitionException {

    public FormDefinitionUpdateException(String msg) {
        super(msg);
    }

    @Override
    public Object getData() {
        return this.getMessage();
    }

    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.FormDefinitionUpdateException;
    }

}
