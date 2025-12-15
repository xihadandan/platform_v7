/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceColumn;
import com.wellsoft.pt.basicdata.datasource.iexport.acceptor.DataSourceColumnIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据源列表
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
@Service
@Transactional(readOnly = true)
public class DataSourceColumnIexportDataProvider extends AbstractIexportDataProvider<DataSourceColumn, String> {
    static {
        TableMetaData.register(IexportType.DataSourceColumn, "数据源列", DataSourceColumn.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.DataSourceColumn;
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
        DataSourceColumn dataSourceColumn = this.dao.get(DataSourceColumn.class, uuid);
        if (dataSourceColumn == null) {
            return new ErrorDataIexportData(IexportType.DataSourceColumn, "找不到对应的数据源列表依赖关系,可能已经被删除", "数据源列表", uuid);
        }
        return new DataSourceColumnIexportData(dataSourceColumn);
    }


    @Override
    public String getTreeName(DataSourceColumn dataSourceColumn) {
        return new DataSourceColumnIexportData(dataSourceColumn).getName();
    }

    @Override
    public Map<String, List<DataSourceColumn>> getParentMapList(ProtoDataHql protoDataHql) {
        List<DataSourceColumn> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), DataSourceColumn.class);
        Map<String, List<DataSourceColumn>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.DataSourceDefinition)) {
            for (DataSourceColumn dataSourceColumn : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + dataSourceColumn.getDataSourceDefinition().getUuid();
                this.putParentMap(map, dataSourceColumn, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;

    }
}
