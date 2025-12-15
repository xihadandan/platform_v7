/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.facade.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.app.entity.AppPageDefinition;
import com.wellsoft.pt.app.entity.AppPageDefinitionRefEntity;
import com.wellsoft.pt.app.service.AppPageDefinitionRefService;
import com.wellsoft.pt.app.service.AppPageDefinitionService;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.jpa.util.HqlUtils;
import com.wellsoft.pt.security.audit.bean.RoleBean;
import com.wellsoft.pt.security.audit.dto.UpdateRoleMemberDto;
import com.wellsoft.pt.security.audit.entity.NestedRole;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.audit.service.NestedRoleService;
import com.wellsoft.pt.security.audit.service.PrivilegeResourceService;
import com.wellsoft.pt.security.audit.service.PrivilegeService;
import com.wellsoft.pt.security.audit.service.RoleService;
import com.wellsoft.pt.security.bean.RoleAuthority;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

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
@Service
public class RoleFacadeServiceImpl implements RoleFacadeService {

    @Resource
    RoleService roleService;
    @Autowired
    private NestedRoleService nestedRoleService;
    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private AppPageDefinitionService appPageDefinitionService;
    @Autowired
    private PrivilegeResourceService privilegeResourceService;
    @Autowired
    private PrivilegeFacadeService privilegeFacadeService;
    @Autowired
    private AppPageDefinitionRefService appPageDefinitionRefService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.RoleFacadeService#getBean(java.lang.String)
     */
    @Override
    public RoleBean getBean(String uuid) {
        return roleService.getBean(uuid);
    }

