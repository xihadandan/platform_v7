/*
 * @(#)2018年11月16日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.support;

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
 * 2018年11月16日.1	zhulh		2018年11月16日		Create
 * </pre>
 * @date 2018年11月16日
 */
public class TaskLimitTime {
    // 时限是否为指定日期
    private boolean isDateOfLimitTime;

    // 办理数字日期，当isDateOfLimitTime为true时有效
    private Date taskDueTime;

    // 办理时限数字
    private Double taskLimitTime;

    /**
     * @return the isDateOfLimitTime
     */
    public boolean isDateOfLimitTime() {
        return isDateOfLimitTime;
    }

    /**
     * @param isDateOfLimitTime 要设置的isDateOfLimitTime
     */
    public void setDateOfLimitTime(boolean isDateOfLimitTime) {
        this.isDateOfLimitTime = isDateOfLimitTime;
    }

    /**
     * @return the taskDueTime
     */
    public Date getTaskDueTime() {
        return taskDueTime;
    }

    /**
     * @param taskDueTime 要设置的taskDueTime
     */
    public void setTaskDueTime(Date taskDueTime) {
        this.taskDueTime = taskDueTime;
    }

    /**
     * @return the taskLimitTime
     */
    public Double getTaskLimitTime() {
        return taskLimitTime;
    }

    /**
     * @param taskLimitTime 要设置的taskLimitTime
     */
    public void setTaskLimitTime(Double taskLimitTime) {
        this.taskLimitTime = taskLimitTime;
    }

}
