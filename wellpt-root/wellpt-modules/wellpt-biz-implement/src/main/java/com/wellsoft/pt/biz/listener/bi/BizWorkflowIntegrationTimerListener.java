/*
 * @(#)11/18/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.listener.bi;

import com.wellsoft.pt.biz.facade.service.BizWorkflowIntegrationService;
import com.wellsoft.pt.bpm.engine.context.listener.InternalListener;
import com.wellsoft.pt.bpm.engine.context.listener.Listener;
import com.wellsoft.pt.bpm.engine.context.listener.TimerListener;
import com.wellsoft.pt.bpm.engine.context.listener.impl.TimerListenerAdapter;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.support.TaskData;
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
@Component(BizWorkflowIntegrationTimerListener.BEAN_NAME)
public class BizWorkflowIntegrationTimerListener extends TimerListenerAdapter implements InternalListener {

    public static final String BEAN_NAME = "bizWorkflowIntegrationTimerListener";

    @Autowired
    private BizWorkflowIntegrationService workflowIntegrationService;

    /**
     * (non-Javadoc)
     *
     * @see Listener#getName()
     */
    @Override
    public String getName() {
        return "业务流程_业务集成_工作流_计时监听器";
    }

    /**
     * (non-Javadoc)
     *
     * @see Listener#getOrder()
     */
    @Override
    public int getOrder() {
        return super.getOrder();
    }

    /**
     * (non-Javadoc)
     *
     * @param taskTimer
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     * @see TimerListener#onTimerStarted(TaskTimer, TaskInstance, FlowInstance)
     */
    @Override
    public void onTimerStarted(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData) {
        workflowIntegrationService.syncTimerInfoIfRequired(flowInstance.getUuid(), taskTimer, taskData);
    }

    /**
     * (non-Javadoc)
     *
     * @param taskTimer
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     * @see TimerListener#onTimerPause(TaskTimer, TaskInstance, FlowInstance)
     */
    @Override
    public void onTimerPause(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData) {
        workflowIntegrationService.syncTimerInfoIfRequired(flowInstance.getUuid(), taskTimer, taskData);
    }

    /**
     * (non-Javadoc)
     *
     * @param taskTimer
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     * @see TimerListener#onTimerAlarm(TaskTimer, TaskInstance, FlowInstance, TaskData)
     */
    @Override
    public void onTimerAlarm(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData) {
        workflowIntegrationService.syncTimerInfoIfRequired(flowInstance.getUuid(), taskTimer, taskData);
    }

    /**
     * (non-Javadoc)
     *
     * @param taskTimer
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     * @see TimerListener#onTimerDue(TaskTimer, TaskInstance, FlowInstance, TaskData)
     */
    @Override
    public void onTimerDue(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData) {
        workflowIntegrationService.syncTimerInfoIfRequired(flowInstance.getUuid(), taskTimer, taskData);
    }

    /**
     * (non-Javadoc)
     *
     * @param taskTimer
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     * @see TimerListener#onTimerOverDue(TaskTimer, TaskInstance, FlowInstance, TaskData)
     */
    @Override
    public void onTimerOverDue(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData) {
        workflowIntegrationService.syncTimerInfoIfRequired(flowInstance.getUuid(), taskTimer, taskData);
    }

    /**
     * (non-Javadoc)
     *
     * @param taskTimer
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     * @see TimerListener#onTimerStopped(TaskTimer, TaskInstance, FlowInstance)
     */
    @Override
    public void onTimerStopped(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData) {
        workflowIntegrationService.syncTimerInfoIfRequired(flowInstance.getUuid(), taskTimer, taskData);
    }

    /**
     * (non-Javadoc)
     *
     * @param taskTimer
     * @param taskInstance
     * @param flowInstance
     * @param taskData
     * @see TimerListener#onTimerRestarted(TaskTimer, TaskInstance, FlowInstance)
     */
    @Override
    public void onTimerRestarted(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData) {
        workflowIntegrationService.syncTimerInfoIfRequired(flowInstance.getUuid(), taskTimer, taskData);
    }

}
