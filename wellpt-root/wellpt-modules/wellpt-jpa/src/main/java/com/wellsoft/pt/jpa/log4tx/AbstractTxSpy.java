/*
 * @(#)2019年11月8日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.log4tx;

/**
 * Description: 抽象的TxSpy
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
public abstract class AbstractTxSpy implements TxLog, TxSpy {

    protected TxLog log4Tx;

    public AbstractTxSpy() {
        this.log4Tx = TxLogFactory.createTxLog();
    }

    protected <T> T infoReturn(String methodCall, T value) {
        infoReturn(this, methodCall, String.valueOf(value));
        return value;
    }

    protected void infoReturn(String methodCall) {
        infoReturn(this, methodCall, null);
    }

    protected <T extends Throwable> void errorReturn(String methodCall, T value) {
        errorReturn(this, methodCall, value.getMessage());
    }

    protected <T> T debugReturn(String methodCall, T value) {
        debugReturn(this, methodCall, String.valueOf(value));
        return value;
    }

    protected void debugReturn(String methodCall) {
        debugReturn(this, methodCall, null);
    }

    // adapter

    @Override
    public void infoReturn(TxSpy spy, String methodCall, String returnMsg) {
        log4Tx.infoReturn(spy, methodCall, returnMsg);
    }

    @Override
    public void errorReturn(TxSpy spy, String methodCall, String returnMsg) {
        log4Tx.infoReturn(spy, methodCall, returnMsg);
    }

    @Override
    public void debugReturn(TxSpy spy, String methodCall, String returnMsg) {
        log4Tx.debugReturn(spy, methodCall, returnMsg);
    }

    public String logStackTrace() {
        return log4Tx.logStackTrace();// 提交或者回滚时打印日志
        // Log4JdbcFactory.clearLog();// 打印后，清空日志，准备给下一次使用
    }

    public boolean isJtaLoggingEnabled() {
        return log4Tx.isJtaLoggingEnabled();
    }

    public boolean isSpringLoggingEnabled() {
        return log4Tx.isSpringLoggingEnabled();
    }

}
