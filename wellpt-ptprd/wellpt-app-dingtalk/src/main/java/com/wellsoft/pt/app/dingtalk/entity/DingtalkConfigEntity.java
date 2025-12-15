/*
 * @(#)4/16/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/16/25.1	    zhulh		4/16/25		    Create
 * </pre>
 * @date 4/16/25
 */
@Entity
@Table(name = "dingtalk_config")
@DynamicUpdate
@DynamicInsert
@ApiModel("钉钉接入配置")
public class DingtalkConfigEntity extends SysEntity {
    private static final long serialVersionUID = -1047341801005137249L;

    // 应用的App ID
    @ApiModelProperty("应用的App ID")
    private String appId;

    // 应用的AppKey
    @ApiModelProperty("应用的Client ID")
    private String clientId;

    // 应用的AppSecret
    @ApiModelProperty("应用的Client Secret")
    private String clientSecret;

    // 服务URL
    @ApiModelProperty("服务URL")
    private String serviceUri;

    // 企业代理ID
    @ApiModelProperty("企业代理ID")
    private String agentId;

    // 企业ID
    @ApiModelProperty("企业ID")
    private String corpId;

    @ApiModelProperty("企业回调域名")
    private String corpDomainUri;

    @ApiModelProperty("应用的移动端URL")
    private String mobileAppUri;

    @ApiModelProperty("是否启用")
    private Boolean enabled;

    // 应用的其他配置信息
    @ApiModelProperty("应用的其他配置信息")
    private String definitionJson;

    /**
     * @return the appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId 要设置的appId
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * @return the clientId
     */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId 要设置的clientId
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    /**
     * @return the clientSecret
     */
    public String getClientSecret() {
        return clientSecret;
    }

    /**
     * @param clientSecret 要设置的clientSecret
     */
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    /**
     * @return the serviceUri
     */
    public String getServiceUri() {
        return serviceUri;
    }

    /**
     * @param serviceUri 要设置的serviceUri
     */
    public void setServiceUri(String serviceUri) {
        this.serviceUri = serviceUri;
    }

    /**
     * @return the agentId
     */
    public String getAgentId() {
        return agentId;
    }

    /**
     * @param agentId 要设置的agentId
     */
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    /**
     * @return the corpId
     */
    public String getCorpId() {
        return corpId;
    }

    /**
     * @param corpId 要设置的corpId
     */
    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    /**
     * @return the corpDomainUri
     */
    public String getCorpDomainUri() {
        return corpDomainUri;
    }

    /**
     * @param corpDomainUri 要设置的corpDomainUri
     */
    public void setCorpDomainUri(String corpDomainUri) {
        this.corpDomainUri = corpDomainUri;
    }

    /**
     * @return the mobileAppUri
     */
    public String getMobileAppUri() {
        return mobileAppUri;
    }

    /**
     * @param mobileAppUri 要设置的mobileAppUri
     */
    public void setMobileAppUri(String mobileAppUri) {
        this.mobileAppUri = mobileAppUri;
    }

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled 要设置的enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the definitionJson
     */
    public String getDefinitionJson() {
        return definitionJson;
    }

    /**
     * @param definitionJson 要设置的definitionJson
     */
    public void setDefinitionJson(String definitionJson) {
        this.definitionJson = definitionJson;
    }

}
