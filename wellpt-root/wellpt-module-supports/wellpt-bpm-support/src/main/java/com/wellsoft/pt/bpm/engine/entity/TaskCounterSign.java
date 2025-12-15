/*
 * @(#)2012-11-29 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
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
 * Description: 任务会签实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-29.1	zhulh		2012-11-29		Create
 * </pre>
 * @date 2012-11-29
 */
@Entity
@Table(name = "wf_task_counter_sign")
@DynamicUpdate
@DynamicInsert
public class TaskCounterSign extends IdEntity {

    private static final long serialVersionUID = -1655139912504848474L;

    /**
     * 会签的任务的拥有者
     */
    private String owner;

    /**
     * 参与会签的人
     */
    private String assignee;

    /**
     * 会签的任务实例UUID
     */
    private String taskInstUuid;

    /**
     * 自关联
     */
    @UnCloneable
    private TaskCounterSign parent;

    /**
     * 子结点
     */
    @UnCloneable
    private Set<TaskCounterSign> children;

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
     * @return the assignee
     */
    public String getAssignee() {
        return assignee;
    }

    /**
     * @param assignee 要设置的assignee
     */
    public void setAssignee(String assignee) {
        this.assignee = assignee;
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
    public TaskCounterSign getParent() {
        return parent;
    }

    /**
     * @param parent 要设置的parent
     */
    public void setParent(TaskCounterSign parent) {
        this.parent = parent;
    }

    /**
     * @return the children
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Cascade({CascadeType.ALL})
    public Set<TaskCounterSign> getChildren() {
        return children;
    }

    /**
     * @param children 要设置的children
     */
    public void setChildren(Set<TaskCounterSign> children) {
        this.children = children;
    }

}
