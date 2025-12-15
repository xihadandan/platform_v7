/*
 * @(#)2013-1-17 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.security.audit.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.component.jqgrid.JqGridQueryData;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.reflection.ConvertUtils;
import com.wellsoft.pt.app.function.AppFunctionSource;
import com.wellsoft.pt.app.function.AppFunctionSourceManager;
import com.wellsoft.pt.app.support.AppFunctionType;
import com.wellsoft.pt.basicdata.facade.service.BasicDataApiFacade;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.access.RequestSystemContextPathResolver;
import com.wellsoft.pt.security.audit.bean.PrivilegeBean;
import com.wellsoft.pt.security.audit.bean.PrivilegeDto;
import com.wellsoft.pt.security.audit.bean.ResourceDto;
import com.wellsoft.pt.security.audit.bean.RoleDto;
import com.wellsoft.pt.security.audit.dao.PrivilegeDao;
import com.wellsoft.pt.security.audit.dto.AuditDataLogDto;
import com.wellsoft.pt.security.audit.dto.UpdatePrivilegeDto;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.PrivilegeResource;
import com.wellsoft.pt.security.audit.entity.Resource;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.AuditDataFacadeService;
import com.wellsoft.pt.security.audit.service.PrivilegeResourceService;
import com.wellsoft.pt.security.audit.service.PrivilegeService;
import com.wellsoft.pt.security.audit.service.ResourceService;
import com.wellsoft.pt.security.audit.service.RoleService;
import com.wellsoft.pt.security.audit.support.ResourceDataSource;
import com.wellsoft.pt.security.audit.support.ResourceType;
import com.wellsoft.pt.security.audit.support.SecurityConfigType;
import com.wellsoft.pt.security.service.SecurityMetadataSourceService;
import com.wellsoft.pt.security.util.SecurityConfigUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PrivilegeServiceImpl extends AbstractJpaServiceImpl<Privilege, PrivilegeDao, String> implements
        PrivilegeService {
    private static final String ISPUSHROLE = "audit.isPushRole";
    private static final String NAMEPREFIX = "audit.pushRole.NamePrefix";
    private String QUERY_FOR_RESOURCE_TREE = "select resource.uuid as uuid, resource.name as name, resource.parent.uuid as parentUuid from Resource resource order by resource.code asc";
    @Autowired
    private ResourceService resourceService;

    @Autowired
    private PrivilegeResourceService privilegeResourceService;

    @Autowired
    private SecurityMetadataSourceService securityMetadataSourceService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private BasicDataApiFacade basicDataApiFacade;

    @Autowired(required = false)
    private List<ResourceDataSource> resourceDataSources;

    @Autowired
    private OrgApiFacade orgApiFacade;

    @Autowired
    private AppFunctionSourceManager appFunctionSourceManager;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.PrivilegeService#getBean(java.lang.String)
     */
    @Override
    public PrivilegeBean getBean(String uuid) {
        PrivilegeBean bean = new PrivilegeBean();
        Privilege privilege = this.dao.getOne(uuid);
        BeanUtils.copyProperties(privilege, bean);

        // 1、设置权限资源
        Set<Resource> resources = privilege.getResources();
        Set<Resource> resourceBeans = new HashSet<Resource>();
        for (Resource resource : resources) {
            Resource resourceBean = new Resource();
            resourceBean.setUuid(resource.getUuid());
            resourceBeans.add(resourceBean);
        }
        bean.setResources(resourceBeans);

        // 2、设置其他权限资源
        PrivilegeResource example = new PrivilegeResource();
        example.setPrivilegeUuid(uuid);
        List<PrivilegeResource> privilegeResources = this.privilegeResourceService.findByExample(example);
        Set<PrivilegeResource> otherResources = new HashSet<PrivilegeResource>();
        for (PrivilegeResource privilegeResource : privilegeResources) {
            otherResources.add(privilegeResource);
        }
        bean.setOtherResources(otherResources);

        return bean;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.PrivilegeService#saveBean(com.wellsoft.pt.security.audit.bean.PrivilegeBean)
     */
    @Override
    @Transactional
    public String saveBean(PrivilegeBean bean) {
        Privilege privilege = new Privilege();

        // modifiy by xujm 增加角色编号
        if (StringUtils.isBlank(bean.getCode())) {
            throw new RuntimeException("编号不能为空！");
        }
        if (StringUtils.isEmpty(bean.getCategoryName())) {
            bean.setCategoryUuid(null);
        }

        if (StringUtils.isBlank(bean.getUuid())) {
            bean.setUuid(null);
        } else {
            privilege = this.dao.getOne(bean.getUuid());
        }

        // 权限命名保证以权限二字结尾
//		if (!bean.getName().endsWith("权限")) {
//			bean.setName(bean.getName() + "权限");
//		}

        // 乐观锁判断
        if (privilege.getRecVer() != null && !privilege.getRecVer().equals(bean.getRecVer())) {
            throw new RuntimeException("数据已过时，请重新加载再更改保存!");
        }

        BeanUtils.copyProperties(bean, privilege);

        // 1、设置权限资源
        Set<Resource> resources = privilege.getResources();
        resources.clear();
        Set<Resource> newResources = bean.getResources();
        for (Resource newResource : newResources) {
            Resource resource = resourceService.get(newResource.getUuid());
            resources.add(resource);
        }

        privilege.setSystem(RequestSystemContextPathResolver.system());
        if (StringUtils.isNotBlank(privilege.getSystem())) {
            privilege.setTenant(SpringSecurityUtils.getCurrentTenantId());
        }
        this.dao.save(privilege);
        //uuid赋值
        bean.setUuid(privilege.getUuid());

        // 2、设置其他权限资源
        if (bean.getOtherResources() != null) {
            this.privilegeResourceService.deleteByPrivilegeUuid(privilege.getUuid());
            Set<PrivilegeResource> otherResources = bean.getOtherResources();
            for (PrivilegeResource otherResource : otherResources) {
                otherResource.setPrivilegeUuid(privilege.getUuid());
                this.privilegeResourceService.save(otherResource);
            }
        }

        if (StringUtils.isBlank(bean.getUuid())) {
            pushRole(privilege);
        } else {
            // 更新分类
            String categoryUuid = privilege.getCategoryUuid();
            String categoryUame = privilege.getCategoryName();
            String sourceuuid = privilege.getUuid();
            Set<Role> roles = privilege.getRoles();
            for (Role role : roles) {
                if (sourceuuid.equals(role.getSourceUuid())) {
                    role.setCategoryUuid(categoryUuid);
                    role.setCategoryName(categoryUame);
                    roleService.save(role);
                }
            }
        }

        this.dao.flushSession();
        return privilege.getUuid();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.PrivilegeService#publishPrivilegeUpdatedEvent()
     */
    @Override
    @Transactional(readOnly = true)
    public void publishPrivilegeUpdatedEvent(String uuid) {
        // 发布安全配置变更事件
        SecurityConfigUtils.publishSecurityConfigUpdatedEvent(uuid, SecurityConfigType.PRIVILEGE);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.PrivilegeService#remove(java.lang.String)
     */
    @Override
    @Transactional
    public void remove(String uuid) {
        // 权限-角色关系，权限是被控方，多对多 被控方删除(可以删除中间表记录)
        Privilege privilege = this.dao.getOne(uuid);
        Set<Role> roles = privilege.getRoles();
        Role pushRole = null;
        for (Role role : roles) {
            role.getPrivileges().remove(privilege);
            if (uuid.equals(role.getSourceUuid())) {
                pushRole = role;
            }
        }
        this.dao.delete(privilege);
        Map<String, Object> params = Maps.newHashMap();
        params.put("uuid", uuid);
        this.dao.deleteBySQL("delete from audit_role_privilege where privilege_uuid=:uuid", params);
        ApplicationContextHolder.getBean(AuditDataFacadeService.class).saveAuditDataLog(
                new AuditDataLogDto().name(privilege.getName()).operation("delete_audit_privilege").remark("删除权限")
                        .diffEntity(null, privilege));
        // 删除其他权限资源
        this.privilegeResourceService.deleteByPrivilegeUuid(uuid);

        // 删除推式生成的角色
        deletePushRole(pushRole);
    }

    /**
     * 删除推式生成的角色
     *
     * @param pushRole
     */
    private void deletePushRole(Role pushRole) {
        String ispushrole = Config.getValue(ISPUSHROLE);
        if ("true".equals(ispushrole)) {
            if (pushRole == null) {
                return;
            }
            // 如果生成的角色只有一条对应关系，则删除此角色
            Set<Privilege> privileges = pushRole.getPrivileges();
            if (privileges.size() < 1) {
                roleService.remove(pushRole.getUuid());
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.PrivilegeService#removeAll(java.util.Collection)
     */
    @Override
    @Transactional
    public void removeAll(Collection<String> uuids) {
        for (String uuid : uuids) {
            this.remove(uuid);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.PrivilegeService#query(com.wellsoft.pt.common.component.jqgrid.JqGridQueryInfo)
     */
    @Override
    public JqGridQueryData query(JqGridQueryInfo queryInfo) {
        PagingInfo page = new PagingInfo(queryInfo.getPage(), queryInfo.getRows(), true);
        List<Privilege> privileges = this.dao.listAllByOrderPage(page, null);
        List<Privilege> jqUsers = new ArrayList<Privilege>();
        for (Privilege privilege : privileges) {
            Privilege jqPrivilege = new Privilege();
            BeanUtils.copyProperties(privilege, jqPrivilege);
            jqUsers.add(jqPrivilege);
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
     * 根据权限UUID获取相应的资源树
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.PrivilegeService#getResourceTree(java.lang.String)
     */
    @Override
    public TreeNode getResourceTree(String uuid) {
        // 构建权限资源树
        List<QueryItem> allResources = resourceService.listQueryItemByHQL(QUERY_FOR_RESOURCE_TREE, null, null);
        TreeNode treeNode = new TreeNode();
        treeNode.setName("权限资源");
        treeNode.setId(TreeNode.ROOT_ID);
        treeNode.setNocheck(true);
        List<QueryItem> topResources = new ArrayList<QueryItem>();
        Map<String, List<QueryItem>> parentResourceMap = new HashMap<String, List<QueryItem>>();
        for (QueryItem queryItem : allResources) {
            String parentUuid = queryItem.getString("parentUuid");
            if (StringUtils.isNotBlank(parentUuid)) {
                if (!parentResourceMap.containsKey(parentUuid)) {
                    parentResourceMap.put(parentUuid, new ArrayList<QueryItem>());
                }
                parentResourceMap.get(parentUuid).add(queryItem);
            } else {
                topResources.add(queryItem);
            }
        }
        for (QueryItem queryItem : topResources) {
            TreeNode node = new TreeNode();
            String resourceUuid = queryItem.getString("uuid");
            String resourceName = queryItem.getString("name");
            node.setId(resourceUuid);
            node.setName(resourceName);

            // 生成子结点
            buildChildNodes(node, parentResourceMap);

            treeNode.getChildren().add(node);
        }

        // 选中资源结点
        Privilege privilege = this.dao.getOne(uuid);
        Set<Resource> ownerResources = privilege.getResources();
        Map<String, Resource> ownerResourceMap = ConvertUtils.convertElementToMap(ownerResources, "uuid");
        checkedResourceTreeNodes(treeNode, ownerResourceMap);
        return treeNode;
    }

    /**
     * 选中资源结点
     *
     * @param treeNode
     * @param ownerResources
     */
    private void checkedResourceTreeNodes(TreeNode treeNode, Map<String, Resource> ownerResourceMap) {
        if (ownerResourceMap.containsKey(treeNode.getId())) {
            treeNode.setChecked(true);
        }

        for (TreeNode child : treeNode.getChildren()) {
            checkedResourceTreeNodes(child, ownerResourceMap);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.PrivilegeService#getOtherResourceTree(java.lang.String)
     */
    @Override
    public List<TreeNode> getOtherResourceTree(String uuid) {
        return getOtherResourceTree(uuid, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.PrivilegeService#getOtherResourceTree(java.lang.String, java.lang.String)
     */
    @Override
    public List<TreeNode> getOtherResourceTree(String uuid, String appSystemUuid) {
        Set<String> resourceUuidSet = new HashSet<String>();
        if (StringUtils.isNotEmpty(uuid)) {
            PrivilegeResource example = new PrivilegeResource();
            example.setPrivilegeUuid(uuid);
            List<PrivilegeResource> ownerPrivilegeResource = this.privilegeResourceService.findByExample(example);
            for (PrivilegeResource privilegeResource : ownerPrivilegeResource) {
                resourceUuidSet.add(privilegeResource.getResourceUuid());
            }
        }

        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        if (resourceDataSources == null) {
            return treeNodes;
        }

        // 所有其他权限资源ID
        List<String> allOtherResourceIds = new ArrayList<String>();

        // 资源数据接口
        Map<String, Object> params = Maps.newHashMap();
        params.put("onlyProtected", true);
        for (ResourceDataSource resourceDataSource : resourceDataSources) {
            // 只加载产品集成树资源
            if (!StringUtils.equals(resourceDataSource.getId(), AppFunctionType.AppProductIntegration)) {
                continue;
            }
            TreeNode treeNode = new TreeNode();
            treeNode.setName(resourceDataSource.getName());
            treeNode.setId(resourceDataSource.getId());
            treeNode.setNocheck(true);
            List<TreeNode> children = null;
            if (StringUtils.isNotBlank(appSystemUuid)) {
                children = resourceDataSource.getDataByAppSystemUuid(appSystemUuid);
            } else {
                children = resourceDataSource.getData(params);
            }
            if (children != null) {
                treeNode.setChildren(children);
            }

            allOtherResourceIds.add(resourceDataSource.getId());
            treeNodes.add(treeNode);
        }

        // 选中资源结点及ID规则检测
        for (TreeNode treeNode : treeNodes) {
            checkedOtherResourceTreeNodes(resourceUuidSet, treeNode.getChildren(), allOtherResourceIds);
        }

        // 检测ID重复性
        Map<String, String> tmp = new HashMap<String, String>();
        for (String otherResourceId : allOtherResourceIds) {
            if (tmp.containsKey(otherResourceId)) {
                throw new RuntimeException("其他权限资源ID[" + otherResourceId + "]重复，无法加载其他资源树");
            }
            tmp.put(otherResourceId, otherResourceId);
        }

        return treeNodes;
    }

    /**
     * 选中资源结点
     *
     * @param resourceUuidSet
     * @param children
     */
    private void checkedOtherResourceTreeNodes(Set<String> resourceUuidSet, List<TreeNode> children,
                                               List<String> allOtherResourceIds) {
        for (TreeNode treeNode : children) {
            String resourceId = treeNode.getId();
            if (StringUtils.isBlank(resourceId)) {
                continue;
            }
            if (resourceId.startsWith(ResourceType.URL) || resourceId.startsWith(ResourceType.BUTTON)
                    || resourceId.startsWith(ResourceType.METHOD)) {
                throw new RuntimeException("其他资源类型ID不能以字符U、B或M开头");
            }
            allOtherResourceIds.add(resourceId);

            if (resourceUuidSet.contains(treeNode.getId())) {
                treeNode.setChecked(true);
            }

            // 选中资源结点
            checkedOtherResourceTreeNodes(resourceUuidSet, treeNode.getChildren(), allOtherResourceIds);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.security.audit.service.PrivilegeService#getAllOtherResourceIds()
     */
    @Override
    public List<String> getAllOtherResourceIds() {
        // 所有其他权限资源ID
        List<String> allOtherResourceIds = new ArrayList<String>();
        if (resourceDataSources == null || resourceDataSources.isEmpty()) {
            return allOtherResourceIds;
        }

        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        // 资源数据接口
        for (ResourceDataSource resourceDataSource : resourceDataSources) {
            TreeNode treeNode = new TreeNode();
            treeNode.setName(resourceDataSource.getName());
            treeNode.setId(resourceDataSource.getId());
            treeNode.setNocheck(true);
            List<TreeNode> children = resourceDataSource.getData(null);
            if (children != null) {
                treeNode.setChildren(children);
            }

            allOtherResourceIds.add(resourceDataSource.getId());
            treeNodes.add(treeNode);
        }
        // 收集其他资源ID
        for (TreeNode treeNode : treeNodes) {
            addOtherResourceIds(treeNode.getChildren(), allOtherResourceIds);
        }

        return allOtherResourceIds;
    }

    /**
     * 如何描述该方法
     *
     * @param children
     * @param allOtherResourceIds
     */
    private void addOtherResourceIds(List<TreeNode> children, List<String> allOtherResourceIds) {
        for (TreeNode treeNode : children) {
            String resourceId = treeNode.getId();
            allOtherResourceIds.add(resourceId);

            // 选中资源结点
            addOtherResourceIds(treeNode.getChildren(), allOtherResourceIds);
        }
    }

    /**
     * @param node
     * @param parentMap
     */
    private void buildChildNodes(TreeNode node, Map<String, List<QueryItem>> parentResourceMap) {
        String key = node.getId();
        List<QueryItem> queryItems = parentResourceMap.get(key);
        if (queryItems == null) {
            return;
        }

        for (QueryItem queryItem : queryItems) {
            TreeNode child = new TreeNode();
            String id = queryItem.getString("uuid");
            String name = queryItem.getString("name");

            child.setId(id);
            child.setName(name);
            node.getChildren().add(child);

            buildChildNodes(child, parentResourceMap);
        }
    }

    /**
     * 如何描述该方法
     * lmw 2015-6-8
     *
     * @return
     */
    @Override
    public List<TreeNode> getPrivilegetTree() {
        List<Privilege> list = listAll();
        List<TreeNode> rlist = new LinkedList<TreeNode>();

        TreeNode top = new TreeNode();
        for (Privilege p : list) {
            TreeNode child = new TreeNode();
            child.setId(p.getUuid());
            child.setData(p.getUuid());
            child.setNocheck(false);
            child.setName(p.getName());
            top.getChildren().add(child);
        }
        rlist.add(top);
        return rlist;
    }

    /**
     * 权限保存时推式生成角色,并建立关联
     *
     * @param privilege
     */
    private void pushRole(Privilege privilege) {
        String ispushrole = Config.getValue(ISPUSHROLE);

        String nameprefix = "";

        if (!StringUtils.isEmpty(Config.getValue(NAMEPREFIX))) {
            nameprefix = Config.getValue(NAMEPREFIX);
        }

        if ("true".equals(ispushrole)) {

            Role role = new Role();

            role.setName(nameprefix + privilege.getName().replace("权限", "") + "角色");
            role.setSourceUuid(privilege.getUuid());

            List<IdEntity> entity = new ArrayList<IdEntity>();
            String id = "";
            try {
                id = basicDataApiFacade.getSerialNumber("QXGL_JSID", entity, true, "");
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
            role.setId(id);
            role.setCode(id);
            role.setRemark(privilege.getRemark());
            role.setCategoryUuid(privilege.getCategoryUuid());
            role.setCategoryName(privilege.getCategoryName());
            roleService.save(role);

            Set<Privilege> privileges = new HashSet<Privilege>();
            privileges.add(privilege);
            role.setPrivileges(privileges);
        }
    }

    @Override
    public List<Privilege> getAll() {
        return listAll();
    }

    @Override
    public Privilege get(String uuid) {
        return dao.getOne(uuid);
    }

    @Override
    public List<Privilege> queryPrivilegeListOfUnitIdAndPTRoleList(String unitId, String... appIds) {
        // 平台单位，则返回平台的全部权限
        if (unitId.equals(MultiOrgSystemUnit.PT_ID)) {
            Privilege q = new Privilege();
            q.setSystemUnitId(unitId);
            return this.dao.listByHQL("from Privilege where systemDef=0 and systemUnitId='" + MultiOrgSystemUnit.PT_ID
                    + "' order by name ", null);
        } else {
            // 返回平台的那些不带“后台_”前缀的权限以及和单位自己的
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("unitId", unitId);
            params.put("ptId", MultiOrgSystemUnit.PT_ID);
            params.put("appIds", appIds);
            List<Privilege> objs = this.dao.listByNameHQLQuery("queryPrivilegeListOfUnitIdAndPTRoleList", params);
            return objs;
        }
    }

    @Override
    public List<PrivilegeDto> queryAppRolePrivileges(String appId) {
        List<Privilege> privileges = dao.listByFieldEqValue("appId", appId);
        List<PrivilegeDto> privilegeDtos = Lists.newArrayListWithCapacity(privileges.size());
        for (Privilege p : privileges) {
            Set<Role> roles = p.getRoles();
            PrivilegeDto dto = new PrivilegeDto();
            org.springframework.beans.BeanUtils.copyProperties(p, dto);
            privilegeDtos.add(dto);
            if (CollectionUtils.isNotEmpty(roles)) {
                for (Role r : roles) {
                    RoleDto roleDto = new RoleDto(r.getUuid(), r.getId(), r.getName(), r.getRemark(), r.getRecVer(), r.getAppId());
                    dto.getRoles().add(roleDto);
                }
            }
        }
        return privilegeDtos;
    }

    @Override
    @Transactional
    public void savePrivilegeOtherResource(String uuid, Set<PrivilegeResource> otherResources) {
        // 2、设置其他权限资源
        this.privilegeResourceService.deleteByPrivilegeUuid(uuid);
        for (PrivilegeResource otherResource : otherResources) {
            otherResource.setPrivilegeUuid(uuid);
            this.privilegeResourceService.save(otherResource);
        }
    }

    @Override
    @Transactional
    public void savePrivilegeRoles(String uuid, List<String> roleUuids) {
        // 删除权限关联的角色
        Map<String, Object> params = Maps.newHashMap();
        params.put("puuid", uuid);
        this.dao.deleteBySQL("delete from AUDIT_ROLE_PRIVILEGE where privilege_uuid=:puuid", params);
        for (String r : roleUuids) {
            StringBuilder insertSql = new StringBuilder("INSERT INTO AUDIT_ROLE_PRIVILEGE (ROLE_UUID,PRIVILEGE_UUID) VALUES ");
            insertSql.append("(").append("'" + r + "'").append(",").append("'" + uuid + "'").append(")");
            this.dao.updateBySQL(insertSql.toString(), null);
        }

    }

    @Override
    public List<ResourceDto> getPrivilegeResourceByPrivilegeUuid(String uuid) {
        Privilege privilege = getOne(uuid);
        if (privilege != null) {
            Set<Resource> resources = privilege.getResources();
            if (CollectionUtils.isNotEmpty(resources)) {
                List<ResourceDto> resourceDtos = Lists.newArrayListWithCapacity(resources.size());
                for (Resource rs : resources) {
                    ResourceDto dto = new ResourceDto(rs.getUuid(), rs.getCode(), rs.getName(), rs.getUrl(), rs.getApplyTo(), rs.getRemark());
                    dto.setModuleId(rs.getModuleId());
                    resourceDtos.add(dto);
                }
                return resourceDtos;
            }
        }
        return null;
    }

    @Override
    @Transactional
    public String savePrivilegeResource(PrivilegeBean bean) {
        boolean update = StringUtils.isNotBlank(bean.getUuid());
        if (!update) {
            Privilege privilege = new Privilege();
            privilege.setAppId(bean.getAppId());
            privilege.setCode(bean.getCode());
            privilege.setName(bean.getName());
            privilege.setEnabled(bean.getEnabled());
            privilege.setSystemDef(bean.getSystemDef());
            privilege.setSystem(bean.getSystem());
            if (StringUtils.isNotBlank(bean.getSystem())) {
                privilege.setTenant(StringUtils.defaultIfBlank(bean.getTenant(), SpringSecurityUtils.getCurrentTenantId()));
            }
            this.save(privilege);
            bean.setUuid(privilege.getUuid());
        }
        // 2、设置其他权限资源
        if (update) {
            this.privilegeResourceService.deleteByPrivilegeUuid(bean.getUuid());
        }
        for (PrivilegeResource otherResource : bean.getOtherResources()) {
            otherResource.setPrivilegeUuid(bean.getUuid());
            this.privilegeResourceService.save(otherResource);
        }
        Map<String, Object> params = Maps.newHashMap();
        params.put("puuid", bean.getUuid());
        if (update) {
            this.dao.deleteBySQL("delete from AUDIT_PRIVILEGE_RESOURCE where privilege_uuid=:puuid", params);
        }
        if (CollectionUtils.isNotEmpty(bean.getResources())) {
            for (Resource res : bean.getResources()) {
                StringBuilder insertSql = new StringBuilder("INSERT INTO AUDIT_PRIVILEGE_RESOURCE (RESOURCE_UUID,PRIVILEGE_UUID) VALUES ");
                insertSql.append("(").append("'" + res.getUuid() + "'").append(",").append("'" + bean.getUuid() + "'").append(")");
                this.dao.updateBySQL(insertSql.toString(), null);
            }
        }
        return bean.getUuid();
    }

    @Override
    public List<TreeNode> getPrivilegeOtherResourceTreeNode(String uuid) {
        Privilege privilege = getOne(uuid);
        List<TreeNode> nodes = Lists.newArrayList();
        if (privilege != null) {

            List<PrivilegeResource> resources = privilegeResourceService.getByPrivilegeUuid(uuid);
            if (CollectionUtils.isNotEmpty(resources)) {
                for (PrivilegeResource r : resources) {
                    if (StringUtils.isNotBlank(r.getType())) {
                        List<AppFunctionSource> appFunctionSources
                                = appFunctionSourceManager.getAppFunctionSourcesById(r.getResourceUuid(), r.getType());
                        if (CollectionUtils.isNotEmpty(appFunctionSources)) {
                            TreeNode n = new TreeNode();
                            n.setId(appFunctionSources.get(0).getId());
                            n.setType(r.getType());
                            n.setName(appFunctionSources.get(0).getName());
                            nodes.add(n);
                        }
                    }
                }
            }
        }
        return nodes;
    }

    @Override
    public PrivilegeDto getPrivilegeWithRoleDetails(String uuid) {
        Privilege p = getOne(uuid);
        Set<Role> roles = p.getRoles();
        PrivilegeDto dto = new PrivilegeDto();
        org.springframework.beans.BeanUtils.copyProperties(p, dto);
        if (CollectionUtils.isNotEmpty(roles)) {
            for (Role r : roles) {
                RoleDto roleDto = new RoleDto(r.getUuid(), r.getId(), r.getName(), r.getRemark(), r.getRecVer(), r.getAppId());
                dto.getRoles().add(roleDto);
            }
        }
        return dto;
    }

    @Override
    @Transactional
    public List<String> updatePrivilege(List<UpdatePrivilegeDto> dtoList) {
        List<String> uuids = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(dtoList)) {
            for (UpdatePrivilegeDto dto : dtoList) {
                Privilege privilege = new Privilege();
                AuditDataLogDto logDto = new AuditDataLogDto();
                if (dto.getPrivilege() != null) {
                    Privilege oldPrivilege = null;
                    if (StringUtils.isNotBlank(dto.getPrivilege().getUuid())) {
                        privilege = getOne(dto.getPrivilege().getUuid());
                        oldPrivilege = new Privilege();
                        org.springframework.beans.BeanUtils.copyProperties(privilege, oldPrivilege, "roles", "resources");
                        logDto.name(privilege.getName()).remark("编辑权限").operation("edit").dataUuid(oldPrivilege.getUuid()).name(privilege.getName());
                    } else {
                        logDto.name(dto.getPrivilege().getName()).remark("新建权限").operation("create");
                        privilege.setUuid(dto.getPrivilege().getUuid());
                    }
                    if (StringUtils.isNotBlank(dto.getPrivilege().getName())) {
                        privilege.setCode(dto.getPrivilege().getCode());
                        privilege.setName(dto.getPrivilege().getName());
                        privilege.setRemark(dto.getPrivilege().getRemark());
                        privilege.setSystem(dto.getPrivilege().getSystem());
                        privilege.setAppId(dto.getPrivilege().getAppId());
                        if (dto.getPrivilege().getSystemDef() != null) {
                            privilege.setSystemDef(dto.getPrivilege().getSystemDef());
                        }
                        if (StringUtils.isNotBlank(privilege.getSystem())) {
                            privilege.setTenant(StringUtils.defaultIfBlank(dto.getPrivilege().getTenant(), SpringSecurityUtils.getCurrentTenantId()));
                        }


                        this.save(privilege);
                        logDto.diffEntity(privilege, oldPrivilege);
                    }
                    uuids.add(privilege.getUuid());

                }


                if (StringUtils.isNotBlank(privilege.getUuid())) {
                    if (CollectionUtils.isNotEmpty(dto.getPrivilegeResourceAdded())) {
                        List<PrivilegeResource> saves = Lists.newArrayList();
                        for (PrivilegeResource res : dto.getPrivilegeResourceAdded()) {
                            PrivilegeResource entity = new PrivilegeResource();
                            entity.setPrivilegeUuid(privilege.getUuid());
                            entity.setType(res.getType());
                            entity.setResourceUuid(res.getResourceUuid());
                            saves.add(entity);
                            logDto.getChildren().add(new AuditDataLogDto().tableName("audit_privilege_other_resource").name(res.getResourceName()).dataUuid(res.getResourceUuid()).remark("添加权限关联资源"));
                        }
                        privilegeResourceService.saveAll(saves);

                    }
                    Map<String, Object> param = Maps.newHashMap();
                    param.put("privilegeUuid", privilege.getUuid());
                    if (CollectionUtils.isNotEmpty(dto.getPrivilegeResourceDeleted())) {
                        for (PrivilegeResource res : dto.getPrivilegeResourceDeleted()) {
                            logDto.getChildren().add(new AuditDataLogDto()
                                    .tableName("audit_privilege_other_resource").name(res.getResourceName()).dataUuid(res.getResourceUuid()).remark("删除权限关联资源"));
                            param.put("resourceUuid", res.getResourceUuid());
                            param.put("type", res.getType());
                            this.dao.deleteBySQL("delete from audit_privilege_other_resource where resource_uuid=:resourceUuid and type=:type and privilege_uuid=:privilegeUuid", param);
                        }

                    }

                    if (CollectionUtils.isNotEmpty(dto.getResourceAdded())) {
                        for (Resource res : dto.getResourceAdded()) {
                            param.put("resourceUuid", res.getUuid());
                            logDto.getChildren().add(new AuditDataLogDto().tableName("audit_privilege_resource").name(res.getName()).dataUuid(res.getUuid()).remark("添加权限关联资源"));
                            this.dao.updateBySQL("insert into audit_privilege_resource (privilege_uuid , resource_uuid ) values (:privilegeUuid,:resourceUuid) ", param);
                        }
                    }

                    if (CollectionUtils.isNotEmpty(dto.getResourceDeleted())) {
                        for (Resource res : dto.getResourceAdded()) {
                            param.put("resourceUuid", res.getUuid());
                            logDto.getChildren().add(new AuditDataLogDto().tableName("audit_privilege_resource").name(res.getName()).dataUuid(res.getUuid()).remark("删除权限关联资源"));
                            this.dao.deleteBySQL("delete from audit_privilege_resource  where privilege_uuid = :privilegeUuid and resource_uuid = :resourceUuid ", param);
                        }
                    }

                    if (CollectionUtils.isNotEmpty(dto.getRoleAdded())) {
                        for (String roleUuid : dto.getRoleAdded()) {
                            param.put("roleUuid", roleUuid);
                            Role role = roleService.get(roleUuid);
                            logDto.getChildren().add(new AuditDataLogDto().tableName("audit_role_privilege").name(role.getName()).dataUuid(role.getUuid()).remark("授权角色"));
                            this.dao.updateBySQL("insert into audit_role_privilege ( privilege_uuid,role_uuid ) values (:privilegeUuid,:roleUuid) ", param);
                        }
                    }

                    if (CollectionUtils.isNotEmpty(dto.getRoleDeleted())) {
                        for (String roleUuid : dto.getRoleDeleted()) {
                            param.put("roleUuid", roleUuid);
                            Role role = roleService.get(roleUuid);
                            logDto.getChildren().add(new AuditDataLogDto().tableName("audit_role_privilege").name(role.getName()).dataUuid(role.getUuid()).remark("回收权限"));
                            this.dao.deleteBySQL("delete from audit_role_privilege  where privilege_uuid = :privilegeUuid and role_uuid = :roleUuid ", param);
                        }
                    }
                }

                ApplicationContextHolder.getBean(AuditDataFacadeService.class).saveAuditDataLog(logDto);
            }
        }

        return uuids;
    }

    @Override
    public List<PrivilegeDto> getPrivilegeInTenantSystem(String system, String tenant) {
        StringBuilder sql = new StringBuilder("select * from audit_privilege  p where 1=1 ");
        Map<String, Object> params = Maps.newHashMap();
        if (StringUtils.isNotBlank("system")) {
            params.put("system", system);
            params.put("tenant", tenant);
            // 归属系统租户下创建的权限
            sql.append(" and  ( (  p.system =:system and p.tenant=:tenant ) ");
            // 当前系统的模块里创建的权限
            sql.append(" or exists (select 1\n" +
                    "          from app_module m\n" +
                    "         where m.id = p.app_id\n" +
                    "           and (exists (select 1\n" +
                    "                          from app_system_info s, app_prod_module p\n" +
                    "                         where p.module_id = m.id\n" +
                    "                           and p.prod_version_uuid = s.prod_version_uuid\n" +
                    "                           and s.system = :system\n" +
                    "                           and s.tenant = :tenant)\n" +
                    "               \n" +
                    "                or (m.system = :system and m.tenant = :tenant)\n" +
                    "               \n" +
                    "               ))");
            sql.append(" ) ");
        }
        List<Privilege> privileges = this.dao.listBySQL(sql.toString(), params);
        List<PrivilegeDto> privilegeDtos = Lists.newArrayListWithCapacity(privileges.size());
        if (CollectionUtils.isNotEmpty(privileges)) {
            for (Privilege p : privileges) {
                PrivilegeDto dto = new PrivilegeDto();
                dto.setUuid(p.getUuid());
                dto.setAppId(p.getAppId());
                dto.setCode(p.getCode());
                dto.setName(p.getName());
                dto.setRemark(p.getRemark());
                dto.setSystem(p.getSystem());
                dto.setTenant(p.getTenant());
                dto.setSystemDef(p.getSystemDef());
                privilegeDtos.add(dto);
            }
        }
        return privilegeDtos;
    }

    @Override
    public PrivilegeBean getPrivilegeBeanByCode(String code) {
        List<Privilege> privileges = this.dao.listByFieldEqValue("code", code);
        if (CollectionUtils.isNotEmpty(privileges)) {
            PrivilegeBean bean = new PrivilegeBean();
            Privilege privilege = privileges.get(0);
            bean.setUuid(privilege.getUuid());
            bean.setCode(privilege.getCode());
            bean.setName(privilege.getName());
            bean.setTenant(privilege.getTenant());
            bean.setSystem(privilege.getSystem());
            bean.setAppId(privilege.getAppId());
            return bean;
        }
        return null;
    }

    @Override
    public Privilege getSystemDefPrivilegeByCode(String code) {
        Privilege example = new Privilege();
        example.setCode(code);
        example.setSystemDef(1);
        List<Privilege> privileges = this.dao.listByEntity(example);
        if (CollectionUtils.isNotEmpty(privileges)) {
            PrivilegeBean bean = new PrivilegeBean();
            Privilege privilege = privileges.get(0);
            bean.setUuid(privilege.getUuid());
            bean.setCode(privilege.getCode());
            bean.setName(privilege.getName());
            bean.setTenant(privilege.getTenant());
            bean.setSystem(privilege.getSystem());
            bean.setAppId(privilege.getAppId());
            return bean;
        }
        return null;
    }


}
