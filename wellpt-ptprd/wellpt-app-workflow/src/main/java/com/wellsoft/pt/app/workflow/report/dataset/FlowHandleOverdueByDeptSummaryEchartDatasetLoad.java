/*
 * @(#)8/5/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowHandleOverdueReportService;
import com.wellsoft.pt.app.workflow.report.service.FlowTodoReportService;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.Dimension;
import com.wellsoft.pt.report.echart.option.SourceKeyValue;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/5/25.1	    zhulh		8/5/25		    Create
 * </pre>
 * @date 8/5/25
 */
@Component
public class FlowHandleOverdueByDeptSummaryEchartDatasetLoad implements EchartDatasetLoad {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FlowHandleOverdueReportService flowHandleOverdueReportService;

    @Autowired
    private FlowTodoReportService flowTodoReportService;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public String datasetName() {
        return "流程统计分析_办理逾期分析按部门概要";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        List<String> deptIds = flowTodoReportService.listFlowTodoDeptId(params);
        if (CollectionUtils.isEmpty(deptIds)) {
            return Lists.newArrayList();
        }

        List<Dataset> datasets = Lists.newArrayList();
        datasets.add(buildDataset("统计部门", "deptCount", deptIds.size()));

        try {
            if (CollectionUtils.size(deptIds) > 0) {
                params.put("deptIds", deptIds);

                Future<Long> overdueDeptCountFuture = scheduledExecutorService.submit(() ->
                        flowHandleOverdueReportService.queryOverdueCountByDept(Maps.newHashMap(params)));
                Future<Double> avgOverdueTimeInMinuteFuture = scheduledExecutorService.submit(() ->
                        flowHandleOverdueReportService.queryAvgOverdueTimeByDept(Maps.newHashMap(params)));
                Future<Double> avgOverdueCountFuture = scheduledExecutorService.submit(() ->
                        flowHandleOverdueReportService.queryAvgOverdueCountByDept(Maps.newHashMap(params)));
                Future<QueryItem> avgMaxOverdueTimeItemFuture = scheduledExecutorService.submit(() ->
                        flowHandleOverdueReportService.getAvgMaxOverdueTimeByDept(Maps.newHashMap(params)));

                long overdueDeptCount = overdueDeptCountFuture.get();// flowHandleOverdueReportService.queryOverdueCountByDept(Maps.newHashMap(params));
                datasets.add(buildDataset("逾期部门数", "overdueDeptCount", Math.round(overdueDeptCount)));

                double avgOverdueTimeInMinute = avgOverdueTimeInMinuteFuture.get(); // flowHandleOverdueReportService.queryAvgOverdueTimeByDept(Maps.newHashMap(params));
                datasets.add(buildDataset("部门平均逾期时长", "avgOverdueTimeInMinute", Math.round(avgOverdueTimeInMinute)));

                double avgOverdueCount = avgOverdueCountFuture.get(); // flowHandleOverdueReportService.queryAvgOverdueCountByDept(Maps.newHashMap(params));
                datasets.add(buildDataset("部门平均逾期次数", "avgOverdueCount", Double.valueOf(String.format("%.1f", avgOverdueCount))));

                // 平均逾期时长最长流程
                QueryItem avgMaxOverdueTimeItem = avgMaxOverdueTimeItemFuture.get(); // flowHandleOverdueReportService.getAvgMaxOverdueTimeByDept(Maps.newHashMap(params));
                if (avgMaxOverdueTimeItem != null) {
                    datasets.add(buildAvgMaxOverdueDataset(avgMaxOverdueTimeItem));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return datasets;
    }

    private Dataset buildAvgMaxOverdueDataset(QueryItem queryItem) {
        String categoryName = "平均逾期时长最长流程";
        String categoryCode = "avgMaxOverdueTimeInMinute";
        Double avgOverdueMinutes = queryItem.getDouble("avgOverdueMinutes");
        Long count = avgOverdueMinutes == null ? 0 : Math.round(avgOverdueMinutes);
        String flowDefName = queryItem.getString("name");
        Dataset dataset = new Dataset();
        // 添加维度信息
        dataset.getDimensions().add(new Dimension("分类"));
        dataset.getDimensions().add(new Dimension("数量", DimensionTypeEnum.FLOAT, null));

        dataset.getSource().add(
                new SourceKeyValue<String, Object>().add("分类", categoryName)
                        .add("数量", count).add(categoryCode, count).add("name", flowDefName));
        return dataset;
    }

    private Dataset buildDataset(String categoryName, String categoryCode, double count) {
        Dataset dataset = new Dataset();
        // 添加维度信息
        dataset.getDimensions().add(new Dimension("分类"));
        dataset.getDimensions().add(new Dimension("数量", DimensionTypeEnum.FLOAT, null));

        dataset.getSource().add(
                new SourceKeyValue<String, Object>().add("分类", categoryName)
                        .add("数量", count).add(categoryCode, count));
        return dataset;
    }

}
