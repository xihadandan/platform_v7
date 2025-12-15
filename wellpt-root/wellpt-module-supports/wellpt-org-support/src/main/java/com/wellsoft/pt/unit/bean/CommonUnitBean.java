package com.wellsoft.pt.unit.bean;

import com.wellsoft.pt.unit.entity.CommonUnit;

/**
 * Description: CommonUnitBean VO
 *
 * @author liuzq
 * @date 2013-11-5
 */
public class CommonUnitBean extends CommonUnit {
    private static final long serialVersionUID = 1034165346563913836L;

    private String tenantUuid;

    private String tenantName;

    private String temp1;

    public String getTemp1() {
        return temp1;
    }

    public void setTemp1(String temp1) {
        this.temp1 = temp1;
    }

    public String getTenantUuid() {
        return tenantUuid;
    }

    public void setTenantUuid(String tenantUuid) {
        this.tenantUuid = tenantUuid;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }

}
