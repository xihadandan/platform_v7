/*
 * @(#)Jan 23, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.dms.bean.DmsFolderAssignRoleBean;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.util.Collections;
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
 * Jan 23, 2018.1	zhulh		Jan 23, 2018		Create
 * </pre>
 * @date Jan 23, 2018
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DmsFolderConfiguration extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6477029684032436584L;

    // 夹UUID
    private String folderUuid;
    // 数据类型
    private String dataType;
    // 数据类型
    private List<String> dataTypes = Lists.newArrayList();
    // 文件类型模式
    private String fileAcceptMode;
    // 文件类型
    private List<String> fileAccept = Lists.newArrayList();
    // 文件水印配置
    private Map<String, Object> fileWatermarkConfig = Maps.newHashMapWithExpectedSize(0);
    // 表单定义UUID
    private String formUuid;
    // 表单定义名称
    private String formName;
    // 展示表单定义UUID
    private String displayFormUuid;
    // 展示表单定义名称
    private String displayFormName;
    // 展示手机表单定义UUID
    private String displayMFormUuid;
    // 展示手机表单定义名称
    private String displayMFormName;
    // 展示视图ID
    private String listViewId;
    // 展示视图名称
    private String listViewName;
    // 打印模板ID
    private String printTemplateUuids;
    // 打印模板名称
    private String printTemplateNames;
    // 文档标题字段
    private String fileNameField;
    // 文档标题字段名称
    private String fileNameFieldName;
    // 文档状态字段
    private String fileStatusField;
    // 文档状态字段名称
    private String fileStatusFieldName;
    // 阅读人员字段
    private String readFileField;
    // 阅读人员字段名称
    private String readFileFieldName;
    // 编辑人员字段
    private String editFileField;
    // 编辑人员字段名称
    private String editFileFieldName;
    // 置顶状态
    private boolean stick;
    // 置顶状态字段
    private String stickStatusField;
    // 置顶状态字段名称
    private String stickStatusFieldName;
    // 置顶时间字段
    private String stickTimeField;
    // 置顶时间字段名称
    private String stickTimeFieldName;
    // 启用阅读记录
    private boolean readRecord;
    // 阅读人员字段
    private String readRecordField;
    // 阅读人员字段名称
    private String readRecordFieldName;
    // 是否继承夹的权限配置，1是，0否
    private Integer isInheritFolderRole;
    // 是否继承夹的消息通知配置，1是，0否
    private Integer isEnableMessageNotice;
    // 是否启用全文索引
    private Boolean enabledFulltextIndex;
    // 应用ID
    private String appId;
    // 分配的权限
    private List<DmsFolderAssignRoleBean> assignRoles = Collections.emptyList();

    // 启用版本化
    private Boolean enabledVersion;
    // 启用版本化审批
    private Boolean enabledVersionApprove;
    // 送审批流程ID
    private String approveFlowDefIds;
    // 送审批流程名称
    private String approveFlowDefNames;

    // 管理者
    private String administrator;
    // 管理者操作权限
    private List<String> administratorActions;
    // 编辑者
    private String editor;
    // 编辑者操作权限
    private List<String> editorActions;
    // 阅读者
    private String reader;
    // 阅读者操作权限
    private List<String> readerActions;
    // 库存配置
    private LibraryStorage storage = new LibraryStorage();
    // 消息通知配置
    private LibraryMessage message = new LibraryMessage();

    public String getPrintTemplateUuids() {
        return this.printTemplateUuids;
    }

    public void setPrintTemplateUuids(final String printTemplateUuids) {
        this.printTemplateUuids = printTemplateUuids;
    }

    public String getPrintTemplateNames() {
        return this.printTemplateNames;
    }

    public void setPrintTemplateNames(final String printTemplateNames) {
        this.printTemplateNames = printTemplateNames;
    }

    /**
     * @return the folderUuid
     */
    public String getFolderUuid() {
        return folderUuid;
    }

    /**
     * @param folderUuid 要设置的folderUuid
     */
    public void setFolderUuid(String folderUuid) {
        this.folderUuid = folderUuid;
    }

    /**
     * @return the dataType
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * @param dataType 要设置的dataType
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the dataTypes
     */
    public List<String> getDataTypes() {
        return dataTypes;
    }

    /**
     * @param dataTypes 要设置的dataTypes
     */
    public void setDataTypes(List<String> dataTypes) {
        this.dataTypes = dataTypes;
    }

    /**
     * @return the fileAcceptMode
     */
    public String getFileAcceptMode() {
        return fileAcceptMode;
    }

    /**
     * @param fileAcceptMode 要设置的fileAcceptMode
     */
    public void setFileAcceptMode(String fileAcceptMode) {
        this.fileAcceptMode = fileAcceptMode;
    }

    /**
     * @return the fileAccept
     */
    public List<String> getFileAccept() {
        return fileAccept;
    }

    /**
     * @param fileAccept 要设置的fileAccept
     */
    public void setFileAccept(List<String> fileAccept) {
        this.fileAccept = fileAccept;
    }

    /**
     * @return the fileWatermarkConfig
     */
    public Map<String, Object> getFileWatermarkConfig() {
        return fileWatermarkConfig;
    }

    /**
     * @param fileWatermarkConfig 要设置的fileWatermarkConfig
     */
    public void setFileWatermarkConfig(Map<String, Object> fileWatermarkConfig) {
        this.fileWatermarkConfig = fileWatermarkConfig;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the formName
     */
    public String getFormName() {
        return formName;
    }

    /**
     * @param formName 要设置的formName
     */
    public void setFormName(String formName) {
        this.formName = formName;
    }

    /**
     * @return the displayFormUuid
     */
    public String getDisplayFormUuid() {
        return displayFormUuid;
    }

    /**
     * @param displayFormUuid 要设置的displayFormUuid
     */
    public void setDisplayFormUuid(String displayFormUuid) {
        this.displayFormUuid = displayFormUuid;
    }

    /**
     * @return the displayFormName
     */
    public String getDisplayFormName() {
        return displayFormName;
    }

    /**
     * @param displayFormName 要设置的displayFormName
     */
    public void setDisplayFormName(String displayFormName) {
        this.displayFormName = displayFormName;
    }

    /**
     * @return the listViewId
     */
    public String getListViewId() {
        return listViewId;
    }

    /**
     * @param listViewId 要设置的listViewId
     */
    public void setListViewId(String listViewId) {
        this.listViewId = listViewId;
    }

    /**
     * @return the listViewName
     */
    public String getListViewName() {
        return listViewName;
    }

    /**
     * @param listViewName 要设置的listViewName
     */
    public void setListViewName(String listViewName) {
        this.listViewName = listViewName;
    }

    /**
     * @return the fileNameField
     */
    public String getFileNameField() {
        return fileNameField;
    }

    /**
     * @param fileNameField 要设置的fileNameField
     */
    public void setFileNameField(String fileNameField) {
        this.fileNameField = fileNameField;
    }

    /**
     * @return the fileNameFieldName
     */
    public String getFileNameFieldName() {
        return fileNameFieldName;
    }

    /**
     * @param fileNameFieldName 要设置的fileNameFieldName
     */
    public void setFileNameFieldName(String fileNameFieldName) {
        this.fileNameFieldName = fileNameFieldName;
    }

    /**
     * @return the fileStatusField
     */
    public String getFileStatusField() {
        return fileStatusField;
    }

    /**
     * @param fileStatusField 要设置的fileStatusField
     */
    public void setFileStatusField(String fileStatusField) {
        this.fileStatusField = fileStatusField;
    }

    /**
     * @return the fileStatusFieldName
     */
    public String getFileStatusFieldName() {
        return fileStatusFieldName;
    }

    /**
     * @param fileStatusFieldName 要设置的fileStatusFieldName
     */
    public void setFileStatusFieldName(String fileStatusFieldName) {
        this.fileStatusFieldName = fileStatusFieldName;
    }

    /**
     * @return the readFileField
     */
    public String getReadFileField() {
        return readFileField;
    }

    /**
     * @param readFileField 要设置的readFileField
     */
    public void setReadFileField(String readFileField) {
        this.readFileField = readFileField;
    }

    /**
     * @return the readFileFieldName
     */
    public String getReadFileFieldName() {
        return readFileFieldName;
    }

    /**
     * @param readFileFieldName 要设置的readFileFieldName
     */
    public void setReadFileFieldName(String readFileFieldName) {
        this.readFileFieldName = readFileFieldName;
    }

    /**
     * @return the editFileField
     */
    public String getEditFileField() {
        return editFileField;
    }

    /**
     * @param editFileField 要设置的editFileField
     */
    public void setEditFileField(String editFileField) {
        this.editFileField = editFileField;
    }

    /**
     * @return the editFileFieldName
     */
    public String getEditFileFieldName() {
        return editFileFieldName;
    }

    /**
     * @param editFileFieldName 要设置的editFileFieldName
     */
    public void setEditFileFieldName(String editFileFieldName) {
        this.editFileFieldName = editFileFieldName;
    }

    /**
     * @return the stick
     */
    public boolean isStick() {
        return stick;
    }

    /**
     * @param stick 要设置的stick
     */
    public void setStick(boolean stick) {
        this.stick = stick;
    }

    /**
     * @return the stickStatusField
     */
    public String getStickStatusField() {
        return stickStatusField;
    }

    /**
     * @param stickStatusField 要设置的stickStatusField
     */
    public void setStickStatusField(String stickStatusField) {
        this.stickStatusField = stickStatusField;
    }

    /**
     * @return the stickStatusFieldName
     */
    public String getStickStatusFieldName() {
        return stickStatusFieldName;
    }

    /**
     * @param stickStatusFieldName 要设置的stickStatusFieldName
     */
    public void setStickStatusFieldName(String stickStatusFieldName) {
        this.stickStatusFieldName = stickStatusFieldName;
    }

    /**
     * @return the stickTimeField
     */
    public String getStickTimeField() {
        return stickTimeField;
    }

    /**
     * @param stickTimeField 要设置的stickTimeField
     */
    public void setStickTimeField(String stickTimeField) {
        this.stickTimeField = stickTimeField;
    }

    /**
     * @return the stickTimeFieldName
     */
    public String getStickTimeFieldName() {
        return stickTimeFieldName;
    }

    /**
     * @param stickTimeFieldName 要设置的stickTimeFieldName
     */
    public void setStickTimeFieldName(String stickTimeFieldName) {
        this.stickTimeFieldName = stickTimeFieldName;
    }

    /**
     * @return the readRecord
     */
    public boolean isReadRecord() {
        return readRecord;
    }

    /**
     * @param readRecord 要设置的readRecord
     */
    public void setReadRecord(boolean readRecord) {
        this.readRecord = readRecord;
    }

    /**
     * @return the readRecordField
     */
    public String getReadRecordField() {
        return readRecordField;
    }

    /**
     * @param readRecordField 要设置的readRecordField
     */
    public void setReadRecordField(String readRecordField) {
        this.readRecordField = readRecordField;
    }

    /**
     * @return the readRecordFieldName
     */
    public String getReadRecordFieldName() {
        return readRecordFieldName;
    }

    /**
     * @param readRecordFieldName 要设置的readRecordFieldName
     */
    public void setReadRecordFieldName(String readRecordFieldName) {
        this.readRecordFieldName = readRecordFieldName;
    }

    /**
     * @return the isInheritFolderRole
     */
    public Integer getIsInheritFolderRole() {
        return isInheritFolderRole;
    }

    /**
     * @param isInheritFolderRole 要设置的isInheritFolderRole
     */
    public void setIsInheritFolderRole(Integer isInheritFolderRole) {
        this.isInheritFolderRole = isInheritFolderRole;
    }

    /**
     * @return the isEnableMessageNotice
     */
    public Integer getIsEnableMessageNotice() {
        return isEnableMessageNotice;
    }

    /**
     * @param isEnableMessageNotice 要设置的isEnableMessageNotice
     */
    public void setIsEnableMessageNotice(Integer isEnableMessageNotice) {
        this.isEnableMessageNotice = isEnableMessageNotice;
    }

    /**
     * @return the enabledFulltextIndex
     */
    public Boolean getEnabledFulltextIndex() {
        return enabledFulltextIndex;
    }

    /**
     * @param enabledFulltextIndex 要设置的enabledFulltextIndex
     */
    public void setEnabledFulltextIndex(Boolean enabledFulltextIndex) {
        this.enabledFulltextIndex = enabledFulltextIndex;
    }

    /**
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId 要设置的appId
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * @return the assignRoles
     */
    public List<DmsFolderAssignRoleBean> getAssignRoles() {
        return assignRoles;
    }

    /**
     * @param assignRoles 要设置的assignRoles
     */
    public void setAssignRoles(List<DmsFolderAssignRoleBean> assignRoles) {
        this.assignRoles = assignRoles;
    }

    /**
     * @return the enabledVersion
     */
    public Boolean getEnabledVersion() {
        return enabledVersion;
    }

    /**
     * @param enabledVersion 要设置的enabledVersion
     */
    public void setEnabledVersion(Boolean enabledVersion) {
        this.enabledVersion = enabledVersion;
    }

    /**
     * @return the enabledVersionApprove
     */
    public Boolean getEnabledVersionApprove() {
        return enabledVersionApprove;
    }

    /**
     * @param enabledVersionApprove 要设置的enabledVersionApprove
     */
    public void setEnabledVersionApprove(Boolean enabledVersionApprove) {
        this.enabledVersionApprove = enabledVersionApprove;
    }

    /**
     * @return the approveFlowDefIds
     */
    public String getApproveFlowDefIds() {
        return approveFlowDefIds;
    }

    /**
     * @param approveFlowDefIds 要设置的approveFlowDefIds
     */
    public void setApproveFlowDefIds(String approveFlowDefIds) {
        this.approveFlowDefIds = approveFlowDefIds;
    }

    /**
     * @return the approveFlowDefNames
     */
    public String getApproveFlowDefNames() {
        return approveFlowDefNames;
    }

    /**
     * @param approveFlowDefNames 要设置的approveFlowDefNames
     */
    public void setApproveFlowDefNames(String approveFlowDefNames) {
        this.approveFlowDefNames = approveFlowDefNames;
    }

    public String getDisplayMFormUuid() {
        return displayMFormUuid;
    }

    public void setDisplayMFormUuid(String displayMFormUuid) {
        this.displayMFormUuid = displayMFormUuid;
    }

    public String getDisplayMFormName() {
        return displayMFormName;
    }

    public void setDisplayMFormName(String displayMFormName) {
        this.displayMFormName = displayMFormName;
    }

    /**
     * @return the administrator
     */
    public String getAdministrator() {
        return administrator;
    }

    /**
     * @param administrator 要设置的administrator
     */
    public void setAdministrator(String administrator) {
        this.administrator = administrator;
    }

    /**
     * @return the administratorActions
     */
    public List<String> getAdministratorActions() {
        return administratorActions;
    }

    /**
     * @param administratorActions 要设置的administratorActions
     */
    public void setAdministratorActions(List<String> administratorActions) {
        this.administratorActions = administratorActions;
    }

    /**
     * @return the editor
     */
    public String getEditor() {
        return editor;
    }

    /**
     * @param editor 要设置的editor
     */
    public void setEditor(String editor) {
        this.editor = editor;
    }

    /**
     * @return the editorActions
     */
    public List<String> getEditorActions() {
        return editorActions;
    }

    /**
     * @param editorActions 要设置的editorActions
     */
    public void setEditorActions(List<String> editorActions) {
        this.editorActions = editorActions;
    }

    /**
     * @return the reader
     */
    public String getReader() {
        return reader;
    }

    /**
     * @param reader 要设置的reader
     */
    public void setReader(String reader) {
        this.reader = reader;
    }

    /**
     * @return the readerActions
     */
    public List<String> getReaderActions() {
        return readerActions;
    }

    /**
     * @param readerActions 要设置的readerActions
     */
    public void setReaderActions(List<String> readerActions) {
        this.readerActions = readerActions;
    }

    /**
     * @return the storage
     */
    public LibraryStorage getStorage() {
        return storage;
    }

    /**
     * @param storage 要设置的storage
     */
    public void setStorage(LibraryStorage storage) {
        this.storage = storage;
    }

    /**
     * @return the message
     */
    public LibraryMessage getMessage() {
        return message;
    }

    /**
     * @param message 要设置的message
     */
    public void setMessage(LibraryMessage message) {
        this.message = message;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LibraryStorage extends BaseObject {

        private static final long serialVersionUID = 269728178633633575L;
        private String quota;
        private Integer limitSize;
        private String limitUnit;
        private String fileQuota;
        private Integer fileSizeLimit;
        private String fileSizeLimitUnit;
        private String recycleBinRetention;
        private Integer recycleBinRetentionTimeLimit;
        private String recycleBinRetentionTimeUnit;

        /**
         * @return the quota
         */
        public String getQuota() {
            return quota;
        }

        /**
         * @param quota 要设置的quota
         */
        public void setQuota(String quota) {
            this.quota = quota;
        }

        /**
         * @return the limitSize
         */
        public Integer getLimitSize() {
            return limitSize;
        }

        /**
         * @param limitSize 要设置的limitSize
         */
        public void setLimitSize(Integer limitSize) {
            this.limitSize = limitSize;
        }

        /**
         * @return the limitUnit
         */
        public String getLimitUnit() {
            return limitUnit;
        }

        /**
         * @param limitUnit 要设置的limitUnit
         */
        public void setLimitUnit(String limitUnit) {
            this.limitUnit = limitUnit;
        }

        /**
         * @return the fileQuota
         */
        public String getFileQuota() {
            return fileQuota;
        }

        /**
         * @param fileQuota 要设置的fileQuota
         */
        public void setFileQuota(String fileQuota) {
            this.fileQuota = fileQuota;
        }

        /**
         * @return the fileSizeLimit
         */
        public Integer getFileSizeLimit() {
            return fileSizeLimit;
        }

        /**
         * @param fileSizeLimit 要设置的fileSizeLimit
         */
        public void setFileSizeLimit(Integer fileSizeLimit) {
            this.fileSizeLimit = fileSizeLimit;
        }

        /**
         * @return the fileSizeLimitUnit
         */
        public String getFileSizeLimitUnit() {
            return fileSizeLimitUnit;
        }

        /**
         * @param fileSizeLimitUnit 要设置的fileSizeLimitUnit
         */
        public void setFileSizeLimitUnit(String fileSizeLimitUnit) {
            this.fileSizeLimitUnit = fileSizeLimitUnit;
        }

        /**
         * @return the recycleBinRetention
         */
        public String getRecycleBinRetention() {
            return recycleBinRetention;
        }

        /**
         * @param recycleBinRetention 要设置的recycleBinRetention
         */
        public void setRecycleBinRetention(String recycleBinRetention) {
            this.recycleBinRetention = recycleBinRetention;
        }

        /**
         * @return the recycleBinRetentionTimeLimit
         */
        public Integer getRecycleBinRetentionTimeLimit() {
            return recycleBinRetentionTimeLimit;
        }

        /**
         * @param recycleBinRetentionTimeLimit 要设置的recycleBinRetentionTimeLimit
         */
        public void setRecycleBinRetentionTimeLimit(Integer recycleBinRetentionTimeLimit) {
            this.recycleBinRetentionTimeLimit = recycleBinRetentionTimeLimit;
        }

        /**
         * @return the recycleBinRetentionTimeUnit
         */
        public String getRecycleBinRetentionTimeUnit() {
            return recycleBinRetentionTimeUnit;
        }

        /**
         * @param recycleBinRetentionTimeUnit 要设置的recycleBinRetentionTimeUnit
         */
        public void setRecycleBinRetentionTimeUnit(String recycleBinRetentionTimeUnit) {
            this.recycleBinRetentionTimeUnit = recycleBinRetentionTimeUnit;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LibraryMessage extends BaseObject {

        private static final long serialVersionUID = -552318213803533403L;

        private Boolean enabledFileCreated;
        private Boolean enabledFileUpdated;
        private Boolean enabledFileDeleted;
        private String fileCreatedRecipient;
        private String fileUpdatedRecipient;
        private String fileDeletedRecipient;

        /**
         * @return the enabledFileCreated
         */
        public Boolean getEnabledFileCreated() {
            return enabledFileCreated;
        }

        /**
         * @param enabledFileCreated 要设置的enabledFileCreated
         */
        public void setEnabledFileCreated(Boolean enabledFileCreated) {
            this.enabledFileCreated = enabledFileCreated;
        }

        /**
         * @return the enabledFileUpdated
         */
        public Boolean getEnabledFileUpdated() {
            return enabledFileUpdated;
        }

        /**
         * @param enabledFileUpdated 要设置的enabledFileUpdated
         */
        public void setEnabledFileUpdated(Boolean enabledFileUpdated) {
            this.enabledFileUpdated = enabledFileUpdated;
        }

        /**
         * @return the enabledFileDeleted
         */
        public Boolean getEnabledFileDeleted() {
            return enabledFileDeleted;
        }

        /**
         * @param enabledFileDeleted 要设置的enabledFileDeleted
         */
        public void setEnabledFileDeleted(Boolean enabledFileDeleted) {
            this.enabledFileDeleted = enabledFileDeleted;
        }

        /**
         * @return the fileCreatedRecipient
         */
        public String getFileCreatedRecipient() {
            return fileCreatedRecipient;
        }

        /**
         * @param fileCreatedRecipient 要设置的fileCreatedRecipient
         */
        public void setFileCreatedRecipient(String fileCreatedRecipient) {
            this.fileCreatedRecipient = fileCreatedRecipient;
        }

        /**
         * @return the fileUpdatedRecipient
         */
        public String getFileUpdatedRecipient() {
            return fileUpdatedRecipient;
        }

        /**
         * @param fileUpdatedRecipient 要设置的fileUpdatedRecipient
         */
        public void setFileUpdatedRecipient(String fileUpdatedRecipient) {
            this.fileUpdatedRecipient = fileUpdatedRecipient;
        }

        /**
         * @return the fileDeletedRecipient
         */
        public String getFileDeletedRecipient() {
            return fileDeletedRecipient;
        }

        /**
         * @param fileDeletedRecipient 要设置的fileDeletedRecipient
         */
        public void setFileDeletedRecipient(String fileDeletedRecipient) {
            this.fileDeletedRecipient = fileDeletedRecipient;
        }
    }

}
