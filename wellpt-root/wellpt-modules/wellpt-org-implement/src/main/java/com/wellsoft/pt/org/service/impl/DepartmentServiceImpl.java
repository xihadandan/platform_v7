/*
 * @(#)2012-12-25 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.wellsoft.context.component.jqgrid.JqGridQueryInfo;
import com.wellsoft.context.component.jqgrid.JqTreeGridNode;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.support.*;
import com.wellsoft.context.util.PinyinUtil;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.basicdata.datadict.entity.DataDictionary;
import com.wellsoft.pt.basicdata.datadict.service.DataDictionaryService;
import com.wellsoft.pt.cache.config.CacheName;
import com.wellsoft.pt.common.pinyin.entity.Pinyin;
import com.wellsoft.pt.common.pinyin.service.PinyinService;
import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.org.adsycn.service.ADDeptService;
import com.wellsoft.pt.org.adsycn.service.ADGroupService;
import com.wellsoft.pt.org.adsycn.service.ADUserService;
import com.wellsoft.pt.org.adsycn.vo.ADDept;
import com.wellsoft.pt.org.adsycn.vo.ADGroup;
import com.wellsoft.pt.org.bean.DepartmentBean;
import com.wellsoft.pt.org.dao.DeptPrincipalDao;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.*;
import com.wellsoft.pt.security.audit.entity.Privilege;
import com.wellsoft.pt.security.audit.entity.Role;
import com.wellsoft.pt.security.audit.facade.service.PrivilegeFacadeService;
import com.wellsoft.pt.security.audit.facade.service.RoleFacadeService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.facade.service.SecurityApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import com.wellsoft.pt.unit.dao.CommonUnitDao;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.service.CommonDepartmentService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: DepartmentServiceImpl.java
 *
 * @author zhulh
 * @date 2012-12-23
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-12-23.1	zhulh		2012-12-23		Create
 * 2013-9-25    liuzq       2013-9-25       Update
 * </pre>
 */
@Service
@Transactional
public class DepartmentServiceImpl extends BaseServiceImpl implements DepartmentService {
    private static final String DEPARTMENT_ID_PATTERN = "D0000000000";
    private static final String QUERY_GROUP_BY_DEPID = "select g from Group g left join g.departments o where o.uuid=:uuid";
    private static final String QUERY_USER_BY_DEPID = "select u from Department o ,DepartmentUser ouj,User u where ouj.user = u and ouj.department =o and o.uuid=:uuid ";
    // @Autowired
    // private DepartmentDao departmentDao;
    private static final String QUERY_CHILDREN_BY_ID = "select o from Department o where o.parent.id = :id";
    private static final String QUERY_CHILDREN = "select o from Department o where o.parent.uuid = :uuid";
    private static final String QUERY_TOPLEVEL_DEPARTMENT = "select o from Department o where (o.parent.uuid is null or o.parent.uuid ='') and o.tenantId = :tenantId";
    private static final String QUERY_DEP_BY_USERID = "select o from Department o ,DepartmentUser ouj,User u where ouj.user = u and ouj.department =o and u.uuid=:uuid";
    private static final String GET_DEPTMENT_ID_LIKE_NAME = "select id from Department department where department.name like '%' || :name || '%' or department.shortName like '%' || :name || '%'";
    private static final String QUERY_CHILDREN_IDS_BY_ID = "select id from Department department where department.parent.id = :departmentId";
    private static final String QUERY_PARENT_DEPARTMENT = "select parent from Department d where d.uuid= :deptUuid";
    private static final String GET_UUIDS_BY_UUID = "select d.uuid from Department d where d.parent.uuid=:uuid and (d.isActive=1 or d.isActive is null)";
    private static Logger logger = Logger.getLogger(DepartmentServiceImpl.class);
    @Autowired
    private com.wellsoft.pt.common.generator.service.IdGeneratorService idGeneratorService;
    @Autowired
    private DepartmentUserJobService departmentUserJobService;
    @Autowired
    private DepartmentPrincipalService departmentPrincipalService;
    @Autowired
    private PinyinService pinyinService;
    @Autowired
    private RoleFacadeService roleService;
    @Autowired
    private DeptPrincipalDao deptPrincipalDao;
    @Autowired
    private CommonDepartmentService commonDepartmentService;
    @Autowired
    private PrivilegeFacadeService privilegeFacadeService;
    @Autowired
    private DepartmentFunctionService departmentFunctionService;
    @Autowired
    private DataDictionaryService dataDictionaryService;
    @Autowired
    private JobService jobService;
    @Autowired
    private DeptPrincipalService deptPrincipalService;
    @Autowired
    private ADDeptService adDeptService;
    @Autowired
    private ADGroupService adGroupService;
    @Autowired
    private ADUserService adUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private CommonUnitDao commonUnitDao;
    @Autowired
    private SecurityApiFacade securityApiFacade;
    @Autowired
    private DepartmentRoleService departmentRoleService;
    @Autowired
    private DepartmentPrivilegeService departmentPrivilegeService;

