/*
 * @(#)2016年11月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support.renderer;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年11月1日.1	xiem		2016年11月1日		Create
 * </pre>
 * @date 2016年11月1日
 */
// @Component
public class DyFormJsonDisplayDataStoreRenderer extends AbstractDataStoreRenderer {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getType()
     */
    @Override
    public String getType() {
        return "dyFormDisplayRenderer";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getName()
     */
    @Override
    public String getName() {
        return "表单JSON渲染器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer#doRenderData(java.lang.String, java.lang.Object, java.util.Map, com.wellsoft.pt.basicdata.datastore.support.RendererParam)
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return StringUtils.EMPTY;
        }

        try {
            String valueStr = value.toString();
            if (!(StringUtils.startsWith(valueStr, "{") && StringUtils.endsWith(valueStr, "}"))) {
                return value;
            }
            Map<String, String> map = JsonUtils.json2Object(valueStr, LinkedHashMap.class);
            if (map == null) {
                return value;
            }
            Iterator<?> it = map.values().iterator();
            StringBuilder sb = new StringBuilder();
            while (it.hasNext()) {
                sb.append(it.next());
                if (it.hasNext()) {
                    sb.append(Separator.SEMICOLON.getValue());
                }
            }
            return sb.toString();
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }

        return StringUtils.EMPTY;
    }
}
