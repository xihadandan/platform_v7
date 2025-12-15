/*
 * @(#)2019年11月8日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.log4tx.jta;

import com.wellsoft.pt.jpa.log4tx.TxLogFactory;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

/**
 * Description: Spring JtaTransactionManager事务管理器注入
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年11月8日.1	zhongzh		2019年11月8日		Create
 * </pre>
 * @date 2019年11月8日
 */
public class JtaTransactionManager4TxLog extends JtaTransactionManager {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void setTransactionManager(TransactionManager transactionManager) {
        super.setTransactionManager(TxLogFactory.createTransactionManager(transactionManager));
    }

    @Override
    public void setUserTransaction(UserTransaction userTransaction) {
        super.setUserTransaction(TxLogFactory.createUserTransaction(userTransaction));
    }

}
