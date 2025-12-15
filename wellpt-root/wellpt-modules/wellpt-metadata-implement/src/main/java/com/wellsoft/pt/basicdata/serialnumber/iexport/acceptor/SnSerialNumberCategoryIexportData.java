/*
 * @(#)8/1/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.iexport.acceptor;

import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberCategoryEntity;

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
 * 8/1/22.1	zhulh		8/1/22		Create
 * </pre>
 * @date 8/1/22
 */
public class SnSerialNumberCategoryIexportData extends IexportData {
    public SnSerialNumberCategoryEntity serialNumberCategoryEntity;

    /**
     * @param serialNumberCategoryEntity
     */
    public SnSerialNumberCategoryIexportData(SnSerialNumberCategoryEntity serialNumberCategoryEntity) {
        this.serialNumberCategoryEntity = serialNumberCategoryEntity;
    }

    @Override
    public String getUuid() {
        return serialNumberCategoryEntity.getUuid();
    }

    @Override
    public String getName() {
        return "流水号分类：" + serialNumberCategoryEntity.getName();
    }

    @Override
    public String getType() {
        return IexportType.SnSerialNumberCategory;
    }

    @Override
    public Integer getRecVer() {
        return serialNumberCategoryEntity.getRecVer();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, serialNumberCategoryEntity);
    }

    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = new ArrayList<IexportData>();
        return dependencies;
    }

}
