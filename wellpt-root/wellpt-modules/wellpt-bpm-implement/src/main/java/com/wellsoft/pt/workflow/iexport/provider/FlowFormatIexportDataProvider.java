/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.workflow.entity.FlowFormat;
import com.wellsoft.pt.workflow.iexport.acceptor.FlowFormatIexportData;
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
 * 2015-6-16.1	zhulh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
@Service
@Transactional(readOnly = true)
public class FlowFormatIexportDataProvider extends AbstractIexportDataProvider<FlowFormat, String> {
    static {
        TableMetaData.register(IexportType.FlowFormat, "信息格式", FlowFormat.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.FlowFormat;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        FlowFormat flowFormat = this.dao.get(FlowFormat.class, uuid);
        if (flowFormat == null) {
            return new ErrorDataIexportData(IexportType.FlowFormat, "找不到对应的消息格式依赖关系,可能已经被删除", "消息格式", uuid);
        }
        return new FlowFormatIexportData(flowFormat);
    }


    @Override
    public String getTreeName(FlowFormat flowFormat) {
        return new FlowFormatIexportData(flowFormat).getName();
    }


    @Override
    public Map<String, List<FlowFormat>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<FlowFormat>> map = new HashMap<>();
        List<FlowFormat> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), FlowFormat.class);
        if (protoDataHql.getParentType().equals(IexportType.FlowDefinition)) {
            for (FlowFormat flowFormat : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "flowFormat" + Separator.UNDERLINE.getValue() + flowFormat.getCode();
                this.putParentMap(map, flowFormat, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
