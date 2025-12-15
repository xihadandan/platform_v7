/*
 * @(#)8/5/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowHandleOverdueReportService;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.Dimension;
import com.wellsoft.pt.report.echart.option.SourceKeyValue;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

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
public class FlowHandleOverdueByUserSummaryEchartDatasetLoad implements EchartDatasetLoad {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FlowHandleOverdueReportService flowHandleOverdueReportService;

    @Autowired
    private ScheduledExecutorService scheduledExecutorService;

    @Override
    public String datasetName() {
        return "流程统计分析_办理逾期分析按人员概要";
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

        try {
            if (CollectionUtils.size(userIds) > 0) {
                params.put("userIds", userIds);

                Future<Long> overdueUserCountFuture = scheduledExecutorService.submit(() ->
                        flowHandleOverdueReportService.queryOverdueCountByUser(Maps.newHashMap(params)));
                Future<Double> avgOverdueTimeInMinuteFuture = scheduledExecutorService.submit(() ->
                        flowHandleOverdueReportService.queryAvgOverdueTimeByUser(Maps.newHashMap(params)));
                Future<Double> avgOverdueCountFuture = scheduledExecutorService.submit(() ->
                        flowHandleOverdueReportService.queryAvgOverdueCountByUser(Maps.newHashMap(params)));
                Future<QueryItem> avgMaxOverdueTimeItemFuture = scheduledExecutorService.submit(() ->
                        flowHandleOverdueReportService.getAvgMaxOverdueTimeByUser(Maps.newHashMap(params)));

                long overdueUserCount = overdueUserCountFuture.get();// flowHandleOverdueReportService.queryOverdueCountByUser(Maps.newHashMap(params));
                datasets.add(buildDataset("逾期人数", "overdueUserCount", Math.round(overdueUserCount)));

                double avgOverdueTimeInMinute = avgOverdueTimeInMinuteFuture.get(); // flowHandleOverdueReportService.queryAvgOverdueTimeByUser(Maps.newHashMap(params));
                datasets.add(buildDataset("人员平均逾期时长", "avgOverdueTimeInMinute", Math.round(avgOverdueTimeInMinute)));

                double avgOverdueCount = avgOverdueCountFuture.get();// flowHandleOverdueReportService.queryAvgOverdueCountByUser(Maps.newHashMap(params));
                datasets.add(buildDataset("人员平均逾期次数", "avgOverdueCount", Double.valueOf(String.format("%.1f", avgOverdueCount))));

                // 平均逾期时长最长流程
                QueryItem avgMaxOverdueTimeItem = avgMaxOverdueTimeItemFuture.get(); // flowHandleOverdueReportService.getAvgMaxOverdueTimeByUser(Maps.newHashMap(params));
                if (avgMaxOverdueTimeItem != null) {
                    datasets.add(buildAvgMaxOverdueDataset(avgMaxOverdueTimeItem));
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return datasets;
    }

    private Dataset buildAvgMaxOverdueDataset(QueryItem queryItem) {
        String categoryName = "平均逾期时长最长流程";
        String categoryCode = "avgMaxOverdueTimeInMinute";
        Double avgOverdueMinutes = queryItem.getDouble("avgOverdueMinutes");
        Long count = avgOverdueMinutes == null ? 0 : Math.round(avgOverdueMinutes);
        String flowDefName = queryItem.getString("name");
        Dataset dataset = new Dataset();
        // 添加维度信息
        dataset.getDimensions().add(new Dimension("分类"));
        dataset.getDimensions().add(new Dimension("数量", DimensionTypeEnum.FLOAT, null));

        dataset.getSource().add(
                new SourceKeyValue<String, Object>().add("分类", categoryName)
                        .add("数量", count).add(categoryCode, count).add("name", flowDefName));
        return dataset;
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
