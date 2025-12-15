/*
 * @(#)8/18/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.service;

import com.wellsoft.pt.app.dao.AppDataDefinitionRefResourceDao;
import com.wellsoft.pt.app.dto.AppDataDefinitionRefResourceDto;
import com.wellsoft.pt.app.entity.AppDataDefinitionRefResourceEntity;
import com.wellsoft.pt.app.entity.AppFunction;
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
 * 8/18/23.1	zhulh		8/18/23		Create
 * </pre>
 * @date 8/18/23
 */
public interface AppDataDefinitionRefResourceService extends JpaService<AppDataDefinitionRefResourceEntity, AppDataDefinitionRefResourceDao, Long> {

    /***
     * 保存数据定义引用资源
     *
     * @param dataDefUuid
     * @param refResourceDtos
     */
    void saveRefResources(String dataDefUuid, List<AppDataDefinitionRefResourceDto> refResourceDtos);

    /**
     * 根据数据定义UUID删除引用资源
     *
     * @param dataDefUuid
     */
    void deleteByDataDefUuid(String dataDefUuid);

    List<AppFunction> getDataRefFunctions(String dataDefUuid);
}
