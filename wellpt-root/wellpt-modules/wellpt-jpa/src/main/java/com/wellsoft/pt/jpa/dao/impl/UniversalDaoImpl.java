/*
 * @(#)2014-7-7 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.dao.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.jdbc.entity.BaseEntity;
import com.wellsoft.context.jdbc.entity.JpaEntity;
import com.wellsoft.context.jdbc.support.Page;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.PropertyFilter;
import com.wellsoft.context.jdbc.support.PropertyFilter.MatchType;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.context.util.reflection.ReflectionUtils;
import com.wellsoft.pt.jpa.criteria.EntityCriteria;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import com.wellsoft.pt.jpa.hibernate.SessionFactoryRegistrar;
import com.wellsoft.pt.jpa.support.QueryItemResultTransformer;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.*;
import org.hibernate.criterion.*;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.*;

/**
 * Description: 租户库通用DAO接口
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
@Repository
@Scope(value = "prototype")
public class UniversalDaoImpl extends BaseDaoImpl implements UniversalDao {
    // protected Logger logger = LoggerFactory.getLogger(getClass());

    private SessionFactory sessionFactory;

    private String multiTenancyStrategy;

    public UniversalDaoImpl() {
        super();
    }

    /**
     *
     */
    @PostConstruct
    public void init() {
        sessionFactory = (SessionFactory) ApplicationContextHolder.getBean(Config.TENANT_SESSION_FACTORY_BEAN_NAME);
        multiTenancyStrategy = Config.getValue(Config.KEY_MULTI_TENANCY_STRATEGY);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#setSessionFactory(org.hibernate.SessionFactory)
     */
    @Override
    public void setSessionFactory(SessionFactory sessionFactory, String multiTenancyStrategy) {
        this.sessionFactory = sessionFactory;
        this.multiTenancyStrategy = multiTenancyStrategy;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#save(java.io.Serializable)
     */
    @Override
    public <ENTITY extends Serializable> void save(ENTITY entity) {
        if (entity instanceof BaseEntity) {
            BaseEntity identity = (BaseEntity) entity;
            if (StringUtils.isBlank(identity.getUuid())) {
                identity.setUuid(null);
            }
        }
        getSession().saveOrUpdate(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#saveAll(java.util.Collection)
     */
    @Override
    public <ENTITY extends Serializable> void saveAll(Collection<ENTITY> entities) {
        for (ENTITY entity : entities) {
            getSession().saveOrUpdate(entity);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#delete(java.io.Serializable)
     */
    @Override
    public <ENTITY extends Serializable> void delete(ENTITY entity) {
        getSession().delete(entity);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#deleteAll(java.util.Collection)
     */
    @Override
    public <ENTITY extends Serializable> void deleteAll(Collection<ENTITY> entities) {
        for (ENTITY entity : entities) {
            getSession().delete(entity);
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#deleteByPk(java.lang.Class, java.io.Serializable)
     */
    @Override
    public <ENTITY extends Serializable> void deleteByPk(Class<ENTITY> entityClass, Serializable id) {
        getSession().delete(getSession().get(entityClass, id));
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#deleteAllByPk(java.lang.Class, java.util.Collection)
     */
    @Override
    public <ENTITY extends Serializable> void deleteAllByPk(Class<ENTITY> entityClass, Collection<Serializable> ids) {
        for (Serializable id : ids) {
            getSession().delete(getSession().get(entityClass, id));
        }
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#batchExecute(java.lang.String, java.util.Map)
     */
    @Override
    public int batchExecute(String hql, Map<String, Object> values) {
        Query query = getSession().createQuery(hql);
        if (values != null) {
            query.setProperties(values);
        }
        return query.executeUpdate();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#flush()
     */
    @Override
    public void flush() {
        getSession().flush();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#get(java.lang.Class, java.io.Serializable)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <ENTITY extends Serializable> ENTITY get(Class<ENTITY> entityClass, Serializable id) {
        return (ENTITY) getSession().get(entityClass, id);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#get(java.lang.Class, java.lang.String, java.util.Collection)
     */
    @Override
    @SuppressWarnings({"unchecked", "cast"})
    public <ENTITY extends Serializable> List<ENTITY> get(Class<ENTITY> entityClass, String propertyName,
                                                          Collection<?> values) {
        Criterion criterion = Restrictions.in(propertyName, values);
        return (List<ENTITY>) createCriteria(entityClass, criterion).list();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#get(java.lang.Class, java.lang.String, java.lang.Object[])
     */
    @Override
    @SuppressWarnings({"unchecked", "cast"})
    public <ENTITY extends Serializable> List<ENTITY> get(Class<ENTITY> entityClass, String propertyName,
                                                          Object[] values) {
        Criterion criterion = Restrictions.in(propertyName, values);
        return (List<ENTITY>) createCriteria(entityClass, criterion).list();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#getAll(java.lang.Class)
     */
    @Override
    public <ENTITY extends Serializable> List<ENTITY> getAll(Class<ENTITY> entityClass) {
        return getAll(entityClass, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#getAll(java.lang.Class, java.lang.String)
     */
    @Override
    public <ENTITY extends Serializable> List<ENTITY> getAll(Class<ENTITY> entityClass, String orderBy) {
        return getAll(entityClass, orderBy, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#getAll(java.lang.Class, java.lang.String, com.wellsoft.pt.core.support.PagingInfo)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <ENTITY extends Serializable> List<ENTITY> getAll(Class<ENTITY> entityClass, String orderBy,
                                                             PagingInfo pagingInfo) {
        Criteria criteria = getSession().createCriteria(entityClass);
        // 排序
        addOrder(criteria, orderBy);
        // 分页
        if (pagingInfo != null) {
            if (pagingInfo.isAutoCount()) {
                pagingInfo.setTotalCount(countCriteriaResult(criteria));
                // criteria.setProjection(Projections.rowCount());
                // pagingInfo.setTotalCount((Long) criteria.uniqueResult());
                // criteria.setProjection(null);
            }
            // hibernate的firstResult的序号从0开始
            criteria.setFirstResult(pagingInfo.getFirst());
            criteria.setMaxResults(pagingInfo.getPageSize());
        }
        return criteria.list();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#namedQuery(java.lang.String, java.util.Map)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> namedQuery(String queryName, Map<String, Object> values) {
        return this.namedQuery(queryName, values, null, null);
    }

    /**
     * HQL命名查询
     *
     * @param queryName
     * @param bean
     * @param itemClass
     * @return
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> namedQuery(String queryName, final Map<String, Object> values,
                                                             Class<ITEM> itemClass) {
        return this.namedQuery(queryName, values, itemClass, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#namedQuery(java.lang.String, java.util.Map, com.wellsoft.pt.core.support.PagingInfo)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> namedQuery(String queryName, Map<String, Object> values,
                                                             PagingInfo pagingInfo) {
        return this.namedQuery(queryName, values, null, pagingInfo);
    }

    /**
     * HQL命名查询
     *
     * @param queryName
     * @param values
     * @param itemClass
     * @param pagingInfo
     * @return
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> namedQuery(String queryName, final Map<String, Object> values,
                                                             Class<ITEM> itemClass, PagingInfo pagingInfo) {
        // 条件与排序信息在HQL中
        String hql = getDynamicNamedQueryString(sessionFactory, queryName, values);
        return this.query(hql, values, itemClass, pagingInfo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#find(java.lang.String, java.util.Map, java.lang.Class)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> find(String hql, Map<String, Object> values, Class<ITEM> itemClass) {
        return this.query(hql, values, itemClass, null);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#find(java.lang.String, java.util.Map, java.lang.Class, com.wellsoft.pt.core.support.PagingInfo)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> find(String hql, Map<String, Object> values, Class<ITEM> itemClass,
                                                       PagingInfo pagingInfo) {
        return this.query(hql, values, itemClass, pagingInfo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#query(java.lang.String, java.util.Map)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> query(String hql, Map<String, Object> values) {
        return this.query(hql, values, null, null);
    }

    @Override
    public <ITEM extends Serializable> List<ITEM> query(String hql, final Map<String, Object> values,
                                                        Class<ITEM> itemClass) {
        return this.query(hql, values, itemClass, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#query(java.lang.String, java.util.Map, com.wellsoft.pt.core.support.PagingInfo)
     */
    @Override
    public <ITEM extends Serializable> List<ITEM> query(String hql, Map<String, Object> values, PagingInfo pagingInfo) {
        return this.query(hql, values, null, pagingInfo);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <ITEM extends Serializable> List<ITEM> query(String hql, final Map<String, Object> values,
                                                        Class<ITEM> itemClass, PagingInfo pagingInfo) {
        // 条件与排序信息在HQL中
        Query query = getSession().createQuery(hql);
        query.setProperties(values);

        // 分页
        if (pagingInfo != null) {
            if (pagingInfo.isAutoCount()) {
                long totalCount = countHqlResult(hql, values);
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
        } else if (JpaEntity.class.isAssignableFrom(itemClass)) {
            // 别名查询转换为实体
            if (hql.trim().substring(0, 6).toLowerCase().equals("select")) {
                Type[] types = query.getReturnTypes();
                if (types.length == 1) {
                    Type type = types[0];
                    if (itemClass.equals(type.getReturnedClass())) {
                    } else {
                        query.setResultTransformer(getEntityTransformerMap(itemClass, hql));
                    }
                } else {
                    query.setResultTransformer(getEntityTransformerMap(itemClass, hql));
                }
            }
        } else {
            query.setResultTransformer(getTtemTransformerMap(itemClass));
        }

        return query.list();
    }

    /**
     * 执行count查询获得本次Hql查询所能获得的对象总数.
     * <p>
     * 本函数只能自动处理简单的hql语句,复杂的hql查询请另行编写count语句查询.
     */
    protected long countHqlResult(final String hql, final Map<String, Object> values) {
        String countHql = prepareCountHql(hql);

        try {
            Long count = (Long) createQuery(countHql, values).uniqueResult();
            return count;
        } catch (Exception e) {
            throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
        }
    }

    private String prepareCountHql(String orgHql) {
        String fromHql = orgHql;
        // select子句与order by子句会影响count查询,进行简单的排除.
        fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
        fromHql = StringUtils.substringBefore(fromHql, "order by");
        String countHql = "select count(*) " + fromHql;
        return countHql;
    }

    /**
     * 根据查询HQL与参数列表创建Query对象. 与find()函数可进行更加灵活的操作.
     *
     * @param values 命名参数,按名称绑定.
     */
    public Query createQuery(final String queryString, final Map<String, ?> values) {
        Assert.hasText(queryString, "queryString不能为空");
        Query query = getSession().createQuery(queryString);
        if (values != null) {
            query.setProperties(values);
        }
        return query;
    }

    /**
     * @return
     */
    @Override
    public Session getSession() {
        if (Config.MULTI_TENANCY_STRATEGY_SESSION_FACTORY.equals(multiTenancyStrategy)) {
            SessionFactory sf = SessionFactoryRegistrar.getCurrentTenantSessionFactory();
            if (sf != null) {
                return sf.getCurrentSession();
            }
        }
        return sessionFactory.getCurrentSession();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#findBy(java.lang.String, java.lang.Object, java.lang.Class)
     */
    @Override
    @SuppressWarnings({"unchecked", "cast"})
    public <ENTITY extends Serializable> List<ENTITY> findBy(Class<ENTITY> entityClass, String propertyName,
                                                             Object value) {
        Criterion criterion = Restrictions.eq(propertyName, value);
        return (List<ENTITY>) createCriteria(entityClass, criterion).list();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#findUnique(java.lang.String, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <U extends Serializable> U findUnique(String hql, Map<String, Object> values) {
        return (U) createQuery(hql, values).uniqueResult();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#findUniqueBy(java.lang.String, java.lang.Object, java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <U extends Serializable> U findUniqueBy(Class<U> entityClass, String propertyName, Object value) {
        Criterion criterion = Restrictions.eq(propertyName, value);
        return (U) createCriteria(entityClass, criterion).uniqueResult();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#findUniqueByNamedQuery(java.lang.String, java.util.Map)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <U extends Serializable> U findUniqueByNamedQuery(String queryName, Map<String, Object> values) {
        return (U) createQuery(getDynamicNamedQueryString(sessionFactory, queryName, values), values).uniqueResult();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#findUniqueByNamedQuery(java.lang.String, java.util.Map, java.lang.Class)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <U extends Serializable> U findUniqueByNamedQuery(String queryName, Map<String, Object> values,
                                                             Class<U> itemClass) {
        return (U) createQuery(getDynamicNamedQueryString(sessionFactory, queryName, values), values)
                .setResultTransformer(getTtemTransformerMap(itemClass)).uniqueResult();
    }

    /**
     * 根据Criterion条件创建Criteria. 与find()函数可进行更加灵活的操作.
     *
     * @param criterions 数量可变的Criterion.
     */
    public <ENTITY extends Serializable> Criteria createCriteria(Class<ENTITY> entityClass,
                                                                 final Criterion... criterions) {
        Criteria criteria = getSession().createCriteria(entityClass);
        for (Criterion c : criterions) {
            criteria.add(c);
        }
        return criteria;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#findByExample(java.io.Serializable)
     */
    @Override
    public <ENTITY extends Serializable> List<ENTITY> findByExample(ENTITY example) {
        return this.findByExample(example, null, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#findByExample(java.io.Serializable, java.lang.String)
     */
    @Override
    public <ENTITY extends Serializable> List<ENTITY> findByExample(ENTITY example, String orderBy) {
        return this.findByExample(example, orderBy, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#findByExample(java.io.Serializable, java.lang.String, com.wellsoft.pt.core.support.PagingInfo)
     */
    @Override
    public <ENTITY extends Serializable> List<ENTITY> findByExample(ENTITY example, String orderBy,
                                                                    PagingInfo pagingInfo) {
        return this.findByExample(example, null, orderBy, pagingInfo);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#findByExample(java.io.Serializable, java.util.List, java.lang.String, com.wellsoft.pt.core.support.PagingInfo)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <ENTITY extends Serializable> List<ENTITY> findByExample(ENTITY example,
                                                                    List<PropertyFilter> propertyFilters, String orderBy, PagingInfo pagingInfo) {
        Criteria criteria = getSession().createCriteria(ClassUtils.getUserClass(example)).add(Example.create(example));
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
                // criteria.setProjection(Projections.rowCount());
                // pagingInfo.setTotalCount((Long) criteria.uniqueResult());
                // criteria.setProjection(null);
            }
            // hibernate的firstResult的序号从0开始
            criteria.setFirstResult(pagingInfo.getFirst());
            criteria.setMaxResults(pagingInfo.getPageSize());
        }
        return criteria.list();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#count(java.lang.String, java.util.Map)
     */
    @Override
    public Long count(String hql, Map<String, Object> values) {
        Query query = getSession().createQuery(hql);
        if (values != null) {
            query.setProperties(values);
        }
        return (Long) query.uniqueResult();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#countByNamedQuery(java.lang.String, java.util.Map)
     */
    @Override
    public Long countByNamedQuery(String queryName, Map<String, Object> values) {
        // 条件与排序信息在HQL中
        String hql = getDynamicNamedQueryString(sessionFactory, queryName, values);
        return countHqlResult(hql, values);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#countByExample(java.io.Serializable)
     */
    @Override
    public <ENTITY extends Serializable> Long countByExample(ENTITY example) {
        Criteria criteria = getSession().createCriteria(ClassUtils.getUserClass(example)).add(Example.create(example));
        return countCriteriaResult(criteria);
    }

    /**
     * 排序
     *
     * @param criteria
     * @param orderBy
     */
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

    /**
     * 按属性条件列表创建Criterion数组,辅助函数.
     */
    protected Criterion[] buildCriterionByPropertyFilter(final List<PropertyFilter> filters) {
        List<Criterion> criterionList = new ArrayList<Criterion>();
        for (PropertyFilter filter : filters) {
            if (!filter.hasMultiProperties()) { // 只有一个属性需要比较的情况.
                Criterion criterion = buildCriterion(filter.getPropertyName(), filter.getMatchValue(),
                        filter.getMatchType());
                criterionList.add(criterion);
            } else {// 包含多个属性需要比较的情况,进行or处理.
                Disjunction disjunction = Restrictions.disjunction();
                for (String param : filter.getPropertyNames()) {
                    Criterion criterion = buildCriterion(param, filter.getMatchValue(), filter.getMatchType());
                    disjunction.add(criterion);
                }
                criterionList.add(disjunction);
            }
        }
        return criterionList.toArray(new Criterion[criterionList.size()]);
    }

    /**
     * 按属性条件列表创建Criterion数组,辅助函数.
     */
    protected Criterion[] buildCriterionByPropertyFilter(final List<PropertyFilter> filters, boolean queryOr) {
        if (!queryOr) {
            return buildCriterionByPropertyFilter(filters);
        }
        List<Criterion> criterionList = new ArrayList<Criterion>();
        // 包含多个属性需要比较的情况,进行or处理.
        Disjunction disjunction = Restrictions.disjunction();
        for (PropertyFilter filter : filters) {
            if (!filter.hasMultiProperties()) { // 只有一个属性需要比较的情况.
                Criterion criterion = buildCriterion(filter.getPropertyName(), filter.getMatchValue(),
                        filter.getMatchType());
                disjunction.add(criterion);
            } else {
                for (String param : filter.getPropertyNames()) {
                    Criterion criterion = buildCriterion(param, filter.getMatchValue(), filter.getMatchType());
                    disjunction.add(criterion);
                }
            }
        }
        criterionList.add(disjunction);
        return criterionList.toArray(new Criterion[criterionList.size()]);
    }

    /**
     * 按属性条件参数创建Criterion,辅助函数.
     */
    protected Criterion buildCriterion(final String propertyName, final Object propertyValue, final MatchType matchType) {
        Assert.hasText(propertyName, "propertyName不能为空");
        Criterion criterion = null;
        // 根据MatchType构造criterion
        switch (matchType) {
            case EQ:
                criterion = Restrictions.eq(propertyName, propertyValue);
                break;
            case LIKE:
                criterion = Restrictions.like(propertyName, (String) propertyValue, MatchMode.ANYWHERE);
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
                break;
            case IN:
                criterion = Restrictions.in(propertyName, (Collection) propertyValue);
                break;
            case NOT_IN:
                criterion = Restrictions.not(Restrictions.in(propertyName, (Collection) propertyValue));
                break;
            default:
                throw new UnsupportedOperationException("不支持的查询表达式");
        }
        if (criterion instanceof SimpleExpression && propertyValue != null && String.class.isAssignableFrom(propertyValue.getClass())) {
            ((SimpleExpression) criterion).ignoreCase();
        }
        return criterion;
    }

    /**
     * 执行count查询获得本次Criteria查询所能获得的对象总数.
     */
    @SuppressWarnings("unchecked")
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
            logger.error("不可能抛出的异常:{}", ExceptionUtils.getStackTrace(e));
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
            logger.error("不可能抛出的异常:{}", ExceptionUtils.getStackTrace(e));
        }

        return totalCount;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#createStoredProcedureCall(java.lang.String)
     */
    @Override
    public ProcedureCall createStoredProcedureCall(String storedProcedure) {
        return getSession().createStoredProcedureCall(storedProcedure);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#createStoredProcedureCall(java.lang.String, java.lang.Class)
     */
    @Override
    public ProcedureCall createStoredProcedureCall(String storedProcedure, Class<?> resultClass) {
        return getSession().createStoredProcedureCall(storedProcedure, resultClass);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#createStoredProcedureCall(java.lang.String, java.lang.String)
     */
    @Override
    public ProcedureCall createStoredProcedureCall(String storedProcedure, String resultClass) {
        return getSession().createStoredProcedureCall(storedProcedure, resultClass);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.dao.UniversalDao#getSysDate()
     */
    @Override
    public Date getSysDate() {
        SessionFactoryImplementor sessionFactoryImplementor = (SessionFactoryImplementor) this.sessionFactory;
        String currentTimestampSelectString = sessionFactoryImplementor.getDialect().getCurrentTimestampSelectString();
        SQLQuery queryObject = getSession().createSQLQuery(currentTimestampSelectString);
        return (Date) queryObject.uniqueResult();
    }

    @Override
    public List<Map<String, Object>> query(String sql) throws Exception {

        SQLQuery query = getSession().createSQLQuery(sql);
        // query.setResultSetMapping(arg0)

        AliasedTupleSubsetResultTransformer f = new AliasedTupleSubsetResultTransformer() {

            @Override
            public boolean isTransformedValueATupleElement(String[] arg0, int arg1) {

                return false;
            }

            @Override
            // 重写这个方法是关键
            public Object transformTuple(Object[] tuple/* 值数组 */, String[] aliases/* 字段数组 */) {
                Map<String, Object> map = new HashMap<String, Object>();
                for (int i = 0; i < aliases.length; i++) {

                    map.put(aliases[i].toLowerCase(), tuple[i]);

                }
                return map;
            }
        };
        List<Map<String, Object>> resultList = query.setResultTransformer(f).list();
        return resultList;

    }


    @Override
    public int namedExecute(String executeName, Map<String, Object> values) {
        // 条件与排序信息在HQL中
        String hql = getDynamicNamedQueryString(this.sessionFactory, executeName, values);
        Query query = sessionFactory.getCurrentSession().createQuery(hql);
        if (values != null) {
            query.setProperties(values);
        }
        return query.executeUpdate();
    }


    @Override
    public com.wellsoft.pt.jpa.criteria.Criteria createEntityCriteria(Class<?> clz) {
        return new EntityCriteria(this, clz);
    }

}
