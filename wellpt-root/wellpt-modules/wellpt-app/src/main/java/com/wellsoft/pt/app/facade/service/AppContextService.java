/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.js.JavaScriptTemplate;
import com.wellsoft.pt.app.support.PiApplication;
import com.wellsoft.pt.app.support.PiFunction;
import com.wellsoft.pt.app.support.PiItem;
import com.wellsoft.pt.app.theme.Theme;
import com.wellsoft.pt.app.ui.Component;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author wujx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-08-19.1	t		2016-08-19		Create
 * </pre>
 * @date 2016-08-19
 */
public interface AppContextService extends BaseService {

    /**
     * 根据wtype获取组件定义对象
     *
     * @param wtype
     * @return
     */
    Component getComponent(String wtype);

    /**
     * 获取所有主题
     *
     * @param
     * @return
     */
    List<Theme> getAllThemes();

    /**
     * 设置页面主题
     *
     * @param themeId
     * @param pageUuid
     */
    void setThemeOfPage(String themeId, String pageUuid);

    /**
     * 根据功能集成信息UUID，获取集成信息
     *
     * @param piUuid
     * @return
     */
    PiItem getPiItemByPiUuid(String piUuid);

    /**
     * 根据功能集成信息路径，获取功能信息
     *
     * @param piPath
     * @return
     */
    PiFunction getFunctionByPath(String piPath);

    /**
     * 根据应用集成信息路径，获取应用信息
     *
     * @param piPath
     * @return
     */
    PiApplication getApplicationByPath(String piPath);

    /**
     * 根据组件定义ID获取定义信息
     *
     * @param appWidgetDefId
     * @param cloneable
     * @return
     */
    AppWidgetDefinition getAppWidgetDefinitionById(String appWidgetDefId, Boolean cloneable);

    /**
     * 根据组件定义ID获取移动端定义信息
     *
     * @param appWidgetDefId
     * @return
     */
    AppWidgetDefinition getUniAppWidgetDefinitionById(String appWidgetDefId);

    /**
     * 根据JS模块返回模块配置信息的脚本
     *
     * @param modules
     * @return
     */
    String getJavaScriptModuleConfigScript(List<String> modules);

    /**
     * 根据模板ID，获取JS模板
     *
     * @param templateId
     * @return
     */
    JavaScriptTemplate getJavaScriptTemplateById(String templateId);

}
