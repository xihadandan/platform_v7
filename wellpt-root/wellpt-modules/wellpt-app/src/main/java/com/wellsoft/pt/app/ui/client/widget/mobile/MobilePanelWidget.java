/*
 * @(#)2016-09-12 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget.mobile;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.app.ui.Widget;
import com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年10月28日.1	zhongzh		2020年10月28日		Create
 * </pre>
 * @date 2020年10月28日
 */
public class MobilePanelWidget extends DefaultWidgetDefinitionProxyView {

    private static final String CONFIGURATION = "configuration";
    private static final String COLUMNS = "menuItems";
    private static final String APP_UUID = "appUuid";
    private static final String EVENTHANDLER = "eventHandler";
    private static final String EVENT_HANDLER_ID = "id";

    /**
     * @param widgetDefinition
     * @throws Exception
     */
    public MobilePanelWidget(String widgetDefinition) throws Exception {
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
        // 权限过滤
        this.filterAuthority(COLUMNS);
        try {
            List<JSONObject> itemJsons = new ArrayList<JSONObject>();
            for (Widget widget : items) {
                itemJsons.add(new JSONObject(widget.getDefinitionJson()));
            }
            jsonObject.remove(AppConstants.KEY_ITEMS);
            jsonObject.put(AppConstants.KEY_ITEMS, itemJsons);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject.toString();
    }

    /**
     * 过滤权限
     *
     * @param key
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    private void filterAuthority(String key) throws Exception {
        if (!jsonObject.has(CONFIGURATION)) {
            return;
        }
        if (!jsonObject.getJSONObject(CONFIGURATION).has(key)) {
            return;
        }
        JSONArray jsonArray = jsonObject.getJSONObject(CONFIGURATION).getJSONArray(key);
        List<Map<String, Object>> columns = JsonUtils.json2Object(jsonArray.toString(), List.class);
        List<Map<String, Object>> grantedColumns = new ArrayList<Map<String, Object>>();

        filterColumnsAuthority(columns, grantedColumns);
        jsonObject.getJSONObject(CONFIGURATION).put(key, grantedColumns);
        // 左导航权限判断
        String eventHanlderId = jsonObject.getJSONObject(CONFIGURATION).optString("eventHanlderId");
        String eventHanlderPath = jsonObject.getJSONObject(CONFIGURATION).optString("eventHanlderPath");
        if (StringUtils.isNotBlank(eventHanlderId) && StringUtils.isNotBlank(eventHanlderPath)) {
            SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder
                    .getBean(SecurityAuditFacadeService.class);
            if (!securityAuditFacadeService.isGranted(eventHanlderId, AppFunctionType.AppProductIntegration)) {
                jsonObject.getJSONObject(CONFIGURATION).remove("eventHanlderId");
                jsonObject.getJSONObject(CONFIGURATION).remove("eventHanlderPath");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void filterColumnsAuthority(List<Map<String, Object>> tabs, List<Map<String, Object>> grantedColumns) {
        SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder
                .getBean(SecurityAuditFacadeService.class);
        for (Map<String, Object> map : tabs) {
            Object dataObject = map.get(EVENTHANDLER);
            if (dataObject != null && dataObject instanceof Map) {
                Map<String, Object> dataMap = (Map<String, Object>) dataObject;
                if (false == dataMap.isEmpty()) {
                    String appUuid = ObjectUtils.toString(dataMap.get(APP_UUID), StringUtils.EMPTY);
                    if (StringUtils.isBlank(appUuid)) {
                        appUuid = ObjectUtils.toString(dataMap.get(EVENT_HANDLER_ID), StringUtils.EMPTY);
                    }
                    if (StringUtils.isNotBlank(appUuid)
                            && !securityAuditFacadeService.isGranted(appUuid, AppFunctionType.AppProductIntegration)) {
                        continue;
                    }
                }
            }
            Map<String, Object> grantedMap = new HashMap<String, Object>();
            grantedMap.putAll(map);
            grantedColumns.add(grantedMap);
        }
    }
}
