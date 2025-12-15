/*
 * @(#)2013-5-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao;

import com.wellsoft.pt.bpm.engine.entity.TaskTimerUser;
import com.wellsoft.pt.jpa.dao.JpaDao;

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
 * 2013-5-25.1	zhulh		2013-5-25		Create
 * </pre>
 * @date 2013-5-25
 */
public interface TaskTimerUserDao extends JpaDao<TaskTimerUser, String> {

    /**
     * 如何描述该方法
     *
     * @param taskTimerUuid
     * @return
     */
    List<TaskTimerUser> getByTaskTimerUuid(String taskTimerUuid);

}
