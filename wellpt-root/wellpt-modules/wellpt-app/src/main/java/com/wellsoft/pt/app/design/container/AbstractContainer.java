/*
 * @(#)2016年5月11日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.design.container;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.design.component.UIDesignComponent;
import com.wellsoft.pt.app.design.support.ContainerComponentConfiguration;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 页面容器抽象类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年5月11日.1	zhulh		2016年5月11日		Create
 * </pre>
 * @date 2016年5月11日
 */
public abstract class AbstractContainer extends UIDesignComponent implements Container {

    // 页面容器要加载的组件
    protected List<UIDesignComponent> components = new ArrayList<UIDesignComponent>(0);


    /**
     * 返回页面窗口包含的组件
     *
     * @return
     */
    public List<UIDesignComponent> getComponents() {
        if (components.isEmpty()) {
            configureComponents();
        }
        return components;
    }

    /**
     *
     */
    private void configureComponents() {
        ContainerComponentConfiguration componentConfiguration = ApplicationContextHolder
                .getBean(ContainerComponentConfiguration.class);
        components.addAll(componentConfiguration.getComponents(getType()));
    }

    /**
     * 是否支持组件所见即所得
     *
     * @return
     */
    public boolean supportsWysiwyg() {
        return true;
    }

}
