/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 组织节点基本类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-11-21.1	zyguo		2017-11-21		Create
 * </pre>
 * @date 2017-11-21
 */
@Entity
@Table(name = "MULTI_ORG_USER_ROLE")
@DynamicUpdate
@DynamicInsert
public class MultiOrgUserRole extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4209204825311365451L;

    // 对应的元素UUID
    private String userId;
    // 对应的角色UUID
    private String roleUuid;

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
