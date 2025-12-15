/*
 * @(#)2017年11月24日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
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
 * 2017年11月24日.1	zhongzh		2017年11月24日		Create
 * </pre>
 * @date 2017年11月24日
 */
public class TilesWidget extends DefaultWidgetDefinitionProxyView {

    private static final String CONFIGURATION = "configuration";
    private static final String TILES = "tiles";
    private static final String EVENT_HANDLER_ID = "id";
    private static final String EVENT_HANDLER = "eventHandler";

    /**
     * 如何描述该构造方法
     *
     * @param widgetDefinition
     * @throws Exception
     */
    public TilesWidget(String widgetDefinition) throws Exception {
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
        // 磁贴tiles权限过滤
        filterAuthority(TILES);
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
        if (!jsonObject.has(CONFIGURATION) || !jsonObject.getJSONObject(CONFIGURATION).has(key)) {
            return;
        }
        JSONArray jsonArray = jsonObject.getJSONObject(CONFIGURATION).getJSONArray(key);
        List<Map<String, Object>> tilesNavs = JsonUtils.json2Object(jsonArray.toString(), List.class);
        List<Map<String, Object>> grantedTilesNavs = new ArrayList<Map<String, Object>>();

        filterNavAuthority(tilesNavs, grantedTilesNavs);
        jsonObject.getJSONObject(CONFIGURATION).put(key, grantedTilesNavs);

    }

    @SuppressWarnings("unchecked")
    private void filterNavAuthority(List<Map<String, Object>> tilesNavs, List<Map<String, Object>> grantedTilesNavs) {
        for (Map<String, Object> map : tilesNavs) {
            Map<String, Object> dataMap = (Map<String, Object>) map.get(EVENT_HANDLER);
            if (dataMap != null && !dataMap.isEmpty()) {
                String appUuid = ObjectUtils.toString(dataMap.get(EVENT_HANDLER_ID), StringUtils.EMPTY);
                if (StringUtils.isNotBlank(appUuid)
                        && !ApplicationContextHolder.getBean(SecurityAuditFacadeService.class).isGranted(appUuid,
                        AppFunctionType.AppProductIntegration)) {
                    continue;
                }
            }
            grantedTilesNavs.add(map);
        }
    }

}
