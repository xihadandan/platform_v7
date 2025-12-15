/*
 * @(#)2016-09-12 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.ui.client.widget.mobile;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class MobileNavWidget extends DefaultWidgetDefinitionProxyView {

    private static final String CONFIGURATION = "configuration";
    private static final String NAV = "nav";
    private static final String DATA = "data";
    private static final String CHILDREN = "children";
    private static final String APP_UUID = "appUuid";
    private static final String EVENT_HANDLER_ID = "eventHanlderId";

    /**
     * @param widgetDefinition
     * @throws Exception
     */
    public MobileNavWidget(String widgetDefinition) throws Exception {
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
        // 导航项NAV权限过滤
        this.filterAuthority(NAV);
        return this.jsonObject.toString();
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
        JSONArray jsonArray = jsonObject.getJSONObject(CONFIGURATION)
                .getJSONArray(key);
        List<Map<String, Object>> navs = JsonUtils.json2Object(
                jsonArray.toString(), List.class);
        List<Map<String, Object>> grantedNavs = new ArrayList<Map<String, Object>>();

        filterNavAuthority(navs, grantedNavs);
        this.jsonObject.getJSONObject(CONFIGURATION).put(key, grantedNavs);

    }

    @SuppressWarnings("unchecked")
    private void filterNavAuthority(List<Map<String, Object>> navs,
                                    List<Map<String, Object>> grantedNavs) {
        SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder
                .getBean(SecurityAuditFacadeService.class);
        for (Map<String, Object> map : navs) {
            Map<String, Object> dataMap = (Map<String, Object>) map.get(DATA);
            if (dataMap != null && dataMap.size() > 0) {
                String appUuid = ObjectUtils.toString(dataMap.get(APP_UUID),
                        StringUtils.EMPTY);
                if (StringUtils.isBlank(appUuid)) {
                    appUuid = ObjectUtils.toString(
                            dataMap.get(EVENT_HANDLER_ID), StringUtils.EMPTY);
                }
                if (StringUtils.isNotBlank(appUuid)
                        && !securityAuditFacadeService.isGranted(appUuid,
                        AppFunctionType.AppProductIntegration)) {
                    continue;
                }
            }
            Map<String, Object> grantedMap = new HashMap<String, Object>();
            grantedMap.putAll(map);
            List<Map<String, Object>> grantedChildren = new ArrayList<Map<String, Object>>();
            grantedMap.put(CHILDREN, grantedChildren);
            grantedNavs.add(grantedMap);
            List<Map<String, Object>> children = (List<Map<String, Object>>) map
                    .get(CHILDREN);
            if (children != null && children.size() > 0) {
                filterNavAuthority(children, grantedChildren);
            }
        }
    }
}
