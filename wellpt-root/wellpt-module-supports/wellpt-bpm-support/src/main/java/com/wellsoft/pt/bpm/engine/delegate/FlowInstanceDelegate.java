/*
 * @(#)2012-11-19 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.delegate;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.FlowEngine;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.service.TaskService;
import com.wellsoft.pt.bpm.engine.service.TaskSubFlowDispatchService;
import com.wellsoft.pt.bpm.engine.service.TaskSubFlowService;
import com.wellsoft.pt.bpm.engine.support.NewFlow;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * Description: 流程实例操作委派类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-11-19.1	zhulh		2012-11-19		Create
 * </pre>
 * @date 2012-11-19
 */
public class FlowInstanceDelegate {

    // private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param flowInstance
     * @param taskData
     */
    public void start(FlowInstance flowInstance, TaskData taskData) {
        Token token = new Token(flowInstance, taskData);
        this.start(flowInstance, taskData, token);
    }
    
    /**
     * @param token
     */
    public void start(FlowInstance flowInstance, TaskData taskData, Token token) {
        // 设置任务数据的环节实例UUID
        taskData.setFlowInstUuid(flowInstance.getUuid());
        // 如果启动的流程实例草稿是子流程，则设置父流程实例的任务节点
        if (flowInstance.getParent() != null) {
            // 解决子流程异步调用且存为草稿时流程错误的问题
            TaskInstance parentTask = null;
            String parentTaskInstUuid = taskData.getParentTaskInstUuid(flowInstance.getUuid());
            if (StringUtils.isNotBlank(parentTaskInstUuid)) {
                parentTask = FlowEngine.getInstance().getTaskService().get(parentTaskInstUuid);
            } else {
                TaskSubFlowService taskSubFlowService = ApplicationContextHolder.getBean(TaskSubFlowService.class);
                parentTask = taskSubFlowService.getParentTaskInstance(flowInstance.getParent().getUuid(),
                        flowInstance.getUuid());
            }
            if (parentTask != null) {
                token.setParentTask(parentTask);
            } else {
                TaskService taskService = ApplicationContextHolder.getBean(TaskService.class);
                List<TaskInstance> taskInstances = taskService.getUnfinishedTasks(flowInstance.getParent().getUuid());
                if (!taskInstances.isEmpty()) {
                    token.setParentTask(taskInstances.get(0));
                }
            }
        }
        token.signal();
    }

    /**
     * 发起子流程
     *
     * @param flowInstance
     * @param taskService
     * @param flowService
     * @param subFlowId
     * @param taskData
     * @param monitors
     */
    public String startSubFlowInstance(NewFlow newFlow, FlowInstance parentFlowInstance,
                                       TaskInstance parentTaskInstance, TaskData taskData, List<String> users, String todoId, String todoName,
                                       List<String> monitors) {
        TaskSubFlowDispatchService taskSubFlowDispatchService = ApplicationContextHolder
                .getBean(TaskSubFlowDispatchService.class);
        // 是否同步发起子流程
        Boolean syncDispatch = (Boolean) taskData.get("syncDispatchSubFlow");
        if (BooleanUtils.isTrue(syncDispatch)) {
            return taskSubFlowDispatchService.dispatchSubFlow(newFlow, parentFlowInstance, parentTaskInstance, taskData,
                    users, todoId, todoName, monitors, false);
        } else {
            return taskSubFlowDispatchService.dispatchSubFlow(newFlow, parentFlowInstance, parentTaskInstance, taskData,
                    users, todoId, todoName, monitors, true);
        }
    }

}
