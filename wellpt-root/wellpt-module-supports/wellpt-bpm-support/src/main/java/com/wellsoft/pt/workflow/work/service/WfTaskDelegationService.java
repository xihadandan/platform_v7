/*
 * @(#)2016-07-04 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.work.service;

import com.wellsoft.pt.bpm.engine.dao.TaskDelegationDao;
import com.wellsoft.pt.bpm.engine.entity.TaskDelegation;
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
 * 2016-07-04.1	zhulh		2016-07-04		Create
 * </pre>
 * @date 2016-07-04
 */
public interface WfTaskDelegationService extends JpaService<TaskDelegation, TaskDelegationDao, String> {

    /**
     * 根据UUID获取
     *
     * @param uuid
     * @return
     */
    TaskDelegation get(String uuid);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<TaskDelegation> getAll();

    /**
     * 根据实例查询列表
     *
     * @param example
     * @return
     */
    List<TaskDelegation> findByExample(TaskDelegation example);

}
