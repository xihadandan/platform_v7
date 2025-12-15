/*
 * @(#)11/18/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.listener;

import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.DirectionListener;
import com.wellsoft.pt.bpm.engine.context.listener.Listener;
import com.wellsoft.pt.bpm.engine.context.listener.impl.DirectionListenerAdapter;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.workflow.facade.service.WfFlowBusinessFacadeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 11/18/22.1	zhulh		11/18/22		Create
 * </pre>
 * @date 11/18/22
 */
@Component(WfFlowBusinessDirectionListener.BEAN_NAME)
public class WfFlowBusinessDirectionListener extends DirectionListenerAdapter {

    public static final String BEAN_NAME = "wfFlowBusinessDirectionListener";

    @Autowired
    private WfFlowBusinessFacadeService flowBusinessFacadeService;

    /**
     * (non-Javadoc)
     *
     * @see Listener#getName()
     */
    @Override
    public String getName() {
        return "流程业务_工作流_流向监听器";
    }

    /**
     * (non-Javadoc)
     *
     * @param event
     * @see DirectionListener#transition(Event)
     */
    @Override
    public void transition(Event event) throws WorkFlowException {
        flowBusinessFacadeService.onDirectionTransition(event);
    }
}
