/*
 * @(#)2013-2-21 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;

/**
 * Description: SID中的成员类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-21.1	zhulh		2013-2-21		Create
 * </pre>
 * @date 2013-2-21
 */
@Entity
@Table(name = "acl_sid_member")
public class AclSidMember extends IdEntity {
    private static final long serialVersionUID = -7296515507704946599L;

    // 模板ID
    private String moduleId;
    // 成员
    private String member;

    private AclSid aclSid;

    /**
     * @return the moduleId
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * @param moduleId 要设置的moduleId
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * @return the member
     */
    public String getMember() {
        return member;
    }

    /**
     * @param member 要设置的member
     */
    public void setMember(String member) {
        this.member = member;
    }

    /**
     * @return the aclSid
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_sid")
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SELECT)
    public AclSid getAclSid() {
        return aclSid;
    }

    /**
     * @param aclSid 要设置的aclSid
     */
    public void setAclSid(AclSid aclSid) {
        this.aclSid = aclSid;
    }

}
