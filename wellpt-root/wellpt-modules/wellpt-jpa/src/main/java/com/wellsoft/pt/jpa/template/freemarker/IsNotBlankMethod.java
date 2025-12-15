/*
 * @(#)2013-8-2 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.template.freemarker;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-8-2.1	zhulh		2013-8-2		Create
 * </pre>
 * @date 2013-8-2
 */
public class IsNotBlankMethod implements TemplateMethodModel {

    /**
     * (non-Javadoc)
     *
     * @see freemarker.template.TemplateMethodModel#exec(java.util.List)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object exec(List arguments) throws TemplateModelException {
        if (arguments == null || arguments.size() != 1) {
            throw new TemplateModelException("the isEmpty method  requires an argument and only.");
        }
        return isNotBlank(arguments.get(0));
    }

    protected Boolean isNotBlank(Object object) {
        if (object == null) {
            return false;
        }
        if (String.class.isAssignableFrom(object.getClass())) {
            return StringUtils.isNotBlank((String) object);
        }
        return true;
    }

}
