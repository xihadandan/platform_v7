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

import java.util.List;

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
public class HandOverParam extends Param {
    private List<String> handOverUser;

    private boolean requiredHandOverPermission = true;

    /**
     *
     */
    public HandOverParam() {
        super();
    }

    /**
     * @param taskInstance
     * @param taskData
     * @param taskIdentity
     * @param log
     */
    public HandOverParam(TaskInstance taskInstance, TaskData taskData, TaskIdentity taskIdentity, boolean log,
                         boolean requiredHandOverPermission) {
        super(taskInstance, taskData, taskIdentity, log);
        this.requiredHandOverPermission = requiredHandOverPermission;
    }

    /**
     * @param taskInstance
     * @param taskData
     * @param taskIdentity
     */
    public HandOverParam(TaskInstance taskInstance, TaskData taskData, TaskIdentity taskIdentity) {
        super(taskInstance, taskData, taskIdentity);
    }

    /**
     * 如何描述该构造方法
     *
     * @param taskInstance
     * @param taskData
     */
    public HandOverParam(TaskInstance taskInstance, TaskData taskData) {
        super(taskInstance, taskData);
    }

    /**
     * @return the requiredHandOverPermission
     */
    public boolean isRequiredHandOverPermission() {
        return requiredHandOverPermission;
    }

    /**
     * @return the handOverUser
     */
    public List<String> getHandOverUser() {
        return handOverUser;
    }

    /**
     * @param handOverUser 要设置的handOverUser
     */
    public void setHandOverUser(List<String> handOverUser) {
        this.handOverUser = handOverUser;
    }

}
