/*
 * @(#)2014-7-7 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.dao;

import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.pt.jpa.criteria.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.procedure.ProcedureCall;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
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
public interface UniversalDao {

    void setSessionFactory(SessionFactory sessionFactory, String multiTenancyStrategy);

    Session getSession();

    <ENTITY extends Serializable> void save(ENTITY entity);

    <ENTITY extends Serializable> void saveAll(Collection<ENTITY> entities);

    <ENTITY extends Serializable> void delete(ENTITY entity);

    <ENTITY extends Serializable> void deleteAll(Collection<ENTITY> entities);

    <ENTITY extends Serializable> void deleteByPk(Class<ENTITY> entityClass, Serializable id);

    <ENTITY extends Serializable> void deleteAllByPk(Class<ENTITY> entityClass,
                                                     Collection<Serializable> ids);

    int batchExecute(final String hql, final Map<String, Object> values);

    void flush();

    <ENTITY extends Serializable> ENTITY get(Class<ENTITY> entityClass, final Serializable id);

    <ENTITY extends Serializable> List<ENTITY> get(Class<ENTITY> entityClass, String propertyName,
                                                   Collection<?> values);

    <ENTITY extends Serializable> List<ENTITY> get(Class<ENTITY> entityClass, String propertyName,
                                                   Object[] values);

    <ENTITY extends Serializable> List<ENTITY> getAll(Class<ENTITY> entityClass);

    <ENTITY extends Serializable> List<ENTITY> getAll(Class<ENTITY> entityClass, String orderBy);

    <ENTITY extends Serializable> List<ENTITY> getAll(Class<ENTITY> entityClass, String orderBy,
                                                      PagingInfo pagingInfo);

    <ITEM extends Serializable> List<ITEM> find(String hql, Map<String, Object> values,
                                                Class<ITEM> itemClass);

    <ITEM extends Serializable> List<ITEM> find(String hql, Map<String, Object> values,
                                                Class<ITEM> itemClass,
                                                PagingInfo pagingInfo);

    <ITEM extends Serializable> List<ITEM> query(String hql, Map<String, Object> values);

    <ITEM extends Serializable> List<ITEM> query(String hql, Map<String, Object> values,
                                                 Class<ITEM> itemClass);

    <ITEM extends Serializable> List<ITEM> query(String hql, Map<String, Object> values,
                                                 PagingInfo pagingInfo);

    <ITEM extends Serializable> List<ITEM> query(String hql, Map<String, Object> values,
                                                 Class<ITEM> itemClass,
                                                 PagingInfo pagingInfo);

    <ITEM extends Serializable> List<ITEM> namedQuery(String queryName, Map<String, Object> values);

    <ITEM extends Serializable> List<ITEM> namedQuery(String queryName, Map<String, Object> values,
                                                      Class<ITEM> itemClass);

    <ITEM extends Serializable> List<ITEM> namedQuery(String queryName, Map<String, Object> values,
                                                      PagingInfo pagingInfo);

    <ITEM extends Serializable> List<ITEM> namedQuery(String queryName, Map<String, Object> values,
                                                      Class<ITEM> itemClass, PagingInfo pagingInfo);

    <ENTITY extends Serializable> List<ENTITY> findBy(Class<ENTITY> entityClass,
                                                      final String propertyName,
                                                      final Object value);

    <U extends Serializable> U findUnique(String hql, Map<String, Object> values);

    <U extends Serializable> U findUniqueBy(Class<U> entityClass, final String propertyName,
                                            final Object value);

    <U extends Serializable> U findUniqueByNamedQuery(String queryName, Map<String, Object> values);

    <U extends Serializable> U findUniqueByNamedQuery(String queryName, Map<String, Object> values,
                                                      Class<U> itemClass);

    <ENTITY extends Serializable> List<ENTITY> findByExample(ENTITY example);

    <ENTITY extends Serializable> List<ENTITY> findByExample(ENTITY example, String orderBy);

    <ENTITY extends Serializable> List<ENTITY> findByExample(ENTITY example, String orderBy,
                                                             PagingInfo pagingInfo);

    <ENTITY extends Serializable> List<ENTITY> findByExample(ENTITY example,
                                                             List<PropertyFilter> propertyFilters,
                                                             String orderBy, PagingInfo pagingInfo);

    Long count(String hql, Map<String, Object> values);

    Long countByNamedQuery(String queryName, Map<String, Object> values);

    <ENTITY extends Serializable> Long countByExample(ENTITY example);

    ProcedureCall createStoredProcedureCall(String storedProcedure);

    ProcedureCall createStoredProcedureCall(String storedProcedure, Class<?> resultClass);

    ProcedureCall createStoredProcedureCall(String storedProcedure, String resultClass);

    Date getSysDate();

    /**
     * 添加人：吴宾 2015-11-21
     *
     * @param sql
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> query(String sql) throws Exception;

    Criteria createEntityCriteria(Class<?> class1);

    int namedExecute(String executeName, Map<String, Object> values);
}
