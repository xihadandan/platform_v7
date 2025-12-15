package com.wellsoft.pt.security.audit.entity;

// Generated 2011-4-26 9:37:15 by Hibernate Tools 3.4.0.CR1

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "AUDIT_PRIVILEGE")
@DynamicUpdate
@DynamicInsert
public class Privilege extends TenantEntity {
    public static final String ID = "id";
    /**
     * SpringSecurity默认role开头.
     */
    public static final String AUTHORITY_PREFIX = "ROLE_";
    private static final long serialVersionUID = -2677679365126096600L;
    @ApiModelProperty("权限名称")
    @NotBlank
    private String name;
    @ApiModelProperty("编号")
    @NotBlank
    private String code;
    @ApiModelProperty("是否启用")
    private Boolean enabled;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("权限分类uuid")
    private String categoryUuid;
    @ApiModelProperty("权限分类name")
    private String categoryName;
    @ApiModelProperty("appId")
    private String appId;
    @ApiModelProperty(value = "是否系统默认生成", allowableValues = "0,1")
    private Integer systemDef = 0;//系统默认生成（0否 1是）默认0

    private String tenant;
    private String system;

    @ApiModelProperty("权限所属的角色")
    @UnCloneable
    private Set<Role> roles = new HashSet<Role>();

    @ApiModelProperty("权限拥有的资源")
    @UnCloneable
    private Set<Resource> resources = new HashSet<Resource>();

    // 权限所属的部门
    // @UnCloneable
    // private Set<Department> departments = new HashSet<Department>();

    // 权限所属的群组
    // @UnCloneable
    // private Set<Group> groups = new HashSet<Group>();

    // 权限所属的用户
    // @UnCloneable
    // private Set<User> users = new HashSet<User>();

    // 权限所属的岗位
    // @UnCloneable
    // private Set<Job> jobs = new HashSet<Job>();

    /**
     *
     */
    public Privilege() {
        super();
    }

    /**
     * @param uuid
     */
    public Privilege(String uuid) {
        super(uuid);
    }

    @Column(nullable = false, unique = true)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled 要设置的enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
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
     * @return the roles
     */
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "privileges")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<Role> getRoles() {
        return roles;
    }

    /**
     * @param roles 要设置的roles
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    /**
     * PERMISSION为主控方
     *
     * @return the resources
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "AUDIT_PRIVILEGE_RESOURCE", joinColumns = {@JoinColumn(name = "PRIVILEGE_UUID")}, inverseJoinColumns = {@JoinColumn(name = "RESOURCE_UUID")})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<Resource> getResources() {
        return resources;
    }

    /**
     * @param resources 要设置的resources
     */
    public void setResources(Set<Resource> resources) {
        this.resources = resources;
    }

    //
    // @Transient
    // public String getResourceNames() {
    // return ConvertUtils.convertElementPropertyToString(resourceList, "name",
    // ", ");
    // }
    //
    // @Transient
    // @SuppressWarnings("unchecked")
    // public List<String> getResourceIds() {
    // return ConvertUtils.convertElementPropertyToList(resourceList,
    // IdEntity.UUID);
    // }

    // @Transient
    // public String getPrefixedName() {
    // return AUTHORITY_PREFIX + name;
    // }

    // /**
    // * @return the departments
    // */
    // @ManyToMany(fetch = FetchType.LAZY, mappedBy = "privileges")
    // @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
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
    //
    // /**
    // * @return the groups
    // */
    // @ManyToMany(fetch = FetchType.LAZY, mappedBy = "privileges")
    // @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
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
    //
    // /**
    // * @return the users
    // */
    // @ManyToMany(fetch = FetchType.LAZY, mappedBy = "privileges")
    // @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
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
    //
    // /**
    // * @return the users
    // */
    // @ManyToMany(fetch = FetchType.LAZY, mappedBy = "privileges")
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

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }
}
