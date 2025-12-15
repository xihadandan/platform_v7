/*
 * @(#)2019年6月19日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget.configuration;

import com.google.common.collect.Lists;

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
 * 2019年6月19日.1	zhulh		2019年6月19日		Create
 * </pre>
 * @date 2019年6月19日
 */
public class DyformDataViewerConfiguration extends AppWidgetDefinitionConfiguration {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4639644577218405021L;

    private String formUuid;

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidgetConfiguration#getFunctionElements()
     */
    @Override
    public List<FunctionElement> getFunctionElements() {
        List<FunctionElement> functionElements = Lists.newArrayList();
        // 表单定义UUID
        appendRefDyformFunctionElementByUuid(formUuid, functionElements);
        return functionElements;
    }

}
