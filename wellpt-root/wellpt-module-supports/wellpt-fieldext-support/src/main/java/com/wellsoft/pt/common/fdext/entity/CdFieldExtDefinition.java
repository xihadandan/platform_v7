/*
 * @(#)2016-03-11 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.common.fdext.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.pt.common.fdext.support.ICdFieldDefinition;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2016-03-11.1	zhongzh		2016-03-11		Create
 * </pre>
 * @date 2016-03-11
 */
@Entity
@Table(name = "CD_FIELD_EXT_DEFINITION")
@DynamicUpdate
@DynamicInsert
public class CdFieldExtDefinition extends TenantEntity implements ICdFieldDefinition {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1457678443578L;

    // 显示名称
    @NotBlank
    private String name;
    // 字段名，由字母、数字、下划线组成
    @NotBlank
    private String fieldName;
    // 默认值
    private String defaultValue;
    // 排序号，字段解析展示的顺序
    private Integer sortOrder;
    // 是否启用，TRUE启用、FALSE禁用
    private Integer enabled;
    // 级别代码
    private String groupCode;
    // INPUT控件类型，包含单行文本、多行文本、单选框、复选框、下拉框、日期
    @NotBlank
    private String inputType;
    // 配置的字典类型
    private String cfgKey;
    // 配置的字典名称，冗余字段
    private String cfgKeyName;
    // 日期格式
    private String dateFormat;
    // 验证规则
    private String validationRule;
    // 验证规则的约束值
    private String constraintValue;
    // 租户ID
    private String tenantId;
    // 备注
    private String remark;

    /**
     * @return the name
     */
    @Column(name = "NAME")
    public String getName() {
        return this.name;
    }

    /**
     * @param name
     */
    public String setName(String name) {
        return this.name = name;
    }

    /**
     * @return the fieldName
     */
    @Column(name = "FIELD_NAME")
    public String getFieldName() {
        return this.fieldName;
    }

    /**
     * @param fieldName
     */
    public String setFieldName(String fieldName) {
        return this.fieldName = fieldName;
    }

    /**
     * @return the defaultValue
     */
    @Column(name = "DEFAULT_VALUE")
    public String getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * @param defaultValue
     */
    public String setDefaultValue(String defaultValue) {
        return this.defaultValue = defaultValue;
    }

    /**
     * @return the sortOrder
     */
    @Column(name = "SORT_ORDER")
    public Integer getSortOrder() {
        return this.sortOrder;
    }

    /**
     * @param sortOrder
     */
    public Integer setSortOrder(Integer sortOrder) {
        return this.sortOrder = sortOrder;
    }

    /**
     * @return the enabled
     */
    @Column(name = "ENABLED")
    public Integer getEnabled() {
        return this.enabled;
    }

    /**
     * @param enabled
     */
    public Integer setEnabled(Integer enabled) {
        return this.enabled = enabled;
    }

    /**
     * @return the groupCode
     */
    @Column(name = "GROUP_CODE")
    public String getGroupCode() {
        return this.groupCode;
    }

    /**
     * @param groupCode
     */
    public String setGroupCode(String groupCode) {
        return this.groupCode = groupCode;
    }

    /**
     * @return the inputType
     */
    @Column(name = "INPUT_TYPE")
    public String getInputType() {
        return this.inputType;
    }

    /**
     * @param inputType
     */
    public String setInputType(String inputType) {
        return this.inputType = inputType;
    }

    /**
     * @return the cfgKey
     */
    @Column(name = "CFG_KEY")
    public String getCfgKey() {
        return this.cfgKey;
    }

    /**
     * @param cfgKey
     */
    public String setCfgKey(String cfgKey) {
        return this.cfgKey = cfgKey;
    }

    /**
     * @return the cfgKeyName
     */
    @Column(name = "CFG_KEY_NAME")
    public String getCfgKeyName() {
        return this.cfgKeyName;
    }

    /**
     * @param cfgKeyName
     */
    public String setCfgKeyName(String cfgKeyName) {
        return this.cfgKeyName = cfgKeyName;
    }

    /**
     * @return the dateFormat
     */
    @Column(name = "DATE_FORMAT")
    public String getDateFormat() {
        return this.dateFormat;
    }

    /**
     * @param dateFormat
     */
    public String setDateFormat(String dateFormat) {
        return this.dateFormat = dateFormat;
    }

    /**
     * @return the validationRule
     */
    @Column(name = "VALIDATION_RULE")
    public String getValidationRule() {
        return this.validationRule;
    }

    /**
     * @param validationRule
     */
    public String setValidationRule(String validationRule) {
        return this.validationRule = validationRule;
    }

    /**
     * @return the constraintValue
     */
    @Column(name = "CONSTRAINT_VALUE")
    public String getConstraintValue() {
        return this.constraintValue;
    }

    /**
     * @param constraintValue
     */
    public String setConstraintValue(String constraintValue) {
        return this.constraintValue = constraintValue;
    }

    /**
     * @return the tenantId
     */
    public String getTenantId() {
        return tenantId;
    }

    /**
     * @param tenantId 要设置的tenantId
     */
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
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
