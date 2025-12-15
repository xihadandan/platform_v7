/*
 * @(#)2016年8月14日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.ModuleID;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.app.context.support.UserAppData;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppSystem;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.service.*;
import com.wellsoft.pt.cache.Cache;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月14日.1	zhulh		2016年8月14日		Create
 * </pre>
 * @date 2016年8月14日
 */
public class AppCacheUtils implements HttpSessionListener {
    private static final Logger LOG = LoggerFactory.getLogger(AppCacheUtils.class);


    private static final String PI_ALL_SYSTEM_EXPIRED_CACHE_KEY = "PI_ALL_SYSTEM_EXPIRED";
    private static final String PI_ALL_MODULE_EXPIRED_CACHE_KEY = "PI_ALL_MODULE_EXPIRED";
    private static final String PI_ALL_APPLICATION_EXPIRED_CACHE_KEY = "PI_ALL_APPLICATION_EXPIRED";
    private static final String PI_ALL_ITEM_EXPIRED_CACHE_KEY = "PI_ALL_ITEM_EXPIRED";
    private static final String PI_ALL_FUNCTION_EXPIRED_CACHE_KEY = "PI_ALL_FUNCTION_EXPIRED";

    // private static final String PI_ALL_SYSTEM_CACHE_KEY = "PI_ALL_SYSTEM";
    // private static final String PI_ALL_MODULE_CACHE_KEY = "PI_ALL_MODULE";
    // private static final String PI_ALL_APPLICATION_CACHE_KEY =
    // "PI_ALL_APPLICATION";
    // private static final String PI_ALL_ITEM_CACHE_KEY = "PI_ALL_ITEM";
    // private static final String PI_SYSTEM_CACHE_KEY_PREFIX = "PI_SYSTEM_";
    // private static final String PI_MODULE_CACHE_KEY_PREFIX = "PI_MODULE_";
    private static final String PI_MODULE_APPS_CACHE_KEY_PREFIX = "PI_MODULE_APPS_";
    // private static final String PI_APPLICATION_CACHE_KEY_PREFIX =
    // "PI_APPLICATION_";
    private static final String PI_APP_FUNCTIONS_CACHE_KEY_PREFIX = "PI_APP_FUNCTIONS_";
    private static final String PI_FUNCTION_CACHE_KEY_PREFIX = "PI_FUNCTION_";
    private static final String PI_SCRIPT_FUNCTION_CACHE_KEY_PREFIX = "PI_SCRIPT_FUNCTION_";
    // private static final String PI_ITEM_CACHE_KEY_PREFIX = "PI_ITEM_";
    private static final String PI_PREF_CACHE_KEY_PREFIX = "PI_PREF";

    private static final String APP_ALL_FUNCTION_CACHE_KEY = "MAP_APP_ALL_FUNCTION";
    private static final String APP_ALL_PI_SYSTEM_CACHE_KEY = "MAP_APP_ALL_PI_SYSTEM";
    private static final String APP_ALL_PI_MODULE_CACHE_KEY = "MAP_APP_ALL_PI_MODULE";
    private static final String APP_ALL_PI_APP_CACHE_KEY = "MAP_APP_ALL_PI_APP";
    private static final String APP_ALL_PI_ITEM_CACHE_KEY = "MAP_APP_ALL_PI_ITEM";

    private static final String APP_SYSTEM_ID_KEY_PREFIX = "APP_SYSTEM_ID_";
    // private static final String APP_FUNCTION_CACHE_KEY_PREFIX =
    // "APP_FUNCTION_";
    private static final String APP_PAGE_CACHE_KEY_PREFIX = "APP_PAGE_";
    private static final String APP_PAGE_IS_CACHED_CACHE_KEY_PREFIX = "APP_PAGE_IS_CACHED";
    private static final String APP_PAGE_CACHE_PAGE_CACHE_KEY_PREFIX = "APP_PAGE_CACHE_PAGE";
    private static final String APP_WIDGET_CACHE_KEY_PREFIX = "APP_WIDGET_";

    private static Map<String, String> piPathMap = new ConcurrentHashMap<String, String>();

    private static Map<String, String> packagePathMap = new ConcurrentHashMap<String, String>();

