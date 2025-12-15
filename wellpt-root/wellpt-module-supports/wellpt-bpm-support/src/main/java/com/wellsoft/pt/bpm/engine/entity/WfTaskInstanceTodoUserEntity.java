/*
 * @(#)2021-09-24 V1.0
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
 * Description: 数据库表WF_TASK_INSTANCE_TODO_USER的实体类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-09-24.1	zenghw		2021-09-24		Create
 * </pre>
 * @date 2021-09-24
 */
@Entity
@Table(name = "WF_TASK_INSTANCE_TODO_USER")
@DynamicUpdate
@DynamicInsert
public class WfTaskInstanceTodoUserEntity extends IdEntity {

    private static final long serialVersionUID = 1632476551653L;

    // 任务待办人员ID
    private String todoUserId;
    // 任务待办人员名称
    private String todoUserName;
    // 所在环节实例UUID
    private String taskInstUuid;
    // 任务待办人员职位路径
    private String todoUserJobPath;
    //环节ID
    private String taskId;
    //流程实例uuid
    private String flowInstUuid;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * @return the todoUserId
     */
    public String getTodoUserId() {
        return this.todoUserId;
    }

    /**
     * @param todoUserId
     */
    public void setTodoUserId(String todoUserId) {
        this.todoUserId = todoUserId;
    }

    /**
     * @return the todoUserName
     */
    public String getTodoUserName() {
        return this.todoUserName;
    }

    /**
     * @param todoUserName
     */
    public void setTodoUserName(String todoUserName) {
        this.todoUserName = todoUserName;
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
     * @return the todoUserJobPath
     */
    public String getTodoUserJobPath() {
        return this.todoUserJobPath;
    }

    /**
     * @param todoUserJobPath
     */
    public void setTodoUserJobPath(String todoUserJobPath) {
        this.todoUserJobPath = todoUserJobPath;
    }

}
