/*
 * @(#)2013-5-24 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer;

import com.wellsoft.context.service.BaseService;
import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.core.Direction;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.TaskData;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-24.1	zhulh		2013-5-24		Create
 * </pre>
 * @date 2013-5-24
 */
public interface TimerExecutor extends BaseService {

    /**
     * 计时
     *
     * @param node
     * @param executionContext
     */
    void timer(Node node, ExecutionContext executionContext);

    /**
     * 暂停计时器
     *
     * @param node
     * @param executionContext
     */
    void pause(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData);

    void pause(TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData, String timerName);

    /**
     * 停止计时器
     *
     * @param node
     * @param executionContext
     */
    void stop(FlowInstance flowInstance);

    /**
     * 根据流向结束计时
     *
     * @param direction
     * @param executionContext
     */
    void stopByDirection(Direction direction, ExecutionContext executionContext);

    /**
     * 根据环节实例停止计时器
     *
     * @param taskInstance
     * @param executionContext
     */
    void stopByTaskInstance(TaskInstance taskInstance, ExecutionContext executionContext);

    /**
     * 根据环节实例以及计时器名称停止计时器
     *
     * @param taskInstance
     * @param executionContext
     * @param timerName
     */
    void stopByTaskInstanceAndTimerName(TaskInstance taskInstance, ExecutionContext executionContext, String timerName);


    /**
     * 重启流程的计时器
     *
     * @param flowInstUuid
     */
    void restart(String flowInstUuid);

    /**
     * 重启环节的计时器
     *
     * @param taskInstUuid
     * @param flowInstUuid
     */
    void restart(String taskInstUuid, String flowInstUuid);

    /**
     * 更改办理时限
     *
     * @param timeLimit
     * @param flowInstUuid
     */
    void changeLimitTime(int timeLimit, String flowInstUuid);

    /**
     * 更改办理时限
     *
     * @param dueTime
     * @param flowInstUuid
     * @return
     */
    void changeDueTime(Date dueTime, String flowInstUuid);

    /**
     * 更改办理时限
     *
     * @param dueTime
     * @param taskInstUuid
     * @param flowInstUuid
     */
    void changeDueTime(Date dueTime, String taskInstUuid, String flowInstUuid);

    /**
     * 更新流程实例的计时状态
     *
     * @param flowInstance
     */
    void update(FlowInstance flowInstance);

    /**
     * 更新流程实例的计时状态
     *
     * @param flowInstUuid
     */
    void update(String flowInstUuid);

    /**
     * 同步环节、流程实例的计时器任务
     *
     * @param taskInstance
     * @param flowInstance
     * @param taskTimer
     */
    void syncTaskFlowData(TaskInstance taskInstance, FlowInstance flowInstance, TaskTimer taskTimer);

    /**
     * 根据环节实例UUID判断环节实例是否在计时器中
     *
     * @param taskInstUuid
     * @return
     */
    boolean isTaskInstanceInTimer(String taskInstUuid);

    /**
     * 判断流程是否有计时器配置
     *
     * @param flowInstUuid
     * @return
     */
    boolean hasTimerConfiguration(String flowInstUuid);

    void stop(FlowInstance flowInstance, String timerName);
}