    private static Map<String, String> confusePathMap = new ConcurrentHashMap<String, String>();
    private static Map<String, String> realPathMap = new ConcurrentHashMap<String, String>();

    private static List<PiSystem> allPiSystems = new ArrayList<PiSystem>();
    private static List<PiModule> allPiModules = new ArrayList<PiModule>();
    private static List<PiApplication> allPiApplications = new ArrayList<PiApplication>();
    private static List<PiItem> allPiItems = new ArrayList<PiItem>();
    private static Map<String, PiSystem> piSystemMap = new ConcurrentHashMap<String, PiSystem>();
    private static Map<String, PiModule> piModuleMap = new ConcurrentHashMap<String, PiModule>();
    private static Map<String, PiApplication> piApplicationMap = new ConcurrentHashMap<String, PiApplication>();
    private static Map<String, PiItem> piItemMap = new ConcurrentHashMap<String, PiItem>();
    private static Map<String, AppFunction> appFunctionMap = new ConcurrentHashMap<String, AppFunction>();

    // 获取所有系统集成信息
    public static List<PiSystem> getAllPiSystems() {
        Cache cache = getCache();
        Boolean allPiSystemsExpired = (Boolean) cache.getValue(PI_ALL_SYSTEM_EXPIRED_CACHE_KEY);
        if (!Boolean.FALSE.equals(allPiSystemsExpired)) {//缓存不存在，加载到缓存内
            AppProductIntegrationContextService contextService = ApplicationContextHolder
                    .getBean(AppProductIntegrationContextService.class);
            allPiSystems = contextService.getAllSystems();
            for (PiSystem piSystem : allPiSystems) {
                String piUuid = piSystem.getPiUuid();
                piSystemMap.put(piUuid, piSystem);
            }
            cache.put(APP_ALL_PI_SYSTEM_CACHE_KEY, piSystemMap);
            cache.put(PI_ALL_SYSTEM_EXPIRED_CACHE_KEY, false);
            return allPiSystems;
        }

        //多应用部署时候有一台缓存生效即可
        if (Boolean.FALSE.equals(
                allPiSystemsExpired) && (allPiSystems.isEmpty() || piSystemMap.isEmpty())) {
            piSystemMap = (Map<String, PiSystem>) cache.get(APP_ALL_PI_SYSTEM_CACHE_KEY).get();
            allPiSystems = Lists.newArrayList(piSystemMap.values());
        }

        return allPiSystems;
    }

    // 获取所有模块集成信息
    public static List<PiModule> getAllPiModules() {
        Cache cache = getCache();
        Boolean allPiModulesExpired = (Boolean) cache.getValue(PI_ALL_MODULE_EXPIRED_CACHE_KEY);
        if (!Boolean.FALSE.equals(allPiModulesExpired)) {
            AppProductIntegrationContextService contextService = ApplicationContextHolder
                    .getBean(AppProductIntegrationContextService.class);
            allPiModules = contextService.getAllModules();
            for (PiModule piModule : allPiModules) {
                String piUuid = piModule.getPiUuid();
                piModuleMap.put(piUuid, piModule);
            }
            cache.put(APP_ALL_PI_MODULE_CACHE_KEY, piModuleMap);
            cache.put(PI_ALL_MODULE_EXPIRED_CACHE_KEY, false);
            return allPiModules;
        }

        //多应用部署时候有一台缓存生效即可
        if (Boolean.FALSE.equals(
                allPiModulesExpired) && (allPiModules.isEmpty() || piModuleMap.isEmpty())) {
            piModuleMap = (Map<String, PiModule>) cache.get(APP_ALL_PI_MODULE_CACHE_KEY).get();
            allPiModules = Lists.newArrayList(piModuleMap.values());
        }
        return allPiModules;

    }

