/*
 * @(#)2019年11月8日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.log4tx.support;

import com.wellsoft.pt.jpa.log4tx.TxLog;
import com.wellsoft.pt.jpa.log4tx.TxSpy;

/**
 * Description: NOOP
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
public class NoopTxLog implements TxLog {

    @Override
    public void infoReturn(TxSpy spy, String methodCall, String returnMsg) {

    }

    @Override
    public void errorReturn(TxSpy spy, String methodCall, String returnMsg) {

    }

    @Override
    public void debugReturn(TxSpy spy, String methodCall, String returnMsg) {

    }

    @Override
    public String logStackTrace() {
        return null;
    }

    @Override
    public boolean isJtaLoggingEnabled() {
        return false;
    }

    public boolean isSpringLoggingEnabled() {
        return false;
    }

}
