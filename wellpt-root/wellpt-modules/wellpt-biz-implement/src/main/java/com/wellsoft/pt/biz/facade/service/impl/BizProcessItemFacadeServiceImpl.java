/*
 * @(#)10/13/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.facade.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.exception.BusinessException;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.pt.biz.condition.ItemSituationConditionEvaluator;
import com.wellsoft.pt.biz.condition.ProcessNodeConditionEvaluator;
import com.wellsoft.pt.biz.dto.BizProcessItemDataDto;
import com.wellsoft.pt.biz.dto.BizProcessItemDataRequestParamDto;
import com.wellsoft.pt.biz.dto.BizProcessItemOperationDto;
import com.wellsoft.pt.biz.dto.BizWorkflowProcessItemDataDto;
import com.wellsoft.pt.biz.entity.*;
import com.wellsoft.pt.biz.enums.*;
import com.wellsoft.pt.biz.facade.service.BizBusinessIntegrationFacadeService;
import com.wellsoft.pt.biz.facade.service.BizProcessItemFacadeService;
import com.wellsoft.pt.biz.listener.BizEventListenerPublisher;
import com.wellsoft.pt.biz.listener.BizProcessItemTimerListener;
import com.wellsoft.pt.biz.service.*;
import com.wellsoft.pt.biz.state.BizStateChangedPublisher;
import com.wellsoft.pt.biz.support.*;
import com.wellsoft.pt.biz.utils.BusinessIntegrationContextHolder;
import com.wellsoft.pt.biz.utils.ProcessDefinitionUtils;
import com.wellsoft.pt.biz.utils.ProcessTitleUtils;
import com.wellsoft.pt.bot.facade.service.BotFacadeService;
import com.wellsoft.pt.bpm.engine.support.SubmitResult;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.dto.DyFormFormDefinition;
import com.wellsoft.pt.dyform.facade.dto.DyformFieldDefinition;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.implement.definition.enums.EnumRelationTblSystemField;
import com.wellsoft.pt.jpa.comparator.IdEntityComparators;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.timer.dto.TsTimerConfigDto;
import com.wellsoft.pt.timer.dto.TsTimerDto;
import com.wellsoft.pt.timer.enums.*;
import com.wellsoft.pt.timer.facade.service.TsTimerConfigFacadeService;
import com.wellsoft.pt.timer.facade.service.TsTimerFacadeService;
import com.wellsoft.pt.timer.support.TsTimerParam;
import com.wellsoft.pt.timer.support.TsTimerParamBuilder;
import com.wellsoft.pt.workflow.facade.service.WfFlowBusinessDefinitionFacadeService;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import com.wellsoft.pt.workflow.work.service.WorkService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 10/13/22.1	zhulh		10/13/22		Create
 * </pre>
 * @date 10/13/22
 */
@Service
public class BizProcessItemFacadeServiceImpl extends AbstractApiFacade implements BizProcessItemFacadeService {

    @Autowired
    private BizProcessItemInstanceService bizProcessItemInstanceService;

    @Autowired
    private BizProcessItemInstanceDispenseService bizProcessItemInstanceDispenseService;

    @Autowired
    private BizProcessNodeInstanceService bizProcessNodeInstanceService;

    @Autowired
    private BizProcessInstanceService bizProcessInstanceService;

    @Autowired
    private BizItemDefinitionService bizItemDefinitionService;

    @Autowired
    private BizProcessItemOperationService bizProcessItemOperationService;

    @Autowired
    private DyFormFacade dyFormFacade;

    @Autowired
    private TsTimerFacadeService timerFacadeService;

    @Autowired
    private TsTimerConfigFacadeService timerConfigFacadeService;

    @Autowired
    private BotFacadeService botFacadeService;

    @Autowired
    private BizEventListenerPublisher eventListenerPublisher;

    @Autowired
    private BizStateChangedPublisher stateChangedPublisher;

    @Autowired
    private ProcessNodeConditionEvaluator processNodeConditionEvaluator;

    @Autowired
    private ItemSituationConditionEvaluator itemSituationConditionEvaluator;

    @Autowired
    private BizBusinessIntegrationFacadeService bizBusinessIntegrationFacadeService;

    @Autowired
    private WfFlowBusinessDefinitionFacadeService flowBusinessDefinitionFacadeService;

    @Autowired
    private BizBusinessIntegrationService businessIntegrationService;

    @Autowired
    private BizNewItemRelationService newItemRelationService;

    @Autowired
    @Qualifier("workService")
    private WorkService workService;

    /**
     * 创建事项办件实例数据
     *
     * @param processDefId
     * @param processItemIds
     * @return
     */
    @Override
    public BizProcessItemDataDto newItemById(String processDefId, String processItemIds) {
        String itemId = getFirstItemIdIfRequired(processItemIds);
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefId(processDefId);
        ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(itemId);

        // 获取使用事项表单数据的流程集成配置
        String flowDefId = getFlowDefId(processItemConfig, EnumBizBiFormDataType.UseItemFormData.getValue());
        //  String flowDefId = getFlowDefIdByFlowBizDefId(flowBizDefId);
        BizProcessItemDataDto itemDataDto = new BizProcessItemDataDto();
        itemDataDto.setTitle(ProcessTitleUtils.generateItemInstanceTitle(parser.getProcessItemNameById(processItemIds)));
        itemDataDto.setProcessDefId(processDefId);
        itemDataDto.setItemId(processItemIds);
        itemDataDto.setState(EnumBizProcessItemState.Created.getValue());
        itemDataDto.setFormUuid(processItemConfig.getFormConfig().getFormUuid());
        // itemDataDto.setFlowBizDefId(flowBizDefId);
        itemDataDto.setFlowDefId(flowDefId);
        return itemDataDto;
    }

    /**
     * 获取使用事项表单数据的流程定义ID
     *
     * @param processItemConfig
     * @param useFormDataType
     * @return
     */
    private String getFlowDefId(ProcessItemConfig processItemConfig, String useFormDataType) {
        ProcessItemConfig.BusinessIntegrationConfig businessIntegrationConfig = getWorkflowIntegrationConfig(processItemConfig, useFormDataType);
        if (businessIntegrationConfig == null) {
            return null;
        }
        return businessIntegrationConfig.getFlowDefId();
    }

    /**
     * 根据流程业务定义ID获取流程定义ID
     *
     * @param flowBizDefId
     * @return
     */
    private String getFlowDefIdByFlowBizDefId(String flowBizDefId) {
        if (StringUtils.isBlank(flowBizDefId)) {
            return null;
        }
        return flowBusinessDefinitionFacadeService.getFlowDefIdById(flowBizDefId);
    }

    /**
     * 获取使用事项表单数据的流程集成配置
     *
     * @param processItemConfig
     * @param useFormDataType
     * @return
     */
    private ProcessItemConfig.BusinessIntegrationConfig getWorkflowIntegrationConfig(ProcessItemConfig processItemConfig, String useFormDataType) {
        List<ProcessItemConfig.BusinessIntegrationConfig> businessIntegrationConfigs = processItemConfig.getBusinessIntegrationConfigs();
        if (CollectionUtils.isEmpty(businessIntegrationConfigs)) {
            return null;
        }
        for (ProcessItemConfig.BusinessIntegrationConfig businessIntegrationConfig : businessIntegrationConfigs) {
            if (BooleanUtils.isNotTrue(businessIntegrationConfig.isEnabled()) && StringUtils.equals("1", businessIntegrationConfig.getType())) {
                continue;
            }
            if (StringUtils.equals(businessIntegrationConfig.getFormDataType(), useFormDataType)) {
                return businessIntegrationConfig;
            }
        }
        return null;
    }

    /**
     * 根据业务事项实例UUID获取事项办件实例数据
     *
     * @param itemInstUuid
     * @return
     */
    @Override
    public BizProcessItemDataDto getItemByUuid(String itemInstUuid) {
        BizProcessItemInstanceEntity itemInstanceEntity = bizProcessItemInstanceService.getOne(itemInstUuid);
        BizProcessItemDataDto itemDataDto = new BizProcessItemDataDto();
        itemDataDto.setTitle(itemInstanceEntity.getTitle());
        itemDataDto.setItemInstUuid(itemInstUuid);
        itemDataDto.setItemId(itemInstanceEntity.getItemId());
        itemDataDto.setFormUuid(itemInstanceEntity.getFormUuid());
        itemDataDto.setDataUuid(itemInstanceEntity.getDataUuid());
        return itemDataDto;
    }

    /**
     * 多个事项ID时，获取第一个事项ID
     *
     * @param processItemIds
     * @return
     */
    private String getFirstItemIdIfRequired(String processItemIds) {
        if (StringUtils.indexOf(processItemIds, Separator.SEMICOLON.getValue()) == -1) {
            return processItemIds;
        }
        List<String> pids = Arrays.asList(StringUtils.split(processItemIds, Separator.SEMICOLON.getValue()));
        return pids.get(0);
    }

    /**
     * 获取业务事项实例数据
     *
     * @param itemDataRequestParamDto
     * @return
     */
    @Override
    public BizProcessItemDataDto getItemData(BizProcessItemDataRequestParamDto itemDataRequestParamDto) {
        String itemInstUuid = itemDataRequestParamDto.getItemInstUuid();
        BizProcessItemDataDto itemDataDto = null;
        if (StringUtils.isBlank(itemInstUuid)) {
            itemDataDto = loadItemData(itemDataRequestParamDto);
        } else {
            itemDataDto = loadItemDataByItemInstUuid(itemInstUuid);
        }
        return itemDataDto;
    }

    /**
     * 获取流程集成工作数据
     *
     * @param processDefId
     * @param processItemIds
     * @param workBean
     * @return
     */
    @Override
    public WorkBean getWorkData(String processDefId, String processItemIds, WorkBean workBean) {
        // 获取流程数据
        WorkBean workData = workService.getWorkData(workBean);
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefId(processDefId);
        // 事项数据
        String itemName = parser.getProcessItemNameById(processItemIds);
        String itemCode = parser.getProcessItemCodeById(processItemIds);

        // 表单数据
        DyFormData dyFormData = workData.getDyFormData();

        BizProcessItemDataDto itemDataDto = new BizProcessItemDataDto();
        itemDataDto.setProcessDefId(processDefId);
        itemDataDto.setItemId(processItemIds);
        itemDataDto.setItemName(itemName);
        itemDataDto.setItemCode(itemCode);
        // 设置表单映射字段
        setMappingFieldValues(itemDataDto, parser, dyFormData);
        // 业务事项配置
        String itemId = getFirstItemIdIfRequired(processItemIds);
        workData.getExtraParams().put("processItemConfig", parser.getProcessItemConfigById(itemId));
        return workData;
    }

    /**
     * @param itemInstUuid
     * @return
     */
    @Override
    public BizBusinessIntegrationEntity getWorkflowBusinessIntegrationByItemInstUuid(String itemInstUuid) {
        return businessIntegrationService.getByTypeAndItemInstUuid(EnumBizBiType.Workflow.getValue(), itemInstUuid);
    }

