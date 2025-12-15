/*
 * @(#)2018年7月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.template.freemarker;

import freemarker.template.TemplateModel;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年7月26日.1	zhulh		2018年7月26日		Create
 * </pre>
 * @date 2018年7月26日
 */
@Component
public class IsBlankSharedVariable implements CustomFreemarkerTemplateSharedVariable {
    public static final String IS_BLANK_METHOD = "isBlank";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.template.freemarker.CustomSharedVariable#getName()
     */
    @Override
    public String getName() {
        return IS_BLANK_METHOD;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.template.freemarker.CustomSharedVariable#getValue()
     */
    @Override
    public TemplateModel getValue() {
        return new IsBlankMethod();
    }

}
