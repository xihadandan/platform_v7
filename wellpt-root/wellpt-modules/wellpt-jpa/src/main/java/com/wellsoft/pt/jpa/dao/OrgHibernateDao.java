/*
 * @(#)2015年12月17日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.dao;

import com.wellsoft.pt.jpa.hibernate.HibernateDao;
import com.wellsoft.pt.jpa.hibernate.SessionFactoryUtils;

import javax.annotation.PostConstruct;
import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年12月17日.1	zhulh		2015年12月17日		Create
 * </pre>
 * @date 2015年12月17日
 */
public class OrgHibernateDao<T, PK extends Serializable> extends HibernateDao<T, PK> {

    // private static final String KEY_ORG_SESSION_FACTORY_BEAN_NAME = "multi.tenancy.org.session_factory.bean_name";
    // private static final String DEFAULT_VALUE = "orgSessionFactory";

    @Override
    @PostConstruct
    public void init() {
        // String sessionFactoryId =SystemParamsUtils.getValue(KEY_ORG_SESSION_FACTORY_BEAN_NAME, DEFAULT_VALUE);
        //SystemParams.getValue(KEY_ORG_SESSION_FACTORY_BEAN_NAME, DEFAULT_VALUE);
        // SessionFactory sessionFactory = ApplicationContextHolder.getBean(sessionFactoryId, SessionFactory.class);
        setSessionFactory(SessionFactoryUtils.getOrgSessionFactory());
    }

}
