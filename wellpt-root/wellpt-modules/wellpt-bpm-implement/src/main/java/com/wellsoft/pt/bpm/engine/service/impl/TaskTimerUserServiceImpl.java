/*
 * @(#)2013-5-25 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.wellsoft.pt.bpm.engine.dao.TaskTimerUserDao;
import com.wellsoft.pt.bpm.engine.entity.TaskTimerUser;
import com.wellsoft.pt.bpm.engine.service.TaskTimerUserService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
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
 * 2013-5-25.1	zhulh		2013-5-25		Create
 * </pre>
 * @date 2013-5-25
 */
@Service
public class TaskTimerUserServiceImpl extends AbstractJpaServiceImpl<TaskTimerUser, TaskTimerUserDao, String> implements
        TaskTimerUserService {

    private static final String REMOVE_BY_TASK_TIMER_UUID = "delete from TaskTimerUser task_timer_user where task_timer_user.taskTimerUuid = :taskTimerUuid";

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerUserService#findByExample(com.wellsoft.pt.bpm.engine.entity.TaskTimerUser)
     */
    @Override
    public List<TaskTimerUser> findByExample(TaskTimerUser example) {
        return dao.listByEntity(example);
    }

    /**
     * 根据任务定时UUID获取相应的人员配置信息
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerUserService#getByTaskTimerUuid(java.lang.String)
     */
    @Override
    public List<TaskTimerUser> getByTaskTimerUuid(String taskTimerUuid) {
        return dao.getByTaskTimerUuid(taskTimerUuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskTimerUserService#removeByTaskTimerUuid(java.lang.String)
     */
    @Override
    @Transactional
    public void removeByTaskTimerUuid(String taskTimerUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskTimerUuid", taskTimerUuid);
        this.dao.deleteByHQL(REMOVE_BY_TASK_TIMER_UUID, values);
    }

}
