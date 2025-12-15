/*
 * @(#)2016年11月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support.renderer;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.basicdata.selective.facade.SelectiveDatas;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public abstract class AbstractSelectiveDatasDataStoreRenderer extends AbstractDataStoreRenderer {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer#doRenderData(java.lang.String, java.lang.Object, java.util.Map, com.wellsoft.pt.basicdata.datastore.support.RendererParam)
     */
    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        if (value == null) {
            return StringUtils.EMPTY;
        }
        List<String> values = Arrays.asList(StringUtils.split(value.toString(), Separator.SEMICOLON.getValue()));
        List<String> labels = new ArrayList<String>();
        for (String v : values) {
            labels.add(SelectiveDatas.getLabel(getConfigKey(param), v, v));
        }
        return StringUtils.join(labels, Separator.SEMICOLON.getValue());
    }

    /**
     * @param param
     * @return
     */
    public abstract String getConfigKey(RendererParam param);
}
