/*
 * @(#)2016-12-06 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.support;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.context.AppContextHolder;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.design.container.Container;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Description: 页面容器包含的组件配置
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-12-06.1	zhulh		2016-12-06		Create
 * </pre>
 * @date 2016-12-06
 */
public class ContainerComponentConfiguration implements InitializingBean {

    private final Map<String, List<String>> containerComponentMap = Maps.newConcurrentMap();
    private final Map<String, String> componentMap = Maps.newConcurrentMap();

    /**
     * @return the componentMap
     */
    public Map<String, String> getComponentMap() {
        return componentMap;
    }


    /**
     * @param containerWtype
     * @return
     */
    public List<UIDesignComponent> getComponents(String containerWtype) {
        List<UIDesignComponent> components = new ArrayList<UIDesignComponent>();
        if (!containerComponentMap.containsKey(containerWtype)) {
            return components;
        }
        String[] wtypes = containerComponentMap.get(containerWtype).toArray(new String[0]);
        for (String wtype : wtypes) {
            UIDesignComponent component = AppContextHolder.getContext().getComponent(
                    StringUtils.trim(wtype));
            if (component == null) {
                continue;
            }
            components.add(component);
        }
        return components;
    }

    /**
     * @param containerComponentMap
     */
    public void addContainerComponent(Map<Container, Set<UIDesignComponent>> map) {
        for (Entry<Container, Set<UIDesignComponent>> entry : map.entrySet()) {
            Container container = entry.getKey();
            Set<UIDesignComponent> components = entry.getValue();
            String containerType = container.getType();
            if (!containerComponentMap.containsKey(containerType)) {
                containerComponentMap.put(containerType, new ArrayList<String>());
            }
            List<String> componentTypes = containerComponentMap.get(containerType);
            for (UIDesignComponent uiDesignComponent : components) {
                if (!componentTypes.contains(uiDesignComponent.getType())) {
                    componentTypes.add(uiDesignComponent.getType());
                }
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        for (Entry<String, String> entry : componentMap.entrySet()) {
            String containerType = entry.getKey();
            String componentWtype = entry.getValue();
            if (StringUtils.isBlank(containerType)) {
                continue;
            }
            String[] wtypes = StringUtils.split(componentWtype, Separator.COMMA.getValue());
            for (String wtype : wtypes) {
                if (StringUtils.isBlank(componentWtype)) {
                    continue;
                }
                if (!containerComponentMap.containsKey(containerType)) {
                    containerComponentMap.put(containerType, new ArrayList<String>());
                }
                containerComponentMap.get(containerType).add(wtype);
            }
        }
    }

}
