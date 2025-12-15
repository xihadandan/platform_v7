/*
 * @(#)5/28/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.entity;

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
 * 5/28/24.1	zhulh		5/28/24		Create
 * </pre>
 * @date 5/28/24
 */
@Entity
@Table(name = "DMS_OBJECT_ASSIGN_ACTION")
@DynamicUpdate
@DynamicInsert
public class DmsObjectAssignActionEntity extends com.wellsoft.context.jdbc.entity.Entity {

    // 授权对象实体UUID
    private String objectIdentityUuid;

    // 对象数据标识
    private String objectIdIdentity;

    // 分配的组织相关ID
    private String orgId;

    // 操作权限
    private String action;

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
     * @return the objectIdIdentity
     */
    public String getObjectIdIdentity() {
        return objectIdIdentity;
    }

    /**
     * @param objectIdIdentity 要设置的objectIdIdentity
     */
    public void setObjectIdIdentity(String objectIdIdentity) {
        this.objectIdIdentity = objectIdIdentity;
    }

    /**
     * @return the orgId
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * @param orgId 要设置的orgId
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

    /**
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * @param action 要设置的action
     */
    public void setAction(String action) {
        this.action = action;
    }

}
