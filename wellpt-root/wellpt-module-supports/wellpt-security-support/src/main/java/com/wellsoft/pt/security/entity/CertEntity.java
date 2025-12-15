package com.wellsoft.pt.security.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigInteger;
import java.util.Date;

/**
 * This will provide cert as well as keystore important data.
 */
@Entity
@Table(name = "cd_key_cert")
public class CertEntity extends TenantEntity {

    private static final long serialVersionUID = 1L;
    private String alias;
    private String subjectDN;
    private String issuerDN;
    private BigInteger serialNumber;
    private int version;
    private Date notBefore;
    private Date notAfter;
    private String publicKey;

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getSubjectDN() {
        return subjectDN;
    }

    public void setSubjectDN(String subjectDN) {
        this.subjectDN = subjectDN;
    }

    public String getIssuerDN() {
        return issuerDN;
    }

    public void setIssuerDN(String issuerDN) {
        this.issuerDN = issuerDN;
    }

    public BigInteger getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(BigInteger serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Date getNotBefore() {
        return notBefore;
    }

    public void setNotBefore(Date notBefore) {
        this.notBefore = notBefore;
    }

    public Date getNotAfter() {
        return notAfter;
    }

    public void setNotAfter(Date notAfter) {
        this.notAfter = notAfter;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

}
