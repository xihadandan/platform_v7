/*
 * @(#)2013-2-19 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.service.impl;

import com.wellsoft.pt.org.dao.UnitDao;
import com.wellsoft.pt.org.entity.Unit;
import com.wellsoft.pt.org.service.UnitService;
import com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

import static com.wellsoft.pt.org.unit.service.impl.UnitTreeServiceImpl.*;

/**
 * Description: 组织单元选项服务类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-19.1	zhulh		2013-2-19		Create
 * </pre>
 * @date 2013-2-19
 */
@Service
@Transactional
public class UnitTreeOrgUnitTypeDelegateServiceImpl extends UnitTreeTypeDelegateService {

    @Autowired
    private UnitDao unitDao;

    @Autowired
    private UnitService unitService;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#parserUnit(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document parserUnit(String type, String all, String login, String filterCondition, HashMap<String, String> filterDisplayMap) {
        return parseOrgUnit(all, filterDisplayMap);
    }

    /**
     * @param all
     * @return
     */
    private Document parseOrgUnit(String all, HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        List<Unit> units = this.unitDao.getTopLevel();
        buildOrgUnitXML(root, units, "", filterDisplayMap);
        return document;
    }

    /**
     * @param root
     * @param units
     */
    private void buildOrgUnitXML(Element root, List<Unit> units, String prefix, HashMap<String, String> filterDisplayMap) {
        for (Unit unit : units) {
            Element child = root.addElement(NODE_UNIT);
            String name = "1".equals(unit.getType()) ? unit.getCategory() : unit.getName();
            String path = prefix + name;
            String isLeaf = unit.getChildren().size() == 0 ? TRUE : FALSE;
            setOrgUnitElement(child, unit, isLeaf, path, filterDisplayMap);

            //			if (unit.getChildren().size() != 0) {
            //				buildOrgUnitXML(child, unit.getChildren(), path + "/");
            //			}
        }
    }

    /**
     * @param child
     * @param unit
     * @param isLeaf
     * @param path
     */
    private void setOrgUnitElement(Element element, Unit unit, String isLeaf, String path, HashMap<String, String> filterDisplayMap) {
        // 类型，1为分类、2为组织单元
        if (filterDisplayMap.get(unit.getId()) != null) return;
        String name = "1".equals(unit.getType()) ? unit.getCategory() : unit.getName();
        String type = "1".equals(unit.getType()) ? ATTRIBUTE_TYPE_DEP : ATTRIBUTE_TYPE_ORG_UNIT;
        String realPath = "1".equals(unit.getType()) ? path : unit.getName();
        element.addAttribute(ATTRIBUTE_TYPE, type);
        element.addAttribute(ATTRIBUTE_ISLEAF, isLeaf);
        element.addAttribute(ATTRIBUTE_ID, unit.getId());
        element.addAttribute(ATTRIBUTE_NAME, name);
        element.addAttribute(ATTRIBUTE_PATH, realPath);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#toggleUnit(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document toggleUnit(String type, String id, String all, String login, HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        Unit unit = this.unitDao.getById(id);
        String name = "1".equals(unit.getType()) ? unit.getCategory() : unit.getName();
        buildToggleUnitXML(root, unit, name, filterDisplayMap);
        return document;
    }

    /**
     * @param root
     * @param unit
     * @param string
     */
    private void buildToggleUnitXML(Element element, Unit unit, String path, HashMap<String, String> filterDisplayMap) {
        Element child = element.addElement(NODE_UNIT);
        String isLeaf = unit.getChildren().size() == 0 ? TRUE : FALSE;
        setOrgUnitElement(child, unit, isLeaf, path, filterDisplayMap);
        for (Unit childUnit : unit.getChildren()) {
            String name = "1".equals(childUnit.getType()) ? childUnit.getCategory() : childUnit.getName();
            buildToggleUnitXML(child, childUnit, path + "/" + name, filterDisplayMap);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#leafUnit(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document leafUnit(String type, String id, String leafType, String login, HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        List<Unit> units = this.unitService.getLeafUnits(id);
        for (Unit unit : units) {
            Element child = root.addElement(NODE_UNIT);
            String isLeaf = unit.getChildren().size() == 0 ? TRUE : FALSE;
            String name = "1".equals(unit.getType()) ? unit.getCategory() : unit.getName();
            setOrgUnitElement(child, unit, isLeaf, name, filterDisplayMap);
        }
        return document;
    }

}
