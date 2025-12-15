/*
 * @(#)2016-07-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service;

import com.wellsoft.context.jdbc.entity.ConfigurableIdEntity;
import com.wellsoft.pt.app.dao.AppProductIntegrationDao;
import com.wellsoft.pt.app.entity.*;
import com.wellsoft.pt.jpa.service.JpaService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-07-24.1	zhulh		2016-07-24		Create
 * </pre>
 * @date 2016-07-24
 */
public interface AppProductIntegrationService extends
        JpaService<AppProductIntegration, AppProductIntegrationDao, String> {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    AppProductIntegration get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<AppProductIntegration> getAll();

    /**
     * 根据产品UUID，获取顶级结点信息
     *
     * @param productUuid
     * @return
     */
    List<AppProductIntegration> getTopByProductUuid(String productUuid);

    /**
     * 获取子结点信息
     *
     * @param uuid
     * @return
     */
    List<AppProductIntegration> getChildren(String uuid);

    /**
     * 判断是否有子结点
     *
     * @param uuid
     * @return
     */
    boolean hasChildren(String uuid);

    /**
     * 判断是否有子结点
     *
     * @param dataUuid
     * @return
     */
    boolean hasChildrenByDataUuid(String dataUuid);

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<AppProductIntegration> findByExample(AppProductIntegration example);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(AppProductIntegration entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<AppProductIntegration> entities);

    /**
     * 根据UUID删除记录
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 根据数据UUID及类型删除记录
     *
     * @param dataUuid
     * @param dataType
     */
    void removeByDataUuidAndType(String dataUuid, String dataType);

    @Transactional
    void removeFunctionUnderParentDataId(String parentDataId, String functionDataUuid);

    List<AppProductIntegration> getByDataUuidAndType(String dataUuid, String dataType);

    /**
     * 批量删除
     *
     * @param uuids
     */
    void removeAllByPk(Collection<String> uuids);

    /**
     * 根据产品集成信息UUID删除对应的数据
     *
     * @param appProductUuid
     */
    void removeByProductUuid(String productUuid);

    /**
     * 查询总数
     *
     * @param dataUuid
     * @param appType
     * @return
     */
    Long count(String dataUuid, Integer appType);

    /**
     * 根据系统ID、模块ID或应用ID获取相应的页面定义
     *
     * @param sysId
     * @param moduleId
     * @param appId
     * @return
     */
    AppPageDefinition getAppPageDefinition(String sysId, String moduleId, String appId);

    /**
     * 根据集成信息UUID获取相应的页面定义
     *
     * @param piUuid
     * @return
     */
    AppPageDefinition getAppPageDefinition(String piUuid);

    /**
     * 根据集成信息UUID、页面UUID获取相应的页面定义
     *
     * @param piUuid
     * @param pageUuid
     * @return
     */
    AppPageDefinition getAppPageDefinition(String piUuid, String pageUuid);

    /**
     * 根据用户ID、集成信息UUID、页面UUID获取相应的页面定义
     *
     * @param userId
     * @param piUuid
     * @param pageUuid
     * @return
     */
    AppPageDefinition getUserAppPageDefinition(String userId, String piUuid, String pageUuid);

    /**
     * 根据用户ID、集成信息UUID，获取所有用户可访问的页面信息
     *
     * @param userId
     * @param appPiUuid
     * @return
     */
    List<AppPageDefinition> getAllAvailableAppPageInfoByUserIdAndAppPiUuid(String userId, String appPiUuid);

    /**
     * 更新集成配置信息
     *
     * @param entity
     */
    void updatPiDataName(ConfigurableIdEntity entity);

    /**
     * 在产品下面创建系统集成信息
     *
     * @param appProductUuid
     * @param appSystem
     */
    AppProductIntegration createAppSystem(String appProductUuid, AppSystem appSystem);

    /**
     * 根据集成信息UUID，添加模块
     *
     * @param piUuid
     * @param appModule
     */
    AppProductIntegration addAppModule(String piUuid, AppModule appModule);

    /**
     * 根据集成信息UUID，添加应用
     *
     * @param piUuid
     * @param appApplication
     */
    AppProductIntegration addAppApplication(String piUuid, AppApplication appApplication);

    /**
     * 根据集成信息UUID，添加功能
     *
     * @param piUuid
     * @param appFunction
     */
    AppProductIntegration addAppFunction(String piUuid, AppFunction appFunction);

    /**
     * 根据集成信息UUID，添加功能
     *
     * @param piUuid
     * @param appFunction
     */
    AppProductIntegration addAppFunction(String piUuid, AppFunction appFunction, boolean isProtected);

    /**
     * 如何描述该方法
     *
     * @param removeByProductUuid
     * @param values
     */
    void deleteByHQL(String hql, Map<String, Object> values);

    /**
     * 如何描述该方法
     *
     * @param uuid
     * @param dataTypes
     * @param functionTypes
     * @return
     */
    List<AppProductIntegration> queryAppProductIntegrationTree(String uuid, String[] dataTypes, String[] functionTypes);

    /**
     * 如何描述该方法
     *
     * @param appProductUuid
     * @return
     */
    long countByAppProductUuid(String appProductUuid);

    /**
     * 获取自身及上级所有结点
     *
     * @param uuid
     * @return
     */
    List<AppProductIntegration> getSelfWithParentsByUuid(String uuid);

    /**
     * 获取自身及上级所有结点UUID
     *
     * @param piUuid
     * @return
     */
    List<String> getSelfWithParentUuidsByUuid(String piUuid);

    /**
     * 获取自身及下级所有结点
     *
     * @param uuid
     * @return
     */
    List<AppProductIntegration> getSelfWithChildrenByUuid(String uuid);

    /**
     * 获取自身及下级模块、应用数据ID
     *
     * @param dataId
     * @return
     */
    List<String> getSelfWithChildrenModuleAndAppDataIdsByDataId(String dataId);

    /**
     * 更新集成信息的产品UUID
     *
     * @param uuid
     * @param appProductUuid
     */
    void updateAppProductUuid(String uuid, String appProductUuid);

    /**
     * 更新集成信息的上级结点
     *
     * @param uuid
     * @param parentUuid
     */
    void updateParentUuid(String uuid, String parentUuid);

    /**
     * 重置产品集成信息的页面信息
     *
     * @param uuid
     */
    void resetAppPageDefinition(String uuid);

    /**
     * 获取产品集成信息的系统UUID
     *
     * @param uuid
     * @return
     */
    String getAppSystemUuidByUuid(String uuid);


}
