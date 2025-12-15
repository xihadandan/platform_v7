/*
 * @(#)2013-1-29 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.util;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import com.wellsoft.pt.jpa.hibernate.MultiTenantConnectionProviderConnectionHelper;
import org.hibernate.tool.hbm2ddl.ConnectionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.PropertyAccessorFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Description: 数据库Dao工具类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-29.1	zhulh		2013-1-29		Create
 * </pre>
 * @date 2013-1-29
 */
public class DaoUtils {

    private static final Logger LOG = LoggerFactory.getLogger(DaoUtils.class);

    /**
     * 实例化对象
     *
     * @param clazz
     * @return 实例化对象
     */
    public static Object instance(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断验证实例（entity）对应属性集合（checkFields）在数据库中是否唯一
     *
     * @param dao         HibernateDao
     * @param entity      验证实例
     * @param checkFields 验证唯一的属性
     * @return true-唯一 ;false-不唯一
     */
    @Deprecated
    public static boolean isUnique(HibernateDao<?, ?> dao, IdEntity entity, String... checkFields) {
        BeanWrapper ebw = new BeanWrapperImpl(entity);
        // 生成查询的entity,获得对象
        IdEntity queryModel = (IdEntity) DaoUtils.instance(entity.getClass());
        for (String checkField : checkFields) {
            Object checkFieldValue = ebw.getPropertyValue(checkField);
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(queryModel);
            bw.setPropertyValue(checkField, checkFieldValue);
        }
        List<IdEntity> queryResult = dao.findByExample(queryModel);
        // 查询判断返回值个数，两个及两个以上直接返回false
        if (queryResult.size() > 1) {
            return false;
        }
        return true;
    }

    /**
     * 判断验证实例（entity）对应属性集合（checkFields）在数据库中是否唯一
     *
     * @param daoName     dao注册的名称
     * @param entity      验证实例
     * @param checkFields
     * @return
     */
    public static boolean isUnique(String daoName, IdEntity entity, String... checkFields) {
        BeanWrapper ebw = new BeanWrapperImpl(entity);
        // 生成查询的entity,获得对象
        IdEntity queryModel = (IdEntity) DaoUtils.instance(entity.getClass());
        for (String checkField : checkFields) {
            Object checkFieldValue = ebw.getPropertyValue(checkField);
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(queryModel);
            bw.setPropertyValue(checkField, checkFieldValue);
        }
        List<IdEntity> queryResult = ApplicationContextHolder.getBean(daoName, HibernateDao.class).findByExample(
                queryModel);
        // 查询判断返回值个数，两个及两个以上直接返回false
        if (queryResult.size() > 1) {
            return false;
        }
        return true;
    }

    /**
     * 判断验证实例（entity）对应属性集合（checkFields）在数据库中是否存在
     *
     * @param daoName     dao注册的名称
     * @param entity      验证实例
     * @param checkFields 验证唯一的属性
     * @return true-存在 ;false-不存在
     */
    public static boolean isExists(String daoName, IdEntity entity, String... checkFields) {
        BeanWrapper ebw = new BeanWrapperImpl(entity);
        // 生成查询的entity,获得对象
        IdEntity queryModel = (IdEntity) DaoUtils.instance(entity.getClass());
        for (String checkField : checkFields) {
            Object checkFieldValue = ebw.getPropertyValue(checkField);
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(queryModel);
            bw.setPropertyValue(checkField, checkFieldValue);
        }
        HibernateDao dao = ApplicationContextHolder.getBean(daoName, HibernateDao.class);
        List<IdEntity> queryResult = ApplicationContextHolder.getBean(daoName, HibernateDao.class).findByExample(
                queryModel);
        // 查询判断返回值个数，两个及两个以上直接返回false
        if (queryResult.size() >= 1) {
            return true;
        }
        return false;
    }

    /**
     * 判断验证实例（entity）对应属性集合（checkFields）在数据库中是否存在
     *
     * @param dao         HibernateDao
     * @param entity      验证实例
     * @param checkFields 验证唯一的属性
     * @return true-存在 ;false-不存在
     */
    @Deprecated
    public static boolean isExists(HibernateDao<?, ?> dao, IdEntity entity, String... checkFields) {
        BeanWrapper ebw = new BeanWrapperImpl(entity);
        // 生成查询的entity,获得对象
        IdEntity queryModel = (IdEntity) DaoUtils.instance(entity.getClass());
        for (String checkField : checkFields) {
            Object checkFieldValue = ebw.getPropertyValue(checkField);
            BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(queryModel);
            bw.setPropertyValue(checkField, checkFieldValue);
        }
        List<IdEntity> queryResult = dao.findByExample(queryModel);
        // 查询判断返回值个数，两个及两个以上直接返回false
        if (queryResult.size() >= 1) {
            return true;
        }
        return false;
    }

    /**
     * 执行SQL（INSERT/UPDATE/DELETE/DDL）语句
     *
     * @param sql SQL语句（INSERT/UPDATE/DELETE/DDL）
     * @return 返回更改行数
     * @see java.sql.Statement#executeUpdate(java.lang.String)
     */
    public static int executeSql(String sql) {
        ConnectionHelper connectionHelper = new MultiTenantConnectionProviderConnectionHelper();
        Connection connection = null;
        int result = -1;
        try {
            connectionHelper.prepare(true);
            connection = connectionHelper.getConnection();
            result = connection.createStatement().executeUpdate(sql);
        } catch (SQLException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            try {
                connectionHelper.release();
            } catch (SQLException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return result;
    }

}
