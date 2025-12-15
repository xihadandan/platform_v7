/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.listener.event;

import com.google.common.collect.Maps;
import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumProcessEventType;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

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
public class ProcessEvent extends ApplicationEvent implements Event {

    private BizProcessInstanceEntity processInstanceEntity;

    private String eventType;

    private ProcessDefinitionJsonParser parser;

    private Map<String, Object> extraData = Maps.newHashMapWithExpectedSize(0);

    /**
     * @param processInstanceEntity
     * @param eventType
     */
    public ProcessEvent(BizProcessInstanceEntity processInstanceEntity, EnumProcessEventType eventType) {
        super(processInstanceEntity);
        this.processInstanceEntity = processInstanceEntity;
        this.eventType = eventType.getValue();
    }

    /**
     * 获取事件名称
     *
     * @return
     */
    @Override
    public String getName() {
        return "业务流程事件";
    }

    /**
     * 获取事件类型
     *
     * @return
     */
    @Override
    public String getEventType() {
        return eventType;
    }

    /**
     * 获取业务流程定义UUID
     *
     * @return
     */
    @Override
    public String getProcessDefUuid() {
        return processInstanceEntity.getProcessDefUuid();
    }

    /**
     * 获取业务流程定义ID
     *
     * @return
     */
    @Override
    public String getProcessDefId() {
        return processInstanceEntity.getId();
    }

    /**
     * 获取表单定义UUID
     *
     * @return
     */
    @Override
    public String getFormUuid() {
        return processInstanceEntity.getFormUuid();
    }

    /**
     * 获取表单数据UUID
     *
     * @return
     */
    @Override
    public String getDataUuid() {
        return processInstanceEntity.getDataUuid();
    }

    /**
     * 获取表单数据
     *
     * @return
     */
    @Override
    public DyFormData getDyFormData() {
        return null;
    }

    /**
     * 获取业务主体ID
     *
     * @return
     */
    @Override
    public String getEntityId() {
        return processInstanceEntity.getEntityId();
    }

    /**
     * 获取业务流程定义解析
     *
     * @return
     */
    @Override
    public ProcessDefinitionJsonParser getProcessDefinitionJsonParser() {
        if (parser == null) {
            parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processInstanceEntity.getProcessDefUuid());
        }
        return parser;
    }

    /**
     * @return the processInstUuid
     */
    public String getProcessInstUuid() {
        return processInstanceEntity.getUuid();
    }

    /**
     * @return the extraData
     */
    @Override
    public Map<String, Object> getExtraData() {
        return extraData;
    }
    
}
