/*
 * @(#)2017年12月22日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.dao;

import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.jdbc.support.BaseQueryItem;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.QueryItem;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Description: 基本dao接口类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年12月22日.1	chenqiong		2017年12月22日		Create
 * </pre>
 * @date 2017年12月22日
 */
public interface JpaDao<T extends JpaEntity, UUID extends Serializable> {

    /**
     * 根据主键uuid，获取数据
     *
     * @param uuid
     * @return
     */
    T getOne(UUID uuid);

    /**
     * 获取对象锁
     *
     * @param uuid
     * @return
     */
    T getLockOne(UUID uuid, LockOptions lockOptions);

    /**
     * 根据HQL命名查询获取数据
     *
     * @param queryName 命名查询名称
     * @param values    参数-值
     * @return
     */
    List<T> listByNameHQLQuery(String queryName, Map<String, Object> values);

    /**
     * 根据SQL命名查询获取数据
     *
     * @param queryName
     * @param values
     * @return
     */
    List<T> listByNameSQLQuery(String queryName, Map<String, Object> values);

    /**
     * 根据HQL命名查询获取一条数据
     *
     * @param queryName 命名查询名称
     * @param values    参数-值
     * @return
     */
    T getOneByNameHQLQuery(String queryName, Map<String, Object> values);

    /**
     * 根据SQL命名查询获取一条数据
     *
     * @param queryName
     * @param values
     * @return
     */
    T getOneByNameSQLQuery(String queryName, Map<String, Object> values);

    /**
     * 根据HQL命名查询获取数据
     *
     * @param queryName  命名查询名称
     * @param values     参数-值
     * @param pagingInfo 分页
     * @return
     */
    List<T> listByNameHQLQuery(String queryName, final Map<String, Object> values,
                               PagingInfo pagingInfo);

    /**
     * 根据SQL命名查询获取数据
     *
     * @param queryName  命名查询名称
     * @param values     参数-值
     * @param pagingInfo 分页
     * @return
     */
    List<T> listByNameSQLQuery(String queryName, final Map<String, Object> values,
                               PagingInfo pagingInfo);

    /**
     * 根据HQL命名查询获取数据
     *
     * @param queryName  命名查询名称
     * @param values     参数-值
     * @param pagingInfo 分页
     * @return
     */
    List<QueryItem> listQueryItemByNameHQLQuery(String queryName, final Map<String, Object> values,
                                                PagingInfo pagingInfo);

    /**
     * 根据SQL命名查询获取数据
     *
     * @param queryName  命名查询名称
     * @param values     参数-值
     * @param pagingInfo 分页
     * @return
     */
    List<QueryItem> listQueryItemByNameSQLQuery(String queryName, final Map<String, Object> values,
                                                PagingInfo pagingInfo);

    /**
     * 根据SQL命名查询获取数据
     * 不去除注释
     * oracle表强制hash关联查询场景
     *
     * @param queryName  命名查询名称
     * @param values     参数-值
     * @param pagingInfo 分页
     * @return
     */
    List<QueryItem> listQueryItemByNameSQLQueryHash(String queryName, final Map<String, Object> values, PagingInfo pagingInfo);

    /**
     * 根据HQL查询获取数据
     *
     * @param hql
     * @param values     参数-值
     * @param pagingInfo 分页
     * @return
     */
    List<QueryItem> listQueryItemByHQL(String hql, final Map<String, Object> values,
                                       PagingInfo pagingInfo);

    /**
     * 根据SQL查询获取数据
     *
     * @param sql
     * @param values     参数-值
     * @param pagingInfo 分页
     * @return
     */
    List<QueryItem> listQueryItemBySQL(String sql, final Map<String, Object> values,
                                       PagingInfo pagingInfo);

    /**
     * 根据SQL命名查询获取数据
     *
     * @param queryName
     * @param itemClass  数据类必须是继承BaseQueryItem的类
     * @param values
     * @param pagingInfo 分页信息为null，则不进行分页查询
     * @return
     */
    <ITEM extends BaseQueryItem> List<ITEM> listItemByNameSQLQuery(String queryName,
                                                                   Class<ITEM> itemClass,
                                                                   Map<String, Object> values,
                                                                   PagingInfo pagingInfo);

    /**
     * 根据Hql 查询 返回指定List<itemClass>
     *
     * @param hql
     * @param itemClass
     * @param values
     * @param <ITEM>
     * @return
     */
    <ITEM extends BaseQueryItem> List<ITEM> listItemHqlQuery(String hql, Class<ITEM> itemClass,
                                                             Map<String, Object> values);

    /**
     * 根据HQL命名查询获取数据
     *
     * @param queryName
     * @param itemClass  数据类必须是继承BaseQueryItem的类
     * @param values
     * @param pagingInfo 分页信息为null，则不进行分页查询
     * @return
     */
    <ITEM extends BaseQueryItem> List<ITEM> listItemByNameHQLQuery(String queryName,
                                                                   Class<ITEM> itemClass,
                                                                   Map<String, Object> values,
                                                                   PagingInfo pagingInfo);

