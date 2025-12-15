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
import com.wellsoft.pt.bpm.engine.context.listener.FlowListener;
import com.wellsoft.pt.bpm.engine.context.listener.InternalListener;
import com.wellsoft.pt.bpm.engine.context.listener.Listener;
import com.wellsoft.pt.bpm.engine.context.listener.impl.FlowListenerAdapter;
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
@Component(BizWorkflowIntegrationFlowListener.BEAN_NAME)
public class BizWorkflowIntegrationFlowListener extends FlowListenerAdapter implements InternalListener {

    public static final String BEAN_NAME = "bizWorkflowIntegrationFlowListener";

    @Autowired
    private BizWorkflowIntegrationService workflowIntegrationService;

    /**
     * (non-Javadoc)
     *
     * @see Listener#getName()
     */
    @Override
    public String getName() {
        return "业务流程_业务集成_工作流_流程监听器";
    }

    /**
     * (non-Javadoc)
     *
     * @param event
     * @see FlowListener#onStarted(Event)
     */
    @Override
    public void onStarted(Event event) throws WorkFlowException {
        try {
            BusinessIntegrationContextHolder.setWorkflowEvent(event);
            workflowIntegrationService.onFlowStarted(event);
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

    /**
     * (non-Javadoc)
     *
     * @param event
     * @see FlowListener#onEnd(Event)
     */
    @Override
    public void onEnd(Event event) throws WorkFlowException {
        // 流程办结时处理
        try {
            BusinessIntegrationContextHolder.setWorkflowEvent(event);
            workflowIntegrationService.onFlowEnd(event);
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
