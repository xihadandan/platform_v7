/*
 * @(#)2018-11-07 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.wellsoft.pt.bpm.engine.dao.TaskInstanceToppingDao;
import com.wellsoft.pt.bpm.engine.entity.TaskInstanceTopping;
import com.wellsoft.pt.bpm.engine.service.TaskInstanceToppingService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表WF_TASK_INSTANCE_TOPPING的service服务接口实现类
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
@Service
public class TaskInstanceToppingServiceImpl extends
        AbstractJpaServiceImpl<TaskInstanceTopping, TaskInstanceToppingDao, String> implements
        TaskInstanceToppingService {

    private static final String COUNT_BY_TASK_INST_UUID = "select count(uuid) from TaskInstanceTopping t where t.userId = :userId and t.taskInstUuid = :taskInstUuid";

    private static final String DELELE_BY_TASK_INST_UUID = "delete from TaskInstanceTopping t where t.userId = :userId and t.taskInstUuid = :taskInstUuid";

    @Override
    public long countByFlowInstUuid(String taskInstUuid, String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("userId", userId);
        return this.dao.countByHQL(COUNT_BY_TASK_INST_UUID, values);
    }

    @Override
    @Transactional
    public void deleteByFlowInstUuid(String taskInstUuid, String userId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        values.put("userId", userId);
        this.dao.deleteByHQL(DELELE_BY_TASK_INST_UUID, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskInstanceToppingService#increaseLowPriorityByTaskInstUuidAndUserId(java.lang.String, java.lang.String)
     */
    @Override
    @Transactional
    public void increaseLowPriorityByTaskInstUuidAndUserId(String taskInstUuid, String userId) {
        TaskInstanceTopping entity = new TaskInstanceTopping();
        entity.setTaskInstUuid(taskInstUuid);
        entity.setUserId(userId);
        List<TaskInstanceTopping> entities = this.listByEntity(entity);
        if (CollectionUtils.isNotEmpty(entities)) {
            entity = entities.get(0);
        }
        // 低优先级加1
        Integer lowPriority = entity.getLowPriority() == null ? 0 : entity.getLowPriority();
        lowPriority++;
        entity.setLowPriority(lowPriority);
        // 不置顶
        entity.setIsTopping(null);
        save(entity);
    }

    /**
     * @param taskInstUuid
     * @return
     */
    @Override
    public List<TaskInstanceTopping> listByTaskInstUuid(String taskInstUuid) {
        return this.dao.listByFieldEqValue("taskInstUuid", taskInstUuid);
    }

    /**
     * @param taskInstUuid
     */
    @Override
    @Transactional
    public void removeByTaskInstUuid(String taskInstUuid) {
        String hql = "delete from TaskInstanceTopping t where t.taskInstUuid = :taskInstUuid";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("taskInstUuid", taskInstUuid);
        this.dao.deleteByHQL(hql, values);
    }

}
