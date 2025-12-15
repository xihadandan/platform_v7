package com.wellsoft.pt.org.entity;

import org.springframework.util.ClassUtils;

import javax.persistence.Embeddable;
import java.io.Serializable;

// @SuppressWarnings("serial")
@Embeddable
public class GroupPrivilegeId implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private String groupUuid;

    private String privilegeUuid;

    private String tenantId;

    public GroupPrivilegeId() {
        super();
    }

    public GroupPrivilegeId(String groupUuid, String privilegeUuid, String tenantId) {
        super();
        this.privilegeUuid = privilegeUuid;
        this.groupUuid = groupUuid;
        this.tenantId = tenantId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getGroupUuid() {
        return groupUuid;
    }

    public void setGroupUuid(String groupUuid) {
        this.groupUuid = groupUuid;
    }

    public String getPrivilegeUuid() {
        return privilegeUuid;
    }

    public void setPrivilegeUuid(String privilegeUuid) {
        this.privilegeUuid = privilegeUuid;
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
        result = prime * result + ((groupUuid == null) ? 0 : groupUuid.hashCode());
        result = prime * result + ((privilegeUuid == null) ? 0 : privilegeUuid.hashCode());
        result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
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
        GroupPrivilegeId other = (GroupPrivilegeId) obj;
        if (groupUuid == null) {
            if (other.groupUuid != null)
                return false;
        } else if (!groupUuid.equals(other.getGroupUuid()))
            return false;
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
        return true;
    }

}
