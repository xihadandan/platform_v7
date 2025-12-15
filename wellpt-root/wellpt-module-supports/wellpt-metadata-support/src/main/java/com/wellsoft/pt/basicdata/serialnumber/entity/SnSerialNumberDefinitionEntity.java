/*
 * @(#)7/12/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.pt.app.entity.AppDefElementI18nEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Description: 流水号定义
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/12/22.1	zhulh		7/12/22		Create
 * </pre>
 * @date 7/12/22
 */
@ApiModel("流水号定义")
@Entity
@Table(name = "sn_serial_number_definition")
@DynamicUpdate
@DynamicInsert
public class SnSerialNumberDefinitionEntity extends TenantEntity {
    private static final long serialVersionUID = 8973839773215336434L;

    @ApiModelProperty("名称")
    @NotBlank
    private String name;

    @ApiModelProperty("ID")
    @NotBlank
    private String id;

    @ApiModelProperty("编号")
    private String code;

    @ApiModelProperty("分类UUID")
    private String categoryUuid;

    @ApiModelProperty("模块ID")
    private String moduleId;

    @ApiModelProperty("前缀")
    private String prefix;

    @ApiModelProperty("指针初始值")
    private String initialValue;

    @ApiModelProperty("指针增量")
    @NotNull
    @Digits(fraction = 0, integer = Integer.MAX_VALUE)
    private Integer incremental;

    @ApiModelProperty("指针默认位数")
    private Integer defaultDigits;

    @ApiModelProperty("后缀")
    private String suffix;

    @ApiModelProperty("是否启用指针自动重置")
    private Boolean enablePointerReset;

    @ApiModelProperty("1按周期重置、2按变量重置")
    private String pointerResetType;

    @ApiModelProperty("指针重置规则")
    private String pointerResetRule;

    @ApiModelProperty("下一年新年度开始日期，默认值为 01-01")
    private String nextYearStartDate;

    @ApiModelProperty("使用人ID，多个以分号隔开")
    private String ownerIds;

    @ApiModelProperty("使用人名称，多个以分号隔开")
    private String ownerNames;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("归属系统")
    private String system;
    @ApiModelProperty("归属租户")
    private String tenant;

    private List<AppDefElementI18nEntity> i18ns;

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
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the categoryUuid
     */
    public String getCategoryUuid() {
        return categoryUuid;
    }

    /**
     * @param categoryUuid 要设置的categoryUuid
     */
    public void setCategoryUuid(String categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

    /**
     * @return the moduleId
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * @param moduleId 要设置的moduleId
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * @param prefix 要设置的prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * @return the initialValue
     */
    public String getInitialValue() {
        return initialValue;
    }

    /**
     * @param initialValue 要设置的initialValue
     */
    public void setInitialValue(String initialValue) {
        this.initialValue = initialValue;
    }

    /**
     * @return the incremental
     */
    public Integer getIncremental() {
        return incremental;
    }

    /**
     * @param incremental 要设置的incremental
     */
    public void setIncremental(Integer incremental) {
        this.incremental = incremental;
    }

    /**
     * @return the defaultDigits
     */
    public Integer getDefaultDigits() {
        return defaultDigits;
    }

    /**
     * @param defaultDigits 要设置的defaultDigits
     */
    public void setDefaultDigits(Integer defaultDigits) {
        this.defaultDigits = defaultDigits;
    }

    /**
     * @return the suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * @param suffix 要设置的suffix
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    /**
     * @return the enablePointerReset
     */
    public Boolean getEnablePointerReset() {
        return enablePointerReset;
    }

    /**
     * @param enablePointerReset 要设置的enablePointerReset
     */
    public void setEnablePointerReset(Boolean enablePointerReset) {
        this.enablePointerReset = enablePointerReset;
    }

    /**
     * @return the pointerResetType
     */
    public String getPointerResetType() {
        return pointerResetType;
    }

    /**
     * @param pointerResetType 要设置的pointerResetType
     */
    public void setPointerResetType(String pointerResetType) {
        this.pointerResetType = pointerResetType;
    }

    /**
     * @return the pointerResetRule
     */
    public String getPointerResetRule() {
        return pointerResetRule;
    }

    /**
     * @param pointerResetRule 要设置的pointerResetRule
     */
    public void setPointerResetRule(String pointerResetRule) {
        this.pointerResetRule = pointerResetRule;
    }

    /**
     * @return the nextYearStartDate
     */
    public String getNextYearStartDate() {
        return nextYearStartDate;
    }

    /**
     * @param nextYearStartDate 要设置的nextYearStartDate
     */
    public void setNextYearStartDate(String nextYearStartDate) {
        this.nextYearStartDate = nextYearStartDate;
    }

    /**
     * @return the ownerIds
     */
    public String getOwnerIds() {
        return ownerIds;
    }

    /**
     * @param ownerIds 要设置的ownerIds
     */
    public void setOwnerIds(String ownerIds) {
        this.ownerIds = ownerIds;
    }

    /**
     * @return the ownerNames
     */
    public String getOwnerNames() {
        return ownerNames;
    }

    /**
     * @param ownerNames 要设置的ownerNames
     */
    public void setOwnerNames(String ownerNames) {
        this.ownerNames = ownerNames;
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
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    @Transient
    public List<AppDefElementI18nEntity> getI18ns() {
        return i18ns;
    }

    public void setI18ns(List<AppDefElementI18nEntity> i18ns) {
        this.i18ns = i18ns;
    }
}
