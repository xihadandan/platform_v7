/*
 * @(#)8/8/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.listener;

import com.wellsoft.pt.biz.entity.BizProcessEntityTimerEntity;
import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumProcessEntityTimerEventType;
import com.wellsoft.pt.biz.listener.event.ProcessEntityTimerEvent;
import com.wellsoft.pt.biz.state.StateManagerProcessEntityTimerListener;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
@Component
public class BizProcessEntityTimerEventListenerPublisherImpl implements BizProcessEntityTimerEventListenerPublisher {

    @Autowired
    private List<StateManagerProcessEntityTimerListener> stateManagerProcessEntityTimerListeners;

    @Override
    public void publishTimerDue(BizProcessEntityTimerEntity timerEntity, BizProcessInstanceEntity processInstanceEntity, ProcessDefinitionJsonParser parser) {
        ProcessEntityTimerEvent timerEvent = new ProcessEntityTimerEvent(timerEntity, EnumProcessEntityTimerEventType.Due, processInstanceEntity, parser);

        // 状态管理监听业务主体计时器事件
        stateManagerProcessEntityTimerListeners.stream().forEach(listener -> listener.onTimerDue(timerEvent));
    }

    @Override
    public void publishTimerOverdue(BizProcessEntityTimerEntity timerEntity, BizProcessInstanceEntity processInstanceEntity, ProcessDefinitionJsonParser parser) {
        ProcessEntityTimerEvent timerEvent = new ProcessEntityTimerEvent(timerEntity, EnumProcessEntityTimerEventType.Overdue, processInstanceEntity, parser);

        // 状态管理监听业务主体计时器事件
        stateManagerProcessEntityTimerListeners.stream().forEach(listener -> listener.onTimerOverDue(timerEvent));
    }
}
