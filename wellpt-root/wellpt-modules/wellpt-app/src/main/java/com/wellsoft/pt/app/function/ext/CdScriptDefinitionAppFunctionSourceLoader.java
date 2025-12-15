/*
 * @(#)Sep 6, 2016 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function.ext;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.basicdata.script.entity.CdScriptDefinitionEntity;
import com.wellsoft.pt.basicdata.script.service.CdScriptDefinitionService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 组件定义
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-09-06.1	zhulh		2016-09-06		Create
 * </pre>
 * @date 2016-09-06
 */
@Service
@Transactional(readOnly = true)
public class CdScriptDefinitionAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    @Autowired
    private CdScriptDefinitionService cdScriptDefinitionService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.CdScriptDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        List<CdScriptDefinitionEntity> cdScriptDefinitionEntities = cdScriptDefinitionService.listAll();
        for (CdScriptDefinitionEntity cdScriptDefinitionEntity : cdScriptDefinitionEntities) {
            appFunctionSources.add(convert2AppFunctionSource(cdScriptDefinitionEntity));
        }
        return appFunctionSources;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#convert2AppFunctionSource(java.io.Serializable)
     */
    @Override
    public <ITEM extends Serializable> AppFunctionSource convert2AppFunctionSource(ITEM item) {
        CdScriptDefinitionEntity cdScriptDefinitionEntity = (CdScriptDefinitionEntity) item;
        String uuid = cdScriptDefinitionEntity.getUuid();
        String fullName = cdScriptDefinitionEntity.getName();
        String name = "脚本定义_" + fullName;
        String id = cdScriptDefinitionEntity.getId();
        String code = id.hashCode() + StringUtils.EMPTY;
        String category = getAppFunctionType();
        Map<String, Object> extras = new HashMap<String, Object>();
        extras.put(IdEntity.UUID, uuid);
        return new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category, true, category, false,
                extras);
    }

}
