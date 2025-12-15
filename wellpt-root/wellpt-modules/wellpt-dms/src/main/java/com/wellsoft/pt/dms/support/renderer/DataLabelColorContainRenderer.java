/*
 * @(#)Feb 11, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.support.renderer;

import com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractCustomDataStoreRenderer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description: 平台标签颜色渲染器，渲染颜色容器，配合前端二开脚本
 *
 * @author chenq
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * </pre>
 */
@Component
public class DataLabelColorContainRenderer extends AbstractCustomDataStoreRenderer {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getType()
     */
    @Override
    public String getType() {
        return "dataLabelColorRender";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getName()
     */
    @Override
    public String getName() {
        return "数据管理_数据标签_标签颜色容器渲染器";
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractCustomDataStoreRenderer#doRenderData(String, Object, Map, String)
     */
    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData,
                               String exParams) {
        return "<div style='position: absolute;width: 30px;height: 30px;margin-top: -15px;'>" +
                String.format("<input type='hidden' class='rowLabelColor' value='%s' uuid='%s'/>",
                        rowData.get("LABEL_COLOR"), rowData.get("UUID")) +
                "</div>";
    }

}