    // 获取所有应用集成信息
    public static List<PiApplication> getAllApplications() {
        Cache cache = getCache();
        Boolean allPiApplicationsExpired = (Boolean) cache.getValue(
                PI_ALL_APPLICATION_EXPIRED_CACHE_KEY);
        if (!Boolean.FALSE.equals(allPiApplicationsExpired)) {
            AppProductIntegrationContextService contextService = ApplicationContextHolder
                    .getBean(AppProductIntegrationContextService.class);
            allPiApplications = contextService.getAllApplications();
            for (PiApplication piApplication : allPiApplications) {
                String piUuid = piApplication.getPiUuid();
                piApplicationMap.put(piUuid, piApplication);
            }
            cache.put(APP_ALL_PI_APP_CACHE_KEY, piApplicationMap);
            cache.put(PI_ALL_APPLICATION_EXPIRED_CACHE_KEY, false);
            return allPiApplications;
        }
        //多应用部署时候有一台缓存生效即可
        if (Boolean.FALSE.equals(allPiApplicationsExpired)
                && (allPiApplications.isEmpty() || piApplicationMap.isEmpty())) {
            piApplicationMap = (Map<String, PiApplication>) cache.get(
                    APP_ALL_PI_APP_CACHE_KEY).get();
            allPiApplications = Lists.newArrayList(piApplicationMap.values());
        }

        return allPiApplications;
    }

    // 获取所有产品项集成信息，包括系统，模块， 应用，功能
    private static List<PiItem> getAllItems() {
        Cache cache = getCache();
        Boolean allPiItemsExpired = (Boolean) cache.getValue(PI_ALL_ITEM_EXPIRED_CACHE_KEY);
        if (!Boolean.FALSE.equals(allPiItemsExpired)) {
            AppProductIntegrationContextService contextService = ApplicationContextHolder
                    .getBean(AppProductIntegrationContextService.class);
            allPiItems = contextService.getAllItems();
            for (PiItem piItem : allPiItems) {
                String piUuid = piItem.getUuid();
                piItemMap.put(piUuid, piItem);
            }
            cache.put(APP_ALL_PI_ITEM_CACHE_KEY, piItemMap);
            cache.put(PI_ALL_ITEM_EXPIRED_CACHE_KEY, false);
            return allPiItems;
        }

        //多应用部署时候有一台缓存生效即可
        if (Boolean.FALSE.equals(
                allPiItemsExpired) && (allPiItems.isEmpty() || piItemMap.isEmpty())) {
            piItemMap = (Map<String, PiItem>) cache.get(APP_ALL_PI_ITEM_CACHE_KEY).get();
            allPiItems = Lists.newArrayList(piItemMap.values());
        }
        return allPiItems;
    }


    // 获取指定的系统集成信息
    public static PiSystem getPiSystem(String piUuid) {
        if (piSystemMap.isEmpty()) {
            getAllPiSystems();
        }
        if (!piSystemMap.containsKey(piUuid)) {
            AppProductIntegrationContextService contextService = ApplicationContextHolder
                    .getBean(AppProductIntegrationContextService.class);
            PiSystem piSystem = contextService.getPiSystem(piUuid);
            if (piSystem == null) {
                return null;
            }
            piSystemMap.put(piUuid, piSystem);
            getCache().put(APP_ALL_PI_SYSTEM_CACHE_KEY, piSystemMap);
        }
        return piSystemMap.get(piUuid);
    }

    // 获取指定的模块集成信息
    public static PiModule getPiModule(String piUuid) {
        if (piModuleMap.isEmpty()) {
            getAllPiModules();
        }
        if (!piModuleMap.containsKey(piUuid)) {
            AppProductIntegrationContextService contextService = ApplicationContextHolder
                    .getBean(AppProductIntegrationContextService.class);
            PiModule piModule = contextService.getPiModule(piUuid);
            if (piModule == null) {
                return null;
            }
            piModuleMap.put(piUuid, piModule);
            getCache().put(APP_ALL_PI_MODULE_CACHE_KEY, piModuleMap);

        }
        return piModuleMap.get(piUuid);
    }

    // 获取指定的应用集成信息
    public static PiApplication getPiApplication(String piUuid) {
        if (piApplicationMap.isEmpty()) {
            getAllApplications();
        }
        if (!piApplicationMap.containsKey(piUuid)) {
            AppProductIntegrationContextService contextService = ApplicationContextHolder
                    .getBean(AppProductIntegrationContextService.class);
            PiApplication piApplication = contextService.getApplication(piUuid);
            if (piApplication == null) {
                return null;
            }
            piApplicationMap.put(piUuid, piApplication);
            getCache().put(APP_ALL_PI_APP_CACHE_KEY, piApplicationMap);
        }
        return piApplicationMap.get(piUuid);
    }

