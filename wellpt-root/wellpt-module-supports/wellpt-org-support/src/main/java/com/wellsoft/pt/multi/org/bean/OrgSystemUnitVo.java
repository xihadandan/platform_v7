/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.bean;

import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.MaxLength;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.List;

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
@ApiModel("系统单位")
public class OrgSystemUnitVo extends IdEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -6238024052322413769L;
    // ID
    @ApiModelProperty("id")
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
    @ApiModelProperty("备注")
    @MaxLength(max = 255)
    private String remark;
    @ApiModelProperty("isGroupUnit")
    private Integer isGroupUnit;
    @ApiModelProperty("members")
    private String members;

    private List<OrgElementAttrVo> orgElementAttrs;

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
     * @return the members
     */
    public String getMembers() {
        return members;
    }

    /**
     * @param members 要设置的members
     */
    public void setMembers(String members) {
        this.members = members;
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

    @Transient
    public List<OrgElementAttrVo> getOrgElementAttrs() {
        return orgElementAttrs;
    }

    public void setOrgElementAttrs(List<OrgElementAttrVo> orgElementAttrs) {
        this.orgElementAttrs = orgElementAttrs;
    }
}
