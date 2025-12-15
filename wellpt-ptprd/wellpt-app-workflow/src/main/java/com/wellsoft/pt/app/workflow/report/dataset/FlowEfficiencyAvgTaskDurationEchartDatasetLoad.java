/*
 * @(#)7/10/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowEfficiencyReportService;
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

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 7/10/25.1	    zhulh		7/10/25		    Create
 * </pre>
 * @date 7/10/25
 */
@Component
public class FlowEfficiencyAvgTaskDurationEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowEfficiencyReportService flowEfficiencyReportService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String datasetName() {
        return "流程统计分析_流程效率分析_流程审批环节平均用时";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        int maxBarCount = FlowReportUtils.getMaxTaskBarCountOfFlowEfficiency(flowSettingService.getWorkFlowSettings(), 12);
        List<QueryItem> queryItems = flowEfficiencyReportService.listAverageTaskDurationInMinute(params, new PagingInfo(1, maxBarCount));

        Dataset dataset = new Dataset();
        // 添加维度信息
        dataset.getDimensions().add(new Dimension("流程名称"));
        dataset.getDimensions().add(new Dimension("平均时长", DimensionTypeEnum.FLOAT, null));

        queryItems.forEach(item -> {
            long avgTaskDurationInMinute = Math.round(item.getDouble("avgDuration"));
            dataset.getSource().add(
                    new SourceKeyValue<String, Object>().add("流程名称", item.getString("name"))
                            .add("平均时长", (avgTaskDurationInMinute / (60 * 24D)))
                            .add("avgTaskDurationInMinute", avgTaskDurationInMinute).add("id", item.getString("id")));
        });

        return Lists.newArrayList(dataset);
    }

}
