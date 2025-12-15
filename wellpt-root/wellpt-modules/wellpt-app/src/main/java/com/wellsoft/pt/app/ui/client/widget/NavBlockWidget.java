/*
 * @(#)2020年12月14日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget;

import com.wellsoft.pt.app.ui.WidgetConfiguration;
import com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView;
import com.wellsoft.pt.app.ui.client.widget.configuration.NavBlockConfiguration;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年12月14日.1	zhulh		2020年12月14日		Create
 * </pre>
 * @date 2020年12月14日
 */
public class NavBlockWidget extends DefaultWidgetDefinitionProxyView {

    /**
     * @param widgetDefinition
     * @throws Exception
     */
    public NavBlockWidget(String widgetDefinition) throws Exception {
        super(widgetDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidget#getConfiguration()
     */
    @Override
    public WidgetConfiguration getConfiguration() {
        return getConfiguration(NavBlockConfiguration.class);
    }

}
