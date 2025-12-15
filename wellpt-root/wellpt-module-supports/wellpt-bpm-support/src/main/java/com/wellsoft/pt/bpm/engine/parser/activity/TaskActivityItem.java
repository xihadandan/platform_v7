/*
 * @(#)2014-10-28 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.parser.activity;

import com.wellsoft.context.util.date.DateUtils;

import java.util.*;

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
public class TaskActivityItem implements Comparable<TaskActivityItem> {
    private String taskId;

    private String taskName;

    private String taskInstUuid;

    private String preTaskId;

    private String preTaskInstUuid;

    private String preGatewayIds;

    private Integer transferCode;

    private String flowInstUuid;

    // 是否并行任务
    private Boolean isParallel;

    // 发起并行任务的任务UUID
    private String parallelTaskInstUuid;

    private String creator;

    private Date createTime;

    private Date endTime;

    private List<TaskOperationItem> operationItems;

    private TaskOperationStack operationStack;

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
     * @return the preTaskId
     */
    public String getPreTaskId() {
        return preTaskId;
    }

    /**
     * @param preTaskId 要设置的preTaskId
     */
    public void setPreTaskId(String preTaskId) {
        this.preTaskId = preTaskId;
    }

    /**
     * @return the preTaskInstUuid
     */
    public String getPreTaskInstUuid() {
        return preTaskInstUuid;
    }

    /**
     * @param preTaskInstUuid 要设置的preTaskInstUuid
     */
    public void setPreTaskInstUuid(String preTaskInstUuid) {
        this.preTaskInstUuid = preTaskInstUuid;
    }

    /**
     * @return the preGatewayIds
     */
    public String getPreGatewayIds() {
        return preGatewayIds;
    }

    /**
     * @param preGatewayIds 要设置的preGatewayIds
     */
    public void setPreGatewayIds(String preGatewayIds) {
        this.preGatewayIds = preGatewayIds;
    }

    /**
     * @return the transferCode
     */
    public Integer getTransferCode() {
        return transferCode;
    }

    /**
     * @param transferCode 要设置的transferCode
     */
    public void setTransferCode(Integer transferCode) {
        this.transferCode = transferCode;
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
     * @return the isParallel
     */
    public Boolean getIsParallel() {
        return isParallel;
    }

    /**
     * @param isParallel 要设置的isParallel
     */
    public void setIsParallel(Boolean isParallel) {
        this.isParallel = isParallel;
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
     * @return the creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator 要设置的creator
     */
    public void setCreator(String creator) {
        this.creator = creator;
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
     * @return the endTime
     */
    public Date getEndTime() {
        return endTime;
    }

    /**
     * @param endTime 要设置的endTime
     */
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the operationItems
     */
    public List<TaskOperationItem> getOperationItems() {
        return operationItems;
    }

    /**
     * @return the operationStack
     */
    public TaskOperationStack getOperationStack() {
        return operationStack;
    }

    /**
     * @param operationStack 要设置的operationStack
     */
    public void setOperationStack(TaskOperationStack operationStack) {
        this.operationStack = operationStack;

        if (this.operationStack != null) {
            List<TaskOperationItem> items = new ArrayList<TaskOperationItem>();
            Iterator<TaskOperationItem> it = this.operationStack.iterator();
            while (it.hasNext()) {
                items.add(it.next());
            }
            Collections.sort(items);
            this.operationItems = items;
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(TaskActivityItem o) {
        return this.createTime.compareTo(o.getCreateTime());
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.taskId + " : " + this.taskInstUuid + " : " + this.transferCode + " : "
                + DateUtils.formatDateTime(createTime);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((taskInstUuid == null) ? 0 : taskInstUuid.hashCode());
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TaskActivityItem other = (TaskActivityItem) obj;
        if (taskInstUuid == null) {
            if (other.taskInstUuid != null)
                return false;
        } else if (!taskInstUuid.equals(other.taskInstUuid))
            return false;
        return true;
    }

}
