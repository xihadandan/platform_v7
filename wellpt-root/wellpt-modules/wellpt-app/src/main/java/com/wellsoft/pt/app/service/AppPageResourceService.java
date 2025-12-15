/*
 * @(#)2019年6月10日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppPageResourceDao;
import com.wellsoft.pt.app.dto.AppPageResourceDto;
import com.wellsoft.pt.app.entity.AppPageResourceEntity;
import com.wellsoft.pt.jpa.service.JpaService;

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
 * 2019年6月10日.1	zhulh		2019年6月10日		Create
 * </pre>
 * @date 2019年6月10日
 */
public interface AppPageResourceService extends JpaService<AppPageResourceEntity, AppPageResourceDao, String> {

    /**
     * 如何描述该方法
     *
     * @param entity
     * @return
     */
    List<AppPageResourceEntity> listByEntity(AppPageResourceEntity entity);

    /**
     * @param appPageUuid
     */
    void removeByAppPageUuid(String appPageUuid);

    /**
     * @param appPageUuid
     * @param configType
     */
    void removeByAppPageUuidAndConfigType(String appPageUuid, String configType);

    /**
     * @param appPageUuid
     * @param configType
     * @return
     */
    List<String> getProtectedUuidsByAppPageUuidAndConfigType(String appPageUuid, String configType);

    List<AppPageResourceEntity> listByAppPageUuid(String uuid);

    List<AppPageResourceDto> getAppPageResourcesAndFunction(String uuid);

    List<AppPageResourceEntity> getAppPageResourcesByPageUuid(List<String> appPageUuids);

    List<String> getProtectedIdsByAppPageUuidAndConfigType(String appPageUuid, String configType);

    AppPageResourceEntity getByIdAndAppPageUuid(String id, String pageDefinitionUuid);
}