    /**
     * 加载新的事项实例数据
     *
     * @param itemDataRequestParamDto
     * @return
     */
    private BizProcessItemDataDto loadItemData(BizProcessItemDataRequestParamDto itemDataRequestParamDto) {
        String processDefId = itemDataRequestParamDto.getProcessDefId();
        String processItemId = itemDataRequestParamDto.getItemId();
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefId(processDefId);
        // 事项数据
        String itemName = parser.getProcessItemNameById(processItemId);
        String itemCode = parser.getProcessItemCodeById(processItemId);

        // 表单数据
        String formUuid = itemDataRequestParamDto.getFormUuid();
        String dataUuid = itemDataRequestParamDto.getDataUuid();
        if (StringUtils.isBlank(formUuid)) {
            throw new BusinessException("事项办理表单不能为空！");
        }
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);

        // 业务事项配置
        String itemId = getFirstItemIdIfRequired(processItemId);
        ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(itemId);
        boolean enabledSituation = isEnableItemSituation(processItemId, parser);

        BizProcessItemDataDto itemDataDto = new BizProcessItemDataDto();
        BeanUtils.copyProperties(itemDataRequestParamDto, itemDataDto);
        if (StringUtils.isBlank(itemDataDto.getTitle())) {
            itemDataDto.setTitle(ProcessTitleUtils.generateItemInstanceTitle(processItemConfig.getItemName()));
        }
        itemDataDto.setItemName(itemName);
        itemDataDto.setItemCode(itemCode);
        itemDataDto.setItemType(processItemConfig.getItemType());
        itemDataDto.setState(EnumBizProcessItemState.Created.getValue());
        itemDataDto.setTimerState(EnumTimerStatus.READY.getValue());
        itemDataDto.setDyFormData(dyFormData);
        itemDataDto.setProcessItemConfig(processItemConfig);
        itemDataDto.setEnabledSituation(enabledSituation);
        // 设置表单映射字段
        setMappingFieldValues(itemDataDto, parser, dyFormData);
        return itemDataDto;
    }

    /**
     * 是否启用事项办理情形
     *
     * @param processItemId
     * @param parser
     * @return
     */
    private boolean isEnableItemSituation(String processItemId, ProcessDefinitionJsonParser parser) {
        List<String> pids = Arrays.asList(StringUtils.split(processItemId, Separator.SEMICOLON.getValue()));
        for (String itemId : pids) {
            if (parser.getProcessItemConfigById(itemId).getEnabledSituation()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置表单映射字段
     *
     * @param itemDataDto
     * @param parser
     * @param dyFormData
     */
    private void setMappingFieldValues(BizProcessItemDataDto itemDataDto, ProcessDefinitionJsonParser parser, DyFormData dyFormData) {
        String itemId = itemDataDto.getItemId();
        String itemCode = itemDataDto.getItemCode();
        List<String> itemIds = Arrays.asList(StringUtils.split(itemId, Separator.SEMICOLON.getValue()));
        List<String> itemCodes = Arrays.asList(StringUtils.split(itemCode, Separator.SEMICOLON.getValue()));
        ProcessItemConfig processItemConfig = null;
        Set<ItemMaterial> materialSet = Sets.newLinkedHashSet();
        for (int index = 0; index < itemIds.size(); index++) {
            ProcessItemConfig itemConfig = parser.getProcessItemConfigById(itemIds.get(index));

            // 事项信息
            if (index == 0) {
                processItemConfig = itemConfig;
                setMappingItemField(itemDataDto, processItemConfig, parser, dyFormData);
            }

            // 事项材料
            String itemDefId = itemConfig.getItemDefId();
            materialSet.addAll(bizItemDefinitionService.listMaterialDataByItemCode(itemDefId, itemCodes.get(index)));
        }

        ProcessItemConfig.ProcessItemFormConfig formConfig = processItemConfig.getFormConfig();
        String materialSubformId = formConfig.getMaterialSubformId();
        String materialNameField = formConfig.getMaterialNameField();
        String materialCodeField = formConfig.getMaterialCodeField();
        String materialRequiredField = formConfig.getMaterialRequiredField();
        // 没有材料从表，直接返回
        if (StringUtils.isBlank(materialSubformId)) {
            return;
        }
        if (StringUtils.isBlank(materialNameField)) {
            throw new BusinessException("材料名称字段不能为空！");
        }
        if (StringUtils.isBlank(materialCodeField)) {
            throw new BusinessException("材料编码字段不能为空！");
        }
        DyFormFormDefinition materialFormDefinition = dyFormFacade.getFormDefinitionById(materialSubformId);
        if (materialFormDefinition == null) {
            throw new BusinessException(String.format("业务流程事项配置[%s]，材料从表不存在！", processItemConfig.getItemName()));
        }
        List<DyFormData> subformDataList = dyFormData.getDyformDatasByFormId(materialSubformId);

        Map<String, Object> dataHolderMap = Maps.newHashMap();
        int sortOrder = 0;
        for (ItemMaterial itemMaterial : materialSet) {
            // 已经存在材料，忽略掉
            if (existsMaterial(materialCodeField, itemMaterial.getMaterialCode(), subformDataList)) {
                continue;
            }
            DyFormData subformData = dyFormFacade.createDyformData(materialFormDefinition.getUuid());
            subformData.setFieldValue(materialNameField, itemMaterial.getMaterialName());
            subformData.setFieldValue(materialCodeField, itemMaterial.getMaterialCode());
            subformData.setFieldValue(EnumRelationTblSystemField.sort_order.name(), sortOrder++);
            if (StringUtils.isNotBlank(materialRequiredField)) {
                subformData.setFieldValue(materialRequiredField, itemMaterial.getMaterialRequired());
            }
            if (CollectionUtils.isNotEmpty(formConfig.getMaterialFieldMappings())) {
                setMaterialOtherMappingItemField(itemDataDto, processItemConfig, parser, itemMaterial, subformData, formConfig.getMaterialFieldMappings(), dataHolderMap);
            }
            dyFormData.addSubformData(subformData);
        }
    }

    /**
     * @param itemDataDto
     * @param processItemConfig
     * @param parser
     * @param itemMaterial
     * @param subformData
     * @param materialFieldMappings
     */
    private void setMaterialOtherMappingItemField(BizProcessItemDataDto itemDataDto, ProcessItemConfig processItemConfig,
                                                  ProcessDefinitionJsonParser parser,
                                                  ItemMaterial itemMaterial, DyFormData subformData,
                                                  List<ProcessItemConfig.ItemFormFieldMapping> materialFieldMappings,
                                                  Map<String, Object> dataHolderMap) {
        boolean hasBizEntityMapping = false;
        List<String> itemSourceFields = Lists.newArrayList();
        for (ProcessItemConfig.ItemFormFieldMapping fieldMapping : materialFieldMappings) {
            // 业务主体
            if (EnumProcessItemFieldMappingSourceType.Entity.getValue().equals(fieldMapping.getSourceType())) {
                hasBizEntityMapping = true;
            } else if (EnumProcessItemFieldMappingSourceType.Item.getValue().equals(fieldMapping.getSourceType())) {
                itemSourceFields.add(fieldMapping.getSourceField());
            } else if (EnumProcessItemFieldMappingSourceType.ItemMaterial.getValue().equals(fieldMapping.getSourceType())) {
                // 材料附件映射
                String key = "itemMaterial_" + processItemConfig.getItemCode();
                List<DyFormData> materials = (List<DyFormData>) dataHolderMap.get(key);
                if (materials == null) {
                    materials = bizItemDefinitionService.getItemDyFormDataOfMaterials(processItemConfig.getItemDefId(), processItemConfig.getItemCode());
                    dataHolderMap.put(key, materials);
                }
                if (CollectionUtils.isNotEmpty(materials)) {
                    for (DyFormData material : materials) {
                        if (StringUtils.equals(Objects.toString(material.getFieldValue(itemMaterial.getMaterialCodeField())),
                                itemMaterial.getMaterialCode())) {
                            subformData.setFieldValue(fieldMapping.getTargetField(), material.getFieldValue(fieldMapping.getSourceField()));
                            break;
                        }
                    }
                }
            }
        }

        // 获取源数据
        Map<String, Object> entityDyformData = (Map<String, Object>) dataHolderMap.get("entityDyformData");
        Map<String, Object> itemDyformData = (Map<String, Object>) dataHolderMap.get("itemDyformData_" + processItemConfig.getItemCode());
        if (entityDyformData == null) {
            ProcessDefinitionJson.ProcessEntityConfig processEntityConfig = parser.getProcessEntityConfig();
            if (hasBizEntityMapping && processEntityConfig != null) {
                String entityFormUuid = processEntityConfig.getFormUuid();
                String entityIdField = processEntityConfig.getEntityIdField();
                String entityId = StringUtils.EMPTY;
                String itemInstUuid = itemDataDto.getItemInstUuid();
                if (StringUtils.isBlank(entityId) && StringUtils.isNotBlank(itemInstUuid)) {
                    BizProcessItemInstanceEntity itemInstanceEntity = bizProcessItemInstanceService.getOne(itemInstUuid);
                    entityId = itemInstanceEntity.getEntityId();
                }
                if (StringUtils.isNotBlank(entityId)) {
                    entityDyformData = bizProcessInstanceService.getEntityFormDataOfMainform(entityFormUuid, entityId, entityIdField);
                }
            }
            dataHolderMap.put("entityDyformData", entityDyformData != null ? entityDyformData : Collections.emptyMap());
        }
        if (itemDyformData == null) {
            if (CollectionUtils.isNotEmpty(itemSourceFields)) {
                itemDyformData = bizItemDefinitionService.getItemFormDataOfMainform(processItemConfig.getItemDefId(), processItemConfig.getItemCode());
            }
            dataHolderMap.put("itemDyformData_" + processItemConfig.getItemCode(), itemDyformData != null ? itemDyformData : Collections.emptyMap());
        }

        // 设置映射字段值
        for (ProcessItemConfig.ItemFormFieldMapping fieldMapping : materialFieldMappings) {
            // 业务主体
            if (MapUtils.isNotEmpty(entityDyformData) && EnumProcessItemFieldMappingSourceType.Entity.getValue().equals(fieldMapping.getSourceType())) {
                subformData.setFieldValue(fieldMapping.getTargetField(), entityDyformData.get(fieldMapping.getSourceField()));
            } else if (MapUtils.isNotEmpty(itemDyformData) && EnumProcessItemFieldMappingSourceType.Item.getValue().equals(fieldMapping.getSourceType())) {
                subformData.setFieldValue(fieldMapping.getTargetField(), itemDyformData.get(fieldMapping.getSourceField()));
            }
        }
    }

    /**
     * 从表数据是否存在材料
     *
     * @param materialCodeField
     * @param materialCode
     * @param subformDataList
     * @return
     */
    private boolean existsMaterial(String materialCodeField, String materialCode, List<DyFormData> subformDataList) {
        if (CollectionUtils.isEmpty(subformDataList)) {
            return false;
        }
        for (DyFormData subformData : subformDataList) {
            if (subformData.isFieldExist(materialCodeField) &&
                    StringUtils.equals(materialCode, Objects.toString(subformData.getFieldValue(materialCodeField)))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置表单事项映射字段
     *
     * @param itemDataDto
     * @param processItemConfig
     * @param parser
     * @param dyFormData
     */
    private void setMappingItemField(BizProcessItemDataDto itemDataDto, ProcessItemConfig processItemConfig,
                                     ProcessDefinitionJsonParser parser, DyFormData dyFormData) {
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
        // 其他字段映射
        List<ProcessItemConfig.ItemFormFieldMapping> fieldMappings = formConfig.getFieldMappings();
        if (CollectionUtils.isNotEmpty(fieldMappings)) {
            setOtherMappingItemField(itemDataDto, processItemConfig, parser, dyFormData, fieldMappings);
        }
    }

    /**
     * @param itemDataDto
     * @param processItemConfig
     * @param parser
     * @param dyFormData
     * @param fieldMappings
     */
    private void setOtherMappingItemField(BizProcessItemDataDto itemDataDto, ProcessItemConfig processItemConfig,
                                          ProcessDefinitionJsonParser parser, DyFormData dyFormData,
                                          List<ProcessItemConfig.ItemFormFieldMapping> fieldMappings) {
        boolean hasBizEntityMapping = false;
        List<String> itemSourceFields = Lists.newArrayList();
        for (ProcessItemConfig.ItemFormFieldMapping fieldMapping : fieldMappings) {
            // 业务主体
            if (EnumProcessItemFieldMappingSourceType.Entity.getValue().equals(fieldMapping.getSourceType())) {
                hasBizEntityMapping = true;
            } else if (EnumProcessItemFieldMappingSourceType.Item.getValue().equals(fieldMapping.getSourceType())) {
                itemSourceFields.add(fieldMapping.getSourceField());
            }
        }

        // 获取源数据
        Map<String, Object> entityDyformData = null;
        Map<String, Object> itemDyformData = null;
        ProcessDefinitionJson.ProcessEntityConfig processEntityConfig = parser.getProcessEntityConfig();
        if (hasBizEntityMapping && processEntityConfig != null) {
            String entityFormUuid = processEntityConfig.getFormUuid();
            String entityIdField = processEntityConfig.getEntityIdField();
            String entityId = Objects.toString(dyFormData.getFieldValue(entityIdField), StringUtils.EMPTY);
            String itemInstUuid = itemDataDto.getItemInstUuid();
            if (StringUtils.isBlank(entityId) && StringUtils.isNotBlank(itemInstUuid)) {
                BizProcessItemInstanceEntity itemInstanceEntity = bizProcessItemInstanceService.getOne(itemInstUuid);
                entityId = itemInstanceEntity.getEntityId();
            }
            if (StringUtils.isNotBlank(entityId)) {
                entityDyformData = bizProcessInstanceService.getEntityFormDataOfMainform(entityFormUuid, entityId, entityIdField);
            }
        }
        if (CollectionUtils.isNotEmpty(itemSourceFields)) {
            itemDyformData = bizItemDefinitionService.getItemFormDataOfMainform(processItemConfig.getItemDefId(), processItemConfig.getItemCode());
        }

        // 设置映射字段值
        for (ProcessItemConfig.ItemFormFieldMapping fieldMapping : fieldMappings) {
            // 业务主体
            if (MapUtils.isNotEmpty(entityDyformData) && EnumProcessItemFieldMappingSourceType.Entity.getValue().equals(fieldMapping.getSourceType())) {
                dyFormData.setFieldValue(fieldMapping.getTargetField(), entityDyformData.get(fieldMapping.getSourceField()));
            } else if (MapUtils.isNotEmpty(itemDyformData) && EnumProcessItemFieldMappingSourceType.Item.getValue().equals(fieldMapping.getSourceType())) {
                dyFormData.setFieldValue(fieldMapping.getTargetField(), itemDyformData.get(fieldMapping.getSourceField()));
            }
        }
    }

    /**
     * 根据业务事项实例UUID加载事项数据
     *
     * @param itemInstUuid
     * @return
     */
    private BizProcessItemDataDto loadItemDataByItemInstUuid(String itemInstUuid) {
        BizProcessItemInstanceEntity itemInstanceEntity = bizProcessItemInstanceService.getOne(itemInstUuid);
        String processDefUuid = itemInstanceEntity.getProcessDefUuid();
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(processDefUuid);
        // 事项数据
        String title = itemInstanceEntity.getTitle();
        String processDefId = parser.getProcessDefId();
        String itemName = itemInstanceEntity.getItemName();
        String itemCode = itemInstanceEntity.getItemCode();
        String itemType = itemInstanceEntity.getItemType();
        String state = itemInstanceEntity.getState();
        Integer timerState = itemInstanceEntity.getTimerState();
        String parentItemInstUuid = itemInstanceEntity.getParentItemInstUuid();
        String itemFlowId = itemInstanceEntity.getItemFlowDefId();
        String itemFlowInstUuid = itemInstanceEntity.getItemFlowInstUuid();

        // 表单数据
        String formUuid = itemInstanceEntity.getFormUuid();
        String dataUuid = itemInstanceEntity.getDataUuid();
        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);

        // 业务事项配置
        String itemId = itemInstanceEntity.getItemId();
        String firstItemId = getFirstItemIdIfRequired(itemId);
        ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(firstItemId);

        BizProcessItemDataDto itemDataDto = new BizProcessItemDataDto();
        itemDataDto.setTitle(title);
        itemDataDto.setProcessDefId(processDefId);
        itemDataDto.setItemInstUuid(itemInstUuid);
        itemDataDto.setItemId(itemId);
        itemDataDto.setItemName(itemName);
        itemDataDto.setItemCode(itemCode);
        itemDataDto.setItemType(itemType);
        itemDataDto.setState(state);
        itemDataDto.setTimerState(timerState);
        itemDataDto.setParentItemInstUuid(parentItemInstUuid);
        itemDataDto.setItemFlowId(itemFlowId);
        itemDataDto.setItemFlowInstUuid(itemFlowInstUuid);
        itemDataDto.setFormUuid(formUuid);
        itemDataDto.setDataUuid(dataUuid);
        itemDataDto.setDyFormData(dyFormData);
        itemDataDto.setProcessItemConfig(processItemConfig);
        itemDataDto.setEnabledSituation(processItemConfig.getEnabledSituation());
        return itemDataDto;
    }

    /**
     * 保存业务事项实例数据
     *
     * @param itemDataDto
     * @return
     */
    @Override
    @Transactional
    public String save(BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = itemDataDto.getItemInstUuid();
        // String processDefId = itemDataDto.getProcessDefId();
        // String processItemId = itemDataDto.getItemId();
        DyFormData dyFormData = itemDataDto.getDyFormData();
        // BizProcessItemInstanceEntity itemInstanceEntity = null;

        if (StringUtils.isBlank(itemInstUuid)) {
            itemInstUuid = bizProcessItemInstanceService.saveAsDraft(itemDataDto, dyFormData);
        } else {
            bizProcessItemInstanceService.updateByUUid(itemInstUuid, dyFormData);
        }
        bizProcessItemOperationService.log(itemInstUuid, EnumOperateType.Save);
        return itemInstUuid;
    }

    /**
     * 提交业务事项实例数据
     *
     * @param itemDataDto
     * @return
     */
    @Override
    @Transactional
    public String submit(BizProcessItemDataDto itemDataDto) {
        BizProcessItemDataDto processItemDataDto = itemDataDto;
        String itemInstUuid = processItemDataDto.getItemInstUuid();
        try {
            BusinessIntegrationContextHolder.setInteractionTaskDataMap(itemDataDto.getInteractionTaskDataMap());
            // 保存业务事项为草稿
            if (StringUtils.isBlank(itemInstUuid)) {
                itemInstUuid = save(processItemDataDto);
                processItemDataDto = loadItemDataByItemInstUuid(itemInstUuid);
            } else {
                dyFormFacade.saveFormData(itemDataDto.getDyFormData());
            }

            // 草稿件进行提交处理
            if (isDraft(processItemDataDto)) {
                // 单个事项
                if (isSingleItem(processItemDataDto)) {
                    submitItem(processItemDataDto);
                } else {
                    // 多个事项，拆分事项并提交
                    splitItemAndSubmit(processItemDataDto);
                }
            } else {
                // 非草稿件直接提交
                submitItem(processItemDataDto);
            }
        } finally {
            BusinessIntegrationContextHolder.removeInteractionTaskDataMap();
        }

        return itemInstUuid;
    }

    /**
     * 发起业务事项
     *
     * @param itemDataDto
     * @param workflowIntegrationParams
     * @return
     */
    @Override
    @Transactional
    public String startItemInstance(BizProcessItemDataDto itemDataDto, WorkflowIntegrationParams workflowIntegrationParams) {
        try {
            BusinessIntegrationContextHolder.setWorkflowIntegrationParams(workflowIntegrationParams);
            String processDefId = itemDataDto.getProcessDefId();
            ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefId(processDefId);
            // 设置表单映射字段
            setMappingFieldValues(itemDataDto, parser, itemDataDto.getDyFormData());
            // 提交事项
            return submit(itemDataDto);
        } finally {
            BusinessIntegrationContextHolder.removeWorkflowIntegrationParams();
        }
    }

    /**
     * 从流程单据提交业务事项实例数据
     *
     * @param flowInstUuid
     * @param itemDataDto
     * @return
     */
    @Override
    @Transactional
    public SubmitResult startFromWorkflow(String flowInstUuid, BizWorkflowProcessItemDataDto itemDataDto) {
        WorkflowIntegrationParams workflowIntegrationParams = new WorkflowIntegrationParams();
        workflowIntegrationParams.setFlowInstUuid(flowInstUuid);
        workflowIntegrationParams.setWorkData(itemDataDto.getWorkData());
        workflowIntegrationParams.setExtraParams(itemDataDto.getExtraParams());
        if (itemDataDto.getDyFormData() == null && itemDataDto.getWorkData() != null) {
            itemDataDto.setDyFormData(itemDataDto.getWorkData().getDyFormData());
        }
        try {
            BusinessIntegrationContextHolder.setWorkflowIntegrationParams(workflowIntegrationParams);
            String itemInstUuid = submit(itemDataDto);
            SubmitResult submitResult = workflowIntegrationParams.getSubmitResult();
            if (submitResult != null) {
                submitResult.setData(itemInstUuid);
            }
            return submitResult;
        } finally {
            BusinessIntegrationContextHolder.removeWorkflowIntegrationParams();
        }
    }

    // 提交事项
    private void submitItem(BizProcessItemDataDto itemDataDto, String belongItemInstUuid) {
        String itemInstUuid = itemDataDto.getItemInstUuid();
        BizProcessItemInstanceEntity itemInstanceEntity = bizProcessItemInstanceService.getOne(itemInstUuid);
        String oldState = itemInstanceEntity.getState();
        if (StringUtils.isBlank(itemInstanceEntity.getBelongItemInstUuid())) {
            itemInstanceEntity.setBelongItemInstUuid(belongItemInstUuid);
        }
        // 运行状态
        itemInstanceEntity.setState(EnumBizProcessItemState.Running.getValue());
        boolean itemStarted = false;
        // 开始时间
        if (itemInstanceEntity.getStartTime() == null) {
            itemStarted = true;
            itemInstanceEntity.setStartTime(Calendar.getInstance().getTime());
        }

        // 启动业务流程
        bizProcessInstanceService.startByUuid(itemInstanceEntity.getProcessInstUuid());

        // 启动过程节点
        bizProcessNodeInstanceService.startByUuid(itemInstanceEntity.getProcessNodeInstUuid());

        bizProcessItemInstanceService.save(itemInstanceEntity);

        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(itemInstanceEntity.getProcessDefUuid());
        // 发布业务事项开始事件监听
        if (itemStarted) {
            eventListenerPublisher.publishItemStarted(itemInstanceEntity, parser.getProcessItemConfigById(itemInstanceEntity.getItemId()));
        }

        // 发布状态变更
        if (!StringUtils.equals(EnumBizProcessItemState.Running.getValue(), oldState)) {
            stateChangedPublisher.publishItemStateChanged(EnumBizProcessItemState.Running, EnumBizProcessItemState.getByValue(oldState), itemInstanceEntity);
        }

        // 记录日志
        bizProcessItemOperationService.log(itemInstanceEntity, EnumOperateType.Submit);

        // 发起业务集成
        bizBusinessIntegrationFacadeService.startBusinessIntegration(itemInstanceEntity, itemDataDto.getDyFormData(),
                parser, itemDataDto.getExtraParams());

        // 分发事项
        // dispenseItemIfRequired(itemInstanceEntity, itemDataDto.getDyFormData(), parser);
    }

//    /**
//     * 分发事项
//     *
//     * @param itemInstanceEntity
//     * @param parser
//     */
//    private void dispenseItemIfRequired(BizProcessItemInstanceEntity itemInstanceEntity, DyFormData dyFormData, ProcessDefinitionJsonParser parser) {
//        if (!isDispenseItem(itemInstanceEntity)) {
//            return;
//        }
//        String itemDefId = itemInstanceEntity.getItemDefId();
//        String itemCode = itemInstanceEntity.getItemCode();
//        List<ItemIncludeItem> includeItems = bizItemDefinitionService.listIncludeItemDataByItemCode(itemDefId, itemCode);
//        if (CollectionUtils.isNotEmpty(includeItems)) {
//            // 串联事项
//            if (EnumItemType.Series.getValue().equalsIgnoreCase(itemInstanceEntity.getItemType())) {
//                dispenseSeriesItem(includeItems, itemInstanceEntity, dyFormData, parser);
//            } else if (EnumItemType.Parallel.getValue().equalsIgnoreCase(itemInstanceEntity.getItemType())) {
//                dispenseParallelItem(includeItems, itemInstanceEntity, dyFormData, parser);
//            }
//        }
//    }

//    /**
//     * 分发串联事项
//     *
//     * @param includeItems
//     * @param itemInstanceEntity
//     * @param parser
//     */
//    private void dispenseSeriesItem(List<ItemIncludeItem> includeItems, BizProcessItemInstanceEntity itemInstanceEntity,
//                                    DyFormData dyFormData, ProcessDefinitionJsonParser parser) {
//        String itemId = itemInstanceEntity.getItemId();
//        ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(itemId);
//
//        List<BizProcessItemInstanceDispenseEntity> dispenseEntities = bizProcessItemInstanceDispenseService.saveAllWithOrder(itemInstanceEntity.getUuid(), includeItems);
//        BizProcessItemInstanceDispenseEntity dispenseEntity = dispenseEntities.get(0);
//
//        // 分发处理
//        String itemInstUuid = dispenseItem(itemInstanceEntity, dyFormData, processItemConfig, dispenseEntity);
//
//        // 更新事项分发信息
//        dispenseEntity.setItemInstUuid(itemInstUuid);
//        dispenseEntity.setCompletionState(EnumBizProcessItemDispenseState.Complete.getValue());
//        bizProcessItemInstanceDispenseService.save(dispenseEntity);
//    }

//    /**
//     * 分发串联事项的下一事项
//     *
//     * @param parentItemInstanceEntity
//     * @param nextDispenseEntity
//     * @param parser
//     */
//    private void dispenseNextItemOfSeriesItem(BizProcessItemInstanceEntity parentItemInstanceEntity,
//                                              BizProcessItemInstanceDispenseEntity nextDispenseEntity,
//                                              ProcessDefinitionJsonParser parser) {
//        String formUuid = parentItemInstanceEntity.getFormUuid();
//        String dataUuid = parentItemInstanceEntity.getDataUuid();
//        String itemId = parentItemInstanceEntity.getItemId();
//        DyFormData dyFormData = dyFormFacade.getDyFormData(formUuid, dataUuid);
//        ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(itemId);
//        String nextItemInstUuid = dispenseItem(parentItemInstanceEntity, dyFormData, processItemConfig, nextDispenseEntity);
//
//        // 更新事项分发信息
//        nextDispenseEntity.setItemInstUuid(nextItemInstUuid);
//        nextDispenseEntity.setCompletionState(EnumBizProcessItemDispenseState.Complete.getValue());
//        bizProcessItemInstanceDispenseService.save(nextDispenseEntity);
//    }

//    private String dispenseItem(BizProcessItemInstanceEntity parentItemInstanceEntity, DyFormData parentDyFormData, ProcessItemConfig processItemConfig, BizProcessItemInstanceDispenseEntity dispenseEntity) {
//        // 事项分发表单配置
//        ProcessItemConfig.DispenseFormConfig dispenseFormConfig = processItemConfig.getDispenseFormConfigByItemCode(dispenseEntity.getItemCode());
//
//        String dispenseFormType = ProcessItemConfig.DispenseFormConfig.TYPE_USE_PARENT_ITEM_FORM;
//        if (dispenseFormConfig != null) {
//            dispenseFormType = dispenseFormConfig.getType();
//        }
//
//        DyFormData newDyformData = null;
//        // 使用主表单
//        if (StringUtils.equals(ProcessItemConfig.DispenseFormConfig.TYPE_USE_PARENT_ITEM_FORM, dispenseFormType)) {
//            newDyformData = parentDyFormData;
//        } else {
//            // 使用单据转换
//            String botId = dispenseFormConfig.getBotId();
//            newDyformData = copyDyFormDataWithBotId(botId, parentDyFormData);
//        }
//        bizProcessItemInstanceService.getDao().getSession().flush();
//        bizProcessItemInstanceService.getDao().getSession().clear();
//
//        String itemInstUuid = bizProcessItemInstanceService.saveSubItemAsDraft(parentItemInstanceEntity, dispenseEntity, newDyformData, dispenseFormConfig);
//
//        // 提交事项
//        BizProcessItemDataDto bizProcessItemDataDto = new BizProcessItemDataDto();
//        bizProcessItemDataDto.setItemInstUuid(itemInstUuid);
//        bizProcessItemDataDto.setDyFormData(newDyformData);
//        submitItem(bizProcessItemDataDto);
//
//        return itemInstUuid;
//    }
//    private DyFormData copyDyFormDataWithBotId(String botRuleId, DyFormData dyFormData) {
//        Set<BotParam.BotFromParam> froms = new HashSet<BotParam.BotFromParam>();
//        BotParam.BotFromParam botFromParam = new BotParam.BotFromParam(dyFormData.getDataUuid(), dyFormData.getFormUuid(), dyFormData);
//        froms.add(botFromParam);
//        BotParam botParam = new BotParam(botRuleId, froms);
//        botParam.setFroms(froms);
//        BotResult botResult = null;
//        try {
//            botResult = botFacadeService.startBot(botParam);
//        } catch (Exception e) {
//            logger.error("业务事项分发时单据转换出错！", e);
//            throw new BusinessException("业务事项分发时单据转换出错！", e);
//        }
//        Object data = botResult.getData();
//        String newFormUuid = null;
//        String newDataUuid = null;
//        if (data instanceof Map) {
//            Map<String, Object> formData = (Map<String, Object>) data;
//            newFormUuid = (String) formData.get("form_uuid");
//            newDataUuid = botResult.getDataUuid();
//            if (StringUtils.isBlank(newFormUuid)) {
//                throw new WorkFlowException("业务流程使用的单据转换规则[" + botRuleId + "]配置异常，没有勾选“保存单据”，无法分发，请联系管理员修改");
//            }
//        } else {
//            throw new BusinessException("业务事项分发失败，无法取到业务事项数据单据转换后的数据！");
//        }
//
//        return dyFormFacade.getDyFormData(newFormUuid, newDataUuid);
//    }

    /**
     * 分发并联事项
     *
     * @param includeItems
     * @param itemInstanceEntity
     * @param parser
     */
//    private void dispenseParallelItem(List<ItemIncludeItem> includeItems, BizProcessItemInstanceEntity itemInstanceEntity,
//                                      DyFormData dyFormData, ProcessDefinitionJsonParser parser) {
//        String itemId = itemInstanceEntity.getItemId();
//        ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(itemId);
//        List<BizProcessItemInstanceDispenseEntity> dispenseEntities = getDispenseEntities(itemInstanceEntity.getUuid(), includeItems);
//
//        // 分发处理
//        for (BizProcessItemInstanceDispenseEntity dispenseEntity : dispenseEntities) {
//            dispenseItem(itemInstanceEntity, dyFormData, processItemConfig, dispenseEntity);
//        }
//
//        // 保存分发信息
//        bizProcessItemInstanceDispenseService.saveAll(dispenseEntities);
//    }

//    /**
//     * 分发所有
//     *
//     * @param parentItemInstUuid
//     * @param includeItems
//     * @return
//     */
//    public List<BizProcessItemInstanceDispenseEntity> getDispenseEntities(String parentItemInstUuid, List<ItemIncludeItem> includeItems) {
//        List<BizProcessItemInstanceDispenseEntity> entities = Lists.newArrayList();
//        for (ItemIncludeItem includeItem : includeItems) {
//            BizProcessItemInstanceDispenseEntity entity = new BizProcessItemInstanceDispenseEntity();
//            entity.setParentItemInstUuid(parentItemInstUuid);
//            entity.setItemName(includeItem.getItemName());
//            entity.setItemCode(includeItem.getItemCode());
//            entity.setCompletionState(EnumBizProcessItemDispenseState.Complete.getValue());
//            entities.add(entity);
//        }
//        return entities;
//    }

    /**
     * 是否分发事项
     *
     * @param itemInstanceEntity
     * @return
    //     */
//    private boolean isDispenseItem(BizProcessItemInstanceEntity itemInstanceEntity) {
//        if (EnumItemType.Single.getValue().equalsIgnoreCase(itemInstanceEntity.getItemType())) {
//            return false;
//        }
//        return BooleanUtils.isTrue(itemInstanceEntity.getDispenseItem()) && !isItemDispensed(itemInstanceEntity.getUuid());
//    }

    /**
     * 事项是否已经分发
     *
     * @param itemInstUuid
     * @return
     */
//    private boolean isItemDispensed(String itemInstUuid) {
//        return bizProcessItemInstanceDispenseService.countByParentInstUuid(itemInstUuid) > 0;
//    }

    /**
     * 获取业务流程实例
     *
     * @param processInstUuid
     * @param entityId
     * @param parser
     * @return
     */
    private BizProcessInstanceEntity getProcessInstance(String processInstUuid, String entityId, ProcessDefinitionJsonParser parser) {
        if (StringUtils.isNotBlank(processInstUuid)) {
            return bizProcessInstanceService.getOne(processInstUuid);
        }
        String processDefId = parser.getProcessDefId();
        BizProcessInstanceEntity processInstanceEntity = bizProcessInstanceService.getByIdAndEntityId(processDefId, entityId);
        return processInstanceEntity;
    }

    /**
     * 获取过程节点实例
     *
     * @param itemInstanceEntity
     * @param parser
     * @return
     */
    private BizProcessNodeInstanceEntity getProcessNodeInstance(BizProcessItemInstanceEntity itemInstanceEntity, ProcessNodeConfig processNodeConfig, ProcessDefinitionJsonParser parser) {
        String processNodeInstUuid = itemInstanceEntity.getProcessNodeInstUuid();
        if (StringUtils.isNotBlank(processNodeInstUuid)) {
            return bizProcessNodeInstanceService.getOne(processNodeInstUuid);
        }
        String processNodeId = processNodeConfig.getId();
        String entityId = itemInstanceEntity.getEntityId();
        BizProcessNodeInstanceEntity nodeInstanceEntity = bizProcessNodeInstanceService.getByIdAndEntityId(processNodeId, entityId);
        return nodeInstanceEntity;
    }

    // 提交事项
    private void submitItem(BizProcessItemDataDto itemDataDto) {
        this.submitItem(itemDataDto, null);
    }

    // 拆分并提交事项
    private void splitItemAndSubmit(BizProcessItemDataDto itemDataDto) {
        String processDefId = itemDataDto.getProcessDefId();
        List<String> itemIds = Arrays.asList(StringUtils.split(itemDataDto.getItemId(), Separator.SEMICOLON.getValue()));
        String firstItemId = itemIds.get(0);
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefId(processDefId);
        ProcessItemConfig sourceItemConfig = parser.getProcessItemConfigById(firstItemId);
        for (String itemId : itemIds) {
            BizProcessItemDataDto itemData = splitItemData(processDefId, itemId, parser, sourceItemConfig, itemDataDto.getDyFormData());
            submitItem(itemData, itemDataDto.getItemInstUuid());
        }
        // 事项办件标记完成
        bizProcessItemInstanceService.completeByUuid(itemDataDto.getItemInstUuid());
    }

    /**
     * 拆分事项数据
     *
     * @param processDefId
     * @param itemId
     * @param dyFormData
     * @return
     */
    private BizProcessItemDataDto splitItemData(String processDefId, String itemId, ProcessDefinitionJsonParser parser, ProcessItemConfig sourceItemConfig, DyFormData dyFormData) {
        ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(itemId);
        ProcessItemConfig.ProcessItemFormConfig formConfig = processItemConfig.getFormConfig();
        String materialSubformId = formConfig.getMaterialSubformId();
        String materialCodeField = formConfig.getMaterialCodeField();
        String materialFileField = formConfig.getMaterialFileField();

        String itemName = processItemConfig.getItemName();
        String itemCode = processItemConfig.getItemCode();

        BizProcessItemDataDto itemDataDto = new BizProcessItemDataDto();
        itemDataDto.setProcessDefId(processDefId);
        itemDataDto.setItemId(itemId);
        itemDataDto.setItemName(itemName);
        itemDataDto.setItemCode(itemCode);

        // 使用定义单
        String formUuid = processItemConfig.getFormConfig().getFormUuid();
        String applyFormUuid = dyFormData.getFormUuid();
        DyFormData newDyformData = null;
        // 使用单据相同，有材料时复制数据
        if (StringUtils.equals(formUuid, applyFormUuid)) {
            // 材料分发
            if (StringUtils.isNotBlank(materialSubformId)) {
                String dataUuid = dyFormFacade.copyFormData(dyFormData, formUuid);
                newDyformData = dyFormFacade.getDyFormData(formUuid, dataUuid);
                // 事项信息
                setMappingItemField(itemDataDto, processItemConfig, parser, newDyformData);
                // 材料从表数据
                List<DyFormData> materialFormDatas = newDyformData.getDyformDatasByFormId(materialSubformId);
                Set<String> itemMaterialCodes = getItemMaterialCodes(itemCode, processItemConfig);
                List<String> deleteMaterialUuids = Lists.newArrayList();
                for (DyFormData materialFormData : materialFormDatas) {
                    String materialCode = Objects.toString(materialFormData.getFieldValue(materialCodeField), StringUtils.EMPTY);
                    if (!itemMaterialCodes.contains(materialCode)) {
                        deleteMaterialUuids.add(materialFormData.getDataUuid());
                    }
                }
                // 删除材料不一致的材料从表
                String materialSubformUuid = newDyformData.getFormUuidByFormId(materialSubformId);
                newDyformData.deleteFormData(materialSubformUuid, deleteMaterialUuids);
            } else {
                newDyformData = dyFormData;
            }
//        } else if (StringUtils.isNotBlank(formConfig.getBotId())) {
//            // 使用单据转换
//            String botId = formConfig.getBotId();
//            newDyformData = copyDyFormDataWithBotId(botId, dyFormData);
        } else {
            newDyformData = dyFormFacade.createDyformData(applyFormUuid);
            // 复制主表相同字段的数据
            copyMainFormOfSameField(dyFormData, newDyformData);
            setMappingItemField(itemDataDto, processItemConfig, parser, newDyformData);
            // 材料从表数据
            if (StringUtils.isNotBlank(materialSubformId)) {
                List<DyFormData> materialFormDatas = newDyformData.getDyformDatasByFormId(materialSubformId);
                for (DyFormData materialFormData : materialFormDatas) {
                    String materialCode = Objects.toString(dyFormData.getFieldValue(materialCodeField), StringUtils.EMPTY);
                    Object materialFile = getMaterialFileByMaterialCode(materialCode, sourceItemConfig, dyFormData);
                    if (materialFile != null) {
                        materialFormData.setFieldValue(materialFileField, materialFile);
                    }
                }
            }
        }

        bizProcessItemInstanceService.getDao().getSession().flush();
        bizProcessItemInstanceService.getDao().getSession().clear();

        itemDataDto.setDyFormData(newDyformData);
        String itemInstUuid = bizProcessItemInstanceService.saveAsDraft(itemDataDto, newDyformData);
        itemDataDto.setItemInstUuid(itemInstUuid);
        return itemDataDto;
    }

    /**
     * 根据材料编码获取材料从表对应行的材料附件
     *
     * @param materialCode
     * @param sourceItemConfig
     * @param dyFormData
     * @return
     */
    private Object getMaterialFileByMaterialCode(String materialCode, ProcessItemConfig sourceItemConfig, DyFormData dyFormData) {
        ProcessItemConfig.ProcessItemFormConfig souceFormConfig = sourceItemConfig.getFormConfig();
        String materialCodeField = souceFormConfig.getMaterialCodeField();
        String materialFileField = souceFormConfig.getMaterialFileField();
        String materialFormId = souceFormConfig.getMaterialSubformId();
        List<DyFormData> materialFormDatas = dyFormData.getDyformDatasByFormId(materialFormId);
        if (CollectionUtils.isNotEmpty(materialFormDatas)) {
            for (DyFormData materialFormData : materialFormDatas) {
                String code = Objects.toString(materialFormData.getFieldValue(materialCodeField));
                if (StringUtils.equals(materialCode, code)) {
                    return materialFormData.getFieldValue(materialFileField);
                }
            }
        }
        return null;
    }

    /**
     * 复制主表相同字段的数据
     *
     * @param dyFormData
     * @param newDyformData
     */
    private void copyMainFormOfSameField(DyFormData dyFormData, DyFormData newDyformData) {
        String formUuid = dyFormData.getFormUuid();
        DyFormFormDefinition dyFormFormDefinition = dyFormFacade.getFormDefinition(formUuid);
        List<DyformFieldDefinition> dyformFieldDefinitions = dyFormFormDefinition.doGetFieldDefintions();
        for (DyformFieldDefinition dyformFieldDefinition : dyformFieldDefinitions) {
            String fieldName = dyformFieldDefinition.getFieldName();
            if (newDyformData.isFieldExist(fieldName)) {
                newDyformData.setFieldValue(fieldName, dyFormData.getFieldValue(fieldName));
            }
        }
    }

    /**
     * 获取事项材料列表
     *
     * @param itemCode
     * @param processItemConfig
     * @return
     */
    private Set<String> getItemMaterialCodes(String itemCode, ProcessItemConfig processItemConfig) {
        String itemDefId = processItemConfig.getItemDefId();
        // 事项材料
        List<ItemMaterial> itemMaterials = bizItemDefinitionService.listMaterialDataByItemCode(itemDefId, itemCode);
        Set<String> codeSet = Sets.newHashSet();
        for (ItemMaterial itemMaterial : itemMaterials) {
            codeSet.add(itemMaterial.getMaterialCode());
        }
        return codeSet;
    }

    /**
     * 是否草稿
     *
     * @param processItemDataDto
     * @return
     */
    private boolean isDraft(BizProcessItemDataDto processItemDataDto) {
        return EnumBizProcessItemState.Created.getValue().equals(processItemDataDto.getState());
    }

    /**
     * 是否单个事项
     *
     * @param itemDataDto
     * @return
     */
    private boolean isSingleItem(BizProcessItemDataDto itemDataDto) {
        return StringUtils.indexOf(itemDataDto.getItemId(), Separator.SEMICOLON.getValue()) == -1;
    }

    /**
     * 启动计时器
     *
     * @param itemDataDto
     * @return
     */
    @Override
    @Transactional
    public String startTimer(BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = itemDataDto.getItemInstUuid();
        BizProcessItemInstanceEntity itemInstanceEntity = bizProcessItemInstanceService.getOne(itemInstUuid);
        String timerUuid = itemInstanceEntity.getTimerUuid();
        if (StringUtils.isNotBlank(timerUuid)) {
            throw new BusinessException("已经存在计时器，不能启动计时！");
        }
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(itemInstanceEntity.getProcessDefUuid());

        DyFormData dyFormData = itemDataDto.getDyFormData();
        Integer timeLimit = getTimeLimit(itemInstanceEntity.getItemId(), dyFormData, parser);
        Integer timeLimitType = itemInstanceEntity.getTimeLimitType();
        // 保存表单数据
        dyFormFacade.saveFormData(dyFormData);

        // 获取计时器配置，不存在时创建
        TsTimerConfigDto timerConfigDto = getOrCreateTimerConfig(timeLimitType);

        // 启动计时器
        TsTimerParamBuilder builder = new TsTimerParamBuilder();
        TsTimerParam timerParam = builder.setTimeLimit(Double.valueOf(timeLimit))
                .setTimerConfigUuid(timerConfigDto.getUuid())
                .setListener(BizProcessItemTimerListener.LISTENER_BEAN_NAME).build();
        TsTimerDto tsTimerDto = timerFacadeService.startTimer(timerParam);

        // 更新计时数据
        bizProcessItemInstanceService.updateTimerData(itemInstanceEntity, tsTimerDto);

        // 发布事项开始计时事件监听
        eventListenerPublisher.publishItemTimerStarted(itemInstanceEntity, parser.getProcessItemConfigById(itemInstanceEntity.getItemId()));

        // 记录日志
        bizProcessItemOperationService.log(itemInstanceEntity, EnumOperateType.StartTimer);
        return itemInstUuid;
    }

    /**
     * 获取计时器配置，不存在时创建
     *
     * @param timeLimitType
     * @return
     */
    private TsTimerConfigDto getOrCreateTimerConfig(Integer timeLimitType) {
        String timerConfigId = "BIZ_TIMER_" + timeLimitType;
        TsTimerConfigDto timerConfigDto = timerConfigFacadeService.getDtoById(timerConfigId);
        if (StringUtils.isBlank(timerConfigDto.getUuid())) {
            timerConfigDto.setName("业务流程管理_计时器配置_" + timeLimitType);
            timerConfigDto.setId(timerConfigId);
            // 工作日
            if (EnumBizTimeLimitType.WorkingDay.getValue().equals(timeLimitType)) {
                timerConfigDto.setTimingMode(EnumTimingMode.WORKING_DAY.getValue());
                timerConfigDto.setTimingModeType(EnumTimingModeType.WORKING_DAY.getValue());
                timerConfigDto.setTimingModeUnit(EnumTimingModeUnit.DAY.getValue());
            } else {
                // 自然日
                timerConfigDto.setTimingMode(EnumTimingMode.DAY.getValue());
                timerConfigDto.setTimingModeType(EnumTimingModeType.DAY.getValue());
                timerConfigDto.setTimingModeUnit(EnumTimingModeUnit.DAY.getValue());
            }
            timerConfigDto.setTimeLimitType(EnumTimeLimitType.CUSTOM_NUMBER.getValue());
            timerConfigDto.setTimeLimitUnit(EnumTimeLimitUnit.Day.getValue());
            timerConfigDto.setIncludeStartTimePoint(false);
            timerConfigDto.setAutoDelay(true);
            String timerConfigUuid = timerConfigFacadeService.saveDto(timerConfigDto);
            timerConfigDto = timerConfigFacadeService.getDto(timerConfigUuid);
        }
        return timerConfigDto;
    }

    /**
     * 获取办理时限
     *
     * @param itemId
     * @param dyFormData
     * @param parser
     * @return
     */
    private Integer getTimeLimit(String itemId, DyFormData dyFormData, ProcessDefinitionJsonParser parser) {
        ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(itemId);
        String timeLimitField = processItemConfig.getFormConfig().getTimeLimitField();
        if (StringUtils.isBlank(timeLimitField)) {
            throw new BusinessException("事项[" + processItemConfig.getItemName() + "]的办理单时限字段没有配置！");
        }
        String timeLimitString = Objects.toString(dyFormData.getFieldValue(timeLimitField), StringUtils.EMPTY);
        Integer timeLimit = null;
        try {
            timeLimit = Integer.valueOf(timeLimitString);
        } catch (Exception e) {
        }
        if (timeLimit == null) {
            throw new BusinessException("请输入有效的办理时限！");
        }
        return timeLimit;
    }

    /**
     * 暂停计时器
     *
     * @param itemDataDto
     * @return
     */
    @Override
    @Transactional
    public String pauseTimer(BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = itemDataDto.getItemInstUuid();
        BizProcessItemInstanceEntity itemInstanceEntity = bizProcessItemInstanceService.getOne(itemInstUuid);

        // 保存表单数据
        DyFormData dyFormData = itemDataDto.getDyFormData();
        if (dyFormData != null) {
            dyFormFacade.saveFormData(dyFormData);
        }

        // 暂停计时器
        String timerUuid = itemInstanceEntity.getTimerUuid();
        timerFacadeService.pauseTimer(timerUuid);

        // 更新计时数据
        TsTimerDto tsTimerDto = timerFacadeService.getTimer(timerUuid);
        bizProcessItemInstanceService.updateTimerData(itemInstanceEntity, tsTimerDto);

        // 发布事项暂停计时事件监听
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(itemInstanceEntity.getProcessDefUuid());
        eventListenerPublisher.publishItemTimerPaused(itemInstanceEntity, parser.getProcessItemConfigById(itemInstanceEntity.getItemId()));

        // 记录日志
        bizProcessItemOperationService.log(itemInstanceEntity, EnumOperateType.PauseTimer);
        return itemInstUuid;
    }

    /**
     * 恢复计时器
     *
     * @param itemDataDto
     * @return
     */
    @Override
    @Transactional
    public String resumeTimer(BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = itemDataDto.getItemInstUuid();
        BizProcessItemInstanceEntity itemInstanceEntity = bizProcessItemInstanceService.getOne(itemInstUuid);

        // 保存表单数据
        DyFormData dyFormData = itemDataDto.getDyFormData();
        if (dyFormData != null) {
            dyFormFacade.saveFormData(dyFormData);
        }

        // 恢复计时器
        String timerUuid = itemInstanceEntity.getTimerUuid();
        timerFacadeService.resumeTimer(timerUuid);

        // 更新计时数据
        TsTimerDto tsTimerDto = timerFacadeService.getTimer(timerUuid);
        bizProcessItemInstanceService.updateTimerData(itemInstanceEntity, tsTimerDto);

        // 发布事项恢复计时事件监听
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(itemInstanceEntity.getProcessDefUuid());
        eventListenerPublisher.publishItemTimerResumed(itemInstanceEntity, parser.getProcessItemConfigById(itemInstanceEntity.getItemId()));

        // 记录日志
        bizProcessItemOperationService.log(itemInstanceEntity, EnumOperateType.ResumeTimer);
        return itemInstUuid;
    }

    /**
     * 挂起业务事项实例
     *
     * @param itemDataDto
     * @return
     */
    @Override
    public String suspend(BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = itemDataDto.getItemInstUuid();

        // 保存表单数据
        DyFormData dyFormData = itemDataDto.getDyFormData();
        if (dyFormData != null) {
            dyFormFacade.saveFormData(dyFormData);
        }

        // 挂起事项
        BizProcessItemInstanceEntity itemInstanceEntity = bizProcessItemInstanceService.getOne(itemInstUuid);

        String oldState = itemInstanceEntity.getState();
        // 挂起状态
        itemInstanceEntity.setState(EnumBizProcessItemState.Suspended.getValue());

        // 暂停计时器
        String timerUuid = itemInstanceEntity.getTimerUuid();
        if (StringUtils.isNotBlank(timerUuid)) {
            timerFacadeService.pauseTimer(timerUuid);
            // 更新计时数据
            TsTimerDto tsTimerDto = timerFacadeService.getTimer(timerUuid);
            bizProcessItemInstanceService.updateTimerData(itemInstanceEntity, tsTimerDto);
        } else {
            bizProcessItemInstanceService.save(itemInstanceEntity);
        }

        // 发布事项挂起事件监听
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(itemInstanceEntity.getProcessDefUuid());
        eventListenerPublisher.publishItemSuspended(itemInstanceEntity, parser.getProcessItemConfigById(itemInstanceEntity.getItemId()));

        // 发布状态变更
        if (!StringUtils.equals(EnumBizProcessItemState.Suspended.getValue(), oldState)) {
            stateChangedPublisher.publishItemStateChanged(EnumBizProcessItemState.Suspended, EnumBizProcessItemState.getByValue(oldState), itemInstanceEntity);
        }

        // 记录日志
        bizProcessItemOperationService.log(itemInstanceEntity, EnumOperateType.Suspend);

        return itemInstUuid;
    }

    /**
     * 恢复业务事项实例
     *
     * @param itemDataDto
     * @return
     */
    @Override
    public String resume(BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = itemDataDto.getItemInstUuid();

        // 保存表单数据
        DyFormData dyFormData = itemDataDto.getDyFormData();
        if (dyFormData != null) {
            dyFormFacade.saveFormData(dyFormData);
        }

        // 恢复事项
        BizProcessItemInstanceEntity itemInstanceEntity = bizProcessItemInstanceService.getOne(itemInstUuid);

        String oldState = itemInstanceEntity.getState();
        // 运行状态
        itemInstanceEntity.setState(EnumBizProcessItemState.Running.getValue());

        // 恢复计时器
        String timerUuid = itemInstanceEntity.getTimerUuid();
        if (StringUtils.isNotBlank(timerUuid)) {
            timerFacadeService.resumeTimer(timerUuid);
            // 更新计时数据
            TsTimerDto tsTimerDto = timerFacadeService.getTimer(timerUuid);
            bizProcessItemInstanceService.updateTimerData(itemInstanceEntity, tsTimerDto);
        } else {
            bizProcessItemInstanceService.save(itemInstanceEntity);
        }

        // 发布事项恢复事件监听
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(itemInstanceEntity.getProcessDefUuid());
        eventListenerPublisher.publishItemResumed(itemInstanceEntity, parser.getProcessItemConfigById(itemInstanceEntity.getItemId()));

        // 发布状态变更
        if (!StringUtils.equals(EnumBizProcessItemState.Running.getValue(), oldState)) {
            stateChangedPublisher.publishItemStateChanged(EnumBizProcessItemState.Running, EnumBizProcessItemState.getByValue(oldState), itemInstanceEntity);
        }

        // 记录日志
        bizProcessItemOperationService.log(itemInstanceEntity, EnumOperateType.Resume);

        return itemInstUuid;
    }

    /**
     * 撤销业务事项实例
     *
     * @param itemDataDto
     * @return
     */
    @Override
    @Transactional
    public String cancel(BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = itemDataDto.getItemInstUuid();

        // 保存表单数据
        DyFormData dyFormData = itemDataDto.getDyFormData();
        if (dyFormData != null) {
            dyFormFacade.saveFormData(dyFormData);
        }

        // 撤销事项实例
        cancelItemByUuid(itemInstUuid, itemDataDto.getExtraParams());

        return itemInstUuid;
    }

    /**
     * 根据流程数据，撤销业务事项实例
     *
     * @param workData
     * @return
     */
    @Override
    @Transactional
    public String cancelByWorkData(WorkBean workData) {
        String flowInstUuid = workData.getFlowInstUuid();
        // 获取流程集成信息
        BizBusinessIntegrationEntity integrationEntity = businessIntegrationService.getByTypeAndBizInstUuid(EnumBizBiType.Workflow.getValue(), flowInstUuid);
        if (integrationEntity == null) {
            throw new BusinessException("流程集成信息不存在，无法撤回");
        }

        // 保存表单数据
        DyFormData dyFormData = workData.getDyFormData();
        if (dyFormData != null) {
            dyFormFacade.saveFormData(dyFormData);
        }

        // 撤销事项实例
        String itemInstUuid = integrationEntity.getItemInstUuid();
        WorkflowIntegrationParams workflowIntegrationParams = new WorkflowIntegrationParams();
        workflowIntegrationParams.setFlowInstUuid(flowInstUuid);
        workflowIntegrationParams.setWorkData(workData);
        workflowIntegrationParams.setOpinionText(workData.getOpinionText());
        try {
            BusinessIntegrationContextHolder.setWorkflowIntegrationParams(workflowIntegrationParams);

            cancelItemByUuid(itemInstUuid, workData.getExtraParams());

            // 源事项实例撤回到上一状态
            BizNewItemRelationEntity newItemRelationEntity = newItemRelationService.getByTargetItemInstUuid(itemInstUuid);
            if (newItemRelationEntity != null) {
                restartByItemInstUuid(newItemRelationEntity.getSourceItemInstUuid(), newItemRelationEntity);
            }
        } finally {
            BusinessIntegrationContextHolder.removeWorkflowIntegrationParams();
        }

        return itemInstUuid;
    }

    /**
     * 根据流程数据，撤销发起的业务事项实例
     *
     * @param workData
     * @return
     */
    @Override
    @Transactional
    public String cancelOtherByWorkData(WorkBean workData) {
        String flowInstUuid = workData.getFlowInstUuid();
        // 获取流程集成信息
        BizBusinessIntegrationEntity integrationEntity = businessIntegrationService.getByTypeAndBizInstUuid(EnumBizBiType.Workflow.getValue(), flowInstUuid);
        if (integrationEntity == null) {
            throw new BusinessException("流程集成信息不存在，无法撤回");
        }

        // 保存表单数据
        DyFormData dyFormData = workData.getDyFormData();
        if (dyFormData != null) {
            dyFormFacade.saveFormData(dyFormData);
        }

        // 撤销发起的事项实例
        String sourceItemInstUuid = integrationEntity.getItemInstUuid();
        List<BizNewItemRelationEntity> newItemRelationEntities = newItemRelationService.listBySourceItemInstUuid(sourceItemInstUuid);

        WorkflowIntegrationParams workflowIntegrationParams = new WorkflowIntegrationParams();
        workflowIntegrationParams.setOpinionText(workData.getOpinionText());
        try {
            BusinessIntegrationContextHolder.setWorkflowIntegrationParams(workflowIntegrationParams);

            // 撤回办理的事项
            List<BizNewItemRelationEntity> canceled = Lists.newArrayList();
            for (BizNewItemRelationEntity bizNewItemRelationEntity : newItemRelationEntities) {
                BizProcessItemInstanceEntity itemInstanceEntity = bizProcessItemInstanceService.getOne(bizNewItemRelationEntity.getTargetItemInstUuid());
                if (EnumBizProcessItemState.Running.getValue().equals(itemInstanceEntity.getState()) ||
                        EnumBizProcessItemState.Suspended.getValue().equals(itemInstanceEntity.getState())) {
                    cancelItemByUuid(bizNewItemRelationEntity.getTargetItemInstUuid(), workData.getExtraParams());
                    canceled.add(bizNewItemRelationEntity);
                }
            }

            // 当前事项实例撤回到上一状态
            BizNewItemRelationEntity newItemRelationEntity = CollectionUtils.isNotEmpty(canceled) ? canceled.get(0) : null;
            restartByItemInstUuid(sourceItemInstUuid, newItemRelationEntity);
        } finally {
            BusinessIntegrationContextHolder.removeWorkflowIntegrationParams();
        }

        return sourceItemInstUuid;
    }

    /**
     * 撤销事项实例
     *
     * @param itemInstUuid
     */
    private void cancelItemByUuid(String itemInstUuid, Map<String, Object> extraParams) {
        BizProcessItemInstanceEntity itemInstanceEntity = bizProcessItemInstanceService.getOne(itemInstUuid);
        String oldState = itemInstanceEntity.getState();
        // 完成状态
        itemInstanceEntity.setState(EnumBizProcessItemState.Cancelled.getValue());
        // 结束时间
        itemInstanceEntity.setEndTime(Calendar.getInstance().getTime());

        // 停止计时器
        String timerUuid = itemInstanceEntity.getTimerUuid();
        if (StringUtils.isNotBlank(timerUuid)) {
            timerFacadeService.stopTimer(timerUuid);
            // 更新计时数据
            TsTimerDto tsTimerDto = timerFacadeService.getTimer(timerUuid);
            bizProcessItemInstanceService.updateTimerData(itemInstanceEntity, tsTimerDto);
        } else {
            bizProcessItemInstanceService.save(itemInstanceEntity);
        }

        // 发布事项撤销事件监听
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(itemInstanceEntity.getProcessDefUuid());
        eventListenerPublisher.publishItemCanceled(itemInstanceEntity, parser.getProcessItemConfigById(itemInstanceEntity.getItemId()));

        // 发布状态变更
        if (!StringUtils.equals(EnumBizProcessItemState.Cancelled.getValue(), oldState)) {
            stateChangedPublisher.publishItemStateChanged(EnumBizProcessItemState.Cancelled, EnumBizProcessItemState.getByValue(oldState), itemInstanceEntity);
        }

        // 记录日志
        bizProcessItemOperationService.log(itemInstanceEntity, EnumOperateType.Cancel);

        // 撤回业务集成
        bizBusinessIntegrationFacadeService.cancelBusinessIntegration(itemInstanceEntity, parser, extraParams);

        // 撤销子事项实例
        cancelChildrenItemIfRequired(itemInstanceEntity);
    }

    /**
     * 撤销子事项实例
     *
     * @param itemInstanceEntity
     */
    private void cancelChildrenItemIfRequired(BizProcessItemInstanceEntity itemInstanceEntity) {
        List<BizProcessItemInstanceEntity> itemInstanceEntities = bizProcessItemInstanceService.listByParentItemInstUuid(itemInstanceEntity.getUuid());
        for (BizProcessItemInstanceEntity entity : itemInstanceEntities) {
            cancelItemByUuid(entity.getUuid(), null);
        }
    }

    /**
     * 根据业务事项实例UUID，恢复业务事项实例
     *
     * @param itemInstUuid
     * @param newItemRelationEntity
     * @return
     */
    @Override
    @Transactional
    public void restartByItemInstUuid(String itemInstUuid, BizNewItemRelationEntity newItemRelationEntity) {
        BizProcessItemInstanceEntity itemInstanceEntity = bizProcessItemInstanceService.getOne(itemInstUuid);
        if (EnumBizProcessItemState.Completed.getValue().equals(itemInstanceEntity.getState())) {
            // 办理中状态
            itemInstanceEntity.setState(EnumBizProcessItemState.Running.getValue());
            // 结束时间为空
            itemInstanceEntity.setEndTime(null);

            String processNodeInstUuid = itemInstanceEntity.getProcessNodeInstUuid();
            String processInstUuid = itemInstanceEntity.getProcessInstUuid();
            if (StringUtils.isNotBlank(processNodeInstUuid)) {
                bizProcessNodeInstanceService.restartByUuid(processNodeInstUuid);
            }
            if (StringUtils.isNotBlank(processInstUuid)) {
                bizProcessInstanceService.restartByUuid(processInstUuid);
            }

            // 发布状态变更
            stateChangedPublisher.publishItemStateChanged(EnumBizProcessItemState.Running, EnumBizProcessItemState.Completed, itemInstanceEntity);
        }

        // 停止计时器
        String timerUuid = itemInstanceEntity.getTimerUuid();
        if (StringUtils.isNotBlank(timerUuid)) {
            timerFacadeService.restartTimer(timerUuid);
            // 更新计时数据
            TsTimerDto tsTimerDto = timerFacadeService.getTimer(timerUuid);
            bizProcessItemInstanceService.updateTimerData(itemInstanceEntity, tsTimerDto);
        } else {
            bizProcessItemInstanceService.save(itemInstanceEntity);
        }

        // 发布事项撤销事件监听
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(itemInstanceEntity.getProcessDefUuid());
        eventListenerPublisher.publishItemRestarted(itemInstanceEntity, parser.getProcessItemConfigById(itemInstanceEntity.getItemId()));

        // 记录日志
        bizProcessItemOperationService.log(itemInstanceEntity, EnumOperateType.Restart);

        // 重新开始业务集成
        if (newItemRelationEntity != null) {
            bizBusinessIntegrationFacadeService.restartBusinessIntegration(itemInstanceEntity, parser, newItemRelationEntity);
        }
    }

    /**
     * 完成业务事项实例
     *
     * @param itemDataDto
     * @return
     */
    @Override
    @Transactional
    public String complete(BizProcessItemDataDto itemDataDto) {
        String itemInstUuid = itemDataDto.getItemInstUuid();

        // 保存表单数据
        DyFormData dyFormData = itemDataDto.getDyFormData();
        if (dyFormData != null) {
            dyFormFacade.saveFormData(dyFormData);
        }

        // 完成事项
        completeItemByUuid(itemInstUuid);

        return itemInstUuid;
    }

    /**
     * 完成事项
     *
     * @param itemInstUuid
     */
    private void completeItemByUuid(String itemInstUuid) {
        BizProcessItemInstanceEntity itemInstanceEntity = bizProcessItemInstanceService.getOne(itemInstUuid);

        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefUuid(itemInstanceEntity.getProcessDefUuid());
        ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(itemInstanceEntity.getItemId());
        // 前置事项未完成，不可办结
        List<BizProcessItemInstanceEntity> frontItemInstances = listFrontItemInstance(itemInstanceEntity, processItemConfig);
        frontItemInstances = frontItemInstances.stream().filter(item -> item.getEndTime() == null).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(frontItemInstances)) {
            String frontItemNames = StringUtils.join(frontItemInstances.stream().map(item -> item.getItemName())
                    .collect(Collectors.toList()), Separator.SEMICOLON.getValue());
            throw new BusinessException(String.format("前置事项[%s]未完成，不可办结", frontItemNames));
        }

        String oldState = itemInstanceEntity.getState();
        // 完成状态
        itemInstanceEntity.setState(EnumBizProcessItemState.Completed.getValue());
        // 结束时间
        itemInstanceEntity.setEndTime(Calendar.getInstance().getTime());

        // 停止计时器
        String timerUuid = itemInstanceEntity.getTimerUuid();
        if (StringUtils.isNotBlank(timerUuid)) {
            timerFacadeService.stopTimer(timerUuid);
            // 更新计时数据
            TsTimerDto tsTimerDto = timerFacadeService.getTimer(timerUuid);
            bizProcessItemInstanceService.updateTimerData(itemInstanceEntity, tsTimerDto);
        } else {
            bizProcessItemInstanceService.save(itemInstanceEntity);
        }

        // 发布事项结束事件监听
        eventListenerPublisher.publishItemCompleted(itemInstanceEntity, processItemConfig);

        // 发布状态变更
        if (!StringUtils.equals(EnumBizProcessItemState.Completed.getValue(), oldState)) {
            stateChangedPublisher.publishItemStateChanged(EnumBizProcessItemState.Completed, EnumBizProcessItemState.getByValue(oldState), itemInstanceEntity);
        }

        // 记录日志
        bizProcessItemOperationService.log(itemInstanceEntity, EnumOperateType.Complete);

        // 串联事项进入下一事项或返回结束
        // completeSeriesItemIfRequired(itemInstanceEntity, parser);

        // 检查并完成过程节点
        String processNodeInstUuid = itemInstanceEntity.getProcessNodeInstUuid();
        String processInstUuid = itemInstanceEntity.getProcessInstUuid();
        ProcessNodeConfig processNodeConfig = parser.getProcessNodeConfigByItemId(itemInstanceEntity.getItemId());
        checkAndCompleProcessNode(processNodeInstUuid, processInstUuid, processNodeConfig, parser);
    }

    /**
     * 获取前置事项实例
     *
     * @param itemInstanceEntity
     * @param processItemConfig
     * @return
     */
    private List<BizProcessItemInstanceEntity> listFrontItemInstance(BizProcessItemInstanceEntity itemInstanceEntity,
                                                                     ProcessItemConfig processItemConfig) {
        String belongItemInstUuid = itemInstanceEntity.getBelongItemInstUuid();
        String parentItemInstUuid = itemInstanceEntity.getParentItemInstUuid();
        String frontItemCode = processItemConfig.getFrontItemCode();
        if ((StringUtils.isBlank(belongItemInstUuid) && StringUtils.isBlank(parentItemInstUuid))
                || StringUtils.isBlank(frontItemCode)) {
            return Collections.emptyList();
        }

        List<BizProcessItemInstanceEntity> itemInstanceEntities = null;
        if (StringUtils.isNotBlank(belongItemInstUuid)) {
            itemInstanceEntities = bizProcessItemInstanceService.listByBelongItemInstUuid(belongItemInstUuid);
        } else if (StringUtils.isNotBlank(parentItemInstUuid)) {
            itemInstanceEntities = bizProcessItemInstanceService.listByParentItemInstUuid(parentItemInstUuid);
        }
        List<String> frontItemCodes = Arrays.asList(StringUtils.split(frontItemCode, Separator.SEMICOLON.getValue()));
        return itemInstanceEntities.stream().filter(item -> frontItemCodes.contains(item.getItemCode())).collect(Collectors.toList());
    }

    /**
     * 串联事项进入下一事项或返回结束
     *
     * @param itemInstanceEntity
     */
//    private void completeSeriesItemIfRequired(BizProcessItemInstanceEntity itemInstanceEntity, ProcessDefinitionJsonParser parser) {
//        String itemId = itemInstanceEntity.getItemId();
//        if (!StringUtils.contains(itemId, Separator.SLASH.getValue())) {
//            return;
//        }
//        String itemInstUuid = itemInstanceEntity.getUuid();
//        BizProcessItemInstanceDispenseEntity dispenseEntity = bizProcessItemInstanceDispenseService.getByItemInstUuid(itemInstUuid);
//        if (dispenseEntity == null) {
//            return;
//        }
//
//        // 获取串联事项的下一事项
//        String parentItemInstUuid = itemInstanceEntity.getParentItemInstUuid();
//        Integer sortOrder = dispenseEntity.getSortOrder();
//        BizProcessItemInstanceDispenseEntity nextDispenseEntity = bizProcessItemInstanceDispenseService.getByParentInstUuidAndSortOrder(parentItemInstUuid, ++sortOrder);
//
//        // 分发串联事项的下一事项
//        BizProcessItemInstanceEntity parentItemInstanceEntity = bizProcessItemInstanceService.getOne(parentItemInstUuid);
//        if (nextDispenseEntity != null) {
//            dispenseNextItemOfSeriesItem(parentItemInstanceEntity, nextDispenseEntity, parser);
//        } else {
//            // 上级事项已完成
//            if (EnumBizProcessItemState.Completed.getValue().equals(parentItemInstanceEntity.getState())) {
//                logger.info("parent item instance [{}] is completed", parentItemInstUuid);
//            } else {
//                // 上级事项未完成，完成上级事项
//                completeItemByUuid(parentItemInstUuid);
//            }
//        }
//    }

    /**
     * 检查并完成过程节点
     *
     * @param processNodeInstUuid
     * @param processInstUuid
     * @param processNodeConfig
     * @param parser
     */
    private void checkAndCompleProcessNode(String processNodeInstUuid, String processInstUuid, ProcessNodeConfig processNodeConfig, ProcessDefinitionJsonParser parser) {
        // 阶段下的阶段、事项都办理完成，阶段标记完成
        List<String> itemIds = parser.getChildrenItemIdsByNodeId(processNodeConfig.getId());
        // 同级事项是否完成
        boolean siblingItemCompleted = CollectionUtils.isEmpty(itemIds) || bizProcessItemInstanceService.
                isCompleteByItemIdsAndProcessInstUuid(itemIds, processInstUuid);
        if (siblingItemCompleted) {
            List<String> siblingNodeIds = parser.getChildrenNodeIdsByNodeId(processNodeConfig.getId());
            // 同级过程节点是否完成
            boolean siblingNodeCompleted = CollectionUtils.isEmpty(siblingNodeIds) || bizProcessNodeInstanceService.
                    isCompleteByNodeIdsAndProcessInstUuid(siblingNodeIds, processInstUuid);
            // 阶段下的事项、过程节点都完成，标记为完成
            if (siblingNodeCompleted) {
                BizProcessNodeInstanceEntity processNodeInstanceEntity = bizProcessNodeInstanceService.completeByUuid(processNodeInstUuid);
                // 完成上级过程节点
                String parentNodeInstUuid = processNodeInstanceEntity.getParentNodeInstUuid();
                if (StringUtils.isNotBlank(parentNodeInstUuid)) {
                    String parentNodeId = bizProcessNodeInstanceService.getOne(parentNodeInstUuid).getId();
                    checkAndCompleProcessNode(parentNodeInstUuid, processInstUuid, parser.getProcessNodeConfigById(parentNodeId), parser);
                }
            }
            // 所有阶段都完成时，完成业务流程
            List<String> allNodeIds = parser.getAllNodeId();
            boolean allNodeCompleted = bizProcessNodeInstanceService.isCompleteByNodeIdsAndProcessInstUuid(allNodeIds, processInstUuid);
            if (allNodeCompleted) {
                bizProcessInstanceService.completeByUuid(processInstUuid);
            }
        }
    }

    /**
     * 根据业务流程定义ID、事项ID列表获取相应的事项办理状态
     *
     * @param processDefId
     * @param itemIds
     * @return
     */
    @Override
    public Map<String, String> listItemStatesByProcessDefIdAndItemIds(String processDefId, List<String> itemIds) {
        Map<String, String> stateMap = bizProcessItemInstanceService.listItemStatesByProcessDefIdAndItemIds(processDefId, itemIds);
        return stateMap;
    }

    /**
     * 根据业务事项实例UUID获取业务事项操作列表
     *
     * @param itemInstUuid
     * @return
     */
    @Override
    public List<BizProcessItemOperationDto> listProcessItemOperationByUuid(String itemInstUuid) {
        List<BizProcessItemOperationEntity> entities = bizProcessItemOperationService.listByItemInstUuid(itemInstUuid);
        List<BizProcessItemOperationDto> dtos = BeanUtils.copyCollection(entities, BizProcessItemOperationDto.class);
        dtos.stream().forEach(opt -> opt.setOperateName(EnumOperateType.getNameByValue(opt.getOperateType())));
        Collections.sort(dtos, IdEntityComparators.CREATE_TIME_ASC);
        return dtos;
    }

    /**
     * 根据事项ID、业务流程定义ID、JSON条件参数MAP，获取事项办理情形材料
     *
     * @param itemId
     * @param processDefId
     * @param values
     * @return
     */
    @Override
    public List<ItemMaterial> listItemSituationMaterial(String itemId, String processDefId, Map<String, Object> values) {
        List<String> itemIds = Lists.newArrayList(StringUtils.split(itemId, Separator.SEMICOLON.getValue()));
        Set<ItemMaterial> itemMaterials = Sets.newHashSet();
        for (String processItemId : itemIds) {
            itemMaterials.addAll(getItemMaterials(processItemId, processDefId, values));
        }
        return Lists.newArrayList(itemMaterials);
    }

    private List<ItemMaterial> getItemMaterials(String itemId, String processDefId, Map<String, Object> values) {
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefId(processDefId);
        ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(itemId);
        String itemDefId = processItemConfig.getItemDefId();
        String itemCode = processItemConfig.getItemCode();
        List<ItemMaterial> itemMaterials = bizItemDefinitionService.listMaterialDataByItemCode(itemDefId, itemCode);
        if (CollectionUtils.isEmpty(itemMaterials)) {
            return Collections.emptyList();
        }

        if (!processItemConfig.getEnabledSituation()) {
            return itemMaterials;
        }

        List<ProcessItemConfig.SituationConfig> situationConfigs = processItemConfig.getSituationConfigs();
        Set<String> matchMaterialCodeSet = Sets.newHashSet();
        for (ProcessItemConfig.SituationConfig situationConfig : situationConfigs) {
            boolean match = itemSituationConditionEvaluator.evaluate(situationConfig, values);
            if (match) {
                String itemMaterialCodes = situationConfig.getItemMaterialCodes();
                if (StringUtils.isNotBlank(itemMaterialCodes)) {
                    matchMaterialCodeSet.addAll(Arrays.asList(StringUtils.split(itemMaterialCodes, Separator.SEMICOLON.getValue())));
                }
            }
        }

        // 无匹配项返回所有
        if (CollectionUtils.isEmpty(matchMaterialCodeSet)) {
            return itemMaterials;
        }

        return itemMaterials.stream().filter(material -> matchMaterialCodeSet.contains(material.getMaterialCode()))
                .collect(Collectors.toList());
    }

    /**
     * 根据事项ID、业务流程定义ID、JSON条件参数MAP，获取事项办理情形时限
     *
     * @param itemId
     * @param processDefId
     * @param values
     * @return
     */
    @Override
    public List<ItemTimeLimit> listItemSituationTimeLimit(String itemId, String processDefId, Map<String, Object> values) {
        List<String> itemIds = Lists.newArrayList(StringUtils.split(itemId, Separator.SEMICOLON.getValue()));
        Set<ItemTimeLimit> itemMaterials = Sets.newHashSet();
        for (String processItemId : itemIds) {
            itemMaterials.addAll(getItemTimeLimits(processItemId, processDefId, values));
        }
        return Lists.newArrayList(itemMaterials);
    }

    private List<ItemTimeLimit> getItemTimeLimits(String itemId, String processDefId, Map<String, Object> values) {
        ProcessDefinitionJsonParser parser = ProcessDefinitionUtils.getJsonParserByProcessDefId(processDefId);
        ProcessItemConfig processItemConfig = parser.getProcessItemConfigById(itemId);
        String itemDefId = processItemConfig.getItemDefId();
        String itemCode = processItemConfig.getItemCode();
        List<ItemTimeLimit> itemTimeLimits = bizItemDefinitionService.listTimeLimitDataByItemCode(itemDefId, itemCode);
        if (CollectionUtils.isEmpty(itemTimeLimits)) {
            return Collections.emptyList();
        }

        if (!processItemConfig.getEnabledSituation()) {
            return itemTimeLimits;
        }

        List<ProcessItemConfig.SituationConfig> situationConfigs = processItemConfig.getSituationConfigs();
        Set<Integer> matchTimeLimitSet = Sets.newHashSet();
        for (ProcessItemConfig.SituationConfig situationConfig : situationConfigs) {
            boolean match = itemSituationConditionEvaluator.evaluate(situationConfig, values);
            if (match) {
                Integer itemWorkday = situationConfig.getItemWorkday();
                if (itemWorkday != null) {
                    matchTimeLimitSet.add(itemWorkday);
                }
            }
        }

        // 无匹配项返回所有
        if (CollectionUtils.isEmpty(matchTimeLimitSet)) {
            return itemTimeLimits;
        }

        return itemTimeLimits.stream().filter(timeLimit -> matchTimeLimitSet.contains(timeLimit.getTimeLimit()))
                .collect(Collectors.toList());
    }

}
