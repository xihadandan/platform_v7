/*
 * @(#)2016年11月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support.renderer;

import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import com.wellsoft.pt.basicdata.favorite.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年7月18日.1	zhongzh		2017年7月18日		Create
 * </pre>
 * @date 2017年7月18日
 */
@Component
public class FavoriteDataStoreRenderer extends AbstractDataStoreRenderer {

    private static final String CLASS_UNFAVORITE = "jquery-ui-favorite";
    private static final String CLASS_FAVORITE = CLASS_UNFAVORITE + " favorite";
    @Autowired
    private FavoriteService favoriteService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getType()
     */
    @Override
    public String getType() {
        return "favoriteRenderer";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getName()
     */
    @Override
    public String getName() {
        return "收藏渲染器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractDataStoreRenderer#doRenderData(java.lang.String, java.lang.Object, java.util.Map, com.wellsoft.pt.basicdata.datastore.support.RendererParam)
     */
    @Override
    public Object doRenderData(String columnIndex, Object value, Map<String, Object> rowData, RendererParam param) {
        String strValue = String.valueOf(value);
        if (favoriteService.isFavorite(strValue)) {
            return "<span class=\"" + CLASS_FAVORITE + "\" data-value=\"" + strValue + "\"/>";
        }
        return "<span class=\"" + CLASS_UNFAVORITE + "\" data-value=\"" + strValue + "\"/>";
    }
}
