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
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

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
@Table(name = "MT_TENANT_TEMPLATE_MODULE")
@DynamicUpdate
@CommonEntity
@DynamicInsert
public class TenantTemplateModule extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1456814032076L;

    // NAME 名称
    private String name;
    // SORT_ORDER 排序号
    private Integer sortOrder;
    // REPO_FILE_NAMES 模板文件名称
    private String repoFileNames;
    // REPO_FILE_UUIDS
    private String repoFileUuids;

    @UnCloneable
    @JsonIgnore
    private TenantTemplate tenantTemplate;
    // REMARK
    private String remark;

    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.REFRESH})
    @JoinColumn(name = "template_uuid")
    public TenantTemplate getTenantTemplate() {
        return tenantTemplate;
    }

    public void setTenantTemplate(TenantTemplate tenantTemplate) {
        this.tenantTemplate = tenantTemplate;
    }

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
     * @return the sortOrder
     */
    @Column(name = "SORT_ORDER")
    public Integer getSortOrder() {
        return this.sortOrder;
    }

    /**
     * @param sortOrder
     */
    public Integer setSortOrder(Integer sortOrder) {
        return this.sortOrder = sortOrder;
    }

    /**
     * @return the repoFileNames
     */
    @Column(name = "REPO_FILE_NAMES")
    public String getRepoFileNames() {
        return this.repoFileNames;
    }

    /**
     * @param repoFileNames
     */
    public String setRepoFileNames(String repoFileNames) {
        return this.repoFileNames = repoFileNames;
    }

    /**
     * @return the repoFileUuids
     */
    @Column(name = "REPO_FILE_UUIDS")
    public String getRepoFileUuids() {
        return this.repoFileUuids;
    }

    /**
     * @param repoFileUuids
     */
    public String setRepoFileUuids(String repoFileUuids) {
        return this.repoFileUuids = repoFileUuids;
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
