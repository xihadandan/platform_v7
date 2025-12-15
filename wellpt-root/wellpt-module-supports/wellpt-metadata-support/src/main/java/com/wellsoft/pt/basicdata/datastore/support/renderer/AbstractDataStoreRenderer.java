/*
 * @(#)2016年11月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support.renderer;

import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public abstract class AbstractDataStoreRenderer implements DataStoreRenderer {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#renderData(java.lang.String, java.lang.Object, java.util.Map, com.wellsoft.pt.basicdata.datastore.support.RendererParam)
     */
    @Override
    public Object renderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        if (!param.containsKey(TYPE_KEY)) {
            return null;
        }
        if (param.getString(TYPE_KEY).equals(getType())) {
            return doRenderData(columnIndex, value, rowData, param);
        }
        return null;
    }

    /**
     * @param columnIndex
     * @param value
     * @param rowData
     * @param param
     * @return
     */
    public abstract Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData,
                                        RendererParam param);
}
