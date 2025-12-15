/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.bean;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.MaxLength;
import com.wellsoft.pt.multi.org.vo.JobRankLevelVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.Date;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-11-21.1	zyguo		2017-11-21		Create
 * </pre>
 * @date 2017-11-21
 */
@Entity
@ApiModel("组织用户对象")
public class OrgUserVo extends IdEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7502329594438242170L;
    // 登录名
    @NotBlank
    @ApiModelProperty("登录名")
    private String loginName;

    @ApiModelProperty("中文用户名")
    private String loginNameZh;
    // 登录密码
    @ApiModelProperty("登录密码")
    private String password;
    // 账号编码
    @NotBlank
    @ApiModelProperty("账号编码")
    private String code;
    // 类型, 0:普通账号，1: 平台管理员，2：业务管理员
    @ApiModelProperty(" 类型, 0:普通账号，1: 平台管理员，2：业务管理员")
    private Integer type;
    // 账号状态 0：正常，1：禁用
    @ApiModelProperty(" 账号状态 0：正常，1：禁用")
    private Integer isForbidden;
    // 账号ID,也是用户ID
    @ApiModelProperty("账号ID,也是用户ID")
    private String id;
    // 账号的归属单位ID
    @ApiModelProperty("账号的归属单位ID")
    private String systemUnitId;
    // 证书主体
    @MaxLength(max = 2000)
    @ApiModelProperty("证书主体")
    private String certificateSubject;
    // 备注
    @MaxLength(max = 255)
    @ApiModelProperty("备注")
    private String remark;
    // 是否锁住
    @ApiModelProperty("是否锁住")
    private Integer isLocked;

    @ApiModelProperty("是否密码错误锁定")
    private Integer pwdErrorLock;
    // 员工编号
    @ApiModelProperty("员工编号")
    private String employeeNumber;
    // 传真号
    @ApiModelProperty("传真号")
    private String fax;
    // 身份证号
    @ApiModelProperty("身份证号")
    private String idNumber;
    // 手机号
    @ApiModelProperty("手机号")
    private String mobilePhone;
    // 办公电话
    @ApiModelProperty("办公电话")
    private String officePhone;
    // 头像UUID
    @ApiModelProperty("头像UUID")
    private String photoUuid;
    // 性别
    @ApiModelProperty("性别")
    private String sex;
    // 姓名
    @NotBlank
    @ApiModelProperty("姓名")
    private String userName;
    // 姓名拼音
    @ApiModelProperty("姓名拼音")
    private String userNamePy;
    // 家电电话
    @ApiModelProperty("家电电话")
    private String homePhone;
    // 英文名
    @ApiModelProperty("英文名")
    private String englishName;
    // MAIN_EMAIL
    @ApiModelProperty("MAIN_EMAIL")
    private String mainEmail;
    // 最后一次登录时间
    @ApiModelProperty("最后一次登录时间")
    private Date lastLoginTime;
    // 最后一次账号解锁时间
    @ApiModelProperty("最后一次账号解锁时间")
    private Date lastUnLockedTime;
    @ApiModelProperty("本地化姓名")
    private String localUserName;

    // 主职
    @ApiModelProperty("主职ID")
    private String mainJobId;
    @ApiModelProperty("主职姓名")
    private String mainJobName;
    @ApiModelProperty("主职路径")
    private String mainJobNamePath;
    @ApiModelProperty("主职智能路径")
    private String mainJobSmartNamePath;
    @ApiModelProperty("主职路径")
    private String mainJobIdPath;

    // 主业务单位
    @ApiModelProperty("主业务单位ID")
    private String mainBusinessUnitId;
    @ApiModelProperty("主业务单位名称")
    private String mainBusinessUnitName;
    @ApiModelProperty("主业务单位路径")
    private String mainBusinessUnitNamePath;
    @ApiModelProperty("主业务单位路径")
    private String mainBusinessUnitIdPath;

    // 其他职位
    @ApiModelProperty("其他职位ID路径")
    private String otherJobIdPaths;
    @ApiModelProperty("其他职位ID")
    private String otherJobIds;
    @ApiModelProperty("其他职位名称")
    private String otherJobNames;
    @ApiModelProperty("其他职位名称路径")
    private String otherJobNamePaths;
    @ApiModelProperty("其他职位智能路径")
    private String otherJobSmartNamePaths;
    @ApiModelProperty("其他业务单位ID")
    private String otherBusinessUnitIds;
    @ApiModelProperty("其他业务单位名称")
    private String otherBusinessUnitNames;

    // 职位列表
    @ApiModelProperty("职位列表")
    private List<OrgUserJobDto> jobList;

    @Transient
    private List<JobRankLevelVo> relationList;

    // 主部门相关
    @ApiModelProperty("主部门相关ID")
    private String mainDepartmentId;
    @ApiModelProperty("主部门相关id路径")
    private String mainDepartmentIdPath;
    @ApiModelProperty("主部门相关名称")
    private String mainDepartmentName;
    @ApiModelProperty("主部门相关名称路径")
    private String mainDepartmentNamePath;

    // 直接领导 //直属上级领导
    @ApiModelProperty("// 直接领导 //直属上级领导")
    private String directLeaderIdPaths;
    @ApiModelProperty("// 直接领导 //直属上级领导ID")
    private String directLeaderIds;
    @ApiModelProperty("// 直接领导 //直属上级领导 姓名")
    private String directLeaderNames;
    @ApiModelProperty("// 直接领导 //直属上级领导 路径")
    private String directLeaderNamePaths;
    @ApiModelProperty("// 直接领导 //直属上级领导 智能路径")
    private String directLeaderSmartNamePaths;

    // 角色信息
    @ApiModelProperty("角色信息")
    private String roleUuids;
    @ApiModelProperty("// 职务名称")
    private String dutyId; // 职务名称
    @ApiModelProperty("// 职务ID")
    private String dutyName; // 职务ID

    public Date getLastUnLockedTime() {
        return lastUnLockedTime;
    }

    public void setLastUnLockedTime(Date lastUnLockedTime) {
        this.lastUnLockedTime = lastUnLockedTime;
    }

    public Integer getPwdErrorLock() {
        return pwdErrorLock;
    }

    public void setPwdErrorLock(Integer pwdErrorLock) {
        this.pwdErrorLock = pwdErrorLock;
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

    public String getLoginNameZh() {
        return loginNameZh;
    }

    public void setLoginNameZh(String loginNameZh) {
        this.loginNameZh = loginNameZh;
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
     * @return the systemUnitId
     */
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * @param systemUnitId 要设置的systemUnitId
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
     * @return the employeeNumber
     */
    public String getEmployeeNumber() {
        return employeeNumber;
    }

    /**
     * @param employeeNumber 要设置的employeeNumber
     */
    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
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
     * @return the idNumber
     */
    public String getIdNumber() {
        return idNumber;
    }

    /**
     * @param idNumber 要设置的idNumber
     */
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
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
     * @return the sex
     */
    public String getSex() {
        return sex;
    }

    /**
     * @param sex 要设置的sex
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * @return the homePhone
     */
    public String getHomePhone() {
        return homePhone;
    }

    /**
     * @param homePhone 要设置的homePhone
     */
    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    /**
     * @return the englishName
     */
    public String getEnglishName() {
        return englishName;
    }

    /**
     * @param englishName 要设置的englishName
     */
    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    /**
     * @return the mainEmail
     */
    public String getMainEmail() {
        return mainEmail;
    }

    /**
     * @param mainEmail 要设置的mainEmail
     */
    public void setMainEmail(String mainEmail) {
        this.mainEmail = mainEmail;
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
     * @return the mainJobUuid
     */
    // public String getMainJobUuid() {
    // return mainJobUuid;
    // }
    //
    // /**
    // * @param mainJobUuid 要设置的mainJobUuid
    // */
    // public void setMainJobUuid(String mainJobUuid) {
    // this.mainJobUuid = mainJobUuid;
    // }

    /**
     * @return the mainJobName
     */
    public String getMainJobName() {
        return mainJobName;
    }

    /**
     * @param mainJobName 要设置的mainJobName
     */
    public void setMainJobName(String mainJobName) {
        this.mainJobName = mainJobName;
    }

    /**
     * @return the mainJobIdPath
     */
    public String getMainJobIdPath() {
        return mainJobIdPath;
    }

    /**
     * @param mainJobIdPath 要设置的mainJobIdPath
     */
    public void setMainJobIdPath(String mainJobIdPath) {
        this.mainJobIdPath = mainJobIdPath;
    }

    /**
     * @return the otherJobIds
     */
    public String getOtherJobIdPaths() {
        return otherJobIdPaths;
    }

    /**
     * @param otherJobIdPaths 要设置的otherJobIds
     */
    public void setOtherJobIdPaths(String otherJobIdPaths) {
        this.otherJobIdPaths = otherJobIdPaths;
    }

    /**
     * @return the otherJobNames
     */
    public String getOtherJobNames() {
        return otherJobNames;
    }

    /**
     * @param otherJobNames 要设置的otherJobNames
     */
    public void setOtherJobNames(String otherJobNames) {
        this.otherJobNames = otherJobNames;
    }

    public String getMainJobSmartNamePath() {
        return mainJobSmartNamePath;
    }

    public void setMainJobSmartNamePath(String mainJobSmartNamePath) {
        this.mainJobSmartNamePath = mainJobSmartNamePath;
    }

    public String getOtherJobSmartNamePaths() {
        return otherJobSmartNamePaths;
    }

    public void setOtherJobSmartNamePaths(String otherJobSmartNamePaths) {
        this.otherJobSmartNamePaths = otherJobSmartNamePaths;
    }

    public String getDirectLeaderSmartNamePaths() {
        return directLeaderSmartNamePaths;
    }

    public void setDirectLeaderSmartNamePaths(String directLeaderSmartNamePaths) {
        this.directLeaderSmartNamePaths = directLeaderSmartNamePaths;
    }

    /**
     * @return the jobList
     */
    @Transient
    public List<OrgUserJobDto> getJobList() {
        return jobList;
    }

    /**
     * @param jobList 要设置的jobList
     */
    public void setJobList(List<OrgUserJobDto> jobList) {
        this.jobList = jobList;
    }

    /**
     * @return the directLeaderIds
     */
    public String getDirectLeaderIds() {
        return directLeaderIds;
    }

    /**
     * @param directLeaderIds 要设置的directLeaderIds
     */
    public void setDirectLeaderIds(String directLeaderIds) {
        this.directLeaderIds = directLeaderIds;
    }

    /**
     * @return the directLeaderNames
     */
    public String getDirectLeaderNames() {
        return directLeaderNames;
    }

    /**
     * @param directLeaderNames 要设置的directLeaderNames
     */
    public void setDirectLeaderNames(String directLeaderNames) {
        this.directLeaderNames = directLeaderNames;
    }

    public String getDirectLeaderIdPaths() {
        return directLeaderIdPaths;
    }

    public void setDirectLeaderIdPaths(String directLeaderIdPaths) {
        this.directLeaderIdPaths = directLeaderIdPaths;
    }

    public String getDirectLeaderNamePaths() {
        return directLeaderNamePaths;
    }

    public void setDirectLeaderNamePaths(String directLeaderNamePaths) {
        this.directLeaderNamePaths = directLeaderNamePaths;
    }

    /**
     * @return the roleUuids
     */
    public String getRoleUuids() {
        return roleUuids;
    }

    /**
     * @param roleUuids 要设置的roleUuids
     */
    public void setRoleUuids(String roleUuids) {
        this.roleUuids = roleUuids;
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
     * @param userNamePy 要设置的userNamePy
     */
    public void setUserNamePy(String userNamePy) {
        this.userNamePy = userNamePy;
    }

    /**
     * @return the mainJobId
     */
    public String getMainJobId() {
        return mainJobId;
    }

    /**
     * @param mainJobId 要设置的mainJobId
     */
    public void setMainJobId(String mainJobId) {
        this.mainJobId = mainJobId;
    }

    /**
     * @return the otherJobIds
     */
    public String getOtherJobIds() {
        return otherJobIds;
    }

    /**
     * @param otherJobIds 要设置的otherJobIds
     */
    public void setOtherJobIds(String otherJobIds) {
        this.otherJobIds = otherJobIds;
    }

    /**
     * @return the mainDepartmentId
     */
    public String getMainDepartmentId() {
        return mainDepartmentId;
    }

    /**
     * @param mainDepartmentId 要设置的mainDepartmentId
     */
    public void setMainDepartmentId(String mainDepartmentId) {
        this.mainDepartmentId = mainDepartmentId;
    }

    /**
     * @return the mainDepartmentIdPath
     */
    public String getMainDepartmentIdPath() {
        return mainDepartmentIdPath;
    }

    /**
     * @param mainDepartmentIdPath 要设置的mainDepartmentIdPath
     */
    public void setMainDepartmentIdPath(String mainDepartmentIdPath) {
        this.mainDepartmentIdPath = mainDepartmentIdPath;
    }

    /**
     * @return the mainDepartmentName
     */
    public String getMainDepartmentName() {
        return mainDepartmentName;
    }

    /**
     * @param mainDepartmentName 要设置的mainDepartmentName
     */
    public void setMainDepartmentName(String mainDepartmentName) {
        this.mainDepartmentName = mainDepartmentName;
    }

    /**
     * @return the mainDepartmentNamePath
     */
    public String getMainDepartmentNamePath() {
        return mainDepartmentNamePath;
    }

    /**
     * @param mainDepartmentNamePath 要设置的mainDepartmentNamePath
     */
    public void setMainDepartmentNamePath(String mainDepartmentNamePath) {
        this.mainDepartmentNamePath = mainDepartmentNamePath;
    }

    /**
     * @return the mainJobNamePath
     */
    public String getMainJobNamePath() {
        return mainJobNamePath;
    }

    /**
     * @param mainJobNamePath 要设置的mainJobNamePath
     */
    public void setMainJobNamePath(String mainJobNamePath) {
        this.mainJobNamePath = mainJobNamePath;
    }

    /**
     * @return the otherJobNamePaths
     */
    public String getOtherJobNamePaths() {
        return otherJobNamePaths;
    }

    /**
     * @param otherJobNamePaths 要设置的otherJobNamePaths
     */
    public void setOtherJobNamePaths(String otherJobNamePaths) {
        this.otherJobNamePaths = otherJobNamePaths;
    }

    public String getMainBusinessUnitId() {
        return mainBusinessUnitId;
    }

    public void setMainBusinessUnitId(String mainBusinessUnitId) {
        this.mainBusinessUnitId = mainBusinessUnitId;
    }

    public String getMainBusinessUnitName() {
        return mainBusinessUnitName;
    }

    public void setMainBusinessUnitName(String mainBusinessUnitName) {
        this.mainBusinessUnitName = mainBusinessUnitName;
    }

    public String getOtherBusinessUnitIds() {
        return otherBusinessUnitIds;
    }

    public void setOtherBusinessUnitIds(String otherBusinessUnitIds) {
        this.otherBusinessUnitIds = otherBusinessUnitIds;
    }

    public String getOtherBusinessUnitNames() {
        return otherBusinessUnitNames;
    }

    public void setOtherBusinessUnitNames(String otherBusinessUnitNames) {
        this.otherBusinessUnitNames = otherBusinessUnitNames;
    }

    public String getMainBusinessUnitNamePath() {
        return mainBusinessUnitNamePath;
    }

    public void setMainBusinessUnitNamePath(String mainBusinessUnitNamePath) {
        this.mainBusinessUnitNamePath = mainBusinessUnitNamePath;
    }

    public String getMainBusinessUnitIdPath() {
        return mainBusinessUnitIdPath;
    }

    public void setMainBusinessUnitIdPath(String mainBusinessUnitIdPath) {
        this.mainBusinessUnitIdPath = mainBusinessUnitIdPath;
    }

    public String getCertificateSubject() {
        return certificateSubject;
    }

    public void setCertificateSubject(String certificateSubject) {
        this.certificateSubject = certificateSubject;
    }

    public String getDutyId() {
        return dutyId;
    }

    public void setDutyId(String dutyId) {
        this.dutyId = dutyId;
    }

    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    @Transient
    public List<JobRankLevelVo> getRelationList() {
        return relationList;
    }

    public void setRelationList(List<JobRankLevelVo> relationList) {
        this.relationList = relationList;
    }

    public String getLocalUserName() {
        return localUserName;
    }

    public void setLocalUserName(String localUserName) {
        this.localUserName = localUserName;
    }
}
