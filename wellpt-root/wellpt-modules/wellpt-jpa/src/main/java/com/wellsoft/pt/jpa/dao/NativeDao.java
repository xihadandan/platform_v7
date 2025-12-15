/*
 * @(#)2014-7-7 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.dao;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.pt.jpa.criteria.Criteria;
import com.wellsoft.pt.jpa.criteria.QueryInterface;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.ResultTransformer;

import java.io.Serializable;
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
 * 2014-7-7.1	zhulh		2014-7-7		Create
 * </pre>
 * @date 2014-7-7
 */
public interface NativeDao {

    void setSessionFactory(SessionFactory sessionFactory, String multiTenancyStrategy);

    <U extends Serializable> U findUnique(String sql, Map<String, Object> values);

    <U extends Serializable> U findUnique(String sql, Map<String, Object> values, Class<U> itemClass);

    <U extends Serializable> U findUniqueByNamedQuery(String queryName, Map<String, Object> values);

    <U extends Serializable> U findUniqueByNamedQuery(String queryName, Map<String, Object> values, Class<U> itemClass);

    <ITEM extends Serializable> List<ITEM> query(String sql, Map<String, Object> values);

    <ITEM extends Serializable> List<ITEM> query(String sql, Map<String, Object> values, Class<ITEM> itemClass);

    <ITEM extends Serializable> List<ITEM> query(String sql, Map<String, Object> values, PagingInfo pagingInfo);

    <ITEM extends Serializable> List<ITEM> query(String sql, Map<String, Object> values, Class<ITEM> itemClass, PagingInfo pagingInfo);

    <ITEM extends Serializable> List<ITEM> namedQuery(String queryName, final Map<String, Object> values);

    <ITEM extends Serializable> List<ITEM> namedQuery(String queryName, final Map<String, Object> values, Class<ITEM> itemClass);

    <ITEM extends Serializable> List<ITEM> namedQuery(String queryName, final Map<String, Object> values, PagingInfo pagingInfo);

    <ITEM extends Serializable> List<ITEM> namedQuery(String queryName, final Map<String, Object> values, Class<ITEM> itemClass, PagingInfo pagingInfo);

    <ITEM extends Serializable> List<ITEM> namedQuery(String queryName, final Map<String, Object> values, ResultTransformer transformer);

    Long countByNamedQuery(String queryName, final Map<String, Object> values);

    int batchExecute(String sql, Map<String, Object> values);

    @Deprecated
    int update(String sql, Map<String, Object> values);

    void executeSql(String sql);

    int namedExecute(String executeName, Map<String, Object> values);

    public <ENTITY extends Serializable> Session getSession();

    Criteria createTableCriteria(String sql);

    Criteria createSqlCriteria(String sql);

    Criteria createInterfaceCriteria(QueryInterface query, String interfaceParam);

    Criteria createNamedQueryCriteria(String sqlName);

    SessionFactory getSessionFactory();
}
