/*
 * @(#)5/20/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.entity;

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
 * 5/20/25.1	    zhulh		5/20/25		    Create
 * </pre>
 * @date 5/20/25
 */
@Entity
@Table(name = "weixin_config")
@DynamicUpdate
@DynamicInsert
@ApiModel("微信接入配置")
public class WeixinConfigEntity extends SysEntity {
    private static final long serialVersionUID = 3756931982785194493L;

    @ApiModelProperty("应用的ID")
    private String appId;

    @ApiModelProperty("应用的凭证密钥")
    private String appSecret;

    @ApiModelProperty("企业ID")
    private String corpId;

    @ApiModelProperty("企业回调域名")
    private String corpDomainUri;

    @ApiModelProperty("应用的移动端URL")
    private String mobileAppUri;

    @ApiModelProperty("是否启用")
    private Boolean enabled;

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
     * @return the appSecret
     */
    public String getAppSecret() {
        return appSecret;
    }

    /**
     * @param appSecret 要设置的appSecret
     */
    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
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
