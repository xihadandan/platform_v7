/*
 * @(#)2016年1月29日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.exception;

import com.wellsoft.context.enums.JsonDataErrorCode;
import com.wellsoft.context.util.i18n.MsgUtils;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年1月29日.1	zhulh		2016年1月29日		Create
 * </pre>
 * @date 2016年1月29日
 */
public class FieldValidationException extends JsonDataException {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6079042619689719565L;

    private final MethodParameter parameter;

    private final BindingResult bindingResult;

    /**
     * @param parameter
     * @param bindingResult
     */
    public FieldValidationException(MethodParameter parameter, BeanPropertyBindingResult bindingResult) {
        this.parameter = parameter;
        this.bindingResult = bindingResult;
    }

    /**
     * @param cve
     * @return
     */
    public static Object extractData(ConstraintViolationException cve) {
        List<Map<String, Object>> fieldErrors = new ArrayList<Map<String, Object>>();
        Set<ConstraintViolation<?>> constraintViolations = cve.getConstraintViolations();
        for (ConstraintViolation<?> constraintViolation : constraintViolations) {
            Object field = constraintViolation.getPropertyPath().toString();
            Object value = constraintViolation.getInvalidValue();
            Object msg = constraintViolation.getMessage();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("field", field);
            map.put("value", value);
            map.put("msg", convertMsg(msg));
            fieldErrors.add(map);
        }
        return fieldErrors;
    }

    /**
     * @param msg
     * @return
     */
    private static String convertMsg(Object message) {
        if (message == null) {
            return "";
        }
        String msg = message.toString();
        if (msg.startsWith("{")) {
            msg = msg.substring(1);
        }
        if (msg.endsWith("}")) {
            msg = msg.substring(0, msg.length() - 1);
        }
        msg = MsgUtils.getMessage(msg);
        return msg.toString();
    }

    /**
     * @return the parameter
     */
    public MethodParameter getParameter() {
        return parameter;
    }

    /**
     * @return the bindingResult
     */
    public BindingResult getBindingResult() {
        return bindingResult;
    }

    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder();
        for (ObjectError error : this.bindingResult.getAllErrors()) {
            FieldError fieldError = (FieldError) error;
            sb.append(fieldError.getField() + ": " + convertMsg(fieldError.getDefaultMessage()));
        }
        return sb.toString();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getData()
     */
    @Override
    public Object getData() {
        List<Map<String, Object>> fieldErrors = new ArrayList<Map<String, Object>>();
        for (ObjectError error : this.bindingResult.getAllErrors()) {
            FieldError fieldError = (FieldError) error;
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("field", fieldError.getField());
            map.put("value", fieldError.getRejectedValue());
            map.put("msg", convertMsg(fieldError.getDefaultMessage()));
            fieldErrors.add(map);
        }
        return fieldErrors;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.web.json.JsonDataException#getErrorCode()
     */
    @Override
    public JsonDataErrorCode getErrorCode() {
        return JsonDataErrorCode.FieldValidation;
    }

}
