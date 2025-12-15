/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.listener;

import com.wellsoft.pt.biz.listener.event.ProcessEvent;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/25/22.1	zhulh		10/25/22		Create
 * </pre>
 * @date 10/25/22
 */
public abstract class AbstractBizProcessListener implements BizProcessListener {

    /**
     * 监听器名称
     *
     * @return
     */
    @Override
    public String getName() {
        return "业务流程监听器";
    }

    /**
     * 业务流程实例创建
     *
     * @param event
     */
    @Override
    public void onCreated(ProcessEvent event) {

    }

    /**
     * 业务流程实例启动
     *
     * @param event
     */
    @Override
    public void onStarted(ProcessEvent event) {

    }

    /**
     * 业务流程实例完成
     *
     * @param event
     */
    @Override
    public void onCompleted(ProcessEvent event) {

    }

    /**
     * 业务流程实例取消
     *
     * @param event
     */
    @Override
    public void onCancelled(ProcessEvent event) {

    }

    /**
     * Get the order value of this object.
     * <p>Higher values are interpreted as lower priority. As a consequence,
     * the object with the lowest value has the highest priority (somewhat
     * analogous to Servlet {@code load-on-startup} values).
     * <p>Same order values will result in arbitrary sort positions for the
     * affected objects.
     *
     * @return the order value
     * @see #HIGHEST_PRECEDENCE
     * @see #LOWEST_PRECEDENCE
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
