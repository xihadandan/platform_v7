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
 * Description: 任务子流程分发信息实体类
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
@Table(name = "wf_task_sub_flow_dispatch")
@DynamicUpdate
@DynamicInsert
public class TaskSubFlowDispatch extends IdEntity {

    // 分发中
    public static final Integer STATUS_NORMAL = 0;
    // 分发成功
    public static final Integer STATUS_COMPLETED = 1;
    // 分发失败
    public static final Integer STATUS_STOP = 2;
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4633380816884148974L;
    // 父任务实例UUID
    private String parentTaskInstUuid;
    // 父流程实例UUID
    private String parentFlowInstUuid;
    // 子流程实例UUID
    private String flowInstUuid;
    // 子流程UUID
    private String taskSubFlowUuid;
    // 子流程ID
    private String newFlowId;
    // 办理人
    private String taskUsers;
    // 记录添加子流程日志
    private Boolean logAddSubflow;
    // 分发状态 0分发中、1分发成功、2分发失败
    private Integer completionState;
    // 分发结果信息
    private String resultMsg;
    private String tenant;
    private String system;

    /**
     * @return the parentTaskInstUuid
     */
    public String getParentTaskInstUuid() {
        return parentTaskInstUuid;
    }

    /**
     * @param parentTaskInstUuid 要设置的parentTaskInstUuid
     */
    public void setParentTaskInstUuid(String parentTaskInstUuid) {
        this.parentTaskInstUuid = parentTaskInstUuid;
    }

    /**
     * @return the parentFlowInstUuid
     */
    public String getParentFlowInstUuid() {
        return parentFlowInstUuid;
    }

    /**
     * @param parentFlowInstUuid 要设置的parentFlowInstUuid
     */
    public void setParentFlowInstUuid(String parentFlowInstUuid) {
        this.parentFlowInstUuid = parentFlowInstUuid;
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
     * @return the taskSubFlowUuid
     */
    public String getTaskSubFlowUuid() {
        return taskSubFlowUuid;
    }

    /**
     * @param taskSubFlowUuid 要设置的taskSubFlowUuid
     */
    public void setTaskSubFlowUuid(String taskSubFlowUuid) {
        this.taskSubFlowUuid = taskSubFlowUuid;
    }

    /**
     * @return the newFlowId
     */
    public String getNewFlowId() {
        return newFlowId;
    }

    /**
     * @param newFlowId 要设置的newFlowId
     */
    public void setNewFlowId(String newFlowId) {
        this.newFlowId = newFlowId;
    }

    /**
     * @return the taskUsers
     */
    public String getTaskUsers() {
        return taskUsers;
    }

    /**
     * @param taskUsers 要设置的taskUsers
     */
    public void setTaskUsers(String taskUsers) {
        this.taskUsers = taskUsers;
    }

    /**
     * @return the logAddSubflow
     */
    public Boolean getLogAddSubflow() {
        return logAddSubflow;
    }

    /**
     * @param logAddSubflow 要设置的logAddSubflow
     */
    public void setLogAddSubflow(Boolean logAddSubflow) {
        this.logAddSubflow = logAddSubflow;
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
     * @return the resultMsg
     */
    public String getResultMsg() {
        return resultMsg;
    }

    /**
     * @param resultMsg 要设置的resultMsg
     */
    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }
}
