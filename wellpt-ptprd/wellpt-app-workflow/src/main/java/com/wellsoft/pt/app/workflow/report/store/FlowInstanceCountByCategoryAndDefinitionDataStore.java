/*
 * @(#)7/1/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
 * 7/1/25.1	    zhulh		7/1/25		    Create
 * </pre>
 * @date 7/1/25
 */
@Component
public class FlowInstanceCountByCategoryAndDefinitionDataStore extends FlowInstanceCountByDefinitionDataStore {


    @Override
    public String getQueryName() {
        return "流程统计分析_流程分类流程量统计";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = super.initCriteriaMetadata(context);
        criteriaMetadata.add("percent", "percent", "占比", String.class);
        criteriaMetadata.add("categoryName", "c.name", "流程分类名称", String.class);
        criteriaMetadata.add("children", "children", "子节点", String.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        context.getPagingInfo().setAutoCount(false);
        context.getQueryParams().put("joinCategory", true);
        List<QueryItem> queryItems = doQuery(context);
        Map<String, String> categoryMap = Maps.newHashMap();
        queryItems.stream().forEach(item -> {
            String categoryUuid = item.getString("categoryUuid");
            String categoryName = item.getString("categoryName");
            if (StringUtils.isBlank(categoryUuid) || StringUtils.isBlank(categoryName)) {
                return;
            }
            categoryMap.put(categoryUuid, categoryName);
        });
        long allCount = queryItems.stream().mapToLong(item -> item.getLong("totalCount")).sum();
        List<QueryItem> result = Lists.newArrayList();
        categoryMap.forEach((categoryUuid, categoryName) -> {
            QueryItem categoryItem = new QueryItem();
            categoryItem.put("uuid", categoryUuid);
            categoryItem.put("name", categoryName);
            List<QueryItem> children = queryItems.stream().filter(item -> StringUtils.equals(categoryUuid, item.getString("categoryUuid"))).collect(Collectors.toList());
            categoryItem.put("children", children);
            categoryItem.put("totalCount", children.stream().mapToLong(item -> item.getLong("totalCount")).sum());
            categoryItem.put("completedCount", children.stream().mapToLong(item -> item.getLong("completedCount")).sum());
            categoryItem.put("uncompletedCount", children.stream().mapToLong(item -> item.getLong("uncompletedCount")).sum());
            if (allCount > 0) {
                categoryItem.put("percent", String.format("%.2f", categoryItem.getLong("totalCount") * 100.0 / allCount));
            } else {
                categoryItem.put("percent", "0.00");
            }
            long categoryCount = children.stream().mapToLong(item -> item.getLong("totalCount")).sum();
            children.forEach(child -> {
                if (categoryCount > 0) {
                    child.put("percent", String.format("%.2f", child.getLong("totalCount") * 100.0 / categoryCount));
                } else {
                    child.put("percent", "0.00");
                }
            });
            result.add(categoryItem);
        });

        // 排序
        Collections.sort(result, (o1, o2) -> -o1.getLong("totalCount").compareTo(o2.getLong("totalCount")));
        return result;
    }

    /**
     * @param context
     * @return
     */
    private List<QueryItem> doQuery(QueryContext context) {
        List<QueryItem> queryItems = super.query(context);
        String flowRange = Objects.toString(context.getQueryParams().get("flowRange"), StringUtils.EMPTY);
        if (StringUtils.isNotBlank(flowRange)) {
            List<String> flowRanges = Lists.newArrayList(StringUtils.split(flowRange, Separator.SEMICOLON.getValue()));
            queryItems = queryItems.stream().filter(item -> flowRanges.contains(item.getString("id"))
                    || flowRanges.contains(item.getString("categoryUuid"))).collect(Collectors.toList());
        }
        return queryItems;
    }

}
