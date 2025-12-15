package com.wellsoft.pt.dwr;

import org.directwebremoting.spring.DwrAnnotationPostProcessor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(DwrAnnotationPostProcessor.class)
public @interface EnableDwrAnnotationConfig {

    String id() default "dwr-annotation-config";
}
