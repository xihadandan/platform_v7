/*
 * @(#)2019年8月28日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.repository.usertable.service.impl;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQuery;
import com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryData;
import com.wellsoft.pt.dyform.implement.repository.usertable.service.UserTableFormDataService;
import com.wellsoft.pt.dyform.implement.repository.usertable.support.UserTableFormDataQueryImpl;
import com.wellsoft.pt.dyform.implement.repository.usertable.support.UserTableFormDataQueryInfo;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.repository.dao.DbTableDao;
import org.apache.commons.collections.MapUtils;
import org.hibernate.SQLQuery;
import org.hibernate.engine.spi.TypedValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月28日.1	zhulh		2019年8月28日		Create
 * </pre>
 * @date 2019年8月28日
 */
@Service
public class UserTableFormDataServiceImpl implements UserTableFormDataService {

    @Autowired
    private NativeDao nativeDao;

    @Autowired
    private DbTableDao dbTableDao;

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.usertable.service.UserTableFormDataService#executeUpdate(java.lang.String, java.util.Map)
     */
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional
    public int executeUpdate(String sql, Map<String, TypedValue> values) {
        SQLQuery sqlQuery = dbTableDao.getSession().createSQLQuery(sql);
        if (MapUtils.isNotEmpty(values)) {
            for (Entry<String, TypedValue> entry : values.entrySet()) {
                String name = entry.getKey();
                TypedValue typedValue = entry.getValue();
                Object object = typedValue.getValue();
                if (object != null) {
                    Class<?> retType = object.getClass();
                    if (Collection.class.isAssignableFrom(retType)) {
                        sqlQuery.setParameterList(name, (Collection) object, typedValue.getType());
                    } else if (retType.isArray()) {
                        sqlQuery.setParameterList(name, (Object[]) object, typedValue.getType());
                    } else {
                        sqlQuery.setParameter(name, object, typedValue.getType());
                    }
                } else {
                    sqlQuery.setParameter(name, object, typedValue.getType());
                }

            }
        }
        // setProperties方法会忽略掉null值
        // sqlQuery.setProperties(values);
        return sqlQuery.executeUpdate();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.usertable.service.UserTableFormDataService#query(java.lang.String, java.util.Map, int)
     */
    @Override
    public List<Map<String, Object>> query(String sql, Map<String, Object> values, int maxResults) {
        try {
            return dbTableDao.query(sql, values, maxResults);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.query.service.FormDataQueryService#query(com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public FormDataQueryData query(UserTableFormDataQueryInfo queryInfo) {
        FormDataQuery formDataQuery = new UserTableFormDataQueryImpl(queryInfo, nativeDao);
        List<HashMap> queryItems = formDataQuery.list(HashMap.class);
        FormDataQueryData queryData = new FormDataQueryData();
        queryData.setDataList(queryItems);
        PagingInfo pagingInfo = queryInfo.getPagingInfo();
        // 查询总数
        if (pagingInfo != null && pagingInfo.isAutoCount()) {
            pagingInfo.setTotalCount(formDataQuery.count());
        }
        queryData.setPagingInfo(pagingInfo);
        return queryData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.implement.repository.query.service.FormDataQueryService#count(com.wellsoft.pt.dyform.implement.repository.query.FormDataQueryInfo)
     */
    @Override
    public long count(UserTableFormDataQueryInfo queryInfo) {
        FormDataQuery formDataQuery = new UserTableFormDataQueryImpl(queryInfo, nativeDao);
        return formDataQuery.count();
    }

}
