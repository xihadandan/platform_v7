/*
 * @(#)2013-8-2 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.support.utils;

import com.wellsoft.pt.jpa.template.freemarker.CustomBeanWrapper;
import freemarker.ext.beans.StringModel;
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
public class CustomXMLPrintTemplateBeanWrapper extends CustomBeanWrapper {

    @Override
    public TemplateModel wrap(Object object) throws TemplateModelException {
        if (object != null && object instanceof String) {
            StringModel strM = new StringModel(object.toString().replaceAll("<", "&lt;").replaceAll(">", "&gt;"), this);
            return strM;
        }

        return super.wrap(object);
    }

}
