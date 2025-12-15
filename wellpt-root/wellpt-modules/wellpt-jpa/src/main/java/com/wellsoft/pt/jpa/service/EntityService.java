/*
 * @(#)2017年12月15日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.service;

import com.wellsoft.context.jdbc.support.BaseQueryItem;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.pt.jpa.dao.EntityDao;
import org.hibernate.LockOptions;

import java.io.Serializable;
import java.sql.Clob;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据源服务基类，需要指定数据库实体类，dao服务，主键类型
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017年12月15日.1	chenqiong		2017年12月15日		Create
 * </pre>
 * @date 2017年12月15日
 */
public interface EntityService<Entity extends Serializable, DAO extends EntityDao<Entity>> {

    /**
     * 根据主键uuid查询数据实体
     *
     * @param id
     * @return
     */
    Entity getOne(Serializable uuid);


    /**
     * 获取所有数据实体
     *
     * @return
     */
    List<Entity> listAll();

    /**
     * 根据主键uuids集合批量查询数据实体
     *
     * @param uuids
     * @return
     */
    List<Entity> listByUuids(List<? extends Serializable> uuids);

    /**
     * 分页排序获取所有数据实体
     *
     * @param page
     * @param orderBy
     * @return
     */
    List<Entity> listAllByOrderPage(PagingInfo page, String orderBy);

    /**
     * 根据HQL命名查询数据实体列表
     *
     * @param queryName
     * @param params
     * @return
     */
    List<Entity> listByNameHQLQuery(String queryName, Map<String, Object> params);

    /**
     * 根据SQL命名查询数据实体列表
     *
     * @param queryName
     * @param params
     * @return
     */
    List<Entity> listByNameSQLQuery(String queryName, Map<String, Object> params);

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
     * 保存
     *
     * @param entity
     * @return
     */
    void save(Entity entity);

    /**
     * 批量保存
     *
     * @param entities
     */
    void saveAll(List<Entity> entities);

    /**
     * 更新
     *
     * @param entity
     * @return
     */
    void update(Entity entity);

    /**
     * 根据Hql语句进行更新操作
     *
     * @param hql
     * @param params 更新字段值参数
     * @return 更新影响行数
     */
    int updateByHQL(String hql, Map<String, Object> params);

    /**
     * 根据主键uuid删除数据
     *
     * @param uuid
     */
    void deleteByUuid(Serializable uuid);

    void delete(Entity entity);

    /**
     * 按实体数据删除
     *
     * @param entities
     */
    void deleteByEntities(Collection<Entity> entities);

    /**
     * 根据主键uuid集合批量删除数据
     *
     * @param uuids
     */
    void deleteByUuids(List<? extends Serializable> uuids);

    /**
     * 分页列出所有数据
     *
     * @param entity
     * @param pagingInfo 分页参数
     * @param orderBy    排序
     * @return
     */
    List<Entity> listAllByPage(Entity entity, PagingInfo pagingInfo, String orderBy);

    /**
     * 通过hql语句查询数据集合
     *
     * @param hql
     * @param params 参数
     * @return
     */
    List<Entity> listByHQL(String hql, Map<String, Object> params);

    /**
     * 根据hql，分页查询数据集合
     *
     * @param hql
     * @param params
     * @param page
     * @return
     */
    List<Entity> listByHQLAndPage(String hql, Map<String, Object> params, PagingInfo page);

    /**
     * 通过HQL命名查询，分页检索数据集合
     *
     * @param queryName  命名查询名称
     * @param params     参数映射
     * @param pagingInfo 分页参数
     * @param orderBy    排序
     * @return
     */
    List<Entity> listByNameHQLQueryAndPage(String queryName, Map<String, Object> params,
                                           PagingInfo pagingInfo);

    /**
     * 通过SQL命名查询，分页检索数据集合
     *
     * @param queryName  命名查询名称
     * @param params     参数映射
     * @param pagingInfo 分页参数
     * @param orderBy    排序
     * @return
     */
    List<Entity> listByNameSQLQueryAndPage(String queryName, Map<String, Object> params,
                                           PagingInfo pagingInfo,
                                           String orderBy);

    /**
     * clear session
     */
    void clearSession();

    /**
     * flush session
     */
    void flushSession();

    /**
     * refresh entity
     *
     * @param entity
     * @param lockOptions
     */
    void refreshEntity(Entity entity, LockOptions lockOptions);

    /**
     * 字符串转Clob
     *
     * @param str
     * @return
     */
    Clob convertString2Clob(String str);

}
