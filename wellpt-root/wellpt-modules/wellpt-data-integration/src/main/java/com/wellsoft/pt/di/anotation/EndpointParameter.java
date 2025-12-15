package com.wellsoft.pt.di.anotation;

import com.wellsoft.pt.di.enums.DIParameterDomType;
import com.wellsoft.pt.di.enums.EdpParameterType;

import java.lang.annotation.*;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/23
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/23    chenq		2019/7/23		Create
 * </pre>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EndpointParameter {


    String name() default "";

    EdpParameterType type() default EdpParameterType.BOTH;

    String defaultValue() default "";

    DIParameterDomType domType() default DIParameterDomType.INPUT;

    String dataJSON() default "";

    boolean show() default true;


}
