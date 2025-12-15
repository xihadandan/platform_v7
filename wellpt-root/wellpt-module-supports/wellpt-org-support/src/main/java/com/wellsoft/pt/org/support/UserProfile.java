/*
 * @(#)2013-6-1 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.support;

import com.wellsoft.context.jdbc.support.QueryItem;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-6-1.1	zhulh		2013-6-1		Create
 * </pre>
 * @date 2013-6-1
 */
public class UserProfile implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7634512574841441384L;

    // 用户UUID
    private String uuid;
    // 用户ID
    private String userId;
    // 登录名
    private String loginName;
    // 用户名
    private String userName;
    // 所属主部门ID
    private String departmentId;
    // 所属主部门
    private String departmentName;
    // 所属主路径
    private String departmentPath;
    // 岗位
    private String jobName;
    // 上次登录时间
    private Date lastLoginTime;
    // 手机
    private String mobilePhone;
    // 办公电话
    private String officePhone;
    // 办公电话
    private String email;
    // 传真
    private String fax;
    // photoUuid
    private String photoUuid;
    // 图片
    private String photoUrl;

    private String sex;

    // 是否管理员
    private Boolean isAdmin;

    // 是否允许登录后台
    private Boolean isAllowedBack;

    // 主职、挂职租户信息
    private List<QueryItem> gzTenants;

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    /**
     * @return the loginName
     */
    public String getLoginName() {
        return loginName;
    }

    /**
     * @param loginName 要设置的loginName
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName 要设置的userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the departmentId
     */
    public String getDepartmentId() {
        return departmentId;
    }

    /**
     * @param departmentId 要设置的departmentId
     */
    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    /**
     * @return the departmentName
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * @param departmentName 要设置的departmentName
     */
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    /**
     * @return the departmentPath
     */
    public String getDepartmentPath() {
        return departmentPath;
    }

    /**
     * @param departmentPath 要设置的departmentPath
     */
    public void setDepartmentPath(String departmentPath) {
        this.departmentPath = departmentPath;
    }

    /**
     * @return the jobName
     */
    public String getJobName() {
        if (StringUtils.isNotEmpty(jobName)) {
            return jobName.substring(jobName.lastIndexOf("/") + 1);
        }
        return jobName;
    }

    /**
     * @param jobName 要设置的jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * @return the lastLoginTime
     */
    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    /**
     * @param lastLoginTime 要设置的lastLoginTime
     */
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    /**
     * @return the mobilePhone
     */
    public String getMobilePhone() {
        return mobilePhone;
    }

    /**
     * @param mobilePhone 要设置的mobilePhone
     */
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    /**
     * @return the officePhone
     */
    public String getOfficePhone() {
        return officePhone;
    }

    /**
     * @param officePhone 要设置的officePhone
     */
    public void setOfficePhone(String officePhone) {
        this.officePhone = officePhone;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email 要设置的email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the fax
     */
    public String getFax() {
        return fax;
    }

    /**
     * @param fax 要设置的fax
     */
    public void setFax(String fax) {
        this.fax = fax;
    }

    /**
     * @return the photoUuid
     */
    public String getPhotoUuid() {
        return photoUuid;
    }

    /**
     * @param photoUuid 要设置的photoUuid
     */
    public void setPhotoUuid(String photoUuid) {
        this.photoUuid = photoUuid;
    }

    /**
     * @return the photoUrl
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    /**
     * @param photoUrl 要设置的photoUrl
     */
    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * @return the isAdmin
     */
    public Boolean getIsAdmin() {
        return isAdmin == null ? false : isAdmin;
    }

    /**
     * @param isAdmin 要设置的isAdmin
     */
    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    /**
     * @return the isAllowedBack
     */
    public Boolean getIsAllowedBack() {
        return isAllowedBack == null ? false : isAllowedBack;
    }

    /**
     * @param isAllowedBack 要设置的isAllowedBack
     */
    public void setIsAllowedBack(Boolean isAllowedBack) {
        this.isAllowedBack = isAllowedBack;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * @return the gzTenants
     */
    public List<QueryItem> getGzTenants() {
        return gzTenants;
    }

    /**
     * @param gzTenants 要设置的gzTenants
     */
    public void setGzTenants(List<QueryItem> gzTenants) {
        this.gzTenants = gzTenants;
    }

}
