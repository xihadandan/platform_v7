/*
 * @(#)2021-03-25 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 数据库表MULTI_ORG_PWD_SETTING的实体类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-03-25.1	zenghw		2021-03-25		Create
 * </pre>
 * @date 2021-03-25
 */
@Entity
@Table(name = "MULTI_ORG_PWD_SETTING")
@DynamicUpdate
@DynamicInsert
public class MultiOrgPwdSettingEntity extends IdEntity {

    private static final long serialVersionUID = 1616644868572L;

    // 超过输错次数触发
    @ApiModelProperty("超过输错次数")
    private Integer moreTheanErrorNumber;
    // 枚举 IsPwdErrorLockEnum
    // 0：否，1：是
    @ApiModelProperty("是否密码输入错误锁定")
    private Integer isPwdErrorLock;
    // 枚举 IsPwdValidityEnum
    // 0：否，1：是
    @ApiModelProperty("是否密码有效期")
    private Integer isPwdValidity;
    @ApiModelProperty("密码有效期-提前多少天提醒")
    private Integer advancePwdDay;
    @ApiModelProperty("最小长度要求")
    private Integer minLength;
    // 枚举 IsEnforceUpdatePwdEnum
    @ApiModelProperty("是否登录时校验密码格式")
    // 0：否，1：是
    private Integer isEnforceUpdatePwd;
    @ApiModelProperty("账号锁定分钟数")
    private Integer accountLockMinute;
    @ApiModelProperty("密码有效期")
    private Integer pwdValidity;
    // 字母、数字、特殊字符 至少包含 枚举：LetterAskEnum
    // LA01 :至少1种
    // LA02: 至少2种
    // LA02:3种
    @ApiModelProperty("字符要求")
    private String letterAsk;
    @ApiModelProperty("最大长度要求")
    private Integer maxLength;
    // 是否必须要有大写、小写 枚举：LetterLimitedEnum
    // LL01 : 是
    // LL02:否
    @ApiModelProperty("字母限制")
    private String letterLimited;

    // 关闭账号锁定时，自动解锁【已锁定账号】
    @ApiModelProperty("关闭账号锁定时，自动解锁【已锁定账号】 0：否\n 1：是")
    private Integer isPwdErrorLockAutoUnlock;

    // 非用户设置的密码-登录时强制修改
    @ApiModelProperty("非用户设置的密码-登录时强制修改 0：否\n 1：是")
    private Integer isNotUserSetPwdUpdate;

    // 管理员设置的密码类型
    @ApiModelProperty("管理员设置的密码类型 ASPT01: 随机密码\n ASPT02:自定义密码")
    private String adminSetPwdType;

    public MultiOrgPwdSettingEntity() {
    }

    public Integer getIsPwdErrorLockAutoUnlock() {
        return isPwdErrorLockAutoUnlock;
    }

    public void setIsPwdErrorLockAutoUnlock(Integer isPwdErrorLockAutoUnlock) {
        this.isPwdErrorLockAutoUnlock = isPwdErrorLockAutoUnlock;
    }

    public Integer getIsNotUserSetPwdUpdate() {
        return isNotUserSetPwdUpdate;
    }

    public void setIsNotUserSetPwdUpdate(Integer isNotUserSetPwdUpdate) {
        this.isNotUserSetPwdUpdate = isNotUserSetPwdUpdate;
    }

    public String getAdminSetPwdType() {
        return adminSetPwdType;
    }

    public void setAdminSetPwdType(String adminSetPwdType) {
        this.adminSetPwdType = adminSetPwdType;
    }

    /**
     * @return the moreTheanErrorNumber
     */
    public Integer getMoreTheanErrorNumber() {
        return this.moreTheanErrorNumber;
    }

    /**
     * @param moreTheanErrorNumber
     */
    public void setMoreTheanErrorNumber(Integer moreTheanErrorNumber) {
        this.moreTheanErrorNumber = moreTheanErrorNumber;
    }

    /**
     * @return the isPwdErrorLock
     */
    public Integer getIsPwdErrorLock() {
        return this.isPwdErrorLock;
    }

    /**
     * @param isPwdErrorLock
     */
    public void setIsPwdErrorLock(Integer isPwdErrorLock) {
        this.isPwdErrorLock = isPwdErrorLock;
    }

    /**
     * @return the isPwdValidity
     */
    public Integer getIsPwdValidity() {
        return this.isPwdValidity;
    }

    /**
     * @param isPwdValidity
     */
    public void setIsPwdValidity(Integer isPwdValidity) {
        this.isPwdValidity = isPwdValidity;
    }

    /**
     * @return the advancePwdDay
     */
    public Integer getAdvancePwdDay() {
        return this.advancePwdDay;
    }

    /**
     * @param advancePwdDay
     */
    public void setAdvancePwdDay(Integer advancePwdDay) {
        this.advancePwdDay = advancePwdDay;
    }

    /**
     * @return the minLength
     */
    public Integer getMinLength() {
        return this.minLength;
    }

    /**
     * @param minLength
     */
    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    /**
     * @return the isEnforceUpdatePwd
     */
    public Integer getIsEnforceUpdatePwd() {
        return this.isEnforceUpdatePwd;
    }

    /**
     * @param isEnforceUpdatePwd
     */
    public void setIsEnforceUpdatePwd(Integer isEnforceUpdatePwd) {
        this.isEnforceUpdatePwd = isEnforceUpdatePwd;
    }

    /**
     * @return the accountLockMinute
     */
    public Integer getAccountLockMinute() {
        return this.accountLockMinute;
    }

    /**
     * @param accountLockMinute
     */
    public void setAccountLockMinute(Integer accountLockMinute) {
        this.accountLockMinute = accountLockMinute;
    }

    /**
     * @return the pwdValidity
     */
    public Integer getPwdValidity() {
        return this.pwdValidity;
    }

    /**
     * @param pwdValidity
     */
    public void setPwdValidity(Integer pwdValidity) {
        this.pwdValidity = pwdValidity;
    }

    /**
     * @return the letterAsk
     */
    public String getLetterAsk() {
        return this.letterAsk;
    }

    /**
     * @param letterAsk
     */
    public void setLetterAsk(String letterAsk) {
        this.letterAsk = letterAsk;
    }

    /**
     * @return the maxLength
     */
    public Integer getMaxLength() {
        return this.maxLength;
    }

    /**
     * @param maxLength
     */
    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * @return the letterLimited
     */
    public String getLetterLimited() {
        return this.letterLimited;
    }

    /**
     * @param letterLimited
     */
    public void setLetterLimited(String letterLimited) {
        this.letterLimited = letterLimited;
    }

}
