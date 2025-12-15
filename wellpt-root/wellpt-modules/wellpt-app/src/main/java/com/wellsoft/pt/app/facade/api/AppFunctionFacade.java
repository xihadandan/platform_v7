/*
 * @(#)2016-07-26 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.facade.api;

import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.app.facade.service.AppFunctionMgr;
import com.wellsoft.pt.app.service.AppFunctionService;
import com.wellsoft.pt.app.service.AppProductIntegrationContextService;
import com.wellsoft.pt.app.support.AppType;
import com.wellsoft.pt.app.support.PiItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
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
 * 2016-07-26.1	zhulh		2016-07-26		Create
 * </pre>
 * @date 2016-07-26
 */
@Component
public class AppFunctionFacade extends AbstractApiFacade {

    @Autowired
    private AppFunctionService appFunctionService;

    @Autowired
    private AppFunctionMgr appFunctionMgr;

    @Autowired
    private AppProductIntegrationContextService appProductIntegrationContextService;

    /**
     * 同步功能，并添加到指定的集成信息下
     *
     * @param entityClass
     * @param entityUuid
     * @param functionType
     * @param piUuid
     */
    public <ITEM extends Serializable> void synchronize(Class<ITEM> entityClass, String entityUuid,
                                                        String functionType, String piUuid) {
        appFunctionService.synchronize(entityClass, entityUuid, functionType, piUuid);
    }


    /**
     * 同步功能，并添加到指定的模块ID集成信息下
     *
     * @param entityClass
     * @param <ITEM>
     * @param entityUuid
     * @param functionType
     * @param moduleId
     * @param isProtected  是否参与权限控制
     */
    public <ITEM extends Serializable> void synchronizeFunction2ModuleProductIntegrate(
            String entityUuid,
            String functionType, String moduleId, boolean isProtected) {
        appFunctionService.synchronizeFunction2ModuleProductIntegrate(entityUuid, functionType, moduleId, isProtected);
    }

    public <ITEM extends Serializable> void synchronizeFunction2ProductIntegrate(
            String entityUuid,
            String functionType, String piUuid, boolean isProtected) {
        appFunctionService.synchronizeFunction2ProductIntegrate(entityUuid, functionType, piUuid, isProtected);
    }

    /**
     * 根据数据UUID获取相应的集成信息
     *
     * @param dataUuid
     * @return
     */
    public List<PiItem> getPiItems(String dataUuid) {
        return appProductIntegrationContextService.getPiItemsByDataUuidAndType(dataUuid, AppType.FUNCTION.toString());
    }

    /**
     * 根据数据UUID删除功能及相应的集成信息
     *
     * @param dataUuid
     */
    public void delete(String dataUuid) {
        appFunctionMgr.forceRemove(dataUuid);
    }


}
