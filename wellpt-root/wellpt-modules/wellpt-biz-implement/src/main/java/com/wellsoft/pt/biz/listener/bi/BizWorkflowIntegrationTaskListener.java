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
import com.wellsoft.pt.bpm.engine.context.listener.InternalListener;
import com.wellsoft.pt.bpm.engine.context.listener.Listener;
import com.wellsoft.pt.bpm.engine.context.listener.TaskListener;
import com.wellsoft.pt.bpm.engine.context.listener.impl.TaskListenerAdapter;
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
@Component(BizWorkflowIntegrationTaskListener.BEAN_NAME)
public class BizWorkflowIntegrationTaskListener extends TaskListenerAdapter implements InternalListener {

    public static final String BEAN_NAME = "bizWorkflowIntegrationTaskListener";

    @Autowired
    private BizWorkflowIntegrationService workflowIntegrationService;

    /**
     * (non-Javadoc)
     *
     * @see Listener#getName()
     */
    @Override
    public String getName() {
        return "业务流程_业务集成_工作流_环节监听器";
    }

    /**
     * (non-Javadoc)
     *
     * @param event
     * @see TaskListener#onCreated(Event)
     */
    @Override
    public void onCreated(Event event) throws WorkFlowException {
        // 环节创建处理
        try {
            BusinessIntegrationContextHolder.setWorkflowEvent(event);
            workflowIntegrationService.onTaskCreated(event);
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
     * @see TaskListener#onCompleted(Event)
     */
    @Override
    public void onCompleted(Event event) throws WorkFlowException {
        // 环节完成处理
        try {
            BusinessIntegrationContextHolder.setWorkflowEvent(event);
            workflowIntegrationService.onTaskCompleted(event);
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
