/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceDefinition;
import com.wellsoft.pt.basicdata.datasource.iexport.acceptor.DataSourceDefinitionIexportData;
import com.wellsoft.pt.basicdata.datasource.service.DataSourceDefinitionService;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.GenerateHql;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.jpa.util.HqlUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 数据源
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-1-18.1	linz		2016-1-18		Create
 * </pre>
 * @date 2016-1-18
 */
//@Service
//@Transactional(readOnly = true)
public class DataSourceDefinitionIexportDataProvider extends AbstractIexportDataProvider<DataSourceDefinition, String> {
    static {
        TableMetaData.register(IexportType.DataSourceDefinition, "数据源", DataSourceDefinition.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.DataSourceDefinition;
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
        DataSourceDefinition dataSourceDefinition = this.dao.get(DataSourceDefinition.class, uuid);
        if (dataSourceDefinition == null) {
            return new ErrorDataIexportData(IexportType.DataSourceDefinition, "找不到对应的 数据源依赖关系,可能已经被删除", "数据源", uuid);
        }
        return new DataSourceDefinitionIexportData(dataSourceDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#storeData(com.wellsoft.pt.basicdata.iexport.acceptor.IexportData, boolean)
     */
    @Override
    @Transactional(readOnly = false)
    public void storeData(IexportData iexportData, boolean newVer) throws Exception {
        DataSourceDefinition dataSourceDefinition = this.dao.get(DataSourceDefinition.class, iexportData.getUuid());
        if (dataSourceDefinition != null) {
            DataSourceDefinitionService dataSourceDefinitionService = ApplicationContextHolder
                    .getBean(DataSourceDefinitionService.class);
            dataSourceDefinitionService.deleteById(dataSourceDefinition.getId());
            this.dao.getSession().flush();
            this.dao.getSession().clear();
        }
        super.storeData(iexportData, newVer);
    }

    @Override
    public String getTreeName(DataSourceDefinition dataSourceDefinition) {
        return new DataSourceDefinitionIexportData(dataSourceDefinition).getName();
    }


    @Override
    public void putChildProtoDataHqlParams(DataSourceDefinition dataSourceDefinition, Map<String, DataSourceDefinition> parentMap, Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        String key = start + dataSourceDefinition.getUuid();
        parentMap.put(key, dataSourceDefinition);
        if (StringUtils.isNotBlank(dataSourceDefinition.getOutDataSourceId())) {
            String key1 = start + dataSourceDefinition.getOutDataSourceId();
            parentMap.put(key1, dataSourceDefinition);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.DataSourceProfile))) {
                hqlMap.put(this.getChildHqlKey(IexportType.DataSourceProfile), this.getProtoDataHql("DataSourceProfile"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.DataSourceProfile)), dataSourceDefinition.getOutDataSourceId());
        }
        if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.DataSourceColumn))) {
            ProtoDataHql protoDataHql = new ProtoDataHql(getType(), "DataSourceColumn");
            protoDataHql.setGenerateHql(new GenerateHql() {
                @Override
                public void build(ProtoDataHql protoDataHql) {
                    protoDataHql.getSbHql().append("from DataSourceColumn where ");
                    HqlUtils.appendSql("source_def_uuid", protoDataHql.getParams(), protoDataHql.getSbHql(), (Set<Serializable>) protoDataHql.getParams().get("uuids"));
                }
            });

            hqlMap.put(this.getChildHqlKey(IexportType.DataSourceColumn), protoDataHql);
        }
        this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.DataSourceColumn)), dataSourceDefinition.getUuid());
    }

    @Override
    public Map<String, List<DataSourceDefinition>> getParentMapList(ProtoDataHql protoDataHql) {
        List<DataSourceDefinition> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), DataSourceDefinition.class);
        Map<String, List<DataSourceDefinition>> map = new HashMap<>();
        // 页面或组件定义依赖的数据源定义
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (DataSourceDefinition dataSourceDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, dataSourceDefinition, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (DataSourceDefinition dataSourceDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + dataSourceDefinition.getUuid();
                this.putParentMap(map, dataSourceDefinition, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }

        return map;
    }
}
