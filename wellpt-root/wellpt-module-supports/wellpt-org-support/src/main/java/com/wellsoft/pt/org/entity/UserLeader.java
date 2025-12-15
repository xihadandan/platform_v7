/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * Description: 用户上级领导
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-15.1	zhulh		2013-1-15		Create
 * </pre>
 * @date 2013-1-15
 */
//@Entity
//@Table(name = "org_user_leader")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class UserLeader extends IdEntity {
    private static final long serialVersionUID = 4162314678083952747L;

    private User user;

    private User leader;
    private String tenantId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the user
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.REFRESH})
    @JoinColumn(name = "user_uuid")
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public User getUser() {
        return user;
    }

    /**
     * @param user 要设置的user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the leader
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leader_uuid")
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public User getLeader() {
        return leader;
    }

    /**
     * @param leader 要设置的leader
     */
    public void setLeader(User leader) {
        this.leader = leader;
    }

    /**
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((leader == null) ? 0 : leader.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    /**
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
        UserLeader other = (UserLeader) obj;
        if (leader == null) {
            if (other.leader != null)
                return false;
        } else if (!leader.equals(other.leader))
            return false;
        if (user == null) {
            if (other.user != null)
                return false;
        } else if (!user.equals(other.user))
            return false;
        return true;
    }

}
