/*
 * @(#)2013-1-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.jdbc.entity;

import java.lang.annotation.*;

/**
 * Description: 标识公共实体
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-6.1	zhulh		2013-1-6		Create
 * </pre>
 * @date 2013-1-6
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommonEntity {
}
