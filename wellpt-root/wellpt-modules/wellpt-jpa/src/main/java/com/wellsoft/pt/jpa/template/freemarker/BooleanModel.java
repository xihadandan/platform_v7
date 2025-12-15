/*
 * @(#)2013-8-2 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.template.freemarker;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

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
public class BooleanModel extends freemarker.ext.beans.BooleanModel implements TemplateScalarModel {

    /**
     * 如何描述该构造方法
     *
     * @param bool
     * @param wrapper
     */
    public BooleanModel(Boolean bool, BeansWrapper wrapper) {
        super(bool, wrapper);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see freemarker.template.TemplateScalarModel#getAsString()
     */
    @Override
    public String getAsString() throws TemplateModelException {
        return object.toString();
    }

}
