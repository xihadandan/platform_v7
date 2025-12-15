/*
 * @(#)7/9/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.app.workflow.report.service.FlowInstanceReportService;
import com.wellsoft.pt.app.workflow.report.service.FowOperationReportService;
import com.wellsoft.pt.bpm.engine.support.WorkFlowOperation;
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
 * 7/9/25.1	    zhulh		7/9/25		    Create
 * </pre>
 * @date 7/9/25
 */
@Component
public class FlowOperationDetailSummaryEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowInstanceReportService flowInstanceReportService;

    @Autowired
    private FowOperationReportService fowOperationReportService;

    @Override
    public String datasetName() {
        return "流程统计分析_流程行为统计表概要";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        List<Dataset> datasets = Lists.newArrayList();
        long startCount = flowInstanceReportService.queryFlowInstanceStartCount(Maps.newHashMap(params));
        datasets.add(buildDataset("发起的流程总数", "startCount", startCount));

        // 退回
        buildTaskOperationDataset(datasets, "rollbackCount", "退回", WorkFlowOperation.ROLLBACK, Maps.newHashMap(params));
        // 转办
        buildTaskOperationDataset(datasets, "transferCount", "转办", WorkFlowOperation.TRANSFER, Maps.newHashMap(params));
        // 会签
        buildTaskOperationDataset(datasets, "counterSignCount", "会签", WorkFlowOperation.COUNTER_SIGN, Maps.newHashMap(params));
        // 加签
        buildTaskOperationDataset(datasets, "addSignCount", "加签", WorkFlowOperation.ADD_SIGN, Maps.newHashMap(params));
        // 移交
        buildTaskOperationDataset(datasets, "handOverCount", "移交", WorkFlowOperation.HAND_OVER, Maps.newHashMap(params));
        // 跳转
        buildTaskOperationDataset(datasets, "gotoTaskCount", "跳转", WorkFlowOperation.GOTO_TASK, Maps.newHashMap(params));

        return datasets;
    }

    private void buildTaskOperationDataset(List<Dataset> datasets, String categoryCode, String operationName, String operationType, Map<String, Object> params) {
        params.put("operationTypes", Lists.newArrayList(operationType));
        long count = fowOperationReportService.queryInefficientTaskCount(params);
        datasets.add(buildDataset(operationName + "次数", categoryCode, count));
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

}
