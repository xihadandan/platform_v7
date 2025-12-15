/*
 * @(#)2013-8-2 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.template.freemarker;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

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
public class CustomBeanWrapper extends BeansWrapper {
    private final BooleanModel FALSE = new BooleanModel(Boolean.FALSE, this);
    private final BooleanModel TRUE = new BooleanModel(Boolean.TRUE, this);

    /**
     * (non-Javadoc)
     *
     * @see freemarker.ext.beans.BeansWrapper#wrap(java.lang.Object)
     */
    @Override
    public TemplateModel wrap(Object object) throws TemplateModelException {
        if (object instanceof Boolean) {
            return ((Boolean) object).booleanValue() ? TRUE : FALSE;
        } /*else if (object != null && object instanceof String) {
			StringModel strM = new StringModel(object.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;"), this);
			return strM;
		}*/
        return super.wrap(object);
    }

}
