package com.wellsoft.pt.org.entity;

import javax.persistence.Embeddable;
import java.io.Serializable;

//@SuppressWarnings("serial")
@Embeddable
public class DepartmentPrivilegeId implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private String departmentUuid;

    private String privilegeUuid;

    private String tenantId;

    public DepartmentPrivilegeId() {
        super();
    }

    public DepartmentPrivilegeId(String departmentUuid, String privilegeUuid, String tenantId) {
        super();
        this.privilegeUuid = privilegeUuid;
        this.departmentUuid = departmentUuid;
        this.tenantId = tenantId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDepartmentUuid() {
        return departmentUuid;
    }

    public void setDepartmentUuid(String departmentUuid) {
        this.departmentUuid = departmentUuid;
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
        result = prime * result + ((departmentUuid == null) ? 0 : departmentUuid.hashCode());
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
        if (getClass() != obj.getClass())
            return false;
        DepartmentPrivilegeId other = (DepartmentPrivilegeId) obj;
        if (departmentUuid == null) {
            if (other.departmentUuid != null)
                return false;
        } else if (!departmentUuid.equals(other.departmentUuid))
            return false;
        if (privilegeUuid == null) {
            if (other.privilegeUuid != null)
                return false;
        } else if (!privilegeUuid.equals(other.privilegeUuid))
            return false;
        if (tenantId == null) {
            if (other.tenantId != null)
                return false;
        } else if (!tenantId.equals(other.tenantId))
            return false;
        return true;
    }

}
