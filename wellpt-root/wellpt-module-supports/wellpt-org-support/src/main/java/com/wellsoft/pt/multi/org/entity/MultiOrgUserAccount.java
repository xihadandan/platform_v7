/*
 * @(#)2017-12-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
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
 * 2017-12-11.1	zyguo		2017-12-11		Create
 * </pre>
 * @date 2017-12-11
 */
@Entity
@Table(name = "MULTI_ORG_USER_ACCOUNT")
@DynamicUpdate
@DynamicInsert
public class MultiOrgUserAccount extends IdEntity {

    public static final String PT_ACCOUNT_ID = "U0000000000";
    public static final int TYPE_UNIT_ADMIN = 1; // 单位管理员
    public static final int TYPE_USER = 0; // 普通用户
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1512981137434L;
    // 登录名
    private String loginName;

    // 中文登录名
    private String loginNameZh;
    // 登录名转小写格式
    private String loginNameLowerCase;
    // 用户名
    private String userName;
    // 登录密码
    private String password;
    // 账号编码
    private String code;
    // 账号ID,也是用户ID
    private String id;
    // 类型, 0:普通账号，1: 平台管理员，2：业务管理员
    private Integer type;
    // 最后一次登录时间
    private Date lastLoginTime;
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
    // userName 转拼音
    private String userNamePy;
    // userName 转简拼
    private String userNameJp;
    // 输入正确后清零-重置次数为0条件：
    // 登录时，锁定时间结束，重置
    // 登录时，输入正确后清零
    private Integer pwdErrorNum;
    // 最后一次账号解锁时间
    private Date lastUnLockedTime;
    // 最后一次账号锁定时间
    private Date lastLockedTime;
    // 密码创建重置时间
    private Date pwdCreateTime;
    // 密码错误锁定 0 正常, 1：锁住 PwdErrorLockEnum
    private Integer pwdErrorLock;
    // 是否用户自己设置的密码
    private Integer isUserSettingPwd;

    public Integer getIsUserSettingPwd() {
        return isUserSettingPwd;
    }

    public void setIsUserSettingPwd(Integer isUserSettingPwd) {
        this.isUserSettingPwd = isUserSettingPwd;
    }

    public Integer getPwdErrorLock() {
        return pwdErrorLock;
    }

    public void setPwdErrorLock(Integer pwdErrorLock) {
        this.pwdErrorLock = pwdErrorLock;
    }

    public Integer getPwdErrorNum() {
        return pwdErrorNum;
    }

    public void setPwdErrorNum(Integer pwdErrorNum) {
        this.pwdErrorNum = pwdErrorNum;
    }

    public Date getLastUnLockedTime() {
        return lastUnLockedTime;
    }

    public void setLastUnLockedTime(Date lastUnLockedTime) {
        this.lastUnLockedTime = lastUnLockedTime;
    }

    public Date getLastLockedTime() {
        return lastLockedTime;
    }

    public void setLastLockedTime(Date lastLockedTime) {
        this.lastLockedTime = lastLockedTime;
    }

    public Date getPwdCreateTime() {
        return pwdCreateTime;
    }

    public void setPwdCreateTime(Date pwdCreateTime) {
        this.pwdCreateTime = pwdCreateTime;
    }

    /**
     * @return the isExpired
     */
    @Column(name = "IS_EXPIRED")
    public Integer getIsExpired() {
        return this.isExpired;
    }

    /**
     * @param isExpired
     */
    public void setIsExpired(Integer isExpired) {
        this.isExpired = isExpired;
    }

    /**
     * @return the isLocked
     */
    @Column(name = "IS_LOCKED")
    public Integer getIsLocked() {
        return this.isLocked;
    }

    /**
     * @param isLocked
     */
    public void setIsLocked(Integer isLocked) {
        this.isLocked = isLocked;
    }

    /**
     * @return the code
     */
    @Column(name = "CODE")
    public String getCode() {
        return this.code;
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the isForbidden
     */
    @Column(name = "IS_FORBIDDEN")
    public Integer getIsForbidden() {
        return this.isForbidden;
    }

    /**
     * @param isForbidden
     */
    public void setIsForbidden(Integer isForbidden) {
        this.isForbidden = isForbidden;
    }

    /**
     * @return the id
     */
    @Column(name = "ID")
    public String getId() {
        return this.id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the type
     */
    @Column(name = "TYPE")
    public Integer getType() {
        return this.type;
    }

    /**
     * @param type
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * @return the lastLoginTime
     */
    @Column(name = "LAST_LOGIN_TIME")
    public Date getLastLoginTime() {
        return this.lastLoginTime;
    }

    /**
     * @param lastLoginTime
     */
    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    /**
     * @return the loginName
     */
    @Column(name = "LOGIN_NAME")
    public String getLoginName() {
        return this.loginName;
    }

    /**
     * @param loginName
     */
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * @return the password
     */
    @Column(name = "PASSWORD")
    public String getPassword() {
        return this.password;
    }

    /**
     * @param password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the systemUnitId
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
     * 如何描述该方法
     *
     * @param vo
     * @param b
     */
    public void setAttrFromOrgUserVo(OrgUserVo vo, boolean isNew) {
        // 密码和ID，不能放进来，因为添加和修改对应的操作是不一样的
        this.loginName = vo.getLoginName();
        this.loginNameZh = vo.getLoginNameZh();
        this.isForbidden = vo.getIsForbidden();
        this.userName = vo.getUserName();
        this.systemUnitId = vo.getSystemUnitId();
        this.code = vo.getCode();
        this.loginNameLowerCase = vo.getLoginName().toLowerCase();
        this.remark = vo.getRemark();
        this.isLocked = vo.getIsLocked();
        if (isNew) { // 新账号设置默认值
            this.isExpired = 0;
            this.type = vo.getType(); // 账号类型创建完，也不能修改
        }
    }

    /**
     * @return the md5LoginName
     */
    @Column(name = "MD5_LOGIN_NAME")
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
     * @return the userNamePy
     */
    public String getUserNamePy() {
        return userNamePy;
    }

    /**
     * @param userName 要设置的userNamePy
     */
    public void setUserNamePy(String userNamePy) {
        this.userNamePy = userNamePy;
    }

    public String getUserNameJp() {
        return userNameJp;
    }

    public void setUserNameJp(String userNameJp) {
        this.userNameJp = userNameJp;
    }

    public String getLoginNameZh() {
        return loginNameZh;
    }

    public void setLoginNameZh(String loginNameZh) {
        this.loginNameZh = loginNameZh;
    }
}
