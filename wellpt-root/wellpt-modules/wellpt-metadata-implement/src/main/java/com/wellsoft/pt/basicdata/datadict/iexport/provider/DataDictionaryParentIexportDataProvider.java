/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.iexport.acceptor.DataDictionaryParentIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据字典接口
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-16.1	linz		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
@Service
@Transactional(readOnly = true)
public class DataDictionaryParentIexportDataProvider extends AbstractIexportDataProvider<DataDictionary, String> {

    static {
        // 2.1、数据字典
        TableMetaData.register(IexportType.DataDictionaryParent, "数据字典", DataDictionary.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.DataDictionaryParent;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        DataDictionary dataDictionary = this.dao.get(DataDictionary.class, uuid);
        if (dataDictionary == null) {
            return new ErrorDataIexportData(IexportType.DataDictionaryParent, "找不到对应的数据字典依赖关系,可能已经被删除", "数据字典", uuid);
        }
        return new DataDictionaryParentIexportData(dataDictionary);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 数据字典code生成方式
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.DataDictionary), "code",
                new ColumnValueProcessor() {

                    @Override
                    public Object doProcess(Object sourceValue, IexportDataRecord dataRecord, IexportData iexportData,
                                            IexportBundle iexportBundle) {
                        String code = dataRecord.getString("code");
                        IdGeneratorService idGeneratorService = ApplicationContextHolder
                                .getBean(IdGeneratorService.class);
                        return code + Separator.UNDERLINE.getValue() + idGeneratorService.getBySysDate();
                    }

                });
        return iexportMetaData;
    }


    @Override
    public String getTreeName(DataDictionary dataDictionary) {
        return new DataDictionaryParentIexportData(dataDictionary).getName();
    }

    @Override
    public void putChildProtoDataHqlParams(DataDictionary dataDictionary, Map<String, DataDictionary> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        if (dataDictionary.getParent() != null) {
            parentMap.put(start + dataDictionary.getParent().getUuid(), dataDictionary);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.DataDictionaryParent))) {
                hqlMap.put(this.getChildHqlKey(IexportType.DataDictionaryParent), this.getProtoDataHql("DataDictionary"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.DataDictionaryParent)), dataDictionary.getParent().getUuid());
        }

        if (StringUtils.isNotBlank(dataDictionary.getModuleId())) {
            this.putAppFunctionParentMap(dataDictionary, parentMap, hqlMap);
        }
    }


    @Override
    public Map<String, List<DataDictionary>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<DataDictionary>> map = new HashMap<>();
        List<DataDictionary> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), DataDictionary.class);
        // 页面或组件定义依赖的数据字典
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (DataDictionary dataDictionary : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, dataDictionary, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.FormDefinition)) {
            for (DataDictionary dataDictionary : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "dictCode" + Separator.UNDERLINE.getValue() + dataDictionary.getType();
                this.putParentMap(map, dataDictionary, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.DataDictionaryParent)) {
            for (DataDictionary dataDictionary : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + dataDictionary.getUuid();
                this.putParentMap(map, dataDictionary, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (DataDictionary dataDictionary : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + dataDictionary.getUuid();
                this.putParentMap(map, dataDictionary, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.SystemTable)) {
            for (DataDictionary dataDictionary : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "MODULE_ID";
                this.putParentMap(map, dataDictionary, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
