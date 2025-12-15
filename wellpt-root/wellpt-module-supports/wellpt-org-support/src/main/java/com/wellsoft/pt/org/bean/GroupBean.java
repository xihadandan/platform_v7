/*
 * @(#)2012-10-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.bean;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.pt.org.entity.Group;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Role;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: GroupBean.java
 *
 * @author zhulh
 * @date 2013-1-14
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-14.1	zhulh		2013-1-14		Create
 * </pre>
 */
public class GroupBean extends Group {

    private static final long serialVersionUID = 1407151148138677421L;

    // 成员名称(用户、部门)
    private String memberNames;
    // 成员ID
    private String memberIds;
    // 使用范围名称(用户、部门)
    private String rangeNames;
    // 使用范围ID
    private String rangeIds;
    // 创建人ID
    private String creatorId;
    // 创建人姓名
    private String creatorName;

    @UnCloneable
    private Set<Role> roles = new HashSet<Role>(0);
    @UnCloneable
    private Set<Privilege> privileges = new HashSet<Privilege>();

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getMemberNames() {
        return memberNames;
    }

    public void setMemberNames(String memberNames) {
        this.memberNames = memberNames;
    }

    public String getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(String memberIds) {
        this.memberIds = memberIds;
    }

    public String getRangeNames() {
        return rangeNames;
    }

    public void setRangeNames(String rangeNames) {
        this.rangeNames = rangeNames;
    }

    public String getRangeIds() {
        return rangeIds;
    }

    public void setRangeIds(String rangeIds) {
        this.rangeIds = rangeIds;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

}
