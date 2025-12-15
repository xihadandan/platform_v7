/*
 * @(#)2016-11-07 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.function.ext;

import com.wellsoft.pt.app.function.AbstractAppFunctionSourceLoader;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.SimpleAppFunctionSource;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.basicdata.excelexporttemplate.entity.ExcelExportDefinition;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
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
 * 2016-11-07.1	zhulh		2016-11-07		Create
 * </pre>
 * @date 2016-11-07
 */
@Service
@Transactional(readOnly = true)
public class ExcelExportDefinitionAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.ExcelExportDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        List<ExcelExportDefinition> excelExportDefinitions = this.dao.getAll(ExcelExportDefinition.class);
        for (ExcelExportDefinition excelExportDefinition : excelExportDefinitions) {
            appFunctionSources.add(convert2AppFunctionSource(excelExportDefinition));
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
        ExcelExportDefinition excelExportDefinition = (ExcelExportDefinition) item;
        String uuid = excelExportDefinition.getUuid();
        String fullName = excelExportDefinition.getName();
        String name = "数据导出规则_" + excelExportDefinition.getName();
        String id = excelExportDefinition.getId();
        String code = excelExportDefinition.getCode();
        String category = getAppFunctionType();
        return new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category, true, category, false,
                null);
    }
}
