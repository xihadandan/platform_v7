/*
 * @(#)2019年10月16日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dp.iexport.acceptor;

import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.dms.entity.DmsDataPermissionDefinitionEntity;

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
 * 2019年10月16日.1	zhulh		2019年10月16日		Create
 * </pre>
 * @date 2019年10月16日
 */
public class DmsDataPermissionDefinitionIexportData extends IexportData {

    private DmsDataPermissionDefinitionEntity dmsDataPermissionDefinitionEntity;

    /**
     * @param dmsDataPermissionDefinitionEntity
     */
    public DmsDataPermissionDefinitionIexportData(DmsDataPermissionDefinitionEntity dmsDataPermissionDefinitionEntity) {
        this.dmsDataPermissionDefinitionEntity = dmsDataPermissionDefinitionEntity;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getUuid()
     */
    @Override
    public String getUuid() {
        return dmsDataPermissionDefinitionEntity.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getName()
     */
    @Override
    public String getName() {
        return "数据权限定义：" + dmsDataPermissionDefinitionEntity.getName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getType()
     */
    @Override
    public String getType() {
        return IexportType.DmsDataPermissionDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getRecVer()
     */
    @Override
    public Integer getRecVer() {
        return dmsDataPermissionDefinitionEntity.getRecVer();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.acceptor.IexportData#getInputStream()
     */
    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, dmsDataPermissionDefinitionEntity);
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
