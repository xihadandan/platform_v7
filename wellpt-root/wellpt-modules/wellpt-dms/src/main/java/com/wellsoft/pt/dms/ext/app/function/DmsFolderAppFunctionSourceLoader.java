/*
 * @(#)2016年8月24日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.ext.app.function;

import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.dms.entity.DmsFolderEntity;
import com.wellsoft.pt.dms.service.DmsFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Description: 数据管理操作功能
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年8月24日.1	zhulh		2016年8月24日		Create
 * </pre>
 * @date 2016年8月24日
 */
@Component
public class DmsFolderAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    @Autowired
    private DmsFolderService dmsFolderService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return IexportType.DmsFolder;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader#getAppFunctionSourceByUuid(java.lang.String)
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSourceByUuid(String uuid) {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        DmsFolderEntity dmsFolderEntity = dmsFolderService.get(uuid);
        if (dmsFolderEntity != null) {
            appFunctionSources.add(convert2AppFunctionSource(dmsFolderEntity));
        }
        return appFunctionSources;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        Collection<DmsFolderEntity> dmsFolderEntities = dmsFolderService.getRootFolders();
        for (DmsFolderEntity dmsFolderEntity : dmsFolderEntities) {
            appFunctionSources.add(convert2AppFunctionSource(dmsFolderEntity));
        }
        return appFunctionSources;
    }

    /**
     * 如何描述该方法
     *
     * @param appFunctionSources
     * @param dmsFolderEntity
     */
    private AppFunctionSource convert2AppFunctionSource(DmsFolderEntity dmsFolderEntity) {
        String uuid = dmsFolderEntity.getUuid();
        String fullName = "数据管理_文件夹功能_" + dmsFolderEntity.getName();
        String name = fullName;
        String id = dmsFolderEntity.getUuid();
        String code = dmsFolderEntity.getUuid();
        String category = getAppFunctionType();
        return new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category, true, category, false,
                null);
    }

}
