/*
 * @(#)2013-10-30 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.context.listener;

import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.exception.WorkFlowException;

/**
 * Description: 流向监听器接口
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
public interface DirectionListener extends Listener {

    /**
     * 环节流向转变事件处理
     *
     * @param event
     * @throws WorkFlowException
     */
    public void transition(Event event) throws WorkFlowException;

}
