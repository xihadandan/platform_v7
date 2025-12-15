/*
 * @(#)12/8/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state.event;

import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumProcessItemEventType;
import com.wellsoft.pt.biz.listener.event.ProcessItemEvent;

/**
 * Description: 业务事项状态变更事件
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
public class BizItemStateChangedEvent extends ProcessItemEvent {

    private static final long serialVersionUID = 7155260854126045560L;

    private String newState;
    private String oldState;
    private BizProcessItemInstanceEntity itemInstanceEntity;

    /**
     * @param newState
     * @param oldState
     * @param itemInstanceEntity
     */
    public BizItemStateChangedEvent(String newState, String oldState, BizProcessItemInstanceEntity itemInstanceEntity) {
        super(itemInstanceEntity, EnumProcessItemEventType.Updated);

        this.newState = newState;
        this.oldState = oldState;
        this.itemInstanceEntity = itemInstanceEntity;
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
     * @return the itemInstanceEntity
     */
    public BizProcessItemInstanceEntity getItemInstanceEntity() {
        return itemInstanceEntity;
    }

}
