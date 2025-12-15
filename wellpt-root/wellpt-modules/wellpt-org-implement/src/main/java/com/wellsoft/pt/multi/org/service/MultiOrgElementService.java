/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgElementDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;

import java.util.List;
import java.util.Map;

/**
 * Description:
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年4月4日.1	chenqiong		2018年4月4日		Create
 * </pre>
 * @date 2018年4月4日
 */
public interface MultiOrgElementService extends JpaService<MultiOrgElement, MultiOrgElementDao, String> {

    /**
     * 根据id获取数据
     *
     * @param dutyId
     * @return
     */
    MultiOrgElement getById(String id);

    /**
     * 获取一个组织用到的所有的元素信息
     *
     * @param orgVersionId
     * @return
     */
    Map<String, MultiOrgElement> queryElementMapByOrgVersion(String orgVersionId);

    /**
     * 根据属性名及值查询总数
     *
     * @param propertyName
     * @param propertyValue
     * @return
     */
    long countByProperty(String propertyName, Object propertyValue);

    /**
     * 根据属性名及值查询总数
     *
     * @param propertyNames
     * @param propertyValues
     * @return
     */
    long countByProperties(String[] propertyNames, Object[] propertyValues);


    List<MultiOrgElement> getOrgElementsByIds(List<String> ids);
}
