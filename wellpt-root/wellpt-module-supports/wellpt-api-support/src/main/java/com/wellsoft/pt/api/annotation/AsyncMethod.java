package com.wellsoft.pt.api.annotation;

import java.lang.annotation.*;

/**
 * Description: 修饰方法为异步调用的
 *
 * @author chenq
 * @date 2018/10/30
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/10/30    chenq		2018/10/30		Create
 * </pre>
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AsyncMethod {
}
