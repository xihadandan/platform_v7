package com.wellsoft.pt.security.audit.dto;

import com.google.common.collect.Sets;
import com.wellsoft.pt.security.audit.bean.PrivilegeDto;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;
import com.wellsoft.pt.security.audit.entity.Resource;

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
public class UpdatePrivilegeDto implements Serializable {

    private static final long serialVersionUID = -2704413592407861854L;

    private PrivilegeDto privilege;

    private Set<PrivilegeResource> privilegeResourceAdded = Sets.newHashSet();// 增量添加的权限其他资源

    private Set<PrivilegeResource> privilegeResourceDeleted = Sets.newHashSet();// 增量删除的权限其他资源

    private Set<Resource> resourceAdded = Sets.newHashSet(); // 增量添加的权限关联资源（URL）
    private Set<Resource> resourceDeleted = Sets.newHashSet(); // 增量删除的权限关联资源（URL）

    private Set<String> roleAdded = Sets.newHashSet(); // 增量添加的关联角色
    private Set<String> roleDeleted = Sets.newHashSet(); // 增量删除的关联角色


    public Set<PrivilegeResource> getPrivilegeResourceAdded() {
        return privilegeResourceAdded;
    }

    public void setPrivilegeResourceAdded(Set<PrivilegeResource> privilegeResourceAdded) {
        this.privilegeResourceAdded = privilegeResourceAdded;
    }


    public Set<PrivilegeResource> getPrivilegeResourceDeleted() {
        return privilegeResourceDeleted;
    }

    public void setPrivilegeResourceDeleted(Set<PrivilegeResource> privilegeResourceDeleted) {
        this.privilegeResourceDeleted = privilegeResourceDeleted;
    }

    public Set<Resource> getResourceAdded() {
        return resourceAdded;
    }

    public void setResourceAdded(Set<Resource> resourceAdded) {
        this.resourceAdded = resourceAdded;
    }

    public Set<Resource> getResourceDeleted() {
        return resourceDeleted;
    }

    public void setResourceDeleted(Set<Resource> resourceDeleted) {
        this.resourceDeleted = resourceDeleted;
    }


    public Set<String> getRoleAdded() {
        return roleAdded;
    }

    public void setRoleAdded(Set<String> roleAdded) {
        this.roleAdded = roleAdded;
    }

    public Set<String> getRoleDeleted() {
        return roleDeleted;
    }

    public void setRoleDeleted(Set<String> roleDeleted) {
        this.roleDeleted = roleDeleted;
    }


    public PrivilegeDto getPrivilege() {
        return privilege;
    }

    public void setPrivilege(PrivilegeDto privilege) {
        this.privilege = privilege;
    }
}
