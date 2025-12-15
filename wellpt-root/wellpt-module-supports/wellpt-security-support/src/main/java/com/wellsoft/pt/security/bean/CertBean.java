package com.wellsoft.pt.security.bean;

import com.wellsoft.pt.security.entity.CertEntity;

import java.security.cert.X509Certificate;

public class CertBean extends CertEntity {
    private static final long serialVersionUID = 1L;
    private X509Certificate certificate;

    public X509Certificate getCertificate() {
        return certificate;
    }

    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }
}
