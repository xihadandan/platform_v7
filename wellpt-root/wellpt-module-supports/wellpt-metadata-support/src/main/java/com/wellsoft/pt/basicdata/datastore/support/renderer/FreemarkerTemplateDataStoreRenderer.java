/*
 * @(#)May 26, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support.renderer;

import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.jpa.template.freemarker.FreeMarkerTemplateEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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
 * May 26, 2017.1	zhulh		May 26, 2017		Create
 * </pre>
 * @date May 26, 2017
 */
@Component
public class FreemarkerTemplateDataStoreRenderer extends AbstractDataStoreRenderer {

    @Autowired
    private FreeMarkerTemplateEngine freeMarkerTemplateEngine;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getType()
     */
    @Override
    public String getType() {
        return "freemarkerTemplateRenderer";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getName()
     */
    @Override
    public String getName() {
        return "Freemarker模板渲染器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer#doRenderData(java.lang.String, java.lang.Object, java.util.Map, com.wellsoft.pt.basicdata.datastore.support.RendererParam)
     */
    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        Map<String, Object> root = new HashMap<String, Object>();
        root.put("rowData", rowData);
        root.put("columnIndex", columnIndex);
        root.put("value", value);

        if (RequestContextHolder.getRequestAttributes() != null) {
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attr.getRequest();
            root.put("contextPath", request.getContextPath());
        }

        root.putAll(param);
        try {
            return freeMarkerTemplateEngine.process(param.getString("template"), root);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return value;
    }

}
