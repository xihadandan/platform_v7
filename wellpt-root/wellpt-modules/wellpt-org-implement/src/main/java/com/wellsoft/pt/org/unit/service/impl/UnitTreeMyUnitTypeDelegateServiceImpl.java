/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.service.impl;

import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.DepartmentUserJob;
import com.wellsoft.pt.org.entity.User;
import com.wellsoft.pt.org.entity.UserProperty;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.service.DepartmentUserJobService;
import com.wellsoft.pt.org.service.UserPropertyService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.wellsoft.pt.org.unit.service.impl.UnitTreeServiceImpl.*;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-15.1	zhulh		2013-1-15		Create
 * </pre>
 * @date 2013-1-15
 */
@Service
@Transactional
public class UnitTreeMyUnitTypeDelegateServiceImpl extends UnitTreeTypeDelegateService {
    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private DepartmentUserJobService departmentUserJobService;
    @Autowired
    private UserPropertyService userPropertyService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#parserUnit(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public Document parserUnit(String type, String all, String login, String filterCondition,
                               HashMap<String, String> filterDisplayMap) {
        return parseDepartmentUser(all, login, filterDisplayMap);
    }

    /**
     * all=2(全部树型节点) all=1(当只有一个根节点时多返回一级子节点，否则返回多个根节点即可) all=0(返回包括给定节点和其直接子节点)
     *
     * @param all
     * @param login
     * @return
     */
    private Document parseDepartmentUser(String all, String login,
                                         HashMap<String, String> filterDisplayMap) {
        // 我所属的顶级部门
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        Document document = DocumentHelper.createDocument();
        Department department = userService.getTopDepartment(user.getUserUuid());
        Element root = null;
        if (department.getIsVisible() == true) {
            root = document.addElement(NODE_UNITS);
        }

        if (department != null) {
            if (all.equals(ALL_FULL)) {
                buildFullDepartmentUserXML(root, department, "", login, filterDisplayMap);
            } else if (all.equals(ALL_SELF)) {
                buildSelfDepartmentUserXML(root, department, "", login, filterDisplayMap);
            }
        }
        return document;
    }

    /**
     * @param element
     * @param department
     * @param string
     * @param login
     */
    protected void buildFullDepartmentUserXML(Element element, Department department, String prefix,
                                              String login,
                                              HashMap<String, String> filterDisplayMap) {
        String deptPath = StringUtils.isEmpty(prefix) ? prefix + department.getName() : prefix + "/"
                + department.getName();
        Element root = null;
        if (department.getIsVisible() == true) {
            root = element.addElement(NODE_UNITS);

            // 设置部门
            String isLeaf = FALSE;
            setDepartmentElement(root, department, isLeaf, deptPath, filterDisplayMap);

            // 获取部门下的用户
            List<DepartmentUserJob> departmentUserJobs = departmentUserJobService.getByDepartment(
                    department.getUuid());
            for (DepartmentUserJob departmentUserJob : departmentUserJobs) {
                Element child = root.addElement(NODE_UNIT);
                User user = departmentUserJob.getUser();
                String userPath = deptPath + "/" + user.getUserName();
                // 设置部门下的用户信息
                if (LOGIN_TRUE.equals(login)) {
                    setDepartmentUserJobElement(child, user, userPath,
                            departmentUserJob.getJobName(), filterDisplayMap);
                } else {
                    setDepartmentUserJobElement(child, user, userPath,
                            departmentUserJob.getJobName(), filterDisplayMap);
                }
            }

            // 递归遍历子结点
            for (Department dept : department.getChildren()) {
                Element child = root;
                // root.addElement(NODE_UNIT);
                buildFullDepartmentUserXML(child, dept, deptPath, login, filterDisplayMap);
            }
        }
    }

    /**
     * 如何描述该方法
     *
     * @param element
     * @param user
     * @param path
     */
    protected void setDepartmentUserJobElement(Element element, User user, String path, String job,
                                               HashMap<String, String> filterDisplayMap) {
        // element.addAttribute(ATTRIBUTE_TYPE, ATTRIBUTE_TYPE_USER);
        // element.addAttribute(ATTRIBUTE_ISLEAF, "1");
        // element.addAttribute(ATTRIBUTE_ID,
        // user.getId()+","+user.getLoginName());
        // element.addAttribute(ATTRIBUTE_NAME, user.getUserName());
        // element.addAttribute(ATTRIBUTE_PATH, path);
        // element.addAttribute(ATTRIBUTE_SEX, user.getSex());
        // element.addAttribute(ATTRIBUTE_JOB, user.getUserName());
        setDepartmentUserJobElement(element, ATTRIBUTE_TYPE_USER, "1", user.getId(),
                user.getUserName(), path,
                user.getSex(), job, getUserEmail(user), user.getEmployeeNumber(),
                user.getLoginName(), "",
                filterDisplayMap);
    }

    protected void setDepartmentUserJobElement(Element element, User user, String type, String path,
                                               String job,
                                               String title,
                                               HashMap<String, String> filterDisplayMap) {
        setDepartmentUserJobElement(element, ATTRIBUTE_TYPE_USER, type, user.getId(),
                user.getUserName(), path,
                user.getSex(), job, getUserEmail(user), user.getEmployeeNumber(),
                user.getLoginName(), title,
                filterDisplayMap);

    }

    protected void setDepartmentUserJobElement(Element element, String type, String isLeaf,
                                               String id, String name,
                                               String path, String sex, String job, String email,
                                               String employeenumber, String loginname,
                                               String title,
                                               HashMap<String, String> filterDisplayMap) {
        if (filterDisplayMap.get(id) != null)
            return;
        element.addAttribute(ATTRIBUTE_TYPE, type);
        element.addAttribute(ATTRIBUTE_ISLEAF, isLeaf);
        element.addAttribute(ATTRIBUTE_ID, id);
        element.addAttribute(ATTRIBUTE_NAME, name);
        element.addAttribute(ATTRIBUTE_PATH, path);
        element.addAttribute(ATTRIBUTE_SEX, sex);
        element.addAttribute(ATTRIBUTE_JOB, job);
        // 增加邮件和员工号 add byzky 20140922
        element.addAttribute(ATTRIBUTE_EMAIL, email);
        element.addAttribute(ATTRIBUTE_EMPLOYEENUMBER, employeenumber);
        element.addAttribute(ATTRIBUTE_LOGINNAME, loginname);

        if (StringUtils.isNotBlank(title)) {
            element.addAttribute(ATTRIBUTE_TITLE, title);
        }
    }

    /**
     * @param element
     * @param department
     * @param string
     * @param login
     */
    protected void buildSelfDepartmentUserXML(Element element, Department department, String prefix,
                                              String login,
                                              HashMap<String, String> filterDisplayMap) {
        String deptPath = prefix + department.getName();

        Element root = element.addElement(NODE_UNIT);
        // 设置部门
        String isLeaf = FALSE;
        setDepartmentElement(root, department, isLeaf, deptPath, filterDisplayMap);

        // 获取部门下的用户
        List<DepartmentUserJob> departmentUserJobs = departmentUserJobService.getByDepartment(
                department.getUuid());
        for (DepartmentUserJob departmentUserJob : departmentUserJobs) {
            Element child = root.addElement(NODE_UNIT);
            User user = departmentUserJob.getUser();
            // 设置部门下的用户信息
            String userPath = deptPath + "/" + user.getUserName();
            if (LOGIN_TRUE.equals(login)) {
                setDepartmentUserJobElement(child, user, userPath, departmentUserJob.getJobName(),
                        filterDisplayMap);
            } else {
                setDepartmentUserJobElement(child, user, userPath, departmentUserJob.getJobName(),
                        filterDisplayMap);
            }
        }

        for (Department dept : department.getChildren()) {
            Element child = root.addElement(NODE_UNIT);
            // 设置部门
            setDepartmentElement(child, dept, isLeaf, deptPath + "/" + dept.getName(),
                    filterDisplayMap);

            // // 获取部门下的用户
            // List<DepartmentUserJob> departmentUserJobs2 =
            // departmentUserJobDao
            // .getByDepartment(dept.getUuid());
            // for (DepartmentUserJob departmentUserJob : departmentUserJobs2) {
            // Element userElement = child.addElement(NODE_UNIT);
            // User user = departmentUserJob.getUser();
            // String userPath = deptPath + "/" + dept.getName() + "/"
            // + user.getUserName();
            // // 设置部门下的用户信息
            // if (LOGIN_TRUE.equals(login)) {
            // setDepartmentUserJobElement(userElement, user, userPath);
            // } else {
            // setDepartmentUserJobElement(userElement, user, userPath);
            // }
            // }
            //
            // // 获取部门下的部门
            // for (Department childDept : dept.getChildren()) {
            // Element childDeptElement = child.addElement(NODE_UNIT);
            // // 设置部门
            // setDepartmentElement(
            // childDeptElement,
            // childDept,
            // isLeaf,
            // deptPath + "/" + dept.getName() + "/"
            // + childDept.getName());
            // }
        }
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#toggleUnit(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document toggleUnit(String type, String id, String all, String login,
                               HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        Department department = this.departmentService.getById(id);
        Element root = document.addElement(NODE_UNITS);
        buildSelfDepartmentUserXML(root, department, "", login, filterDisplayMap);
        return document;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#leafUnit(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document leafUnit(String type, String id, String leafType, String login,
                             HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        Department department = this.departmentService.getById(id);
        Element root = document.addElement(NODE_UNITS);
        buildLeftDepartmentUserXML(root, department, leafType, login, filterDisplayMap);
        return document;
    }

    /**
     * Description how to use this method
     *
     * @param root
     * @param department
     * @param string
     * @param login
     */
    private void buildLeftDepartmentUserXML(Element root, Department department, String leafType,
                                            String login,
                                            HashMap<String, String> filterDisplayMap) {
        List<Department> children = department.getChildren();
        // 末端结点，添加用户
        // if (children.isEmpty()) {
        // 获取部门下的用户
        List<DepartmentUserJob> departmentUserJobs = departmentUserJobService.getByDepartment(
                department.getUuid());
        for (DepartmentUserJob departmentUserJob : departmentUserJobs) {
            Element child = root.addElement(NODE_UNIT);
            User user = departmentUserJob.getUser();
            // 设置部门下的用户信息
            String userPath = department.getName() + "/" + user.getUserName();
            if (LOGIN_TRUE.equals(login)) {
                setDepartmentUserJobElement(child, user, "1", userPath,
                        departmentUserJob.getJobName(), "",
                        filterDisplayMap);
            } else {
                setDepartmentUserJobElement(child, user, "1", userPath,
                        departmentUserJob.getJobName(), "",
                        filterDisplayMap);
            }
        }
        // }
        for (Department childDept : children) {
            buildLeftDepartmentUserXML(root, childDept, leafType, login, filterDisplayMap);
        }
    }

    /**
     * 解析我的单位组织树
     *
     * @param treeId
     * @return
     */
    public List<TreeNode> parseUnitTree(String treeId) {
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        // 我所属的顶级部门
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        Department department = null;
        if (TreeNode.ROOT_ID.equals(treeId)) {
            department = userService.getTopDepartment(user.getUserUuid());
            // 顶级单位展开
            TreeNode treeNode = new TreeNode();
            treeNode.setId(department.getId());
            treeNode.setName(department.getName());
            treeNode.setIsParent(true);
            treeNode.setOpen(true);
            treeNodes.add(treeNode);
            addDepartmentUser(department, treeNode.getChildren());
        } else {
            if (treeId != null && treeId.startsWith(IdPrefix.DEPARTMENT.getValue())) {
                department = this.departmentService.getById(treeId);
                addDepartmentUser(department, treeNodes);
            }
        }
        return treeNodes;
    }

    /**
     * 解析我的单位组织树
     *
     * @param department
     * @param treeNodes
     */
    private void addDepartmentUser(Department department, List<TreeNode> treeNodes) {
        if (department == null) {
            return;
        }
        // 获取部门下的用户
        List<DepartmentUserJob> departmentUserJobs = departmentUserJobService.getByDepartment(
                department.getUuid());
        for (DepartmentUserJob departmentUserJob : departmentUserJobs) {
            User user = departmentUserJob.getUser();
            // 设置部门下的用户信息
            TreeNode treeNode = new TreeNode();
            treeNode.setId(user.getId());
            treeNode.setName(user.getUserName());
            treeNodes.add(treeNode);
        }

        // 递归遍历子结点
        for (Department dept : department.getChildren()) {
            TreeNode treeNode = new TreeNode();
            treeNode.setId(dept.getId());
            treeNode.setName(dept.getName());
            treeNode.setIsParent(true);
            treeNodes.add(treeNode);
        }
    }

    /**
     * 如何描述该方法
     *
     * @param type
     * @param all
     * @param login
     * @param searchValue
     */
    public Document searchXml(String type, String all, String login, String searchValue,
                              HashMap<String, String> filterDisplayMap) {
        // QueryInfo queryInfo = new QueryInfo();
        // List<PropertyFilter> propertyFilters = new
        // ArrayList<PropertyFilter>();
        // PropertyFilter propertyFilter = new
        // PropertyFilter("LIKES_loginName_OR_userName", searchValue);
        // propertyFilters.add(propertyFilter);
        // queryInfo.setPropertyFilters(propertyFilters);
        // queryInfo.setPagingInfo(new PagingInfo(1, Integer.MAX_VALUE));
        // queryInfo.setQueryType("user");
        // queryInfo.setOrderBy("code asc");
        // QueryData queryData = commonQueryService.query(queryInfo);
        // List<User> dataList = (List<User>) queryData.getDataList();
		/*	Map<String, Object> values = new HashMap<String, Object>();
			values.put("loginName", searchValue);
			values.put("userName", searchValue);
			values.put("pinyin", searchValue);*/
		/*	List<User> dataList = this.userDao.namedQuery("userPinyinQuery", values, User.class, new PagingInfo(1,
					Integer.MAX_VALUE));*/

        Query query = this.userService.createSQLQuery(createSearchSql(searchValue));
        List idList = query.list();

        List<User> dataList = userService.getByIds(idList);
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        for (User user : dataList) {
            User u = userService.get(user.getUuid());
            Set<DepartmentUserJob> departmentUserJobSet = u.getDepartmentUsers();
            for (DepartmentUserJob departmentUserJob : departmentUserJobSet) {
                Element child = root.addElement(NODE_UNIT);
                String userPath = departmentService.getFullPath(
                        departmentUserJob.getDepartment().getUuid()) + "/"
                        + user.getUserName();
                // setDepartmentUserJobElement(child, user, userPath,
                // departmentUserJob.getJobName());
                setDepartmentUserJobElement(child, ATTRIBUTE_TYPE_USER, "1", user.getId(), userPath,
                        userPath,
                        user.getSex(), user.getJobName(), getUserEmail(user),
                        user.getEmployeeNumber(),
                        user.getLoginName(), "", filterDisplayMap);
            }
        }

        return document;
    }

    /**
     * 获得用户email
     *
     * @param user
     * @return
     */
    protected String getUserEmail(User user) {
        return this.userPropertyService.getUserPropertyValue(UserProperty.KEY_EMAIL,
                user.getUuid());

    }

    private String createSearchSql(String searchValue) {
        StringBuilder querySql = new StringBuilder();
        // select a.id from org_user a where a.login_name like'%zhangys%' or
        // a.user_name like '%zhangys%' union select a.id from org_user a where
        // exists (select b.entity_uuid from cd_pinyin b where
        // b.entity_uuid=a.uuid and b.type='User' and (b.pinyin like
        // '%zhangys%'))
        querySql.append("select a.id from org_user a where a.login_name like");
        querySql.append("'%").append(searchValue).append("%'");
        querySql.append(" or a.user_name like ").append("'%").append(searchValue).append("%'");
        querySql.append(" union ");
        querySql.append(" select a.id from org_user a where exists ");
        querySql.append(
                " (select b.entity_uuid from cd_pinyin b where b.entity_uuid=a.uuid and b.type='User'");
        querySql.append(" and (b.pinyin like ").append("'%").append(searchValue).append("%'))");
        return querySql.toString();
    }
}
