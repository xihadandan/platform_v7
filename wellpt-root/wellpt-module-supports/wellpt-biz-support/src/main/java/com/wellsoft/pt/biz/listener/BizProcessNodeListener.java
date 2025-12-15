/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.listener;

import com.wellsoft.pt.biz.listener.event.ProcessNodeEvent;

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
public interface BizProcessNodeListener extends Listener {

    /**
     * 过程节点创建
     *
     * @param event
     */
    void onCreated(ProcessNodeEvent event);

    /**
     * 过程节点启动
     *
     * @param event
     */
    void onStarted(ProcessNodeEvent event);

    /**
     * 过程节点完成
     *
     * @param event
     */
    void onCompleted(ProcessNodeEvent event);

    /**
     * 过程节点取消
     *
     * @param event
     */
    void onCancelled(ProcessNodeEvent event);

}
