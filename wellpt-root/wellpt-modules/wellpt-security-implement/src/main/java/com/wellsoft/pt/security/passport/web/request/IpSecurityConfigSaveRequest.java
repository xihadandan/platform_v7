package com.wellsoft.pt.security.passport.web.request;

import com.wellsoft.pt.security.passport.entity.IpSecurityConfig;

import java.io.Serializable;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年02月28日   chenq	 Create
 * </pre>
 */
public class IpSecurityConfigSaveRequest implements Serializable {
    private String system;
    private String tenant;

    private List<IpSecurityConfig> config;

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

    public List<IpSecurityConfig> getConfig() {
        return config;
    }

    public void setConfig(List<IpSecurityConfig> config) {
        this.config = config;
    }
}
