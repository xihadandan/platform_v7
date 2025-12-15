/*
 * @(#)2016-05-09 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.bean.AppPageDefinitionParamDto;
import com.wellsoft.pt.app.dao.AppPageDefinitionDao;
import com.wellsoft.pt.app.dto.AppPageResourceDto;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.Collection;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author t
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-05-09.1	t		2016-05-09		Create
 * </pre>
 * @date 2016-05-09
 */
public interface AppPageDefinitionService extends
        JpaService<AppPageDefinition, AppPageDefinitionDao, String> {

    @Override
    void save(AppPageDefinition entity);

    void deleteByUuid(String pageUuid);

    void deleteById(String id);

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    AppPageDefinition get(String uuid);

    AppPageDefinition getLatestUuidAndVersion(String id);

    AppPageDefinition getLatestPageDefinition(String id);

    List<AppPageDefinition> getPageDefinitionVersions(String id);

    /**
     * 根据ID获取
     *
     * @param id
     * @return
     */
    AppPageDefinition getById(String id);

    /**
     * 获取产品集成信息下的页面定义UUID
     *
     * @param appPiUuid
     * @return
     */
    List<String> getAppPageDefinitionUuidsByAppPiUuid(String appPiUuid);

    /**
     * 获取产品集成信息下的默认页面定义UUID
     *
     * @param appPiUuid
     * @return
     */
    List<String> getDefaultAppPageDefinitionUuidsByAppPiUuid(String appPiUuid);

    /**
     * 获取用户产品集成信息下的页面定义
     *
     * @param userId
     * @param appPiUuid
     * @return
     */
    List<AppPageDefinition> getByUserIdAndAppPiUuid(String userId, String appPiUuid);

    /**
     * 获取用户所有可访问的页面信息
     *
     * @param userId
     * @param appPiUuid
     * @return
     */
    List<AppPageDefinition> getAllAvailableAppPageInfoByUserIdAndAppPiUuid(String userId,
                                                                           String appPiUuid);

    /**
     * 根据页面UUID设置为默认门户
     *
     * @param userId
     * @param pageUuid
     */
    void updateUserDefaultPortalByPageUuid(String userId, String pageUuid);

    /**
     * 根据UUID获取最新版本号
     *
     * @param uuid
     * @return
     */
    Double getLatestVersionByUuid(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<AppPageDefinition> getAll();


    List<AppPageDefinition> getAllByWtype(String wtype);

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<AppPageDefinition> findByExample(AppPageDefinition example);

    /**
     * 根据实例查询列表
     *
     * @param example
     * @param order
     * @return
     */
    List<AppPageDefinition> findByExample(AppPageDefinition example, String order);

    AppPageDefinition getDefaultPageDefinition(String appId, Boolean isPc);

    /**
     * 删除
     *
     * @param entity
     */
    void remove(AppPageDefinition entity);

    /**
     * 批量删除
     *
     * @param entities
     */
    void removeAll(Collection<AppPageDefinition> entities);

    /**
     * 根据UUID删除记录
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 批量删除
     *
     * @param uuids
     */
    void removeAllByPk(Collection<String> uuids);

    void updateUnDefaultByAppPiUuid(String appPiUuid);

    void updateDefaultTrueByUuid(String appPiUuid, String uuid);

    List<AppPageDefinition> listByAppId(String appId);

    List<AppPageDefinition> listRecentVersionPageByAppId(String appId);


    void updateBasicInfo(AppPageDefinitionParamDto params);

    void updatePageProtected(String pageDefinitionUuid, Boolean isProtected, List<AppPageResourceDto> resourceDtos);

    void updateDesignable(String uuid, Boolean designable);

    String getPageIdByUuid(String pageUuid);

    void updatePageTheme(List<AppPageDefinitionParamDto> dtos);

    List<AppPageDefinition> listByAppIds(List<String> appIds);

    List<AppPageDefinition> listLatestVersionPageByAppIds(List<String> appIds);

    List<AppPageDefinition> listLatestVersionPageBySystem(String system);

    boolean existId(String id);

    void updateAnonymous(String uuid, Boolean anonymous);

    void updateEnabled(String uuid, Boolean enabled);

    void deleteByIds(List<String> ids);

    List<AppPageDefinition> listMaxVersionPagesByAppIdAndTenant(String appId, String tenant);

    List<String> getAllAnonymousPageDefinitionIds();

}
