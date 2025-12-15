package com.wellsoft.pt.org.unit.service.impl;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.pt.org.dao.DepartmentUserJobDao;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.service.DutyService;
import com.wellsoft.pt.org.service.JobService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.wellsoft.pt.org.unit.service.impl.UnitTreeServiceImpl.*;

/**
 * 混合型组织框
 *
 * @author xieming
 */
@Service
@Transactional
public class UnitTreeMixtrueTypeDelegateServiceImpl extends UnitTreeTypeDelegateService {
    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentUserJobDao departmentUserJobDao;
    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private JobService jobService;
    @Autowired
    private DutyService dutyService;

    @Override
    public Document parserUnit(String type, String all, String login, String filterContion,
                               HashMap<String, String> filterDisplayMap) {
        return mixtureOrgXml(filterContion.split(","), filterDisplayMap);
    }

    @Override
    public Document toggleUnit(String type, String id, String all, String login,
                               HashMap<String, String> filterDisplayMap) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Document leafUnit(String type, String id, String leafType, String login,
                             HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        Set<User> users = new HashSet<User>();
        if (id.startsWith(IdPrefix.USER.getValue())) {// U 用户
            User user = userService.getById(id);
            if (user != null) {
                users.add(user);
            }
        } else if (id.startsWith(IdPrefix.DEPARTMENT.getValue())) { // D 部门
            List<User> userdeptls = departmentService.getAllUsersByDepartmentId(id);
            users.addAll(userdeptls);
        } else if (id.startsWith("J")) {// J 职位
            List<User> userjobls = jobService.getUsersByJobId(id);
            users.addAll(userjobls);
        } else if (id.startsWith(IdPrefix.GROUP.getValue())) {// G 群组
            // List<User> userGroupls = groupService.getAllUsersByGroupId(id);
            // users.addAll(userGroupls);
        } else if (id.startsWith("W")) {// W 职务
            List<User> userDutyls = dutyService.getUsersByDutyId(id);
            users.addAll(userDutyls);
        }
        for (User user : users) {
            if (user.getEnabled()) {
                addUserElement(root, user, "", false, filterDisplayMap);
            }
        }
        return document;
    }

    public Document searchXml(String type, String all, String login, String searchValue, String filterContion,
                              HashMap<String, String> filterDisplayMap) {
        return mixtureSearchOrgXml(filterContion.split(","), searchValue, filterDisplayMap);
    }

    /**
     * 根据组织机构ID和搜索条件初始化一个混合类型的组织树
     * 以本身为起点，人员为终点。这点和组织选择框中的层级有差异
     * 搜索策略：
     * 除了人员以Id,Name,Pinyin的模糊匹配作为条件，其他都是以Id,Name作为匹配条件
     * 每次搜索到满足条件的组织或者人员，作为一个根节点，展示其所有子节点。
     *
     * @param orgIds
     * @param searchValue
     * @return
     */
    private Document mixtureSearchOrgXml(String[] orgIds, String searchValue, HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);