    /**
     * 根据hql语句统计数量
     *
     * @param hql
     * @param values 参数-值映射
     * @return
     */
    long countByHQL(final String hql, final Map<String, Object> values);

    /**
     * 根据sql语句统计数量
     *
     * @param sql
     * @param values
     * @return
     */
    long countBySQL(final String sql, final Map<String, Object> values);

    /**
     * 根据实体类参数统计数量
     *
     * @param entity
     * @return
     */
    long countByEntity(T entity);

    /**
     * 批量循环保存实体类
     *
     * @param entities
     */
    void saveAll(Collection<T> entities);

    /**
     * 保存或更新
     *
     * @param entity
     */
    void save(T entity);


    /**
     * 保存或更新
     *
     * @param entity
     */
    void update(T entity);

    /**
     * 根据主键值集合，删除数据
     *
     * @param uuids
     */
    void deleteByUuids(Collection<UUID> uuids);

    /**
     * 根据主键删除数据
     *
     * @param uuid
     */
    void delete(UUID uuid);

    /**
     * 根据实体删除数据
     *
     * @param entity
     */
    void delete(T entity);

    void deleteByEntities(Collection<T> entities);

    /**
     * 刷新session
     */
    void flushSession();

    /**
     * 根据hql，查询数据集合
     *
     * @param hql
     * @param params 参数-值映射
     * @return
     */
    List<T> listByHQL(String hql, Map<String, Object> params);

    /**
     * 根据hql，分页查询数据集合
     *
     * @param hql
     * @param params
     * @param page
     * @return
     */
    List<T> listByHQLAndPage(String hql, Map<String, Object> params, PagingInfo page);

    /**
     * 根据hql查询返回一条数据
     *
     * @param hql
     * @param params
     * @return
     */
    T getOneByHQL(String hql, Map<String, Object> params);

    /**
     * @param sql
     * @param params
     * @return
     */
    T getOneBySQL(String sql, Map<String, Object> params);

    /**
     * 根据sql，查询数据集合
     *
     * @param sql
     * @param params
     * @return
     */
    List<T> listBySQL(String sql, Map<String, Object> params);

    /**
     * 根据sql，分页查询数据集合
     *
     * @param sql
     * @param params
     * @param page
     * @return
     */
    List<T> listBySQLAndPage(String sql, Map<String, Object> params, PagingInfo page);

    /**
     * 根据sql查询返回一条数字类型的结果
     *
     * @param sql
     * @param params
     * @return
     */
    @Deprecated
    <NBR extends Number> NBR getNumberBySQL(String sql, Map<String, Object> params);

    /**
     * 根据hql查询返回一条数字类型的结果
     *
     * @param hql
     * @param params
     * @return
     */
    @Deprecated
    <NBR extends Number> NBR getNumberByHQL(String hql, Map<String, Object> params);

    /**
     * 根据sql查询返回一条数字类型的结果
     *
     * @param sql
     * @param params
     * @param numberClass 数字类
     * @return
     */
    @Deprecated
    <NBR extends Number> NBR getNumberBySQL(String sql, Map<String, Object> params,
                                            Class<NBR> numberClass);

    /**
     * 根据hql查询返回一条数字类型的结果
     *
     * @param hql
     * @param params
     * @param numberClass 数字类
     * @return
     */
    @Deprecated
    <NBR extends Number> NBR getNumberByHQL(String hql, Map<String, Object> params,
                                            Class<NBR> numberClass);


    /**
     * 根据sql查询返回数字类型的集合结果
     *
     * @param sql
     * @param params
     * @return
     */
    @Deprecated
    <NBR extends Number> List<NBR> listNumberBySQL(String sql, Map<String, Object> params);

    /**
     * 根据hql查询返回数字类型的集合结果
     *
     * @param hql
     * @param params
     * @return
     */
    @Deprecated
    <NBR extends Number> List<NBR> listNumberByHQL(String hql, Map<String, Object> params);

    /**
     * 根据sql查询返回一条字符类型的结果
     *
     * @param sql
     * @param params
     * @return
     */
    <CHAR> CHAR getCharSequenceBySQL(String sql, Map<String, Object> params);

    /**
     * 根据hql查询返回一条字符类型的结果
     *
     * @param hql
     * @param params
     * @return
     */
    <CHAR> CHAR getCharSequenceByHQL(String hql, Map<String, Object> params);

    /**
     * 根据sql查询返回字符类型的集合结果
     *
     * @param sql
     * @param params
     * @return
     */
    <CHAR> List<CHAR> listCharSequenceBySQL(String sql, Map<String, Object> params);

