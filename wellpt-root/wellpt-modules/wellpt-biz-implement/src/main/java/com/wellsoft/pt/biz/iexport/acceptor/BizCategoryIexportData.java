/*
 * @(#)11/1/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.iexport.acceptor;

import com.google.common.collect.Lists;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.biz.entity.BizCategoryEntity;

import java.io.IOException;
import java.io.InputStream;
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
 * 11/1/22.1	zhulh		11/1/22		Create
 * </pre>
 * @date 11/1/22
 */
public class BizCategoryIexportData extends IexportData {

    private BizCategoryEntity bizCategoryEntity;

    /**
     * @param bizCategoryEntity
     */
    public BizCategoryIexportData(BizCategoryEntity bizCategoryEntity) {
        this.bizCategoryEntity = bizCategoryEntity;
    }

    @Override
    public String getUuid() {
        return bizCategoryEntity.getUuid();
    }

    @Override
    public String getName() {
        return "业务分类：" + bizCategoryEntity.getName();
    }

    @Override
    public String getType() {
        return IexportType.BizCategory;
    }

    @Override
    public Integer getRecVer() {
        return bizCategoryEntity.getRecVer();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, bizCategoryEntity);
    }

    @Override
    public List<IexportData> getDependencies() {
        return Lists.newArrayListWithExpectedSize(0);
    }
}
