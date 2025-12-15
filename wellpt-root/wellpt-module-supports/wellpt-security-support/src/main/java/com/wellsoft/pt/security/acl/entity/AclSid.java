package com.wellsoft.pt.security.acl.entity;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgService;
import com.wellsoft.pt.security.acl.service.AclService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * AclSid entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "acl_sid")
public class AclSid extends IdEntity {

    // Fields

    private static final long serialVersionUID = -5232858243848388291L;

    private Boolean principal;
    private String sid;
    private Set<AclEntry> aclEntries = new HashSet<AclEntry>(0);
    private Set<AclObjectIdentity> aclObjectIdentities = new HashSet<AclObjectIdentity>(0);
    private Set<AclSidMember> aclSidMembers = new HashSet<AclSidMember>(0);

    // Constructors

    /**
     * default constructor
     */
    public AclSid() {
    }

    /**
     * minimal constructor
     */
    public AclSid(Boolean principal, String sid) {
        if (!sid.startsWith(AclService.PREFIX_USERNAME) && !sid.startsWith(AclService.PREFIX_JOB)
                && !sid.startsWith(AclService.PREFIX_ROLE) && !sid.startsWith(AclService.PREFIX_GROUP)
                && !sid.startsWith(IdPrefix.ORG_VERSION.getValue())
                && !(SpringSecurityUtils.isInternetLoginUser() || ApplicationContextHolder.getBean(UserInfoFacadeService.class).isNotStaffUser(sid))) {
            throw new RuntimeException("安全标识符sid必须以U或ROLE_或GROUP_开头");
        }
        if (sid.startsWith(IdPrefix.ORG_VERSION.getValue())) {
            sid = sid.split(MultiOrgService.PATH_SPLIT_SYSMBOL)[1];
        }
        this.principal = principal;
        this.sid = sid;

    }

    /**
     * full constructor
     */
    public AclSid(Boolean principal, String sid, Set<AclEntry> aclEntries, Set<AclObjectIdentity> aclObjectIdentities) {
        this.principal = principal;
        this.sid = sid;
        this.aclEntries = aclEntries;
        this.aclObjectIdentities = aclObjectIdentities;
    }

    @Column(name = "principal", nullable = false)
    public Boolean getPrincipal() {
        return this.principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

    @Column(name = "sid", nullable = false, length = 100)
    public String getSid() {
        return this.sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "aclSid")
    @Cascade(value = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SELECT)
    public Set<AclEntry> getAclEntries() {
        return this.aclEntries;
    }

    public void setAclEntries(Set<AclEntry> aclEntries) {
        this.aclEntries = aclEntries;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "aclSid")
    @Cascade(value = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SELECT)
    public Set<AclObjectIdentity> getAclObjectIdentities() {
        return this.aclObjectIdentities;
    }

    public void setAclObjectIdentities(Set<AclObjectIdentity> aclObjectIdentities) {
        this.aclObjectIdentities = aclObjectIdentities;
    }

    /**
     * @return the aclSidMembers
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "aclSid")
    @Cascade(value = {CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SELECT)
    public Set<AclSidMember> getAclSidMembers() {
        return aclSidMembers;
    }

    /**
     * @param aclSidMembers 要设置的aclSidMembers
     */
    public void setAclSidMembers(Set<AclSidMember> aclSidMembers) {
        this.aclSidMembers = aclSidMembers;
    }

}