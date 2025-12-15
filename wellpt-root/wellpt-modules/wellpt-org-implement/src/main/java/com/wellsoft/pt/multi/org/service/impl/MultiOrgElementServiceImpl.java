/*
 * @(#)2018年4月4日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.service.impl;

import com.wellsoft.context.util.collection.List2Map;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.multi.org.dao.MultiOrgElementDao;
import com.wellsoft.pt.multi.org.entity.MultiOrgElement;
import com.wellsoft.pt.multi.org.service.MultiOrgElementService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
public class MultiOrgElementServiceImpl extends AbstractJpaServiceImpl<MultiOrgElement, MultiOrgElementDao, String> implements MultiOrgElementService {

    @Override
    public MultiOrgElement getById(String id) {
        MultiOrgElement q = new MultiOrgElement();
        q.setId(id);
        List<MultiOrgElement> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    @Override
    public Map<String, MultiOrgElement> queryElementMapByOrgVersion(String orgVersionId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("orgVersionId", orgVersionId);
        List<MultiOrgElement> objs = listByNameSQLQuery("queryElementMapByOrgVersion", params);
        Map<String, MultiOrgElement> map = new HashMap<String, MultiOrgElement>();
        if (!CollectionUtils.isEmpty(objs)) {
            map = new List2Map<MultiOrgElement>() {
                @Override
                protected String getMapKey(MultiOrgElement obj) {
                    return obj.getId();
                }
            }.convert(objs);
        }
        return map;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.service.MultiOrgElementService#countByProperty(java.lang.String, java.lang.Object)
     */
    @Override
    public long countByProperty(String propertyName, Object propertyValue) {
        MultiOrgElement entity = new MultiOrgElement();
        ReflectionUtils.setFieldValue(entity, propertyName, propertyValue);
        return this.dao.countByEntity(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.multi.org.service.MultiOrgElementService#countByProperties(java.lang.String[], java.lang.Object[])
     */
    @Override
    public long countByProperties(String[] propertyNames, Object[] propertyValues) {
        MultiOrgElement entity = new MultiOrgElement();
        int index = 0;
        for (String propertyName : propertyNames) {
            ReflectionUtils.setFieldValue(entity, propertyName, propertyValues[index]);
            index++;
        }
        return this.dao.countByEntity(entity);
    }

    @Override
    public List<MultiOrgElement> getOrgElementsByIds(List<String> ids) {
        return this.dao.listByFieldInValues("id", ids);
    }

}
