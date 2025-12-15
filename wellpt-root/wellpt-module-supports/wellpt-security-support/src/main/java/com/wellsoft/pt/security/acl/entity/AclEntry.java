package com.wellsoft.pt.security.acl.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.*;

/**
 * AclEntry entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "acl_entry")
public class AclEntry extends IdEntity {

    // Fields

    private static final long serialVersionUID = 7174062487118045572L;

    private AclObjectIdentity aclObjectIdentity;
    private AclSid aclSid;
    private Integer mask;
    private Boolean granting;
    private String objectIdIdentity;

    // Constructors

    /**
     * default constructor
     */
    public AclEntry() {
    }

    /**
     * full constructor
     */
    public AclEntry(AclObjectIdentity aclObjectIdentity, AclSid aclSid, Integer mask, Boolean granting) {
        this.aclObjectIdentity = aclObjectIdentity;
        this.aclSid = aclSid;
        this.mask = mask;
        this.granting = granting;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acl_object_identity", nullable = false)
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public AclObjectIdentity getAclObjectIdentity() {
        return this.aclObjectIdentity;
    }

    public void setAclObjectIdentity(AclObjectIdentity aclObjectIdentity) {
        this.aclObjectIdentity = aclObjectIdentity;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sid", nullable = false)
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public AclSid getAclSid() {
        return this.aclSid;
    }

    public void setAclSid(AclSid aclSid) {
        this.aclSid = aclSid;
    }

    @Column(name = "mask", nullable = false)
    public Integer getMask() {
        return this.mask;
    }

    public void setMask(Integer mask) {
        this.mask = mask;
    }

    @Column(name = "granting", nullable = false)
    public Boolean getGranting() {
        return this.granting;
    }

    public void setGranting(Boolean granting) {
        this.granting = granting;
    }

    /**
     * @return the objectIdIdentity
     */
    public String getObjectIdIdentity() {
        return objectIdIdentity;
    }

    /**
     * @param objectIdIdentity 要设置的objectIdIdentity
     */
    public void setObjectIdIdentity(String objectIdIdentity) {
        this.objectIdIdentity = objectIdIdentity;
    }

}