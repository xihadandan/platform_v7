/*
 * @(#)12/12/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.itemflow.listener.impl;

import com.google.common.collect.Maps;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.exception.WorkFlowException;
import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.biz.dto.BizProcessItemDataDto;
import com.wellsoft.pt.biz.entity.BizNewItemRelationEntity;
import com.wellsoft.pt.biz.entity.BizProcessItemInstanceEntity;
import com.wellsoft.pt.biz.enums.EnumProcessItemEventType;
import com.wellsoft.pt.biz.facade.service.BizProcessItemFacadeService;
import com.wellsoft.pt.biz.itemflow.AbstractItemFlowProcessItemListener;
import com.wellsoft.pt.biz.itemflow.resolver.ItemFlowResolver;
import com.wellsoft.pt.biz.itemflow.support.ItemReturnInfo;
import com.wellsoft.pt.biz.listener.RuntimeListener;
import com.wellsoft.pt.biz.listener.event.ProcessItemEvent;
import com.wellsoft.pt.biz.service.BizNewItemRelationService;
import com.wellsoft.pt.biz.service.BizProcessItemInstanceService;
import com.wellsoft.pt.biz.support.ItemFlowDefinition;
import com.wellsoft.pt.biz.support.ProcessDefinitionJsonParser;
import com.wellsoft.pt.biz.support.ProcessItemConfig;
import com.wellsoft.pt.biz.support.WorkflowIntegrationParams;
import com.wellsoft.pt.biz.utils.BusinessIntegrationContextHolder;
import com.wellsoft.pt.bot.facade.service.BotFacadeService;
import com.wellsoft.pt.bot.support.BotParam;
import com.wellsoft.pt.bot.support.BotResult;
import com.wellsoft.pt.bpm.engine.context.event.Event;
import com.wellsoft.pt.bpm.engine.core.FlowUserSid;
import com.wellsoft.pt.bpm.engine.support.InteractionTaskData;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 12/12/23.1	zhulh		12/12/23		Create
 * </pre>
 * @date 12/12/23
 */
