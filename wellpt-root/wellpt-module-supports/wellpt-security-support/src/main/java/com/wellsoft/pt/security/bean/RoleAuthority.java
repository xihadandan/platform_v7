/*
 * @(#)2018年9月29日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.bean;

import com.google.common.collect.Sets;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月29日.1	zyguo	2018年9月29日		Create
 * </pre>
 * @date 2018年9月29日
 */
public final class RoleAuthority implements GrantedAuthority {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 7872562072532710056L;

    private final String role; // 角色

    private final String roleName;

    private Set<String> from = Sets.newHashSet(); // 角色来源

    public RoleAuthority(String roleId, String roleName, Set<String> from) {
        Assert.hasText(roleId, "A granted authority textual representation is required");
        this.role = roleId;
        this.roleName = roleName;
        if (from != null) {
            this.from = from;
        }

    }

    public String getAuthority() {
        return role;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof RoleAuthority) {
            return role.equals(((RoleAuthority) obj).role);
        }

        return false;
    }

    public int hashCode() {
        return this.role.hashCode();
    }

    public String toString() {
        return this.role;
    }

    /**
     * @return the from
     */
    public Set<String> getFrom() {
        return from;
    }

    public void addFrom(String from) {
        this.from.add(from);
    }

    /**
     * @return the roleName
     */
    public String getRoleName() {
        return roleName;
    }
}
