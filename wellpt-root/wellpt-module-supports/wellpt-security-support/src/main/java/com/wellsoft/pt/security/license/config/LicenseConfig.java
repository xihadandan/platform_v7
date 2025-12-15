/*
 * @(#)2019年5月14日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.license.config;

import com.wellsoft.context.base.BaseObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Description: 许可证配置
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年5月14日.1	zhulh		2019年5月14日		Create
 * </pre>
 * @date 2019年5月14日
 */
@Configuration
public class LicenseConfig extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 3061136216639883606L;

    // 证书别名
    @Value("${license.trusted.cert.alias}")
    private String publicKeyAlias;

    // 证书密钥库密码
    @Value("${license.trusted.keystore.password}")
    private String publicKeyStorePassword;

    // 证书密钥库路径
    @Value("${license.trusted.keystore.classpath}")
    private String publicKeyStorePath;

    /**
     * @return the publicKeyAlias
     */
    public String getPublicKeyAlias() {
        return publicKeyAlias;
    }

    /**
     * @param publicKeyAlias 要设置的publicKeyAlias
     */
    public void setPublicKeyAlias(String publicKeyAlias) {
        this.publicKeyAlias = publicKeyAlias;
    }

    /**
     * @return the publicKeyStorePassword
     */
    public String getPublicKeyStorePassword() {
        return publicKeyStorePassword;
    }

    /**
     * @param publicKeyStorePassword 要设置的publicKeyStorePassword
     */
    public void setPublicKeyStorePassword(String publicKeyStorePassword) {
        this.publicKeyStorePassword = publicKeyStorePassword;
    }

    /**
     * @return the publicKeyStorePath
     */
    public String getPublicKeyStorePath() {
        return publicKeyStorePath;
    }

    /**
     * @param publicKeyStorePath 要设置的publicKeyStorePath
     */
    public void setPublicKeyStorePath(String publicKeyStorePath) {
        this.publicKeyStorePath = publicKeyStorePath;
    }

}
