/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.multi.org.dao.MultiOrgSystemUnitDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;

/**
 * Description: 如何描述该类
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
public interface MultiOrgSystemUnitService extends JpaService<MultiOrgSystemUnit, MultiOrgSystemUnitDao, String> {
    /**
     * 如何描述该方法
     *
     * @param systemUnitId
     * @return
     */
    MultiOrgSystemUnit getById(String systemUnitId);

    /**
     * 如何描述该方法
     *
     * @param select2QueryInfo
     * @return
     */
    Select2QueryData queryUnitListForSelect2(Select2QueryInfo select2QueryInfo);

    /**
     * 如何描述该方法
     *
     * @param name
     */
    MultiOrgSystemUnit getByName(String name);

    MultiOrgSystemUnit getByCode(String unitCode);

    /**
     * 统计总数
     *
     * @return
     */
    long count();
}
