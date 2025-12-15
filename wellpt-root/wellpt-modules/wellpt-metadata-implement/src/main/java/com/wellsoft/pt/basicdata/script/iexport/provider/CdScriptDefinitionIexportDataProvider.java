/*
 * @(#)2018年9月26日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.script.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.basicdata.script.entity.CdScriptDefinitionEntity;
import com.wellsoft.pt.basicdata.script.iexport.acceptor.CdScriptDefinitionIexportData;
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
 * 2018年9月26日.1	zhulh		2018年9月26日		Create
 * </pre>
 * @date 2018年9月26日
 */
@Service
@Transactional(readOnly = true)
public class CdScriptDefinitionIexportDataProvider extends AbstractIexportDataProvider<CdScriptDefinitionEntity, String> {

    static {
        // 15.1 脚本定义
        TableMetaData.register(IexportType.CdScriptDefinition, "脚本定义", CdScriptDefinitionEntity.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.CdScriptDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        CdScriptDefinitionEntity scriptDefinitionEntity = this.dao.get(CdScriptDefinitionEntity.class, uuid);
        if (scriptDefinitionEntity == null) {
            return new ErrorDataIexportData(IexportType.CdScriptDefinition, "找不到对应的脚本定义依赖关系,可能已经被删除", "脚本定义", uuid);
        }
        return new CdScriptDefinitionIexportData(scriptDefinitionEntity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 组件ID生成方式
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.CdScriptDefinition), "id",
                EntityIdColumnValueProcessorFactory.getColumnValueProcessor(CdScriptDefinitionEntity.class));
        return iexportMetaData;
    }

    @Override
    public String getTreeName(CdScriptDefinitionEntity cdScriptDefinitionEntity) {
        return new CdScriptDefinitionIexportData(cdScriptDefinitionEntity).getName();
    }

    @Override
    public Map<String, List<CdScriptDefinitionEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<CdScriptDefinitionEntity>> map = new HashMap<>();
        List<CdScriptDefinitionEntity> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), CdScriptDefinitionEntity.class);
        // 页面或组件定义依赖的脚本定义
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (CdScriptDefinitionEntity cdScriptDefinitionEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, cdScriptDefinitionEntity, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (CdScriptDefinitionEntity cdScriptDefinitionEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + cdScriptDefinitionEntity.getUuid();
                this.putParentMap(map, cdScriptDefinitionEntity, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
