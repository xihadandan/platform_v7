/*
 * @(#)12/11/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state.event;

import com.wellsoft.pt.biz.entity.BizProcessNodeInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumProcessNodeEventType;
import com.wellsoft.pt.biz.listener.event.ProcessNodeEvent;

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
public class BizNodeStateChangedEvent extends ProcessNodeEvent {
    private String newState;
    private String oldState;
    private BizProcessNodeInstanceEntity nodeInstanceEntity;

    /**
     * @param newState
     * @param oldState
     * @param nodeInstanceEntity
     */
    public BizNodeStateChangedEvent(String newState, String oldState, BizProcessNodeInstanceEntity nodeInstanceEntity) {
        super(nodeInstanceEntity, EnumProcessNodeEventType.Updated);

        this.newState = newState;
        this.oldState = oldState;
        this.nodeInstanceEntity = nodeInstanceEntity;
    }

    /**
     * @return the newState
     */
    public String getNewState() {
        return newState;
    }

    /**
     * @return the oldState
     */
    public String getOldState() {
        return oldState;
    }

    /**
     * @return the nodeInstanceEntity
     */
    public BizProcessNodeInstanceEntity getNodeInstanceEntity() {
        return nodeInstanceEntity;
    }

}
