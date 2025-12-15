package com.wellsoft.pt.unit.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.mt.entity.Tenant;
import com.wellsoft.pt.mt.entity.TenantPinyin;
import com.wellsoft.pt.mt.facade.service.TenantFacadeService;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.DepartmentUserJob;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.service.DepartmentUserService;
import com.wellsoft.pt.unit.bean.CommonUnitTreeBean;
import com.wellsoft.pt.unit.dao.CommonDepartmentDao;
import com.wellsoft.pt.unit.dao.CommonUnitDao;
import com.wellsoft.pt.unit.dao.CommonUnitTreeDao;
import com.wellsoft.pt.unit.entity.CommonDepartment;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.entity.CommonUnitTree;
import com.wellsoft.pt.unit.entity.CommonUser;
import com.wellsoft.pt.unit.service.CommonDepartmentService;
import com.wellsoft.pt.unit.service.CommonUnitTreeService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.wellsoft.pt.org.unit.service.impl.UnitTreeServiceImpl.*;

/**
 * Description: CommonUnitTreeServiceImpl.java
 *
 * @author liuzq
 * @date 2013-11-5
 */
@Service
@Transactional
public class CommonUnitTreeServiceImpl extends BaseServiceImpl implements CommonUnitTreeService {
    public static final String NODE_DEPARTMENT = "department";
    public static final String NODE_USER = "user";
    private static final String UNIT_ID_PATTERN = "O0000000000";

    // @Autowired
    // private IdGeneratorService idGeneratorService;
    @Autowired
    private CommonUnitDao commonUnitDao;

    @Autowired
    private CommonUnitTreeDao commonUnitTreeDao;

    @Autowired
    private CommonDepartmentDao commonDepartmentDao;

    @Autowired
    private TenantFacadeService tenantDao;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private DepartmentUserService departmentUserService;
    @Autowired
    private IdGeneratorService tenantIdGeneratorService;
    @Autowired
    private CommonDepartmentService commonDepartmentService;

    @Override
    public List<TreeNode> getAsTree(String nodeUuid) {
        List<TreeNode> treeNodeList = new ArrayList<TreeNode>();
        List<CommonUnitTree> unitTreeRoots = this.commonUnitTreeDao.getCommonUnitTreeRootList();
        for (CommonUnitTree unitTree : unitTreeRoots) {
            TreeNode rootNode = new TreeNode();
            rootNode.setId(unitTree.getUuid());
            CommonUnit commonUnit = unitTree.getUnit();
            rootNode.setName(commonUnit.getName());
            // 是单位
            if (StringUtils.isNotBlank(commonUnit.getTenantId())) {
                rootNode.setData("1");
            } else {
                rootNode.setIconSkin("type");
            }
            treeNodeList.add(rootNode);
            buildTree(rootNode, unitTree.getChildren());
        }
        return treeNodeList;
    }

    public List<TreeNode> getAsTreeList(String uuid) {
        List<TreeNode> treeNodeList = new ArrayList<TreeNode>();
        List<CommonUnitTree> unitTreeRoots = this.commonUnitTreeDao.getCommonUnitTreeRootList();
        for (CommonUnitTree unitTree : unitTreeRoots) {
            TreeNode rootNode = new TreeNode();
            CommonUnit commonUnit = unitTree.getUnit();
            rootNode.setId(commonUnit.getId());
            rootNode.setName(commonUnit.getName());
            // 是单位
            if (StringUtils.isNotBlank(commonUnit.getTenantId())) {
                rootNode.setData(commonUnit.getTenantId());
            } else {
                rootNode.setIconSkin("type");
            }
            treeNodeList.add(rootNode);
            buildTreeAsAll(rootNode, unitTree.getChildren());
        }
        return treeNodeList;
    }

    /**
     * @param treeNode
     * @param groups
     */
    private void buildTreeAsAll(TreeNode treeNode, List<CommonUnitTree> unitTrees) {
        List<TreeNode> children = new ArrayList<TreeNode>();
        for (CommonUnitTree unitTree : unitTrees) {
            TreeNode rootNode = new TreeNode();

            CommonUnit commonUnit = unitTree.getUnit();
            rootNode.setName(commonUnit.getName());
            rootNode.setId(commonUnit.getId());
            // 是单位
            if (StringUtils.isNotBlank(commonUnit.getTenantId())) {
                rootNode.setData(commonUnit.getTenantId());
            } else {
                rootNode.setIconSkin("type");
            }
            children.add(rootNode);
            if (unitTree.getChildren().size() > 0) {
                buildTreeAsAll(rootNode, unitTree.getChildren());
            }
        }
        treeNode.setChildren(children);
    }

