/*
 * @(#)2015年7月23日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.gz.listener;

import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.impl.FlowListenerAdapter;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.workflow.gz.facade.GzWorkDataFlowListenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年7月23日.1	zhulh		2015年7月23日		Create
 * </pre>
 * @date 2015年7月23日
 */
@Service
@Transactional
public class GzWorkDataFlowListener extends FlowListenerAdapter {

    @Autowired
    private GzWorkDataFlowListenerService gzWorkDataFlowListenerService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.FlowListenerAdapter#getName()
     */
    @Override
    public String getName() {
        return "挂职流程运行时流程事件监听器";
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.FlowListenerAdapter#getOrder()
     */
    @Override
    public int getOrder() {
        return super.getOrder();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.FlowListenerAdapter#onCreated(com.wellsoft.pt.bpm.engine.context.event.Event)
     */
    @Override
    public void onCreated(Event event) throws WorkFlowException {
        super.onCreated(event);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.FlowListenerAdapter#onStarted(com.wellsoft.pt.bpm.engine.context.event.Event)
     */
    @Override
    public void onStarted(Event event) throws WorkFlowException {
        super.onStarted(event);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.FlowListenerAdapter#onEnd(com.wellsoft.pt.bpm.engine.context.event.Event)
     */
    @Override
    public void onEnd(Event event) throws WorkFlowException {
        gzWorkDataFlowListenerService.addCompletedSyncData(event.getFlowInstUuid());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.impl.FlowListenerAdapter#onDeleted(com.wellsoft.pt.bpm.engine.context.event.Event)
     */
    @Override
    public void onDeleted(Event event) throws WorkFlowException {
        gzWorkDataFlowListenerService.addCompletedSyncData(event.getFlowInstUuid());
    }

}
