/*
 * @(#)2016年8月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.iexport;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.context.util.collection.ListUtils;
import com.wellsoft.pt.app.entity.AppWidgetDefinition;
import com.wellsoft.pt.app.iexport.acceptor.AppWidgetDefinitionIexportData;
import com.wellsoft.pt.app.support.WidgetDefinitionUtils;
import com.wellsoft.pt.app.ui.ReadonlyWidgetDefinitionView;
import com.wellsoft.pt.app.ui.Widget;
import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElement;
import com.wellsoft.pt.app.ui.client.widget.configuration.FunctionElementDependenciesConfiguration;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.PrintMongoFileSerializable;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;

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
@Service
@Transactional(readOnly = true)
public class AppWidgetDefinitionIexportDataProvider extends AbstractIexportDataProvider<AppWidgetDefinition, String> {
    static {
        TableMetaData.register(IexportType.AppWidgetDefinition, "组件", AppWidgetDefinition.class);
    }

    @Autowired
    MongoFileService mongoFileService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.AppWidgetDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(String)
     */
    @Override
    public IexportData getData(String uuid) {
        AppWidgetDefinition appWidgetDefinition = this.dao.get(AppWidgetDefinition.class, uuid);
        if (appWidgetDefinition == null) {
            return new ErrorDataIexportData(getType(), "找不到对应的组件依赖关系", "组件定义", uuid);
        }
        return new AppWidgetDefinitionIexportData(appWidgetDefinition);
    }

    @Override
    @Transactional
    public void storeData(IexportData iexportData, boolean newVer) throws Exception {
        Object object = IexportDataResultSetUtils.inputStream2Object(iexportData.getInputStream());
        IexportDataRecordSet exportRecordSet = null;
        List<PrintMongoFileSerializable> printMongoFileSerializables = null;
        if (object instanceof IexportDataRecordSet) {
            exportRecordSet = (IexportDataRecordSet) object;
        } else {
            Map<String, Object> dataMap = (Map<String, Object>) object;
            exportRecordSet = (IexportDataRecordSet) dataMap
                    .get(IexportDataResultSetUtils.ENTITY_BEAN);
            //保存附件
            printMongoFileSerializables = (List<PrintMongoFileSerializable>) dataMap
                    .get(IexportDataResultSetUtils.MONGO_FILES);
        }
        iexportDataMetaDataService.save(exportRecordSet);
        if (printMongoFileSerializables == null || printMongoFileSerializables.isEmpty()) {
            return;
        }
        for (PrintMongoFileSerializable printMongoFileSerializable : printMongoFileSerializables) {
            byte fileArray[] = printMongoFileSerializable.getFileArray();
            mongoFileService.saveFile(printMongoFileSerializable.getFileId(),
                    printMongoFileSerializable.getFileName(),
                    new ByteArrayInputStream(fileArray));
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 组件ID生成方式
        iexportMetaData.registerColumnValueProcessor(
                TableMetaData.getTableName(IexportType.AppWidgetDefinition), "id",
                new ColumnValueProcessor() {

                    private Map<String, String> newWidgetIdMap = Maps.newHashMap();

                    @Override
                    public Object doProcess(Object sourceValue, IexportDataRecord dataRecord,
                                            IexportData iexportData,
                                            IexportBundle iexportBundle) {
                        String primaryKeyValue = dataRecord.getPrimaryKeyValue();
                        // 组件ID有新值时，直接返回
                        if (newWidgetIdMap.containsKey(primaryKeyValue)) {
                            return newWidgetIdMap.get(primaryKeyValue);
                        }

                        String wtype = dataRecord.getString("wtype");
                        String newId = wtype + Separator.UNDERLINE.getValue()
                                + StringUtils.upperCase(UuidUtils.createUuid());
                        newWidgetIdMap.put(primaryKeyValue, newId);
                        return newId;
                    }

                });

        return iexportMetaData;
    }

    @Override
    public String getTreeName(AppWidgetDefinition appWidgetDefinition) {
        return new AppWidgetDefinitionIexportData(appWidgetDefinition).getName();
    }

    @Override
    public Set<String> getFileIds(AppWidgetDefinition appWidgetDefinition) {
        Set<String> fileIds = new HashSet<>();
        // 表格组件的带有附件
        if ("wBootstrapTable".equalsIgnoreCase(appWidgetDefinition.getWtype())) {
            String defineJson = appWidgetDefinition.getDefinitionJson();
            Map<String, Object> defineMap = new Gson().fromJson(defineJson, Map.class);
            Map<String, Object> configuration = (Map) defineMap.get("configuration");
            // 导出功能的导出模板
            String fileId = (String) configuration.get("exportTemplateFileId");
            if (org.apache.commons.lang3.StringUtils.isNotBlank(fileId)) {
                fileIds.add(fileId);
            }

            // 导入功能的导入模板
            List<Map<String, Object>> dataImports = (List) configuration.get("dataImport");
            if (CollectionUtils.isNotEmpty(dataImports)) {
                for (Map imports : dataImports) {
                    Map importConf = (Map) imports.get("dataImportConfiguration");
                    if (importConf != null) {
                        String importFileId = (String) importConf.get("importTemplate");
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(importFileId)) {
                            fileIds.add(importFileId);
                        }
                    }
                }
            }
        }
        if (fileIds.size() == 0) {
            return null;
        }
        return fileIds;
    }

    @Override
    public Map<String, List<AppWidgetDefinition>> getParentMapList(ProtoDataHql protoDataHql) {
        List<AppWidgetDefinition> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), AppWidgetDefinition.class);
        Map<String, List<AppWidgetDefinition>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)) {
            Collections.sort(list, protoDataHql.getComparator());
            for (AppWidgetDefinition appWidgetDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + appWidgetDefinition.getAppPageUuid();
                this.putParentMap(map, appWidgetDefinition, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (AppWidgetDefinition appWidgetDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + appWidgetDefinition.getUuid();
                this.putParentMap(map, appWidgetDefinition, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.FormDefinition)) {
            for (AppWidgetDefinition appWidgetDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "viewStore" + Separator.UNDERLINE.getValue() + appWidgetDefinition.getUuid();
                this.putParentMap(map, appWidgetDefinition, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#putChildProtoDataHqlParams(com.wellsoft.context.jdbc.entity.IdEntity, java.util.Map, java.util.Map)
     */
    @Override
    public void putChildProtoDataHqlParams(AppWidgetDefinition appWidgetDefinition, Map<String, AppWidgetDefinition> parentMap, Map<String, ProtoDataHql> hqlMap) {
        // 组件定义依赖的元素
        Widget widget = WidgetDefinitionUtils.parseWidget(appWidgetDefinition.getDefinitionJson(), ReadonlyWidgetDefinitionView.class);
        FunctionElementDependenciesConfiguration dependenciesConfiguration = widget.getConfiguration(FunctionElementDependenciesConfiguration.class);
        List<FunctionElement> dependenciesFunctionElements = dependenciesConfiguration.getDependencies();
        Map<String, List<FunctionElement>> exportTypeMap = ListUtils.list2group(dependenciesFunctionElements, "exportType");
        for (Entry<String, List<FunctionElement>> entry : exportTypeMap.entrySet()) {
            List<FunctionElement> functionElements = entry.getValue();
            for (FunctionElement functionElement : functionElements) {
                String type = functionElement.getExportType();
                String childHqlkey = this.getChildHqlKey(type);
                ProtoDataHql protoDataHql = hqlMap.get(childHqlkey);
                if (protoDataHql == null) {
                    String entityName = IexportType.exportType2EntityName(type);
                    protoDataHql = new ProtoDataHql(getType(), entityName);
                    protoDataHql.setGenerateHql(new GenerateHql() {
                        @Override
                        public void build(ProtoDataHql protoDataHql) {
                            protoDataHql.getSbHql().append("from ").append(protoDataHql.getEntityName()).append(" where ");
                            HqlUtils.appendSql("uuid", protoDataHql.getParams(), protoDataHql.getSbHql(), (Set<Serializable>) protoDataHql.getParams().get("uuids"));
                        }
                    });
                    hqlMap.put(childHqlkey, protoDataHql);
                    protoDataHql.getParams().put("dependencyUuid", appWidgetDefinition.getUuid());
                }
                this.addDataUuid(hqlMap.get(childHqlkey), functionElement.getUuid());
            }
        }
    }

}
