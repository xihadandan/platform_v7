package com.wellsoft.pt.api.dto;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 *
 * @author chenq
 * @date 2018/8/13
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2018/8/13    chenq		2018/8/13		Create
 * </pre>
 */
public class ApiOutSystemCofigDto implements Serializable {

    private static final long serialVersionUID = 6724279251333426849L;

    private String uuid;

    private String systemCode;

    private String systemName;

    private String token;


    private List<ApiOutSysServiceConfigDto> serviceConfigs = Lists.newArrayList();

    private List<ApiAuthorizeItemDto> authorizeItems = Lists.newArrayList();

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<ApiOutSysServiceConfigDto> getServiceConfigs() {
        return serviceConfigs;
    }

    public void setServiceConfigs(
            List<ApiOutSysServiceConfigDto> serviceConfigs) {
        this.serviceConfigs = serviceConfigs;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<ApiAuthorizeItemDto> getAuthorizeItems() {
        return authorizeItems;
    }

    public void setAuthorizeItems(List<ApiAuthorizeItemDto> authorizeItems) {
        this.authorizeItems = authorizeItems;
    }
}
