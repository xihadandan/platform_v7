/*
 * @(#)12/8/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state;

import com.wellsoft.pt.biz.state.event.BizItemStateChangedEvent;
import com.wellsoft.pt.biz.state.event.BizNodeStateChangedEvent;
import com.wellsoft.pt.biz.state.event.BizProcessStateChangedEvent;
import org.springframework.core.Ordered;

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
public interface StateManagerStateListener extends Ordered {
    /**
     * 事项状态变更
     *
     * @param event
     */
    void onItemStateChanged(BizItemStateChangedEvent event);

    /**
     * 过程节点状态变更
     *
     * @param event
     */
    void onNodeStateChanged(BizNodeStateChangedEvent event);

    /**
     * 业务流程状态变更
     *
     * @param event
     */
    void onProcessStateChanged(BizProcessStateChangedEvent event);
}
