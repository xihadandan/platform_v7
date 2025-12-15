/*
 * @(#)10/9/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.workflow.support.FlowBusinessDefinitionJson;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
 * 10/9/22.1	zhulh		10/9/22		Create
 * </pre>
 * @date 10/9/22
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessItemConfig extends BaseObject {
    private static final long serialVersionUID = 2789156085213400472L;

    // 引用业务流程定义UUID
    private String refProcessDefUuid;
    // ID
    private String id;
    // 事项定义名称
    private String itemDefName;
    // 事项定义ID
    private String itemDefId;
    // 事项名称
    private String itemName;
    // 事项编码
    private String itemCode;
    // 事项类型
    private String itemType;
    // 前置事项
    private String frontItemCode;
    // 事项办理单
    private ProcessItemFormConfig formConfig;
    // 时限类型，1工作日，2自然日
    private int timeLimitType;
    // 事件监听
    private String listener;
    // 是否必办件
    private boolean mandatory;
    // 是否里程碑事项
    private boolean milestone;
    //    // 是否分发事项
//    private boolean dispenseItem;
//    // 表单分发配置
//    private List<DispenseFormConfig> dispenseFormConfigs = Lists.newArrayListWithCapacity(0);
    // 分发事项办理信息显示位置
//    private String dispenseItemPlaceHolder;
    // 是否启用办理情形
    private boolean enabledSituation;
    // 办理情形配置
    private List<SituationConfig> situationConfigs = Lists.newArrayListWithCapacity(0);
    // 事件定义
    private List<DefineEventConfig> defineEvents = Lists.newArrayListWithCapacity(0);
    // 事件发布
    private List<DefineEventPublishConfig> eventPublishConfigs = Lists.newArrayListWithCapacity(0);
    // 包含事项
    private List<ProcessItemConfig> includeItems = Lists.newArrayListWithCapacity(0);
    // 互斥事项
    private List<ItemMutexItemConfig> mutexItems = Lists.newArrayListWithCapacity(0);
    // 关联事项
    private List<ItemRelatedItemConfig> relatedItems = Lists.newArrayListWithCapacity(0);
    // 业务集成配置
    private List<BusinessIntegrationConfig> businessIntegrationConfigs = Lists.newArrayListWithCapacity(0);

    /**
     * @return the refProcessDefUuid
     */
    public String getRefProcessDefUuid() {
        return refProcessDefUuid;
    }

    /**
     * @param refProcessDefUuid 要设置的refProcessDefUuid
     */
    public void setRefProcessDefUuid(String refProcessDefUuid) {
        this.refProcessDefUuid = refProcessDefUuid;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the itemDefName
     */
    public String getItemDefName() {
        return itemDefName;
    }

    /**
     * @param itemDefName 要设置的itemDefName
     */
    public void setItemDefName(String itemDefName) {
        this.itemDefName = itemDefName;
    }

    /**
     * @return the itemDefId
     */
    public String getItemDefId() {
        return itemDefId;
    }

    /**
     * @param itemDefId 要设置的itemDefId
     */
    public void setItemDefId(String itemDefId) {
        this.itemDefId = itemDefId;
    }

    /**
     * @return the itemName
     */
    public String getItemName() {
        return itemName;
    }

    /**
     * @param itemName 要设置的itemName
     */
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * @return the itemCode
     */
    public String getItemCode() {
        return itemCode;
    }

    /**
     * @param itemCode 要设置的itemCode
     */
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    /**
     * @return the itemType
     */
    public String getItemType() {
        return itemType;
    }

    /**
     * @param itemType 要设置的itemType
     */
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    /**
     * @return the frontItemCode
     */
    public String getFrontItemCode() {
        return frontItemCode;
    }

    /**
     * @param frontItemCode 要设置的frontItemCode
     */
    public void setFrontItemCode(String frontItemCode) {
        this.frontItemCode = frontItemCode;
    }

    /**
     * @return the formConfig
     */
    public ProcessItemFormConfig getFormConfig() {
        return formConfig;
    }

    /**
     * @param formConfig 要设置的formConfig
     */
    public void setFormConfig(ProcessItemFormConfig formConfig) {
        this.formConfig = formConfig;
    }

    /**
     * @return the timeLimitType
     */
    public int getTimeLimitType() {
        return timeLimitType;
    }

    /**
     * @param timeLimitType 要设置的timeLimitType
     */
    public void setTimeLimitType(int timeLimitType) {
        this.timeLimitType = timeLimitType;
    }

    /**
     * @return the listener
     */
    public String getListener() {
        return listener;
    }

    /**
     * @param listener 要设置的listener
     */
    public void setListener(String listener) {
        this.listener = listener;
    }

    /**
     * @return the mandatory
     */
    public boolean getMandatory() {
        return mandatory;
    }

    /**
     * @param mandatory 要设置的mandatory
     */
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    /**
     * @return the milestone
     */
    public boolean getMilestone() {
        return milestone;
    }

    /**
     * @param milestone 要设置的milestone
     */
    public void setMilestone(boolean milestone) {
        this.milestone = milestone;
    }

//    /**
//     * @return the dispenseItem
//     */
//    public boolean getDispenseItem() {
//        return dispenseItem;
//    }
//
//    /**
//     * @param dispenseItem 要设置的dispenseItem
//     */
//    public void setDispenseItem(boolean dispenseItem) {
//        this.dispenseItem = dispenseItem;
//    }
//
//    /**
//     * @return the dispenseFormConfigs
//     */
//    public List<DispenseFormConfig> getDispenseFormConfigs() {
//        return dispenseFormConfigs;
//    }
//
//    /**
//     * @param dispenseFormConfigs 要设置的dispenseFormConfigs
//     */
//    public void setDispenseFormConfigs(List<DispenseFormConfig> dispenseFormConfigs) {
//        this.dispenseFormConfigs = dispenseFormConfigs;
//    }

//    /**
//     * @return the dispenseItemPlaceHolder
//     */
//    public String getDispenseItemPlaceHolder() {
//        return dispenseItemPlaceHolder;
//    }
//
//    /**
//     * @param dispenseItemPlaceHolder 要设置的dispenseItemPlaceHolder
//     */
//    public void setDispenseItemPlaceHolder(String dispenseItemPlaceHolder) {
//        this.dispenseItemPlaceHolder = dispenseItemPlaceHolder;
//    }

    /**
     * @return the enabledSituation
     */
    public boolean getEnabledSituation() {
        return enabledSituation;
    }

    /**
     * @param enabledSituation 要设置的enabledSituation
     */
    public void setEnabledSituation(boolean enabledSituation) {
        this.enabledSituation = enabledSituation;
    }

    /**
     * @return the situationConfigs
     */
    public List<SituationConfig> getSituationConfigs() {
        return situationConfigs;
    }

    /**
     * @param situationConfigs 要设置的situationConfigs
     */
    public void setSituationConfigs(List<SituationConfig> situationConfigs) {
        this.situationConfigs = situationConfigs;
    }

//    public DispenseFormConfig getDispenseFormConfigByItemCode(String itemCode) {
//        List<DispenseFormConfig> dispenseFormConfigs = this.getDispenseFormConfigs();
//        if (CollectionUtils.isEmpty(dispenseFormConfigs)) {
//            return null;
//        }
//        for (DispenseFormConfig dispenseFormConfig : dispenseFormConfigs) {
//            if (StringUtils.equals(dispenseFormConfig.getItemCode(), itemCode)) {
//                return dispenseFormConfig;
//            }
//        }
//        return null;
//    }

    /**
     * @return the defineEvents
     */
    public List<DefineEventConfig> getDefineEvents() {
        return defineEvents;
    }

    /**
     * @param defineEvents 要设置的defineEvents
     */
    public void setDefineEvents(List<DefineEventConfig> defineEvents) {
        this.defineEvents = defineEvents;
    }

    /**
     * @return the eventPublishConfigs
     */
    public List<DefineEventPublishConfig> getEventPublishConfigs() {
        return eventPublishConfigs;
    }

    /**
     * @param eventPublishConfigs 要设置的eventPublishConfigs
     */
    public void setEventPublishConfigs(List<DefineEventPublishConfig> eventPublishConfigs) {
        this.eventPublishConfigs = eventPublishConfigs;
    }

    /**
     * @return the includeItems
     */
    public List<ProcessItemConfig> getIncludeItems() {
        return includeItems;
    }

    /**
     * @param includeItems 要设置的includeItems
     */
    public void setIncludeItems(List<ProcessItemConfig> includeItems) {
        this.includeItems = includeItems;
    }

    /**
     * @return the mutexItems
     */
    public List<ItemMutexItemConfig> getMutexItems() {
        return mutexItems;
    }

    /**
     * @param mutexItems 要设置的mutexItems
     */
    public void setMutexItems(List<ItemMutexItemConfig> mutexItems) {
        this.mutexItems = mutexItems;
    }

    /**
     * @return the relatedItems
     */
    public List<ItemRelatedItemConfig> getRelatedItems() {
        return relatedItems;
    }

    /**
     * @param relatedItems 要设置的relatedItems
     */
    public void setRelatedItems(List<ItemRelatedItemConfig> relatedItems) {
        this.relatedItems = relatedItems;
    }

    /**
     * @return the businessIntegrationConfigs
     */
    public List<BusinessIntegrationConfig> getBusinessIntegrationConfigs() {
        return businessIntegrationConfigs;
    }

    /**
     * @param businessIntegrationConfigs 要设置的businessIntegrationConfigs
     */
    public void setBusinessIntegrationConfigs(List<BusinessIntegrationConfig> businessIntegrationConfigs) {
        this.businessIntegrationConfigs = businessIntegrationConfigs;
    }

    /**
     * 根据业务集成类型获取业务集成配置
     *
     * @param type
     * @return
     */
    public BusinessIntegrationConfig getBusinessIntegrationConfigByType(String type) {
        if (CollectionUtils.isEmpty(businessIntegrationConfigs)) {
            return null;
        }
        for (BusinessIntegrationConfig config : businessIntegrationConfigs) {
            if (StringUtils.equals(config.getType(), type)) {
                return config;
            }
        }
        return null;
    }

    /**
     * 表单设置
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ProcessItemFormConfig extends BaseObject {
        private static final long serialVersionUID = -8226359851821192916L;

        // 配置类型，1引用模板，2自定义
        private String configType;
        // 引用模板UUID
        private String templateUuid;
        // 引用模板名称
        private String templateName;
        // 表单定义UUID
        private String formUuid;
        // 表单定义名称
        private String formName;
        // 事项名称字段
        private String itemNameField;
        // 事项编码字段
        private String itemCodeField;
        // 业务主体名称字段
        private String entityNameField;
        // 业务主体ID字段
        private String entityIdField;
        // 办理时限字段
        private String timeLimitField;
        // 其他字段回填配置
        private List<ItemFormFieldMapping> fieldMappings;
        // 材料从表ID
        private String materialSubformId;
        // 材料名称字段
        private String materialNameField;
        // 材料编码字段
        private String materialCodeField;
        // 材料是否必填字段
        private String materialRequiredField;
        // 材料附件字段
        private String materialFileField;
        // 材料其他字段回填配置
        private List<ItemFormFieldMapping> materialFieldMappings;
        // 包含事项办理信息显示位置
        private String includeItemPlaceHolder;
        // 状态定义列表
        private List<StateDefinition> states;

        // 是否启用表单设置
        private boolean enabledDyformSetting;
        // 表单设置配置
        private Map<String, Object> widgetDyformSetting;

//        // 单据转换ID——表单分发的单据转换规则
//        private String botId;

        /**
         * @return the configType
         */
        public String getConfigType() {
            return configType;
        }

        /**
         * @param configType 要设置的configType
         */
        public void setConfigType(String configType) {
            this.configType = configType;
        }

        /**
         * @return the templateUuid
         */
        public String getTemplateUuid() {
            return templateUuid;
        }

        /**
         * @param templateUuid 要设置的templateUuid
         */
        public void setTemplateUuid(String templateUuid) {
            this.templateUuid = templateUuid;
        }

        /**
         * @return the templateName
         */
        public String getTemplateName() {
            return templateName;
        }

        /**
         * @param templateName 要设置的templateName
         */
        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }

        /**
         * @return the formUuid
         */
        public String getFormUuid() {
            return formUuid;
        }

        /**
         * @param formUuid 要设置的formUuid
         */
        public void setFormUuid(String formUuid) {
            this.formUuid = formUuid;
        }

        /**
         * @return the formName
         */
        public String getFormName() {
            return formName;
        }

        /**
         * @param formName 要设置的formName
         */
        public void setFormName(String formName) {
            this.formName = formName;
        }

        /**
         * @return the itemNameField
         */
        public String getItemNameField() {
            return itemNameField;
        }

        /**
         * @param itemNameField 要设置的itemNameField
         */
        public void setItemNameField(String itemNameField) {
            this.itemNameField = itemNameField;
        }

        /**
         * @return the itemCodeField
         */
        public String getItemCodeField() {
            return itemCodeField;
        }

        /**
         * @param itemCodeField 要设置的itemCodeField
         */
        public void setItemCodeField(String itemCodeField) {
            this.itemCodeField = itemCodeField;
        }

        /**
         * @return the entityNameField
         */
        public String getEntityNameField() {
            return entityNameField;
        }

        /**
         * @param entityNameField 要设置的entityNameField
         */
        public void setEntityNameField(String entityNameField) {
            this.entityNameField = entityNameField;
        }

        /**
         * @return the entityIdField
         */
        public String getEntityIdField() {
            return entityIdField;
        }

        /**
         * @param entityIdField 要设置的entityIdField
         */
        public void setEntityIdField(String entityIdField) {
            this.entityIdField = entityIdField;
        }

        /**
         * @return the timeLimitField
         */
        public String getTimeLimitField() {
            return timeLimitField;
        }

        /**
         * @param timeLimitField 要设置的timeLimitField
         */
        public void setTimeLimitField(String timeLimitField) {
            this.timeLimitField = timeLimitField;
        }

        /**
         * @return the fieldMappings
         */
        public List<ItemFormFieldMapping> getFieldMappings() {
            return fieldMappings;
        }

        /**
         * @param fieldMappings 要设置的fieldMappings
         */
        public void setFieldMappings(List<ItemFormFieldMapping> fieldMappings) {
            this.fieldMappings = fieldMappings;
        }

        /**
         * @return the materialSubformId
         */
        public String getMaterialSubformId() {
            return materialSubformId;
        }

        /**
         * @param materialSubformId 要设置的materialSubformId
         */
        public void setMaterialSubformId(String materialSubformId) {
            this.materialSubformId = materialSubformId;
        }

        /**
         * @return the materialNameField
         */
        public String getMaterialNameField() {
            return materialNameField;
        }

        /**
         * @param materialNameField 要设置的materialNameField
         */
        public void setMaterialNameField(String materialNameField) {
            this.materialNameField = materialNameField;
        }

        /**
         * @return the materialCodeField
         */
        public String getMaterialCodeField() {
            return materialCodeField;
        }

        /**
         * @param materialCodeField 要设置的materialCodeField
         */
        public void setMaterialCodeField(String materialCodeField) {
            this.materialCodeField = materialCodeField;
        }

        /**
         * @return the materialRequiredField
         */
        public String getMaterialRequiredField() {
            return materialRequiredField;
        }

        /**
         * @param materialRequiredField 要设置的materialRequiredField
         */
        public void setMaterialRequiredField(String materialRequiredField) {
            this.materialRequiredField = materialRequiredField;
        }

        /**
         * @return the materialFileField
         */
        public String getMaterialFileField() {
            return materialFileField;
        }

        /**
         * @return the materialFieldMappings
         */
        public List<ItemFormFieldMapping> getMaterialFieldMappings() {
            return materialFieldMappings;
        }

        /**
         * @param materialFieldMappings 要设置的materialFieldMappings
         */
        public void setMaterialFieldMappings(List<ItemFormFieldMapping> materialFieldMappings) {
            this.materialFieldMappings = materialFieldMappings;
        }

        /**
         * @param materialFileField 要设置的materialFileField
         */
        public void setMaterialFileField(String materialFileField) {
            this.materialFileField = materialFileField;
        }

        /**
         * @return the includeItemPlaceHolder
         */
        public String getIncludeItemPlaceHolder() {
            return includeItemPlaceHolder;
        }

        /**
         * @param includeItemPlaceHolder 要设置的includeItemPlaceHolder
         */
        public void setIncludeItemPlaceHolder(String includeItemPlaceHolder) {
            this.includeItemPlaceHolder = includeItemPlaceHolder;
        }

        /**
         * @return the states
         */
        public List<StateDefinition> getStates() {
            return states;
        }

        /**
         * @param states 要设置的states
         */
        public void setStates(List<StateDefinition> states) {
            this.states = states;
        }

        /**
         * @return the enabledDyformSetting
         */
        public boolean isEnabledDyformSetting() {
            return enabledDyformSetting;
        }

        /**
         * @param enabledDyformSetting 要设置的enabledDyformSetting
         */
        public void setEnabledDyformSetting(boolean enabledDyformSetting) {
            this.enabledDyformSetting = enabledDyformSetting;
        }

        /**
         * @return the widgetDyformSetting
         */
        public Map<String, Object> getWidgetDyformSetting() {
            return widgetDyformSetting;
        }

        /**
         * @param widgetDyformSetting 要设置的widgetDyformSetting
         */
        public void setWidgetDyformSetting(Map<String, Object> widgetDyformSetting) {
            this.widgetDyformSetting = widgetDyformSetting;
        }

        //        /**
