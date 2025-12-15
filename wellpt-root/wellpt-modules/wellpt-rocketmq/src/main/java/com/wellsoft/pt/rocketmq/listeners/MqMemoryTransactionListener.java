/*
 * @(#)2021年1月12日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.rocketmq.listeners;

import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.transaction.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: Rocketmq事务集成，不支持事务状态持久化
 *
 * @author zhongzh
 * @date 2021年1月12日
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年1月12日.1	zhongzh		2021年1月12日		Create
 * </pre>
 */

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年1月12日.1	zhongzh		2021年1月12日		Create
 * </pre>
 * @date 2021年1月12日
 */
public final class MqMemoryTransactionListener implements TransactionListener {

    private final Map<String, Integer> tranactionStatus = new ConcurrentHashMap<>();
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private TransactionManager transactionManager;

    /**
     * @param transactionManager
     */
    public MqMemoryTransactionListener(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public LocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        final String transactionId = msg.getTransactionId();
        if (StringUtils.isBlank(transactionId)) {
            return LocalTransactionState.COMMIT_MESSAGE;
        }
        try {
            Transaction transaction = transactionManager.getTransaction();
            if (null == transaction) {
                return LocalTransactionState.COMMIT_MESSAGE;
            }
            /**
             * @see bitronix.tm.BitronixTransaction#registerSynchronization(Synchronization synchronization)
             */
            int status = transaction.getStatus();
            if (Status.STATUS_NO_TRANSACTION == status) {
                return LocalTransactionState.COMMIT_MESSAGE;// 没有事务，事务提交
            } else if (Status.STATUS_MARKED_ROLLBACK == status) {
                return LocalTransactionState.ROLLBACK_MESSAGE;// 标记回滚，事务回滚
            } else if (isDone(status)) {
                /**
                 * STATUS_PREPARING、STATUS_PREPARED:事务管理器控制，不应该进来。会立刻进入到beforeCompletion
                 * STATUS_COMMITTING、STATUS_ROLLING_BACK:在beforeCompletion中调用
                 * STATUS_COMMITTED、STATUS_ROLLEDBACK:在afterCompletion中调用
                 */
                // 事务结束：事务提交 & 抛错？
                // return LocalTransactionState.COMMIT_MESSAGE;
                throw new RuntimeException("本地事务已结束");
            }
            putStatus(transactionId, status);
            transaction.registerSynchronization(new Synchronization() {

                @Override
                public void beforeCompletion() {

                }

                @Override
                public void afterCompletion(int status) {
                    putStatus(transactionId, status);
                }
            });
        } catch (IllegalStateException ex) {
            logger.warn(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage(), ex);
        } catch (RollbackException ex) {
            logger.warn(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage(), ex);
        } catch (SystemException ex) {
            logger.warn(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage(), ex);
        }
        return LocalTransactionState.UNKNOW;
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt msg) {
        String transactionId = msg.getTransactionId();
        if (StringUtils.isBlank(transactionId)) {
            return LocalTransactionState.COMMIT_MESSAGE;
        }
        Integer status = getStatus(transactionId);
        if (null == status) {
            return LocalTransactionState.COMMIT_MESSAGE;
        } else if (Status.STATUS_COMMITTED == status) {
            removeStatus(transactionId);
            return LocalTransactionState.COMMIT_MESSAGE;
        } else if (Status.STATUS_ROLLEDBACK == status) {
            removeStatus(transactionId);
            return LocalTransactionState.ROLLBACK_MESSAGE;
        }
        return LocalTransactionState.UNKNOW;
    }

    private boolean isDone(int status) {
        switch (status) {
            case Status.STATUS_PREPARING:
            case Status.STATUS_PREPARED:
            case Status.STATUS_COMMITTING:
            case Status.STATUS_COMMITTED:
            case Status.STATUS_ROLLING_BACK:
            case Status.STATUS_ROLLEDBACK:
                return true;
        }
        return false;
    }

    /**
     * 记录事务状态
     *
     * @param transactionId
     * @param status
     */
    protected void putStatus(final String transactionId, int status) {
        tranactionStatus.put(transactionId, status);
    }

    /**
     * 获取事务状态
     *
     * @param transactionId
     * @return
     */
    protected Integer getStatus(String transactionId) {
        return tranactionStatus.get(transactionId);
    }

    /**
     * 删除事务状态
     *
     * @param transactionId
     */
    protected void removeStatus(String transactionId) {
        tranactionStatus.remove(transactionId);
    }
}
