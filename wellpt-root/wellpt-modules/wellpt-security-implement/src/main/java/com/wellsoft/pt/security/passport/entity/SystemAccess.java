/*
 * @(#)2013-5-3 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.passport.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 系统访问实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-5-3.1	zhulh		2013-5-3		Create
 * </pre>
 * @date 2013-5-3
 */
@Entity
@Table(name = "SYS_SYSTEM_ACCESS")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region = "entity")
@DynamicUpdate
@DynamicInsert
public class SystemAccess extends IdEntity {

    private static final long serialVersionUID = 6123967265258584572L;

    // 用户ID
    private String userId;

    // 允许访问
    private Boolean permit;

    // 排序
    private Integer sortOrder;

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

    /**
     * @return the permit
     */
    public Boolean getPermit() {
        return permit;
    }

    /**
     * @param permit 要设置的permit
     */
    public void setPermit(Boolean permit) {
        this.permit = permit;
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
