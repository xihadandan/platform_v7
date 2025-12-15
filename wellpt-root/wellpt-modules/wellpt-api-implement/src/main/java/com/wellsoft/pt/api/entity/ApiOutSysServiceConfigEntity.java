package com.wellsoft.pt.api.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Description: api对接系统服务配置
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
@Table(name = "API_OUT_SYSTEM_SERVICE_CONFIG")
@Entity
@DynamicUpdate
@DynamicInsert
public class ApiOutSysServiceConfigEntity extends TenantEntity {

    private static final long serialVersionUID = -6875367678817898146L;

    private String serviceCode;//服务编码


    private ApiOutSystemConfigEntity systemConfig;

    private String serviceUrl;

    private String serviceName;

    private Long overtimeLimit;

    private String serviceAdapter;


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

    @ManyToOne
    @JoinColumn(name = "system_uuid")
    public ApiOutSystemConfigEntity getSystemConfig() {
        return systemConfig;
    }

    public void setSystemConfig(ApiOutSystemConfigEntity systemConfig) {
        this.systemConfig = systemConfig;
    }
}