    /*
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.user.service.DepartmentService#get(java.lang.String)
     */
    @Override
    public Department get(String uuid) {
        return this.dao.get(Department.class, uuid);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wellsoft.pt.user.service.DepartmentService#save(com.wellsoft.pt.user
     * .entity.Department)
     */
    @Override
    public void save(Department department) {
        this.dao.save(department);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wellsoft.pt.user.service.DepartmentService#remove(java.lang.String)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void remove(String uuid) {
        Department department = this.get(uuid);
        UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
        commonDepartmentService.deleteCommonDepartment(department.getId(), userDetail.getTenantId());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.user.service.DepartmentService#getAsTree()
     */
    @Override
    public TreeNode getAsTree(String excludeUuid) {
        List<Department> departments = this.getTopLevel();
        TreeNode treeNode = new TreeNode();
        treeNode.setId(TreeNode.ROOT_ID);
        treeNode.setName("部门树");
        buildTree(treeNode, departments, excludeUuid);
        return treeNode;
    }

    /**
     * Description how to use this method
     *
     * @param treeNode
     * @param departments
     */
    private void buildTree(TreeNode treeNode, List<Department> departments, String excludeUuid) {

        List<TreeNode> children = new ArrayList<TreeNode>();
        for (Department department : departments) {
            if (department.getUuid().equals(excludeUuid)) {
                continue;
            }

            TreeNode child = new TreeNode();
            child.setId(department.getId());
            child.setName(department.getName());
            child.setData(department.getCommonUnitId());
            children.add(child);

            if (department.getChildren().size() != 0) {
                buildTree(child, department.getChildren(), excludeUuid);
            }

        }
        treeNode.setChildren(children);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wellsoft.pt.user.service.DepartmentService#getBean(java.lang.String)
     */
    @Override
    public DepartmentBean getBean(String uuid) {
        // Department department = this.get(uuid);
        DepartmentBean bean = new DepartmentBean();
        // BeanUtils.copyProperties(department, bean);
        // if (department.getParent() != null) {
        // bean.setParentUi(department.getParent().getUuid());
        // bean.setParentName(department.getParent().getName());
        // }
        return bean;
    }

    private void updateCommonDepartmentVisibleByCommonUnit(Department child, String currentId, Boolean isVisible) {
        Department parent = child.getParent();
        if (parent != null) {
            if (StringUtils.isNotBlank(parent.getCommonUnitId())) {
                commonDepartmentService.updateCommonDepartmentVisibleByCommonUnit(parent.getCommonUnitId(), currentId,
                        isVisible);
            }
            this.updateCommonDepartmentVisibleByCommonUnit(parent, currentId, isVisible);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wellsoft.pt.user.service.DepartmentService#saveBean(com.wellsoft.
     * pt.user.bean.DepartmentBean)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void saveBean(DepartmentBean bean) {
        Department department = new Department();

        String oldOuPath = getDeptOuPath(this.getById(bean.getId()));
        String oldDeptName = null;
        String pathString = null;
        if (!StringUtils.trimToEmpty(bean.getId()).equals("")) {
            oldDeptName = this.getById(bean.getId()).getName();
            pathString = this.getById(bean.getId()).getPath();
        }

        if (StringUtils.isBlank(bean.getUuid())) {
            bean.setUuid(null);
            String id = idGeneratorService.generate(Department.class, DEPARTMENT_ID_PATTERN);
            String tenantId = SpringSecurityUtils.getCurrentTenantId();
            id = id.substring(0, 1) + tenantId.substring(1, tenantId.length()) + id.substring(4, 11);
            bean.setId(id);
            bean.setTenantId(tenantId);
        } else {
            department = this.get(bean.getUuid());
        }
        String oldCommonUnitId = department.getCommonUnitId();
        BeanUtils.copyProperties(bean, department);

        // 部门级别特殊处理
        if (StringUtils.isNotBlank(department.getDepartmentLevel())) {
            String deptlevel = department.getDepartmentLevel().substring(1, department.getDepartmentLevel().length());
            department.setDepartmentLevel(deptlevel);
        }
        // 1、设置上级部门
        if (StringUtils.isNotBlank(bean.getParentId())) {
            Department parent = this.getById(bean.getParentId());
            // 判断部门是否递归相互嵌套
            checkDepartmentRecursively(department, parent);
            department.setParent(parent);
        } else {
            department.setParent(null);
        }

        // 更新部门完整路径 判断名称是否修改 没修改就不更新了 。
        setPath(department, pathString);
        department.setTenantId(SpringSecurityUtils.getCurrentTenantId());
        this.dao.save(department);
        saveDeptPrincipal(bean, department);

        // 设置部门职能
        String functionUuidString = bean.getFunctionUuids();
        if (StringUtils.isNotBlank(functionUuidString)) {
            // 删除领导
            departmentFunctionService.deleteByDepartment(department.getUuid());
            // 新的领导
            String[] functionUuids = functionUuidString.split(Separator.SEMICOLON.getValue());
            List<DepartmentFunction> newFunctions = new ArrayList<DepartmentFunction>();
            for (String functionUuid : functionUuids) {
                List<DataDictionary> functions = dataDictionaryService.findBy("uuid", functionUuid);
                DepartmentFunction jobFunction = new DepartmentFunction();
                jobFunction.setDepartment(department);
                jobFunction.setTenantId(SpringSecurityUtils.getCurrentTenantId());
                jobFunction.setFunctionUuid(functions.get(0).getUuid());
                newFunctions.add(jobFunction);
            }
            // 更新
            for (DepartmentFunction entity : newFunctions) {
                this.dao.save(entity);
            }
        } else {
            // 删除岗位对应职能
            departmentFunctionService.deleteByDepartment(department.getUuid());
        }
        // Set<Role> roles =
        // this.roleService.getRolesByDepartmentUuid(department.getUuid());
        // for (Role role : roles) {
        // this.departmentRoleService.deleteDepartmentRoleByDepartmentUuidAndRoleUuid(department.getUuid(),
        // role.getUuid());
        // }
        for (Role role : bean.getRoles()) {
            Role tmp = this.roleService.get(role.getUuid());

            if (tmp != null) {
                DepartmentRole departmentRole = new DepartmentRole();
                DepartmentRoleId departmentRoleId = new DepartmentRoleId(department.getUuid(), tmp.getUuid(),
                        SpringSecurityUtils.getCurrentTenantId());
                departmentRole.setDepartmentRoleId(departmentRoleId);
                this.departmentRoleService.save(departmentRole);
            }
        }

        // 5.2、设置部门权限信息 add by zky 20140805
        for (Privilege privilege : bean.getPrivileges()) {
            Privilege pritmp = this.privilegeFacadeService.get(privilege.getUuid());
            if (pritmp != null) {
                DepartmentPrivilege departmentPrivilege = new DepartmentPrivilege();
                DepartmentPrivilegeId departmentPrivilegeId = new DepartmentPrivilegeId();
                departmentPrivilegeId.setPrivilegeUuid(pritmp.getUuid());
                departmentPrivilegeId.setDepartmentUuid(department.getUuid());
                departmentPrivilege.setDepartmentPrivilegeId(departmentPrivilegeId);
                this.departmentPrivilegeService.save(departmentPrivilege);
            }
        }

        // 6、保存用户拼音信息，用于拼音搜索
        pinyinService.deleteByEntityUuid(department.getUuid());
        Set<Pinyin> departmentPinyins = getDepartmentPinyin(department);
        for (Pinyin departmentPinyin : departmentPinyins) {
            pinyinService.save(departmentPinyin);

        }

        // 7、更新公共库部门和人员
        // 如果都为空的话就去查找父节点
        if (StringUtils.isBlank(department.getCommonUnitId()) && StringUtils.isBlank(bean.getCommonUnitId())) {
            department.setIsVisible(true);
            this.updateCommonDepartmentVisibleByCommonUnit(department, department.getId(), bean.getIsVisible());
        }
        department.setIsVisible(true);
        UserDetails userDetail = SpringSecurityUtils.getCurrentUser();
        // commonDepartmentService.updateCommonDepartmentUserByCommonUnit(oldCommonUnitId,
        // department.getId(),
        // bean.getCommonUnitId(), bean.getIsVisible(),
        // userDetail.getTenantId());

        // 更新用户
        Department updateDeptUser = getById(department.getId());
        if (updateDeptUser != null) {
            iteratorDeptUser(updateDeptUser); // 遍历此部门下所有用户（包括子部门）
        }

        if (OrgUtil.isAdSync()) {
            // 同步部门
            synchronousADDept(department, oldOuPath, oldDeptName);

        }
    }

    // 遍历所有子部门
    public void iteratorDeptUser(Department department) {
        Set<String> setIds = new HashSet<String>();
        List<String> uIds = getAllUserIdsByDepartmentId(department.getId());
        setIds.addAll(uIds); // 把有相同的用户id去掉
        for (String id : setIds) {
            updateUserDeptById(id); // 修改部门
            updateUserJobById(id); // 修改职位
        }
    }

    public void updateUserDeptById(String id) {
        User user = userService.getById(id);
        if (user != null) {
            String userId = user.getId();
            List<QueryItem> queryItems = queryDepartmentsByUserId(userId);
            String deptPathName = new String();

            for (QueryItem queryItem : queryItems) {
                deptPathName += ";" + queryItem.get("path") + "";
            }
            deptPathName = deptPathName.replaceFirst(";", "");
            userService.updateUserDeptName(deptPathName, user.getUuid());
        }
    }

    public void updateUserJobById(String id) {
        User user = userService.getById(id);
        if (user != null) {
            Set<UserJob> userJobs = user.getUserJobs();
            String majorJobName = "";
            String otherJobName = "";
            boolean boo = false;

            Iterator<UserJob> it = userJobs.iterator();
            while (it.hasNext()) {
                UserJob userJob = it.next();
                if (userJob != null) {
                    Job job = userJob.getJob();
                    if (job != null) {
                        // 判断是否为主岗位
                        if (userJob.getIsMajor()) {
                            if (!job.getDepartmentName().equals("")) {
                                majorJobName = job.getDepartmentName() + "/" + job.getName();
                                userService.updateMajorName(majorJobName, user.getUuid());
                            }
                        } else {
                            if (!job.getDepartmentName().equals("")) {
                                otherJobName += ";" + job.getDepartmentName() + "/" + job.getName();
                                boo = true;
                            }
                        }
                    }
                }
            }
            if (boo) {
                otherJobName = otherJobName.replaceFirst(";", "");
                userService.updateOtherName(otherJobName, user.getUuid());
            }
        }
    }

    private String getDeptOuPath(Department department) {
        if (department == null) {
            return null;
        }
        // 获取父部门
        Department parentDept = getParentDepartment(department.getUuid());
        if (parentDept == null) {
            return null;
        }
        // 获取父部门的全路径（无最顶级部门）
        String sdeptPath = "";
        if (getParentDepartment(parentDept.getUuid()) != null) {
            sdeptPath = parentDept.getPath(); // 得到父部门的全路径
            sdeptPath = sdeptPath.substring(sdeptPath.indexOf("/") + 1, sdeptPath.length());
        }
        return sdeptPath;
    }

    /**
     * @param id
     * @return
     */
    // @Override
    // @Cacheable(value = CacheName.DEFAULT)
    // public Department getById(String id) {
    // Department department = this.dao.findUniqueBy(Department.class, "id",
    // id);
    // if (department == null) {
    // return null;
    // }
    // Department target = new Department();
    // BeanUtils.copyProperties(department, target);
    // return target;
    // }

    /**
     * 同步部门
     * liyb  2015.01.11
     *
     * @param department
     * @param ouOldPath
     * @param oldDeptName
     */
    protected void synchronousADDept(Department department, String ouOldPath, String oldDeptName) {
        String str = "";
        ADDept dept = new ADDept();
        dept.setName(department.getName());
        dept.setOu(department.getName());
        String ouNewPath = getDeptOuPath(department);
        if (ouNewPath == null) {
            return;
        }
        dept.setDeptPath(ouNewPath.split("/"));

        // 判断新部门的父部门是否存在，如果不存在(返回false-->return)，则不创建
        if (!adDeptService.checkIsExistByPath(ouNewPath)) {
            return;
        }
        // 判断旧部门和新部门的父部门、名字是否相等；两者相等时，就不需要改路径
        if (!StringUtils.isEmpty(ouOldPath) && !ouNewPath.equals(ouOldPath) || !StringUtils.isEmpty(oldDeptName)
                && !department.getName().equals(oldDeptName)) {

            if (!adDeptService.checkIsExistByPath(ouOldPath)) {
                return;
            }
            ADDept oldadDept = new ADDept();
            oldadDept.setDeptPath(ouOldPath.split("/"));
            oldadDept.setOu(oldDeptName);
            adDeptService.renameDn(oldadDept, dept);
            str = "update"; // 修改部门时，会执行此处
        }
        boolean isexist = adDeptService.checkIsExist(dept);
        if (!isexist) {
            adDeptService.add(dept);
        }

        String oldPath = ouOldPath + "/" + oldDeptName; // 未修改前的路径（无最顶级部门）
        String newPath = department.getPath(); // 修改后的全路径路径
        if (str.equals("update")) { // 更新部门时
            synchronousADGroup2(department, oldPath, newPath);// 修改每个部门下面的通讯组/安全组
            iteratorDeptChild(department, oldPath, newPath, "update");
            return;
        }
        synchronousADGroup1(department, oldPath, newPath);// 每个部门下面建一个通讯组/安全组
        iteratorDeptChild(department, oldPath, newPath, "save");
    }

    /**
     * 同步子部门
     *
     * @param department
     * @param oldPath
     */
    private void iteratorDeptChild(Department department, String oldPath, String newPath, String strName) {
        if (department == null) {
            return;
        }
        // 获取到本部门下的下一级的部门（即子部门）
        List<Department> departments = this.getChildrenById(department.getId());
        if (strName.equals("update")) { // 修改组
            for (Department dept : departments) {
                synchronousADGroup2(dept, oldPath, newPath);// 修改通讯组/安全组
                iteratorDeptChild(dept, oldPath, newPath, "update");
            }
        } else if (strName.equals("save")) { // 添加组
            for (Department dept : departments) {
                synchronousADGroup1(dept, oldPath, newPath);// 同步通讯组/安全组
                iteratorDeptChild(dept, oldPath, newPath, "save");
            }
        }
        return;
    }

    /**
     * 同步通讯组/安全组
     *
     * @param department
     * @param ouOldPath
     * @param oldDeptName
     */
    protected void synchronousADGroup1(Department department, String oldPath, String newPath) {
        for (int i = 0; i <= 1; i++) {
            ADGroup group = new ADGroup();
            String grouptName = department.getPath(); // 获取新部门的全路径
            int start = grouptName.indexOf("/");
            grouptName = (grouptName.substring(start + 1, grouptName.length())).replaceAll("/", "");
            if (i == 0) {
                grouptName = grouptName + "-通讯组";
                group.setCommunicationGroup(true); // true为通讯组
            } else if (i == 1) {
                grouptName = grouptName + "-安全组";
                group.setCommunicationGroup(false); // false安全组
            }
            group.setName(grouptName);
            group.setCn(grouptName);
            group.setsAMAccountName(grouptName);

            String ouPath = department.getPath(); // 获取新部门的全路径
            ouPath = ouPath.substring(ouPath.indexOf("/") + 1, ouPath.length());
            group.setDeptPath(ouPath.split("/"));

            // 判断本部门是否存在，如果不存在，则不创建组；存在，则创建组
            if (!adGroupService.checkIsExistByPath(ouPath)) {
                return;
            }
            boolean isexist = adGroupService.checkIsExist(group);
            if (!isexist) { // 添加通讯组/安全组
                adGroupService.add(group);
                addMemberToGroup(department, group); // 将本部门的人员加入此通讯组/安全组中

                // 将本部门的通讯组/安全组，加入上级的通讯组安全组中
                List<Department> parentDepts = this.getParentDeptByUuid(department.getUuid());
                for (Department parentDept : parentDepts) {
                    if (parentDept.getParent() == null) { // 最顶级无组，所以二级部门就不用把它的组加入到上一级中（）
                        continue;
                    }
                    adGroupService.addInfoToGroup(parentDept, adGroupService.getDnByAdGroup(group), i);
                }
            }
        }
    }

    /**
     * 修改通讯组/安全组的名称
     *
     * @param department
     * @param ouOldPath
     * @param oldDeptName
     */
    protected void synchronousADGroup2(Department department, String oldPath, String newPath) {
        for (int i = 0; i <= 1; i++) {
            ADGroup group = new ADGroup();
            String grouptName = department.getPath(); // 获取新部门的全路径
            int start = grouptName.indexOf("/");
            grouptName = (grouptName.substring(start + 1, grouptName.length())).replaceAll("/", "");
            if (i == 0) {
                grouptName = grouptName + "-通讯组";
                group.setCommunicationGroup(true); // true为通讯组
            } else if (i == 1) {
                grouptName = grouptName + "-安全组";
                group.setCommunicationGroup(false); // false安全组
            }
            group.setName(grouptName);
            group.setCn(grouptName);
            group.setsAMAccountName(grouptName);

            String ouPath = department.getPath(); // 获取新部门的全路径
            ouPath = ouPath.substring(ouPath.indexOf("/") + 1, ouPath.length());
            group.setDeptPath(ouPath.split("/"));
            // 判断本部门是否存在，不存在就不执行修改组
            if (!adGroupService.checkIsExistByPath(ouPath)) {
                return;
            }

            String oldAllPath = department.getPath(); // 获取新部门的全路径
            oldAllPath = oldAllPath.replace(newPath, oldPath); // 把新的路径变成旧路径

            ADGroup oldadGroup = new ADGroup();
            if (i == 0) {
                oldAllPath = (oldAllPath.replaceAll("/", "")) + "-通讯组";
            } else if (i == 1) {
                oldAllPath = (oldAllPath.replaceAll("/", "")) + "-安全组";
            }
            oldadGroup.setDeptPath(ouPath.split("/")); // 新部门的全路径（因为部门的路径先改变到AD中了）

            oldadGroup.setName(oldAllPath);
            oldadGroup.setCn(oldAllPath);
            oldadGroup.setsAMAccountName(oldAllPath);
            oldadGroup.setCommunicationGroup(i == 0); // 当被赋予i=0时，为true，i=1为false
            adGroupService.renameDn(oldadGroup, group);

            if (!department.getPath().equals(newPath)) { // 判断修改的是否是本部门，是就执行下列操作，不是就不执行
                continue;
            }
            String deptName = (department.getPath()).substring(0, department.getPath().lastIndexOf("/")); // 获取上级部门
            String oldDept = oldPath.substring(0, oldPath.lastIndexOf("/")); // 获取上级部门
            deptName = (deptName.substring(deptName.indexOf("/") + 1, deptName.length())).replaceAll("/", "");
            oldDept = oldDept.replaceAll("/", "");
            if (deptName.equals(oldDept)) { // 它的上级部门没有改变，就不执行
                continue;
            }
            int start1 = oldPath.indexOf("/"); // 无"/"时，返回-1
            if (start1 < 1) { // 因为有部门的形式：可能有-->"/部门名"（第二级部门）(二级部门不执行下面代码)
                continue;
            }
            // 删除以前所在的旧部门的组中的自己
            String parentDn = oldPath.substring(0, oldPath.lastIndexOf("/")); // 获取到父部门的路径
            String groupDn = adGroupService.getDnByAdGroup(group); // 通过修改后的组实体，获取AD中的DN
            adGroupService.removerInfoFromGroup(parentDn, groupDn, i); // 删除它

            // 将本部门的通讯组/安全组，加入更新后的上级的通讯组安全组中
            List<Department> parentDepts = this.getParentDeptByUuid(department.getUuid());
            for (Department parentDept : parentDepts) {
                if (parentDept.getParent() == null) { // 最顶级无组，所以二级部门就不用把它的组加入到上一级中（）
                    continue;
                }
                adGroupService.addInfoToGroup(parentDept, adGroupService.getDnByAdGroup(group), i);
            }
        }
    }

    /**
     * 把人员同步到通讯组/安全组中
     *
     * @param department
     * @param group
     */
    public void addMemberToGroup(Department department, ADGroup group) {
        List<User> users = getUsersByDepartmentId(department.getId());
        for (User user : users) {
            String memberDn = "";
            try {
                memberDn = adUserService.getDnByLoginName(user.getLoginName()); // 获得的是AD全路径
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
                continue;
            }
            if (memberDn != "") {
                adGroupService.addMemberToGroup(memberDn, adGroupService.getDnByAdGroup(group));
            }
        }
    }

    /**
     * AD同步删除部门
     *
     * @param department
     */
    private void deleteADDept(Department department) {
        ADDept dept = new ADDept();
        dept.setName(department.getName());
        dept.setOu(department.getName());
        String sdeptPath = department.getParent().getPath();
        String ouPath = "";

        if (department.getParent() == null) {
            return;
        }
        if (department.getParent().getParent() != null) {
            ouPath = sdeptPath.substring(sdeptPath.indexOf("/") + 1, sdeptPath.length());
        }
        dept.setDeptPath(ouPath.split("/"));
        adDeptService.delete(dept);
    }

    /**
     * 设置部门负责人新
     *
     * @param bean
     * @param department
     */
    private void saveDeptPrincipal(DepartmentBean bean, Department department) {

        // 1、设置部门负责人
        String principalIdString = bean.getPrincipalLeaderIds();
        if (StringUtils.isNotBlank(principalIdString)) {
            // 删除部门负责人
            deptPrincipalService.deletePrincipal(department.getUuid());

            // 新的部门负责人
            String[] principalIds = principalIdString.split(Separator.SEMICOLON.getValue());
            List<DeptPrincipal> newPrincipals = new ArrayList<DeptPrincipal>();
            for (String principalId : principalIds) {
                DeptPrincipal principal = new DeptPrincipal();
                principal.setDepartmentUuid(department.getUuid());
                principal.setType(DeptPrincipal.TYPE_PRINCIPAL);
                principal.setOrgId(principalId);
                principal.setTenantId(SpringSecurityUtils.getCurrentTenantId());
                newPrincipals.add(principal);
            }
            // 更新
            for (DeptPrincipal entity : newPrincipals) {
                this.dao.save(entity);
            }
        } else {
            // 删除部门负责人
            deptPrincipalService.deletePrincipal(department.getUuid());
        }

        // 2、设置部门分管领导
        String branchedIdString = bean.getBranchedLeaderIds();
        if (StringUtils.isNotBlank(branchedIdString)) {
            // 删除部门负责人
            deptPrincipalService.deleteBranched(department.getUuid());

            // 新的部门负责人
            String[] branchedIds = branchedIdString.split(Separator.SEMICOLON.getValue());
            List<DeptPrincipal> newBrancheds = new ArrayList<DeptPrincipal>();
            for (String branchedId : branchedIds) {
                DeptPrincipal principal = new DeptPrincipal();
                principal.setDepartmentUuid(department.getUuid());
                principal.setType(DeptPrincipal.TYPE_BRANCHED);
                principal.setOrgId(branchedId);
                principal.setTenantId(SpringSecurityUtils.getCurrentTenantId());
                newBrancheds.add(principal);
            }
            // 更新
            for (DeptPrincipal entity : newBrancheds) {
                this.dao.save(entity);
            }
        } else {
            // 删除部门负责人
            deptPrincipalService.deleteBranched(department.getUuid());
        }

        // 3、设置管理员
        String managerIdString = bean.getManagerIds();
        if (StringUtils.isNotBlank(managerIdString)) {
            // 删除部门负责人
            deptPrincipalService.deleteManager(department.getUuid());

            // 新的部门负责人
            String[] managerIds = managerIdString.split(Separator.SEMICOLON.getValue());
            List<DeptPrincipal> newManagers = new ArrayList<DeptPrincipal>();
            for (String managerId : managerIds) {
                DeptPrincipal principal = new DeptPrincipal();
                principal.setDepartmentUuid(department.getUuid());
                principal.setType(DeptPrincipal.TYPE_MANAGER);
                principal.setOrgId(managerId);
                principal.setTenantId(SpringSecurityUtils.getCurrentTenantId());
                newManagers.add(principal);
            }
            // 更新
            for (DeptPrincipal entity : newManagers) {
                this.dao.save(entity);
            }
        } else {
            // 删除部门负责人
            deptPrincipalService.deleteManager(department.getUuid());
        }
    }

    /**
     * 设置部门完整路径
     * 修改 yuyq	2014-12-09
     * 在setpath中多传入一个就部门路径的参数
     *
     * @param department
     */
    private void setPath(Department department, String pathsString) {
        Department parent = department.getParent();
        if (parent != null) {
            department.setPath(parent.getPath() + Separator.SLASH.getValue() + department.getName());
        } else {
            department.setPath(department.getName());
        }
        // 不需要每次更新下级路径
        if (this.getById(department.getId()) != null && StringUtils.isNotBlank(pathsString)
                && pathsString.equals(department.getPath())) {
            return;
        }
        this.dao.save(department);

        // 更新部门下职位存储的部门路径
        List<Job> jobs = this.jobService.getJobByDeptUuid(department.getUuid());
        for (Job job : jobs) {
            job.setDepartmentName(department.getPath());
            this.jobService.save(job);
        }

        // 更新子部门路径
        for (Department child : department.getChildren()) {
            setPath(child, pathsString);
        }
    }

    // /**
    // * (non-Javadoc)
    // *
    // * @see com.wellsoft.pt.org.service.DepartmentService#getTopLevel()
    // */
    // @Override
    // public List<Department> getTopLevel() {
    // return this.getTopLevel();
    // }

    // /**
    // *
    // * (non-Javadoc)
    // *
    // * @see
    // com.wellsoft.pt.org.service.DepartmentService#getFullPath(java.lang.String)
    // */
    // @Override
    // @Cacheable(value = CacheName.DEFAULT)
    // public String getFullPath(String id) {
    // Department department = this.getById(id);
    // String path = department.getName();
    // Department parent = department.getParent();
    // while (parent != null) {
    // path = parent.getName() + "/" + path;
    // parent = parent.getParent();
    // }
    // return path;
    // }

    /**
     * 获取部门的所有部门拼音实体
     *
     * @param department
     * @return
     */
    private Set<Pinyin> getDepartmentPinyin(Department department) {
        Set<String> pinyins = new HashSet<String>();
        String entityUuid = department.getUuid();
        String name = department.getName();
        String shortName = department.getShortName();
        pinyins.add(PinyinUtil.getPinYin(name));
        pinyins.add(PinyinUtil.getPinYinHeadChar(name));
        pinyins.add(PinyinUtil.getPinYin(shortName));
        pinyins.add(PinyinUtil.getPinYinHeadChar(shortName));

        Set<Pinyin> departmentPinyins = new HashSet<Pinyin>();
        for (String pinyin : pinyins) {
            Pinyin departmentPinyin = new Pinyin();
            departmentPinyin.setType(Department.class.getSimpleName());
            departmentPinyin.setEntityUuid(entityUuid);
            departmentPinyin.setPinyin(pinyin);
            departmentPinyins.add(departmentPinyin);
        }
        return departmentPinyins;
    }

    /**
     * 判断部门是否递归相互嵌套，如果checkDepartment的子结点包含parentDepartment，则为递归相互嵌套
     *
     * @param checkDepartment
     * @param parentDepartment
     */
    private void checkDepartmentRecursively(Department checkDepartment, Department parentDepartment) {
        List<Department> children = checkDepartment.getChildren();
        for (Department department : children) {
            // 部门是否递归相互嵌套，抛出异常
            if (department.getUuid().equals(parentDepartment.getUuid())) {
                throw new RuntimeException("部门不能递归相互嵌套！");
            }

            // 判断部门是否递归相互嵌套
            checkDepartmentRecursively(department, parentDepartment);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#getForPageAsTree(com.wellsoft.pt.common.component.jqgrid.JqGridQueryInfo)
     */
    @Override
    public QueryData getForPageAsTree(JqGridQueryInfo jqGridQueryInfo, QueryInfo queryInfo) {
        // 设置查询字段条件
        Map<String, Object> values = PropertyFilter.convertToMap(queryInfo.getPropertyFilters());

        // 查询父节点为null的部门
        List<Department> results = null;
        if (StringUtils.isBlank(jqGridQueryInfo.getNodeid())) {
            // String hql =
            // "from Department dept where dept.parent.uuid is null";
            // departmentDao.findPage(pageData, hql);
            if (values.isEmpty()) {
                values.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
                results = this.dao.namedQuery("topDepartmentTreeQuery", values, Department.class,
                        queryInfo.getPagingInfo());
            } else {
                values.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
                results = this.dao.namedQuery("departmentTreeQuery", values, Department.class,
                        queryInfo.getPagingInfo());
            }
        } else {
            // Department department =
            // departmentDao.getById(queryInfo.getNodeid());
            // pageData.setResult(department.getChildren());
            values.put("id", jqGridQueryInfo.getNodeid());
            values.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
            results = this.dao.namedQuery("departmentTreeByIdQuery", values, Department.class,
                    queryInfo.getPagingInfo());
        }

        // results = pageData.getResult();
        List<JqTreeGridNode> retResults = new ArrayList<JqTreeGridNode>();

        int level = jqGridQueryInfo.getN_level() == null ? 0 : jqGridQueryInfo.getN_level() + 1;
        String parentId = jqGridQueryInfo.getNodeid() == null ? "null" : jqGridQueryInfo.getNodeid();
        for (int index = 0; index < results.size(); index++) {
            Department department = results.get(index);
            JqTreeGridNode node = new JqTreeGridNode();
            node.setId(department.getId());// ID
            List<Object> cell = node.getCell();
            cell.add(department.getId());// UUID
            cell.add(department.getUuid());// UUID
            cell.add("<span id='" + department.getId() + "'>" + department.getName() + "</span>");// Name
            cell.add(department.getCode());// Code
            cell.add(department.getPrincipalLeaderNames());// PrincipalLeaderNames
            cell.add(department.getBranchedLeaderNames());// BranchedLeaderNames
            cell.add(department.getManagerNames());// ManagerNames
            // level field
            cell.add(level);
            // parent id field
            cell.add(parentId);
            // leaf field
            cell.add(department.getChildren().size() == 0);
            // expanded field 第一个节点展开
            if ("null".equals(parentId)) {
                cell.add(true);
            } else {
                cell.add(false);
            }

            retResults.add(node);
        }
        QueryData queryData = new QueryData();
        queryData.setDataList(retResults);
        queryData.setPagingInfo(queryInfo.getPagingInfo());
        return queryData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#getBeanById(java.lang.String)
     */
    @Override
    public DepartmentBean getBeanById(String id) {
        Department department = this.getById(id);
        DepartmentBean bean = new DepartmentBean();
        BeanUtils.copyProperties(department, bean);

        if (StringUtils.isNotBlank(department.getDepartmentLevel())) {
            bean.setDepartmentLevel("L" + department.getDepartmentLevel());
        }

        // 1、获取上级部门
        Department parent = department.getParent();
        if (parent != null) {
            bean.setParentId(parent.getId());
            bean.setParentName(parent.getName());
        }

        // 将部门负责人信息设置到部门bean
        // setDepartmentPrincipalToBean(department, bean);
        // 将部门负责人信息设置到部门bean
        setDeptPrincipalToBean(department, bean);

        // 获得职能线
        Set<DepartmentFunction> functions = department.getFunctions();
        StringBuilder functionUuids = new StringBuilder();
        StringBuilder functionNames = new StringBuilder();
        Iterator<DepartmentFunction> itfunction = functions.iterator();
        while (itfunction.hasNext()) {
            DepartmentFunction jobFunction = itfunction.next();
            DataDictionary dataDictionary = dataDictionaryService.get(jobFunction.getFunctionUuid());
            functionUuids.append(dataDictionary.getUuid());
            functionNames.append(dataDictionary.getName());
            if (itfunction.hasNext()) {
                functionUuids.append(Separator.SEMICOLON.getValue());
                functionNames.append(Separator.SEMICOLON.getValue());
            }
        }
        bean.setFunctionUuids(functionUuids.toString());
        bean.setFunctionNames(functionNames.toString());

        return bean;
    }

    private void setDepartmentPrincipalToBean(Department department, DepartmentBean bean) {
        // // 2、获取部门负责人
        List<DepartmentPrincipal> principals = departmentPrincipalService.getPrincipal(department.getUuid());
        StringBuilder principalIds = new StringBuilder();
        StringBuilder principalNames = new StringBuilder();
        Iterator<DepartmentPrincipal> principalIt = principals.iterator();
        while (principalIt.hasNext()) {
            DepartmentPrincipal principal = principalIt.next();
            principalIds.append(principal.getUser().getId());
            principalNames.append(principal.getUser().getUserName());
            if (principalIt.hasNext()) {
                principalIds.append(Separator.SEMICOLON.getValue());
                principalNames.append(Separator.SEMICOLON.getValue());
            }
        }
        bean.setPrincipalLeaderIds(principalIds.toString());
        bean.setPrincipalLeaderNames(principalNames.toString());

        // 3、获取部门分管领导
        List<DepartmentPrincipal> brancheds = departmentPrincipalService.getBranched(department.getUuid());
        StringBuilder branchedIds = new StringBuilder();
        StringBuilder branchedNames = new StringBuilder();
        Iterator<DepartmentPrincipal> branchedIt = brancheds.iterator();
        while (branchedIt.hasNext()) {
            DepartmentPrincipal principal = branchedIt.next();
            branchedIds.append(principal.getUser().getId());
            branchedNames.append(principal.getUser().getUserName());
            if (branchedIt.hasNext()) {
                branchedIds.append(Separator.SEMICOLON.getValue());
                branchedNames.append(Separator.SEMICOLON.getValue());
            }
        }
        bean.setBranchedLeaderIds(branchedIds.toString());
        bean.setBranchedLeaderNames(branchedNames.toString());

        // 4、获取部门管理员
        List<DepartmentPrincipal> managers = departmentPrincipalService.getManager(department.getUuid());
        StringBuilder managerIds = new StringBuilder();
        StringBuilder managerNames = new StringBuilder();
        Iterator<DepartmentPrincipal> managerIt = managers.iterator();
        while (managerIt.hasNext()) {
            DepartmentPrincipal manager = managerIt.next();
            managerIds.append(manager.getUser().getId());
            managerNames.append(manager.getUser().getUserName());
            if (managerIt.hasNext()) {
                managerIds.append(Separator.SEMICOLON.getValue());
                managerNames.append(Separator.SEMICOLON.getValue());
            }
        }
        bean.setManagerIds(managerIds.toString());
        bean.setManagerNames(managerNames.toString());
    }

    /**
     * 跟组组织id获得组织名称
     *
     * @param orgId
     * @return
     */
    private String getOrgNameById(String orgId) {
        String name = "";
        if (orgId.startsWith(IdPrefix.USER.getValue())) {// U
            User user = userService.getById(orgId);
            if (user != null) {
                name = user.getUserName();
            }
        } else if (orgId.startsWith(IdPrefix.DEPARTMENT.getValue())) {// D
            Department department = this.getById(orgId);
            if (department != null) {
                name = department.getPath();
            }
        } else if (orgId.startsWith("J")) {// J
            Job job = jobService.getById(orgId);
            if (job != null) {
                name = job.getDepartmentName() + "/" + jobService.getById(orgId).getName();
            }
        }
        return name;
    }

    /**
     * 设置部门负责人到bean(新)
     *
     * @param department
     * @param bean
     */
    private void setDeptPrincipalToBean(Department department, DepartmentBean bean) {
        // // 2、获取部门负责人
        List<DeptPrincipal> principals = deptPrincipalService.getPrincipal(department.getUuid());
        StringBuilder principalIds = new StringBuilder();
        StringBuilder principalNames = new StringBuilder();
        Iterator<DeptPrincipal> principalIt = principals.iterator();
        while (principalIt.hasNext()) {
            DeptPrincipal principal = principalIt.next();
            principalIds.append(principal.getOrgId());
            principalNames.append(getOrgNameById(principal.getOrgId()));
            if (principalIt.hasNext()) {
                principalIds.append(Separator.SEMICOLON.getValue());
                principalNames.append(Separator.SEMICOLON.getValue());
            }
        }
        bean.setPrincipalLeaderIds(principalIds.toString());
        bean.setPrincipalLeaderNames(principalNames.toString());

        // 3、获取部门分管领导
        List<DeptPrincipal> brancheds = deptPrincipalService.getBranched(department.getUuid());
        StringBuilder branchedIds = new StringBuilder();
        StringBuilder branchedNames = new StringBuilder();
        Iterator<DeptPrincipal> branchedIt = brancheds.iterator();
        while (branchedIt.hasNext()) {
            DeptPrincipal branched = branchedIt.next();
            branchedIds.append(branched.getOrgId());
            branchedNames.append(getOrgNameById(branched.getOrgId()));
            if (branchedIt.hasNext()) {
                branchedIds.append(Separator.SEMICOLON.getValue());
                branchedNames.append(Separator.SEMICOLON.getValue());
            }
        }
        bean.setBranchedLeaderIds(branchedIds.toString());
        bean.setBranchedLeaderNames(branchedNames.toString());

        // 4、获取部门管理员
        List<DeptPrincipal> managers = deptPrincipalService.getManager(department.getUuid());
        StringBuilder managerIds = new StringBuilder();
        StringBuilder managerNames = new StringBuilder();
        Iterator<DeptPrincipal> managerIt = managers.iterator();
        while (managerIt.hasNext()) {
            DeptPrincipal manager = managerIt.next();
            managerIds.append(manager.getOrgId());
            managerNames.append(getOrgNameById(manager.getOrgId()));
            if (managerIt.hasNext()) {
                managerIds.append(Separator.SEMICOLON.getValue());
                managerNames.append(Separator.SEMICOLON.getValue());
            }
        }
        bean.setManagerIds(managerIds.toString());
        bean.setManagerNames(managerNames.toString());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#removeById(java.lang.String)
     */
    @Override
    @CacheEvict(value = CacheName.DEFAULT, allEntries = true)
    public void removeById(String id) {
        Department department = this.getById(id);

        // 如果部门被用户引用，则不能删除
        checkIsUserUsed(department);
        // 如果部门被职位引用，则不能删除
        checkIsJobUsed(department);

        // 1、删除部门与用户组多对多关系中作为被控方的关系表
        // Set<Group> groups = department.getGroups();
        // for (Group group : groups) {
        // group.getDepartments().remove(department);
        // }

        if (OrgUtil.isAdSync()) {
            deleteADDept(department);
        }

        // 2、删除部门
        this.dao.delete(department);
    }

    /**
     * 校验是否被用户引用
     * 如何描述该方法
     *
     * @param department
     */
    private void checkIsUserUsed(Department department) {
        StringBuilder userNames = new StringBuilder();
        if (department != null) {
            List<String> userIds = getAllUserIdsByDepartmentId(department.getId());
            if (userIds.size() > 0) {
                Iterator<String> it = userIds.iterator();
                while (it.hasNext()) {
                    userNames.append(userService.getById(it.next()).getUserName());
                    if (it.hasNext()) {
                        userNames.append(Separator.SEMICOLON.getValue());
                    }
                }
                throw new RuntimeException("部门【" + department.getName() + "】已被用户【" + userNames + "】引用!不能删除!");
            }
        }
    }

    /**
     * 校验是否被职位引用.
     * 如何描述该方法
     *
     * @param department
     */
    private void checkIsJobUsed(Department department) {
        List<Department> children = department.getChildren();
        for (Department department2 : children) {
            List<Job> jobs = jobService.getJobByDeptUuid(department2.getUuid());
            if (jobs.size() > 0) {
                throw new RuntimeException("部门【" + department2.getName() + "】已被职位引用!不能删除!");
            }
            checkIsJobUsed(department2);
        }
    }

    // /**
    // * (non-Javadoc)
    // * @see
    // com.wellsoft.pt.org.service.DepartmentService#getDeptmentIdsLikeName(java.lang.String)
    // */
    // @Override
    // public List<String> getDeptmentIdsLikeName(String rawName) {
    // return this.departmentDao.getDeptmentIdsLikeName(rawName);
    // }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#getLeafDepartments(java.lang.String)
     */
    @Override
    public List<Department> getLeafDepartments(String id) {
        List<Department> departments = new ArrayList<Department>();
        Department department = this.getById(id);
        traverseAndAddLeaf(departments, department.getChildren());
        return departments;
    }

    /**
     * 遍历添加叶子结点
     *
     * @param departments
     * @param department
     */
    private void traverseAndAddLeaf(List<Department> departments, List<Department> depts) {
        for (Department department : depts) {
            if (department.getChildren().size() == 0) {
                departments.add(department);
            } else {
                traverseAndAddLeaf(departments, department.getChildren());
            }
        }
    }

    /**
     * 根据部门UUID获取该部门的最顶级部门
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#getTopDepartment(java.lang.String)
     */
    @Override
    public Department getTopDepartment(String uuid) {
        Department department = this.get(uuid);
        Department parent = department;
        if (parent.getParent() != null) {
            while (parent.getParent() != null) {
                parent = parent.getParent();
            }
        } else {
            parent = department;
        }
        return parent;
    }

    /**
     * 根据部门UUID加载角色树，自动选择已有角色
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#getRoleTree(java.lang.String)
     */
    @Override
    public TreeNode getRoleTree(String uuid) {
        List<Role> roles = roleService.getAll();
        // Set<Role> userRoles =
        // this.securityApiFacade.getRolesByDepartmentUuid(uuid);
        // return OrgUtil.getCategoryRoleTree(dataDictionaryDao, userRoles,
        // roles);
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#getDepartmentRoleNestedRoleTree(java.lang.String)
     */
    @Override
    public TreeNode getDepartmentRoleNestedRoleTree(String uuid) {
        Department department = this.get(uuid);
        TreeNode treeNode = new TreeNode();
        treeNode.setName(department.getName());
        treeNode.setId(TreeNode.ROOT_ID);
        // Set<Role> roles =
        // this.securityApiFacade.getRolesByDepartmentUuid(uuid);
        // List<TreeNode> children = new ArrayList<TreeNode>();
        // for (Role role : roles) {
        // TreeNode child = new TreeNode();
        // child.setId(role.getUuid());
        // child.setName(role.getName());
        // children.add(child);
        // this.roleService.buildRoleNestedRoleTree(child, role);
        // }
        // treeNode.setChildren(children);
        return treeNode;
    }

    /**
     * 根据部门ID获取部门下的所有用户ID(包括子部门)
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#getAllUserIdsByDepartmentId(java.lang.String)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public List<String> getAllUserIdsByDepartmentId(String departmentId) {
        List<String> userIds = new ArrayList<String>();
        // 获取部门下的用户
        Department department = this.getById(departmentId);
        traverseAndUserId(department, userIds, true);
        return userIds;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#getAllUserIdsByDepartmentId(java.lang.String, boolean)
     */
    @Override
    @Cacheable(value = CacheName.DEFAULT)
    public List<String> getAllUserIdsByDepartmentId(String departmentId, boolean includeDisable) {
        List<String> userIds = new ArrayList<String>();
        // 获取部门下的用户
        Department department = this.getById(departmentId);
        traverseAndUserId(department, userIds, includeDisable);
        return userIds;
    }

    /**
     * 根据部门ID获取部门下的用户ID(不包括子部门)
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#getUserIdsByDepartmentId(java.lang.String)
     */
    @Override
    public List<String> getUserIdsByDepartmentId(String departmentId) {
        List<String> userIds = new ArrayList<String>();
        List<User> userls = getUsersByDepartmentId(departmentId);
        for (User user : userls) {
            userIds.add(user.getId());
        }
        return userIds;
    }

    /**
     * 遍历添加叶子结点
     *
     * @param department
     * @param userIds
     */
    private void traverseAndUserId(Department department, List<String> userIds, boolean includeDisable) {
        if (department == null) {
            return;
        }

        List<DepartmentUserJob> departmentUserJobs = this.departmentUserJobService
                .getByDepartment(department.getUuid());
        for (DepartmentUserJob departmentUserJob : departmentUserJobs) {
            User user = departmentUserJob.getUser();
            if (includeDisable) {
                userIds.add(user.getId());
            } else if (Boolean.TRUE.equals(user.getEnabled())) {
                userIds.add(user.getId());
            }
        }
        // 递归遍历子结点
        for (Department dept : department.getChildren()) {
            traverseAndUserId(dept, userIds, includeDisable);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#getByIds(java.util.Collection)
     */
    @Override
    public List<Department> getByIds(Collection<String> departmentIds) {
        List<Department> departments = new ArrayList<Department>();
        for (String id : departmentIds) {
            Department departmentModel = this.getById(id);
            if (departmentModel == null) {
                continue;
            }
            Department department = new Department();
            BeanUtils.copyProperties(departmentModel, department);
            departments.add(department);
        }
        return departments;
    }

    /*	*/

    @Override
    public List<TreeNode> getDepartmentNestedDepartmentTree(String uuid) {
        List<TreeNode> rootTreeNodeList = new ArrayList<TreeNode>();
        if (uuid != null && !uuid.equals("")) {
            // 根据uuid获得当前部门
            Department department = this.get(uuid);
            TreeNode root = new TreeNode();
            root.setName(department.getName());
            root.setId(department.getUuid());
            rootTreeNodeList.add(root);
            this.getDepartChildren(department, root);
        } else {
            // 获得顶级部门
            List<Department> departmentList = this.getTopDepartment();
            for (Department department : departmentList) {
                TreeNode root = new TreeNode();
                root.setId(department.getUuid());
                root.setName(department.getName());
                rootTreeNodeList.add(root);
                this.getDepartChildren(department, root);
            }
        }

        return rootTreeNodeList;
    }

    public void getDepartChildren(Department department, TreeNode currNode) {
        List<Department> childrenDepts = department.getChildren();
        if (!childrenDepts.isEmpty()) {
            List<TreeNode> children = new ArrayList<TreeNode>();
            for (Department dept : childrenDepts) {
                TreeNode child = new TreeNode();
                child.setId(dept.getUuid());
                child.setName(dept.getName());
                children.add(child);
                this.getDepartChildren(dept, child);
            }
            currNode.setChildren(children);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#queryDepartmentsByUserId(java.lang.String)
     */
    @Override
    public List<QueryItem> queryDepartmentsByUserId(String userId) {
        String hql = "select id as id, name as name, path as path from Department department "
                + " where exists (select uuid from DepartmentUserJob departmentUserJob where departmentUserJob.department = department and departmentUserJob.user.id = :userId)";
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("userId", userId);
        return this.dao.query(hql, values, QueryItem.class);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#getPrivilegeTree(java.lang.String)
     */
    @Override
    public TreeNode getPrivilegeTree(String uuid) {
        // 附加构建权限树
        Set<Privilege> ownerPrivileges = null;// this.securityApiFacade.getPrivilegesByDepartmentUuid(uuid);
        List<Privilege> privileges = this.privilegeFacadeService.getAll();
        TreeNode treeNode = OrgUtil.buildPrivilegeTree(privileges,
                Arrays.asList(ownerPrivileges.toArray(new Privilege[0])));
        return treeNode;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#getDepartmentPrivilegeTree(java.lang.String)
     */
    @Override
    public TreeNode getDepartmentPrivilegeTree(String uuid) {
        Department department = this.get(uuid);
        TreeNode treeNode = new TreeNode();
        treeNode.setName(department.getName());
        treeNode.setId(TreeNode.ROOT_ID);
        Set<Privilege> privileges = null;// this.securityApiFacade.getPrivilegesByDepartmentUuid(uuid);
        List<TreeNode> children = new ArrayList<TreeNode>();
        for (Privilege privilege : privileges) {
            TreeNode child = new TreeNode();
            child.setId(privilege.getUuid());
            child.setName(privilege.getName());
            children.add(child);
        }
        treeNode.setChildren(children);
        return treeNode;
    }

    @Override
    public HashMap<String, Object> saveDepartmentFromList(List list) {
        // "共获得100条部门记录，成功导入80条，已经存在20条!"
        int duplicatecount = 0;
        int successcount = 0;
        int totalcount = list.size();
        HashMap<String, Object> rsmap = new HashMap<String, Object>();
        HashMap<String, String> deptParentMap = new HashMap<String, String>();// 部门名，部门父级路径
        HashMap<String, String> deptPrincipalMap = new HashMap<String, String>();// 部门名，部门负责人名字
        if (list != null && !list.isEmpty()) {
            // name type定义
            List<DataDictionary> datadictionarys = dataDictionaryService.getDataDictionariesByType("FUNCTION_TYPE");
            for (Object o : list) {
                Department department = (Department) o;

                String deptpath = department.getPath();
                deptPrincipalMap.put(deptpath, department.getPrincipalLeaderNames());

                department.setUuid(null);
                // 设置部门名称
                String deptname = deptpath.substring(deptpath.lastIndexOf("/") + 1, deptpath.length());

                department.setName(deptname);
                // 设置部门父级map
                if (deptpath.indexOf("/") < 0) {
                    deptParentMap.put(deptpath, "");
                } else {
                    deptParentMap.put(deptpath, deptpath.substring(0, deptpath.lastIndexOf("/")));
                }

                // 部门级别的特殊处理,第一位的L去掉,数据库存1、2、3...
                String deptlevel = department.getDepartmentLevel().substring(1,
                        department.getDepartmentLevel().length());
                department.setDepartmentLevel(deptlevel);

                // add by wujx 20160623 begin
                // 默认当前租户
                department.setTenantId(SpringSecurityUtils.getCurrentTenantId());
                // add by wujx 20160623 begin

                Department departmentUpdate = new Department();
                departmentUpdate.setIsVisible(true);
                boolean isUpdate = false;
                List<Department> departmentls = this.dao.findBy(Department.class, "externalId",
                        department.getExternalId());
                if (departmentls != null && departmentls.size() > 0) {
                    departmentUpdate = departmentls.get(0);
                    isUpdate = true;
                }

                if (isUpdate) {
                    departmentUpdate.setName(department.getName());// 更新部门名称
                    departmentUpdate.setDepartmentLevel(department.getDepartmentLevel());// 更新部门级别
                    departmentUpdate.setPath(department.getPath());// 更新部门路径
                    departmentUpdate.setIsVisible(true);
                    department.setCode(department.getExternalId());
                    this.dao.save(departmentUpdate);
                    duplicatecount = duplicatecount + 1;
                } else {
                    String id = idGeneratorService.generate(Department.class, DEPARTMENT_ID_PATTERN);
                    String tenantId = SpringSecurityUtils.getCurrentTenantId();
                    id = id.substring(0, 1) + tenantId.substring(1, tenantId.length()) + id.substring(4, 11);
                    department.setId(id);
                    department.setIsVisible(true);
                    department.setCode(department.getExternalId());
                    this.dao.save(department);
                    successcount = successcount + 1;
                    // 保存职能线
                    createDeptFunctionRelation(datadictionarys, department);
                }

            }
        }

        String errormsg = "";
        for (String deptpath : deptParentMap.keySet()) {
            List<Department> deptList = this.dao.findBy(Department.class, "path", deptpath);
            Department tempdepartment = deptList.get(0);

            if (StringUtils.isEmpty(deptParentMap.get(deptpath))) {
                continue;
            }
            List<Department> preparentList = this.dao.findBy(Department.class, "path", deptParentMap.get(deptpath));
            // 如果有父级 则设置父级节点
            if (!preparentList.isEmpty()) {
                tempdepartment.setParent(preparentList.get(0));
            } else {
                // errormsg = errormsg + "\n" + "部门:【" + deptpath + "】找不到父级部门:【"
                // + deptParentMap.get(deptpath) + "】";
                errormsg = errormsg + "\n" + deptpath;
            }
        }

        if (!errormsg.equals("")) {
            throw new RuntimeException(errormsg);
        }

        String msg = "选择的部门数据共【" + totalcount + "】行，成功更新【" + duplicatecount + "】行，成功导入【" + successcount + "】行";
        // System.out.println(msg);
        logger.info(msg);
        rsmap.put("msg", msg);
        rsmap.put("deptPrincipalMap", deptPrincipalMap);
        return rsmap;
    }

    private void createDeptFunctionRelation(List<DataDictionary> datadictionarys, Department department) {
        if (!StringUtils.isEmpty(department.getFunctionNames())) {
            String[] functionnames = department.getFunctionNames().split(";");
            List<DepartmentFunction> newFunctions = new ArrayList<DepartmentFunction>();
            for (int i = 0; i < functionnames.length; i++) {
                for (DataDictionary dataDictionary : datadictionarys) {
                    if (functionnames[i].equals(dataDictionary.getName())) {
                        List<DataDictionary> functions = dataDictionaryService.findBy("uuid", dataDictionary.getUuid());
                        DepartmentFunction departmentFunction = new DepartmentFunction();
                        departmentFunction.setDepartment(department);
                        departmentFunction.setFunctionUuid(functions.get(0).getUuid());
                        newFunctions.add(departmentFunction);
                    }
                }
            }
            // 建立职能关系
            for (DepartmentFunction entity : newFunctions) {
                this.dao.save(entity);
            }
        }
    }

    /**
     * 导入后更新部门负责人
     *
     * @param deptRsMp
     */
    @Override
    public void updateDeptPrincipalAfterImport(HashMap<String, Object> deptRsMp) {
        HashMap<String, String> deptPrincipalMap = (HashMap<String, String>) deptRsMp.get("deptPrincipalMap");

        for (String path : deptPrincipalMap.keySet()) {
            List<Department> deptList = this.dao.findBy(Department.class, "path", path);
            Department department = deptList.get(0);

            // 如果为空，则直接删除关联信息
            if (StringUtils.isEmpty(deptPrincipalMap.get(path))) {
                deptPrincipalService.deletePrincipal(department.getUuid());
                department.setPrincipalLeaderNames(null);
                this.dao.save(department);
                continue;
            }

            String[] leaders = deptPrincipalMap.get(path).split(";");
            List<DeptPrincipal> newPrincipals = new ArrayList<DeptPrincipal>();
            for (int i = 0; i < leaders.length; i++) {

                String jobName = leaders[i];

                String deptPath = jobName.substring(0, jobName.lastIndexOf("/"));
                String realJobName = jobName.substring(jobName.lastIndexOf("/") + 1, jobName.length());

                // 判断职位是否存在 不存在就报错了..
                Job job = null;
                List<Job> jobls = jobService.getJobByNameAndDeptName(realJobName.replaceAll(" ", ""), deptPath);
                if (jobls == null || jobls.size() == 0) {
                    throw new RuntimeException("职位：【" + realJobName + "】+部门:【" + deptPath + "】找不到相应的职位记录！");
                } else {
                    job = jobls.get(0);
                }

                DeptPrincipal principal = new DeptPrincipal();
                principal.setDepartmentUuid(department.getUuid());
                principal.setType(DeptPrincipal.TYPE_PRINCIPAL);
                principal.setOrgId(job.getId());
                principal.setTenantId(SpringSecurityUtils.getCurrentTenantId());
                newPrincipals.add(principal);
            }

            // 更新之前先删除一把 这样解决负责人变更的问题
            deptPrincipalService.deletePrincipal(department.getUuid());

            for (DeptPrincipal entity : newPrincipals) {
                this.dao.save(entity);
            }
            department.setPrincipalLeaderNames(deptPrincipalMap.get(path));
            this.dao.save(department);
        }
    }

    @Override
    public List<User> getUsersByDepartmentId(String departmentId) {
        List<User> userls = new ArrayList<User>();
        // 获取部门下的用户
        Department department = this.getById(departmentId);
        List<DepartmentUserJob> departmentUserJobs = this.departmentUserJobService
                .getByDepartment(department.getUuid());
        for (DepartmentUserJob departmentUserJob : departmentUserJobs) {
            userls.add(departmentUserJob.getUser());
        }
        return userls;
    }

    /**
     * 获取该部门下的所有用户（20141103 效率优化 关联查询.）
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#getAllUsersByDepartmentId(java.lang.String)
     */
    @Override
    public List<User> getAllUsersByDepartmentId(String departmentId) {
        List<User> userls = new ArrayList<User>();
        // 获取部门下的用户
        Department department = this.getById(departmentId);
        if (department == null) {
            return userls;
        }
        userls = this.getAllUserByDepartmentPath(department.getPath());
        // traverseAndUser(department, userls);
        return userls;
    }

    /**
     * 遍历添加叶子结点
     *
     * @param department
     * @param userIds
     */
    /*
     * private void traverseAndUser(Department department, List<User> userls) {
     * if (department == null) { return; }
     *
     * List<DepartmentUserJob> departmentUserJobs =
     * this.departmentUserJobDao.getByDepartment(department.getUuid()); for
     * (DepartmentUserJob departmentUserJob : departmentUserJobs) {
     * userls.add(departmentUserJob.getUser()); } // 递归遍历子结点 for (Department
     * dept : department.getChildren()) { traverseAndUser(dept, userls); } }
     */

    /*
     * (No Javadoc) <p>Title: getAll</p> <p>Description: </p>
     *
     * @return
     *
     * @see com.wellsoft.pt.org.service.DepartmentService#getAll()
     */
    public List<Department> getAll() {
        return this.dao.getAll(Department.class);
    }

    /**
     * 获取上一级部门
     */
    @Override
    public Department getParentDepartment(String deptUuid) {
        List<Department> departments = this.getParentDeptByUuid(deptUuid);
        for (Department department : departments) {
            return department;
        }
        return null;
    }

    @Override
    public List<String> getAllChildrenDeptUuids(String departmentUuid) {
        Department department = this.get(departmentUuid);
        List<String> uuids = new ArrayList<String>();
        traverseUuids(department, uuids);
        return uuids;
    }

    private void traverseUuids(Department department, List<String> uuids) {
        if (department != null) {
            uuids.add(department.getUuid());
            for (Department child : department.getChildren()) {
                traverseUuids(child, uuids);
            }
        }
    }

    @Override
    public Set<String> getAllChildDeptUuidByUuid(String uuid) {
        Set<String> sets = new HashSet<String>();
        sets.add(uuid);
        traverseDeptUuid(sets, uuid);
        return sets;
    }

    private void traverseDeptUuid(Set<String> sets, String uuid) {
        List<String> list = this.getChildDeptUuidsByUuid(uuid);
        sets.addAll(list);
        for (String uuidStr : list) {
            traverseDeptUuid(sets, uuidStr);
        }
    }

    @Override
    public List<DepartmentBean> getListDepartmentByUnitId(String unitId) {
        // TODO Auto-generated method stub
        List<DepartmentBean> departmentBeans = new ArrayList<DepartmentBean>();
        List<Department> departments = this.dao.findBy(Department.class, "commonUnitId", unitId);
        DepartmentBean departmentBean = null;
        for (Department department : departments) {
            departmentBean = new DepartmentBean();
            BeanUtils.copyProperties(department, departmentBean);
            departmentBeans.add(departmentBean);
        }
        return departmentBeans;
    }

    @Override
    public void saveOrUpdateCommonDepartment(String unitId, String depJson) {
        // TODO Auto-generated method stub
        net.sf.json.JSONArray jsonarray = net.sf.json.JSONArray.fromObject(depJson);
        // 找出原先挂接在该单位的部门
        List<DepartmentBean> departmentBeans = this.getListDepartmentByUnitId(unitId);
        CommonUnit commonUnit = commonUnitDao.findUniqueBy("id", unitId);
        Map<String, Map<String, String>> saveDataMap = new HashMap<String, Map<String, String>>();
        // 组装保存的MAP
        for (Object json : jsonarray) {
            Map<String, String> hashMap = JsonUtils.toMap(json.toString().substring(1, json.toString().length() - 1));
            saveDataMap.put(hashMap.get("id"), hashMap);
        }
        // 遍历原始数据判断增加/删除/改
        for (DepartmentBean departmentBean : departmentBeans) {
            // 原始数据没有的删除departmentBean;
            Department department = this.get(departmentBean.getUuid());
            if (saveDataMap.get(departmentBean.getId()) == null) {
                // 移除挂接
                commonDepartmentService.updateCommonDepartmentUserByCommonUnit(unitId, departmentBean.getId(), null,
                        departmentBean.getIsVisible(), commonUnit.getTenantId());
                department.setCommonUnitId(null);
                this.dao.save(department);
            } else {
                // 更新对应的可见
                Boolean isvisible = "1".equals(saveDataMap.get(departmentBean.getId()).get("isVisible"));
                commonDepartmentService.updateCommonDepartmentUserByCommonUnit(null, departmentBean.getId(), null,
                        isvisible, commonUnit.getTenantId());
                // 更新完后移除MAP集合中，移除完毕后剩下的就是新增的节点
                this.dao.save(department);
                saveDataMap.remove(departmentBean.getId());
            }
        }
        for (String key : saveDataMap.keySet()) {
            // 新增操作
            String isVisible = saveDataMap.get(key).get("isVisible");
            commonDepartmentService.updateCommonDepartmentUserByCommonUnit(null, saveDataMap.get(key).get("id"),
                    commonUnit.getId(), "1".equals(isVisible), commonUnit.getTenantId());
        }
    }

    @Override
    public Set<DepartmentUserJob> getDepartmentJobUserByDepartment(Department department) {
        // TODO Auto-generated method stub
        return this.get(department.getUuid()).getDepartmentUsers();
    }

    @Override
    public User getUserByDepartmentJobUser(DepartmentUserJob departmentUserJob) {
        // TODO Auto-generated method stub
        return this.departmentUserJobService.get(departmentUserJob.getUuid()).getUser();
    }

    @Override
    public List<Department> getChildren(Department department) {
        // TODO Auto-generated method stub
        return this.get(department.getUuid()).getChildren();
    }

    @Override
    public Set<Department> getDepartmentByRoleUuid(String roleUuid) {
        // TODO Auto-generated method stub
        Set<Department> departments = new HashSet<Department>();
        List<DepartmentRole> departmentRoles = departmentRoleService.getDepartmentRoleByRoleUuid(roleUuid);
        for (DepartmentRole departmentRole : departmentRoles) {
            departments.add(this.get(departmentRole.getDepartmentRoleId().getDepartmentUuid()));
        }
        return departments;
    }

    public List<Department> getChildrenById(String id) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("id", id);
        return this.dao.query(QUERY_CHILDREN_BY_ID, queryMap, Department.class);
    }

    public List<Department> getTopDepartment() {
        String hql = "select o from Department o where o.parent.uuid is null";
        return this.dao.query(hql, null, Department.class);
    }

    public Department getByPath(String path) {
        String[] paths = path.split("/");
        int length = paths.length;
        String orgpath = paths[length - 1];
        List<String> pathlist = Lists.newArrayList();
        // 倒序处理
        for (int i = length - 2; i >= 0; i--) {
            pathlist.add(paths[i]);
        }
        String hql = "select o from Department o where o.name = '" + orgpath + "'";
        String parent = "o";
        for (String o : pathlist) {
            parent = parent + ".parentDep";
            hql = hql + " and " + parent + ".name ='" + o + "'";
        }

        Department dep = null;
        try {
            dep = this.dao.findUnique(hql, null);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return dep;
    }

    /**
     * @param @param  uuid
     * @param @return 设定文件
     * @return List<Organization> 返回类型
     * @throws
     * @Title: getAllChildren
     * @Description: 级联返回部门所有下属部门
     */
    public List<Department> getAllChildren(String id) {
        List<Department> allchildren = Lists.newArrayList();
        for (Department node : getChildrenById(id)) {
            iteratorChild(node, allchildren);
        }
        return allchildren;
    }

    /**
     * @param @param  uuid
     * @param @return 设定文件
     * @return Set<User> 返回类型
     * @throws
     * @Title: getAllUser
     * @Description: 返回所有部门下的用户，包括子部门下的用户，这里使用set来处理,解决重复问题
     */
    public Set<User> getAllUserByUUID(String uuid) {
        // 添加本部门用户
        Department dept = get(uuid);
        Set<User> users = Sets.newHashSet();
        users.addAll(getUser(uuid));
        // 添加所有子部门用户
        List<Department> allOrg = this.getAllChildren(uuid);
        for (Department org : allOrg) {
            users.addAll(getUser(org.getUuid()));
        }
        return users;
    }

    /**
     * @param @param  uuid
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     * @Title: getPath
     * @Description: 获取路径
     */
    public String getFullPath(String uuid) {
        Department department = this.getById(uuid);
        if (department == null)
            department = this.get(uuid);
        String path = department.getName();
        Department parent = department.getParent();
        while (parent != null) {
            path = parent.getName() + "/" + path;
            parent = parent.getParent();
        }
        return path;
    }

    /**
     * @param @param  orguuid
     * @param @return 设定文件
     * @return List<User> 返回类型
     * @throws
     * @Title: getUser
     * @Description: 获取本部门下用户
     */
    public List<User> getUser(String deptuuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("uuid", deptuuid);
        List<User> users = this.dao.query(QUERY_USER_BY_DEPID, queryMap, User.class);
        return users;
    }

    public List<Department> getDeptsByUser(String useruuid) {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("uuid", useruuid);
        List<Department> depts = this.dao.query(QUERY_DEP_BY_USERID, queryMap, Department.class);
        return depts;
    }

    public List<User> getSameDeptUsers(String useruuid) {
        List<Department> depts = getDeptsByUser(useruuid);
        List<User> users = Lists.newArrayList();
        for (Department dept : depts) {
            users.addAll(getUser(dept.getUuid()));
        }
        return users;
    }

    // 遍历部门
    private void iteratorChild(Department node, List<Department> allchildren) {
        if (node != null) {
            allchildren.add(node);
            for (Department child : getChildrenById(node.getUuid())) {
                if (getChildrenById(child.getUuid()).size() > 0) {
                    // 进入递归处理
                    iteratorChild(child, allchildren);
                } else {
                    // 处理没有子部门
                    if (!allchildren.contains(child)) {
                        allchildren.add(child);
                    }
                }
            }
        }
    }

    public void addChild(String uuid, Department child) {
        Department dep = get(uuid);
        child.setParent(dep);
        this.save(child);
    }

    public void addChild(Department dep, Department child) {
        child.setParent(dep);
        this.save(child);
    }

    public List<Department> getTopLevel() {
        HashMap<String, Object> queryMap = new HashMap<String, Object>();
        queryMap.put("tenantId", SpringSecurityUtils.getCurrentTenantId());
        return this.dao.query(QUERY_TOPLEVEL_DEPARTMENT, queryMap, Department.class);
    }

    // public List<Group> getGroupByDep(String uuid) {
    // Department dep = get(uuid);
    // List<Group> groups = Lists.newArrayList();
    // if (dep != null) {
    // // 查询出拥有部门的群组
    // HashMap<String, Object> queryMap = new HashMap<String, Object>();
    // queryMap.put("uuid", uuid);
    // groups = this.dao.query(QUERY_GROUP_BY_DEPID, queryMap, Group.class);
    // }
    // return groups;
    // }

    /**
     * @return
     */
    public Department getById(String id) {
        return this.dao.findUniqueBy(Department.class, "id", id);
    }

    public List<Department> getChildren(String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuid", uuid);
        return this.dao.query(QUERY_CHILDREN, values, Department.class);
    }

    /**
     * 如何描述该方法
     *
     * @param rawName
     * @return
     */
    public List<String> getDeptmentIdsLikeName(String rawName) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", rawName);
        return this.dao.query(GET_DEPTMENT_ID_LIKE_NAME, values);
    }

    /**
     * @param id
     * @param childrenIds
     */
    public void traverseAndAddChildrenIds(String departmentId, Collection<String> childrenIds) {
        List<String> ids = getChildrenIdsById(departmentId);
        childrenIds.addAll(ids);
        for (String id : ids) {
            traverseAndAddChildrenIds(id, childrenIds);
        }
    }

    /**
     * @param departmentId
     * @return
     */
    private List<String> getChildrenIdsById(String departmentId) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("departmentId", departmentId);
        return this.dao.query(QUERY_CHILDREN_IDS_BY_ID, values);
    }

    /**
     * 获得该部门下的所有用户(包括子部门)
     *
     * @param department
     */
    public List<User> getAllUserByDepartmentPath(String deptPath) {
        String UserjobsSql = "select u from User u,DepartmentUserJob dept_user_job where dept_user_job.department.path like '"
                + deptPath + "%' and u=dept_user_job.user order by dept_user_job.user.code asc";
        List<User> users = this.dao.find(UserjobsSql, null, User.class);
        return users;
    }

    /**
     * 获得该部门下的所有用户(包括子部门)
     *
     * @param department
     */
    public List<Job> getAllJobByDepartmentPath(String deptPath) {
        String jobsSql = "select j from Job j,Department dept where dept.path like '" + deptPath
                + "%' and j.departmentUuid=dept.uuid order by j.code asc,j.name ";
        List<Job> jobs = this.dao.query(jobsSql, null, Job.class);
        return jobs;
    }

    public List<Department> getAllChildrenDeptByPath(String deptPath) {
        String qrySql = "select dept from Department dept where dept.path like '" + deptPath + "%'  ";
        List<Department> depts = this.dao.query(qrySql, null, Department.class);
        return depts;
    }

    /**
     * 由部门uuid获取上一级部门
     *
     * @param deptUuid
     * @return
     */
    public List<Department> getParentDeptByUuid(String deptUuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("deptUuid", deptUuid);
        return this.dao.query(QUERY_PARENT_DEPARTMENT, values, Department.class);
    }

    public List<String> getChildDeptUuidsByUuid(String uuid) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuid", uuid);
        return this.dao.query(GET_UUIDS_BY_UUID, values);
    }

    @Override
    public List<Department> namedQuery(String string, Map<String, Object> values, Class<Department> class1,
                                       PagingInfo pagingInfo) {
        // TODO Auto-generated method stub
        return this.dao.namedQuery(string, values, pagingInfo);
    }

    @Override
    public List<Department> namedQuery(String string, Map<String, Object> values, Class<Department> class1) {
        // TODO Auto-generated method stub
        return this.dao.namedQuery(string, values, class1);
    }

    @Override
    public List<User> getAllUserByUnitId(String id) {
        List<Department> departments = this.dao.findBy(Department.class, "commonUnitId", id);
        List<User> users = new ArrayList<User>();
        for (Department department : departments) {
            Set<DepartmentUserJob> departmentUserJobs = department.getDepartmentUsers();
            for (DepartmentUserJob departmentUserJob : departmentUserJobs) {
                User user = departmentUserJob.getUser();
                if (user != null) {
                    users.add(user);
                }
            }
        }

        return users;
    }

    @Override
    public List<DeptPrincipal> getAllPrincipals(String deptUuid) {
        return this.deptPrincipalDao.getAllPrincipals(deptUuid);
    }

    @Override
    public String getManagerWithRecursionByDeptUuid(String deptUuid) {
        String sql = " with org as(                       " + " select uuid,id,name,rownum as rn   "
                + " from org_department                " + " start with uuid='" + deptUuid + "'"
                + " connect by prior parent_uuid=uuid  " + " )                                  "
                + " select a.id,a.rn,listagg(b.org_id,';') within group (order by a.id) from org a"
                + " left join org_dept_principal b on a.uuid = b.department_uuid and b.type = '2' "
                + " group by a.id,a.rn                                                            " + " order by a.rn";
        List list = this.dao.getSession().createSQLQuery(sql).list();
        String manager = "";
        if (null != list && list.size() > 0) {
            for (Object item : list) {
                Object[] deptInfo = (Object[]) item;
                if (deptInfo[2] != null) {
                    String str = deptInfo[2].toString();
                    if (str.trim().length() > 0) {
                        manager = str;
                    }

                    break;
                }
            }
        }
        return null;
    }

    @Override
    public String getManagerWithRecursionByDeptId(String deptId) {
        String sql = " with org as(                       " + " select uuid,id,name,rownum as rn   "
                + " from org_department                " + " start with id='" + deptId + "'"
                + " connect by prior parent_uuid=uuid  " + " )                                  "
                + " select a.id,a.rn,listagg(b.org_id,';') within group (order by a.id) from org a"
                + " left join org_dept_principal b on a.uuid = b.department_uuid and b.type = '2' "
                + " group by a.id,a.rn                                                            " + " order by a.rn";
        List list = this.dao.getSession().createSQLQuery(sql).list();
        String manager = "";
        if (null != list && list.size() > 0) {
            for (Object item : list) {
                Object[] deptInfo = (Object[]) item;
                if (deptInfo[2] != null) {
                    String str = deptInfo[2].toString();
                    if (str.trim().length() > 0) {
                        manager = str;
                    }

                    break;
                }
            }
        }
        return manager;
    }

}
