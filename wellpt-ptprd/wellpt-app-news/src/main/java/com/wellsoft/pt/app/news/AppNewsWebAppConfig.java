/*
 * @(#)2018年6月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.news;

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
 * 2018年6月26日.1	zhulh		2018年6月26日		Create
 * </pre>
 * @date 2018年6月26日
 */
@Configuration
public class AppNewsWebAppConfig extends AppContextConfigurerAdapter {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContextConfigurerAdapter#addProperties(com.wellsoft.pt.app.context.config.PropertiesRegistry)
     */
    @Override
    public void addProperties(PropertiesRegistry propertiesRegistry) {
        propertiesRegistry.registerProperties("pt-app-news.properties");
    }

}
