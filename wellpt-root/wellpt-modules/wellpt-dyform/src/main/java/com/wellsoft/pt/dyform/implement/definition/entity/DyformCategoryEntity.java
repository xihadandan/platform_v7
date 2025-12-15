/*
 * @(#)1/30/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.entity;

import com.wellsoft.context.jdbc.entity.SysEntity;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 1/31/24.1	zhulh		1/30/24		Create
 * </pre>
 * @date 1/31/24
 */
@Entity
@Table(name = "DYFORM_CATEGORY")
@DynamicUpdate
@DynamicInsert
public class DyformCategoryEntity extends SysEntity {
    private static final long serialVersionUID = -6184224171490422885L;

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("编号")
    private String code;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("图标颜色")
    private String iconColor;

    @ApiModelProperty("上级结点UUID")
    private Long parentUuid;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("模块ID")
    private String moduleId;

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
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon 要设置的icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return the iconColor
     */
    public String getIconColor() {
        return iconColor;
    }

    /**
     * @param iconColor 要设置的iconColor
     */
    public void setIconColor(String iconColor) {
        this.iconColor = iconColor;
    }

    /**
     * @return the parentUuid
     */
    public Long getParentUuid() {
        return parentUuid;
    }

    /**
     * @param parentUuid 要设置的parentUuid
     */
    public void setParentUuid(Long parentUuid) {
        this.parentUuid = parentUuid;
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

}