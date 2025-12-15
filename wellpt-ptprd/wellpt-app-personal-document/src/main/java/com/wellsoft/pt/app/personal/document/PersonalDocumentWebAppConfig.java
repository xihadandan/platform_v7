/*
 * @(#)Jan 30, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.personal.document;

import com.wellsoft.pt.app.context.AppContextConfigurerAdapter;
import com.wellsoft.pt.app.context.config.ContainerComponentRegistry;
import com.wellsoft.pt.app.context.config.PropertiesRegistry;
import com.wellsoft.pt.app.design.container.Container;
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
 * Jan 30, 2018.1	zhulh		Jan 30, 2018		Create
 * </pre>
 * @date Jan 30, 2018
 */
@Configuration
public class PersonalDocumentWebAppConfig extends AppContextConfigurerAdapter {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContextConfigurerAdapter#addProperties(com.wellsoft.pt.app.context.config.PropertiesRegistry)
     */
    @Override
    public void addProperties(PropertiesRegistry propertiesRegistry) {
        propertiesRegistry.registerProperties("pt-app-personal-document.properties");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.context.AppContextConfigurerAdapter#addContainerComponent(com.wellsoft.pt.app.context.config.ContainerComponentRegistry)
     */
    @Override
    public void addContainerComponent(ContainerComponentRegistry containerComponentRegistry) {
        // 默认的页面设计器
        Container container = containerComponentRegistry.getDefaultContainer();
        // 添加组件
        // containerComponentRegistry.addComponent(container,
        // ApplicationContextHolder.getBean(PersonalDocumentComponent.class));
    }

}
