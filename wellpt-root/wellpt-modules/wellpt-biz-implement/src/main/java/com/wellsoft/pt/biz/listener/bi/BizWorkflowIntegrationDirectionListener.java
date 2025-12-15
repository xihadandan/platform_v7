/*
 * @(#)11/17/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.listener.bi;

import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.pt.biz.facade.service.BizWorkflowIntegrationService;
import com.wellsoft.pt.biz.utils.BusinessIntegrationContextHolder;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.DirectionListener;
import com.wellsoft.pt.bpm.engine.context.listener.InternalListener;
import com.wellsoft.pt.bpm.engine.context.listener.Listener;
import com.wellsoft.pt.bpm.engine.context.listener.impl.DirectionListenerAdapter;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
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
 * 11/17/22.1	zhulh		11/17/22		Create
 * </pre>
 * @date 11/17/22
 */
@Component(BizWorkflowIntegrationDirectionListener.BEAN_NAME)
public class BizWorkflowIntegrationDirectionListener extends DirectionListenerAdapter implements InternalListener {

    public static final String BEAN_NAME = "bizWorkflowIntegrationDirectionListener";

    @Autowired
    private BizWorkflowIntegrationService workflowIntegrationService;

    /**
     * (non-Javadoc)
     *
     * @see Listener#getName()
     */
    @Override
    public String getName() {
        return "业务流程_业务集成_工作流_流向监听器";
    }

    /**
     * (non-Javadoc)
     *
     * @param event
     * @see DirectionListener#transition(Event)
     */
    @Override
    public void transition(Event event) throws WorkFlowException {
        try {
            BusinessIntegrationContextHolder.setWorkflowEvent(event);
            workflowIntegrationService.onDirectionTransition(event);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                throw new WorkFlowException(e.getMessage(), e.getCause());
            } else {
                throw e;
            }
        } finally {
            BusinessIntegrationContextHolder.removeWorkflowEvent();
        }
    }

}
