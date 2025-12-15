/*
 * @(#)2015年10月23日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate4;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年10月23日.1	zhulh		2015年10月23日		Create
 * </pre>
 * @date 2015年10月23日
 */
class SpringFlushSynchronization extends TransactionSynchronizationAdapter {

    private final Session session;

    public SpringFlushSynchronization(Session session) {
        this.session = session;
    }

    @Override
    public void flush() {
        try {
            // SessionFactoryUtils.logger.debug("Flushing Hibernate Session on explicit request");
            this.session.flush();
        } catch (HibernateException ex) {
            throw SessionFactoryUtils.convertHibernateAccessException(ex);
        }
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof SpringFlushSynchronization
                && this.session == ((SpringFlushSynchronization) obj).session);
    }

    @Override
    public int hashCode() {
        return this.session.hashCode();
    }

}
