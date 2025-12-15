/*
 * @(#)8/6/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.report.store;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AtomicDouble;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.SnowFlake;
import com.wellsoft.pt.jpa.criteria.CriteriaMetadata;
import com.wellsoft.pt.jpa.criteria.QueryContext;
import com.wellsoft.pt.workflow.service.WfFlowSettingService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/6/25.1	    zhulh		8/6/25		    Create
 * </pre>
 * @date 8/6/25
 */
@Component
public class FlowHandleEfficiencyByUserDetailDataStore extends FlowHandleEfficiencyByUserDataStore {

    @Autowired
    private WfFlowSettingService flowSettingService;

    @Override
    public String getQueryName() {
        return "流程统计分析_办理效率分析_人员办理时间分析表";
    }

    @Override
    public CriteriaMetadata initCriteriaMetadata(QueryContext context) {
        CriteriaMetadata criteriaMetadata = super.initCriteriaMetadata(context);
        criteriaMetadata.add("uuid", "t.uuid", "UUID", String.class);
        criteriaMetadata.add("id", "t.id", "流程定义ID", String.class);
        criteriaMetadata.add("name", "t.name", "流程定义名称", String.class);
        criteriaMetadata.add("taskId", "t.task_id", "环节ID", String.class);
        criteriaMetadata.add("taskName", "t.task_name", "环节名称", String.class);
        criteriaMetadata.add("flowAvgHandleTime", "t.flow_avg_handle_time", "流程平均用时", Double.class);
        criteriaMetadata.add("details", "t.details", "办理详情", String.class);
        return criteriaMetadata;
    }

    @Override
    public List<QueryItem> query(QueryContext context) {
        String userRange = Objects.toString(context.getQueryParams().get("userRange"), StringUtils.EMPTY);
        if (StringUtils.isBlank(userRange)) {
            return Collections.emptyList();
        }

        Map<String, Object> queryParams = getQueryParams(context);
        List<QueryItem> queryItems = context.getNativeDao().namedQuery("flowHandleEfficiencyAverageHandleTimeGroupByUserDetailQuery",
                queryParams, QueryItem.class, context.getPagingInfo());

        Map<String, List<QueryItem>> userItemMap = queryItems.stream().collect(Collectors.groupingBy(item -> item.getString("userId")));
        List<QueryItem> userItems = Lists.newArrayList();
        userItemMap.forEach((userId, items) -> {
            String userName = items.get(0).getString("userName");
            long handleCount = items.stream().mapToLong(item -> item.getLong("handleCount")).sum();
            double handleTime = items.stream().mapToDouble(item -> item.getDouble("avgHandleTime") * item.getLong("handleCount")).sum();
            double avgHandleTime = handleTime / handleCount;
            items.forEach(item -> {
                item.put("avgHandleTime", Math.round(item.getDouble("avgHandleTime")));
            });
            items.sort(Comparator.comparing(item -> item.getString("id")));
            // 流程平均用时
            AtomicLong totalHandleCount = new AtomicLong(0);
            AtomicDouble totalFlowAvgHandleTime = new AtomicDouble(0);
            Map<String, List<QueryItem>> flowItemMap = items.stream().collect(Collectors.groupingBy(item -> item.getString("id")));
            flowItemMap.forEach((flowId, flowItems) -> {
                long flowHandleCount = flowItems.stream().mapToLong(item -> item.getLong("handleCount")).sum();
                double flowHandleTime = flowItems.stream().mapToDouble(item -> item.getDouble("avgHandleTime") * item.getLong("handleCount")).sum();
                double flowAvgHandleTime = flowHandleTime / flowHandleCount;
                flowItems.forEach(item -> {
                    item.put("flowAvgHandleTime", Math.round(flowAvgHandleTime), false);
                });
                totalHandleCount.addAndGet(1);
                totalFlowAvgHandleTime.addAndGet(flowHandleTime);
            });

            QueryItem userItem = new QueryItem();
            userItem.put("uuid", SnowFlake.getId());
            userItem.put("userId", userId);
            userItem.put("userName", userName);
            userItem.put("handleCount", handleCount);
            userItem.put("avgHandleTime", Math.round(avgHandleTime));
            userItem.put("details", items);
            userItem.put("flowAvgHandleTime", Math.round(totalFlowAvgHandleTime.get() / (CollectionUtils.size(flowItemMap.keySet()))));
            userItems.add(userItem);
        });
        userItems.sort(Comparator.comparing(item -> -item.getLong("handleCount")));

        if (BooleanUtils.isTrue((Boolean) queryParams.get("export"))) {
            return userItems.stream().flatMap(item -> {
                        List<QueryItem> details = (List<QueryItem>) item.get("details");
                        if (CollectionUtils.isEmpty(details)) {
                            return Stream.empty();
                        }
                        return Stream.of(details.toArray(new QueryItem[0]));
                    })
                    .collect(Collectors.toList());
        } else {
            return userItems;
        }
    }

}
