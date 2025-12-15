/*
 * @(#)Dec 6, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.dispatcher.ext;

import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.app.ui.client.dispatcher.AppFunctionDispatcherHandlerScriptMapping;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Dec 6, 2017.1	zhulh		Dec 6, 2017		Create
 * </pre>
 * @date Dec 6, 2017
 */
@Component
public class AppWidgetDefinitionAppFunctionDispatcherHandlerScriptMapping implements
        AppFunctionDispatcherHandlerScriptMapping {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.client.dispatcher.AppFunctionDispatcherHandlerScriptMapping#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.AppWidgetDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.client.dispatcher.AppFunctionDispatcherHandlerScriptMapping#getHandlerFunction()
     */
    @Override
    public String getHandlerFunction() {
        StringBuilder sb = new StringBuilder();
        sb.append("function(options, appFunction) {");
        sb.append("}");
        return sb.toString();
    }

}
