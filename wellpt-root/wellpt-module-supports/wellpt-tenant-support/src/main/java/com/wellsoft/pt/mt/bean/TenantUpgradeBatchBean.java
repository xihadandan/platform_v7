/*
 * @(#)2012-10-21 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.mt.bean;

import com.wellsoft.pt.mt.entity.TenantUpgradeBatch;

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
public class TenantUpgradeBatchBean extends TenantUpgradeBatch {

    private String tenantId;

    private String tenantName;

    private String tenantCode;

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

}
