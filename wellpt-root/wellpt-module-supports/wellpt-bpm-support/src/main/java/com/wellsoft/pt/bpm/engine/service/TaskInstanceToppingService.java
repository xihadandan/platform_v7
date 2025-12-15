/*
 * @(#)2018-11-07 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.TaskInstanceToppingDao;
import com.wellsoft.pt.bpm.engine.entity.TaskInstanceTopping;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据库表WF_TASK_INSTANCE_TOPPING的service服务接口
 *
 * @author lst
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018-11-07.1	lst		2018-11-07		Create
 * </pre>
 * @date 2018-11-07
 */
public interface TaskInstanceToppingService extends JpaService<TaskInstanceTopping, TaskInstanceToppingDao, String> {

    public long countByFlowInstUuid(String taskInstUuid, String userId);

    public void deleteByFlowInstUuid(String taskInstUuid, String userId);

    /**
     * 根据环节实例UUID及用户ID，递增低优先级
     *
     * @param taskInstUuid
     * @param userId
     */
    public void increaseLowPriorityByTaskInstUuidAndUserId(String taskInstUuid, String userId);

    /**
     * @param taskInstUuid
     * @return
     */
    List<TaskInstanceTopping> listByTaskInstUuid(String taskInstUuid);

    /**
     * @param taskInstUuid
     */
    void removeByTaskInstUuid(String taskInstUuid);
}