    /**
     * @param treeNode
     * @param groups
     */
    private void buildTree(TreeNode treeNode, List<CommonUnitTree> unitTrees) {
        List<TreeNode> children = new ArrayList<TreeNode>();
        for (CommonUnitTree unitTree : unitTrees) {
            TreeNode rootNode = new TreeNode();
            rootNode.setId(unitTree.getUuid());
            CommonUnit commonUnit = unitTree.getUnit();
            rootNode.setName(commonUnit.getName());
            // 是单位
            if (StringUtils.isNotBlank(commonUnit.getTenantId())) {
                rootNode.setData("1");
            } else {
                rootNode.setIconSkin("type");
            }
            children.add(rootNode);
            if (unitTree.getChildren().size() > 0) {
                buildTree(rootNode, unitTree.getChildren());
            }
        }
        treeNode.setChildren(children);
    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void saveBean(CommonUnitTreeBean bean) {
        // 保存单位树信息
        CommonUnitTree unitTree = new CommonUnitTree();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            unitTree = this.commonUnitTreeDao.get(bean.getUuid());
        }
        BeanUtils.copyProperties(bean, unitTree);

        // 1、保存单位信息
        CommonUnit commonUnit = new CommonUnit();
        if (StringUtils.isNotBlank(bean.getUnitUuid())) {
            commonUnit = this.commonUnitDao.get(bean.getUnitUuid());
        }
        BeanUtils.copyProperties(bean, commonUnit);
        commonUnit.setUuid(bean.getUnitUuid());
        commonUnit.setName(bean.getUnitName());
        commonUnit.setShortName(bean.getUnitShortName());
        commonUnit.setId(bean.getId());
        commonUnit.setCode(bean.getCode());
        commonUnit.setCommunityId(bean.getCommunityId());
        commonUnit.setUnitId(bean.getUnitId());
        commonUnit.setOrgCode(bean.getOrgCode());
        // 租户
        // if (StringUtils.isNotBlank(bean.getTenantUuid())) {
        // Tenant tenant = this.tenantDao.get(bean.getTenantUuid());
        // commonUnit.setTenant(tenant);
        // }
        String tenantId = commonUnit.getTenantId();
        if (StringUtils.isNotEmpty(tenantId)) {
            String unitId = tenantIdGeneratorService.generate(CommonUnit.class, UNIT_ID_PATTERN, false);
            commonUnit.setUnitId(unitId.substring(0, 1) + tenantId.substring(1, tenantId.length())
                    + unitId.substring(4, 11));
        }

        this.commonUnitDao.save(commonUnit);
        unitTree.setUnit(commonUnit);

        // 2、保存父节点信息
        if (StringUtils.isNotBlank(bean.getParentUuid())) {
            CommonUnitTree parent = this.commonUnitTreeDao.get(bean.getParentUuid());
            unitTree.setParent(parent);
        }

        // 3、保存拼音信息，用于拼音搜索
        tenantDao.deleteTenantPinyinByUuid(commonUnit.getUuid());
        Set<TenantPinyin> commonUnitPinyins = getCommonUnitPinyin(commonUnit);
        for (TenantPinyin commonUnitPinyin : commonUnitPinyins) {
            tenantDao.saveTenantPinyin(commonUnitPinyin);
        }

        this.commonUnitTreeDao.save(unitTree);
    }

    /**
     * 如何描述该方法
     *
     * @param commonUnit
     * @return
     */
    private Set<TenantPinyin> getCommonUnitPinyin(CommonUnit commonUnit) {
        Set<String> pinyins = new HashSet<String>();
        String unitUuid = commonUnit.getUuid();
        String commonName = commonUnit.getName();
        pinyins.add(PinyinUtil.getPinYin(commonName));
        pinyins.add(PinyinUtil.getPinYinHeadChar(commonName));

        Set<TenantPinyin> userPinyins = new HashSet<TenantPinyin>();
        for (String pinyin : pinyins) {
            TenantPinyin userPinyin = new TenantPinyin();
            userPinyin.setType(CommonUnit.class.getSimpleName());
            userPinyin.setEntityUuid(unitUuid);
            userPinyin.setPinyin(pinyin);
            userPinyins.add(userPinyin);
        }
        return userPinyins;
    }

