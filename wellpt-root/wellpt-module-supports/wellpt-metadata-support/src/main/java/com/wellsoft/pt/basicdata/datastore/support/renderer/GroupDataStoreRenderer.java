/*
 * @(#)2016年11月1日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.support.renderer;

import com.wellsoft.pt.basicdata.datastore.support.RendererParam;
import org.springframework.stereotype.Component;

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
@Component
public class GroupDataStoreRenderer extends AbstractSelectiveDatasDataStoreRenderer {
    private static final String SELECTIVE_CONFIG_KEY = "ORG_PUBLIC_GROUP_NAME_ID";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getType()
     */
    @Override
    public String getType() {
        return "groupRenderer";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.DataStoreRenderer#getName()
     */
    @Override
    public String getName() {
        return "群组渲染器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.datastore.support.renderer.AbstractSelectiveDatasDataStoreRenderer#getConfigKey(com.wellsoft.pt.basicdata.datastore.support.RendererParam)
     */
    @Override
    public String getConfigKey(RendererParam param) {
        return SELECTIVE_CONFIG_KEY;
    }

}
