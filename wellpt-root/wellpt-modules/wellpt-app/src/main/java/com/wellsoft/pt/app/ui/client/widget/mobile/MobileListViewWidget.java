/*
 * @(#)2016-09-12 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget.mobile;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Description: 左导航组件定义对应的后台Java实例化对象
 *
 * @author wujx
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-10-21.1	wujx		2016-10-21		Create
 * </pre>
 * @date 2016-10-21
 */
public class MobileListViewWidget extends DefaultWidgetDefinitionProxyView {
    private static final String CONFIGURATION = "configuration";
    private static final String BUTTONS = "buttons";
    private static final String RESOURCE = "resource";
    private static final String ID = "id";
    private static final String CODE = "CODE";
    private static final String TYPE = "type";
    private static final String EVENT_HANDLER = "eventHandler";

    /**
     * 如何描述该构造方法
     *
     * @param widgetDefinition
     * @throws Exception
     */
    public MobileListViewWidget(String widgetDefinition) throws Exception {
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
        if (!jsonObject.has(CONFIGURATION) || !jsonObject.getJSONObject(CONFIGURATION).has(BUTTONS)) {
            return this.jsonObject.toString();
        }
        JSONObject configuration = jsonObject.getJSONObject(CONFIGURATION);
        if (!configuration.has(BUTTONS)) {
            return this.jsonObject.toString();
        }
        SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder.getBean(
                SecurityAuditFacadeService.class);
        JSONArray buttons = configuration.getJSONArray(BUTTONS);
        JSONArray newButtons = new JSONArray();
        for (int i = 0; i < buttons.length(); i++) {
            JSONObject button = buttons.getJSONObject(i);
            if (button.has(RESOURCE) && button.getJSONObject(RESOURCE).has(ID)) {
                String resourceId = button.getJSONObject(RESOURCE).optString(ID, "");
                if (!securityAuditFacadeService.isGranted(resourceId, AppFunctionType.AppProductIntegration)) {
                    continue;
                }
            }

            if (button.has(EVENT_HANDLER) && button.getJSONObject(EVENT_HANDLER).has(ID)) {
                String eventHandlerId = button.getJSONObject(EVENT_HANDLER).optString(ID, "");
                if (!securityAuditFacadeService.isGranted(eventHandlerId, AppFunctionType.AppProductIntegration)) {
                    continue;
                }
            }
            newButtons.put(button);
        }
        // configuration.remove(BUTTONS);
        configuration.put(BUTTONS, newButtons);
        return jsonObject.toString();
    }
}
