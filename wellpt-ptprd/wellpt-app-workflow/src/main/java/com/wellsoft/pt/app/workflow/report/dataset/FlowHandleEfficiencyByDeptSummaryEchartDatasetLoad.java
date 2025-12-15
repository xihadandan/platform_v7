/*
 * @(#)8/5/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.app.workflow.report.service.FlowHandleEfficiencyReportService;
import com.wellsoft.pt.app.workflow.report.service.FlowTodoReportService;
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
 * 8/5/25.1	    zhulh		8/5/25		    Create
 * </pre>
 * @date 8/5/25
 */
@Component
public class FlowHandleEfficiencyByDeptSummaryEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowHandleEfficiencyReportService flowHandleEfficiencyReportService;

    @Autowired
    private FlowTodoReportService flowTodoReportService;

    @Override
    public String datasetName() {
        return "流程统计分析_办理效率分析按部门概要";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        List<String> deptIds = flowTodoReportService.listFlowTodoDeptId(params);
        if (CollectionUtils.isEmpty(deptIds)) {
            return Lists.newArrayList();
        }

        List<Dataset> datasets = Lists.newArrayList();
        datasets.add(buildDataset("统计部门", "deptCount", deptIds.size()));

        if (CollectionUtils.size(deptIds) > 0) {
            double avgHandleTimeInMinute = flowHandleEfficiencyReportService.queryAverageHandleTimeInMinuteByDept(deptIds, Maps.newHashMap(params));
            datasets.add(buildDataset("部门平均操作用时", "avgHandleTimeInMinute", Math.round(avgHandleTimeInMinute)));
        }

        return datasets;
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
