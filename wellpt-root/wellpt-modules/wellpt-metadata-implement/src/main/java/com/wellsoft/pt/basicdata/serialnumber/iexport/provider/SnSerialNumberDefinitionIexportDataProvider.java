/*
 * @(#)8/1/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.iexport.provider;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.basicdata.iexport.suport.ProtoDataHql;
import com.wellsoft.pt.basicdata.iexport.suport.TableMetaData;
import com.wellsoft.pt.basicdata.serialnumber.entity.SnSerialNumberDefinitionEntity;
import com.wellsoft.pt.basicdata.serialnumber.iexport.acceptor.SnSerialNumberDefinitionIexportData;
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
 * 8/1/22.1	zhulh		8/1/22		Create
 * </pre>
 * @date 8/1/22
 */
@Service
@Transactional(readOnly = true)
public class SnSerialNumberDefinitionIexportDataProvider extends AbstractIexportDataProvider<SnSerialNumberDefinitionEntity, String> {
    static {
        // 4.1、 流水号
        TableMetaData.register(IexportType.SnSerialNumberDefinition, "新版流水号定义", SnSerialNumberDefinitionEntity.class);
    }

    @Override
    public String getType() {
        return IexportType.SnSerialNumberDefinition;
    }

    @Override
    public IexportData getData(String uuid) {
        SnSerialNumberDefinitionEntity serialNumber = this.dao.get(SnSerialNumberDefinitionEntity.class, uuid);
        if (serialNumber == null) {
            return new ErrorDataIexportData(IexportType.SnSerialNumberDefinition, "找不到对应的新版流水号定义依赖关系,可能已经被删除", "新版流水号定义", uuid);
        }
        return new SnSerialNumberDefinitionIexportData(serialNumber);
    }

    /**
     * 获取treeName
     *
     * @param snSerialNumberDefinitionEntity
     * @return
     */
    @Override
    public String getTreeName(SnSerialNumberDefinitionEntity snSerialNumberDefinitionEntity) {
        return new SnSerialNumberDefinitionIexportData(snSerialNumberDefinitionEntity).getName();
    }

    @Override
    public TreeNode treeNode(String uuid) {
        TreeNode node = super.treeNode(uuid);
        // 导出分类
        SnSerialNumberDefinitionEntity entity = getEntity(uuid);
        if (entity != null && StringUtils.isNotBlank(entity.getCategoryUuid())) {
            node.appendChild(this.exportTreeNodeByDataProvider(entity.getCategoryUuid(), IexportType.SnSerialNumberCategory));
        }
        return node;
    }

    @Override
    public void putChildProtoDataHqlParams(SnSerialNumberDefinitionEntity serialNumber, Map<String, SnSerialNumberDefinitionEntity> parentMap,
                                           Map<String, ProtoDataHql> hqlMap) {
        String start = getType() + Separator.UNDERLINE.getValue();
        if (StringUtils.isNotBlank(serialNumber.getCategoryUuid())) {
            parentMap.put(start + "snSerialNumberCategoroy" + Separator.UNDERLINE.getValue() + serialNumber.getCategoryUuid(),
                    serialNumber);
            if (!hqlMap.containsKey(this.getChildHqlKey(IexportType.SnSerialNumberCategory))) {
                hqlMap.put(this.getChildHqlKey(IexportType.SnSerialNumberCategory), this.getProtoDataHql("SnSerialNumberCategoryEntity"));
            }
            this.addDataUuid(hqlMap.get(this.getChildHqlKey(IexportType.SnSerialNumberCategory)), serialNumber.getCategoryUuid());
        }

        if (StringUtils.isNotBlank(serialNumber.getModuleId())) {
            this.putAppFunctionParentMap(serialNumber, parentMap, hqlMap);
        }
    }

    @Override
    public Map<String, List<SnSerialNumberDefinitionEntity>> getParentMapList(ProtoDataHql protoDataHql) {
        Map<String, List<SnSerialNumberDefinitionEntity>> map = new HashMap<>();
        List<SnSerialNumberDefinitionEntity> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(),
                SnSerialNumberDefinitionEntity.class);
        // 页面或组件定义依赖的流水号定义
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (SnSerialNumberDefinitionEntity serialNumber : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue()
                        + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, serialNumber, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.FlowDefinition)) {
            for (SnSerialNumberDefinitionEntity serialNumber : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "snSerialNumberDefinition"
                        + Separator.UNDERLINE.getValue() + serialNumber.getId();
                this.putParentMap(map, serialNumber, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (SnSerialNumberDefinitionEntity serialNumber : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + serialNumber.getUuid();
                this.putParentMap(map, serialNumber, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }
        return map;
    }

}
