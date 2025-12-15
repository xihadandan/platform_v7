/*
 * @(#)8/8/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.listener.event;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.biz.entity.BizProcessEntityTimerEntity;
import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumProcessEntityTimerEventType;
import com.wellsoft.pt.biz.service.BizProcessInstanceService;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

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
public class ProcessEntityTimerEvent extends ApplicationEvent implements Event {

    private BizProcessEntityTimerEntity timerEntity;

    private String eventType;

    private BizProcessInstanceEntity processInstanceEntity;

    private ProcessDefinitionJsonParser parser;

    private DyFormData dyFormData;

    private Map<String, Object> extraData = Maps.newHashMapWithExpectedSize(0);

    /**
     * @param timerEntity
     */
    public ProcessEntityTimerEvent(BizProcessEntityTimerEntity timerEntity, EnumProcessEntityTimerEventType eventType,
                                   BizProcessInstanceEntity processInstanceEntity, ProcessDefinitionJsonParser parser) {
        super(timerEntity);
        this.timerEntity = timerEntity;
        this.eventType = eventType.getValue();
        this.processInstanceEntity = processInstanceEntity;
        this.parser = parser;
    }

    @Override
    public String getName() {
        return "业务主体计时器事件";
    }

    @Override
    public String getEventType() {
        return eventType;
    }

    @Override
    public String getProcessDefUuid() {
        return processInstanceEntity.getProcessDefUuid();
    }

    @Override
    public String getProcessDefId() {
        return processInstanceEntity.getId();
    }

    @Override
    public String getProcessInstUuid() {
        return processInstanceEntity.getUuid();
    }

    @Override
    public String getFormUuid() {
        return null;
    }

    @Override
    public String getDataUuid() {
        return null;
    }

    @Override
    public DyFormData getDyFormData() {
        if (dyFormData == null) {
            BizProcessInstanceService processInstanceService = ApplicationContextHolder.getBean(BizProcessInstanceService.class);
            dyFormData = processInstanceService.getEntityDyformData(this.parser.getProcessEntityConfig(), timerEntity.getEntityId());
        }
        return dyFormData;
    }

    @Override
    public String getEntityId() {
        return timerEntity.getEntityId();
    }

    @Override
    public ProcessDefinitionJsonParser getProcessDefinitionJsonParser() {
        return parser;
    }

    /**
     * @return the extraData
     */
    @Override
    public Map<String, Object> getExtraData() {
        return extraData;
    }
    
}
