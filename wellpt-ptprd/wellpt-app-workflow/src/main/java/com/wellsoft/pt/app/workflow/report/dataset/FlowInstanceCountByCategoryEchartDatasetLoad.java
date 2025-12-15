/*
 * @(#)6/27/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.dataset;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.app.workflow.report.query.FlowInstanceCountByCategoryQueryItem;
import com.wellsoft.pt.app.workflow.report.service.FlowInstanceReportService;
import com.wellsoft.pt.app.workflow.report.utils.FlowReportUtils;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.report.echart.enums.DimensionTypeEnum;
import com.wellsoft.pt.report.echart.option.Dataset;
import com.wellsoft.pt.report.echart.option.Dimension;
import com.wellsoft.pt.report.echart.option.SourceKeyValue;
import com.wellsoft.pt.report.echart.support.EchartDatasetLoad;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class FlowInstanceCountByCategoryEchartDatasetLoad implements EchartDatasetLoad {

    @Autowired
    private FlowInstanceReportService flowInstanceReportService;

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String datasetName() {
        return "流程统计分析_流程实例发起数量按流程分类统计";
    }

    @Override
    public List<Dataset> loadDataset(Map<String, Object> params) {
        List<FlowInstanceCountByCategoryQueryItem> items = flowInstanceReportService.queryFlowInstanceCountByCategory(params);

        FlowInstanceCountByCategoryData data = new FlowInstanceCountByCategoryData(items);
        data.decorate();

        int maxBarCount = FlowReportUtils.getMaxBarCountOfFlowInstCount(flowSettingService.getWorkFlowSettings(), 12);
        List<Dataset> datasets = Lists.newArrayList();
        datasets.add(createDataset(data.getCompletedItems(maxBarCount)));
        datasets.add(createDataset(data.getUncompletedItems(maxBarCount)));

        return datasets;
    }

    private Dataset createDataset(List<FlowInstanceCountByCategoryQueryItem> completedItems) {
        Dataset dataset = new Dataset();
        // 添加维度信息
        dataset.getDimensions().add(new Dimension("流程分类"));
        dataset.getDimensions().add(new Dimension("数量", DimensionTypeEnum.FLOAT, null));

        completedItems.forEach(item -> {
            dataset.getSource().add(
                    new SourceKeyValue<String, Object>().add("流程分类", item.getCategoryName())
                            .add("数量", item.getCount()).add("categoryUuid", item.getCategoryUuid()));
        });

        return dataset;
    }

    private class FlowInstanceCountByCategoryData extends BaseObject {

        List<FlowInstanceCountByCategoryQueryItem> items;

        List<FlowInstanceCountByCategoryQueryItem> completedItems;

        List<FlowInstanceCountByCategoryQueryItem> uncompletedItems;

        public FlowInstanceCountByCategoryData(List<FlowInstanceCountByCategoryQueryItem> items) {
            this.items = items;
        }

        /**
         * @return the completedItems
         */
        public List<FlowInstanceCountByCategoryQueryItem> getCompletedItems(int subListSize) {
            if (CollectionUtils.size(completedItems) <= subListSize) {
                return completedItems;
            }
            return completedItems.subList(0, subListSize);
        }

        /**
         * @return the uncompletedItems
         */
        public List<FlowInstanceCountByCategoryQueryItem> getUncompletedItems(int subListSize) {
            if (CollectionUtils.size(uncompletedItems) <= subListSize) {
                return uncompletedItems;
            }
            return uncompletedItems.subList(0, subListSize);
        }

        public void decorate() {
            List<FlowInstanceCountByCategoryQueryItem> completedItems = items.stream().filter(item -> BooleanUtils.isTrue(item.getCompleted())).collect(Collectors.toList());
            List<FlowInstanceCountByCategoryQueryItem> uncompletedItems = items.stream().filter(item -> BooleanUtils.isNotTrue(item.getCompleted())).collect(Collectors.toList());

            // 同步实例数量，确保两者数量一致
            syncItems(completedItems, uncompletedItems);

            List<FlowInstanceCountByCategoryItem> categoryItems = createCategoryItems(completedItems, uncompletedItems);

            Collections.sort(categoryItems);

            this.completedItems = categoryItems.stream().map(item -> item.completedItem).collect(Collectors.toList());
            this.uncompletedItems = categoryItems.stream().map(item -> item.uncompletedItem).collect(Collectors.toList());
        }

        private List<FlowInstanceCountByCategoryItem> createCategoryItems(List<FlowInstanceCountByCategoryQueryItem> completedItems, List<FlowInstanceCountByCategoryQueryItem> uncompletedItems) {
            Map<String, FlowInstanceCountByCategoryQueryItem> completedMap = ConvertUtils.convertElementToMap(completedItems, "categoryUuid");
            Map<String, FlowInstanceCountByCategoryQueryItem> uncompletedMap = ConvertUtils.convertElementToMap(uncompletedItems, "categoryUuid");

            List<FlowInstanceCountByCategoryItem> categoryItems = Lists.newArrayList();
            completedMap.forEach((key, value) -> {
                FlowInstanceCountByCategoryItem categoryItem = new FlowInstanceCountByCategoryItem(value, uncompletedMap.get(key));
                categoryItems.add(categoryItem);
            });
            return categoryItems;
        }

        /**
         * @param completedItems
         * @param uncompletedItems
         */
        private void syncItems(List<FlowInstanceCountByCategoryQueryItem> completedItems, List<FlowInstanceCountByCategoryQueryItem> uncompletedItems) {
            List<String> completedCategoryUuids = completedItems.stream().map(FlowInstanceCountByCategoryQueryItem::getCategoryUuid).distinct().collect(Collectors.toList());
            List<String> uncompletedCategoryUuids = uncompletedItems.stream().map(FlowInstanceCountByCategoryQueryItem::getCategoryUuid).distinct().collect(Collectors.toList());
            List<String> tmpCompletedCategoryUuids = Lists.newArrayList(completedCategoryUuids);
            List<String> tmpUncompletedCategoryUuids = Lists.newArrayList(uncompletedCategoryUuids);
            tmpCompletedCategoryUuids.removeAll(tmpUncompletedCategoryUuids);
            if (CollectionUtils.isNotEmpty(tmpCompletedCategoryUuids)) {
                for (String categoryUuid : tmpCompletedCategoryUuids) {
                    FlowInstanceCountByCategoryQueryItem completedItem = completedItems.stream().filter(item -> StringUtils.equals(categoryUuid, item.getCategoryUuid())).findFirst().get();
                    FlowInstanceCountByCategoryQueryItem uncompletedItem = new FlowInstanceCountByCategoryQueryItem();
                    BeanUtils.copyProperties(completedItem, uncompletedItem);
                    uncompletedItem.setCompleted(false);
                    uncompletedItem.setCount(0L);
                    uncompletedItems.add(uncompletedItem);
                }
            }

            tmpCompletedCategoryUuids = Lists.newArrayList(completedCategoryUuids);
            tmpUncompletedCategoryUuids.removeAll(tmpCompletedCategoryUuids);
            if (CollectionUtils.isNotEmpty(tmpUncompletedCategoryUuids)) {
                for (String categoryUuid : tmpUncompletedCategoryUuids) {
                    FlowInstanceCountByCategoryQueryItem uncompletedItem = uncompletedItems.stream().filter(item -> StringUtils.equals(categoryUuid, item.getCategoryUuid())).findFirst().get();
                    FlowInstanceCountByCategoryQueryItem completedItem = new FlowInstanceCountByCategoryQueryItem();
                    BeanUtils.copyProperties(uncompletedItem, completedItem);
                    completedItem.setCompleted(true);
                    completedItem.setCount(0L);
                    completedItems.add(completedItem);
                }
            }
        }
    }

    private class FlowInstanceCountByCategoryItem implements Comparable<FlowInstanceCountByCategoryItem> {

        FlowInstanceCountByCategoryQueryItem completedItem;
        FlowInstanceCountByCategoryQueryItem uncompletedItem;

        FlowInstanceCountByCategoryItem(FlowInstanceCountByCategoryQueryItem completedItem, FlowInstanceCountByCategoryQueryItem uncompletedItem) {
            this.completedItem = completedItem;
            this.uncompletedItem = uncompletedItem;
        }

        @Override
        public int compareTo(FlowInstanceCountByCategoryItem o) {
            return -Long.valueOf((completedItem.getCount() + uncompletedItem.getCount())).compareTo(Long.valueOf((o.completedItem.getCount() + o.uncompletedItem.getCount())));
        }
    }

}
