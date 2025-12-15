/*
 * @(#)2014-1-1 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.unit.service.impl;

import com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService;
import com.wellsoft.pt.unit.dao.CommonUnitTreeDao;
import com.wellsoft.pt.unit.entity.CommonUnit;
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
 * 2014-1-1.1	zhulh		2014-1-1		Create
 * </pre>
 * @date 2014-1-1
 */
@Service
@Transactional
public class UnitTreeUnitTypeDelegateServiceImpl extends UnitTreeTypeDelegateService {

    @Autowired
    private CommonUnitTreeDao commonUnitTreeDao;

    private String hql = "select tree.unit from CommonUnitTree tree where 1 = 1 and ( "
            + " tree.unit.name like '%' || :name || '%' or exists (select entityUuid from TenantPinyin pinyin  "
            + " where pinyin.entityUuid = tree.unit.uuid and pinyin.type = 'CommonUnit' and pinyin.pinyin like '%' || :pinyin || '%' ) ) "
            + " order by tree.unit.code asc";

    //	private String hql = "select tree.unit from CommonUnitTree tree where 1 = 1 and ( "
    //			+ "tree.unit.name like '%' || :name || '%' ) order by tree.unit.code asc";

    public Document searchXml(String optionType, String all, String login, String searchValue, HashMap<String, String> filterDisplayMap) {
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("name", searchValue);
        values.put("pinyin", searchValue);
        List<CommonUnit> dataList = this.commonUnitTreeDao.query(hql, values, CommonUnit.class);
        // List<CommonUnit> dataList = this.commonUnitTreeDao.namedQuery("unitPinyinQuery", values, CommonUnit.class);
        Document document = DocumentHelper.createDocument();
        Element root = document.addElement(NODE_UNITS);
        for (CommonUnit commonUnit : dataList) {
            Element child = root.addElement(NODE_UNIT);
            String isLeaf = TRUE;
            String path = commonUnit.getName();
            setDepartmentElement(child, commonUnit.getUnitId(), path, isLeaf, path, filterDisplayMap);
        }

        return document;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#parserUnit(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document parserUnit(String type, String all, String login, String filterCondition, HashMap<String, String> filterDisplayList) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#toggleUnit(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document toggleUnit(String type, String id, String all, String login, HashMap<String, String> filterDisplayMap) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.org.unit.service.UnitTreeTypeDelegateService#leafUnit(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public Document leafUnit(String type, String id, String leafType, String login, HashMap<String, String> filterDisplayMap) {
        // TODO Auto-generated method stub
        return null;
    }

}
