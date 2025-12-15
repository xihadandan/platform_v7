/*
 * @(#)8/6/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.jdbc.entity.Entity;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.util.groovy.GroovyUtils;
import com.wellsoft.pt.biz.entity.BizProcessEntityTimerEntity;
import com.wellsoft.pt.biz.entity.BizProcessInstanceEntity;
import com.wellsoft.pt.biz.service.BizProcessEntityTimerService;
import com.wellsoft.pt.biz.service.BizProcessInstanceService;
import com.wellsoft.pt.biz.support.ProcessDefinitionJson;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.jpa.template.TemplateEngineFactory;
import com.wellsoft.pt.message.facade.service.MessageClientApiFacade;
import com.wellsoft.pt.timer.dto.TsTimerDto;
import com.wellsoft.pt.timer.facade.service.TsTimerFacadeService;
import com.wellsoft.pt.timer.listener.AbstractTimerListener;
import com.wellsoft.pt.timer.support.event.TimerEvent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 8/6/24.1	    zhulh		8/6/24		    Create
 * </pre>
 * @date 8/6/24
 */
@Component
public class BizProcessEntityStateTimerListener extends AbstractTimerListener {

    public static String LISTENER_BEAN_NAME = "bizProcessEntityStateTimerListener";

    @Autowired
    private BizProcessEntityTimerService processEntityTimerService;

    @Autowired
    private BizProcessInstanceService processInstanceService;

    @Autowired
    private TsTimerFacadeService tsTimerFacadeService;

    @Autowired
    private MessageClientApiFacade messageClientApiFacade;

    @Autowired
    private BizProcessEntityTimerEventListenerPublisher processEntityTimerEventListenerPublisher;

    @Override
    public String getName() {
        return "业务流程管理_业务主体状态计时监听器";
    }

    @Override
    public void onTimerAlarm(TimerEvent event) {
        String timerUuid = event.getTimerUuid();
        BizProcessEntityTimerEntity timerEntity = processEntityTimerService.getByTimerUuid(timerUuid);
        if (timerEntity == null) {
            return;
        }

        TsTimerDto tsTimerDto = tsTimerFacadeService.getTimer(timerUuid);
        processEntityTimerService.updateTimerData(timerEntity, tsTimerDto);

        String processInstUuid = timerEntity.getProcessInstUuid();
        BizProcessInstanceEntity processInstanceEntity = processInstanceService.getOne(processInstUuid);
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processInstanceEntity.getProcessDefUuid());

