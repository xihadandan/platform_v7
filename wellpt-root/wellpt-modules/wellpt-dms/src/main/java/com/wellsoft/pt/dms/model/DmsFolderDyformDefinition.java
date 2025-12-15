/*
 * @(#)Jan 23, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.model;

import com.wellsoft.context.base.BaseObject;

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
public class DmsFolderDyformDefinition extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7943009433569115443L;

    private String dataType;
    private String formName;
    private String formUuid;
    private String displayFormName;
    private String displayFormUuid;
    private String displayMFormUuid;
    private String displayMFormName;
    private String dataStoreName;
    private String dataStoreId;
    private boolean stick;
    private String stickStatusField;
    private String stickTimeField;
    private boolean readRecord;
    private String readRecordField;
    private String fileNameField;
    private String fileStatusField;
    private String readFileField;
    private String editFileField;

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
     * @return the dataStoreName
     */
    public String getDataStoreName() {
        return dataStoreName;
    }

    /**
     * @param dataStoreName 要设置的dataStoreName
     */
    public void setDataStoreName(String dataStoreName) {
        this.dataStoreName = dataStoreName;
    }

    /**
     * @return the dataStoreId
     */
    public String getDataStoreId() {
        return dataStoreId;
    }

    /**
     * @param dataStoreId 要设置的dataStoreId
     */
    public void setDataStoreId(String dataStoreId) {
        this.dataStoreId = dataStoreId;
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
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((formUuid == null) ? 0 : formUuid.hashCode());
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DmsFolderDyformDefinition other = (DmsFolderDyformDefinition) obj;
        if (formUuid == null) {
            if (other.formUuid != null)
                return false;
        } else if (!formUuid.equals(other.formUuid))
            return false;
        return true;
    }

}
