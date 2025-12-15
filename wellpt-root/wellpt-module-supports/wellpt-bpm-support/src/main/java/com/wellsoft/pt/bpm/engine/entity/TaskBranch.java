/*
 * @(#)2013-5-15 V1.0
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
 * Description: 任务分支实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-15.1	zhulh		2013-5-15		Create
 * </pre>
 * @date 2013-5-15
 */
@Entity
@Table(name = "wf_task_branch")
@DynamicUpdate
@DynamicInsert
public class TaskBranch extends IdEntity {

    // 运行中
    public static final Integer STATUS_NORMAL = 0;
    // 已结束
    public static final Integer STATUS_COMPLETED = 1;
    // 终止
    public static final Integer STATUS_STOP = 2;
    // 主流程撤销
    public static final Integer STATUS_CANCEL = 3;
    // 退回主流程
    public static final Integer STATUS_ROLLBACK = 4;
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7273038411448921565L;
    // 流程实例UUID
    private String flowInstUuid;
    // 发起并行任务的环节实例UUID
    private String parallelTaskInstUuid;
    // 发起并行任务的环节ID
    private String parallelTaskId;
    // 并行任务的第一个任务UUID
    private String initTaskInstUuid;
    // 当前并行分支所在的环节实例UUID
    private String currentTaskInstUuid;
    // 聚合的环节实例UUID
    private String joinTaskInstUuid;
    // 办理对象ID
    private String todoId;
    // 办理对象名称
    private String todoName;
    // 0、静态分支;1、独立分支;2、主办分支;3、协办分支
    private String branchType;
    // 是否合并
    private Boolean isMerge;
    // 是否共享
    private Boolean isShare;

    // 标记分支是否已经完成
    private Boolean completed;

    // 完成状态 0运行中、1正常结束、2终止、3撤销、4退回主流程
    private Integer completionState;

    // 排序
    private Integer sortOrder;

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
     * @return the parallelTaskInstUuid
     */
    public String getParallelTaskInstUuid() {
        return parallelTaskInstUuid;
    }

    /**
     * @param parallelTaskInstUuid 要设置的parallelTaskInstUuid
     */
    public void setParallelTaskInstUuid(String parallelTaskInstUuid) {
        this.parallelTaskInstUuid = parallelTaskInstUuid;
    }

    /**
     * @return the parallelTaskId
     */
    public String getParallelTaskId() {
        return parallelTaskId;
    }

    /**
     * @param parallelTaskId 要设置的parallelTaskId
     */
    public void setParallelTaskId(String parallelTaskId) {
        this.parallelTaskId = parallelTaskId;
    }

    /**
     * @return the initTaskInstUuid
     */
    public String getInitTaskInstUuid() {
        return initTaskInstUuid;
    }

    /**
     * @param initTaskInstUuid 要设置的initTaskInstUuid
     */
    public void setInitTaskInstUuid(String initTaskInstUuid) {
        this.initTaskInstUuid = initTaskInstUuid;
    }

    /**
     * @return the currentTaskInstUuid
     */
    public String getCurrentTaskInstUuid() {
        return currentTaskInstUuid;
    }

    /**
     * @param currentTaskInstUuid 要设置的currentTaskInstUuid
     */
    public void setCurrentTaskInstUuid(String currentTaskInstUuid) {
        this.currentTaskInstUuid = currentTaskInstUuid;
    }

    /**
     * @return the joinTaskInstUuid
     */
    public String getJoinTaskInstUuid() {
        return joinTaskInstUuid;
    }

    /**
     * @param joinTaskInstUuid 要设置的joinTaskInstUuid
     */
    public void setJoinTaskInstUuid(String joinTaskInstUuid) {
        this.joinTaskInstUuid = joinTaskInstUuid;
    }

    /**
     * @return the todoId
     */
    public String getTodoId() {
        return todoId;
    }

    /**
     * @param todoId 要设置的todoId
     */
    public void setTodoId(String todoId) {
        this.todoId = todoId;
    }

    /**
     * @return the todoName
     */
    public String getTodoName() {
        return todoName;
    }

    /**
     * @param todoName 要设置的todoName
     */
    public void setTodoName(String todoName) {
        this.todoName = todoName;
    }

    /**
     * @return the branchType
     */
    public String getBranchType() {
        return branchType;
    }

    /**
     * @param branchType 要设置的branchType
     */
    public void setBranchType(String branchType) {
        this.branchType = branchType;
    }

    /**
     * @return the isMerge
     */
    public Boolean getIsMerge() {
        return isMerge;
    }

    /**
     * @param isMerge 要设置的isMerge
     */
    public void setIsMerge(Boolean isMerge) {
        this.isMerge = isMerge;
    }

    /**
     * @return the isShare
     */
    public Boolean getIsShare() {
        return isShare;
    }

    /**
     * @param isShare 要设置的isShare
     */
    public void setIsShare(Boolean isShare) {
        this.isShare = isShare;
    }

    /**
     * @return the completed
     */
    public Boolean getCompleted() {
        return completed;
    }

    /**
     * @param completed 要设置的completed
     */
    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    /**
     * @return the completionState
     */
    public Integer getCompletionState() {
        return completionState;
    }

    /**
     * @param completionState 要设置的completionState
     */
    public void setCompletionState(Integer completionState) {
        this.completionState = completionState;
    }

    /**
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

}
