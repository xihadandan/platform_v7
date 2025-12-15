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
 * Description: 业务流程表单配置模板
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
public class DefinitionTemplateProcessNodeFormConfig extends BaseObject {
    private static final long serialVersionUID = 4906732142139750967L;

    // 表单定义UUID
    private String formUuid;
    // 业务主体名称字段
    private String entityNameField;
    // 业务主体ID字段
    private String entityIdField;
    // 事项办理信息显示位置
    private String itemPlaceHolder;
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
     * @return the itemPlaceHolder
     */
    public String getItemPlaceHolder() {
        return itemPlaceHolder;
    }

    /**
     * @param itemPlaceHolder 要设置的itemPlaceHolder
     */
    public void setItemPlaceHolder(String itemPlaceHolder) {
        this.itemPlaceHolder = itemPlaceHolder;
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
