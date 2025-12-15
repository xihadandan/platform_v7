package com.wellsoft.pt.security.acl.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * AclClass entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "acl_class")
public class AclClass extends IdEntity {

    // Fields

    private static final long serialVersionUID = -2699627214390362714L;

    private String cls;
    private Set<AclObjectIdentity> aclObjectIdentities = new HashSet<AclObjectIdentity>(0);

    // Constructors

    /**
     * default constructor
     */
    public AclClass() {
    }

    /**
     * minimal constructor
     */
    public AclClass(String cls) {
        this.cls = cls;
    }

    /**
     * full constructor
     */
    public AclClass(String cls, Set<AclObjectIdentity> aclObjectIdentities) {
        this.cls = cls;
        this.aclObjectIdentities = aclObjectIdentities;
    }

    @Column(name = "class", unique = true, nullable = false)
    public String getCls() {
        return this.cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "aclClass")
    @Cascade(value = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SELECT)
    public Set<AclObjectIdentity> getAclObjectIdentities() {
        return this.aclObjectIdentities;
    }

    public void setAclObjectIdentities(Set<AclObjectIdentity> aclObjectIdentities) {
        this.aclObjectIdentities = aclObjectIdentities;
    }

}