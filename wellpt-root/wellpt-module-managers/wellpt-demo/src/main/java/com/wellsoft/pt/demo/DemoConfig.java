/*
 * @(#)May 22, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.demo;

import com.wellsoft.pt.app.context.AppContextConfigurerAdapter;
import com.wellsoft.pt.app.context.config.PropertiesRegistry;
import org.springframework.context.annotation.Configuration;

/**
 * Description: js模块加载demo
 *
 * @author hongjz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * May 22, 2017.1	hongjz		May 22, 2017		Create
 * </pre>
 * @date May 22, 2017
 */
@Configuration
public class DemoConfig extends AppContextConfigurerAdapter {

    /**
     * (non-Javadoc)
     *
     * @see AppContextConfigurerAdapter#addProperties(PropertiesRegistry)
     */
    @Override
    public void addProperties(PropertiesRegistry propertiesRegistry) {
        // String basePath = "context/config/properties/";
        propertiesRegistry.registerProperties("pt-demo.properties");
    }


}
