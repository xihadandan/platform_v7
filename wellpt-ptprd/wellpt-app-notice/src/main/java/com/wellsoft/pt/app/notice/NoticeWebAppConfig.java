/*
 * @(#)May 22, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.notice;

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
 * May 22, 2017.1	zhulh		May 22, 2017		Create
 * </pre>
 * @date May 22, 2017
 */
@Configuration
public class NoticeWebAppConfig extends AppContextConfigurerAdapter {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContextConfigurerAdapter#addProperties(com.wellsoft.pt.app.context.config.PropertiesRegistry)
     */
    @Override
    public void addProperties(PropertiesRegistry propertiesRegistry) {
        propertiesRegistry.registerProperties("pt-app-notice.properties");
    }

}
