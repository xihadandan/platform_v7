package com.wellsoft.context.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/1/30
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/1/30    chenq		2019/1/30		Create
 * </pre>
 */
@Target({METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IpLog {

    /**
     * 日志内容
     *
     * @return
     */
    String info();
}
