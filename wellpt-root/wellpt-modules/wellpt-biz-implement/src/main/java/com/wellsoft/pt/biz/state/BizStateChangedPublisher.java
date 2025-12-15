/*
 * @(#)12/8/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state;

import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.entity.BizProcessNodeInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumBizProcessItemState;
import com.wellsoft.pt.biz.enums.EnumBizProcessNodeState;
import com.wellsoft.pt.biz.enums.EnumBizProcessState;

/**
 * Description: 状态变更发布
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
public interface BizStateChangedPublisher {

    /**
     * 发布事项状态变更
     *
     * @param newState
     * @param oldState
     * @param itemInstanceEntity
     */
    void publishItemStateChanged(EnumBizProcessItemState newState, EnumBizProcessItemState oldState, BizProcessItemInstanceEntity itemInstanceEntity);

    /**
     * 发布过程节点状态变更
     *
     * @param newState
     * @param oldState
     * @param nodeInstanceEntity
     */
    void publishNodeStateChanged(EnumBizProcessNodeState newState, EnumBizProcessNodeState oldState, BizProcessNodeInstanceEntity nodeInstanceEntity);

    /**
     * 发布业务流程状态变更
     *
     * @param newState
     * @param oldState
     * @param processInstanceEntity
     */
    void publishProcessStateChanged(EnumBizProcessState newState, EnumBizProcessState oldState, BizProcessInstanceEntity processInstanceEntity);

}
