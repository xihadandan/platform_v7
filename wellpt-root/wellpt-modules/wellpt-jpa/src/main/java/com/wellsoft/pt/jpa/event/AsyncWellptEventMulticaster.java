/*
 * @(#)2015-1-27 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.event;

import com.google.common.collect.Lists;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.IgnoreLoginUtils;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-1-27.1	zhulh		2015-1-27		Create
 * </pre>
 * @date 2015-1-27
 */
public class AsyncWellptEventMulticaster extends SimpleApplicationEventMulticaster {

    private Executor asyncTaskExecutor = new SimpleAsyncTaskExecutor();

    /**
     * getTaskExecutor(不重写父类，保持同步执行)
     *
     * @return the taskExecutor
     */
    public Executor getAsyncTaskExecutor() {
        return asyncTaskExecutor;
    }

    /**
     * @param taskExecutor 要设置的taskExecutor
     */
    public void setAsyncTaskExecutor(Executor taskExecutor) {
        this.asyncTaskExecutor = taskExecutor;
    }

    @Override
    public void multicastEvent(final ApplicationEvent event, ResolvableType eventType) {
        ResolvableType type = (eventType != null ? eventType : resolveDefaultEventType(event));
        for (final ApplicationListener<?> listener : getApplicationListeners(event, type)) {
            if (listener instanceof WellptTransactionalEventListener) {
                WellptTransactionalEventListener<?> transactionListener = (WellptTransactionalEventListener<?>) listener;
                if (TransactionSynchronizationManager.isSynchronizationActive()) {
                    TranSyncEventAdapter tranSyncEventAdapter = getResource();
                    if (null == tranSyncEventAdapter) {
                        bindResource(tranSyncEventAdapter = new TranSyncEventAdapter(SpringSecurityUtils.getCurrentUser()));
                        TransactionSynchronizationManager.registerSynchronization(tranSyncEventAdapter);
                    }
                    tranSyncEventAdapter.addEvent(transactionListener, event);
                } else {
                    invokeInnerListener(transactionListener.isAsyncExecute(), listener, event);
                }
            } else {
                invokeInnerListener(listener instanceof WellptEventListener, listener, event);
            }
        }
    }

    protected void processTranSyncEvent(TransactionPhase phase, final List<EventListenerPair> eventListenerPairs, UserDetails userDetails) {
        final List<EventListenerPair> seqEventListenerPairs = Lists.newArrayList();
        for (EventListenerPair eventListenerPair : eventListenerPairs) {
            WellptTransactionalEventListener<?> listener = eventListenerPair.getListener();
            if (eventListenerPair.isNeedExecute() && listener.getPhase() == phase) {
                if (listener.isTransExecute()) {
                    invokeInnerListener(listener.isAsyncExecute(), listener, eventListenerPair.getEvent());
                } else {
                    seqEventListenerPairs.add(eventListenerPair);
                }
            }
        }
        if (false == seqEventListenerPairs.isEmpty()) {
            Executor executor = getAsyncTaskExecutor();
            // final UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
            executor.execute(new Runnable() {
                private Logger logger = LoggerFactory.getLogger(AsyncWellptEventMulticaster.class);

                @Override
                public void run() {
                    try {
                        if (userDetails != null) {
                            IgnoreLoginUtils.login(userDetails);
                        }
                        for (EventListenerPair eventListenerPair : seqEventListenerPairs) {
                            WellptTransactionalEventListener<?> listener = eventListenerPair.getListener();
                            invokeInnerListener(listener.isAsyncExecute(), listener,
                                    eventListenerPair.getEvent());
                        }
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        if (userDetails != null) {
                            IgnoreLoginUtils.logout();
                        }
                    }
                }
            });
        }
    }

    protected void invokeInnerListener(boolean asyncExecute, final ApplicationListener<?> listener,
                                       final ApplicationEvent event) {
        Executor executor = getAsyncTaskExecutor();
        final UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
        if (asyncExecute) {
            executor.execute(new Runnable() {
                private Logger logger = LoggerFactory.getLogger(AsyncWellptEventMulticaster.class);

                @Override
                public void run() {
                    try {
                        if (userDetails != null) {
                            IgnoreLoginUtils.login(userDetails);
                        }
                        invokeListener(listener, event);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    } finally {
                        if (userDetails != null) {
                            IgnoreLoginUtils.logout();
                        }
                    }
                }
            });
        } else {
            invokeListener(listener, event);
        }
    }

    private ResolvableType resolveDefaultEventType(ApplicationEvent event) {
        return ResolvableType.forInstance(event);
    }

    private void bindResource(Object value) {
        TransactionSynchronizationManager.bindResource(TranSyncEventAdapter.class, value);
    }

    private void unbindResource() {
        TransactionSynchronizationManager.unbindResourceIfPossible(TranSyncEventAdapter.class);
    }

    private TranSyncEventAdapter getResource() {
        return (TranSyncEventAdapter) TransactionSynchronizationManager.getResource(TranSyncEventAdapter.class);
    }

    private class TranSyncEventAdapter extends TransactionSynchronizationAdapter {

        protected final List<EventListenerPair> eventListenerPairs = Lists.newArrayList();

        private UserDetails userDetails;

        public TranSyncEventAdapter(UserDetails userDetails) {
            this.userDetails = userDetails;
        }

        public void addEvent(WellptTransactionalEventListener<?> listener, ApplicationEvent event) {
            boolean needExecute = listener.onAddEvent(eventListenerPairs, event);
            EventListenerPair eventListenerPair = new EventListenerPair(listener, event, needExecute);
            eventListenerPairs.add(eventListenerPair);
        }

        @Override
        public void beforeCommit(boolean readOnly) {
            processTranSyncEvent(TransactionPhase.BEFORE_COMMIT, eventListenerPairs, this.userDetails);
        }

        @Override
        public void afterCompletion(int status) {
            unbindResource();// 解绑事件队列
            TransactionPhase phase;
            if (status == STATUS_COMMITTED) {
                phase = TransactionPhase.AFTER_COMMIT;
            } else if (status == STATUS_ROLLED_BACK) {
                phase = TransactionPhase.AFTER_ROLLBACK;
            } else {
                phase = TransactionPhase.AFTER_COMPLETION;
            }
            processTranSyncEvent(phase, eventListenerPairs, this.userDetails);
        }

        @Override
        public int getOrder() {
            return Ordered.LOWEST_PRECEDENCE;
        }
    }
}
