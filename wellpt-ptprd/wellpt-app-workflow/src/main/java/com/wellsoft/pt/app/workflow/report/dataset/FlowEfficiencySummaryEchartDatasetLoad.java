/*
 * @(#)7/9/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.wellsoft.pt.app.workflow.report.query.FlowInstanceCountQueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowEfficiencyReportService;
import com.wellsoft.pt.app.workflow.report.service.FlowInstanceReportService;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.Dimension;
import com.wellsoft.pt.report.echart.option.SourceKeyValue;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
import org.apache.commons.lang.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

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
public class FlowEfficiencySummaryEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowInstanceReportService flowInstanceReportService;

    @Autowired
    private FlowEfficiencyReportService flowEfficiencyReportService;

    @Override
    public String datasetName() {
        return "流程统计分析_流程效率分析概要";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        params.put("flowCompleted", true);
        List<FlowInstanceCountQueryItem> queryItems = flowInstanceReportService.queryFlowInstanceCount(params);
        List<Dataset> datasets = Lists.newArrayList();
        datasets.add(buildDataset("发起的流程总数", "startCount", queryItems.stream().mapToLong(FlowInstanceCountQueryItem::getCount).sum()));
        datasets.add(buildDataset("已结束流程数", "completedCount", queryItems.stream().filter(item -> BooleanUtils.isTrue(item.getCompleted())).mapToLong(FlowInstanceCountQueryItem::getCount).sum()));
        datasets.add(buildDataset("流转中流程数", "uncompletedCount", queryItems.stream().filter(item -> BooleanUtils.isNotTrue(item.getCompleted())).mapToLong(FlowInstanceCountQueryItem::getCount).sum()));
        datasets.add(buildDataset("平均办结时长", "avgCompletedTimeInMinute", Math.round(flowEfficiencyReportService.queryAverageCompletedTimeInMinute(params))));
        datasets.add(buildDataset("环节平均用时", "avgTaskDurationInMinute", Math.round(flowEfficiencyReportService.queryAverageTaskDurationInMinute(params))));
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

}
