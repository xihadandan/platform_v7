/*
 * @(#)2016年8月16日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.app.support.*;
import net.sf.json.JSONObject;

import java.util.List;

/**
 * Description: 产品集成的上下文信息服务
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月16日.1	zhulh		2016年8月16日		Create
 * </pre>
 * @date 2016年8月16日
 */
public interface AppProductIntegrationContextService extends BaseService {

    List<PiSystem> getAllSystems();

    List<PiModule> getAllModules();

    /**
     * 根据模块集成信息UUID获取其下面的应用集成信息
     *
     * @param piUuid
     * @return
     */
    List<PiApplication> getModuleApps(String piUuid);

    List<PiApplication> getAllApplications();

    /**
     * 根据应用集成信息UUID获取其下面的功能集成信息
     *
     * @param piUuid
     * @return
     */
    List<PiFunction> getAppFunctions(String piUuid);

    List<PiFunction> getAllFunctions();

    /**
     * 获取不受权限控制的功能集成UUID列表
     *
     * @return
     */
    List<String> getAllAnonymousFunctionUuids();

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<PiItem> getAllItems();

    /**
     * 根据集成UUID获取集成信息
     *
     * @param piUuid
     * @return
     */
    PiFunction getFunction(String piUuid);

    /**
     * 获取产品下某系统的指定类型功能集成信息
     *
     * @param functionType
     * @param productUuid
     * @param systemId
     * @return
     */
    List<PiFunction> getFunctions(String functionType, String productUuid, String systemId);

    /**
     * 获取产品下某系统的JS模块功能集成信息
     *
     * @param productUuid
     * @param systemId
     * @return
     */
    List<PiFunction> getJavaScriptModuleFunction(String productUuid, String systemId);

    /**
     * 获取产品下某系统的JS模板功能集成信息
     *
     * @param productUuid
     * @param systemId
     * @return
     */
    List<PiFunction> getJavaScriptTemplateFunction(String productUuid, String systemId);

    /**
     * 根据集成UUID获取集成信息
     *
     * @param piUuid
     * @return
     */
    PiItem getPiItem(String piUuid);

    /**
     * 根据数据UUID获取相应的集成信息
     *
     * @param dataUuid
     * @param appType
     * @return
     */
    List<PiItem> getPiItemsByDataUuidAndType(String dataUuid, String appType);

    /**
     * 根据集成路径获取对应的集成UUID
     *
     * @param piPath
     * @return
     */
    String getPiUuidByPath(String piPath);

    /**
     * 根据当前登录的用户获取用户的个性化配置数据
     *
     * @return
     */
    JSONObject getPiPreferences();

    /**
     * 保存当前用户的个性化配置数据
     *
     * @return
     */
    void savePiPreferences(String propertyValue);

    PiApplication getApplication(String piUuid);

    PiSystem getPiSystem(String piUuid);

    PiModule getPiModule(String piUuid);
}
