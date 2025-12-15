/*
 * @(#)2015-9-29 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.validator;

import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-9-29.1	zhulh		2015-9-29		Create
 * </pre>
 * @date 2015-9-29
 */
public class MaxLengthValidator implements ConstraintValidator<MaxLength, Object> {

    private int length = 0;

    /**
     * (non-Javadoc)
     *
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(MaxLength maxLengthAnnotation) {
        length = maxLengthAnnotation.max();
    }

    /**
     * (non-Javadoc)
     *
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return true;
        }

        return value.toString().length() <= length;
    }

}
