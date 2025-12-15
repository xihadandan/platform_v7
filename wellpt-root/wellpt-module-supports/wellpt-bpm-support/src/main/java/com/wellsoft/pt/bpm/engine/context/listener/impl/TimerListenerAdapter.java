/*
 * @(#)2014-10-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.context.listener.impl;

import com.wellsoft.pt.bpm.engine.context.listener.TimerListener;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.support.TaskData;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-10.1	zhulh		2014-10-10		Create
 * </pre>
 * @date 2014-10-10
 */
public class TimerListenerAdapter implements TimerListener {

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
        return 100;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.TimerListener#preHandle(com.wellsoft.pt.bpm.engine.entity.TaskTimer, java.lang.String, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance)
     */
    @Override
    public boolean preHandle(TaskTimer taskTimer, String timerEvent, TaskInstance taskInstance,
                             FlowInstance flowInstance) {
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.TimerListener#onTimerStarted(com.wellsoft.pt.bpm.engine.entity.TaskTimer, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance)
     */
    @Override
    public void onTimerStarted(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance,
                               TaskData taskData) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.TimerListener#onTimerPause(com.wellsoft.pt.bpm.engine.entity.TaskTimer, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance)
     */
    @Override
    public void onTimerPause(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance,
                             TaskData taskData) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.TimerListener#onTimerAlarm(com.wellsoft.pt.bpm.engine.entity.TaskTimer, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public void onTimerAlarm(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance,
                             TaskData taskData) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.TimerListener#onTimerDue(com.wellsoft.pt.bpm.engine.entity.TaskTimer, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public void onTimerDue(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.TimerListener#onTimerOverDue(com.wellsoft.pt.bpm.engine.entity.TaskTimer, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance, com.wellsoft.pt.bpm.engine.support.TaskData)
     */
    @Override
    public void onTimerOverDue(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance,
                               TaskData taskData) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.TimerListener#onTimerStopped(com.wellsoft.pt.bpm.engine.entity.TaskTimer, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance)
     */
    @Override
    public void onTimerStopped(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance,
                               TaskData taskData) {
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.context.listener.TimerListener#onTimerRestarted(com.wellsoft.pt.bpm.engine.entity.TaskTimer, com.wellsoft.pt.bpm.engine.entity.TaskInstance, com.wellsoft.pt.bpm.engine.entity.FlowInstance)
     */
    @Override
    public void onTimerRestarted(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance,
                                 TaskData taskData) {
    }

}
