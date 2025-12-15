/*
 * @(#)2016年11月7日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget;

import com.wellsoft.pt.app.ui.WidgetConfiguration;
import com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView;
import com.wellsoft.pt.app.ui.client.widget.configuration.DyformDataViewerConfiguration;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年11月7日.1	xiem		2016年11月7日		Create
 * </pre>
 * @date 2016年11月7日
 */
public class DyformDataViewerWidget extends DefaultWidgetDefinitionProxyView {

    /**
     * @param widgetDefinition
     * @throws Exception
     */
    public DyformDataViewerWidget(String widgetDefinition) throws Exception {
        super(widgetDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidget#getConfiguration()
     */
    @Override
    public WidgetConfiguration getConfiguration() {
        return super.getConfiguration(DyformDataViewerConfiguration.class);
    }

}
