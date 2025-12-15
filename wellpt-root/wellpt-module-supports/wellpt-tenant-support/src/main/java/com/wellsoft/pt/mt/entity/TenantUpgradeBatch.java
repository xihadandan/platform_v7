/*
 * @(#)2012-10-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.Set;

/**
 * Description: 数据升级批次表
 *
 * @author linz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2016-03-15.1	linz		2016-03-15		Create
 * </pre>
 * @date 2016-03-15
 */
@Entity
@CommonEntity
@Table(name = "MT_TENANT_UPGRADE_BATCH")
@DynamicUpdate
@DynamicInsert
public class TenantUpgradeBatch extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5506954582526992874L;
    /**
     * 过程步骤名称
     */
    private String name;
    /**
     * 文件名
     */
    private String repoFileNames;
    /**
     * 文件UUID
     */
    private String repoFileUuids;

    @UnCloneable
    private TenantUpgradeLog tenantUpgradeLog;

    @UnCloneable
    private Set<TenantUpgradeProcessLog> tenantUpgradeProcessLogs;

    @OneToMany(mappedBy = "tenantUpgradeBatch", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<TenantUpgradeProcessLog> getTenantUpgradeProcessLogs() {
        return tenantUpgradeProcessLogs;
    }

    public void setTenantUpgradeProcessLogs(Set<TenantUpgradeProcessLog> tenantUpgradeProcessLogs) {
        this.tenantUpgradeProcessLogs = tenantUpgradeProcessLogs;
    }

    @OneToOne(mappedBy = "tenantUpgradeBatch", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    public TenantUpgradeLog getTenantUpgradeLog() {
        return tenantUpgradeLog;
    }

    public void setTenantUpgradeLog(TenantUpgradeLog tenantUpgradeLog) {
        this.tenantUpgradeLog = tenantUpgradeLog;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRepoFileNames() {
        return repoFileNames;
    }

    public void setRepoFileNames(String repoFileNames) {
        this.repoFileNames = repoFileNames;
    }

    public String getRepoFileUuids() {
        return repoFileUuids;
    }

    public void setRepoFileUuids(String repoFileUuids) {
        this.repoFileUuids = repoFileUuids;
    }
}
