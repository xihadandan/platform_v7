/*
 * @(#)2014-11-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.param;

import com.wellsoft.pt.bpm.engine.executor.Param;

/**
 * Description: 撤回转换参数
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-11-10.1	zhulh		2014-11-10		Create
 * </pre>
 * @date 2014-11-10
 */
public class CancelTransferParam extends Param {
    // 要撤销的任务UUID
    private String sourceTaskInstUuid;

    // 目标任务UUID
    private String targetTaskInstUuid;

    // 办理人ID
    private String todoUserId;

    /**
     * @return the sourceTaskInstUuid
     */
    public String getSourceTaskInstUuid() {
        return sourceTaskInstUuid;
    }

    /**
     * @param sourceTaskInstUuid 要设置的sourceTaskInstUuid
     */
    public void setSourceTaskInstUuid(String sourceTaskInstUuid) {
        this.sourceTaskInstUuid = sourceTaskInstUuid;
    }

    /**
     * @return the targetTaskInstUuid
     */
    public String getTargetTaskInstUuid() {
        return targetTaskInstUuid;
    }

    /**
     * @param targetTaskInstUuid 要设置的targetTaskInstUuid
     */
    public void setTargetTaskInstUuid(String targetTaskInstUuid) {
        this.targetTaskInstUuid = targetTaskInstUuid;
    }

    /**
     * @return the todoUserId
     */
    public String getTodoUserId() {
        return todoUserId;
    }

    /**
     * @param todoUserId 要设置的todoUserId
     */
    public void setTodoUserId(String todoUserId) {
        this.todoUserId = todoUserId;
    }

}
