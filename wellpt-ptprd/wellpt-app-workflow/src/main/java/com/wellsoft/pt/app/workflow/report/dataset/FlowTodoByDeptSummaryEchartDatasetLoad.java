/*
 * @(#)7/9/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowTodoReportService;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
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
public class FlowTodoByDeptSummaryEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowTodoReportService flowTodoReportService;

    @Autowired
    private OrgFacadeService orgFacadeService;

    @Override
    public String datasetName() {
        return "流程统计分析_待办流程分析按部门概要";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
//        String deptRange = Objects.toString(params.get("deptRange"), StringUtils.EMPTY);
//        if (StringUtils.isBlank(deptRange)) {
//            return Collections.emptyList();
//        }
        List<String> deptIds = flowTodoReportService.listFlowTodoDeptId(params);
        if (CollectionUtils.isEmpty(deptIds)) {
            return Lists.newArrayList();
        }

        List<Dataset> datasets = Lists.newArrayList();
        datasets.add(buildDataset("统计部门", "deptCount", deptIds.size()));

        if (CollectionUtils.size(deptIds) > 0) {
            long todoCount = flowTodoReportService.getFlowTodoCountByDept(deptIds, Maps.newHashMap(params));
            datasets.add(buildDataset("待办数量", "todoCount", todoCount));
        }

        if (CollectionUtils.size(deptIds) > 0) {
            QueryItem queryItem = flowTodoReportService.getMaxFlowTodoCountByDept(deptIds, Maps.newHashMap(params));
            if (queryItem != null) {
                datasets.add(buildMaxFlowTodoCountByDeptGrouyByFlowDataset(queryItem));
            }
        }

        return datasets;
    }

    private Dataset buildMaxFlowTodoCountByDeptGrouyByFlowDataset(QueryItem queryItem) {
        String categoryName = "待办最多流程";
        String categoryCode = "maxFlowTodoCountByDeptGrouyByFlow";
        Long count = Math.round(queryItem.getDouble("count"));
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
