/*
 * @(#)May 22, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk;

import com.wellsoft.pt.app.context.AppContextConfigurerAdapter;
import com.wellsoft.pt.app.context.config.PropertiesRegistry;
import org.springframework.context.annotation.Configuration;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * May 5, 2020.1	liut		May 5, 2020		Create
 * </pre>
 * @date May 5, 2020
 */
@Configuration
public class DingtalkWebAppConfig extends AppContextConfigurerAdapter {

    /**
     * (non-Javadoc)
     *
     * @see AppContextConfigurerAdapter#addProperties(PropertiesRegistry)
     */
    @Override
    public void addProperties(PropertiesRegistry propertiesRegistry) {
        // String basePath = "context/config/properties/";
        propertiesRegistry.registerProperties("pt-dingtalk-manager.properties");
    }


}
