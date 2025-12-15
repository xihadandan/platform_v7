package com.wellsoft.pt.ei.annotate;

import com.wellsoft.pt.ei.constants.ExportFieldTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author yt
 * @Auther: yt
 * @Date: 2021/9/23 17:31
 * @Description:
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FieldType {


    /**
     * 说明
     *
     * @return
     */
    String desc() default "";

    /**
     * 字典值
     *
     * @return
     */
    String dictValue() default "";

    /**
     * 显示值
     *
     * @return
     */
    String displayValue() default "";

    /**
     * 分组属性
     *
     * @return
     */
    boolean isGroup() default false;

    /**
     * 数据类型
     */
    ExportFieldTypeEnum type() default ExportFieldTypeEnum.STRING;

    /**
     * 是否必填
     *
     * @return
     */
    boolean required() default false;


}
