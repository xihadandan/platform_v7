/*
 * @(#)2013-10-22 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.timer.service.impl;

import com.wellsoft.pt.bpm.engine.entity.TaskInstance;
import com.wellsoft.pt.bpm.engine.entity.TaskTimer;
import com.wellsoft.pt.bpm.engine.entity.TaskTimerUser;
import com.wellsoft.pt.bpm.engine.service.TaskInstanceService;
import com.wellsoft.pt.bpm.engine.service.TaskTimerService;
import com.wellsoft.pt.bpm.engine.service.TaskTimerUserService;
import com.wellsoft.pt.bpm.engine.timer.service.AbstractTaskAlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2013-10-22.1	zhulh		2013-10-22		Create
 * </pre>
 * @date 2013-10-22
 */
@Service
@Transactional
public class TaskAlarmSendRepeatMessageServiceImpl extends AbstractTaskAlarmService {

    @Autowired
    private TaskTimerService taskTimerService;

    @Autowired
    private TaskTimerUserService taskTimerUserService;

    @Autowired
    private TaskInstanceService taskInstanceService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.timer.service.AbstractTaskTimerService#handler(java.lang.String)
     */
    @Override
    public void handler(String taskTimerUuid) {
        TaskTimer taskTimer = taskTimerService.get(taskTimerUuid);
        if (taskTimer == null) {
            logger.warn("Task timer is null for uuid " + taskTimerUuid);
            return;
        }

        // 如果任务已经完成则不需要再进行预警提醒及逾期处理
        TaskInstance taskInstance = taskInstanceService.get(taskTimer.getTaskInstUuid());
        if (taskInstance.getEndTime() != null) {
            return;
        }

        List<TaskTimerUser> taskTimerUsers = taskTimerUserService.getByTaskTimerUuid(taskTimerUuid);

        // 解析人员及发送消息
        hanlderAndSendMessage(taskTimerUsers, taskInstance);
    }

}