//         * @return the botId
//         */
//        public String getBotId() {
//            return botId;
//        }
//
//        /**
//         * @param botId 要设置的botId
//         */
//        public void setBotId(String botId) {
//            this.botId = botId;
//        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class ItemFormFieldMapping extends BaseObject {
        private static final long serialVersionUID = 4380021002098844514L;
        // 源字段来源1、业务主体，2、事项源
        private String sourceType;
        // 源字段
        private String sourceField;
        // 源字段名称
        private String sourceFieldName;
        // 目标字段
        private String targetField;
        // 目标字段名称
        private String targetFieldName;

        /**
         * @return the sourceType
         */
        public String getSourceType() {
            return sourceType;
        }

        /**
         * @param sourceType 要设置的sourceType
         */
        public void setSourceType(String sourceType) {
            this.sourceType = sourceType;
        }

        /**
         * @return the sourceField
         */
        public String getSourceField() {
            return sourceField;
        }

        /**
         * @param sourceField 要设置的sourceField
         */
        public void setSourceField(String sourceField) {
            this.sourceField = sourceField;
        }

        /**
         * @return the sourceFieldName
         */
        public String getSourceFieldName() {
            return sourceFieldName;
        }

        /**
         * @param sourceFieldName 要设置的sourceFieldName
         */
        public void setSourceFieldName(String sourceFieldName) {
            this.sourceFieldName = sourceFieldName;
        }

        /**
         * @return the targetField
         */
        public String getTargetField() {
            return targetField;
        }

        /**
         * @param targetField 要设置的targetField
         */
        public void setTargetField(String targetField) {
            this.targetField = targetField;
        }

        /**
         * @return the targetFieldName
         */
        public String getTargetFieldName() {
            return targetFieldName;
        }

        /**
         * @param targetFieldName 要设置的targetFieldName
         */
        public void setTargetFieldName(String targetFieldName) {
            this.targetFieldName = targetFieldName;
        }
    }

    /**
     * 表单分发配置类
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class DispenseFormConfig extends BaseObject {
        private static final long serialVersionUID = 2579686911889081696L;

        public static final String TYPE_USE_PARENT_ITEM_FORM = "1";

        public static final String TYPE_USE_BOT = "2";

        // ID，自动生成
        private String id;
        // 事项定义名称
        private String itemDefName;
        // 事项定义ID
        private String itemDefId;
        // 事项类型
        private String itemType;
        // 事项名称
        private String itemName;
        // 事项编码
        private String itemCode;
        // 表单分发类型
        private String type;
        // 单据转换ID
        private String botId;
        // 单据转换名称
        private String botName;
        // 使用表单UUID
        private String formUuid;
        // 使用表单名称
        private String formName;
        // 业务主体名称字段
        private String entityNameField;
        // 业务主体ID字段
        private String entityIdField;
        // 办理时限字段
        private String timeLimitField;

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id 要设置的id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the itemDefName
         */
        public String getItemDefName() {
            return itemDefName;
        }

        /**
         * @param itemDefName 要设置的itemDefName
         */
        public void setItemDefName(String itemDefName) {
            this.itemDefName = itemDefName;
        }

        /**
         * @return the itemDefId
         */
        public String getItemDefId() {
            return itemDefId;
        }

        /**
         * @param itemDefId 要设置的itemDefId
         */
        public void setItemDefId(String itemDefId) {
            this.itemDefId = itemDefId;
        }

        /**
         * @return the itemType
         */
        public String getItemType() {
            return itemType;
        }

        /**
         * @param itemType 要设置的itemType
         */
        public void setItemType(String itemType) {
            this.itemType = itemType;
        }

        /**
         * @return the itemName
         */
        public String getItemName() {
            return itemName;
        }

        /**
         * @param itemName 要设置的itemName
         */
        public void setItemName(String itemName) {
            this.itemName = itemName;
        }

        /**
         * @return the itemCode
         */
        public String getItemCode() {
            return itemCode;
        }

        /**
         * @param itemCode 要设置的itemCode
         */
        public void setItemCode(String itemCode) {
            this.itemCode = itemCode;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type 要设置的type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * @return the botId
         */
        public String getBotId() {
            return botId;
        }

        /**
         * @param botId 要设置的botId
         */
        public void setBotId(String botId) {
            this.botId = botId;
        }

        /**
         * @return the botName
         */
        public String getBotName() {
            return botName;
        }

        /**
         * @param botName 要设置的botName
         */
        public void setBotName(String botName) {
            this.botName = botName;
        }

        /**
         * @return the formUuid
         */
        public String getFormUuid() {
            return formUuid;
        }

        /**
         * @param formUuid 要设置的formUuid
         */
        public void setFormUuid(String formUuid) {
            this.formUuid = formUuid;
        }

        /**
         * @return the formName
         */
        public String getFormName() {
            return formName;
        }

        /**
         * @param formName 要设置的formName
         */
        public void setFormName(String formName) {
            this.formName = formName;
        }

        /**
         * @return the entityNameField
         */
        public String getEntityNameField() {
            return entityNameField;
        }

        /**
         * @param entityNameField 要设置的entityNameField
         */
        public void setEntityNameField(String entityNameField) {
            this.entityNameField = entityNameField;
        }

        /**
         * @return the entityIdField
         */
        public String getEntityIdField() {
            return entityIdField;
        }

        /**
         * @param entityIdField 要设置的entityIdField
         */
        public void setEntityIdField(String entityIdField) {
            this.entityIdField = entityIdField;
        }

        /**
         * @return the timeLimitField
         */
        public String getTimeLimitField() {
            return timeLimitField;
        }

        /**
         * @param timeLimitField 要设置的timeLimitField
         */
        public void setTimeLimitField(String timeLimitField) {
            this.timeLimitField = timeLimitField;
        }
    }

    /**
     * 办理情形配置类
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class SituationConfig extends BaseObject {
        private static final long serialVersionUID = 8502782215805681065L;
        // 名称
        private String situationName;
        // ID，自动生成
        private String id;
        // 事项工作日
        private int itemWorkday;
        // 事项材料编码列表，多个以分号隔开
        private String itemMaterialCodes;
        // 备注
        private String remark;
        // 办理条件名称
        private String conditionName;
        // 办理情形条件
        private List<SituationConditionConfig> conditionConfigs = Lists.newArrayListWithCapacity(0);

        /**
         * @return the situationName
         */
        public String getSituationName() {
            return situationName;
        }

        /**
         * @param situationName 要设置的situationName
         */
        public void setSituationName(String situationName) {
            this.situationName = situationName;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id 要设置的id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the itemWorkday
         */
        public int getItemWorkday() {
            return itemWorkday;
        }

        /**
         * @param itemWorkday 要设置的itemWorkday
         */
        public void setItemWorkday(int itemWorkday) {
            this.itemWorkday = itemWorkday;
        }

        /**
         * @return the itemMaterialCodes
         */
        public String getItemMaterialCodes() {
            return itemMaterialCodes;
        }

        /**
         * @param itemMaterialCodes 要设置的itemMaterialCodes
         */
        public void setItemMaterialCodes(String itemMaterialCodes) {
            this.itemMaterialCodes = itemMaterialCodes;
        }

        /**
         * @return the remark
         */
        public String getRemark() {
            return remark;
        }

        /**
         * @param remark 要设置的remark
         */
        public void setRemark(String remark) {
            this.remark = remark;
        }

        /**
         * @return the conditionName
         */
        public String getConditionName() {
            return conditionName;
        }

        /**
         * @param conditionName 要设置的conditionName
         */
        public void setConditionName(String conditionName) {
            this.conditionName = conditionName;
        }

        /**
         * @return the conditionConfigs
         */
        public List<SituationConditionConfig> getConditionConfigs() {
            return conditionConfigs;
        }

        /**
         * @param conditionConfigs 要设置的conditionConfigs
         */
        public void setConditionConfigs(List<SituationConditionConfig> conditionConfigs) {
            this.conditionConfigs = conditionConfigs;
        }
    }

    /**
     * 办理情形条件配置类
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class SituationConditionConfig extends BaseObject {
        private static final long serialVersionUID = -2704148638121486903L;

        // 条件名称
        private String name;
        // 条件ID，自动生成
        private String id;
        // 表达式类型，1表单域值，9逻辑条件
        private String type;
        // 连接符，表达式类型为逻辑条件时有效
        private String connector;
        // 左括号
        private String leftBracket;
        // 操作符
        private String operator;
        // 表单定义UUID
        private String formUuid;
        // 表单字段名
        private String fieldName;
        // 比较值
        private String value;
        // 右括号
        private String rightBracket;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name 要设置的name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id 要设置的id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type 要设置的type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * @return the connector
         */
        public String getConnector() {
            return connector;
        }

        /**
         * @param connector 要设置的connector
         */
        public void setConnector(String connector) {
            this.connector = connector;
        }

        /**
         * @return the leftBracket
         */
        public String getLeftBracket() {
            return leftBracket;
        }

        /**
         * @param leftBracket 要设置的leftBracket
         */
        public void setLeftBracket(String leftBracket) {
            this.leftBracket = leftBracket;
        }

        /**
         * @return the operator
         */
        public String getOperator() {
            return operator;
        }

        /**
         * @param operator 要设置的operator
         */
        public void setOperator(String operator) {
            this.operator = operator;
        }

        /**
         * @return the formUuid
         */
        public String getFormUuid() {
            return formUuid;
        }

        /**
         * @param formUuid 要设置的formUuid
         */
        public void setFormUuid(String formUuid) {
            this.formUuid = formUuid;
        }

        /**
         * @return the fieldName
         */
        public String getFieldName() {
            return fieldName;
        }

        /**
         * @param fieldName 要设置的fieldName
         */
        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return value;
        }

        /**
         * @param value 要设置的value
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * @return the rightBracket
         */
        public String getRightBracket() {
            return rightBracket;
        }

        /**
         * @param rightBracket 要设置的rightBracket
         */
        public void setRightBracket(String rightBracket) {
            this.rightBracket = rightBracket;
        }
    }

