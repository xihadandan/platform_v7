package com.wellsoft.pt.multi.org.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 保存配置输入输出类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/3/25.1	    zenghw		2021/3/25		    Create
 * </pre>
 * @date 2021/3/25
 */
@ApiModel("密码配置对象")
public class MultiOrgPwdSettingDto {

    @ApiModelProperty("uuid主键")
    private String uuid;
    // 超过输错次数触发
    @ApiModelProperty(value = "超过输错次数")
    private Integer moreTheanErrorNumber;
    @ApiModelProperty(value = "是否密码输入错误锁定:0：否，1：是", required = true)
    private Integer isPwdErrorLock;
    @ApiModelProperty(value = "是否密码有效期 :0：否，1：是", required = true)
    private Integer isPwdValidity;
    @ApiModelProperty("密码有效期-提前多少天提醒")
    private Integer advancePwdDay;
    @ApiModelProperty("最小长度要求")
    private Integer minLength;
    @ApiModelProperty(value = "是否登录时校验密码格式 :0：否，1：是", required = true)
    private Integer isEnforceUpdatePwd;
    @ApiModelProperty("账号锁定分钟数")
    private Integer accountLockMinute;
    @ApiModelProperty("密码有效期")
    private Integer pwdValidity;
    @ApiModelProperty(value = "字符要求 LA01 :至少1种 LA02: 至少2种 LA02:3种", required = true)
    private String letterAsk;
    @ApiModelProperty("最大长度要求")
    private Integer maxLength;
    @ApiModelProperty(value = "字母限制 LL01 : 是 LL02:否", required = true)
    private String letterLimited;
    // 关闭账号锁定时，自动解锁【已锁定账号】
    @ApiModelProperty(value = "关闭账号锁定时，自动解锁【已锁定账号】 0：否\n 1：是", required = true)
    private Integer isPwdErrorLockAutoUnlock;

    // 非用户设置的密码-登录时强制修改
    @ApiModelProperty(value = "非用户设置的密码-登录时强制修改 0：否\n 1：是", required = true)
    private Integer isNotUserSetPwdUpdate;

    // 管理员设置的密码类型
    @ApiModelProperty(value = "管理员设置的密码类型 ASPT01: 随机密码\n ASPT02:自定义密码", required = true)
    private String adminSetPwdType;

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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getMoreTheanErrorNumber() {
        return moreTheanErrorNumber;
    }

    public void setMoreTheanErrorNumber(Integer moreTheanErrorNumber) {
        this.moreTheanErrorNumber = moreTheanErrorNumber;
    }

    public Integer getIsPwdErrorLock() {
        return isPwdErrorLock;
    }

    public void setIsPwdErrorLock(Integer isPwdErrorLock) {
        this.isPwdErrorLock = isPwdErrorLock;
    }

    public Integer getIsPwdValidity() {
        return isPwdValidity;
    }

    public void setIsPwdValidity(Integer isPwdValidity) {
        this.isPwdValidity = isPwdValidity;
    }

    public Integer getAdvancePwdDay() {
        return advancePwdDay;
    }

    public void setAdvancePwdDay(Integer advancePwdDay) {
        this.advancePwdDay = advancePwdDay;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getIsEnforceUpdatePwd() {
        return isEnforceUpdatePwd;
    }

    public void setIsEnforceUpdatePwd(Integer isEnforceUpdatePwd) {
        this.isEnforceUpdatePwd = isEnforceUpdatePwd;
    }

    public Integer getAccountLockMinute() {
        return accountLockMinute;
    }

    public void setAccountLockMinute(Integer accountLockMinute) {
        this.accountLockMinute = accountLockMinute;
    }

    public Integer getPwdValidity() {
        return pwdValidity;
    }

    public void setPwdValidity(Integer pwdValidity) {
        this.pwdValidity = pwdValidity;
    }

    public String getLetterAsk() {
        return letterAsk;
    }

    public void setLetterAsk(String letterAsk) {
        this.letterAsk = letterAsk;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }

    public String getLetterLimited() {
        return letterLimited;
    }

    public void setLetterLimited(String letterLimited) {
        this.letterLimited = letterLimited;
    }
}
