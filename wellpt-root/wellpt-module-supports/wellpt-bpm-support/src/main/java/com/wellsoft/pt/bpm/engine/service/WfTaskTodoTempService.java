/*
 * @(#)10/9/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.WfTaskTodoTempDao;
import com.wellsoft.pt.bpm.engine.entity.WfTaskTodoTempEntity;
import com.wellsoft.pt.jpa.service.JpaService;

import java.util.List;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 10/9/25.1	    zhulh		10/9/25		    Create
 * </pre>
 * @date 10/9/25
 */
public interface WfTaskTodoTempService extends JpaService<WfTaskTodoTempEntity, WfTaskTodoTempDao, Long> {
    WfTaskTodoTempEntity getByTaskInstUuidAndUserId(String taskInstUuid, String userId);

    List<WfTaskTodoTempEntity> listByTaskInstUuid(String taskInstUuid);

    void deleteByFlowInstUuid(String flowInstUuid);

    List<WfTaskTodoTempEntity> listByTaskInstUuidAndTaskInstRecVer(String taskInstUuid, Integer taskInstRecVer);

    void updateTaskTodoTemp(List<WfTaskTodoTempEntity> taskTodoTempEntities, Integer taskInstRecVer);
}
