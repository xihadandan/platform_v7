/*
 * @(#)2019年11月7日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.log4tx;

/**
 * Description: 日志接口
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
public interface TxLog {

    /**
     * begin、resume、suspend、commit、rollback、setRollbackOnly、setTransactionTimeout方法使用
     *
     * @param spy
     * @param methodCall
     * @param returnMsg
     */
    public abstract void infoReturn(TxSpy spy, String methodCall, String returnMsg);

    public abstract void errorReturn(TxSpy spy, String methodCall, String returnMsg);

    public abstract void debugReturn(TxSpy spy, String methodCall, String returnMsg);

    public abstract String logStackTrace();

    public abstract boolean isJtaLoggingEnabled();

    public abstract boolean isSpringLoggingEnabled();

}
