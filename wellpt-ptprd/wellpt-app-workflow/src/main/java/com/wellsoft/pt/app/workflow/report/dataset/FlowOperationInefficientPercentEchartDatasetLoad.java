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
import com.wellsoft.pt.app.workflow.report.service.FowOperationReportService;
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
public class FlowOperationInefficientPercentEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private FowOperationReportService fowOperationReportService;

    @Override
    public String datasetName() {
        return "流程统计分析_流程行为分析_流程低效操作率";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        int maxBarCount = FlowReportUtils.getMaxBarCountOfFlowInefficient(flowSettingService.getWorkFlowSettings(), 12);
        List<QueryItem> queryItems = fowOperationReportService.listInefficientPercent(Maps.newHashMap(params), new PagingInfo(1, maxBarCount));
        String seriesName = params.containsKey("seriesName") ? Objects.toString(params.get("seriesName"), "低效操作率") : "低效操作率";
        Dataset dataset = new Dataset();
        // 添加维度信息
        dataset.getDimensions().add(new Dimension("流程名称"));
        dataset.getDimensions().add(new Dimension(seriesName, DimensionTypeEnum.FLOAT, null));

        // 数量
        queryItems.forEach(item -> {
            long totalCount = Math.round(item.getDouble("totalCount"));
            long inefficientCount = Math.round(item.getDouble("inefficientCount"));
            Double percent = item.getDouble("inefficientPercent");
            if (percent == null) {
                percent = inefficientCount * 100.0 / totalCount;
            }
            dataset.getSource().add(
                    new SourceKeyValue<String, Object>().add("流程名称", item.getString("name"))
                            .add(seriesName, Double.valueOf(String.format(" % .2f", percent)))
                            .add("totalCount", totalCount)
                            .add("inefficientCount", inefficientCount)
                            .add("id", item.getString("id")));
        });

        return Lists.newArrayList(dataset);
    }

}
