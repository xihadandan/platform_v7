package com.wellsoft.pt.basicdata.datastore.support;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataStoreInterfaceField {


    String name() default "";

    String defaultValue() default "";

    DataStoreInterfaceFieldElement domType() default DataStoreInterfaceFieldElement.INPUT;

    String dataJSON() default "";

    String service() default ""; //xxxxxService.yyyMethod

    String placeholder() default "";

}
