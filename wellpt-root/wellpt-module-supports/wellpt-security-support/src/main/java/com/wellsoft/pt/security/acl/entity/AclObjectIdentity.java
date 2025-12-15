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
 * AclObjectIdentity entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "acl_object_identity")
public class AclObjectIdentity extends IdEntity {

    // Fields

    private static final long serialVersionUID = 1362038939018555799L;

    private AclSid aclSid;
    private AclObjectIdentity aclObjectIdentity;
    private AclClass aclClass;
    private String objectIdIdentity;
    private Boolean entriesInheriting;
    private Set<AclObjectIdentity> aclObjectIdentities = new HashSet<AclObjectIdentity>(0);
    private Set<AclEntry> aclEntries = new HashSet<AclEntry>(0);

    // Constructors

    /**
     * default constructor
     */
    public AclObjectIdentity() {
    }

    /**
     * minimal constructor
     */
    public AclObjectIdentity(AclClass aclClass, String objectIdIdentity, Boolean entriesInheriting) {
        this.aclClass = aclClass;
        this.objectIdIdentity = objectIdIdentity;
        this.entriesInheriting = entriesInheriting;
    }

    /**
     * minimal constructor
     */
    public AclObjectIdentity(AclClass aclClass, String objectIdIdentity, AclSid aclSid, Boolean entriesInheriting) {
        this.aclClass = aclClass;
        this.objectIdIdentity = objectIdIdentity;
        this.aclSid = aclSid;
        this.entriesInheriting = entriesInheriting;
    }

    /**
     * full constructor
     */
    public AclObjectIdentity(AclSid aclSid, AclObjectIdentity aclObjectIdentity, AclClass aclClass,
                             String objectIdIdentity, Boolean entriesInheriting, Set<AclObjectIdentity> aclObjectIdentities,
                             Set<AclEntry> aclEntries) {
        this.aclSid = aclSid;
        this.aclObjectIdentity = aclObjectIdentity;
        this.aclClass = aclClass;
        this.objectIdIdentity = objectIdIdentity;
        this.entriesInheriting = entriesInheriting;
        this.aclObjectIdentities = aclObjectIdentities;
        this.aclEntries = aclEntries;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_sid")
    public AclSid getAclSid() {
        return this.aclSid;
    }

    public void setAclSid(AclSid aclSid) {
        this.aclSid = aclSid;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_object")
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SELECT)
    public AclObjectIdentity getAclObjectIdentity() {
        return this.aclObjectIdentity;
    }

    public void setAclObjectIdentity(AclObjectIdentity aclObjectIdentity) {
        this.aclObjectIdentity = aclObjectIdentity;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "object_id_class", nullable = false)
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SELECT)
    public AclClass getAclClass() {
        return this.aclClass;
    }

    public void setAclClass(AclClass aclClass) {
        this.aclClass = aclClass;
    }

    @Column(name = "object_id_identity", nullable = false, length = 64)
    public String getObjectIdIdentity() {
        return this.objectIdIdentity;
    }

    public void setObjectIdIdentity(String objectIdIdentity) {
        this.objectIdIdentity = objectIdIdentity;
    }

    @Column(name = "entries_inheriting", nullable = false)
    public Boolean getEntriesInheriting() {
        return this.entriesInheriting;
    }

    public void setEntriesInheriting(Boolean entriesInheriting) {
        this.entriesInheriting = entriesInheriting;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "aclObjectIdentity")
    @Cascade(value = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SELECT)
    public Set<AclObjectIdentity> getAclObjectIdentities() {
        return this.aclObjectIdentities;
    }

    public void setAclObjectIdentities(Set<AclObjectIdentity> aclObjectIdentities) {
        this.aclObjectIdentities = aclObjectIdentities;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "aclObjectIdentity")
    @Cascade(value = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SELECT)
    public Set<AclEntry> getAclEntries() {
        return this.aclEntries;
    }

    public void setAclEntries(Set<AclEntry> aclEntries) {
        this.aclEntries = aclEntries;
    }

}