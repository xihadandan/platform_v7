/*
 * @(#)8/13/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datadict.iexport.acceptor;

import com.wellsoft.pt.basicdata.datadict.entity.CdDataDictionaryCategoryEntity;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 8/13/23.1	zhulh		8/13/23		Create
 * </pre>
 * @date 8/13/23
 */
public class CdDataDictionaryCategoryIexportData extends IexportData {
    private CdDataDictionaryCategoryEntity dataDictionaryCategoryEntity;

    /**
     * @param dataDictionaryCategoryEntity
     */
    public CdDataDictionaryCategoryIexportData(CdDataDictionaryCategoryEntity dataDictionaryCategoryEntity) {
        this.dataDictionaryCategoryEntity = dataDictionaryCategoryEntity;
    }

    @Override
    public String getUuid() {
        return Objects.toString(dataDictionaryCategoryEntity.getUuid());
    }

    @Override
    public String getName() {
        return "字典分类：" + dataDictionaryCategoryEntity.getName();
    }

    @Override
    public String getType() {
        return IexportType.CdDataDictionaryCategory;
    }

    @Override
    public Integer getRecVer() {
        return dataDictionaryCategoryEntity.getRecVer();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, dataDictionaryCategoryEntity);
    }

    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = new ArrayList<IexportData>();
        return dependencies;
    }
}
