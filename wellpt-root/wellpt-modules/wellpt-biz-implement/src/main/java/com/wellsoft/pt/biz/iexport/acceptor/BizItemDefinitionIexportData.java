/*
 * @(#)10/31/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.iexport.acceptor;

import com.google.common.collect.Lists;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.biz.entity.BizItemDefinitionEntity;

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
 * 10/31/22.1	zhulh		10/31/22		Create
 * </pre>
 * @date 10/31/22
 */
public class BizItemDefinitionIexportData extends IexportData {

    private BizItemDefinitionEntity bizItemDefinitionEntity;

    /**
     * @param bizItemDefinitionEntity
     */
    public BizItemDefinitionIexportData(BizItemDefinitionEntity bizItemDefinitionEntity) {
        this.bizItemDefinitionEntity = bizItemDefinitionEntity;
    }

    @Override
    public String getUuid() {
        return bizItemDefinitionEntity.getUuid();
    }

    @Override
    public String getName() {
        return "事项定义：" + bizItemDefinitionEntity.getName();
    }

    @Override
    public String getType() {
        return IexportType.BizItemDefinition;
    }

    @Override
    public Integer getRecVer() {
        return bizItemDefinitionEntity.getRecVer();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, bizItemDefinitionEntity);
    }

    @Override
    public List<IexportData> getDependencies() {
        List<IexportData> dependencies = Lists.newArrayListWithExpectedSize(0);
        return dependencies;
    }
}
