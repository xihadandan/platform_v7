/*
 * @(#)2016年8月16日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service.impl;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.app.service.AppProductIntegrationContextService;
import com.wellsoft.pt.app.support.*;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.org.service.UserService;
import net.sf.json.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author huanglc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月16日.1	huanglc		2016年8月16日		Create
 * </pre>
 * @date 2016年8月16日
 */
@Service
@Transactional(readOnly = true)
public class AppProductIntegrationContextServiceImpl extends BaseServiceImpl implements
        AppProductIntegrationContextService {

    private static final String USER_PROPERTY_PREFERENCES = "user.preferences";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#getAllSystems()
     */
    @Override
    public List<PiSystem> getAllSystems() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataType", AppType.SYSTEM.toString());
        return this.nativeDao.namedQuery("getAllSystems", values, PiSystem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#getAllModules()
     */
    @Override
    public List<PiModule> getAllModules() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataType", AppType.MODULE.toString());
        return this.nativeDao.namedQuery("getAllModules", values, PiModule.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#getModuleApps(java.lang.String)
     */
    @Override
    public List<PiApplication> getModuleApps(String piUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataType", AppType.APPLICATION.toString());
        values.put("piUuid", piUuid);
        return this.nativeDao.namedQuery("getModuleApps", values, PiApplication.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#getAllApplications()
     */
    @Override
    public List<PiApplication> getAllApplications() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataType", AppType.APPLICATION.toString());
        return this.nativeDao.namedQuery("getAllApplications", values, PiApplication.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#getAppFunctions(java.lang.String)
     */
    @Override
    public List<PiFunction> getAppFunctions(String piUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataType", AppType.FUNCTION.toString());
        values.put("piUuid", piUuid);
        return this.nativeDao.namedQuery("getAppFunctions", values, PiFunction.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#getAllFunctions()
     */
    @Override
    public List<PiFunction> getAllFunctions() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataType", AppType.FUNCTION.toString());
        return this.dao.namedQuery("getAllFunctions", values, PiFunction.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#getAllAnonymousFunctionUuids()
     */
    @Override
    public List<String> getAllAnonymousFunctionUuids() {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataType", AppType.FUNCTION.toString());
        return this.nativeDao.namedQuery("getAllAnonymousFunctionUuids", values, String.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#getAllItems()
     */
    @Override
    public List<PiItem> getAllItems() {
        Map<String, Object> values = new HashMap<String, Object>();
        return this.nativeDao.namedQuery("getAllItems", values, PiItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#getFunction(java.lang.String)
     */
    @Override
    public PiFunction getFunction(String piUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataType", AppType.FUNCTION.toString());
        values.put("piUuid", piUuid);
        return this.nativeDao.findUniqueByNamedQuery("getPiFunction", values, PiFunction.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#getFunctions(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<PiFunction> getFunctions(String functionType, String productUuid, String systemId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataType", AppType.FUNCTION.toString());
        values.put("functionType", functionType);
        values.put("productUuid", productUuid);
        values.put("systemId", systemId);
        return this.nativeDao.namedQuery("getFunctions", values, PiFunction.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#getJavaScriptFunction(java.lang.String, java.lang.String)
     */
    @Override
    public List<PiFunction> getJavaScriptModuleFunction(String productUuid, String systemId) {
        return getFunctions(AppFunctionType.JavaScriptModule, productUuid, systemId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#getJavaScriptTemplateFunction(java.lang.String, java.lang.String)
     */
    @Override
    public List<PiFunction> getJavaScriptTemplateFunction(String productUuid, String systemId) {
        return getFunctions(AppFunctionType.JavaScriptTemplate, productUuid, systemId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#getPiItem(java.lang.String)
     */
    @Override
    public PiItem getPiItem(String piUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuid", piUuid);
        return this.nativeDao.findUniqueByNamedQuery("getPiItem", values, PiItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#getPiItemsByDataUuidAndType(java.lang.String, java.lang.String)
     */
    @Override
    public List<PiItem> getPiItemsByDataUuidAndType(String dataUuid, String dataType) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataUuid", dataUuid);
        values.put("dataType", dataType);
        return this.nativeDao.namedQuery("getPiItemsByDataUuidAndType", values, PiItem.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#getPiUuidByPath(java.lang.String)
     */
    @Override
    public String getPiUuidByPath(String piPath) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataPath", piPath);
        List<String> list = this.nativeDao.namedQuery("getPiUuidByPath", values);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
//        return this.nativeDao.findUniqueByNamedQuery("getPiUuidByPath", values, String.class);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#getPiPreferences()
     */
    @Override
    public JSONObject getPiPreferences() {
        UserService userService = ApplicationContextHolder.getBean(UserService.class);
        String propertyValue = userService.getUserClobProperty(USER_PROPERTY_PREFERENCES);
        if (StringUtils.isNotBlank(propertyValue)) {
            return JSONObject.fromObject(propertyValue);
        }
        return JSONObject.fromObject("{}");
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.service.AppProductIntegrationContextService#savePiPreferences(java.lang.String)
     */
    @Override
    @Transactional(readOnly = false)
    public void savePiPreferences(String propertyValue) {
        UserService userService = ApplicationContextHolder.getBean(UserService.class);
        userService.saveUserClobProperty(USER_PROPERTY_PREFERENCES, propertyValue);
        AppCacheUtils.evictPreferences();
    }

    @Override
    public PiApplication getApplication(String piUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataType", AppType.APPLICATION.toString());
        values.put("piUuid", piUuid);
        return this.nativeDao.findUniqueByNamedQuery("getPiApplication", values, PiApplication.class);
    }

    @Override
    public PiSystem getPiSystem(String piUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataType", AppType.SYSTEM.toString());
        values.put("piUuid", piUuid);
        return this.nativeDao.findUniqueByNamedQuery("getPiSystem", values, PiSystem.class);
    }

    @Override
    public PiModule getPiModule(String piUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("dataType", AppType.SYSTEM.toString());
        values.put("piUuid", piUuid);
        return this.nativeDao.findUniqueByNamedQuery("getPiModule", values, PiModule.class);
    }

}