//    /**
//     * 包含事项
//     */
//    public static final class ItemIncludeItemConfig extends BaseObject {
//        private static final long serialVersionUID = -8529498571390107492L;
//
//        // 事项名称
//        private String itemName;
//        // 事项编码
//        private String itemCode;
//        // 前置事项
//        private String frontItemCode;
//        // 事项定义名称
//        private String itemDefName;
//        // 事项定义ID
//        private String itemDefId;
//        // 事项类型
//        private String itemType;
//
//        /**
//         * @return the itemName
//         */
//        public String getItemName() {
//            return itemName;
//        }
//
//        /**
//         * @param itemName 要设置的itemName
//         */
//        public void setItemName(String itemName) {
//            this.itemName = itemName;
//        }
//
//        /**
//         * @return the itemCode
//         */
//        public String getItemCode() {
//            return itemCode;
//        }
//
//        /**
//         * @param itemCode 要设置的itemCode
//         */
//        public void setItemCode(String itemCode) {
//            this.itemCode = itemCode;
//        }
//
//        /**
//         * @return the frontItemCode
//         */
//        public String getFrontItemCode() {
//            return frontItemCode;
//        }
//
//        /**
//         * @param frontItemCode 要设置的frontItemCode
//         */
//        public void setFrontItemCode(String frontItemCode) {
//            this.frontItemCode = frontItemCode;
//        }
//
//        /**
//         * @return the itemDefName
//         */
//        public String getItemDefName() {
//            return itemDefName;
//        }
//
//        /**
//         * @param itemDefName 要设置的itemDefName
//         */
//        public void setItemDefName(String itemDefName) {
//            this.itemDefName = itemDefName;
//        }
//
//        /**
//         * @return the itemDefId
//         */
//        public String getItemDefId() {
//            return itemDefId;
//        }
//
//        /**
//         * @param itemDefId 要设置的itemDefId
//         */
//        public void setItemDefId(String itemDefId) {
//            this.itemDefId = itemDefId;
//        }
//
//        /**
//         * @return the itemType
//         */
//        public String getItemType() {
//            return itemType;
//        }
//
//        /**
//         * @param itemType 要设置的itemType
//         */
//        public void setItemType(String itemType) {
//            this.itemType = itemType;
//        }
//    }

    /**
     * 互斥事项
     */
    public static final class ItemMutexItemConfig extends BaseObject {
        private static final long serialVersionUID = -4383937241047590862L;

        // ID，自动生成
        private String id;
        // 互斥组别
        private String groupName;
        // 互斥事项
        private List<String> itemCodes;

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id 要设置的id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the groupName
         */
        public String getGroupName() {
            return groupName;
        }

        /**
         * @param groupName 要设置的groupName
         */
        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        /**
         * @return the itemCodes
         */
        public List<String> getItemCodes() {
            return itemCodes;
        }

        /**
         * @param itemCodes 要设置的itemCodes
         */
        public void setItemCodes(List<String> itemCodes) {
            this.itemCodes = itemCodes;
        }
    }

    /**
     * 关联事项
     */
    public static final class ItemRelatedItemConfig extends BaseObject {
        private static final long serialVersionUID = 3644040806629906887L;

        // ID，自动生成
        private String id;
        // 关联组别
        private String groupName;
        // 关联事项
        private List<String> itemCodes;

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id 要设置的id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the groupName
         */
        public String getGroupName() {
            return groupName;
        }

        /**
         * @param groupName 要设置的groupName
         */
        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        /**
         * @return the itemCodes
         */
        public List<String> getItemCodes() {
            return itemCodes;
        }

        /**
         * @param itemCodes 要设置的itemCodes
         */
        public void setItemCodes(List<String> itemCodes) {
            this.itemCodes = itemCodes;
        }
    }

    /**
     * 事件定义
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class DefineEventConfig extends BaseObject {
        private static final long serialVersionUID = 6586220442761129196L;

        // 名称
        private String name;
        // ID
        private String id;
        // 是否内置事件
        private boolean builtIn;
        // 是否里程碑事件
        private boolean milestone;
        // 备注
        private String remark;

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name 要设置的name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id 要设置的id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the builtIn
         */
        public boolean getBuiltIn() {
            return builtIn;
        }

        /**
         * @param builtIn 要设置的builtIn
         */
        public void setBuiltIn(boolean builtIn) {
            this.builtIn = builtIn;
        }

        /**
         * @return the milestone
         */
        public boolean getMilestone() {
            return milestone;
        }

        /**
         * @param milestone 要设置的milestone
         */
        public void setMilestone(boolean milestone) {
            this.milestone = milestone;
        }

        /**
         * @return the remark
         */
        public String getRemark() {
            return remark;
        }

        /**
         * @param remark 要设置的remark
         */
        public void setRemark(String remark) {
            this.remark = remark;
        }
    }

    /**
     * 事件发布
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class DefineEventPublishConfig extends BaseObject {
        private static final long serialVersionUID = -3742277489928162757L;

        // 事件发布ID，自动生成
        private String id;
        // 事件名称
        private String eventName;
        // 事件ID
        private String eventId;
        // ITEM_STATE_CHANGED业务事项状态变更
        private String triggerType;
        // 事项状态
        private String itemState;
        // 附加条件
        private boolean additionalCondition;
        // 表达式脚本类型
        private String expressionScriptType;
        // 条件表达式
        private String conditionExpression;
        // 是否里程碑事件
        private boolean milestone;
        // 交付物字段
        private String resultField;

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id 要设置的id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the eventName
         */
        public String getEventName() {
            return eventName;
        }

        /**
         * @param eventName 要设置的eventName
         */
        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        /**
         * @return the eventId
         */
        public String getEventId() {
            return eventId;
        }

        /**
         * @param eventId 要设置的eventId
         */
        public void setEventId(String eventId) {
            this.eventId = eventId;
        }

        /**
         * @return the triggerType
         */
        public String getTriggerType() {
            return triggerType;
        }

        /**
         * @param triggerType 要设置的triggerType
         */
        public void setTriggerType(String triggerType) {
            this.triggerType = triggerType;
        }

        /**
         * @return the itemState
         */
        public String getItemState() {
            return itemState;
        }

        /**
         * @param itemState 要设置的itemState
         */
        public void setItemState(String itemState) {
            this.itemState = itemState;
        }

        /**
         * @return the additionalCondition
         */
        public boolean getAdditionalCondition() {
            return additionalCondition;
        }

        /**
         * @param additionalCondition 要设置的additionalCondition
         */
        public void setAdditionalCondition(boolean additionalCondition) {
            this.additionalCondition = additionalCondition;
        }

        /**
         * @return the expressionScriptType
         */
        public String getExpressionScriptType() {
            return expressionScriptType;
        }

        /**
         * @param expressionScriptType 要设置的expressionScriptType
         */
        public void setExpressionScriptType(String expressionScriptType) {
            this.expressionScriptType = expressionScriptType;
        }

        /**
         * @return the conditionExpression
         */
        public String getConditionExpression() {
            return conditionExpression;
        }

        /**
         * @param conditionExpression 要设置的conditionExpression
         */
        public void setConditionExpression(String conditionExpression) {
            this.conditionExpression = conditionExpression;
        }

        /**
         * @return the milestone
         */
        public boolean getMilestone() {
            return milestone;
        }

        /**
         * @param milestone 要设置的milestone
         */
        public void setMilestone(boolean milestone) {
            this.milestone = milestone;
        }

        /**
         * @return the resultField
         */
        public String getResultField() {
            return resultField;
        }

        /**
         * @param resultField 要设置的resultField
         */
        public void setResultField(String resultField) {
            this.resultField = resultField;
        }
    }

    /**
     * 业务集成
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class BusinessIntegrationConfig extends BaseObject {
        private static final long serialVersionUID = 3843554401504692773L;

        // 使用事项表单
        public static final String FORM_DATA_TYPE_USE_ITEM = "1";
        // 使用单据转换
        public static final String FORM_DATA_TYPE_USE_BOT = "2";

        // 集成方式，workflow工作流
        private String type;
        // 是否启用
        private boolean enabled;
        // 配置类型，1引用模板，2自定义
        private String configType;
        // 引用模板UUID
        private String templateUuid;
        // 引用模板名称
        private String templateName;
        // 流程定义ID
        private String flowDefId;
        //        // 流程业务定义ID
//        private String flowBizDefId;
        // 表单数据类型，1使用事项表单，2使用单据转换
        private String formDataType;
        // 单据转换规则ID
        private String copyBotRuleId;
        // 办结时反馈
        private boolean returnWithOver;
        // 指定流向反馈
        private boolean returnWithDirection;
        // 反馈流向ID
        private String returnDirectionId;
        // 反馈单据转换规则ID
        private String returnBotRuleId;
        // 里程碑
        // private List<MilestoneConfig> milestoneConfigs;
        // 事件发布
        private List<EventPublishConfig> eventPublishConfigs;
        // 同步计时信息
        private boolean syncTimerInfo;
        // 状态管理
        private List<FlowBusinessDefinitionJson.FlowBusinessState> states;
        // 发起的事项配置列表
        // private List<BusinessIntegrationNewItemConfig> newItemConfigs;

        /**
         * @return the type
         */
        public String getType() {
            return type;
        }

        /**
         * @param type 要设置的type
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * @return
         */
        public boolean isEnabled() {
            return enabled;
        }

        /**
         * @param enabled
         */
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        /**
         * @return the configType
         */
        public String getConfigType() {
            return configType;
        }

        /**
         * @param configType 要设置的configType
         */
        public void setConfigType(String configType) {
            this.configType = configType;
        }

        /**
         * @return the templateUuid
         */
        public String getTemplateUuid() {
            return templateUuid;
        }

        /**
         * @param templateUuid 要设置的templateUuid
         */
        public void setTemplateUuid(String templateUuid) {
            this.templateUuid = templateUuid;
        }

        /**
         * @return the templateName
         */
        public String getTemplateName() {
            return templateName;
        }

        /**
         * @param templateName 要设置的templateName
         */
        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }

        /**
         * @return the flowDefId
         */
        public String getFlowDefId() {
            return flowDefId;
        }

        /**
         * @param flowDefId 要设置的flowDefId
         */
        public void setFlowDefId(String flowDefId) {
            this.flowDefId = flowDefId;
        }

