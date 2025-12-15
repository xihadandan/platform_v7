/*
 * @(#)2019年3月25日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.TaskFormAttachmentLogDao;
import com.wellsoft.pt.bpm.engine.entity.TaskFormAttachment;
import com.wellsoft.pt.bpm.engine.entity.TaskFormAttachmentLog;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.jpa.service.JpaService;

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
public interface TaskFormAttachmentLogService extends
        JpaService<TaskFormAttachmentLog, TaskFormAttachmentLogDao, String> {

    /**
     * @param taskFormAttachment
     * @param taskOperation
     * @param operateType
     * @param contentMap
     */
    void log(TaskFormAttachment taskFormAttachment, TaskOperation taskOperation, String operateType,
             Map<String, Object> contentMap);

    /**
     * @param flowInstUuid
     * @return
     */
    List<TaskFormAttachmentLog> listByFlowInstUuid(String flowInstUuid);

    /**
     * @param flowInstUuid
     */
    void removeByFlowInstUuid(String flowInstUuid);
}
