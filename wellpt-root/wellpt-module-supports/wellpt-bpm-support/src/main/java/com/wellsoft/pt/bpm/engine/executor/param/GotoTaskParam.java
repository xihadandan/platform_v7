/*
 * @(#)2014-10-21 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.executor.param;

import com.wellsoft.pt.bpm.engine.entity.TaskIdentity;
import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.executor.Param;
import com.wellsoft.pt.bpm.engine.support.TaskData;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-10-21.1	zhulh		2014-10-21		Create
 * </pre>
 * @date 2014-10-21
 */
public class GotoTaskParam extends Param {
    private String gotoTaskId;

    private boolean requiredGotoTaskPermission = true;

    /**
     *
     */
    public GotoTaskParam() {
        super();
    }

    /**
     * @param taskInstance
     * @param taskData
     * @param taskIdentity
     * @param log
     */
    public GotoTaskParam(TaskInstance taskInstance, TaskData taskData, TaskIdentity taskIdentity, boolean log,
                         String gotoTaskId, boolean requiredGotoTaskPermission) {
        super(taskInstance, taskData, taskIdentity, log);
        this.requiredGotoTaskPermission = requiredGotoTaskPermission;
        this.gotoTaskId = gotoTaskId;
    }

    /**
     * @return the gotoTaskId
     */
    public String getGotoTaskId() {
        return gotoTaskId;
    }

    /**
     * @return the requiredGotoTaskPermission
     */
    public boolean isRequiredGotoTaskPermission() {
        return requiredGotoTaskPermission;
    }

}
