/*
 * @(#)8/8/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.state;

import com.wellsoft.pt.biz.listener.event.ProcessEntityTimerEvent;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/8/24.1	    zhulh		8/8/24		    Create
 * </pre>
 * @date 8/8/24
 */
public abstract class AbstractStateManagerProcessEntityTimerListener implements StateManagerProcessEntityTimerListener {

    /**
     * @param event
     */
    @Override
    public void onTimerDue(ProcessEntityTimerEvent event) {
        changeBusinessStateIfRequired(event, event.getProcessDefinitionJsonParser());
    }

    /**
     * @param event
     */
    @Override
    public void onTimerOverDue(ProcessEntityTimerEvent event) {
        changeBusinessStateIfRequired(event, event.getProcessDefinitionJsonParser());
    }

    protected abstract void changeBusinessStateIfRequired(ProcessEntityTimerEvent event, ProcessDefinitionJsonParser parser);


    @Override
    public int getOrder() {
        return 0;
    }
}
