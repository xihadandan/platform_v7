/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.MaxLength;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 组织节点基本类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2017-11-21.1	zyguo		2017-11-21		Create
 * </pre>
 * @date 2017-11-21
 */
@Entity
@Table(name = "MULTI_ORG_SYSTEM_UNIT")
@DynamicUpdate
@DynamicInsert
public class MultiOrgSystemUnit extends IdEntity {
    public static final String PT_ID = "S0000000000";
    public static final String PT_NAME = "基础平台";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6339911721413218146L;
    // 将平台列为特殊的系统单位
    public static MultiOrgSystemUnit PT = new MultiOrgSystemUnit(PT_ID, PT_NAME);
    // ID
    @ApiModelProperty("ID")
    private String id;
    // CODE
    @NotBlank
    @ApiModelProperty("CODE")
    private String code;
    // NAME
    @NotBlank
    @ApiModelProperty("NAME")
    private String name;
    // 简称
    @ApiModelProperty("简称")
    private String shortName;
    // SAP_CODE
    @ApiModelProperty("SAP_CODE")
    private String sapCode;
    // 备注
    @MaxLength(max = 255)
    @ApiModelProperty("备注")
    private String remark;
    // 是否集团单位
    @ApiModelProperty("是否集团单位")
    private Integer isGroupUnit;

    public MultiOrgSystemUnit() {
    }

    public MultiOrgSystemUnit(String id, String name) {
        this.id = id;
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
     * @return the shortName
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * @param shortName 要设置的shortName
     */
    public void setShortName(String shortName) {
        this.shortName = shortName;
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
     * @return the sapCode
     */
    public String getSapCode() {
        return sapCode;
    }

    /**
     * @param sapCode 要设置的sapCode
     */
    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    /**
     * @return the isGroupUnit
     */
    public Integer getIsGroupUnit() {
        return isGroupUnit;
    }

    /**
     * @param isGroupUnit 要设置的isGroupUnit
     */
    public void setIsGroupUnit(Integer isGroupUnit) {
        this.isGroupUnit = isGroupUnit;
    }

}
