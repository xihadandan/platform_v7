package com.wellsoft.pt.org.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.IdCardNumber;
import com.wellsoft.context.validator.MaxLength;
import com.wellsoft.context.validator.MobilePhone;
import com.wellsoft.context.validator.Telephone;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.OrderBy;
import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: 用户基本信息，做认证时必须用到
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-18.1	zhulh		2013-2-18		增加组织单元关联
 * </pre>
 * @date 2013-2-18
 */
//@Entity
//@Table(name = "org_user")
//@DynamicUpdate
//@DynamicInsert
//@Deprecated
@Deprecated
public class User extends IdEntity {
    public static final String ID = "id";
    public static final String USER_NAME = "userName";
    public static final String MOBILE_PHONE = "mobilePhone";
    private static final long serialVersionUID = -7334668305982178721L;
    // 用来程序内部标示群组，所有的群组id 会以u开头+10位的增长数字
    // 用户ID
    private String id;
    // 用户编号
    @NotBlank
    private String code;
    // 登录名
    @NotBlank
    private String loginName;
    // 登录名哈希算法，支持明文(NONE)、MD5(随机盐loginName)，默认MD5
    private String loginNameHashAlgorithm;
    // 登录名哈希算法对就的值
    private String loginNameHashValue;
    // 用户名
    @NotBlank
    private String userName;
    // 密码哈希算法，默认MD5(随机盐loginName)
    private String passwordHashAlgorithm;
    // 密码
    @NotBlank
    private String password;
    // 性别
    private String sex;
    // 账号未过期
    private Boolean accountNonExpired;
    // 账号未锁定
    private Boolean accountNonLocked;
    // 密码凭证未过期
    private Boolean credentialsNonExpired;
    // 是否激活
    private Boolean enabled;
    // 最近登录时间
    private Date lastLoginTime;
    // 员工编号
    private String employeeNumber;
    // 部门名称
    @NotBlank
    private String departmentName;
    // 岗位名称
    private String jobName;
    // 上级领导
    private String leaderNames;
    // 手机
    @MobilePhone
    private String mobilePhone;
    // 办公电话
    @Telephone
    private String officePhone;
    // 传真
    private String fax;
    // 照片uuid
    private String photoUuid;
    // 小照片uuid
    private String smallPhotoUuid;
    // 身份证
    @IdCardNumber
    private String idNumber;
    // 备注
    @MaxLength(max = Integer.MAX_VALUE)
    private String remark;
    // 是否为管理员
    private Boolean issys;
    // 我的去向
    private String trace;
    // 设置去向时间
    private Date traceDate;
    // B岗人员
    private String deputyNames;
    // 所属群组
    private String groupNames;
    // 角色名称
    private String roleNames;
    // 租户
    private String tenantId;
    // 是否允许登录后台
    private Boolean isAllowedBack;
    // 家庭电话
    @Telephone
    private String homePhone;
    // 人事范围
    private String personnelArea;
    // 主职位名称
    @NotBlank
    private String majorJobName;
    // 其他职位名称
    private String otherJobNames;
    // 公司主体
    private String principalCompany;

    // 系统外ID
    private String externalId;

    private String boUser;

    private String boPwd;

    // 英文名
    private String englishName;

    // 手机-其他
    @MobilePhone
    private String otherMobilePhone;

    // 邮件-主
    @Email
    private String mainEmail;

    // 邮件-其他
    @Email
    private String otherEmail;

    /**
     * 不允许登陆此系统
     */
    private Boolean notAllowedTenantId;

    /**
     * 只能以证书登录
     */
    private Boolean onlyLogonWidthCertificate;

    // 用户的详细信息
    // @UnCloneable
    // private UserDetail userDetail;
    //

