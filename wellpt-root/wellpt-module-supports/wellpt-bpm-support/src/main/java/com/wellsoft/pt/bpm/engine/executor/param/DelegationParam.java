/*
 * @(#)2015-4-28 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.param;

import com.wellsoft.pt.bpm.engine.entity.TaskDelegation;
import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.executor.Param;
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
 * 2015-4-28.1	zhulh		2015-4-28		Create
 * </pre>
 * @date 2015-4-28
 */
public class DelegationParam extends Param {

    private TaskDelegation taskDelegation;

    /**
     * @param taskInstance
     * @param taskData
     * @param taskIdentity
     * @param log
     */
    public DelegationParam(TaskDelegation taskDelegation, TaskInstance taskInstance, TaskData taskData,
                           TaskIdentity taskIdentity, boolean log) {
        super(taskInstance, taskData, taskIdentity, log);
        this.taskDelegation = taskDelegation;
    }

    /**
     * @return the taskDelegation
     */
    public TaskDelegation getTaskDelegation() {
        return taskDelegation;
    }

}
