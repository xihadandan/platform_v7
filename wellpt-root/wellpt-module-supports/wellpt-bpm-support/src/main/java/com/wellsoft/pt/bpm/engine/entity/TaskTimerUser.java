/*
 * @(#)2013-5-24 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 定时任务相关用户
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-24.1	zhulh		2013-5-24		Create
 * </pre>
 * @date 2013-5-24
 */
@Entity
@Table(name = "wf_task_timer_user")
@DynamicUpdate
@DynamicInsert
public class TaskTimerUser extends IdEntity {

    private static final long serialVersionUID = 4584665509534672496L;

    // 用户类型(责任人、预警提醒人、预警流程办理人、逾期处理人、逾期处理流程办理人)
    private Integer userType;

    // unit用户类型
    private Integer type;
    // unit用户值
    private String value;
    // unit用户参数值
    private String argValue;

    // 任务定时信息
    private String taskTimerUuid;

    /**
     * @return the userType
     */
    public Integer getUserType() {
        return userType;
    }

    /**
     * @param userType 要设置的userType
     */
    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    /**
     * @return the type
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the argValue
     */
    public String getArgValue() {
        return argValue;
    }

    /**
     * @param argValue 要设置的argValue
     */
    public void setArgValue(String argValue) {
        this.argValue = argValue;
    }

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

}
