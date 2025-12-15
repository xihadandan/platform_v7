/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Description: B岗人员
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-15.1	zhulh		2013-1-15		Create
 * </pre>
 * @date 2013-1-15
 */
//@Entity
//@Table(name = "org_user_minor_job")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class UserDeputy extends IdEntity {

    private static final long serialVersionUID = -3201135006538038986L;
    private User user;

    private User deputy;

    private String orgId;
    private String tenantId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the user
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.REFRESH})
    @JoinColumn(name = "user_uuid")
    public User getUser() {
        return user;
    }

    /**
     * @param user 要设置的user
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the deputy
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deputy_uuid")
    public User getDeputy() {
        return deputy;
    }

    /**
     * @param deputy 要设置的deputy
     */
    public void setDeputy(User deputy) {
        this.deputy = deputy;
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

}
