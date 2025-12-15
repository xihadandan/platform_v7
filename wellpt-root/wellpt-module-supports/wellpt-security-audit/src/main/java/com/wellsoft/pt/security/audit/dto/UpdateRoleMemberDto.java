package com.wellsoft.pt.security.audit.dto;

import com.google.common.collect.Sets;
import com.wellsoft.pt.security.audit.bean.RoleDto;

import java.io.Serializable;
import java.util.Set;

/**
 * Description: 增量更新角色成员
 *
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期    修改内容
 * 2024年03月12日   chenq	 Create
 * </pre>
 */
public class UpdateRoleMemberDto implements Serializable {


    private RoleDto role;

    // 嵌套角色更新
    private Set<String> nestedRoleRemoved = Sets.newHashSet();
    private Set<String> nestedRoleAdded = Sets.newHashSet();

    // 权限关联更新
    private Set<String> privilegeRemoved = Sets.newHashSet();
    private Set<String> privilegeAdded = Sets.newHashSet();

    // 角色关联用户更新
    private Set<String> userIdRemoved = Sets.newHashSet();
    private Set<String> userIdAdded = Sets.newHashSet();

    // 角色关联新版组织单元实例更新
    private Set<String> orgElementRemoved = Sets.newHashSet();
    private Set<String> orgElementAdded = Sets.newHashSet();

    // 父级角色更新
    private Set<String> parentRoleRemoved = Sets.newHashSet();
    private Set<String> parentRoleAdded = Sets.newHashSet();


    public Set<String> getNestedRoleRemoved() {
        return nestedRoleRemoved;
    }

    public void setNestedRoleRemoved(Set<String> nestedRoleRemoved) {
        this.nestedRoleRemoved = nestedRoleRemoved;
    }

    public Set<String> getPrivilegeRemoved() {
        return privilegeRemoved;
    }

    public void setPrivilegeRemoved(Set<String> privilegeRemoved) {
        this.privilegeRemoved = privilegeRemoved;
    }

    public Set<String> getNestedRoleAdded() {
        return nestedRoleAdded;
    }

    public void setNestedRoleAdded(Set<String> nestedRoleAdded) {
        this.nestedRoleAdded = nestedRoleAdded;
    }

    public Set<String> getPrivilegeAdded() {
        return privilegeAdded;
    }

    public void setPrivilegeAdded(Set<String> privilegeAdded) {
        this.privilegeAdded = privilegeAdded;
    }

    public Set<String> getUserIdRemoved() {
        return userIdRemoved;
    }

    public void setUserIdRemoved(Set<String> userIdRemoved) {
        this.userIdRemoved = userIdRemoved;
    }

    public Set<String> getUserIdAdded() {
        return userIdAdded;
    }

    public void setUserIdAdded(Set<String> userIdAdded) {
        this.userIdAdded = userIdAdded;
    }

    public Set<String> getOrgElementRemoved() {
        return orgElementRemoved;
    }

    public void setOrgElementRemoved(Set<String> orgElementRemoved) {
        this.orgElementRemoved = orgElementRemoved;
    }

    public Set<String> getOrgElementAdded() {
        return orgElementAdded;
    }

    public void setOrgElementAdded(Set<String> orgElementAdded) {
        this.orgElementAdded = orgElementAdded;
    }

    public RoleDto getRole() {
        return role;
    }

    public void setRole(RoleDto role) {
        this.role = role;
    }

    public Set<String> getParentRoleRemoved() {
        return parentRoleRemoved;
    }

    public void setParentRoleRemoved(Set<String> parentRoleRemoved) {
        this.parentRoleRemoved = parentRoleRemoved;
    }

    public Set<String> getParentRoleAdded() {
        return parentRoleAdded;
    }

    public void setParentRoleAdded(Set<String> parentRoleAdded) {
        this.parentRoleAdded = parentRoleAdded;
    }
}
