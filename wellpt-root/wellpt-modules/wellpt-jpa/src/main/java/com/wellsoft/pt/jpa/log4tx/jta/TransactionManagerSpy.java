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
public class TransactionManagerSpy extends AbstractTxSpy implements TransactionManager {

    private TransactionManager transactionManager;

    /**
     * @param transactionManager
     */
    public TransactionManagerSpy(TransactionManager transactionManager) {
        super();
        this.transactionManager = transactionManager;
    }

    @Override
    public String getClassType() {
        return "TransactionManager";
    }

    public void begin() throws NotSupportedException, SystemException {
        TxLogUtils.createLog();
        String methodCall = "begin";
        try {
            transactionManager.begin();
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
            transactionManager.commit();
            infoReturn(methodCall);
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        } finally {
            // logStackTrace(); // UserTransaction
        }
    }

    public int getStatus() throws SystemException {
        String methodCall = "getStatus";
        try {
            return debugReturn(methodCall, transactionManager.getStatus());
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        }
    }

    public Transaction getTransaction() throws SystemException {
        String methodCall = "getTransaction";
        try {
            return debugReturn(methodCall, transactionManager.getTransaction());
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        }
    }

    public void resume(Transaction tobj) throws IllegalStateException, InvalidTransactionException, SystemException {
        String methodCall = "resume(" + tobj + ")";
        try {
            transactionManager.resume(tobj);
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        }
    }

    public void rollback() throws IllegalStateException, SecurityException, SystemException {
        String methodCall = "rollback";
        try {
            transactionManager.rollback();
            infoReturn(methodCall);
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        } finally {
            // logStackTrace(); // UserTransaction
        }
    }

    public void setRollbackOnly() throws IllegalStateException, SystemException {
        String methodCall = "setRollbackOnly";
        try {
            transactionManager.setRollbackOnly();
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        }
    }

    public void setTransactionTimeout(int seconds) throws SystemException {
        String methodCall = "setTransactionTimeout(" + seconds + ")";
        try {
            transactionManager.setTransactionTimeout(seconds);
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        }
    }

    public Transaction suspend() throws SystemException {
        String methodCall = "suspend";
        try {
            return infoReturn(methodCall, transactionManager.suspend());
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        }
    }

}
