/*
 * @(#)Jan 22, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.config.support;

import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Jan 22, 2018.1	zhulh		Jan 22, 2018		Create
 * </pre>
 * @date Jan 22, 2018
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Store extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 5726055066266720644L;

    private String configUuid;
    private String dataType;
    private String formName;
    private String formUuid;
    private boolean useDyFormLatestVersion;
    private boolean useCForm;
    private String displayFormName;
    private String displayFormUuid;
    private String dataStoreName;
    private String dataStoreId;
    private boolean stick;
    private String stickStatusField;
    private String stickTimeField;
    private boolean readRecord;
    private String readRecordField;

    public String getConfigUuid() {
        return configUuid;
    }

    public void setConfigUuid(String configUuid) {
        this.configUuid = configUuid;
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

    public boolean getUseDyFormLatestVersion() {
        return useDyFormLatestVersion;
    }

    public void setUseDyFormLatestVersion(boolean useDyFormLatestVersion) {
        this.useDyFormLatestVersion = useDyFormLatestVersion;
    }

    public boolean isUseCForm() {
        return useCForm;
    }

    public void setUseCForm(boolean useCForm) {
        this.useCForm = useCForm;
    }

}
