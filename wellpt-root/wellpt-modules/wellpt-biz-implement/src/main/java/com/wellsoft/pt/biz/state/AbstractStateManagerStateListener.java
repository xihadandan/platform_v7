/*
 * @(#)12/11/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state;

import com.wellsoft.pt.biz.enums.EnumStateTriggerType;
import com.wellsoft.pt.biz.listener.event.Event;
import com.wellsoft.pt.biz.state.event.BizItemStateChangedEvent;
import com.wellsoft.pt.biz.state.event.BizNodeStateChangedEvent;
import com.wellsoft.pt.biz.state.event.BizProcessStateChangedEvent;
import com.wellsoft.pt.biz.state.support.StateConfigMatcher;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/11/23.1	zhulh		12/11/23		Create
 * </pre>
 * @date 12/11/23
 */
public abstract class AbstractStateManagerStateListener implements StateManagerStateListener {

    /**
     * 事项状态变更
     *
     * @param event
     */
    @Override
    public void onItemStateChanged(BizItemStateChangedEvent event) {
        doStateChanged(event, EnumStateTriggerType.ITEM_STATE_CHANGED, getItemStateChangedMatcher(event));
    }

    /**
     * 过程节点状态变更
     *
     * @param event
     */
    @Override
    public void onNodeStateChanged(BizNodeStateChangedEvent event) {
        doStateChanged(event, EnumStateTriggerType.PROCESS_NODE_STATE_CHANGED, getNodeStateChangedMatcher(event));
    }

    /**
     * 业务流程状态变更
     *
     * @param event
     */
    @Override
    public void onProcessStateChanged(BizProcessStateChangedEvent event) {
        doStateChanged(event, EnumStateTriggerType.PROCESS_STATE_CHANGED, getProcessStateChangedMatcher(event));
    }

    /**
     * @param event
     * @param stateTriggerType
     * @param configMatcher
     */
    abstract protected void doStateChanged(Event event, EnumStateTriggerType stateTriggerType, StateConfigMatcher configMatcher);

    /**
     * @param event
     * @return
     */
    protected StateConfigMatcher getItemStateChangedMatcher(BizItemStateChangedEvent event) {
        return config -> StringUtils.equals(event.getItemCode(), config.getItemCode()) &&
                StringUtils.equals(event.getNewState(), config.getItemState());
    }

    /**
     * @param event
     * @return
     */
    protected StateConfigMatcher getNodeStateChangedMatcher(BizNodeStateChangedEvent event) {
        return config -> StringUtils.equals(event.getProcessNodeCode(), config.getProcessNodeCode()) &&
                StringUtils.equals(event.getNewState(), config.getProcessNodeState());
    }

    /**
     * @param event
     * @return
     */
    protected StateConfigMatcher getProcessStateChangedMatcher(BizProcessStateChangedEvent event) {
        return config -> StringUtils.equals(event.getNewState(), config.getProcessState());
    }

}