    // 获取指定的功能集成信息
    public static PiFunction getPiFunction(String piUuid) {
        Cache cache = getCache();
        String functionCacheKey = getScriptFunctionCacheKey(piUuid);
        PiFunction piFunction = (PiFunction) cache.getValue(functionCacheKey);
        if (piFunction == null) {
            AppProductIntegrationContextService contextService = ApplicationContextHolder
                    .getBean(AppProductIntegrationContextService.class);
            piFunction = contextService.getFunction(piUuid);
            cache.put(functionCacheKey, piFunction);
        }
        return piFunction;
    }

    // 获取指定的集成信息记录
    public static PiItem getPiItem(String piUuid) {
        if (piItemMap.isEmpty()) {
            getAllItems();
        }
        /*if (!piItemMap.containsKey(piUuid)) {
            AppProductIntegrationContextService contextService = ApplicationContextHolder
                    .getBean(AppProductIntegrationContextService.class);
            PiItem item = contextService.getPiItem(piUuid);
            if (item == null) {
                return null;
            }
            piItemMap.put(piUuid, item);
            getCache().put(APP_ALL_PI_ITEM_CACHE_KEY, piItemMap);//更新缓存
        }*/
        return piItemMap.get(piUuid);
    }

    public static PiItem updatePiItem(String piUuid) {
        if (!piItemMap.containsKey(piUuid)) {
            AppProductIntegrationContextService contextService = ApplicationContextHolder
                    .getBean(AppProductIntegrationContextService.class);
            PiItem item = contextService.getPiItem(piUuid);
            if (item == null) {
                return null;
            }
            piItemMap.put(piUuid, item);
            getCache().put(APP_ALL_PI_ITEM_CACHE_KEY, piItemMap);//更新缓存
        }

        return piItemMap.get(piUuid);
    }


    // 按单位获取系统集成信息
    public static List<PiSystem> getMyUnitPiSystems(String unitId) {
        List<PiSystem> all = getAllPiSystems();
        List<PiSystem> myUnit = new ArrayList<PiSystem>();
        if (CollectionUtils.isNotEmpty(all)) {
            for (PiSystem piSystem : all) {
                if (piSystem.getSystemUnitId().equals(unitId)) {
                    myUnit.add(piSystem);
                }
            }
        }
        return myUnit;
    }

    private static Object getPiPreferences(String userId) {
        AppProductIntegrationContextService contextService = ApplicationContextHolder
                .getBean(AppProductIntegrationContextService.class);
        return contextService.getPiPreferences();
    }

    /**
     * @param sysId
     * @return
     */
    public static AppSystem getAppSystemById(String sysId) {
        Cache cache = getCache();
        String appSystemIdCacheKey = getAppSystemIdCacheKey(sysId);
        AppSystem appSystem = (AppSystem) cache.getValue(appSystemIdCacheKey);
        if (appSystem == null) {
            AppSystemService appSystemService = ApplicationContextHolder.getBean(
                    AppSystemService.class);
            appSystem = appSystemService.getById(sysId);
            cache.put(appSystemIdCacheKey, appSystem);
        }
        return appSystem;
    }

    // 获取指定模块下集成的所有应用集成信息
    @SuppressWarnings("unchecked")
    public static List<PiApplication> getPiAppsByModule(String piUuid) {
        Cache cache = getCache();
        String moduleAppsCacheKey = getModuleAppsCacheKey(piUuid);
        List<PiApplication> moduleApps = (List<PiApplication>) cache.getValue(moduleAppsCacheKey);
        if (moduleApps == null) {
            AppProductIntegrationContextService contextService = ApplicationContextHolder
                    .getBean(AppProductIntegrationContextService.class);
            moduleApps = contextService.getModuleApps(piUuid);
            cache.put(moduleAppsCacheKey, moduleApps);
        }
        return moduleApps;
    }

