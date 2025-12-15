/*
 * @(#)2012-10-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
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
@Table(name = "MT_TENANT_UPGRADE_PROCESS_LOG")
@DynamicUpdate
@DynamicInsert
public class TenantUpgradeProcessLog extends IdEntity {

    public static final int SUCCESS = 1;
    public static final int COMPLETE = 2;
    public static final int FAIL = 0;
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8340503926570878361L;
    /**
     * 过程步骤名称
     */
    private String name;
    /**
     * 租户UUID
     */
    private String tenantUuid;
    /**
     * 升级记录日志表
     */
    @JsonIgnore
    @UnCloneable
    private TenantUpgradeBatch tenantUpgradeBatch;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件类型
     */
    private String dataType;

    /**
     * 升级的排序号，从1开始递增
     */
    private int sortOrder;
    /**
     * 状态，0失败，1成功;
     */
    private int status;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTenantUuid() {
        return tenantUuid;
    }

    public void setTenantUuid(String tenantUuid) {
        this.tenantUuid = tenantUuid;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.REFRESH})
    @JoinColumn(name = "upgrade_batch_uuid")
    public TenantUpgradeBatch getTenantUpgradeBatch() {
        return tenantUpgradeBatch;
    }

    public void setTenantUpgradeBatch(TenantUpgradeBatch tenantUpgradeBatch) {
        this.tenantUpgradeBatch = tenantUpgradeBatch;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
