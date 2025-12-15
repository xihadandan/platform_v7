/*
 * @(#)7/11/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.serialnumber.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 流水号分类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 7/11/22.1	zhulh		7/11/22		Create
 * </pre>
 * @date 7/11/22
 */
@ApiModel("流水号分类")
@Entity
@Table(name = "sn_serial_number_category")
@DynamicUpdate
@DynamicInsert
public class SnSerialNumberCategoryEntity extends TenantEntity {

    @ApiModelProperty("分类名称")
    private String name;

    @ApiModelProperty("分类编号")
    private String code;

    @ApiModelProperty("图标")
    private String icon;

    @ApiModelProperty("图标颜色")
    private String iconColor;

    // @ApiModelProperty("上级分类UUID")
    // private String parentCategoryUuid;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("归属系统")
    private String system;
    @ApiModelProperty("归属租户")
    private String tenant;

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
}
