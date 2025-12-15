/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.service.impl;

import com.google.common.base.Objects;
import com.wellsoft.context.component.tree.TreeNode;
import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.org.dao.DepartmentDao;
import com.wellsoft.pt.org.dao.UserDao;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.service.DepartmentUserJobService;
import com.wellsoft.pt.org.service.JobService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.wellsoft.pt.org.unit.service.impl.UnitTreeServiceImpl.*;

/**
 * Description: 我的单位(职位)
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
public class UnitTreeMyUnit1TypeDelegateServiceImpl extends UnitTreeTypeDelegateService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private DepartmentUserJobService departmentUserJobService;
    @Autowired
    private JobService jobService;
    @Autowired
    private DepartmentService departmentService;

    private Logger logger = LoggerFactory.getLogger(UnitTreeMyUnit1TypeDelegateServiceImpl.class);

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
                               HashMap<String, String> filterDisplayList) {
        return parseDepartmentJobUser(all, login, filterDisplayList);
    }

    /**
     * all=2(全部树型节点) all=1(当只有一个根节点时多返回一级子节点，否则返回多个根节点即可) all=0(返回包括给定节点和其直接子节点)
     *
     * @param all
     * @param login
     * @return
     */
    protected Document parseDepartmentJobUser(String all, String login, HashMap<String, String> filterDisplayList) {
        // 我所属的顶级部门
        UserDetails user = SpringSecurityUtils.getCurrentUser();
        Department department = userService.getTopDepartment(user.getUserUuid());

        Document document = DocumentHelper.createDocument();

        Element root = document.addElement(NODE_UNITS);

        if (department != null) {
            if (all.equals(ALL_FULL)) {
                // throw new RuntimeException("不支持展开!");
                buildFullDepartmentJobUserXML(root, department, "", login, filterDisplayList);
            } else if (all.equals(ALL_SELF)) {
                buildSelfDepartmentJobUserXML(root, department, "", login, filterDisplayList);
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
    protected void buildFullDepartmentJobUserXML(Element element, Department department, String prefix, String login,
                                                 HashMap<String, String> filterDisplayMap) {
        String deptPath = StringUtils.isEmpty(prefix) ? prefix + department.getName() : prefix + "/"
                + department.getName();
        // System.out.println("111------------"+department.getIsVisible());
        if (department.getIsVisible() == true) {
            Element root = element.addElement(NODE_UNIT);
            // 设置部门
            String isLeaf = FALSE;
            setDepartmentElement(root, department, isLeaf, deptPath, filterDisplayMap);

            // 设置部门下的职位
            List<Job> jobs = jobService.getJobByDeptUuid(department.getUuid());
            for (Job job : jobs) {
                Element child = root.addElement(NODE_UNIT);
                String jobPath = deptPath + "/" + job.getName();
                // 设置部门下的岗位信息
                if (LOGIN_TRUE.equals(login)) {
                    setDepartmentUserJobElement1(child, job, jobPath, filterDisplayMap);
                } else {
                    setDepartmentUserJobElement1(child, job, jobPath, filterDisplayMap);
                }

                // 获取职位下的用户
                Set<UserJob> userJobs = job.getJobUsers();

                int userAccount = 0;
                for (UserJob userJob : userJobs) {
                    User user = userJob.getUser();
                    if (user.getEnabled() == false) {
                        continue;
                    }
                    Element child1 = root.addElement(NODE_UNIT);

                    String userPath = deptPath + "/" + userJob.getJob().getName() + "/" + user.getUserName();
                    // 设置部门下的用户信息
                    if (LOGIN_TRUE.equals(login)) {
                        setDepartmentUserJobElement(child1, user, userPath, "", filterDisplayMap);
                    } else {
                        setDepartmentUserJobElement(child1, user, userPath, "", filterDisplayMap);
                    }
                    userAccount = userAccount + 1;
                }

                if (userAccount > 0) {
                    child.addAttribute(ATTRIBUTE_ISLEAF, FALSE);
                } else {
                    child.addAttribute(ATTRIBUTE_ISLEAF, TRUE);
                }
            }

            // 递归遍历子结点
            for (Department dept : department.getChildren()) {

                Element child = root;// root.addElement(NODE_UNIT);
                buildFullDepartmentJobUserXML(child, dept, deptPath, login, filterDisplayMap);
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
    protected void setDepartmentUserJobElement1(Element element, Job job, String path,
                                                HashMap<String, String> filterDisplayMap) {
        setDepartmentJobElement1(element, ATTRIBUTE_TYPE_JOB, "0", job.getId(), job.getName(), path, "",
                filterDisplayMap);
    }

    protected void setDepartmentJobElement1(Element element, Job job, String type, String path, String title,
                                            HashMap<String, String> filterDisplayMap) {
        setDepartmentJobElement1(element, ATTRIBUTE_TYPE_JOB, type, job.getId(), job.getName(), path, title,
                filterDisplayMap);
    }

    protected void setDepartmentJobElement1(Element element, String type, String isLeaf, String id, String name,
                                            String path, String title, HashMap<String, String> filterDisplayMap) {
        if (filterDisplayMap.get(id) != null)
            return;
        element.addAttribute(ATTRIBUTE_TYPE, type);
        element.addAttribute(ATTRIBUTE_ISLEAF, isLeaf);
        element.addAttribute(ATTRIBUTE_ID, id);
        element.addAttribute(ATTRIBUTE_NAME, name);
        element.addAttribute(ATTRIBUTE_PATH, path);
        if (StringUtils.isNotBlank(title)) {
            element.addAttribute(ATTRIBUTE_TITLE, title);
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
        setDepartmentUserJobElement(element, ATTRIBUTE_TYPE_USER, "1", user.getId(), user.getUserName(), path,
                user.getSex(), job, getUserEmail(user), user.getEmployeeNumber(), user.getLoginName(), "",
                filterDisplayMap);
    }

    protected void setDepartmentUserJobElement(Element element, User user, String type, String path, String job,
                                               String title, HashMap<String, String> filterDisplayMap) {
        setDepartmentUserJobElement(element, ATTRIBUTE_TYPE_USER, type, user.getId(), user.getUserName(), path,
                user.getSex(), job, getUserEmail(user), user.getEmployeeNumber(), user.getLoginName(), title,
                filterDisplayMap);

    }

    protected void setDepartmentUserJobElement(Element element, String type, String isLeaf, String id, String name,
                                               String path, String sex, String job, String email, String employeenumber, String loginname, String title,
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
    protected void buildSelfDepartmentJobUserXML(Element element, Department department, String prefix, String login,
                                                 HashMap<String, String> filterDisplayMap) {
        // String deptPath = prefix + department.getName();

        if (department.getIsVisible() == true) {
            String deptPath = departmentService.getFullPath(department.getId());
            Element root = element.addElement(NODE_UNIT);
            // 设置部门
            String isLeaf = FALSE;
            setDepartmentElement(root, department, isLeaf, deptPath, filterDisplayMap);

            // 设置部门下的职位
            List<Job> jobs = jobService.getJobByDeptUuid(department.getUuid());
            for (Job job : jobs) {
                Element child = root.addElement(NODE_UNIT);
                String jobPath = deptPath + "/" + job.getName();
                // 设置部门下的岗位信息
                if (LOGIN_TRUE.equals(login)) {
                    setDepartmentUserJobElement1(child, job, jobPath, filterDisplayMap);
                } else {
                    setDepartmentUserJobElement1(child, job, jobPath, filterDisplayMap);
                }

                // 获取职位下的用户
                Set<UserJob> userJobs = job.getJobUsers();

                int userAccount = 0;
                for (UserJob userJob : userJobs) {
                    User user = userJob.getUser();
                    if (user.getEnabled() == false) {
                        continue;
                    }
                    Element child1 = child.addElement(NODE_UNIT);

                    String userPath = deptPath + "/" + userJob.getJob().getName() + "/" + user.getUserName();
                    // 设置部门下的用户信息
                    if (LOGIN_TRUE.equals(login)) {
                        setDepartmentUserJobElement(child1, user, userPath, "", filterDisplayMap);
                    } else {
                        setDepartmentUserJobElement(child1, user, userPath, "", filterDisplayMap);
                    }
                    userAccount = userAccount + 1;
                }

                if (userAccount > 0) {
                    child.addAttribute(ATTRIBUTE_ISLEAF, FALSE);
                } else {
                    child.addAttribute(ATTRIBUTE_ISLEAF, TRUE);
                }
            }

            for (Department dept : department.getChildren()) {
                // 设置部门
                if (dept.getIsVisible() == true) {
                    Element child = root.addElement(NODE_UNIT);
                    setDepartmentElement(child, dept, isLeaf, deptPath + "/" + dept.getName(), filterDisplayMap);

                }
            }
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
        if (id.startsWith("D")) {
            Department department = this.departmentService.getById(id);
            Element root = document.addElement(NODE_UNITS);
            buildSelfDepartmentJobUserXML(root, department, "", login, filterDisplayMap);
        } else if (id.startsWith("J")) {
            /*
             * Element root = document.addElement(NODE_UNITS); //设置部门下的职位 Job
             * job = jobDao.getById(id); Element child =
             * root.addElement(NODE_UNIT); String jobPath =
             * job.getDepartmentName() + "/" + job.getName(); // 设置部门下的岗位信息 if
             * (LOGIN_TRUE.equals(login)) { setDepartmentUserJobElement1(child,
             * job, jobPath); } else { setDepartmentUserJobElement1(child, job,
             * jobPath); } // 获取职位下的用户 Set<UserJob> userJobs =
             * job.getJobUsers(); for (UserJob userJob : userJobs) { Element
             * child1 = child.addElement(NODE_UNIT); User user =
             * userJob.getUser(); String userPath = job.getDepartmentName() +
             * "/" + user.getUserName(); // 设置部门下的用户信息 if
             * (LOGIN_TRUE.equals(login)) { setDepartmentUserJobElement(child1,
             * user, userPath, ""); } else { setDepartmentUserJobElement(child1,
             * user, userPath, ""); } }
             */
        }
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
        long time1 = System.currentTimeMillis();
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        if (id.startsWith("D")) {
            Department department = this.departmentService.getById(id);
            buildLeftDepartmentUserXML(root, department, leafType, login, filterDisplayMap);
        } else if (id.startsWith("J")) {
            Job job = jobService.getById(id);
            Set<UserJob> userJobs = job.getJobUsers();
            List<UserJob> userJobList = new ArrayList<UserJob>();
            userJobList.addAll(userJobs);
            Collections.sort(userJobList, new Comparator<UserJob>() {
                @Override
                public int compare(UserJob o1, UserJob o2) {
                    String code1 = o1.getUser().getCode();
                    String code2 = o2.getUser().getCode();
                    if (StringUtils.isBlank(code1)) {
                        return 1;
                    }
                    if (StringUtils.isBlank(code2)) {
                        return 1;
                    }
                    return code1.compareTo(code2);
                }
            });
            for (UserJob userJob : userJobList) {
                User user = userJob.getUser();
                if (user.getEnabled() == false) {
                    continue;
                }
                Element child1 = root.addElement(NODE_UNIT);
                // 设置部门下的用户信息
                if (LOGIN_TRUE.equals(login)) {
                    setDepartmentUserJobElement(child1, user, user.getUserName(), "", filterDisplayMap);
                } else {
                    setDepartmentUserJobElement(child1, user, user.getUserName(), "", filterDisplayMap);
                }
            }
        }
        long time2 = System.currentTimeMillis();
        logger.info("load leafUnit id:[" + id + "]and Data spent " + (time2 - time1) / 1000.0 + "s");
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
    private void buildLeftDepartmentUserXML(Element root, Department department, String leafType, String login,
                                            HashMap<String, String> filterDisplayMap) {
        // 关联查询 速度优化.
        List<User> users = departmentService.getAllUserByDepartmentPath(department.getPath());
        Set<User> susers = new LinkedHashSet<User>();
        susers.addAll(users);// 去掉重复的.
        for (User user : susers) {
            if (user.getEnabled() == false) {
                continue;
            }
            Element child = root.addElement(NODE_UNIT);
            // 设置部门下的用户信息
            String userPath = department.getName() + "/" + user.getUserName();
            setDepartmentUserJobElement(child, user, "1", userPath, "", "", filterDisplayMap);
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
        List<DepartmentUserJob> departmentUserJobs = departmentUserJobService.getByDepartment(department.getUuid());
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
     * begin2015-01-15 @ yuyq
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
        // 我所属的顶级部门
        UserDetails currentUser = SpringSecurityUtils.getCurrentUser();
        Department myTopDepartment = userService.getTopDepartment(currentUser.getUserUuid());

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("code", searchValue);
        values.put("name", searchValue);
        values.put("pinyin", searchValue);
        /*
         * List<User> dataList = this.userDao.namedQuery("userPinyinQuery",
         * values, User.class, new PagingInfo(1, Integer.MAX_VALUE));
         */
        long time1 = System.currentTimeMillis();
        String rssearchValue = searchValue.toLowerCase();
        Query query = this.userService.createSQLQuery(createSearchSql(rssearchValue));
        List idList = query.list();
        List<User> dataList = userService.getByIds(idList);
        List<Department> deptList = new ArrayList<Department>();
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        if (dataList.size() == 0) {
            deptList = this.departmentService.namedQuery("departmentTreeQuery", values, Department.class);
            if (deptList.size() > 0) {
                for (Department department : deptList) {
                    String isLeaf = FALSE;

                    buildSelfDepartmentJobUserXML(root, department, "", "", filterDisplayMap);
                    // buildLeftDepartmentUserXML(root, department, "","");
                }
            } else {
                List<Job> deptListx = this.jobService.namedQuery("jobPinyinQuery", values, Job.class);
                for (Job job : deptListx) {
                    Department dept = departmentService.get(job.getDepartmentUuid());
                    if (dept != null) {
                        Job u = jobService.getByUuid(job.getUuid());
                        Element child = root.addElement(NODE_UNIT);
                        String jobPath = u.getDepartmentName() + "/" + u.getName();
                        String isLeaf = FALSE;
                        // 在搜索职位时往xml放部门信息，这是没必要的，会影响到js的解析判断
                        // setDepartmentElement(root, dept,
                        // isLeaf,u.getDepartmentName());
                        setDepartmentJobElement1(child, ATTRIBUTE_TYPE_JOB, "0", job.getId(), jobPath, jobPath, "",
                                filterDisplayMap);
                        // 获取职位下的用户
                        Set<UserJob> userJobs = job.getJobUsers();
                        for (UserJob userJob : userJobs) {
                            User user = userJob.getUser();
                            if (user.getEnabled() == false) {
                                continue;
                            }

                            Element child1 = child.addElement(NODE_UNIT);
                            String userPath = dept.getPath() + "/" + userJob.getJob().getName() + "/"
                                    + user.getUserName();
                            // 设置部门下的用户信息
                            if (LOGIN_TRUE.equals(login)) {
                                setDepartmentUserJobElement(child1, user, userPath, "", filterDisplayMap);
                            } else {
                                setDepartmentUserJobElement(child1, user, userPath, "", filterDisplayMap);
                            }

                        }
                    }
                }
            }
        }
        for (User user : dataList) {
            User u = userService.get(user.getUuid());
            if (u.getEnabled() == false) {
                continue;
            }
            // 按职位搜索
            Set<UserJob> userjobs = u.getUserJobs();
            // System.out.println(user.getUserName());
            for (UserJob userJob : userjobs) {
                Department dept = departmentService.get(userJob.getJob().getDepartmentUuid());
                Department topDept = departmentService.getTopDepartment(dept.getUuid());
                if (!Objects.equal(topDept, myTopDepartment)) {
                    continue; // 过滤非本单位的用户
                }
                if (dept.getIsVisible() == true) {
                    Element child = root.addElement(NODE_UNIT);
                    String userPath = dept.getPath() + "/" + userJob.getJob().getName() + "/" + user.getUserName();

                    // setDepartmentUserJobElement(child, user, userPath,
                    // departmentUserJob.getJobName());
                    setDepartmentUserJobElement(child, ATTRIBUTE_TYPE_USER, "1", user.getId(), userPath, userPath,
                            user.getSex(), user.getJobName(), getUserEmail(user), user.getEmployeeNumber(),
                            user.getLoginName(), "", filterDisplayMap);
                }
            }
        }
        long time2 = System.currentTimeMillis();
        logger.info("load searchXml searchValue:[" + searchValue + "]and Data spent " + (time2 - time1) / 1000.0 + "s");
        return document;
    }

    /**
     * 获得用户email
     *
     * @param user
     * @return
     */
    protected String getUserEmail(User user) {
        String email = user.getMainEmail();
        // 获得主邮件，取不到再取辅邮件.
        if (StringUtils.isEmpty(email)) {
            email = user.getOtherEmail();
        }
        return email;
    }

    public String createSearchSql(String searchValue) {
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
        querySql.append(" (select b.entity_uuid from cd_pinyin b where b.entity_uuid=a.uuid and b.type='User'");
        querySql.append(" and (b.pinyin like ").append("'%").append(searchValue).append("%'))");
        return querySql.toString();
    }
}
