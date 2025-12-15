/*
 * @(#)2013-2-1 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.service.impl;

import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.jdbc.support.*;
import com.wellsoft.context.service.CommonQueryService;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.jpa.util.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-2-1.1	zhulh		2013-2-1		Create
 * </pre>
 * @date 2013-2-1
 */
@Service
@Transactional(readOnly = true)
public class CommonQueryServiceImpl extends BaseServiceImpl implements CommonQueryService {

    private static final String SERVICE_QUERY_NAME = "query";

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.service.CommonQueryService#query(com.wellsoft.pt.core.support.QueryInfo)
     */
    @Override
    public QueryData query(QueryInfo queryInfo) {
        List<?> dataList = null;
        if (StringUtils.isBlank(queryInfo.getServiceName())) {
            dataList = queryByType(queryInfo);
        } else {
            dataList = queryByService(queryInfo);
        }
        QueryData queryData = new QueryData();
        queryData.setDataList(dataList);
        queryData.setPagingInfo(queryInfo.getPagingInfo());
        return queryData;
    }

    /**
     * @param queryInfo
     * @return
     */
    private List<?> queryByService(QueryInfo queryInfo) {
        if (queryInfo.getPagingInfo() == null) {
            queryInfo.setPagingInfo(new PagingInfo(1, 10));
        }

        String serviceName = queryInfo.getServiceName();
        Object service = ApplicationContextHolder.getBean(serviceName);
        try {
            Method method = service.getClass().getMethod(SERVICE_QUERY_NAME, QueryInfo.class);
            return (List<?>) method.invoke(service, queryInfo);
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(e);
        }
    }

    /**
     * @param queryInfo
     * @return
     */
    @SuppressWarnings("unchecked")
    private List<?> queryByType(QueryInfo queryInfo) {
        String sessionFactoryId = getSessionFactoryId(queryInfo.getSfId());
        String queryType = queryInfo.getQueryType();
        Class<?> entityClass = null;
        if (queryType.endsWith("Query")) {
            entityClass = ClassUtils.getItemClasses().get(queryType + "Item");
        } else {
            entityClass = ClassUtils.getEntityClasses().get(queryType);
        }

        if (entityClass == null) {
            throw new RuntimeException("queryType " + queryType + " not found");
        }

        Object entityObject;
        try {
            entityObject = entityClass.newInstance();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        // 设置查询字段条件
        List<PropertyFilter> propertyFilters = queryInfo.getPropertyFilters();
        // 设置分页信息
        String order = queryInfo.getOrderBy();

        if (queryInfo.getPagingInfo() == null) {
            queryInfo.setPagingInfo(new PagingInfo(1, 10));
        }

        if (entityObject instanceof BaseQueryItem) {
            Map<String, Object> values = PropertyFilter.convertToMap(queryInfo.getPropertyFilters());
            values.put("orderBy", queryInfo.getOrderBy());
            Class<Serializable> itemClass = (Class<Serializable>) entityObject.getClass();
            String itemName = itemClass.getSimpleName();
            String queryName = Introspector.decapitalize(itemName.substring(0, itemName.length() - 4));
            if (StringUtils.isNotBlank(sessionFactoryId)) {
                return this.getDao(sessionFactoryId)
                        .namedQuery(queryName, values, itemClass, queryInfo.getPagingInfo());
            }
            return this.dao.namedQuery(queryName, values, itemClass, queryInfo.getPagingInfo());
        }

        // 实例查询，复制属性
        List<?> list = null;
        CommonEntity commonEntity = entityClass.getAnnotation(CommonEntity.class);
        if (commonEntity != null) {
            list = this.getCommonDao().findByExample((Serializable) entityObject, propertyFilters, order,
                    queryInfo.getPagingInfo());
        } else {
            if (StringUtils.isNotBlank(sessionFactoryId)) {
                list = this.getDao(sessionFactoryId).findByExample((Serializable) entityObject, propertyFilters, order,
                        queryInfo.getPagingInfo());
            } else {
                list = this.dao.findByExample((Serializable) entityObject, propertyFilters, order,
                        queryInfo.getPagingInfo());
            }
        }
        List<Object> result = new ArrayList<Object>();
        Iterator<?> it = list.iterator();
        try {
            while (it.hasNext()) {
                Object target = entityClass.newInstance();
                BeanUtils.copyProperties(it.next(), target);
                result.add(target);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return result;
    }

    /**
     * @param sfId
     * @return
     */
    private String getSessionFactoryId(String sfId) {
        if (StringUtils.isBlank(sfId)) {
            return sfId;
        }
        if (!sfId.endsWith("SessionFactory")) {
            return sfId + "SessionFactory";
        }
        return sfId;
    }
}
