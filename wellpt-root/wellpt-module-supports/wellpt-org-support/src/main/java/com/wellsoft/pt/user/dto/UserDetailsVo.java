package com.wellsoft.pt.user.dto;

import com.google.common.collect.Sets;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import org.apache.commons.compress.utils.Lists;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2023年02月03日   chenq	 Create
 * </pre>
 */
public class UserDetailsVo implements Serializable {

    private static long serialVersionUID = 6158468382340973673L;

    private String uuid;

    private String userNo;

    private String userName;

    private String localUserName;

    private String enName;

    private String loginName;

    private String password;

    private int userType;

    private String avatar;

    private String userId;

    private String mainJobId;

    private String mainJobName;

    private String mainJobNamePath;

    private String mainJobIdPath;

    private UserInfoEntity.Gender gender;

    private String workState;

    private List<String> otherJobIds = Lists.newArrayList();

    private List<String> otherJobIdPaths = Lists.newArrayList();

    private List<String> otherJobNames = Lists.newArrayList();

    private List<String> otherJobNamePaths = Lists.newArrayList();

    private Set<String> subordinateUsers = Sets.newHashSet(); // 直接下属
    private String unitName; // 单位
    private String deptName; // 部门
    private String jobRankName;// 职级
    private String dutyName;// 职务
    private String directorUserId;// 直接上级用户ID
    private Date lastLoginTime;
    private String ceilPhoneNumber;
    private String mail;
    private String businessPhoneNumber;
    private String familyPhoneNumber;
    private String idNumber;
    private String workLocation;


    private boolean enabled;
    private boolean accountNonExpired;
    private boolean accountNonLocked;
    private boolean credentialsNonExpired;

    public String getUserNo() {
        return this.userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginName() {
        return this.loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserType() {
        return this.userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMainJobId() {
        return this.mainJobId;
    }

    public void setMainJobId(String mainJobId) {
        this.mainJobId = mainJobId;
    }

    public String getMainJobName() {
        return this.mainJobName;
    }

    public void setMainJobName(String mainJobName) {
        this.mainJobName = mainJobName;
    }

    public String getMainJobNamePath() {
        return this.mainJobNamePath;
    }

    public void setMainJobNamePath(String mainJobNamePath) {
        this.mainJobNamePath = mainJobNamePath;
    }

    public String getMainJobIdPath() {
        return this.mainJobIdPath;
    }

    public void setMainJobIdPath(String mainJobIdPath) {
        this.mainJobIdPath = mainJobIdPath;
    }

    public List<String> getOtherJobIds() {
        return this.otherJobIds;
    }

    public void setOtherJobIds(List<String> otherJobIds) {
        this.otherJobIds = otherJobIds;
    }

    public List<String> getOtherJobIdPaths() {
        return this.otherJobIdPaths;
    }

    public void setOtherJobIdPaths(List<String> otherJobIdPaths) {
        this.otherJobIdPaths = otherJobIdPaths;
    }

    public List<String> getOtherJobNames() {
        return this.otherJobNames;
    }

    public void setOtherJobNames(List<String> otherJobNames) {
        this.otherJobNames = otherJobNames;
    }

    public List<String> getOtherJobNamePaths() {
        return this.otherJobNamePaths;
    }

    public void setOtherJobNamePaths(List<String> otherJobNamePaths) {
        this.otherJobNamePaths = otherJobNamePaths;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    public void setAccountNonExpired(boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    public void setCredentialsNonExpired(boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getJobRankName() {
        return jobRankName;
    }

    public void setJobRankName(String jobRankName) {
        this.jobRankName = jobRankName;
    }

    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    public String getDirectorUserId() {
        return directorUserId;
    }

    public void setDirectorUserId(String directorUserId) {
        this.directorUserId = directorUserId;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public void setCeilPhoneNumber(String ceilPhoneNumber) {
        this.ceilPhoneNumber = ceilPhoneNumber;
    }

    public String getCeilPhoneNumber() {
        return ceilPhoneNumber;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public UserInfoEntity.Gender getGender() {
        return gender;
    }

    public void setGender(UserInfoEntity.Gender gender) {
        this.gender = gender;
    }

    public Set<String> getSubordinateUsers() {
        return subordinateUsers;
    }

    public void setSubordinateUsers(Set<String> subordinateUsers) {
        this.subordinateUsers = subordinateUsers;
    }

    public String getBusinessPhoneNumber() {
        return businessPhoneNumber;
    }

    public void setBusinessPhoneNumber(String businessPhoneNumber) {
        this.businessPhoneNumber = businessPhoneNumber;
    }

    public String getFamilyPhoneNumber() {
        return familyPhoneNumber;
    }

    public void setFamilyPhoneNumber(String familyPhoneNumber) {
        this.familyPhoneNumber = familyPhoneNumber;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getEnName() {
        return enName;
    }

    public void setEnName(String enName) {
        this.enName = enName;
    }

    public String getWorkState() {
        return workState;
    }

    public void setWorkState(String workState) {
        this.workState = workState;
    }

    public String getLocalUserName() {
        return localUserName;
    }

    public void setLocalUserName(String localUserName) {
        this.localUserName = localUserName;
    }

    public String getWorkLocation() {
        return workLocation;
    }

    public void setWorkLocation(String workLocation) {
        this.workLocation = workLocation;
    }
}
