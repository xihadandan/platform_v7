/*
 * @(#)2015年10月23日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate4;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.orm.hibernate4.SpringJtaSessionContext;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.transaction.TransactionManager;

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
public class SpringSessionContext implements CurrentSessionContext {
    private final SessionFactoryImplementor sessionFactory;

    private final CurrentSessionContext jtaSessionContext;

    public SpringSessionContext(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
        JtaPlatform jtaPlatform = sessionFactory.getServiceRegistry().getService(JtaPlatform.class);
        TransactionManager transactionManager = jtaPlatform.retrieveTransactionManager();
        this.jtaSessionContext = (transactionManager != null ? new SpringJtaSessionContext(sessionFactory) : null);
    }

    /**
     * 如何描述该方法
     * <p>
     * (non-Javadoc)
     *
     * @see org.hibernate.context.spi.CurrentSessionContext#currentSession()
     */
    @Override
    public Session currentSession() throws HibernateException {
        Object value = TransactionSynchronizationManager.getResource(this.sessionFactory);
        if (value instanceof Session) {
            return (Session) value;
        } else if (value instanceof SessionHolder) {
            SessionHolder sessionHolder = (SessionHolder) value;
            Session session = sessionHolder.getSession();
            if (!sessionHolder.isSynchronizedWithTransaction()
                    && TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager
                        .registerSynchronization(new SpringSessionSynchronization(sessionHolder, this.sessionFactory));
                sessionHolder.setSynchronizedWithTransaction(true);
                // Switch to FlushMode.AUTO, as we have to assume a thread-bound Session
                // with FlushMode.MANUAL, which needs to allow flushing within the transaction.
                FlushMode flushMode = session.getFlushMode();
                if (FlushMode.isManualFlushMode(flushMode)
                        && !TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
                    session.setFlushMode(FlushMode.AUTO);
                    sessionHolder.setPreviousFlushMode(flushMode);
                }
            }
            return session;
        } else if (this.jtaSessionContext != null) {
            Session session = this.jtaSessionContext.currentSession();
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new SpringFlushSynchronization(session));
            }
            return session;
        } else {
            throw new HibernateException("No Session found for current thread");
        }
    }

}
