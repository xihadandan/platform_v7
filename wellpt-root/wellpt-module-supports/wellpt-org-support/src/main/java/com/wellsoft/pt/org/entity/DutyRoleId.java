package com.wellsoft.pt.org.entity;

import org.springframework.util.ClassUtils;

import javax.persistence.Embeddable;
import java.io.Serializable;

//@SuppressWarnings("serial")
@Embeddable
public class DutyRoleId implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    /**
     *
     */
    private String dutyUuid;
    private String roleUuid;
    private String tenantId;

    public DutyRoleId() {
        super();
    }

    public DutyRoleId(String dutyUuid, String roleUuid, String tenantId) {
        super();
        this.dutyUuid = dutyUuid;
        this.roleUuid = roleUuid;
        this.tenantId = tenantId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDutyUuid() {
        return dutyUuid;
    }

    public void setDutyUuid(String dutyUuid) {
        this.dutyUuid = dutyUuid;
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
        result = prime * result + ((dutyUuid == null) ? 0 : dutyUuid.hashCode());
        result = prime * result + ((roleUuid == null) ? 0 : roleUuid.hashCode());
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
        DutyRoleId other = (DutyRoleId) obj;
        if (dutyUuid == null) {
            if (other.dutyUuid != null)
                return false;
        } else if (!dutyUuid.equals(other.getDutyUuid()))
            return false;
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
        return true;
    }

}
