/*
 * @(#)2014-6-27 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.context.listener.impl;

import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.DirectionListener;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-27.1	zhulh		2014-6-27		Create
 * </pre>
 * @date 2014-6-27
 */
public class DirectionListenerAdapter implements DirectionListener {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.Listener#getName()
     */
    @Override
    public String getName() {
        return this.getClass().getCanonicalName();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.Listener#getOrder()
     */
    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.DirectionListener#transition(com.wellsoft.pt.bpm.engine.context.event.Event)
     */
    @Override
    public void transition(Event event) throws WorkFlowException {
    }

}
