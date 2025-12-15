/*
 * @(#)2016年10月26日 V1.0
 *
 * Copyright 2016 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.criteria;

import com.wellsoft.pt.jpa.dao.NativeDao;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 * Description: 如何描述该类
 *
 * @author xiem
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016年10月26日.1	xiem		2016年10月26日		Create
 * </pre>
 * @date 2016年10月26日
 */
public abstract class AbstractNativeCriteria extends AbstractCriteria {
    protected final NativeDao nativeDao;

    public AbstractNativeCriteria(NativeDao nativeDao) {
        this.nativeDao = nativeDao;
    }

    /**
     * @return the nativeDao
     */
    protected NativeDao getNativeDao() {
        return nativeDao;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return getSession().getSessionFactory();
    }

    @Override
    public Session getSession() {
        return this.getNativeDao().getSession();
    }

}