    // 获取指定应用下集成的所有功能集成信息
    @SuppressWarnings("unchecked")
    public static List<PiFunction> getPiFunctionsByApp(String piUuid) {
        Cache cache = getCache();
        String appFunctionsCacheKey = getAppFunctionsCacheKey(piUuid);
        List<PiFunction> appFunctions = (List<PiFunction>) cache.getValue(appFunctionsCacheKey);
        if (appFunctions == null) {
            AppProductIntegrationContextService contextService = ApplicationContextHolder
                    .getBean(AppProductIntegrationContextService.class);
            appFunctions = contextService.getAppFunctions(piUuid);
            cache.put(appFunctionsCacheKey, appFunctions);
        }
        return appFunctions;
    }

    // 获取指定系统下，指定类型的所有功能集成信息
    @SuppressWarnings("unchecked")
    public static List<PiFunction> getFunctionsBySystem(String functionType, PiSystem system) {
        String productUuid = system.getProductUuid();
        String systemId = system.getId();
        Cache cache = getCache();
        String functionCacheKey = getFunctionCacheKey(
                functionType + Separator.DOT.getValue() + productUuid
                        + Separator.DOT.getValue() + systemId);
        List<PiFunction> piFunctions = (List<PiFunction>) cache.getValue(functionCacheKey);
        if (piFunctions == null) {
            AppProductIntegrationContextService contextService = ApplicationContextHolder
                    .getBean(AppProductIntegrationContextService.class);
            piFunctions = contextService.getFunctions(functionType, productUuid, systemId);
            cache.put(functionCacheKey, piFunctions);
        }
        return piFunctions;
    }

    // 获取指定系统下的所有javascripptModule
    public static List<PiFunction> getJavaScriptModuleFunctionBySystem(PiSystem system) {
        return getFunctionsBySystem(AppFunctionType.JavaScriptModule, system);

    }

    // 获取指定系统下的所有javascripptTemplate
    public static List<PiFunction> getJavaScriptTemplateFunctionBySystem(PiSystem system) {
        return getFunctionsBySystem(AppFunctionType.JavaScriptTemplate, system);

    }

    @SuppressWarnings("unchecked")
    private static Map<String, AppFunction> getAllAppFunctions() {
        Cache cache = getCache();
        Boolean allFunctionExpired = (Boolean) cache.getValue(PI_ALL_FUNCTION_EXPIRED_CACHE_KEY);
        if (!Boolean.FALSE.equals(allFunctionExpired)) {
            AppFunctionService appFunctionService = ApplicationContextHolder.getBean(
                    AppFunctionService.class);
            List<AppFunction> list = appFunctionService.getAll();
            Map convertResult = ListUtils.list2map(list, "uuid");
            appFunctionMap.putAll(convertResult);
            cache.put(APP_ALL_FUNCTION_CACHE_KEY, appFunctionMap);
            cache.put(PI_ALL_FUNCTION_EXPIRED_CACHE_KEY, false);
            return appFunctionMap;
        }

        //多应用部署时候只有有一台缓存生效即可
        if (Boolean.FALSE.equals(allFunctionExpired) && appFunctionMap.isEmpty()) {
            appFunctionMap = (Map<String, AppFunction>) cache.get(APP_ALL_FUNCTION_CACHE_KEY).get();
        }
        return appFunctionMap;

    }

    /**
     * @param appFunctionUuid
     * @return
     */
    public static AppFunction getAppFunction(String appFunctionUuid) {
        Map<String, AppFunction> appFunctionMap = getAllAppFunctions();
        AppFunction appFunction = appFunctionMap.get(appFunctionUuid);
        return appFunction;
    }

    /**
     * @param piPath
     * @return
     */
    public static Object getPreferences(String userId) {
        Cache cache = getCache();
        String piPerfCacheKey = getPiPreferencesCacheKey(userId);
        Object piPref = cache.getValue(piPerfCacheKey);
        if (piPref == null) {
            piPref = getPiPreferences(userId);
            cache.put(piPerfCacheKey, piPref);
        }
        return piPref;
    }

    /**
     * @param piPath
     * @return
     */
    public static Object evictPreferences() {
        Cache cache = getCache();
        String userId = SpringSecurityUtils.getCurrentUserId();
        String piPerfCacheKey = getPiPreferencesCacheKey(userId);
        cache.evict(piPerfCacheKey);
        return piPerfCacheKey;
    }

