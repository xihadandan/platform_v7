/*
 * @(#)2021年7月29日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.support;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年7月29日.1	zhulh		2021年7月29日		Create
 * </pre>
 * @date 2021年7月29日
 */
public class TaskLimitTimeInitResult {

    private boolean success;

    private TaskLimitTime taskLimitTime;

    /**
     * @param success
     */
    public TaskLimitTimeInitResult(boolean success) {
        super();
        this.success = success;
    }

    /**
     * @param success
     * @param taskLimitTime
     */
    public TaskLimitTimeInitResult(boolean success, TaskLimitTime taskLimitTime) {
        super();
        this.success = success;
        this.taskLimitTime = taskLimitTime;
    }

    /**
     * @return the success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @param success 要设置的success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     * @return the taskLimitTime
     */
    public TaskLimitTime getTaskLimitTime() {
        return taskLimitTime;
    }

    /**
     * @param taskLimitTime 要设置的taskLimitTime
     */
    public void setTaskLimitTime(TaskLimitTime taskLimitTime) {
        this.taskLimitTime = taskLimitTime;
    }

}
