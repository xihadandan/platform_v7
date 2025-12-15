/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.wellsoft.context.component.select2.Select2QueryData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgTypeDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgType;
import com.wellsoft.pt.multi.org.service.MultiOrgTypeService;
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
public class MultiOrgTypeServiceImpl extends AbstractJpaServiceImpl<MultiOrgType, MultiOrgTypeDao, String> implements MultiOrgTypeService {

    @Override
    public MultiOrgType getByIdAndSystemUnitId(String typeId, String systemUnitId) {
        MultiOrgType q = new MultiOrgType();
        q.setId(typeId);
        q.setSystemUnitId(systemUnitId);
        List<MultiOrgType> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    // 获取指定系统单位的所有的启用的组织选择项
    @Override
    public List<MultiOrgType> queryOrgTypeListBySystemUnitId(String systemUnitId) {
        MultiOrgType q = new MultiOrgType();
        q.setSystemUnitId(systemUnitId);
        q.setIsForbidden(0);
        return this.dao.listByEntity(q);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.dao.MultiOrgTypeDao#queryOrgTypeListForSelect2(com.wellsoft.context.component.select2.Select2QueryInfo)
     */
    @Override
    public Select2QueryData queryOrgTypeListForSelect2(Select2QueryInfo select2QueryInfo) {
        String excludeIds = select2QueryInfo.getOtherParams("excludeIds", "");
        String systemUnitId = select2QueryInfo.getOtherParams("systemUnitId", "");
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("excludeIds", excludeIds);
        params.put("systemUnitId", systemUnitId);
        List<MultiOrgType> list = this.dao.listByNameSQLQuery("queryOrgTypeListForSelect2", params, select2QueryInfo.getPagingInfo());
        if (CollectionUtils.isEmpty(list)) {
            list = new ArrayList<MultiOrgType>();
        }
        return new Select2QueryData(list, "id", "name", select2QueryInfo.getPagingInfo());
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.dao.MultiOrgTypeDao#getByNameAndSystemUnitId(java.lang.String, java.lang.String)
     */
    @Override
    public MultiOrgType getByNameAndSystemUnitId(String name, String systemUnitId) {
        MultiOrgType q = new MultiOrgType();
        q.setName(name);
        q.setSystemUnitId(systemUnitId);
        List<MultiOrgType> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    @Override
    public long countBySystemUnitIds(List<String> systemUnitIds) {
        HashMap<String, Object> query = new HashMap<String, Object>();
        query.put("unitIds", systemUnitIds);
        long count = this.dao.countByHQL("from MultiOrgType where systemUnitId in (:unitIds)", query);
        return count;
    }
}
