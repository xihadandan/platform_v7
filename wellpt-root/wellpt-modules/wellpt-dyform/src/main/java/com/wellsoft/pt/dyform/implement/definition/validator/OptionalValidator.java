/*
 * @(#)2019年8月16日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.validator;

import java.util.Collection;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月16日.1	zhongzh		2019年8月16日		Create
 * </pre>
 * @date 2019年8月16日
 */
public class OptionalValidator implements Validator {

    /**
     * (non-Javadoc)
     *
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public final ValidatorResult isValid(Object value, ValidatorFieldContent fieldDefinition, Map<String, Object> params) {
        if (optional(value, fieldDefinition, params)) {
            return null;
        }
        return isValidValue(value, fieldDefinition, params);
    }

    protected boolean optional(Object value, ValidatorFieldContent fieldDefinition, Map<String, Object> params) {
        if (value == null) {
            return true;
        } else if (value instanceof Collection && ((Collection) value).isEmpty()) {
            return true;
        } else if ((value.toString()).length() == 0) {
            return true;
        }
        return false;
    }

    protected ValidatorResult isValidValue(Object value, ValidatorFieldContent fieldDefinition,
                                           Map<String, Object> params) {
        return null;
    }
}
