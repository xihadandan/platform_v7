/*
 * @(#)2013-1-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.service;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.security.audit.bean.RoleBean;
import com.wellsoft.pt.security.audit.bean.RoleDto;
import com.wellsoft.pt.security.audit.dao.RoleDao;
import com.wellsoft.pt.security.audit.dto.UpdateRoleMemberDto;
import com.wellsoft.pt.security.audit.entity.NestedRole;
import com.wellsoft.pt.security.audit.entity.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 角色管理服务类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-17.1	zhulh		2013-1-17		Create
 * </pre>
 * @date 2013-1-17
 */
public interface RoleService extends JpaService<Role, RoleDao, String> {
    /**
     * 根据UUID获取角色
     *
     * @param roleUuid
     * @return
     */
    Role get(String uuid);

    /**
     * 根据UUID获取角色
     *
     * @param uuid
     * @return
     */
    RoleBean getBean(String uuid);

    /**
     * 保存角色
     *
     * @param bean
     */
    String saveBean(RoleBean bean);

    void save(Role role);

    /**
     * 发布角色更新事件
     */
    void publishRoleUpdatedEvent(String uuid);

    /**
     * 根据UUID删除角色
     *
     * @param uuid
     */
    void remove(String uuid);

    /**
     * 根据UUID，批量删除角色
     *
     * @param uuid
     */
    void removeAll(Collection<String> uuids);

    /**
     * 如何描述该方法
     *
     * @param queryInfo
     * @return
     */
    JqGridQueryData query(JqGridQueryInfo queryInfo);

    /**
     * 加载角色权限树，选择已选角色权限
     *
     * @param uuid
     * @param appId
     * @return
     */
    TreeNode getRolePrivilegeTree(String uuid, String appId);

    /**
     * 获取角色树
     *
     * @return
     */
    TreeNode getRoleTree();

    /**
     * 根据UUID加载权限树, 包含角色嵌套及权限
     *
     * @param uuid
     * @return
     */
    TreeNode queryPrivilegeResultAsTree(String uuid);

    void buildRoleNestedRoleTree(TreeNode treeNode, Role role);

    /**
     * 获取人员/部门/群组列表
     *
     * @param uuid
     * @return
     */
    Map<String, String> queryRoleMembers(String uuid);


    /**
     * 判断指定的用户是否有指定的角色
     *
     * @param userId
     * @param rolename
     * @return
     */
    boolean hasRole(String userId, String roleId);

    /**
     * 如何描述该方法
     *
     * @return
     */
    List<Role> getAll();

    /**
     * 根据角色ID判断角色是否存在
     *
     * @param roleId
     * @return
     */
    boolean isExistsById(String roleId);

    /**
     * 根据角色名称、ID创建角色
     *
     * @param roleName
     * @param roleId
     */
    void createRole(String roleName, String roleId);

    /**
     * 根据角色ID授权组织ID列表
     *
     * @param roleId
     * @param orgIds
     */
    void authorizeOrgIdsById(String roleId, Collection<String> orgIds);

    /**
     * 根据角色ID取消授权组织ID列表
     *
     * @param roleId
     * @param orgIds
     */
    void revokeOrgIdsById(String roleId, Collection<String> orgIds);

    /**
     * 获取一家单位的所有角色列表
     *
     * @param unitId
     */
    List<Role> queryRoleListByUnit(String unitId);

    Role getRoleById(String id);

    public List<Role> queryRoleByCurrentUserUnitId();

    Set<NestedRole> getNestedRolesByRoleUuid(String roleUuid);

    Set<GrantedAuthority> obtainGrantedAuthorities(Map<String, Set<String>> userRoles);

    Role getByCode(String roleCode);

    public abstract Role getByName(String roleName);

    public abstract List<Role> queryByName(String roleName);

    List<RoleDto> queryRoleDtosBySystemUnitId(String systemUnitId);

    List<Role> getRolesByIds(List<String> ids);

    List<RoleDto> getRolesByAppId(String appId);

    RoleDto getRoleMembers(String uuid);

    List<RoleDto> getRolesByAppIds(List<String> appId);

    List<Role> listRolesByAppId(String appId);

    List<RoleDto> getRolesByUuids(List<String> roleUuids);

    TreeNode getRolePrivilegeTree(String roleUuid);

    List<TreeNode> getRoleResourceTreeByUuid(String uuid);

    List<TreeNode> getRolePrivilegeResourceTreeByUuid(String uuid);

    List<String> updateRoleMember(List<UpdateRoleMemberDto> dto);

    boolean checkNestedRecursively(String roleUuid, String childRoleUuid);

    List<RoleDto> getRoleNestedRoles(String uuid);

    List<RoleDto> getRolesInTenantSystem(String system, String tenant);

    RoleDto getRoleMembersById(String roleId);

    List<RoleDto> getRolesByNestedRole(String uuid);
}
