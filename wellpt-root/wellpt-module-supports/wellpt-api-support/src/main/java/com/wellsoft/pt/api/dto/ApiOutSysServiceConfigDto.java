package com.wellsoft.pt.api.dto;

import java.io.Serializable;

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
public class ApiOutSysServiceConfigDto implements Serializable {

    private static final long serialVersionUID = 7266655391546960978L;
    private String serviceCode;//服务编码

    private String systemUuid;

    private String serviceUrl;

    private String serviceName;

    private Long overtimeLimit;

    private String serviceAdapter;

    public String getSystemUuid() {
        return systemUuid;
    }

    public void setSystemUuid(String systemUuid) {
        this.systemUuid = systemUuid;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getOvertimeLimit() {
        return overtimeLimit;
    }

    public void setOvertimeLimit(Long overtimeLimit) {
        this.overtimeLimit = overtimeLimit;
    }

    public String getServiceAdapter() {
        return serviceAdapter;
    }

    public void setServiceAdapter(String serviceAdapter) {
        this.serviceAdapter = serviceAdapter;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }
}
