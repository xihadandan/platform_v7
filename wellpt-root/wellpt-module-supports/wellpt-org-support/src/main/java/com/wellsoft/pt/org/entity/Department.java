package com.wellsoft.pt.org.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.MaxLength;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@Entity
//@Table(name = "org_department")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class Department extends IdEntity {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PATH = "path";
    private static final long serialVersionUID = 3284986610699923133L;
    // 用来程序内部标示群组，所有的群组id 会以o开头+10位的增长数字
    private String id;

    // 部门名称
    @NotBlank
    private String name;

    // 部门简称
    private String shortName;

    // 部门完整路径名称
    private String path;

    // 部门编码
    @NotBlank
    private String code;

    // 部门负责人
    @MaxLength(max = Integer.MAX_VALUE)
    private String principalLeaderNames;
    // 分管领导
    @MaxLength(max = Integer.MAX_VALUE)
    private String branchedLeaderNames;
    // 部门管理员
    @MaxLength(max = Integer.MAX_VALUE)
    private String managerNames;

    // 父部门uuid
    @UnCloneable
    private Department parent;

    private String tenantId;
    // 部门用户岗位
    @UnCloneable
    private Set<DepartmentUserJob> departmentUsers = new HashSet<DepartmentUserJob>(0);
    // 部门负责用户
    @UnCloneable
    private Set<DepartmentPrincipal> departmentPrincipals = new HashSet<DepartmentPrincipal>(0);
    // 备注
    @MaxLength(max = Integer.MAX_VALUE)
    private String remark;
    // 挂接的单位ID
    private String commonUnitId;
    // 是否允许显示
    private Boolean isVisible;
    // 自关联
    @UnCloneable
    private List<Department> children = new ArrayList<Department>(0);
    // 职能
    @UnCloneable
    private Set<DepartmentFunction> functions = new HashSet<DepartmentFunction>(0);
    // 职能
    @MaxLength(max = Integer.MAX_VALUE)
    private String functionNames;

    // 部门拥有的角色
    // @UnCloneable
    // private Set<Role> roles = new HashSet<Role>(0);

    // 部门拥有的权限
    // @UnCloneable
    // private Set<Privilege> privileges = new HashSet<Privilege>();
    // 部门级别
    private String departmentLevel;
    // 系统外ID
    private String externalId;
    // 所属组织ID
    private String orgId;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    /**
     * @return the jobLeaders
     */
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    public Set<DepartmentFunction> getFunctions() {
        return functions;
    }

    /**
     * @param jobLeaders 要设置的jobLeaders
     */
    public void setFunctions(Set<DepartmentFunction> departmentFunctions) {
        this.functions = departmentFunctions;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    public String getDepartmentLevel() {
        return departmentLevel;
    }

    public void setDepartmentLevel(String departmentLevel) {
        this.departmentLevel = departmentLevel;
    }

    public String getFunctionNames() {
        return functionNames;
    }

    public void setFunctionNames(String functionNames) {
        this.functionNames = functionNames;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName 要设置的shortName
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path 要设置的path
     */
    public void setPath(String path) {
        this.path = path;
    }

    @ManyToOne
    @JoinColumn(name = "parent_uuid")
    @LazyToOne(LazyToOneOption.PROXY)
    @Fetch(FetchMode.SELECT)
    public Department getParent() {
        return parent;
    }

    public void setParent(Department parent) {
        this.parent = parent;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the principalLeaderNames
     */
    public String getPrincipalLeaderNames() {
        return principalLeaderNames;
    }

    /**
     * @param principalLeaderNames 要设置的principalLeaderNames
     */
    public void setPrincipalLeaderNames(String principalLeaderNames) {
        this.principalLeaderNames = principalLeaderNames;
    }

    /**
     * @return the branchedLeaderNames
     */
    public String getBranchedLeaderNames() {
        return branchedLeaderNames;
    }

    /**
     * @param branchedLeaderNames 要设置的branchedLeaderNames
     */
    public void setBranchedLeaderNames(String branchedLeaderNames) {
        this.branchedLeaderNames = branchedLeaderNames;
    }

    /**
     * @return the managerNames
     */
    public String getManagerNames() {
        return managerNames;
    }

    /**
     * @param managerNames 要设置的managerNames
     */
    public void setManagerNames(String managerNames) {
        this.managerNames = managerNames;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Column(nullable = false, unique = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the departmentUsers
     */
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL, CascadeType.REFRESH})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<DepartmentUserJob> getDepartmentUsers() {
        return departmentUsers;
    }

    /**
     * @param departmentUsers 要设置的departmentUsers
     */
    public void setDepartmentUsers(Set<DepartmentUserJob> departmentUsers) {
        this.departmentUsers = departmentUsers;
    }

    // @Transient
    // public String getUserNames() {
    // return ConvertUtils.convertElementPropertyToString(departmentUsers,
    // "loginName", ";");
    // }
    //
    // @Transient
    // public String getUserIds() {
    // return ConvertUtils.convertElementPropertyToString(departmentUsers, "id",
    // ";");
    // }

    /**
     * @return the departmentPrincipals
     */
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL, CascadeType.REFRESH})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<DepartmentPrincipal> getDepartmentPrincipals() {
        return departmentPrincipals;
    }

    /**
     * @param departmentPrincipals 要设置的departmentPrincipals
     */
    public void setDepartmentPrincipals(Set<DepartmentPrincipal> departmentPrincipals) {
        this.departmentPrincipals = departmentPrincipals;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Cascade({CascadeType.ALL, CascadeType.REFRESH})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    @OrderBy("code asc,name ")
    public List<Department> getChildren() {
        return children;
    }

    public void setChildren(List<Department> children) {
        this.children = children;
    }

    // /**
    // * @return the roles
    // */
    // @ManyToMany(fetch = FetchType.LAZY)
    // @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE })
    // @JoinTable(name = "ORG_DEPARTMENT_ROLE", joinColumns = {
    // @JoinColumn(name = "department_uuid") }, inverseJoinColumns = {
    // @JoinColumn(name = "role_uuid") })
    // @LazyCollection(LazyCollectionOption.TRUE)
    // @Fetch(FetchMode.SUBSELECT)
    // public Set<Role> getRoles() {
    // return roles;
    // }
    //
    // /**
    // * @param roles 要设置的roles
    // */
    // public void setRoles(Set<Role> roles) {
    // this.roles = roles;
    // }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public String getCommonUnitId() {
        return commonUnitId;
    }

    public void setCommonUnitId(String commonUnitId) {
        this.commonUnitId = commonUnitId;
    }

    // /**
    // * @return the privileges
    // */
    // @ManyToMany(fetch = FetchType.LAZY)
    // @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    // @JoinTable(name = "ORG_DEPARTMENT_PRIVILEGE", joinColumns = {
    // @JoinColumn(name = "department_uuid") }, inverseJoinColumns = {
    // @JoinColumn(name = "privilege_uuid") })
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
     * @return the orgId
     */
    public String getOrgId() {
        return orgId;
    }

    /**
     * @param orgId 要设置的orgId
     */
    public void setOrgId(String orgId) {
        this.orgId = orgId;
    }

}
