package com.wellsoft.pt.org.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.MaxLength;
import com.wellsoft.pt.unit.entity.CommonUnit;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.*;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author lilin
 * @ClassName: group
 * @Description: 群组
 */

//@Entity
//@Table(name = "org_group")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class Group extends IdEntity {

    private static final long serialVersionUID = 1690456990630982355L;

    // 用来程序内部标示群组，所有的群组id 会以g开头+10位的增长数字
    private String id;
    // 群组名称
    @NotBlank
    private String name;
    // 群组备注
    private String remark;
    // 群组的排序标识
    @NotBlank
    private String code;
    // 父群组
    @UnCloneable
    private Group parent;
    // 子群组
    @UnCloneable
    private List<Group> children = new ArrayList<Group>(0);
    // 群组拥有的角色
    // @UnCloneable
    // private Set<Role> roles = new HashSet<Role>(0);
    // 群组成员-用户
    @UnCloneable
    private Set<User> users = new HashSet<User>(0);
    // 群组成员-部门
    @UnCloneable
    private Set<Department> departments = new HashSet<Department>(0);
    // 部门拥有的权限
    // @UnCloneable
    // private Set<Privilege> privileges = new HashSet<Privilege>();
    // 群组成员-职位
    @UnCloneable
    private Set<Job> jobs = new HashSet<Job>(0);
    // 群组成员-职务
    @UnCloneable
    private Set<Duty> dutys = new HashSet<Duty>(0);
    // 嵌套群组
    @UnCloneable
    private Set<GroupNestedGroup> nestedGroups = new HashSet<GroupNestedGroup>(0);
    @UnCloneable
    private Set<CommonUnit> commonUnits = new HashSet<CommonUnit>(0);

    // 群组分类，文本用户手输
    private String category;
    // 群组类型：0公共群组，1个人群组
    private Integer itype;
    /**
     * begin 2014-12-23 yuyq
     */
    // 成员名称(用户、部门)
    @MaxLength(max = Integer.MAX_VALUE)
    private String memberNames;
    // 成员ID
    @MaxLength(max = Integer.MAX_VALUE)
    private String memberIds;

    // 使用范围名称(用户、部门)
    @MaxLength(max = Integer.MAX_VALUE)
    private String rangeNames;
    // 使用范围ID
    @MaxLength(max = Integer.MAX_VALUE)
    private String rangeIds;

    private String tenantId;
    // 嵌套群组
    @UnCloneable
    private Set<GroupMember> groupMembers = new HashSet<GroupMember>(0);

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getMemberNames() {
        return memberNames;
    }

    public void setMemberNames(String memberNames) {
        this.memberNames = memberNames;
    }

    public String getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(String memberIds) {
        this.memberIds = memberIds;
    }

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<GroupMember> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(Set<GroupMember> groupMembers) {
        this.groupMembers = groupMembers;
    }

    // end 2014-12-24
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the parent
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_uuid")
    public Group getParent() {
        return parent;
    }

    /**
     * @param parent 要设置的parent
     */
    public void setParent(Group parent) {
        this.parent = parent;
    }

    /**
     * @return the children
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    @Cascade({CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public List<Group> getChildren() {
        return children;
    }

    /**
     * @param children 要设置的children
     */
    public void setChildren(List<Group> children) {
        this.children = children;
    }

    // /**
    // * 群组-角色多对多关系，群组为主控方
    // *
    // * @return the roles
    // */
    // @ManyToMany(fetch = FetchType.LAZY)
    // @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE })
    // @JoinTable(name = "ORG_GROUP_ROLE", joinColumns = { @JoinColumn(name =
    // "group_uuid") }, inverseJoinColumns = { @JoinColumn(name = "role_uuid")
    // })
    // @LazyCollection(LazyCollectionOption.TRUE)
    // @Fetch(FetchMode.SUBSELECT)
    // @OrderBy(IdEntity.CREATE_TIME_ASC)
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

    /**
     * 群组成员-用户多对多关系，群组为主控方
     *
     * @return the roles
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE})
    @JoinTable(name = "ORG_GROUP_USER", joinColumns = {@JoinColumn(name = "group_uuid")}, inverseJoinColumns = {@JoinColumn(name = "user_uuid")})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    /**
     * 群组成员-部门多对多关系，群组为主控方
     *
     * @return the roles
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE})
    @JoinTable(name = "ORG_GROUP_DEPARTMENT", joinColumns = {@JoinColumn(name = "group_uuid")}, inverseJoinColumns = {@JoinColumn(name = "department_uuid")})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<Department> departments) {
        this.departments = departments;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE})
    @JoinTable(name = "ORG_GROUP_UNIT", joinColumns = {@JoinColumn(name = "group_uuid")}, inverseJoinColumns = {@JoinColumn(name = "unit_uuid")})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<CommonUnit> getCommonUnits() {
        return commonUnits;
    }

    public void setCommonUnits(Set<CommonUnit> commonUnits) {
        this.commonUnits = commonUnits;
    }

    /**
     * 群组成员-职位多对多关系，群组为主控方
     *
     * @return the roles
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE})
    @JoinTable(name = "ORG_GROUP_JOB", joinColumns = {@JoinColumn(name = "group_uuid")}, inverseJoinColumns = {@JoinColumn(name = "job_uuid")})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<Job> getJobs() {
        return jobs;
    }

    public void setJobs(Set<Job> jobs) {
        this.jobs = jobs;
    }

    /**
     * 群组成员-职务多对多关系，群组为主控方
     *
     * @return the roles
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @Cascade({CascadeType.SAVE_UPDATE, CascadeType.MERGE})
    @JoinTable(name = "ORG_GROUP_DUTY", joinColumns = {@JoinColumn(name = "group_uuid")}, inverseJoinColumns = {@JoinColumn(name = "duty_uuid")})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<Duty> getDutys() {
        return dutys;
    }

    public void setDutys(Set<Duty> dutys) {
        this.dutys = dutys;
    }

    // @Transient
    // public String getUserNames() {
    // return ConvertUtils.convertElementPropertyToString(users, "loginName",
    // ";");
    // }

    // @Transient
    // public List<String> getUserIds() {
    // return ConvertUtils.convertElementPropertyToList(users, "id");
    // }

    // @Transient
    // public List<String> getChildGroupIds() {
    // return ConvertUtils.convertElementPropertyToList(children, "id");
    // }

    // @Transient
    // public String getDepartmentNames() {
    // return ConvertUtils.convertElementPropertyToString(departments, "name",
    // ";");
    // }

    // @Transient
    // public String getAllChildIds() {
    // String groupids = ConvertUtils.convertElementPropertyToString(children,
    // "id", ";");
    // // String depids =
    // // ConvertUtils.convertElementPropertyToString(departments, "id", ";");
    // // String userids = ConvertUtils.convertElementPropertyToString(users,
    // // "id", ";");
    // String childids = "";
    // if (!groupids.equals("")) {
    // groupids = groupids + ";";
    // }
    // // if (!depids.equals("")) {
    // // depids = depids + ";";
    // // }
    // // childids = groupids + depids + userids;
    // return childids;
    // }

    // @Transient
    // public List<String> getDepartmentIds() {
    // return ConvertUtils.convertElementPropertyToList(departments, "id");
    // }
    //
    // /**
    // * @return the privileges
    // */
    // @ManyToMany(fetch = FetchType.LAZY)
    // @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    // @JoinTable(name = "ORG_GROUP_PRIVILEGE", joinColumns = { @JoinColumn(name
    // = "group_uuid") }, inverseJoinColumns = { @JoinColumn(name =
    // "privilege_uuid") })
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
     * @return the nestedGroups
     */
    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    @LazyCollection(LazyCollectionOption.TRUE)
    @Fetch(FetchMode.SUBSELECT)
    public Set<GroupNestedGroup> getNestedGroups() {
        return nestedGroups;
    }

    /**
     * @param nestedGroups 要设置的nestedGroups
     */
    public void setNestedGroups(Set<GroupNestedGroup> nestedGroups) {
        this.nestedGroups = nestedGroups;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getItype() {
        return itype;
    }

    public void setItype(Integer itype) {
        this.itype = itype;
    }

    public String getRangeNames() {
        return rangeNames;
    }

    public void setRangeNames(String rangeNames) {
        this.rangeNames = rangeNames;
    }

    public String getRangeIds() {
        return rangeIds;
    }

    public void setRangeIds(String rangeIds) {
        this.rangeIds = rangeIds;
    }

    // public String getUnitIds() {
    // Set<CommonUnit> commonUnits = this.getCommonUnits();
    // StringBuffer buf = new StringBuffer();
    // for (CommonUnit commonUnit : commonUnits) {
    // buf.append(commonUnit.getUnitId());
    // buf.append(",");
    // }
    // String result = buf.toString();
    // if (result != null && result.lastIndexOf(",") > 0) {
    // result = result.substring(0, result.lastIndexOf(","));
    // }
    // return result;
    // }

}
