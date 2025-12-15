/*
 * @(#)10/19/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.listener;

import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.service.BizProcessItemInstanceService;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.timer.listener.AbstractTimerListener;
import com.wellsoft.pt.timer.support.event.TimerEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/19/22.1	zhulh		10/19/22		Create
 * </pre>
 * @date 10/19/22
 */
@Component
public class BizProcessItemTimerListener extends AbstractTimerListener {

    public static String LISTENER_BEAN_NAME = "bizProcessItemTimerListener";

    @Autowired
    private BizProcessItemInstanceService processItemInstanceService;

    @Autowired
    private BizEventListenerPublisher eventListenerPublisher;

    /**
     * 监听器名称
     *
     * @return
     */
    @Override
    public String getName() {
        return "业务流程管理_业务事项计时监听器";
    }

    /**
     * 到期
     *
     * @param event
     */
    @Override
    public void onTimerDue(TimerEvent event) {
        BizProcessItemInstanceEntity processItemInstanceEntity = processItemInstanceService.getByTimerUuid(event.getTimerUuid());
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processItemInstanceEntity.getProcessDefUuid());
        eventListenerPublisher.publishItemTimerDue(processItemInstanceEntity, parser.getProcessItemConfigById(processItemInstanceEntity.getItemId()));
    }

    /**
     * 逾期
     *
     * @param event
     */
    @Override
    public void onTimerOverDue(TimerEvent event) {
        BizProcessItemInstanceEntity processItemInstanceEntity = processItemInstanceService.getByTimerUuid(event.getTimerUuid());
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processItemInstanceEntity.getProcessDefUuid());
        eventListenerPublisher.publishItemTimerOverDue(processItemInstanceEntity, parser.getProcessItemConfigById(processItemInstanceEntity.getItemId()));
    }

    /**
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
