package com.wellsoft.pt.security.audit.entity;

// Generated 2011-4-26 9:37:15 by Hibernate Tools 3.4.0.CR1

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * 角色
 */
@Entity
@Table(name = "AUDIT_ROLE")
@DynamicUpdate
@DynamicInsert
public class Role extends TenantEntity {

    public static final String ID = "id";
    private static final long serialVersionUID = 8637760599941236452L;
    @ApiModelProperty("角色名称")
    @NotBlank
    private String name;
    @ApiModelProperty("ID")
    @NotBlank
    private String id;
    @ApiModelProperty("编号")
    @NotBlank
    private String code;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("是否系统内置角色")
    private Boolean issys;

    @ApiModelProperty("来源uuid 表示该角色被推式来源uuid")
    private String sourceUuid;
    @ApiModelProperty("分类uuid")
    private String categoryUuid;
    @ApiModelProperty("分类name")
    private String categoryName;
    @ApiModelProperty("归属的系统/模块/应用ID")
    private String appId;//归属的系统/模块/应用ID
    @ApiModelProperty("系统默认生成（0否 1是）默认0")
    private Integer systemDef = 0;//系统默认生成（0否 1是）默认0

    private String tenant;
    private String system;

    // 角色所属的群组
    // @UnCloneable
    // private Set<Group> groups = new HashSet<Group>();
    // 角色所属的用户
    // @UnCloneable
    // private Set<User> users = new HashSet<User>();
    // 角色所属的部门
    // @UnCloneable
    // private Set<Department> departments = new HashSet<Department>();

    @ApiModelProperty("角色嵌套")
    @UnCloneable
    private Set<NestedRole> nestedRoles = new HashSet<NestedRole>();

    @ApiModelProperty("角色拥有的权限")
    @UnCloneable
    private Set<Privilege> privileges = new HashSet<Privilege>();

    // 角色所属的岗位
    // @UnCloneable
    // private Set<Job> jobs = new HashSet<Job>();

    // 角色所属的职务
    // @UnCloneable
    // private Set<Duty> dutys = new HashSet<Duty>();

    public Role() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @param uuid
     */
    public Role(String uuid) {
        super(uuid);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(nullable = false, unique = true)
    @OrderBy
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // @Transient
    // public String getPermissionNames() {
    // return ConvertUtils.convertElementPropertyToString(permissionList,
    // "name", ", ");
    // }
    //
    // @Transient
    // @SuppressWarnings("unchecked")
    // public List<String> getPermissionIds() {
    // return ConvertUtils.convertElementPropertyToList(permissionList,
    // IdEntity.UUID);
    // }

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
     * @return the issys
     */
    public Boolean getIssys() {
        return issys;
    }

    /**
     * @param issys 要设置的issys
     */
    public void setIssys(Boolean issys) {
        this.issys = issys;
    }

    // /**
    // * @return the groups
    // */
    // @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    // @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    // @LazyCollection(LazyCollectionOption.TRUE)
    // @Fetch(FetchMode.SUBSELECT)
    // @OrderBy("code asc")
    // public Set<Group> getGroups() {
    // return groups;
    // }
    //
    // /**
    // * @param groups 要设置的groups
    // */
    // public void setGroups(Set<Group> groups) {
    // this.groups = groups;
    // }

    // /**
    // * @return the users
    // */
    // @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    // @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    // @LazyCollection(LazyCollectionOption.TRUE)
    // @Fetch(FetchMode.SUBSELECT)
    // @OrderBy("code asc")
    // public Set<User> getUsers() {
    // return users;
    // }
    //
    // /**
    // * @param users 要设置的users
    // */
    // public void setUsers(Set<User> users) {
    // this.users = users;
    // }

    // /**
    // * @return the departments
    // */
    // @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    // @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    // @LazyCollection(LazyCollectionOption.TRUE)
    // @Fetch(FetchMode.SUBSELECT)
    // @OrderBy("code asc")
    // public Set<Department> getDepartments() {
    // return departments;
    // }
    //
    // /**
    // * @param departments 要设置的departments
    // */
    // public void setDepartments(Set<Department> departments) {
    // this.departments = departments;
    // }

    /**
     * @return the nestedRoles
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "AUDIT_ROLE_NESTED_ROLE", joinColumns = {@JoinColumn(name = "ROLE_UUID")}, inverseJoinColumns = {@JoinColumn(name = "NESTED_ROLE_UUID")})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<NestedRole> getNestedRoles() {
        return nestedRoles;
    }

    /**
     * @param nestedRoles 要设置的nestedRoles
     */
    public void setNestedRoles(Set<NestedRole> nestedRoles) {
        this.nestedRoles = nestedRoles;
    }

    /**
     * @return the privileges
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "AUDIT_ROLE_PRIVILEGE", joinColumns = {@JoinColumn(name = "ROLE_UUID")}, inverseJoinColumns = {@JoinColumn(name = "PRIVILEGE_UUID")})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    /**
     * @param privileges 要设置的privileges
     */
    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }

    // /**
    // * @return the users
    // */
    // @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    // @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    // @OrderBy("code asc")
    // public Set<Job> getJobs() {
    // return jobs;
    // }
    //
    // /**
    // * @param users 要设置的users
    // */
    // public void setJobs(Set<Job> jobs) {
    // this.jobs = jobs;
    // }

    // /**
    // * @return the users
    // */
    // @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles")
    // @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    // @OrderBy("code asc")
    // public Set<Duty> getDutys() {
    // return dutys;
    // }
    //
    // /**
    // * @param users 要设置的users
    // */
    // public void setDutys(Set<Duty> dutys) {
    // this.dutys = dutys;
    // }

    public String getSourceUuid() {
        return sourceUuid;
    }

    public void setSourceUuid(String sourceUuid) {
        this.sourceUuid = sourceUuid;
    }

    public String getCategoryUuid() {
        return categoryUuid;
    }

    public void setCategoryUuid(String categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Integer getSystemDef() {
        return systemDef;
    }

    public void setSystemDef(Integer systemDef) {
        this.systemDef = systemDef;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
}