    /**
     * @param piPath
     * @return
     */
    public static String getPiUuidByPath(String piPath) {
        String piUuid = piPathMap.get(piPath);
        if (StringUtils.isBlank(piUuid)) {
            AppProductIntegrationContextService contextService = ApplicationContextHolder
                    .getBean(AppProductIntegrationContextService.class);
            piUuid = contextService.getPiUuidByPath(piPath);
            if (StringUtils.isNotBlank(piUuid)) {
                piPathMap.put(piPath, piUuid);
            } else {
                piPathMap.put(piPath, piPath);
                piUuid = piPath;
                LOG.warn("The piUuid of piPath——" + piPath + " is null");
            }
        }
        return piUuid;
    }

    /**
     * @param sysId
     * @param moduleId
     * @param appId
     * @return
     */
    @Deprecated
    public static AppPageDefinition getAppPageDefinition(String sysId, String moduleId,
                                                         String appId) {
        Cache cache = getCache();
        String pageCacheKey = getPageCacheKey(sysId, moduleId, appId);
        AppPageDefinition appPageDefinition = (AppPageDefinition) cache.getValue(pageCacheKey);
        if (appPageDefinition == null) {
            AppProductIntegrationService appProductIntegrationService = ApplicationContextHolder
                    .getBean(AppProductIntegrationService.class);
            appPageDefinition = appProductIntegrationService.getAppPageDefinition(sysId, moduleId,
                    appId);
            cache.put(pageCacheKey, appPageDefinition);
        }
        return appPageDefinition;
    }

    /**
     * @param appPath
     * @return
     */
    public static AppPageDefinition getAppPageDefinition(String appPath) {
        return getAppPageDefinition(appPath, StringUtils.EMPTY);
    }

    /**
     * @param appPath
     * @param pageUuid
     * @return
     */
    public static AppPageDefinition getAppPageDefinition(String appPath, String pageUuid) {
        Cache cache = getCache();
        String piUuid = getPiUuidByPath(appPath);
        String pageCacheKey = getPageCacheKey(piUuid + pageUuid);
        AppPageDefinition appPageDefinition = (AppPageDefinition) cache.getValue(pageCacheKey);
        if (appPageDefinition == null) {
            AppProductIntegrationService appProductIntegrationService = ApplicationContextHolder
                    .getBean(AppProductIntegrationService.class);
            // 获取产品集成配置指定的页面，如果页面UUID为空，则取集成信息的页面
            appPageDefinition = appProductIntegrationService.getAppPageDefinition(piUuid, pageUuid);
            cache.put(pageCacheKey, appPageDefinition);
        }
        return appPageDefinition;
    }

    /**
     * @param userId
     * @param appPath
     * @param pageUuid
     * @return
     */
    public static AppPageDefinition getUserAppPageDefinition(String userId, String appPath,
                                                             String pageUuid) {
        AppPageDefinition appPageDefinition = null;
        String piUuid = getPiUuidByPath(appPath);
        Cache cache = getCache();
        // 用户页面缓存key
        String userPageCacheKey = getPageCacheKey(userId + piUuid + pageUuid);
        appPageDefinition = (AppPageDefinition) cache.getValue(userPageCacheKey);
        if (appPageDefinition == null) {
            AppProductIntegrationService appProductIntegrationService = ApplicationContextHolder
                    .getBean(AppProductIntegrationService.class);
            // 获取产品集成配置指定的页面，如果页面UUID为空，则取集成信息的页面
            appPageDefinition = appProductIntegrationService.getUserAppPageDefinition(userId, piUuid, pageUuid);
            //无权限或者无配置的情况下不需要缓存
            if (appPageDefinition instanceof UnauthorizePageDefinition || appPageDefinition instanceof NoPageDefinition) {
                return appPageDefinition;
            }
            cache.put(userPageCacheKey, appPageDefinition);
        }
        return appPageDefinition;
    }

    /**
     * @param appWidgetDefUuid
     * @return
     */
    public static AppWidgetDefinition getAppWidgetDefinition(String appWidgetDefUuid) {
        Cache cache = getCache();
        String widgetCacheKey = getWidgetCacheKey(appWidgetDefUuid);
        AppWidgetDefinition appWidgetDefinition = (AppWidgetDefinition) cache.getValue(
                widgetCacheKey);
        if (appWidgetDefinition == null) {
            AppWidgetDefinitionService appWidgetDefinitionService = ApplicationContextHolder
                    .getBean(AppWidgetDefinitionService.class);
            appWidgetDefinition = appWidgetDefinitionService.get(appWidgetDefUuid);
            cache.put(widgetCacheKey, appWidgetDefinition);
        }
        return appWidgetDefinition;
    }

