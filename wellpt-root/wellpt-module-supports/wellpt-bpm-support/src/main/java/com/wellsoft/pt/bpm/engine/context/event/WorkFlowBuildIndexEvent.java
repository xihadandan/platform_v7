/*
 * @(#)6/17/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.context.event;

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
 * 6/17/25.1	    zhulh		6/17/25		    Create
 * </pre>
 * @date 6/17/25
 */
public class WorkFlowBuildIndexEvent extends WellptEvent {

    private static final long serialVersionUID = -3492686683677422173L;

    private TaskData taskData;

    private String system;

    /**
     * @param taskData
     */
    public WorkFlowBuildIndexEvent(TaskData taskData, String system) {
        super(taskData.getFlowInstUuid());
        this.taskData = taskData;
        this.system = system;
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

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }
}
