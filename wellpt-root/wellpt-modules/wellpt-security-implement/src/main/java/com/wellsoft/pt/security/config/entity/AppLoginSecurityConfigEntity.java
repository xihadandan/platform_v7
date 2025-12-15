package com.wellsoft.pt.security.config.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年12月07日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "APP_LOGIN_SECURITY_CONFIG")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "entity")
@DynamicUpdate
@DynamicInsert
public class AppLoginSecurityConfigEntity extends TenantEntity {

    // 是否允许多台设备同时登录
    private Boolean isAllowMultiDeviceLogin;

    private String system;
    private String tenant;

    public Boolean getIsAllowMultiDeviceLogin() {
        return isAllowMultiDeviceLogin;
    }

    public void setIsAllowMultiDeviceLogin(Boolean allowMultiDeviceLogin) {
        isAllowMultiDeviceLogin = allowMultiDeviceLogin;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}
