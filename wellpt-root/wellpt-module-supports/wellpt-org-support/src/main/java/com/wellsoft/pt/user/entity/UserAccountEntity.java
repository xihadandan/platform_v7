package com.wellsoft.pt.user.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 用户账号表
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月10日   chenq	 Create
 * </pre>
 */
@Entity
@Table(name = "USER_ACCOUNT")
@DynamicUpdate
@DynamicInsert
public class UserAccountEntity extends IdEntity {

    private static final long serialVersionUID = -3790256602904692194L;
    private String loginName;
    private String password;
    private Boolean isEnabled;
    private Boolean isAccountNonExpired;
    private Boolean isAccountNonLocked;
    private Boolean isCredentialsNonExpired;
    private String extLoginName;
    private String extLoginType;

    private Type type;
    private Date expiredTime;
    private Date passwordExpiredTime;
    private String email;
    private String tel;
    private Date lastLoginTime;

    private Date modifyPasswordTime;
    private LockCause lockCause;
    private Integer passwordErrorNum;
    private Boolean passwordModifiedByUser;
    private Date lockTime;
    private Date unlockTime;


    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public Boolean getIsAccountNonExpired() {
        return isAccountNonExpired;
    }

    public void setIsAccountNonExpired(Boolean accountNonExpired) {
        isAccountNonExpired = accountNonExpired;
    }

    public Boolean getIsAccountNonLocked() {
        return isAccountNonLocked;
    }

    public void setIsAccountNonLocked(Boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    public Boolean getIsCredentialsNonExpired() {
        return isCredentialsNonExpired;
    }

    public void setIsCredentialsNonExpired(Boolean credentialsNonExpired) {
        isCredentialsNonExpired = credentialsNonExpired;
    }

    public String getExtLoginName() {
        return extLoginName;
    }

    public void setExtLoginName(String extLoginName) {
        this.extLoginName = extLoginName;
    }

    public String getExtLoginType() {
        return extLoginType;
    }

    public void setExtLoginType(String extLoginType) {
        this.extLoginType = extLoginType;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public Date getExpiredTime() {
        return this.expiredTime;
    }

    public void setExpiredTime(final Date expiredTime) {
        this.expiredTime = expiredTime;
    }

    public Date getPasswordExpiredTime() {
        return this.passwordExpiredTime;
    }

    public void setPasswordExpiredTime(final Date passwordExpiredTime) {
        this.passwordExpiredTime = passwordExpiredTime;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getTel() {
        return this.tel;
    }

    public void setTel(final String tel) {
        this.tel = tel;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Date getModifyPasswordTime() {
        return modifyPasswordTime;
    }

    public void setModifyPasswordTime(Date modifyPasswordTime) {
        this.modifyPasswordTime = modifyPasswordTime;
    }

    public LockCause getLockCause() {
        return lockCause;
    }

    public void setLockCause(LockCause lockCause) {
        this.lockCause = lockCause;
    }

    public Boolean getPasswordModifiedByUser() {
        return passwordModifiedByUser;
    }

    public void setPasswordModifiedByUser(Boolean passwordModifiedByUser) {
        this.passwordModifiedByUser = passwordModifiedByUser;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    /**
     * 账号类型
     */
    public static enum Type {
        SUPER_ADMIN, // 超级管理员
        PT_ADMIN, // 平台管理员
        TENANT_ADMIN, // 租户管理员
        DEVELOPER, // 开发者
        BUSINESS_USER, // 业务用户
    }


    public static enum LockCause {
        PASSWORD_ERROR, PASSWORD_EXPIRED, MANAGER_LOCK;
    }

    public Integer getPasswordErrorNum() {
        return passwordErrorNum;
    }

    public void setPasswordErrorNum(Integer passwordErrorNum) {
        this.passwordErrorNum = passwordErrorNum;
    }

    public Date getUnlockTime() {
        return unlockTime;
    }

    public void setUnlockTime(Date unlockTime) {
        this.unlockTime = unlockTime;
    }
}
