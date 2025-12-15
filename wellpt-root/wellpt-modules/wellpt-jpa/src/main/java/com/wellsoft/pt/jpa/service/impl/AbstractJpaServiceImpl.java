/*
 * @(#)2017年12月15日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.service.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.jdbc.support.BaseQueryItem;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.jpa.service.JpaService;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Hibernate;
import org.hibernate.LockOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Clob;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Description: 数据源服务基类，继承该类的子类所有方法将默认包裹只读事务，可以通过子方法声明读写事务进行事务特性切换
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
@Transactional(readOnly = true)
@SuppressWarnings("unchecked")
public abstract class AbstractJpaServiceImpl<T extends JpaEntity, DAO extends JpaDao<T, UUID>, UUID extends Serializable>
        implements JpaService<T, DAO, UUID> {
    protected final static ThreadLocal<String> SESSION_FACTORY = new ThreadLocal<String>();
    protected Logger logger = LoggerFactory.getLogger(getClass());
    //    @Autowired
    protected DAO dao;

    Class<T> entityClass;

    public AbstractJpaServiceImpl() {
        super();
        try {
            Type genType = this.getClass().getGenericSuperclass();
            if (genType instanceof ParameterizedType) {
                Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                entityClass = (Class<T>) params[0];
                Class<DAO> daoClass = (Class<DAO>) params[1];
                dao = (DAO) ApplicationContextHolder.getBean(
                        daoClass);// 泛型类的依赖注入在spring4.x才支持，故只能采用这个方式注入
            }
        } catch (Exception e) {
            logger.error("AbstractJPAServiceImplApplicationContextHolder 实例化异常：", e);
            throw Throwables.propagate(e);
        }

    }

    @Override
    public DAO getDao() {
        return dao;
    }

    @Override
    public T getOne(UUID id) {
        return dao.getOne(id);
    }

    @Override
    public List<T> listAll() {
        return dao.listAllByOrderPage(null, null);
    }

    @Override
    public List<T> listByUuids(List<UUID> uuids) {
        List<Object> uuidsList = Lists.newArrayList();
        CollectionUtils.addAll(uuidsList, uuids.iterator());
        return dao.listByFieldInValues("uuid", uuidsList);
    }

    @Override
    public List<T> listBySQL(String sql, Map<String, Object> params) {
        return this.dao.listBySQL(sql, params);
    }

    @Override
    public List<T> listBySQL(String sql, Map<String, Object> params, PagingInfo pagingInfo) {
        return this.dao.listBySQLAndPage(sql, params, pagingInfo);
    }

    @Override
    public List<T> listAllByOrderPage(PagingInfo page, String orderBy) {
        return dao.listAllByOrderPage(page, orderBy);
    }

    @Override
    public List<T> listByNameHQLQuery(String queryName, Map<String, Object> params) {
        return dao.listByNameHQLQuery(queryName, params);
    }

    @Override
    @Transactional
    public void save(T entity) {
        dao.save(entity);
    }

    @Override
    @Transactional
    public void saveAll(List<T> entities) {
        dao.saveAll(entities);
    }

    @Override
    @Transactional
    public void update(T entity) {
        dao.update(entity);
    }

    @Override
    @Transactional
    public void delete(UUID uuid) {
        dao.delete(uuid);
    }

    @Override
    @Transactional
    public void delete(T entity) {
        dao.delete(entity);
    }

    @Override
    @Transactional
    public void deleteByUuids(List<UUID> uuids) {
        dao.deleteByUuids(uuids);
    }

    @Override
    @Transactional
    public void deleteByEntities(Collection<T> entities) {
        dao.deleteByEntities(entities);
    }

    public List<T> listByEntity(T entity) {
        return dao.listByEntity(entity);
    }

    @Override
    public List<T> listAllByPage(T entity, PagingInfo pagingInfo, String orderBy) {
        return dao.listByEntity(entity, null, orderBy, pagingInfo);
    }

    @Override
    public List<T> listByHQL(String hql, Map<String, Object> params) {
        return dao.listByHQL(hql, params);
    }

    @Override
    public List<T> listByHQLAndPage(String hql, Map<String, Object> params, PagingInfo page) {
        return dao.listByHQLAndPage(hql, params, page);
    }

    @Override
    public List<T> listByNameHQLQueryAndPage(String queryName, Map<String, Object> params,
                                             PagingInfo pagingInfo) {
        return dao.listByNameHQLQuery(queryName, params, pagingInfo);
    }

    @Override
    @Transactional
    public int updateByHQL(String hql, Map<String, Object> params) {
        return dao.updateByHQL(hql, params);
    }

    @Override
    public List<T> listByNameSQLQuery(String queryName, Map<String, Object> params) {
        return dao.listByNameSQLQuery(queryName, params);
    }

    @Override
    public List<T> listByNameSQLQueryAndPage(String queryName, Map<String, Object> params,
                                             PagingInfo pagingInfo,
                                             String orderBy) {
        return dao.listByNameSQLQuery(queryName, params, pagingInfo);
    }


    @Override
    public <ITEM extends BaseQueryItem> List<ITEM> listItemByNameHQLQuery(String queryName,
                                                                          Class<ITEM> itemClass,
                                                                          Map<String, Object> values,
                                                                          PagingInfo pagingInfo) {
        return dao.listItemByNameHQLQuery(queryName, itemClass, values, pagingInfo);
    }

    @Override
    public <ITEM extends BaseQueryItem> List<ITEM> listItemByNameSQLQuery(String queryName,
                                                                          Class<ITEM> itemClass,
                                                                          Map<String, Object> values,
                                                                          PagingInfo pagingInfo) {
        return dao.listItemByNameSQLQuery(queryName, itemClass, values, pagingInfo);
    }

    @Override
    public <ITEM extends BaseQueryItem> List<ITEM> listItemHqlQuery(String hql, Class<ITEM> itemClass, Map<String, Object> values) {
        return dao.listItemHqlQuery(hql, itemClass, values);
    }

    @Override
    public List<QueryItem> listQueryItemByNameHQLQuery(String queryName, Map<String, Object> values,
                                                       PagingInfo pagingInfo) {
        return dao.listQueryItemByNameHQLQuery(queryName, values, pagingInfo);
    }

    @Override
    public List<QueryItem> listQueryItemByHQL(String hql, Map<String, Object> values,
                                              PagingInfo pagingInfo) {
        return dao.listQueryItemByHQL(hql, values, pagingInfo);
    }

    @Override
    public List<QueryItem> listQueryItemBySQL(String sql, Map<String, Object> values,
                                              PagingInfo pagingInfo) {
        return dao.listQueryItemBySQL(sql, values, pagingInfo);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.service.JpaService#listQueryItemByNameSQLQuery(java.lang.String, java.util.Map, com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public List<QueryItem> listQueryItemByNameSQLQuery(String queryName, Map<String, Object> values,
                                                       PagingInfo pagingInfo) {
        return dao.listQueryItemByNameSQLQuery(queryName, values, pagingInfo);
    }

    @Override
    public void clearSession() {
        this.dao.getSession().clear();
    }

    @Override
    public void flushSession() {
        this.dao.getSession().flush();
    }

    @Override
    public void refreshEntity(T entity, LockOptions lockOptions) {
        this.dao.getSession().refresh(entity, lockOptions);
    }

    @Override
    public Clob convertString2Clob(String str) {
        return Hibernate.getLobCreator(this.dao.getSession()).createClob(str);
    }

    @Override
    public Long countByNamedHQLQuery(String queryName, Map<String, Object> params) {
        return this.dao.countByNamedHQLQuery(queryName, params);
    }

    @Override
    public Long countByNamedSQLQuery(String queryName, Map<String, Object> params) {
        return this.dao.countByNamedSQLQuery(queryName, params);
    }
}
