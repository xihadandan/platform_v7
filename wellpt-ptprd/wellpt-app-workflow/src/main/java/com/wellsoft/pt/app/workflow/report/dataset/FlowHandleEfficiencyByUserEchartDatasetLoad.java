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
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.Dimension;
import com.wellsoft.pt.report.echart.option.SourceKeyValue;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

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
public class FlowHandleEfficiencyByUserEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowHandleEfficiencyReportService flowHandleEfficiencyReportService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String datasetName() {
        return "流程统计分析_办理效率分析按人员统计";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        // 添加维度信息
        // 平均操作用时
        Dataset dataset1 = new Dataset();
        dataset1.getDimensions().add(new Dimension("人员名称"));
        dataset1.getDimensions().add(new Dimension("平均操作用时", DimensionTypeEnum.FLOAT, null));
        // 操作次数
        Dataset dataset2 = new Dataset();
        dataset2.getDimensions().add(new Dimension("人员名称"));
        dataset2.getDimensions().add(new Dimension("操作次数", DimensionTypeEnum.FLOAT, null));

        String userRange = Objects.toString(params.get("userRange"), StringUtils.EMPTY);
        if (StringUtils.isBlank(userRange)) {
            return Lists.newArrayList(dataset1, dataset2);
        }
        Map<String, String> userMap = FlowReportUtils.getUsersByIds(userRange);
        Set<String> userIds = userMap.keySet();
        params.put("userIds", userIds);

        int maxBarCount = FlowReportUtils.getMaxDeptBarCountOfFlowHandleEfficiency(flowSettingService.getWorkFlowSettings());
        List<QueryItem> queryItems = flowHandleEfficiencyReportService.queryAverageHandleTimeInMinuteByUserGroupByUser(Maps.newHashMap(params), new PagingInfo(1, maxBarCount));

        // 平均操作用时
        queryItems.forEach(item -> {
            String userId = item.getString("userId");
            double avgHandleTimeInMinute = item.getDouble("avgHandleTime");
            double avgHandleTimeInHour = Double.valueOf(String.format("%.1f", avgHandleTimeInMinute / 60.0));
            dataset1.getSource().add(
                    new SourceKeyValue<String, Object>().add("人员名称", userMap.get(userId))
                            .add("平均操作用时", avgHandleTimeInHour)
                            .add("avgHandleTimeInHour", avgHandleTimeInHour)
                            .add("avgHandleTimeInMinute", Math.round(avgHandleTimeInMinute))
                            .add("userId", userId));
        });

        // 操作次数
        queryItems.forEach(item -> {
            String userId = item.getString("userId");
            long handleCount = item.getLong("handleCount");
            dataset2.getSource().add(
                    new SourceKeyValue<String, Object>().add("人员名称", userMap.get(userId))
                            .add("操作次数", handleCount)
                            .add("handleCount", handleCount)
                            .add("userId", userId));
        });

        return Lists.newArrayList(dataset1, dataset2);
    }

}
