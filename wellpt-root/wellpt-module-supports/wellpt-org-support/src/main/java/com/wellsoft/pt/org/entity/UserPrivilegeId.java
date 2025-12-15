package com.wellsoft.pt.org.entity;

import org.springframework.util.ClassUtils;

import javax.persistence.Embeddable;
import java.io.Serializable;

//@SuppressWarnings("serial")
@Embeddable
public class UserPrivilegeId implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private String userUuid;

    private String privilegeUuid;

    private String tenantId;

    public UserPrivilegeId() {
        super();
    }

    public UserPrivilegeId(String userUuid, String privilegeUuid, String tenantId) {
        super();
        this.privilegeUuid = privilegeUuid;
        this.userUuid = userUuid;
        this.tenantId = tenantId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getPrivilegeUuid() {
        return privilegeUuid;
    }

    public void setPrivilegeUuid(String privilegeUuid) {
        this.privilegeUuid = privilegeUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
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
        result = prime * result + ((privilegeUuid == null) ? 0 : privilegeUuid.hashCode());
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
        UserPrivilegeId other = (UserPrivilegeId) obj;
        if (privilegeUuid == null) {
            if (other.privilegeUuid != null)
                return false;
        } else if (!privilegeUuid.equals(other.getPrivilegeUuid()))
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