    // 用户拥有的角色
    // @UnCloneable
    // private Set<Role> roles = new HashSet<Role>(0);
    // 所有部门
    @UnCloneable
    private Set<DepartmentUserJob> departmentUsers = new HashSet<DepartmentUserJob>(0);
    // 我的领导
    @UnCloneable
    private Set<UserLeader> leaders = new HashSet<UserLeader>(0);
    // 职务代理人
    @UnCloneable
    private Set<UserDeputy> minorJobs = new HashSet<UserDeputy>(0);
    // 所有部门
    @UnCloneable
    private Set<UserJob> userJobs = new HashSet<UserJob>(0);

    // // 用户关联的群组成员
    // @UnCloneable
    // private Set<Group> groups = new HashSet<Group>(0);

    public Boolean getNotAllowedTenantId() {
        return notAllowedTenantId;
    }

    public void setNotAllowedTenantId(Boolean notAllowedTenantId) {
        this.notAllowedTenantId = notAllowedTenantId;
    }

    public Boolean getOnlyLogonWidthCertificate() {
        return onlyLogonWidthCertificate;
    }

    // 用户拥有的权限
    // @UnCloneable
    // private Set<Privilege> privileges = new HashSet<Privilege>();

    public void setOnlyLogonWidthCertificate(Boolean onlyLogonWidthCertificate) {
        this.onlyLogonWidthCertificate = onlyLogonWidthCertificate;
    }

    // 用户的属性列表
    // @UnCloneable
    // private Set<UserProperty> userProperties = new HashSet<UserProperty>(0);

    // @UnCloneable
    // private Set<UserLoginLog> loginLogs = new HashSet<UserLoginLog>(0);

