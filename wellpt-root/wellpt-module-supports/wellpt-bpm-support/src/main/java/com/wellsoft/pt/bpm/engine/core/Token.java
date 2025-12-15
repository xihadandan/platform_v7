/*
 * @(#)2012-11-20 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.core;

import com.wellsoft.pt.bpm.engine.context.ExecutionContext;
import com.wellsoft.pt.bpm.engine.delegate.FlowDelegate;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.SubmitResult;
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
 * 2012-11-20.1	zhulh		2012-11-20		Create
 * </pre>
 * @date 2012-11-20
 */
public class Token {
    private FlowInstance flowInstance;
    private FlowDelegate flowDelegate;
    private TaskInstance task;
    private TaskInstance parentTask;
    private Node node;
    private Transition transition;
    private TaskData taskData;

    public Token() {
        throw new WorkFlowException("Not Support!");
    }

    public Token(TaskData taskData) {
        this.taskData = taskData;
    }

    /**
     * @param flowDelegate
     * @param node
     * @param taskData
     */
    public Token(FlowDelegate flowDelegate, Node node, TaskData taskData) {
        this.flowDelegate = flowDelegate;
        this.taskData = taskData;
        this.node = node;
    }

    /**
     * @param flowInstance
     * @param taskData
     */
    public Token(FlowInstance flowInstance, TaskData taskData) {
        this.flowInstance = flowInstance;
        this.flowDelegate = new FlowDelegate(flowInstance.getFlowDefinition());
        this.node = this.flowDelegate.getStartNode();
        this.taskData = taskData;
        /*add by huanglinchuan 2014.10.22 begin*/
        if (taskData != null) {
            this.taskData.setToken(this);
            taskData.getSubmitResult().setFlowInstUuid(flowInstance.getUuid());
        }
        /*add by huanglinchuan 2014.10.22 end*/
    }

    /**
     * 如何描述该构造方法
     *
     * @param task
     */
    public Token(TaskInstance task, TaskData taskData) {
        this(task.getFlowInstance(), taskData);

        this.task = task;
        this.parentTask = task.getParent();
        this.node = flowDelegate.getTaskNode(task.getId());

        if (taskData != null) {
            SubmitResult submitResult = taskData.getSubmitResult();
            submitResult.setFlowInstUuid(task.getFlowInstance().getUuid());
            submitResult.setFromTaskInstUuid(task.getUuid());
            submitResult.setFromTaskId(task.getId());
        }
    }

    /**
     * @return the flowInstance
     */
    public FlowInstance getFlowInstance() {
        return flowInstance;
    }

    /**
     * @param flowInstance 要设置的flowInstance
     */
    public void setFlowInstance(FlowInstance flowInstance) {
        this.flowInstance = flowInstance;
    }

    /**
     * @return the flowDelegate
     */
    public FlowDelegate getFlowDelegate() {
        return flowDelegate;
    }

    /**
     * @param flowDelegate 要设置的flowDelegate
     */
    public void setFlowDelegate(FlowDelegate flowDelegate) {
        this.flowDelegate = flowDelegate;
    }

    /**
     * @return the task
     */
    public TaskInstance getTask() {
        return task;
    }

    /**
     * @param task 要设置的task
     */
    public void setTask(TaskInstance task) {
        this.task = task;
    }

    /**
     * @return the taskData
     */
    public TaskData getTaskData() {
        return taskData;
    }

    /**
     * @return the parentTask
     */
    public TaskInstance getParentTask() {
        return parentTask;
    }

    /**
     * @param parentTask 要设置的parentTask
     */
    public void setParentTask(TaskInstance parentTask) {
        this.parentTask = parentTask;
    }

    /**
     * @return the node
     */
    public Node getNode() {
        return node;
    }

    /**
     * @param node 要设置的node
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     *
     */
    public void signal() {
        signal(node.getLeavingTransition(), new ExecutionContext(this));
    }

    /**
     * @param trans
     * @param executionContext
     */
    private void signal(Transition trans, ExecutionContext executionContext) {
        // start calculating the next state
        if (node.complete(executionContext)) {
            // 评估这次转流程转换是否可行，计算转向的下一结点
            executionContext.evaluate(trans);
            // 计算下一结点
            if (executionContext.evaluateTransition(trans)) {
                this.transition = trans;
                // 计算结点用户
                executionContext.evaluateTaskUser(trans);
                node.leave(this.transition, executionContext);
            } else {
                // 完成当前任务
                node.getHandler().leave(node, executionContext);
            }
        }
    }

    /**
     * @return the transition
     */
    public Transition getTransition() {
        return transition;
    }

    /**
     * @param transition 要设置的transition
     */
    public void setTransition(Transition transition) {
        this.transition = transition;
    }

}
