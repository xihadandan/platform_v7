/*
 * @(#)2019年3月25日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service;

import com.wellsoft.pt.bpm.engine.dao.TaskFormAttachmentDao;
import com.wellsoft.pt.bpm.engine.entity.TaskFormAttachment;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.repository.entity.LogicFileInfo;

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
 * 2019年3月25日.1	zhulh		2019年3月25日		Create
 * </pre>
 * @date 2019年3月25日
 */
public interface TaskFormAttachmentService extends JpaService<TaskFormAttachment, TaskFormAttachmentDao, String> {

    /**
     * @param dyFormData
     * @param taskOperation
     */
    void saveSubmitOrRollbackAttachments(DyFormData dyFormData, TaskOperation taskOperation);

    /**
     * @param dyFormData
     * @param fieldName
     * @param logicFileInfos
     * @param taskOperation
     */
    void addTaskFormAttachment(DyFormData dyFormData, String fieldName, List<LogicFileInfo> logicFileInfos,
                               TaskOperation taskOperation);

    /**
     * @param taskFormAttachment
     * @param logicFileInfos
     * @param taskOperation
     */
    void updateTaskFormAttachment(TaskFormAttachment taskFormAttachment, List<LogicFileInfo> logicFileInfos,
                                  TaskOperation taskOperation);

    /**
     * @param flowInstUuid
     * @return
     */
    List<TaskFormAttachment> getByFlowInstUuid(String flowInstUuid);

    /**
     * @param flowInstUuids
     * @return
     */
    List<TaskFormAttachment> getByFlowInstUuids(List<String> flowInstUuids);

    /**
     * @param flowInstUuid
     * @param fieldNames
     * @return
     */
    List<TaskFormAttachment> getByFlowInstUuidAndFieldNames(String flowInstUuid, List<String> fieldNames);

    /**
     * @param flowInstUuid
     */
    void removeByFlowInstUuid(String flowInstUuid);

}
