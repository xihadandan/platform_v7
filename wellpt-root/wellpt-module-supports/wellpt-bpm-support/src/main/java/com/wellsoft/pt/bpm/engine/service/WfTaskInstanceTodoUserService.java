/*
 * @(#)2021-09-24 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;


import com.wellsoft.pt.bpm.engine.dao.WfTaskInstanceTodoUserDao;
import com.wellsoft.pt.bpm.engine.entity.WfTaskInstanceTodoUserEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description: 数据库表WF_TASK_INSTANCE_TODO_USER的service服务接口
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-09-24.1	zenghw		2021-09-24		Create
 * </pre>
 * @date 2021-09-24
 */
public interface WfTaskInstanceTodoUserService extends JpaService<WfTaskInstanceTodoUserEntity, WfTaskInstanceTodoUserDao, String> {


    /**
     * //获取此环节的待办人员和待办人员路径（此环节的办理人是其中的人）
     *
     * @param flowInstUuid 流程实例uuid
     * @param taskIds      环节id集合
     * @return java.util.List<com.wellsoft.pt.bpm.engine.entity.WfTaskInstanceTodoUserEntity>
     **/
    public List<WfTaskInstanceTodoUserEntity> getListByFlowInstUuidAndTaskIds(String flowInstUuid, List<String> taskIds);

    /**
     * 获取指定环节的办理人和办理人路径
     *
     * @param flowInstUuid 流程实例uuid
     * @param taskIds      环节id集合
     * @param userId       用户ID
     * @return com.wellsoft.pt.bpm.engine.entity.WfTaskInstanceTodoUserEntity
     **/
    public WfTaskInstanceTodoUserEntity getListByFlowInstUuidAndTaskIdsAndUserId(String flowInstUuid, List<String> taskIds, String userId);
}
