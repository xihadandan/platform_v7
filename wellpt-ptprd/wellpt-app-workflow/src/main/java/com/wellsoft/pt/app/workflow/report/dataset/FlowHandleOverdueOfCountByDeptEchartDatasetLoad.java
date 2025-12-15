/*
 * @(#)8/5/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowHandleOverdueReportService;
import com.wellsoft.pt.app.workflow.report.service.FlowTodoReportService;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.Dimension;
import com.wellsoft.pt.report.echart.option.SourceKeyValue;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.MapUtils;
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
 * 8/5/25.1	    zhulh		8/5/25		    Create
 * </pre>
 * @date 8/5/25
 */
@Component
public class FlowHandleOverdueOfCountByDeptEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowHandleOverdueReportService flowHandleOverdueReportService;

    @Autowired
    private FlowTodoReportService flowTodoReportService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String datasetName() {
        return "流程统计分析_办理逾期分析_部门逾期次数";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        // 添加维度信息
        // 平均逾期时长
        Dataset dataset = new Dataset();
        dataset.getDimensions().add(new Dimension("部门名称"));
        dataset.getDimensions().add(new Dimension("逾期次数", DimensionTypeEnum.FLOAT, null));

        if (MapUtils.isEmpty(params)) {
            return Lists.newArrayList(dataset);
        }
        Map<String, String> deptMap = flowTodoReportService.listFlowTodoDeptIdAndName(params);
        if (MapUtils.isEmpty(deptMap)) {
            return Lists.newArrayList(dataset);
        }

        int maxBarCount = FlowReportUtils.getMaxDeptBarCountOfFlowHandleOverdue(flowSettingService.getWorkFlowSettings());
        List<String> deptIds = Lists.newArrayList(deptMap.keySet());
        List<QueryItem> queryItems = flowHandleOverdueReportService.queryOverdueCountByDeptGroupByDept(deptIds, Maps.newHashMap(params), new PagingInfo(1, maxBarCount));

        // 平均逾期时长
        queryItems.forEach(item -> {
            String deptId = item.getString("deptId");
            long overdueCount = item.getLong("overdueCount");
            dataset.getSource().add(
                    new SourceKeyValue<String, Object>().add("部门名称", deptMap.get(deptId))
                            .add("逾期次数", overdueCount)
                            .add("overdueCount", overdueCount)
                            .add("deptId", deptId));
        });

        return Lists.newArrayList(dataset);
    }

}
