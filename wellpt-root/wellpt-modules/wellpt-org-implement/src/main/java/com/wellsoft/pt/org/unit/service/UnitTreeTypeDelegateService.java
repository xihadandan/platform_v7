/*
 * @(#)2013-1-15 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.service;

import com.wellsoft.pt.org.entity.Department;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.HashMap;

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
public abstract class UnitTreeTypeDelegateService {
    /**
     * @param element
     * @param department
     * @param prefix
     */
    protected void setDepartmentElement(Element element, Department department, String isLeaf, String path, HashMap<String, String> filterDisplayMap) {
        if (filterDisplayMap.get(department.getId()) != null) return;
        element.addAttribute(ATTRIBUTE_TYPE, ATTRIBUTE_TYPE_DEP);
        element.addAttribute(ATTRIBUTE_ISLEAF, isLeaf);
        element.addAttribute(ATTRIBUTE_ID, department.getId());
        element.addAttribute(ATTRIBUTE_NAME, department.getName());
        element.addAttribute(ATTRIBUTE_PATH, path);
    }

    protected void setDepartmentElement(Element element, String id, String name, String isLeaf, String path, HashMap<String, String> filterDisplayMap) {
        if (filterDisplayMap.get(id) != null) return;
        element.addAttribute(ATTRIBUTE_TYPE, ATTRIBUTE_TYPE_DEP);
        element.addAttribute(ATTRIBUTE_ISLEAF, isLeaf);
        element.addAttribute(ATTRIBUTE_ID, id);
        element.addAttribute(ATTRIBUTE_NAME, name);
        element.addAttribute(ATTRIBUTE_PATH, path);
    }

    /**
     * @param type
     * @param all
     * @param login
     * @param filterContion
     * @return
     */
    public abstract Document parserUnit(String type, String all, String login, String filterContion, HashMap<String, String> filterDisplayMap);

    /**
     * @param type
     * @param id
     * @param all
     * @param login
     * @return
     */
    public abstract Document toggleUnit(String type, String id, String all, String login, HashMap<String, String> filterDisplayMap);

    /**
     * 获取组织单元根结点
     *
     * @param type
     * @param id
     * @param leafType
     * @param login
     * @return
     */
    public abstract Document leafUnit(String type, String id, String leafType, String login, HashMap<String, String> filterDisplayMap);
}
