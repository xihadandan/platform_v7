/*
 * @(#)2015年10月23日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate4;

import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.core.Ordered;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate4.SessionHolder;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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
class SpringSessionSynchronization implements TransactionSynchronization, Ordered {

    private final SessionHolder sessionHolder;

    private final SessionFactory sessionFactory;

    private boolean holderActive = true;

    public SpringSessionSynchronization(SessionHolder sessionHolder, SessionFactory sessionFactory) {
        this.sessionHolder = sessionHolder;
        this.sessionFactory = sessionFactory;
    }

    private Session getCurrentSession() {
        return this.sessionHolder.getSession();
    }

    public int getOrder() {
        return SessionFactoryUtils.SESSION_SYNCHRONIZATION_ORDER;
    }

    public void suspend() {
        if (this.holderActive) {
            TransactionSynchronizationManager.unbindResource(this.sessionFactory);
            // Eagerly disconnect the Session here, to make release mode "on_close" work on JBoss.
            getCurrentSession().disconnect();
        }
    }

    public void resume() {
        if (this.holderActive) {
            TransactionSynchronizationManager.bindResource(this.sessionFactory, this.sessionHolder);
        }
    }

    public void flush() {
        try {
            // SessionFactoryUtils.logger.debug("Flushing Hibernate Session on explicit request");
            getCurrentSession().flush();
        } catch (HibernateException ex) {
            throw SessionFactoryUtils.convertHibernateAccessException(ex);
        }
    }

    public void beforeCommit(boolean readOnly) throws DataAccessException {
        if (!readOnly) {
            Session session = getCurrentSession();
            // Read-write transaction -> flush the Hibernate Session.
            // Further check: only flush when not FlushMode.MANUAL.
            if (!FlushMode.isManualFlushMode(session.getFlushMode())) {
                try {
                    // SessionFactoryUtils.logger.debug("Flushing Hibernate Session on transaction synchronization");
                    session.flush();
                } catch (HibernateException ex) {
                    throw SessionFactoryUtils.convertHibernateAccessException(ex);
                }
            }
        }
    }

    public void beforeCompletion() {
        Session session = this.sessionHolder.getSession();
        if (this.sessionHolder.getPreviousFlushMode() != null) {
            // In case of pre-bound Session, restore previous flush mode.
            session.setFlushMode(this.sessionHolder.getPreviousFlushMode());
        }
        // Eagerly disconnect the Session here, to make release mode "on_close" work nicely.
        session.disconnect();
    }

    public void afterCommit() {
    }

    public void afterCompletion(int status) {
        try {
            if (status != STATUS_COMMITTED) {
                // Clear all pending inserts/updates/deletes in the Session.
                // Necessary for pre-bound Sessions, to avoid inconsistent state.
                this.sessionHolder.getSession().clear();
            }
        } finally {
            this.sessionHolder.setSynchronizedWithTransaction(false);
        }
    }

}
