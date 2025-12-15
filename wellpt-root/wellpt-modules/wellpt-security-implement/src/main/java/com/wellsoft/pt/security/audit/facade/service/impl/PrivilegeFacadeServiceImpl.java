/*
 * @(#)2018年4月12日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.facade.service.impl;

import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.OrgNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.app.support.AppCacheUtils;
import com.wellsoft.pt.app.support.AppConstants;
import com.wellsoft.pt.basicdata.iexport.suport.IexportType;
import com.wellsoft.pt.multi.org.facade.service.MultiOrgApiFacade;
import com.wellsoft.pt.security.audit.bean.PrivilegeBean;
import com.wellsoft.pt.security.audit.dto.UpdatePrivilegeDto;
import com.wellsoft.pt.security.audit.entity.NestedRole;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.audit.service.NestedRoleService;
import com.wellsoft.pt.security.audit.service.PrivilegeResourceService;
import com.wellsoft.pt.security.audit.service.PrivilegeService;
import com.wellsoft.pt.security.audit.service.RoleService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
public class PrivilegeFacadeServiceImpl implements PrivilegeFacadeService {

    @Resource
    private PrivilegeService privilegeService;

    @Autowired
    private PrivilegeResourceService privilegeResourceService;

    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleFacadeService roleFacadeService;
    @Autowired
    private MultiOrgApiFacade multiOrgApiFacade;
    @Autowired
    private NestedRoleService nestedRoleService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService#getPrivilegeBean(java.lang.String)
     */
    @Override
    public PrivilegeBean getPrivilegeBean(String uuid) {
        return privilegeService.getBean(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.SecurityAuditFacadeService#getOtherResourceTree(java.lang.String)
     */
    @Override
    public List<TreeNode> getOtherResourceTree(String uuid) {
        return privilegeService.getOtherResourceTree(uuid);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService#getOtherResourceTree(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getOtherResourceTree(String uuid, String appSystemUuid) {
        return privilegeService.getOtherResourceTree(uuid, appSystemUuid);
    }

    @Override
    public List<TreeNode> getOtherResourceTreeOnlyCheck(String uuid, String privilegeUuid) {
        // 树结构及选中节点数据获取
        List<TreeNode> otherResourceTree = privilegeService.getOtherResourceTree(uuid);
        PrivilegeBean privilegeBean = privilegeService.getBean(privilegeUuid);
        Set<PrivilegeResource> otherResources = privilegeBean.getOtherResources();
        List<String> otherResourceUuidList = new ArrayList<>();
        for (PrivilegeResource otherResource : otherResources) {
            otherResourceUuidList.add(otherResource.getResourceUuid());
        }
        //1、标记
        for (TreeNode treeNode : otherResourceTree) {
            tipDeleteNode(treeNode, otherResourceUuidList, new HashMap<String, TreeNode>());
        }

        //2、移除
        removeSurplusNode(otherResourceTree);

        return otherResourceTree;
    }

    private void removeSurplusNode(List<TreeNode> treeNodeList) {
        for (Iterator<TreeNode> iterator = treeNodeList.iterator(); iterator.hasNext(); ) {
            TreeNode treeNode = iterator.next();
            if (treeNode.isChecked()) {
                removeSurplusNode(treeNode.getChildren());
            } else {
                iterator.remove();
            }
        }
    }


    private void tipDeleteNode(TreeNode treeNode, List<String> otherResourceUuidList, Map<String, TreeNode> parentMap) {
        treeNode.setNocheck(true);

        if (otherResourceUuidList.contains(treeNode.getId())) {
            treeNode.setChecked(true);
            setCheckForAllParentNode(treeNode, parentMap);
        }

        for (TreeNode child : treeNode.getChildren()) {
            parentMap.put(child.getId(), treeNode);
            tipDeleteNode(child, otherResourceUuidList, parentMap);
        }
    }

    private void setCheckForAllParentNode(TreeNode treeNode, Map<String, TreeNode> parentMap) {
        treeNode.setChecked(true);
        String treeNodeId = treeNode.getId();
        if (parentMap.containsKey(treeNodeId)) {
            TreeNode parentTreeNode = parentMap.get(treeNodeId);
            setCheckForAllParentNode(parentTreeNode, parentMap);
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService#getResourceTree(java.lang.String)
     */
    @Override
    public TreeNode getResourceTree(String uuid) {
        return privilegeService.getResourceTree(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService#saveBean(com.wellsoft.pt.security.audit.bean.PrivilegeBean)
     */
    @Override
    public String saveBean(PrivilegeBean bean) {
        return privilegeService.saveBean(bean);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService#publishPrivilegeUpdatedEvent(java.lang.String)
     */
    @Override
    public void publishPrivilegeUpdatedEvent(String uuid) {
        privilegeService.publishPrivilegeUpdatedEvent(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService#remove(java.lang.String)
     */
    @Override
    public void remove(String uuid) {
        privilegeService.remove(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService#removeAll(java.util.Collection)
     */
    @Override
    public void removeAll(Collection<String> uuids) {
        privilegeService.removeAll(uuids);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService#query(com.wellsoft.context.component.jqgrid.JqGridQueryInfo)
     */
    @Override
    public JqGridQueryData query(JqGridQueryInfo queryInfo) {
        return privilegeService.query(queryInfo);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService#get(java.lang.String)
     */
    @Override
    public Privilege get(String uuid) {
        return privilegeService.get(uuid);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService#getAll()
     */
    @Override
    public List<Privilege> getAll() {
        return privilegeService.getAll();
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService#getPrivilegetTree()
     */
    @Override
    public List<TreeNode> getPrivilegetTree() {
        return privilegeService.getPrivilegetTree();
    }


    @Override
    @Transactional
    public void saveAppPageEleIds(boolean isRef, String appPiUuid, String uuid, List<String> eleIds) {

        Set<Role> roleSet = new HashSet<>();
        Set<String> eleIdSet = new HashSet<>();
        for (String eleId : eleIds) {
            //roleId
            if (eleId.startsWith(IdPrefix.ROLE.getValue())) {
                Role role1 = roleService.getRoleById(eleId);
                roleSet.add(role1);
            } else {
                //节点Id
                eleIdSet.add(eleId);
            }
        }
        Role role = this.saveAppPageDefRole(SpringSecurityUtils.getCurrentUserUnitId(), isRef, appPiUuid, uuid);
        this.saveRoleEleIds(role.getUuid(), eleIdSet);
        this.saveNestedRole(role.getUuid(), roleSet);
        this.roleService.flushSession();
        this.roleService.publishRoleUpdatedEvent(role.getUuid());
        AppCacheUtils.clear();
    }

    private String getId(boolean isRef, String appPiUuid, String uuid) {
        String id = "";
        if (isRef) {
            id = AppConstants.PAGE_DEF_PREFIX + appPiUuid + "_" + uuid;
        } else {
            id = AppConstants.PAGE_DEF_PREFIX + uuid;
        }
        return id;
    }

    private String getResourecUuid(boolean isRef, String appPiUuid, String uuid) {
        String resourceUuid = "";
        if (isRef) {
            resourceUuid = StringUtils.join(AppConstants.PAGEREF_PREFIX, appPiUuid, "_", uuid);
        } else {
            resourceUuid = StringUtils.join(AppConstants.PAGE_PREFIX, appPiUuid, "_", uuid);
        }
        return resourceUuid;
    }

    private void saveRoleEleIds(String roleUuid, Set<String> eleIds) {
        //删除节点关联角色
        multiOrgApiFacade.dealRoleRemoveEvent(roleUuid);
        if (eleIds.size() > 0) {
            String memberIds = StringUtils.join(eleIds, ";");
            //保存节点关联角色
            multiOrgApiFacade.addRoleMembers(roleUuid, memberIds);
        }
    }

    private void saveNestedRole(String roleUuid, Set<Role> roleIds) {
        //添加关联角色
        List<NestedRole> nestedRoles = nestedRoleService.getByRole(roleUuid);
        NestedRole nestedRole = null;
        if (nestedRoles.size() == 0) {
            nestedRole = new NestedRole();
            nestedRole.setRoleUuid(roleUuid);
            nestedRoleService.save(nestedRole);
        } else {
            nestedRole = nestedRoles.get(0);
        }

        for (Role nestedRoleRole : nestedRole.getRoles()) {
            if (roleIds.contains(nestedRoleRole)) {
                roleIds.remove(nestedRoleRole);
            } else {
                nestedRoleRole.getNestedRoles().remove(nestedRole);
                roleService.update(nestedRoleRole);
            }
        }
        for (Role role : roleIds) {
            role.getNestedRoles().add(nestedRole);
            roleService.update(role);
        }
    }

    private Role saveAppPageDefRole(String systemUnitId, boolean isRef, String appPiUuid, String uuid) {
        String id = getId(isRef, appPiUuid, uuid);
        String defName = id + "_工作台(系统默认)";
        Role role = new Role();
        role.setSystemDef(1);
        role.setId(id);
        role.setCode(id);
        List<Role> roles = roleService.listByEntity(role);
        if (roles.size() == 0) {
            role.setName(defName);
            role.setSystemUnitId(systemUnitId);
            roleService.save(role);

        } else {
            role = roles.get(0);
        }
        Privilege privilege = new Privilege();
        privilege.setSystemDef(1);
        privilege.setCode(id);
        List<Privilege> privileges = privilegeService.listByEntity(privilege);
        if (privileges.size() == 0) {
            privilege.setName(defName);
            privilege.setSystemUnitId(systemUnitId);
            privilegeService.save(privilege);
        } else {
            privilege = privileges.get(0);
        }
        if (!role.getPrivileges().contains(privilege)) {
            Set<Privilege> privilegeSet = new HashSet<>();
            privilegeSet.add(privilege);
            role.setPrivileges(privilegeSet);
            roleService.save(role);
        }
        //权限资源关联
        PrivilegeResource privilegeResource = new PrivilegeResource();
        privilegeResource.setResourceUuid(this.getResourecUuid(isRef, appPiUuid, uuid));
        privilegeResource.setPrivilegeUuid(privilege.getUuid());
        privilegeResource.setType(IexportType.AppProductIntegration);
        List<PrivilegeResource> privilegeResourceList = privilegeResourceService.listByEntity(privilegeResource);
        if (privilegeResourceList.size() == 0) {
            privilegeResourceService.save(privilegeResource);
        }
        return role;
    }

    @Override
    public Role initSaveAppPageRoleIds(String systemUnitId, boolean isRef, String appPiUuid, String uuid, Set<String> roleSet) {
        Role role = this.saveAppPageDefRole(systemUnitId, isRef, appPiUuid, uuid);
        Set<Role> setRole = new HashSet<>();
        for (String s : roleSet) {
            Role role1 = roleService.get(s.replaceFirst(IdPrefix.ROLE.getValue(), ""));
            if (role1 != null) {
                setRole.add(role1);
            }
        }
        this.saveNestedRole(role.getUuid(), setRole);
        return role;
    }


    @Override
    @Transactional
    public List<OrgNode> getEleIds(boolean isRef, String appPiUuid, String uuid) {
        String id = this.getId(isRef, appPiUuid, uuid);
        Role role = new Role();
        role.setSystemDef(1);
        role.setId(id);
        List<Role> roles = roleService.listByEntity(role);
        List<OrgNode> orgNodes = new ArrayList<>();
        if (roles.size() == 0) {
            return orgNodes;
        }
        role = roles.get(0);
        List<NestedRole> nestedRoles = nestedRoleService.getByRole(role.getUuid());
        for (NestedRole nestedRole : nestedRoles) {
            for (Role nestedRoleRole : nestedRole.getRoles()) {
                OrgNode orgNode = new OrgNode();
                orgNode.setId(nestedRoleRole.getId());
                orgNode.setName(nestedRoleRole.getName());
                orgNode.setType(IdPrefix.ROLE.getValue());
                orgNode.setIconSkin(IdPrefix.ROLE.getValue());
                orgNodes.add(orgNode);
            }
        }
        List<OrgNode> orgNodeList = multiOrgApiFacade.queryOrgNodeListByRoleUuid(role.getUuid());
        orgNodes.addAll(orgNodeList);
        return orgNodes;
    }

//    @Override
//    @Transactional
//    public void saveAppPageDef(Boolean isDefault, String appPageUuid, String appPiUuid) {
//        //权限code 角色id 都用该id
//        Role role = roleFacadeService.getDefWorkbenchRole();
//        Privilege privilege = role.getPrivileges().iterator().next();
//        String resourceUuid = StringUtils.join(AppConstants.PAGE_PREFIX, appPiUuid, "_%");
//        HashMap<String, Object> param = new HashMap<>();
//        param.put("type", IexportType.AppProductIntegration);
//        param.put("privilegeUuid", privilege.getUuid());
//        param.put("resourceUuid", resourceUuid);
//        StringBuilder hql = new StringBuilder("from PrivilegeResource where type=:type and privilegeUuid=:privilegeUuid and resourceUuid like :resourceUuid ");
//        List<PrivilegeResource> privilegeResourceList = privilegeResourceService.listByHQL(hql.toString(), param);
//        boolean updatedEventFlg = false;
//        if (isDefault == null) {
//            isDefault = false;
//        }
//        if (isDefault) {
//            //添加
//            resourceUuid = StringUtils.join(AppConstants.PAGE_PREFIX, appPiUuid, "_", appPageUuid);
//            boolean flg = true;
//            for (PrivilegeResource privilegeResource : privilegeResourceList) {
//                if (privilegeResource.getResourceUuid().equals(resourceUuid)) {
//                    flg = false;
//                } else {
//                    //删除其他
//                    privilegeResourceService.delete(privilegeResource);
//                    updatedEventFlg = true;
//                }
//            }
//            //添加
//            if (flg) {
//                PrivilegeResource privilegeResource = new PrivilegeResource();
//                privilegeResource.setPrivilegeUuid(privilege.getUuid());
//                privilegeResource.setResourceUuid(resourceUuid);
//                privilegeResource.setType(IexportType.AppProductIntegration);
//                privilegeResourceService.save(privilegeResource);
//                updatedEventFlg = true;
//            }
//        } else {
//            for (PrivilegeResource privilegeResource : privilegeResourceList) {
//                if(privilegeResource.getResourceUuid().contains(appPageUuid)){
//                    //删除
//                    privilegeResourceService.delete(privilegeResource);
//                    updatedEventFlg = true;
//                }
//            }
//        }
//        if (updatedEventFlg) {
//            privilegeService.flushSession();
//            this.publishPrivilegeUpdatedEvent(privilege.getUuid());
//        }
//    }

    @Override
    public List<PrivilegeResource> getByPrivilegeUuid(String privilegeUuid) {
        HashMap<String, Object> param = new HashMap<>();
        param.put("type", IexportType.AppProductIntegration);
        param.put("privilegeUuid", privilegeUuid);
        StringBuilder hql = new StringBuilder("from PrivilegeResource where type=:type and privilegeUuid=:privilegeUuid ");
        List<PrivilegeResource> privilegeResourceList = privilegeResourceService.listByHQL(hql.toString(), param);
        return privilegeResourceList;
    }

    @Override
    public List<Privilege> getPrivilegeByAppId(String appId) {
        Privilege privilege = new Privilege();
        privilege.setAppId(appId);
        return privilegeService.listByEntity(privilege);
    }

    @Override
    public List<String> updatePrivilege(List<UpdatePrivilegeDto> dtoList) {
        return privilegeService.updatePrivilege(dtoList);
    }

    @Override
    public Privilege getSystemDefPrivilegeByCode(String code) {
        return privilegeService.getSystemDefPrivilegeByCode(code);
    }
}
