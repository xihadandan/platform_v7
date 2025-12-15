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
import com.wellsoft.pt.app.workflow.report.service.FlowHandleEfficiencyReportService;
import com.wellsoft.pt.app.workflow.report.service.FlowTodoReportService;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
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
public class FlowHandleEfficiencyByDeptEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowHandleEfficiencyReportService flowHandleEfficiencyReportService;

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Autowired
    private FlowTodoReportService flowTodoReportService;

    @Override
    public String datasetName() {
        return "流程统计分析_办理效率分析按部门统计";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        // 添加维度信息
        // 平均操作用时
        Dataset dataset1 = new Dataset();
        dataset1.getDimensions().add(new Dimension("部门名称"));
        dataset1.getDimensions().add(new Dimension("人均操作用时", DimensionTypeEnum.FLOAT, null));
        // 操作次数
        Dataset dataset2 = new Dataset();
        dataset2.getDimensions().add(new Dimension("部门名称"));
        dataset2.getDimensions().add(new Dimension("操作次数", DimensionTypeEnum.FLOAT, null));

        if (MapUtils.isEmpty(params)) {
            return Lists.newArrayList(dataset1, dataset2);
        }
        Map<String, String> deptMap = flowTodoReportService.listFlowTodoDeptIdAndName(params);
        if (MapUtils.isEmpty(deptMap)) {
            return Lists.newArrayList(dataset1, dataset2);
        }

        int maxBarCount = FlowReportUtils.getMaxUserBarCountOfFlowHandleEfficiency(flowSettingService.getWorkFlowSettings());
        List<String> deptIds = Lists.newArrayList(deptMap.keySet());
        List<QueryItem> queryItems = flowHandleEfficiencyReportService.queryAverageHandleTimeInMinuteByDeptGroupByDept(deptIds, Maps.newHashMap(params), new PagingInfo(1, maxBarCount));

        // 平均操作用时
        queryItems.forEach(item -> {
            String deptId = item.getString("deptId");
            double avgHandleTimeInMinute = item.getDouble("avgHandleTime");
            double avgHandleTimeInHour = Double.valueOf(String.format("%.1f", avgHandleTimeInMinute / 60.0));
            dataset1.getSource().add(
                    new SourceKeyValue<String, Object>().add("部门名称", deptMap.get(deptId))
                            .add("人均操作用时", avgHandleTimeInHour)
                            .add("avgHandleTimeInHour", avgHandleTimeInHour)
                            .add("avgHandleTimeInMinute", Math.round(avgHandleTimeInMinute))
                            .add("deptId", deptId));
        });

        // 操作次数
        queryItems.forEach(item -> {
            String deptId = item.getString("deptId");
            long handleCount = item.getLong("handleCount");
            dataset2.getSource().add(
                    new SourceKeyValue<String, Object>().add("部门名称", deptMap.get(deptId))
                            .add("操作次数", handleCount)
                            .add("handleCount", handleCount)
                            .add("deptId", deptId));
        });

        return Lists.newArrayList(dataset1, dataset2);
    }

}
