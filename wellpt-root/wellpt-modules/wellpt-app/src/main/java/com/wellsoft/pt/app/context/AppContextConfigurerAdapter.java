/*
 * @(#)2017-01-19 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context;

import com.wellsoft.pt.app.context.config.ContainerComponentRegistry;
import com.wellsoft.pt.app.context.config.PropertiesRegistry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-01-19.1	zhulh		2017-01-19		Create
 * </pre>
 * @date 2017-01-19
 */
public abstract class AppContextConfigurerAdapter implements AppContextConfigurer {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContextConfigurer#addProperties(java.lang.String)
     */
    @Override
    public void addProperties(PropertiesRegistry propertiesRegistry) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContextConfigurer#addContainerComponent(com.wellsoft.pt.app.context.config.ContainerComponentRegistry)
     */
    @Override
    public void addContainerComponent(ContainerComponentRegistry containerComponentRegistry) {
    }

}