@Component
public class ItemFlowProcessItemListenerImpl extends AbstractItemFlowProcessItemListener implements RuntimeListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ItemFlowResolver itemFlowResolver;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private BotFacadeService botFacadeService;

    @Autowired
    private BizProcessItemFacadeService processItemFacadeService;

    @Autowired
    private BizProcessItemInstanceService processItemInstanceService;

    @Autowired
    private BizNewItemRelationService newItemRelationService;

    /**
     * 监听器名称
     *
     * @return
     */
    @Override
    public String getName() {
        return "事项流事项监听处理";
    }

    /**
     * 单据反馈
     *
     * @param event
     */
    @Override
    protected void returnIfRequired(ProcessItemEvent event) {
        BizNewItemRelationEntity newItemRelationEntity = newItemRelationService.getByTargetItemInstUuid(event.getItemInstUuid());
        if (newItemRelationEntity == null) {
            return;
        }
        String extraData = newItemRelationEntity.getExtraData();
        if (StringUtils.isBlank(extraData)) {
            return;
        }

        ItemReturnInfo itemReturnInfo = JsonUtils.json2Object(extraData, ItemReturnInfo.class);
        if (matchReturn(event, itemReturnInfo)) {
            doReturnWithItemReturnInfo(event, itemReturnInfo);
        }
    }

    /**
     * @param event
     * @param itemReturnInfo
     * @return
     */
    private boolean matchReturn(ProcessItemEvent event, ItemReturnInfo itemReturnInfo) {
        if (StringUtils.isBlank(itemReturnInfo.getReturnBotRuleId())) {
            return false;
        }

        if (itemReturnInfo.getReturnWithOver() && EnumProcessItemEventType.Completed.getValue().equals(event.getEventType())) {
            return true;
        } else if (itemReturnInfo.getReturnWithEvent() && itemReturnInfo.getReturnEvents().contains(event.getEventType())) {
            return true;
        }

        return false;
    }

    /**
     * 单据反馈处理
     *
     * @param event
     * @param itemReturnInfo
     */
    private void doReturnWithItemReturnInfo(ProcessItemEvent event, ItemReturnInfo itemReturnInfo) {
        String returnBotRuleId = itemReturnInfo.getReturnBotRuleId();
        String targetDataUuid = itemReturnInfo.getSourceDataUuid();
        // 单据转换数据反馈处理
        Set<BotParam.BotFromParam> froms = new HashSet<BotParam.BotFromParam>();
        BotParam.BotFromParam botFromParam = new BotParam.BotFromParam(event.getDataUuid(), event.getFormUuid(),
                event.getDyFormData());
        froms.add(botFromParam);
        BotParam botParam = new BotParam(returnBotRuleId, froms);
        botParam.setFroms(froms);
        botParam.setTargetUuid(targetDataUuid);
        try {
            botFacadeService.startBot(botParam);
        } catch (Exception e) {
            logger.error("单据转换:业务事项单据反馈时出错！", e);
            throw new RuntimeException("业务事项单据反馈时出错,请检查单据转换配置！", e);
        }
    }

    /**
     * 事项流流转
     *
     * @param event
     */
    @Override
    protected void continueItemFlowIfRequired(ProcessItemEvent event) {
        String itemFlowId = event.getItemFlowId();
        ProcessDefinitionJsonParser parser = event.getProcessDefinitionJsonParser();
        ItemFlowDefinition itemFlowDefinition = parser.getItemFlow(itemFlowId);
        if (itemFlowDefinition == null) {
            return;
        }

        List<ItemFlowDefinition.NextItemInfo> nextItemInfos = itemFlowResolver.resolveNextItem(event, itemFlowDefinition);
        // 发起下一事项
        if (CollectionUtils.isNotEmpty(nextItemInfos)) {
            nextItemInfos.forEach(item -> startNextItem(event, item, parser));
        }
    }

    /**
     * 发起下一事项
     *
     * @param event
     * @param nextItemInfo
     */
    private void startNextItem(ProcessItemEvent event, ItemFlowDefinition.NextItemInfo nextItemInfo, ProcessDefinitionJsonParser parser) {
        ItemFlowDefinition.EdgeConfiguration edgeConfiguration = nextItemInfo.getEdge().getConfiguration();
        ProcessItemConfig nextItemConfig = nextItemInfo.getItemNode().getItemConfig();
        // 是否子事项
        boolean isChildItem = StringUtils.isNotBlank(nextItemInfo.getItemNode().getBelongItemId());
        boolean currentItemIsParent = StringUtils.equals(event.getItemId(), nextItemInfo.getItemNode().getBelongItemId());

        String itemFlowId = event.getItemFlowId();
        String itemFlowInstUuid = event.getItemFlowInstUuid();
        String startItemId = nextItemConfig.getId();
        String sourceFormUuid = event.getFormUuid();
        String sourceDataUuid = event.getDataUuid();
        DyFormData dyFormData = getItemDyformData(event, nextItemConfig, edgeConfiguration, currentItemIsParent, parser);

        BizProcessItemDataDto itemDataDto = new BizProcessItemDataDto();
        itemDataDto.setProcessDefId(event.getProcessDefId());
        itemDataDto.setItemFlowId(itemFlowId);
        itemDataDto.setItemFlowInstUuid(itemFlowInstUuid);
        itemDataDto.setItemId(startItemId);
        itemDataDto.setItemName(nextItemConfig.getItemName());
        itemDataDto.setItemCode(nextItemConfig.getItemCode());
        itemDataDto.setFormUuid(dyFormData.getFormUuid());
        itemDataDto.setDataUuid(dyFormData.getDataUuid());
        itemDataDto.setDyFormData(dyFormData);
        Map<String, InteractionTaskData> interactionTaskDataMap = BusinessIntegrationContextHolder.getInteractionTaskDataMap();
        if (interactionTaskDataMap == null) {
            interactionTaskDataMap = Maps.newHashMap();
            interactionTaskDataMap.put(startItemId, extractInteractionTaskData(event));
        } else if (!interactionTaskDataMap.containsKey(startItemId)) {
            interactionTaskDataMap.put(startItemId, extractInteractionTaskData(event));
        }
        itemDataDto.setInteractionTaskDataMap(interactionTaskDataMap);
        // setMappingItemField(itemDataDto, nextItemConfig, dyFormData);

        String parentItemInstUuid = null;
        if (isChildItem) {
            if (currentItemIsParent) {
                parentItemInstUuid = event.getItemInstUuid();
            } else {
                parentItemInstUuid = event.getParentItemInstUuid();
            }
        }
        itemDataDto.setParentItemInstUuid(parentItemInstUuid);

        WorkflowIntegrationParams workflowIntegrationParams = new WorkflowIntegrationParams();
        workflowIntegrationParams.setEdgeConfiguration(edgeConfiguration);

        String itemInstUuid = processItemFacadeService.startItemInstance(itemDataDto, workflowIntegrationParams);

        Map<String, Object> extraData = Maps.newHashMap();
        extraData.put("sourceFormUuid", sourceFormUuid);
        extraData.put("sourceDataUuid", sourceDataUuid);
        extraData.put("targetFormUuid", dyFormData.getFormUuid());
        extraData.put("targetDataUuid", dyFormData.getDataUuid());
        extraData.put("returnWithOver", edgeConfiguration.getReturnWithOver());
        extraData.put("returnWithEvent", edgeConfiguration.getReturnWithEvent());
        extraData.put("returnBotRuleId", edgeConfiguration.getReturnBotRuleId());
        extraData.put("returnEvents", edgeConfiguration.getReturnEvents());
        extraData.put("targetIsChild", isChildItem);
        newItemRelationService.addRelation(event.getItemInstUuid(), itemInstUuid, itemFlowInstUuid, JsonUtils.object2Json(extraData));
    }

    private InteractionTaskData extractInteractionTaskData(ProcessItemEvent event) {
        InteractionTaskData interactionTaskData = new InteractionTaskData();
        TaskData taskData = (TaskData) event.getExtraData().get("taskData");
        if (taskData == null) {
            Event workflowEvent = BusinessIntegrationContextHolder.getWorkflowEvent();
            if (workflowEvent != null) {
                taskData = workflowEvent.getTaskData();
            } else {
                taskData = new TaskData();
            }
        }
        interactionTaskData.setToDirectionIds(taskData.getToDirectionIds());
        interactionTaskData.setToTaskIds(taskData.getToTaskIds());
        interactionTaskData.setTaskUsers(convertFlowUserSidMap(taskData.getTaskUserSidsMap()));
        interactionTaskData.setTaskUserJobPaths(taskData.getTaskUserJobPaths());
        interactionTaskData.setTaskCopyUsers(convertFlowUserSidMap(taskData.getTaskCopyUserSidsMap()));
        interactionTaskData.setTaskMonitors(convertFlowUserSidMap(taskData.getTaskMonitorSidsMap()));
        return interactionTaskData;
    }

    private Map<String, List<String>> convertFlowUserSidMap(Map<String, Set<FlowUserSid>> taskUsersMap) {
        if (MapUtils.isEmpty(taskUsersMap)) {
            return Collections.emptyMap();
        }
        Map<String, List<String>> map = Maps.newHashMap();
        for (Map.Entry<String, Set<FlowUserSid>> entry : taskUsersMap.entrySet()) {
            map.put(entry.getKey(), entry.getValue().stream().map(sid -> sid.getId()).collect(Collectors.toList()));
        }
        return map;
    }

    /**
     * 获取事项表单数据
     *
     * @param event
     * @param edgeConfiguration
     * @return
     */
    protected DyFormData getItemDyformData(ProcessItemEvent event, ProcessItemConfig nextItemConfig, ItemFlowDefinition.EdgeConfiguration edgeConfiguration,
                                           boolean currentItemIsParent, ProcessDefinitionJsonParser parser) {
        String formDataType = edgeConfiguration.getFormDataType();
        if (StringUtils.isBlank(formDataType)) {
            throw new BusinessException("事项流发起的事项[" + nextItemConfig.getItemName() + "]没有配置使用单据");
        }

        String copyBotRuleId = edgeConfiguration.getCopyBotRuleId();
        DyFormData dyFormData = event.getDyFormData();
        // 使用单据，1使用上一办理事项单据、2上一办理事项单据转换、10使用组合事项办理单据、20组合事项单据转换
        switch (formDataType) {
            case "1":
                dyFormData.doForceCover();
                break;
            case "2":
                dyFormData = copyDyFormDataWithBotId(copyBotRuleId, dyFormData, event,
                        parser.getProcessItemConfigById(event.getItemId()), nextItemConfig, edgeConfiguration);
                break;
            case "10":
                // 当前事项是组合事项
                if (currentItemIsParent) {
                    dyFormData.doForceCover();
                } else {
                    // 获取组合事项数据
                    String parentItemInstUuid = event.getParentItemInstUuid();
                    dyFormData = getItemDyFormData(getParentItemInstance(parentItemInstUuid));
                }
                break;
            case "20":
                // 当前事项是组合事项
                if (currentItemIsParent) {
                    dyFormData = copyDyFormDataWithBotId(copyBotRuleId, dyFormData, event,
                            parser.getProcessItemConfigById(event.getItemId()), nextItemConfig, edgeConfiguration);
                } else {
                    // 获取组合事项数据
                    String parentItemInstUuid = event.getParentItemInstUuid();
                    BizProcessItemInstanceEntity parentItemInstanceEntity = getParentItemInstance(parentItemInstUuid);
                    DyFormData parentItemDyFormData = getItemDyFormData(parentItemInstanceEntity);
                    dyFormData = copyDyFormDataWithBotId(copyBotRuleId, parentItemDyFormData, event,
                            parser.getProcessItemConfigById(parentItemInstanceEntity.getItemId()), nextItemConfig, edgeConfiguration);
                }
                break;
        }
        return dyFormData;
    }

    /**
     * 根据上级事项UUID获取上级事项实例
     *
     * @param parentItemInstUuid
     * @return
     */
    private BizProcessItemInstanceEntity getParentItemInstance(String parentItemInstUuid) {
        if (StringUtils.isBlank(parentItemInstUuid)) {
            throw new BusinessException("业务数据错误，当前事项数据没有上级事项数据！");
        }
        return processItemInstanceService.getOne(parentItemInstUuid);
    }

    /**
     * 根据事项实例获取事项表单数据
     *
     * @param itemInstanceEntity
     * @return
     */
    private DyFormData getItemDyFormData(BizProcessItemInstanceEntity itemInstanceEntity) {
        return dyFormFacade.getDyFormData(itemInstanceEntity.getFormUuid(), itemInstanceEntity.getDataUuid());
    }

    /**
     * 设置表单事项映射字段
     *
     * @param itemDataDto
     * @param processItemConfig
     * @param dyFormData
     */
    private void setMappingItemField(BizProcessItemDataDto itemDataDto, ProcessItemConfig processItemConfig, DyFormData dyFormData) {
        ProcessItemConfig.ProcessItemFormConfig formConfig = processItemConfig.getFormConfig();
        String itemNameField = formConfig.getItemNameField();
        String itemCodeField = formConfig.getItemCodeField();
        // 事项名称
        if (dyFormData.isFieldExist(itemNameField)) {
            dyFormData.setFieldValue(itemNameField, itemDataDto.getItemName());
        }
        // 事项编码
        if (dyFormData.isFieldExist(itemCodeField)) {
            dyFormData.setFieldValue(itemCodeField, itemDataDto.getItemCode());
        }
    }

    /**
     * 单据转换处理
     *
     * @param botRuleId
     * @param dyFormData
     * @param event
     * @param sourceItemConfig
     * @param nextItemConfig
     * @param edgeConfiguration
     * @return
     */
    private DyFormData copyDyFormDataWithBotId(String botRuleId, DyFormData dyFormData, ProcessItemEvent event,
                                               ProcessItemConfig sourceItemConfig,
                                               ProcessItemConfig nextItemConfig,
                                               ItemFlowDefinition.EdgeConfiguration edgeConfiguration) {
        Set<BotParam.BotFromParam> froms = new HashSet<BotParam.BotFromParam>();
        BotParam.BotFromParam botFromParam = new BotParam.BotFromParam(dyFormData.getDataUuid(), dyFormData.getFormUuid(), dyFormData);
        froms.add(botFromParam);
        BotParam botParam = new BotParam(botRuleId, froms);
        botParam.setFroms(froms);
        HashMap<String, Object> jsonBody = Maps.newHashMap();
        jsonBody.put("sourceItemConfig", sourceItemConfig);
        jsonBody.put("targetItemConfig", nextItemConfig);
        botParam.setJsonBody(jsonBody);
        BotResult botResult = null;
        try {
            botResult = botFacadeService.startBot(botParam);
        } catch (Exception e) {
            logger.error("事项流发起业务事项单据转换出错！", e);
            throw new BusinessException("事项流发起业务事项单据转换出错！", e);
        }
        Object dyformData = botResult.getDyformData();
        Object data = botResult.getData();
        String newFormUuid = null;
        String newDataUuid = null;
        if (dyformData instanceof DyFormData) {
            return (DyFormData) dyformData;
        } else if (data instanceof Map) {
            Map<String, Object> formData = (Map<String, Object>) data;
            newFormUuid = (String) formData.get("form_uuid");
            newDataUuid = botResult.getDataUuid();
            if (StringUtils.isBlank(newFormUuid)) {
                throw new WorkFlowException("事项流发起业务事项使用的单据转换规则[" + botRuleId + "]配置异常，没有勾选“保存单据”，无法发起事项，请联系管理员修改");
            }
        } else {
            throw new BusinessException("事项流发起业务事项失败，无法取到业务事项数据单据转换后的数据！");
        }

        return dyFormFacade.getDyFormData(newFormUuid, newDataUuid);
    }

}
