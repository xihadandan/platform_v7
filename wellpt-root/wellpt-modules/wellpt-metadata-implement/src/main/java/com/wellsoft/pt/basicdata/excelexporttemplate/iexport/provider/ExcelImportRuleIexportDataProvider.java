/*
 * @(#)2016-11-07 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.excelexporttemplate.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.excelexporttemplate.iexport.acceptor.ExcelImportRuleIexportData;
import com.wellsoft.pt.basicdata.exceltemplate.entity.ExcelImportRule;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.PrintMongoFileSerializable;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.service.IexportDataRecordSetService;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.repository.service.MongoFileService;
import org.apache.commons.lang.StringUtils;
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
public class ExcelImportRuleIexportDataProvider extends AbstractIexportDataProvider<ExcelImportRule, String> {
    static {
        TableMetaData.register(IexportType.ExcelImportRule, "数据导入规则", ExcelImportRule.class);
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
        return IexportType.ExcelImportRule;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        ExcelImportRule excelImportRule = this.dao.get(ExcelImportRule.class, uuid);
        if (excelImportRule == null) {
            return new ErrorDataIexportData(IexportType.ExcelImportRule, "找不到对应的数据导入规则依赖关系,可能已经被删除", "数据导入规则", uuid);
        }
        return new ExcelImportRuleIexportData(excelImportRule);
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
            ExcelImportRule excelImportRule = this.dao.get(ExcelImportRule.class, iexportData.getUuid());
            mongoFileService.saveFile(excelImportRule.getFileUuid(), printMongoFileSerializable.getFileName(),
                    new ByteArrayInputStream(fileArray));
            mongoFileService.pushFileToFolder(excelImportRule.getUuid(), excelImportRule.getFileUuid(), "attach");
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
        // 数据导入规则ID生成方式
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.ExcelImportRule), "id",
                EntityIdColumnValueProcessorFactory.getColumnValueProcessor(ExcelImportRule.class));
        return iexportMetaData;
    }

    @Override
    public String getTreeName(ExcelImportRule excelImportRule) {
        return new ExcelImportRuleIexportData(excelImportRule).getName();
    }

    @Override
    public Set<String> getFileIds(ExcelImportRule excelImportRule) {
        if (StringUtils.isNotBlank(excelImportRule.getFileUuid())) {
            Set<String> fileIds = new HashSet<>();
            fileIds.add(excelImportRule.getFileUuid());
            return fileIds;
        }
        return null;
    }

    @Override
    public void putChildProtoDataHqlParams(ExcelImportRule excelImportRule, Map<String, ExcelImportRule> parentMap, Map<String, ProtoDataHql> hqlMap) {
        if (StringUtils.isNotBlank(excelImportRule.getModuleId())) {
            this.putAppFunctionParentMap(excelImportRule, parentMap, hqlMap);
        }
    }

    @Override
    public Map<String, List<ExcelImportRule>> getParentMapList(ProtoDataHql protoDataHql) {
        List<ExcelImportRule> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), ExcelImportRule.class);
        Map<String, List<ExcelImportRule>> map = new HashMap<>();
        // 页面或组件定义依赖的excel导入模板
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (ExcelImportRule excelImportRule : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, excelImportRule, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (ExcelImportRule excelImportRule : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + excelImportRule.getUuid();
                this.putParentMap(map, excelImportRule, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
