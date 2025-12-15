package com.wellsoft.pt.api.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/10
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/10    chenq		2018/8/10		Create
 * </pre>
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ApiComponent {

    String name();
}
