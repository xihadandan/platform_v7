/*
 * @(#)2013-1-14 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.MaxLength;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * Description: 组织选择框实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	zhulh		2013-1-14		Create
 * </pre>
 * @date 2013-1-14
 */
//@Entity
//@Table(name = "org_option")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "entity")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class Option extends IdEntity {

    private static final long serialVersionUID = 2948153327347510680L;

    // 名称
    @NotBlank
    private String name;
    // 编号
    @NotBlank
    private String code;
    // ID
    @NotBlank
    private String id;
    // 使用人ID(用户、部门、群组)
    @MaxLength(max = Integer.MAX_VALUE)
    private String owner;
    // 使用人名称(用户、部门、群组)
    @MaxLength(max = Integer.MAX_VALUE)
    private String ownerName;
    // 是否默认显示，如果有设置使用人则无效
    private Boolean show;
    // 备注
    @MaxLength(max = Integer.MAX_VALUE)
    private String remark;
    private String tenantId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the owner
     */
    @Column(length = 4000)
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner 要设置的owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return the ownerName
     */
    @Column(length = 4000)
    public String getOwnerName() {
        return ownerName;
    }

    /**
     * @param ownerName 要设置的ownerName
     */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /**
     * @return the code
     */
    @OrderBy(value = "code asc")
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the show
     */
    public Boolean getShow() {
        return show;
    }

    /**
     * @param show 要设置的show
     */
    public void setShow(Boolean show) {
        this.show = show;
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

}
