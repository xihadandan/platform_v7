/*
 * @(#)2/17/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.store;

import com.google.common.collect.Lists;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.basicdata.datastore.support.AbstractDataStoreQueryInterface;
import com.wellsoft.pt.bpm.engine.entity.FlowDefinition;
import com.wellsoft.pt.bpm.engine.service.FlowDefinitionService;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.workflow.entity.FlowCategory;
import com.wellsoft.pt.workflow.service.FlowCategoryService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2/17/25.1	    zhulh		2/17/25		    Create
 * </pre>
 * @date 2/17/25
 */
@Component
public class WorkFlowCategoryAndDefinitionDataStore extends AbstractDataStoreQueryInterface {

    @Autowired
    private FlowCategoryService flowCategoryService;

    @Autowired
    private FlowDefinitionService flowDefinitionService;

    @Override
    public String getQueryName() {
        return "流程管理_流程分类及定义数据";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = CriteriaMetadata.createMetadata();
        criteriaMetadata.add("uuid", "t1.uui", "UUID", String.class);
        criteriaMetadata.add("creator", "t1.creator", "创建人", String.class);
        criteriaMetadata.add("createTime", "t1.create_time", "创建时间", Date.class);
        criteriaMetadata.add("name", "t1.name", "名称", String.class);
        criteriaMetadata.add("id", "t1.id", "ID", String.class);
        criteriaMetadata.add("parentUuid", "t1.parent_uuid", "上级分类UUID", String.class);
        criteriaMetadata.add("remark", "t1.ramark", "备注", String.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        List<FlowCategory> flowCategories = flowCategoryService.getAllBySystemUnitIds();
        List<QueryItem> queryItems = new ArrayList<>();
        List<String> categoryUuids = flowCategories.stream().map(FlowCategory::getUuid).collect(Collectors.toList());
        List<FlowDefinition> flowDefinitions = flowDefinitionService.getListByIdsOrCategoryUuids(categoryUuids);
        Map<String, List<FlowDefinition>> definitionMap = flowDefinitions.stream().collect(Collectors.groupingBy(FlowDefinition::getCategory));
        flowCategories.forEach(category -> {
            List<FlowDefinition> definitions = definitionMap.get(category.getUuid());
            List<String> categoryIds = Lists.newArrayList(category.getUuid());
            if (definitions != null) {
                definitions.forEach(definition -> {
                    categoryIds.add(definition.getId());
                    QueryItem queryItem = new QueryItem();
                    queryItem.put("uuid", definition.getUuid());
                    queryItem.put("name", definition.getName());
                    queryItem.put("id", definition.getId());
                    queryItem.put("parentUuid", definition.getCategory());
                    queryItem.put("ramark", definition.getRemark());
                    queryItems.add(queryItem);
                });
            }
            QueryItem queryItem = new QueryItem();
            queryItem.put("uuid", category.getUuid());
            queryItem.put("creator", category.getCreator());
            queryItem.put("createTime", category.getCreateTime());
            queryItem.put("name", category.getName());
            queryItem.put("id", StringUtils.join(categoryIds, Separator.SEMICOLON.getValue()));
            queryItem.put("remark", category.getRemark());
            queryItems.add(queryItem);
        });
        return queryItems;
    }

    @Override
    public long count(QueryContext context) {
        return 0;
    }

}
