package com.wellsoft.pt.org.bean;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.pt.org.entity.Job;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Role;

import java.util.HashSet;
import java.util.Set;

/**
 * Description: 岗位VO类
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
public class JobBean extends Job {

    // 所属部门id
    private String departmentId;

    // 上级领导ID
    private String leaderIds;

    // 上级领导
    private String leaderNames;

    private String functionNames;

    private String functionUuids;

    //修改者： yuyq @2014-12-11 新加职级和职位序列字段
    //职务职级
    private String dutyLevel;
    //职位职位序列
    private String seriesName;

    @UnCloneable
    private Set<Role> roles = new HashSet<Role>(0);
    @UnCloneable
    private Set<Privilege> privileges = new HashSet<Privilege>();
    //职务uuid
    private String dutyUuid;

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }

    public String getDutyLevel() {
        return dutyLevel;
    }

    public void setDutyLevel(String dutyLevel) {
        this.dutyLevel = dutyLevel;
    }

    public String getSeriesName() {
        return seriesName;
    }

    public void setSeriesName(String seriesName) {
        this.seriesName = seriesName;
    }

    public String getFunctionNames() {
        return functionNames;
    }

    public void setFunctionNames(String functionNames) {
        this.functionNames = functionNames;
    }

    public String getFunctionUuids() {
        return functionUuids;
    }

    public void setFunctionUuids(String functionUuids) {
        this.functionUuids = functionUuids;
    }

    public String getLeaderNames() {
        return leaderNames;
    }

    public void setLeaderNames(String leaderNames) {
        this.leaderNames = leaderNames;
    }

    public String getLeaderIds() {
        return leaderIds;
    }

    public void setLeaderIds(String leaderIds) {
        this.leaderIds = leaderIds;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getDutyUuid() {
        return dutyUuid;
    }

    public void setDutyUuid(String dutyUuid) {
        this.dutyUuid = dutyUuid;
    }

}
