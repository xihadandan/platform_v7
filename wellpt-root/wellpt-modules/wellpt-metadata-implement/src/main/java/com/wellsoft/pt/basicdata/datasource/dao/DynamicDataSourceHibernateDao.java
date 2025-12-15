/*
 * @(#)2014-8-7 V1.0
 *
 * Copyright 2014 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.datasource.dao;

import com.google.common.collect.Maps;
import com.wellsoft.context.config.Config;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.hibernate4.NamedQueryScriptLoader;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author wubin
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-8-7.1	wubin		2014-8-7		Create
 * </pre>
 * @date 2014-8-7
 */
@Component
public class DynamicDataSourceHibernateDao {
    private Logger logger = LoggerFactory.getLogger(DynamicDataSourceHibernateDao.class);
    /**
     * 通过单例类configuration获取的sessionfactory
     */
    private SessionFactory sessionFactory;

    /**
     * @param @return 设定文件
     * @return SessionFactory    返回类型
     * @throws
     * @Title: getSessionFactory
     * @Description: 获取sessionfactory
     */
    public SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = ApplicationContextHolder.getBean(
                    Config.TENANT_SESSION_FACTORY_BEAN_NAME,
                    SessionFactory.class);
        }
        return sessionFactory;
    }

    /**
     * @param @param sessionFactory    设定文件
     * @return void    返回类型
     * @throws
     * @Title: setSessionFactory
     * @Description:设置sessionfactory
     */
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * @param @return 设定文件
     * @return Session    返回类型
     * @throws
     * @Title: getSession
     * @Description: h
     */
    public Session getSession() {
        sessionFactory = getSessionFactory();
        return this.sessionFactory.getCurrentSession();
    }

    /**
     * @param 设定文件
     * @return void    返回类型
     * @throws
     * @Title: flush
     * @Description: Flush当前Session.
     */
    public void flush() {
        getSession().flush();
    }

    /**
     * 获得所有的系统表
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<Map> getAllDataBaseTables() {
        List tableList = null;
        try {

            String queryScript = NamedQueryScriptLoader.generateDynamicNamedQueryString(
                    getSessionFactory(),
                    "queryAllDatabaseTables", null);
            Session s = getSession();
            SQLQuery query = s.createSQLQuery(queryScript);
            tableList = query.list();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return tableList;
    }

    /**
     * 获得所有的视图
     *
     * @return
     */
    @Transactional(readOnly = true)
    public List<Map> getAllDataBaseViews() {
        List viewList = null;
        try {

            String queryScript = NamedQueryScriptLoader.generateDynamicNamedQueryString(
                    getSessionFactory(),
                    "queryAllDatabaseTableViews", null);
            Session s = getSession();
            SQLQuery query = s.createSQLQuery(queryScript);
            return query.list();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return viewList;
    }

    /**
     * 获得一张系统表的所有字段
     *
     * @param tableName
     * @return
     */
    @Transactional(readOnly = true)
    public List<Map> getColumnsByTables(String tableName) {
        List columnList = null;
        try {

            String queryScript = NamedQueryScriptLoader.generateDynamicNamedQueryString(
                    getSessionFactory(),
                    "queryTableColumnsByTableName", null);
            Session s = getSession();
            SQLQuery query = s.createSQLQuery(queryScript);
            Map<String, Object> params = Maps.newHashMap();
            params.put("tableName", tableName);
            query.setProperties(params);
            return query.list();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return columnList;
    }

}
