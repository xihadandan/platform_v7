/*
 * @(#)2020年5月27日 V1.0
 *
 * Copyright 2020 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.event;

import com.wellsoft.context.event.WellptEvent;
import com.wellsoft.pt.bpm.engine.entity.FlowInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;

import java.util.Set;

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
public class WorkCopyToEvent extends WellptEvent {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private Set<String> userIds;
    private FlowInstance flowInstance;
    private TaskInstance faskInstance;

    /**
     * @param userIds
     * @param flowInstUuid
     * @param taskInstUuid
     */
    public WorkCopyToEvent(Set<String> userIds, FlowInstance flowInstance, TaskInstance faskInstance) {
        this(null, userIds, flowInstance, faskInstance);
    }

    /**
     * @param source
     * @param userIds
     * @param flowInstUuid
     * @param taskInstUuid
     */
    public WorkCopyToEvent(Object source, Set<String> userIds, FlowInstance flowInstance, TaskInstance faskInstance) {
        super(source);
        this.userIds = userIds;
        this.flowInstance = flowInstance;
        this.faskInstance = faskInstance;
    }

    public Set<String> getUserIds() {
        return userIds;
    }

    public FlowInstance getFlowInstance() {
        return flowInstance;
    }

    public TaskInstance getTaskInstance() {
        return faskInstance;
    }
}
