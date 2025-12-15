/*
 * @(#)2019年8月21日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.UuidUtils;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumRelationTblSystemField;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumSystemField;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryData;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo;
import com.wellsoft.pt.dyform.implement.repository.usertable.metadata.ColumnMetadata;
import com.wellsoft.pt.dyform.implement.repository.usertable.service.UserTableFormDataService;
import com.wellsoft.pt.dyform.implement.repository.usertable.service.UserTableMetadataService;
import com.wellsoft.pt.dyform.implement.repository.usertable.support.UserTableFormDataQueryInfo;
import com.wellsoft.pt.dyform.implement.repository.util.RepositoryConvertUtils;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月21日.1	zhulh		2019年8月21日		Create
 * </pre>
 * @date 2019年8月21日
 */
@Component
public class UserTableFormRepository extends AbstractFormRepository {

    @Autowired
    private UserTableMetadataService userTableMetadataService;

    @Autowired
    private UserTableFormDataService userTableFormDataService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#getDataOfMainform(java.lang.String, java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public Map<String, Object> getDataOfMainform(String formId, String dataUuid, FormRepositoryContext repositoryContext) {
        String tableName = repositoryContext.getUserTableName();
        Assert.hasText(tableName, "用户表不能为空！");
        Map<String, Object> formData = Maps.newHashMap();
        Set<String> fieldNames = userTableMetadataService.getColumnNames(tableName);
        StringBuilder sb = new StringBuilder("select ");
        sb.append(StringUtils.join(fieldNames, Separator.COMMA.getValue())).append(" ");
        sb.append("from " + tableName + " t ");
        sb.append("where t.uuid = :uuid");
        Map<String, Object> values = Maps.newHashMap();
        values.put("uuid", dataUuid);
        try {
            List<Map<String, Object>> list = userTableFormDataService.query(sb.toString(), values, 1);
            if (CollectionUtils.isNotEmpty(list)) {
                // 装饰数据
                decorateResultData(list, repositoryContext);
                return list.get(0);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return formData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#getDataOfSubform(java.lang.String, java.lang.String, java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public List<Map<String, Object>> getDataOfSubform(String subformId, String mainformId, String mainformDataUuid,
                                                      FormRepositoryContext repositoryContext) {
        String tableName = repositoryContext.getUserTableName();
        String relationTableName = repositoryContext.getRelationTableName();
        Assert.hasText(tableName, "用户表不能为空！");
        Set<String> fieldNames = userTableMetadataService.getColumnNames(tableName);
        Set<String> columns = Sets.newLinkedHashSet();
        for (String fieldName : fieldNames) {
            columns.add("t1." + fieldName);
        }
        StringBuilder sb = new StringBuilder("select ");
        sb.append(StringUtils.join(columns, Separator.COMMA.getValue())).append(",");
        sb.append("t2.sort_order ");
        sb.append("from " + tableName + " t1 ");
        sb.append("inner join " + relationTableName + " t2 ");
        sb.append("on t1.uuid = t2.data_uuid ");
        sb.append("where t2.mainform_data_uuid = :mainform_data_uuid ");
        sb.append("order by t2.sort_order asc");
        Map<String, Object> values = Maps.newHashMap();
        values.put("mainform_data_uuid", mainformDataUuid);
        List<Map<String, Object>> list = Lists.newArrayListWithExpectedSize(0);
        try {
            list = userTableFormDataService.query(sb.toString(), values, Integer.MAX_VALUE);
            if (CollectionUtils.isNotEmpty(list)) {
                // 装饰数据
                decorateResultData(list, repositoryContext);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return list;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#saveFormData(com.wellsoft.pt.dyform.implement.repository.FormData, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public String saveFormData(FormData formData, FormRepositoryContext repositoryContext) {
        String tableName = repositoryContext.getUserTableName(formData.getFormId());
        Assert.hasText(tableName, "用户表不能为空！");
        // 主表数据
        if (formData.isNew()) {
            insertFormData(tableName, formData, repositoryContext);
        } else if (formData.isUpdate()) {
            updateFormData(tableName, formData, repositoryContext);
        } else if (formData.isDelete()) {
            deleteFormData(formData.getFormId(), formData.getDataUuid(), false, repositoryContext);
        }
        // 从表数据
        List<FormData> subformDatas = formData.getSubformDatas();
        for (FormData subformData : subformDatas) {
            FormRepositoryContext subformRepositoryContext = FormRepositoryContextFactory.getByFormId(subformData
                    .getFormId());
            saveFormData(subformData, subformRepositoryContext);
            // 添加主从表数据关系
            if (subformData.isNew()) {
                addSubformRelation(formData, subformData, subformRepositoryContext);
            }
        }
        return formData.getDataUuid();
    }

    /**
     * 添加主从表数据关系
     *
     * @param formData
     * @param subformData
     * @param subformRepositoryContext
     */
    private void addSubformRelation(FormData formData, FormData subformData,
                                    FormRepositoryContext subformRepositoryContext) {
        String userId = SpringSecurityUtils.getCurrentUserId();
        Date currentTime = Calendar.getInstance().getTime();
        String dataUuid = subformData.getDataUuid();
        String mainformDataUuid = formData.getDataUuid();
        String mainformFormUuid = formData.getFormUuid();
        Integer sortOrder = Integer.valueOf(ObjectUtils.toString(subformData.getDataMap().get("seqNO"), "1"));
        String relationTableName = subformRepositoryContext.getRelationTableName();
        // 插入SQL语句
        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(relationTableName).append("(");
        sb.append(EnumRelationTblSystemField.uuid.name()).append(",");
        sb.append(EnumRelationTblSystemField.creator.name()).append(",");
        sb.append(EnumRelationTblSystemField.create_time.name()).append(",");
        sb.append(EnumRelationTblSystemField.modifier.name()).append(",");
        sb.append(EnumRelationTblSystemField.modify_time.name()).append(",");
        sb.append(EnumRelationTblSystemField.rec_ver.name()).append(",");
        sb.append(EnumRelationTblSystemField.data_uuid.name()).append(",");
        sb.append(EnumRelationTblSystemField.mainform_data_uuid.name()).append(",");
        sb.append(EnumRelationTblSystemField.mainform_form_uuid.name()).append(",");
        sb.append(EnumRelationTblSystemField.sort_order.name()).append(") ");
        sb.append("values(");
        sb.append(":").append(EnumRelationTblSystemField.uuid.name()).append(",");
        sb.append(":").append(EnumRelationTblSystemField.creator.name()).append(",");
        sb.append(":").append(EnumRelationTblSystemField.create_time.name()).append(",");
        sb.append(":").append(EnumRelationTblSystemField.modifier.name()).append(",");
        sb.append(":").append(EnumRelationTblSystemField.modify_time.name()).append(",");
        sb.append(":").append(EnumRelationTblSystemField.rec_ver.name()).append(",");
        sb.append(":").append(EnumRelationTblSystemField.data_uuid.name()).append(",");
        sb.append(":").append(EnumRelationTblSystemField.mainform_data_uuid.name()).append(",");
        sb.append(":").append(EnumRelationTblSystemField.mainform_form_uuid.name()).append(",");
        sb.append(":").append(EnumRelationTblSystemField.sort_order.name()).append(") ");
        // 参数值
        Map<String, TypedValue> values = Maps.newHashMap();
        values.put(EnumRelationTblSystemField.uuid.name(),
                new TypedValue(StandardBasicTypes.STRING, UuidUtils.createUuid()));
        values.put(EnumRelationTblSystemField.creator.name(), new TypedValue(StandardBasicTypes.STRING, userId));
        values.put(EnumRelationTblSystemField.create_time.name(), new TypedValue(StandardBasicTypes.TIMESTAMP,
                currentTime));
        values.put(EnumRelationTblSystemField.modifier.name(), new TypedValue(StandardBasicTypes.STRING, userId));
        values.put(EnumRelationTblSystemField.modify_time.name(), new TypedValue(StandardBasicTypes.TIMESTAMP,
                currentTime));
        values.put(EnumRelationTblSystemField.rec_ver.name(), new TypedValue(StandardBasicTypes.INTEGER, 0));
        values.put(EnumRelationTblSystemField.data_uuid.name(), new TypedValue(StandardBasicTypes.STRING, dataUuid));
        values.put(EnumRelationTblSystemField.mainform_data_uuid.name(), new TypedValue(StandardBasicTypes.STRING,
                mainformDataUuid));
        values.put(EnumRelationTblSystemField.mainform_form_uuid.name(), new TypedValue(StandardBasicTypes.STRING,
                mainformFormUuid));
        values.put(EnumRelationTblSystemField.sort_order.name(), new TypedValue(StandardBasicTypes.INTEGER, sortOrder));
        userTableFormDataService.executeUpdate(sb.toString(), values);
    }

    /**
     * @param tableName
     * @param formData
     * @param repositoryContext
     */
    private void insertFormData(String tableName, FormData formData, FormRepositoryContext repositoryContext) {
        Map<String, Object> dataMap = formData.getDataMap();
        List<String> fieldParams = Lists.newArrayList();
        List<String> fieldNames = Lists.newArrayList();
        Map<String, TypedValue> paramValues = Maps.newHashMap();
        Set<String> keys = userTableMetadataService.filterTableFields(dataMap.keySet(), tableName);
        for (String key : keys) {
            if (repositoryContext.isFileField(key)) {
                // 附件字段值处理
                handleFileFileValue(key, formData);
                continue;
            }
            ColumnMetadata columnMetadata = userTableMetadataService.getColumnMetadata(key, tableName);
            TypedValue expectedTypeValue = RepositoryConvertUtils.convert2ExpectedTypeValue(dataMap.get(key),
                    columnMetadata);
            paramValues.put(key, expectedTypeValue);
            fieldNames.add(key);
            fieldParams.add(":" + key);
        }
        // 系统字段
        Map<String, Object> systemColumns = userTableMetadataService.getSystemColumns(tableName);
        for (Entry<String, Object> entry : systemColumns.entrySet()) {
            String columnName = entry.getKey();
            ColumnMetadata columnMetadata = userTableMetadataService.getColumnMetadata(columnName, tableName);
            TypedValue expectedTypeValue = RepositoryConvertUtils.convert2ExpectedTypeValue(entry.getValue(),
                    columnMetadata);
            paramValues.put(columnName, expectedTypeValue);
            fieldNames.add(columnName);
            fieldParams.add(":" + columnName);
        }

        StringBuilder sb = new StringBuilder("insert into ");
        sb.append(tableName + "(" + StringUtils.join(fieldNames, Separator.COMMA.getValue()) + " ) ");
        sb.append("values(" + StringUtils.join(fieldParams, Separator.COMMA.getValue()) + ")");
        userTableFormDataService.executeUpdate(sb.toString(), paramValues);
    }

    /**
     * @param fieldName
     * @param formData
     */
    private void handleFileFileValue(String fieldName, FormData formData) {
        String folderId = formData.getDataUuid();
        List<String> fileIds = getFileIds(fieldName, formData);

        List<String> addedFileIds = Lists.newArrayList();
        addedFileIds.addAll(fileIds);
        List<String> deletedFileIds = Lists.newArrayList();
        List<String> existsFileIds = Lists.newArrayList();
        boolean folderExist = mongoFileService.isFolderExist(folderId);
        if (folderExist) {
            List<LogicFileInfo> dbFiles = mongoFileService.getNonioFilesFromFolder(folderId, fieldName);
            if (CollectionUtils.isNotEmpty(dbFiles)) {
                for (LogicFileInfo logicFileInfo : dbFiles) {
                    existsFileIds.add(logicFileInfo.getFileID());
                }
            }
            deletedFileIds.addAll(existsFileIds);
            // 删除的文件ID
            deletedFileIds.removeAll(addedFileIds);
            // 新增的文件ID
            addedFileIds.removeAll(existsFileIds);
        }
        // 删除文件
        for (String deletedFileId : deletedFileIds) {
            mongoFileService.popFileFromFolder(folderId, deletedFileId);
        }
        // 增加文件
        if (CollectionUtils.isNotEmpty(addedFileIds)) {
            mongoFileService.pushFilesToFolder(folderId, addedFileIds, fieldName);
        }
    }

    /**
     * @param fieldName
     * @param formData
     */
    @SuppressWarnings("unchecked")
    private List<String> getFileIds(String fieldName, FormData formData) {
        Object fieldValue = formData.getDataMap().get(fieldName);
        if (fieldValue == null) {
            return Collections.emptyList();
        }
        List<Object> files = (List<Object>) fieldValue;
        if (CollectionUtils.isEmpty(files)) {
            return Collections.emptyList();
        }
        List<String> fileIds = new ArrayList<String>();
        for (Object fileObject : files) {
            if (fileObject instanceof LogicFileInfo) {
                LogicFileInfo file = (LogicFileInfo) fileObject;
                fileIds.add(file.getFileID());
            } else {
                Map<String, String> fileInfo = (Map<String, String>) fileObject;
                fileIds.add(fileInfo.get("fileID"));
            }
        }
        return fileIds;
    }

    /**
     * @param tableName
     * @param formData
     * @param repositoryContext
     */
    private void updateFormData(String tableName, FormData formData, FormRepositoryContext repositoryContext) {
        Set<String> updateFields = userTableMetadataService.filterTableFields(formData.getUpdatedFields(), tableName);
        if (CollectionUtils.isEmpty(updateFields)) {
            return;
        }
        Map<String, Object> dataMap = formData.getDataMap();
        Map<String, TypedValue> paramValues = Maps.newHashMap();
        List<String> fieldParams = Lists.newArrayList();
        boolean needUpdate = false;
        for (String updateField : updateFields) {
            if (repositoryContext.isFileField(updateField)) {
                // 附件字段值处理
                handleFileFileValue(updateField, formData);
                continue;
            }
            ColumnMetadata columnMetadata = userTableMetadataService.getColumnMetadata(updateField, tableName);
            TypedValue expectedTypeValue = RepositoryConvertUtils.convert2ExpectedTypeValue(dataMap.get(updateField),
                    columnMetadata);
            paramValues.put(updateField, expectedTypeValue);
            fieldParams.add(updateField + "= :" + updateField);
            needUpdate = true;
        }
        // 系统更新字段
        Map<String, Object> systemColumns = userTableMetadataService.getSystemColumns(tableName,
                EnumSystemField.modifier.name(), EnumSystemField.modify_time.name());
        for (Entry<String, Object> entry : systemColumns.entrySet()) {
            String updateField = entry.getKey();
            ColumnMetadata columnMetadata = userTableMetadataService.getColumnMetadata(updateField, tableName);
            TypedValue expectedTypeValue = RepositoryConvertUtils.convert2ExpectedTypeValue(entry.getValue(),
                    columnMetadata);
            paramValues.put(updateField, expectedTypeValue);
            fieldParams.add(updateField + "= :" + updateField);
            needUpdate = true;
        }

        // 不需要更新列值，直接返回
        if (Boolean.FALSE.equals(needUpdate)) {
            return;
        }
        StringBuilder sb = new StringBuilder("update ");
        sb.append(tableName + " set " + StringUtils.join(fieldParams, Separator.COMMA.getValue()) + " ");
        sb.append("where uuid = :uuid");
        paramValues.put(FormData.KEY_UUID, new TypedValue(StandardBasicTypes.STRING, formData.getDataUuid()));
        userTableFormDataService.executeUpdate(sb.toString(), paramValues);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#query(com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @SuppressWarnings("unchecked")
    @Override
    public FormDataQueryData query(FormDataQueryInfo queryInfo, FormRepositoryContext repositoryContext) {
        FormDataQueryData formDataQueryData = userTableFormDataService.query(new UserTableFormDataQueryInfo(
                repositoryContext.getUserTableName(), queryInfo));
        List<?> dataList = formDataQueryData.getDataList();
        // 装饰数据
        decorateResultData((List<Map<String, Object>>) dataList, repositoryContext);
        return formDataQueryData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#count(com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public long count(FormDataQueryInfo queryInfo, FormRepositoryContext repositoryContext) {
        return userTableFormDataService.count(new UserTableFormDataQueryInfo(repositoryContext.getUserTableName(),
                queryInfo));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#deleteFormData(java.lang.String, java.lang.String, boolean, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public void deleteFormData(String formId, String dataUuid, boolean cascade, FormRepositoryContext repositoryContext) {
        // 清空附件
        mongoFileService.popAllFilesFromFolder(dataUuid);

        // 删除关系表数据
        String relationTableName = repositoryContext.getRelationTableName();
        StringBuilder sb = new StringBuilder("delete from ");
        sb.append(relationTableName).append(" ");
        sb.append("where ");
        sb.append(EnumRelationTblSystemField.data_uuid.name());
        sb.append("=:dataUuid");
        Map<String, TypedValue> values = Maps.newHashMap();
        values.put("dataUuid", new TypedValue(StandardBasicTypes.STRING, dataUuid));
        userTableFormDataService.executeUpdate(sb.toString(), values);

        // 删除主表数据
        String userTableName = repositoryContext.getUserTableName();
        sb = new StringBuilder("delete from ");
        sb.append(userTableName).append(" ");
        sb.append("where ");
        sb.append(EnumRelationTblSystemField.uuid.name());
        sb.append("=:dataUuid");
        userTableFormDataService.executeUpdate(sb.toString(), values);

        // 级联删除从表数据
        if (cascade) {
            deleteSubformData(formId, dataUuid, repositoryContext);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#deleteSubformData(java.lang.String, java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public void deleteSubformData(String mainformId, String mainformDataUuid, FormRepositoryContext repositoryContext) {
        // 获取从表信息
        List<String> subformUuids = repositoryContext.getSubformUuids();
        if (CollectionUtils.isEmpty(subformUuids)) {
            return;
        }

        // 删除从表数据
        for (String subformUuid : subformUuids) {
            String subformId = repositoryContext.getFormId(subformUuid);
            deleteSubformData(mainformId, mainformDataUuid, subformId, repositoryContext);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.AbstractFormRepository#deleteSubformData(java.lang.String, java.lang.String, java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public void deleteSubformData(String mainformId, String mainformDataUuid, String subformId,
                                  FormRepositoryContext repositoryContext) {
        FormRepositoryContext subFormRepositoryContext = FormRepositoryContextFactory.getByFormId(subformId);
        // 获取从表数据
        List<Map<String, Object>> subformDatas = getDataOfSubform(subformId, mainformId, mainformDataUuid,
                subFormRepositoryContext);
        // 删除从表数据
        for (Map<String, Object> map : subformDatas) {
            String subformDataUuid = ObjectUtils.toString(map.get(EnumSystemField.uuid.name()));
            deleteFormData(subformId, subformDataUuid, false, subFormRepositoryContext);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.FormRepository#getFormFields(java.lang.String, com.wellsoft.pt.dyform.implement.repository.FormRepositoryContext)
     */
    @Override
    public List<FormField> getFormFields(String formId, FormRepositoryContext repositoryContext) {
        List<ColumnMetadata> columnMetadatas = userTableMetadataService.getColumnMetadatas(repositoryContext
                .getUserTableName());
        List<FormField> formFields = Lists.newArrayList();
        for (ColumnMetadata columnMetadata : columnMetadatas) {
            FormField formField = new FormField();
            formField.setName(columnMetadata.getName());
            formField.setLabel(columnMetadata.getName());
            formField.setType(columnMetadata.getDataType());
            formFields.add(formField);
        }
        return formFields;
    }

}
