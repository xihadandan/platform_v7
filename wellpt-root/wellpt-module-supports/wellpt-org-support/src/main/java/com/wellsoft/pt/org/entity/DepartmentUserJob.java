/*
 * @(#)2012-12-31 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;

/**
 * Description: 用户部门岗位表
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2012-12-31.1	zhulh		2012-12-31		Create
 * </pre>
 * @date 2012-12-31
 */
//@Entity
//@Table(name = "org_department_user_job")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class DepartmentUserJob extends IdEntity {

    private static final long serialVersionUID = 8040699009250753162L;

    private User user;

    private Department department;

    //是否主部门
    private Boolean isMajor;

    //岗位名称
    private String jobName;

    //岗位编号
    private String jobCode;

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
     * @return the isMajor
     */
    public Boolean getIsMajor() {
        return isMajor;
    }

    /**
     * @param isMajor 要设置的isMajor
     */
    public void setIsMajor(Boolean isMajor) {
        this.isMajor = isMajor;
    }

    /**
     * @return the jobName
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * @param jobName 要设置的jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * @return the jobCode
     */
    public String getJobCode() {
        return jobCode;
    }

    /**
     * @param jobCode 要设置的jobCode
     */
    public void setJobCode(String jobCode) {
        this.jobCode = jobCode;
    }

}