//        /**
//         * @return the flowBizDefId
//         */
//        public String getFlowBizDefId() {
//            return flowBizDefId;
//        }
//
//        /**
//         * @param flowBizDefId 要设置的flowBizDefId
//         */
//        public void setFlowBizDefId(String flowBizDefId) {
//            this.flowBizDefId = flowBizDefId;
//        }

        /**
         * @return the formDataType
         */
        public String getFormDataType() {
            return formDataType;
        }

        /**
         * @param formDataType 要设置的formDataType
         */
        public void setFormDataType(String formDataType) {
            this.formDataType = formDataType;
        }

        /**
         * @return the copyBotRuleId
         */
        public String getCopyBotRuleId() {
            return copyBotRuleId;
        }

        /**
         * @param copyBotRuleId 要设置的copyBotRuleId
         */
        public void setCopyBotRuleId(String copyBotRuleId) {
            this.copyBotRuleId = copyBotRuleId;
        }

        /**
         * @return the returnWithOver
         */
        public boolean isReturnWithOver() {
            return returnWithOver;
        }

        /**
         * @param returnWithOver 要设置的returnWithOver
         */
        public void setReturnWithOver(boolean returnWithOver) {
            this.returnWithOver = returnWithOver;
        }

        /**
         * @return the returnWithDirection
         */
        public boolean isReturnWithDirection() {
            return returnWithDirection;
        }

        /**
         * @param returnWithDirection 要设置的returnWithDirection
         */
        public void setReturnWithDirection(boolean returnWithDirection) {
            this.returnWithDirection = returnWithDirection;
        }

        /**
         * @return the returnDirectionId
         */
        public String getReturnDirectionId() {
            return returnDirectionId;
        }

        /**
         * @param returnDirectionId 要设置的returnDirectionId
         */
        public void setReturnDirectionId(String returnDirectionId) {
            this.returnDirectionId = returnDirectionId;
        }

        /**
         * @return the returnBotRuleId
         */
        public String getReturnBotRuleId() {
            return returnBotRuleId;
        }

        /**
         * @param returnBotRuleId 要设置的returnBotRuleId
         */
        public void setReturnBotRuleId(String returnBotRuleId) {
            this.returnBotRuleId = returnBotRuleId;
        }

