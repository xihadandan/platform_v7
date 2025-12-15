/*
 * @(#)2014-6-10 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.entity;

import org.springframework.util.ClassUtils;

import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-6-10.1	zhulh		2014-6-10		Create
 * </pre>
 * @date 2014-6-10
 */
// @SuppressWarnings("serial")
@Embeddable
public class PrivilegeResourceId implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    // 权限UUID
    private String privilegeUuid;

    // 视图等列资源UUID
    private String resourceUuid;

    public PrivilegeResourceId() {
        super();
    }

    public PrivilegeResourceId(String privilegeUuid, String resourceUuid) {
        super();
        this.privilegeUuid = privilegeUuid;
        this.resourceUuid = resourceUuid;
    }

    /**
     * @return the privilegeUuid
     */
    public String getPrivilegeUuid() {
        return privilegeUuid;
    }

    /**
     * @param privilegeUuid 要设置的privilegeUuid
     */
    public void setPrivilegeUuid(String privilegeUuid) {
        this.privilegeUuid = privilegeUuid;
    }

    /**
     * @return the resourceUuid
     */
    public String getResourceUuid() {
        return resourceUuid;
    }

    /**
     * @param resourceUuid 要设置的resourceUuid
     */
    public void setResourceUuid(String resourceUuid) {
        this.resourceUuid = resourceUuid;
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
        result = prime * result + ((resourceUuid == null) ? 0 : resourceUuid.hashCode());
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
        PrivilegeResourceId other = (PrivilegeResourceId) obj;
        if (privilegeUuid == null) {
            if (other.privilegeUuid != null)
                return false;
        } else if (!privilegeUuid.equals(other.getPrivilegeUuid()))
            return false;
        if (resourceUuid == null) {
            if (other.resourceUuid != null)
                return false;
        } else if (!resourceUuid.equals(other.getResourceUuid()))
            return false;
        return true;
    }

}
