/*
 * @(#)2016年8月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.iexport.acceptor;

import com.google.gson.Gson;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
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
 * 2016年8月4日.1	zhulh		2016年8月4日		Create
 * </pre>
 * @date 2016年8月4日
 */
public class AppWidgetDefinitionIexportData extends IexportData {

    public AppWidgetDefinition appWidgetDefinition;

    /**
     * @param appProduct
     */
    public AppWidgetDefinitionIexportData(AppWidgetDefinition appWidgetDefinition) {
        this.appWidgetDefinition = appWidgetDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return appWidgetDefinition.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getName()
     */
    @Override
    public String getName() {
        return "组件定义: " + appWidgetDefinition.getName();
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getType()
     */
    @Override
    public String getType() {
        return IexportType.AppWidgetDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getRecVer()
     */
    @Override
    public Integer getRecVer() {
        return appWidgetDefinition.getRecVer();
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        List<String> fileIds = new ArrayList<String>();
        // 表格组件的带有附件
        if ("wBootstrapTable".equalsIgnoreCase(appWidgetDefinition.getWtype())) {
            String defineJson = appWidgetDefinition.getDefinitionJson();
            Map<String, Object> defineMap = new Gson().fromJson(defineJson, Map.class);
            Map<String, Object> configuration = (Map) defineMap.get("configuration");
            // 导出功能的导出模板
            String fileId = (String) configuration.get("exportTemplateFileId");
            if (StringUtils.isNotBlank(fileId)) {
                fileIds.add(fileId);
            }

            // 导入功能的导入模板
            List<Map<String, Object>> dataImports = (List) configuration.get("dataImport");
            if (CollectionUtils.isNotEmpty(dataImports)) {
                for (Map imports : dataImports) {
                    Map importConf = (Map) imports.get("dataImportConfiguration");
                    if (importConf != null) {
                        String importFileId = (String) importConf.get("importTemplate");
                        if (StringUtils.isNotBlank(importFileId)) {
                            fileIds.add(importFileId);
                        }
                    }
                }
            }

        }

        return IexportDataResultSetUtils.mongoFileResultInputStream(this, appWidgetDefinition, fileIds);
    }

    /**
     * (non-Javadoc)
     *
     * @see IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = new ArrayList<IexportData>();
        return dependencies;
    }

}
