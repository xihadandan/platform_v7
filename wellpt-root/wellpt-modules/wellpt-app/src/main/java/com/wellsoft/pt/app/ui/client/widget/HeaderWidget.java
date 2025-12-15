/*
 * @(#)2016-09-12 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.app.ui.WidgetConfiguration;
import com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView;
import com.wellsoft.pt.app.ui.client.widget.configuration.HeaderConfiguration;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 头部组件定义对应的后台Java实例化对象
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-12.1	zhulh		2016-09-12		Create
 * </pre>
 * @date 2016-09-12
 */
public class HeaderWidget extends DefaultWidgetDefinitionProxyView {

    /**
     * 如何描述Visibile
     */
    private static final String CONFIGURATION = "configuration";
    private static final String MAIN_NAV = "mainNav";
    private static final String SUB_NAV = "subNav";
    private static final String TOOL_BAR = "toolBar";
    private static final String MENU_ITEMS = "menuItems";
    private static final String EVENT_HANDLER = "eventHandler";
    private static final String HIDDEN = "hidden";
    private static final String VALUE_VISIBILE = "0";

    /**
     * @param widgetDefinition
     * @throws Exception
     */
    public HeaderWidget(String widgetDefinition) throws Exception {
        super(widgetDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @throws JSONException
     * @see com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView#getDefinitionJson()
     */
    @Override
    public String getDefinitionJson() throws Exception {
        // 导航项MAIN_NAV权限过滤
        this.filterAuthority(MAIN_NAV);
        // 导航项SUB_NAV权限过滤
        this.filterAuthority(SUB_NAV);
        // 工具条TOOL_BAR权限过滤
        this.filterAuthority(TOOL_BAR);
        return this.jsonObject.toString();
    }

    /**
     * 过滤权限
     *
     * @param key
     * @throws Exception
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void filterAuthority(String key) throws Exception {
        if (!this.jsonObject.has(CONFIGURATION)) {
            return;
        }
        JSONObject configuration = jsonObject.getJSONObject(CONFIGURATION);
        if (!configuration.has(key)) {
            return;
        }
        JSONObject navInfo = configuration.getJSONObject(key);
        if (!navInfo.has(MENU_ITEMS)) {
            return;
        }
        JSONArray jsonArray = navInfo.getJSONArray(MENU_ITEMS);
        List<Map<String, Object>> navs = JsonUtils.json2Object(jsonArray.toString(), List.class);
        List<Map<String, Object>> grantedNavs = new ArrayList<Map<String, Object>>();

        for (Map<String, Object> map : navs) {
            // 是否显示，如果显示先不进行权限判断
            Object hidden = map.get(HIDDEN);
            if (hidden != null && hidden.toString().equals(VALUE_VISIBILE)) {
                grantedNavs.add(map);
                continue;
            }

            Object eventHandler = map.get(EVENT_HANDLER);
            if (StringUtils.isBlank(ObjectUtils.toString(eventHandler, StringUtils.EMPTY))) {
                grantedNavs.add(map);
                continue;
            }
            // 旧版本定义JSON的判断
            if (ApplicationContextHolder.getBean(SecurityAuditFacadeService.class).isGranted(eventHandler,
                    AppFunctionType.AppProductIntegration)) {
                grantedNavs.add(map);
                continue;
            }
            // 新版本定义JSON的判断
            if (eventHandler instanceof Map) {
                Map<?, ?> eventHandlerMap = (Map) eventHandler;
                if (ApplicationContextHolder.getBean(SecurityAuditFacadeService.class).isGranted(
                        eventHandlerMap.get(AppConstants.KEY_ID), AppFunctionType.AppProductIntegration)) {
                    grantedNavs.add(map);
                }
            }
        }
        navInfo.remove(MENU_ITEMS);
        navInfo.put(MENU_ITEMS, new JSONArray(grantedNavs));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidget#getConfiguration()
     */
    @Override
    public WidgetConfiguration getConfiguration() {
        return super.getConfiguration(HeaderConfiguration.class);
    }

}
