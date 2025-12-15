/*
 * @(#)2014-10-24 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.bean;

import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-24.1	zhulh		2014-10-24		Create
 * </pre>
 * @date 2014-10-24
 */
public class FlowDevelopBean extends FlowDefinition {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8795837527291217247L;
    private String customJsUrl;
    private String customJsModule;

    /**
     * @return the customJsUrl
     */
    public String getCustomJsUrl() {
        return customJsUrl;
    }

    /**
     * @param customJsUrl 要设置的customJsUrl
     */
    public void setCustomJsUrl(String customJsUrl) {
        this.customJsUrl = customJsUrl;
    }

    /**
     * @return the customJsModule
     */
    public String getCustomJsModule() {
        return customJsModule;
    }

    /**
     * @param customJsModule 要设置的customJsModule
     */
    public void setCustomJsModule(String customJsModule) {
        this.customJsModule = customJsModule;
    }

}
