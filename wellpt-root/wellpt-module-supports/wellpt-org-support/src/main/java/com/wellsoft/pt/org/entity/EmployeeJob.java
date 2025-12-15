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
 * Description: 人员职位对照表
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-9-24.1  zhengky	2014-9-24	  Create
 * </pre>
 * @date 2014-9-24
 */
//@Entity
//@Table(name = "org_employee_job")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class EmployeeJob extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 4112620172046782916L;

    private Employee employee;

    private Job job;

    //是否主岗位
    private Boolean isMajor;

    //职位名称
    private String JobName;

    // 员工编号
    private String employeeNumber;

    private String tenantId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getJobName() {
        return JobName;
    }

    public void setJobName(String jobName) {
        JobName = jobName;
    }

    /**
     * @return the employee
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @Cascade({CascadeType.REFRESH})
    @JoinColumn(name = "employee_uuid")
    public Employee getEmployee() {
        return employee;
    }

    /**
     * @param employee 要设置的employee
     */
    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Boolean getIsMajor() {
        return isMajor;
    }

    public void setIsMajor(Boolean isMajor) {
        this.isMajor = isMajor;
    }

    /**
     * @return the job
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_uuid")
    public Job getJob() {
        return job;
    }

    /**
     * @param job 要设置的job
     */
    public void setJob(Job job) {
        this.job = job;
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

}
