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
import com.wellsoft.pt.basicdata.exceltemplate.entity.ExcelImportRule;
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
public class ExcelImportRuleAppFunctionSourceLoader extends AbstractAppFunctionSourceLoader {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionType()
     */
    @Override
    public String getAppFunctionType() {
        return AppFunctionType.ExcelImportRule;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.function.AppFunctionSourceLoader#getAppFunctionSources()
     */
    @Override
    public List<AppFunctionSource> getAppFunctionSources() {
        List<AppFunctionSource> appFunctionSources = new ArrayList<AppFunctionSource>();
        List<ExcelImportRule> excelImportRules = this.dao.getAll(ExcelImportRule.class);
        for (ExcelImportRule excelImportRule : excelImportRules) {
            appFunctionSources.add(convert2AppFunctionSource(excelImportRule));
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
        ExcelImportRule excelImportRule = (ExcelImportRule) item;
        String uuid = excelImportRule.getUuid();
        String fullName = excelImportRule.getName();
        String name = "数据导入规则_" + excelImportRule.getName();
        String id = excelImportRule.getId();
        String code = excelImportRule.getCode();
        String category = getAppFunctionType();
        return new SimpleAppFunctionSource(uuid, fullName, name, id, code, null, null, category, true, category, false,
                null);
    }
}
