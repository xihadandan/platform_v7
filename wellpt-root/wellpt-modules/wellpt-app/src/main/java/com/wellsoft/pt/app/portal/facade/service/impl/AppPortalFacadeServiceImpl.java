/*
 * @(#)2019年7月5日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.portal.facade.service.impl;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.context.AppContext;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.facade.service.AppPageDefinitionMgr;
import com.wellsoft.pt.app.portal.facade.service.AppPortalFacadeService;
import com.wellsoft.pt.app.service.AppPageDefinitionService;
import com.wellsoft.pt.app.service.AppPageResourceService;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.app.service.AppWidgetDefinitionService;
import com.wellsoft.pt.app.support.*;
import com.wellsoft.pt.app.ui.ModifiableWidgetDefinitionView;
import com.wellsoft.pt.app.ui.View;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年7月5日.1	zhulh		2019年7月5日		Create
 * </pre>
 * @date 2019年7月5日
 */
@Service
public class AppPortalFacadeServiceImpl implements AppPortalFacadeService {

    private static final String PORTAL = "portal";
    private static final String APPS = "apps";
    private static final String ID = "id";
    private static final String EVENT_HANDLER = "eventHandler";
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private AppContext appContext;

    @Autowired
    private AppPageDefinitionService appPageDefinitionService;

    @Autowired
    private AppWidgetDefinitionService appWidgetDefinitionService;

    @Autowired
    private AppPageDefinitionMgr appPageDefinitionMgr;

    @Autowired
    private AppProductIntegrationService appProductIntegrationService;

