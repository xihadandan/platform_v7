/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.facade.service;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.service.Facade;
import com.wellsoft.pt.security.audit.bean.RoleBean;
import com.wellsoft.pt.security.audit.dto.UpdateRoleMemberDto;
import com.wellsoft.pt.security.audit.entity.NestedRole;
import com.wellsoft.pt.security.audit.entity.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月12日.1	chenqiong		2018年4月12日		Create
 * </pre>
 * @date 2018年4月12日
 */
public interface RoleFacadeService extends Facade {
    /**
     * 根据UUID获取角色
     *
     * @param uuid
     * @return
     */
    RoleBean getBean(String uuid);

    /**
     * 获取角色树
     *
     * @return
     */
    TreeNode getRoleTree();

    Role get(String uuid);

    void buildRoleNestedRoleTree(TreeNode treeNode, Role role);

    /**
     * 保存角色
     *
     * @param bean
     */
    void saveBean(RoleBean bean);

    /**
     * 获取人员/部门/群组列表
     *
     * @param uuid
     * @return
     */
    Map<String, String> queryRoleMembers(String uuid);

    /**
     * 根据UUID加载权限树, 包含角色嵌套及权限
     *
     * @param uuid
     * @return
     */
    TreeNode queryPrivilegeResultAsTree(String uuid);

    /**
     * 加载角色权限树，选择已选角色权限
     *
     * @param uuid
     * @return
     */
    TreeNode getRolePrivilegeTree(String uuid, String appId);

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
     * @return
     */
    List<Role> getAll();

    List<Role> getByUuids(List<String> uuids);

    List<Map<String, String>> queryRoleByCurrentUserUnitId();

    /**
     * 根据角色ID判断角色是否存在
     *
     * @param roleId
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

    public Role getRoleById(String id);

    Set<NestedRole> getNestedRolesByRoleUuid(String roleUuid);

    Set<GrantedAuthority> obtainGrantedAuthorities(Map<String, Set<String>> userRoles);

    Role getByCode(String roleCode);

    void save(Role role);

    public Role getByName(String roleName);

    public abstract List<Role> queryByName(String roleName);

    /**
     * 查出工作台默认角色
     * @return
     */
//	public Role getDefWorkbenchRole();

    /**
     * 查出工作台角色
     *
     * @return
     */
//	public List<Role> getWorkbenchRole();

//	public void workbenchByAppPiuuid(String appPiUuid);

//	public void initOldWorkbenchRole();
    public List<Role> roleNoDefList(String unitId, String keyword, String sort);

    public List<NestedRole> getNestedRoleByRoleUuid(String roleUuid);

    Set<String> getRoleAndNetedRolesById(String role);

    List<Role> getRolesByIds(List<String> ids);

    Map<String, Set<GrantedAuthority>> queryAllRoleNestedRoleGrantedAuthorities();

    List<Role> getRolesByAppId(String appId);

    List<Role> getByAppIdAndTenant(String system, String tenant);

    List<String> updateRoleMember(List<UpdateRoleMemberDto> dto);

}
