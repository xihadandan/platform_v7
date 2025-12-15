/*
 * @(#)Apr 3, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support.renderer;

import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
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
 * Apr 3, 2018.1	zhulh		Apr 3, 2018		Create
 * </pre>
 * @date Apr 3, 2018
 */
@Component
public class PercentageDataStoreRenderer extends AbstractDataStoreRenderer {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getType()
     */
    @Override
    public String getType() {
        return "percentageDataStoreRenderer";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getName()
     */
    @Override
    public String getName() {
        return "百分比渲染器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer#doRenderData(java.lang.String, java.lang.Object, java.util.Map, com.wellsoft.pt.basicdata.datastore.support.RendererParam)
     */
    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        if (value == null) {
            return "0.0%";
        }
        Double doubleValues = Double.valueOf(value.toString());
        return (doubleValues * 100) + "%";
    }

}
