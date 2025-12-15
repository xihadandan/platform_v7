/*
 * @(#)8/5/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.pt.app.workflow.report.service.FlowHandleEfficiencyReportService;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.Dimension;
import com.wellsoft.pt.report.echart.option.SourceKeyValue;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

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
public class FlowHandleEfficiencyByUserSummaryEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowHandleEfficiencyReportService flowHandleEfficiencyReportService;

    @Override
    public String datasetName() {
        return "流程统计分析_办理效率分析按人员概要";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        String userRange = Objects.toString(params.get("userRange"), StringUtils.EMPTY);
        if (StringUtils.isBlank(userRange)) {
            return Collections.emptyList();
        }
        Map<String, String> userMap = FlowReportUtils.getUsersByIds(userRange);
        Set<String> userIds = userMap.keySet();

        List<Dataset> datasets = Lists.newArrayList();
        datasets.add(buildDataset("统计人数", "userCount", userIds.size()));

        if (CollectionUtils.size(userIds) > 0) {
            params.put("userIds", userIds);
            double avgHandleTimeInMinute = flowHandleEfficiencyReportService.queryAverageHandleTimeInMinuteByUser(Maps.newHashMap(params));
            datasets.add(buildDataset("人员平均操作用时", "avgHandleTimeInMinute", Math.round(avgHandleTimeInMinute)));
        }

        if (CollectionUtils.size(userIds) > 0) {
            params.put("userIds", userIds);
            double maxHandleTimeInMinute = flowHandleEfficiencyReportService.queryMaxHandleTimeInMinuteByUser(Maps.newHashMap(params));
            datasets.add(buildDataset("人员最长操作用时", "maxHandleTimeInMinute", Math.round(maxHandleTimeInMinute)));
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
