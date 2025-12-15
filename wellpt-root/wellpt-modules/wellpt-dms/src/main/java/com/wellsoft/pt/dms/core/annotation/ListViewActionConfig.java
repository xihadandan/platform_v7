/*
 * @(#)2017-02-21 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.core.annotation;

import java.lang.annotation.*;

/**
 * Description: 视图列表操作配置信息
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-02-21.1	zhulh		2017-02-21		Create
 * </pre>
 * @date 2017-02-21
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ActionConfig
public @interface ListViewActionConfig {
    // 操作名称
    String name() default "";

    // 为空自动生成，唯一值
    String id() default "";

    // 操作执行的前台JS模块，为空执行后台操作方法
    String executeJsModule() default "";

    // 单选
    boolean singleSelect() default false;

    // 单选提示
    String singleSelectPromptMsg() default "只能选择一条记录！";

    // 操作提示信息，为空不提示
    String promptMsg() default "请选择操作记录！";

    // 确认前执行的操作ID，为空不执行
    String beforeConfirmAction() default "";

    // 操作确认信息，为空不确认
    String confirmMsg() default "";
}
