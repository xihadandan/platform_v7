/*
 * @(#)7/9/25 V1.0
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
 * 7/9/25.1	    zhulh		7/9/25		    Create
 * </pre>
 * @date 7/9/25
 */
@Component
public class FlowEfficiencyAvgCompletedTimeEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowEfficiencyReportService flowEfficiencyReportService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String datasetName() {
        return "流程统计分析_流程效率分析_流程审批平均时长";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        int maxBarCount = FlowReportUtils.getMaxFlowBarCountOfFlowEfficiency(flowSettingService.getWorkFlowSettings(), 12);
        params.put("flowCompleted", true);
        List<QueryItem> queryItems = flowEfficiencyReportService.listFlowAverageCompletedTimeInMinute(params, new PagingInfo(1, maxBarCount));

        Dataset dataset = new Dataset();
        // 添加维度信息
        dataset.getDimensions().add(new Dimension("流程名称"));
        dataset.getDimensions().add(new Dimension("平均时长", DimensionTypeEnum.FLOAT, null));

        queryItems.forEach(item -> {
            long avgCompletedTimeInMinute = Math.round(item.getDouble("avgCompletedTime"));
            dataset.getSource().add(
                    new SourceKeyValue<String, Object>().add("流程名称", item.getString("name"))
                            .add("平均时长", (avgCompletedTimeInMinute / (60 * 24D)))
                            .add("avgCompletedTimeInMinute", avgCompletedTimeInMinute).add("id", item.getString("id")));
        });

        return Lists.newArrayList(dataset);
    }

}
