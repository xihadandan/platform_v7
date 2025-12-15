/*
 * @(#)2017年11月24日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.bean;

import com.wellsoft.context.jdbc.support.BaseQueryItem;
import com.wellsoft.pt.multi.org.entity.MultiOrgUserInfo;

import java.util.Date;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年11月24日.1	zyguo		2017年11月24日		Create
 * </pre>
 * @date 2017年11月24日
 */
public class OrgUserTableVo extends MultiOrgUserInfo implements BaseQueryItem {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2734382644907473611L;
    // 账号编码
    private String code;
    // 账号ID,也是用户ID
    private String id;
    // 类型, 0:普通账号，1: 平台管理员，2：业务管理员
    private Integer type;
    // 最后一次登录时间
    private Date lastLoginTime;
    // 登录名
    private String loginName;
    // 登录名转小写格式
    private String loginNameLowerCase;
    // 姓名
    private String userName;
    // 登录密码
    private String password;
    // 登录名哈希算法对就的值
    private String md5LoginName;
    // 账号的归属组织ID
    private String systemUnitId;
    // 账号的备注信息
    private String remark;
    // 是否过期：1：过期， 0：正常
    private Integer isExpired;
    // 是否冻结锁住 0 正常, 1：锁住
    private Integer isLocked;
    // 账号状态 0：正常，1：禁用
    private Integer isForbidden;
    private String jobIds;
    private String deptIds;
    private String jobNames; // 用户的所有职位名称
    private String deptNames; // 用户归属的所有部门名称

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the type
     */
    public Integer getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(Integer type) {
        this.type = type;
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
     * @return the loginNameLowerCase
     */
    public String getLoginNameLowerCase() {
        return loginNameLowerCase;
    }

    /**
     * @param loginNameLowerCase 要设置的loginNameLowerCase
     */
    public void setLoginNameLowerCase(String loginNameLowerCase) {
        this.loginNameLowerCase = loginNameLowerCase;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password 要设置的password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the md5LoginName
     */
    public String getMd5LoginName() {
        return md5LoginName;
    }

    /**
     * @param md5LoginName 要设置的md5LoginName
     */
    public void setMd5LoginName(String md5LoginName) {
        this.md5LoginName = md5LoginName;
    }

    /**
     * @return the unitId
     */
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * @param systemUnitId 要设置的unitId
     */
    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    /**
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the isExpired
     */
    public Integer getIsExpired() {
        return isExpired;
    }

    /**
     * @param isExpired 要设置的isExpired
     */
    public void setIsExpired(Integer isExpired) {
        this.isExpired = isExpired;
    }

    /**
     * @return the isLocked
     */
    public Integer getIsLocked() {
        return isLocked;
    }

    /**
     * @param isLocked 要设置的isLocked
     */
    public void setIsLocked(Integer isLocked) {
        this.isLocked = isLocked;
    }

    /**
     * @return the isForbidden
     */
    public Integer getIsForbidden() {
        return isForbidden;
    }

    /**
     * @param isForbidden 要设置的isForbidden
     */
    public void setIsForbidden(Integer isForbidden) {
        this.isForbidden = isForbidden;
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
     * @return the jobNames
     */
    public String getJobNames() {
        return jobNames;
    }

    /**
     * @param jobNames 要设置的jobNames
     */
    public void setJobNames(String jobNames) {
        this.jobNames = jobNames;
    }

    /**
     * @return the deptNames
     */
    public String getDeptNames() {
        return deptNames;
    }

    /**
     * @param deptNames 要设置的deptNames
     */
    public void setDeptNames(String deptNames) {
        this.deptNames = deptNames;
    }

    /**
     * @return the jobIds
     */
    public String getJobIds() {
        return jobIds;
    }

    /**
     * @param jobIds 要设置的jobIds
     */
    public void setJobIds(String jobIds) {
        this.jobIds = jobIds;
    }

    /**
     * @return the deptIds
     */
    public String getDeptIds() {
        return deptIds;
    }

    /**
     * @param deptIds 要设置的deptIds
     */
    public void setDeptIds(String deptIds) {
        this.deptIds = deptIds;
    }

}
