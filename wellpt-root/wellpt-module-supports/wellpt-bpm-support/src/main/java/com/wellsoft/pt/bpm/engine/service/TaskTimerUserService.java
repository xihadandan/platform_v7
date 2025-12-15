/*
 * @(#)2013-5-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.TaskTimerUserDao;
import com.wellsoft.pt.bpm.engine.entity.TaskTimerUser;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface TaskTimerUserService extends JpaService<TaskTimerUser, TaskTimerUserDao, String> {

    /**
     * 根据定时器用户实例查询用户列表
     *
     * @param example
     * @return
     */
    List<TaskTimerUser> findByExample(TaskTimerUser example);

    /**
     * 根据任务定时UUID获取相应的人员配置信息
     *
     * @param taskTimerUuid
     * @return
     */
    List<TaskTimerUser> getByTaskTimerUuid(String taskTimerUuid);

    /**
     * 如何描述该方法
     *
     * @param taskTimerUuid
     */
    void removeByTaskTimerUuid(String taskTimerUuid);

}
