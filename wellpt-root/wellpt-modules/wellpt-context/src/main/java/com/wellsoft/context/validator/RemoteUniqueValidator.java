/*
 * @(#)2016年2月24日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.validator;

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
 * 2016年2月24日.1	zhulh		2016年2月24日		Create
 * </pre>
 * @date 2016年2月24日
 */
public class RemoteUniqueValidator implements ConstraintValidator<RemoteUnique, Object> {

    /**
     * (non-Javadoc)
     *
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(RemoteUnique constraintAnnotation) {
    }

    /**
     * (non-Javadoc)
     *
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        return true;
    }

}
