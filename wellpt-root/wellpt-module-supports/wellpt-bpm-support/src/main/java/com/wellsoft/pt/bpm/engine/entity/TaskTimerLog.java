/*
 * @(#)2013-11-6 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
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
 * 2013-11-6.1	zhulh		2013-11-6		Create
 * </pre>
 * @date 2013-11-6
 */
@Entity
@Table(name = "wf_task_timer_log")
@DynamicUpdate
@DynamicInsert
public class TaskTimerLog extends IdEntity {

    public static final String TYPE_START = "START";
    public static final String TYPE_PAUSE = "PAUSE";
    public static final String TYPE_RESUME = "RESUME";
    public static final String TYPE_END = "END";
    public static final String TYPE_ALARM = "ALARM";
    public static final String TYPE_DUE_DOING = "DUE_DOING";
    public static final String TYPE_OVER_DUE = "OVER_DUE";
    public static final String TYPE_FORCE_STOP_ALARM = "FORCE_STOP_ALARM";
    public static final String TYPE_FORCE_STOP_DUE_DOING = "FORCE_STOP_DUE_DOING";
    public static final String TYPE_FORCE_STOP_OVER_DUE_DOING = "FORCE_STOP_OVER_DUE_DOING";
    public static final String TYPE_INFO = "INFO";
    public static final String TYPE_ERROR = "ERROR";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2426667201085160997L;
    // 计时器UUID
    private String taskTimerUuid;

    // 环节实例UUID
    private String taskInstUuid;

    // 流程实例UUID
    private String flowInstUuid;

    // 记录的时间
    private Date logTime;

    // 记录类型
    private String type;

    // 记录备注
    private String remark;

    /**
     * @return the taskTimerUuid
     */
    public String getTaskTimerUuid() {
        return taskTimerUuid;
    }

    /**
     * @param taskTimerUuid 要设置的taskTimerUuid
     */
    public void setTaskTimerUuid(String taskTimerUuid) {
        this.taskTimerUuid = taskTimerUuid;
    }

    /**
     * @return the taskInstUuid
     */
    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    /**
     * @param taskInstUuid 要设置的taskInstUuid
     */
    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    /**
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * @param flowInstUuid 要设置的flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * @return the logTime
     */
    public Date getLogTime() {
        return logTime;
    }

    /**
     * @param logTime 要设置的logTime
     */
    public void setLogTime(Date logTime) {
        this.logTime = logTime;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

}
