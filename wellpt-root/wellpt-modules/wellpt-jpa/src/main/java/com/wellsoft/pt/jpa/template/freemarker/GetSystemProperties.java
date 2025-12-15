/*
 * @(#)2013-8-2 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.template.freemarker;

import com.wellsoft.context.config.Config;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

import java.util.List;

/**
 * 获取系统properties的值
 */
public class GetSystemProperties implements TemplateMethodModel {

    /**
     * (non-Javadoc)
     *
     * @see freemarker.template.TemplateMethodModel#exec(List)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object exec(List arguments) throws TemplateModelException {
        return Config.getValue(arguments.get(0).toString());
    }

}
