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
import com.wellsoft.pt.app.workflow.report.service.FlowInstanceReportService;
import com.wellsoft.pt.app.workflow.report.service.FowOperationReportService;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.Dimension;
import com.wellsoft.pt.report.echart.option.SourceKeyValue;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
import org.apache.commons.collections.CollectionUtils;
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
public class FlowOperationSummaryEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowInstanceReportService flowInstanceReportService;

    @Autowired
    private FowOperationReportService fowOperationReportService;

    @Override
    public String datasetName() {
        return "流程统计分析_流程行为分析概要";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        List<Dataset> datasets = Lists.newArrayList();
        long startCount = flowInstanceReportService.queryFlowInstanceStartCount(Maps.newHashMap(params));
        datasets.add(buildDataset("发起的流程总数", "startCount", startCount));

        long inefficientCount = fowOperationReportService.queryInefficientCount(Maps.newHashMap(params));
        datasets.add(buildDataset("低效操作流程数", "inefficientCount", inefficientCount));
        datasets.add(buildDataset("平均低效操作率", "avgInefficientPercent", startCount > 0 ? Double.valueOf(String.format("%.2f", inefficientCount * 100d / startCount)) : 0d));

        // 低效操作率最高流程
        List<QueryItem> inefficientPercents = fowOperationReportService.listInefficientPercent(Maps.newHashMap(params), new PagingInfo(1, 1));
        if (CollectionUtils.isNotEmpty(inefficientPercents)) {
            datasets.add(buildMaxInefficientCountByDefinitionDataset(inefficientPercents.get(0)));
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

    private Dataset buildMaxInefficientCountByDefinitionDataset(QueryItem queryItem) {
        String categoryName = "低效操作率最高流程";
        String categoryCode = "maxInefficientCountByDefinition";
        Long count = Math.round(queryItem.getDouble("totalCount"));
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
