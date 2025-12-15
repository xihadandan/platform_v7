/*
 * @(#)2016-09-18 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.bean;

import com.wellsoft.pt.app.entity.AppWidgetDefinition;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-18.1	zhulh		2016-09-18		Create
 * </pre>
 * @date 2016-09-18
 */
public class AppWidgetDefinitionBean extends AppWidgetDefinition {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1474182004552L;

    // requirejs组件依赖配置脚本
    private String configScript;
    private String[] requireJavaScriptModules;

    /**
     * @return the configScript
     */
    public String getConfigScript() {
        return configScript;
    }

    /**
     * @param configScript 要设置的configScript
     */
    public void setConfigScript(String configScript) {
        this.configScript = configScript;
    }

    public String[] getRequireJavaScriptModules() {
        return requireJavaScriptModules;
    }

    public void setRequireJavaScriptModules(String[] requireJavaScriptModules) {
        this.requireJavaScriptModules = requireJavaScriptModules;
    }

}
