/*
 * @(#)2017年3月13日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.iexport.acceptor;

import com.wellsoft.pt.basicdata.datastore.entity.CdDataStoreDefinition;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年3月13日.1	zhulh		2017年3月13日		Create
 * </pre>
 * @date 2017年3月13日
 */
public class DataStoreDefinitionIexportData extends IexportData {

    private CdDataStoreDefinition cdDataStoreDefinition;

    public DataStoreDefinitionIexportData(CdDataStoreDefinition cdDataStoreDefinition) {
        this.cdDataStoreDefinition = cdDataStoreDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return this.cdDataStoreDefinition.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getName()
     */
    @Override
    public String getName() {
        return "数据仓库：" + this.cdDataStoreDefinition.getName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getType()
     */
    @Override
    public String getType() {
        return IexportType.DataStoreDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getRecVer()
     */
    @Override
    public Integer getRecVer() {
        return this.cdDataStoreDefinition.getRecVer();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, this.cdDataStoreDefinition);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = new ArrayList<IexportData>();
        return dependencies;
    }

}
