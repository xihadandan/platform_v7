/*
 * @(#)2012-11-20 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.bpm.engine.enums.TodoTypeOperate;
import com.wellsoft.pt.bpm.engine.enums.ViewFormMode;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;
import java.util.Map;

/**
 * Description: 流程任务参与者表示类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-11-20.1	zhulh		2012-11-20		Create
 * </pre>
 * @date 2012-11-20
 */
@Entity
@Table(name = "wf_task_identity")
@DynamicUpdate
@DynamicInsert
public class TaskIdentity extends IdEntity {

    private static final long serialVersionUID = -5477380165238093169L;
    // 任务实例
    private String taskInstUuid;
    // 办理类型工作待办(1)、会签待办(2)、转办待办(3)、移交待办(4)、委托待办(5)
    private Integer todoType;
    // 挂起状态(0正常、1挂起、2删除)
    private Integer suspensionState = 0;
    // 用户ID
    private String userId;
    // 任务办理所有者ID(工作委托人ID)
    private String ownerId;
    // 按人员顺序依次办理
    private Integer sortOrder;
    // 源待办UUID
    private String sourceTaskIdentityUuid;
    // 相关联的操作记录UUID
    private String relatedTaskOperationUuid;
    // 查看表单方式
    private ViewFormMode viewFormMode;
    // 办理类型操作权限
    private TodoTypeOperate todoTypeOperate;
    // 并行的待办标识[{"suspensionState":suspensionState,"uuid":uuid}]
    private List<Map<String, Object>> parallelTaskIdentities;
    // 跳转的环节ID
    private String gotoTaskId;
    // 用户身份ID
    private String identityId;
    // 用户身份路径
    private String identityIdPath;
    // 逾期状态(0未逾期、1逾期中)
    private Integer overdueState;

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
     * @return the todoType
     */
    public Integer getTodoType() {
        return todoType;
    }

    /**
     * @param todoType 要设置的todoType
     */
    public void setTodoType(Integer todoType) {
        this.todoType = todoType;
    }

    /**
     * @return the suspensionState
     */
    public Integer getSuspensionState() {
        return suspensionState;
    }

    /**
     * @param suspensionState 要设置的suspensionState
     */
    public void setSuspensionState(Integer suspensionState) {
        this.suspensionState = suspensionState;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the ownerId
     */
    public String getOwnerId() {
        return ownerId;
    }

    /**
     * @param ownerId 要设置的ownerId
     */
    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
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

    /**
     * @return the sourceTaskIdentityUuid
     */
    public String getSourceTaskIdentityUuid() {
        return sourceTaskIdentityUuid;
    }

    /**
     * @param sourceTaskIdentityUuid 要设置的sourceTaskIdentityUuid
     */
    public void setSourceTaskIdentityUuid(String sourceTaskIdentityUuid) {
        this.sourceTaskIdentityUuid = sourceTaskIdentityUuid;
    }

    /**
     * @return the relatedTaskOperationUuid
     */
    public String getRelatedTaskOperationUuid() {
        return relatedTaskOperationUuid;
    }

    /**
     * @param relatedTaskOperationUuid 要设置的relatedTaskOperationUuid
     */
    public void setRelatedTaskOperationUuid(String relatedTaskOperationUuid) {
        this.relatedTaskOperationUuid = relatedTaskOperationUuid;
    }

    /**
     * @return the viewFormMode
     */
    public ViewFormMode getViewFormMode() {
        return viewFormMode;
    }

    /**
     * @param viewFormMode 要设置的viewFormMode
     */
    public void setViewFormMode(ViewFormMode viewFormMode) {
        this.viewFormMode = viewFormMode;
    }

    /**
     * @return the todoTypeOperate
     */
    public TodoTypeOperate getTodoTypeOperate() {
        return todoTypeOperate;
    }

    /**
     * @param todoTypeOperate 要设置的todoTypeOperate
     */
    public void setTodoTypeOperate(TodoTypeOperate todoTypeOperate) {
        this.todoTypeOperate = todoTypeOperate;
    }

    /**
     * @return the parallelTaskIdentities
     */
    @Transient
    public List<Map<String, Object>> getParallelTaskIdentities() {
        return parallelTaskIdentities;
    }

    /**
     * @param parallelTaskIdentities 要设置的parallelTaskIdentities
     */
    public void setParallelTaskIdentities(List<Map<String, Object>> parallelTaskIdentities) {
        this.parallelTaskIdentities = parallelTaskIdentities;
    }

    /**
     * @return the gotoTaskId
     */
    @Transient
    public String getGotoTaskId() {
        return gotoTaskId;
    }

    /**
     * @param gotoTaskId 要设置的gotoTaskId
     */
    public void setGotoTaskId(String gotoTaskId) {
        this.gotoTaskId = gotoTaskId;
    }

    /**
     * @return the identityId
     */
    public String getIdentityId() {
        return identityId;
    }

    /**
     * @param identityId 要设置的identityId
     */
    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    /**
     * @return the identityIdPath
     */
    public String getIdentityIdPath() {
        return identityIdPath;
    }

    /**
     * @param identityIdPath 要设置的identityIdPath
     */
    public void setIdentityIdPath(String identityIdPath) {
        this.identityIdPath = identityIdPath;
    }

    /**
     * @return the overdueState
     */
    public Integer getOverdueState() {
        return overdueState;
    }

    /**
     * @param overdueState 要设置的overdueState
     */
    public void setOverdueState(Integer overdueState) {
        this.overdueState = overdueState;
    }
    
}
