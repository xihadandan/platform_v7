/*
 * @(#)2019年3月25日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.bpm.engine.dao.TaskFormAttachmentDao;
import com.wellsoft.pt.bpm.engine.entity.TaskFormAttachment;
import com.wellsoft.pt.bpm.engine.entity.TaskOperation;
import com.wellsoft.pt.bpm.engine.service.TaskFormAttachmentLogService;
import com.wellsoft.pt.bpm.engine.service.TaskFormAttachmentService;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.implement.combiner.dto.impl.DyFormDataImpl;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.repository.support.enums.EnumOperateType;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
public class TaskFormAttachmentServiceImpl extends
        AbstractJpaServiceImpl<TaskFormAttachment, TaskFormAttachmentDao, String> implements TaskFormAttachmentService {

    @Autowired
    private TaskFormAttachmentLogService taskFormAttachmentLogService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormAttachmentService#saveSubmitOrRollbackAttachments(com.wellsoft.pt.dyform.facade.dto.DyFormData, com.wellsoft.pt.bpm.engine.entity.TaskOperation)
     */
    @SuppressWarnings("unchecked")
    @Override
    @Transactional
    public void saveSubmitOrRollbackAttachments(DyFormData dyFormData, TaskOperation taskOperation) {
        DyFormDataImpl dyFormDataImpl = (DyFormDataImpl) dyFormData;
        List<String> fieldNames = dyFormDataImpl.doGetFieldNamesOfFile();
        if (CollectionUtils.isEmpty(fieldNames)) {
            return;
        }

        List<TaskFormAttachment> taskFormAttachments = getByFlowInstUuidAndFieldNames(taskOperation.getFlowInstUuid(),
                fieldNames);
        for (String fieldName : fieldNames) {
            if (!dyFormData.isFieldExist(fieldName)) {
                continue;
            }
            List<LogicFileInfo> logicFileInfos = (List<LogicFileInfo>) dyFormData.getFieldValue(fieldName);
            if (CollectionUtils.isEmpty(logicFileInfos)) {
                continue;
            }
            TaskFormAttachment taskFormAttachment = getTaskFormAttachment(fieldName, taskFormAttachments);
            // 添加表单附件信息
            if (taskFormAttachment == null) {
                addTaskFormAttachment(dyFormData, fieldName, logicFileInfos, taskOperation);
            } else {
                // 更新表单附件信息
                updateTaskFormAttachment(taskFormAttachment, logicFileInfos, taskOperation);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormAttachmentService#addTaskFormAttachment(com.wellsoft.pt.dyform.facade.dto.DyFormData, java.lang.String, java.util.List, com.wellsoft.pt.bpm.engine.entity.TaskOperation)
     */
    @Override
    @Transactional
    public void addTaskFormAttachment(DyFormData dyFormData, String fieldName, List<LogicFileInfo> logicFileInfos,
                                      TaskOperation taskOperation) {
        TaskFormAttachment taskFormAttachment = new TaskFormAttachment();
        // 流程实例UUID
        taskFormAttachment.setFlowInstUuid(taskOperation.getFlowInstUuid());
        // 表单数据UUID
        taskFormAttachment.setDataUuid(dyFormData.getDataUuid());
        // 表单附件域字段名
        taskFormAttachment.setFieldName(fieldName);
        // 表单附件域内容
        Map<String, Object> contentMap = getLogicFilesContent(logicFileInfos);
        taskFormAttachment.setContent(JsonUtils.object2Json(contentMap));
        this.dao.save(taskFormAttachment);

        // 记录日志
        taskFormAttachmentLogService.log(taskFormAttachment, taskOperation, EnumOperateType.NEW.getValue(), contentMap);
    }

    /**
     * @param logicFileInfos
     * @return
     */
    @SuppressWarnings("rawtypes")
    private Map<String, Object> getLogicFilesContent(List<LogicFileInfo> logicFileInfos) {
        Map<String, Object> contentMap = Maps.newLinkedHashMap();
        for (Object logicFileInfo : logicFileInfos) {
            if (logicFileInfo instanceof LogicFileInfo) {
                contentMap.put(((LogicFileInfo) logicFileInfo).getFileID(),
                        ((LogicFileInfo) logicFileInfo).getFileName());
            } else if (logicFileInfo instanceof Map) {
                Object fileID = ((Map) logicFileInfo).get("fileID");
                String fileName = Objects.toString(((Map) logicFileInfo).get("fileName"), StringUtils.EMPTY);
                if (StringUtils.isBlank(fileName)) {
                    fileName = Objects.toString(((Map) logicFileInfo).get("filename"), StringUtils.EMPTY);
                }
                contentMap.put(ObjectUtils.toString(fileID), fileName);
            }
        }
        return contentMap;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormAttachmentService#updateTaskFormAttachment(com.wellsoft.pt.dyform.facade.dto.DyFormData, com.wellsoft.pt.bpm.engine.entity.TaskFormAttachment, java.util.List, com.wellsoft.pt.bpm.engine.entity.TaskOperation)
     */
    @Override
    @Transactional
    public void updateTaskFormAttachment(TaskFormAttachment taskFormAttachment, List<LogicFileInfo> logicFileInfos,
                                         TaskOperation taskOperation) {
        String oldContent = taskFormAttachment.getContent();
        Map<String, Object> contentMap = getLogicFilesContent(logicFileInfos);
        String newContent = JsonUtils.object2Json(contentMap);
        if (StringUtils.equals(oldContent, newContent)) {
            return;
        }

        taskFormAttachment.setContent(newContent);
        this.dao.save(taskFormAttachment);

        // 添加的附件信息
        Map<String, Object> addedContent = getAddedContent(oldContent, newContent);
        // 删除的附件信息
        Map<String, Object> deletedContent = getDeleteContent(oldContent, newContent);

        // 记录日志
        if (!addedContent.isEmpty()) {
            taskFormAttachmentLogService.log(taskFormAttachment, taskOperation, EnumOperateType.NEW.getValue(),
                    addedContent);
        }
        if (!deletedContent.isEmpty()) {
            taskFormAttachmentLogService.log(taskFormAttachment, taskOperation, EnumOperateType.DEL.getValue(),
                    deletedContent);
        }
    }

    /**
     * @param oldContent
     * @param newContent
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getAddedContent(String oldContent, String newContent) {
        Map<String, Object> oldContentMap = JsonUtils.json2Object(oldContent, LinkedHashMap.class);
        Map<String, Object> newContentMap = JsonUtils.json2Object(newContent, LinkedHashMap.class);
        for (String key : oldContentMap.keySet()) {
            newContentMap.remove(key);
        }
        return newContentMap;
    }

    /**
     * @param oldContent
     * @param newContent
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> getDeleteContent(String oldContent, String newContent) {
        Map<String, Object> oldContentMap = JsonUtils.json2Object(oldContent, LinkedHashMap.class);
        Map<String, Object> newContentMap = JsonUtils.json2Object(newContent, LinkedHashMap.class);
        for (String key : newContentMap.keySet()) {
            oldContentMap.remove(key);
        }
        return oldContentMap;
    }

    /**
     * @param fieldName
     * @param taskFormAttachments
     * @return
     */
    private TaskFormAttachment getTaskFormAttachment(String fieldName, List<TaskFormAttachment> taskFormAttachments) {
        for (TaskFormAttachment taskFormAttachment : taskFormAttachments) {
            if (StringUtils.equals(taskFormAttachment.getFieldName(), fieldName)) {
                return taskFormAttachment;
            }
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormAttachmentService#getByFlowInstUuid(java.lang.String)
     */
    @Override
    public List<TaskFormAttachment> getByFlowInstUuid(String flowInstUuid) {
        TaskFormAttachment taskFormAttachment = new TaskFormAttachment();
        taskFormAttachment.setFlowInstUuid(flowInstUuid);
        return this.dao.listByEntity(taskFormAttachment);
    }

    @Override
    public List<TaskFormAttachment> getByFlowInstUuids(List<String> flowInstUuids) {
        if (CollectionUtils.isEmpty(flowInstUuids)) {
            return Lists.newArrayList();
        }
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuids", flowInstUuids);

        return this.dao.listByNameSQLQuery("getTaskFormAttachmentsByFlowInstUuids", values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.service.TaskFormAttachmentService#getByFlowInstUuidAndFieldNames(java.lang.String, java.util.List)
     */
    @Override
    public List<TaskFormAttachment> getByFlowInstUuidAndFieldNames(String flowInstUuid, List<String> fieldNames) {
        String hql = "from TaskFormAttachment t where t.flowInstUuid = :flowInstUuid and t.fieldName in(:fieldNames)";
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        values.put("fieldNames", fieldNames);
        return this.dao.listByHQL(hql, values);
    }

    /**
     * @param flowInstUuid
     */
    @Override
    @Transactional
    public void removeByFlowInstUuid(String flowInstUuid) {
        String hql = "delete from TaskFormAttachment t where t.flowInstUuid = :flowInstUuid";
        Map<String, Object> values = Maps.newHashMap();
        values.put("flowInstUuid", flowInstUuid);
        this.dao.deleteByHQL(hql, values);
    }

}