    @Override
    public CommonUnitTreeBean getBean(String unitTreeUuid) {
        CommonUnitTreeBean bean = new CommonUnitTreeBean();

        // 当前节点
        CommonUnitTree unitTree = this.commonUnitTreeDao.get(unitTreeUuid);

        BeanUtils.copyProperties(unitTree, bean);

        // 节点关联单位
        CommonUnit commonUnit = unitTree.getUnit();
        if (commonUnit != null) {
            bean.setUnitUuid(commonUnit.getUuid());
            bean.setUnitName(commonUnit.getName());
            bean.setUnitShortName(commonUnit.getShortName());
            bean.setUnitId(commonUnit.getUnitId());
            bean.setId(commonUnit.getId());
            bean.setEmail(commonUnit.getEmail());
            bean.setRemark(commonUnit.getRemark());
            bean.setCommunityId(commonUnit.getCommunityId());
            bean.setOrgCode(commonUnit.getOrgCode());
            // 租户
            Tenant tenant = this.tenantDao.getById(commonUnit.getTenantId());
            if (tenant != null) {
                bean.setTenantId(tenant.getId());
                bean.setTenantName(tenant.getName());
            }
        }

        // 获取父节点UUID
        CommonUnitTree parent = unitTree.getParent();
        if (parent != null) {
            bean.setParentUuid(parent.getUuid());
            CommonUnit parentUnit = parent.getUnit();
            if (parentUnit != null) {
                bean.setParentName(parentUnit.getName());
            }
        }
        return bean;
    }

    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void remove(String uuid) {
        CommonUnitTree commonUnitTree = this.commonUnitTreeDao.get(uuid);
        List<CommonDepartment> commonDepartments = commonDepartmentService.getByUnitUuid(commonUnitTree.getUnit()
                .getUuid());
        if (commonDepartments.size() > 0) {
            throw new RuntimeException("请先取消单位下的部门挂接后再删除对应的单位！");
        }
        this.commonUnitTreeDao.delete(uuid);
    }

