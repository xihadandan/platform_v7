/*
 * @(#)2020年12月31日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget.configuration;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年12月31日.1	zhulh		2020年12月31日		Create
 * </pre>
 * @date 2020年12月31日
 */
public class FunctionElementDependenciesConfiguration extends AppWidgetDefinitionConfiguration {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5828770558410064581L;

    private List<FunctionElement> dependencies = Lists.newArrayList();

    /**
     * @return the dependencies
     */
    public List<FunctionElement> getDependencies() {
        return dependencies;
    }

    /**
     * @param dependencies 要设置的dependencies
     */
    public void setDependencies(List<FunctionElement> dependencies) {
        this.dependencies = dependencies;
    }

}
