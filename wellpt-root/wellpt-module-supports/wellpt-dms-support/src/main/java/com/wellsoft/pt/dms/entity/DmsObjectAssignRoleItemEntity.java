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
@Table(name = "DMS_OBJECT_ASSIGN_ROLE_ITEM")
@DynamicUpdate
@DynamicInsert
public class DmsObjectAssignRoleItemEntity extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -753013550006398682L;

    // 分配角色的UUID
    private String assignRoleUuid;
    // 分配的组织相关ID或角色
    private String orgId;
    // 排序号
    private Integer sortOrder;

    /**
     * @return the assignRoleUuid
     */
    public String getAssignRoleUuid() {
        return assignRoleUuid;
    }

    /**
     * @param assignRoleUuid 要设置的assignRoleUuid
     */
    public void setAssignRoleUuid(String assignRoleUuid) {
        this.assignRoleUuid = assignRoleUuid;
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
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

}
