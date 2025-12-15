/*
 * @(#)7/14/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
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
public class FlowOverdueAvgEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowInstanceReportService flowInstanceReportService;

    @Autowired
    private FlowOverdueReportService flowOverdueReportService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String datasetName() {
        return "流程统计分析_流程平均逾期时长排名";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        int maxBarCount = FlowReportUtils.getMaxBarCountOfFlowAvgOverdue(flowSettingService.getWorkFlowSettings(), 12);
        List<QueryItem> queryItems = flowOverdueReportService.listAvgOverdueTime(Maps.newHashMap(params), new PagingInfo(1, maxBarCount));
        Dataset dataset = new Dataset();
        // 添加维度信息
        dataset.getDimensions().add(new Dimension("流程名称"));
        dataset.getDimensions().add(new Dimension("平均逾期时长", DimensionTypeEnum.FLOAT, null));

        // 数量
        queryItems.forEach(item -> {
            long avgOverdueMinutes = Math.round(item.getDouble("avgOverdueMinutes"));
            dataset.getSource().add(
                    new SourceKeyValue<String, Object>().add("流程名称", item.getString("name"))
                            .add("平均逾期时长", avgOverdueMinutes / (60))
                            .add("avgOverdueTimeInMinute", avgOverdueMinutes)
                            .add("id", item.getString("id")));
        });

        return Lists.newArrayList(dataset);
    }

}
