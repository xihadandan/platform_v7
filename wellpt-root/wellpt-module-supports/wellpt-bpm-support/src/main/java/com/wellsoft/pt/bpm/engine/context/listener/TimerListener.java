/*
 * @(#)2014-10-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.context.listener;

import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.support.TaskData;

/**
 * Description: 计时监听器
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
public interface TimerListener extends Listener {
    /**
     * 启动与重启计时器前检查处理，如果返回true继续启动，否则停止
     *
     * @param taskTimer
     * @param timerEvent
     * @param taskInstance
     * @param flowInstance
     * @return
     */
    boolean preHandle(TaskTimer taskTimer, String timerEvent, TaskInstance taskInstance, FlowInstance flowInstance);

    /**
     * 启动计时器
     *
     * @param node
     * @param executionContext
     */
    void onTimerStarted(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData);

    /**
     * 暂停计时器
     *
     * @param node
     * @param executionContext
     */
    void onTimerPause(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData);

    /**
     * 计时预警
     *
     * @param node
     * @param executionContext
     */
    void onTimerAlarm(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData);

    /**
     * 计时到期
     *
     * @param node
     * @param executionContext
     */
    void onTimerDue(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData);

    /**
     * 计时逾期
     *
     * @param node
     * @param executionContext
     */
    void onTimerOverDue(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData);

    /**
     * 停止计时器
     *
     * @param node
     * @param executionContext
     */
    void onTimerStopped(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData);

    /**
     * 重启流程环节所在的计时器
     *
     * @param taskId
     * @param flowInstUuid
     */
    void onTimerRestarted(TaskTimer taskTimer, TaskInstance taskInstance, FlowInstance flowInstance, TaskData taskData);
}
