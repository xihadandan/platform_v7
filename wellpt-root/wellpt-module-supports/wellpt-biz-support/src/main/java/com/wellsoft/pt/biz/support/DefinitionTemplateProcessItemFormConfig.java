/*
 * @(#)11/25/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.support;

import com.wellsoft.context.base.BaseObject;
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
 * 11/25/22.1	zhulh		11/25/22		Create
 * </pre>
 * @date 11/25/22
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefinitionTemplateProcessItemFormConfig extends BaseObject {
    private static final long serialVersionUID = 8908271668343896937L;

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
    private List<ProcessItemConfig.ItemFormFieldMapping> fieldMappings;
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
    private List<ProcessItemConfig.ItemFormFieldMapping> materialFieldMappings;
    // 包含事项办理信息显示位置
    private String includeItemPlaceHolder;
    // 状态定义列表
    private List<StateDefinition> states;

    // 是否启用表单设置
    private boolean enabledDyformSetting;
    // 表单设置配置
    private Map<String, Object> widgetDyformSetting;

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
    public List<ProcessItemConfig.ItemFormFieldMapping> getFieldMappings() {
        return fieldMappings;
    }

    /**
     * @param fieldMappings 要设置的fieldMappings
     */
    public void setFieldMappings(List<ProcessItemConfig.ItemFormFieldMapping> fieldMappings) {
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
     * @param materialFileField 要设置的materialFileField
     */
    public void setMaterialFileField(String materialFileField) {
        this.materialFileField = materialFileField;
    }

    /**
     * @return the materialFieldMappings
     */
    public List<ProcessItemConfig.ItemFormFieldMapping> getMaterialFieldMappings() {
        return materialFieldMappings;
    }

    /**
     * @param materialFieldMappings 要设置的materialFieldMappings
     */
    public void setMaterialFieldMappings(List<ProcessItemConfig.ItemFormFieldMapping> materialFieldMappings) {
        this.materialFieldMappings = materialFieldMappings;
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
    
}
