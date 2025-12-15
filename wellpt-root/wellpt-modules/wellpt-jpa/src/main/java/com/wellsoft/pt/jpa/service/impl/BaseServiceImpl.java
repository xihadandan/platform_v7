/*
 * @(#)2013-1-28 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.service.impl;

import com.wellsoft.context.config.Config;
import com.wellsoft.context.service.BaseService;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import com.wellsoft.pt.jpa.hibernate.SessionFactoryRegistrar;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DelegatingMessageSource;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-28.1	zhulh		2013-1-28		Create
 * </pre>
 * @date 2013-1-28
 */
public class BaseServiceImpl implements BaseService {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    @Resource(name = "universalDao")
    protected UniversalDao dao;
    @Resource(name = "nativeDao")
    protected NativeDao nativeDao;
    // WARN  [org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor] - Autowired annotation is not supported on static fields
    @Autowired
    private DelegatingMessageSource messageSource;
    private Map<String, UniversalDao> daoMap;
    private Map<String, NativeDao> nativeDaoMap;

    public BaseServiceImpl() {
    }

    protected UniversalDao getDao(String sessionFactoryId) {
        if (Config.TENANT_SESSION_FACTORY_BEAN_NAME.equals(sessionFactoryId)) {
            return dao;
        }

        if (daoMap == null) {
            daoMap = new ConcurrentHashMap<String, UniversalDao>();
        }
        if (!daoMap.containsKey(sessionFactoryId)) {
            SessionFactory sessionFactory = SessionFactoryRegistrar.get(sessionFactoryId);
            if (sessionFactory == null) {
                sessionFactory = ApplicationContextHolder.getBean(sessionFactoryId, SessionFactory.class);
            }

            UniversalDao universalDao = ApplicationContextHolder.getBean(UniversalDao.class);
            universalDao.setSessionFactory(sessionFactory, null);
            daoMap.put(sessionFactoryId, universalDao);
        }
        return daoMap.get(sessionFactoryId);
    }

    protected NativeDao getNativeDao(String sessionFactoryId) {
        if (Config.TENANT_SESSION_FACTORY_BEAN_NAME.equals(sessionFactoryId)) {
            return nativeDao;
        }

        if (nativeDaoMap == null) {
            nativeDaoMap = new ConcurrentHashMap<String, NativeDao>();
        }
        if (!nativeDaoMap.containsKey(sessionFactoryId)) {
            SessionFactory sessionFactory = ApplicationContextHolder.getBean(sessionFactoryId, SessionFactory.class);
            NativeDao tmpNativeDao = ApplicationContextHolder.getBean(NativeDao.class);
            tmpNativeDao.setSessionFactory(sessionFactory, null);
            nativeDaoMap.put(sessionFactoryId, tmpNativeDao);
        }
        return nativeDaoMap.get(sessionFactoryId);
    }

    protected UniversalDao getCommonDao() {
        return this.getDao(Config.COMMON_SESSION_FACTORY_BEAN_NAME);
    }

    protected String getMessage(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
