package com.wellsoft.pt.unit.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.org.dao.UserDao;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.security.audit.constant.RoleConstants;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.bean.BusinessManage;
import com.wellsoft.pt.unit.bean.BusinessTypeBean;
import com.wellsoft.pt.unit.bean.BusinessUnitTreeBean;
import com.wellsoft.pt.unit.bean.BusinessUnitTreeRoleBean;
import com.wellsoft.pt.unit.dao.BusinessTypeDao;
import com.wellsoft.pt.unit.dao.BusinessUnitTreeDao;
import com.wellsoft.pt.unit.dao.BusinessUnitTreeRoleDao;
import com.wellsoft.pt.unit.dao.CommonUnitDao;
import com.wellsoft.pt.unit.entity.BusinessType;
import com.wellsoft.pt.unit.entity.BusinessUnitTree;
import com.wellsoft.pt.unit.entity.BusinessUnitTreeRole;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.facade.service.UnitApiFacade;
import com.wellsoft.pt.unit.service.BusinessUnitTreeRoleService;
import com.wellsoft.pt.unit.service.BusinessUnitTreeService;
import com.wellsoft.pt.unit.support.BusinessUnitRole;
import com.wellsoft.pt.unit.support.BusinessUnitRoleEvent;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: BusinessUnitTreeServiceImpl.java
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Service
@Transactional
public class BusinessUnitTreeServiceImpl extends BaseServiceImpl implements BusinessUnitTreeService {

    private static final String QUERY_BUSINESS_ROLE = "select memberId from BusinessUnitTreeRole t where t.businessRoleId = :businessRoleId and "
            + "exists(select uuid from BusinessUnitTree b where b.uuid = t.businessUnitTreeUuid and b.businessType.id = :businessTypeId and (b.unit.id = :unitId or b.unit.unitId=:unitId))";

    private static final String QUERY_BUSINESS_UNIT_TREE_ROLE = "from BusinessUnitTreeRole t where t.businessUnitTreeUuid in (:businessUnitTreeUuids) and t.memberId like :memberId";

    @Autowired
    private BusinessTypeDao businessTypeDao;

    @Autowired
    private CommonUnitDao commonUnitDao;

    @Autowired
    private BusinessUnitTreeDao businessUnitTreeDao;

    @Autowired
    private BusinessUnitTreeRoleDao businessUnitTreeRoleDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private TenantFacadeService tenantDao;

    @Autowired
    private BusinessUnitTreeRoleService businessUnitTreeRoleService;

    @Autowired
    private SecurityApiFacade securityApiFacade;
    @Autowired
    private UnitApiFacade unitApiFacade;

    @Override
    public List<BusinessTypeBean> query(QueryInfo queryInfo) {
        Map<String, Object> values = PropertyFilter.convertToMap(queryInfo.getPropertyFilters());
        // 获取当前登录用户
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();

        if (userDetails == null)
            return null;

        // 过滤数据权限
        if (!securityApiFacade.hasRole(userDetails.getUserId(), RoleConstants.ROLE_ADMIN)) {
            values.put("userId", userDetails.getUserId());
        }
        values.put("tenantId", userDetails.getTenantId());
        values.put("orderBy", queryInfo.getOrderBy());
        List<BusinessType> managerUnitList = businessTypeDao.namedQuery("businessTypeQuery", values,
                BusinessType.class, queryInfo.getPagingInfo());

        List<BusinessTypeBean> beanList = new ArrayList<BusinessTypeBean>();
        for (BusinessType type : managerUnitList) {
            BusinessTypeBean bean = new BusinessTypeBean();
            BeanUtils.copyProperties(type, bean);

            CommonUnit unit = type.getUnit();
            if (unit != null) {
                bean.setUnitId(unit.getId());
                bean.setUnitName(unit.getName());
            }
            // 单位内通讯录管理员
            bean.setUnitManagerUserName(type.getUnitManagerUserName());
            bean.setUnitManagerUserId(type.getUnitManagerUserId());

            beanList.add(bean);
        }
        return beanList;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.UnitService#getAsTree(java.lang.String)
     */
    @Override
    public List<TreeNode> getAsTree(String businessTypeUuid) {
        List<TreeNode> rootNodeList = null;
        BusinessType businessType = this.businessTypeDao.get(businessTypeUuid);
        if (businessType != null) {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("businessTypeUuid", businessType.getUuid());
            List<BusinessUnitTree> businessUnitTreeSet = this.businessUnitTreeDao.getUnitTreeRoot(paramMap);
            if (!businessUnitTreeSet.isEmpty()) {
                rootNodeList = new ArrayList<TreeNode>();
                for (BusinessUnitTree bean : businessUnitTreeSet) {
                    TreeNode rootNode = new TreeNode();
                    rootNode.setId(bean.getUuid());
                    CommonUnit commonUnit = bean.getUnit();
                    if (commonUnit != null) {
                        rootNode.setName(commonUnit.getName());
                    } else {
                        rootNode.setName(bean.getName());
                    }
                    // 是单位
                    if (commonUnit != null) {
                        rootNode.setData("1");
                    }
                    rootNodeList.add(rootNode);
                    buildTree(rootNode, bean.getChildren());
                }
            }
        }
        return rootNodeList;
    }

    @Override
    public List<TreeNode> getAsTreeBranch(String businessTypeId, String commonUnitId) {
        // 根据业务类别ID和单位ID获得节点列表
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(businessTypeId)) {
            paramMap.put("businessTypeId", businessTypeId);
        }
        if (StringUtils.isNotBlank(commonUnitId)) {
            paramMap.put("unitId", commonUnitId);
        }
        List<BusinessUnitTree> businessUnitTreeList = this.businessUnitTreeDao.getBusinessUnitTreeByParams(paramMap);
        List<TreeNode> rootNodeList = new ArrayList<TreeNode>();
        for (BusinessUnitTree bean : businessUnitTreeList) {
            TreeNode rootNode = new TreeNode();
            rootNode.setId(bean.getUuid());
            CommonUnit commonUnit = bean.getUnit();
            if (commonUnit != null) {
                rootNode.setName(commonUnit.getName());
            } else {
                rootNode.setName(bean.getName());
            }
            // 是单位
            if (commonUnit != null) {
                rootNode.setData("1");
            }
            rootNodeList.add(rootNode);
            buildTree(rootNode, bean.getChildren());
        }
        return rootNodeList;
    }

