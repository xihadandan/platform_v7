/*
 * @(#)1/8/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.acl.entity;

import com.wellsoft.context.jdbc.entity.Entity;

import javax.persistence.Table;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 1/8/25.1	    zhulh		1/8/25		    Create
 * </pre>
 * @date 1/8/25
 */
@javax.persistence.Entity
@Table(name = "acl_task_done_marker")
public class AclTaskDoneMarker extends Entity {
    private static final long serialVersionUID = -3983387773173391811L;

    private String aclTaskUuid;

    // 阅读人员ID
    private String userId;

    /**
     * @return the aclTaskUuid
     */
    public String getAclTaskUuid() {
        return aclTaskUuid;
    }

    /**
     * @param aclTaskUuid 要设置的aclTaskUuid
     */
    public void setAclTaskUuid(String aclTaskUuid) {
        this.aclTaskUuid = aclTaskUuid;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId 要设置的userId
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
