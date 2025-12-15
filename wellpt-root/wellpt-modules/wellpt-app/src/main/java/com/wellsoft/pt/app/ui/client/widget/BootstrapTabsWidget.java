/*
 * @(#)2019年6月19日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.app.ui.WidgetConfiguration;
import com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView;
import com.wellsoft.pt.app.ui.client.widget.configuration.BootstrapTabsConfiguration;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年6月19日.1	zhulh		2019年6月19日		Create
 * </pre>
 * @date 2019年6月19日
 */
public class BootstrapTabsWidget extends DefaultWidgetDefinitionProxyView {

    private static final String TABS = "tabs";

    /**
     * @param widgetDefinition
     * @throws Exception
     */
    public BootstrapTabsWidget(String widgetDefinition) throws Exception {
        super(widgetDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView#getDefinitionJson()
     */
    @Override
    public String getDefinitionJson() throws Exception {
        if (!jsonObject.has(AppConstants.KEY_CONFIGURATION)) {
            return this.jsonObject.toString();
        }
        JSONObject configuration = jsonObject.getJSONObject(AppConstants.KEY_CONFIGURATION);
        if (!configuration.has(TABS)) {
            return this.jsonObject.toString();
        }
        SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder
                .getBean(SecurityAuditFacadeService.class);
        JSONArray tabs = jsonObject.getJSONObject(AppConstants.KEY_CONFIGURATION).getJSONArray(
                TABS);
        JSONArray newTabs = new JSONArray();
        for (int index = 0; index < tabs.length(); index++) {
            JSONObject tab = tabs.getJSONObject(index);
            // 页面功能元素权限判断
            if (tab.has(AppConstants.KEY_UUID)) {
                Object eventHandler = tab.get("eventHandler");
                String functionUuid = null;
                if (eventHandler != null && eventHandler instanceof JSONObject && StringUtils.isNotBlank(
                        ((JSONObject) eventHandler).getString(AppConstants.KEY_ID))) {
                    //校验tab项绑定的事件处理是否权限
                    functionUuid = ((JSONObject) eventHandler).getString(AppConstants.KEY_ID);
                    if (!securityAuditFacadeService.isGranted(functionUuid,
                            AppFunctionType.AppProductIntegration)) {
                        continue;
                    }
                } else {
                    // 组件元素功能UUID
                    functionUuid = DigestUtils.md5Hex(tab.getString(AppConstants.KEY_UUID)
                            + getAttribute(AppConstants.KEY_ID));
                    if (!securityAuditFacadeService.isGranted(functionUuid,
                            AppFunctionType.AppWidgetFunctionElement)) {
                        continue;
                    }
                }

                newTabs.put(tab);
            }
        }
        configuration.remove(TABS);
        configuration.put(TABS, newTabs);
        return jsonObject.toString();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidget#getConfiguration()
     */
    @Override
    public WidgetConfiguration getConfiguration() {
        return getConfiguration(BootstrapTabsConfiguration.class);
    }

}
