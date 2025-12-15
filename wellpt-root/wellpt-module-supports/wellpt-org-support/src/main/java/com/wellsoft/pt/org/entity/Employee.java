package com.wellsoft.pt.org.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

//@Entity
//@Table(name = "org_employee")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class Employee extends IdEntity {

    public static final String ID = "id";
    public static final String USER_NAME = "employeeName";
    public static final String MOBILE_PHONE = "mobilePhone";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -5810862975120656268L;
    // 用来程序内部标示群组，所有的群组id 会以u开头+10位的增长数字
    // 用户ID
    private String id;
    // 用户编号
    private String code;
    // 用户名
    private String name;
    // 登录名
    private String loginName;
    // 密码
    private String password;
    // 性别
    private String sex;
    // 是否激活
    private Boolean enabled;
    // 员工编号
    private String employeeNumber;
    // 部门名称
    private String departmentName;
    // 上级领导
    private String leaderNames;
    // 手机
    private String mobilePhone;
    // 办公电话
    private String officePhone;
    // 传真
    private String fax;
    // 照片uuid
    private String photoUuid;
    // 身份证
    private String idNumber;
    // 备注
    private String remark;
    //B岗人员
    private String deputyNames;

    //家庭电话
    private String homePhone;
    //人事范围
    private String personnelArea;
    //主职位名称
    private String majorJobName;
    //其他职位名称
    private String otherJobNames;
    //公司主体
    private String principalCompany;

    //系统外ID
    private String externalId;

    //英文名
    private String englishName;

    // 手机-其他
    private String otherMobilePhone;

    //邮件-主
    private String mainEmail;

    //邮件-其他
    private String otherEmail;

    // 所有部门
    @UnCloneable
    private Set<DepartmentEmployeeJob> departmentEmployees = new HashSet<DepartmentEmployeeJob>(0);

    // 所有职位
    @UnCloneable
    private Set<EmployeeJob> employeeJobs = new HashSet<EmployeeJob>(0);

    private String tenantId;


    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    // 用户的属性列表
    //	@UnCloneable
    //	private Set<EmployeeProperty> employeeProperties = new HashSet<EmployeeProperty>(0);

    //	@UnCloneable
    //	private Set<EmployeeLoginLog> loginLogs = new HashSet<EmployeeLoginLog>(0);

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

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
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
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the employeeNumber
     */
    @Column(nullable = false, unique = true)
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
     * @return the departmentEmployees
     */
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<DepartmentEmployeeJob> getDepartmentEmployees() {
        return departmentEmployees;
    }

    /**
     * @param departmentEmployees 要设置的departmentEmployees
     */
    public void setDepartmentEmployees(Set<DepartmentEmployeeJob> departmentEmployees) {
        this.departmentEmployees = departmentEmployees;
    }

    public String getDeputyNames() {
        return deputyNames;
    }

    public void setDeputyNames(String deputyNames) {
        this.deputyNames = deputyNames;
    }

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

    /**
     * @return the employeeJobs
     */
    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL, CascadeType.REFRESH})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<EmployeeJob> getEmployeeJobs() {
        return employeeJobs;
    }

    /**
     * @param employeeJobs 要设置的employeeJobs
     */
    public void setEmployeeJobs(Set<EmployeeJob> employeeJobs) {
        this.employeeJobs = employeeJobs;
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

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
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

}
