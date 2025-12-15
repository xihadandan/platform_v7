/*
 * @(#)2013-5-27 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.service.impl;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.context.listener.TimerListener;
import com.wellsoft.pt.bpm.engine.entity.*;
import com.wellsoft.pt.bpm.engine.service.*;
import com.wellsoft.pt.bpm.engine.timer.TimerExecutor;
import com.wellsoft.pt.bpm.engine.timer.service.AbstractTaskDueService;
import com.wellsoft.pt.bpm.engine.timer.service.TaskDueHanlderService;
import com.wellsoft.pt.bpm.engine.timer.support.TimingState;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 流程任务逾期时间到达处理服务类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-27.1	zhulh		2013-5-27		Create
 * </pre>
 * @date 2013-5-27
 */
@Service
@Transactional
public class TaskDueHanlderServiceImpl extends AbstractTaskDueService implements TaskDueHanlderService {

    @Autowired
    private FlowService flowService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskTimerService taskTimerService;

    @Autowired
    private TimerExecutor timerExecutor;

    @Autowired(required = false)
    private Map<String, TimerListener> listenerMap;

    @Autowired
    private TaskTimerLogService taskTimerLogService;

    @Autowired
    private WfTaskTodoTempService taskTodoTempService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.TaskDueHanlderService#markOverDueInfo(java.lang.String)
     */
    @Override
    public boolean markDueInfo(String taskTimerUuid) {
        TaskTimer taskTimer = taskTimerService.get(taskTimerUuid);
        // 是否有效的计时器
        boolean isValid = isValidTaskTimer(taskTimerUuid, taskTimer);

        if (isValid) {
            // 环节实例
            TaskInstance taskInstance = taskService.get(taskTimer.getTaskInstUuid());
            Integer taskInstRecVer = taskInstance.getRecVer();
            List<WfTaskTodoTempEntity> taskTodoTempEntities = taskTodoTempService.listByTaskInstUuidAndTaskInstRecVer(taskInstance.getUuid(), taskInstRecVer);
            // 流程实例
            FlowInstance flowInstance = flowService.getFlowInstanceByTaskInstUuid(taskTimer.getTaskInstUuid());
            // 更新计时器状态
            taskTimer.setTimingState(TimingState.DUE);
            taskTimerService.save(taskTimer);
            // 同步环节、流程数据
            timerExecutor.syncTaskFlowData(taskInstance, flowInstance, taskTimer);
            // 记录日志
            taskTimerLogService.log(taskTimerUuid, TaskTimerLog.TYPE_DUE_DOING, "计时器到期！");
            // 更新用户待办临时表
            if (CollectionUtils.isNotEmpty(taskTodoTempEntities)) {
                taskTimerLogService.flushSession();
                taskInstance = taskService.get(taskTimer.getTaskInstUuid());
                if (!taskInstRecVer.equals(taskInstance.getRecVer())) {
                    taskTodoTempService.updateTaskTodoTemp(taskTodoTempEntities, taskInstance.getRecVer());
                }
            }
        } else {
            // 记录日志
            taskTimerLogService.log(taskTimerUuid, TaskTimerLog.TYPE_INFO, "标记计时器信息返回false，不进行到期处理！");
        }

        return isValid;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.AbstractTaskTimerService#handler(java.lang.String)
     */
    @Override
    public void handler(String taskTimerUuid) {
        // 计时器
        TaskTimer taskTimer = taskTimerService.get(taskTimerUuid);
        // 发布到期事件
        String listener = taskTimer.getListener();
        if (StringUtils.isNotBlank(listener)) {
            // 环节实例
            TaskInstance taskInstance = taskService.getTask(taskTimer.getTaskInstUuid());
            // 流程实例
            FlowInstance flowInstance = taskInstance.getFlowInstance();
            String[] listeners = StringUtils.split(listener, Separator.SEMICOLON.getValue());
            for (String l : listeners) {
                TimerListener timerListener = listenerMap.get(l);
                if (timerListener == null) {
                    continue;
                }
                timerListener.onTimerDue(taskTimer, taskInstance, flowInstance, null);
            }
        }
    }

}