    @Autowired
    private AppPageResourceService appPageResourceService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.portal.service.AppPortalFacadeService#copyAppWidgetDefinitionByPiUud(java.lang.String)
     */
    @Override
    public AppWidgetDefinition copyAppWidgetDefinitionByPiUud(String piUuid) {
        AppWidgetDefinition returnAppWidgetDefinition = new AppWidgetDefinition();
        try {
            PiFunction piFunction = appContext.getFunction(piUuid);
            String appWidgetDefId = piFunction.getId();
            AppWidgetDefinition appWidgetDefinition = AppCacheUtils.getAppWidgetDefinitionById(appWidgetDefId);
            // 复制组件定义
            ModifiableWidgetDefinitionView modifiableWidgetDefinitionView = WidgetDefinitionUtils.copyWidget(
                    appWidgetDefinition.getDefinitionJson(), appWidgetDefinition.getHtml());
            // 设置返回的组件信息
            BeanUtils.copyProperties(appWidgetDefinition, returnAppWidgetDefinition);
            returnAppWidgetDefinition.setHtml(modifiableWidgetDefinitionView.getAttribute(AppConstants.KEY_HTML));
            modifiableWidgetDefinitionView.removeAttribute(AppConstants.KEY_HTML);
            returnAppWidgetDefinition.setDefinitionJson(modifiableWidgetDefinitionView.getDefinitionJson());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return returnAppWidgetDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.portal.facade.service.AppPortalFacadeService#getAppPageDefinitionByPageUuid(java.lang.String)
     */
    @SuppressWarnings("unchecked")
    @Override
    public AppPageDefinition getAppPageDefinitionByPageUuid(String pageUuid) {
        AppPageDefinition appPageDefinition = appPageDefinitionService.get(pageUuid);
        String id = appPageDefinition.getId();
        String wtype = appPageDefinition.getWtype();
        String title = appPageDefinition.getTitle();
        String theme = appPageDefinition.getTheme();
        String jsModule = StringUtils.EMPTY;
        String definitionJson = appPageDefinition.getDefinitionJson();
        Class<?> viewClass = appContext.getComponent(wtype).getViewClass();
        Class<?>[] parameterTypes = new Class[]{String.class, String.class, String.class, String.class, String.class};
        try {
            Constructor<View> constructor = (Constructor<View>) viewClass.getConstructor(parameterTypes);
            View pageView = constructor.newInstance(id, title, theme, jsModule, definitionJson);
            // 权限过滤页面定义的组件功能元素
            definitionJson = pageView.getDefinitionJson();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        AppPageDefinition returnAppPageDefinition = new AppPageDefinition();
        BeanUtils.copyProperties(appPageDefinition, returnAppPageDefinition);
        returnAppPageDefinition.setDefinitionJson(definitionJson);
        return returnAppPageDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.portal.facade.service.AppPortalFacadeService#isWidgetReferencedById(java.lang.String)
     */
    @Override
    public boolean isWidgetReferencedById(String widgtId) {
        return appWidgetDefinitionService.isWidgetReferencedById(widgtId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.portal.facade.service.AppPortalFacadeService#getPagePortalInfoByPageUuid(java.lang.String)
     */
    @Override
    public String getPagePortalInfoByPageUuid(String pageUuid) {
        String portalInfo = AppConstants.JSON_EMPTY;
        AppPageDefinition appPageDefinition = appPageDefinitionService.get(pageUuid);
        try {
            JSONObject json = new JSONObject(appPageDefinition.getDefinitionJson());
            if (json.has(AppConstants.KEY_CONFIGURATION)) {
                JSONObject configurationJson = json.getJSONObject(AppConstants.KEY_CONFIGURATION);
                if (configurationJson.has(PORTAL)) {
                    return filterPortalAppPrivilege(configurationJson.getJSONObject(PORTAL)).toString();
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return portalInfo;
    }

    /**
     * 过滤门户应用权限
     *
     * @param portalJsonObject
     * @return
     * @throws JSONException
     */
    private JSONObject filterPortalAppPrivilege(JSONObject portalJsonObject) throws JSONException {
        if (!portalJsonObject.has(APPS)) {
            return portalJsonObject;
        }
        SecurityAuditFacadeService securityAuditFacadeService = ApplicationContextHolder
                .getBean(SecurityAuditFacadeService.class);
        JSONArray apps = portalJsonObject.getJSONArray(APPS);
        JSONArray newApps = new JSONArray();
        for (int index = 0; index < apps.length(); index++) {
            JSONObject app = apps.getJSONObject(index);
            if (app.has(EVENT_HANDLER) && (app.get(EVENT_HANDLER) instanceof JSONObject)
                    && app.getJSONObject(EVENT_HANDLER).has(ID)) {
                String eventHandlerId = app.getJSONObject(EVENT_HANDLER).optString(ID, StringUtils.EMPTY);
                if (securityAuditFacadeService.isGranted(eventHandlerId, AppFunctionType.AppProductIntegration)) {
                    newApps.put(app);
                }
            }
        }
        portalJsonObject.remove(APPS);
        portalJsonObject.put(APPS, newApps);
        return portalJsonObject;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.portal.facade.service.AppPortalFacadeService#saveUserPageDefinitionJson(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String saveUserPageDefinitionJson(String sourcePageUuid, String name, String theme, String definitionJson) {
        return appPageDefinitionMgr.saveUserPageDefinitionJson(sourcePageUuid, name, theme, definitionJson);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.facade.service.AppContextService#getUserAppPageDefinitionByPiUuid(java.lang.String)
     */
    @Override
    public List<AppPageDefinition> getUserAppPageDefinitionByPiUuid(String piUuid) {
        List<AppPageDefinition> appPageDefinitions = appProductIntegrationService
                .getAllAvailableAppPageInfoByUserIdAndAppPiUuid(SpringSecurityUtils.getCurrentUserId(), piUuid);
        return appPageDefinitions;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.portal.facade.service.AppPortalFacadeService#getPortalCorrelativePageUuidByPageUuid(java.lang.String)
     */
    @Override
    public String getPortalCorrelativePageUuidByPageUuid(String pageUuid) {
        AppPageDefinition appPageDefinition = appPageDefinitionService.get(pageUuid);
        return StringUtils.isBlank(appPageDefinition.getCorrelativePageUuid()) ? pageUuid : appPageDefinition
                .getCorrelativePageUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.portal.facade.service.AppPortalFacadeService#updateUserDefaultPortalByPageUuid(java.lang.String)
     */
    @Override
    public void updateUserDefaultPortalByPageUuid(String pageUuid) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        appPageDefinitionService.updateUserDefaultPortalByPageUuid(userId, pageUuid);
        AppCacheUtils.clear();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.portal.facade.service.AppPortalFacadeService#deleteUserPortalByPageUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void deleteUserPortalByPageUuid(String pageUuid) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        AppPageDefinition appPageDefinition = appPageDefinitionService.get(pageUuid);
        if (!StringUtils.equals(userId, appPageDefinition.getUserId())) {
            throw new RuntimeException("只能删除自己创建的个人门户！");
        }
        // 删除页面资源
        appPageResourceService.removeByAppPageUuid(pageUuid);
        // 删除页面组件
        appWidgetDefinitionService.removeByAppPageUuid(pageUuid);
        // 删除页面
        appPageDefinitionMgr.remove(pageUuid);
        AppCacheUtils.clear();
    }

}
