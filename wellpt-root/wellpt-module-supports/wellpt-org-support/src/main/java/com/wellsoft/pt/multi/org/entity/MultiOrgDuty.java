/*
 * @(#)2017-11-21 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.multi.org.entity;

import com.wellsoft.context.enums.IdPrefix;
import com.wellsoft.context.jdbc.entity.TenantEntity;
import com.wellsoft.context.validator.MaxLength;
import com.wellsoft.pt.multi.org.bean.OrgTreeNode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 职务
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
@Table(name = "MULTI_ORG_DUTY")
@DynamicUpdate
@DynamicInsert
@ApiModel("职务对象")
public class MultiOrgDuty extends TenantEntity {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 6522161209632137741L;
    // ID
    @ApiModelProperty("ID")
    private String id;
    // CODE
    @NotBlank
    @ApiModelProperty("编号")
    private String code;
    // NAME
    @NotBlank
    @ApiModelProperty("名称")
    private String name;
    // 简称
    @ApiModelProperty("简称")
    private String shortName;
    // SAP_CODE
    @ApiModelProperty("SAP_CODE")
    private String sapCode;
    // 备注
    @MaxLength(max = 1000)
    @ApiModelProperty("描述")
    private String remark;

    @ApiModelProperty("职务序列uuid")
    private String dutySeqUuid;

    @ApiModelProperty("职务序列名称")
    private String dutySeqName;

    @ApiModelProperty("职级Id多个,号隔开")
    private String jobRank;

    @ApiModelProperty("职等多个,号隔开")
    private String jobGrade;

    private String tenant;

    private String system;


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

    public String getDutySeqUuid() {
        return dutySeqUuid;
    }

    public void setDutySeqUuid(String dutySeqUuid) {
        this.dutySeqUuid = dutySeqUuid;
    }

    public String getDutySeqName() {
        return dutySeqName;
    }

    public void setDutySeqName(String dutySeqName) {
        this.dutySeqName = dutySeqName;
    }

    public String getJobRank() {
        return jobRank;
    }

    public void setJobRank(String jobRank) {
        this.jobRank = jobRank;
    }

    public String getJobGrade() {
        return jobGrade;
    }

    public void setJobGrade(String jobGrade) {
        this.jobGrade = jobGrade;
    }

    /**
     * 如何描述该方法
     *
     * @return
     */
    public OrgTreeNode convert2TreeNode() {
        OrgTreeNode node = new OrgTreeNode();
        node.setName(this.name);
        node.setType(IdPrefix.DUTY.getValue());
        node.setIconSkin(IdPrefix.GROUP.getValue());
        node.setId(this.id);
        return node;
    }

    public String getTenant() {
        return this.tenant;
    }

    public void setTenant(final String tenant) {
        this.tenant = tenant;
    }

    public String getSystem() {
        return this.system;
    }

    public void setSystem(final String system) {
        this.system = system;
    }
}
