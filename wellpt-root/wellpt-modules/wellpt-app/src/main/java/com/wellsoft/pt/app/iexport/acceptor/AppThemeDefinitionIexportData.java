/*
 * @(#)2/28/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.iexport.acceptor;

import com.google.common.collect.Lists;
import com.wellsoft.pt.app.entity.AppThemeDefinitionEntity;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;

import java.io.IOException;
import java.io.InputStream;
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
 * 2/28/23.1	zhulh		2/28/23		Create
 * </pre>
 * @date 2/28/23
 */
public class AppThemeDefinitionIexportData extends IexportData {

    public AppThemeDefinitionEntity themeDefinitionEntity;

    /**
     * @param themeDefinitionEntity
     */
    public AppThemeDefinitionIexportData(AppThemeDefinitionEntity themeDefinitionEntity) {
        this.themeDefinitionEntity = themeDefinitionEntity;
    }

    @Override
    public String getUuid() {
        return themeDefinitionEntity.getUuid();
    }

    @Override
    public String getName() {
        return "主题定义: " + themeDefinitionEntity.getName();
    }

    @Override
    public String getType() {
        return IexportType.AppThemeDefinition;
    }

    @Override
    public Integer getRecVer() {
        return themeDefinitionEntity.getRecVer();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, themeDefinitionEntity);
    }

    @Override
    public List<IexportData> getDependencies() {
        return Lists.newArrayListWithExpectedSize(0);
    }
}
