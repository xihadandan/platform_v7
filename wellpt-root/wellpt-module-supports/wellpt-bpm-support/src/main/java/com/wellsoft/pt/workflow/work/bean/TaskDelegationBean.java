/*
 * @(#)2016年7月4日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import com.wellsoft.pt.bpm.engine.entity.TaskDelegation;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年7月4日.1	zhulh		2016年7月4日		Create
 * </pre>
 * @date 2016年7月4日
 */
public class TaskDelegationBean extends TaskDelegation {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -3807273182064898572L;

    private String flowTitle;

    private String taskName;

    /**
     * @return the flowTitle
     */
    public String getFlowTitle() {
        return flowTitle;
    }

    /**
     * @param flowTitle 要设置的flowTitle
     */
    public void setFlowTitle(String flowTitle) {
        this.flowTitle = flowTitle;
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

}
