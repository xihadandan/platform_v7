/*
 * @(#)2016年2月24日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
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
 * 2016年2月24日.1	zhulh		2016年2月24日		Create
 * </pre>
 * @date 2016年2月24日
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = RemoteUniqueValidator.class)
@Documented
public @interface RemoteUnique {

    // 该字段值已经存在!
    String message() default "{global.validator.RemoteUnique.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
