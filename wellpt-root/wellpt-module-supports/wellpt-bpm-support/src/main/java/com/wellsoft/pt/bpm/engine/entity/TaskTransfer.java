/*
 * @(#)2013-4-9 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-9.1	zhulh		2013-4-9		Create
 * </pre>
 * @date 2013-4-9
 */
@Entity
@Table(name = "wf_task_transfer")
@DynamicUpdate
@DynamicInsert
public class TaskTransfer extends IdEntity {

    private static final long serialVersionUID = -3800766300164299790L;

    // 转办任务的拥有者
    private String owner;

    // 代办人
    private String agent;

    // 转办任务实例UUID
    private String taskInstUuid;
    /**
     * 自关联
     */
    @UnCloneable
    private TaskTransfer parent;

    /**
     * 子结点
     */
    @UnCloneable
    private Set<TaskTransfer> children;

    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner 要设置的owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return the agent
     */
    public String getAgent() {
        return agent;
    }

    /**
     * @param agent 要设置的agent
     */
    public void setAgent(String agent) {
        this.agent = agent;
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
     * @return the parent
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_uuid")
    public TaskTransfer getParent() {
        return parent;
    }

    /**
     * @param parent 要设置的parent
     */
    public void setParent(TaskTransfer parent) {
        this.parent = parent;
    }

    /**
     * @return the children
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Cascade({CascadeType.ALL})
    public Set<TaskTransfer> getChildren() {
        return children;
    }

    /**
     * @param children 要设置的children
     */
    public void setChildren(Set<TaskTransfer> children) {
        this.children = children;
    }

}
