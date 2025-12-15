/*
 * @(#)2013-1-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.service.CommonValidateService;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.collection.List2GroupMap;
import com.wellsoft.context.util.collection.TreeNode4AddNodeFromGroupMap;
import com.wellsoft.pt.app.service.AppProductIntegrationService;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.bean.OrgTreeNodeDto;
import com.wellsoft.pt.multi.org.bean.OrgUserVo;
import com.wellsoft.pt.multi.org.entity.*;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgApiFacade;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgTreeDialogService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.multi.org.support.dataprovider.OrgSelectProvider;
import com.wellsoft.pt.org.dto.OrgRelaRoleMembersDto;
import com.wellsoft.pt.org.facade.service.OrgFacadeService;
import com.wellsoft.pt.org.service.DutyRoleService;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.audit.bean.PrivilegeDto;
import com.wellsoft.pt.security.audit.bean.RoleBean;
import com.wellsoft.pt.security.audit.bean.RoleDto;
import com.wellsoft.pt.security.audit.dao.NestedRoleDao;
import com.wellsoft.pt.security.audit.dao.RoleDao;
import com.wellsoft.pt.security.audit.dto.AuditDataLogDto;
import com.wellsoft.pt.security.audit.dto.UpdateRoleMemberDto;
import com.wellsoft.pt.security.audit.entity.NestedRole;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.AuditDataFacadeService;
import com.wellsoft.pt.security.audit.service.NestedRoleService;
import com.wellsoft.pt.security.audit.service.PrivilegeService;
import com.wellsoft.pt.security.audit.service.RoleService;
import com.wellsoft.pt.security.audit.support.SecurityConfigType;
import com.wellsoft.pt.security.bean.RoleAuthority;
import com.wellsoft.pt.security.enums.BuildInRole;
import com.wellsoft.pt.security.util.SecurityConfigUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.user.entity.UserInfoEntity;
import com.wellsoft.pt.user.facade.service.UserInfoFacadeService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
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
@Service
public class RoleServiceImpl extends AbstractJpaServiceImpl<Role, RoleDao, String> implements RoleService {

    @Autowired
    private DataDictionaryService dataDictionaryService;

    @Autowired
    private NestedRoleService nestedRoleService;

    @Autowired
    private PrivilegeService privilegeService;
    @Autowired
    private CommonValidateService commonValidateService;
    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private MultiOrgApiFacade multiOrgApiFacade;
    @Autowired
    private OrgFacadeService orgFacadeService;
    @Autowired
    private UserInfoFacadeService userInfoFacadeService;
    @Autowired
    private DutyRoleService dutyRoleService;
    @Autowired
    private DyFormFacade dyFormFacade;
    @Autowired
    private AppProductIntegrationService appProductIntegrationService;
    @Autowired
    private MultiOrgTreeDialogService multiOrgTreeDialogService;

    @Autowired
    private NestedRoleDao nestedRoleDao;

    /**
     * 根据UUID获取角色
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.RoleService#get(java.lang.String)
     */
    @Override
    public Role get(String uuid) {
        return this.dao.getOne(uuid);
    }

    /**
     * 根据UUID获取角色
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.RoleService#getBean(java.lang.String)
     */
    @Override
    public RoleBean getBean(String uuid) {
        RoleBean bean = new RoleBean();
        Role role = this.dao.getOne(uuid);
        BeanUtils.copyProperties(role, bean);
        // 1、设置权限
        Set<Privilege> privileges = role.getPrivileges();
        Set<Privilege> privilegeBeans = new HashSet<Privilege>();
        for (Privilege privilege : privileges) {
            Privilege privilegeBean = new Privilege();
            privilegeBean.setUuid(privilege.getUuid());
            privilegeBeans.add(privilegeBean);
        }
        bean.setPrivileges(privilegeBeans);

        // 2、设置角色
        Set<NestedRole> nestedRoles = role.getNestedRoles();
        Set<NestedRole> nestedRoleBeans = new HashSet<NestedRole>();
        for (NestedRole nestedRole : nestedRoles) {
            NestedRole nestedRole2 = new NestedRole();
            nestedRole2.setRoleUuid(nestedRole.getRoleUuid());
            nestedRoleBeans.add(nestedRole2);
        }
        bean.setNestedRoles(nestedRoleBeans);

        // 获取角色的所有成员
        Map<String, String> members = this.queryRoleMembers(uuid);
        if (MapUtils.isNotEmpty(members)) {
            bean.setMemberIds(StringUtils.join(members.keySet(), ";"));
            bean.setMemberNames(StringUtils.join(members.values(), ";"));
            List<String> eleIds = new ArrayList<>();
            List<String> names = new ArrayList<>();
            for (String key : members.keySet()) {
                if (!key.startsWith(IdPrefix.GROUP.getValue())) {
                    eleIds.add(key);
                    names.add(members.get(key));
                }
            }
            Map<String, OrgNode> map = multiOrgTreeDialogService.smartName(eleIds, names);
            List<String> smartNameList = new ArrayList<>();
            for (String key : members.keySet()) {
                if (!key.startsWith(IdPrefix.USER.getValue()) && !key.startsWith(IdPrefix.GROUP.getValue())) {
                    smartNameList.add(map.get(key).getSmartNamePath());
                } else {
                    smartNameList.add(members.get(key));
                }
            }
            bean.setMemberSmartNames(StringUtils.join(smartNameList, ";"));
        }
        return bean;
    }

    /**
     * 保存角色
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.RoleService#saveBean(com.wellsoft.pt.security.audit.bean.RoleBean)
     */
    @Override
    @Transactional
    public String saveBean(RoleBean bean) {
        Role role = new Role();

        if (StringUtils.isEmpty(bean.getCategoryName())) {
            bean.setCategoryUuid(null);
        }

        if (StringUtils.isBlank(bean.getUuid())) {
            bean.setUuid(null);
            // ID非空唯一性判断
            if (StringUtils.isNotBlank(bean.getId()) && commonValidateService.checkExists("role", "id", bean.getId())) {
                throw new RuntimeException("已经存在ID为[" + bean.getId() + "]的角色!");
            }
        } else {
            role = this.dao.getOne(bean.getUuid());
            // ID非空唯一性判断
            if (StringUtils.isNotBlank(bean.getId())
                    && !commonValidateService.checkUnique(bean.getUuid(), "role", "id", bean.getId())) {
                throw new RuntimeException("已经存在ID为[" + bean.getId() + "]的角色!");
            }
        }

        // 乐观锁判断
        if (role.getRecVer() != null && !role.getRecVer().equals(bean.getRecVer())) {
            throw new RuntimeException("数据已过时，请重新加载再更改保存!");
        }

        if (StringUtils.isBlank(bean.getId())) {
            throw new RuntimeException("角色ID不能为空！");
        }
        if (StringUtils.isBlank(bean.getCode())) {
            bean.setCode(bean.getId());
        }

        BeanUtils.copyProperties(bean, role, IdEntity.BASE_FIELDS);
        role.setSystem(RequestSystemContextPathResolver.system());
        role.setTenant(SpringSecurityUtils.getCurrentTenantId());
        this.dao.save(role);
        bean.setUuid(role.getUuid());
        if (bean.getPrivileges() != null) {
            // 1、设置权限
            Set<Privilege> privileges = role.getPrivileges();
            if (privileges != null) {
                privileges.clear();
                Set<Privilege> newPrivileges = bean.getPrivileges();
                for (Privilege newPrivilege : newPrivileges) {
                    Privilege privilege = this.privilegeService.get(newPrivilege.getUuid());
                    privileges.add(privilege);
                }
            }
        }


        // 2、设置角色(角色嵌套多对多)
        if (bean.getNestedRoles() != null) {
            Set<NestedRole> nestedRoles = role.getNestedRoles();
            if (nestedRoles != null) {
                // 删除role对应的被控制方
                Iterator<NestedRole> iterator = nestedRoles.iterator();
                while (iterator.hasNext()) {
                    NestedRole nestedRole = iterator.next();
                    Role child = this.get(nestedRole.getRoleUuid());
                    //系统默认生成不能删除
                    if (child.getSystemDef() == 1) {
                        continue;
                    }
                    iterator.remove();
                    this.nestedRoleService.delete(nestedRole);
                }

                Set<NestedRole> newNestedRoles = bean.getNestedRoles();
                for (NestedRole nestedRole : newNestedRoles) {
                    nestedRoleService.save(nestedRole);
                    nestedRoles.add(nestedRole);
                }
                // 判断角色是否递归相互嵌套
                checkRoleRecursively(role, role, role.getNestedRoles());
            }
        }


        this.dao.flushSession();
        return role.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.RoleService#publishRoleUpdatedEvent(java.lang.String)
     */
    @Override
    @Transactional(readOnly = true)
    public void publishRoleUpdatedEvent(String uuid) {
        // 发布安全配置变更事件
        SecurityConfigUtils.publishSecurityConfigUpdatedEvent(uuid, SecurityConfigType.ROLE);
    }

    /**
     * 判断角色是否递归相互嵌套
     *
     * @param checkRole   要检查的角色
     * @param rootRole    包含角色嵌套的父节点
     * @param nestedRoles 角色嵌套的子结点
     */
    private void checkRoleRecursively(Role checkRole, Role rootRole, Set<NestedRole> nestedRoles) {
        for (NestedRole nestedRole : nestedRoles) {
            Role childRole = this.dao.getOne(nestedRole.getRoleUuid());
            if (childRole == null) {
                continue;
            }
            // 角色是否递归相互嵌套，抛出异常
            if (checkRole.equals(childRole)) {
                throw new RuntimeException("Role cannot contain itself recursively (via role '" + rootRole.getName()
                        + "').");
            }
            // 角色结点ID以R开头, 权限结点ID以P开头
            checkRoleRecursively(checkRole, childRole, childRole.getNestedRoles());
        }
    }

    /**
     * 根据UUID删除角色
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.RoleService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        Role role = this.dao.getOne(uuid);
        if (role == null) {
            return;
        }
        // 1、删除角色与用户组多对多关系中作为被控方的关系表
        // 2、删除角色与用户多对多关系中作为被控方的关系表
        // 3、删除角色与职位多对多关系中作为被控方的关系表
        this.multiOrgApiFacade.dealRoleRemoveEvent(uuid);
        this.orgFacadeService.deleteRelaRoleByRoleUuid(uuid);

        // 5、 查找并删除role作为被控方的实体NestedRole及关联表
        List<NestedRole> nestedRoles = this.nestedRoleService.getByRole(uuid);
        for (NestedRole nestedRole : nestedRoles) {
            Set<Role> roles = nestedRole.getRoles();
            for (Role tmp : roles) {
                tmp.getNestedRoles().remove(nestedRole);
            }
            this.nestedRoleService.delete(nestedRole);
        }

        // 6、 删除role作为主控方的实体，自动删除关联
        this.dao.delete(role);

        ApplicationContextHolder.getBean(AuditDataFacadeService.class).saveAuditDataLog(
                new AuditDataLogDto().name(role.getName()).operation("delete_audit_role").remark("删除角色")
                        .diffEntity(null, role));

        // 7、 删除role对应的被控制方
        Set<NestedRole> nestedRoles2 = role.getNestedRoles();
        for (NestedRole nestedRole : nestedRoles2) {
            this.nestedRoleService.delete(nestedRole);
        }
        // 删除不存在的内嵌角色
        this.dao.deleteBySQL("delete from audit_nested_role p where not exists ( select 1 from audit_role a where a.uuid = p.role_uuid )", null);
        this.dao.deleteBySQL("delete from audit_role_nested_role p where not exists ( select 1 from audit_role a where a.uuid = p.role_uuid ) " +
                " or not exists ( select 1 from audit_nested_role a where a.uuid = p.nested_role_uuid) ", null);

    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.RoleService#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.remove(uuid);
        }
        // 删除不存在的内嵌角色
        this.dao.deleteBySQL("delete from audit_nested_role p where not exists ( select 1 from audit_role a where a.uuid = p.role_uuid )", null);
        this.dao.deleteBySQL("delete from audit_role_nested_role p where not exists ( select 1 from audit_role a where a.uuid = p.role_uuid ) " +
                " or not exists ( select 1 from audit_nested_role a where a.uuid = p.nested_role_uuid) ", null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.RoleService#query(com.wellsoft.pt.common.component.jqgrid.JqGridQueryInfo)
     */
    @Override
    public JqGridQueryData query(JqGridQueryInfo queryInfo) {
        PagingInfo page = new PagingInfo(queryInfo.getPage(), queryInfo.getRows(), true);
        List<Role> roles = this.dao.listAllByOrderPage(page, null);
        List<Role> jqUsers = new ArrayList<Role>();
        for (Role role : roles) {
            Role jqRole = new Role();
            BeanUtils.copyProperties(role, jqRole);
            jqUsers.add(jqRole);
        }
        JqGridQueryData queryData = new JqGridQueryData();
        queryData.setCurrentPage(queryInfo.getPage());
        queryData.setDataList(jqUsers);
        queryData.setRepeatitems(false);
        queryData.setTotalPages(page.getTotalPages());
        queryData.setTotalRows(page.getTotalCount());
        return queryData;
    }

    /**
     * 加载角色权限树，选择已选角色权限，不包含当前角色
     * <p>
     * (non-Javadoc)
     *
     * @see RoleService#getRolePrivilegeTree(String, String)
     */
    @Override
    public TreeNode getRolePrivilegeTree(String uuid, String appId) {
        TreeNode treeNode = new TreeNode();
        treeNode.setName("角色/权限");
        treeNode.setId(TreeNode.ROOT_ID);
        treeNode.setNocheck(true);
        List<String> appIds = Lists.newArrayList(appProductIntegrationService
                .getSelfWithChildrenModuleAndAppDataIdsByDataId(appId));

        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        List<Role> roles = this.dao.queryRoleListOfUnitIdAndPTRoleList(unitId, appIds.toArray(new String[0]));
        Set<String> ownerRoles = new HashSet<String>();
        Set<Privilege> ownerPrivileges = new HashSet<Privilege>();
        if (StringUtils.isNotBlank(uuid)) {
            Role role = this.dao.getOne(uuid);
            Set<NestedRole> nestedRoles = role.getNestedRoles();

            for (NestedRole nestedRole : nestedRoles) {
                ownerRoles.add(nestedRole.getRoleUuid());
            }
            ownerPrivileges = role.getPrivileges();
        }

        //按角色编码升序
        Collections.sort(roles, new Comparator<Role>() {
            @Override
            public int compare(Role o1, Role o2) {
                return o1.getCode().compareTo(o2.getCode());
            }
        });

        // 附加构建角色树，排除当前角色及已拥有的角色

        buildRoleTree(treeNode, roles, Arrays.asList(ownerRoles.toArray(new String[0])), uuid);

        // 附加构建权限树
        // 只差平台和自己单位的权限
        List<Privilege> privileges = this.privilegeService.queryPrivilegeListOfUnitIdAndPTRoleList(unitId,
                appIds.toArray(new String[0]));

        //按权限编码升序
        Collections.sort(privileges, new Comparator<Privilege>() {
            @Override
            public int compare(Privilege o1, Privilege o2) {
                return o1.getCode().compareTo(o2.getCode());
            }
        });
        buildPrivilegeTree(treeNode, privileges, Arrays.asList(ownerPrivileges.toArray(new Privilege[0])));

        return treeNode;
    }

    /**
     * 附加构建角色树，排除当前角色及已拥有的角色
     *
     * @param treeNode
     * @param roles
     * @param uuid
     */
    private void buildRoleTree(TreeNode treeNode, List<Role> roles, List<String> checkedRoles, String excludeUuid) {
        List<TreeNode> children = new ArrayList<TreeNode>();
        for (Role role : roles) {
            if (role.getUuid().equals(excludeUuid)) {
                continue;
            }

            TreeNode child = new TreeNode();
            // 角色结点ID以R开头, 权限结点ID以P开头
            child.setId(IdPrefix.ROLE.getValue() + role.getUuid());
            child.setName(role.getName());
            children.add(child);
            child.setData(role.getSystemUnitId());

            // 选中已拥有的结点
            child.setChecked(checkedRoles.contains(role.getUuid()));
        }
        treeNode.setChildren(children);
    }

    /**
     * 附加构建权限树
     *
     * @param treeNode
     * @param privileges
     * @param asList
     */
    private void buildPrivilegeTree(TreeNode treeNode, List<Privilege> privileges, List<Privilege> checkedResources) {
        List<TreeNode> children = new ArrayList<TreeNode>();
        for (Privilege privilege : privileges) {
            TreeNode child = new TreeNode();
            // 角色结点ID以R开头, 权限结点ID以P开头
            child.setId(IdPrefix.PRIVILEGE.getValue() + privilege.getUuid());
            child.setName(privilege.getName());
            child.setData(privilege.getSystemUnitId());
            children.add(child);

            // 选中已拥有的结点
            child.setChecked(checkedResources.contains(privilege));
        }
        treeNode.getChildren().addAll(children);
    }

    /**
     * 根据UUID加载权限树, 包含角色嵌套及权限
     */
    @Override
    public TreeNode queryPrivilegeResultAsTree(String uuid) {
        Role role = this.dao.getOne(uuid);
        TreeNode treeNode = new TreeNode();
        treeNode.setName(role.getName());
        treeNode.setId(TreeNode.ROOT_ID);

        buildRoleNestedRoleTree(treeNode, role);
        return treeNode;
    }

    /**
     * @param treeNode
     * @param role
     */
    @Override
    public void buildRoleNestedRoleTree(TreeNode treeNode, Role role) {
        List<TreeNode> children = new ArrayList<TreeNode>();
        // 1、角色
        Set<NestedRole> nestedRoles = role.getNestedRoles();
        for (NestedRole nestedRole : nestedRoles) {
            Role childRole = this.dao.getOne(nestedRole.getRoleUuid());
            if (childRole == null) {
                continue;
            }
            if (childRole.getSystemDef() == 1) {
                continue;
            }
            TreeNode childNode = new TreeNode();
            // 角色结点ID以R开头, 权限结点ID以P开头
            childNode.setId(IdPrefix.ROLE.getValue() + childRole.getUuid());
            childNode.setName(childRole.getName());
            buildRoleNestedRoleTree(childNode, childRole);

            children.add(childNode);
        }

        // 2、权限
        Set<Privilege> privileges = role.getPrivileges();
        for (Privilege privilege : privileges) {
            if (privilege.getSystemDef() == 1) {
                continue;
            }
            TreeNode childNode = new TreeNode();
            // 角色结点ID以R开头, 权限结点ID以P开头
            childNode.setId(IdPrefix.PRIVILEGE.getValue() + privilege.getUuid());
            childNode.setName(privilege.getName());

            children.add(childNode);
        }
        treeNode.setChildren(children);
    }

    /**
     * 获取人员/部门/群组列表
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.RoleService#queryRoleMembers(java.lang.String)
     */
    @Override
    public Map<String, String> queryRoleMembers(String uuid) {
        Map<String, String> map = Maps.newHashMap();
        // 1、人员
        List<MultiOrgUserAccount> users = this.multiOrgApiFacade.queryUserListByRole(uuid);
        for (MultiOrgUserAccount user : users) {
            map.put(user.getId(), user.getUserName());
        }

        // 2，组织节点,包括单位，部门，职位
        List<OrgTreeNodeDto> orgNodes = this.multiOrgApiFacade.queryOrgNodeListByRole(uuid);
        for (OrgTreeNodeDto node : orgNodes) {
            map.put(node.getEleId(), node.getEleNamePath());
        }

        // 3、群组
        List<MultiOrgGroup> groups = this.multiOrgApiFacade.queryGroupListByRole(uuid);
        for (MultiOrgGroup group : groups) {
            map.put(group.getId(), group.getName());
        }

        // 7.0组织角色成员信息
        OrgRelaRoleMembersDto relaRoleMemberDto = orgFacadeService.getRelaRoleMembersByRoleUuid(uuid);
        map.putAll(relaRoleMemberDto.getUsers());
        map.putAll(relaRoleMemberDto.getOrgElements());
        map.putAll(relaRoleMemberDto.getGroups());
        return map;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.RoleService#hasRole(java.lang.String, java.lang.String)
     */
    @Override
    public boolean hasRole(String userId, String roleId) {
        Role auditRole = this.dao.getById(roleId);
        if (auditRole == null) {
            return false;
        }

        // 7.0组织的用户角色
        List<String> uerRoleUuids = userInfoFacadeService.getUserRolesByUserId(userId);
        if (CollectionUtils.isNotEmpty(uerRoleUuids) && uerRoleUuids.contains(auditRole.getUuid())) {
            return true;
        }
        Set<String> orgUserRoleUuids = orgFacadeService.getOrgUserRoleUuidsByUserId(userId);
        if (CollectionUtils.isNotEmpty(orgUserRoleUuids) && orgUserRoleUuids.contains(auditRole.getUuid())) {
            return true;
        }

        OrgUserVo user = this.multiOrgApiFacade.getUserById(userId);
        if (user == null) {
            return false;
        }
        String roleUuid = roleId;
        if (roleId.startsWith("ROLE_")) {
            roleUuid = auditRole.getUuid();
        }
        // 1、用户自身具有的角色
        String[] userRoles = user.getRoleUuids() == null ? new String[]{} : user.getRoleUuids().split(";");
        for (String role : userRoles) {
            if (role.equals(roleUuid)) {
                return true;
            }
        }

        // 2、用户所在职位，部门，单位 具有的角色
        Set<String> userOrgIds = orgApiFacade.getUserOrgIds(user);
        for (String id : userOrgIds) {
            List<MultiOrgElementRole> eleRoles = this.multiOrgApiFacade.queryRoleListOfElement(id);
            if (!CollectionUtils.isEmpty(eleRoles)) {
                for (MultiOrgElementRole eleRole : eleRoles) {
                    if (eleRole.getRoleUuid().equals(roleUuid)) {
                        return true;
                    }
                }
            }
        }

        // 3、用户所在群组具有的角色
        List<MultiOrgGroupMember> list = this.multiOrgApiFacade.queryGroupListByMemberId(user.getId());
        if (!CollectionUtils.isEmpty(list)) {
            for (MultiOrgGroupMember group : list) {
                List<MultiOrgGroupRole> groupRoles = this.multiOrgApiFacade.queryRoleListOfGroup(group.getGroupId());
                if (!CollectionUtils.isEmpty(groupRoles)) {
                    for (MultiOrgGroupRole groupRole : groupRoles) {
                        if (groupRole.getRoleUuid().equals(roleUuid)) {
                            return true;
                        }
                    }
                }
            }
        }

        // 4，检查用户是否属于表单中的有关联角色的组织控件的用户
        // 获取有关联该角色的
//        List<FormControlRole> dyformRoleList = this.dyFormFacade.queryDyformListByRole(roleUuid);
//        if (!CollectionUtils.isEmpty(dyformRoleList)) {
//            // 获取用户对应的组织相关的ID,并包含自己
//            userOrgIds.add(userId);
//            for (FormControlRole controlRole : dyformRoleList) {
//                // 获取该表单对应的所有数据
//                if (this.dyFormFacade.isHasControlRoleByUserOrgIds(userOrgIds, controlRole)) {
//                    return true;
//                }
//            }
//
//        }
        return false;
    }

    /**
     * 获取角色树
     */
    @Override
    public TreeNode getRoleTree() {
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        List<Role> allRoles = this.dao.queryRoleListOfUnitIdAndPTRoleList(unitId, null);
        // 将所有角色按目录分类
        Map<String, List<Role>> roleGroupMap = new List2GroupMap<Role>() {
            @Override
            protected String getGroupUuid(Role obj) {
                if (StringUtils.isBlank(obj.getCategoryUuid())) {
                    obj.setCategoryUuid("-1");
                    obj.setCategoryName("无分类");
                }
                return obj.getCategoryUuid();
            }
        }.convert(allRoles);
        String categoryType = Config.getValue("audit.security.category.type", "MODULE_CATEGORY");
        DataDictionary type = dataDictionaryService.getByType(categoryType);
        TreeNode treeNode = dataDictionaryService.getAllAsTree(type.getUuid());
        if (roleGroupMap.containsKey("-1")) {
            TreeNode defNode = new TreeNode("-1", "无分类", null);
            defNode.setData("-1");
            treeNode.getChildren().add(defNode);
        }
        if (treeNode != null) {
            treeNode.setName("角色列表");

            treeNode = new TreeNode4AddNodeFromGroupMap<Role>() {
                @Override
                protected String getGroupMapKeyFromTreeNode(TreeNode node) {
                    return node.getData().toString();
                }

                @Override
                protected TreeNode obj2TreeNode(Role obj, TreeNode parentNode) {
                    TreeNode node = new TreeNode();
                    node.setId(obj.getUuid());
                    node.setName(obj.getName());
                    node.setData(obj.getId());
                    node.setType(IdPrefix.ROLE.getValue());
                    return node;
                }
            }.addNode(treeNode, roleGroupMap);
        }
        return treeNode;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.RoleService#getAll()
     */
    @Override
    public List<Role> getAll() {
        return listAll();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.RoleService#isExistsById(java.lang.String)
     */
    @Override
    public boolean isExistsById(String roleId) {
        Role role = new Role();
        role.setId(roleId);
        role.setSystemUnitId(SpringSecurityUtils.getCurrentUserUnitId());
        return this.dao.countByEntity(role) > 0;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.RoleService#createRole(java.lang.String, java.lang.String)
     */
    @Override
    public void createRole(String roleName, String roleId) {
        Role role = new Role();
        role.setName(roleName);
        role.setId(roleId);
        role.setCode(roleId);
        String categoryType = Config.getValue("audit.security.category.type");
        DataDictionary parent = dataDictionaryService.getByType(categoryType);
        List<DataDictionary> dataDictionarySet = parent.getChildren();
        if (!CollectionUtils.isEmpty(dataDictionarySet)) {
            Iterator<DataDictionary> it = dataDictionarySet.iterator();
            DataDictionary dataDictionary = it.next();
            role.setCategoryUuid(dataDictionary.getCode());
            role.setCategoryName(dataDictionary.getName());
        }
        this.dao.save(role);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.RoleService#authorizeOrgIdsById(java.lang.String, java.util.Collection)
     */
    @Override
    public void authorizeOrgIdsById(String roleId, Collection<String> orgIds) {
        // TODO Auto-generated method stub
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.RoleService#revokeOrgIdsById(java.lang.String, java.util.Collection)
     */
    @Override
    public void revokeOrgIdsById(String roleId, Collection<String> orgIds) {
        // TODO Auto-generated method stub
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.RoleService#queryRoleListByUnit(java.lang.String)
     */
    @Override
    public List<Role> queryRoleListByUnit(String unitId) {
        Role role = new Role();
        role.setSystemUnitId(unitId);
        return this.dao.listByEntity(role, null, "name asc", null);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.RoleService#getRoleById(java.lang.String)
     */
    @Override
    public Role getRoleById(String id) {
        return this.dao.getById(id);
    }

    @Override
    public List<Role> queryRoleByCurrentUserUnitId() {
        String unitId = SpringSecurityUtils.getCurrentUserUnitId();
        List<Role> allRoles = this.dao.queryRoleListOfUnitIdAndPTRoleList(unitId, null);
        return allRoles;
    }

    @Override
    public Set<NestedRole> getNestedRolesByRoleUuid(String roleUuid) {
        Role role = getOne(roleUuid);
        Set<NestedRole> nestedRoles = role.getNestedRoles();
        return nestedRoles;
    }


    @Override
    public Set<GrantedAuthority> obtainGrantedAuthorities(Map<String, Set<String>> userRoles) {
        Set<GrantedAuthority> authSet = Sets.newHashSet();
        authSet.add(new SimpleGrantedAuthority(BuildInRole.ROLE_ANONYMOUS.name()));
        authSet.add(new SimpleGrantedAuthority(BuildInRole.ROLE_USER.name()));

        if (userRoles != null && !userRoles.isEmpty()) {
            // Set<Role> grantedRoles = userService.getUserOrgRoles(user);
            for (String roleUuid : userRoles.keySet()) {
                Role role = getOne(roleUuid);
                if (role != null) {
                    Set<String> from = userRoles.get(roleUuid);
                    authSet.add(new RoleAuthority(role.getId(), role.getName(), from));
                    // 获取嵌套的角色
                    obtainNestedRole(authSet, role);
                }
            }
        }

        return authSet;
    }

    @Override
    public Role getByCode(String roleCode) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("code", roleCode);
        return dao.getOneByHQL("from Role where code=:code", params);
    }

    @Override
    public Role getByName(String roleName) {
        List<Role> list = queryByName(roleName);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        Role bean = new Role();
        BeanUtils.copyProperties(list.get(0), bean);
        return bean;
    }

    @Override
    public List<Role> queryByName(String roleName) {
        Role entity = new Role();
        entity.setName(roleName);
        String currentUserUnitId = SpringSecurityUtils.getCurrentUserUnitId();
        if (StringUtils.isNotBlank(currentUserUnitId)) {
            entity.setSystemUnitId(currentUserUnitId);
        }
        return dao.listByEntity(entity);
    }

    @Override
    public List<RoleDto> queryRoleDtosBySystemUnitId(String unitId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("unitId", unitId);
        List<Role> roles = this.queryRoleListByUnit(unitId);
        List<RoleDto> roleDtos = Lists.newArrayList();
        for (Role r : roles) {
            roleDtos.add(new RoleDto(r.getId(), r.getName()));
        }
        return roleDtos;
    }

    @Override
    public List<Role> getRolesByIds(List<String> ids) {
        return this.dao.listByFieldInValues("id", ids);
    }

    @Override
    public List<RoleDto> getRolesByAppId(String appId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("appId", appId);
        List<Role> roles = dao.listByHQL("from Role where appId=:appId order by createTime desc", params);
        List<RoleDto> dtos = Lists.newArrayListWithCapacity(roles.size());
        for (Role role : roles) {
//            RoleDto dto = new RoleDto(role.getUuid(), role.getId(), role.getName(), role.getRemark(), role.getRecVer(), role.getAppId());
            dtos.add(getRoleMembers(role.getUuid()));
        }
        return dtos;
    }

    @Override
    public RoleDto getRoleMembers(String uuid) {
        Role role = getOne(uuid);
        Set<Privilege> privileges = role.getPrivileges();
        Set<NestedRole> nestedRoles = role.getNestedRoles();
        RoleDto dto = new RoleDto();
        org.springframework.beans.BeanUtils.copyProperties(role, dto);
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(privileges)) {
            for (Privilege p : privileges) {
                PrivilegeDto pDto = new PrivilegeDto();
                org.springframework.beans.BeanUtils.copyProperties(p, pDto);
                dto.getPrivileges().add(pDto);
            }
        }
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(nestedRoles)) {
            for (NestedRole p : nestedRoles) {
                Role _role = getOne(p.getRoleUuid());
                if (_role != null) {
                    RoleDto rDto = new RoleDto();
                    org.springframework.beans.BeanUtils.copyProperties(_role, rDto);
                    dto.getNestedRoles().add(rDto);
                }

            }
        }
        return dto;
    }

    @Override
    public List<RoleDto> getRolesByAppIds(List<String> appId) {
        Map<String, Object> params = Maps.newHashMap();
        params.put("appId", appId);
        List<Role> roles = dao.listByHQL("from Role where systemDef = 0 " + (CollectionUtils.isEmpty(appId) ? "" : " and appId in :appId") + " order by createTime desc", params);
        List<RoleDto> dtos = Lists.newArrayListWithCapacity(roles.size());
        for (Role role : roles) {
            RoleDto dto = new RoleDto(role.getUuid(), role.getId(), role.getName(), role.getRemark(), role.getRecVer(), role.getAppId());
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public List<Role> listRolesByAppId(String appId) {
        List<Role> roles = dao.listByFieldEqValue("appId", appId);
        return roles;
    }

    @Override
    public List<RoleDto> getRolesByUuids(List<String> roleUuids) {
        List<Role> roles = dao.listByFieldInValues("uuid", roleUuids);
        List<RoleDto> dtos = Lists.newArrayListWithCapacity(roles.size());
        for (Role r : roles) {
            RoleDto dto = new RoleDto();
            dto.setUuid(r.getUuid());
            dto.setName(r.getName());
            dto.setId(r.getId());
            dto.setRemark(dto.getRemark());
            dto.setSystemDef(r.getSystemDef());
            dto.setAppId(r.getAppId());
            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public TreeNode getRolePrivilegeTree(String roleUuid) {
        Role role = getOne(roleUuid);
        if (role != null && TreeNode.TreeContextHolder.addId(roleUuid) && role.getSystemDef() == 0) {
            TreeNode node = new TreeNode();
            node.setName(role.getName());
            node.setId(role.getUuid());
            node.setType("ROLE");
            this.obtainNestedRoleAndPrivilegeTreeNode(node, role);
            return node;
        }
        return null;
    }

    @Override
    public List<TreeNode> getRoleResourceTreeByUuid(String uuid) {
        Role role = getOne(uuid);
        List<TreeNode> nodes = Lists.newArrayList();
        if (role != null && role.getSystemDef() == 0) {
            Set<Privilege> privileges = role.getPrivileges();
            if (!CollectionUtils.isEmpty(privileges)) {
                for (Privilege p : privileges) {
                    if (p.getSystemDef() == 0) {
                        List<TreeNode> list = privilegeService.getPrivilegeOtherResourceTreeNode(p.getUuid());
                        if (!CollectionUtils.isEmpty(list)) {
                            nodes.addAll(list);
                        }
                    }
                }
            }
            List<TreeNode> nestedRoleResource = getNestedRoleResourceTree(role);
            if (!CollectionUtils.isEmpty(nestedRoleResource)) {
                nodes.addAll(nestedRoleResource);
            }
        }
        return nodes;
    }


    @Override
    public List<TreeNode> getRolePrivilegeResourceTreeByUuid(String uuid) {
        Role role = getOne(uuid);
        List<TreeNode> nodes = Lists.newArrayList();
        if (role != null && role.getSystemDef() == 0) {
            Set<Privilege> privileges = role.getPrivileges();
            if (!CollectionUtils.isEmpty(privileges)) {
                for (Privilege p : privileges) {
                    TreeNode privilegeNode = new TreeNode(p.getUuid(), p.getName(), null);
                    privilegeNode.setType("PRIVILEGE");
                    PrivilegeDto privilegeDto = new PrivilegeDto();
                    privilegeDto.setUuid(p.getUuid());
                    privilegeDto.setName(p.getName());
                    privilegeDto.setCode(p.getCode());
                    privilegeDto.setAppId(p.getAppId());
                    privilegeDto.setSystemDef(p.getSystemDef());
                    privilegeNode.setData(privilegeDto);
                    nodes.add(privilegeNode);
                    List<TreeNode> list = privilegeService.getPrivilegeOtherResourceTreeNode(p.getUuid());
                    if (!CollectionUtils.isEmpty(list)) {
                        privilegeNode.getChildren().addAll(list);
                    }
                }
            }
            List<TreeNode> nestedRoleResource = getNestedRoleResourceTree(role);
            if (!CollectionUtils.isEmpty(nestedRoleResource)) {
                nodes.addAll(nestedRoleResource);
            }
        }
        return nodes;
    }

    @Override
    @Transactional
    public List<String> updateRoleMember(List<UpdateRoleMemberDto> list) {
        List<String> uuids = Lists.newArrayList();
        for (UpdateRoleMemberDto dto : list) {
            Role role = new Role();
            if (dto.getRole() == null) {
                continue;
            }
            Role oldRole = null;
            if (StringUtils.isNotBlank(dto.getRole().getUuid())) {
                role = getOne(dto.getRole().getUuid());
                oldRole = new Role();
                org.springframework.beans.BeanUtils.copyProperties(role, oldRole, "nestedRoles", "privileges");
            } else {
                role.setUuid(dto.getRole().getUuid());
            }
            if (StringUtils.isNotBlank(dto.getRole().getName())) {
                role.setId(dto.getRole().getId());
                role.setName(dto.getRole().getName());
                role.setRemark(dto.getRole().getRemark());
                role.setCode(dto.getRole().getCode());
                role.setSystem(dto.getRole().getSystem());
                role.setAppId(dto.getRole().getAppId());
                role.setSystemDef(dto.getRole().getSystemDef());
                if (StringUtils.isNotBlank(dto.getRole().getSystem())) {
                    role.setTenant(StringUtils.defaultIfBlank(dto.getRole().getTenant(), SpringSecurityUtils.getCurrentTenantId()));
                }
                this.save(role);
            }
            AuditDataLogDto logDto = new AuditDataLogDto().diffEntity(role, oldRole).name(role.getName()).remark(oldRole == null ? "创建角色" : "编辑角色");
            uuids.add(role.getUuid());

            if (StringUtils.isNotBlank(role.getUuid())) {
                if (CollectionUtils.isNotEmpty(dto.getPrivilegeRemoved())) {
                    // 删除权限与角色的关系
                    Map<String, Object> param = Maps.newHashMap();
                    param.put("deletePrivilegeUuids", dto.getPrivilegeRemoved());
                    param.put("roleUuid", role.getUuid());
                    for (String p : dto.getPrivilegeRemoved()) {
                        Privilege privilege = privilegeService.getOne(p);
                        AuditDataLogDto child = new AuditDataLogDto().tableName("audit_role_privilege").dataUuid(p).operation("remove_audit_role_privilege").remark("移除权限");
                        if (privilege != null) {
                            child.name(privilege.getName());
                        }
                        logDto.getChildren().add(child);
                    }
                    this.dao.deleteBySQL("delete from audit_role_privilege where privilege_uuid in (:deletePrivilegeUuids) and role_uuid = :roleUuid", param);
                }
                if (CollectionUtils.isNotEmpty(dto.getPrivilegeAdded())) {
                    // 新增权限与角色的关系
                    for (String p : dto.getPrivilegeAdded()) {
                        Map<String, Object> param = Maps.newHashMap();
                        param.put("privilegeUuid", p);
                        param.put("roleUuid", role.getUuid());
                        long num = this.dao.countBySQL("select 1 from audit_role_privilege where role_uuid=:roleUuid and privilege_uuid=:privilegeUuid ", param);
                        if (num == 0) {
                            Privilege privilege = privilegeService.getOne(p);
                            AuditDataLogDto child = new AuditDataLogDto().tableName("audit_role_privilege").dataUuid(p).operation("add_audit_role_privilege").remark("添加权限");
                            if (privilege != null) {
                                child.name(privilege.getName());
                            }
                            logDto.getChildren().add(child);
                            this.dao.updateBySQL("insert into audit_role_privilege  (role_uuid,privilege_uuid) values ( :roleUuid,:privilegeUuid )", param);
                        }
                    }
                }

                if (CollectionUtils.isNotEmpty(dto.getNestedRoleRemoved())) {
                    Map<String, Object> param = Maps.newHashMap();
                    param.put("roleUuid", role.getUuid());
                    for (String childRoleUuid : dto.getNestedRoleRemoved()) {
                        param.put("childRoleUuid", childRoleUuid);
                        List<String> nestedRoleUuids = this.dao.listCharSequenceBySQL("select uuid from audit_nested_role nr where nr.role_uuid=:childRoleUuid and  " +
                                "exists ( select 1 from audit_role_nested_role rnr where rnr.role_uuid=:roleUuid and rnr.nested_role_uuid=nr.uuid ) ", param);
                        this.dao.deleteBySQL("delete from audit_role_nested_role rnr where rnr.role_uuid=:roleUuid and  " +
                                "exists ( select 1 from audit_nested_role nr where nr.role_uuid=:childRoleUuid and rnr.nested_role_uuid=nr.uuid ) ", param);
                        if (CollectionUtils.isNotEmpty(nestedRoleUuids)) {
                            nestedRoleDao.delete(nestedRoleUuids.get(0));
                            Role childRole = this.getOne(childRoleUuid);
                            AuditDataLogDto child = new AuditDataLogDto().tableName("audit_nested_role").dataUuid(childRoleUuid).operation("remove_audit_nested_role").remark("移除子角色");
                            if (childRole != null) {
                                child.name(childRole.getName());
                            }
                            logDto.getChildren().add(child);
                        }
                    }

                }
                if (CollectionUtils.isNotEmpty(dto.getNestedRoleAdded())) {
                    // 前端判断是否存在角色嵌套
                    Map<String, Object> param = Maps.newHashMap();
                    param.put("roleUuid", role.getUuid());
                    for (String r : dto.getNestedRoleAdded()) {
                        boolean recursive = this.checkNestedRecursively(role.getUuid(), r);
                        if (recursive) {
                            this.logger.warn("角色存在嵌套: 角色->{} , 成员角色->{}", new String[]{role.getUuid(), r});
                            continue;
                        }
                        NestedRole nestedRole = new NestedRole();
                        nestedRole.setRoleUuid(r);
                        nestedRoleDao.save(nestedRole);
                        param.put("nestedRoleUuid", nestedRole.getUuid());
                        this.dao.updateBySQL("insert into audit_role_nested_role (role_uuid,nested_role_uuid) values (:roleUuid,:nestedRoleUuid)", param);
                        Role childRole = this.getOne(r);
                        AuditDataLogDto child = new AuditDataLogDto().tableName("audit_nested_role").dataUuid(r).operation("add_audit_nested_role").remark("添加子角色");
                        if (childRole != null) {
                            child.name(childRole.getName());
                        }
                        logDto.getChildren().add(child);
                    }
                }

                if (CollectionUtils.isNotEmpty(dto.getParentRoleAdded())) {
                    // 前端判断是否存在角色嵌套
                    Map<String, Object> param = Maps.newHashMap();
                    param.put("roleUuid", role.getUuid());
                    for (String r : dto.getParentRoleAdded()) {
                        boolean recursive = this.checkNestedRecursively(r, role.getUuid());
                        if (recursive) {
                            this.logger.warn("角色存在嵌套: 角色->{} , 成员角色->{}", new String[]{role.getUuid(), r});
                            continue;
                        }
                        NestedRole nestedRole = new NestedRole();
                        nestedRole.setRoleUuid(role.getUuid());
                        nestedRoleDao.save(nestedRole);
                        param.put("nestedRoleUuid", nestedRole.getUuid());
                        param.put("parentRoleUuid", r);
                        this.dao.updateBySQL("insert into audit_role_nested_role (role_uuid,nested_role_uuid) values (:parentRoleUuid,:nestedRoleUuid)", param);

                        Role parentRole = this.getOne(r);
                        AuditDataLogDto child = new AuditDataLogDto().tableName("audit_role_nested_role").dataUuid(r).operation("add_to_parent_role").remark("添加至父级角色");
                        if (parentRole != null) {
                            child.name(parentRole.getName());
                        }
                        logDto.getChildren().add(child);
                    }
                }

                if (CollectionUtils.isNotEmpty(dto.getParentRoleRemoved())) {
                    Map<String, Object> param = Maps.newHashMap();
                    param.put("roleUuid", role.getUuid());
                    for (String p : dto.getParentRoleRemoved()) {
                        param.put("parentRole", p);
                        List<String> nestedRoleUuids = this.dao.listCharSequenceBySQL("select uuid from audit_nested_role nr where nr.role_uuid=:roleUuid and  " +
                                "exists ( select 1 from audit_role_nested_role rnr where rnr.role_uuid=:parentRole and rnr.nested_role_uuid=nr.uuid ) ", param);
                        this.dao.deleteBySQL("delete from audit_role_nested_role rnr where rnr.role_uuid=:parentRole and  " +
                                "exists ( select 1 from audit_nested_role nr where nr.role_uuid=:roleUuid and rnr.nested_role_uuid=nr.uuid ) ", param);
                        if (CollectionUtils.isNotEmpty(nestedRoleUuids)) {
                            nestedRoleDao.delete(nestedRoleUuids.get(0));
                            AuditDataLogDto child = new AuditDataLogDto().tableName("audit_role_nested_role").dataUuid(p).operation("remove_from_parent_role").remark("从父级角色中移除");
                            Role parentRole = this.getOne(p);
                            if (parentRole != null) {
                                child.name(parentRole.getName());
                            }
                            logDto.getChildren().add(child);
                        }
                    }
                }

                if (CollectionUtils.isNotEmpty(dto.getUserIdRemoved())) {
                    orgFacadeService.deleteRoleRelaUsers(role.getUuid(), Lists.newArrayList(dto.getUserIdRemoved()));
                    List<UserInfoEntity> userInfoEntities = orgFacadeService.getUserInfosByUserId(Lists.newArrayList(dto.getUserIdRemoved()));
                    if (CollectionUtils.isNotEmpty(userInfoEntities)) {
                        for (UserInfoEntity u : userInfoEntities) {
                            logDto.getChildren().add(new AuditDataLogDto().tableName("user_info").name(u.getUserName()).dataUuid(u.getUuid()).operation("remove_user_role")
                                    .remark("移除角色成员"));
                        }
                    }

                }
                if (CollectionUtils.isNotEmpty(dto.getUserIdAdded())) {
                    orgFacadeService.addRelaRoleMembers(role.getUuid(), Lists.newArrayList(dto.getUserIdAdded()));
                    List<UserInfoEntity> userInfoEntities = orgFacadeService.getUserInfosByUserId(Lists.newArrayList(dto.getUserIdAdded()));
                    if (CollectionUtils.isNotEmpty(userInfoEntities)) {
                        for (UserInfoEntity u : userInfoEntities) {
                            logDto.getChildren().add(new AuditDataLogDto().tableName("user_info").name("用户: " + u.getUserName()).dataUuid(u.getUuid()).operation("add_user_role")
                                    .remark("添加角色成员"));
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(dto.getOrgElementAdded())) {
                    orgFacadeService.addRelaRoleMembers(role.getUuid(), Lists.newArrayList(dto.getOrgElementAdded()));
                    List<OrgSelectProvider.Node> nodes = orgFacadeService.explainOrgElementIdsToNode(dto.getOrgElementAdded());
                    if (CollectionUtils.isNotEmpty(nodes)) {
                        for (OrgSelectProvider.Node n : nodes) {
                            logDto.getChildren().add(new AuditDataLogDto().name(StringUtils.isNotBlank(n.getTypeName()) ? n.getTypeName() + ": " + n.getTitle() : n.getTitle()).dataUuid(n.getKey()).operation("add_role_member")
                                    .remark("添加角色成员"));
                        }
                    }

                }
                if (CollectionUtils.isNotEmpty(dto.getOrgElementRemoved())) {
                    orgFacadeService.deleteRelaRoleMembers(role.getUuid(), Lists.newArrayList(dto.getOrgElementRemoved()));
                    List<OrgSelectProvider.Node> nodes = orgFacadeService.explainOrgElementIdsToNode(dto.getOrgElementRemoved());
                    if (CollectionUtils.isNotEmpty(nodes)) {
                        for (OrgSelectProvider.Node n : nodes) {
                            logDto.getChildren().add(new AuditDataLogDto().name(StringUtils.isNotBlank(n.getTypeName()) ? n.getTypeName() + ": " + n.getTitle() : n.getTitle()).dataUuid(n.getKey()).operation("remove_role_member")
                                    .remark("移除角色成员"));
                        }
                    }
                }
                ApplicationContextHolder.getBean(AuditDataFacadeService.class).saveAuditDataLog(logDto);
            }
        }

        return uuids;

    }

    @Override
    public boolean checkNestedRecursively(String roleUuid, String childRoleUuid) {
        try {
            Map<String, Object> param = Maps.newHashMap();
            param.put("roleUuid", childRoleUuid);
            List<String> roleUuids = nestedRoleDao.listCharSequenceBySQL("select anr.role_uuid from audit_nested_role anr where exists (" +
                    "select 1 from audit_role_nested_role r where r.nested_role_uuid = anr.uuid and r.role_uuid=:roleUuid  )", param);
            if (roleUuids.contains(roleUuid)) {
                throw new RuntimeException("角色循环依赖");
            }
            for (String sub : roleUuids) {
                this.checkNestedRecursively(roleUuid, sub);
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    @Override
    public List<RoleDto> getRoleNestedRoles(String uuid) {
        List<RoleDto> dtos = Lists.newArrayList();
        Map<String, Object> param = Maps.newHashMap();
        param.put("roleUuid", uuid);
        List<Role> roles = this.dao.listBySQL("select * from audit_role r where exists (" +
                " select 1 from audit_role_nested_role rnr , audit_nested_role nr  where rnr.role_uuid=:roleUuid and nr.role_uuid=r.uuid and nr.uuid = rnr.nested_role_uuid " +
                ")", param);
        if (CollectionUtils.isNotEmpty(roles)) {
            for (Role r : roles) {
                RoleDto roleDto = new RoleDto();
                roleDto.setUuid(r.getUuid());
                roleDto.setName(r.getName());
                roleDto.setAppId(r.getAppId());
                roleDto.setSystem(r.getSystem());
                roleDto.setTenant(r.getTenant());
                dtos.add(roleDto);
            }
        }
        return dtos;
    }

    @Override
    public List<RoleDto> getRolesInTenantSystem(String system, String tenant) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("system", system);
        param.put("tenant", tenant);
        // 当前租户系统下创建角色
        List<Role> roles = dao.listByHQL("from Role r where " +
                // 当前租户系统下创建的角色
                "( r.system=:system and tenant=:tenant )" +
                // 当前租户系统对应的产品版本下的模块里创建的角色
                " or exists (" +
                "select 1 from AppSystemInfoEntity s ,AppProdModuleEntity m where s.system=:system and s.tenant=:tenant and " +
                " m.prodVersionUuid = s.prodVersionUuid and m.moduleId = r.appId " +
                ") " +
                // 当前租户系统对应的产品版本里创建的角色
                "or exists (" +
                "select 1 from AppSystemInfoEntity s ,AppProdVersionEntity m where s.system=:system and s.tenant=:tenant and " +
                "m.uuid = s.prodVersionUuid and m.versionId = r.appId " +
                ") " +
                " ", param);
        List<RoleDto> dtos = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(roles)) {
            for (Role r : roles) {
                RoleDto dto = new RoleDto(r.getUuid(), r.getId(), r.getName());
                dto.setSystemDef(r.getSystemDef());
                dto.setSystem(r.getSystem());
                dto.setAppId(r.getAppId());
                dto.setTenant(r.getTenant());
                dtos.add(dto);
            }
        }
        return dtos;
    }

    @Override
    public RoleDto getRoleMembersById(String roleId) {
        Role role = getRoleById(roleId);
        if (role != null) {
            return this.getRoleMembers(role.getUuid());
        }
        return null;
    }

    @Override
    public List<RoleDto> getRolesByNestedRole(String uuid) {
        List<RoleDto> dtos = Lists.newArrayList();
        Map<String, Object> param = Maps.newHashMap();
        param.put("roleUuid", uuid);
        List<Role> roles = this.dao.listBySQL("select * from audit_role r where exists (\n" +
                "    select 1 from audit_nested_role nr , audit_role_nested_role rnr where nr.role_uuid = :roleUuid\n" +
                "    and rnr.nested_role_uuid= nr.uuid and rnr.role_uuid = r.uuid\n" +
                ")", param);
        if (CollectionUtils.isNotEmpty(roles)) {
            for (Role r : roles) {
                RoleDto roleDto = new RoleDto();
                roleDto.setUuid(r.getUuid());
                roleDto.setName(r.getName());
                roleDto.setAppId(r.getAppId());
                roleDto.setSystem(r.getSystem());
                roleDto.setTenant(r.getTenant());
                dtos.add(roleDto);
            }
        }
        return dtos;
    }

    private List<TreeNode> getNestedRoleResourceTree(Role role) {
        Set<NestedRole> nestedRoles = role.getNestedRoles();
        List<TreeNode> nodes = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(nestedRoles)) {
            for (NestedRole nestedRole : nestedRoles) {
                List<TreeNode> list = this.getRolePrivilegeResourceTreeByUuid(nestedRole.getRoleUuid());
                if (!CollectionUtils.isEmpty(list)) {
                    nodes.addAll(list);
                }

            }
        }
        return nodes;
    }

    private void obtainNestedRole(Set<GrantedAuthority> authSet, Role role) {
        for (NestedRole nestedRole : role.getNestedRoles()) {
            Role childRole = getOne(nestedRole.getRoleUuid());
            Set<String> from = Sets.newHashSet("父角色：" + role.getName());
            authSet.add(new RoleAuthority(childRole.getId(), childRole.getName(), from));
            obtainNestedRole(authSet, childRole);
        }
    }

    private void obtainNestedRoleAndPrivilegeTreeNode(TreeNode parent, Role role) {
        Set<NestedRole> nestedRoles = this.getNestedRolesByRoleUuid(parent.getId());
        if (!CollectionUtils.isEmpty(nestedRoles)) {
            for (NestedRole nestedRole : nestedRoles) {
                TreeNode nestedRoleNode = this.getRolePrivilegeTree(nestedRole.getRoleUuid());
                parent.appendChild(nestedRoleNode);
            }
        }
        this.obtainRolePrivileges(role, parent);
    }

    private void obtainRolePrivileges(Role role, TreeNode parent) {
        Set<Privilege> privileges = role.getPrivileges();
        if (!CollectionUtils.isEmpty(privileges)) {
            for (Privilege privilege : privileges) {
                if (TreeNode.TreeContextHolder.addId(privilege.getUuid()) && privilege.getSystemDef() == 0) {
                    TreeNode node = new TreeNode(privilege.getUuid(), privilege.getName(), null);
                    node.setType("PRIVILEGE");
                    parent.appendChild(node);
                }
            }
        }
    }
}
