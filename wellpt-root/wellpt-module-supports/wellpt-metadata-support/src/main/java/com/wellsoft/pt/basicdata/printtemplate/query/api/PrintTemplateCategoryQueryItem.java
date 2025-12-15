/*
 * @(#)2021年10月15日 V1.0
 *
 * Copyright 2021 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.basicdata.printtemplate.query.api;

import com.wellsoft.context.jdbc.support.BaseQueryItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel("打印模版分类")
public class PrintTemplateCategoryQueryItem implements BaseQueryItem {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1464419710385877113L;

    // UUID
    @ApiModelProperty("分类UUID")
    private String uuid;

    // 创建时间
    @ApiModelProperty("创建时间")
    private Date createTime;

    // 名称
    @ApiModelProperty("名称")
    private String name;

    // 编号
    @ApiModelProperty("编号")
    private String code;

    // 图标
    @ApiModelProperty("图标")
    private String icon;

    // 图标颜色
    @ApiModelProperty("图标颜色")
    private String iconColor;

    // 流程备注
    @ApiModelProperty("备注")
    private String remark;

    // 归属系统单位ID
    @ApiModelProperty("归属系统单位ID")
    private String systemUnitId;

    /**
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime 要设置的createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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
     * @return the systemUnitId
     */
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * @param systemUnitId 要设置的systemUnitId
     */
    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

}
