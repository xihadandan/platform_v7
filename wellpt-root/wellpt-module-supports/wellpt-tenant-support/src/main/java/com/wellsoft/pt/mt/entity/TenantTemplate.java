/*
 * @(#)2016-03-01 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-03-01.1	linz		2016-03-01		Create
 * </pre>
 * @date 2016-03-01
 */
@Entity
@CommonEntity
@Table(name = "MT_TENANT_TEMPLATE")
@DynamicUpdate
@DynamicInsert
public class TenantTemplate extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1456803451258L;

    // NAME名称
    private String name;
    // DDL_FILE_NAMESddl文件名集合
    private String ddlFileNames;
    // DDL_FILE_UUIDS
    private String ddlFileUuids;
    // DML_FILE_NAMESdml文件名集合
    private String dmlFileNames;
    // DML_FILE_UUIDS
    private String dmlFileUuids;
    // REMARK备注
    private String remark;
    @UnCloneable
    @JsonIgnore
    private Set<TenantTemplateModule> tenantTemplateModules;

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
    public String setName(String name) {
        return this.name = name;
    }

    /**
     * @return the ddlFileNames
     */
    @Column(name = "DDL_FILE_NAMES")
    public String getDdlFileNames() {
        return this.ddlFileNames;
    }

    @OneToMany(mappedBy = "tenantTemplate", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    @Fetch(FetchMode.SUBSELECT)
    public Set<TenantTemplateModule> getTenantTemplateModules() {
        return tenantTemplateModules;
    }

    public void setTenantTemplateModules(Set<TenantTemplateModule> tenantTemplateModules) {
        this.tenantTemplateModules = tenantTemplateModules;
    }

    /**
     * @param ddlFileNames
     */
    public String setDdlFileNames(String ddlFileNames) {
        return this.ddlFileNames = ddlFileNames;
    }

    /**
     * @return the ddlFileUuids
     */
    @Column(name = "DDL_FILE_UUIDS")
    public String getDdlFileUuids() {
        return this.ddlFileUuids;
    }

    /**
     * @param ddlFileUuids
     */
    public String setDdlFileUuids(String ddlFileUuids) {
        return this.ddlFileUuids = ddlFileUuids;
    }

    /**
     * @return the dmlFileNames
     */
    @Column(name = "DML_FILE_NAMES")
    public String getDmlFileNames() {
        return this.dmlFileNames;
    }

    /**
     * @param dmlFileNames
     */
    public String setDmlFileNames(String dmlFileNames) {
        return this.dmlFileNames = dmlFileNames;
    }

    /**
     * @return the dmlFileUuids
     */
    @Column(name = "DML_FILE_UUIDS")
    public String getDmlFileUuids() {
        return this.dmlFileUuids;
    }

    /**
     * @param dmlFileUuids
     */
    public String setDmlFileUuids(String dmlFileUuids) {
        return this.dmlFileUuids = dmlFileUuids;
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
    public String setRemark(String remark) {
        return this.remark = remark;
    }

}
