/*
 * @(#)2018年7月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.template.freemarker;

import freemarker.template.TemplateModel;
import org.springframework.stereotype.Component;


@Component
public class GetSystemPropertiesSharedVariable implements CustomFreemarkerTemplateSharedVariable {
    public static final String TABLE_SCHEMA = "getSystemProperties";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.template.freemarker.CustomSharedVariable#getName()
     */
    @Override
    public String getName() {
        return TABLE_SCHEMA;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.template.freemarker.CustomSharedVariable#getValue()
     */
    @Override
    public TemplateModel getValue() {
        return new GetSystemProperties();
    }

}
