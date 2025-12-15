/*
 * @(#)2018年9月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.script.iexport.acceptor;

import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.script.entity.CdScriptDefinitionEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
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
 * 2018年9月26日.1	zhulh		2018年9月26日		Create
 * </pre>
 * @date 2018年9月26日
 */
public class CdScriptDefinitionIexportData extends IexportData {

    public CdScriptDefinitionEntity cdScriptDefinitionEntity;

    public CdScriptDefinitionIexportData(CdScriptDefinitionEntity cdScriptDefinitionEntity) {
        this.cdScriptDefinitionEntity = cdScriptDefinitionEntity;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return cdScriptDefinitionEntity.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getName()
     */
    @Override
    public String getName() {
        return "脚本定义：" + cdScriptDefinitionEntity.getName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getType()
     */
    @Override
    public String getType() {
        return IexportType.CdScriptDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getRecVer()
     */
    @Override
    public Integer getRecVer() {
        return cdScriptDefinitionEntity.getRecVer();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, cdScriptDefinitionEntity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getDependencies()
     */
    @Override
    public List<IexportData> getDependencies() {
        return Collections.emptyList();
    }

}
