package com.wellsoft.context.test.groups;

import java.lang.annotation.*;

/**
 * 实现TestNG Groups分组执行用例功能的annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Documented
public @interface Groups {
    /**
     * 执行所有组别的测试.
     */
    String ALL = "all";

    /**
     * 组别定义,默认为ALL.
     */
    String value() default ALL;
}
