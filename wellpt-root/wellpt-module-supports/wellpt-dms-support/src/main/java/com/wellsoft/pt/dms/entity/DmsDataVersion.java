/*
 * @(#)2017年4月26日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Description: 数据版本
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年4月26日.1	zhulh		2017年4月26日		Create
 * </pre>
 * @date 2017年4月26日
 */
@Entity
@Table(name = "DMS_DATA_VERSION")
@DynamicUpdate
@DynamicInsert
public class DmsDataVersion extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4317445795112477931L;

    // 版本数据类型
    private String dataType;

    // 初始化版本的定义UUID
    private String initDefUuid;

    // 初始化版本的定义ID
    private String initDefId;

    // 初始化版本的数据UUID
    private String initDataUuid;

    // 原版本数据定义UUID
    private String sourceDefUuid;
    // 原版本数据定义ID
    private String sourceDefId;
    // 原版本版本数据UUID
    private String sourceDataUuid;

    // 当前版本数据标题
    private String title;
    // 当前版本数据定义UUID
    private String dataDefUuid;
    // 当前版本数据定义ID
    private String dataDefId;
    // 当前版本数据UUID
    private String dataUuid;

    // 当前版本号
    private String version;

    // 备注
    private String remark;

    // 是否最新的版本
    private Boolean isLatestVersion;

    private String creatorName;

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
     * @return the initDefUuid
     */
    public String getInitDefUuid() {
        return initDefUuid;
    }

    /**
     * @param initDefUuid 要设置的initDefUuid
     */
    public void setInitDefUuid(String initDefUuid) {
        this.initDefUuid = initDefUuid;
    }

    /**
     * @return the initDefId
     */
    public String getInitDefId() {
        return initDefId;
    }

    /**
     * @param initDefId 要设置的initDefId
     */
    public void setInitDefId(String initDefId) {
        this.initDefId = initDefId;
    }

    /**
     * @return the initDataUuid
     */
    public String getInitDataUuid() {
        return initDataUuid;
    }

    /**
     * @param initDataUuid 要设置的initDataUuid
     */
    public void setInitDataUuid(String initDataUuid) {
        this.initDataUuid = initDataUuid;
    }

    /**
     * @return the sourceDefUuid
     */
    public String getSourceDefUuid() {
        return sourceDefUuid;
    }

    /**
     * @param sourceDefUuid 要设置的sourceDefUuid
     */
    public void setSourceDefUuid(String sourceDefUuid) {
        this.sourceDefUuid = sourceDefUuid;
    }

    /**
     * @return the sourceDefId
     */
    public String getSourceDefId() {
        return sourceDefId;
    }

    /**
     * @param sourceDefId 要设置的sourceDefId
     */
    public void setSourceDefId(String sourceDefId) {
        this.sourceDefId = sourceDefId;
    }

    /**
     * @return the sourceDataUuid
     */
    public String getSourceDataUuid() {
        return sourceDataUuid;
    }

    /**
     * @param sourceDataUuid 要设置的sourceDataUuid
     */
    public void setSourceDataUuid(String sourceDataUuid) {
        this.sourceDataUuid = sourceDataUuid;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title 要设置的title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the dataDefUuid
     */
    public String getDataDefUuid() {
        return dataDefUuid;
    }

    /**
     * @param dataDefUuid 要设置的dataDefUuid
     */
    public void setDataDefUuid(String dataDefUuid) {
        this.dataDefUuid = dataDefUuid;
    }

    /**
     * @return the dataDefId
     */
    public String getDataDefId() {
        return dataDefId;
    }

    /**
     * @param dataDefId 要设置的dataDefId
     */
    public void setDataDefId(String dataDefId) {
        this.dataDefId = dataDefId;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version 要设置的version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the isLatestVersion
     */
    public Boolean getIsLatestVersion() {
        return isLatestVersion;
    }

    /**
     * @param isLatestVersion 要设置的isLatestVersion
     */
    public void setIsLatestVersion(Boolean isLatestVersion) {
        this.isLatestVersion = isLatestVersion;
    }

    @Transient
    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}
