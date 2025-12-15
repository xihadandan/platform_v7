/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wellsoft.pt.jpa.hibernate4;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.orm.hibernate4.SpringFlushSynchronization;
import org.springframework.orm.hibernate4.SpringSessionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

/**
 * Description: 去除分布式事务，切换成本地事务，
 *
 * @author liuxj
 * @version 6.2
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * V6.2	   liuxj		2021/1/8		Create
 * </pre>
 * @da
 */
@SuppressWarnings("serial")
public class LocalSpringSessionContext implements CurrentSessionContext {

    private final SessionFactoryImplementor sessionFactory;

    private TransactionManager transactionManager;

    private CurrentSessionContext jtaSessionContext;


    /**
     * Create a new SpringSessionContext for the given Hibernate SessionFactory.
     *
     * @param sessionFactory the SessionFactory to provide current Sessions for
     */
    public LocalSpringSessionContext(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;

    }


    /**
     * Retrieve the Spring-managed Session for the current thread, if any.
     */
    @Override
    public Session currentSession() throws HibernateException {
        Object value = TransactionSynchronizationManager.getResource(this.sessionFactory);
        if (value instanceof Session) {
            return (Session) value;
        } else if (value instanceof SessionHolder) {
            SessionHolder sessionHolder = (SessionHolder) value;
            Session session = sessionHolder.getSession();
            if (!sessionHolder.isSynchronizedWithTransaction() &&
                    TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(
                        new org.springframework.orm.hibernate4.SpringSessionSynchronization(sessionHolder, this.sessionFactory, false));
                sessionHolder.setSynchronizedWithTransaction(true);
                // Switch to FlushMode.AUTO, as we have to assume a thread-bound Session
                // with FlushMode.MANUAL, which needs to allow flushing within the transaction.
                FlushMode flushMode = session.getFlushMode();
                if (flushMode.equals(FlushMode.MANUAL) &&
                        !TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
                    session.setFlushMode(FlushMode.AUTO);
                    sessionHolder.setPreviousFlushMode(flushMode);
                }
            }
            return session;
        }

        if (this.transactionManager != null) {
            try {
                if (this.transactionManager.getStatus() == Status.STATUS_ACTIVE) {
                    Session session = this.jtaSessionContext.currentSession();
                    if (TransactionSynchronizationManager.isSynchronizationActive()) {
                        TransactionSynchronizationManager.registerSynchronization(new SpringFlushSynchronization(session));
                    }
                    return session;
                }
            } catch (SystemException ex) {
                throw new HibernateException("JTA TransactionManager found but status check failed", ex);
            }
        }

        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            Session session = this.sessionFactory.openSession();
            if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
                session.setFlushMode(FlushMode.MANUAL);
            }
            SessionHolder sessionHolder = new SessionHolder(session);
            TransactionSynchronizationManager.registerSynchronization(
                    new SpringSessionSynchronization(sessionHolder, this.sessionFactory, true));
            TransactionSynchronizationManager.bindResource(this.sessionFactory, sessionHolder);
            sessionHolder.setSynchronizedWithTransaction(true);
            return session;
        } else {
            throw new HibernateException("Could not obtain transaction-synchronized Session for current thread");
        }
    }

}
