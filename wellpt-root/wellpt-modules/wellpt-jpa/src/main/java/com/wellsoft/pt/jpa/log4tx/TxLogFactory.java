/*
 * @(#)2019年11月7日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.log4tx;

import com.wellsoft.pt.jpa.log4tx.jta.TransactionManagerSpy;
import com.wellsoft.pt.jpa.log4tx.jta.UserTransactionSpy;
import com.wellsoft.pt.jpa.log4tx.support.NoopTxLog;
import com.wellsoft.pt.jpa.log4tx.support.Slf4jTxLog;
import net.sf.log4jdbc.ConnectionSpy;
import net.sf.log4jdbc.SpyLogFactory;

import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import java.sql.Connection;

/**
 * Description: Factory
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年11月7日.1	zhongzh		2019年11月7日		Create
 * </pre>
 * @date 2019年11月7日
 */
public abstract class TxLogFactory {

    private volatile static TxLog logger = null;

    public static TxLog createTxLog() {
        if (logger == null) {
            synchronized (TxLogFactory.class) {
                if (logger == null) {
                    try {
                        logger = new Slf4jTxLog();
                    } catch (Exception ex) {
                        logger = new NoopTxLog();
                    }
                }
            }
        }
        return logger;
    }

    public static Connection createConnection(Connection realConnection) {
        if (SpyLogFactory.getSpyLogDelegator().isJdbcLoggingEnabled()) {
            if (createTxLog().isJtaLoggingEnabled()) {
                // return new ConnectionSpy4Tx(realConnection);
            }
            return new ConnectionSpy(realConnection);
        }
        return realConnection;
    }

    public static XADataSource createXADataSource(XADataSource realXADataSource) {
        if (SpyLogFactory.getSpyLogDelegator().isJdbcLoggingEnabled() && createTxLog().isJtaLoggingEnabled()) {
            // return new XADataSourceSpy4Tx(realXADataSource);
        }
        return realXADataSource;
    }

    public static TransactionManager createTransactionManager(TransactionManager realTransactionManager) {
        return new TransactionManagerSpy(realTransactionManager);
    }

    public static UserTransaction createUserTransaction(UserTransaction realUserTransaction) {
        return new UserTransactionSpy(realUserTransaction);
    }

}
