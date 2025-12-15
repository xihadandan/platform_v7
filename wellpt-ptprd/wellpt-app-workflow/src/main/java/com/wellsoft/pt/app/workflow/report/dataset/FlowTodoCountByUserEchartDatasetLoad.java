/*
 * @(#)7/9/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowTodoReportService;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.Dimension;
import com.wellsoft.pt.report.echart.option.SourceKeyValue;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
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
public class FlowTodoCountByUserEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowTodoReportService flowTodoReportService;

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String datasetName() {
        return "流程统计分析_待办流程分析按人员_待办数量排名";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        // 添加维度信息
        // 正常数量
        Dataset dataset1 = new Dataset();
        dataset1.getDimensions().add(new Dimension("人员名称"));
        dataset1.getDimensions().add(new Dimension("数量", DimensionTypeEnum.FLOAT, null));
        // 逾期数量
        Dataset dataset2 = new Dataset();
        dataset2.getDimensions().add(new Dimension("人员名称"));
        dataset2.getDimensions().add(new Dimension("数量", DimensionTypeEnum.FLOAT, null));

        String userRange = Objects.toString(params.get("userRange"), StringUtils.EMPTY);
        if (StringUtils.isBlank(userRange)) {
            return Lists.newArrayList(dataset1, dataset2);
        }

        int maxBarCount = FlowReportUtils.getMaxBarCountOfFlowTodoByUser(flowSettingService.getWorkFlowSettings(), 12);
        params.put("isOverdue", 0);
        List<QueryItem> normalQueryItems = flowTodoReportService.listTodoCountByUserGroupByUser(Maps.newHashMap(params), null);
        params.put("isOverdue", 1);
        List<QueryItem> overdueQueryItems = flowTodoReportService.listTodoCountByUserGroupByUser(Maps.newHashMap(params), null);

        List<QueryItem> queryItems = mergeQueryItems(normalQueryItems, overdueQueryItems);
        if (CollectionUtils.size(queryItems) > maxBarCount) {
            queryItems = queryItems.subList(0, maxBarCount);
        }

        // 正常数量
        queryItems.forEach(item -> {
            dataset1.getSource().add(
                    new SourceKeyValue<String, Object>().add("人员名称", item.getString("userName"))
                            .add("数量", item.getLong("count"))
                            .add("count", item.getLong("count"))
                            .add("userId", item.getString("userId")));
        });
        // 逾期数量
        queryItems.forEach(item -> {
            dataset2.getSource().add(
                    new SourceKeyValue<String, Object>().add("人员名称", item.getString("userName"))
                            .add("数量", item.getLong("overdueCount"))
                            .add("count", item.getLong("overdueCount"))
                            .add("userId", item.getString("userId")));
        });

        return Lists.newArrayList(dataset1, dataset2);
    }

    public static List<QueryItem> mergeQueryItems(List<QueryItem> normalQueryItems, List<QueryItem> overdueQueryItems) {
        Set<String> userIds = Sets.newHashSet();
        Map<String, QueryItem> normalMap = normalQueryItems.stream().collect(Collectors.toMap(item -> item.getString("userId"), item -> item));
        Map<String, QueryItem> overdueMap = overdueQueryItems.stream().collect(Collectors.toMap(item -> item.getString("userId"), item -> item));
        userIds.addAll(normalMap.keySet());
        userIds.addAll(overdueMap.keySet());

        List<QueryItem> queryItems = Lists.newArrayList();
        userIds.forEach(userId -> {
            QueryItem normalItem = normalMap.get(userId);
            QueryItem overdueItem = overdueMap.get(userId);
            if (normalItem == null) {
                normalItem = new QueryItem();
                normalItem.put("userId", userId);
                normalItem.put("userName", overdueItem.getString("userName"));
                normalItem.put("count", 0L);
            }
            if (overdueItem == null) {
                overdueItem = new QueryItem();
                overdueItem.put("userId", userId);
                overdueItem.put("userName", overdueItem.getString("userName"));
                overdueItem.put("count", 0L);
            }
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
