/*
 * @(#)2018-11-07 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 数据库表WF_TASK_INSTANCE_TOPPING的实体类
 *
 * @author lst
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018-11-07.1	lst		2018-11-07		Create
 * </pre>
 * @date 2018-11-07
 */
@Entity
@Table(name = "WF_TASK_INSTANCE_TOPPING")
@DynamicUpdate
@DynamicInsert
public class TaskInstanceTopping extends IdEntity {

    private static final long serialVersionUID = 1541591418442L;

    private String userId;

    private String taskInstUuid;

    private Integer isTopping;

    // 低优先级
    private Integer lowPriority;

    /**
     * @return the userId
     */
    public String getUserId() {
        return this.userId;
    }

    /**
     * @param userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the taskInstUuid
     */
    public String getTaskInstUuid() {
        return this.taskInstUuid;
    }

    /**
     * @param taskInstUuid
     */
    public void setTaskInstUuid(String taskInstUuid) {
        this.taskInstUuid = taskInstUuid;
    }

    /**
     * @return the isTopping
     */
    public Integer getIsTopping() {
        return this.isTopping;
    }

    /**
     * @param isTopping
     */
    public void setIsTopping(Integer isTopping) {
        this.isTopping = isTopping;
    }

    /**
     * @return the lowPriority
     */
    public Integer getLowPriority() {
        return lowPriority;
    }

    /**
     * @param lowPriority 要设置的lowPriority
     */
    public void setLowPriority(Integer lowPriority) {
        this.lowPriority = lowPriority;
    }

}