        ProcessDefinitionJson.StateTimerConfig stateTimerConfig = getStateTimerConfig(timerEntity, parser);
        if (stateTimerConfig != null && stateTimerConfig.isEnableAlarmDoing()) {
            sendTimerAlarmMessage(timerEntity, tsTimerDto, stateTimerConfig, parser);
        }
    }

    /**
     * @param timerEntity
     * @param tsTimerDto
     * @param stateTimerConfig
     * @param parser
     */
    private void sendTimerAlarmMessage(BizProcessEntityTimerEntity timerEntity, TsTimerDto tsTimerDto, ProcessDefinitionJson.StateTimerConfig stateTimerConfig, ProcessDefinitionJsonParser parser) {
        String dueMsgTemplateId = stateTimerConfig.getAlarmMsgTemplateId();
        List<String> dueMsgObjects = stateTimerConfig.getAlarmMsgObjects();
        if (StringUtils.isBlank(dueMsgTemplateId) || CollectionUtils.isEmpty(dueMsgObjects)) {
            return;
        }
        String alarmMsgOtherUsers = stateTimerConfig.getAlarmMsgOtherUsers();

        // 发送消息
        sendMessage(timerEntity, tsTimerDto, parser, dueMsgTemplateId, dueMsgObjects, alarmMsgOtherUsers);
    }

    @Override
    public void onTimerDue(TimerEvent event) {
        String timerUuid = event.getTimerUuid();
        BizProcessEntityTimerEntity timerEntity = processEntityTimerService.getByTimerUuid(timerUuid);
        if (timerEntity == null) {
            return;
        }

        TsTimerDto tsTimerDto = tsTimerFacadeService.getTimer(timerUuid);
        processEntityTimerService.updateTimerData(timerEntity, tsTimerDto);

        String processInstUuid = timerEntity.getProcessInstUuid();
        BizProcessInstanceEntity processInstanceEntity = processInstanceService.getOne(processInstUuid);
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processInstanceEntity.getProcessDefUuid());

        ProcessDefinitionJson.StateTimerConfig stateTimerConfig = getStateTimerConfig(timerEntity, parser);
        if (stateTimerConfig != null && stateTimerConfig.isEnableDueDoing()) {
            sendTimerDueMessage(timerEntity, tsTimerDto, stateTimerConfig, parser);
        }

        // 发布业务主体计时器到期事件
        processEntityTimerEventListenerPublisher.publishTimerDue(timerEntity, processInstanceEntity, parser);
    }

    /**
     * @param timerEntity
     * @param tsTimerDto
     * @param stateTimerConfig
     */
    private void sendTimerDueMessage(BizProcessEntityTimerEntity timerEntity, TsTimerDto tsTimerDto, ProcessDefinitionJson.StateTimerConfig stateTimerConfig,
                                     ProcessDefinitionJsonParser parser) {
        String dueMsgTemplateId = stateTimerConfig.getDueMsgTemplateId();
        List<String> dueMsgObjects = stateTimerConfig.getDueMsgObjects();
        if (StringUtils.isBlank(dueMsgTemplateId) || CollectionUtils.isEmpty(dueMsgObjects)) {
            return;
        }
        String dueMsgOtherUsers = stateTimerConfig.getDueMsgOtherUsers();

        // 发送消息
        sendMessage(timerEntity, tsTimerDto, parser, dueMsgTemplateId, dueMsgObjects, dueMsgOtherUsers);
    }

    /**
     * @param timerEntity
     * @param tsTimerDto
     * @param parser
     * @param dueMsgTemplateId
     * @param dueMsgObjects
     * @param dueMsgOtherUsers
     */
    private void sendMessage(BizProcessEntityTimerEntity timerEntity, TsTimerDto tsTimerDto, ProcessDefinitionJsonParser parser, String dueMsgTemplateId, List<String> dueMsgObjects, String dueMsgOtherUsers) {
        ProcessDefinitionJson.ProcessEntityConfig entityConfig = parser.getProcessEntityConfig();
        Map<String, Object> entityMainformData = null;
        List<String> toSendOrgIds = Lists.newArrayList();
        for (String dueMsgObject : dueMsgObjects) {
            switch (dueMsgObject) {
                // 业务主体创建人
                case "EntityCreator":
                    entityMainformData = processInstanceService.getEntityFormDataOfMainform(entityConfig.getFormUuid(), timerEntity.getEntityId(), entityConfig.getEntityIdField());
                    toSendOrgIds.add(Objects.toString(entityMainformData.get(Entity.CREATOR)));
                    break;
                // 其他人员
                case "Other":
                    if (StringUtils.isNotBlank(dueMsgOtherUsers)) {
                        toSendOrgIds.addAll(Arrays.asList(StringUtils.split(dueMsgOtherUsers, Separator.SEMICOLON.getValue())));
                    }
                    break;
            }
        }

        if (CollectionUtils.isNotEmpty(toSendOrgIds)) {
            Map<Object, Object> dyformValues = Maps.newHashMap();
            if (entityMainformData == null) {
                entityMainformData = processInstanceService.getEntityFormDataOfMainform(entityConfig.getFormUuid(), timerEntity.getEntityId(), entityConfig.getEntityIdField());
            }
            for (Map.Entry<String, Object> entry : entityMainformData.entrySet()) {
                dyformValues.put(entry.getKey(), entry.getValue());
            }
            List<IdEntity> entities = Lists.newArrayList();
            entities.add(tsTimerDto);
            messageClientApiFacade.send(dueMsgTemplateId, entities, dyformValues, toSendOrgIds);
        }
    }

    @Override
    public void onTimerOverDue(TimerEvent event) {
        String timerUuid = event.getTimerUuid();
        Date overDueTime = event.getOverdueTime();
        BizProcessEntityTimerEntity timerEntity = processEntityTimerService.getByTimerUuid(timerUuid);
        if (timerEntity == null) {
            return;
        }

        timerEntity.setOverDueTime(overDueTime);

        TsTimerDto tsTimerDto = tsTimerFacadeService.getTimer(timerUuid);
        processEntityTimerService.updateTimerData(timerEntity, tsTimerDto);

        String processInstUuid = timerEntity.getProcessInstUuid();
        BizProcessInstanceEntity processInstanceEntity = processInstanceService.getOne(processInstUuid);
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processInstanceEntity.getProcessDefUuid());

        ProcessDefinitionJson.StateTimerConfig stateTimerConfig = getStateTimerConfig(timerEntity, parser);
        if (stateTimerConfig != null && stateTimerConfig.isEnableOverdueDoing()) {
            // 发送消息
            sendTimerOverdueMessage(timerEntity, tsTimerDto, stateTimerConfig, parser);
            // 逾期处理
            handleOverdueAction(timerEntity, tsTimerDto, processInstanceEntity, stateTimerConfig, parser);
        }

        // 发布业务主体计时器逾期事件
        processEntityTimerEventListenerPublisher.publishTimerOverdue(timerEntity, processInstanceEntity, parser);
    }

    /**
     * @param timerEntity
     * @param tsTimerDto
     * @param stateTimerConfig
     * @param parser
     */
    private void sendTimerOverdueMessage(BizProcessEntityTimerEntity timerEntity, TsTimerDto tsTimerDto, ProcessDefinitionJson.StateTimerConfig stateTimerConfig, ProcessDefinitionJsonParser parser) {
        String overdueMsgTemplateId = stateTimerConfig.getOverdueMsgTemplateId();
        List<String> overdueMsgObjects = stateTimerConfig.getOverdueMsgObjects();
        if (StringUtils.isBlank(overdueMsgTemplateId) || CollectionUtils.isEmpty(overdueMsgObjects)) {
            return;
        }
        String overdueMsgOtherUsers = stateTimerConfig.getOverdueMsgOtherUsers();

        // 发送消息
        sendMessage(timerEntity, tsTimerDto, parser, overdueMsgTemplateId, overdueMsgObjects, overdueMsgOtherUsers);
    }

    /**
     * @param timerEntity
     * @param tsTimerDto
     * @param stateTimerConfig
     * @param parser
     */
    private void handleOverdueAction(BizProcessEntityTimerEntity timerEntity, TsTimerDto tsTimerDto, BizProcessInstanceEntity processInstanceEntity,
                                     ProcessDefinitionJson.StateTimerConfig stateTimerConfig, ProcessDefinitionJsonParser parser) {
        String overdueAction = stateTimerConfig.getOverdueAction();
        if (StringUtils.isBlank(overdueAction)) {
            return;
        }

        // 执行Groovy脚本
        String overdueGroovyScript = stateTimerConfig.getOverdueGroovyScript();
        if (StringUtils.equals("groovyScript", overdueAction) && StringUtils.isNotBlank(overdueGroovyScript)) {
            DyFormData dyFormData = processInstanceService.getEntityDyformData(parser.getProcessEntityConfig(), timerEntity.getEntityId());
            Map<String, Object> values = Maps.newHashMap();
            Map<String, Object> formData = Maps.newHashMap();
            formData.put(dyFormData.getFormId(), dyFormData.getFormDataOfMainform());
            values.put("processInstance", processInstanceEntity);
            values.put("dyFormData", dyFormData);
            values.put("formData", formData);
            values.put("entityTimer", timerEntity);
            values.put("tsTimer", tsTimerDto);
            // 当前用户相关变量
            values.putAll(TemplateEngineFactory.getExplainRootModel());
            GroovyUtils.run(overdueGroovyScript, values);
        }
    }

    /**
     * @param timerEntity
     * @param parser
     * @return
     */
    private ProcessDefinitionJson.StateTimerConfig getStateTimerConfig(BizProcessEntityTimerEntity timerEntity, ProcessDefinitionJsonParser parser) {
        ProcessDefinitionJson.ProcessEntityConfig entityConfig = parser.getProcessEntityConfig();
        ProcessDefinitionJson.StateTimerConfig stateTimerConfig = entityConfig.getTimers().stream().filter(timerConfig -> StringUtils.equals(timerConfig.getId(), timerEntity.getId())).findFirst().orElse(null);
        return stateTimerConfig;
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
