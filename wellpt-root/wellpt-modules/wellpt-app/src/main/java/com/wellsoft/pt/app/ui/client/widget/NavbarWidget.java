package com.wellsoft.pt.app.ui.client.widget;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.app.service.AppPageResourceService;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.app.ui.WidgetConfiguration;
import com.wellsoft.pt.app.ui.client.DefaultWidgetDefinitionProxyView;
import com.wellsoft.pt.app.ui.client.widget.configuration.NavbarConfiguration;
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
 * Description:
 *
 * @author chenq
 * @date 2020/4/3
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2020/4/3    chenq		2020/4/3		Create
 * </pre>
 */
public class NavbarWidget extends DefaultWidgetDefinitionProxyView {
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
    public NavbarWidget(String widgetDefinition) throws Exception {
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
        JSONObject jsonConfiguration = jsonObject.getJSONObject(CONFIGURATION);
        if (!jsonConfiguration.has(key)) {
            return;
        }
        SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder
                .getBean(SecurityAuditFacadeService.class);
        JSONArray jsonArray = jsonConfiguration.getJSONArray(key);
        List<Map<String, Object>> navs = JsonUtils.json2Object(jsonArray.toString(), List.class);
        if (navs.isEmpty() && jsonConfiguration.has(EVENT_HANDLER_ID)) {
            String appUuid = ObjectUtils.toString(jsonConfiguration.getString(EVENT_HANDLER_ID),
                    StringUtils.EMPTY);
            if (StringUtils.isNotBlank(appUuid)
                    && !securityAuditFacadeService.isGranted(appUuid,
                    AppFunctionType.AppProductIntegration)) {
                jsonConfiguration.put("noGranted", true);
            }
        }
        List<Map<String, Object>> grantedNavs = new ArrayList<Map<String, Object>>();
        filterNavAuthority(navs, grantedNavs);
        jsonConfiguration.put(key, grantedNavs);

    }

    @SuppressWarnings("unchecked")
    private void filterNavAuthority(List<Map<String, Object>> navs,
                                    List<Map<String, Object>> grantedNavs) {
        SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder
                .getBean(SecurityAuditFacadeService.class);
        AppPageResourceService appPageResourceService = ApplicationContextHolder.getBean(
                AppPageResourceService.class);
        for (Map<String, Object> map : navs) {
            Map<String, Object> dataMap = (Map<String, Object>) map.get(DATA);
            if (dataMap != null && dataMap.size() > 0) {
                String appUuid = ObjectUtils.toString(dataMap.get(APP_UUID), StringUtils.EMPTY);
                if (StringUtils.isBlank(appUuid)) {
                    appUuid = ObjectUtils.toString(dataMap.get(EVENT_HANDLER_ID),
                            StringUtils.EMPTY);
                }

                //优先判断导航绑定的事件处理是否有权限，然后再判断导航功能资源是否有参与权限并且有权限
                if (StringUtils.isNotBlank(appUuid)
                        && !securityAuditFacadeService.isGranted(appUuid,
                        AppFunctionType.AppProductIntegration)) {//1.判断事件处理是否有权限
                    continue;
                }

            }
            Map<String, Object> grantedMap = new HashMap<String, Object>();
            grantedMap.putAll(map);
            List<Map<String, Object>> grantedChildren = new ArrayList<Map<String, Object>>();
            grantedMap.put(CHILDREN, grantedChildren);
            List<Map<String, Object>> children = (List<Map<String, Object>>) map.get(CHILDREN);
            boolean hasGranted = true;
            if (children != null && children.size() > 0) {//有子导航的情况下则需要判断子导航的权限情况
                filterNavAuthority(children, grantedChildren);
                if (grantedChildren.isEmpty()) {//所有子导航都没有权限的情况下，父导航也没有权限
                    hasGranted = false;
                }
            }
            if (hasGranted) {
                grantedNavs.add(grantedMap);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.ui.AbstractWidget#getConfiguration()
     */
    @Override
    public WidgetConfiguration getConfiguration() {
        return super.getConfiguration(NavbarConfiguration.class);
    }

}
