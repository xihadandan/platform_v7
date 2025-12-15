/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.iexport.acceptor.DataDictionaryIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.jpa.util.HqlUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
public class DataDictionaryIexportDataProvider extends AbstractIexportDataProvider<DataDictionary, String> {

    static {
        // 2.1、数据字典
        TableMetaData.register(IexportType.DataDictionary, "数据字典", DataDictionary.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.DataDictionary;
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
            return new ErrorDataIexportData(IexportType.DataDictionary, "找不到对应的数据字典依赖关系,可能已经被删除", "数据字典", uuid);
        }
        return new DataDictionaryIexportData(dataDictionary);
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
        return new DataDictionaryIexportData(dataDictionary).getName();
    }

    @Override
    public void putChildProtoDataHqlParams(DataDictionary dataDictionary, Map<String, DataDictionary> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        parentMap.put(start + dataDictionary.getUuid(), dataDictionary);
        if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.DataDictionary))) {
            ProtoDataHql protoDataHql = new ProtoDataHql(getType(), "DataDictionary");
            protoDataHql.setGenerateHql(new GenerateHql() {
                @Override
                public void build(ProtoDataHql protoDataHql) {
                    protoDataHql.getSbHql().append("from DataDictionary where ");
                    HqlUtils.appendSql("parent_uuid", protoDataHql.getParams(), protoDataHql.getSbHql(), (Set<Serializable>) protoDataHql.getParams().get("parentUuids"));
                    protoDataHql.getSbHql().append(" order by seq asc ,createTime asc ");
                }
            });

            hqlMap.put(this.getChildHqlKey(IexportType.DataDictionary), protoDataHql);
        }
        this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.DataDictionary)), dataDictionary.getUuid(), "parentUuids");
    }

    @Override
    public Map<String, List<DataDictionary>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<DataDictionary>> map = new HashMap<>();
        List<DataDictionary> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), DataDictionary.class);
        if (protoDataHql.getParentType().equals(IexportType.FormDefinition)) {
            for (DataDictionary dataDictionary : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "dictCode" + Separator.UNDERLINE.getValue() + dataDictionary.getType();
                this.putParentMap(map, dataDictionary, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.DataDictionary)) {
            for (DataDictionary dataDictionary : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + dataDictionary.getParent().getUuid();
                this.putParentMap(map, dataDictionary, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
