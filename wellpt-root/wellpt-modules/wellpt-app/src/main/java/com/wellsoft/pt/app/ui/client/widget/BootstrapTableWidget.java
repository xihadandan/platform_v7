/*
 * @(#)2016年11月7日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.design.config.BootstrapTableConfiguration;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.app.ui.WidgetConfiguration;
import com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年11月7日.1	xiem		2016年11月7日		Create
 * </pre>
 * @date 2016年11月7日
 */
public class BootstrapTableWidget extends DefaultWidgetDefinitionProxyView {
    private static final String CONFIGURATION = "configuration";
    private static final String BUTTONS = "buttons";
    private static final String RESOURCE = "resource";
    private static final String ID = "id";
    private static final String CODE = "CODE";
    private static final String TYPE = "type";
    private static final String EVENT_HANDLER = "eventHandler";
    private static final String TREE_GRID = "treeGrid";
    private static final String ENABLE_TREE_GRID = "enableTreegrid";
    private static final String CARD_GRID = "cardGrid";
    private static final String ENABLE_CARD_GRID = "enableCardgrid";

    /**
     * 如何描述该构造方法
     *
     * @param widgetDefinition
     * @throws Exception
     */
    public BootstrapTableWidget(String widgetDefinition) throws Exception {
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
        SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder
                .getBean(SecurityAuditFacadeService.class);
        JSONArray buttons = configuration.getJSONArray(BUTTONS);
        JSONArray newButtons = new JSONArray();
        for (int i = 0; i < buttons.length(); i++) {
            JSONObject button = buttons.getJSONObject(i);
            boolean hasCheckpPivilege = false;
            if (button.has(RESOURCE) && button.getJSONObject(RESOURCE).has(ID)) {
                hasCheckpPivilege = true;
                String resourceId = button.getJSONObject(RESOURCE).optString(ID, "");
                if (!securityAuditFacadeService.isGranted(resourceId, AppFunctionType.AppProductIntegration)) {
                    continue;
                }
            }

            if (button.has(EVENT_HANDLER) && button.getJSONObject(EVENT_HANDLER).has(ID)) {
                hasCheckpPivilege = true;
                String eventHandlerId = button.getJSONObject(EVENT_HANDLER).optString(ID, "");
                if (!securityAuditFacadeService.isGranted(eventHandlerId, AppFunctionType.AppProductIntegration)) {
                    continue;
                }
            }

            // 页面功能元素权限判断
            if (!hasCheckpPivilege && button.has(AppConstants.KEY_UUID)) {
                // 组件元素功能UUID
                String functionUuid = DigestUtils.md5Hex(button.getString(AppConstants.KEY_UUID)
                        + getAttribute(AppConstants.KEY_ID));
                if (!securityAuditFacadeService.isGranted(functionUuid, AppFunctionType.AppWidgetFunctionElement)) {
                    continue;
                }
            }
            newButtons.put(button);
        }
        configuration.remove(BUTTONS);
        configuration.put(BUTTONS, newButtons);
        return jsonObject.toString();
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    public boolean isEnableTreegrid() {
        boolean enableTreegrid = false;
        try {
            JSONObject configuration = jsonObject.getJSONObject(CONFIGURATION);
            JSONObject treeGrid = configuration.getJSONObject(TREE_GRID);
            enableTreegrid = treeGrid.getBoolean(ENABLE_TREE_GRID);
        } catch (JSONException e) {
        }
        return enableTreegrid;
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    public boolean isEnableCardgrid() {
        boolean enableCardgrid = false;
        try {
            JSONObject configuration = jsonObject.getJSONObject(CONFIGURATION);
            JSONObject cardGrid = configuration.getJSONObject(CARD_GRID);
            enableCardgrid = cardGrid.getBoolean(ENABLE_CARD_GRID);
        } catch (JSONException e) {
        }
        return enableCardgrid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidget#getConfiguration()
     */
    @Override
    public WidgetConfiguration getConfiguration() {
        return getConfiguration(BootstrapTableConfiguration.class);
    }

}