        if (orgIds != null) {

            for (String orgId : orgIds) {
                if (orgId.startsWith(IdPrefix.USER.getValue())) {// U 用户直接取用户
                    User user = userService.getById(orgId);
                    if (user != null && user.getEnabled()) {
                        addUserElementForSearch(root, user, "", searchValue, filterDisplayMap);
                    }
                } else if (orgId.startsWith(IdPrefix.DEPARTMENT.getValue())) { // D
                    // 部门：按部门-职位-人员显示
                    Department department = departmentService.getById(orgId);
                    if (department != null) {
                        buildDepartmentJobUserXMLForSearch(root, department, "", searchValue, filterDisplayMap);
                    }
                } else if (orgId.startsWith("J")) {// J 职位：按：职位-人员显示
                    Job job = jobService.getById(orgId);
                    if (job != null) {
                        buildUserJobXmlForSearch(root, job, "", searchValue, filterDisplayMap);
                    }
                } else if (orgId.startsWith(IdPrefix.GROUP.getValue())) {// G
                    // 群组：层次机构参考其他。
                    // Group
                    // group
                    // =
                    // groupService.getById(orgId);
                    // if (group != null) {
                    // buildToggleGroupXMLForSearch(root, group, "",
                    // searchValue, filterDisplayMap);
                    // }
                } else if (orgId.startsWith("W")) {// W 职务 ：按：职务-职位-人员显示
                    Duty duty = dutyService.getById(orgId);
                    if (duty != null) {
                        buildDutyUserXMLForSearch(root, duty, "", searchValue, filterDisplayMap);
                    }

                }
            }
        }
        return document;
    }

    /**
     * 根据组织机构ID初始化一个混合类型的组织树
     * 以本身为起点，人员为终点。这点和组织选择框中的层级有差异
     *
     * @param orgIds
     * @return
     */
    private Document mixtureOrgXml(String[] orgIds, HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);

        if (orgIds != null) {
            for (String orgId : orgIds) {
                if (orgId.startsWith(IdPrefix.USER.getValue())) {// // U 用户直接取用户
                    User user = userService.getById(orgId);
                    if (user != null && user.getEnabled()) {
                        addUserElement(root, user, "", false, filterDisplayMap);
                    }
                } else if (orgId.startsWith(IdPrefix.DEPARTMENT.getValue())) { // D
                    // 部门：按：部门-职位-人员显示
                    Department department = departmentService.getById(orgId);
                    if (department != null) {
                        buildDepartmentJobUserXML(root, department, "", false, filterDisplayMap);
                    }
                } else if (orgId.startsWith("J")) {// J 职位：按：职位-人员显示
                    Job job = jobService.getById(orgId);
                    if (job != null) {
                        buildUserJobXml(root, job, "", false, filterDisplayMap);
                    }
                } else if (orgId.startsWith(IdPrefix.GROUP.getValue())) {// G
                    // 群组：群组嵌套其他类型。具体层级关系查看其他类型
                    // Group
                    // group
                    // =
                    // groupService.getById(orgId);
                    // if (group != null) {
                    // buildToggleGroupXML(root, group, "", false,
                    // filterDisplayMap);
                    // }
                } else if (orgId.startsWith("W")) {// W 职务 ：按职务-职位-人员显示
                    Duty duty = dutyService.getById(orgId);
                    if (duty != null) {
                        buildDutyUserXML(root, duty, "", false, filterDisplayMap);
                    }
                }
            }
        }
        return document;
    }

    /**
     * 构架职位人员树（有搜索条件）
     *
     * @param root
     * @param job
     * @param prefix
     * @param searchValue
     */
    private void buildUserJobXmlForSearch(Element root, Job job, String prefix, String searchValue,
                                          HashMap<String, String> filterDisplayMap) {
        String jobPath = StringUtils.isEmpty(prefix) ? prefix + job.getName() : prefix + "/" + job.getName();
        if (job.getName().indexOf(searchValue) >= 0 || job.getId().indexOf(searchValue) >= 0) {
            buildUserJobXml(root, job, prefix, true, filterDisplayMap);
        }
        // 获取职位下的用户
        Set<UserJob> userJobs = job.getJobUsers();
        for (UserJob userJob : userJobs) {
            User user = userJob.getUser();
            if (user.getEnabled()) {
                addUserElementForSearch(root, user, jobPath, searchValue, filterDisplayMap);
            }
        }
    }

    /**
     * 构架职位人员树
     *
     * @param root
     * @param job
     * @param prefix
     * @param showPath
     */
    private void buildUserJobXml(Element root, Job job, String prefix, boolean showPath, HashMap filterDisplayMap) {
        String jobPath = StringUtils.isEmpty(prefix) ? prefix + job.getName() : prefix + "/" + job.getName();
        Element child = root.addElement(NODE_UNIT);
        String JobIsLeaf = TRUE;
        addElement(child, job.getId(), showPath ? jobPath : job.getName(), JobIsLeaf, jobPath,
                UnitTreeServiceImpl.ATTRIBUTE_TYPE_JOB, "", filterDisplayMap);
        // 获取职位下的用户
        Set<UserJob> userJobs = job.getJobUsers();
        for (UserJob userJob : userJobs) {
            User user = userJob.getUser();
            if (user.getEnabled()) {
                JobIsLeaf = FALSE;
                addUserElement(child, user, jobPath, false, filterDisplayMap);
            }
        }
        child.addAttribute(ATTRIBUTE_ISLEAF, JobIsLeaf);
    }

    /**
     * 构架部门职位人员树（有搜索条件）
     *
     * @param element
     * @param department
     * @param prefix
     * @param searchValue
     */
    protected void buildDepartmentJobUserXMLForSearch(Element element, Department department, String prefix,
                                                      String searchValue, HashMap<String, String> filterDisplayMap) {
        // String deptPath = StringUtils.isEmpty(prefix) ? prefix +
        // department.getName() : prefix + "/"
        // + department.getName();
        String deptPath = departmentService.getFullPath(department.getId());
        if (department.getIsVisible()) {
            if (department.getName().indexOf(searchValue) >= 0 || department.getId().indexOf(searchValue) >= 0) {
                buildDepartmentJobUserXML(element, department, prefix, true, filterDisplayMap);
            }

            List<Job> jobs = jobService.getJobByDeptUuid(department.getUuid());
            for (Job job : jobs) {
                buildUserJobXmlForSearch(element, job, deptPath, searchValue, filterDisplayMap);
            }

        }
    }

    /**
     * 构架部门职位人员树
     *
     * @param element
     * @param department
     * @param string
     * @param login
     */
    protected void buildDepartmentJobUserXML(Element element, Department department, String prefix, boolean showPath,
                                             HashMap<String, String> filterDisplayMap) {
        // String deptPath = StringUtils.isEmpty(prefix) ? prefix +
        // department.getName() : prefix + "/"
        // + department.getName();
        String deptPath = departmentService.getFullPath(department.getId());
        if (department.getIsVisible()) {
            Element root = element.addElement(NODE_UNIT);
            // 设置部门
            String isLeaf = FALSE;
            addElement(root, department.getId(), showPath ? deptPath : department.getName(), isLeaf, deptPath,
                    UnitTreeServiceImpl.ATTRIBUTE_TYPE_DEP, "", filterDisplayMap);
            // 设置部门下的职位
            List<Job> jobs = jobService.getJobByDeptUuid(department.getUuid());
            for (Job job : jobs) {
                buildUserJobXml(root, job, deptPath, false, filterDisplayMap);
            }
            // 递归遍历子结点
            for (Department dept : department.getChildren()) {
                buildDepartmentJobUserXML(root, dept, deptPath, false, filterDisplayMap);
            }
        }
    }

    /**
     * 构架群组部门职位人员下拉树（有搜索条件）
     * @param element
     * @param group
     * @param prefix
     * @param searchValue
     */
    // protected void buildToggleGroupXMLForSearch(Element element, Group group,
    // String prefix, String searchValue,
    // HashMap<String, String> filterDisplayMap) {
    // if (group.getId().indexOf(searchValue) >= 0 ||
    // group.getName().indexOf(searchValue) >= 0) {
    // buildToggleGroupXML(element, group, prefix, true, filterDisplayMap);
    // }
    // String path = StringUtils.isEmpty(prefix) ? prefix + group.getName() :
    // prefix + "/" + group.getName();
    // // 获取群组下人员
    // Set<User> users = group.getUsers();
    // for (User user : users) {
    // if (user.getEnabled()) {
    // addUserElementForSearch(element, user, path, searchValue,
    // filterDisplayMap);
    // }
    // }
    //
    // // 获取群组下部门
    // Set<Department> departments = group.getDepartments();
    // for (Department department : departments) {
    // buildDepartmentJobUserXMLForSearch(element, department, path,
    // searchValue, filterDisplayMap);
    // }
    //
    // // 获取群组下职务
    // Set<Duty> dutys = group.getDutys();
    // for (Duty duty : dutys) {
    // buildDutyUserXMLForSearch(element, duty, path, searchValue,
    // filterDisplayMap);
    // }
    //
    // // 获取群组下的岗位
    // Set<Job> jobs = group.getJobs();
    // for (Job job : jobs) {
    // buildUserJobXmlForSearch(element, job, path, searchValue,
    // filterDisplayMap);
    // }
    //
    // // 获取群组下群组
    // Set<GroupNestedGroup> groupNestedGroups = group.getNestedGroups();
    // for (GroupNestedGroup groupNestedGroup : groupNestedGroups) {
    // Group childGroup = groupNestedGroup.getNestedGroup();
    // buildToggleGroupXMLForSearch(element, childGroup, searchValue, path,
    // filterDisplayMap);
    // }
    // }

    /**
     * 构架群组部门职位人员下拉树
     * @param element
     * @param group
     * @param prefix
     * @param showPath
     */
    // protected void buildToggleGroupXML(Element element, Group group, String
    // prefix, boolean showPath,
    // HashMap<String, String> filterDisplayMap) {
    // String path = StringUtils.isEmpty(prefix) ? prefix + group.getName() :
    // prefix + "/" + group.getName();
    // Element child = element.addElement(NODE_UNIT);
    // addElement(child, group.getId(), showPath ? path : group.getName(),
    // FALSE, path,
    // UnitTreeServiceImpl.ATTRIBUTE_TYPE_GROUP, "", filterDisplayMap);
    //
    // // 获取群组下人员
    // Set<User> users = group.getUsers();
    // for (User user : users) {
    // if (user.getEnabled()) {
    // addUserElement(child, user, path, false, filterDisplayMap);
    // }
    // }
    //
    // // 获取群组下部门
    // Set<Department> departments = group.getDepartments();
    // for (Department department : departments) {
    // buildDepartmentJobUserXML(child, department, "", true, filterDisplayMap);
    // }
    //
    // // 获取群组下职务
    // Set<Duty> dutys = group.getDutys();
    // for (Duty duty : dutys) {
    // buildDutyUserXML(child, duty, path, false, filterDisplayMap);
    // }
    //
    // // 获取群组下的岗位
    // Set<Job> jobs = group.getJobs();
    // for (Job job : jobs) {
    // buildUserJobXml(child, job, "", false, filterDisplayMap);
    // }
    //
    // // 获取群组下群组
    // Set<GroupNestedGroup> groupNestedGroups = group.getNestedGroups();
    // for (GroupNestedGroup groupNestedGroup : groupNestedGroups) {
    // Group childGroup = groupNestedGroup.getNestedGroup();
    // buildToggleGroupXML(child, childGroup, path, false, filterDisplayMap);
    // }
    // }

    /**
     * 职务人员树（有搜索条件）
     *
     * @param element
     * @param duty
     * @param dutyPath
     * @param showPath
     */
    private void buildDutyUserXMLForSearch(Element element, Duty duty, String prefix, String searchValue,
                                           HashMap<String, String> filterDisplayMap) {
        String path = StringUtils.isEmpty(prefix) ? prefix + duty.getName() : prefix + "/" + duty.getName();
        if (duty.getName().indexOf(searchValue) >= 0 || duty.getId().indexOf(searchValue) >= 0) {
            buildDutyUserXML(element, duty, prefix, true, filterDisplayMap);
        }
        for (Job job : duty.getJobs()) {
            buildUserJobXmlForSearch(element, job, path, searchValue, filterDisplayMap);
        }
    }

    /**
     * 职务人员树
     *
     * @param element
     * @param duty
     * @param dutyPath
     * @param showPath
     */
    private void buildDutyUserXML(Element element, Duty duty, String prefix, boolean showPath,
                                  HashMap<String, String> filterDisplayMap) {
        String path = StringUtils.isEmpty(prefix) ? prefix + duty.getName() : prefix + "/" + duty.getName();
        String isLeaf = duty.getJobs().size() > 0 ? FALSE : TRUE;
        Element child = element.addElement(NODE_UNIT);
        addElement(child, duty.getId(), (showPath ? path : duty.getName()) + "【" + duty.getDutyLevel() + "】", isLeaf,
                path, ATTRIBUTE_TYPE_DUTY, "", filterDisplayMap);
        for (Job job : duty.getJobs()) {
            buildUserJobXml(child, job, path, false, filterDisplayMap);
        }
    }

    /**
     * 添加用户节点（有搜索条件）
     *
     * @param root
     * @param user
     * @param prefix
     * @param searchValue
     * @param showPath
     */
    private void addUserElementForSearch(Element root, User user, String prefix, String searchValue,
                                         HashMap<String, String> filterDisplayMap) {
        if (user.getId().indexOf(searchValue) >= 0 || user.getUserName().indexOf(searchValue) >= 0
                || checkPinyin(user.getUuid(), searchValue)) {
            addUserElement(root, user, prefix, true, filterDisplayMap);
        }
    }

    /**
     * 添加用户阶段
     *
     * @param root
     * @param user
     * @param prefix
     * @param showPath
     */
    private void addUserElement(Element root, User user, String prefix, boolean showPath,
                                HashMap<String, String> filterDisplayMap) {
        String path = StringUtils.isEmpty(prefix) ? prefix + user.getUserName() : prefix + "/" + user.getUserName();
        Element child = root.addElement(NODE_UNIT);
        addElement(child, user.getId(), showPath ? path : user.getUserName(), TRUE, path,
                UnitTreeServiceImpl.ATTRIBUTE_TYPE_USER, user.getSex(), filterDisplayMap);
    }

    private void addElement(Element element, String id, String name, String isLeaf, String path, String type,
                            String sex, HashMap<String, String> filterDisplayMap) {
        if (filterDisplayMap.get(id) != null)
            return;
        element.addAttribute(ATTRIBUTE_TYPE, type);
        element.addAttribute(ATTRIBUTE_ISLEAF, isLeaf);
        element.addAttribute(ATTRIBUTE_ID, id);
        element.addAttribute(ATTRIBUTE_NAME, name);
        element.addAttribute(ATTRIBUTE_PATH, path);
        if (!StringUtils.isEmpty(sex)) {
            element.addAttribute(ATTRIBUTE_SEX, sex);
        }
    }

    private boolean checkPinyin(String uuid, String searchValue) {
        boolean rel = false;
        if (searchValue.matches("[a-zA-Z]+")) {// 先判断searchValue 是不是拼音。减少数据库资源浪费
            // Query query =
            // this.userService.e(createSearchSql(uuid,searchValue));
            // return query.list().size()>0;
        }
        return rel;
    }

    /**
     * 查询满足条件的所有人员
     *
     * @param searchValue
     * @return
     */
    public String createSearchSql(String uuid, String searchValue) {
        StringBuilder querySql = new StringBuilder();
        querySql.append(" select b.entity_uuid from cd_pinyin b where b.entity_uuid='").append(uuid)
                .append("' and b.type='User'");
        querySql.append(" and (b.pinyin like ").append("'%").append(searchValue).append("%')");
        return querySql.toString();
    }
}
