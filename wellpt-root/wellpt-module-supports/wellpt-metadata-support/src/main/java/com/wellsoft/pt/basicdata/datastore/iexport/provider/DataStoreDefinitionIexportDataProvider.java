/*
 * @(#)2017年3月13日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datastore.iexport.provider;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.app.entity.AppFunction;
import com.wellsoft.pt.basicdata.datastore.entity.CdDataStoreDefinition;
import com.wellsoft.pt.basicdata.datastore.iexport.acceptor.DataStoreDefinitionIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.cache.CacheManager;
import com.wellsoft.pt.cache.config.CacheName;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 2017年3月13日.1	zhulh		2017年3月13日		Create
 * </pre>
 * @date 2017年3月13日
 */
@Service
@Transactional(readOnly = true)
public class DataStoreDefinitionIexportDataProvider extends AbstractIexportDataProvider<CdDataStoreDefinition, String> {

    static {
        TableMetaData.register(IexportType.DataStoreDefinition, "数据仓库", CdDataStoreDefinition.class);
    }

    @Autowired
    CacheManager cacheManager;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.DataStoreDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        CdDataStoreDefinition cdDataStoreDefinition = this.dao.get(CdDataStoreDefinition.class, uuid);
        if (cdDataStoreDefinition == null) {
            return new ErrorDataIexportData(IexportType.DataSourceProfile, "找不到对应的数据仓库依赖关系,可能已经被删除", "数据仓库", uuid);
        }
        return new DataStoreDefinitionIexportData(cdDataStoreDefinition);
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
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.DataStoreDefinition), "id",
                EntityIdColumnValueProcessorFactory.getColumnValueProcessor(CdDataStoreDefinition.class));
        return iexportMetaData;
    }

    @Override
    public String getTreeName(CdDataStoreDefinition cdDataStoreDefinition) {
        return new DataStoreDefinitionIexportData(cdDataStoreDefinition).getName();
    }

    @Override
    public void putChildProtoDataHqlParams(CdDataStoreDefinition cdDataStoreDefinition, Map<String, CdDataStoreDefinition> parentMap, Map<String, ProtoDataHql> hqlMap) {
        if (StringUtils.isNotBlank(cdDataStoreDefinition.getModuleId())) {
            this.putAppFunctionParentMap(cdDataStoreDefinition, parentMap, hqlMap);
        }
    }


    @Override
    public Map<String, List<CdDataStoreDefinition>> getParentMapList(ProtoDataHql protoDataHql) {
        List<CdDataStoreDefinition> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), CdDataStoreDefinition.class);
        Map<String, List<CdDataStoreDefinition>> map = new HashMap<>();
        // 页面或组件定义依赖的数据仓库定义
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (CdDataStoreDefinition dataStoreDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, dataStoreDefinition, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (CdDataStoreDefinition dataStoreDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + dataStoreDefinition.getUuid();
                this.putParentMap(map, dataStoreDefinition, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.FormDefinition)) {
            for (CdDataStoreDefinition dataStoreDefinition : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "dataStore" + Separator.UNDERLINE.getValue() + dataStoreDefinition.getId();
                this.putParentMap(map, dataStoreDefinition, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }

    @Override
    public TreeNode exportAsTreeNodeByFunction(AppFunction function) {
        if (StringUtils.isNotBlank(function.getId())) {
            CdDataStoreDefinition entity = this.dao.findUniqueBy(CdDataStoreDefinition.class, "id", function.getId());
            if (entity != null) {
                return this.treeNode(entity.getUuid());
            }
        }
        return null;
    }

    @Override
    protected void afterSaveEntityStream(CdDataStoreDefinition entity) {
        cacheManager.getCache(CacheName.DEFAULT).evict(entity.getId());
        cacheManager.getCache(CacheName.DEFAULT).evict(entity.getUuid());
    }
}
