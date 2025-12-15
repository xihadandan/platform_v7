/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * Description: 部门相关负责人，包括负责人、分管领导、管理员
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
//@Table(name = "org_department_principal")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class DepartmentPrincipal extends IdEntity {

    private static final long serialVersionUID = 5140191967675967820L;

    private User user;

    private Department department;

    //是否部门负责人
    private Boolean isPrincipal;

    //是否部门分管领导人
    private Boolean isBranched;

    //是否部门管理员
    private Boolean isManager;

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
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
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
     * @return the department
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.REFRESH})
    @JoinColumn(name = "department_uuid")
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public Department getDepartment() {
        return department;
    }

    /**
     * @param department 要设置的department
     */
    public void setDepartment(Department department) {
        this.department = department;
    }

    /**
     * @return the isPrincipal
     */
    public Boolean getIsPrincipal() {
        return isPrincipal;
    }

    /**
     * @param isPrincipal 要设置的isPrincipal
     */
    public void setIsPrincipal(Boolean isPrincipal) {
        this.isPrincipal = isPrincipal;
    }

    /**
     * @return the isBranched
     */
    public Boolean getIsBranched() {
        return isBranched;
    }

    /**
     * @param isBranched 要设置的isBranched
     */
    public void setIsBranched(Boolean isBranched) {
        this.isBranched = isBranched;
    }

    /**
     * @return the isManager
     */
    public Boolean getIsManager() {
        return isManager;
    }

    /**
     * @param isManager 要设置的isManager
     */
    public void setIsManager(Boolean isManager) {
        this.isManager = isManager;
    }

}
