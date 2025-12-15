/*
 * @(#)11/17/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service;

import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.workflow.support.event.FlowBusinessStateChangedEvent;

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
public interface BizWorkflowIntegrationService extends Facade {

    /**
     * 流程启动时处理
     *
     * @param event
     */
    void onFlowStarted(Event event);

    /**
     * 流程办结时处理
     *
     * @param event
     */
    void onFlowEnd(Event event);

    /**
     * 环节创建时处理
     *
     * @param event
     */
    void onTaskCreated(Event event);

    /**
     * 环节完成时处理
     *
     * @param event
     */
    void onTaskCompleted(Event event);

    /**
     * 流向流转
     *
     * @param event
     */
    void onDirectionTransition(Event event);

    /**
     * 同步计时信息
     *
     * @param flowInstUuid
     * @param taskTimer
     */
    void syncTimerInfoIfRequired(String flowInstUuid, TaskTimer taskTimer, TaskData taskData);

    /**
     * 流程业务状态变更
     *
     * @param event
     */
    void onFlowBusinessStateChanged(FlowBusinessStateChangedEvent event);
}
