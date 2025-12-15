/*
 * @(#)11/24/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.acceptor;

import com.wellsoft.pt.basicdata.iexport.suport.IexportDataResultSetUtils;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.workflow.entity.WfFlowBusinessDefinitionEntity;

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
 * 11/24/22.1	zhulh		11/24/22		Create
 * </pre>
 * @date 11/24/22
 */
public class WfFlowBusinessDefinitionIexportData extends IexportData {

    private WfFlowBusinessDefinitionEntity flowBusinessDefinitionEntity;

    /**
     * @param flowBusinessDefinitionEntity
     */
    public WfFlowBusinessDefinitionIexportData(WfFlowBusinessDefinitionEntity flowBusinessDefinitionEntity) {
        this.flowBusinessDefinitionEntity = flowBusinessDefinitionEntity;
    }

    @Override
    public String getUuid() {
        return flowBusinessDefinitionEntity.getUuid();
    }

    @Override
    public String getName() {
        return "流程业务定义：" + flowBusinessDefinitionEntity.getName();
    }

    @Override
    public String getType() {
        return IexportType.FlowBusinessDefinition;
    }

    @Override
    public Integer getRecVer() {
        return flowBusinessDefinitionEntity.getRecVer();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return IexportDataResultSetUtils.iexportDataResultSet2InputStream(this, flowBusinessDefinitionEntity);
    }

    @Override
    public List<IexportData> getDependencies() {
        return Collections.emptyList();
    }
}