//        /**
//         * @return the milestoneConfigs
//         */
//        public List<MilestoneConfig> getMilestoneConfigs() {
//            return milestoneConfigs;
//        }
//
//        /**
//         * @param milestoneConfigs 要设置的milestoneConfigs
//         */
//        public void setMilestoneConfigs(List<MilestoneConfig> milestoneConfigs) {
//            this.milestoneConfigs = milestoneConfigs;
//        }

        /**
         * @return the eventPublishConfigs
         */
        public List<EventPublishConfig> getEventPublishConfigs() {
            return eventPublishConfigs;
        }

        /**
         * @param eventPublishConfigs 要设置的eventPublishConfigs
         */
        public void setEventPublishConfigs(List<EventPublishConfig> eventPublishConfigs) {
            this.eventPublishConfigs = eventPublishConfigs;
        }

        /**
         * @return the syncTimerInfo
         */
        public boolean isSyncTimerInfo() {
            return syncTimerInfo;
        }

        /**
         * @param syncTimerInfo 要设置的syncTimerInfo
         */
        public void setSyncTimerInfo(boolean syncTimerInfo) {
            this.syncTimerInfo = syncTimerInfo;
        }

        /**
         * @return the states
         */
        public List<FlowBusinessDefinitionJson.FlowBusinessState> getStates() {
            return states;
        }

        /**
         * @param states 要设置的states
         */
        public void setStates(List<FlowBusinessDefinitionJson.FlowBusinessState> states) {
            this.states = states;
        }

