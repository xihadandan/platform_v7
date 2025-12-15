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
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.Dimension;
import com.wellsoft.pt.report.echart.option.SourceKeyValue;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

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
public class FlowOverdueCountEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowInstanceReportService flowInstanceReportService;

    @Autowired
    private FlowOverdueReportService flowOverdueReportService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String datasetName() {
        return "流程统计分析_流程逾期数量排名";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        int maxBarCount = FlowReportUtils.getMaxBarCountOfFlowOverdue(flowSettingService.getWorkFlowSettings(), 12);
        List<QueryItem> queryItems = flowOverdueReportService.listOverdueCount(Maps.newHashMap(params), null);
        Dataset dataset1 = new Dataset();
        // 添加维度信息
        dataset1.getDimensions().add(new Dimension("流程名称"));
        dataset1.getDimensions().add(new Dimension("数量", DimensionTypeEnum.FLOAT, null));

        // 数量
        AtomicInteger barCount = new AtomicInteger(1);
        queryItems.forEach(item -> {
            if (barCount.getAndIncrement() > maxBarCount) {
                return;
            }
            dataset1.getSource().add(
                    new SourceKeyValue<String, Object>().add("流程名称", item.getString("name"))
                            .add("数量", item.getLong("count"))
                            .add("count", item.getLong("count"))
                            .add("id", item.getString("id")));
        });

        // 占比
        Dataset dataset2 = new Dataset();
        dataset2.getDimensions().add(new Dimension("流程名称"));
        dataset2.getDimensions().add(new Dimension("累计占比", DimensionTypeEnum.FLOAT, null));
        long totalCount = queryItems.stream().mapToLong(item -> item.getLong("count")).sum();
        double totalPercent = 0d;
        barCount.set(1);
        for (QueryItem item : queryItems) {
            if (barCount.getAndIncrement() > maxBarCount) {
                continue;
            }
            double percent = totalCount > 0 ? (item.getLong("count") * 100d / totalCount) : 0;
            totalPercent += percent;
            dataset2.getSource().add(
                    new SourceKeyValue<String, Object>().add("流程名称", item.getString("name"))
                            .add("累计占比", Double.valueOf(String.format("%.2f", totalPercent)))
                            .add("totalPercent", Double.valueOf(String.format("%.2f", totalPercent)))
                            .add("percent", Double.valueOf(String.format("%.2f", percent)))
                            .add("id", item.getString("id")));
        }

        return Lists.newArrayList(dataset1, dataset2);
    }

}
