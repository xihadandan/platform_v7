/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.acceptor;

import com.wellsoft.context.util.serialization.SerializationUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataRecord;
import com.wellsoft.pt.basicdata.iexport.suport.IexportMetaData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportStorageData;
import com.wellsoft.pt.basicdata.iexport.visitor.Visitor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-6-16.1	zhulh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
public class IexportStorageDataProxy extends IexportData {

    private IexportStorageData iexportStorageData;

    private List<IexportData> dependencies;

    private IexportDataRecord dataRecord;

    private IexportMetaData iexportMetaData;

    public IexportStorageDataProxy(IexportStorageData iexportStorageData) {
        this.iexportStorageData = iexportStorageData;
    }

    public IexportStorageDataProxy(IexportStorageData iexportStorageData, IexportMetaData iexportMetaData) {
        this.iexportStorageData = iexportStorageData;
        this.iexportMetaData = iexportMetaData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return iexportStorageData.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getName()
     */
    @Override
    public String getName() {
        return iexportStorageData.getName();
    }

    @Override
    public Integer getRecVer() {
        return iexportStorageData.getRecVer();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getRowData()
     */
    @Override
    public Map<String, Object> getRowData() {
        return iexportStorageData.getRowData();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getType()
     */
    @Override
    public String getType() {
        return iexportStorageData.getType();
    }

    /**
     * @return the priority
     */
    public boolean isPriority() {
        return iexportStorageData.isPriority();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        ByteArrayOutputStream o = new ByteArrayOutputStream();
        o.write(iexportStorageData.getData());
        ByteArrayInputStream bi = new ByteArrayInputStream(o.toByteArray());
        o.flush();
        o.close();
        return bi;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getRecord()
     */
    @Override
    public IexportDataRecord getRecord() {
        if (dataRecord == null) {
            dataRecord = SerializationUtils.deserialization(iexportStorageData.getRecord());
        }
        return dataRecord;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        if (iexportMetaData == null) {
            iexportMetaData = super.getMetaData();
        }
        return iexportMetaData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#accept(com.wellsoft.pt.basicdata.iexport.visitor.Visitor)
     */
    @Override
    public void accept(Visitor visitor) {
        // 依赖优化导入
        if (iexportStorageData.isPriority()) {
            doVisit(visitor, this);
        } else {
            // 根结点优化导入
            super.accept(visitor);
        }
    }

    /**
     * @param visitor
     * @param iexportData
     */
    private void doVisit(Visitor visitor, IexportData iexportData) {
        for (IexportData child : iexportData.getChildren()) {
            // 依赖优化导入
            if (child.isPriority()) {
                doVisit(visitor, child);
            } else {
                // 根结点优化导入
                child.accept(visitor);
            }
        }

        // 依赖优化导入
        if (iexportData.isPriority()) {
            visitor.visit(iexportData);
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        return dependencies;
    }

    /**
     * @param dependencies
     */
    public void setDependencies(List<IexportData> dependencies) {
        this.dependencies = dependencies;
    }

}
