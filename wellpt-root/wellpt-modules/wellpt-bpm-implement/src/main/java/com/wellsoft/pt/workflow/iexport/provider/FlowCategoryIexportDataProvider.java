/*
 * @(#)2015-6-16 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.iexport.provider;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.basicdata.iexport.acceptor.ErrorDataIexportData;
import com.wellsoft.pt.basicdata.iexport.acceptor.IexportData;
import com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider;
import com.wellsoft.pt.basicdata.iexport.suport.*;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.workflow.entity.FlowCategory;
import com.wellsoft.pt.workflow.iexport.acceptor.FlowCategoryIexportData;
import org.apache.commons.lang.ObjectUtils;
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
 * 2015-6-16.1	zhulh		2015-6-16		Create
 * </pre>
 * @date 2015-6-16
 */
@Service
@Transactional(readOnly = true)
public class FlowCategoryIexportDataProvider extends AbstractIexportDataProvider<FlowCategory, String> {
    static {
        TableMetaData.register(IexportType.FlowCategory, "流程分类", FlowCategory.class);
    }

    @Autowired
    private FlowDefinitionService flowDefinitionService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getType()
     */
    @Override
    public String getType() {
        return IexportType.FlowCategory;
    }

    @Override
    public TreeNode treeNode(String s) {
        TreeNode node = super.treeNode(s);
//        List<FlowDefinition> flowDefinitions = flowDefinitionService.getByCategory(s);
//        if (CollectionUtils.isNotEmpty(flowDefinitions)) {
//            for (FlowDefinition flow : flowDefinitions) {
//                TreeNode treeNode = this.exportTreeNodeByDataProvider(flow.getUuid(), IexportType.FlowDefinition);
//                if (treeNode != null) {
//                    node.getChildren().add(treeNode);
//                }
//            }
//        }
        return node;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.IexportDataProvider#getData(java.lang.String)
     */
    @Override
    public IexportData getData(String uuid) {
        FlowCategory flowCategory = this.dao.get(FlowCategory.class, uuid);
        if (flowCategory == null) {
            return new ErrorDataIexportData(IexportType.FlowCategory, "找不到对应的流程分类依赖关系,可能已经被删除", "流程分类", uuid);
        }
        return new FlowCategoryIexportData(flowCategory);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.basicdata.iexport.service.AbstractIexportDataProvider#getMetaData()
     */
    @Override
    public IexportMetaData getMetaData() {
        IexportMetaData iexportMetaData = super.getMetaData();
        // 流程分类编号CODE生成方式
        iexportMetaData.registerColumnValueProcessor(TableMetaData.getTableName(IexportType.FlowCategory), "code",
                new ColumnValueProcessor() {

                    @Override
                    public Object doProcess(Object sourceValue, IexportDataRecord dataRecord, IexportData iexportData,
                                            IexportBundle iexportBundle) {
                        IexportDataColumn dataColumn = dataRecord.getDataColumn("code");
                        String oldCode = ObjectUtils.toString(dataColumn.getValue(), StringUtils.EMPTY);
                        IexportMappingColumn mappingColumn = iexportBundle.getMappingColumn(dataRecord.getTableName(),
                                dataColumn);
                        // 流程分类编号有新值时，直接返回
                        if (mappingColumn != null) {
                            return oldCode;
                        }
                        IdGeneratorService idGeneratorService = ApplicationContextHolder
                                .getBean(IdGeneratorService.class);
                        return oldCode + Separator.UNDERLINE.getValue() + idGeneratorService.getBySysDate();
                    }

                });
        return iexportMetaData;
    }

    @Override
    public String getTreeName(FlowCategory flowCategory) {
        return new FlowCategoryIexportData(flowCategory).getName();
    }

    @Override
    public Map<String, List<FlowCategory>> getParentMapList(ProtoDataHql protoDataHql) {
        List<FlowCategory> list = this.dao.find(protoDataHql.getHql().toString(), protoDataHql.getParams(), FlowCategory.class);
        Map<String, List<FlowCategory>> map = new HashMap<>();
        // 页面或组件定义依赖的流程分类
        if (protoDataHql.getParentType().equals(IexportType.AppPageDefinition)
                || protoDataHql.getParentType().equals(IexportType.AppWidgetDefinition)) {
            for (FlowCategory flowCategory : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + protoDataHql.getParams().get("dependencyUuid");
                this.putParentMap(map, flowCategory, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.FlowDefinition)) {
            for (FlowCategory flowCategory : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + "category" + Separator.UNDERLINE.getValue() + flowCategory.getUuid();
                this.putParentMap(map, flowCategory, key);
            }
        } else if (protoDataHql.getParentType().equals(IexportType.AppFunction)) {
            for (FlowCategory flowCategory : list) {
                String key = protoDataHql.getParentType() + Separator.UNDERLINE.getValue() + flowCategory.getUuid();
                this.putParentMap(map, flowCategory, key);
            }
        } else {
            super.getParentMapList(protoDataHql);
        }

        return map;
    }
}
