/*
 * @(#)6/27/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.wellsoft.pt.app.workflow.report.query.FlowInstanceCountQueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowInstanceReportService;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.Dimension;
import com.wellsoft.pt.report.echart.option.SourceKeyValue;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
import org.apache.commons.lang.BooleanUtils;
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
 * 6/27/25.1	    zhulh		6/27/25		    Create
 * </pre>
 * @date 6/27/25
 */
@Component
public class FlowInstanceCountEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowInstanceReportService flowInstanceReportService;

    @Override
    public String datasetName() {
        return "流程统计分析_流程实例发起数量统计";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        List<FlowInstanceCountQueryItem> queryItems = flowInstanceReportService.queryFlowInstanceCount(params);
        List<Dataset> datasets = Lists.newArrayList();
        datasets.add(buildDataset("发起的流程总数", queryItems.stream().mapToLong(FlowInstanceCountQueryItem::getCount).sum()));
        datasets.add(buildDataset("已结束流程数", queryItems.stream().filter(item -> BooleanUtils.isTrue(item.getCompleted())).mapToLong(FlowInstanceCountQueryItem::getCount).sum()));
        datasets.add(buildDataset("流转中流程数", queryItems.stream().filter(item -> BooleanUtils.isNotTrue(item.getCompleted())).mapToLong(FlowInstanceCountQueryItem::getCount).sum()));
        return datasets;
    }

    private Dataset buildDataset(String categoryName, long count) {
        Dataset dataset = new Dataset();
        // 添加维度信息
        dataset.getDimensions().add(new Dimension("分类"));
        dataset.getDimensions().add(new Dimension("数量", DimensionTypeEnum.FLOAT, null));

        dataset.getSource().add(
                new SourceKeyValue<String, Object>().add("分类", categoryName)
                        .add("数量", count));
        return dataset;
    }

}
