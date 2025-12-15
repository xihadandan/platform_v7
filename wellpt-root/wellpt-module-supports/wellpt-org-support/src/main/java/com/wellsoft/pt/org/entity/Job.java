package com.wellsoft.pt.org.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.MaxLength;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Description: 岗位信息实体类
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-11.1  zhengky	2014-8-11	  Create
 * </pre>
 * @date 2014-8-11
 */

//@Entity
//@Table(name = "org_job")
//@DynamicUpdate
//@DynamicInsert
@Deprecated
public class Job extends IdEntity {

    public static final String ID = "id";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -7153457238066027146L;
    // 岗位id
    private String id;
    // 岗位编号
    @NotBlank
    private String code;
    // 岗位名称
    @NotBlank
    private String name;

    // 所属部门名称
    private String departmentName;
    // 所属部门id
    private String departmentUuid;
    // 备注
    @MaxLength(max = Integer.MAX_VALUE)
    private String remark;
    // 职务名称
    @NotBlank
    private String dutyName;

    // 上级领导
    private String leaderNames;
    // 职能
    @MaxLength(max = Integer.MAX_VALUE)
    private String functionNames;

    private String tenantId;
    // 岗位对应的用户
    @UnCloneable
    private Set<UserJob> jobUsers = new HashSet<UserJob>(0);
    // 职能
    @UnCloneable
    private Set<JobFunction> functions = new HashSet<JobFunction>(0);
    // 系统外ID
    private String externalId;
    // 所属组织ID
    private String orgId;

    // 用户拥有的角色
    // @UnCloneable
    // private Set<Role> roles = new HashSet<Role>(0);

    // 用户拥有的权限
    // @UnCloneable
    // private Set<Privilege> privileges = new HashSet<Privilege>();
    // 岗位对应职务
    @UnCloneable
    private Duty duty;

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getLeaderNames() {
        return leaderNames;
    }

    public void setLeaderNames(String leaderNames) {
        this.leaderNames = leaderNames;
    }

    public String getFunctionNames() {
        return functionNames;
    }

    public void setFunctionNames(String functionNames) {
        this.functionNames = functionNames;
    }

    /**
     * @return the departmentUsers
     */
    @OneToMany(mappedBy = "job", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    public Set<UserJob> getJobUsers() {
        return jobUsers;
    }

    /**
     * @param departmentUsers 要设置的departmentUsers
     */
    public void setJobUsers(Set<UserJob> jobUsers) {
        this.jobUsers = jobUsers;
    }

    /**
     * @return the jobLeaders
     */
    @OneToMany(mappedBy = "job", fetch = FetchType.LAZY)
    @Cascade({CascadeType.ALL})
    public Set<JobFunction> getFunctions() {
        return functions;
    }

    /**
     * @param jobLeaders 要设置的jobLeaders
     */
    public void setFunctions(Set<JobFunction> jobfunctions) {
        this.functions = jobfunctions;
    }

    public String getDepartmentUuid() {
        return departmentUuid;
    }

    public void setDepartmentUuid(String departmentUuid) {
        this.departmentUuid = departmentUuid;
    }

    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    // /**
    // * @return the roles
    // */
    // @ManyToMany(fetch = FetchType.LAZY)
    // @Cascade({ CascadeType.SAVE_UPDATE, CascadeType.MERGE })
    // @JoinTable(name = "ORG_JOB_ROLE", joinColumns = { @JoinColumn(name =
    // "job_uuid") }, inverseJoinColumns = {
    // @JoinColumn(name = "role_uuid") })
    // // Fecth策略定义
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
    //
    // /**
    // * @return the privileges
    // */
    // @ManyToMany(fetch = FetchType.LAZY)
    // @Cascade({ CascadeType.PERSIST, CascadeType.MERGE })
    // @JoinTable(name = "ORG_JOB_PRIVILEGE", joinColumns = { @JoinColumn(name =
    // "job_uuid") }, inverseJoinColumns = {
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

    @Column(nullable = false, unique = true)
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

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

    /**
     * @return the job
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "duty_uuid")
    public Duty getDuty() {
        return duty;
    }

    /**
     * @param job 要设置的job
     */
    public void setDuty(Duty duty) {
        this.duty = duty;
    }

}
