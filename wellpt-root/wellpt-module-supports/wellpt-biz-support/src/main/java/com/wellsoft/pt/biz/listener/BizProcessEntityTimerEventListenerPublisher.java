/*
 * @(#)8/8/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.listener;

import com.wellsoft.pt.biz.entity.BizProcessEntityTimerEntity;
import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
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
public interface BizProcessEntityTimerEventListenerPublisher {

    /**
     * 发布业务主体计时器到期事件监听
     *
     * @param timerEntity
     */
    void publishTimerDue(BizProcessEntityTimerEntity timerEntity, BizProcessInstanceEntity processInstanceEntity, ProcessDefinitionJsonParser parser);

    /**
     * 发布业务主体计时器逾期事件监听
     *
     * @param timerEntity
     */
    void publishTimerOverdue(BizProcessEntityTimerEntity timerEntity, BizProcessInstanceEntity processInstanceEntity, ProcessDefinitionJsonParser parser);

}
