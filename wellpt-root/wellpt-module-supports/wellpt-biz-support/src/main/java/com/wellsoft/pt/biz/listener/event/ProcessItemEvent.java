/*
 * @(#)10/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.listener.event;

import com.google.common.collect.Maps;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumProcessItemEventType;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
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
public class ProcessItemEvent extends ApplicationEvent implements Event {

    private BizProcessItemInstanceEntity processItemInstanceEntity;

    private String eventType;

    private ProcessDefinitionJsonParser parser;

    private DyFormData dyFormData;

    private Map<String, Object> extraData = Maps.newHashMapWithExpectedSize(0);

    public ProcessItemEvent(BizProcessItemInstanceEntity processItemInstanceEntity, EnumProcessItemEventType eventType) {
        super(processItemInstanceEntity);
        this.processItemInstanceEntity = processItemInstanceEntity;
        this.eventType = eventType.getValue();
    }

    public ProcessItemEvent(BizProcessItemInstanceEntity processItemInstanceEntity, String eventType) {
        super(processItemInstanceEntity);
        this.processItemInstanceEntity = processItemInstanceEntity;
        this.eventType = eventType;
    }

    /**
     * 获取事件名称
     *
     * @return
     */
    @Override
    public String getName() {
        return "业务事项事件";
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
        return processItemInstanceEntity.getProcessDefUuid();
    }

    /**
     * 获取业务流程定义ID
     *
     * @return
     */
    @Override
    public String getProcessDefId() {
        return processItemInstanceEntity.getProcessDefId();
    }

    /**
     * 获取业务流程实例UUID
     *
     * @return
     */
    @Override
    public String getProcessInstUuid() {
        return processItemInstanceEntity.getProcessInstUuid();
    }

    /**
     * 获取表单定义UUID
     *
     * @return
     */
    @Override
    public String getFormUuid() {
        return processItemInstanceEntity.getFormUuid();
    }

    /**
     * 获取表单数据UUID
     *
     * @return
     */
    @Override
    public String getDataUuid() {
        return processItemInstanceEntity.getDataUuid();
    }

    /**
     * 获取表单数据
     *
     * @return
     */
    @Override
    public DyFormData getDyFormData() {
        if (dyFormData == null) {
            dyFormData = ApplicationContextHolder.getBean(DyFormFacade.class)
                    .getDyFormData(processItemInstanceEntity.getFormUuid(), processItemInstanceEntity.getDataUuid());
        }
        return dyFormData;
    }

    /**
     * 获取业务主体ID
     *
     * @return
     */
    @Override
    public String getEntityId() {
        return processItemInstanceEntity.getEntityId();
    }

    /**
     * 获取业务流程定义解析
     *
     * @return
     */
    @Override
    public ProcessDefinitionJsonParser getProcessDefinitionJsonParser() {
        if (parser == null) {
            parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processItemInstanceEntity.getProcessDefUuid());
        }
        return parser;
    }

    /**
     * @return the itemInstUuid
     */
    public String getItemInstUuid() {
        return processItemInstanceEntity.getUuid();
    }

    /**
     * @return
     */
    public String getParentItemInstUuid() {
        return processItemInstanceEntity.getParentItemInstUuid();
    }

    /**
     * @return
     */
    public String getItemCode() {
        return processItemInstanceEntity.getItemCode();
    }

    /**
     * @return
     */
    public String getItemId() {
        return processItemInstanceEntity.getItemId();
    }

    /***
     *
     * @return
     */
    public String getItemFlowId() {
        return processItemInstanceEntity.getItemFlowDefId();
    }

    /***
     *
     * @return
     */
    public String getItemFlowInstUuid() {
        return processItemInstanceEntity.getItemFlowInstUuid();
    }

    /**
     * @return the extraData
     */
    public Map<String, Object> getExtraData() {
        return extraData;
    }
}
