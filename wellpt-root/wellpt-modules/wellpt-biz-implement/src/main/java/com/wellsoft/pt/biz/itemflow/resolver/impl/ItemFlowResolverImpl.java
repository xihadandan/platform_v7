/*
 * @(#)12/12/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.itemflow.resolver.impl;

import com.wellsoft.pt.biz.enums.EnumItemFlowStartNewItemWay;
import com.wellsoft.pt.biz.enums.EnumProcessItemEventType;
import com.wellsoft.pt.biz.itemflow.resolver.ItemFlowResolver;
import com.wellsoft.pt.biz.listener.event.ProcessItemEvent;
import com.wellsoft.pt.biz.support.ItemFlowDefinition;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/12/23.1	zhulh		12/12/23		Create
 * </pre>
 * @date 12/12/23
 */
@Component
public class ItemFlowResolverImpl implements ItemFlowResolver {

    /**
     * 解析下一事项
     *
     * @param event
     * @param itemFlowDefinition
     */
    @Override
    public List<ItemFlowDefinition.NextItemInfo> resolveNextItem(ProcessItemEvent event, ItemFlowDefinition itemFlowDefinition) {
        String itemId = event.getItemId();
        ItemFlowDefinition.ItemNode itemNode = itemFlowDefinition.getItemNodeByItemId(itemId);

        // 配置的下一事项
        List<ItemFlowDefinition.NextItemInfo> nextItemInfos = itemFlowDefinition.listNextItemInfoByNode(itemNode);
        // 过滤事项
        nextItemInfos = filter(event, nextItemInfos);

        return nextItemInfos;
    }

    private List<ItemFlowDefinition.NextItemInfo> filter(ProcessItemEvent event, List<ItemFlowDefinition.NextItemInfo> nextItemInfos) {
        return nextItemInfos.stream().filter(item -> matchNextItemInfo(event, item)).collect(Collectors.toList());
    }

    private boolean matchNextItemInfo(ProcessItemEvent event, ItemFlowDefinition.NextItemInfo item) {
        ItemFlowDefinition.Edge edge = item.getEdge();
        ItemFlowDefinition.EdgeConfiguration edgeConfiguration = edge.getConfiguration();
        if (edgeConfiguration == null) {
            return false;
        }
        String startWay = edgeConfiguration.getStartWay();
        List<String> listenEvents = edgeConfiguration.getListenEvents();
        // 上一办理事项结束时发起
        if (StringUtils.equals(EnumItemFlowStartNewItemWay.PreItemEnd.getValue(), startWay)) {
            return EnumProcessItemEventType.Completed.getValue().equals(event.getEventType());
        } else if (StringUtils.equals(EnumItemFlowStartNewItemWay.OnPreItemEvent.getValue(), startWay)) {
            // 监听上一办理事项办理事件发生
            return listenEvents.contains(event.getEventType());
        } else if (StringUtils.equals(EnumItemFlowStartNewItemWay.OnCombinedItemEvent.getValue(), startWay)) {
            // 监听组合事项办理事件发生
            return listenEvents.contains(event.getEventType());
        }
        return false;
    }

}
