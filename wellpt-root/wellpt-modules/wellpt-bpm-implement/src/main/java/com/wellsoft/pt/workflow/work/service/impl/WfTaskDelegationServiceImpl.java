/*
 * @(#)2016-07-04 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.service.impl;

import com.wellsoft.pt.bpm.engine.dao.TaskDelegationDao;
import com.wellsoft.pt.bpm.engine.entity.TaskDelegation;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.workflow.work.service.WfTaskDelegationService;
import org.springframework.stereotype.Service;

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
 * 2016-07-04.1	zhulh		2016-07-04		Create
 * </pre>
 * @date 2016-07-04
 */
@Service
public class WfTaskDelegationServiceImpl extends AbstractJpaServiceImpl<TaskDelegation, TaskDelegationDao, String>
        implements WfTaskDelegationService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WfTaskDelegationService#get(java.lang.String)
     */
    @Override
    public TaskDelegation get(String uuid) {
        return this.dao.getOne(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.WfTaskDelegationService#getAll()
     */
    @Override
    public List<TaskDelegation> getAll() {
        return listAll();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.workflow.work.service.TaskDelegationService#findByExample(TaskDelegation)
     */
    @Override
    public List<TaskDelegation> findByExample(TaskDelegation example) {
        return this.dao.listByEntity(example);
    }

}
