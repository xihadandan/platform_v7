/*
 * @(#)6/3/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.fulltext.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 6/3/24.1	zhulh		6/3/24		Create
 * </pre>
 * @date 6/3/24
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = {FIELD})
public @interface Pipeline {
    String value() default "attachment";
}
