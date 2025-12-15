/*
 * @(#)2018年6月6日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.bean;

import com.wellsoft.pt.bpm.engine.entity.TaskOperation;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年6月6日.1	zhulh		2018年6月6日		Create
 * </pre>
 * @date 2018年6月6日
 */
public class TaskOperationBean extends TaskOperation {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6537724941109596601L;
    // 主送人名称
    private String userName;

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName 要设置的userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

}
