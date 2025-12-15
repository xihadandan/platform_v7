/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.entity.Job;
import com.wellsoft.pt.org.service.DepartmentService;
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
import java.util.List;
import java.util.Map;

import static com.wellsoft.pt.org.unit.service.impl.UnitTreeServiceImpl.*;

/**
 * Description: 岗位选择service
 *
 * @author zhengky
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	       修改人		修改日期	      修改内容
 * 2014-8-17.1  zhengky	2014-8-17	  Create
 * </pre>
 * @date 2014-8-17
 */
@Service
@Transactional
public class UnitTreeJobTypeDelegateServiceImpl extends UnitTreeTypeDelegateService {
    @Autowired
    private JobService jobService;
    @Autowired
    private UserService userService;
    @Autowired
    private DepartmentService departmentService;

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#parserUnit(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public Document parserUnit(String type, String all, String login, String filterCondition, HashMap<String, String> filterDisplayMap) {
        return parseDepartmentJob(all, login, filterDisplayMap);
    }

    /**
     * all=2(全部树型节点) all=1(当只有一个根节点时多返回一级子节点，否则返回多个根节点即可) all=0(返回包括给定节点和其直接子节点)
     *
     * @param all
     * @param login
     * @return
     */
    private Document parseDepartmentJob(String all, String login, HashMap<String, String> filterDisplayMap) {
        // 我所属的岗位
        Document document = DocumentHelper.createDocument();
        List<Department> departments = this.departmentService.getTopLevel();
        Element root = document.addElement(NODE_UNITS);
        for (Department department : departments) {
            if (department != null) {
                if (all.equals(ALL_FULL)) {
                    buildFullDepartmentJobXML(root, department, "", login, filterDisplayMap);
                } else if (all.equals(ALL_SELF)) {
                    buildSelfDepartmentUserXML(root, department, "", login, filterDisplayMap);
                }
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
    protected void buildFullDepartmentJobXML(Element element, Department department, String prefix, String login, HashMap<String, String> filterDisplayMap) {
        String deptPath = StringUtils.isEmpty(prefix) ? prefix + department.getName() : prefix + "/"
                + department.getName();
        //该部门是否显示
        if (department.getIsVisible() == true) {
            Element root = element.addElement(NODE_UNIT);
            // 设置部门
            String isLeaf = FALSE;
            setDepartmentElement(root, department, isLeaf, deptPath, filterDisplayMap);

            // 获取部门下的岗位
            List<Job> jobs = jobService.getJobByDeptUuid(department.getUuid());
            for (Job job : jobs) {
                Element child = root.addElement(NODE_UNIT);
                String jobPath = deptPath + "/" + job.getName();
                // 设置部门下的岗位信息
                if (LOGIN_TRUE.equals(login)) {
                    setDepartmentUserJobElement(child, job, jobPath, filterDisplayMap);
                } else {
                    setDepartmentUserJobElement(child, job, jobPath, filterDisplayMap);
                }
            }

            // 递归遍历子结点
            for (Department dept : department.getChildren()) {
                Element child = root;// root.addElement(NODE_UNIT);
                buildFullDepartmentJobXML(child, dept, deptPath, login, filterDisplayMap);
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
    protected void setDepartmentUserJobElement(Element element, Job job, String path, HashMap<String, String> filterDisplayMap) {
        setDepartmentJobElement(element, ATTRIBUTE_TYPE_JOB, "1", job.getId(), job.getName(), path, "", filterDisplayMap);
    }

    protected void setDepartmentJobElement(Element element, Job job, String type, String path, String title, HashMap<String, String> filterDisplayMap) {
        setDepartmentJobElement(element, ATTRIBUTE_TYPE_JOB, type, job.getId(), job.getName(), path, title, filterDisplayMap);
    }

    protected void setDepartmentJobElement(Element element, String type, String isLeaf, String id, String name,
                                           String path, String title, HashMap<String, String> filterDisplayMap) {
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
     * @param element
     * @param department
     * @param string
     * @param login
     */
    protected void buildSelfDepartmentUserXML(Element element, Department department, String prefix, String login, HashMap<String, String> filterDisplayMap) {
        //String deptPath = prefix + department.getName();
        //该部门是否显示
        if (department.getIsVisible() == true) {
            String deptPath = departmentService.getFullPath(department.getId());
            Element root = element.addElement(NODE_UNIT);
            // 设置部门
            String isLeaf = FALSE;
            setDepartmentElement(root, department, isLeaf, deptPath, filterDisplayMap);

            // 获取部门下的用户
            List<Job> jobs = jobService.getJobByDeptUuid(department.getUuid());
            for (Job job : jobs) {
                Element child = root.addElement(NODE_UNIT);
                String jobPath = deptPath + "/" + job.getName();
                // 设置部门下的岗位信息
                if (LOGIN_TRUE.equals(login)) {
                    setDepartmentUserJobElement(child, job, jobPath, filterDisplayMap);
                } else {
                    setDepartmentUserJobElement(child, job, jobPath, filterDisplayMap);
                }
            }

            for (Department dept : department.getChildren()) {
                //该部门是否显示
                if (dept.getIsVisible() == true) {
                    Element child = root.addElement(NODE_UNIT);
                    // 设置部门
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
    public Document toggleUnit(String type, String id, String all, String login, HashMap<String, String> filterDisplayMap) {
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
    public Document leafUnit(String type, String id, String leafType, String login, HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        Department department = this.departmentService.getById(id);
        if (department != null) {
            if (department.getParent() == null) {
                return document;
            }
            Element root = document.addElement(NODE_UNITS);
            //职位查询速度优化
            List<Job> jobs = departmentService.getAllJobByDepartmentPath(department.getPath());
            for (Job job : jobs) {
                Element child = root.addElement(NODE_UNIT);
                String jobPath = job.getDepartmentName() + "/" + job.getName();
                // 设置部门下的岗位信息
                if (LOGIN_TRUE.equals(login)) {
                    setDepartmentUserJobElement(child, job, jobPath, filterDisplayMap);
                } else {
                    setDepartmentUserJobElement(child, job, jobPath, filterDisplayMap);
                }
            }
        }
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
    private void buildLeftDepartmentJobXML(Element root, Department department, String leafType, String login, HashMap<String, String> filterDisplayMap) {
        List<Department> children = department.getChildren();
        // 末端结点，添加用户
        //if (children.isEmpty()) {
        // 获取部门下的用户
        List<Job> jobs = jobService.getJobByDeptUuid(department.getUuid());
        for (Job job : jobs) {
            Element child = root.addElement(NODE_UNIT);
            String jobPath = department.getName() + "/" + job.getName();
            // 设置部门下的岗位信息
            if (LOGIN_TRUE.equals(login)) {
                setDepartmentUserJobElement(child, job, jobPath, filterDisplayMap);
            } else {
                setDepartmentUserJobElement(child, job, jobPath, filterDisplayMap);
            }
        }
        //}
        for (Department childDept : children) {
            buildLeftDepartmentJobXML(root, childDept, leafType, login, filterDisplayMap);
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
    public Document searchXml(String type, String all, String login, String searchValue, HashMap<String, String> filterDisplayMap) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", searchValue);
        values.put("pinyin", searchValue);
        List<Job> dataList = this.jobService.namedQuery("jobPinyinQuery", values, Job.class, new PagingInfo(1,
                Integer.MAX_VALUE));
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);

        for (Job job : dataList) {
            Job u = jobService.getByUuid(job.getUuid());
            Element child = root.addElement(NODE_UNIT);
            String jobPath = u.getDepartmentName() + "/" + u.getName();
            // setDepartmentUserJobElement(child, user, userPath, departmentUserJob.getJobName());
            setDepartmentJobElement(child, ATTRIBUTE_TYPE_JOB, "1", job.getId(), jobPath, jobPath, "", filterDisplayMap);
        }

        return document;
    }

}
