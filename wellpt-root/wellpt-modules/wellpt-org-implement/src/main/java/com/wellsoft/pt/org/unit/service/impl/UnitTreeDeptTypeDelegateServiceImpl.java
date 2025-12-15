/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.org.entity.Department;
import com.wellsoft.pt.org.service.DepartmentService;
import com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService;
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
public class UnitTreeDeptTypeDelegateServiceImpl extends UnitTreeTypeDelegateService {

    @Autowired
    private DepartmentService departmentService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#parserUnit(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document parserUnit(String type, String all, String login, String filterCondition,
                               HashMap<String, String> filterDisplayMap) {
        return parseDepartment(all, filterDisplayMap);
    }

    /**
     * all=2(全部树型节点)
     * all=1(当只有一个根节点时多返回一级子节点，否则返回多个根节点即可)
     * all=0(返回包括给定节点和其直接子节点)
     *
     * @param all
     * @return
     */
    private Document parseDepartment(String all, HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        List<Department> departments = this.departmentService.getTopLevel();
        if (all.equals(ALL_FULL)) {
            buildFullDepartmentXML(root, departments, "", filterDisplayMap);
        } else if (all.equals(ALL_SELF)) {
            buildSelfDepartmentXML(root, departments, "", filterDisplayMap);
        }
        return document;
    }

    /**
     * @param element
     * @param departments
     * @param prefix
     */
    private void buildFullDepartmentXML(Element element, List<Department> departments, String prefix,
                                        HashMap<String, String> filterDisplayMap) {
        for (Department department : departments) {
            //该部门是否显示
            if (department.getIsVisible() == true) {
                Element child = element.addElement(NODE_UNIT);
                String path = prefix + department.getName();
                String isLeaf = department.getChildren().size() == 0 ? TRUE : FALSE;
                setDepartmentElement(child, department, isLeaf, path, filterDisplayMap);

                if (department.getChildren().size() != 0) {
                    buildFullDepartmentXML(child, department.getChildren(), path + "/", filterDisplayMap);
                }
            }
        }
    }

    /**
     * @param element
     * @param departments
     * @param prefix
     */
    private void buildSelfDepartmentXML(Element element, List<Department> departments, String prefix,
                                        HashMap<String, String> filterDisplayMap) {
        List<Department> selfDepts = departments;

        Element root = element;
        String path = prefix;
        if (selfDepts.size() == 1) {
            root = element.addElement(NODE_UNIT);
            Department department = selfDepts.get(0);
            path = department.getName();
            String isLeaf = department.getChildren().size() == 0 ? TRUE : FALSE;
            setDepartmentElement(root, department, isLeaf, path, filterDisplayMap);

            selfDepts = department.getChildren();

            path += "/";
        }

        for (Department department : selfDepts) {
            if (department.getIsVisible() == true) {
                Element child = root.addElement(NODE_UNIT);
                String isLeaf = department.getChildren().size() == 0 ? TRUE : FALSE;
                setDepartmentElement(child, department, isLeaf, path + department.getName(), filterDisplayMap);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#toggleUnit(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document toggleUnit(String type, String id, String all, String login,
                               HashMap<String, String> filterDisplayMap) {
        return toggleDepartmentUnit(id, filterDisplayMap);
    }

    /**
     * @param id
     * @return
     */
    private Document toggleDepartmentUnit(String id, HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        String path = departmentService.getFullPath(id);
        Department department = this.departmentService.getById(id);
        buildToggleDepartmentXML(root, department, path, filterDisplayMap);
        return document;
    }

    /**
     * @param element
     * @param department
     * @param path
     */
    private void buildToggleDepartmentXML(Element element, Department department, String path,
                                          HashMap<String, String> filterDisplayMap) {
        Element child = element.addElement(NODE_UNIT);
        String isLeaf = department.getChildren().size() == 0 ? TRUE : FALSE;
        setDepartmentElement(child, department, isLeaf, path, filterDisplayMap);
        for (Department dept : department.getChildren()) {
            //buildToggleDepartmentXML(child, dept, path + "/" + dept.getName());
            if (dept.getIsVisible() == true) {
                Element childchildren = child.addElement(NODE_UNIT);
                String isLeafchild = dept.getChildren().size() == 0 ? TRUE : FALSE;
                setDepartmentElement(childchildren, dept, isLeafchild, path + "/" + dept.getName(), filterDisplayMap);
            }
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#leafUnit(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document leafUnit(String type, String id, String leafType, String login,
                             HashMap<String, String> filterDisplayMap) {
        return leafDepartmentUnit(id, filterDisplayMap);
    }

    /**
     * 获取组织单元根结点的Document
     *
     * @param id
     * @return
     */
    private Document leafDepartmentUnit(String id, HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        List<Department> departments = departmentService.getLeafDepartments(id);
        for (Department department : departments) {
            //该部门是否显示
            if (department.getIsVisible() == true) {
                Element child = root.addElement(NODE_UNIT);
                String isLeaf = department.getChildren().size() == 0 ? TRUE : FALSE;
                setDepartmentElement(child, department, isLeaf, departmentService.getFullPath(department.getId()),
                        filterDisplayMap);
            }
        }
        return document;
    }

    /**
     * 如何描述该方法
     *
     * @param optionType
     * @param all
     * @param login
     * @param searchValue
     * @return
     */
    public Document searchXml(String optionType, String all, String login, String searchValue,
                              HashMap<String, String> filterDisplayMap) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", searchValue);
        values.put("shortName", searchValue);
        values.put("pinyin", searchValue);
        List<Department> dataList = this.departmentService.namedQuery("departmentPinyinQuery", values,
                Department.class, new PagingInfo(1, Integer.MAX_VALUE));
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        for (Department department : dataList) {
            if (department.getIsVisible() == true) {
                Element child = root.addElement(NODE_UNIT);
                String isLeaf = department.getChildren().size() == 0 ? TRUE : FALSE;
                String path = departmentService.getFullPath(department.getId());
                setDepartmentElement(child, department.getId(), path, isLeaf, path, filterDisplayMap);
            }
        }
        return document;
    }
}
