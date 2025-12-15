package com.wellsoft.pt.org.entity;

import org.springframework.util.ClassUtils;

import javax.persistence.Embeddable;
import java.io.Serializable;

// @SuppressWarnings("serial")
@Embeddable
public class UserRoleId implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private String userUuid;

    private String roleUuid;

    private String tenantId;

    public UserRoleId() {
        super();
    }

    public UserRoleId(String userUuid, String roleUuid, String tenantId) {
        super();
        this.userUuid = userUuid;
        this.roleUuid = roleUuid;
        this.tenantId = tenantId;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getRoleUuid() {
        return roleUuid;
    }

    public void setRoleUuid(String roleUuid) {
        this.roleUuid = roleUuid;
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
        int result = 1;
        result = prime * result + ((roleUuid == null) ? 0 : roleUuid.hashCode());
        result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
        result = prime * result + ((userUuid == null) ? 0 : userUuid.hashCode());
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
        if (obj == null)
            return false;
        if (getClass() != ClassUtils.getUserClass(obj.getClass()))
            return false;
        UserRoleId other = (UserRoleId) obj;
        if (roleUuid == null) {
            if (other.roleUuid != null)
                return false;
        } else if (!roleUuid.equals(other.getRoleUuid()))
            return false;
        if (tenantId == null) {
            if (other.tenantId != null)
                return false;
        } else if (!tenantId.equals(other.getTenantId()))
            return false;
        if (userUuid == null) {
            if (other.userUuid != null)
                return false;
        } else if (!userUuid.equals(other.getUserUuid()))
            return false;
        return true;
    }

}
