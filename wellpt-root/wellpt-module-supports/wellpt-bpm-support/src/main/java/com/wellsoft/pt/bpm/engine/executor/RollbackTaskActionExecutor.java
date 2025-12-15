/*
 * @(#)2014-10-3 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor;

import com.wellsoft.pt.bpm.engine.entity.TaskInstance;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-3.1	zhulh		2014-10-3		Create
 * </pre>
 * @date 2014-10-3
 */
public interface RollbackTaskActionExecutor extends TaskExecutor {

    /**
     * 如何描述该方法
     *
     * @param taskInstance
     * @param variables
     * @return
     */
    List<RollBackTask> buildToRollbackTasks(TaskInstance taskInstance);

    /**
     * 如何描述该方法
     *
     * @param taskInstance
     * @param variables
     * @return
     */
    List<RollBackTask> buildToRollbackTasks(TaskInstance taskInstance, Map<String, Object> variables);

    /**
     * 获取直接退回环节信息
     *
     * @param currentTaskInstUuid
     * @param rollbackToTaskInstUuid
     * @param taskIdentityUuid
     * @return
     */
    RollBackTaskInfo getDirectRollbackTaskInfo(String currentTaskInstUuid, String rollbackToTaskInstUuid, String taskIdentityUuid);

}
