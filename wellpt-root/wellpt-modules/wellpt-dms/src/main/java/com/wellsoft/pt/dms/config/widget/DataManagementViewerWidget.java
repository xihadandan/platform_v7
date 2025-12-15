/*
 * @(#)2019年6月19日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.config.widget;

import com.wellsoft.pt.app.ui.WidgetConfiguration;
import com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView;
import com.wellsoft.pt.dms.config.support.DmsDataManagementViewerConfiguration;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年6月19日.1	zhulh		2019年6月19日		Create
 * </pre>
 * @date 2019年6月19日
 */
public class DataManagementViewerWidget extends DefaultWidgetDefinitionProxyView {

    /**
     * @param widgetDefinition
     * @throws Exception
     */
    public DataManagementViewerWidget(String widgetDefinition) throws Exception {
        super(widgetDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidget#getConfiguration()
     */
    @Override
    public WidgetConfiguration getConfiguration() {
        return getConfiguration(DmsDataManagementViewerConfiguration.class);
    }

}
