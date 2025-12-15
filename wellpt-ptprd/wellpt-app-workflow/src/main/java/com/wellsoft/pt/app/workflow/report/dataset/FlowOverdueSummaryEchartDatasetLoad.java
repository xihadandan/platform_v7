/*
 * @(#)7/14/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.app.workflow.report.query.FlowInstanceCountQueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowInstanceReportService;
import com.wellsoft.pt.app.workflow.report.service.FlowOverdueReportService;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.Dimension;
import com.wellsoft.pt.report.echart.option.SourceKeyValue;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
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
 * 7/14/25.1	    zhulh		7/14/25		    Create
 * </pre>
 * @date 7/14/25
 */
@Component
public class FlowOverdueSummaryEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowInstanceReportService flowInstanceReportService;

    @Autowired
    private FlowOverdueReportService flowOverdueReportService;

    @Override
    public String datasetName() {
        return "流程统计分析_流程逾期分析概要";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        String flowState = Objects.toString(params.get("flowState"), StringUtils.EMPTY);
        List<FlowInstanceCountQueryItem> queryItems = flowInstanceReportService.queryFlowInstanceCount(Maps.newHashMap(params));
        List<Dataset> datasets = Lists.newArrayList();
        long startCount = queryItems.stream().mapToLong(item -> {
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
        }).sum();
        datasets.add(buildDataset("流程总数", "startCount", startCount));
        long overdueCount = flowOverdueReportService.queryOverdueCount(Maps.newHashMap(params));
        datasets.add(buildDataset("逾期流程数", "flowOverdueCount", overdueCount));
        datasets.add(buildDataset("逾期流程占比", "overduePercent", Double.valueOf(String.format("%.2f", startCount > 0 ? (overdueCount * 100d / startCount) : 0d))));
        // 最大逾期时长
        Double maxOverdueTimeInMinute = flowOverdueReportService.getMaxOverdueTime(Maps.newHashMap(params));
        datasets.add(buildDataset("最大逾期时长", "maxOverdueTimeInMinute", Math.round(maxOverdueTimeInMinute)));
        // 平均逾期时长
        datasets.add(buildDataset("平均逾期时长", "avgOverdueTimeInMinute", Math.round(flowOverdueReportService.getAvgOverdueTime(Maps.newHashMap(params)))));
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
