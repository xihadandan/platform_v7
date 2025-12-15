/*
 * @(#)2013-2-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.service.impl;

import com.wellsoft.pt.org.dao.DepartmentDao;
import com.wellsoft.pt.org.dao.DepartmentUserJobDao;
import com.wellsoft.pt.org.dao.UserDao;
import com.wellsoft.pt.org.entity.*;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.service.JobService;
import com.wellsoft.pt.org.service.UserService;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
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
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-18.1	zhulh		2013-2-18		Create
 * </pre>
 * @date 2013-2-18
 */
@Service
@Transactional
public class UnitTreeMyDeptTypeDelegateServiceImpl extends UnitTreeMyUnit1TypeDelegateServiceImpl {
    @Autowired
    private UserDao userDao;
    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentDao departmentDao;
    @Autowired
    private DepartmentUserJobDao departmentUserJobDao;
    @Autowired
    private JobService jobService;
    @Autowired
    private DepartmentService departmentService;
    //部门uuid 过滤时使用
    private String[] departmentUuids;

    private Logger logger = LoggerFactory.getLogger(UnitTreeMyUnit1TypeDelegateServiceImpl.class);

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#parserUnit
     * (java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document parserUnit(String type, String all, String login, String filterCondtion,
                               HashMap<String, String> filterDisplayList) {
        return parseDepartmentUser(all, login, filterDisplayList);
    }

    /**
     * all=2(全部树型节点) all=1(当只有一个根节点时多返回一级子节点，否则返回多个根节点即可) all=0(返回包括给定节点和其直接子节点)
     *
     * @param all
     * @param login
     * @return
     */
    private Document parseDepartmentUser(String all, String login, HashMap<String, String> filterDisplayMap) {
        // 我所属的部门
        UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        Document document = DocumentHelper.createDocument();
        User user = userService.get(userDetails.getUserUuid());
        Set<DepartmentUserJob> departmentUserJobs = user.getDepartmentUsers();
        Element root = document.addElement(NODE_UNITS);
        List<String> departmentUuids = new ArrayList<String>();
        for (DepartmentUserJob departmentUserJob : departmentUserJobs) {
            Department department = departmentUserJob.getDepartment();
            List<Department> depts = this.departmentService.getLeafDepartments(department.getId());
            for (Department department2 : depts) {
                departmentUuids.add(department2.getUuid());
            }
            departmentUuids.add(department.getUuid());
            if (all.equals(ALL_FULL)) {
                super.buildFullDepartmentJobUserXML(root, department, "", login, filterDisplayMap);
            } else if (all.equals(ALL_SELF)) {
                super.buildSelfDepartmentJobUserXML(root, department, "", login, filterDisplayMap);
            }
        }
        this.departmentUuids = departmentUuids.toArray(new String[0]);
        return document;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#toggleUnit
     * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document toggleUnit(String type, String id, String all, String login,
                               HashMap<String, String> filterDisplayMap) {
        return super.toggleUnit(type, id, all, login, filterDisplayMap);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#leafUnit
     * (java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document leafUnit(String type, String id, String leafType, String login,
                             HashMap<String, String> filterDisplayMap) {
        return super.leafUnit(type, id, leafType, login, filterDisplayMap);
    }

    @Override
    public String createSearchSql(String searchValue) {
        StringBuilder querySql = new StringBuilder();
        //select a.id from org_user a,org_department_user_job b where (a.login_name like'%chen%' or a.user_name like '%chen%') and  a.uuid=b.user_uuid and b.department_uuid in ('93ec3033-38fe-4257-b161-2c5098f11d35','53e3cdff-36e9-4122-b264-64ae055923bd','3bfcd425-e096-4bf6-9e7d-c900d2eb2ff5','8911a9eb-65af-4fda-a9e4-f000834d5a85','6b38e257-3614-435e-8e71-2c9fbd837e54','fe3e3f91-9984-4baa-ac40-995a08763eaa') union  select a.id from org_user a,org_department_user_job b where exists  (select b.entity_uuid from cd_pinyin b where b.entity_uuid=a.uuid and b.type='User' and (b.pinyin like '%李杰斌%')) and  a.uuid=b.user_uuid and b.department_uuid in ('93ec3033-38fe-4257-b161-2c5098f11d35','53e3cdff-36e9-4122-b264-64ae055923bd','3bfcd425-e096-4bf6-9e7d-c900d2eb2ff5','8911a9eb-65af-4fda-a9e4-f000834d5a85','6b38e257-3614-435e-8e71-2c9fbd837e54','fe3e3f91-9984-4baa-ac40-995a08763eaa')
        querySql.append("select a.id from org_user a,org_department_user_job b where (a.login_name like");
        querySql.append("'%").append(searchValue).append("%'");
        querySql.append(" or a.user_name like ").append("'%").append(searchValue).append("%')");
        querySql.append(" and  a.uuid=b.user_uuid and b.department_uuid in (")
                .append(getUnionSql(getDepartmentUuids())).append(")");
        querySql.append(" union ");
        querySql.append(" select a.id from org_user a,org_department_user_job b where exists ");
        querySql.append(" (select b.entity_uuid from cd_pinyin b where b.entity_uuid=a.uuid and b.type='User'");
        querySql.append(" and (b.pinyin like ").append("'%").append(searchValue).append("%'))");
        querySql.append(" and  a.uuid=b.user_uuid and b.department_uuid in (")
                .append(getUnionSql(getDepartmentUuids())).append(")");
        return querySql.toString();
    }

    public String[] getDepartmentUuids() {
        return this.departmentUuids;
    }

    public String getUnionSql(String[] uuids) {
        StringBuilder idsStr = new StringBuilder();
        for (int i = 0; i < uuids.length; i++) {
            if (i > 0) {
                idsStr.append(",");
            }
            idsStr.append("'").append(uuids[i]).append("'");
        }
        return idsStr.toString();
    }

    /**
     * 如何描述该方法
     * begin2105-01-15 @ yuyq
     *
     * @param type
     * @param all
     * @param login
     * @param searchValue
     */
    @Override
    public Document searchXml(String type, String all, String login, String searchValue,
                              HashMap<String, String> filterDisplayMap) {

        Map<String, Object> values = new HashMap<String, Object>();
        values.put("code", searchValue);
        values.put("name", searchValue);
        values.put("pinyin", searchValue);

        long time1 = System.currentTimeMillis();
        String rssearchValue = searchValue.toLowerCase();
        Query query = this.userService.createSQLQuery(createSearchSql(rssearchValue));
        List idList = query.list();
        //将查询返回的object对象转换为User对象
        List<User> dataList = userService.getByIds(idList);
        List<Department> deptList = new ArrayList<Department>();
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        if (dataList.size() == 0) {
            //获取当前登录人的信息
            UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            User user1 = userService.get(userDetails.getUserUuid());
            //获取当前登陆用户所属的所有部门
            Set<DepartmentUserJob> departmentUserJobs = user1.getDepartmentUsers();
            List<String> departmentUuids = new ArrayList<String>();
            List<Department> departments = new ArrayList<Department>();
            for (DepartmentUserJob departmentUserJob : departmentUserJobs) {
                Department department = departmentUserJob.getDepartment();
                List<Department> depts = this.departmentService.getLeafDepartments(department.getId());
                for (Department department2 : depts) {
                    departmentUuids.add(department2.getUuid());

                }
                Department department2 = departmentService.getById(departmentUserJob.getDepartment().getId());
                departments.add(department2);
                //获取我的所属部门的所有子部门
                departments.addAll(this.departmentService.getChildren(department.getId()));
                //获取我的所属部门的uuid
                departmentUuids.add(department.getUuid());

            }
            List<Department> deptList1 = new ArrayList<Department>();
            //查找符合条件的所有部门
            deptList1 = this.departmentService.namedQuery("departmentTreeQuery", values, Department.class);
            //判断符合条件的所有部门是否在我所属部门或子部门中
            for (Department department : deptList1) {
                for (Department department1 : departments) {
                    //判断部门是否启用
                    if (department1.getIsVisible() == true && department.getId().equals(department1.getId())) {
                        deptList.add(department);
                    }
                }
            }

            if (deptList.size() > 0) {
                for (Department department : deptList) {
                    buildSelfDepartmentJobUserXML(root, department, "", "", filterDisplayMap);
                }
            } else {
                for (String ids : departmentUuids) {
                    values.put("departmentUuids", ids);
                    List<Job> deptListx = this.jobService.namedQuery("jobPinyinQuery", values, Job.class);
                    if (deptListx.size() > 0) {
                        for (Job job : deptListx) {
                            Department dept = departmentService.get(job.getDepartmentUuid());
                            Job u = jobService.getByUuid(job.getUuid());

                            String jobPath = u.getDepartmentName() + "/" + u.getName();
                            String isLeaf = FALSE;
                            //在搜索职位时往xml放部门信息，这是没必要的，会影响到js的解析判断
                            //setDepartmentElement(root, dept, isLeaf,u.getDepartmentName());
                            Element child = root.addElement(NODE_UNIT);
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
        }
        for (User user : dataList) {
            User u = userService.get(user.getUuid());
            if (u.getEnabled() == false) {
                continue;
            }
            //按职位搜索
            Set<UserJob> userjobs = u.getUserJobs();
            //System.out.println(user.getUserName());
            for (UserJob userJob : userjobs) {
                Department dept = departmentService.get(userJob.getJob().getDepartmentUuid());
                if (dept.getIsVisible() == true) {
                    Element child = root.addElement(NODE_UNIT);
                    String userPath = dept.getPath() + "/" + userJob.getJob().getName() + "/" + user.getUserName();

                    // setDepartmentUserJobElement(child, user, userPath, departmentUserJob.getJobName());
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

}
