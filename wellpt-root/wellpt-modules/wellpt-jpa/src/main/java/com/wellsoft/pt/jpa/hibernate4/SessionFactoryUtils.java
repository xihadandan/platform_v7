/*
 * @(#)2015年10月23日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.hibernate4;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.*;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.exception.*;
import org.springframework.dao.*;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.*;

import javax.sql.DataSource;

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
public abstract class SessionFactoryUtils {

    /**
     * Order value for TransactionSynchronization objects that clean up Hibernate Sessions.
     * Returns {@code DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER - 100}
     * to execute Session cleanup before JDBC Connection cleanup, if any.
     *
     * @see org.springframework.jdbc.datasource.DataSourceUtils#CONNECTION_SYNCHRONIZATION_ORDER
     */
    public static final int SESSION_SYNCHRONIZATION_ORDER = DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER - 100;

    static final Log logger = LogFactory.getLog(SessionFactoryUtils.class);

    /**
     * Determine the DataSource of the given SessionFactory.
     *
     * @param sessionFactory the SessionFactory to check
     * @return the DataSource, or {@code null} if none found
     * @see org.hibernate.engine.spi.SessionFactoryImplementor#getConnectionProvider
     */
    public static DataSource getDataSource(SessionFactory sessionFactory) {
        if (sessionFactory instanceof SessionFactoryImplementor) {
            ConnectionProvider cp = ((SessionFactoryImplementor) sessionFactory).getConnectionProvider();
            return cp.unwrap(DataSource.class);
        }
        return null;
    }

    /**
     * Perform actual closing of the Hibernate Session,
     * catching and logging any cleanup exceptions thrown.
     *
     * @param session the Hibernate Session to close (may be {@code null})
     * @see org.hibernate.Session#close()
     */
    public static void closeSession(Session session) {
        if (session != null) {
            try {
                session.close();
            } catch (HibernateException ex) {
                logger.debug("Could not close Hibernate Session", ex);
            } catch (Exception ex) {
                logger.debug("Unexpected exception on closing Hibernate Session", ex);
            }
        }
    }

    /**
     * Convert the given HibernateException to an appropriate exception
     * from the {@code org.springframework.dao} hierarchy.
     *
     * @param ex HibernateException that occured
     * @return the corresponding DataAccessException instance
     * @see HibernateExceptionTranslator#convertHibernateAccessException
     * @see HibernateTransactionManager#convertHibernateAccessException
     */
    public static DataAccessException convertHibernateAccessException(HibernateException ex) {
        if (ex instanceof JDBCConnectionException) {
            return new DataAccessResourceFailureException(ex.getMessage(), ex);
        }
        if (ex instanceof SQLGrammarException) {
            SQLGrammarException jdbcEx = (SQLGrammarException) ex;
            return new InvalidDataAccessResourceUsageException(ex.getMessage() + "; SQL [" + jdbcEx.getSQL() + "]", ex);
        }
        if (ex instanceof LockAcquisitionException) {
            LockAcquisitionException jdbcEx = (LockAcquisitionException) ex;
            return new CannotAcquireLockException(ex.getMessage() + "; SQL [" + jdbcEx.getSQL() + "]", ex);
        }
        if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException jdbcEx = (ConstraintViolationException) ex;
            return new DataIntegrityViolationException(ex.getMessage() + "; SQL [" + jdbcEx.getSQL()
                    + "]; constraint [" + jdbcEx.getConstraintName() + "]", ex);
        }
        if (ex instanceof DataException) {
            DataException jdbcEx = (DataException) ex;
            return new DataIntegrityViolationException(ex.getMessage() + "; SQL [" + jdbcEx.getSQL() + "]", ex);
        }
        if (ex instanceof JDBCException) {
            return new HibernateJdbcException((JDBCException) ex);
        }
        // end of JDBCException (subclass) handling

        if (ex instanceof QueryException) {
            return new HibernateQueryException((QueryException) ex);
        }
        if (ex instanceof NonUniqueResultException) {
            return new IncorrectResultSizeDataAccessException(ex.getMessage(), 1, ex);
        }
        if (ex instanceof NonUniqueObjectException) {
            return new DuplicateKeyException(ex.getMessage(), ex);
        }
        if (ex instanceof PropertyValueException) {
            return new DataIntegrityViolationException(ex.getMessage(), ex);
        }
        if (ex instanceof PersistentObjectException) {
            return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
        }
        if (ex instanceof TransientObjectException) {
            return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
        }
        if (ex instanceof ObjectDeletedException) {
            return new InvalidDataAccessApiUsageException(ex.getMessage(), ex);
        }
        if (ex instanceof UnresolvableObjectException) {
            return new HibernateObjectRetrievalFailureException((UnresolvableObjectException) ex);
        }
        if (ex instanceof WrongClassException) {
            return new HibernateObjectRetrievalFailureException((WrongClassException) ex);
        }
        if (ex instanceof StaleObjectStateException) {
            return new HibernateOptimisticLockingFailureException((StaleObjectStateException) ex);
        }
        if (ex instanceof StaleStateException) {
            return new HibernateOptimisticLockingFailureException((StaleStateException) ex);
        }

        // fallback
        return new HibernateSystemException(ex);
    }

}
