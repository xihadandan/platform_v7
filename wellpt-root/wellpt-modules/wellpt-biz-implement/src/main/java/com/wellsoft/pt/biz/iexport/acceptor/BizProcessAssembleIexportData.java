/*
 * @(#)3/6/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.iexport.acceptor;

import com.google.common.collect.Lists;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.biz.entity.BizProcessAssembleEntity;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import org.apache.commons.lang.StringUtils;

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
 * 3/6/24.1	zhulh		3/6/24		Create
 * </pre>
 * @date 3/6/24
 */
public class BizProcessAssembleIexportData extends IexportData {

    private BizProcessAssembleEntity bizProcessAssembleEntity;

    /**
     * @param bizProcessAssembleEntity
     */
    public BizProcessAssembleIexportData(BizProcessAssembleEntity bizProcessAssembleEntity) {
        this.bizProcessAssembleEntity = bizProcessAssembleEntity;
    }

    @Override
    public String getUuid() {
        return this.bizProcessAssembleEntity.getUuid() + StringUtils.EMPTY;
    }

    @Override
    public String getName() {
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(bizProcessAssembleEntity.getProcessDefUuid());
        return "业务流程装配：" + parser.getProcessDefName();
    }

    @Override
    public String getType() {
        return IexportType.BizProcessAssemble;
    }

    @Override
    public Integer getRecVer() {
        return this.bizProcessAssembleEntity.getRecVer();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, bizProcessAssembleEntity);
    }

    @Override
    public List<IexportData> getDependencies() {
        return Lists.newArrayListWithExpectedSize(0);
    }
}
