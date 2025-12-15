/*
 * @(#)2020年5月27日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.event;

import com.wellsoft.context.event.WellptEvent;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.support.TaskData;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年5月27日.1	zhongzh		2020年5月27日		Create
 * </pre>
 * @date 2020年5月27日
 */
public class WorkDoneEvent extends WellptEvent {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private String userId;
    private String flowInstUuid;
    private String taskInstUuid;
    private String system;
    private String tenant;
    private TaskData taskData;

    /**
     * @param userId
     * @param flowInstance
     * @param taskInstance
     * @param taskData
     */
    public WorkDoneEvent(String userId, FlowInstance flowInstance, TaskInstance taskInstance, TaskData taskData) {
        this(null, userId, flowInstance, taskInstance, taskData);
    }

    /**
     * @param source
     * @param userId
     * @param flowInstance
     * @param taskInstance
     * @param taskData
     */
    public WorkDoneEvent(Object source, String userId, FlowInstance flowInstance, TaskInstance taskInstance, TaskData taskData) {
        super(source);
        this.userId = userId;
        this.flowInstUuid = flowInstance.getUuid();
        this.taskInstUuid = taskInstance.getUuid();
        this.system = flowInstance.getSystem();
        this.tenant = flowInstance.getTenant();
        this.taskData = taskData;
    }

    public String getUserId() {
        return userId;
    }

    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @return the taskData
     */
    public TaskData getTaskData() {
        return taskData;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((taskInstUuid == null) ? 0 : taskInstUuid.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkDoneEvent other = (WorkDoneEvent) obj;
        if (taskInstUuid == null) {
            if (other.taskInstUuid != null)
                return false;
        } else if (!taskInstUuid.equals(other.taskInstUuid))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        return true;
    }

}
