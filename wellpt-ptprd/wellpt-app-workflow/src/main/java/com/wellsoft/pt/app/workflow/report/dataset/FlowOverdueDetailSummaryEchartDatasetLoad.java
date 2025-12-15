/*
 * @(#)7/14/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowInstanceReportService;
import com.wellsoft.pt.app.workflow.report.service.FlowOverdueReportService;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.Dimension;
import com.wellsoft.pt.report.echart.option.SourceKeyValue;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
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
 * 7/14/25.1	    zhulh		7/14/25		    Create
 * </pre>
 * @date 7/14/25
 */
@Component
public class FlowOverdueDetailSummaryEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowInstanceReportService flowInstanceReportService;

    @Autowired
    private FlowOverdueReportService flowOverdueReportService;

    @Override
    public String datasetName() {
        return "流程统计分析_流程逾期详细统计表概要";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        List<Dataset> datasets = Lists.newArrayList();
        long overdueCount = flowOverdueReportService.queryOverdueCount(Maps.newHashMap(params));
        datasets.add(buildDataset("逾期流程数", "flowOverdueCount", overdueCount));
        // 逾期最多流程
        QueryItem maxOverdueCountItem = flowOverdueReportService.getMaxOverdueCountByDefinition(Maps.newHashMap(params));
        if (maxOverdueCountItem != null) {
            datasets.add(buildMaxOverdueCountByDefinitionDataset(maxOverdueCountItem));
        }
        // 逾期最长流程
        QueryItem maxOverdueTimeItem = flowOverdueReportService.getMaxOverdue(Maps.newHashMap(params));
        if (maxOverdueTimeItem != null) {
            datasets.add(buildMaxOverdueDataset(maxOverdueTimeItem));
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

    private Dataset buildMaxOverdueCountByDefinitionDataset(QueryItem queryItem) {
        String categoryName = "逾期最长流程";
        String categoryCode = "maxOverdueCountByDefinition";
        Long count = Math.round(queryItem.getDouble("count"));
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

    private Dataset buildMaxOverdueDataset(QueryItem queryItem) {
        String categoryName = "逾期最长流程";
        String categoryCode = "maxOverdueTimeInMinute";
        Double overdueMinutes = queryItem.getDouble("overdueMinutes");
        Long count = overdueMinutes == null ? 0 : Math.round(overdueMinutes);
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

}
