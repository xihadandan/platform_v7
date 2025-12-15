package com.wellsoft.pt.jpa.dao;

import com.google.common.collect.Maps;
import com.wellsoft.context.jdbc.support.PagingInfo;
import com.wellsoft.context.jdbc.support.QueryItem;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.hibernate4.NamedQueryScriptLoader;
import com.wellsoft.pt.jpa.support.QueryItemResultTransformer;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.ResultTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.sql.Clob;
import java.util.*;

/**
 * Description:
 *
 * @author chenq
 * @date 2019/7/16
 *
 * <pre>
 * 修改记录:
 * 修改后版本	    修改人		修改日期			修改内容
 * 2019/7/16    chenq		2019/7/16		Create
 * </pre>
 */
@Transactional(readOnly = true)
public class SessionOperationHibernateDao {

    private final static String ACTION_SAVE = "save";
    private final static String ACTION_DELETE = "delete";
    private static final Map<Class<?>, ResultTransformer> entityTransformerMap = new HashMap<Class<?>, ResultTransformer>();
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private SessionFactory sessionFactory;
    private Map<String, SessionOperationHibernateDao> daoSession = Maps.newConcurrentMap();


    private void hibernateObjAction(Object obj, String entityName, String action) {
        if (StringUtils.isNotBlank(entityName)) {
            if (ACTION_SAVE.equals(action)) {
                getSession().saveOrUpdate(entityName, obj);
            } else if (ACTION_DELETE.equals(action)) {
                getSession().delete(entityName, obj);
            }
        } else {
            if (ACTION_SAVE.equals(action)) {
                getSession().saveOrUpdate(obj);
            } else if (ACTION_DELETE.equals(action)) {
                getSession().delete(obj);
            }
        }
    }


    @Transactional
    public void saveOrDelete(Object object, String entityName, boolean isDelete) {
        if (object instanceof Collection) {
            Iterator iterator = ((Collection) object).iterator();
            while (iterator.hasNext()) {
                Object obj = iterator.next();
                this.hibernateObjAction(obj, entityName, isDelete ? ACTION_DELETE : ACTION_SAVE);
            }

        } else {
            this.hibernateObjAction(object, entityName, isDelete ? ACTION_DELETE : ACTION_SAVE);
        }
    }

    private Query createSQLQuery(final String sql, final Map<String, Object> params,
                                 PagingInfo pagingInfo) {
        Query query = getSession().createSQLQuery(sql);
        if (MapUtils.isNotEmpty(params)) {
            query.setProperties(params);
        }
        if (pagingInfo != null) {
            query.setFirstResult(pagingInfo.getFirst());
            query.setMaxResults(pagingInfo.getPageSize());
        }
        return query;
    }

    @Transactional
    public int insert(String sql, Map<String, Object> params) {
        return executeSQL(sql, params);
    }

    @Transactional
    public int update(String sql, Map<String, Object> params) {
        return executeSQL(sql, params);
    }

    @Transactional
    public int delete(String sql, Map<String, Object> params) {
        return executeSQL(sql, params);
    }

    private int executeSQL(String sql, Map<String, Object> params) {
        return createSQLQuery(sql, params, null).executeUpdate();
    }

    @Transactional
    public int updateByNamedSQL(String sqlnamed, Map<String, Object> params) {
        String sql = NamedQueryScriptLoader.generateDynamicNamedQueryString(getSessionFactory(),
                sqlnamed, params);
        return executeSQL(sql, null);
    }

    @Transactional
    public int deleteByNamedSQL(String sqlnamed, Map<String, Object> params) {
        String sql = NamedQueryScriptLoader.generateDynamicNamedQueryString(getSessionFactory(),
                sqlnamed, params);
        return createSQLQuery(sql, null, null).executeUpdate();
    }

    public <T extends Serializable> List<T> query(String sql, Map<String, Object> params,
                                                  PagingInfo pagingInfo,
                                                  Class<T> clazz) {
        Query query = createSQLQuery(sql, params, pagingInfo);
        ResultTransformer resultTransformer = getEntityTransformerMap(clazz);
        if (resultTransformer != null) {
            query.setResultTransformer(getEntityTransformerMap(clazz));
        }
        return query.list();
    }

    public List<QueryItem> query(String sql, Map<String, Object> params, PagingInfo pagingInfo) {
        return query(sql, params, pagingInfo, QueryItem.class);
    }

    public List<String> queryStrings(String sql, Map<String, Object> params,
                                     PagingInfo pagingInfo) {
        return query(sql, params, pagingInfo, String.class);
    }

    public List<String> queryClobAsStrings(String sql, Map<String, Object> params) {
        return query(sql, params, null, String.class);
    }


    private ResultTransformer getEntityTransformerMap(Class<?> entityClass) {
        if (!entityTransformerMap.containsKey(entityClass)) {
            if (QueryItem.class.isAssignableFrom(entityClass)) {
                entityTransformerMap.put(entityClass, QueryItemResultTransformer.INSTANCE);
            } else if (String.class.isAssignableFrom(entityClass)) {
                entityTransformerMap.put(entityClass, new ResultTransformer() {
                    @Override
                    public Object transformTuple(Object[] tuple, String[] aliases) {
                        if (tuple[0] instanceof Clob) {
                            Clob clob = (Clob) tuple[0];
                            try {
                                if (clob != null) {
                                    return IOUtils.toString(clob.getCharacterStream());
                                }
                                return null;
                            } catch (Exception e) {
                                logger.error("读取clob数据流异常：", e);
                                throw new RuntimeException(e);
                            }


                        }
                        return tuple[0].toString();
                    }

                    @Override
                    public List transformList(List collection) {
                        return collection;
                    }
                });
            } else {
                entityTransformerMap.put(entityClass,
                        new AliasToBeanResultTransformer(entityClass));
            }
        }
        return entityTransformerMap.get(entityClass);
    }

    @Transactional
    public int insertByNamedSQL(String sqlnamed, Map<String, Object> params) {
        String sql = NamedQueryScriptLoader.generateDynamicNamedQueryString(getSessionFactory(),
                sqlnamed, params);
        return executeSQL(sql, null);
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public SessionOperationHibernateDao getDao(String sessionFactoryId) {
        if (this.daoSession.containsKey(sessionFactoryId)) {
            return this.daoSession.get(sessionFactoryId);
        }
        SessionOperationHibernateDao dao = new SessionOperationHibernateDao();
        dao.setSessionFactory((SessionFactory) ApplicationContextHolder.getBean(sessionFactoryId));
        this.daoSession.put(sessionFactoryId, dao);
        return dao;
    }

}
