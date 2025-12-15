/*
 * @(#)2016-07-24 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.ConfigurableIdEntity;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

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
@Table(name = "APP_PRODUCT")
@DynamicUpdate
@DynamicInsert
public class AppProduct extends TenantEntity implements ConfigurableIdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1469340749117L;

    @NotBlank
    // 名称
    private String name;

    // ID
    @NotBlank
    private String id;

    // 编号
    private String code;

    // 备注
    private String remark;

    private String versionName;

    private Long categoryUuid;

    private Status status;

    private String icon;


    /**
     * @return the name
     */
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the id
     */
    @Column(name = "ID")
    public String getId() {
        return this.id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the code
     */
    @Column(name = "CODE")
    public String getCode() {
        return this.code;
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the remark
     */
    @Column(name = "REMARK")
    public String getRemark() {
        return this.remark;
    }

    /**
     * @param remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }


    public Long getCategoryUuid() {
        return categoryUuid;
    }

    public void setCategoryUuid(Long categoryUuid) {
        this.categoryUuid = categoryUuid;
    }


    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public static enum Status {
        BUILDING, LAUNCH, NOT_LAUNCH
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
