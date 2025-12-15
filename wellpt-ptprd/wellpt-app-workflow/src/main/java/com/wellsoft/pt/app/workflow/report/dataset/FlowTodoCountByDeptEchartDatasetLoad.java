/*
 * @(#)7/9/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowTodoReportService;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.Dimension;
import com.wellsoft.pt.report.echart.option.SourceKeyValue;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
 * 7/9/25.1	    zhulh		7/9/25		    Create
 * </pre>
 * @date 7/9/25
 */
@Component
public class FlowTodoCountByDeptEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowTodoReportService flowTodoReportService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String datasetName() {
        return "流程统计分析_待办流程分析按部门_待办数量排名";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
//        String userRange = Objects.toString(params.get("userRange"), StringUtils.EMPTY);
//        if (StringUtils.isBlank(userRange)) {
//            return Collections.emptyList();
//        }
        // 添加维度信息
        // 正常数量
        Dataset dataset1 = new Dataset();
        dataset1.getDimensions().add(new Dimension("部门名称"));
        dataset1.getDimensions().add(new Dimension("数量", DimensionTypeEnum.FLOAT, null));
        // 逾期数量
        Dataset dataset2 = new Dataset();
        dataset2.getDimensions().add(new Dimension("部门名称"));
        dataset2.getDimensions().add(new Dimension("数量", DimensionTypeEnum.FLOAT, null));

        if (MapUtils.isEmpty(params)) {
            return Lists.newArrayList(dataset1, dataset2);
        }
        Map<String, String> deptMap = flowTodoReportService.listFlowTodoDeptIdAndName(params);
        if (MapUtils.isEmpty(deptMap)) {
            return Lists.newArrayList(dataset1, dataset2);
        }
        List<String> deptIds = Lists.newArrayList(deptMap.keySet());

        int maxBarCount = FlowReportUtils.getMaxBarCountOfFlowTodoByDept(flowSettingService.getWorkFlowSettings(), 12);
        params.put("isOverdue", 0);
        List<QueryItem> normalQueryItems = flowTodoReportService.listTodoCountByDeptGroupByDept(deptIds, Maps.newHashMap(params), null);
        params.put("isOverdue", 1);
        List<QueryItem> overdueQueryItems = flowTodoReportService.listTodoCountByDeptGroupByDept(deptIds, Maps.newHashMap(params), null);

        List<QueryItem> queryItems = mergeQueryItems(normalQueryItems, overdueQueryItems, deptMap);
        queryItems = FlowReportUtils.filterByPagingInfo(queryItems, new PagingInfo(1, maxBarCount));
        if (CollectionUtils.size(queryItems) > maxBarCount) {
            queryItems = queryItems.subList(0, maxBarCount);
        }

        // 正常数量
        queryItems.forEach(item -> {
            dataset1.getSource().add(
                    new SourceKeyValue<String, Object>().add("部门名称", item.getString("deptName"))
                            .add("数量", item.getLong("count"))
                            .add("count", item.getLong("count"))
                            .add("deptId", item.getString("deptId")));
        });

        // 逾期数量
        queryItems.forEach(item -> {
            dataset2.getSource().add(
                    new SourceKeyValue<String, Object>().add("部门名称", item.getString("deptName"))
                            .add("数量", item.getLong("overdueCount"))
                            .add("count", item.getLong("overdueCount"))
                            .add("deptId", item.getString("deptId")));
        });

        if (CollectionUtils.isEmpty(queryItems)) {
            deptMap.forEach((deptId, deptName) -> {
                dataset1.getSource().add(
                        new SourceKeyValue<String, Object>().add("部门名称", deptName)
                                .add("数量", 0)
                                .add("count", 0)
                                .add("deptId", 0));
                dataset2.getSource().add(
                        new SourceKeyValue<String, Object>().add("部门名称", deptName)
                                .add("数量", 0)
                                .add("count", 0)
                                .add("deptId", 0));
            });
        }

        return Lists.newArrayList(dataset1, dataset2);
    }

    public static List<QueryItem> mergeQueryItems(List<QueryItem> normalQueryItems, List<QueryItem> overdueQueryItems, Map<String, String> deptMap) {
        Set<String> deptIds = Sets.newHashSet();
        Map<String, QueryItem> normalMap = normalQueryItems.stream().collect(Collectors.toMap(item -> item.getString("deptId"), item -> item));
        Map<String, QueryItem> overdueMap = overdueQueryItems.stream().collect(Collectors.toMap(item -> item.getString("deptId"), item -> item));
        deptIds.addAll(normalMap.keySet());
        deptIds.addAll(overdueMap.keySet());

        List<QueryItem> queryItems = Lists.newArrayList();
        deptIds.forEach(deptId -> {
            QueryItem normalItem = normalMap.get(deptId);
            QueryItem overdueItem = overdueMap.get(deptId);
            if (normalItem == null) {
                normalItem = new QueryItem();
                normalItem.put("deptId", deptId);
                normalItem.put("deptName", overdueItem.getString("deptName"));
                normalItem.put("count", 0L);
            }
            if (overdueItem == null) {
                overdueItem = new QueryItem();
                overdueItem.put("deptId", deptId);
                overdueItem.put("deptName", overdueItem.getString("deptName"));
                overdueItem.put("count", 0L);
            }
            normalItem.put("deptName", deptMap.get(deptId));
            normalItem.put("overdueCount", overdueItem.getLong("count"));
            queryItems.add(normalItem);
        });
        Collections.sort(queryItems, (o1, o2) -> {
            long o1Count = o1.getLong("count") + o1.getLong("overdueCount");
            long o2Count = o2.getLong("count") + o2.getLong("overdueCount");
            return Long.compare(o2Count, o1Count);
        });
        return queryItems;
    }

}