    @Override
    public Document parseCommonUnitTree(String unitId, String optionType, String excludeTenantId) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        List<CommonUnitTree> unitTrees = new ArrayList<CommonUnitTree>();
        if (StringUtils.isNotBlank(unitId)) {
            CommonUnit commonUnit = this.commonUnitDao.getByUnitId(unitId);
            excludeTenantId = commonUnit.getTenantId();
            unitTrees.addAll(commonUnit.getUnitTrees());
        } else {
            unitTrees = this.commonUnitTreeDao.getCommonUnitTreeRootList();
        }
        excludeTenantId = excludeTenantId.replaceAll(";", "");
        for (CommonUnitTree unitTree : unitTrees) {
            if (unitTree != null) {
                buildCommonUnitTreeXML(root, unitTree, "", optionType, excludeTenantId);
            }
        }
        return document;
    }

    /**
     * @param root
     * @param units
     */
    @Transactional
    private void buildCommonUnitTreeXML(Element root, CommonUnitTree unitTree, String prefix, String optionType,
                                        String excludeTenantId) {
        Element child = root.addElement(NODE_UNIT);
        CommonUnit unit = unitTree.getUnit();
        String name = unit.getName();
        String path = prefix + name;
        String isLeaf = FALSE;
        child.addAttribute(ATTRIBUTE_ID, unit.getUnitId());

        String type = StringUtils.isNotBlank(unit.getTenantId()) ? ATTRIBUTE_TYPE_DEP : ATTRIBUTE_TYPE_CATEGORY;
        isLeaf = unitTree.getChildren().size() == 0 ? TRUE : FALSE;
        if (ID_COMMON_UNIT_USER.equals(optionType) && StringUtils.isEmpty(excludeTenantId))
            isLeaf = FALSE;
        // 如果commonType=2 即集团通讯录, 则单位下挂接部门和用户
        if (ID_COMMON_UNIT_USER.equals(optionType) && StringUtils.isNotEmpty(excludeTenantId)) {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("unitUuid", unit.getUuid());
            List<CommonDepartment> commonDepartmentList = this.commonDepartmentDao.getListByUnit(paramMap);
            if (!commonDepartmentList.isEmpty()) {
                isLeaf = FALSE;
            }
            for (CommonDepartment commonDepartment : commonDepartmentList) {
                if (StringUtils.isNotEmpty(excludeTenantId)) {
                    Department departmentTemp = departmentService.getById(commonDepartment.getId());
                    Department department = null;
                    try {
                        department = departmentService.get(departmentTemp.getUuid());
                    } catch (Exception e) {
                        logger.error(ExceptionUtils.getStackTrace(e));
                    }

                    buildCommonDepartmentTreeXML(child, department, name + "/");
                } else {
                    buildCommonDepartmentTreeXML(child, commonDepartment, name + "/");
                }
            }
        }
        setTreeElement(child, unit.getName(), isLeaf, path, type);

        for (CommonUnitTree tree : unitTree.getChildren()) {
            if (tree != null) {
                buildCommonUnitTreeXML(child, tree, name + "/", optionType, excludeTenantId);
            }
        }
    }

    @Transactional
    private void buildCommonDepartmentTreeXML(Element root, Department department, String prefix) {
        if (department == null)
            return;
        Set<DepartmentUserJob> departmentUserJobs = department.getDepartmentUsers();
        if (department.getIsVisible() != null && department.getIsVisible() && departmentUserJobs.size() > 0) {
            Element child = null;
            String name = department.getName();
            String path = prefix + name;
            String type = ATTRIBUTE_TYPE_DEP;
            String isLeaf = department.getChildren().size() == 0 && departmentUserJobs.isEmpty() ? TRUE : FALSE;
            if (department.getParent() != null) {
                // 挂接部门
                child = root.addElement(NODE_UNIT);
                child.addAttribute(ATTRIBUTE_ID, department.getId());
                setTreeElement(child, name, isLeaf, path, type);
            }
            // 挂接用户
            for (DepartmentUserJob departmentUserJob : departmentUserJobs) {
                Element childUser = null;
                if (department.getParent() != null) {
                    childUser = child.addElement(NODE_UNIT);
                } else {
                    childUser = root.addElement(NODE_UNIT);
                }
                User user = departmentUserJob.getUser();
                if (user.getEnabled() == false)
                    continue;
                name = user.getUserName();
                String userPath = department.getName() + "/" + name;
                childUser.addAttribute(ATTRIBUTE_ID, user.getId());
                childUser.addAttribute(ATTRIBUTE_SEX, user.getSex());
                type = ATTRIBUTE_TYPE_USER;
                // 如果是一级部门直接挂在单位下面
                setTreeElement(childUser, name, TRUE, userPath, type);
            }
            // for (Department childDepartment : department.getChildren()) {
            // buildCommonDepartmentTreeXML(root, childDepartment,
            // department.getName() + "/");
            // }
        } else {
            // 如果当前这个部门人员不显示，显示对应的部门
            Element childUser = root.addElement(NODE_UNIT);
            String name = department.getName();
            System.out.println(department.getName());
            String userPath = department.getName() + "/" + name;
            childUser.addAttribute(ATTRIBUTE_ID, department.getId());
            String type = ATTRIBUTE_TYPE_DEP;
            setTreeElement(childUser, name, TRUE, userPath, type);
            // 目前你挂接是单部门挂接不存在层级关系，先注释掉
            // for (Department childDepartment : department.getChildren()) {
            // buildCommonDepartmentTreeXML(childUser, childDepartment,
            // department.getName() + "/");
            // }
        }
    }

    /**
     * 第一次载入载入集团单位树和部门点击部门再加载对应租户的部门和用户
     *
     * @param root
     * @param commonDepartment
     * @param prefix
     */
    private void buildCommonDepartmentTreeXML(Element root, CommonDepartment commonDepartment, String prefix) {
        Element child = root.addElement(NODE_UNIT);
        String name = commonDepartment.getName();
        String path = prefix + name;
        String isLeaf = TRUE;
        child.addAttribute(ATTRIBUTE_ID, commonDepartment.getId());
        String type = ATTRIBUTE_TYPE_DEP;
        // 挂接部门
        setTreeElement(child, name, isLeaf, path, type);
        for (CommonDepartment childDepartment : commonDepartment.getChildren()) {
            buildCommonDepartmentTreeXML(child, childDepartment, commonDepartment.getName() + "/");
        }
    }

    /**
     * @param child
     * @param unit
     * @param isLeaf
     * @param path
     */
    private void setTreeElement(Element element, String name, String isLeaf, String path, String type) {
        // 类型，1部门，2用户
        element.addAttribute(ATTRIBUTE_TYPE, type);
        element.addAttribute(ATTRIBUTE_ISLEAF, isLeaf);
        element.addAttribute(ATTRIBUTE_NAME, name);
        element.addAttribute(ATTRIBUTE_PATH, path);
    }

    @Override
    public Document leafUnit(String unitId, String optionType, String leafType) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        CommonUnit commonUnit = this.commonUnitDao.getById(unitId);
        if (commonUnit != null) {
            if (TYPE_DEP.equals(leafType)) {
                for (CommonUnitTree unitTree : commonUnit.getUnitTrees()) {
                    if (unitTree != null) {
                        for (CommonUnitTree tree : unitTree.getChildren()) {
                            this.mergeChildUnit(root, tree);
                        }
                    }
                }
            } else if (ID_COMMON_UNIT_USER.equals(optionType) && TYPE_USER.equals(leafType)) {
                for (CommonUnitTree unitTree : commonUnit.getUnitTrees()) {
                    if (unitTree != null) {
                        for (CommonUnitTree tree : unitTree.getChildren()) {
                            this.mergeChildUnit(root, tree);
                        }
                    }
                }
                for (CommonUser commonUser : commonUnit.getUsers()) {
                    Element childUser = root.addElement(NODE_UNIT);
                    String name = commonUser.getName();
                    String userPath = name;
                    childUser.addAttribute(ATTRIBUTE_ID, commonUser.getId());
                    childUser.addAttribute(ATTRIBUTE_SEX, commonUser.getSex());
                    String type = ATTRIBUTE_TYPE_USER;
                    setTreeElement(childUser, name, TRUE, userPath, type);
                }
            }
            /**List<CommonUnitTree> unitTrees = new ArrayList<CommonUnitTree>();
             unitTrees.addAll(commonUnit.getUnitTrees());
             for (CommonUnitTree unitTree : unitTrees) {
             if (unitTree != null) {
             for (CommonUnitTree tree : unitTree.getChildren()) {
             Element child = root.addElement(NODE_UNIT);
             CommonUnit unit = tree.getUnit();
             String name = unit.getName();
             String path = name;
             String isLeaf = FALSE;
             child.addAttribute(ATTRIBUTE_ID, unit.getId());

             String type = StringUtils.isNotBlank(unit.getTenantId()) ? ATTRIBUTE_TYPE_DEP : ATTRIBUTE_TYPE_CATEGORY;
             isLeaf = tree.getChildren().size() == 0 ? TRUE : FALSE;

             setTreeElement(child, unit.getName(), isLeaf, path, type);
             }
             if (ID_COMMON_UNIT_USER.equals(optionType)) {
             //部门、人员
             Map<String, Object> paramMap = new HashMap<String, Object>();
             paramMap.put("unitUuid", commonUnit.getUuid());
             List<CommonDepartment> commonDepartmentList = this.commonDepartmentDao.getListByUnit(paramMap);
             for (CommonDepartment commonDepartment : commonDepartmentList) {
             buildCommonDepartmentTreeXML(root, commonDepartment, commonUnit.getName() + "/");
             }
             }
             }
             }*/
        } else {
            document = leafDepartment(unitId, optionType, leafType);
        }
        return document;
    }

    private void mergeChildUnit(Element root, CommonUnitTree unitTree) {
        if (unitTree != null) {
            Element child = root.addElement(NODE_UNIT);
            CommonUnit unit = unitTree.getUnit();
            String name = unit.getName();
            String path = name;
            String isLeaf = FALSE;
            child.addAttribute(ATTRIBUTE_ID, unit.getId());

            String type = StringUtils.isNotBlank(unit.getTenantId()) ? ATTRIBUTE_TYPE_DEP : ATTRIBUTE_TYPE_CATEGORY;
            isLeaf = unitTree.getChildren().size() == 0 ? TRUE : FALSE;
            // 挂接部门
            setTreeElement(child, name, isLeaf, path, type);

            for (CommonUnitTree tree : unitTree.getChildren()) {
                this.mergeChildUnit(root, tree);
            }
        }
    }

    public Document leafDepartment(String uuid, String optionType, String leafType) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        CommonDepartment department = this.commonDepartmentDao.get(uuid);

        if (TYPE_DEP.equals(leafType)) {
            for (CommonDepartment commonDepartment : department.getChildren()) {
                Element child = root.addElement(NODE_UNIT);
                String name = commonDepartment.getName();
                String path = name;
                String isLeaf = commonDepartment.getChildren().size() == 0 ? TRUE : FALSE;
                child.addAttribute(ATTRIBUTE_ID, commonDepartment.getUuid());
                String type = ATTRIBUTE_TYPE_DEP;
                // 挂接部门
                setTreeElement(child, name, isLeaf, path, type);
            }
        } else if (ID_COMMON_UNIT_USER.equals(optionType) && TYPE_USER.equals(leafType)) {
            List<User> users = this.departmentService.getAllUsersByDepartmentId(department.getId());
            for (User commonUser : users) {
                Element childUser = root.addElement(NODE_UNIT);
                String name = commonUser.getUserName();
                String userPath = name;
                childUser.addAttribute(ATTRIBUTE_ID, commonUser.getId());
                childUser.addAttribute(ATTRIBUTE_SEX, commonUser.getSex());
                String type = ATTRIBUTE_TYPE_USER;
                setTreeElement(childUser, name, TRUE, userPath, type);
            }
        }
        /**for (CommonDepartment commonDepartment : department.getChildren()) {
         String name = commonDepartment.getName();
         buildCommonDepartmentTreeXML(root, commonDepartment, name + "/");
         }
         for (CommonUser commonUser : department.getUsers()) {
         Element childUser = root.addElement(NODE_UNIT);
         String name = commonUser.getName();
         String userPath = department.getName() + "/" + name;
         childUser.addAttribute(ATTRIBUTE_ID, commonUser.getId());
         childUser.addAttribute(ATTRIBUTE_SEX, commonUser.getSex());
         String type = ATTRIBUTE_TYPE_USER;
         setTreeElement(childUser, name, TRUE, userPath, type);
         }*/
        return document;
    }

    @Override
    public boolean validateId(String uuid, String unitId) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("id", unitId);

        List<CommonUnit> list = null;

        if (StringUtils.isNotBlank(uuid)) {
            paramMap.put("uuid", uuid);
            list = this.commonUnitDao.getListByUuidId(paramMap);
            if (list != null && !list.isEmpty())
                return false;
        } else {
            CommonUnit unit = this.commonUnitDao.getById(unitId);
            if (unit != null)
                return false;
        }
        return true;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.service.CommonUnitTreeService#getCommonUnitsByParentIdAndBusinessTypeId(java.lang.String, java.lang.String)
     */
    @Override
    public List<CommonUnit> getCommonUnitsByParentIdAndBusinessTypeId(String parentUnitId, String businessTypeId) {
        String hql = "select common_unit_tree.unit from CommonUnitTree common_unit_tree where common_unit_tree.parent.unit.id = :parentUnitId "
                + " and exists (select uuid from BusinessUnitTree business_unit_tree where business_unit_tree.unit.id = common_unit_tree.unit.id "
                + " and business_unit_tree.businessType.id = :businessTypeId) "
                + " order by common_unit_tree.unit.code asc";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("parentUnitId", parentUnitId);
        values.put("businessTypeId", businessTypeId);
        List<CommonUnit> commonUnits = this.commonUnitTreeDao.find(hql, values);
        return BeanUtils.convertCollection(commonUnits, CommonUnit.class);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.unit.service.CommonUnitTreeService#getCommonUnitsByBusinessTypeId(java.lang.String)
     */
    @Override
    public List<CommonUnit> getCommonUnitsByBusinessTypeId(String businessTypeId) {
        String hql = "select common_unit_tree.unit from CommonUnitTree common_unit_tree where "
                + " exists (select uuid from BusinessUnitTree business_unit_tree where business_unit_tree.unit.id = common_unit_tree.unit.id "
                + " and business_unit_tree.businessType.id = :businessTypeId) "
                + " order by common_unit_tree.unit.code asc";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("businessTypeId", businessTypeId);
        List<CommonUnit> commonUnits = this.commonUnitTreeDao.find(hql, values);
        return BeanUtils.convertCollection(commonUnits, CommonUnit.class);
    }
}
