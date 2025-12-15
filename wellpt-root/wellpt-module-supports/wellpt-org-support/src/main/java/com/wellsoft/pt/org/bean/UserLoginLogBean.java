/*
 * @(#)2012-12-31 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.bean;

import com.wellsoft.pt.org.entity.UserLoginLog;

import java.util.Date;

/**
 * Description: 用户登录日记
 *
 * @author
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-13	liuzq		2013-10-13
 * </pre>
 * @date 2013-10-13
 */
public class UserLoginLogBean extends UserLoginLog {

    private static final long serialVersionUID = 2627861934930043189L;

    // 登录名
    private String loginName;
    // 用户名
    private String userName;
    // 性别
    private String sex;
    // 员工编号
    private String employeeNumber;
    // 部门名称
    private String departmentName;
    // 岗位名称
    private String jobName;
    // 登录IP
    private String loginIp;
    //登录时间
    private Date loginTime;
    //注销时间
    private Date logoutTime;

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLogoutTime() {
        return logoutTime;
    }

    public void setLogoutTime(Date logoutTime) {
        this.logoutTime = logoutTime;
    }

}
