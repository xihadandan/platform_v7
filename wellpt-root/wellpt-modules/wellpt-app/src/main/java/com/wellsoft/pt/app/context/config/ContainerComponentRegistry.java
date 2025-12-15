/*
 * @(#)Feb 14, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.context.config;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.design.container.Container;
import com.wellsoft.pt.app.design.container.DefaultPageContainer;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Feb 14, 2017.1	zhulh		Feb 14, 2017		Create
 * </pre>
 * @date Feb 14, 2017
 */
public class ContainerComponentRegistry {

    private Map<Container, Set<UIDesignComponent>> containerComponentMap = new HashMap<Container, Set<UIDesignComponent>>();

    public ContainerComponentRegistry addComponent(Container container, UIDesignComponent component) {
        if (!containerComponentMap.containsKey(container)) {
            containerComponentMap.put(container, new LinkedHashSet<UIDesignComponent>());
        }
        containerComponentMap.get(container).add(component);
        return this;
    }

    /**
     * @return the containerComponentMap
     */
    public Map<Container, Set<UIDesignComponent>> getContainerComponentMap() {
        return containerComponentMap;
    }

    /**
     * 返回默认的页面容器
     *
     * @return
     */
    public Container getDefaultContainer() {
        return ApplicationContextHolder.getBean(DefaultPageContainer.class);
    }

}
