package com.wellsoft.pt.app.feishu.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 飞书配置信息实体类
 */
@Entity
@Table(name = "feishu_config")
@DynamicUpdate
@DynamicInsert
public class FeishuConfigEntity extends SysEntity {

    private static final long serialVersionUID = 3840550606341127330L;

    /**
     * 飞书应用的App ID
     */
    private String appId;

    /**
     * 飞书应用的App Secret
     */
    private String appSecret;

    /**
     * 飞书应用的服务URL
     */
    private String serviceUri;

    /**
     * 飞书应用的重定向URL
     */
    private String redirectUri;

    /**
     * 飞书应用的移动端URL
     */
    private String mobileAppUri;

    /**
     * 是否启用飞书应用
     */
    private Boolean enabled;

    /**
     * 飞书应用的配置信息
     */
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
     * @return the redirectUri
     */
    public String getRedirectUri() {
        return redirectUri;
    }

    /**
     * @param redirectUri 要设置的redirectUri
     */
    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
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