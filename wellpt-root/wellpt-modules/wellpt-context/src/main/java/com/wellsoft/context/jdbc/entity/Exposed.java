package com.wellsoft.context.jdbc.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 注解在实体类上，表示该实体类对外暴露
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Exposed {

    String name() default ""; // 定义名称

}
