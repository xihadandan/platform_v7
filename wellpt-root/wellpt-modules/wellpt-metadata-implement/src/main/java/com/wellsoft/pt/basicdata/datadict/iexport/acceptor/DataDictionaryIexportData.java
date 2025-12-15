/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.iexport.acceptor;

import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataProviderFactory;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 数据字典数据源
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-29.1	linz		2015-6-29		Create
 * </pre>
 * @date 2015-6-29
 */
public class DataDictionaryIexportData extends IexportData {
    public DataDictionary dataDictionary;

    public DataDictionaryIexportData(DataDictionary dataDictionary) {
        this.dataDictionary = dataDictionary;
        // 根结点优化导入
        setPriority(false);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return dataDictionary.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.Entry#getName()
     */
    @Override
    public String getName() {
        return "数据字典-->名称【" + dataDictionary.getName() + "】依赖类型-->【向下依赖】";
    }

    @Override
    public Integer getRecVer() {
        // TODO Auto-generated method stub
        return dataDictionary.getRecVer();
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
        return IexportType.DataDictionary;
    }

    /**
     * (non-Javadoc)
     *
     * @throws IOException
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, dataDictionary);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = new ArrayList<IexportData>();
        for (DataDictionary dataDictionaryTemp : dataDictionary.getChildren()) {
            dependencies.add(IexportDataProviderFactory.getDataProvider(IexportType.DataDictionary).getData(
                    dataDictionaryTemp.getUuid()));
        }
        return dependencies;
    }

}
