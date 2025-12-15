package com.wellsoft.pt.security.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "cd_key_store")
public class KeyStoreEntity extends TenantEntity {
    private static final long serialVersionUID = 1L;
    private String fileName;
    private String keyValue;
    private String keyStoreName;
    private String keyStoreType;
    private String provider;
    private String pubKeyFilePath;
    private Boolean isPrivateStore;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getKeyStoreName() {
        return keyStoreName;
    }

    public void setKeyStoreName(String keyStoreName) {
        this.keyStoreName = keyStoreName;
    }

    public String getKeyStoreType() {
        return keyStoreType;
    }

    public void setKeyStoreType(String keyStoreType) {
        this.keyStoreType = keyStoreType;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getPubKeyFilePath() {
        return pubKeyFilePath;
    }

    public void setPubKeyFilePath(String pubKeyFilePath) {
        this.pubKeyFilePath = pubKeyFilePath;
    }

    public Boolean getIsPrivateStore() {
        return isPrivateStore;
    }

    public void setIsPrivateStore(Boolean isPrivateStore) {
        this.isPrivateStore = isPrivateStore;
    }

}
