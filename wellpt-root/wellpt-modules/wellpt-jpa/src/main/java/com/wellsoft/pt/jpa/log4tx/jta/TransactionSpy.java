package com.wellsoft.pt.jpa.log4tx.jta;

import com.wellsoft.pt.jpa.log4tx.AbstractTxSpy;

import javax.transaction.*;
import javax.transaction.xa.XAResource;

public class TransactionSpy extends AbstractTxSpy implements Transaction {

    private Transaction transaction;

    /**
     * @param transaction
     */
    public TransactionSpy(Transaction transaction) {
        super();
        this.transaction = transaction;
    }

    /**
     *
     */
    @Override
    public String getClassType() {
        return "Transaction";
    }

    public void commit() throws HeuristicMixedException, HeuristicRollbackException, RollbackException,
            SecurityException, SystemException {
        String methodCall = "commit";
        try {
            transaction.commit();
            infoReturn(methodCall);
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        } finally {
            // logStackTrace(); // UserTransaction
        }
    }

    public boolean delistResource(XAResource xaRes, int flag) throws IllegalStateException, SystemException {
        String methodCall = "delistResource(" + xaRes + ", " + flag + ")";
        try {
            return debugReturn(methodCall, transaction.delistResource(xaRes, flag));
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        }
    }

    public boolean enlistResource(XAResource xaRes) throws IllegalStateException, RollbackException, SystemException {
        String methodCall = "enlistResource(" + xaRes + ")";
        try {
            return debugReturn(methodCall, transaction.enlistResource(xaRes));
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        }
    }

    public int getStatus() throws SystemException {
        String methodCall = "getStatus";
        try {
            return debugReturn(methodCall, transaction.getStatus());
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        }
    }

    public void registerSynchronization(Synchronization synch) throws IllegalStateException, RollbackException,
            SystemException {
        String methodCall = "registerSynchronization";
        try {
            transaction.registerSynchronization(synch);
            debugReturn(methodCall);
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        }
    }

    public void rollback() throws IllegalStateException, SystemException {
        String methodCall = "rollback";
        try {
            transaction.rollback();
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
            transaction.setRollbackOnly();
            infoReturn(methodCall);
        } catch (Exception ex) {
            errorReturn(methodCall, ex);
            throw ex;
        }
    }

}
