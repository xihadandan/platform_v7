/*
 * @(#)2014-10-28 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.parser.activity;

import com.wellsoft.context.base.BaseObject;

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
 * 2014-10-28.1	zhulh		2014-10-28		Create
 * </pre>
 * @date 2014-10-28
 */
public class TaskOperationItem extends BaseObject implements Comparable<TaskOperationItem> {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8972233449270112723L;

    // 操作记录UUID
    private String uuid;

    private String taskId;

    private String taskName;

    private String taskInstUuid;
    // 操作人
    private String operator;
    // 操作人身份ID
    private String operatorIdentityId;
    // 操作代码
    private Integer actionCode;
    // 操作时间
    private Date createTime;
    // extraInfo
    private String extraInfo;

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId 要设置的taskId
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @param taskName 要设置的taskName
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
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
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * @param operator 要设置的operator
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * @return the operatorIdentityId
     */
    public String getOperatorIdentityId() {
        return operatorIdentityId;
    }

    /**
     * @param operatorIdentityId 要设置的operatorIdentityId
     */
    public void setOperatorIdentityId(String operatorIdentityId) {
        this.operatorIdentityId = operatorIdentityId;
    }

    /**
     * @return the actionCode
     */
    public Integer getActionCode() {
        return actionCode;
    }

    /**
     * @param actionCode 要设置的actionCode
     */
    public void setActionCode(Integer actionCode) {
        this.actionCode = actionCode;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime 要设置的createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * @return the extraInfo
     */
    public String getExtraInfo() {
        return extraInfo;
    }

    /**
     * @param extraInfo 要设置的extraInfo
     */
    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(TaskOperationItem o) {
        return this.createTime.compareTo(o.getCreateTime());
    }

}
