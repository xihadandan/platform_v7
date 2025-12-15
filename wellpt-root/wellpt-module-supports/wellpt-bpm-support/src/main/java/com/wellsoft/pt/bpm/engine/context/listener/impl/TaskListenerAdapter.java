/*
 * @(#)2014-6-27 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.context.listener.impl;

import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.context.listener.TaskListener;
import com.wellsoft.pt.bpm.engine.context.listener.TaskUserIndicate;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.TaskData;

import java.util.Set;

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
public class TaskListenerAdapter implements TaskListener, TaskUserIndicate {

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
     * @see com.wellsoft.pt.bpm.engine.context.listener.TaskListener#onCreated(com.wellsoft.pt.bpm.engine.context.event.Event)
     */
    @Override
    public void onCreated(Event event) throws WorkFlowException {
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
     * @see com.wellsoft.pt.bpm.engine.access.TaskUserIndicate#getCandidateUsers(com.wellsoft.pt.bpm.engine.node.Node, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public Set<String> getCandidateUsers(Node node, TaskData taskData) {
        // TODO Auto-generated method stub
        return null;
    }

}
