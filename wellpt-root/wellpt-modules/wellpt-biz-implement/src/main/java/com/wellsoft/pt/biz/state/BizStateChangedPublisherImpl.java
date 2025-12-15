/*
 * @(#)12/8/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.entity.BizProcessNodeInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumBizProcessItemState;
import com.wellsoft.pt.biz.enums.EnumBizProcessNodeState;
import com.wellsoft.pt.biz.enums.EnumBizProcessState;
import com.wellsoft.pt.biz.state.event.BizItemStateChangedEvent;
import com.wellsoft.pt.biz.state.event.BizNodeStateChangedEvent;
import com.wellsoft.pt.biz.state.event.BizProcessStateChangedEvent;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/8/23.1	zhulh		12/8/23		Create
 * </pre>
 * @date 12/8/23
 */
@Component
public class BizStateChangedPublisherImpl implements BizStateChangedPublisher {

    @Autowired
    private List<StateManagerStateListener> stateManagerStateListeners;

    /**
     * 发布事项状态变更
     *
     * @param newState
     * @param oldState
     * @param itemInstanceEntity
     */
    @Override
    public void publishItemStateChanged(EnumBizProcessItemState newState, EnumBizProcessItemState oldState, BizProcessItemInstanceEntity itemInstanceEntity) {
        String newStateValue = newState.getValue();
        String oldStateValue = oldState != null ? oldState.getValue() : StringUtils.EMPTY;

        BizItemStateChangedEvent event = new BizItemStateChangedEvent(newStateValue, oldStateValue, itemInstanceEntity);
        // 状态管理监听处理
        stateManagerStateListeners.stream().forEach(listener -> listener.onItemStateChanged(event));

        // 发布状态变更事件
        ApplicationContextHolder.getApplicationContext().publishEvent(event);
    }

    /**
     * 发布过程节点状态变更
     *
     * @param newState
     * @param oldState
     * @param nodeInstanceEntity
     */
    @Override
    public void publishNodeStateChanged(EnumBizProcessNodeState newState, EnumBizProcessNodeState oldState, BizProcessNodeInstanceEntity nodeInstanceEntity) {
        String newStateValue = newState.getValue();
        String oldStateValue = oldState != null ? oldState.getValue() : StringUtils.EMPTY;

        BizNodeStateChangedEvent event = new BizNodeStateChangedEvent(newStateValue, oldStateValue, nodeInstanceEntity);
        // 状态管理监听处理
        stateManagerStateListeners.stream().forEach(listener -> listener.onNodeStateChanged(event));

        // 发布状态变更事件
        ApplicationContextHolder.getApplicationContext().publishEvent(event);
    }

    /**
     * 发布业务流程状态变更
     *
     * @param newState
     * @param oldState
     * @param processInstanceEntity
     */
    @Override
    public void publishProcessStateChanged(EnumBizProcessState newState, EnumBizProcessState oldState, BizProcessInstanceEntity processInstanceEntity) {
        String newStateValue = newState.getValue();
        String oldStateValue = oldState != null ? oldState.getValue() : StringUtils.EMPTY;

        BizProcessStateChangedEvent event = new BizProcessStateChangedEvent(newStateValue, oldStateValue, processInstanceEntity);
        // 状态管理监听处理
        stateManagerStateListeners.stream().forEach(listener -> listener.onProcessStateChanged(event));

        // 发布状态变更事件
        ApplicationContextHolder.getApplicationContext().publishEvent(event);
    }

}
