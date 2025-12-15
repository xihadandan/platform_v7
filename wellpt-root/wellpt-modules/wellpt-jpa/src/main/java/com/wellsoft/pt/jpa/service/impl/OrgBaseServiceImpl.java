/*
 * @(#)2015年10月27日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.service.impl;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.jpa.dao.NativeDao;
import com.wellsoft.pt.jpa.dao.UniversalDao;
import com.wellsoft.pt.jpa.hibernate.SessionFactoryUtils;
import com.wellsoft.pt.jpa.service.OrgBaseService;
import org.hibernate.SessionFactory;

import javax.annotation.PostConstruct;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年10月27日.1	zhulh		2015年10月27日		Create
 * </pre>
 * @date 2015年10月27日
 */
public class OrgBaseServiceImpl extends BaseServiceImpl implements OrgBaseService {

    // private static final String KEY_ORG_SESSION_FACTORY_BEAN_NAME = "multi.tenancy.org.session_factory.bean_name";
    // private static final String DEFAULT_VALUE = "orgSessionFactory";

    @PostConstruct
    public void init() {
        // String sessionFactoryId =SystemParamsUtils.getValue(KEY_ORG_SESSION_FACTORY_BEAN_NAME, DEFAULT_VALUE);
        // SystemParams.getValue(KEY_ORG_SESSION_FACTORY_BEAN_NAME, DEFAULT_VALUE);
        // SessionFactory sessionFactory = ApplicationContextHolder.getBean(sessionFactoryId, SessionFactory.class);
        SessionFactory sessionFactory = SessionFactoryUtils.getOrgSessionFactory();
        this.dao = ApplicationContextHolder.getBean(UniversalDao.class);
        this.dao.setSessionFactory(sessionFactory, null);
        this.nativeDao = ApplicationContextHolder.getBean(NativeDao.class);
        this.nativeDao.setSessionFactory(sessionFactory, null);
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.service.impl.BaseServiceImpl#getCommonDao()
     */
    @Override
    protected UniversalDao getCommonDao() {
        throw new RuntimeException("UnSupport");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.service.impl.BaseServiceImpl#getDao(java.lang.String)
     */
    @Override
    protected UniversalDao getDao(String sessionFactoryId) {
        throw new RuntimeException("UnSupport");
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.core.service.impl.BaseServiceImpl#getNativeDao(java.lang.String)
     */
    @Override
    protected NativeDao getNativeDao(String sessionFactoryId) {
        throw new RuntimeException("UnSupport");
    }

}
