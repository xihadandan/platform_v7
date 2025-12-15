package com.wellsoft.pt.security.bean;

import com.wellsoft.pt.security.entity.CertEntity;
import com.wellsoft.pt.security.entity.KeyStoreEntity;

import java.util.List;

public class KeyStoreBean extends KeyStoreEntity {
    private static final long serialVersionUID = 1L;
    private List<CertEntity> certs;
    private CertEntity key;

    public List<CertEntity> getCerts() {
        return certs;
    }

    public void setCerts(List<CertEntity> certs) {
        this.certs = certs;
    }

    public CertEntity getKey() {
        return key;
    }

    public void setKey(CertEntity key) {
        this.key = key;
    }

}
