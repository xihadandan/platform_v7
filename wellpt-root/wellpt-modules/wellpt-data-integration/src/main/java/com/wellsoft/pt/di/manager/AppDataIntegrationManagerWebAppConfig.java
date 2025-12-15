/*
 * @(#)2019年5月29日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.di.manager;

import com.wellsoft.pt.app.context.AppContextConfigurerAdapter;
import com.wellsoft.pt.app.context.config.PropertiesRegistry;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AppDataIntegrationManagerWebAppConfig extends AppContextConfigurerAdapter {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContextConfigurerAdapter#addProperties(com.wellsoft.pt.app.context.config.PropertiesRegistry)
     */
    @Override
    public void addProperties(PropertiesRegistry propertiesRegistry) {
        propertiesRegistry.registerProperties("pt-di-manager.properties");
    }

}
