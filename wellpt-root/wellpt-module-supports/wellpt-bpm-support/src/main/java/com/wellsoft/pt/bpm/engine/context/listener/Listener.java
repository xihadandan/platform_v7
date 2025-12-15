/*
 * @(#)2013-10-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.context.listener;

/**
 * Description: 监听器接口
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-10-30.1	zhulh		2013-10-30		Create
 * </pre>
 * @date 2013-10-30
 */
public interface Listener {
    String FLOW = "flow";

    String TASK = "task";

    String DIRECTION = "direction";

    int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;

    int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

    /**
     * 监听器名称
     *
     * @return
     */
    String getName();

    /**
     * 监听器序号
     *
     * @return
     */
    int getOrder();
}
