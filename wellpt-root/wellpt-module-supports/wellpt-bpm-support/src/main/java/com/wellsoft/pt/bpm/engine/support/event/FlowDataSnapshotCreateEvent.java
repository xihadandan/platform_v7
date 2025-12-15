/*
 * @(#)8/28/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support.event;

import com.wellsoft.context.event.WellptEvent;
import com.wellsoft.pt.bpm.engine.support.TaskData;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/28/25.1	    zhulh		8/28/25		    Create
 * </pre>
 * @date 8/28/25
 */
public class FlowDataSnapshotCreateEvent extends WellptEvent {

    private String taskOperationUuid;
    private String taskInstUuid;
    private String flowInstUuid;
    private TaskData taskData;
    private String system;

    /**
     * @param taskInstUuid
     * @param flowInstUuid
     * @param taskData
     * @param system
     */
    public FlowDataSnapshotCreateEvent(String taskOperationUuid, String taskInstUuid, String flowInstUuid, TaskData taskData, String system) {
        super(flowInstUuid);
        this.taskOperationUuid = taskOperationUuid;
        this.taskInstUuid = taskInstUuid;
        this.flowInstUuid = flowInstUuid;
        this.taskData = taskData;
        this.system = system;
    }

    /**
     * @return the taskOperationUuid
     */
    public String getTaskOperationUuid() {
        return taskOperationUuid;
    }

    /**
     * @return the taskInstUuid
     */
    public String getTaskInstUuid() {
        return taskInstUuid;
    }

    /**
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return flowInstUuid;
    }

    /**
     * @return the taskData
     */
    public TaskData getTaskData() {
        return taskData;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

}
