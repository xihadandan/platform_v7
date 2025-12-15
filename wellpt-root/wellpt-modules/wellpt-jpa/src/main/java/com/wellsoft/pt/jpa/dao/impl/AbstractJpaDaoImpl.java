/*
 * @(#)2017年12月21日 V1.0
 *
 * Copyright 2017 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.dao.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.entity.BaseEntity;
import com.wellsoft.context.jdbc.entity.CommonEntity;
import com.wellsoft.context.jdbc.entity.Entity;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.jdbc.support.*;
import com.wellsoft.context.jdbc.support.PropertyFilter.MatchType;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import com.wellsoft.pt.jpa.dao.JpaDao;
import com.wellsoft.pt.jpa.hibernate.SessionFactoryRegistrar;
import com.wellsoft.pt.jpa.hibernate4.NamedQueryScriptLoader;
import com.wellsoft.pt.jpa.support.BeanPropertyResultTransformer;
import com.wellsoft.pt.jpa.support.QueryItemResultTransformer;
import com.wellsoft.pt.jpa.support.SupportMultiDatabaseConfigurationBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.service.spi.ServiceException;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 基础dao服务
 *
 * @param <T>
 * @param <UUID>
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
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractJpaDaoImpl<T extends JpaEntity, UUID extends Serializable> implements
        JpaDao<T, UUID> {

    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractJpaDaoImpl.class);

    private static final ConcurrentHashMap<String, JpaDao> DAO_MAP = new ConcurrentHashMap<String, JpaDao>();

    private static final Map<Class<?>, ResultTransformer> itemTransformerMap = new HashMap<Class<?>, ResultTransformer>();
    private static final Map<String, ResultTransformer> entityTransformerMap = new HashMap<String, ResultTransformer>();
    private static String DATABASE_TYPE_SUFFIX = "";//命名查询前缀：特定数据库的查询

    static {
        itemTransformerMap.put(String.class, null);
        itemTransformerMap.put(Boolean.class, null);
        itemTransformerMap.put(Character.class, null);
        itemTransformerMap.put(Byte.class, null);
        itemTransformerMap.put(Short.class, null);
        itemTransformerMap.put(Integer.class, null);
        itemTransformerMap.put(Long.class, null);
        itemTransformerMap.put(Float.class, null);
        itemTransformerMap.put(Double.class, null);
    }

    protected SessionFactory sessionFactory;
    protected String sessionFactoryName;
    protected String multiTenancyStrategy;
    Class<T> entityClass;

    public AbstractJpaDaoImpl() {
        super();
        try {
            Type genType = this.getClass().getGenericSuperclass();
            if (genType instanceof ParameterizedType) {
                Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
                entityClass = (Class<T>) params[0];
            }

        } catch (Exception e) {
            LOGGER.error("AbstractJpaDaoImpl 实例化异常：", e);
            throw Throwables.propagate(e);
        }
    }

    /**
     * 通过命名查询并且指定sessionFactory，动态生成hql
     *
     * @param sessionFactory
     * @param queryName      命名查询名称
     * @param values         参数-值映射
     * @return
     */
    protected static String generateDynamicNamedQueryString(SessionFactory sessionFactory,
                                                            String queryName,
                                                            Map<String, Object> values) {

        return NamedQueryScriptLoader.generateDynamicNamedQueryString(sessionFactory, queryName,
                values);
    }

    /**
     * 初始化指定sessionFactory
     * 该方法可以在子类覆盖重写，实现不同子类不同sessionFactory的策略
     */
    protected void initSessionFactory() {
        String sessionFactoryBeanName = Config.MULTI_TENANCY
                && this.entityClass.getAnnotation(
                CommonEntity.class) != null ? Config.COMMON_SESSION_FACTORY_BEAN_NAME
                : Config.TENANT_SESSION_FACTORY_BEAN_NAME;
        try {
            bindSessionFactory(sessionFactoryBeanName,
                    Config.getValue(Config.KEY_MULTI_TENANCY_STRATEGY));
        } catch (Exception e) {
            LOGGER.error(Throwables.getStackTraceAsString(e));
            bindSessionFactory(Config.TENANT_SESSION_FACTORY_BEAN_NAME,
                    Config.getValue(Config.KEY_MULTI_TENANCY_STRATEGY));

        }

    }

    @PostConstruct
    public void init() {
        initSessionFactory();
        DATABASE_TYPE_SUFFIX = SupportMultiDatabaseConfigurationBuilder.resolveDatabaseType(
                ((SessionFactoryImpl) sessionFactory).getDialect());
    }

    /**
     * 根据主键uuid，获取数据
     *
     * @param id
     * @return
     */
    @Override
    public T getOne(UUID uuid) {
        return (T) getSession().get(entityClass, uuid);
    }

    @Override
    public T getLockOne(UUID uuid, LockOptions lockOptions) {
        try {
            return (T) getSession().get(entityClass, uuid, lockOptions);
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 按属性查找唯一对象, 匹配方式为相等.
     */
    public T getUniqueBy(final String propertyName, final Object value) {
        List<T> results = (List<T>) listByFieldEqValue(propertyName, value);
        return CollectionUtils.isNotEmpty(results) ? results.get(0) : null;
    }

    /**
     * 根据HQL命名查询获取一条数据
     *
     * @param queryName 命名查询名称
     * @param values    参数-值
     * @return
     */
    @Override
    public T getOneByNameHQLQuery(String queryName, Map<String, Object> values) {
        String hql = generateDynamicNamedQueryString(sessionFactory, queryName, values);
        List<T> results = (List<T>) this.listQuery(hql, values, entityClass, null, true);
        return CollectionUtils.isNotEmpty(results) ? results.get(0) : null;
    }

    /**
     * 根据SQL命名查询获取一条数据
     *
     * @param queryName
     * @param values
     * @return
     */
    @Override
    public T getOneByNameSQLQuery(String queryName, Map<String, Object> values) {
        String sql = generateDynamicNamedQueryString(sessionFactory, queryName, values);
        List<T> results = (List<T>) this.listQuery(sql, values, entityClass, null, false);
        return CollectionUtils.isNotEmpty(results) ? results.get(0) : null;
    }

    /**
     * 根据命名查询获取数据
     *
     * @param queryName 命名查询名称
     * @param values    参数-值
     * @return
     */
    @Override
    public List<T> listByNameHQLQuery(String queryName, Map<String, Object> values) {
        return this.listByNameHQLQuery(queryName, values, null);
    }

    @Override
    public List<T> listByNameSQLQuery(String queryName, Map<String, Object> values) {
        return this.listByNameSQLQuery(queryName, values, null);
    }

    @Override
    public List<T> listByNameSQLQuery(String queryName, Map<String, Object> values,
                                      PagingInfo pagingInfo) {
        String sql = generateDynamicNamedQueryString(sessionFactory, queryName, values);
        return (List<T>) this.listQuery(sql, values, entityClass, pagingInfo, false);
    }

    /**
     * 根据命名查询获取数据
     *
     * @param queryName  命名查询名称
     * @param values     参数-值
     * @param pagingInfo 分页
     * @return
     */
    public List<T> listByNameHQLQuery(String queryName, final Map<String, Object> values,
                                      PagingInfo pagingInfo) {
        String hql = generateDynamicNamedQueryString(sessionFactory, queryName, values);
        return (List<T>) this.listQuery(hql, values, entityClass, pagingInfo, true);
    }

    /**
     * 根据HQL命名查询获取数据
     *
     * @param queryName  命名查询名称
     * @param values     参数-值
     * @param pagingInfo 分页
     * @return
     */
    public List<QueryItem> listQueryItemByNameHQLQuery(String queryName,
                                                       final Map<String, Object> values,
                                                       PagingInfo pagingInfo) {
        // 条件与排序信息在HQL中
        String hql = generateDynamicNamedQueryString(sessionFactory, queryName, values);
        return (List<QueryItem>) this.listQuery(hql, values, QueryItem.class, pagingInfo, true);
    }

    public <ITEM extends BaseQueryItem> List<ITEM> listItemByNameSQLQuery(String queryName,
                                                                          Class<ITEM> itemClass,
                                                                          Map<String, Object> values,
                                                                          PagingInfo pagingInfo) {
        // 条件与排序信息在SQL中
        String sql = generateDynamicNamedQueryString(sessionFactory, queryName, values);
        return (List<ITEM>) this.listQuery(sql, values, itemClass, pagingInfo, false);
    }

    @Override
    public <ITEM extends BaseQueryItem> List<ITEM> listItemHqlQuery(String hql, Class<ITEM> itemClass, Map<String, Object> values) {

        return this.listQuery(hql, values, itemClass, null, true);
    }

    public <ITEM extends BaseQueryItem> List<ITEM> listItemByNameHQLQuery(String queryName,
                                                                          Class<ITEM> itemClass,
                                                                          Map<String, Object> values,
                                                                          PagingInfo pagingInfo) {
        // 条件与排序信息在HQL中
        String hql = generateDynamicNamedQueryString(sessionFactory, queryName, values);
        return (List<ITEM>) this.listQuery(hql, values, itemClass, pagingInfo, true);
    }

    @Override
    public List<QueryItem> listQueryItemByHQL(String hql, Map<String, Object> values,
                                              PagingInfo pagingInfo) {
        return (List<QueryItem>) this.listQuery(hql, values, QueryItem.class, pagingInfo, true);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.jpa.dao.JpaDao#listQueryItemBySQL(java.lang.String, java.util.Map, com.wellsoft.context.jdbc.support.PagingInfo)
     */
    @Override
    public List<QueryItem> listQueryItemBySQL(String sql, Map<String, Object> values,
                                              PagingInfo pagingInfo) {
        return (List<QueryItem>) this.listQuery(sql, values, QueryItem.class, pagingInfo, false);
    }

    /**
     * 根据SQL命名查询获取数据
     *
     * @param queryName  命名查询名称
     * @param values     参数-值
     * @param pagingInfo 分页
     * @return
     */
    public List<QueryItem> listQueryItemByNameSQLQuery(String queryName,
                                                       final Map<String, Object> values,
                                                       PagingInfo pagingInfo) {
        // 条件与排序信息在HQL中
        String hql = generateDynamicNamedQueryString(sessionFactory, queryName, values);
        return (List<QueryItem>) this.listQuery(hql, values, QueryItem.class, pagingInfo, false);
    }

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
    public List<QueryItem> listQueryItemByNameSQLQueryHash(String queryName, final Map<String, Object> values, PagingInfo pagingInfo) {
        // 条件与排序信息在HQL中
        String hql = NamedQueryScriptLoader.generateDynamicNamedQueryString(sessionFactory, queryName, values, true);
        return (List<QueryItem>) this.listQuery(hql, values, QueryItem.class, pagingInfo, false);
    }

    private <T> List<T> listQuery(String queryLang, final Map<String, Object> values,
                                  Class<T> itemClass,
                                  PagingInfo pagingInfo, boolean isHQL) {
        // 条件与排序信息在HQL中
        Query query = null;
        if (isHQL) {
            query = getSession().createQuery(queryLang);
        } else {
            query = getSession().createSQLQuery(queryLang);
        }

        query.setProperties(values);

        // 分页
        if (pagingInfo != null) {
            if (pagingInfo.isAutoCount()) {
                long totalCount = isHQL ? countByHQL(queryLang, values) : countBySQL(queryLang,
                        values);
                pagingInfo.setTotalCount(totalCount);
            }
            // hibernate的firstResult的序号从0开始
            query.setFirstResult(pagingInfo.getFirst());
            query.setMaxResults(pagingInfo.getPageSize());
        }

        // 结果类型转换
        if (itemClass == null) {
            // hibernate默认处理
        } else if (QueryItem.class.isAssignableFrom(itemClass)) {
            query.setResultTransformer(QueryItemResultTransformer.INSTANCE);
        } else if (BaseEntity.class.isAssignableFrom(itemClass) || Entity.class.isAssignableFrom(itemClass)) {
            if (isHQL) {
                // 别名查询转换为实体
                if (queryLang.trim().substring(0, 6).toLowerCase().equals("select")) {
                    org.hibernate.type.Type[] types = query.getReturnTypes();
                    if (types.length == 1) {
                        org.hibernate.type.Type type = types[0];
                        if (itemClass.equals(type.getReturnedClass())) {
                        } else {
                            query.setResultTransformer(getEntityTransformerMap(itemClass, queryLang));
                        }
                    } else {
                        query.setResultTransformer(getEntityTransformerMap(itemClass, queryLang));
                    }
                }
            } else {
                query.setResultTransformer(getItemTransformerMap(itemClass));
            }
        } else {
            query.setResultTransformer(getItemTransformerMap(itemClass));
        }
        return query.list();
    }

    private ResultTransformer getEntityTransformerMap(Class<?> entityClass, String queryLang) {
        String key = entityClass.getCanonicalName() + "_" + StringUtils.substringBefore(queryLang, "from");
        if (!entityTransformerMap.containsKey(key)) {
            entityTransformerMap.put(key, new AliasToBeanResultTransformer(entityClass));
        }
        return entityTransformerMap.get(key);
    }

    private ResultTransformer getItemTransformerMap(Class<?> itemClass) {
        if (!itemTransformerMap.containsKey(itemClass)) {
            itemTransformerMap.put(itemClass, new BeanPropertyResultTransformer(itemClass));
        }
        return itemTransformerMap.get(itemClass);
    }

    /**
     * 根据hql语句统计数量
     *
     * @param hql
     * @param values 参数-值映射
     * @return
     */
    public long countByHQL(final String hql, final Map<String, Object> values) {
        String countHql = prepareCountHql(hql);
        try {
            Long count = (Long) createHQLQuery(countHql, values).uniqueResult();
            return count;
        } catch (Exception e) {
            throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
        }
    }

    public long countBySQL(final String sql, final Map<String, Object> values) {
        String countHql = prepareCountHql(sql);

        try {
            Object result = createSQLQuery(countHql, values).uniqueResult();
            if (result instanceof BigInteger) {
                return ((BigInteger) result).longValue();
            }
            return ((BigDecimal) result).longValue();
        } catch (Exception e) {
            throw new RuntimeException("sql can't be auto count, sql is:" + countHql, e);
        }
    }

    @Override
    public long countByEntity(T entity) {
        Criteria criteria = getSession().createCriteria(ClassUtils.getUserClass(entity)).add(
                Example.create(entity));
        return countCriteriaResult(criteria);
    }

    private String prepareCountHql(String orgHql) {
        String fromHql = orgHql;
        // select子句与order by子句会影响count查询,进行简单的排除.
        boolean hasCount = orgHql.indexOf("count(") != -1;
        if (!hasCount) {
            if (StringUtils.startsWith(orgHql, "from")) {
                fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
            } else {
                fromHql = "from " + StringUtils.substringAfter(fromHql.replaceAll("\n", " ").replaceAll("\t", " "), " from ");
            }
        }
        fromHql = StringUtils.substringBefore(fromHql, "order by");
        String countHql = (!hasCount ? "select count(1) " : "") + fromHql + "  ";
        return countHql;
    }

    /**
     * 根据hql创建Query
     *
     * @param hql
     * @param values 参数-值映射
     * @return
     */
    protected Query createHQLQuery(final String hql, final Map<String, Object> params) {
        Assert.hasText(hql, "hql不能为空");
        Query query = getSession().createQuery(hql);
        if (MapUtils.isNotEmpty(params)) {
            query.setProperties(params);
        }
        return query;
    }

    /**
     * 根据sql创建Query
     *
     * @param queryString
     * @param values
     * @return
     */
    protected Query createSQLQuery(final String sql, final Map<String, Object> params) {
        Assert.hasText(sql, "sql不能为空");
        Query query = getSession().createSQLQuery(sql);
        if (MapUtils.isNotEmpty(params)) {
            query.setProperties(params);
        }
        return query;
    }

    /**
     * 批量循环保存实体类
     *
     * @param entities
     */
    public void saveAll(Collection<T> entities) {
        int count = 0;
        for (T entity : entities) {
            save(entity);
            if (++count % 20 == 0) {
                flushSession();
                getSession().clear();
            }
        }
    }

    /**
     * 保存或更新
     *
     * @param entity
     */
    public void save(T entity) {
        if (entity.getUuid() != null && StringUtils.isBlank(entity.getUuid().toString())) {
            entity.setUuid(null);
        }
        getSession().saveOrUpdate(entity);
    }

    /**
     * 保存或更新
     *
     * @param entity
     */
    public void update(T entity) {
        getSession().saveOrUpdate(entity);
    }

    /**
     * 根据主键值集合，删除数据
     *
     * @param uuids
     */
    public void deleteByUuids(Collection<UUID> uuids) {
        for (UUID uuid : uuids) {
            delete(uuid);
        }
    }

    /**
     * 根据主键删除数据
     *
     * @param uuid
     */
    public void delete(UUID uuid) {
        delete(getOne(uuid));
    }

    /**
     * 根据实体删除数据
     *
     * @param entity
     */
    public void delete(T entity) {
        getSession().delete(entity);
    }

    public void deleteByEntities(Collection<T> entities) {
        for (T t : entities) {
            delete(t);
        }
    }

    /**
     * 刷新session
     */
    public void flushSession() {
        getSession().flush();
    }

    /**
     * 根据hql，查询数据集合
     *
     * @param hql
     * @param params 参数-值映射
     * @return
     */
    public List<T> listByHQL(String hql, Map<String, Object> params) {
        Query query = getSession().createQuery(hql);
        if (MapUtils.isNotEmpty(params)) {
            query.setProperties(params);
        }
        return query.list();
    }

    public List<T> listByHQLAndPage(String hql, Map<String, Object> params, PagingInfo page) {
        return (List<T>) this.listQuery(hql, params, entityClass, page, true);
    }

    public List<T> listBySQLAndPage(String sql, Map<String, Object> params, PagingInfo page) {
        return (List<T>) this.listQuery(sql, params, entityClass, page, false);
    }

    public T getOneByHQL(String hql, Map<String, Object> params) {
        Query query = getSession().createQuery(hql);
        if (MapUtils.isNotEmpty(params)) {
            query.setProperties(params);
        }
        return (T) query.uniqueResult();
    }

    public T getOneBySQL(String sql, Map<String, Object> params) {
        return (T) createSQLQuery(sql, params).setResultTransformer(
                getItemTransformerMap(entityClass)).uniqueResult();
    }

    @Deprecated
    public <NBR extends Number> NBR getNumberBySQL(String sql, Map<String, Object> params) {
        Query query = createSQLQuery(sql, params);
        return (NBR) query.uniqueResult();
    }

    public <NBR extends Number> NBR getNumberBySQL(String sql, Map<String, Object> params, Class<NBR> nbrClass) {
        Query query = createSQLQuery(sql, params);
        return org.springframework.util.NumberUtils.parseNumber(query.uniqueResult().toString(), nbrClass);
    }


    @Deprecated
    public <NBR extends Number> NBR getNumberByHQL(String hql, Map<String, Object> params) {
        Query query = createHQLQuery(hql, params);
        return (NBR) query.uniqueResult();
    }

    public <NBR extends Number> NBR getNumberByHQL(String hql, Map<String, Object> params, Class<NBR> nbrClass) {
        Query query = createHQLQuery(hql, params);
        Object result = query.uniqueResult();
        return result != null ? org.springframework.util.NumberUtils.parseNumber(result.toString(), nbrClass) : null;
    }

    public <NBR extends Number> List<NBR> listNumberBySQL(String sql, Map<String, Object> params) {
        Query query = createSQLQuery(sql, params);
        return (List<NBR>) query.list();
    }

    public <NBR extends Number> List<NBR> listNumberByHQL(String hql, Map<String, Object> params) {
        Query query = createHQLQuery(hql, params);
        return (List<NBR>) query.list();
    }

    public <NBR extends Number> List<NBR> listNumberByHQL(String hql, Map<String, Object> params, Class<NBR> nbrClass) {
        Query query = createHQLQuery(hql, params);
        query.setResultTransformer(new ResultTransformer() {
            @Override
            public Object transformTuple(Object[] objects, String[] strings) {
                List<NBR> list = Lists.newArrayList();
                for (Object obj : objects) {
                    if (obj != null) {
                        list.add(org.springframework.util.NumberUtils.parseNumber(obj.toString(), nbrClass));
                    }
                }
                return list;
            }

            @Override
            public List transformList(List list) {
                return null;
            }
        });
        return (List<NBR>) query.list();
    }

    public <CHAR> CHAR getCharSequenceBySQL(String sql, Map<String, Object> params) {
        Query query = createSQLQuery(sql, params);
        return (CHAR) query.uniqueResult();
    }

    public <CHAR> CHAR getCharSequenceByHQL(String hql, Map<String, Object> params) {
        Query query = createHQLQuery(hql, params);
        return (CHAR) query.uniqueResult();
    }

    public <CHAR> List<CHAR> listCharSequenceBySQL(String sql, Map<String, Object> params) {
        Query query = createSQLQuery(sql, params);
        return (List<CHAR>) query.list();
    }

    public <CHAR> List<CHAR> listCharSequenceByHQL(String hql, Map<String, Object> params) {
        Query query = createHQLQuery(hql, params);
        return (List<CHAR>) query.list();
    }


    public List<T> listBySQL(String sql, Map<String, Object> params) {
        return createSQLQuery(sql, params).setResultTransformer(
                getItemTransformerMap(entityClass)).list();
    }

    private List<T> listByFieldMatchValue(String field, Object value, final MatchType matchType) {
        Criteria criteria = getSession().createCriteria(entityClass);
        criteria.add(buildCriterion(field, value, matchType));
        return criteria.list();
    }

    /**
     * 基于“字段名=值”的条件检索数据
     *
     * @param field 实体字段名
     * @param value 值
     * @return
     */
    public List<T> listByFieldEqValue(String field, Object value) {
        return this.listByFieldMatchValue(field, value, MatchType.EQ);
    }

    /**
     * 基于“字段名!=值”的条件检索数据
     *
     * @param field 实体字段名
     * @param value 值
     * @return
     */
    public List<T> listByFieldNotEqValue(String field, Object value) {
        Criteria criteria = getSession().createCriteria(entityClass);
        criteria.add(Restrictions.not(buildCriterion(field, value, MatchType.EQ)));
        return criteria.list();
    }

    /**
     * 基于“ 字段名  not in (值1,值2,...) ”的条件检索数据
     *
     * @param field 实体字段名
     * @param value 值
     * @return
     */
    public List<T> listByFieldNotInValues(String field, List<Object> value) {
        if (CollectionUtils.isNotEmpty(value) && value.size() > 1000) {
            throw new ServiceException("in查询数量不能超过1000");
        }
        Criteria criteria = getSession().createCriteria(entityClass);
        criteria.add(Restrictions.not(Restrictions.in(field, value)));
        return criteria.list();
    }

    /**
     * 基于“字段名” 为空值的条件检索
     *
     * @param field
     * @return
     */
    public List<T> listByFieldIsNull(String field) {
        Criteria criteria = getSession().createCriteria(entityClass);
        criteria.add(Restrictions.isNull(field));
        return criteria.list();
    }

    /**
     * 基于“字段名” 不为空值的条件检索
     *
     * @param field
     * @return
     */
    public List<T> listByFieldIsNotNull(String field) {
        Criteria criteria = getSession().createCriteria(entityClass);
        criteria.add(Restrictions.isNotNull(field));
        return criteria.list();
    }


    /**
     * 基于“字段名” 左like 查询： '值%'
     *
     * @param field
     * @param value
     * @return
     */
    @Override
    public List<T> listByFieldStartLike(String field, String value) {
        Criteria criteria = getSession().createCriteria(entityClass);
        criteria.add(Restrictions.like(field, value, MatchMode.START));
        return criteria.list();
    }

    /**
     * 基于“字段名” 右like 查询： '%值'
     *
     * @param field
     * @param value
     * @return
     */
    @Override
    public List<T> listByFieldEndLike(String field, String value) {
        Criteria criteria = getSession().createCriteria(entityClass);
        criteria.add(Restrictions.like(field, value, MatchMode.END));
        return criteria.list();
    }


    /**
     * 基于“字段名” 全局like 查询： '%值'
     *
     * @param field
     * @param value
     * @return
     */
    @Override
    public List<T> listByFieldAnyLike(String field, String value) {
        Criteria criteria = getSession().createCriteria(entityClass);
        criteria.add(Restrictions.like(field, value, MatchMode.ANYWHERE));
        return criteria.list();
    }


    /**
     * 基于“ 字段名  in (值1,值2,...) ”的条件检索数据
     *
     * @param field 实体字段名
     * @param value 值
     * @return
     */
    public List<T> listByFieldInValues(String field, List<?> value) {
        Criteria criteria = getSession().createCriteria(entityClass);
        List<Criterion> idOrIn = Lists.newArrayList();
        int size = value.size();
        if (size > 1000) {
            int fromIndex = 0;
            int endIndex = 0;
            while (endIndex != size) {
                endIndex = fromIndex + 1000;
                if (endIndex > size) {
                    endIndex = size;
                }
                idOrIn.add(Restrictions.in(field, value.subList(fromIndex, endIndex)));
                fromIndex = endIndex;
            }
            criteria.add(Restrictions.or(idOrIn.toArray(new Criterion[]{})));
        } else {
            criteria.add(Restrictions.in(field, value));
        }
        return criteria.list();
    }

    /**
     * 根据hql更新数据
     *
     * @param hql
     * @param params
     * @return
     */
    public int updateByHQL(String hql, Map<String, Object> params) {
        return executeByHQL(hql, params);
    }

    /**
     * 根据hql删除数据
     *
     * @param hql
     * @param params
     * @return
     */
    public int deleteByHQL(String hql, Map<String, Object> params) {
        return executeByHQL(hql, params);
    }

    /**
     * 根据sql命名查询定义的语句进行删除
     *
     * @param queryName
     * @param params
     * @return
     */
    public int deleteByNamedSQL(String queryName, Map<String, Object> params) {
        return executeBySQL(generateDynamicNamedQueryString(sessionFactory, queryName, params),
                params);
    }

    /**
     * 根据hql命名查询定义的语句进行删除
     *
     * @param queryName
     * @param params
     * @return
     */
    public int deleteByNamedHQL(String queryName, Map<String, Object> params) {
        return executeByHQL(generateDynamicNamedQueryString(sessionFactory, queryName, params),
                params);
    }

    /**
     * 根据sql命名查询定义的语句进行更新
     *
     * @param queryName
     * @param params
     * @return
     */
    public int updateByNamedSQL(String queryName, Map<String, Object> params) {
        return executeBySQL(generateDynamicNamedQueryString(sessionFactory, queryName, params),
                params);
    }

    /**
     * 根据hql命名查询定义的语句进行更新
     *
     * @param queryName
     * @param params
     * @return
     */
    public int updateByNamedHQL(String queryName, Map<String, Object> params) {
        return executeByHQL(generateDynamicNamedQueryString(sessionFactory, queryName, params),
                params);
    }

    /**
     * 根据sql更新数据
     *
     * @param sql
     * @param params
     * @return
     */
    public int updateBySQL(String sql, Map<String, Object> params) {
        return executeBySQL(sql, params);
    }

    /**
     * 根据sql删除数据
     *
     * @param sql
     * @param params
     * @return
     */
    public int deleteBySQL(String sql, Map<String, Object> params) {
        return executeBySQL(sql, params);
    }

    private int executeByHQL(String hql, Map<String, Object> params) {
        return createHQLQuery(hql, params).executeUpdate();
    }

    private int executeBySQL(String sql, Map<String, Object> params) {
        return createSQLQuery(sql, params).executeUpdate();
    }

    /**
     * 根据实例参数，字段过滤，进行分页排序数据检索
     *
     * @param entityInst      实例参数
     * @param propertyFilters 字段过滤
     * @param orderBy         排序
     * @param pagingInfo      分页参数
     * @return
     */
    public List<T> listByEntity(T entityInst, List<PropertyFilter> propertyFilters, String orderBy,
                                PagingInfo pagingInfo) {
        Criteria criteria = getSession().createCriteria(ClassUtils.getUserClass(entityInst)).add(
                Example.create(entityInst));
        // 查询字段
        if (propertyFilters != null) {
            boolean queryOr = false;
            if (!propertyFilters.isEmpty()) {
                queryOr = propertyFilters.get(0).isQueryOr();
            }
            Criterion[] criterions = buildCriterionByPropertyFilter(propertyFilters, queryOr);
            for (Criterion criterion : criterions) {
                criteria.add(criterion);
            }
        }
        // 排序
        addOrder(criteria, orderBy);

        // 分页
        if (pagingInfo != null) {
            if (pagingInfo.isAutoCount()) {
                pagingInfo.setTotalCount(countCriteriaResult(criteria));
            }
            // hibernate的firstResult的序号从0开始
            criteria.setFirstResult(pagingInfo.getFirst());
            criteria.setMaxResults(pagingInfo.getPageSize());
        }
        return criteria.list();
    }

    @Override
    public List<T> listAllByOrderPage(PagingInfo pagingInfo, String orderBy) {
        Criteria criteria = getSession().createCriteria(entityClass);
        // 排序
        if (StringUtils.isNotEmpty(orderBy)) {
            addOrder(criteria, orderBy);
        }
        // 分页
        if (pagingInfo != null) {
            if (pagingInfo.isAutoCount()) {
                pagingInfo.setTotalCount(countCriteriaResult(criteria));
            }
            // hibernate的firstResult的序号从0开始
            criteria.setFirstResult(pagingInfo.getFirst());
            criteria.setMaxResults(pagingInfo.getPageSize());
        }

        return criteria.list();
    }

    protected long countCriteriaResult(final Criteria c) {
        CriteriaImpl impl = (CriteriaImpl) c;

        // 先把Projection、ResultTransformer、OrderBy取出来,清空三者后再执行Count操作
        Projection projection = impl.getProjection();
        ResultTransformer transformer = impl.getResultTransformer();

        List<CriteriaImpl.OrderEntry> orderEntries = null;
        try {
            orderEntries = (List) ReflectionUtils.getFieldValue(impl, "orderEntries");
            ReflectionUtils.setFieldValue(impl, "orderEntries", new ArrayList());
        } catch (Exception e) {
            LOGGER.error("不可能抛出的异常:{}", ExceptionUtils.getStackTrace(e));
        }

        // 执行Count查询
        Long totalCountObject = (Long) c.setProjection(Projections.rowCount()).uniqueResult();
        long totalCount = (totalCountObject != null) ? totalCountObject : 0;

        // 将之前的Projection,ResultTransformer和OrderBy条件重新设回去
        c.setProjection(projection);

        if (projection == null) {
            c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
        }
        if (transformer != null) {
            c.setResultTransformer(transformer);
        }
        try {
            ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);
        } catch (Exception e) {
            LOGGER.error("不可能抛出的异常:{}", ExceptionUtils.getStackTrace(e));
        }

        return totalCount;
    }

    private void addOrder(Criteria criteria, String orderBy) {
        if (orderBy != null) {
            String[] orderByArrays = StringUtils.split(orderBy, ',');
            for (int i = 0; i < orderByArrays.length; i++) {
                String[] orderBys = StringUtils.split(orderByArrays[i], ' ');
                String propertyName = orderBys[0];
                if (orderBys.length == 1) {
                    criteria.addOrder(Order.asc(propertyName));
                } else if (orderBys.length == 2) {
                    String order = orderBys[1];
                    if (Page.ASC.equalsIgnoreCase(order)) {
                        criteria.addOrder(Order.asc(propertyName));
                    } else if (Page.DESC.equalsIgnoreCase(order)) {
                        criteria.addOrder(Order.desc(propertyName));
                    } else {
                        throw new RuntimeException("Unknow order [" + order + "]");
                    }
                } else {
                    throw new RuntimeException("Unknow order by [" + orderByArrays[i] + "]");
                }
            }
        }
    }

    private Criterion[] buildCriterionByPropertyFilter(final List<PropertyFilter> filters,
                                                       boolean queryOr) {
        if (!queryOr) {
            return buildCriterionByPropertyFilter(filters);
        }
        List<Criterion> criterionList = new ArrayList<Criterion>();
        // 包含多个属性需要比较的情况,进行or处理.
        Disjunction disjunction = Restrictions.disjunction();
        for (PropertyFilter filter : filters) {
            if (!filter.hasMultiProperties()) { // 只有一个属性需要比较的情况.
                Criterion criterion = buildCriterion(filter.getPropertyName(),
                        filter.getMatchValue(),
                        filter.getMatchType());
                disjunction.add(criterion);
                criterionList.add(disjunction);
            } else {
                for (String param : filter.getPropertyNames()) {
                    Criterion criterion = buildCriterion(param, filter.getMatchValue(),
                            filter.getMatchType());
                    disjunction.add(criterion);
                }
                criterionList.add(disjunction);
            }
        }
        return criterionList.toArray(new Criterion[criterionList.size()]);
    }

    private Criterion[] buildCriterionByPropertyFilter(final List<PropertyFilter> filters) {
        List<Criterion> criterionList = new ArrayList<Criterion>();
        for (PropertyFilter filter : filters) {
            if (!filter.hasMultiProperties()) { // 只有一个属性需要比较的情况.
                Criterion criterion = buildCriterion(filter.getPropertyName(),
                        filter.getMatchValue(),
                        filter.getMatchType());
                criterionList.add(criterion);
            } else {// 包含多个属性需要比较的情况,进行or处理.
                Disjunction disjunction = Restrictions.disjunction();
                for (String param : filter.getPropertyNames()) {
                    Criterion criterion = buildCriterion(param, filter.getMatchValue(),
                            filter.getMatchType());
                    disjunction.add(criterion);
                }
                criterionList.add(disjunction);
            }
        }
        return criterionList.toArray(new Criterion[criterionList.size()]);
    }

    /**
     * 根据实例参数查询数据
     *
     * @param entity 实例参数
     * @return
     */
    public List<T> listByEntity(T entity) {
        return this.listByEntity(entity, null, null, null);
    }

    /**
     * 根据实例参数分页查询数据
     *
     * @param entity     实例参数
     * @param pagingInfo 分页参数
     * @param orderBy    排序
     * @return
     */
    public List<T> listByEntityAndPage(T entity, PagingInfo pagingInfo, String orderBy) {
        return this.listByEntity(entity, null, orderBy, pagingInfo);
    }

    /**
     * 根据字段-值，匹配类型构建查询条件
     *
     * @param propertyName  字段名
     * @param propertyValue 字段值
     * @param matchType     匹配类型
     * @return
     */
    private Criterion buildCriterion(final String propertyName, final Object propertyValue,
                                     final MatchType matchType) {
        Assert.hasText(propertyName, "propertyName不能为空");
        SimpleExpression criterion = null;
        // 根据MatchType构造criterion
        switch (matchType) {
            case EQ:
                criterion = Restrictions.eq(propertyName, propertyValue);
                break;
            case LIKE:
                criterion = Restrictions.like(propertyName, (String) propertyValue,
                        MatchMode.ANYWHERE);
                break;

            case LE:
                criterion = Restrictions.le(propertyName, propertyValue);
                break;
            case LT:
                criterion = Restrictions.lt(propertyName, propertyValue);
                break;
            case GE:
                criterion = Restrictions.ge(propertyName, propertyValue);
                break;
            case GT:
                criterion = Restrictions.gt(propertyName, propertyValue);
        }
        //FIXME: 会影响字段的索引结果
//        if (propertyValue != null && String.class.isAssignableFrom(propertyValue.getClass())) {
//            criterion.ignoreCase();
//        }
        return criterion;
    }

    @Override
    public Date getSysDate() {
        SessionFactoryImplementor sessionFactoryImplementor = (SessionFactoryImplementor) this.sessionFactory;
        String currentTimestampSelectString = sessionFactoryImplementor.getDialect().getCurrentTimestampSelectString();
        SQLQuery queryObject = getSession().createSQLQuery(currentTimestampSelectString);
        return (Date) queryObject.uniqueResult();
    }

    /**
     * 绑定dao的sessionFactory与多租户策略
     *
     * @param sessionFactory
     * @param multiTenancyStrategy
     */
    protected void bindSessionFactory(String sessionFactoryName, String multiTenancyStrategy) {
        SessionFactory sessionFactory = SessionFactoryRegistrar.get(sessionFactoryName);
        if (sessionFactory == null) {
            sessionFactory = ApplicationContextHolder.getBean(sessionFactoryName,
                    SessionFactory.class);
        }
        this.sessionFactoryName = sessionFactoryName;
        setSessionFactory(sessionFactory, multiTenancyStrategy);
    }

    /**
     * 获取session
     *
     * @return
     */
    public Session getSession() {
        if (Config.MULTI_TENANCY_STRATEGY_SESSION_FACTORY.equals(multiTenancyStrategy)) {
            SessionFactory sf = SessionFactoryRegistrar.getCurrentTenantSessionFactory();
            if (sf != null) {
                return sf.getCurrentSession();
            }
        }
        return sessionFactory.getCurrentSession();
    }

    private void setSessionFactory(SessionFactory sessionFactory, String multiTenancyStrategy) {
        this.sessionFactory = sessionFactory;
        this.multiTenancyStrategy = multiTenancyStrategy;
    }

    /**
     * 根据指定的sessionFactory获取dao
     *
     * @param sessionFactoryId
     * @return
     */
    public JpaDao getDaoBySessionFactory(String sessionFactoryId) {
        if (sessionFactoryName != null && sessionFactoryName.equals(sessionFactoryId)) {
            return this;
        }
        if (!DAO_MAP.containsKey(sessionFactoryId)) {
            SessionFactory sessionFactory = SessionFactoryRegistrar.get(sessionFactoryId);
            if (sessionFactory == null) {
                sessionFactory = ApplicationContextHolder.getBean(sessionFactoryId,
                        SessionFactory.class);
            }
            try {
                AbstractJpaDaoImpl jpaDao = this.getClass().newInstance();
                jpaDao.setSessionFactory(sessionFactory, null);
                DAO_MAP.put(sessionFactoryId, jpaDao);
            } catch (Exception e) {
                LOGGER.error("获取dao异常：", e);
            }

        }
        return DAO_MAP.get(sessionFactoryId);
    }


    @Override
    public SessionFactory getSessionFactory(String sessionFactoryId) {
        SessionFactory sessionFactory = SessionFactoryRegistrar.get(sessionFactoryId);
        if (sessionFactory == null) {
            sessionFactory = ApplicationContextHolder.getBean(sessionFactoryId,
                    SessionFactory.class);
        }
        return sessionFactory;
    }

    @Override
    public <CHAR> List<CHAR> listCharSequenceByHqlAndPage(String hql, Map<String, Object> params,
                                                          PagingInfo pagingInfo) {
        Query query = createHQLQuery(hql, params);
        // 分页
        if (pagingInfo != null) {
            if (pagingInfo.isAutoCount()) {
                long totalCount = countByHQL(hql, params);
                pagingInfo.setTotalCount(totalCount);
            }
            // hibernate的firstResult的序号从0开始
            query.setFirstResult(pagingInfo.getFirst());
            query.setMaxResults(pagingInfo.getPageSize());
        }
        return (List<CHAR>) query.list();
    }


    public SessionFactory sessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public T getOneByFieldEq(String field, Object value) {
        List<T> list = this.listByFieldEqValue(field, value);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    @Override
    public Long countByNamedHQLQuery(String queryName, Map<String, Object> params) {
        return this.countByHQL(NamedQueryScriptLoader.generateDynamicNamedQueryString(sessionFactory, queryName, params), params);
    }

    @Override
    public Long countByNamedSQLQuery(String queryName, Map<String, Object> params) {
        return this.countBySQL(NamedQueryScriptLoader.generateDynamicNamedQueryString(sessionFactory, queryName, params), params);
    }
}