    /**
     * @param treeNode
     * @param groups
     */
    private void buildTree(TreeNode treeNode, List<BusinessUnitTree> businessUnitTrees) {
        List<TreeNode> children = new ArrayList<TreeNode>();
        for (BusinessUnitTree businessUnitTree : businessUnitTrees) {
            TreeNode rootNode = new TreeNode();
            rootNode.setId(businessUnitTree.getUuid());
            CommonUnit commonUnit = businessUnitTree.getUnit();
            if (commonUnit != null) {
                rootNode.setName(commonUnit.getName());
            } else {
                rootNode.setName(businessUnitTree.getName());
            }
            // 是单位
            if (commonUnit != null) {
                rootNode.setData("1");
            }
            children.add(rootNode);
            if (businessUnitTree.getChildren().size() > 0) {
                buildTree(rootNode, businessUnitTree.getChildren());
            }
        }
        treeNode.setChildren(children);
    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void saveBean(BusinessUnitTreeBean bean) {
        // 保存单位树信息
        BusinessUnitTree businessUnitTree = new BusinessUnitTree();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            businessUnitTree = this.businessUnitTreeDao.get(bean.getUuid());
        }
        BeanUtils.copyProperties(bean, businessUnitTree);

        if (StringUtils.isNotBlank(bean.getUnitId())) {
            // 1、保存单位信息
            CommonUnit commonUnit = unitApiFacade.getCommonUnitById(bean.getUnitId());
            businessUnitTree.setUnit(commonUnit);
            businessUnitTree.setName(null);
        } else {
            businessUnitTree.setName(bean.getUnitName());
        }

        // 2、保存父节点信息
        if (StringUtils.isNotBlank(bean.getParentUuid())) {
            BusinessUnitTree parent = this.businessUnitTreeDao.get(bean.getParentUuid());
            businessUnitTree.setParent(parent);
        }

        // 3、保存业务类别
        if (StringUtils.isNotBlank(bean.getBusinessTypeUuid())) {
            BusinessType businessType = this.businessTypeDao.get(bean.getBusinessTypeUuid());
            businessUnitTree.setBusinessType(businessType);
        }

        this.businessUnitTreeDao.save(businessUnitTree);
    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public BusinessTypeBean getBusinessTypeBean(String uuid) {
        BusinessType type = this.businessTypeDao.get(uuid);

        BusinessTypeBean bean = new BusinessTypeBean();

        bean.setBusinessTypeUuid(type.getUuid());
        bean.setUuid(type.getUuid());

        // 单位内通讯录管理员
        bean.setUnitManagerUserName(type.getUnitManagerUserName());
        bean.setUnitManagerUserId(type.getUnitManagerUserId());

        return bean;
    }

    @Override
    public BusinessUnitTreeBean getBean(String businessUnitTreeUuid) {
        BusinessUnitTreeBean bean = new BusinessUnitTreeBean();

        // 当前节点
        BusinessUnitTree businessUnitTree = this.businessUnitTreeDao.get(businessUnitTreeUuid);

        BeanUtils.copyProperties(businessUnitTree, bean);

        // 节点关联单位
        CommonUnit commonUnit = businessUnitTree.getUnit();
        if (commonUnit != null) {
            bean.setUnitId(commonUnit.getId());
            bean.setUnitName(commonUnit.getName());
        } else {
            bean.setUnitName(businessUnitTree.getName());
        }

        // 获取父节点UUID
        BusinessUnitTree parent = businessUnitTree.getParent();
        if (parent != null) {
            bean.setParentUuid(parent.getUuid());
            CommonUnit unit = parent.getUnit();
            if (unit != null) {
                bean.setParentName(unit.getName());
            } else {
                bean.setParentName(parent.getName());
            }
        }

        // 业务单位
        BusinessType businessType = businessUnitTree.getBusinessType();
        if (businessType != null) {
            bean.setBusinessTypeUuid(businessType.getUuid());
        }

        // 设置业务角色列表
        List<BusinessUnitTreeRole> roles = businessUnitTreeRoleService.getByBusinessUnitTreeUuid(businessUnitTreeUuid);
        List<BusinessUnitTreeRoleBean> roleBeans = new ArrayList<BusinessUnitTreeRoleBean>();
        for (BusinessUnitTreeRole businessUnitTreeRole : roles) {
            BusinessUnitTreeRoleBean roleBean = new BusinessUnitTreeRoleBean();
            BeanUtils.copyProperties(businessUnitTreeRole, roleBean);
            roleBeans.add(roleBean);
        }
        bean.setBusinessUnitTreeRoles(roleBeans);

        return bean;
    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void remove(String uuid) {
        this.businessUnitTreeDao.delete(uuid);
    }

    @Override
    public void saveUserIdToBusinessUnitTree(BusinessUnitTreeBean bean) {
        Collection<BusinessUnitRole> addedRoles = new ArrayList<BusinessUnitRole>();
        Collection<BusinessUnitRole> deletedRoles = new ArrayList<BusinessUnitRole>();
        BusinessUnitTree unitTree = null;
        if (StringUtils.isNotBlank(bean.getUuid())) {
            unitTree = this.businessUnitTreeDao.get(bean.getUuid());
            StringChangeFinder managerUserIdFd = new StringChangeFinder(unitTree.getBusinessManagerUserId(),
                    bean.getBusinessManagerUserId());
            StringChangeFinder senderIdFd = new StringChangeFinder(unitTree.getBusinessSenderId(),
                    bean.getBusinessSenderId());
            StringChangeFinder receiverIdFd = new StringChangeFinder(unitTree.getBusinessReceiverId(),
                    bean.getBusinessReceiverId());
            // 单位内业务负责人
            unitTree.setBusinessManagerUserId(bean.getBusinessManagerUserId());
            // 单位内业务发送人
            unitTree.setBusinessSenderId(bean.getBusinessSenderId());
            // 单位内业务接收人
            unitTree.setBusinessReceiverId(bean.getBusinessReceiverId());

            // 业务管理者变更
            if (StringUtils.isNotBlank(managerUserIdFd.getAddedString())) {
                BusinessUnitRole businessUnitRole = new BusinessUnitRole();
                businessUnitRole.setChangeType(BusinessUnitRole.CHANGE_ADDED);
                businessUnitRole.setBizTypeId(unitTree.getBusinessType().getId());
                businessUnitRole.setBizRoleId(BUSINESS_OWNER);
                businessUnitRole.setBizRoleName(BUSINESS_OWNER);
                businessUnitRole.setMemberId(managerUserIdFd.getAddedString());
                businessUnitRole.setMemberName(managerUserIdFd.getAddedString());
                businessUnitRole.setUnitId(unitTree.getUnit().getId());
                addedRoles.add(businessUnitRole);
            }
            if (StringUtils.isNotBlank(managerUserIdFd.getDeletedString())) {
                BusinessUnitRole businessUnitRole = new BusinessUnitRole();
                businessUnitRole.setChangeType(BusinessUnitRole.CHANGE_DELETED);
                businessUnitRole.setBizTypeId(unitTree.getBusinessType().getId());
                businessUnitRole.setBizRoleId(BUSINESS_OWNER);
                businessUnitRole.setBizRoleName(BUSINESS_OWNER);
                businessUnitRole.setMemberId(managerUserIdFd.getDeletedString());
                businessUnitRole.setMemberName(managerUserIdFd.getDeletedString());
                businessUnitRole.setUnitId(unitTree.getUnit().getId());
                deletedRoles.add(businessUnitRole);
            }
            // 业务发送者变更
            if (StringUtils.isNotBlank(senderIdFd.getAddedString())) {
                BusinessUnitRole businessUnitRole = new BusinessUnitRole();
                businessUnitRole.setChangeType(BusinessUnitRole.CHANGE_ADDED);
                businessUnitRole.setBizTypeId(unitTree.getBusinessType().getId());
                businessUnitRole.setBizRoleId(BUSINESS_SENDER);
                businessUnitRole.setBizRoleName(BUSINESS_SENDER);
                businessUnitRole.setMemberId(senderIdFd.getAddedString());
                businessUnitRole.setMemberName(senderIdFd.getAddedString());
                businessUnitRole.setUnitId(unitTree.getUnit().getId());
                addedRoles.add(businessUnitRole);
            }
            if (StringUtils.isNotBlank(senderIdFd.getDeletedString())) {
                BusinessUnitRole businessUnitRole = new BusinessUnitRole();
                businessUnitRole.setChangeType(BusinessUnitRole.CHANGE_DELETED);
                businessUnitRole.setBizTypeId(unitTree.getBusinessType().getId());
                businessUnitRole.setBizRoleId(BUSINESS_SENDER);
                businessUnitRole.setBizRoleName(BUSINESS_SENDER);
                businessUnitRole.setMemberId(senderIdFd.getDeletedString());
                businessUnitRole.setMemberName(senderIdFd.getDeletedString());
                businessUnitRole.setUnitId(unitTree.getUnit().getId());
                deletedRoles.add(businessUnitRole);
            }
            // 业务接收者变更
            if (StringUtils.isNotBlank(receiverIdFd.getAddedString())) {
                BusinessUnitRole businessUnitRole = new BusinessUnitRole();
                businessUnitRole.setChangeType(BusinessUnitRole.CHANGE_ADDED);
                businessUnitRole.setBizTypeId(unitTree.getBusinessType().getId());
                businessUnitRole.setBizRoleId(BUSINESS_RECIPIENT);
                businessUnitRole.setBizRoleName(BUSINESS_RECIPIENT);
                businessUnitRole.setMemberId(receiverIdFd.getAddedString());
                businessUnitRole.setMemberName(receiverIdFd.getAddedString());
                businessUnitRole.setUnitId(unitTree.getUnit().getId());
                addedRoles.add(businessUnitRole);
            }
            if (StringUtils.isNotBlank(receiverIdFd.getDeletedString())) {
                BusinessUnitRole businessUnitRole = new BusinessUnitRole();
                businessUnitRole.setChangeType(BusinessUnitRole.CHANGE_DELETED);
                businessUnitRole.setBizTypeId(unitTree.getBusinessType().getId());
                businessUnitRole.setBizRoleId(BUSINESS_RECIPIENT);
                businessUnitRole.setBizRoleName(BUSINESS_RECIPIENT);
                businessUnitRole.setMemberId(receiverIdFd.getDeletedString());
                businessUnitRole.setMemberName(receiverIdFd.getDeletedString());
                businessUnitRole.setUnitId(unitTree.getUnit().getId());
                deletedRoles.add(businessUnitRole);
            }
        }
        this.businessUnitTreeDao.save(unitTree);

        String unitId = unitTree.getUnit().getId();
        String bizTypeId = unitTree.getBusinessType().getId();

        String businessUnitTreeUuid = unitTree.getUuid();
        // 保存业务角色
        for (BusinessUnitTreeRoleBean roleBean : bean.getDeletedBusinessUnitTreeRoles()) {
            if (StringUtils.isNotBlank(roleBean.getUuid())) {
                BusinessUnitTreeRole businessUnitTreeRole = businessUnitTreeRoleService.get(roleBean.getUuid());
                if (businessUnitTreeRole != null) {
                    // 业务角色人员删除
                    BusinessUnitRole businessUnitRole = new BusinessUnitRole();
                    businessUnitRole.setChangeType(BusinessUnitRole.CHANGE_DELETED);
                    businessUnitRole.setBizTypeId(bizTypeId);
                    businessUnitRole.setBizRoleId(businessUnitTreeRole.getBusinessRoleId());
                    businessUnitRole.setBizRoleName(businessUnitTreeRole.getBusinessRoleName());
                    businessUnitRole.setMemberId(businessUnitTreeRole.getMemberId());
                    businessUnitRole.setMemberName(businessUnitTreeRole.getMemberName());
                    businessUnitRole.setUnitId(unitId);
                    deletedRoles.add(businessUnitRole);
                    businessUnitTreeRoleService.remove(businessUnitTreeRole);
                }
            }
        }
        Set<BusinessUnitTreeRoleBean> changeRoleBeans = bean.getChangedBusinessUnitTreeRoles();
        for (BusinessUnitTreeRoleBean changeRoleBean : changeRoleBeans) {
            if (StringUtils.isNotBlank(changeRoleBean.getUuid())) {
                BusinessUnitTreeRole roleModel = this.dao.get(BusinessUnitTreeRole.class, changeRoleBean.getUuid());
                String businessRoleId = roleModel.getBusinessRoleId();
                String changeBusinessRoleId = changeRoleBean.getBusinessRoleId();
                StringChangeFinder changeUserIdFd = new StringChangeFinder(roleModel.getMemberId(),
                        changeRoleBean.getMemberId());
                BeanUtils.copyProperties(changeRoleBean, roleModel);
                roleModel.setBusinessUnitTreeUuid(businessUnitTreeUuid);
                this.dao.save(roleModel);
                // 业务角色人员变更
                if (StringUtils.equals(businessRoleId, changeBusinessRoleId)) {
                    if (StringUtils.isNotBlank(changeUserIdFd.getAddedString())) {
                        BusinessUnitRole businessUnitRole1 = new BusinessUnitRole();
                        businessUnitRole1.setChangeType(BusinessUnitRole.CHANGE_ADDED);
                        businessUnitRole1.setBizTypeId(bizTypeId);
                        businessUnitRole1.setBizRoleId(businessRoleId);
                        businessUnitRole1.setBizRoleName(businessRoleId);
                        businessUnitRole1.setMemberId(changeUserIdFd.getAddedString());
                        businessUnitRole1.setMemberName(changeUserIdFd.getAddedString());
                        businessUnitRole1.setUnitId(unitId);
                        addedRoles.add(businessUnitRole1);
                    }
                    if (StringUtils.isNotBlank(changeUserIdFd.getDeletedString())) {
                        BusinessUnitRole businessUnitRole2 = new BusinessUnitRole();
                        businessUnitRole2.setChangeType(BusinessUnitRole.CHANGE_DELETED);
                        businessUnitRole2.setBizTypeId(bizTypeId);
                        businessUnitRole2.setBizRoleId(businessRoleId);
                        businessUnitRole2.setBizRoleName(businessRoleId);
                        businessUnitRole2.setMemberId(changeUserIdFd.getDeletedString());
                        businessUnitRole2.setMemberName(changeUserIdFd.getDeletedString());
                        businessUnitRole2.setUnitId(unitId);
                        deletedRoles.add(businessUnitRole2);
                    }
                } else {
                    BusinessUnitRole businessUnitRole1 = new BusinessUnitRole();
                    businessUnitRole1.setChangeType(BusinessUnitRole.CHANGE_ADDED);
                    businessUnitRole1.setBizTypeId(bizTypeId);
                    businessUnitRole1.setBizRoleId(changeBusinessRoleId);
                    businessUnitRole1.setBizRoleName(changeBusinessRoleId);
                    businessUnitRole1.setMemberId(changeRoleBean.getMemberId());
                    businessUnitRole1.setMemberName(changeRoleBean.getMemberId());
                    businessUnitRole1.setUnitId(unitId);
                    addedRoles.add(businessUnitRole1);
                    BusinessUnitRole businessUnitRole2 = new BusinessUnitRole();
                    businessUnitRole2.setChangeType(BusinessUnitRole.CHANGE_DELETED);
                    businessUnitRole2.setBizTypeId(bizTypeId);
                    businessUnitRole2.setBizRoleId(businessRoleId);
                    businessUnitRole2.setBizRoleName(businessRoleId);
                    businessUnitRole2.setMemberId(roleModel.getMemberId());
                    businessUnitRole2.setMemberName(roleModel.getMemberId());
                    businessUnitRole2.setUnitId(unitId);
                    deletedRoles.add(businessUnitRole2);
                }
            } else {
                BusinessUnitTreeRole roleModel = new BusinessUnitTreeRole();
                BeanUtils.copyProperties(changeRoleBean, roleModel);
                roleModel.setUuid(null);
                roleModel.setBusinessUnitTreeUuid(businessUnitTreeUuid);
                this.dao.save(roleModel);
                // 业务角色人员新增
                BusinessUnitRole businessUnitRole = new BusinessUnitRole();
                businessUnitRole.setChangeType(BusinessUnitRole.CHANGE_ADDED);
                businessUnitRole.setBizTypeId(bizTypeId);
                businessUnitRole.setBizRoleId(roleModel.getBusinessRoleId());
                businessUnitRole.setBizRoleName(roleModel.getBusinessRoleName());
                businessUnitRole.setMemberId(roleModel.getMemberId());
                businessUnitRole.setMemberName(roleModel.getMemberName());
                businessUnitRole.setUnitId(unitId);
                addedRoles.add(businessUnitRole);
            }
        }
        // 发布业务角色人员更改事件
        ApplicationContextHolder.getApplicationContext().publishEvent(
                new BusinessUnitRoleEvent(unitId, addedRoles, deletedRoles));
    }

    @Override
    public List<BusinessUnitTree> getBusinessUnitManagerById(String businessTypeId, String unitId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(businessTypeId)) {
            paramMap.put("businessTypeId", businessTypeId);
        }
        if (StringUtils.isNotBlank(unitId)) {
            paramMap.put("unitId", unitId);
        }
        return this.businessUnitTreeDao.getBusinessUnitTreeByParams(paramMap);
    }

    @Override
    public List<User> getUserList(String businessTypeId, String commonUnitId, String type) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(businessTypeId)) {
            paramMap.put("businessTypeId", businessTypeId);
        }
        if (StringUtils.isNotBlank(commonUnitId)) {
            paramMap.put("unitId", commonUnitId);
        }
        List<User> userList = new ArrayList<User>();
        List<BusinessUnitTree> unitTreeList = this.businessUnitTreeDao.getBusinessUnitTreeByParams(paramMap);
        if (unitTreeList != null && unitTreeList.size() > 0) {
            for (BusinessUnitTree tree : unitTreeList) {
                CommonUnit unit = tree.getUnit();
                if (unit != null) {
                    if ("1".equals(type)) {
                        Tenant tenant = tenantDao.getById(unit.getTenantId());
                        String userIds = tree.getBusinessSenderId();
                        if (StringUtils.isNotBlank(userIds)) {
                            String[] userIdList = userIds.split(";");
                            for (String userId : userIdList) {
                                try {
                                    IgnoreLoginUtils.login(tenant.getId(), tenant.getCreator());
                                    User user = this.userDao.getById(userId);
                                    if (user != null) {
                                        userList.add(user);
                                    }
                                } catch (Exception e) {
                                    logger.error(ExceptionUtils.getStackTrace(e));
                                } finally {
                                    IgnoreLoginUtils.logout();
                                }
                            }
                        }
                    } else if ("2".equals(type)) {
                        Tenant tenant = tenantDao.getById(unit.getTenantId());
                        String userIds = tree.getBusinessReceiverId();
                        if (StringUtils.isNotBlank(userIds)) {
                            String[] userIdList = userIds.split(";");
                            for (String userId : userIdList) {
                                try {
                                    IgnoreLoginUtils.login(tenant.getId(), tenant.getCreator());
                                    User user = this.userDao.getById(userId);
                                    if (user != null) {
                                        userList.add(user);
                                    }
                                } catch (Exception e) {
                                    logger.error(ExceptionUtils.getStackTrace(e));
                                } finally {
                                    IgnoreLoginUtils.logout();
                                }
                            }
                        }
                    }
                }
            }
        }
        return userList;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.service.BusinessUnitTreeService#getBusinessUnitUserIds(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<String> getBusinessUnitUserIds(String businessTypeId, String commonUnitId, int type) {
        String hql = null;
        // 发送人
        if (Integer.valueOf(1).equals(type)) {
            hql = "select businessSenderId as userId from BusinessUnitTree business_unit_tree where business_unit_tree.businessType.id = :businessTypeId "
                    + " and business_unit_tree.unit.unitId = :unitId";
        } else if (Integer.valueOf(2).equals(type)) {// 接收人
            hql = "select businessReceiverId as userId from BusinessUnitTree business_unit_tree where business_unit_tree.businessType.id = :businessTypeId "
                    + " and business_unit_tree.unit.unitId = :unitId";
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("businessTypeId", businessTypeId);
        values.put("unitId", commonUnitId);
        List<QueryItem> queryItems = this.commonUnitDao.query(hql, values, QueryItem.class);
        Set<String> userIdSet = new HashSet<String>();
        for (QueryItem queryItem : queryItems) {
            Object userId = queryItem.get("userId");
            if (userId != null && StringUtils.isNotBlank(userId.toString())) {
                String[] userIds = StringUtils.split(userId.toString(), ";");
                userIdSet.addAll(Arrays.asList(userIds));
            }
        }
        return Arrays.asList(userIdSet.toArray(new String[0]));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.service.BusinessUnitTreeService#getBusinessUnitUserIds(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<String> getBusinessUnitUserIds(String businessTypeId, String unitId, String bizRole) {
        String hql = null;
        if (BUSINESS_OWNER.equals(bizRole)) {
            hql = "select businessManagerUserId as userId from BusinessUnitTree business_unit_tree where business_unit_tree.businessType.id = :businessTypeId "
                    + " and (business_unit_tree.unit.id = :unitId or business_unit_tree.unit.unitId=:unitId)";
        } else if (BUSINESS_RECIPIENT.equals(bizRole)) {
            hql = "select businessReceiverId as userId from BusinessUnitTree business_unit_tree where business_unit_tree.businessType.id = :businessTypeId "
                    + " and (business_unit_tree.unit.id = :unitId or business_unit_tree.unit.unitId=:unitId)";
        } else if (BUSINESS_SENDER.equals(bizRole)) {
            hql = "select businessSenderId as userId from BusinessUnitTree business_unit_tree where business_unit_tree.businessType.id = :businessTypeId "
                    + " and (business_unit_tree.unit.id = :unitId or business_unit_tree.unit.unitId=:unitId)";
        }

        Set<String> userIdSet = new LinkedHashSet<String>();
        if (StringUtils.isNotBlank(hql)) {
            Map<String, Object> values = new HashMap<String, Object>();
            values.put("businessTypeId", businessTypeId);
            values.put("unitId", unitId);
            List<QueryItem> queryItems = this.commonUnitDao.query(hql, values, QueryItem.class);
            for (QueryItem queryItem : queryItems) {
                Object userId = queryItem.get("userId");
                if (userId != null && StringUtils.isNotBlank(userId.toString())) {
                    String[] userIds = StringUtils.split(userId.toString(), ";");
                    userIdSet.addAll(Arrays.asList(userIds));
                }
            }
        }

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("businessRoleId", bizRole);
        values.put("businessTypeId", businessTypeId);
        values.put("unitId", unitId);
        List<String> memberIds = this.businessUnitTreeRoleDao.find(QUERY_BUSINESS_ROLE, values);
        for (String memberId : memberIds) {
            if (StringUtils.isNotBlank(memberId)) {
                String[] userIds = StringUtils.split(memberId.toString(), ";");
                userIdSet.addAll(Arrays.asList(userIds));
            }
        }

        return Arrays.asList(userIdSet.toArray(new String[0]));
    }

