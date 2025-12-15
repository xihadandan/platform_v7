/*
 * @(#)2018年4月9日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.dao.impl;

import com.wellsoft.pt.bpm.engine.dao.TaskTimerUserDao;
import com.wellsoft.pt.bpm.engine.entity.TaskTimerUser;
import com.wellsoft.pt.jpa.dao.impl.AbstractJpaDaoImpl;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月9日.1	chenqiong		2018年4月9日		Create
 * </pre>
 * @date 2018年4月9日
 */
@Repository
public class TaskTimerUserDaoImpl extends AbstractJpaDaoImpl<TaskTimerUser, String> implements TaskTimerUserDao {
    private static final String GTE_BY_TASK_TIMER_UUID = "from TaskTimerUser task_timer_user where task_timer_user.taskTimerUuid = :taskTimerUuid";

    /**
     * 根据任务定时UUID获取相应的人员配置信息
     *
     * @param taskTimerUuid
     * @return
     */
    public List<TaskTimerUser> getByTaskTimerUuid(String taskTimerUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskTimerUuid", taskTimerUuid);
        return this.listByHQL(GTE_BY_TASK_TIMER_UUID, values);
    }
}
