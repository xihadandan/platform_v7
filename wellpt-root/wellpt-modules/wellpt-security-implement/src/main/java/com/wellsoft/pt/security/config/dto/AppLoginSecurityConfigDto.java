package com.wellsoft.pt.security.config.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

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
@ApiModel(value = "登录安全配置请求实体", description = "登录安全配置接口请求实体参数")
public class AppLoginSecurityConfigDto implements Serializable {
    @ApiModelProperty(value = "是否允许多设备同时登录", example = "1", required = true)
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
