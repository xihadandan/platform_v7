/*
 * @(#)2015-1-27 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.jpa.event;

import com.wellsoft.pt.jpa.service.impl.BaseServiceImpl;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.transaction.event.TransactionPhase;

import java.util.List;

/**
 * Description:
 * <h2>独立事务处理方案比较：</h2>
 * <table>
 * <thead>
 * <tr>
 * <th>功能描述\方案</th>
 * <th>事务线程</th>
 * <th>独立线程</th>
 * <th>定时任务异步执行</th>
 * <th>Spring事件</th>
 * <th>Well事件</th>
 * <th>消息中间件(是否支持事务)</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>是否影响业务响应时间</td>
 * <td>是</td>
 * <td>否</td>
 * <td>否</td>
 * <td>启用异步</td>
 * <td>启用异步</td>
 * <td>启用异步</td>
 * </tr>
 * <tr>
 * <td>是否支持代码解耦</td>
 * <td>否</td>
 * <td>否</td>
 * <td>取决任务队列保存方式</td>
 * <td>支持</td>
 * <td>支持</td>
 * <td>支持</td>
 * </tr>
 * <tr>
 * <td>是否支持外部事务一致性/</br>消息一致性是否有保障</td>
 * <td>否</td>
 * <td>否</td>
 * <td>事件（消息队列）由本地事务做保障</td>
 * <td>支持(同步写本地数据库时支持)</td>
 * <td>支持(提交成功后处理消息)</td>
 * <td>启用分布式事务</td>
 * </tr>
 * <tr>
 * <td>是否支持事件顺序</td>
 * <td>天然支持</td>
 * <td>不支持</td>
 * <td>保存队列时添加保存时间</td>
 * <td>启用同步（TransactionalEventListener不支持）</td>
 * <td>支持</td>
 * <td>部分支持</td>
 * </tr>
 * <tr>
 * <td>异步执行</td>
 * <td></td>
 * <td>支持</td>
 * <td>支持</td>
 * <td>支持</td>
 * <td>支持</td>
 * <td>支持</td>
 * </tr>
 * <tr>
 * <td>复杂度</td>
 * <td>简单</td>
 * <td>一般</td>
 * <td>一般(分布式的定时任务维护较为复杂)</td>
 * <td>一般</td>
 * <td>一般</td>
 * <td>复杂</td>
 * </tr>
 * <tr>
 * <td>可维护性（1~5从低到高）</td>
 * <td>2</td>
 * <td>1</td>
 * <td>3</td>
 * <td>3(线程池可维护)</td>
 * <td>4</td>
 * <td>5</td>
 * </tr>
 * <tr>
 * <td>可水平扩展</td>
 * <td>不可扩展</td>
 * <td>单机扩展</td>
 * <td>定时任务的分布式扩展</td>
 * <td>单机扩展</td>
 * <td>单机扩展</td>
 * <td>单机扩展和分布式扩展</td>
 * </tr>
 * </tbody>
 * </table>
 *
 * <h2>WellptTransactionalEventListener：</h2>
 * <table>
 * <thead>
 * <tr>
 * <th>asyncExecute\transExecute</th>
 * <th>true</th>
 * <th>false</th>
 * </tr>
 * </thead>
 * <tbody>
 * <tr>
 * <td>true</td>
 * <td>事务线程分发事件,业务处理独立于事务线程,多个事件异步执行</td>
 * <td>独立线程分发事件,业务处理独立于事务线程,多个事件异步执行</td>
 * </tr>
 * <tr>
 * <td>false</td>
 * <td>事务线程中同步执行，异常会影响业务线程,事件有序执行</td>
 * <td>独立于事务线程，多个事件按FIFO同步执行,事件有序执行</td>
 * </tr>
 * <tr>
 * </tbody>
 * </table>
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2020年7月1日.1	zhongzh		2020年7月1日		Create
 * </pre>
 * @date 2020年7月1日
 */
public abstract class WellptTransactionalEventListener<E extends ApplicationEvent> extends BaseServiceImpl implements
        ApplicationListener<E> {

    /**
     * 执行的事务阶段,默认为AFTER_COMMIT
     */
    private final TransactionPhase phase;
    /**
     * 是否在事务线程中执行,没有事务的线程无效,默认为false</br>
     * 在独立线程中执行事件列表，可以避免影响正常业务（事件和异常）
     */
    private final boolean transExecute;
    /**
     * 是否异步执行（非顺序执行）,默认为false</br>
     * 一个事务中有多个事件，且事件无先后顺序时，非有序执行可以提高吞吐量
     */
    private final boolean asyncExecute;

    /**
     *
     */
    public WellptTransactionalEventListener() {
        this(TransactionPhase.AFTER_COMMIT, false, false);
    }

    /**
     * 如何描述该构造方法
     *
     * @param phase
     * @param transExecute
     * @param asyncExecute
     */
    public WellptTransactionalEventListener(TransactionPhase phase, boolean transExecute, boolean asyncExecute) {
        this.phase = phase;
        this.transExecute = transExecute;
        this.asyncExecute = asyncExecute;
    }

    /**
     * 事务加入事件时调用，可以遍历eventPair.markIgnoreExecute忽略前置事件</br>
     * 返回false，当前事件忽略执行
     *
     * @param eventListenerPairs
     * @param event
     * @return
     */
    public boolean onAddEvent(List<EventListenerPair> eventListenerPairs, ApplicationEvent event) {
        return true;
    }

    public TransactionPhase getPhase() {
        return phase;
    }

    public boolean isTransExecute() {
        return transExecute;
    }

    public boolean isAsyncExecute() {
        return asyncExecute;
    }

}
