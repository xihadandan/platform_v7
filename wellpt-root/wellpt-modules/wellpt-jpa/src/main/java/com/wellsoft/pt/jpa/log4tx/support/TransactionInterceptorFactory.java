/*
 * @(#)2019年11月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.log4tx.support;

import com.wellsoft.pt.jpa.log4tx.TxLogFactory;
import com.wellsoft.pt.jpa.log4tx.spring.TransactionLogInterceptor;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年11月12日.1	zhongzh		2019年11月12日		Create
 * </pre>
 * @date 2019年11月12日
 */
public class TransactionInterceptorFactory {

    public static TransactionInterceptor createTransactionInterceptor() {
        if (TxLogFactory.createTxLog().isSpringLoggingEnabled()) {
            return new TransactionLogInterceptor();
        }
        return new TransactionInterceptor();
    }

}