    @Override
    public Role get(String uuid) {
        return roleService.get(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.RoleFacadeService#saveBean(com.wellsoft.pt.security.audit.bean.RoleBean)
     */
    @Override
    public void saveBean(RoleBean bean) {
        roleService.saveBean(bean);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.RoleFacadeService#getRoleOwners(java.lang.String)
     */
    @Override
    public Map<String, String> queryRoleMembers(String uuid) {
        return roleService.queryRoleMembers(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.RoleFacadeService#queryPrivilegeResultAsTree(java.lang.String)
     */
    @Override
    public TreeNode queryPrivilegeResultAsTree(String uuid) {
        return roleService.queryPrivilegeResultAsTree(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.RoleFacadeService#getRolePrivilegeTree(java.lang.String)
     */
    @Override
    public TreeNode getRolePrivilegeTree(String uuid, String appId) {
        return roleService.getRolePrivilegeTree(uuid, appId);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.RoleFacadeService#publishRoleUpdatedEvent(java.lang.String)
     */
    @Override
    public void publishRoleUpdatedEvent(String uuid) {
        roleService.publishRoleUpdatedEvent(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.RoleFacadeService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        roleService.remove(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.RoleFacadeService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<String> uuids) {
        roleService.removeAll(uuids);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.RoleFacadeService#buildRoleNestedRoleTree(com.wellsoft.context.component.tree.TreeNode, com.wellsoft.pt.security.audit.entity.Role)
     */
    @Override
    public void buildRoleNestedRoleTree(TreeNode treeNode, Role role) {
        roleService.buildRoleNestedRoleTree(treeNode, role);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.RoleFacadeService#getAll()
     */
    @Override
    public List<Role> getAll() {
        return roleService.listAll();
    }

    @Override
    public List<Role> getByUuids(List<String> uuids) {
        List<Role> roleList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(uuids)) {
            roleList = roleService.listByUuids(uuids);
        }
        return roleList;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.RoleFacadeService#getRoleTree()
     */
    @Override
    public TreeNode getRoleTree() {
        return roleService.getRoleTree();
    }


    @Override
    public List<Map<String, String>> queryRoleByCurrentUserUnitId() {
        List<Map<String, String>> result = new ArrayList<>();

        List<Role> roles = roleService.queryRoleByCurrentUserUnitId();

        Collections.sort(roles, new Comparator<Role>() {
            @Override
            public int compare(Role o1, Role o2) {
                return o1.getCode().compareTo(o2.getCode());
            }
        });

        for (Role role : roles) {
            Map<String, String> map = new HashMap<>();
            map.put("uuid", role.getUuid());
            map.put("id", role.getId());
            map.put("code", role.getCode());
            map.put("name", role.getName());
            result.add(map);
        }
        return result;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.RoleFacadeService#isExistsById(java.lang.String)
     */
    @Override
    public boolean isExistsById(String roleId) {
        return roleService.isExistsById(roleId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.RoleFacadeService#createRole(java.lang.String, java.lang.String)
     */
    @Override
    public void createRole(String roleName, String roleId) {
        roleService.createRole(roleName, roleId);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.RoleFacadeService#authorizeOrgIdsById(java.lang.String, java.util.Collection)
     */
    @Override
    public void authorizeOrgIdsById(String roleId, Collection<String> orgIds) {
        roleService.authorizeOrgIdsById(roleId, orgIds);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.RoleFacadeService#revokeOrgIdsById(java.lang.String, java.util.Collection)
     */
    @Override
    public void revokeOrgIdsById(String roleId, Collection<String> orgIds) {
        roleService.revokeOrgIdsById(roleId, orgIds);
    }

    @Override
    public Role getRoleById(String id) {
        return roleService.getRoleById(id);
    }

    @Override
    public Set<NestedRole> getNestedRolesByRoleUuid(String roleUuid) {
        return roleService.getNestedRolesByRoleUuid(roleUuid);
    }

    @Override
    public Set<GrantedAuthority> obtainGrantedAuthorities(Map<String, Set<String>> userRoles) {
        return roleService.obtainGrantedAuthorities(userRoles);
    }

    @Override
    public Role getByCode(String roleCode) {
        return roleService.getByCode(roleCode);
    }

    @Override
    public Role getByName(String roleName) {
        return roleService.getByName(roleName);
    }

    @Override
    public List<Role> queryByName(String roleName) {
        return roleService.queryByName(roleName);
    }

    @Override
    public void save(Role role) {
        roleService.save(role);
    }

//    @Override
//    @Transactional
//    public Role getDefWorkbenchRole() {
//        UserDetails user = SpringSecurityUtils.getCurrentUser();
//        if(user instanceof InternetUserDetails){
//            //互联网用户 没有默认工作台角色
//            return null;
//        }
//        String systemUnitId = SpringSecurityUtils.getCurrentUserUnitId();
//        String id = RoleConstants.getRolePageSystemDef(systemUnitId);
//        String defName = "平台_工作台(系统默认)_" + systemUnitId;
//        Role role = new Role();
//        role.setSystemDef(1);
//        role.setId(id);
//        role.setCode(id);
//        role.setSystemUnitId(systemUnitId);
//        List<Role> roles = roleService.listByEntity(role);
//        if (roles.size() == 0) {
//            role.setName(defName);
//            //保存角色
//            roleService.save(role);
//        } else {
//            role = roles.get(0);
//        }
//        Privilege privilege = new Privilege();
//        privilege.setSystemDef(1);
//        privilege.setCode(id);
//        privilege.setSystemUnitId(systemUnitId);
//        List<Privilege> privileges = privilegeService.listByEntity(privilege);
//        if (privileges.size() == 0) {
//            privilege.setName(defName);
//            //保存权限
//            privilegeService.save(privilege);
//        } else {
//            privilege = privileges.get(0);
//        }
//        if (!role.getPrivileges().contains(privilege)) {
//            Set<Privilege> privilegeSet = new HashSet<>();
//            privilegeSet.add(privilege);
//            role.setPrivileges(privilegeSet);
//            roleService.save(role);
//            roleService.flushSession();
//            this.publishRoleUpdatedEvent(role.getUuid());
//            AppCacheUtils.clear();
//        }
//        return role;
//    }

//    @Override
//    @Transactional
//    public void workbenchByAppPiuuid(String appPiUuid){
//        //生成默认工作台
//        StringBuilder pageHql = new StringBuilder("select a from AppPageDefinition a,AppProductIntegration b " +
//                "where a.appPiUuid = b.uuid and b.dataType=1 and a.appPiUuid=:appPiUuid order by a.code ");
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("appPiUuid", appPiUuid);
//        this.workbench(pageHql.toString(),param);
//    }
    //处理旧角色关联工作台
//    @Override
//    @Transactional
//    public void initOldWorkbenchRole() {
//        //生成默认工作台
//        StringBuilder pageHql = new StringBuilder("select a from AppPageDefinition a,AppProductIntegration b,AppProduct c " +
//                "where a.appPiUuid = b.uuid and b.appProductUuid = c.uuid and b.dataType=1 and c.systemUnitId=:systemUnitId order by a.code ");
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
//        this.workbench(pageHql.toString(),param);
//    }

//    private void workbench(String hql,Map<String,Object> params){
//        List<AppPageDefinition> appPageDefinitionList = appPageDefinitionService.listByHQL(hql, params);
//        Set<String> appPiUuidSet = new HashSet<>();
//        Set<AppPageDefinition> appPageDefinitionSet = new HashSet<>();
//        ArrayListMultimap<String, AppPageDefinition> pageMultimap = ArrayListMultimap.create();
//        for (AppPageDefinition appPageDefinition : appPageDefinitionList) {
//            pageMultimap.put(appPageDefinition.getAppPiUuid(), appPageDefinition);
//            if (Boolean.TRUE.equals(appPageDefinition.getIsDefault())) {
//                appPiUuidSet.add(appPageDefinition.getAppPiUuid());
//                appPageDefinitionSet.add(appPageDefinition);
//            }
//        }
//        for (String key : appPiUuidSet) {
//            pageMultimap.removeAll(key);
//        }
//        for (String key : pageMultimap.keys()) {
//            AppPageDefinition appPageDefinition = pageMultimap.get(key).get(0);
//            appPageDefinition.setIsDefault(Boolean.TRUE);
//            appPageDefinitionService.save(appPageDefinition);
//            appPageDefinitionSet.add(appPageDefinition);
//        }
//        Role defRole = this.getDefWorkbenchRole();
//        Privilege defPrivilege = defRole.getPrivileges().iterator().next();
//        for (AppPageDefinition appPageDefinition : appPageDefinitionSet) {
//            String resourceUuid = org.apache.commons.lang3.StringUtils.join(AppConstants.PAGE_PREFIX, appPageDefinition.getAppPiUuid(), "_", appPageDefinition.getUuid());
//            PrivilegeResource privilegeResource = new PrivilegeResource();
//            privilegeResource.setPrivilegeUuid(defPrivilege.getUuid());
//            privilegeResource.setResourceUuid(resourceUuid);
//            privilegeResource.setType(IexportType.AppProductIntegration);
//            List<PrivilegeResource> resourceList = privilegeResourceService.findByExample(privilegeResource);
//            if (resourceList.size() == 0) {
//                privilegeResourceService.save(privilegeResource);
//            }
//        }
//        privilegeService.flushSession();
//
//        List<PrivilegeResource> privilegeResourceList = getPrivilegeResources(appPageDefinitionList);
//        //key:resourceUuid+systemUnitId  val:roleId
//        Multimap<String, String> multimap = HashMultimap.create();
//        for (PrivilegeResource privilegeResource : privilegeResourceList) {
//            Privilege privilege = privilegeService.get(privilegeResource.getPrivilegeUuid());
//            for (Role role : privilege.getRoles()) {
//                if (role.getSystemDef() == 0) {
//                    String key = privilegeResource.getResourceUuid() + "_" + role.getSystemUnitId();
//                    this.addEleIds(key, role.getUuid(), multimap);
//                }
//            }
//        }
//        //保存新工作台角色资源
//        for (String key : multimap.keySet()) {
//            String[] strs = key.split("_");
//            boolean isRef = strs[0].equals(AppConstants.PAGEREF_PREFIX) ? true : false;
//            String appPiUuid = strs[1];
//            String uuid = strs[2];
//            String systemUnitId = strs[3];
//            Set<String> set = new HashSet<>(multimap.get(key));
//            privilegeFacadeService.initSaveAppPageRoleIds(systemUnitId, isRef, appPiUuid, uuid, set);
//        }
//    }

    private List<PrivilegeResource> getPrivilegeResources(List<AppPageDefinition> appPageDefinitionList) {
        //查询工作台resourceUuid
        Set<Serializable> resourceUuidSet = new HashSet<>();
        for (AppPageDefinition appPageDefinition : appPageDefinitionList) {
            String resourceUuid = StringUtils.join(AppConstants.PAGE_PREFIX, appPageDefinition.getAppPiUuid(), "_", appPageDefinition.getUuid());
            resourceUuidSet.add(resourceUuid);
        }

        //查询引用工作台resourceUuid
        StringBuilder pageRefHql = new StringBuilder("select a from AppPageDefinitionRefEntity a,AppProductIntegration b where a.appPiUuid = b.uuid and b.dataType=1 and a.systemUnitId=:systemUnitId ");
        HashMap<String, Object> param = new HashMap<>();
        param.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
        List<AppPageDefinitionRefEntity> appPageDefinitionRefEntitieList = appPageDefinitionRefService.listByHQL(pageRefHql.toString(), param);
        for (AppPageDefinitionRefEntity appPageDefinitionRefEntity : appPageDefinitionRefEntitieList) {
            String resourceUuid = StringUtils.join(AppConstants.PAGEREF_PREFIX, appPageDefinitionRefEntity.getAppPiUuid(), "_", appPageDefinitionRefEntity.getRefUuid());
            resourceUuidSet.add(resourceUuid);
        }
        if (resourceUuidSet.size() == 0) {
            return new ArrayList<>();
        }
        //查询权限工作台资源关联
        StringBuilder hql = new StringBuilder("from PrivilegeResource where ");
        hql.append("type=:type and ");
        param.put("type", IexportType.AppProductIntegration);
        HqlUtils.appendSql("resourceUuid", param, hql, resourceUuidSet);
        List<PrivilegeResource> privilegeResourceList = privilegeResourceService.listByHQL(hql.toString(), param);
        return privilegeResourceList;
    }

    private void addEleIds(String key, String roleUUid, Multimap<String, String> multimap) {
        String[] strs = key.split("_");
        boolean isRef = strs[0].equals(AppConstants.PAGEREF_PREFIX) ? true : false;
        String appPiUuid = strs[1];
        String uuid = strs[2];
        List<OrgNode> orgNodeList = privilegeFacadeService.getEleIds(isRef, appPiUuid, uuid);
        for (OrgNode orgNode : orgNodeList) {
            if (orgNode.getId().startsWith(IdPrefix.ROLE.getValue())) {
                multimap.put(key, orgNode.getId());
            }
        }
        multimap.put(key, IdPrefix.ROLE.getValue() + roleUUid);
    }

//    @Override
//    public List<Role> getWorkbenchRole() {
//        StringBuilder hql = new StringBuilder("from Role where systemUnitId = :systemUnitId and systemDef=:systemDef and id like :id");
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("systemUnitId", SpringSecurityUtils.getCurrentUserUnitId());
//        param.put("systemDef", 1);
//        param.put("id", AppConstants.PAGE_DEF_PREFIX + "%");
//        List<Role> roles = roleService.listByHQL(hql.toString(), param);
//        return roles;
//    }

    @Override
    public List<Role> roleNoDefList(String unitId, String keyword, String sort) {
        StringBuilder hql = new StringBuilder("from Role where systemDef=0 ");
        HashMap<String, Object> param = new HashMap<>();
        if (StringUtils.isNotEmpty(unitId)) {
            param.put("systemUnitId", unitId);
            hql.append(" and systemUnitId=:systemUnitId");
        }
        if (StringUtils.isNotEmpty(keyword)) {
            param.put("name", "%" + keyword + "%");
            hql.append(" and name like :name");
        }
        if (StringUtils.isNotEmpty(sort) && sort.equals("code")) {
            hql.append(" order by code");
        }
        List<Role> roleList = roleService.listByHQL(hql.toString(), param);
        return roleList;
    }

    @Override
    public List<NestedRole> getNestedRoleByRoleUuid(String roleUuid) {
        List<NestedRole> nestedRoleList = nestedRoleService.getByRole(roleUuid);
        return nestedRoleList;
    }

    @Override
    @Transactional
    public Set<String> getRoleAndNetedRolesById(String id) {
        Role role = roleService.getRoleById(id);
        Set<String> roles = Sets.newHashSet();
        if (role == null) {
            return roles;
        }
        roles.add(id);
        obtainNestedRole(roles, role);
        return roles;
    }

    @Override
    public List<Role> getRolesByIds(List<String> ids) {
        return roleService.getRolesByIds(ids);
    }

    private void obtainNestedRole(Set<String> roles, Role role) {
        for (NestedRole nestedRole : role.getNestedRoles()) {
            Role childRole = roleService.get(nestedRole.getRoleUuid());
            roles.add(childRole.getId());
            obtainNestedRole(roles, childRole);
        }
    }


    @Override
    @Transactional(readOnly = true)
    public Map<String, Set<GrantedAuthority>> queryAllRoleNestedRoleGrantedAuthorities() {
        Map<String, Set<GrantedAuthority>> roleGrantedAuthorities = Maps.newHashMap();
        List<Role> roleList = roleService.getAll();
        Map<String, Role> roleMap = Maps.uniqueIndex(roleList, new Function<Role, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Role input) {
                return input.getUuid();
            }
        });
        for (Role role : roleList) {
            Set<GrantedAuthority> grantedAuthorities = Sets.newHashSet();
            if (role.getSystemDef() == 1) {
                grantedAuthorities.add(new SimpleGrantedAuthority(role.getId()));
            } else {
                grantedAuthorities.add(new RoleAuthority(role.getId(), role.getName(), null));
            }
            // 获取嵌套的角色
            obtainNestedRole(grantedAuthorities, role, roleMap);
            roleGrantedAuthorities.put(role.getUuid(), grantedAuthorities);
        }
        return roleGrantedAuthorities;
    }

    @Override
    public List<Role> getRolesByAppId(String appId) {
        return roleService.listRolesByAppId(appId);
    }

    @Override
    public List<Role> getByAppIdAndTenant(String appId, String tenant) {
        Role example = new Role();
        example.setAppId(appId);
        if (StringUtils.isNotBlank(tenant)) {
            example.setTenant(tenant);
        }
        return roleService.listByEntity(example);
    }

    @Override
    public List<String> updateRoleMember(List<UpdateRoleMemberDto> dto) {
        return roleService.updateRoleMember(dto);
    }

    protected void obtainNestedRole(Set<GrantedAuthority> authSet, Role role, Map<String, Role> roleMap) {
        for (NestedRole nestedRole : role.getNestedRoles()) {
            Role childRole = roleMap.get(nestedRole.getRoleUuid());
            if (childRole == null) {
                continue;
            }
            Set<String> from = Sets.newHashSet("父角色：" + role.getName());
            if (Integer.valueOf(1).equals(childRole.getSystemDef())) {
                authSet.add(new SimpleGrantedAuthority(childRole.getId()));
            } else {
                authSet.add(new RoleAuthority(childRole.getId(), childRole.getName(), from));
            }
            obtainNestedRole(authSet, childRole, roleMap);
        }
    }
}
