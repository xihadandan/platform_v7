/*
 * @(#)2014-3-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.context.listener.impl;

import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.InternalTaskListener;
import com.wellsoft.pt.bpm.engine.entity.TaskSubFlowRelation;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.service.TaskSubFlowService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Description: 任务到达时设置子流程的前置流程状态未通过
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-3-10.1	zhulh		2014-3-10		Create
 * </pre>
 * @date 2014-3-10
 */
//@Service
//@Transactional
public class NewFlowInternalTaskListener extends InternalTaskListener {

    @Autowired
    private TaskSubFlowService taskSubFlowService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.TaskListener#onCreated(com.wellsoft.pt.bpm.engine.context.event.Event)
     */
    @Override
    public void onCreated(Event event) throws WorkFlowException {
        if (event.isSubFlowInstce()) {
            String taskId = event.getTaskId();
            String flowInstUuid = event.getFlowInstUuid();
            int submitStatus = TaskSubFlowRelation.STATUS_NO_PASS;
            taskSubFlowService.updateSubFlowRelationStatus(taskId, flowInstUuid, submitStatus);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.TaskListener#onCompleted(com.wellsoft.pt.bpm.engine.context.event.Event)
     */
    @Override
    public void onCompleted(Event event) throws WorkFlowException {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.Listener#getName()
     */
    @Override
    public String getName() {
        return NewFlowInternalTaskListener.class.getName();
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

}
