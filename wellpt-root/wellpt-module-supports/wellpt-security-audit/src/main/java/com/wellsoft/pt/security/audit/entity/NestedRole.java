/*
 * @(#)2013-1-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: 嵌套角色实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-18.1	zhulh		2013-1-18		Create
 * </pre>
 * @date 2013-1-18
 */
@Entity
@Table(name = "AUDIT_NESTED_ROLE")
@DynamicUpdate
@DynamicInsert
public class NestedRole extends IdEntity {
    private static final long serialVersionUID = -3762621961514846991L;
    @ApiModelProperty("子结点")
    private String roleUuid;

    @ApiModelProperty("关联结点")
    @JsonIgnore
    private Set<Role> roles = new HashSet<Role>();

    /**
     *
     */
    public NestedRole() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @return the roleUuid
     */
    public String getRoleUuid() {
        return roleUuid;
    }

    /**
     * @param roleUuid 要设置的roleUuid
     */
    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
    }

    /**
     * @return the roles
     */
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "nestedRoles")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * @param roles 要设置的roles
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((roleUuid == null) ? 0 : roleUuid.hashCode());
        return result;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        NestedRole other = (NestedRole) obj;
        if (roleUuid == null) {
            if (other.roleUuid != null)
                return false;
        } else if (!roleUuid.equals(other.roleUuid))
            return false;
        return true;
    }

}
