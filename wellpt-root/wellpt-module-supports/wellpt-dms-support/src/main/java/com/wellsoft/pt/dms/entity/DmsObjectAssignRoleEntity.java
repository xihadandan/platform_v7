/*
 * @(#)Dec 15, 2017 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Dec 15, 2017.1	zhulh		Dec 15, 2017		Create
 * </pre>
 * @date Dec 15, 2017
 */
@Entity
@Table(name = "DMS_OBJECT_ASSIGN_ROLE")
@DynamicUpdate
@DynamicInsert
public class DmsObjectAssignRoleEntity extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -753013550006398682L;

    // 授权对象实体UUID
    private String objectIdentityUuid;
    // 角色UUID
    private String roleUuid;
    // 分配的组织相关ID或角色，冗余
    private String orgIds;
    // 允许
    private String permit;
    // 拒绝
    private String deny;

    /**
     * @return the objectIdentityUuid
     */
    public String getObjectIdentityUuid() {
        return objectIdentityUuid;
    }

    /**
     * @param objectIdentityUuid 要设置的objectIdentityUuid
     */
    public void setObjectIdentityUuid(String objectIdentityUuid) {
        this.objectIdentityUuid = objectIdentityUuid;
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
     * @return the orgIds
     */
    public String getOrgIds() {
        return orgIds;
    }

    /**
     * @param orgIds 要设置的orgIds
     */
    public void setOrgIds(String orgIds) {
        this.orgIds = orgIds;
    }

    /**
     * @return the permit
     */
    public String getPermit() {
        return permit;
    }

    /**
     * @param permit 要设置的permit
     */
    public void setPermit(String permit) {
        this.permit = permit;
    }

    /**
     * @return the deny
     */
    public String getDeny() {
        return deny;
    }

    /**
     * @param deny 要设置的deny
     */
    public void setDeny(String deny) {
        this.deny = deny;
    }

}