    /**
     * @param appWidgetDefId
     * @return
     */
    public static AppWidgetDefinition getAppWidgetDefinitionById(String appWidgetDefId) {
        Cache cache = getCache();
        String widgetCacheKey = getWidgetCacheKey(appWidgetDefId);
        AppWidgetDefinition appWidgetDefinition = (AppWidgetDefinition) cache.getValue(
                widgetCacheKey);
        if (appWidgetDefinition == null) {
            AppWidgetDefinitionService appWidgetDefinitionService = ApplicationContextHolder
                    .getBean(AppWidgetDefinitionService.class);
            appWidgetDefinition = appWidgetDefinitionService.getById(appWidgetDefId);
            cache.put(widgetCacheKey, appWidgetDefinition);
        }
        return appWidgetDefinition;
    }

    /**
     * @param appPath
     */
    public static boolean isCachedAppPage(UserAppData userAppData) {
        String appPath = userAppData.getAppPath();
        String theme = AppConstants.THEME_DEFAULT;
        if (userAppData.getTheme() != null) {
            theme = userAppData.getTheme().getId();
        }
        Cache cache = getSessionCache();
        String piUuid = getPiUuidByPath(appPath);
        String pageUuid = userAppData.getPageUuid();
        String appPageCacheKey = APP_PAGE_IS_CACHED_CACHE_KEY_PREFIX + piUuid + pageUuid + theme;
        // String sessionId = userAppData.getSessionId();//
        // SpringSecurityUtils.getCurrentUserId();
        Boolean isCachedAppPage = (Boolean) cache.getValue(appPageCacheKey);
        return Boolean.TRUE.equals(isCachedAppPage);
    }

    /**
     * @param appPath
     * @param responseBody
     */
    public static void setAppPage(UserAppData userAppData, String responseBody) {
        String appPath = userAppData.getAppPath();
        String theme = AppConstants.THEME_DEFAULT;

        if (userAppData.getTheme() != null) {
            theme = userAppData.getTheme().getId();
        }
        Cache cache = getSessionCache();
        String piUuid = getPiUuidByPath(appPath);
        String pageUuid = userAppData.getPageUuid();
        // String sessionId = userAppData.getSessionId();//
        // SpringSecurityUtils.getCurrentUserId();
        Integer pref = ObjectUtils.hashCode(userAppData.getPreferences());
        String isCachedAppPageCacheKey = APP_PAGE_IS_CACHED_CACHE_KEY_PREFIX + piUuid + pageUuid + theme;
        String appPageCacheKey = APP_PAGE_CACHE_PAGE_CACHE_KEY_PREFIX + piUuid + pageUuid + theme + pref;
        // 缓存标识
        cache.put(isCachedAppPageCacheKey, true);
        cache.put(appPageCacheKey, responseBody);
    }

    /**
     * @param appPath
     */
    public static String getAppPage(UserAppData userAppData) {
        String appPath = userAppData.getAppPath();
        String theme = AppConstants.THEME_DEFAULT;
        if (userAppData.getTheme() != null) {
            theme = userAppData.getTheme().getId();
        }
        Cache cache = getSessionCache();
        String piUuid = getPiUuidByPath(appPath);
        String pageUuid = userAppData.getPageUuid();
        // String sessionId = userAppData.getSessionId();//
        // SpringSecurityUtils.getCurrentUserId();
        Integer pref = ObjectUtils.hashCode(userAppData.getPreferences());
        String appPageCacheKey = APP_PAGE_CACHE_PAGE_CACHE_KEY_PREFIX + piUuid + pageUuid + theme + pref;
        return (String) cache.getValue(appPageCacheKey);
    }

    /**
     * @param sysId
     * @return
     */
    private static String getAppSystemIdCacheKey(String sysId) {
        String appSystemIdCacheKey = APP_SYSTEM_ID_KEY_PREFIX + sysId;
        return appSystemIdCacheKey;
    }

