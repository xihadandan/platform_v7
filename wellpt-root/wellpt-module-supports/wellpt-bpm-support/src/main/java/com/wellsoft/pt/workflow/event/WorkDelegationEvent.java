/*
 * @(#)5/13/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.event;

import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.support.TaskData;

import java.util.Set;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 5/13/25.1	    zhulh		5/13/25		    Create
 * </pre>
 * @date 5/13/25
 */
public class WorkDelegationEvent extends WorkTodoEvent {

    public WorkDelegationEvent(Set<String> userIds, FlowInstance flowInstance, TaskInstance faskInstance, TaskData taskData) {
        super(userIds, flowInstance, faskInstance, taskData);
    }

    public WorkDelegationEvent(Object source, Set<String> userIds, FlowInstance flowInstance, TaskInstance faskInstance, TaskData taskData) {
        super(source, userIds, flowInstance, faskInstance, taskData);
    }

}
