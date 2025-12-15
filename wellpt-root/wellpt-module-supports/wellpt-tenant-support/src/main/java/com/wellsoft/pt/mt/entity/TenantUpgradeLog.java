/*
 * @(#)2012-10-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.entity;

import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

/**
 * Description:数据升级记录日志表
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
@Table(name = "MT_TENANT_UPGRADE_LOG")
@DynamicUpdate
@DynamicInsert
public class TenantUpgradeLog extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7480803345346640507L;
    /**
     * 租户UUID
     */
    private Tenant tenant;
    /**
     * 升级批次UUID
     */
    private TenantUpgradeBatch tenantUpgradeBatch;
    /**
     * 升级结果，0失败，1成功
     */
    private int result;

    /**
     * 升级结果描述
     */
    private String remark;

    @OneToOne(fetch = FetchType.EAGER)
    @Cascade({CascadeType.REFRESH})
    @JoinColumn(name = "tenant_uuid")
    public Tenant getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @Cascade({CascadeType.REFRESH})
    @JoinColumn(name = "upgrade_batch_uuid")
    public TenantUpgradeBatch getTenantUpgradeBatch() {
        return tenantUpgradeBatch;
    }

    public void setTenantUpgradeBatch(TenantUpgradeBatch tenantUpgradeBatch) {
        this.tenantUpgradeBatch = tenantUpgradeBatch;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

}