    @Override
    public BusinessManage getBusinessManage(String businessTypeId, String unitId, String userId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(businessTypeId)) {
            paramMap.put("businessTypeId", businessTypeId);
        }
        if (StringUtils.isNotBlank(unitId)) {
            paramMap.put("unitId", unitId);
        }
        List<BusinessUnitTree> list = this.businessUnitTreeDao.getBusinessUnitTreeByParams(paramMap);
        if (list != null && !list.isEmpty()) {
            BusinessManage businessManage = new BusinessManage();
            BusinessType businessType = this.businessTypeDao.getById(businessTypeId);
            if (businessType != null) {
                businessManage.setBusinessTypeId(businessType.getId());
                businessManage.setBusinessTypeName(businessType.getName());
            }
            CommonUnit unit = this.commonUnitDao.getById(unitId);
            if (unit != null) {
                businessManage.setUnitId(unit.getId());
                businessManage.setUnitName(unit.getName());
            }
            if (StringUtils.isBlank(userId)) {
                return businessManage;
            }
            User user = this.userDao.getById(userId);
            if (user != null) {
                businessManage.setUserId(user.getId());
                businessManage.setUserName(user.getUserName());
            }

            // 是否是业务负责人
            boolean isBusinessManager = false;
            // 是否是业务发送人
            boolean isBusinessSender = false;
            // 是否是业务接收人
            boolean isBusinessReceiver = false;
            List<String> businessUnitTreeUuids = new ArrayList<String>();
            for (BusinessUnitTree unitTree : list) {
                businessUnitTreeUuids.add(unitTree.getUuid());
                String businessManagerUserId = unitTree.getBusinessManagerUserId();
                if (StringUtils.isNotBlank(businessManagerUserId) && businessManagerUserId.indexOf(userId) >= 0) {
                    // 业务负责人
                    isBusinessManager = true;
                }
                String businessSenderId = unitTree.getBusinessSenderId();
                if (StringUtils.isNotBlank(businessSenderId) && businessSenderId.indexOf(userId) >= 0) {
                    // 业务发送人
                    isBusinessSender = true;
                }
                String businessReceiverId = unitTree.getBusinessReceiverId();
                if (StringUtils.isNotBlank(businessReceiverId) && businessReceiverId.indexOf(userId) >= 0) {
                    // 业务发送人
                    isBusinessReceiver = true;
                }
            }
            businessManage.setBusinessManager(isBusinessManager);
            businessManage.setBusinessSender(isBusinessSender);
            businessManage.setBusinessReceiver(isBusinessReceiver);

            Map<String, Object> values = new HashMap<String, Object>();
            values.put("businessUnitTreeUuids", businessUnitTreeUuids);
            values.put("memberId", "%" + userId + "%");
            List<BusinessUnitTreeRole> roles = businessUnitTreeRoleDao.find(QUERY_BUSINESS_UNIT_TREE_ROLE, values);
            List<String> businessUnitRoles = new ArrayList<String>(0);
            for (BusinessUnitTreeRole businessUnitTreeRole : roles) {
                businessUnitRoles.add(businessUnitTreeRole.getBusinessRoleId());
            }
            businessManage.setBusinessUnitRoles(businessUnitRoles);
            return businessManage;
        }
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.service.BusinessUnitTreeService#getBusinessManage(java.lang.String, java.lang.String)
     */
    @Override
    public List<BusinessManage> getBusinessManage(String businessTypeId, String userId) {
        List<BusinessManage> businessManages = new ArrayList<BusinessManage>();
        List<BusinessUnitTree> list = this.businessUnitTreeDao.getByBusinessTypeIdAndUserId(businessTypeId, userId);
        for (BusinessUnitTree unitTree : list) {
            BusinessManage businessManage = new BusinessManage();
            // 是否是业务负责人
            boolean isBusinessManager = false;
            // 是否是业务发送人
            boolean isBusinessSender = false;
            // 是否是业务接收人
            boolean isBusinessReceiver = false;
            String businessManagerUserId = unitTree.getBusinessManagerUserId();
            if (StringUtils.isNotBlank(businessManagerUserId) && businessManagerUserId.indexOf(userId) >= 0) {
                // 业务负责人
                isBusinessManager = true;
            }
            String businessSenderId = unitTree.getBusinessSenderId();
            if (StringUtils.isNotBlank(businessSenderId) && businessSenderId.indexOf(userId) >= 0) {
                // 业务发送人
                isBusinessSender = true;
            }
            String businessReceiverId = unitTree.getBusinessReceiverId();
            if (StringUtils.isNotBlank(businessReceiverId) && businessReceiverId.indexOf(userId) >= 0) {
                // 业务发送人
                isBusinessReceiver = true;
            }
            businessManage.setBusinessManager(isBusinessManager);
            businessManage.setBusinessSender(isBusinessSender);
            businessManage.setBusinessReceiver(isBusinessReceiver);
            businessManages.add(businessManage);

            if (unitTree.getUnit() != null) {
                businessManage.setUnitId(unitTree.getUnit().getUnitId());
                businessManage.setUnitName(unitTree.getUnit().getName());
            }

            BusinessUnitTreeRole example = new BusinessUnitTreeRole();
            example.setBusinessUnitTreeUuid(unitTree.getUuid());
            List<BusinessUnitTreeRole> roles = businessUnitTreeRoleDao.findByExample(example);
            List<String> businessUnitRoles = new ArrayList<String>(0);
            for (BusinessUnitTreeRole businessUnitTreeRole : roles) {
                businessUnitRoles.add(businessUnitTreeRole.getBusinessRoleId());
            }
            businessManage.setBusinessUnitRoles(businessUnitRoles);
        }
        return businessManages;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.service.BusinessUnitTreeService#getCommonUnitsByBusinessTypeIdAndUserId(java.lang.String, java.lang.String)
     */
    @Override
    public List<CommonUnit> getCommonUnitsByBusinessTypeIdAndUserId(String businessTypeId, String userId) {
        String hql = "select business_unit_tree.unit from BusinessUnitTree business_unit_tree where business_unit_tree.businessType.id = :businessTypeId and "
                + "(business_unit_tree.businessManagerUserId like '%' || :userId || '%' or business_unit_tree.businessSenderId like '%' || :userId || '%' "
                + "or business_unit_tree.businessReceiverId like '%' || :userId || '%')";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("businessTypeId", businessTypeId);
        values.put("userId", userId);
        List<CommonUnit> units = this.businessUnitTreeDao.find(hql, values);
        return BeanUtils.convertCollection(units, CommonUnit.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.service.BusinessUnitTreeService#getLeafBusinessUnitTrees(java.lang.String, java.lang.String)
     */
    @Override
    public List<BusinessUnitTree> getLeafBusinessUnitTrees(String businessType, String commonUnitId) {
        List<BusinessUnitTree> list = new ArrayList<BusinessUnitTree>();
        List<BusinessUnitTree> parents = this.businessUnitTreeDao
                .getByBusinessTypeAndUnitId(businessType, commonUnitId);
        for (BusinessUnitTree businessUnitTree : parents) {
            traverseAndAddLeaf(list, businessUnitTree.getChildren());
        }
        return list;
    }

    /**
     * 遍历添加叶子结点
     *
     * @param list
     * @param children
     */
    private void traverseAndAddLeaf(List<BusinessUnitTree> list, List<BusinessUnitTree> children) {
        for (BusinessUnitTree businessUnitTree : children) {
            if (businessUnitTree.getChildren().size() == 0) {
                list.add(businessUnitTree);
            } else {
                traverseAndAddLeaf(list, businessUnitTree.getChildren());
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.service.BusinessUnitTreeService#getFullPath(java.lang.String)
     */
    @Override
    public String getFullPath(String uuid) {
        BusinessUnitTree businessUnitTree = this.businessUnitTreeDao.get(uuid);
        String path = businessUnitTree.getUnit().getName();
        BusinessUnitTree parent = businessUnitTree.getParent();
        while (parent != null) {
            path = parent.getUnit().getName() + "/" + path;
            parent = parent.getParent();
        }
        return path;
    }

    @Override
    public List<BusinessUnitTree> getBusinessUnitManagerById(String businessTypeId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("businessTypeId", businessTypeId);
        return this.businessUnitTreeDao.getBusinessUnitManagerByBusinessTypeId(paramMap);
    }

    private static class StringChangeFinder {
        private String string1;
        private String string2;

        public StringChangeFinder(String string1, String string2) {
            this.string1 = string1;
            this.string2 = string2;
        }

        public String getAddedString() {
            if (!hasChange()) {
                return null;
            }
            if (StringUtils.isBlank(string1)) {
                return string2;
            }
            if (StringUtils.isBlank(string2)) {
                return null;
            }

            List<String> list1 = Arrays.asList(StringUtils.split(string1, Separator.SEMICOLON.getValue()));
            List<String> list2 = Arrays.asList(StringUtils.split(string2, Separator.SEMICOLON.getValue()));
            List<String> addList = new ArrayList<String>();
            for (String string : list2) {
                if (!list1.contains(string)) {
                    addList.add(string);
                }
            }

            return StringUtils.join(addList, Separator.SEMICOLON.getValue());
        }

        public String getDeletedString() {
            if (!hasChange()) {
                return null;
            }
            if (StringUtils.isBlank(string2)) {
                return string1;
            }
            if (StringUtils.isBlank(string1)) {
                return null;
            }

            List<String> list1 = Arrays.asList(StringUtils.split(string1, Separator.SEMICOLON.getValue()));
            List<String> list2 = Arrays.asList(StringUtils.split(string2, Separator.SEMICOLON.getValue()));

            List<String> deleteList = new ArrayList<String>();
            if (list2.isEmpty()) {
                deleteList.addAll(list1);
            }
            for (String string : list1) {
                if (!list2.contains(string)) {
                    deleteList.add(string);
                }
            }

            return StringUtils.join(deleteList, Separator.SEMICOLON.getValue());
        }

        public boolean hasChange() {
            if (StringUtils.isBlank(string1) && StringUtils.isBlank(string2)) {
                return false;
            }

            return !StringUtils.equals(string1, string2);
        }
    }

}
