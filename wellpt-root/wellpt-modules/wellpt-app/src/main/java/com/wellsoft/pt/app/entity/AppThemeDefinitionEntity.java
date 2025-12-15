/*
 * @(#)2/27/23 V1.0
 *
 * Copyright 2023 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 主题定义
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2/27/23.1	zhulh		2/27/23		Create
 * </pre>
 * @date 2/27/23
 */
@ApiModel("应用主题")
@Entity
@Table(name = "APP_THEME_DEFINITION")
@DynamicUpdate
@DynamicInsert
public class AppThemeDefinitionEntity extends TenantEntity {
    private static final long serialVersionUID = 8581060571001427123L;

    @ApiModelProperty("名称")
    @NotBlank
    private String name;
    @ApiModelProperty("ID")
    @NotBlank
    private String id;
    @ApiModelProperty("编号")
    @NotBlank
    private String code;
    @ApiModelProperty("排序号")
    private Integer sortOrder;
    @ApiModelProperty("应用于")
    private String applyTo;
    @ApiModelProperty("是否启用")
    private Boolean enabled;
    @ApiModelProperty("主题定义JSON UUID")
    private String definitionJsonUuid;
    @ApiModelProperty("备注")
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
     * @return the sortOrder
     */
    public Integer getSortOrder() {
        return sortOrder;
    }

    /**
     * @param sortOrder 要设置的sortOrder
     */
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    /**
     * @return the applyTo
     */
    public String getApplyTo() {
        return applyTo;
    }

    /**
     * @param applyTo 要设置的applyTo
     */
    public void setApplyTo(String applyTo) {
        this.applyTo = applyTo;
    }

    /**
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * @param enabled 要设置的enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the definitionJsonUuid
     */
    public String getDefinitionJsonUuid() {
        return definitionJsonUuid;
    }

    /**
     * @param definitionJsonUuid 要设置的definitionJsonUuid
     */
    public void setDefinitionJsonUuid(String definitionJsonUuid) {
        this.definitionJsonUuid = definitionJsonUuid;
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
