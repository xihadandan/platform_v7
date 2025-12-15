/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgSystemUnitDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgSystemUnit;
import com.wellsoft.pt.multi.org.service.MultiOrgSystemUnitService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
@Service
public class MultiOrgSystemUnitServiceImpl extends
        AbstractJpaServiceImpl<MultiOrgSystemUnit, MultiOrgSystemUnitDao, String> implements
        MultiOrgSystemUnitService {

    @Override
    public MultiOrgSystemUnit getById(String systemUnitId) {
        MultiOrgSystemUnit q = new MultiOrgSystemUnit();
        q.setId(systemUnitId);
        List<MultiOrgSystemUnit> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    @Override
    public Select2QueryData queryUnitListForSelect2(Select2QueryInfo select2QueryInfo) {
        String excludeIds = select2QueryInfo.getOtherParams("excludeIds", "");
        String includePt = select2QueryInfo.getOtherParams("includePt", "");
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("excludeIds", excludeIds);
        List<MultiOrgSystemUnit> list = this.dao.listByNameSQLQuery("queryUnitListForSelect2",
                params, select2QueryInfo.getPagingInfo());
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<MultiOrgSystemUnit>();
        }
        if (includePt.equals("1")) {
            list.add(0, MultiOrgSystemUnit.PT);
        }
        return new Select2QueryData(list, "id", "name", select2QueryInfo.getPagingInfo());
    }

    // 按名字查找系统单位
    @Override
    public MultiOrgSystemUnit getByName(String name) {
        MultiOrgSystemUnit q = new MultiOrgSystemUnit();
        q.setName(name);
        List<MultiOrgSystemUnit> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    @Override
    public MultiOrgSystemUnit getByCode(String unitCode) {
        List<MultiOrgSystemUnit> objs = this.dao.listByFieldEqValue("code", unitCode);
        return CollectionUtils.isEmpty(objs) ? null : objs.get(0);
    }

    @Override
    public long count() {
        long count = this.dao.countByHQL("select count(uuid) from MultiOrgSystemUnit ", null);
        return count;
    }
}