//        /**
//         * @return the newItemConfigs
//         */
//        public List<BusinessIntegrationNewItemConfig> getNewItemConfigs() {
//            return newItemConfigs;
//        }
//
//        /**
//         * @param newItemConfigs 要设置的newItemConfigs
//         */
//        public void setNewItemConfigs(List<BusinessIntegrationNewItemConfig> newItemConfigs) {
//            this.newItemConfigs = newItemConfigs;
//        }
    }

    /**
     * 事件发布
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class EventPublishConfig extends BaseObject {

        // 事件发布ID，自动生成
        private String id;
        // 事件名称
        private String eventName;
        // 事件ID
        private String eventId;
        // 触发类型
        private String triggerType;
        // 环节ID
        private List<String> taskIds;
        // 操作类型
        private String actionType;
        // 流向ID
        private List<String> directionIds;
        // 附加条件
        private boolean additionalCondition;
        // 表达式脚本类型
        private String expressionScriptType;
        // 条件表达式
        private String conditionExpression;
        // 交付物字段
        private String resultField;

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id 要设置的id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the eventName
         */
        public String getEventName() {
            return eventName;
        }

        /**
         * @param eventName 要设置的eventName
         */
        public void setEventName(String eventName) {
            this.eventName = eventName;
        }

        /**
         * @return the eventId
         */
        public String getEventId() {
            return eventId;
        }

        /**
         * @param eventId 要设置的eventId
         */
        public void setEventId(String eventId) {
            this.eventId = eventId;
        }

        /**
         * @return the triggerType
         */
        public String getTriggerType() {
            return triggerType;
        }

        /**
         * @param triggerType 要设置的triggerType
         */
        public void setTriggerType(String triggerType) {
            this.triggerType = triggerType;
        }

        /**
         * @return the taskIds
         */
        public List<String> getTaskIds() {
            return taskIds;
        }

        /**
         * @param taskIds 要设置的taskIds
         */
        public void setTaskIds(List<String> taskIds) {
            this.taskIds = taskIds;
        }

        /**
         * @return the actionType
         */
        public String getActionType() {
            return actionType;
        }

        /**
         * @param actionType 要设置的actionType
         */
        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

        /**
         * @return the directionIds
         */
        public List<String> getDirectionIds() {
            return directionIds;
        }

        /**
         * @param directionIds 要设置的directionIds
         */
        public void setDirectionIds(List<String> directionIds) {
            this.directionIds = directionIds;
        }

        /**
         * @return the additionalCondition
         */
        public boolean getAdditionalCondition() {
            return additionalCondition;
        }

        /**
         * @param additionalCondition 要设置的additionalCondition
         */
        public void setAdditionalCondition(boolean additionalCondition) {
            this.additionalCondition = additionalCondition;
        }

        /**
         * @return the expressionScriptType
         */
        public String getExpressionScriptType() {
            return expressionScriptType;
        }

        /**
         * @param expressionScriptType 要设置的expressionScriptType
         */
        public void setExpressionScriptType(String expressionScriptType) {
            this.expressionScriptType = expressionScriptType;
        }

        /**
         * @return the conditionExpression
         */
        public String getConditionExpression() {
            return conditionExpression;
        }

        /**
         * @param conditionExpression 要设置的conditionExpression
         */
        public void setConditionExpression(String conditionExpression) {
            this.conditionExpression = conditionExpression;
        }

        /**
         * @return the resultField
         */
        public String getResultField() {
            return resultField;
        }

        /**
         * @param resultField 要设置的resultField
         */
        public void setResultField(String resultField) {
            this.resultField = resultField;
        }
    }

    /**
     * 里程碑配置
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class MilestoneConfig extends BaseObject {
        private static final long serialVersionUID = 9054847227566347523L;

        // 里程碑ID，自动生成
        private String id;
        // 里程碑名称
        private String name;
        // 30环节创建、40环节完成、50流向流转
        private String triggerType;
        // 环节ID
        private String taskId;
        // 流向ID
        private String directionId;
        // 交付物字段
        private String resultField;

        /**
         * @return the id
         */
        public String getId() {
            return id;
        }

        /**
         * @param id 要设置的id
         */
        public void setId(String id) {
            this.id = id;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param name 要设置的name
         */
        public void setName(String name) {
            this.name = name;
        }

        /**
         * @return the triggerType
         */
        public String getTriggerType() {
            return triggerType;
        }

        /**
         * @param triggerType 要设置的triggerType
         */
        public void setTriggerType(String triggerType) {
            this.triggerType = triggerType;
        }

        /**
         * @return the taskId
         */
        public String getTaskId() {
            return taskId;
        }

        /**
         * @param taskId 要设置的taskId
         */
        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }

        /**
         * @return the directionId
         */
        public String getDirectionId() {
            return directionId;
        }

        /**
         * @param directionId 要设置的directionId
         */
        public void setDirectionId(String directionId) {
            this.directionId = directionId;
        }

        /**
         * @return the resultField
         */
        public String getResultField() {
            return resultField;
        }

        /**
         * @param resultField 要设置的resultField
         */
        public void setResultField(String resultField) {
            this.resultField = resultField;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static final class BusinessIntegrationNewItemConfig extends BaseObject {
        private static final long serialVersionUID = 7026904738258764957L;

        // 显示标题
        private String title;
        // 发起新事项方式，1、流程办结时发起，2、流向流转时发起
        private String startItemWay;
        // 发起新事项的流向ID，多个以分号隔开
        private String startItemDirectionId;
        // 发起新事项的流向名称
        private String startItemDirectionName;
        // 发起的事项名称
        private String startItemName;
        // 发起的事项ID
        private String startItemId;
        // 表单数据类型，1使用原流程表单，2使用单据转换
        private String formDataType;
        // 单据转换规则ID
        private String copyBotRuleId;
        //        // 办结时反馈
//        private boolean returnWithOver;
//        // 指定流向反馈
//        private boolean returnWithDirection;
//        // 反馈流向ID
//        private String returnDirectionId;
//        // 反馈单据转换规则ID
//        private String returnBotRuleId;
        // 事项集成流程时可配置提交到的环节、AUTO_SUBMIT自动提交、指定环节
        private String toTaskId;
        // 办理人来源，1、按流程定义配置，2、现在指定
        private String taskUserSource;
        // 办理人类型，1、组织机构、4、源流程历史环节办理人、8、人员选项
        private String taskUserType;
        // 办理人名称
        private String taskUserName;
        // 办理人ID
        private String taskUserId;

        /**
         * @return the title
         */
        public String getTitle() {
            return title;
        }

        /**
         * @param title 要设置的title
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * @return the startItemWay
         */
        public String getStartItemWay() {
            return startItemWay;
        }

        /**
         * @param startItemWay 要设置的startItemWay
         */
        public void setStartItemWay(String startItemWay) {
            this.startItemWay = startItemWay;
        }

        /**
         * @return the startItemDirectionId
         */
        public String getStartItemDirectionId() {
            return startItemDirectionId;
        }

        /**
         * @param startItemDirectionId 要设置的startItemDirectionId
         */
        public void setStartItemDirectionId(String startItemDirectionId) {
            this.startItemDirectionId = startItemDirectionId;
        }

        /**
         * @return the startItemDirectionName
         */
        public String getStartItemDirectionName() {
            return startItemDirectionName;
        }

        /**
         * @param startItemDirectionName 要设置的startItemDirectionName
         */
        public void setStartItemDirectionName(String startItemDirectionName) {
            this.startItemDirectionName = startItemDirectionName;
        }

        /**
         * @return the startItemName
         */
        public String getStartItemName() {
            return startItemName;
        }

        /**
         * @param startItemName 要设置的startItemName
         */
        public void setStartItemName(String startItemName) {
            this.startItemName = startItemName;
        }

        /**
         * @return the startItemId
         */
        public String getStartItemId() {
            return startItemId;
        }

        /**
         * @param startItemId 要设置的startItemId
         */
        public void setStartItemId(String startItemId) {
            this.startItemId = startItemId;
        }

        /**
         * @return the formDataType
         */
        public String getFormDataType() {
            return formDataType;
        }

        /**
         * @param formDataType 要设置的formDataType
         */
        public void setFormDataType(String formDataType) {
            this.formDataType = formDataType;
        }

        /**
         * @return the copyBotRuleId
         */
        public String getCopyBotRuleId() {
            return copyBotRuleId;
        }

        /**
         * @param copyBotRuleId 要设置的copyBotRuleId
         */
        public void setCopyBotRuleId(String copyBotRuleId) {
            this.copyBotRuleId = copyBotRuleId;
        }

//        /**
//         * @return
//         */
//        public boolean isReturnWithOver() {
//            return returnWithOver;
//        }
//
//        /**
//         * @param returnWithOver
//         */
//        public void setReturnWithOver(boolean returnWithOver) {
//            this.returnWithOver = returnWithOver;
//        }
//
//        /**
//         * @return
//         */
//        public boolean isReturnWithDirection() {
//            return returnWithDirection;
//        }
//
//        /**
//         * @param returnWithDirection
//         */
//        public void setReturnWithDirection(boolean returnWithDirection) {
//            this.returnWithDirection = returnWithDirection;
//        }
//
//        /**
//         * @return the returnDirectionId
//         */
//        public String getReturnDirectionId() {
//            return returnDirectionId;
//        }
//
//        /**
//         * @param returnDirectionId 要设置的returnDirectionId
//         */
//        public void setReturnDirectionId(String returnDirectionId) {
//            this.returnDirectionId = returnDirectionId;
//        }
//
//        /**
//         * @return the returnBotRuleId
//         */
//        public String getReturnBotRuleId() {
//            return returnBotRuleId;
//        }
//
//        /**
//         * @param returnBotRuleId 要设置的returnBotRuleId
//         */
//        public void setReturnBotRuleId(String returnBotRuleId) {
//            this.returnBotRuleId = returnBotRuleId;
//        }

        /**
         * @return the toTaskId
         */
        public String getToTaskId() {
            return toTaskId;
        }

        /**
         * @param toTaskId 要设置的toTaskId
         */
        public void setToTaskId(String toTaskId) {
            this.toTaskId = toTaskId;
        }

        /**
         * @return the taskUserSource
         */
        public String getTaskUserSource() {
            return taskUserSource;
        }

        /**
         * @param taskUserSource 要设置的taskUserSource
         */
        public void setTaskUserSource(String taskUserSource) {
            this.taskUserSource = taskUserSource;
        }

        /**
         * @return the taskUserType
         */
        public String getTaskUserType() {
            return taskUserType;
        }

        /**
         * @param taskUserType 要设置的taskUserType
         */
        public void setTaskUserType(String taskUserType) {
            this.taskUserType = taskUserType;
        }

        /**
         * @return the taskUserName
         */
        public String getTaskUserName() {
            return taskUserName;
        }

        /**
         * @param taskUserName 要设置的taskUserName
         */
        public void setTaskUserName(String taskUserName) {
            this.taskUserName = taskUserName;
        }

        /**
         * @return the taskUserId
         */
        public String getTaskUserId() {
            return taskUserId;
        }

        /**
         * @param taskUserId 要设置的taskUserId
         */
        public void setTaskUserId(String taskUserId) {
            this.taskUserId = taskUserId;
        }
    }

}

