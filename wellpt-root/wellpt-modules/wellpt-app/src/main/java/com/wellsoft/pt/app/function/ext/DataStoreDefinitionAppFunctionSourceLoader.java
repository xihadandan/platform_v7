/*
 * @(#)Jul 19, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function.ext;

import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.AppFunctionSourceLoader;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.basicdata.datastore.entity.CdDataStoreDefinition;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
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
 * Jul 19, 2017.1	zhulh		Jul 19, 2017		Create
 * </pre>
 * @date Jul 19, 2017
 */
@Service
@Transactional(readOnly = true)
public class DataStoreDefinitionAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.DataStoreDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader#getAppFunctionSourceByUuid(java.lang.String)
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSourceByUuid(String uuid) {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        CdDataStoreDefinition dataStoreDefinition = this.dao.get(CdDataStoreDefinition.class, uuid);
        if (dataStoreDefinition != null) {
            appFunctionSources.add(convert2AppFunctionSource(dataStoreDefinition));
        }
        return appFunctionSources;
    }

    /**
     * (non-Javadoc)
     *
     * @param id
     * @see AppFunctionSourceLoader#getAppFunctionSourceByUuid(String)
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSourceById(String id) {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        List<CdDataStoreDefinition> dataStoreDefinitions = this.dao.get(CdDataStoreDefinition.class, "id", new String[]{id});
        if (CollectionUtils.isNotEmpty(dataStoreDefinitions)) {
            dataStoreDefinitions.forEach(dataStoreDefinition -> appFunctionSources.add(convert2AppFunctionSource(dataStoreDefinition)));
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
        List<CdDataStoreDefinition> dataStoreDefinitions = this.dao.getAll(CdDataStoreDefinition.class);
        for (CdDataStoreDefinition cdDataStoreDefinition : dataStoreDefinitions) {
            appFunctionSources.add(convert2AppFunctionSource(cdDataStoreDefinition));
        }
        return appFunctionSources;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader#convert2AppFunctionSource(java.io.Serializable)
     */
    @Override
    public <ITEM extends Serializable> AppFunctionSource convert2AppFunctionSource(ITEM item) {
        CdDataStoreDefinition dataStoreDefinition = (CdDataStoreDefinition) item;
        String uuid = dataStoreDefinition.getUuid();
        String fullName = dataStoreDefinition.getName() + "_" + dataStoreDefinition.getId();
        String name = "数据仓库_" + dataStoreDefinition.getName();
        String id = dataStoreDefinition.getId();
        String code = dataStoreDefinition.getId();
        String category = getAppFunctionType();
        return new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category, true, category, false,
                null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader#getObjectIdentities(com.wellsoft.pt.app.function.AppFunctionSource)
     */
    @Override
    public Collection<Object> getObjectIdentities(AppFunctionSource appFunctionSource) {
        Collection<Object> objectIdentities = super.getObjectIdentities(appFunctionSource);
        objectIdentities.add(appFunctionSource.getId());
        return objectIdentities;
    }


}
