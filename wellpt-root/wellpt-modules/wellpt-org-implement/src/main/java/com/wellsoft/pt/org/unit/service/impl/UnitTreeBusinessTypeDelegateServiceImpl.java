/*
 * @(#)2014-2-7 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.service.impl;

import com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService;
import com.wellsoft.pt.unit.dao.BusinessUnitTreeDao;
import com.wellsoft.pt.unit.entity.BusinessType;
import com.wellsoft.pt.unit.entity.BusinessUnitTree;
import com.wellsoft.pt.unit.entity.CommonUnit;
import com.wellsoft.pt.unit.service.BusinessTypeService;
import com.wellsoft.pt.unit.service.BusinessUnitTreeService;
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
 * Description: 业务类型通讯录
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-2-7.1	zhulh		2014-2-7		Create
 * </pre>
 * @date 2014-2-7
 */
@Service
@Transactional
public class UnitTreeBusinessTypeDelegateServiceImpl extends UnitTreeTypeDelegateService {

    @Autowired
    private BusinessTypeService businessTypeService;

    @Autowired
    private BusinessUnitTreeDao businessUnitTreeDao;

    @Autowired
    private BusinessUnitTreeService businessUnitTreeService;

    private String hql = "select tree.unit from BusinessUnitTree tree where 1 = 1 and tree.businessType.id = :businessTypeId "
            + " and (tree.unit.name like '%' || :name || '%' or exists (select entityUuid from TenantPinyin pinyin  "
            + " where pinyin.entityUuid = tree.unit.uuid and pinyin.type = 'CommonUnit' and pinyin.pinyin like '%' || :pinyin || '%' ) ) "
            + " order by tree.unit.code asc";

    public Document searchXml(String optionType, String all, String login, String searchValue, HashMap<String, String> filterDisplayMap) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("businessTypeId", optionType);
        values.put("name", searchValue);
        values.put("pinyin", searchValue);
        List<CommonUnit> dataList = this.businessUnitTreeDao.query(hql, values, CommonUnit.class);
        // List<CommonUnit> dataList = this.commonUnitTreeDao.namedQuery("unitPinyinQuery", values, CommonUnit.class);
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        for (CommonUnit commonUnit : dataList) {
            Element child = root.addElement(NODE_UNIT);
            String isLeaf = TRUE;
            String path = commonUnit.getName();
            setDepartmentElement(child, commonUnit.getId(), path, isLeaf, path, filterDisplayMap);
        }

        return document;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#parserUnit(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document parserUnit(String type, String all, String login, String filterCondition, HashMap<String, String> filterDisplayMap) {
        BusinessType businessType = businessTypeService.getById(type);
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        if (businessType != null) {
            Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("businessTypeUuid", businessType.getUuid());
            List<BusinessUnitTree> businessUnitTreeSet = this.businessUnitTreeDao.getUnitTreeRoot(paramMap);
            buildFullBusinessTypeCommonUnitXML(root, businessUnitTreeSet, "", filterDisplayMap);
        }
        return document;
    }

    /**
     * 如何描述该方法
     *
     * @param root
     * @param businessUnitTreeSet
     * @param string
     */
    private void buildFullBusinessTypeCommonUnitXML(Element element, List<BusinessUnitTree> businessUnitTreeSet,
                                                    String prefix, HashMap<String, String> filterDisplayMap) {
        for (BusinessUnitTree businessUnitTree : businessUnitTreeSet) {
            if (businessUnitTree.getUnit() == null) {
                continue;
            }
            Element child = element.addElement(NODE_UNIT);
            String path = prefix + businessUnitTree.getUnit().getName();
            String isLeaf = businessUnitTree.getChildren().size() == 0 ? TRUE : FALSE;
            setCommonUnitElement(child, businessUnitTree, isLeaf, path, filterDisplayMap);

            if (businessUnitTree.getChildren().size() != 0) {
                buildFullBusinessTypeCommonUnitXML(child, businessUnitTree.getChildren(), path + "/", filterDisplayMap);
            }
        }
    }

    /**
     * @param child
     * @param businessUnitTree
     * @param isLeaf
     * @param path
     */
    private void setCommonUnitElement(Element element, BusinessUnitTree businessUnitTree, String isLeaf, String path, HashMap<String, String> filterDisplayMap) {
        CommonUnit commonUnit = businessUnitTree.getUnit();
        if (filterDisplayMap.get(commonUnit.getId()) != null) return;
        element.addAttribute(ATTRIBUTE_TYPE, ATTRIBUTE_TYPE_DEP);
        element.addAttribute(ATTRIBUTE_ISLEAF, isLeaf);
        element.addAttribute(ATTRIBUTE_ID, commonUnit.getId());
        element.addAttribute(ATTRIBUTE_NAME, commonUnit.getName());
        element.addAttribute(ATTRIBUTE_PATH, path);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#toggleUnit(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document toggleUnit(String type, String id, String all, String login, HashMap<String, String> filterDisplayMap) {
        return null;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#leafUnit(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document leafUnit(String type, String id, String leafType, String login, HashMap<String, String> filterDisplayMap) {
        return leafBusinessTypeCommonUnit(type, id, filterDisplayMap);
    }

    /**
     * @param id
     * @return
     */
    private Document leafBusinessTypeCommonUnit(String businessType, String commonUnitId, HashMap<String, String> filterDisplayMap) {
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        List<BusinessUnitTree> businessUnitTrees = businessUnitTreeService.getLeafBusinessUnitTrees(businessType,
                commonUnitId);
        for (BusinessUnitTree businessUnitTree : businessUnitTrees) {
            Element child = root.addElement(NODE_UNIT);
            String isLeaf = businessUnitTree.getChildren().size() == 0 ? TRUE : FALSE;
            setCommonUnitElement(child, businessUnitTree, isLeaf,
                    businessUnitTreeService.getFullPath(businessUnitTree.getUuid()), filterDisplayMap);
        }
        return document;
    }

}
