/*
 * @(#)2016-11-07 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.excelexporttemplate.entity.ExcelExportDefinition;
import com.wellsoft.pt.basicdata.excelexporttemplate.iexport.acceptor.ExcelExportDefinitionIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.PrintMongoFileSerializable;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.service.IexportDataRecordSetService;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.util.*;

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
public class ExcelExportDefinitionIexportDataProvider extends AbstractIexportDataProvider<ExcelExportDefinition, String> {

    static {
        TableMetaData.register(IexportType.ExcelExportDefinition, "数据导出规则", ExcelExportDefinition.class);
    }

    @Autowired
    private IexportDataRecordSetService iexportDataMetaDataService;
    @Autowired
    private MongoFileService mongoFileService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.ExcelExportDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        ExcelExportDefinition excelExportDefinition = this.dao.get(ExcelExportDefinition.class, uuid);
        if (excelExportDefinition == null) {
            return new ErrorDataIexportData(IexportType.ExcelExportDefinition, "找不到对应的数据导出规则依赖关系,可能已经被删除", "数据导出规则",
                    uuid);
        }
        return new ExcelExportDefinitionIexportData(excelExportDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#storeData(com.wellsoft.pt.basicdata.iexport.acceptor.IexportData, boolean)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void storeData(IexportData iexportData, boolean newVer) throws Exception {
        Object object = IexportDataResultSetUtils.inputStream2Object(iexportData.getInputStream());
        Map<String, Object> dataMap = (Map<String, Object>) object;
        IexportDataRecordSet printIexportData = (IexportDataRecordSet) dataMap
                .get(IexportDataResultSetUtils.ENTITY_BEAN);
        iexportDataMetaDataService.save(printIexportData);
        List<PrintMongoFileSerializable> printMongoFileSerializables = (List<PrintMongoFileSerializable>) dataMap
                .get(IexportDataResultSetUtils.MONGO_FILES);
        if (printMongoFileSerializables == null || printMongoFileSerializables.isEmpty()) {
            return;
        }
        for (PrintMongoFileSerializable printMongoFileSerializable : printMongoFileSerializables) {
            byte fileArray[] = printMongoFileSerializable.getFileArray();
            ExcelExportDefinition excelExportDefinition = this.dao.get(ExcelExportDefinition.class,
                    iexportData.getUuid());
            mongoFileService.saveFile(excelExportDefinition.getFileUuid(), printMongoFileSerializable.getFileName(),
                    new ByteArrayInputStream(fileArray));
            mongoFileService.pushFileToFolder(excelExportDefinition.getUuid(), excelExportDefinition.getFileUuid(),
                    "attach");
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 数据导出规则ID生成方式
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.ExcelExportDefinition),
                "id", EntityIdColumnValueProcessorFactory.getColumnValueProcessor(ExcelExportDefinition.class));
        return iexportMetaData;
    }

    @Override
    public String getTreeName(ExcelExportDefinition excelExportDefinition) {
        return new ExcelExportDefinitionIexportData(excelExportDefinition).getName();
    }

    @Override
    public Set<String> getFileIds(ExcelExportDefinition excelExportDefinition) {
        if (StringUtils.isNotBlank(excelExportDefinition.getFileUuid())) {
            Set<String> fileIds = new HashSet<>();
            fileIds.add(excelExportDefinition.getFileUuid());
            return fileIds;
        }
        return null;
    }

    @Override
    public Map<String, List<ExcelExportDefinition>> getParentMapList(ProtoDataHql protoDataHql) {
        List<ExcelExportDefinition> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), ExcelExportDefinition.class);
        Map<String, List<ExcelExportDefinition>> map = new HashMap<>();
        // 页面或组件定义依赖的excel导出模板
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (ExcelExportDefinition excelExportDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, excelExportDefinition, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (ExcelExportDefinition excelExportDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + excelExportDefinition.getUuid();
                this.putParentMap(map, excelExportDefinition, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
