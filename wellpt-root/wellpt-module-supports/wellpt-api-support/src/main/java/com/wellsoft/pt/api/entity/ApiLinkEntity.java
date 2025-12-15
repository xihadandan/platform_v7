package com.wellsoft.pt.api.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2025年05月13日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "API_LINK")
@DynamicUpdate
@DynamicInsert
public class ApiLinkEntity extends SysEntity {

    private String id;

    private String name;

    private String remark;

    private String endpoint;

    private String devEndpoint;

    private String testEndpoint;

    private String stagEndpoint;

    private String protocol;

    private String icon;

    private String authConfig;

    private String faultToleranceConfig;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    private List<ApiOperationEntity> apiOperations;


    @Transient
    public List<ApiOperationEntity> getApiOperations() {
        return apiOperations;
    }

    public void setApiOperations(List<ApiOperationEntity> apiOperations) {
        this.apiOperations = apiOperations;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDevEndpoint() {
        return devEndpoint;
    }

    public void setDevEndpoint(String devEndpoint) {
        this.devEndpoint = devEndpoint;
    }

    public String getTestEndpoint() {
        return testEndpoint;
    }

    public void setTestEndpoint(String testEndpoint) {
        this.testEndpoint = testEndpoint;
    }

    public String getStagEndpoint() {
        return stagEndpoint;
    }

    public void setStagEndpoint(String stagEndpoint) {
        this.stagEndpoint = stagEndpoint;
    }

    public String getAuthConfig() {
        return authConfig;
    }

    public void setAuthConfig(String authConfig) {
        this.authConfig = authConfig;
    }

    public String getFaultToleranceConfig() {
        return faultToleranceConfig;
    }

    public void setFaultToleranceConfig(String faultToleranceConfig) {
        this.faultToleranceConfig = faultToleranceConfig;
    }
}
