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
import com.wellsoft.pt.biz.entity.BizProcessDefinitionEntity;

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
public class BizProcessDefinitionIexportData extends IexportData {

    private BizProcessDefinitionEntity bizProcessDefinitionEntity;

    /**
     * @param bizProcessDefinitionEntity
     */
    public BizProcessDefinitionIexportData(BizProcessDefinitionEntity bizProcessDefinitionEntity) {
        this.bizProcessDefinitionEntity = bizProcessDefinitionEntity;
    }

    @Override
    public String getUuid() {
        return bizProcessDefinitionEntity.getUuid();
    }

    @Override
    public String getName() {
        return "业务流程定义：" + bizProcessDefinitionEntity.getName();
    }

    @Override
    public String getType() {
        return IexportType.BizProcessDefinition;
    }

    @Override
    public Integer getRecVer() {
        return bizProcessDefinitionEntity.getRecVer();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, bizProcessDefinitionEntity);
    }

    @Override
    public List<IexportData> getDependencies() {
        return Lists.newArrayListWithExpectedSize(0);
    }
}
