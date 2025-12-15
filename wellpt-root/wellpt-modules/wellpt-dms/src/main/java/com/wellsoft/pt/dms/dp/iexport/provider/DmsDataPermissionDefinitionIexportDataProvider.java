/*
 * @(#)2019年10月16日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dp.iexport.provider;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.dms.dp.iexport.acceptor.DmsDataPermissionDefinitionIexportData;
import com.wellsoft.pt.dms.entity.DmsDataPermissionDefinitionEntity;
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
 * 2019年10月16日.1	zhulh		2019年10月16日		Create
 * </pre>
 * @date 2019年10月16日
 */
@Service
@Transactional(readOnly = true)
public class DmsDataPermissionDefinitionIexportDataProvider extends AbstractIexportDataProvider<DmsDataPermissionDefinitionEntity, String> {

    static {
        // 数据权限定义
        TableMetaData.register(IexportType.DmsDataPermissionDefinition, "数据权限定义",
                DmsDataPermissionDefinitionEntity.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.DmsDataPermissionDefinition;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        DmsDataPermissionDefinitionEntity dmsDataPermissionDefinitionEntity = this.dao.get(
                DmsDataPermissionDefinitionEntity.class, uuid);
        if (dmsDataPermissionDefinitionEntity == null) {
            return new ErrorDataIexportData(IexportType.DmsDataPermissionDefinition, "找不到对应的数据权限定义,可能已经被删除", "数据权限定义",
                    uuid);
        }
        return new DmsDataPermissionDefinitionIexportData(dmsDataPermissionDefinitionEntity);
    }

    @Override
    public String getTreeName(DmsDataPermissionDefinitionEntity dmsDataPermissionDefinitionEntity) {
        return new DmsDataPermissionDefinitionIexportData(dmsDataPermissionDefinitionEntity).getName();
    }


    @Override
    public Map<String, List<DmsDataPermissionDefinitionEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        List<DmsDataPermissionDefinitionEntity> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), DmsDataPermissionDefinitionEntity.class);
        Map<String, List<DmsDataPermissionDefinitionEntity>> map = new HashMap<>();
        // 页面或组件定义依赖的数据权限配置
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (DmsDataPermissionDefinitionEntity dmsDataPermissionDefinitionEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, dmsDataPermissionDefinitionEntity, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (DmsDataPermissionDefinitionEntity dmsDataPermissionDefinitionEntity : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + dmsDataPermissionDefinitionEntity.getUuid();
                this.putParentMap(map, dmsDataPermissionDefinitionEntity, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }
}
