/*
 * @(#)2015-9-29 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

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
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = MaxLengthValidator.class)
@Documented
public @interface MaxLength {

    int max() default Integer.MAX_VALUE;

    // 长度不能超过最大值{max}
    String message() default "{global.validator.MaxLength.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