    @Column(nullable = false, unique = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    @Column(nullable = false, unique = true)
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * @return the loginNameHashAlgorithm
     */
    public String getLoginNameHashAlgorithm() {
        return loginNameHashAlgorithm;
    }

    /**
     * @param loginNameHashAlgorithm 要设置的loginNameHashAlgorithm
     */
    public void setLoginNameHashAlgorithm(String loginNameHashAlgorithm) {
        this.loginNameHashAlgorithm = loginNameHashAlgorithm;
    }

    /**
     * @return the loginNameHashValue
     */
    public String getLoginNameHashValue() {
        return loginNameHashValue;
    }

    /**
     * @param loginNameHashValue 要设置的loginNameHashValue
     */
    public void setLoginNameHashValue(String loginNameHashValue) {
        this.loginNameHashValue = loginNameHashValue;
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
     * @return the passwordHashAlgorithm
     */
    public String getPasswordHashAlgorithm() {
        return passwordHashAlgorithm;
    }

    /**
     * @param passwordHashAlgorithm 要设置的passwordHashAlgorithm
     */
    public void setPasswordHashAlgorithm(String passwordHashAlgorithm) {
        this.passwordHashAlgorithm = passwordHashAlgorithm;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
     * @return the accountNonExpired
     */
    public Boolean getAccountNonExpired() {
        return accountNonExpired;
    }

    /**
     * @param accountNonExpired 要设置的accountNonExpired
     */
    public void setAccountNonExpired(Boolean accountNonExpired) {
        this.accountNonExpired = accountNonExpired;
    }

    /**
     * @return the accountNonLocked
     */
    public Boolean getAccountNonLocked() {
        return accountNonLocked;
    }

    /**
     * @param accountNonLocked 要设置的accountNonLocked
     */
    public void setAccountNonLocked(Boolean accountNonLocked) {
        this.accountNonLocked = accountNonLocked;
    }

    /**
     * @return the credentialsNonExpired
     */
    public Boolean getCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    /**
     * @param credentialsNonExpired 要设置的credentialsNonExpired
     */
    public void setCredentialsNonExpired(Boolean credentialsNonExpired) {
        this.credentialsNonExpired = credentialsNonExpired;
    }

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
     * @return the jobName
     */
    public String getJobName() {
        return jobName;
    }

    /**
     * @param jobName 要设置的jobName
     */
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    /**
     * @return the leaderNames
     */
    public String getLeaderNames() {
        return leaderNames;
    }

    /**
     * @param leaderNames 要设置的leaderNames
     */
    public void setLeaderNames(String leaderNames) {
        this.leaderNames = leaderNames;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the issys
     */
    public Boolean getIssys() {
        return issys;
    }

    public void setIssys(Boolean issys) {
        this.issys = issys;
    }

    /*
     * 基本用户信息和用户详细信息一对一关系,使用主键一对一关联
     *
     * @return
     */
    // @OneToOne(fetch = FetchType.LAZY, mappedBy = "user")
    // @Cascade({ CascadeType.ALL })
    // // userDetail类中的user属性,表示双向外键.
    // public UserDetail getUserDetail() {
    // return userDetail;
    // }
    //
    // public void setUserDetail(UserDetail userDetail) {
    // this.userDetail = userDetail;
    // }

    // /**
    // * 用户和用户属性的一对多关系
    // *
    // * @return
    // */
    // @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    // @Cascade({ CascadeType.ALL })
    // public Set<UserProperty> getUserProperties() {
    // return userProperties;
    // }
    //
    // public void setUserProperties(Set<UserProperty> userProperties) {
    // this.userProperties = userProperties;
    // }

    // 多对多定义
    // 用户是主控制方，所以表放在org中

    public String getTrace() {
        return trace;
    }

    public void setTrace(String trace) {
        this.trace = trace;
    }

    public Date getTraceDate() {
        return traceDate;
    }

    public void setTraceDate(Date traceDate) {
        this.traceDate = traceDate;
    }

    // /**
    // * @return the roles
    // */
    // @ManyToMany(fetch = FetchType.LAZY)
    // @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE })
    // @JoinTable(name = "ORG_USER_ROLE", joinColumns = { @JoinColumn(name =
    // "user_uuid") }, inverseJoinColumns = { @JoinColumn(name = "role_uuid") })
    // @LazyCollection(LazyCollectionOption.TRUE)
    // @Fetch(FetchMode.SUBSELECT)
    // public Set<Role> getRoles() {
    // return roles;
    // }
    //
    // /**
    // * @param roles
    // * 要设置的roles
    // */
    // public void setRoles(Set<Role> roles) {
    // this.roles = roles;
    // }

    // @ManyToMany(fetch = FetchType.LAZY, mappedBy = "users")
    // @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    // @LazyCollection(LazyCollectionOption.TRUE)
    // @Fetch(FetchMode.SUBSELECT)
    // public Set<Group> getGroups() {
    // return groups;
    // }
    //
    // public void setGroups(Set<Group> groups) {
    // this.groups = groups;
    // }

    /**
     * @return the departmentUsers
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("isMajor desc")
    public Set<DepartmentUserJob> getDepartmentUsers() {
        return departmentUsers;
    }

    /**
     * @param departmentUsers 要设置的departmentUsers
     */
    public void setDepartmentUsers(Set<DepartmentUserJob> departmentUsers) {
        this.departmentUsers = departmentUsers;
    }

    /**
     * @return the userLeaders
     */
    /**
     * @return the departmentUsers
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<UserLeader> getLeaders() {
        return leaders;
    }

    /**
     * @param userLeaders 要设置的userLeaders
     */
    public void setLeaders(Set<UserLeader> userLeaders) {
        this.leaders = userLeaders;
    }

    /**
     * @return the userMinorJobs
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<UserDeputy> getMinorJobs() {
        return minorJobs;
    }

    /**
     * @param userMinorJobs 要设置的userMinorJobs
     */
    public void setMinorJobs(Set<UserDeputy> minorJobs) {
        this.minorJobs = minorJobs;
    }

    public Boolean getIsAllowedBack() {
        return isAllowedBack;
    }

    public void setIsAllowedBack(Boolean isAllowedBack) {
        this.isAllowedBack = isAllowedBack;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getDeputyNames() {
        return deputyNames;
    }

    public void setDeputyNames(String deputyNames) {
        this.deputyNames = deputyNames;
    }

    public String getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(String groupNames) {
        this.groupNames = groupNames;
    }

    public String getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(String roleNames) {
        this.roleNames = roleNames;
    }

    // @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    // @Cascade({ CascadeType.ALL })
    // public Set<UserLoginLog> getLoginLogs() {
    // return loginLogs;
    // }
    //
    // public void setLoginLogs(Set<UserLoginLog> loginLogs) {
    // this.loginLogs = loginLogs;
    // }

    /**
     * 用户拥有的角色名称字符串, 多个角色名称用','分隔.
     */
    // 非持久化属性.
    // @Transient
    // public String getRoleNames() {
    // return ConvertUtils.convertElementPropertyToString(roles, "name", ", ");
    // }

    /**
     * 用户拥有的角色id字符串, 多个角色id用','分隔.
     */
    // 非持久化属性.
    // @Transient
    // @SuppressWarnings("unchecked")
    // public List<String> getRoleIds() {
    // return ConvertUtils.convertElementPropertyToList(roles, IdEntity.UUID);
    // }
    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getPersonnelArea() {
        return personnelArea;
    }

    public void setPersonnelArea(String personnelArea) {
        this.personnelArea = personnelArea;
    }

    public String getMajorJobName() {
        return majorJobName;
    }

    public void setMajorJobName(String majorJobName) {
        this.majorJobName = majorJobName;
    }

    public String getOtherJobNames() {
        return otherJobNames;
    }

    public void setOtherJobNames(String otherJobNames) {
        this.otherJobNames = otherJobNames;
    }

    // /**
    // * @return the privileges
    // */
    // @ManyToMany(fetch = FetchType.LAZY)
    // @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    // @JoinTable(name = "ORG_USER_PRIVILEGE", joinColumns = { @JoinColumn(name
    // = "user_uuid") }, inverseJoinColumns = { @JoinColumn(name =
    // "privilege_uuid") })
    // @LazyCollection(LazyCollectionOption.TRUE)
    // @Fetch(FetchMode.SUBSELECT)
    // public Set<Privilege> getPrivileges() {
    // return privileges;
    // }
    //
    // /**
    // * @param privileges 要设置的privileges
    // */
    // public void setPrivileges(Set<Privilege> privileges) {
    // this.privileges = privileges;
    // }

    /**
     * @return the userJobs
     */
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL, CascadeType.REFRESH})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<UserJob> getUserJobs() {
        return userJobs;
    }

    /**
     * @param userJobs 要设置的userJobs
     */
    public void setUserJobs(Set<UserJob> userJobs) {
        this.userJobs = userJobs;
    }

    public String getPrincipalCompany() {
        return principalCompany;
    }

    public void setPrincipalCompany(String principalCompany) {
        this.principalCompany = principalCompany;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    @Transient
    public String getBoUser() {
        return boUser;
    }

    /**
     * @param boUser 要设置的 boUser
     */

    public void setBoUser(String boUser) {
        this.boUser = boUser;
    }

    @Transient
    public String getBoPwd() {
        return boPwd;
    }

    /**
     * @param boPwd 要设置的 boPwd
     */

    public void setBoPwd(String boPwd) {
        this.boPwd = boPwd;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getOtherMobilePhone() {
        return otherMobilePhone;
    }

    public void setOtherMobilePhone(String otherMobilePhone) {
        this.otherMobilePhone = otherMobilePhone;
    }

    public String getMainEmail() {
        return mainEmail;
    }

    public void setMainEmail(String mainEmail) {
        this.mainEmail = mainEmail;
    }

    public String getOtherEmail() {
        return otherEmail;
    }

    public void setOtherEmail(String otherEmail) {
        this.otherEmail = otherEmail;
    }

    /**
     * @return the smallPhotoUuid
     */
    public String getSmallPhotoUuid() {
        return smallPhotoUuid;
    }

    /**
     * @param smallPhotoUuid the smallPhotoUuid to set
     */
    public void setSmallPhotoUuid(String smallPhotoUuid) {
        this.smallPhotoUuid = smallPhotoUuid;
    }

}
