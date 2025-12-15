/*
 * @(#)2016-07-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.jpa.service.UUIDGeneratorIndicate;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-07-24.1	zhulh		2016-07-24		Create
 * </pre>
 * @date 2016-07-24
 */
@Entity
@Table(name = "APP_PRODUCT_INTEGRATION")
@DynamicUpdate
@DynamicInsert
public class AppProductIntegration extends IdEntity implements UUIDGeneratorIndicate {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1469350352591L;

    // 产品UUID
    private String appProductUuid;
    // 系统UUID
    private String appSystemUuid;
    // 页面UUID
    private String appPageUuid;
    // 是否页面引用
    private Boolean appPageReference;
    // 数据UUID
    private String dataUuid;
    // 数据名称
    private String dataName;
    // 数据ID
    private String dataId;
    // 数据类型，1(系统)、2(模块)、3(应用)、4(功能)
    private String dataType;
    // 数据集成信息路径——/系统/模块|子模块/应用等
    private String dataPath;
    // 功能资源是否受保护
    private Boolean isProtected;
    // 排序号
    private Integer sortOrder;
    // 上级数据UUID
    private String parentUuid;

    /**
     * @return the appProductUuid
     */
    @Column(name = "APP_PRODUCT_UUID")
    public String getAppProductUuid() {
        return this.appProductUuid;
    }

    /**
     * @param appProductUuid
     */
    public void setAppProductUuid(String appProductUuid) {
        this.appProductUuid = appProductUuid;
    }

    /**
     * @return the appSystemUuid
     */
    @Column(name = "APP_SYSTEM_UUID")
    public String getAppSystemUuid() {
        return appSystemUuid;
    }

    /**
     * @param appSystemUuid 要设置的appSystemUuid
     */
    public void setAppSystemUuid(String appSystemUuid) {
        this.appSystemUuid = appSystemUuid;
    }

    /**
     * @return the appPageUuid
     */
    @Column(name = "APP_PAGE_UUID")
    public String getAppPageUuid() {
        return appPageUuid;
    }

    /**
     * @param appPageUuid 要设置的appPageUuid
     */
    public void setAppPageUuid(String appPageUuid) {
        this.appPageUuid = appPageUuid;
    }

    /**
     * @return the appPageReference
     */
    @Column(name = "APP_PAGE_REFERENCE")
    public Boolean getAppPageReference() {
        return appPageReference;
    }

    /**
     * @param appPageReference 要设置的appPageReference
     */
    public void setAppPageReference(Boolean appPageReference) {
        this.appPageReference = appPageReference;
    }

    /**
     * @return the dataUuid
     */
    @Column(name = "DATA_UUID")
    public String getDataUuid() {
        return this.dataUuid;
    }

    /**
     * @param dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }

    /**
     * @return the dataName
     */
    @Column(name = "DATA_NAME")
    public String getDataName() {
        return this.dataName;
    }

    /**
     * @param dataName
     */
    public void setDataName(String dataName) {
        this.dataName = dataName;
    }

    /**
     * @return the dataId
     */
    @Column(name = "DATA_ID")
    public String getDataId() {
        return this.dataId;
    }

    /**
     * @param dataId
     */
    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    /**
     * @return the dataType
     */
    @Column(name = "DATA_TYPE")
    public String getDataType() {
        return this.dataType;
    }

    /**
     * @param dataType
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the dataPath
     */
    @Column(name = "DATA_PATH")
    public String getDataPath() {
        return dataPath;
    }

    /**
     * @param dataPath 要设置的dataPath
     */
    public void setDataPath(String dataPath) {
        this.dataPath = dataPath;
    }

    /**
     * @return the isProtected
     */
    public Boolean getIsProtected() {
        return isProtected;
    }

    /**
     * @param isProtected 要设置的isProtected
     */
    public void setIsProtected(Boolean isProtected) {
        this.isProtected = isProtected;
    }

    /**
     * @return the sortOrder
     */
    @Column(name = "SORT_ORDER")
    public Integer getSortOrder() {
        return this.sortOrder;
    }

    /**
     * @param sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * @return the parentUuid
     */
    @Column(name = "PARENT_UUID")
    public String getParentUuid() {
        return this.parentUuid;
    }

    /**
     * @param parentUuid
     */
    public void setParentUuid(String parentUuid) {
        this.parentUuid = parentUuid;
    }

}
