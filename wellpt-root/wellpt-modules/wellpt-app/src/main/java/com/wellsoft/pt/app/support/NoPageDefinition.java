/*
 * @(#)2016年9月9日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.support;

import com.wellsoft.pt.app.entity.AppPageDefinition;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年9月9日.1	zhongzh		2016年9月9日		Create
 * </pre>
 * @date 2016年9月9日
 */
public class NoPageDefinition extends AppPageDefinition {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.entity.AppPageDefinition#getName()
     */
    @Override
    public String getName() {
        return "提示";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.entity.AppPageDefinition#getWtype()
     */
    @Override
    public String getWtype() {
        return "wPage";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.entity.AppPageDefinition#getId()
     */
    @Override
    public String getId() {
        return "INFO";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.entity.AppPageDefinition#getCode()
     */
    @Override
    public String getCode() {
        return "INFO";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.entity.AppPageDefinition#getDefinitionJson()
     */
    @Override
    public String getDefinitionJson() {
        return "{\"id\":\"INFO\",\"title\":\"我的主页\",\"wtype\":\"wPage\",\"items\":[],\"html\":\"<h1>尚未配置或指定默认页面，请先配置页面</h1>\"}";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.entity.AppPageDefinition#getHtml()
     */
    @Override
    public String getHtml() {
        return "<h1>未配置页面</h1>";
    }

}
