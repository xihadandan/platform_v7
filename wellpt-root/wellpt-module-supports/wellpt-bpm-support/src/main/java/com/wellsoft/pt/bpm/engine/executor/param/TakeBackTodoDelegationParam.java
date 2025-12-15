/*
 * @(#)2014-10-21 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.param;

import com.wellsoft.pt.bpm.engine.entity.TaskDelegation;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.executor.Param;
import com.wellsoft.pt.bpm.engine.support.TaskData;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-21.1	zhulh		2014-10-21		Create
 * </pre>
 * @date 2014-10-21
 */
public class TakeBackTodoDelegationParam extends Param {
    // 委托人ID，只能是一个用户ID
    private String consignor;

    // 委托数据
    private TaskDelegation taskDelegation;

    // 受托人待办信息
    private List<TaskIdentity> taskIdentities;

    /**
     * @param taskInstance
     * @param taskData
     * @param taskIdentity
     * @param log
     */
    public TakeBackTodoDelegationParam(TaskInstance taskInstance, TaskData taskData, TaskIdentity taskIdentity,
                                       TaskDelegation taskDelegation, boolean log, List<TaskIdentity> taskIdentities) {
        super(taskInstance, taskData, taskIdentity, log);
        this.consignor = taskDelegation.getConsignor();
        this.taskDelegation = taskDelegation;
        this.taskIdentities = taskIdentities;
    }

    /**
     * @return the consignor
     */
    public String getConsignor() {
        return consignor;
    }

    /**
     * @return the taskIdentities
     */
    public List<TaskIdentity> getTaskIdentities() {
        return taskIdentities;
    }

    /**
     * @param taskIdentities 要设置的taskIdentities
     */
    public void setTaskIdentities(List<TaskIdentity> taskIdentities) {
        this.taskIdentities = taskIdentities;
    }

    /**
     * @return the taskDelegation
     */
    public TaskDelegation getTaskDelegation() {
        return taskDelegation;
    }

    /**
     * @param taskDelegation 要设置的taskDelegation
     */
    public void setTaskDelegation(TaskDelegation taskDelegation) {
        this.taskDelegation = taskDelegation;
    }

}