    /**
     * @param piUuid
     * @return
     */
    private static String getModuleAppsCacheKey(String piUuid) {
        String moduleAppsCacheKey = PI_MODULE_APPS_CACHE_KEY_PREFIX + piUuid;
        return moduleAppsCacheKey;
    }

    /**
     * @param piUuid
     * @return
     */
    private static String getAppFunctionsCacheKey(String piUuid) {
        String appFunctionsCacheKey = PI_APP_FUNCTIONS_CACHE_KEY_PREFIX + piUuid;
        return appFunctionsCacheKey;
    }

    /**
     * @param id
     * @return
     */
    private static String getFunctionCacheKey(String piUuid) {
        String functionCacheKey = PI_FUNCTION_CACHE_KEY_PREFIX + piUuid;
        return functionCacheKey;
    }

    /**
     * @param key
     * @return
     */
    private static String getScriptFunctionCacheKey(String key) {
        String scriptFunctionCacheKey = PI_SCRIPT_FUNCTION_CACHE_KEY_PREFIX + key;
        return scriptFunctionCacheKey;
    }

    /**
     * @param piUuid
     * @return
     */
    private static String getPiPreferencesCacheKey(String userId) {
        String piPrefCacheKey = PI_PREF_CACHE_KEY_PREFIX + "_" + userId;
        return piPrefCacheKey;
    }

    /**
     * 如何描述该方法
     *
     * @param sysId
     * @param moduleId
     * @param appId
     * @return
     */
    private static String getPageCacheKey(String sysId, String moduleId, String appId) {
        String pageCacheKey = APP_PAGE_CACHE_KEY_PREFIX + sysId + "_" + moduleId + "_" + appId;
        return pageCacheKey;
    }

    /**
     * @param piUuid
     * @return
     */
    private static String getPageCacheKey(String piUuid) {
        String pageCacheKey = APP_PAGE_CACHE_KEY_PREFIX + piUuid;
        return pageCacheKey;
    }

    /**
     * @param appWidgetDefUuid
     * @return
     */
    private static String getWidgetCacheKey(String appWidgetDefUuid) {
        String widgetCacheKey = APP_WIDGET_CACHE_KEY_PREFIX + appWidgetDefUuid;
        return widgetCacheKey;
    }

    /**
     * @return
     */
    private static Cache getCache() {
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        Cache cache = cacheManager.getCache(ModuleID.APP);
        return cache;
    }

    /**
     * @return
     */
    private static Cache getSessionCache() {
        CacheManager cacheManager = ApplicationContextHolder.getBean(CacheManager.class);
        Cache cache = cacheManager.getSessionCache(ModuleID.APP);
        return cache;
    }

    public static void clear() {
        piPathMap.clear();
        piSystemMap.clear();
        piModuleMap.clear();
        piApplicationMap.clear();
        piItemMap.clear();
        appFunctionMap.clear();
        getCache().clear();
    }

    public static void loadAll() {
//        AppCacheUtils.getAllPiSystems();
//        AppCacheUtils.getAllApplications();
//        AppCacheUtils.getAllPiModules();
//        AppCacheUtils.getAllItems();
//        AppCacheUtils.getAllAppFunctions();
    }


    /**
     * @param key
     * @param packagePath
     */
    public static void addPackagePath(String key, String packagePath) {
        packagePathMap.put(key, packagePath);
    }

    /**
     * @param key
     */
    public static String getPackagePath(String key) {
        return packagePathMap.get(key);
    }

    /**
     * @param key
     * @param rawPath
     */
    public static void addConfusePath(String confusePath, String rawPath) {
        confusePathMap.put(confusePath, rawPath);
    }

    /**
     * @param confusePath
     */
    public static String getRealPath(String confusePath) {
        return confusePathMap.get(confusePath);
    }

    /**
     * @param realPath
     * @return
     */
    public static boolean hasRealPath(String realPath) {
        return realPathMap.containsKey(realPath);
    }

    /**
     * @param realPath
     * @return
     */
    public static String getConfusePath(String realPath) {
        return realPathMap.get(realPath);
    }

    /**
     * @param realPath
     * @param confusePath
     */
    public static void addRealPath(String realPath, String confusePath) {
        realPathMap.put(realPath, confusePath);
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
    }

}
