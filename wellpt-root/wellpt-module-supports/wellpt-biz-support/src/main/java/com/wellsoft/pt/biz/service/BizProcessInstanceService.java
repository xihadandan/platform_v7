/*
 * @(#)9/27/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.service;

import com.wellsoft.pt.biz.dao.BizProcessInstanceDao;
import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.listener.event.Event;
import com.wellsoft.pt.biz.support.ProcessDefinitionJson;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.StateDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.jpa.service.JpaService;
import com.wellsoft.pt.timer.support.WorkTimePeriod;

import java.util.List;
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
 * 9/27/22.1	zhulh		9/27/22		Create
 * </pre>
 * @date 9/27/22
 */
public interface BizProcessInstanceService extends JpaService<BizProcessInstanceEntity, BizProcessInstanceDao, String> {

    /**
     * 创建业务流程实例
     *
     * @param entityName
     * @param entityId
     * @param parser
     * @return
     */
    BizProcessInstanceEntity create(String entityName, String entityId, ProcessDefinitionJsonParser parser);


    /**
     * 根据业务流程实例UUID，启动业务流程
     *
     * @param uuid
     */
    void startByUuid(String uuid);

    /**
     * 根据业务流程定义UUID列表获取业务流程实例数量
     *
     * @param processDefUuids
     * @return
     */
    Long countByProcessDefUuids(List<String> processDefUuids);

    /**
     * 根据业务流程ID及业务主体ID获取业务流程实例
     *
     * @param id
     * @param entityId
     * @return
     */
    BizProcessInstanceEntity getByIdAndEntityId(String id, String entityId);

    /**
     * 完成业务流程
     *
     * @param processInstUuid
     */
    void completeByUuid(String processInstUuid);

    /**
     * 重新开始业务流程
     *
     * @param processInstUuid
     */
    void restartByUuid(String processInstUuid);

    /**
     * 获取业务流程实例的工作时间段信息
     *
     * @return
     */
    WorkTimePeriod getWorkTimePeriodByUuid(String processInstUuid);

    /**
     * 变更业务主体状态
     *
     * @param event
     * @param stateConfig
     * @param parser
     */
    void changeProcessEntityState(Event event, StateDefinition.StateConfig stateConfig, ProcessDefinitionJsonParser parser);

    /**
     * 变更业务办件状态
     *
     * @param event
     * @param stateConfig
     * @param processFormConfig
     * @param parser
     */
    void changeProcessInstanceState(Event event, StateDefinition.StateConfig stateConfig, ProcessDefinitionJson.ProcessFormConfig processFormConfig, ProcessDefinitionJsonParser parser);

    /**
     * 根据表单定义ID及业务主体ID获取办理过的业务流程定义UUID列表
     *
     * @param entityFormId
     * @param entityId
     * @return
     */
    List<String> listProcessDefUuidByEntityFormIdAndEntityId(String entityFormId, String entityId);

    /**
     * 根据表单定义ID及业务主体ID获取办理过的业务流程实例列表
     *
     * @param entityFormId
     * @param entityId
     * @return
     */
    List<BizProcessInstanceEntity> listByEntityFormIdAndEntityId(String entityFormId, String entityId);

    /**
     * 获取业务主体表单数据
     *
     * @param processEntityConfig
     * @param entityId
     * @return
     */
    DyFormData getEntityDyformData(ProcessDefinitionJson.ProcessEntityConfig processEntityConfig, String entityId);

    /**
     * 获取业务主体主表数据
     *
     * @param entityFormUuid
     * @param entityIdValue
     * @param entityIdField
     * @return
     */
    Map<String, Object> getEntityFormDataOfMainform(String entityFormUuid, String entityIdValue, String entityIdField);

    /**
     * @param entity
     * @return
     */
    long countByEntity(BizProcessInstanceEntity entity);

}
