/*
 * @(#)2019年3月25日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.dao.TaskFormAttachmentLogDao;
import com.wellsoft.pt.bpm.engine.entity.TaskFormAttachment;
import com.wellsoft.pt.bpm.engine.entity.TaskFormAttachmentLog;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.bpm.engine.service.TaskFormAttachmentLogService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 2019年3月25日.1	zhulh		2019年3月25日		Create
 * </pre>
 * @date 2019年3月25日
 */
@Service
public class TaskFormAttachmentLogServiceImpl extends
        AbstractJpaServiceImpl<TaskFormAttachmentLog, TaskFormAttachmentLogDao, String> implements
        TaskFormAttachmentLogService {

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormAttachmentLogService#log(com.wellsoft.pt.bpm.engine.entity.TaskFormAttachment, com.wellsoft.pt.bpm.engine.entity.TaskOperation, java.lang.String, java.util.Map)
     */
    @Override
    @Transactional
    public void log(TaskFormAttachment taskFormAttachment, TaskOperation taskOperation, String operateType,
                    Map<String, Object> contentMap) {
        TaskFormAttachmentLog taskFormAttachmentLog = new TaskFormAttachmentLog();
        // 流程实例UUID
        taskFormAttachmentLog.setFlowInstUuid(taskOperation.getFlowInstUuid());
        // 环节实例UUID
        taskFormAttachmentLog.setTaskInstUuid(taskOperation.getTaskInstUuid());
        // 环节操作UUID
        taskFormAttachmentLog.setTaskOperationUuid(taskOperation.getUuid());
        // 附件记录UUID
        taskFormAttachmentLog.setTaskFormAttachmentUuid(taskFormAttachment.getUuid());
        // 表单附件域字段名
        taskFormAttachmentLog.setFieldName(taskFormAttachment.getFieldName());
        // 表单附件域内容
        taskFormAttachmentLog.setContent(JsonUtils.object2Json(contentMap));
        // 操作类型
        taskFormAttachmentLog.setOperateType(operateType);
        this.dao.save(taskFormAttachmentLog);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormAttachmentLogService#listByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<TaskFormAttachmentLog> listByFlowInstUuid(String flowInstUuid) {
        String hql = "from TaskFormAttachmentLog t where t.flowInstUuid = :flowInstUuid order by t.createTime asc";
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        return this.dao.listByHQL(hql, values);
    }

    /**
     * @param flowInstUuid
     */
    @Override
    @Transactional
    public void removeByFlowInstUuid(String flowInstUuid) {
        String hql = "delete from TaskFormAttachmentLog t where t.flowInstUuid = :flowInstUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        this.dao.deleteByHQL(hql, values);
    }

}
