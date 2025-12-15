package com.wellsoft.pt.org.unit.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.org.dao.JobDao;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.Job;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.service.JobService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.org.service.impl.OrgUtil;
import com.wellsoft.pt.org.support.TreeOrgType;
import com.wellsoft.pt.org.unit.service.UnitOrgTreeService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.locale.converters.DateLocaleConverter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service
@Transactional
public class UnitOrgTreeServiceImpl implements UnitOrgTreeService {
    private Logger logger = LoggerFactory.getLogger(UnitOrgTreeService.class);
    @Autowired
    private UserService userService;
    @Autowired
    private JobService jobService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private JobDao jobDao;
    @Autowired
    private NativeDao nativeDao;

    @Override
    public List<TreeNode> getOrgEmployeeeManageTreeBySearchName(String searchName) {
        Map<String, String> authMap = findDepartmentUuidWithPrincipalUser(SpringSecurityUtils.getCurrentUserId());
        return getOrgEmployeeeManageTreeBySearchName(searchName, authMap);
    }

    @Override
    public List<TreeNode> getOrgEmployeeeTreeBySearchName(String searchName) {
        return getOrgEmployeeeManageTreeBySearchName(searchName, null);
    }

    public List<TreeNode> getOrgEmployeeeManageTreeBySearchName(String searchName, Map<String, String> authMap) {
        // 当name为空时 查询全部
        if (StringUtils.isBlank(searchName)) {
            return getOrgEmployeeManageTree(null, authMap);
        } else {
            try {
                List<TreeNode> treeNodeList = new ArrayList<TreeNode>();
                Map<String, Object> values = new HashMap<String, Object>();
                values.put("code", searchName);
                values.put("name", searchName);
                values.put("pinyin", searchName);
                // add by heshi 20150728 权限修改
                values.put("loginUserId", SpringSecurityUtils.getCurrentUserId());

                // 使用自带转化器转化时间格式
                ConvertUtils.register(new DateLocaleConverter(), Date.class);

                // 查询部门
                List<Department> deptTempLi = departmentService.namedQuery("departmentTreeQuery", values,
                        Department.class);
                // add by heshi 20150728 begin
                List<Department> deptList = new ArrayList<Department>();
                for (Department temp : deptTempLi) {
                    if (authMap == null
                            || (authMap.containsKey(temp.getUuid()) && "1".equals(authMap.get(temp.getUuid())))) {
                        deptList.add(temp);
                    }
                }
                // add by heshi 20150728 end
                // 查找复合职位的所有部门
                List<QueryItem> jobDepList = nativeDao.namedQuery("getSerchJobName2DepmartWithAuth", values,
                        QueryItem.class);
                add2DeptList(jobDepList, deptList);
                // 查找复合用户的所有部门
                List<QueryItem> userDepItems = nativeDao.namedQuery("getHrSerchNameEmployeeDatasWithAuth", values,
                        QueryItem.class);
                add2DeptList(userDepItems, deptList);

                Map<String, TreeNode> depMap = new HashMap<String, TreeNode>();
                if (!CollectionUtils.isEmpty(deptList)) {
                    for (Department dep : deptList) {
                        if (!depMap.containsKey(dep.getUuid())) {
                            TreeNode treeNode = parseDepartment2AuthTree(dep, authMap);
                            depMap.put(dep.getUuid(), treeNode);

                            parseParentDep2AuthTree(treeNode, dep.getParent(), depMap, authMap);
                        }
                    }
                }

                List<Department> childDepartments = departmentService.getTopLevel();
                if (!CollectionUtils.isEmpty(childDepartments)) {
                    for (Department dep : childDepartments) {
                        TreeNode treeNode = depMap.get(dep.getUuid());
                        if (null != treeNode)
                            treeNodeList.add(treeNode);
                    }
                }
                return treeNodeList;
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private void parseParentDep2AuthTree(TreeNode childTreeNode, Department parentDep, Map<String, TreeNode> depMap,
                                         Map<String, String> authMap) {
        if (null != parentDep && null != childTreeNode && null != depMap) {
            // depMap没有时
            if (!depMap.containsKey(parentDep.getUuid())) {
                TreeNode parentTreeNode = parseDepartment2AuthTree(parentDep, authMap);
                depMap.put(parentDep.getUuid(), parentTreeNode);

                addChild2ParnetNodeAuth(childTreeNode, parentTreeNode);

                parseParentDep2AuthTree(parentTreeNode, parentDep.getParent(), depMap, authMap);
                // depMap有时直接取depMap
            } else {
                TreeNode parentTreeNode = depMap.get(parentDep.getUuid());
                addChild2ParnetNodeAuth(childTreeNode, parentTreeNode);
            }
        }
    }

    public void addChild2ParnetNodeAuth(TreeNode childTreeNode, TreeNode parentTreeNode) {
        List<TreeNode> childTreeNodes = parentTreeNode.getChildren();
        int index = 0;
        if (CollectionUtils.isNotEmpty(childTreeNodes)) {
            for (; index < childTreeNodes.size(); index++) {
                if (StringUtils.equals(childTreeNode.getId(), childTreeNodes.get(index).getId())) {
                    break;
                }
            }
            if (childTreeNodes.size() > index) {
                childTreeNodes.set(index, childTreeNode);
            }
        } else {
            childTreeNodes = new ArrayList<TreeNode>();
            childTreeNodes.add(childTreeNode);
        }

        parentTreeNode.setChildren(childTreeNodes);
    }

    private void add2DeptList(List<QueryItem> items, List<Department> deptList) {
        if (CollectionUtils.isNotEmpty(items)) {
            List<String> uuids = new ArrayList<String>();
            for (QueryItem item : items) {
                if (StringUtils.isNotBlank(item.getString("uuid")))
                    uuids.add(item.getString("uuid"));
            }
            if (CollectionUtils.isNotEmpty(uuids)) {
                Map<String, Object> values = new HashMap<String, Object>();
                values.put("uuids", uuids);
                List<Department> deptList1s = departmentService.namedQuery("getHrDepmartByUuids", values,
                        Department.class);
                if (CollectionUtils.isNotEmpty(deptList1s)) {
                    for (Department department : deptList1s) {
                        if (!deptList.contains(department))
                            deptList.add(department);
                    }
                }
            }
        }
    }

    @Override
    public List<TreeNode> getOrgEmployeeManageTree(String uuid) {
        Map<String, String> authMap = findDepartmentUuidWithPrincipalUser(SpringSecurityUtils.getCurrentUserId());
        return getOrgEmployeeManageTree(uuid, authMap);

    }

    @Override
    public List<TreeNode> getOrgEmployeeTree(String uuid) {
        return getOrgEmployeeManageTree(uuid, null);

    }

    public List<TreeNode> getOrgEmployeeManageTree(String uuid, Map<String, String> authMap) {
        List<TreeNode> treeNodeList = new ArrayList<TreeNode>();
        // 使用自带转化器转化时间格式
        ConvertUtils.register(new DateLocaleConverter(), Date.class);
        // 当uuid为空或者 -1 时 查询顶级部门
        List<Department> childDepartments = null;

        if (StringUtils.isBlank(uuid) || "null".equals(uuid) || "-1".equals(uuid)) {
            childDepartments = departmentService.getTopLevel();
            for (Department department : childDepartments) {
                if ((authMap == null || authMap.containsKey(department.getUuid()))
                        && department.getIsVisible()
                        && (StringUtils.isBlank(department.getOrgId()) || OrgUtil.getDefaultOrgId().equals(
                        department.getOrgId()))) {
                    TreeNode treeNode = parseDepartment2AuthTree(department, authMap);
                    if (null != treeNode) {
                        treeNode.setOpen(true);
                        treeNodeList.add(treeNode);
                    }
                }
            }
        } else {
            // 该部门下的所有子节点
            TreeNode treeNode = parseDepartment2AuthTree(departmentService.get(uuid), authMap);
            if (null != treeNode && !treeNode.getChildren().isEmpty()) {
                treeNodeList.addAll(treeNode.getChildren());
            }
        }
        return treeNodeList;

    }

    /**
     * 解析部门
     *
     * @param department
     * @param treeNode
     * @param showDisable
     */
    private TreeNode parseDepartment2AuthTree(Department department, Map<String, String> authMap) {
        // 有权限的部门列表 add by heshi20150728
        TreeNode treeNode = new TreeNode();
        try {
            if (null == department || null == treeNode)
                return null;

            // 设置部门信息
            setDepartment2TreeNode(department, treeNode);

            List<Job> jobs = jobDao.getJobByDeptUuid(department.getUuid());
            for (Job job : jobs) {
                if (null != job
                        && (authMap == null || authMap.containsKey(job.getDepartmentUuid())
                        && "1".equals(authMap.get(job.getDepartmentUuid())))) {
                    // 设置岗位信息
                    TreeNode jobTreeNode = new TreeNode();
                    setJob2TreeNode(job, jobTreeNode);
                    treeNode.getChildren().add(jobTreeNode);
                    // 获取职位下的用户
                    parseJobUser2Tree(job, jobTreeNode.getChildren());
                }
            }

            // 获取子部门
            if (null != department.getChildren() && !department.getChildren().isEmpty()) {
                for (Department childDepartment : department.getChildren()) {
                    if ((authMap == null || authMap.containsKey(childDepartment.getUuid()))
                            && childDepartment.getIsVisible()) {
                        TreeNode childTreeNode = new TreeNode();
                        setDepartment2TreeNode(childDepartment, childTreeNode);
                        // 判断是否有子节点
                        if (childDepartment.getChildren().size() > 0) {
                            childTreeNode.setIsParent(true);
                        } else {
                            childTreeNode.setIsParent(haveActiveJobDepartment(childDepartment.getUuid()));
                        }
                        treeNode.getChildren().add(childTreeNode);
                    }
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        return treeNode;
    }

    public boolean haveActiveJobDepartment(String uuid) {
        if (StringUtils.isEmpty(uuid)) {
            return false;
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("uuid", uuid);
        List<String> result = this.jobDao.find("select d.uuid from Job d where d.departmentUuid=:uuid ", values);

        return null != result && result.size() > 0;
    }

    /**
     * 解析职位角色
     *
     * @param job
     * @param isActive
     * @param jobTreeNode
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void parseJobUser2Tree(Job job, List<TreeNode> childsTreeNodes) throws IllegalAccessException,
            InvocationTargetException {
        if (null == job)
            return;
        List<User> users = jobService.getUsersByJobId(job.getId());
        for (User user1 : users) {
            User user = new User();
            BeanUtils.copyProperties(user1, user);
            Hibernate.initialize(user);
            TreeNode userTreeNode = new TreeNode();
            setUserItem2TreeNode(user, userTreeNode);
            childsTreeNodes.add(userTreeNode);
        }
    }

    /**
     * 设置岗位信息
     *
     * @param user
     * @param userTreeNode
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void setUserItem2TreeNode(User user, TreeNode userTreeNode) {
        userTreeNode.setId(user.getUuid());
        userTreeNode.setName(user.getUserName());

        userTreeNode.setData(user);

        // path放节点类型
        userTreeNode.setPath(TreeOrgType.employee.getType());

        // 设置图标类型
        userTreeNode.setIconSkin(TreeOrgType.employee.getType());
    }

    /**
     * 设置部门下的岗位信息
     *
     * @param job
     * @param jobTreeNode
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void setJob2TreeNode(Job job, TreeNode jobTreeNode) throws IllegalAccessException,
            InvocationTargetException {
        // TODO
        jobTreeNode.setId(job.getUuid());
        jobTreeNode.setName(job.getName());
        Job dataJob = new Job();
        BeanUtils.copyProperties(dataJob, job);
        dataJob.setJobUsers(null);
        dataJob.setFunctions(null);
        // dataJob.setGroups(null);
        dataJob.setJobUsers(null);
        dataJob.setDuty(null);
        jobTreeNode.setData(dataJob);
        // path放节点类型
        jobTreeNode.setPath(TreeOrgType.job.getType());
        // 设置职位图标
        jobTreeNode.setIconSkin(TreeOrgType.job.getType());
    }

    /**
     * 设置部门信息
     *
     * @param department
     * @param treeNode
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private void setDepartment2TreeNode(Department department, TreeNode treeNode) throws IllegalAccessException,
            InvocationTargetException {
        // TODO
        treeNode.setId(department.getUuid());
        treeNode.setName(department.getName());
        Department dataDepartment = new Department();
        BeanUtils.copyProperties(dataDepartment, department);
        dataDepartment.setDepartmentUsers(null);
        dataDepartment.setDepartmentPrincipals(null);
        dataDepartment.setChildren(null);
        // dataDepartment.setGroups(null);
        dataDepartment.setFunctions(null);
        dataDepartment.setParent(null);
        treeNode.setData(dataDepartment);
        // path放节点类型
        treeNode.setPath(TreeOrgType.department.getType());

        // 部门图标
        treeNode.setIconSkin(TreeOrgType.department.getType());
    }

    public Map<String, String> findDepartmentUuidWithPrincipalUser(String userId) {
        Map<String, String> map = new HashMap<String, String>();
        String sql = " SELECT d.uuid,'0' FROM org_department d connect by  d.uuid = prior d.parent_uuid  start with exists (select 1  from org_dept_principal p where p.department_uuid = d.uuid and (p.org_id = '"
                + userId
                + "' or exists (select 1 from org_user_job uj, org_user u, org_job j where uj.job_uuid = j.uuid  and u.uuid = uj.user_uuid and j.id = p.org_id   and u.id ='"
                + userId
                + "')))"
                + "union all"
                + " SELECT d.uuid,'1' FROM org_department d connect by prior d.uuid = d.parent_uuid  start with exists (select 1  from org_dept_principal p where p.department_uuid = d.uuid and (p.org_id = '"
                + userId
                + "' or exists (select 1 from org_user_job uj, org_user u, org_job j where uj.job_uuid = j.uuid  and u.uuid = uj.user_uuid and j.id = p.org_id   and u.id ='"
                + userId + "')))";
        List<?> list = jobDao.getSession().createSQLQuery(sql).list();
        if (null != list && list.size() > 0) {
            for (Object temp : list) {
                Object[] o = (Object[]) temp;
                String uuid = o[0].toString();
                String type = o[1].toString();
                if (!map.containsKey(uuid) || map.get(uuid).equals("0")) {
                    map.put(uuid, type);
                }
            }
        }
        return map;
    }
}
