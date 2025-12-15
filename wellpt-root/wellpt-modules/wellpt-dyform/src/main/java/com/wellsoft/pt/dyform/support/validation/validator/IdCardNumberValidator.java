/*
 * @(#)2015-9-28 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.support.validation.validator;

import com.wellsoft.context.util.regex.RegexUtils;
import com.wellsoft.pt.dyform.support.validation.IdCardNumber;
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
 * 2015-9-28.1	zhulh		2015-9-28		Create
 * </pre>
 * @date 2015-9-28
 */
public class IdCardNumberValidator implements ConstraintValidator<IdCardNumber, Object> {

    /**
     * (non-Javadoc)
     *
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(IdCardNumber idCardNumberAnnotation) {
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

        return RegexUtils.isIdCardNumber(value.toString());
    }

}
