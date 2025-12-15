/*
 * @(#)May 26, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support.renderer;

import com.jayway.jsonpath.JsonPath;
import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import org.springframework.stereotype.Component;

import java.util.Map;

;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 * @date May 26, 2017
 * @see https://github.com/json-path/JsonPath
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * May 26, 2017.1	zhongzh		May 26, 2017		Create
 * </pre>
 */
@Component
public class JsonPathDataStoreRenderer extends AbstractDataStoreRenderer {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getType()
     */
    @Override
    public String getType() {
        return "JsonPathRenderer";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getName()
     */
    @Override
    public String getName() {
        return "JsonPath模板渲染器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer#doRenderData(java.lang.String, java.lang.Object, java.util.Map, com.wellsoft.pt.basicdata.datastore.support.RendererParam)
     */
    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        String jsonpath = null;
        if (value == null || (jsonpath = param.getString("jsonpath")) == null) {
            return value;
        }
        try {
            if (value instanceof String) {
                return JsonPath.read((String) value, jsonpath);
            } else {
                return JsonPath.read(value, jsonpath);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return value;
    }
}
