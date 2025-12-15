/*
 * @(#)2013-5-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.data.exceptions;

import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.exception.JsonDataException;
import com.wellsoft.pt.dyform.implement.data.enums.SaveDataErrorCode;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-17.1	wubin		2013-5-17		Create
 * </pre>
 * @date 2013-5-17
 */
public class SaveDataException extends JsonDataException {

    private static final long serialVersionUID = 1L;

    private SaveDataErrorCode subErrorCode;

    private Map<String, Object> variables;

    public SaveDataException() {

    }

    public SaveDataException(String message) {
        super(message);
    }

    public SaveDataException(Throwable cause) {
        super(cause);
    }

    public SaveDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveDataException(Map<String, Object> variables, Throwable cause) {
        super(cause);
        this.variables = variables;
    }

    public SaveDataException(SaveDataErrorCode subErrorCode, Map<String, Object> variables) {
        this.variables = variables;
        variables.put("subcode", this.subErrorCode = subErrorCode);
    }

    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.SaveData;
    }

    public SaveDataErrorCode getSubErrorCode() {
        return subErrorCode;
    }

    @Override
    public Object getData() {
        return variables;
    }
}
