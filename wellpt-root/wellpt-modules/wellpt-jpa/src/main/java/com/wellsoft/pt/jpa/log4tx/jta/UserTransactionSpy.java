/*
 * @(#)2019年11月8日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.log4tx.jta;

import com.wellsoft.pt.jpa.log4tx.AbstractTxSpy;
import com.wellsoft.pt.jpa.log4tx.TxLogUtils;

import javax.transaction.*;

/**
 * Description: 如何描述该类
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
public class UserTransactionSpy extends AbstractTxSpy implements UserTransaction {

    private UserTransaction userTransaction;

    /**
     * @param userTransaction
     */
    public UserTransactionSpy(UserTransaction userTransaction) {
        super();
        this.userTransaction = userTransaction;
    }

    @Override
    public String getClassType() {
        return "UserTransaction";
    }

    public void begin() throws NotSupportedException, SystemException {
        TxLogUtils.createLog();
        String methodCall = "begin";
        try {
            userTransaction.begin();
            infoReturn(methodCall);
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        }
    }

    public void commit() throws HeuristicMixedException, HeuristicRollbackException, IllegalStateException,
            RollbackException, SecurityException, SystemException {
        String methodCall = "commit";
        try {
            userTransaction.commit();
            infoReturn(methodCall);
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        } finally {
            logStackTrace(); // UserTransaction
        }
    }

    public int getStatus() throws SystemException {
        String methodCall = "getStatus";
        try {
            return debugReturn(methodCall, userTransaction.getStatus());
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        }
    }

    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        String methodCall = "rollback";
        try {
            userTransaction.rollback();
            infoReturn(methodCall);
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        } finally {
            logStackTrace(); // UserTransaction
        }
    }

    public void setRollbackOnly() throws IllegalStateException, SystemException {
        String methodCall = "setRollbackOnly";
        try {
            userTransaction.setRollbackOnly();
            infoReturn(methodCall);
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        }
    }

    public void setTransactionTimeout(int seconds) throws SystemException {
        String methodCall = "setTransactionTimeout(" + seconds + ")";
        try {
            userTransaction.setTransactionTimeout(seconds);
            infoReturn(methodCall);
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        }
    }

}
