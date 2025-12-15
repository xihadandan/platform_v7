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
public class FlowOperationInefficientActionPercentEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowInstanceReportService flowInstanceReportService;

    @Autowired
    private FowOperationReportService fowOperationReportService;

    @Override
    public String datasetName() {
        return "流程统计分析_流程行为分析_流程各个低效操作率";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        List<Dataset> datasets = Lists.newArrayList();
        long startCount = flowInstanceReportService.queryFlowInstanceStartCount(Maps.newHashMap(params));

        Dataset dataset = new Dataset();
        // 添加维度信息
        dataset.getDimensions().add(new Dimension("操作名称"));
        dataset.getDimensions().add(new Dimension("操作率", DimensionTypeEnum.FLOAT, null));

        // 退回
        if (showOperation(WorkFlowOperation.ROLLBACK, params)) {
            addDataset(dataset, "退回", Maps.newHashMap(params), startCount, WorkFlowOperation.ROLLBACK);
        }
        // 转办
        if (showOperation(WorkFlowOperation.TRANSFER, params)) {
            addDataset(dataset, "转办", Maps.newHashMap(params), startCount, WorkFlowOperation.TRANSFER);
        }
        // 会签
        if (showOperation(WorkFlowOperation.COUNTER_SIGN, params)) {
            addDataset(dataset, "会签", Maps.newHashMap(params), startCount, WorkFlowOperation.COUNTER_SIGN);
        }
        // 加签
        if (showOperation(WorkFlowOperation.ADD_SIGN, params)) {
            addDataset(dataset, "加签", Maps.newHashMap(params), startCount, WorkFlowOperation.ADD_SIGN);
        }
        // 移交
        if (showOperation(WorkFlowOperation.HAND_OVER, params)) {
            addDataset(dataset, "移交", Maps.newHashMap(params), startCount, WorkFlowOperation.HAND_OVER);
        }
        // 跳转
        if (showOperation(WorkFlowOperation.GOTO_TASK, params)) {
            addDataset(dataset, "跳转", Maps.newHashMap(params), startCount, WorkFlowOperation.GOTO_TASK);
        }

        return Lists.newArrayList(dataset);
    }

    private void addDataset(Dataset dataset, String actionName, Map<String, Object> params, long startCount, String operationType) {
        params.put("operationTypes", Lists.newArrayList(operationType));
        long actionCount = fowOperationReportService.queryInefficientCount(Maps.newHashMap(params));
        double actionPercent = startCount > 0 ? actionCount * 100.0 / startCount : 0;
        dataset.getSource().add(
                new SourceKeyValue<String, Object>().add("操作名称", actionName)
                        .add("operationType", operationType)
                        .add("操作率", Double.valueOf(String.format(" % .2f", actionPercent)))
                        .add("percentName", actionName + "率")
                        .add("percent", Double.valueOf(String.format(" % .2f", actionPercent))));
    }

    private boolean showOperation(String actionType, Map<String, Object> params) {
        if (params.containsKey("operationTypes")) {
            List<String> operationTypes = (List<String>) params.get("operationTypes");
            return CollectionUtils.isNotEmpty(operationTypes) && operationTypes.contains(actionType);
        }
        return true;
    }

}
