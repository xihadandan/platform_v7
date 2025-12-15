/*
 * @(#)2017-02-20 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Description: 操作类注解
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-02-20.1	zhulh		2017-02-20		Create
 * </pre>
 * @date 2017-02-20
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Action {
    String[] value() default {};
}
