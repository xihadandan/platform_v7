/*
 * @(#)2020年2月14日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.ext.enumcls.renderer;

import com.wellsoft.context.util.enumtool.EnumClassUtils;
import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年2月14日.1	zhulh		2020年2月14日		Create
 * </pre>
 * @date 2020年2月14日
 */
@Component
public class EnumClassDataStoreRenderer extends AbstractDataStoreRenderer {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getName()
     */
    @Override
    public String getName() {
        return "平台公共功能_枚举类渲染器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getType()
     */
    @Override
    public String getType() {
        return "enumClassRenderer";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer#doRenderData(java.lang.String, java.lang.Object, java.util.Map, com.wellsoft.pt.basicdata.datastore.support.RendererParam)
     */
    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData,
                               RendererParam rendererParam) {
        String className = ObjectUtils.toString(rendererParam.get("enumClass"));
        String valueField = ObjectUtils.toString(rendererParam.get("valueField"));
        String textField = ObjectUtils.toString(rendererParam.get("textField"));
        Object textValue = EnumClassUtils.getTextFieldValueWithValueField(className, valueField, value, textField);
        if (textValue != null) {
            return textValue;
        }
        return value;
    }

}
