/*
 * @(#)7/9/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.query.FlowInstanceCountQueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowEfficiencyReportService;
import com.wellsoft.pt.app.workflow.report.service.FlowInstanceReportService;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.Dimension;
import com.wellsoft.pt.report.echart.option.SourceKeyValue;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

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
public class FlowEfficiencyDetailSummaryEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowInstanceReportService flowInstanceReportService;

    @Autowired
    private FlowEfficiencyReportService flowEfficiencyReportService;

    @Override
    public String datasetName() {
        return "流程统计分析_流程效率分析_流程用时详细统计表概要";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        String flowState = Objects.toString(params.get("flowState"), StringUtils.EMPTY);
        List<FlowInstanceCountQueryItem> queryItems = flowInstanceReportService.queryFlowInstanceCount(Maps.newHashMap(params));
        List<Dataset> datasets = Lists.newArrayList();
        datasets.add(buildDataset("发起的流程总数", "startCount", queryItems.stream().mapToLong(item -> {
            if (StringUtils.equals(flowState, "completed")) {
                if (BooleanUtils.isTrue(item.getCompleted())) {
                    return item.getCount();
                } else {
                    return 0L;
                }
            }
            if (StringUtils.equals(flowState, "uncompleted")) {
                if (BooleanUtils.isNotTrue(item.getCompleted())) {
                    return item.getCount();
                } else {
                    return 0L;
                }
            }
            return item.getCount();
        }).sum()));
        datasets.add(buildDataset("流程平均用时", "avgCompletedTimeInMinute", Math.round(flowEfficiencyReportService.queryAverageCompletedTimeInMinute(Maps.newHashMap(params)))));
        datasets.add(buildDataset("环节平均用时", "avgTaskDurationInMinute", Math.round(flowEfficiencyReportService.queryAverageTaskDurationInMinute(Maps.newHashMap(params)))));

        // 流转环节总数
        long taskCompletedCount = flowEfficiencyReportService.queryTaskCompletedCount(Maps.newHashMap(params));
        // long taskAllCount = flowEfficiencyReportService.queryTaskAllCount(Maps.newHashMap(params));
        datasets.add(buildDataset("流转环节总数", "taskCompletedCount", taskCompletedCount));

        // 平均用时最长流程
        List<QueryItem> flowAverageCompletedTimeInMinute = flowEfficiencyReportService.listFlowAverageCompletedTimeInMinute(Maps.newHashMap(params), new PagingInfo(1, 1));
        if (CollectionUtils.isNotEmpty(flowAverageCompletedTimeInMinute)) {
            QueryItem item = flowAverageCompletedTimeInMinute.get(0);
            datasets.add(buildDataset("平均用时最长流程", "maxAvgCompletedTimeInMinute", item.getString("name"), Math.round(item.getDouble("avgCompletedTime"))));
        }
        return datasets;
    }

    private Dataset buildDataset(String categoryName, String categoryCode, long count) {
        Dataset dataset = new Dataset();
        // 添加维度信息
        dataset.getDimensions().add(new Dimension("分类"));
        dataset.getDimensions().add(new Dimension("数量", DimensionTypeEnum.FLOAT, null));

        dataset.getSource().add(
                new SourceKeyValue<String, Object>().add("分类", categoryName)
                        .add("数量", count).add(categoryCode, count));
        return dataset;
    }

    private Dataset buildDataset(String categoryName, String categoryCode, String flowDefName, long count) {
        Dataset dataset = new Dataset();
        // 添加维度信息
        dataset.getDimensions().add(new Dimension("分类"));
        dataset.getDimensions().add(new Dimension("数量", DimensionTypeEnum.FLOAT, null));

        dataset.getSource().add(
                new SourceKeyValue<String, Object>().add("分类", categoryName)
                        .add("数量", count).add(categoryCode, count).add("name", flowDefName));
        return dataset;
    }

}
