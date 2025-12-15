/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.iexport.acceptor;

import com.wellsoft.pt.basicdata.datasource.entity.DataSourceColumn;
import com.wellsoft.pt.basicdata.datasource.entity.DataSourceDefinition;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataProviderFactory;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Description: 数据源定义
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-01-18	linz		2016-01-18		Create
 * </pre>
 * @date 2016-01-18
 */
public class DataSourceDefinitionIexportData extends IexportData {
    public DataSourceDefinition dataSourceDefinition;

    public DataSourceDefinitionIexportData(DataSourceDefinition dataSourceDefinition) {
        setPriority(false);
        this.dataSourceDefinition = dataSourceDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return dataSourceDefinition.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getName()
     */
    @Override
    public String getName() {
        return "数据源：" + dataSourceDefinition.getDataSourceName();
    }

    @Override
    public Integer getRecVer() {
        // TODO Auto-generated method stub
        return dataSourceDefinition.getRecVer();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getType()
     */
    @Override
    public String getType() {
        return IexportType.DataSourceDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @throws IOException
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, dataSourceDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = new ArrayList<IexportData>();
        Set<DataSourceColumn> dataSourceColumns = dataSourceDefinition.getDataSourceColumns();
        for (DataSourceColumn dataSourceColumn : dataSourceColumns) {
            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.DataSourceColumn).getData(
                    dataSourceColumn.getUuid()));
        }
        //外部数据源
        if (StringUtils.isNotBlank(dataSourceDefinition.getOutDataSourceId())) {
            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.DataSourceProfile).getData(
                    dataSourceDefinition.getOutDataSourceId()));
        }
        return dependencies;
    }
}
