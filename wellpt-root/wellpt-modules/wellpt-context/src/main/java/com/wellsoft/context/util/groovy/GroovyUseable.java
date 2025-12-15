package com.wellsoft.context.util.groovy;

import java.lang.annotation.*;

/**
 * Description: 注解groovy可调用类
 *
 * @author chenq
 * @date 2018/12/19
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/12/19    chenq		2018/12/19		Create
 * </pre>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GroovyUseable {


}
