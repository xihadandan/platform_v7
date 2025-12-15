/*
 * @(#)2021-09-24 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.pt.bpm.engine.dao.WfTaskInstanceTodoUserDao;
import com.wellsoft.pt.bpm.engine.entity.WfTaskInstanceTodoUserEntity;
import com.wellsoft.pt.bpm.engine.service.TaskActivityService;
import com.wellsoft.pt.bpm.engine.service.WfTaskInstanceTodoUserService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据库表WF_TASK_INSTANCE_TODO_USER的service服务接口实现类
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
@Service
public class WfTaskInstanceTodoUserServiceImpl
        extends AbstractJpaServiceImpl<WfTaskInstanceTodoUserEntity, WfTaskInstanceTodoUserDao, String>
        implements WfTaskInstanceTodoUserService {

    @Autowired
    private TaskActivityService taskActivityService;

    @Override
    public List<WfTaskInstanceTodoUserEntity> getListByFlowInstUuidAndTaskIds(String flowInstUuid,
                                                                              List<String> taskIds) {
        if (taskIds == null || taskIds.size() == 0) {
            return new ArrayList<>();
        }
        List<String> preTaskIds = new ArrayList<>();
        for (String taskId : taskIds) {
            String preTaskId = taskActivityService.getPreTaskId(flowInstUuid, taskId);
            if (StringUtils.isNotBlank(preTaskId)) {
                preTaskIds.add(taskActivityService.getPreTaskId(flowInstUuid, taskId));
            }
        }
        if (preTaskIds.size() == 0) {
            return new ArrayList<>();
        }

        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        values.put("taskIds", preTaskIds);

        return this.dao.listByNameSQLQuery("getListByFlowInstUuidAndTaskIds", values);
    }

    @Override
    public WfTaskInstanceTodoUserEntity getListByFlowInstUuidAndTaskIdsAndUserId(String flowInstUuid,
                                                                                 List<String> taskIds, String userId) {
        if (taskIds == null || taskIds.size() == 0) {
            return null;
        }

        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        values.put("taskIds", taskIds);
        values.put("userId", userId);
        List<WfTaskInstanceTodoUserEntity> list = this.dao
                .listByNameSQLQuery("getListByFlowInstUuidAndTaskIdsAndUserId", values);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
