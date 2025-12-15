package com.wellsoft.pt.user.dto;

import com.google.common.collect.Lists;
import com.wellsoft.pt.org.entity.OrgUserEntity;
import com.wellsoft.pt.user.entity.UserAccountEntity;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import com.wellsoft.pt.user.entity.UserInfoExtEntity;
import com.wellsoft.pt.user.entity.UserNameI18nEntity;
import com.wellsoft.pt.user.enums.UserTypeEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2020年08月10日   chenq	 Create
 * </pre>
 */
public class UserDto implements Serializable {

    private String uuid;
    /**
     * 登录名
     **/
    private String loginName;
    /**
     * 登录名
     **/
    private String extLoginName;

    private String enName;
    /**
     * 登录类型
     **/
    private String extLoginType;
    /**
     * 用户名
     **/
    private String userName;

    private String localUserName;

    private String pinYin;
    /**
     * 密码
     **/
    private String password;
    /**
     * 用户类型
     **/
    private UserTypeEnum type;

    private UserAccountEntity.Type accountType;

    private UserAccountEntity.LockCause lockCause;

    private Date unlockTime;

    private UserInfoEntity.Gender gender;

    private List<String> roleUuids = Lists.newArrayList();

    private String remark;

    private Boolean isEnabled;
    private Boolean isAccountNonExpired;
    private Boolean isAccountNonLocked;
    private Boolean isCredentialsNonExpired;
    /**
     * 邮箱地址
     **/
    private String mail;
    /**
     * 手机号码
     **/
    private String ceilPhoneNumber;

    /**
     * // 证照号码由业务决定，自己存自已取
     **/
    private String licenseNumber;
    /**
     * // 单位名称
     **/
    private String unitName;
    /**
     * // 证照类型由业务决定，自己存自已取
     **/
    private String licenseType;

    private String userId;
    private String avatar;


    /**
     * 用户信息扩展表
     **/
    @Deprecated
    private List<UserExtraInfoDto> userExtraInfoDtos = new ArrayList<>();

    private List<UserInfoExtEntity> userInfoExts;

    private List<UserNameI18nEntity> userNameI18ns;


    //// 证件信息
    // private Set<UserCredentialDto> userCredentialDtos = Sets.newLinkedHashSet();


    private String mainJobId; // 主职
    private List<String> otherJobIds; // 其他职位
    @Deprecated
    private List<String> reportTos;// 直接汇报人ID
    private Long orgVersionUuid;// 归属组织版本UUID
    private String belongToOrgElement;// 归属与指定组织元素
    private String userNo;// 编号
    private Map<String, List<String>> orgReportTos; // 组织节点汇报人

    private List<OrgUserEntity> orgUsers = Lists.newArrayList(); // 组织用户身份关系

    public UserDto() {
    }

    public UserDto(String loginName, String userName, String password, UserTypeEnum type) {
        this.loginName = loginName;
        this.userName = userName;
        this.password = password;
        this.type = type;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public List<UserExtraInfoDto> getUserExtraInfoDtos() {
        return userExtraInfoDtos;
    }

    public void setUserExtraInfoDtos(List<UserExtraInfoDto> userExtraInfoDtos) {
        this.userExtraInfoDtos = userExtraInfoDtos;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserTypeEnum getType() {
        return type;
    }

    public void setType(UserTypeEnum type) {
        this.type = type;
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

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCeilPhoneNumber() {
        return ceilPhoneNumber;
    }

    public void setCeilPhoneNumber(String ceilPhoneNumber) {
        this.ceilPhoneNumber = ceilPhoneNumber;
    }

    // public Set<UserCredentialDto> getUserCredentialDtos() {
    // return userCredentialDtos;
    // }
    //
    // public void setUserCredentialDtos(Set<UserCredentialDto> userCredentialDtos)
    // {
    // this.userCredentialDtos = userCredentialDtos;
    // }


    public String getUserId() {
        return this.userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(final String avatar) {
        this.avatar = avatar;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }


    public Long getOrgVersionUuid() {
        return this.orgVersionUuid;
    }

    public void setOrgVersionUuid(final Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }

    public String getMainJobId() {
        return this.mainJobId;
    }

    public void setMainJobId(final String mainJobId) {
        this.mainJobId = mainJobId;
    }

    public List<String> getOtherJobIds() {
        return this.otherJobIds;
    }

    public void setOtherJobIds(final List<String> otherJobIds) {
        this.otherJobIds = otherJobIds;
    }

    public List<String> getReportTos() {
        return this.reportTos;
    }

    public void setReportTos(final List<String> reportTos) {
        this.reportTos = reportTos;
    }

    public String getBelongToOrgElement() {
        return this.belongToOrgElement;
    }

    public void setBelongToOrgElement(String belongToOrgElement) {
        this.belongToOrgElement = belongToOrgElement;
    }

    public String getEnName() {
        return this.enName;
    }

    public void setEnName(final String enName) {
        this.enName = enName;
    }

    public List<UserInfoExtEntity> getUserInfoExts() {
        return this.userInfoExts;
    }

    public void setUserInfoExts(final List<UserInfoExtEntity> userInfoExts) {
        this.userInfoExts = userInfoExts;
    }

    public String getUserNo() {
        return this.userNo;
    }

    public void setUserNo(final String userNo) {
        this.userNo = userNo;
    }

    public UserInfoEntity.Gender getGender() {
        return gender;
    }

    public void setGender(UserInfoEntity.Gender gender) {
        this.gender = gender;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public UserAccountEntity.Type getAccountType() {
        return accountType;
    }

    public void setAccountType(UserAccountEntity.Type accountType) {
        this.accountType = accountType;
    }

    public List<String> getRoleUuids() {
        return roleUuids;
    }

    public void setRoleUuids(List<String> roleUuids) {
        this.roleUuids = roleUuids;
    }

    public Map<String, List<String>> getOrgReportTos() {
        return orgReportTos;
    }

    public void setOrgReportTos(Map<String, List<String>> orgReportTos) {
        this.orgReportTos = orgReportTos;
    }

    public List<OrgUserEntity> getOrgUsers() {
        return orgUsers;
    }

    public void setOrgUsers(List<OrgUserEntity> orgUsers) {
        this.orgUsers = orgUsers;
    }

    public String getPinYin() {
        return pinYin;
    }

    public void setPinYin(String pinYin) {
        this.pinYin = pinYin;
    }

    public List<UserNameI18nEntity> getUserNameI18ns() {
        return userNameI18ns;
    }

    public void setUserNameI18ns(List<UserNameI18nEntity> userNameI18ns) {
        this.userNameI18ns = userNameI18ns;
    }


    public String getLocalUserName() {
        return localUserName;
    }

    public void setLocalUserName(String localUserName) {
        this.localUserName = localUserName;
    }

    public UserAccountEntity.LockCause getLockCause() {
        return lockCause;
    }

    public void setLockCause(UserAccountEntity.LockCause lockCause) {
        this.lockCause = lockCause;
    }

    public Date getUnlockTime() {
        return unlockTime;
    }

    public void setUnlockTime(Date unlockTime) {
        this.unlockTime = unlockTime;
    }
}
