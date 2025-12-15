/*
 * @(#)2016年8月2日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function.ext;

import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
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
 * 2016年8月2日.1	zhulh		2016年8月2日		Create
 * </pre>
 * @date 2016年8月2日
 */
@Service
@Transactional(readOnly = true)
public class FlowDefinitionAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.FlowDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        List<FlowDefinition> flowDefinitions = this.dao.getAll(FlowDefinition.class);
        for (FlowDefinition flowDefinition : flowDefinitions) {
            appFunctionSources.add(convert2AppFunctionSource(flowDefinition));
        }
        return appFunctionSources;
    }

    @Override
    public List<AppFunctionSource> getAppFunctionSourceByUuid(String uuid) {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        FlowDefinition flowDefinitions = this.dao.get(FlowDefinition.class, uuid);
        if (flowDefinitions != null) {
            appFunctionSources.add(convert2AppFunctionSource(flowDefinitions));
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
        FlowDefinition flowDefinition = (FlowDefinition) item;
        String uuid = flowDefinition.getUuid();
        String fullName = flowDefinition.getName() + "_" + flowDefinition.getVersion();
        String name = "流程定义_" + flowDefinition.getName() + "_" + flowDefinition.getVersion();
        String id = flowDefinition.getId() + "_" + flowDefinition.getVersion();
        String code = flowDefinition.getCode();
        String category = getAppFunctionType();
        String newWorkUrl = "/workflow/work/v53/new/" + flowDefinition.getId();
        Map<String, Object> extras = new HashMap<String, Object>();
        extras.put("newWorkUrl", newWorkUrl);
        return new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category,
                true, category, false,
                extras);
    }
}
