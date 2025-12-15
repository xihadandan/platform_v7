/*
 * @(#)2019年7月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

/**
 * Description: https://docs.oracle.com/javaee/5/api/javax/transaction/Status.html
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年7月12日.1	zhongzh		2019年7月12日		Create
 * </pre>
 * @date 2019年7月12日
 */
public abstract class TransactionUtils {

    protected static Logger logger = LoggerFactory.getLogger(TransactionUtils.class);

    /**
     * https://docs.oracle.com/javaee/5/api/javax/transaction/Status.html
     *
     * @return
     */
    public static String traceTransationState() {
        String rStatus = null;
        try {
            int status = ApplicationContextHolder.getBean(TransactionManager.class).getStatus();
            if (Status.STATUS_ACTIVE == status) {
                rStatus = "ACTIVE";// 活动
            } else if (Status.STATUS_MARKED_ROLLBACK == status) {
                rStatus = "MARKED_ROLLBACK";// 标记回滚
            } else if (Status.STATUS_PREPARED == status) {
                rStatus = "PREPARED";// 已准备
            } else if (Status.STATUS_COMMITTED == status) {
                rStatus = "COMMITTED";// 已提交
            } else if (Status.STATUS_ROLLEDBACK == status) {
                rStatus = "ROLLEDBACK";// 已退回
            } else if (Status.STATUS_UNKNOWN == status) {
                // A transaction is associated with the target object but its current status cannot be determined.
                rStatus = "UNKNOWN";// 未知
            } else if (Status.STATUS_NO_TRANSACTION == status) {
                // No transaction is currently associated with the target object.
                rStatus = "NO_TRANSACTION";// 没有事务
            } else if (Status.STATUS_PREPARING == status) {
                rStatus = "PREPARING";// 准备中（中间状态）
            } else if (Status.STATUS_COMMITTING == status) {
                rStatus = "COMMITTING";// 提交中（中间状态）
            } else if (Status.STATUS_ROLLING_BACK == status) {
                rStatus = "ROLLING_BACK";// 回滚中（中间状态）
            }
            // logger.error("");
        } catch (SystemException ex) {
            logger.error(ex.getMessage(), ex);
        }
        return rStatus;
    }
}