    /**
     * 根据hql查询返回字符类型的集合结果
     *
     * @param hql
     * @param params
     * @return
     */
    <CHAR> List<CHAR> listCharSequenceByHQL(String hql, Map<String, Object> params);


    /**
     * 基于“字段名=值”的条件检索数据
     *
     * @param field 实体字段名
     * @param value 值
     * @return
     */
    List<T> listByFieldEqValue(String field, Object value);

    /**
     * 基于“字段名!=值”的条件检索数据
     *
     * @param field 实体字段名
     * @param value 值
     * @return
     */
    List<T> listByFieldNotEqValue(String field, Object value);

    /**
     * 基于“ 字段名  not in (值1,值2,...) ”的条件检索数据
     *
     * @param field 实体字段名
     * @param value 值
     * @return
     */
    List<T> listByFieldNotInValues(String field, List<Object> value);

    /**
     * 基于“字段名” 左like 查询： '值%'
     *
     * @param field
     * @param value
     * @return
     */
    List<T> listByFieldStartLike(String field, String value);

    /**
     * 基于“字段名” 右like 查询： '%值'
     *
     * @param field
     * @param value
     * @return
     */
    List<T> listByFieldEndLike(String field, String value);

    /**
     * 基于“字段名” 全局like 查询： '%值'
     *
     * @param field
     * @param value
     * @return
     */
    List<T> listByFieldAnyLike(String field, String value);

    /**
     * 基于“ 字段名  in (值1,值2,...) ”的条件检索数据
     *
     * @param field 实体字段名
     * @param value 值
     * @return
     */
    List<T> listByFieldInValues(String field, List<?> value);

    /**
     * 根据hql更新数据
     *
     * @param hql
     * @param params
     * @return
     */
    int updateByHQL(String hql, Map<String, Object> params);

    /**
     * 根据hql命名查询定义的语句进行删除
     *
     * @param queryName
     * @param params
     * @return
     */
    int deleteByNamedHQL(String queryName, Map<String, Object> params);

    /**
     * 根据hql删除数据
     *
     * @param hql
     * @param params
     * @return
     */
    int deleteByHQL(String hql, Map<String, Object> params);

    /**
     * 根据sql命名查询定义的语句进行删除
     *
     * @param queryName
     * @param params
     * @return
     */
    int deleteByNamedSQL(String queryName, Map<String, Object> params);

    /**
     * 根据sql更新数据
     *
     * @param sql
     * @param params
     * @return
     */
    int updateBySQL(String sql, Map<String, Object> params);

    /**
     * 根据sql命名查询定义的语句进行更新
     *
     * @param sql
     * @param params
     * @return
     */
    int updateByNamedSQL(String queryName, Map<String, Object> params);

    /**
     * 根据hql命名查询定义的语句进行更新
     *
     * @param sql
     * @param params
     * @return
     */
    int updateByNamedHQL(String queryName, Map<String, Object> params);

    /**
     * 根据sql删除数据
     *
     * @param sql
     * @param params
     * @return
     */
    int deleteBySQL(String sql, Map<String, Object> params);

    /**
     * 根据实例参数，字段过滤，进行分页排序数据检索
     *
     * @param entityInst      实例参数
     * @param propertyFilters 字段过滤
     * @param orderBy         排序
     * @param pagingInfo      分页参数
     * @return
     */
    List<T> listByEntity(T entityInst, List<PropertyFilter> propertyFilters, String orderBy,
                         PagingInfo pagingInfo);

    /**
     * 根据实例参数查询数据
     *
     * @param entity 实例参数
     * @return
     */
    List<T> listByEntity(T entity);

    /**
     * 按分页排序查询所有实体数据，分页与排序可为null
     *
     * @param pagingInfo
     * @param orderBy
     * @return
     */
    List<T> listAllByOrderPage(PagingInfo pagingInfo, String orderBy);

    /**
     * 根据实例参数分页查询数据
     *
     * @param entity     实例参数
     * @param pagingInfo 分页参数
     * @param orderBy    排序
     * @return
     */
    List<T> listByEntityAndPage(T entity, PagingInfo pagingInfo, String orderBy);

    /**
     * 获取session
     *
     * @return
     */
    Session getSession();

    /**
     * 根据指定的sessionFactory获取dao
     *
     * @param sessionFactoryId
     * @return
     */
    JpaDao getDaoBySessionFactory(String sessionFactoryId);

    /**
     * 获取数据库的sysdate
     *
     * @return
     */
    Date getSysDate();

    SessionFactory getSessionFactory(String sessionFactoryId);

    <CHAR> List<CHAR> listCharSequenceByHqlAndPage(String hql, Map<String, Object> params,
                                                   PagingInfo pagingInfo);


    SessionFactory sessionFactory();

    T getOneByFieldEq(String field, Object value);

    Long countByNamedHQLQuery(String queryName, Map<String, Object> params);


    Long countByNamedSQLQuery(String queryName, Map<String, Object> params);
}
