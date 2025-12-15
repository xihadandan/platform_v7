/*
 * @(#)2016年11月22日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.entity.FlowSchema;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.workflow.iexport.acceptor.FlowSchemaIexportData;
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
 * 2016年11月22日.1	zhulh		2016年11月22日		Create
 * </pre>
 * @date 2016年11月22日
 */
@Service
@Transactional(readOnly = true)
public class FlowSchemaIexportDataProvider extends AbstractIexportDataProvider<FlowSchema, String> {
    static {
        TableMetaData.register(IexportType.FlowSchema, "流程定义XML", FlowSchema.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.FlowSchema;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        FlowSchema flowSchema = this.dao.get(FlowSchema.class, uuid);
        if (flowSchema == null) {
            return new ErrorDataIexportData(IexportType.FlowSchema, "找不到对应的流程定义XML依赖关系,可能已经被删除", "流程定义XML", uuid);
        }
        return new FlowSchemaIexportData(flowSchema);
    }

    @Override
    public String getTreeName(FlowSchema flowSchema) {
        return new FlowSchemaIexportData(flowSchema).getName();
    }


    @Override
    public Map<String, List<FlowSchema>> getParentMapList(ProtoDataHql protoDataHql) {
        List<FlowSchema> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), FlowSchema.class);
        Map<String, List<FlowSchema>> map = new HashMap<>();
        if (protoDataHql.getParentType().equals(IexportType.FlowDefinition)) {
            FlowDefinitionService flowDefinitionService = ApplicationContextHolder.getBean(FlowDefinitionService.class);
            for (FlowSchema flowSchema : list) {
                FlowDefinition flowDefinition = flowDefinitionService.getByFlowSchemaUuid(flowSchema.getUuid());
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "flowSchema" + Separator.UNDERLINE.getValue() + flowDefinition.getUuid();

                this.putParentMap(map, flowSchema, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
