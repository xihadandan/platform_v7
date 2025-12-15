/*
 * @(#)11/24/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.iexport.facade.impl;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.WfFlowBusinessDefinitionIexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.workflow.entity.WfFlowBusinessDefinitionEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
 * 11/24/22.1	zhulh		11/24/22		Create
 * </pre>
 * @date 11/24/22
 */
@Service
@Transactional(readOnly = true)
public class WfFlowBusinessDefinitionIexportDataProvider extends AbstractIexportDataProvider<WfFlowBusinessDefinitionEntity, String> {
    static {
        // 事项定义
        TableMetaData.register(IexportType.FlowBusinessDefinition, "流程业务定义", WfFlowBusinessDefinitionEntity.class);
    }

    @Override
    public String getType() {
        return IexportType.FlowBusinessDefinition;
    }

    @Override
    public IexportData getData(String uuid) {
        WfFlowBusinessDefinitionEntity flowBusinessDefinitionEntity = this.dao.get(WfFlowBusinessDefinitionEntity.class, uuid);
        if (flowBusinessDefinitionEntity == null) {
            return new ErrorDataIexportData(IexportType.FlowBusinessDefinition, "找不到对应的流程业务定义依赖关系,可能已经被删除", "流程业务定义", uuid);
        }
        return new WfFlowBusinessDefinitionIexportData(flowBusinessDefinitionEntity);
    }

    /**
     * 获取treeName
     *
     * @param wfFlowBusinessDefinitionEntity
     * @return
     */
    @Override
    public String getTreeName(WfFlowBusinessDefinitionEntity wfFlowBusinessDefinitionEntity) {
        return new WfFlowBusinessDefinitionIexportData(wfFlowBusinessDefinitionEntity).getName();
    }

    @Override
    public void putChildProtoDataHqlParams(WfFlowBusinessDefinitionEntity flowBusinessDefinitionEntity,
                                           Map<String, WfFlowBusinessDefinitionEntity> parentMap,
                                           Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        // 流程业务使用流程
        if (StringUtils.isNotBlank(flowBusinessDefinitionEntity.getFlowDefId())) {
            parentMap.put(start + "form" + Separator.UNDERLINE.getValue() + flowBusinessDefinitionEntity.getFlowDefId(),
                    flowBusinessDefinitionEntity);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.FlowDefinition))) {
                hqlMap.put(this.getChildHqlKey(IexportType.FlowDefinition), this.getProtoDataHql("FlowDefinition"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.FlowDefinition)), flowBusinessDefinitionEntity.getFlowDefId());
        }
    }

    @Override
    public Map<String, List<WfFlowBusinessDefinitionEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<WfFlowBusinessDefinitionEntity>> map = new HashMap<>();

        if (protoDataHql.getParentType().equals(IexportType.BizProcessDefinition)) {
            String hql = "from WfFlowBusinessDefinitionEntity t where t.id in(:uuids)";
            List<WfFlowBusinessDefinitionEntity> list = this.dao.find(hql, protoDataHql.getParams(), WfFlowBusinessDefinitionEntity.class);
            for (WfFlowBusinessDefinitionEntity flowBusinessDefinitionEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "form" + Separator.UNDERLINE.getValue() + flowBusinessDefinitionEntity.getId();
                this.putParentMap(map, flowBusinessDefinitionEntity, key);
            }
        }

        return map;
    }

}
