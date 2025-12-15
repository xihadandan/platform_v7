/*
 * @(#)2014-10-2 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor;

import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import org.apache.commons.lang.StringUtils;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-2.1	zhulh		2014-10-2		Create
 * </pre>
 * @date 2014-10-2
 */
public class Param {

    private TaskInstance taskInstance;

    private TaskData taskData;

    private TaskIdentity taskIdentity;

    private boolean log = true;

    /**
     * 如何描述该构造方法
     */
    public Param() {
        super();
    }

    /**
     * @param taskInstance
     * @param taskData
     */
    public Param(TaskInstance taskInstance, TaskData taskData) {
        super();
        this.taskInstance = taskInstance;
        this.taskData = taskData;
    }

    /**
     * @param taskInstance
     * @param taskData
     */
    public Param(TaskInstance taskInstance, TaskData taskData, TaskIdentity taskIdentity) {
        super();
        this.taskInstance = taskInstance;
        this.taskData = taskData;
        this.taskIdentity = taskIdentity;
        setTaskIdentityUuidIfRequired();
    }

    /**
     * @param taskInstance
     * @param taskData
     * @param taskIdentity
     * @param log
     */
    public Param(TaskInstance taskInstance, TaskData taskData, TaskIdentity taskIdentity, boolean log) {
        super();
        this.taskInstance = taskInstance;
        this.taskData = taskData;
        this.taskIdentity = taskIdentity;
        this.log = log;
        setTaskIdentityUuidIfRequired();
    }

    /**
     *
     */
    private void setTaskIdentityUuidIfRequired() {
        if (taskIdentity == null) {
            return;
        }
        String newTaskIdentityUuid = taskIdentity.getUuid();
        String key = taskInstance.getUuid() + taskData.getUserId();
        String taskIdentityUuid = taskData.getTaskIdentityUuid(key);
        if (StringUtils.isBlank(taskIdentityUuid) || !StringUtils.equals(taskIdentityUuid, newTaskIdentityUuid)) {
            taskData.setTaskIdentityUuid(newTaskIdentityUuid, taskIdentityUuid);
            taskData.setTaskIdentityUuid(key, newTaskIdentityUuid);
        }
    }

    /**
     * @return the taskInstance
     */
    public TaskInstance getTaskInstance() {
        return taskInstance;
    }

    /**
     * @param taskInstance 要设置的taskInstance
     */
    public void setTaskInstance(TaskInstance taskInstance) {
        this.taskInstance = taskInstance;
    }

    /**
     * @return the taskData
     */
    public TaskData getTaskData() {
        return taskData;
    }

    /**
     * @param taskData 要设置的taskData
     */
    public void setTaskData(TaskData taskData) {
        this.taskData = taskData;
    }

    /**
     * @return the taskIdentity
     */
    public TaskIdentity getTaskIdentity() {
        return taskIdentity;
    }

    /**
     * @param taskIdentity 要设置的taskIdentity
     */
    public void setTaskIdentity(TaskIdentity taskIdentity) {
        this.taskIdentity = taskIdentity;
    }

    /**
     * @return the log
     */
    public boolean isLog() {
        return log;
    }

    /**
     * @param log 要设置的log
     */
    public void setLog(boolean log) {
        this.log = log;
    }

}
